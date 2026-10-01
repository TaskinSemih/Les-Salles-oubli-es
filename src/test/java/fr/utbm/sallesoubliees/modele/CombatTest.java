package fr.utbm.sallesoubliees.modele;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CombatTest {
    @Test void degatsOntUnMinimumEtRefusentValeursNegatives() {
        assertEquals(1, GestionnaireCombat.calculerDegats(3, 50));
        assertEquals(9, GestionnaireCombat.calculerDegats(12, 3));
        assertThrows(IllegalArgumentException.class, () -> GestionnaireCombat.calculerDegats(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> GestionnaireCombat.calculerDegats(1, -1));
    }

    @Test void vieBorneeEtGrandSoinSansDepassement() {
        Heros h = new Heros(); h.subirDegats(Integer.MAX_VALUE);
        assertEquals(0, h.getVie()); assertTrue(h.estMort());
        h.soigner(Integer.MAX_VALUE); assertEquals(100, h.getVie());
        assertThrows(IllegalArgumentException.class, () -> h.subirDegats(-1));
        assertThrows(IllegalArgumentException.class, () -> h.soigner(-1));
        assertThrows(IllegalArgumentException.class, () -> h.restaurerVie(101));
    }

    @Test void defenseExpireApresUneReponse() {
        Partie p = new Partie(); p.deplacer("ossuaire"); p.defendre();
        assertEquals(99, p.getHeros().getVie());
        p.attaquer(); assertEquals(93, p.getHeros().getVie());
        assertEquals(2, p.getSalleActuelle().getEnnemi().getTours());
    }

    @Test void golemAlternePreparationEtFrappeEtDefenseExpirePendantPreparation() {
        Heros h = new Heros(); Golem g = new Golem();
        GestionnaireCombat.repondre(h, g, true); assertEquals(100, h.getVie());
        assertEquals(22, g.puissanceProchaineAttaque());
        GestionnaireCombat.repondre(h, g, false); assertEquals(81, h.getVie());
        assertEquals(0, g.puissanceProchaineAttaque());
        GestionnaireCombat.repondre(h, g, false);
        GestionnaireCombat.repondre(h, g, true); assertEquals(70, h.getVie());
    }

    @Test void mageIgnoreArmureMaisRespecteDefense() {
        Heros h = new Heros(); Mage m = new Mage();
        GestionnaireCombat.repondre(h, m, false); assertEquals(89, h.getVie());
        GestionnaireCombat.repondre(h, m, true); assertEquals(86, h.getVie());
    }

    @Test void bossEnrageDesLaMoitieDeSaVie() {
        Boss b = new Boss(); assertEquals(16, b.puissanceProchaineAttaque());
        b.subirDegats(44); assertEquals(16, b.puissanceProchaineAttaque());
        b.subirDegats(1); assertEquals(24, b.puissanceProchaineAttaque());
    }

    @Test void ennemiTueNeRipostePasEtNeDonneQuUneRecompense() {
        Partie p = new Partie(); p.deplacer("ossuaire"); p.attaquer(); p.attaquer();
        int vie = p.getHeros().getVie(); p.attaquer();
        assertEquals(vie, p.getHeros().getVie());
        assertEquals(2, p.getSalleActuelle().getEnnemi().getTours());
        assertEquals(4, p.getHeros().getInventaire().getPotions());
        assertThrows(IllegalStateException.class, p::attaquer);
        p.boirePotion(); assertEquals(3, p.getHeros().getInventaire().getPotions());
        p.deplacer("entree"); p.deplacer("ossuaire");
        assertFalse(p.estEnCombat()); assertEquals(3, p.getHeros().getInventaire().getPotions());
    }

    @Test void potionSoigneAvecPlafondEtConsommeUnTourEnCombat() {
        Partie p = new Partie(); p.deplacer("ossuaire"); p.attaquer();
        assertEquals(94, p.getHeros().getVie()); p.boirePotion();
        assertEquals(94, p.getHeros().getVie()); // 100 après le soin, puis 6 dégâts.
        assertEquals(2, p.getHeros().getInventaire().getPotions());
        assertEquals(2, p.getSalleActuelle().getEnnemi().getTours());
    }

    @Test void actionsInvalidesNeModifientRienEtNeConsommentPasDeTour() {
        Partie p = new Partie(); p.deplacer("ossuaire");
        EtatSauvegarde avant = SauvegardePartie.capturer(p);
        assertThrows(IllegalStateException.class, p::boirePotion);
        assertThrows(IllegalStateException.class, () -> p.deplacer("entree"));
        assertThrows(IllegalStateException.class, () -> p.equiper(Arme.ROUILLEE));
        assertThrows(IllegalStateException.class, p::ouvrirCoffre);
        assertThrows(IllegalStateException.class, p::seReposer);
        assertEquals(avant, SauvegardePartie.capturer(p));
    }

    @Test void absenceDePotionNeDonnePasDeTourEnnemi() {
        Partie p = new Partie(); p.deplacer("ossuaire"); p.attaquer();
        while (p.getHeros().getInventaire().getPotions() > 0) p.boirePotion();
        EtatSauvegarde avant = SauvegardePartie.capturer(p);
        assertThrows(IllegalStateException.class, p::boirePotion);
        assertEquals(avant, SauvegardePartie.capturer(p));
    }

    @Test void defaiteInterditToutesLesActionsEtAutresTours() {
        Partie p = new Partie(); p.deplacer("ossuaire");
        for (int i = 0; i < 100; i++) p.defendre();
        assertEquals(EtatPartie.DEFAITE, p.getEtat()); assertEquals(0, p.getHeros().getVie());
        EtatSauvegarde avant = SauvegardePartie.capturer(p);
        assertThrows(IllegalStateException.class, p::attaquer);
        assertThrows(IllegalStateException.class, p::defendre);
        assertThrows(IllegalStateException.class, p::boirePotion);
        assertThrows(IllegalStateException.class, p::ouvrirCoffre);
        assertThrows(IllegalStateException.class, p::seReposer);
        assertThrows(IllegalStateException.class, () -> p.equiper(Arme.ROUILLEE));
        assertThrows(IllegalStateException.class, () -> p.deplacer("entree"));
        assertEquals(avant, SauvegardePartie.capturer(p));
        assertEquals(avant, SauvegardePartie.capturer(SauvegardePartie.restaurer(avant)));
    }
}
