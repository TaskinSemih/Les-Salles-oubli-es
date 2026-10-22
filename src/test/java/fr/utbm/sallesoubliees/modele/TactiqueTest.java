package fr.utbm.sallesoubliees.modele;

import fr.utbm.sallesoubliees.persistance.*;
import fr.utbm.sallesoubliees.vue.ControleurAnimation;
import java.nio.file.Path;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import static fr.utbm.sallesoubliees.modele.EvenementJeu.Type.*;
import static fr.utbm.sallesoubliees.modele.EvenementJeu.Cible.*;

/** Tests des interactions tactiques et des reprises, sans probabilités instables. */
class TactiqueTest {
    @TempDir Path dossier;

    @Test void hasardReproductibleEtVarie() {
        SourceAleatoire a = new HasardReproductible(42), b = new HasardReproductible(42), c = new HasardReproductible(43);
        List<Integer> x = new ArrayList<>(), y = new ArrayList<>();
        for (int i = 0; i < 100; i++) { int n = a.prochainEntier(100); assertEquals(n, b.prochainEntier(100)); x.add(n); y.add(c.prochainEntier(100)); }
        assertNotEquals(x, y); assertThrows(IllegalArgumentException.class, () -> a.prochainEntier(0));
    }

    @Test void coutRefuseSansConsommerHasardPuisFrappeBriseLaGarde() {
        Partie p = squelette(); var avant = SauvegardePartie.capturer(p);
        assertFalse(p.resoudre(ActionCombat.PUISSANTE).valide()); assertEquals(avant, SauvegardePartie.capturer(p));
        p.attaquer(); p.attaquer(); assertEquals(2, p.getTactique().getElan());
        imposer(p, ActionEnnemi.RIPOSTE, 9, List.of(new EffetCombat(EffetCombat.Type.ARMURE_RENFORCEE, ENNEMI, 2)));
        var r = p.resoudre(ActionCombat.PUISSANTE);
        assertTrue(types(r).contains(BRISE_GARDE)); assertTrue(types(r).contains(MORT));
        assertFalse(types(r).contains(CONTRE_ATTAQUE)); assertEquals(0, p.getTactique().getElan());
    }

    @Test void gardeRiposteEtObservationDifferent() {
        Partie a = squelette(), b = squelette();
        imposer(a, ActionEnnemi.RIPOSTE, 9, List.of()); imposer(b, ActionEnnemi.RIPOSTE, 9, List.of());
        assertTrue(types(a.resoudre(ActionCombat.ATTAQUER)).contains(CONTRE_ATTAQUE));
        var r = b.resoudre(ActionCombat.OBSERVER);
        assertFalse(types(r).contains(DEGATS)); assertEquals(100, b.getHeros().getVie());
        assertTrue(b.getTactique().getEffets().stream().anyMatch(e -> e.type() == EffetCombat.Type.VULNERABLE));
        assertTrue(b.getTactique().getEffets().stream().anyMatch(e -> e.type() == EffetCombat.Type.LUCIDE));
    }

    @Test void defenseDonneElanSeulementSiElleReduitUnImpact() {
        Partie p = squelette(); p.defendre(); assertEquals(1, p.getTactique().getElan());
        imposer(p, ActionEnnemi.RECUPERATION, 0, List.of()); p.defendre(); assertEquals(1, p.getTactique().getElan());
        for (int i = 0; i < 5; i++) { imposer(p, ActionEnnemi.ATTAQUE, 9, List.of()); p.defendre(); }
        assertEquals(3, p.getTactique().getElan());
    }

    @Test void golemPreparationInterruptibleEtFrappeAnnoncee() {
        Partie p = golem(); imposer(p, ActionEnnemi.PREPARATION, 0, List.of());
        var r = p.resoudre(ActionCombat.ATTAQUER);
        assertTrue(types(r).contains(INTERRUPTION)); assertFalse(r.evenements().stream().anyMatch(e -> e.type() == DEGATS && e.cible() == HEROS));
        imposer(p, ActionEnnemi.PREPARATION, 0, List.of()); p.resoudre(ActionCombat.OBSERVER);
        assertEquals(ActionEnnemi.FRAPPE_LOURDE, p.getTactique().getDecision().action());
        assertEquals(22, p.getTactique().getDecision().puissance());
        int vie = p.getHeros().getVie(); p.defendre(); assertEquals(vie - 11, p.getHeros().getVie());
        assertEquals(ActionEnnemi.RECUPERATION, p.getTactique().getDecision().action());
    }

    @Test void maledictionExpireEtObservationReveleDeuxIntentions() {
        Partie p = squelette();
        imposer(p, ActionEnnemi.MALEDICTION, 0, List.of()); p.resoudre(ActionCombat.OBSERVER);
        assertEquals(2, p.getTactique().getEffets().stream().filter(e -> e.type() == EffetCombat.Type.AFFAIBLI).findFirst().orElseThrow().duree());
        var r = p.resoudre(ActionCombat.ATTAQUER);
        assertEquals(7, r.evenements().stream().filter(e -> e.type() == DEGATS && e.cible() == ENNEMI).findFirst().orElseThrow().valeur());
        p.defendre(); assertTrue(p.getTactique().getEffets().stream().noneMatch(e -> e.type() == EffetCombat.Type.AFFAIBLI || e.type() == EffetCombat.Type.LUCIDE));
        DecisionEnnemi d = new DecisionEnnemi(ActionEnnemi.ATTAQUE, 11, false);
        assertNotEquals(d.decrire(false), d.decrire(true));
    }

