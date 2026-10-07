package fr.utbm.sallesoubliees.modele;

import fr.utbm.sallesoubliees.vue.ControleurAnimation;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static fr.utbm.sallesoubliees.modele.EvenementJeu.Type.*;

class EvenementsTest {
    @Test void ordreDesFaitsEtDegatsReels() {
        Partie p = new Partie(); p.deplacer("ossuaire");
        ResultatAction r = p.resoudre(ActionCombat.ATTAQUER);
        assertEquals(List.of(ATTAQUE, DEGATS, ATTAQUE, DEGATS), types(r));
        assertEquals(11, r.evenements().get(1).valeur()); assertEquals(6, r.evenements().get(3).valeur());
        assertEquals(100, r.avant().heros().vie()); assertEquals(94, r.apres().heros().vie());
        assertThrows(UnsupportedOperationException.class, () -> r.evenements().clear());
    }
    @Test void mortSansRiposteEtRecompenseUnique() {
        Partie p = new Partie(); p.deplacer("ossuaire"); p.attaquer(); p.attaquer();
        ResultatAction r = p.resoudre(ActionCombat.ATTAQUER);
        assertEquals(List.of(ATTAQUE, DEGATS, MORT, RECOMPENSE), types(r));
        assertEquals(8, r.evenements().get(1).valeur()); // PV restants, pas les dégâts théoriques.
        assertEquals(r.avant().heros().vie(), r.apres().heros().vie());
    }
    @Test void preparationDuGolemSansEvenementDeDegatsHeros() {
        Partie p = golem(); ResultatAction r = p.resoudre(ActionCombat.ATTAQUER);
        assertEquals(List.of(ATTAQUE, DEGATS, PREPARATION), types(r));
        assertEquals(List.of(DEFENSE, ATTAQUE, DEGATS), types(p.resoudre(ActionCombat.DEFENDRE)));
    }
    @Test void potionAnnonceSoinReelAvantLaRiposteEtLeStock() {
        Partie p = new Partie(); p.deplacer("ossuaire"); p.attaquer();
        ResultatAction r = p.resoudre(ActionCombat.POTION);
        assertEquals(List.of(SOIN, ATTAQUE, DEGATS), types(r)); assertEquals(6, r.evenements().getFirst().valeur());
        assertEquals(100, r.evenements().getFirst().vieApres()); assertEquals(94, r.apres().heros().vie());
    }
    @Test void invalideSansEvenementNiModification() {
        Partie p = new Partie(); ResultatAction r = p.resoudre(ActionCombat.ATTAQUER);
        assertFalse(r.valide()); assertTrue(r.evenements().isEmpty()); assertEquals(r.avant(), r.apres());
        p.deplacer("ossuaire"); r = p.resoudre(ActionCombat.POTION);
        assertFalse(r.valide()); assertEquals(r.avant(), SauvegardePartie.capturer(p));
    }
    @Test void rageDuBossAvantSaRiposte() {
        Partie p = new Partie(); p.deplacer("ossuaire"); gagner(p); p.deplacer("arsenal"); p.ouvrirCoffre(); p.equiper(Arme.ACIER);
        p.deplacer("repos"); p.deplacer("bibliotheque"); gagner(p); p.deplacer("antichambre"); gagner(p);
        p.deplacer("tresor"); p.ouvrirCoffre(); p.equiper(Arme.RUNIQUE);
        while (p.peutBoire()) p.boirePotion();
        p.deplacer("antichambre"); p.deplacer("boss");
        p.attaquer(); p.attaquer(); ResultatAction r = p.resoudre(ActionCombat.ATTAQUER);
        assertEquals(List.of(ATTAQUE, DEGATS, RAGE, ATTAQUE, DEGATS), types(r));
        assertEquals(21, r.evenements().getLast().valeur());
    }
    @Test void lecteurAnimeLaCopieEtNeRejoueJamaisLeModele() {
        Partie p = golem(); ResultatAction r = p.resoudre(ActionCombat.ATTAQUER);
        EtatSauvegarde fin = SauvegardePartie.capturer(p); ControleurAnimation a = new ControleurAnimation(); a.demarrer(r);
        assertEquals(48, a.getVieEnnemi()); assertThrows(IllegalStateException.class, () -> a.demarrer(r));
        a.avancer(260); assertEquals(40, a.getVieEnnemi());
        a.avancer(10_000); assertFalse(a.estActive()); assertEquals(fin, SauvegardePartie.capturer(p));
        assertEquals(fin.heros().vie(), a.getVieHeros()); assertEquals(fin.heros().potions(), a.getPotions());
    }
    @Test void defenseExpireMemeEnModeReduitEtStabilisationExacte() {
        Partie p = golem(); p.attaquer(); ResultatAction r = p.resoudre(ActionCombat.DEFENDRE);
        ControleurAnimation a = new ControleurAnimation(); a.setReduite(true); a.demarrer(r); assertTrue(a.estDefense());
        a.finirImmediatement(); assertFalse(a.estActive()); assertFalse(a.estDefense()); assertEquals(r.apres().heros().vie(), a.getVieHeros());
    }
    private static List<EvenementJeu.Type> types(ResultatAction r) { return r.evenements().stream().map(EvenementJeu::type).toList(); }
    private static Partie golem() { Partie p = new Partie(); p.deplacer("ossuaire"); gagner(p); p.deplacer("forge"); return p; }
    private static void gagner(Partie p) { while (p.estEnCombat()) { if (p.getHeros().getVie() < 45 && p.peutBoire()) p.boirePotion(); else p.attaquer(); } }
}
