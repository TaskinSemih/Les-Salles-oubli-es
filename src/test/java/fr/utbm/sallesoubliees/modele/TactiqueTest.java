// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

// Rend disponibles les types ou constantes de fr.utbm.sallesoubliees.persistance sans répéter leur nom complet.
import fr.utbm.sallesoubliees.persistance.*;
// Rend disponible ControleurAnimation depuis fr.utbm.sallesoubliees.vue.
import fr.utbm.sallesoubliees.vue.ControleurAnimation;
// Rend disponible Path depuis java.nio.file.
import java.nio.file.Path;
// Rend disponibles les types ou constantes de java.util sans répéter leur nom complet.
import java.util.*;
// Importe l’annotation JUnit qui identifie une méthode de test.
import org.junit.jupiter.api.Test;
// Importe l’annotation qui confie à JUnit la création et le nettoyage d’un dossier temporaire.
import org.junit.jupiter.api.io.TempDir;
// Importe les assertions JUnit pour comparer les résultats et vérifier les erreurs attendues.
import static org.junit.jupiter.api.Assertions.*;
// Rend disponibles les types ou constantes de fr.utbm.sallesoubliees.modele.EvenementJeu.Type sans répéter leur nom complet.
import static fr.utbm.sallesoubliees.modele.EvenementJeu.Type.*;
// Rend disponibles les types ou constantes de fr.utbm.sallesoubliees.modele.EvenementJeu.Cible sans répéter leur nom complet.
import static fr.utbm.sallesoubliees.modele.EvenementJeu.Cible.*;

/** Tests des interactions tactiques et des reprises, sans probabilités instables. */
// Déclare TactiqueTest, tests reproductibles des stratégies, effets, découvertes et reprises de combat.
class TactiqueTest {
    // JUnit fournit ce dossier temporaire et le nettoie après le test.
    @TempDir Path dossier;

