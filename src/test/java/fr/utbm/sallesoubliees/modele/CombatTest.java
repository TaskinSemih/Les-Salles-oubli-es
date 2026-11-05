// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

// Importe l’annotation JUnit qui identifie une méthode de test.
import org.junit.jupiter.api.Test;
// Importe les assertions JUnit pour comparer les résultats et vérifier les erreurs attendues.
import static org.junit.jupiter.api.Assertions.*;

// Déclare CombatTest, tests des dégâts, de la vie, des réponses ennemies et des actions refusées.
class CombatTest {
    // Teste le scénario suivant : degats ont un minimum et refusent valeurs negatives.
    @Test void degatsOntUnMinimumEtRefusentValeursNegatives() {
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 1 ; GestionnaireCombat.calculerDegats(3, 50).
        assertEquals(1, GestionnaireCombat.calculerDegats(3, 50));
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 9 ; GestionnaireCombat.calculerDegats(12, 3).
        assertEquals(9, GestionnaireCombat.calculerDegats(12, 3));
        // Vérifie que l’opération lève l’exception attendue (IllegalArgumentException) lors de () -> GestionnaireCombat.calculerDegats(-1, 0).
        assertThrows(IllegalArgumentException.class, () -> GestionnaireCombat.calculerDegats(-1, 0));
        // Vérifie que l’opération lève l’exception attendue (IllegalArgumentException) lors de () -> GestionnaireCombat.calculerDegats(1, -1).
        assertThrows(IllegalArgumentException.class, () -> GestionnaireCombat.calculerDegats(1, -1));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : vie bornee et grand soin sans depassement.
    @Test void vieBorneeEtGrandSoinSansDepassement() {
        // Initialise la variable h de type Heros avec un nouvel objet Heros. Applique les dégâts au personnage en bornant sa vie à zéro ; arguments : Integer.MAX_VALUE.
        Heros h = new Heros(); h.subirDegats(Integer.MAX_VALUE);
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 0 ; les PV actuels du héros. Vérifie que la condition est vraie : si les PV valent zéro du héros.
        assertEquals(0, h.getVie()); assertTrue(h.estMort());
        // Restaure les PV sans dépasser le maximum du personnage ; arguments : Integer.MAX_VALUE. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 100 ; les PV actuels du héros.
        h.soigner(Integer.MAX_VALUE); assertEquals(100, h.getVie());
        // Vérifie que l’opération lève l’exception attendue (IllegalArgumentException) lors de () -> h.subirDegats(-1).
        assertThrows(IllegalArgumentException.class, () -> h.subirDegats(-1));
        // Vérifie que l’opération lève l’exception attendue (IllegalArgumentException) lors de () -> h.soigner(-1).
        assertThrows(IllegalArgumentException.class, () -> h.soigner(-1));
        // Vérifie que l’opération lève l’exception attendue (IllegalArgumentException) lors de () -> h.restaurerVie(101).
        assertThrows(IllegalArgumentException.class, () -> h.restaurerVie(101));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : defense expire apres une reponse.
    @Test void defenseExpireApresUneReponse() {
        // Initialise la variable p de type Partie avec un nouvel objet Partie avec new HasardReproductible(3). Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire". Joue une défense pour la réponse qui suit.
        Partie p = new Partie(new HasardReproductible(3)); p.deplacer("ossuaire"); p.defendre();
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 99 ; p.getHeros().getVie().
        assertEquals(99, p.getHeros().getVie());
        // Joue une attaque du héros et l’éventuelle réponse ennemie. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 93 ; p.getHeros().getVie().
        p.attaquer(); assertEquals(93, p.getHeros().getVie());
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 2 ; p.getSalleActuelle().getEnnemi().getTours().
        assertEquals(2, p.getSalleActuelle().getEnnemi().getTours());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : golem alterne preparation et frappe et defense expire pendant preparation.
    @Test void golemAlternePreparationEtFrappeEtDefenseExpirePendantPreparation() {
        // Initialise la variable h de type Heros avec un nouvel objet Heros. Initialise la variable g de type Golem avec un nouvel objet Golem.
        Heros h = new Heros(); Golem g = new Golem();
        // Joue la réponse du résolveur simple avec ou sans défense temporaire ; arguments : h, g, true. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 100 ; les PV actuels du héros.
        GestionnaireCombat.repondre(h, g, true); assertEquals(100, h.getVie());
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 22 ; la puissance du prochain coup du résolveur simple de la variable g.
        assertEquals(22, g.puissanceProchaineAttaque());
        // Joue la réponse du résolveur simple avec ou sans défense temporaire ; arguments : h, g, false. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 81 ; les PV actuels du héros.
        GestionnaireCombat.repondre(h, g, false); assertEquals(81, h.getVie());
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 0 ; la puissance du prochain coup du résolveur simple de la variable g.
        assertEquals(0, g.puissanceProchaineAttaque());
        // Joue la réponse du résolveur simple avec ou sans défense temporaire ; arguments : h, g, false.
        GestionnaireCombat.repondre(h, g, false);
        // Joue la réponse du résolveur simple avec ou sans défense temporaire ; arguments : h, g, true. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 70 ; les PV actuels du héros.
        GestionnaireCombat.repondre(h, g, true); assertEquals(70, h.getVie());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : mage ignore armure mais respecte defense.
    @Test void mageIgnoreArmureMaisRespecteDefense() {
        // Initialise la variable h de type Heros avec un nouvel objet Heros. Initialise « m » avec un nouvel objet Mage.
        Heros h = new Heros(); Mage m = new Mage();
        // Joue la réponse du résolveur simple avec ou sans défense temporaire ; arguments : h, m, false. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 89 ; les PV actuels du héros.
        GestionnaireCombat.repondre(h, m, false); assertEquals(89, h.getVie());
        // Joue la réponse du résolveur simple avec ou sans défense temporaire ; arguments : h, m, true. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 86 ; les PV actuels du héros.
        GestionnaireCombat.repondre(h, m, true); assertEquals(86, h.getVie());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : boss enrage des la moitie de sa vie.
    @Test void bossEnrageDesLaMoitieDeSaVie() {
        // Initialise la variable b de type Boss avec un nouvel objet Boss. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 16 ; la puissance du prochain coup du résolveur simple de « b ».
        Boss b = new Boss(); assertEquals(16, b.puissanceProchaineAttaque());
        // Applique les dégâts au personnage en bornant sa vie à zéro ; arguments : 44. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 16 ; la puissance du prochain coup du résolveur simple de « b ».
        b.subirDegats(44); assertEquals(16, b.puissanceProchaineAttaque());
        // Applique les dégâts au personnage en bornant sa vie à zéro ; arguments : 1. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 24 ; la puissance du prochain coup du résolveur simple de « b ».
        b.subirDegats(1); assertEquals(24, b.puissanceProchaineAttaque());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : ennemi tue ne riposte pas et ne donne qu une recompense.
    @Test void ennemiTueNeRipostePasEtNeDonneQuUneRecompense() {
        // Initialise la variable p de type Partie avec un nouvel objet Partie avec new HasardReproductible(3). Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire". Joue une attaque du héros et l’éventuelle réponse ennemie. Joue une attaque du héros et l’éventuelle réponse ennemie.
        Partie p = new Partie(new HasardReproductible(3)); p.deplacer("ossuaire"); p.attaquer(); p.attaquer();
        // Initialise les points de vie actuels avec p.getHeros().getVie(). Joue une attaque du héros et l’éventuelle réponse ennemie.
        int vie = p.getHeros().getVie(); p.attaquer();
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : les points de vie actuels ; p.getHeros().getVie().
        assertEquals(vie, p.getHeros().getVie());
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 2 ; p.getSalleActuelle().getEnnemi().getTours().
        assertEquals(2, p.getSalleActuelle().getEnnemi().getTours());
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 4 ; p.getHeros().getInventaire().getPotions().
        assertEquals(4, p.getHeros().getInventaire().getPotions());
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de p::attaquer.
        assertThrows(IllegalStateException.class, p::attaquer);
        // Consomme une potion et joue l’éventuelle réponse ennemie. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 3 ; p.getHeros().getInventaire().getPotions().
        p.boirePotion(); assertEquals(3, p.getHeros().getInventaire().getPotions());
        // Emprunte le passage demandé selon les règles du modèle ; arguments : "entree". Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire".
        p.deplacer("entree"); p.deplacer("ossuaire");
        // Vérifie que la condition est fausse : si un combat est actif de la variable p. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 3 ; p.getHeros().getInventaire().getPotions().
        assertFalse(p.estEnCombat()); assertEquals(3, p.getHeros().getInventaire().getPotions());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : potion soigne avec plafond et consomme un tour en combat.
    @Test void potionSoigneAvecPlafondEtConsommeUnTourEnCombat() {
        // Initialise la variable p de type Partie avec un nouvel objet Partie avec new HasardReproductible(3). Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire". Joue une attaque du héros et l’éventuelle réponse ennemie.
        Partie p = new Partie(new HasardReproductible(3)); p.deplacer("ossuaire"); p.attaquer();
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 94 ; p.getHeros().getVie(). Consomme une potion et joue l’éventuelle réponse ennemie.
        assertEquals(94, p.getHeros().getVie()); p.boirePotion();
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 94 ; p.getHeros().getVie().
        assertEquals(94, p.getHeros().getVie()); // 100 après le soin, puis 6 dégâts.
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 2 ; p.getHeros().getInventaire().getPotions().
        assertEquals(2, p.getHeros().getInventaire().getPotions());
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 2 ; p.getSalleActuelle().getEnnemi().getTours().
        assertEquals(2, p.getSalleActuelle().getEnnemi().getTours());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : actions invalides ne modifient rien et ne consomment pas de tour.
    @Test void actionsInvalidesNeModifientRienEtNeConsommentPasDeTour() {
        // Initialise la variable p de type Partie avec un nouvel objet Partie avec new HasardReproductible(3). Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire".
        Partie p = new Partie(new HasardReproductible(3)); p.deplacer("ossuaire");
        // Initialise l'instantané avant l'action avec SauvegardePartie.capturer(p).
        EtatSauvegarde avant = SauvegardePartie.capturer(p);
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de p::boirePotion.
        assertThrows(IllegalStateException.class, p::boirePotion);
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de () -> p.deplacer("entree").
        assertThrows(IllegalStateException.class, () -> p.deplacer("entree"));
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de () -> p.equiper(Arme.ROUILLEE).
        assertThrows(IllegalStateException.class, () -> p.equiper(Arme.ROUILLEE));
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de p::ouvrirCoffre.
        assertThrows(IllegalStateException.class, p::ouvrirCoffre);
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de p::seReposer.
        assertThrows(IllegalStateException.class, p::seReposer);
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : l'instantané avant l'action ; SauvegardePartie.capturer(p).
        assertEquals(avant, SauvegardePartie.capturer(p));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : absence de potion ne donne pas de tour ennemi.
    @Test void absenceDePotionNeDonnePasDeTourEnnemi() {
        // Initialise la variable p de type Partie avec un nouvel objet Partie avec new HasardReproductible(3). Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire". Joue une attaque du héros et l’éventuelle réponse ennemie.
        Partie p = new Partie(new HasardReproductible(3)); p.deplacer("ossuaire"); p.attaquer();
        // Répète ce bloc tant que p.getHeros().getInventaire().getPotions() est supérieur à 0) { p.getHeros().subirDegats(1); p.boirePotion(.
        while (p.getHeros().getInventaire().getPotions() > 0) { p.getHeros().subirDegats(1); p.boirePotion(); }
        // Initialise l'instantané avant l'action avec SauvegardePartie.capturer(p).
        EtatSauvegarde avant = SauvegardePartie.capturer(p);
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de p::boirePotion.
        assertThrows(IllegalStateException.class, p::boirePotion);
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : l'instantané avant l'action ; SauvegardePartie.capturer(p).
        assertEquals(avant, SauvegardePartie.capturer(p));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : defaite interdit toutes les actions et autres tours.
    @Test void defaiteInterditToutesLesActionsEtAutresTours() {
        // Initialise la variable p de type Partie avec un nouvel objet Partie avec new HasardReproductible(3). Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire".
        Partie p = new Partie(new HasardReproductible(3)); p.deplacer("ossuaire");
        // Répète ce bloc tant que p.estEnCours()) p.defendre(.
        while (p.estEnCours()) p.defendre();
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : EtatPartie.DEFAITE ; l'issue de la partie de la variable p. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 0 ; p.getHeros().getVie().
        assertEquals(EtatPartie.DEFAITE, p.getEtat()); assertEquals(0, p.getHeros().getVie());
        // Initialise l'instantané avant l'action avec SauvegardePartie.capturer(p).
        EtatSauvegarde avant = SauvegardePartie.capturer(p);
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de p::attaquer.
        assertThrows(IllegalStateException.class, p::attaquer);
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de p::defendre.
        assertThrows(IllegalStateException.class, p::defendre);
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de p::boirePotion.
        assertThrows(IllegalStateException.class, p::boirePotion);
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de p::ouvrirCoffre.
        assertThrows(IllegalStateException.class, p::ouvrirCoffre);
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de p::seReposer.
        assertThrows(IllegalStateException.class, p::seReposer);
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de () -> p.equiper(Arme.ROUILLEE).
        assertThrows(IllegalStateException.class, () -> p.equiper(Arme.ROUILLEE));
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de () -> p.deplacer("entree").
        assertThrows(IllegalStateException.class, () -> p.deplacer("entree"));
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : l'instantané avant l'action ; SauvegardePartie.capturer(p).
        assertEquals(avant, SauvegardePartie.capturer(p));
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : l'instantané avant l'action ; SauvegardePartie.capturer(SauvegardePartie.restaurer(avant)).
        assertEquals(avant, SauvegardePartie.capturer(SauvegardePartie.restaurer(avant)));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
