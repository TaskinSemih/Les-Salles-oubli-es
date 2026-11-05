// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

// Rend disponible ControleurAnimation depuis fr.utbm.sallesoubliees.vue.
import fr.utbm.sallesoubliees.vue.ControleurAnimation;
// Rend disponible List depuis java.util.
import java.util.List;
// Importe l’annotation JUnit qui identifie une méthode de test.
import org.junit.jupiter.api.Test;
// Importe les assertions JUnit pour comparer les résultats et vérifier les erreurs attendues.
import static org.junit.jupiter.api.Assertions.*;
// Rend disponibles les types ou constantes de fr.utbm.sallesoubliees.modele.EvenementJeu.Type sans répéter leur nom complet.
import static fr.utbm.sallesoubliees.modele.EvenementJeu.Type.*;

// Déclare EvenementsTest, tests de l'ordre des faits et de leur lecture visuelle sans rejouer le modèle.
class EvenementsTest {
    // Teste le scénario suivant : ordre des faits et degats reels.
    @Test void ordreDesFaitsEtDegatsReels() {
        // Initialise la variable p de type Partie avec un nouvel objet Partie avec new HasardReproductible(3). Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire".
        Partie p = new Partie(new HasardReproductible(3)); p.deplacer("ossuaire");
        // Initialise la variable r de type ResultatAction avec p.resoudre(ActionCombat.ATTAQUER).
        ResultatAction r = p.resoudre(ActionCombat.ATTAQUER);
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : la liste non modifiable contenant ATTAQUE, DEGATS, ELAN, ATTAQUE, DEGATS, INTENTION ; types(r).
        assertEquals(List.of(ATTAQUE, DEGATS, ELAN, ATTAQUE, DEGATS, INTENTION), types(r));
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 11 ; r.evenements().get(1).valeur(). Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 6 ; r.evenements().get(4).valeur().
        assertEquals(11, r.evenements().get(1).valeur()); assertEquals(6, r.evenements().get(4).valeur());
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 100 ; r.avant().heros().vie(). Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 94 ; r.apres().heros().vie().
        assertEquals(100, r.avant().heros().vie()); assertEquals(94, r.apres().heros().vie());
        // Vérifie que l’opération lève l’exception attendue (UnsupportedOperationException) lors de () -> r.evenements().clear().
        assertThrows(UnsupportedOperationException.class, () -> r.evenements().clear());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    // Teste le scénario suivant : mort sans riposte et recompense unique.
    @Test void mortSansRiposteEtRecompenseUnique() {
        // Initialise la variable p de type Partie avec un nouvel objet Partie avec new HasardReproductible(3). Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire". Joue une attaque du héros et l’éventuelle réponse ennemie. Joue une attaque du héros et l’éventuelle réponse ennemie.
        Partie p = new Partie(new HasardReproductible(3)); p.deplacer("ossuaire"); p.attaquer(); p.attaquer();
        // Initialise la variable r de type ResultatAction avec p.resoudre(ActionCombat.ATTAQUER).
        ResultatAction r = p.resoudre(ActionCombat.ATTAQUER);
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : la liste non modifiable contenant ATTAQUE, DEGATS, ELAN, MORT, RECOMPENSE ; types(r).
        assertEquals(List.of(ATTAQUE, DEGATS, ELAN, MORT, RECOMPENSE), types(r));
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 8 ; r.evenements().get(1).valeur().
        assertEquals(8, r.evenements().get(1).valeur()); // PV restants, pas les dégâts théoriques.
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : r.avant().heros().vie() ; r.apres().heros().vie().
        assertEquals(r.avant().heros().vie(), r.apres().heros().vie());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    // Teste le scénario suivant : preparation du golem sans evenement de degats heros.
    @Test void preparationDuGolemSansEvenementDeDegatsHeros() {
        // Initialise la variable p de type Partie avec golem(). Initialise la variable r de type ResultatAction avec p.resoudre(ActionCombat.OBSERVER).
        Partie p = golem(); ResultatAction r = p.resoudre(ActionCombat.OBSERVER);
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : la liste non modifiable contenant OBSERVATION, EFFET, PREPARATION, INTENTION ; types(r).
        assertEquals(List.of(OBSERVATION, EFFET, PREPARATION, INTENTION), types(r));
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : la liste non modifiable contenant DEFENSE, ATTAQUE, DEGATS, ELAN, EFFET, INTENTION ; types(p.resoudre(ActionCombat.DEFENDRE)).
        assertEquals(List.of(DEFENSE, ATTAQUE, DEGATS, ELAN, EFFET, INTENTION), types(p.resoudre(ActionCombat.DEFENDRE)));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    // Teste le scénario suivant : potion annonce soin reel avant la riposte et le stock.
    @Test void potionAnnonceSoinReelAvantLaRiposteEtLeStock() {
        // Initialise la variable p de type Partie avec un nouvel objet Partie avec new HasardReproductible(3). Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire". Joue une attaque du héros et l’éventuelle réponse ennemie.
        Partie p = new Partie(new HasardReproductible(3)); p.deplacer("ossuaire"); p.attaquer();
        // Initialise la variable r de type ResultatAction avec p.resoudre(ActionCombat.POTION).
        ResultatAction r = p.resoudre(ActionCombat.POTION);
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : la liste non modifiable contenant le soin maximal de 35 PV par potion, ATTAQUE, DEGATS, INTENTION ; types(r). Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 6 ; r.evenements().getFirst().valeur().
        assertEquals(List.of(SOIN, ATTAQUE, DEGATS, INTENTION), types(r)); assertEquals(6, r.evenements().getFirst().valeur());
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 100 ; r.evenements().getFirst().vieApres(). Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 94 ; r.apres().heros().vie().
        assertEquals(100, r.evenements().getFirst().vieApres()); assertEquals(94, r.apres().heros().vie());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    // Teste le scénario suivant : invalide sans evenement ni modification.
    @Test void invalideSansEvenementNiModification() {
        // Initialise la variable p de type Partie avec un nouvel objet Partie avec new HasardReproductible(3). Initialise la variable r de type ResultatAction avec p.resoudre(ActionCombat.ATTAQUER).
        Partie p = new Partie(new HasardReproductible(3)); ResultatAction r = p.resoudre(ActionCombat.ATTAQUER);
        // Vérifie que la condition est fausse : si la commande est valide de la variable r. Vérifie que la condition est vraie : r.evenements().isEmpty(). Vérifie l’égalité entre la valeur attendue et la valeur obtenue : l'instantané avant le tour de la variable r ; l'instantané après le tour de la variable r.
        assertFalse(r.valide()); assertTrue(r.evenements().isEmpty()); assertEquals(r.avant(), r.apres());
        // Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire". Met à jour la variable r avec p.resoudre(ActionCombat.POTION).
        p.deplacer("ossuaire"); r = p.resoudre(ActionCombat.POTION);
        // Vérifie que la condition est fausse : si la commande est valide de la variable r. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : l'instantané avant le tour de la variable r ; SauvegardePartie.capturer(p).
        assertFalse(r.valide()); assertEquals(r.avant(), SauvegardePartie.capturer(p));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    // Teste le scénario suivant : rage du boss avant sa riposte.
    @Test void rageDuBossAvantSaRiposte() {
        // Initialise la variable p de type Partie avec un nouvel objet Partie avec new HasardReproductible(3). Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire". Joue le combat de test jusqu’à vaincre l’ennemi ; arguments : p. Emprunte le passage demandé selon les règles du modèle ; arguments : "arsenal". Ouvre une fois le coffre et attribue ses récompenses. Équipe l’arme demandée en passant par les contrôles du modèle ; arguments : Arme.ACIER.
        Partie p = new Partie(new HasardReproductible(3)); p.deplacer("ossuaire"); gagner(p); p.deplacer("arsenal"); p.ouvrirCoffre(); p.equiper(Arme.ACIER);
        // Emprunte le passage demandé selon les règles du modèle ; arguments : "repos". Emprunte le passage demandé selon les règles du modèle ; arguments : "bibliotheque". Joue le combat de test jusqu’à vaincre l’ennemi ; arguments : p. Emprunte le passage demandé selon les règles du modèle ; arguments : "antichambre". Joue le combat de test jusqu’à vaincre l’ennemi ; arguments : p.
        p.deplacer("repos"); p.deplacer("bibliotheque"); gagner(p); p.deplacer("antichambre"); gagner(p);
        // Emprunte le passage demandé selon les règles du modèle ; arguments : "tresor". Ouvre une fois le coffre et attribue ses récompenses. Équipe l’arme demandée en passant par les contrôles du modèle ; arguments : Arme.RUNIQUE.
        p.deplacer("tresor"); p.ouvrirCoffre(); p.equiper(Arme.RUNIQUE);
        // Répète ce bloc tant que p.peutBoire()) p.boirePotion(.
        while (p.peutBoire()) p.boirePotion();
        // Emprunte le passage demandé selon les règles du modèle ; arguments : "antichambre". Emprunte le passage demandé selon les règles du modèle ; arguments : "boss".
        p.deplacer("antichambre"); p.deplacer("boss");
        // Joue une attaque du héros et l’éventuelle réponse ennemie. Joue une attaque du héros et l’éventuelle réponse ennemie. Initialise la variable r de type ResultatAction avec p.resoudre(ActionCombat.ATTAQUER).
        p.attaquer(); p.attaquer(); ResultatAction r = p.resoudre(ActionCombat.ATTAQUER);
        // Vérifie que la condition est vraie : types(r).contains(PHASE). Vérifie que la condition est vraie : types(r).contains(RAGE).
        assertTrue(types(r).contains(PHASE)); assertTrue(types(r).contains(RAGE));
        // Vérifie que la condition est vraie : types(r).indexOf(DEGATS) < types(r).indexOf(RAGE).
        assertTrue(types(r).indexOf(DEGATS) < types(r).indexOf(RAGE));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    // Teste le scénario suivant : lecteur anime la copie et ne rejoue jamais le modele.
    @Test void lecteurAnimeLaCopieEtNeRejoueJamaisLeModele() {
        // Initialise la variable p de type Partie avec golem(). Initialise la variable r de type ResultatAction avec p.resoudre(ActionCombat.ATTAQUER).
        Partie p = golem(); ResultatAction r = p.resoudre(ActionCombat.ATTAQUER);
        // Initialise « fin » avec SauvegardePartie.capturer(p). Initialise la variable a de type ControleurAnimation avec un nouvel objet ControleurAnimation. Initialise la lecture visuelle du résultat déjà calculé ; arguments : r.
        EtatSauvegarde fin = SauvegardePartie.capturer(p); ControleurAnimation a = new ControleurAnimation(); a.demarrer(r);
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 48 ; a.getVieEnnemi(). Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de () -> a.demarrer(r).
        assertEquals(48, a.getVieEnnemi()); assertThrows(IllegalStateException.class, () -> a.demarrer(r));
        // Avance l’horloge visuelle du nombre de millisecondes donné ; arguments : 260. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 40 ; a.getVieEnnemi().
        a.avancer(260); assertEquals(40, a.getVieEnnemi());
        // Avance l’horloge visuelle du nombre de millisecondes donné ; arguments : 10_000. Vérifie que la condition est fausse : si la séquence visuelle est en cours de « a ». Vérifie l’égalité entre la valeur attendue et la valeur obtenue : fin ; SauvegardePartie.capturer(p).
        a.avancer(10_000); assertFalse(a.estActive()); assertEquals(fin, SauvegardePartie.capturer(p));
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : fin.heros().vie() ; a.getVieHeros(). Vérifie l’égalité entre la valeur attendue et la valeur obtenue : fin.heros().potions() ; le stock de potions de « a ».
        assertEquals(fin.heros().vie(), a.getVieHeros()); assertEquals(fin.heros().potions(), a.getPotions());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    // Teste le scénario suivant : defense expire meme en mode reduit et stabilisation exacte.
    @Test void defenseExpireMemeEnModeReduitEtStabilisationExacte() {
        // Initialise la variable p de type Partie avec golem(). Joue une attaque du héros et l’éventuelle réponse ennemie. Initialise la variable r de type ResultatAction avec p.resoudre(ActionCombat.DEFENDRE).
        Partie p = golem(); p.attaquer(); ResultatAction r = p.resoudre(ActionCombat.DEFENDRE);
        // Initialise la variable a de type ControleurAnimation avec un nouvel objet ControleurAnimation. Modifie seulement la vitesse et l’amplitude de présentation ; arguments : true. Initialise la lecture visuelle du résultat déjà calculé ; arguments : r. Vérifie que la condition est vraie : si la défense est affichée de « a ».
        ControleurAnimation a = new ControleurAnimation(); a.setReduite(true); a.demarrer(r); assertTrue(a.estDefense());
        // Aligne immédiatement la présentation sur l’état final. Vérifie que la condition est fausse : si la séquence visuelle est en cours de « a ». Vérifie que la condition est fausse : si la défense est affichée de « a ». Vérifie l’égalité entre la valeur attendue et la valeur obtenue : r.apres().heros().vie() ; a.getVieHeros().
        a.finirImmediatement(); assertFalse(a.estActive()); assertFalse(a.estDefense()); assertEquals(r.apres().heros().vie(), a.getVieHeros());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    // Extrait la liste ordonnée des types de faits pour vérifier leur enchaînement. Renvoie r.evenements().stream().map(EvenementJeu::type).toList().
    private static List<EvenementJeu.Type> types(ResultatAction r) { return r.evenements().stream().map(EvenementJeu::type).toList(); }
    // Prépare une partie de test engagée contre le golem de la forge. Initialise la variable p de type Partie avec un nouvel objet Partie avec new HasardReproductible(3). Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire". Joue le combat de test jusqu’à vaincre l’ennemi ; arguments : p. Emprunte le passage demandé selon les règles du modèle ; arguments : "forge". Renvoie la variable p.
    private static Partie golem() { Partie p = new Partie(new HasardReproductible(3)); p.deplacer("ossuaire"); gagner(p); p.deplacer("forge"); return p; }
    // Joue un vrai combat de test jusqu’à sa fin, puis vérifie l’absence de défaite. Répète ce bloc tant que p.estEnCombat()) { if (p.getHeros().getVie() < 45 && p.peutBoire()) p.boirePotion(); else p.attaquer(.
    private static void gagner(Partie p) { while (p.estEnCombat()) { if (p.getHeros().getVie() < 45 && p.peutBoire()) p.boirePotion(); else p.attaquer(); } }
// Ferme le bloc ou la liste de données commencé plus haut.
}