    // Teste le scénario suivant : hasard reproductible et varie.
    @Test void hasardReproductibleEtVarie() {
        // Initialise la variable a de type SourceAleatoire avec un générateur reproductible initialisé avec 42), b = new HasardReproductible(42), c = new HasardReproductible(43.
        SourceAleatoire a = new HasardReproductible(42), b = new HasardReproductible(42), c = new HasardReproductible(43);
        // Initialise « x » avec une nouvelle collection indépendante initialisée avec ), y = new ArrayList<>(.
        List<Integer> x = new ArrayList<>(), y = new ArrayList<>();
        // Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int i = 0; i < 100; i++. Initialise « n » avec a.prochainEntier(100). Vérifie l’égalité entre la valeur attendue et la valeur obtenue : n ; b.prochainEntier(100). Ajoute l’élément à la collection ou au conteneur ; arguments : n. Ajoute l’élément à la collection ou au conteneur ; arguments : c.prochainEntier(100).
        for (int i = 0; i < 100; i++) { int n = a.prochainEntier(100); assertEquals(n, b.prochainEntier(100)); x.add(n); y.add(c.prochainEntier(100)); }
        // Vérifie que les deux résultats sont différents : x ; y. Vérifie que l’opération lève l’exception attendue (IllegalArgumentException) lors de () -> a.prochainEntier(0).
        assertNotEquals(x, y); assertThrows(IllegalArgumentException.class, () -> a.prochainEntier(0));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : cout refuse sans consommer hasard puis frappe brise la garde.
    @Test void coutRefuseSansConsommerHasardPuisFrappeBriseLaGarde() {
        // Initialise la variable p de type Partie avec squelette(). Initialise l'instantané avant l'action avec SauvegardePartie.capturer(p).
        Partie p = squelette(); var avant = SauvegardePartie.capturer(p);
        // Vérifie que la condition est fausse : p.resoudre(ActionCombat.PUISSANTE).valide(). Vérifie l’égalité entre la valeur attendue et la valeur obtenue : l'instantané avant l'action ; SauvegardePartie.capturer(p).
        assertFalse(p.resoudre(ActionCombat.PUISSANTE).valide()); assertEquals(avant, SauvegardePartie.capturer(p));
        // Joue une attaque du héros et l’éventuelle réponse ennemie. Joue une attaque du héros et l’éventuelle réponse ennemie. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 2 ; p.getTactique().getElan().
        p.attaquer(); p.attaquer(); assertEquals(2, p.getTactique().getElan());
        // Injecte un état tactique contrôlé pour tester une réaction précise sans dépendre d’un tirage. Paramètres : p, ActionEnnemi.RIPOSTE, 9, List.of(new EffetCombat(EffetCombat.Type.ARMURE_RENFORCEE, ENNEMI, 2)).
        imposer(p, ActionEnnemi.RIPOSTE, 9, List.of(new EffetCombat(EffetCombat.Type.ARMURE_RENFORCEE, ENNEMI, 2)));
        // Initialise la variable r de type var avec p.resoudre(ActionCombat.PUISSANTE).
        var r = p.resoudre(ActionCombat.PUISSANTE);
        // Vérifie que la condition est vraie : types(r).contains(BRISE_GARDE). Vérifie que la condition est vraie : types(r).contains(MORT).
        assertTrue(types(r).contains(BRISE_GARDE)); assertTrue(types(r).contains(MORT));
        // Vérifie que la condition est fausse : types(r).contains(CONTRE_ATTAQUE). Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 0 ; p.getTactique().getElan().
        assertFalse(types(r).contains(CONTRE_ATTAQUE)); assertEquals(0, p.getTactique().getElan());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : garde riposte et observation different.
    @Test void gardeRiposteEtObservationDifferent() {
        // Initialise la variable a de type Partie avec squelette(), b = squelette().
        Partie a = squelette(), b = squelette();
        // Injecte un état tactique contrôlé pour tester une réaction précise sans dépendre d’un tirage. Paramètres : a, ActionEnnemi.RIPOSTE, 9, List.of(). Injecte un état tactique contrôlé pour tester une réaction précise sans dépendre d’un tirage. Paramètres : b, ActionEnnemi.RIPOSTE, 9, List.of().
        imposer(a, ActionEnnemi.RIPOSTE, 9, List.of()); imposer(b, ActionEnnemi.RIPOSTE, 9, List.of());
        // Vérifie que la condition est vraie : types(a.resoudre(ActionCombat.ATTAQUER)).contains(CONTRE_ATTAQUE).
        assertTrue(types(a.resoudre(ActionCombat.ATTAQUER)).contains(CONTRE_ATTAQUE));
        // Initialise la variable r de type var avec b.resoudre(ActionCombat.OBSERVER).
        var r = b.resoudre(ActionCombat.OBSERVER);
        // Vérifie que la condition est fausse : types(r).contains(DEGATS). Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 100 ; b.getHeros().getVie().
        assertFalse(types(r).contains(DEGATS)); assertEquals(100, b.getHeros().getVie());
        // Vérifie que la condition est vraie : b.getTactique().getEffets().stream().anyMatch(e -> e.type() == EffetCombat.Type.VULNERABLE).
        assertTrue(b.getTactique().getEffets().stream().anyMatch(e -> e.type() == EffetCombat.Type.VULNERABLE));
        // Vérifie que la condition est vraie : b.getTactique().getEffets().stream().anyMatch(e -> e.type() == EffetCombat.Type.LUCIDE).
        assertTrue(b.getTactique().getEffets().stream().anyMatch(e -> e.type() == EffetCombat.Type.LUCIDE));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : defense donne elan seulement si elle reduit un impact.
    @Test void defenseDonneElanSeulementSiElleReduitUnImpact() {
        // Initialise la variable p de type Partie avec squelette(). Joue une défense pour la réponse qui suit. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 1 ; p.getTactique().getElan().
        Partie p = squelette(); p.defendre(); assertEquals(1, p.getTactique().getElan());
        // Injecte un état tactique contrôlé pour tester une réaction précise sans dépendre d’un tirage. Paramètres : p, ActionEnnemi.RECUPERATION, 0, List.of(). Joue une défense pour la réponse qui suit. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 1 ; p.getTactique().getElan().
        imposer(p, ActionEnnemi.RECUPERATION, 0, List.of()); p.defendre(); assertEquals(1, p.getTactique().getElan());
        // Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int i = 0; i < 5; i++. Injecte un état tactique contrôlé pour tester une réaction précise sans dépendre d’un tirage. Paramètres : p, ActionEnnemi.ATTAQUE, 9, List.of(). Joue une défense pour la réponse qui suit.
        for (int i = 0; i < 5; i++) { imposer(p, ActionEnnemi.ATTAQUE, 9, List.of()); p.defendre(); }
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 3 ; p.getTactique().getElan().
        assertEquals(3, p.getTactique().getElan());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : golem preparation interruptible et frappe annoncee.
    @Test void golemPreparationInterruptibleEtFrappeAnnoncee() {
        // Initialise la variable p de type Partie avec golem(). Injecte un état tactique contrôlé pour tester une réaction précise sans dépendre d’un tirage. Paramètres : p, ActionEnnemi.PREPARATION, 0, List.of().
        Partie p = golem(); imposer(p, ActionEnnemi.PREPARATION, 0, List.of());
        // Initialise la variable r de type var avec p.resoudre(ActionCombat.ATTAQUER).
        var r = p.resoudre(ActionCombat.ATTAQUER);
        // Vérifie que la condition est vraie : types(r).contains(INTERRUPTION). Vérifie que la condition est fausse : r.evenements().stream().anyMatch(e -> e.type() == DEGATS && e.cible() == HEROS).
        assertTrue(types(r).contains(INTERRUPTION)); assertFalse(r.evenements().stream().anyMatch(e -> e.type() == DEGATS && e.cible() == HEROS));
        // Injecte un état tactique contrôlé pour tester une réaction précise sans dépendre d’un tirage. Paramètres : p, ActionEnnemi.PREPARATION, 0, List.of(). Résout entièrement la commande avec ses faits et états avant/après ; arguments : ActionCombat.OBSERVER.
        imposer(p, ActionEnnemi.PREPARATION, 0, List.of()); p.resoudre(ActionCombat.OBSERVER);
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : ActionEnnemi.FRAPPE_LOURDE ; p.getTactique().getDecision().action().
        assertEquals(ActionEnnemi.FRAPPE_LOURDE, p.getTactique().getDecision().action());
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 22 ; p.getTactique().getDecision().puissance().
        assertEquals(22, p.getTactique().getDecision().puissance());
        // Initialise les points de vie actuels avec p.getHeros().getVie(). Joue une défense pour la réponse qui suit. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : vie - 11 ; p.getHeros().getVie().
        int vie = p.getHeros().getVie(); p.defendre(); assertEquals(vie - 11, p.getHeros().getVie());
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : ActionEnnemi.RECUPERATION ; p.getTactique().getDecision().action().
        assertEquals(ActionEnnemi.RECUPERATION, p.getTactique().getDecision().action());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : malediction expire et observation revele deux intentions.
    @Test void maledictionExpireEtObservationReveleDeuxIntentions() {
        // Initialise la variable p de type Partie avec squelette().
        Partie p = squelette();
        // Injecte un état tactique contrôlé pour tester une réaction précise sans dépendre d’un tirage. Paramètres : p, ActionEnnemi.MALEDICTION, 0, List.of(). Résout entièrement la commande avec ses faits et états avant/après ; arguments : ActionCombat.OBSERVER.
        imposer(p, ActionEnnemi.MALEDICTION, 0, List.of()); p.resoudre(ActionCombat.OBSERVER);
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 2 ; p.getTactique().getEffets().stream().filter(e -> e.type() == EffetCombat.Type.AFFAIBLI).findFirst().orElseThrow().duree().
        assertEquals(2, p.getTactique().getEffets().stream().filter(e -> e.type() == EffetCombat.Type.AFFAIBLI).findFirst().orElseThrow().duree());
        // Initialise la variable r de type var avec p.resoudre(ActionCombat.ATTAQUER).
        var r = p.resoudre(ActionCombat.ATTAQUER);
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 7 ; r.evenements().stream().filter(e -> e.type() == DEGATS && e.cible() == ENNEMI).findFirst().orElseThrow().valeur().
        assertEquals(7, r.evenements().stream().filter(e -> e.type() == DEGATS && e.cible() == ENNEMI).findFirst().orElseThrow().valeur());
        // Joue une défense pour la réponse qui suit. Vérifie que la condition est vraie : p.getTactique().getEffets().stream().noneMatch(e -> e.type() == EffetCombat.Type.AFFAIBLI || e.type() == EffetCombat.Type.LUCIDE).
        p.defendre(); assertTrue(p.getTactique().getEffets().stream().noneMatch(e -> e.type() == EffetCombat.Type.AFFAIBLI || e.type() == EffetCombat.Type.LUCIDE));
        // Initialise « d » avec un nouvel objet DecisionEnnemi avec ActionEnnemi.ATTAQUE, 11, false.
        DecisionEnnemi d = new DecisionEnnemi(ActionEnnemi.ATTAQUE, 11, false);
        // Vérifie que les deux résultats sont différents : d.decrire(false) ; d.decrire(true).
        assertNotEquals(d.decrire(false), d.decrire(true));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : mage soin unique et reactions contextuelles.
    @Test void mageSoinUniqueEtReactionsContextuelles() {
        // Initialise « m » avec un nouvel objet Mage. Applique les dégâts au personnage en bornant sa vie à zéro ; arguments : 25.
        Mage m = new Mage(); m.subirDegats(25);
        // Initialise la variable c de type var avec un nouvel objet ContexteCombat avec 3, List.of(ActionCombat.POTION, ActionCombat.POTION), null, false.
        var c = new ContexteCombat(3, List.of(ActionCombat.POTION, ActionCombat.POTION), null, false);
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : ActionEnnemi.SOIN ; m.decider(c, new HasardReproductible(0)).action().
        assertEquals(ActionEnnemi.SOIN, m.decider(c, new HasardReproductible(0)).action());
        // Vérifie que les deux résultats sont différents : ActionEnnemi.SOIN ; m.decider(new ContexteCombat(3, c.historique(), ActionEnnemi.SOIN, true), new HasardReproductible(0)).action().
        assertNotEquals(ActionEnnemi.SOIN, m.decider(new ContexteCombat(3, c.historique(), ActionEnnemi.SOIN, true), new HasardReproductible(0)).action());
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : ActionEnnemi.MALEDICTION ; un nouvel objet Mage avec ).decider(c, new HasardReproductible(0)).action(.
        assertEquals(ActionEnnemi.MALEDICTION, new Mage().decider(c, new HasardReproductible(0)).action());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : boss adapte repetitions et traverse trois phases.
    @Test void bossAdapteRepetitionsEtTraverseTroisPhases() {
        // Initialise la variable b de type Boss avec un nouvel objet Boss.
        Boss b = new Boss();
        // Initialise la variable c de type var avec un nouvel objet ContexteCombat avec 0, List.of(ActionCombat.ATTAQUER, ActionCombat.ATTAQUER, ActionCombat.ATTAQUER), null, false.
        var c = new ContexteCombat(0, List.of(ActionCombat.ATTAQUER, ActionCombat.ATTAQUER, ActionCombat.ATTAQUER), null, false);
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : ActionEnnemi.RIPOSTE ; b.decider(c, new HasardReproductible(0)).action().
        assertEquals(ActionEnnemi.RIPOSTE, b.decider(c, new HasardReproductible(0)).action());
        // Vérifie que les deux résultats sont différents : ActionEnnemi.RIPOSTE ; b.decider(new ContexteCombat(0, List.of(), null, false), new HasardReproductible(0)).action().
        assertNotEquals(ActionEnnemi.RIPOSTE, b.decider(new ContexteCombat(0, List.of(), null, false), new HasardReproductible(0)).action());
        // Applique les dégâts au personnage en bornant sa vie à zéro ; arguments : 45. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 30 ; puissanceFinale(b).
        b.subirDegats(45); assertEquals(30, puissanceFinale(b));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    // Blesse le Gardien jusqu’à sa dernière phase et lit la puissance de la frappe préparée.
    private static int puissanceFinale(Boss b) {
        // Applique les dégâts au personnage en bornant sa vie à zéro ; arguments : 23.
        b.subirDegats(23);
        // Renvoie b.decider(new ContexteCombat(0, List.of(), ActionEnnemi.PREPARATION, false), new HasardReproductible(0)).puissance().
        return b.decider(new ContexteCombat(0, List.of(), ActionEnnemi.PREPARATION, false), new HasardReproductible(0)).puissance();
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : effet marque consomme une seule fois et onde traverse la garde.
    @Test void effetMarqueConsommeUneSeuleFoisEtOndeTraverseLaGarde() {
        // Initialise la variable p de type Partie avec squelette().
        Partie p = squelette();
        // Injecte un état tactique contrôlé pour tester une réaction précise sans dépendre d’un tirage. Paramètres : p, ActionEnnemi.ONDE, 16, List.of(new EffetCombat(EffetCombat.Type.MARQUE, HEROS, 3)).
        imposer(p, ActionEnnemi.ONDE, 16, List.of(new EffetCombat(EffetCombat.Type.MARQUE, HEROS, 3)));
        // Initialise la variable r de type var avec p.resoudre(ActionCombat.DEFENDRE). Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 87 ; p.getHeros().getVie().
        var r = p.resoudre(ActionCombat.DEFENDRE); assertEquals(87, p.getHeros().getVie());
        // Vérifie que la condition est vraie : types(r).contains(EXPIRATION).
        assertTrue(types(r).contains(EXPIRATION));
        // Vérifie que la condition est vraie : p.getTactique().getEffets().stream().noneMatch(e -> e.type() == EffetCombat.Type.MARQUE).
        assertTrue(p.getTactique().getEffets().stream().noneMatch(e -> e.type() == EffetCombat.Type.MARQUE));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : reprise json conserve exactement les prochains tours.
    @Test void repriseJsonConserveExactementLesProchainsTours() throws Exception {
        // Initialise la variable p de type Partie avec golem(). Résout entièrement la commande avec ses faits et états avant/après ; arguments : ActionCombat.OBSERVER.
        Partie p = golem(); p.resoudre(ActionCombat.OBSERVER);
        // Initialise la variable g de type var avec un nouvel objet GestionnaireSauvegarde. Initialise la variable f de type Path avec dossier.resolve("tactique.json"). Enregistre l’instantané dans le fichier demandé ; arguments : f, SauvegardePartie.capturer(p). Initialise la variable b de type Partie avec g.charger(f).
        var g = new GestionnaireSauvegarde(); Path f = dossier.resolve("tactique.json"); g.sauvegarder(f, SauvegardePartie.capturer(p)); Partie b = g.charger(f);
        // Parcourt la liste non modifiable contenant ActionCombat.DEFENDRE, ActionCombat.ATTAQUER, ActionCombat.PUISSANTE, ActionCombat.OBSERVER ; chaque élément est placé dans ActionCombat a.
        for (ActionCombat a : List.of(ActionCombat.DEFENDRE, ActionCombat.ATTAQUER, ActionCombat.PUISSANTE, ActionCombat.OBSERVER)) {
            // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : p.resoudre(a) ; b.resoudre(a). Vérifie l’égalité entre la valeur attendue et la valeur obtenue : SauvegardePartie.capturer(p) ; SauvegardePartie.capturer(b).
            assertEquals(p.resoudre(a), b.resoudre(a)); assertEquals(SauvegardePartie.capturer(p), SauvegardePartie.capturer(b));
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : lecteur elan effets et soin ennemi ne consomme pas de potion.
    @Test void lecteurElanEffetsEtSoinEnnemiNeConsommePasDePotion() {
        // Initialise la variable p de type Partie avec squelette(). Applique les dégâts au personnage en bornant sa vie à zéro ; arguments : 5.
        Partie p = squelette(); p.getSalleActuelle().getEnnemi().subirDegats(5);
        // Injecte un état tactique contrôlé pour tester une réaction précise sans dépendre d’un tirage. Paramètres : p, ActionEnnemi.SOIN, 0, List.of().
        imposer(p, ActionEnnemi.SOIN, 0, List.of());
        // Initialise la variable r de type var avec p.resoudre(ActionCombat.OBSERVER). Initialise « lecteur » avec un nouvel objet ControleurAnimation. Initialise la lecture visuelle du résultat déjà calculé ; arguments : r.
        var r = p.resoudre(ActionCombat.OBSERVER); ControleurAnimation lecteur = new ControleurAnimation(); lecteur.demarrer(r);
        // Avance l’horloge visuelle du nombre de millisecondes donné ; arguments : 100_000. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : r.apres().heros().potions() ; le stock de potions de « lecteur ».
        lecteur.avancer(100_000); assertEquals(r.apres().heros().potions(), lecteur.getPotions());
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : r.apres().tactique().effets() ; les effets actifs de « lecteur ». Vérifie l’égalité entre la valeur attendue et la valeur obtenue : r.apres().tactique().elan() ; l'élan disponible de « lecteur ».
        assertEquals(r.apres().tactique().effets(), lecteur.getEffets()); assertEquals(r.apres().tactique().elan(), lecteur.getElan());
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : l'instantané après le tour de la variable r ; SauvegardePartie.capturer(p).
        assertEquals(r.apres(), SauvegardePartie.capturer(p));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : choix uniques toutes les branches et persistance.
    @Test void choixUniquesToutesLesBranchesEtPersistance() throws Exception {
        // Parcourt ChoixEvenement.values() ; chaque élément est placé dans ChoixEvenement c.
        for (ChoixEvenement c : ChoixEvenement.values()) {
            // Initialise la variable p de type Partie avec squelette(). Joue le combat de test jusqu’à vaincre l’ennemi ; arguments : p. Emprunte le passage demandé selon les règles du modèle ; arguments : "arsenal".
            Partie p = squelette(); gagner(p); p.deplacer("arsenal");
            // Vérifie qu’un résultat est présent : la découverte disponible de la variable p. Initialise « stock » avec p.getHeros().getInventaire().getPotions().
            assertNotNull(p.getEvenementSalle()); int stock = p.getHeros().getInventaire().getPotions();
            // Résout définitivement le choix de découverte de salle ; arguments : c. Vérifie l’absence de résultat : la découverte disponible de la variable p.
            p.choisirEvenement(c); assertNull(p.getEvenementSalle());
            // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : stock + (c == ChoixEvenement.IGNORER ? 0 : c == ChoixEvenement.PRUDENT ? 1 : 2) ; p.getHeros().getInventaire().getPotions().
            assertEquals(stock + (c == ChoixEvenement.IGNORER ? 0 : c == ChoixEvenement.PRUDENT ? 1 : 2), p.getHeros().getInventaire().getPotions());
            // Initialise l'état courant avec SauvegardePartie.capturer(p). Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de () -> p.choisirEvenement(c).
            var etat = SauvegardePartie.capturer(p); assertThrows(IllegalStateException.class, () -> p.choisirEvenement(c));
            // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : l'état courant ; SauvegardePartie.capturer(p).
            assertEquals(etat, SauvegardePartie.capturer(p));
            // Initialise le chemin du fichier avec dossier.resolve(c + ".json"). Initialise la variable g de type var avec un nouvel objet GestionnaireSauvegarde. Enregistre l’instantané dans le fichier demandé ; arguments : fichier, etat.
            Path fichier = dossier.resolve(c + ".json"); var g = new GestionnaireSauvegarde(); g.sauvegarder(fichier, etat);
            // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : l'état courant ; SauvegardePartie.capturer(g.charger(fichier)).
            assertEquals(etat, SauvegardePartie.capturer(g.charger(fichier)));
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : tirages risques couvrent piege et succes sans mort.
    @Test void tiragesRisquesCouvrentPiegeEtSuccesSansMort() {
        // Parcourt la liste non modifiable contenant 0L, 2L ; chaque élément est placé dans long graine.
        for (long graine : List.of(0L, 2L)) {
            // Initialise la variable c de type CombatTactique avec un nouvel objet CombatTactique avec new HasardReproductible(graine). Initialise la variable h de type Heros avec un nouvel objet Heros. Applique les dégâts au personnage en bornant sa vie à zéro ; arguments : 99.
            CombatTactique c = new CombatTactique(new HasardReproductible(graine)); Heros h = new Heros(); h.subirDegats(99);
            // Initialise le texte à afficher avec c.choisir("tresor", ChoixEvenement.RISQUER, h). Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 1 ; les PV actuels du héros.
            String message = c.choisir("tresor", ChoixEvenement.RISQUER, h); assertEquals(1, h.getVie());
            // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : graine == 0 ; message.contains("piège").
            assertEquals(graine == 0, message.contains("piège"));
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : rune soigne avec plafond et ne donne pas de potion.
    @Test void runeSoigneAvecPlafondEtNeDonnePasDePotion() {
        // Parcourt ChoixEvenement.values() ; chaque élément est placé dans ChoixEvenement choix.
        for (ChoixEvenement choix : ChoixEvenement.values()) {
            // Initialise la variable c de type CombatTactique avec un nouvel objet CombatTactique avec new HasardReproductible(2).
            CombatTactique c = new CombatTactique(new HasardReproductible(2));
            // Initialise la variable h de type Heros avec un nouvel objet Heros. Applique les dégâts au personnage en bornant sa vie à zéro ; arguments : 20.
            Heros h = new Heros(); h.subirDegats(20);
            // Résout une découverte une seule fois, avec récompense et éventuel piège non mortel. Paramètres : "bibliotheque", choix, h.
            c.choisir("bibliotheque", choix, h);
            // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : choix == ChoixEvenement.IGNORER ? 80 : choix == ChoixEvenement.PRUDENT ? 95 : 100 ; les PV actuels du héros.
            assertEquals(choix == ChoixEvenement.IGNORER ? 80 : choix == ChoixEvenement.PRUDENT ? 95 : 100, h.getVie());
            // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 3 ; h.getInventaire().getPotions().
            assertEquals(3, h.getInventaire().getPotions());
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : etats invalides refuses.
    @Test void etatsInvalidesRefuses() {
        // Vérifie que l’opération lève l’exception attendue (IllegalArgumentException) lors de () -> new EtatTactique(-1, 0, null, List.of(), null, false, List.of(), Map.of()).
        assertThrows(IllegalArgumentException.class, () -> new EtatTactique(-1, 0, null, List.of(), null, false, List.of(), Map.of()));
        // Vérifie que l’opération lève l’exception attendue (IllegalArgumentException) lors de () -> new EtatTactique(1, 4, null, List.of(), null, false, List.of(), Map.of()).
        assertThrows(IllegalArgumentException.class, () -> new EtatTactique(1, 4, null, List.of(), null, false, List.of(), Map.of()));
        // Vérifie que l’opération lève l’exception attendue (IllegalArgumentException) lors de () -> new EffetCombat(EffetCombat.Type.LUCIDE, HEROS, 0).
        assertThrows(IllegalArgumentException.class, () -> new EffetCombat(EffetCombat.Type.LUCIDE, HEROS, 0));
        // Vérifie que l’opération lève l’exception attendue (UnsupportedOperationException) lors de () -> squelette().getTactique().getEffets().clear().
        assertThrows(UnsupportedOperationException.class, () -> squelette().getTactique().getEffets().clear());
        // Initialise « s » avec SauvegardePartie.capturer(new Partie()).
        EtatSauvegarde s = SauvegardePartie.capturer(new Partie());
        // Vérifie que l’opération lève l’exception attendue (IllegalArgumentException) lors de () -> SauvegardePartie.restaurer(new EtatSauvegarde(1, s.heros(), s.salleActuelle(), s.etat(), s.salles(), s.tactique())).
        assertThrows(IllegalArgumentException.class, () -> SauvegardePartie.restaurer(new EtatSauvegarde(1, s.heros(), s.salleActuelle(), s.etat(), s.salles(), s.tactique())));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : cent graines parcours direct et complet gagnants.
    @Test void centGrainesParcoursDirectEtCompletGagnants() {
        // Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int graine = 0; graine < 100; graine++. Parcourt la liste non modifiable contenant faux, vrai ; chaque élément est placé dans boolean detour.
        for (int graine = 0; graine < 100; graine++) for (boolean detour : List.of(false, true)) {
            // Initialise la variable p de type Partie avec squelette(graine). Joue le combat de test jusqu’à vaincre l’ennemi ; arguments : p. Emprunte le passage demandé selon les règles du modèle ; arguments : "arsenal". Ouvre une fois le coffre et attribue ses récompenses. Équipe l’arme demandée en passant par les contrôles du modèle ; arguments : Arme.ACIER.
            Partie p = squelette(graine); gagner(p); p.deplacer("arsenal"); p.ouvrirCoffre(); p.equiper(Arme.ACIER);
            // Résout définitivement le choix de découverte de salle ; arguments : ChoixEvenement.PRUDENT. Emprunte le passage demandé selon les règles du modèle ; arguments : "repos".
            p.choisirEvenement(ChoixEvenement.PRUDENT); p.deplacer("repos");
            // Si detour, Emprunte le passage demandé selon les règles du modèle ; arguments : "forge". Joue le combat de test jusqu’à vaincre l’ennemi ; arguments : p. Emprunte le passage demandé selon les règles du modèle ; arguments : "repos". Si si le sanctuaire peut être utilisé de la variable p, Utilise le sanctuaire pour restaurer la vie si le repos est disponible.
            if (detour) { p.deplacer("forge"); gagner(p); p.deplacer("repos"); if (p.peutReposer()) p.seReposer(); }
            // Emprunte le passage demandé selon les règles du modèle ; arguments : "bibliotheque". Joue le combat de test jusqu’à vaincre l’ennemi ; arguments : p. Résout définitivement le choix de découverte de salle ; arguments : ChoixEvenement.IGNORER.
            p.deplacer("bibliotheque"); gagner(p); p.choisirEvenement(ChoixEvenement.IGNORER);
            // Emprunte le passage demandé selon les règles du modèle ; arguments : "antichambre". Joue le combat de test jusqu’à vaincre l’ennemi ; arguments : p. Emprunte le passage demandé selon les règles du modèle ; arguments : "tresor". Ouvre une fois le coffre et attribue ses récompenses. Équipe l’arme demandée en passant par les contrôles du modèle ; arguments : Arme.RUNIQUE. Résout définitivement le choix de découverte de salle ; arguments : ChoixEvenement.RISQUER.
            p.deplacer("antichambre"); gagner(p); p.deplacer("tresor"); p.ouvrirCoffre(); p.equiper(Arme.RUNIQUE); p.choisirEvenement(ChoixEvenement.RISQUER);
            // Emprunte le passage demandé selon les règles du modèle ; arguments : "antichambre". Emprunte le passage demandé selon les règles du modèle ; arguments : "boss". Joue le combat de test jusqu’à vaincre l’ennemi ; arguments : p.
            p.deplacer("antichambre"); p.deplacer("boss"); gagner(p);
            // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : EtatPartie.VICTOIRE ; l'issue de la partie de la variable p.
            assertEquals(EtatPartie.VICTOIRE, p.getEtat(), "Graine " + graine);
            // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 0 ; p.getTactique().getElan(). Vérifie que la condition est vraie : p.getTactique().getEffets().isEmpty().
            assertEquals(0, p.getTactique().getElan()); assertTrue(p.getTactique().getEffets().isEmpty());
            // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : SauvegardePartie.capturer(p) ; SauvegardePartie.capturer(SauvegardePartie.restaurer(SauvegardePartie.capturer(p))).
            assertEquals(SauvegardePartie.capturer(p), SauvegardePartie.capturer(SauvegardePartie.restaurer(SauvegardePartie.capturer(p))));
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Joue un vrai combat de test jusqu’à sa fin, puis vérifie l’absence de défaite.
    static void gagner(Partie p) {
        // Initialise le nombre de réponses ennemies avec 0.
        int tours = 0;
        // Répète ce bloc tant que p.estEnCombat()) { assertTrue(tours++ < 100); assertTrue(p.resoudre(action(p)).valide().
        while (p.estEnCombat()) { assertTrue(tours++ < 100); assertTrue(p.resoudre(action(p)).valide()); }
        // Vérifie que les deux résultats sont différents : EtatPartie.DEFAITE ; l'issue de la partie de la variable p.
        assertNotEquals(EtatPartie.DEFAITE, p.getEtat());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    // Permet de consulter l'action annoncée.
    static ActionCombat action(Partie p) {
        // Initialise « d » avec p.getTactique().getDecision().action().
        ActionEnnemi d = p.getTactique().getDecision().action();
        // Si (p.getHeros().getVie() est inférieur ou égal à 45 et si une potion peut être consommée de la variable p), renvoie ActionCombat.POTION.
        if (p.getHeros().getVie() <= 45 && p.peutBoire()) return ActionCombat.POTION;
        // Si d est égal à ActionEnnemi.PREPARATION, renvoie ActionCombat.ATTAQUER.
        if (d == ActionEnnemi.PREPARATION) return ActionCombat.ATTAQUER;
        // Si d est égal à ActionEnnemi.RIPOSTE, renvoie p.getTactique().getElan() >= 2 ? ActionCombat.PUISSANTE : ActionCombat.OBSERVER.
        if (d == ActionEnnemi.RIPOSTE) return p.getTactique().getElan() >= 2 ? ActionCombat.PUISSANTE : ActionCombat.OBSERVER;
        // Si (d est égal à ActionEnnemi.FRAPPE_LOURDE ou d est égal à ActionEnnemi.ONDE), renvoie ActionCombat.DEFENDRE.
        if (d == ActionEnnemi.FRAPPE_LOURDE || d == ActionEnnemi.ONDE) return ActionCombat.DEFENDRE;
        // Renvoie p.getTactique().getElan() >= 2 ? ActionCombat.PUISSANTE : ActionCombat.ATTAQUER.
        return p.getTactique().getElan() >= 2 ? ActionCombat.PUISSANTE : ActionCombat.ATTAQUER;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    // Prépare une partie de test engagée contre le squelette avec une graine reproductible. Renvoie squelette(3).
    private static Partie squelette() { return squelette(3); }
    // Prépare une partie de test engagée contre le squelette avec une graine reproductible. Initialise la variable p de type Partie avec un nouvel objet Partie avec new HasardReproductible(graine). Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire". Renvoie la variable p.
    private static Partie squelette(long graine) { Partie p = new Partie(new HasardReproductible(graine)); p.deplacer("ossuaire"); return p; }
    // Prépare une partie de test engagée contre le golem de la forge. Initialise la variable p de type Partie avec squelette(). Joue le combat de test jusqu’à vaincre l’ennemi ; arguments : p. Emprunte le passage demandé selon les règles du modèle ; arguments : "forge". Renvoie la variable p.
    private static Partie golem() { Partie p = squelette(); gagner(p); p.deplacer("forge"); return p; }
    // Extrait la liste ordonnée des types de faits pour vérifier leur enchaînement. Renvoie r.evenements().stream().map(EvenementJeu::type).toList().
    private static List<EvenementJeu.Type> types(ResultatAction r) { return r.evenements().stream().map(EvenementJeu::type).toList(); }
    // Injecte un état tactique contrôlé pour tester une réaction précise sans dépendre d’un tirage.
    private static void imposer(Partie p, ActionEnnemi a, int puissance, List<EffetCombat> effets) {
        // Initialise « t » avec p.getTactique().capturer().
        var t = p.getTactique().capturer();
        // Recharge l’intention, les ressources et les effets tactiques fournis ; arguments : new EtatTactique(t.hasard(), t.elan(), new DecisionEnnemi(a, puissance, true), t.historique(), t.precedente(), t.soinUtilise(), effets, t.choix()).
        p.restaurerTactique(new EtatTactique(t.hasard(), t.elan(), new DecisionEnnemi(a, puissance, true), t.historique(), t.precedente(), t.soinUtilise(), effets, t.choix()));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
