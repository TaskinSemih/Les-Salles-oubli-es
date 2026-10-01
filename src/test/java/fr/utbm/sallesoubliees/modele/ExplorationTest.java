package fr.utbm.sallesoubliees.modele;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExplorationTest {
    @Test void deplacementsSuiventLesPassagesEtCollectionsProtegees() {
        Partie p = new Partie();
        assertEquals(9, p.getDonjon().getSalles().size());
        assertThrows(IllegalStateException.class, () -> p.deplacer("boss"));
        assertThrows(IllegalStateException.class, () -> p.deplacer("inconnue"));
        assertThrows(UnsupportedOperationException.class, () -> p.getSalleActuelle().getPassages().add("boss"));
        assertThrows(UnsupportedOperationException.class, () -> p.getDonjon().getSalles().clear());
        assertThrows(UnsupportedOperationException.class, () -> p.getHeros().getInventaire().getArmes().add(Arme.RUNIQUE));
        p.deplacer("ossuaire"); assertTrue(p.getSalleActuelle().estVisitee());
        for (Salle salle : p.getDonjon().getSalles()) {
            for (String voisin : salle.getPassages()) assertTrue(p.getDonjon().getSalle(voisin).getPassages().contains(salle.getId()));
        }
    }

    @Test void coffreUniqueEtEquipementSansCumul() {
        Partie p = versArsenal();
        p.ouvrirCoffre(); assertEquals(6, p.getHeros().getInventaire().getPotions());
        assertThrows(IllegalStateException.class, p::ouvrirCoffre);
        p.equiper(Arme.ACIER); assertEquals(17, p.getHeros().getAttaque());
        assertThrows(IllegalStateException.class, () -> p.equiper(Arme.ACIER));
        assertThrows(IllegalStateException.class, () -> p.equiper(Arme.RUNIQUE));
        p.equiper(Arme.ROUILLEE); p.equiper(Arme.ACIER); assertEquals(17, p.getHeros().getAttaque());
        p.deplacer("repos"); p.deplacer("arsenal");
        assertFalse(p.peutOuvrir()); assertEquals(6, p.getHeros().getInventaire().getPotions());
    }

    @Test void reposNeSeConsommePasAPleineVieEtNeSertQuUneFois() {
        Partie p = versArsenal(); p.boirePotion(); p.deplacer("repos");
        assertThrows(IllegalStateException.class, p::seReposer); assertFalse(p.getSalleActuelle().estUtilisee());
        p.deplacer("bibliotheque"); gagnerCombat(p); p.deplacer("repos");
        p.seReposer(); assertEquals(100, p.getHeros().getVie());
        assertTrue(p.getSalleActuelle().estUtilisee());
        assertThrows(IllegalStateException.class, p::seReposer);
        p.deplacer("forge"); gagnerCombat(p); p.deplacer("repos");
        assertFalse(p.peutReposer());
    }

    @Test void parcoursCompletGagnantAvecExplorationDesNeufSalles() {
        Partie p = versArsenal(); p.ouvrirCoffre(); p.equiper(Arme.ACIER);
        p.deplacer("repos"); p.deplacer("forge"); gagnerCombat(p);
        p.deplacer("repos"); p.seReposer();
        p.deplacer("bibliotheque"); gagnerCombat(p);
        p.deplacer("antichambre"); gagnerCombat(p);
        p.deplacer("tresor"); p.ouvrirCoffre(); p.equiper(Arme.RUNIQUE);
        p.deplacer("antichambre"); p.deplacer("boss"); gagnerCombat(p);
        assertEquals(EtatPartie.VICTOIRE, p.getEtat()); assertTrue(p.getHeros().getVie() > 0);
        assertTrue(p.getDonjon().getSalles().stream().allMatch(Salle::estVisitee));
        EtatSauvegarde fin = SauvegardePartie.capturer(p);
        assertEquals(fin, SauvegardePartie.capturer(SauvegardePartie.restaurer(fin)));
        assertThrows(IllegalStateException.class, p::attaquer);
        assertThrows(IllegalStateException.class, p::defendre);
        assertThrows(IllegalStateException.class, p::boirePotion);
        assertThrows(IllegalStateException.class, () -> p.deplacer("antichambre"));
        assertThrows(IllegalStateException.class, () -> p.equiper(Arme.ROUILLEE));
        assertThrows(IllegalStateException.class, p::ouvrirCoffre);
        assertThrows(IllegalStateException.class, p::seReposer);
        assertEquals(fin, SauvegardePartie.capturer(p));
    }

    @Test void nouvellePartieNePartageAucunEtat() {
        Partie a = versArsenal(); a.ouvrirCoffre(); Partie b = new Partie();
        assertEquals(100, b.getHeros().getVie()); assertEquals(3, b.getHeros().getInventaire().getPotions());
        assertFalse(b.getDonjon().getSalle("arsenal").estUtilisee());
        assertFalse(b.getDonjon().getSalle("ossuaire").getEnnemi().estMort());
    }

    private static Partie versArsenal() {
        Partie p = new Partie(); p.deplacer("ossuaire"); gagnerCombat(p); p.deplacer("arsenal"); return p;
    }

    private static void gagnerCombat(Partie p) {
        int actions = 0;
        while (p.estEnCombat()) {
            assertTrue(actions++ < 100, "Le combat doit se terminer.");
            Ennemi e = p.getSalleActuelle().getEnnemi();
            int degats = GestionnaireCombat.calculerDegats(p.getHeros().getAttaque(), e.getArmure());
            if (e.getVie() <= degats) p.attaquer();
            else if (p.getHeros().getVie() <= 40 && p.peutBoire()) p.boirePotion();
            else if (e instanceof Golem && e.puissanceProchaineAttaque() > 0) p.defendre();
            else p.attaquer();
        }
        assertNotEquals(EtatPartie.DEFAITE, p.getEtat());
    }
}