    @Test void mageSoinUniqueEtReactionsContextuelles() {
        Mage m = new Mage(); m.subirDegats(25);
        var c = new ContexteCombat(3, List.of(ActionCombat.POTION, ActionCombat.POTION), null, false);
        assertEquals(ActionEnnemi.SOIN, m.decider(c, new HasardReproductible(0)).action());
        assertNotEquals(ActionEnnemi.SOIN, m.decider(new ContexteCombat(3, c.historique(), ActionEnnemi.SOIN, true), new HasardReproductible(0)).action());
        assertEquals(ActionEnnemi.MALEDICTION, new Mage().decider(c, new HasardReproductible(0)).action());
    }

    @Test void bossAdapteRepetitionsEtTraverseTroisPhases() {
        Boss b = new Boss();
        var c = new ContexteCombat(0, List.of(ActionCombat.ATTAQUER, ActionCombat.ATTAQUER, ActionCombat.ATTAQUER), null, false);
        assertEquals(ActionEnnemi.RIPOSTE, b.decider(c, new HasardReproductible(0)).action());
        assertNotEquals(ActionEnnemi.RIPOSTE, b.decider(new ContexteCombat(0, List.of(), null, false), new HasardReproductible(0)).action());
        b.subirDegats(45); assertEquals(30, puissanceFinale(b));
    }
    private static int puissanceFinale(Boss b) {
        b.subirDegats(23);
        return b.decider(new ContexteCombat(0, List.of(), ActionEnnemi.PREPARATION, false), new HasardReproductible(0)).puissance();
    }

    @Test void effetMarqueConsommeUneSeuleFoisEtOndeTraverseLaGarde() {
        Partie p = squelette();
        imposer(p, ActionEnnemi.ONDE, 16, List.of(new EffetCombat(EffetCombat.Type.MARQUE, HEROS, 3)));
        var r = p.resoudre(ActionCombat.DEFENDRE); assertEquals(87, p.getHeros().getVie());
        assertTrue(types(r).contains(EXPIRATION));
        assertTrue(p.getTactique().getEffets().stream().noneMatch(e -> e.type() == EffetCombat.Type.MARQUE));
    }

    @Test void repriseJsonConserveExactementLesProchainsTours() throws Exception {
        Partie p = golem(); p.resoudre(ActionCombat.OBSERVER);
        var g = new GestionnaireSauvegarde(); Path f = dossier.resolve("tactique.json"); g.sauvegarder(f, SauvegardePartie.capturer(p)); Partie b = g.charger(f);
        for (ActionCombat a : List.of(ActionCombat.DEFENDRE, ActionCombat.ATTAQUER, ActionCombat.PUISSANTE, ActionCombat.OBSERVER)) {
            assertEquals(p.resoudre(a), b.resoudre(a)); assertEquals(SauvegardePartie.capturer(p), SauvegardePartie.capturer(b));
        }
    }

    @Test void lecteurElanEffetsEtSoinEnnemiNeConsommePasDePotion() {
        Partie p = squelette(); p.getSalleActuelle().getEnnemi().subirDegats(5);
        imposer(p, ActionEnnemi.SOIN, 0, List.of());
        var r = p.resoudre(ActionCombat.OBSERVER); ControleurAnimation lecteur = new ControleurAnimation(); lecteur.demarrer(r);
        lecteur.avancer(100_000); assertEquals(r.apres().heros().potions(), lecteur.getPotions());
        assertEquals(r.apres().tactique().effets(), lecteur.getEffets()); assertEquals(r.apres().tactique().elan(), lecteur.getElan());
        assertEquals(r.apres(), SauvegardePartie.capturer(p));
    }

    @Test void choixUniquesToutesLesBranchesEtPersistance() throws Exception {
        for (ChoixEvenement c : ChoixEvenement.values()) {
            Partie p = squelette(); gagner(p); p.deplacer("arsenal");
            assertNotNull(p.getEvenementSalle()); int stock = p.getHeros().getInventaire().getPotions();
            p.choisirEvenement(c); assertNull(p.getEvenementSalle());
            assertEquals(stock + (c == ChoixEvenement.IGNORER ? 0 : c == ChoixEvenement.PRUDENT ? 1 : 2), p.getHeros().getInventaire().getPotions());
            var etat = SauvegardePartie.capturer(p); assertThrows(IllegalStateException.class, () -> p.choisirEvenement(c));
            assertEquals(etat, SauvegardePartie.capturer(p));
            Path fichier = dossier.resolve(c + ".json"); var g = new GestionnaireSauvegarde(); g.sauvegarder(fichier, etat);
            assertEquals(etat, SauvegardePartie.capturer(g.charger(fichier)));
        }
    }

    @Test void tiragesRisquesCouvrentPiegeEtSuccesSansMort() {
        for (long graine : List.of(0L, 2L)) {
            CombatTactique c = new CombatTactique(new HasardReproductible(graine)); Heros h = new Heros(); h.subirDegats(99);
            String message = c.choisir("tresor", ChoixEvenement.RISQUER, h); assertEquals(1, h.getVie());
            assertEquals(graine == 0, message.contains("piège"));
        }
    }

    @Test void runeSoigneAvecPlafondEtNeDonnePasDePotion() {
        for (ChoixEvenement choix : ChoixEvenement.values()) {
            CombatTactique c = new CombatTactique(new HasardReproductible(2));
            Heros h = new Heros(); h.subirDegats(20);
            c.choisir("bibliotheque", choix, h);
            assertEquals(choix == ChoixEvenement.IGNORER ? 80 : choix == ChoixEvenement.PRUDENT ? 95 : 100, h.getVie());
            assertEquals(3, h.getInventaire().getPotions());
        }
    }

    @Test void etatsInvalidesRefuses() {
        assertThrows(IllegalArgumentException.class, () -> new EtatTactique(-1, 0, null, List.of(), null, false, List.of(), Map.of()));
        assertThrows(IllegalArgumentException.class, () -> new EtatTactique(1, 4, null, List.of(), null, false, List.of(), Map.of()));
        assertThrows(IllegalArgumentException.class, () -> new EffetCombat(EffetCombat.Type.LUCIDE, HEROS, 0));
        assertThrows(UnsupportedOperationException.class, () -> squelette().getTactique().getEffets().clear());
        EtatSauvegarde s = SauvegardePartie.capturer(new Partie());
        assertThrows(IllegalArgumentException.class, () -> SauvegardePartie.restaurer(new EtatSauvegarde(1, s.heros(), s.salleActuelle(), s.etat(), s.salles(), s.tactique())));
    }

    @Test void centGrainesParcoursDirectEtCompletGagnants() {
        for (int graine = 0; graine < 100; graine++) for (boolean detour : List.of(false, true)) {
            Partie p = squelette(graine); gagner(p); p.deplacer("arsenal"); p.ouvrirCoffre(); p.equiper(Arme.ACIER);
            p.choisirEvenement(ChoixEvenement.PRUDENT); p.deplacer("repos");
            if (detour) { p.deplacer("forge"); gagner(p); p.deplacer("repos"); if (p.peutReposer()) p.seReposer(); }
            p.deplacer("bibliotheque"); gagner(p); p.choisirEvenement(ChoixEvenement.IGNORER);
            p.deplacer("antichambre"); gagner(p); p.deplacer("tresor"); p.ouvrirCoffre(); p.equiper(Arme.RUNIQUE); p.choisirEvenement(ChoixEvenement.RISQUER);
            p.deplacer("antichambre"); p.deplacer("boss"); gagner(p);
            assertEquals(EtatPartie.VICTOIRE, p.getEtat(), "Graine " + graine);
            assertEquals(0, p.getTactique().getElan()); assertTrue(p.getTactique().getEffets().isEmpty());
            assertEquals(SauvegardePartie.capturer(p), SauvegardePartie.capturer(SauvegardePartie.restaurer(SauvegardePartie.capturer(p))));
        }
    }

    static void gagner(Partie p) {
        int tours = 0;
        while (p.estEnCombat()) { assertTrue(tours++ < 100); assertTrue(p.resoudre(action(p)).valide()); }
        assertNotEquals(EtatPartie.DEFAITE, p.getEtat());
    }
    static ActionCombat action(Partie p) {
        ActionEnnemi d = p.getTactique().getDecision().action();
        if (p.getHeros().getVie() <= 45 && p.peutBoire()) return ActionCombat.POTION;
        if (d == ActionEnnemi.PREPARATION) return ActionCombat.ATTAQUER;
        if (d == ActionEnnemi.RIPOSTE) return p.getTactique().getElan() >= 2 ? ActionCombat.PUISSANTE : ActionCombat.OBSERVER;
        if (d == ActionEnnemi.FRAPPE_LOURDE || d == ActionEnnemi.ONDE) return ActionCombat.DEFENDRE;
        return p.getTactique().getElan() >= 2 ? ActionCombat.PUISSANTE : ActionCombat.ATTAQUER;
    }
    private static Partie squelette() { return squelette(3); }
    private static Partie squelette(long graine) { Partie p = new Partie(new HasardReproductible(graine)); p.deplacer("ossuaire"); return p; }
    private static Partie golem() { Partie p = squelette(); gagner(p); p.deplacer("forge"); return p; }
    private static List<EvenementJeu.Type> types(ResultatAction r) { return r.evenements().stream().map(EvenementJeu::type).toList(); }
    private static void imposer(Partie p, ActionEnnemi a, int puissance, List<EffetCombat> effets) {
        var t = p.getTactique().capturer();
        p.restaurerTactique(new EtatTactique(t.hasard(), t.elan(), new DecisionEnnemi(a, puissance, true), t.historique(), t.precedente(), t.soinUtilise(), effets, t.choix()));
    }
}
