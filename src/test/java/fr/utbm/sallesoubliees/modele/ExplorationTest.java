// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

// Importe l’annotation JUnit qui identifie une méthode de test.
import org.junit.jupiter.api.Test;
// Importe les assertions JUnit pour comparer les résultats et vérifier les erreurs attendues.
import static org.junit.jupiter.api.Assertions.*;

// Déclare ExplorationTest, tests des passages, coffres, armes, repos et d'un vrai parcours gagnant dans neuf salles.
class ExplorationTest {
    // Teste le scénario suivant : deplacements suivent les passages et collections protegees.
    @Test void deplacementsSuiventLesPassagesEtCollectionsProtegees() {
        // Initialise la variable p de type Partie avec un nouvel objet Partie avec new HasardReproductible(3).
        Partie p = new Partie(new HasardReproductible(3));
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 9 ; p.getDonjon().getSalles().size().
        assertEquals(9, p.getDonjon().getSalles().size());
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de () -> p.deplacer("boss").
        assertThrows(IllegalStateException.class, () -> p.deplacer("boss"));
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de () -> p.deplacer("inconnue").
        assertThrows(IllegalStateException.class, () -> p.deplacer("inconnue"));
        // Vérifie que l’opération lève l’exception attendue (UnsupportedOperationException) lors de () -> p.getSalleActuelle().getPassages().add("boss").
        assertThrows(UnsupportedOperationException.class, () -> p.getSalleActuelle().getPassages().add("boss"));
        // Vérifie que l’opération lève l’exception attendue (UnsupportedOperationException) lors de () -> p.getDonjon().getSalles().clear().
        assertThrows(UnsupportedOperationException.class, () -> p.getDonjon().getSalles().clear());
        // Vérifie que l’opération lève l’exception attendue (UnsupportedOperationException) lors de () -> p.getHeros().getInventaire().getArmes().add(Arme.RUNIQUE).
        assertThrows(UnsupportedOperationException.class, () -> p.getHeros().getInventaire().getArmes().add(Arme.RUNIQUE));
        // Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire". Vérifie que la condition est vraie : p.getSalleActuelle().estVisitee().
        p.deplacer("ossuaire"); assertTrue(p.getSalleActuelle().estVisitee());
        // Parcourt p.getDonjon().getSalles() ; chaque élément est placé dans Salle salle.
        for (Salle salle : p.getDonjon().getSalles()) {
            // Parcourt les passages autorisés de la salle courante ; chaque élément est placé dans String voisin. Vérifie que la condition est vraie : p.getDonjon().getSalle(voisin).getPassages().contains(salle.getId()).
            for (String voisin : salle.getPassages()) assertTrue(p.getDonjon().getSalle(voisin).getPassages().contains(salle.getId()));
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : coffre unique et equipement sans cumul.
    @Test void coffreUniqueEtEquipementSansCumul() {
        // Initialise la variable p de type Partie avec versArsenal().
        Partie p = versArsenal();
        // Ouvre une fois le coffre et attribue ses récompenses. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 6 ; p.getHeros().getInventaire().getPotions().
        p.ouvrirCoffre(); assertEquals(6, p.getHeros().getInventaire().getPotions());
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de p::ouvrirCoffre.
        assertThrows(IllegalStateException.class, p::ouvrirCoffre);
        // Équipe l’arme demandée en passant par les contrôles du modèle ; arguments : Arme.ACIER. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 17 ; p.getHeros().getAttaque().
        p.equiper(Arme.ACIER); assertEquals(17, p.getHeros().getAttaque());
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de () -> p.equiper(Arme.ACIER).
        assertThrows(IllegalStateException.class, () -> p.equiper(Arme.ACIER));
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de () -> p.equiper(Arme.RUNIQUE).
        assertThrows(IllegalStateException.class, () -> p.equiper(Arme.RUNIQUE));
        // Équipe l’arme demandée en passant par les contrôles du modèle ; arguments : Arme.ROUILLEE. Équipe l’arme demandée en passant par les contrôles du modèle ; arguments : Arme.ACIER. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 17 ; p.getHeros().getAttaque().
        p.equiper(Arme.ROUILLEE); p.equiper(Arme.ACIER); assertEquals(17, p.getHeros().getAttaque());
        // Emprunte le passage demandé selon les règles du modèle ; arguments : "repos". Emprunte le passage demandé selon les règles du modèle ; arguments : "arsenal".
        p.deplacer("repos"); p.deplacer("arsenal");
        // Vérifie que la condition est fausse : si le coffre actuel peut être ouvert de la variable p. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 6 ; p.getHeros().getInventaire().getPotions().
        assertFalse(p.peutOuvrir()); assertEquals(6, p.getHeros().getInventaire().getPotions());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : repos ne se consomme pas apleine vie et ne sert qu une fois.
    @Test void reposNeSeConsommePasAPleineVieEtNeSertQuUneFois() {
        // Initialise la variable p de type Partie avec versArsenal(). Consomme une potion et joue l’éventuelle réponse ennemie. Emprunte le passage demandé selon les règles du modèle ; arguments : "repos".
        Partie p = versArsenal(); p.boirePotion(); p.deplacer("repos");
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de p::seReposer. Vérifie que la condition est fausse : p.getSalleActuelle().estUtilisee().
        assertThrows(IllegalStateException.class, p::seReposer); assertFalse(p.getSalleActuelle().estUtilisee());
        // Emprunte le passage demandé selon les règles du modèle ; arguments : "bibliotheque". Joue le combat de test jusqu’à vaincre l’ennemi ; arguments : p. Emprunte le passage demandé selon les règles du modèle ; arguments : "repos".
        p.deplacer("bibliotheque"); gagnerCombat(p); p.deplacer("repos");
        // Utilise le sanctuaire pour restaurer la vie si le repos est disponible. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 100 ; p.getHeros().getVie().
        p.seReposer(); assertEquals(100, p.getHeros().getVie());
        // Vérifie que la condition est vraie : p.getSalleActuelle().estUtilisee().
        assertTrue(p.getSalleActuelle().estUtilisee());
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de p::seReposer.
        assertThrows(IllegalStateException.class, p::seReposer);
        // Emprunte le passage demandé selon les règles du modèle ; arguments : "forge". Joue le combat de test jusqu’à vaincre l’ennemi ; arguments : p. Emprunte le passage demandé selon les règles du modèle ; arguments : "repos".
        p.deplacer("forge"); gagnerCombat(p); p.deplacer("repos");
        // Vérifie que la condition est fausse : si le sanctuaire peut être utilisé de la variable p.
        assertFalse(p.peutReposer());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : parcours complet gagnant avec exploration des neuf salles.
    @Test void parcoursCompletGagnantAvecExplorationDesNeufSalles() {
        // Initialise la variable p de type Partie avec versArsenal(). Ouvre une fois le coffre et attribue ses récompenses. Équipe l’arme demandée en passant par les contrôles du modèle ; arguments : Arme.ACIER.
        Partie p = versArsenal(); p.ouvrirCoffre(); p.equiper(Arme.ACIER);
        // Emprunte le passage demandé selon les règles du modèle ; arguments : "repos". Emprunte le passage demandé selon les règles du modèle ; arguments : "forge". Joue le combat de test jusqu’à vaincre l’ennemi ; arguments : p.
        p.deplacer("repos"); p.deplacer("forge"); gagnerCombat(p);
        // Emprunte le passage demandé selon les règles du modèle ; arguments : "repos". Utilise le sanctuaire pour restaurer la vie si le repos est disponible.
        p.deplacer("repos"); p.seReposer();
        // Emprunte le passage demandé selon les règles du modèle ; arguments : "bibliotheque". Joue le combat de test jusqu’à vaincre l’ennemi ; arguments : p.
        p.deplacer("bibliotheque"); gagnerCombat(p);
        // Emprunte le passage demandé selon les règles du modèle ; arguments : "antichambre". Joue le combat de test jusqu’à vaincre l’ennemi ; arguments : p.
        p.deplacer("antichambre"); gagnerCombat(p);
        // Emprunte le passage demandé selon les règles du modèle ; arguments : "tresor". Ouvre une fois le coffre et attribue ses récompenses. Équipe l’arme demandée en passant par les contrôles du modèle ; arguments : Arme.RUNIQUE.
        p.deplacer("tresor"); p.ouvrirCoffre(); p.equiper(Arme.RUNIQUE);
        // Emprunte le passage demandé selon les règles du modèle ; arguments : "antichambre". Emprunte le passage demandé selon les règles du modèle ; arguments : "boss". Joue le combat de test jusqu’à vaincre l’ennemi ; arguments : p.
        p.deplacer("antichambre"); p.deplacer("boss"); gagnerCombat(p);
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : EtatPartie.VICTOIRE ; l'issue de la partie de la variable p. Vérifie que la condition est vraie : p.getHeros().getVie() > 0.
        assertEquals(EtatPartie.VICTOIRE, p.getEtat()); assertTrue(p.getHeros().getVie() > 0);
        // Vérifie que la condition est vraie : p.getDonjon().getSalles().stream().allMatch(Salle::estVisitee).
        assertTrue(p.getDonjon().getSalles().stream().allMatch(Salle::estVisitee));
        // Initialise « fin » avec SauvegardePartie.capturer(p).
        EtatSauvegarde fin = SauvegardePartie.capturer(p);
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : fin ; SauvegardePartie.capturer(SauvegardePartie.restaurer(fin)).
        assertEquals(fin, SauvegardePartie.capturer(SauvegardePartie.restaurer(fin)));
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de p::attaquer.
        assertThrows(IllegalStateException.class, p::attaquer);
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de p::defendre.
        assertThrows(IllegalStateException.class, p::defendre);
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de p::boirePotion.
        assertThrows(IllegalStateException.class, p::boirePotion);
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de () -> p.deplacer("antichambre").
        assertThrows(IllegalStateException.class, () -> p.deplacer("antichambre"));
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de () -> p.equiper(Arme.ROUILLEE).
        assertThrows(IllegalStateException.class, () -> p.equiper(Arme.ROUILLEE));
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de p::ouvrirCoffre.
        assertThrows(IllegalStateException.class, p::ouvrirCoffre);
        // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de p::seReposer.
        assertThrows(IllegalStateException.class, p::seReposer);
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : fin ; SauvegardePartie.capturer(p).
        assertEquals(fin, SauvegardePartie.capturer(p));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : nouvelle partie ne partage aucun etat.
    @Test void nouvellePartieNePartageAucunEtat() {
        // Initialise la variable a de type Partie avec versArsenal(). Ouvre une fois le coffre et attribue ses récompenses. Initialise la variable b de type Partie avec un nouvel objet Partie avec new HasardReproductible(3).
        Partie a = versArsenal(); a.ouvrirCoffre(); Partie b = new Partie(new HasardReproductible(3));
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 100 ; b.getHeros().getVie(). Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 3 ; b.getHeros().getInventaire().getPotions().
        assertEquals(100, b.getHeros().getVie()); assertEquals(3, b.getHeros().getInventaire().getPotions());
        // Vérifie que la condition est fausse : b.getDonjon().getSalle("arsenal").estUtilisee().
        assertFalse(b.getDonjon().getSalle("arsenal").estUtilisee());
        // Vérifie que la condition est fausse : b.getDonjon().getSalle("ossuaire").getEnnemi().estMort().
        assertFalse(b.getDonjon().getSalle("ossuaire").getEnnemi().estMort());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Prépare une partie de test qui bat le squelette puis atteint l’arsenal.
    private static Partie versArsenal() {
        // Initialise la variable p de type Partie avec un nouvel objet Partie avec new HasardReproductible(3). Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire". Joue le combat de test jusqu’à vaincre l’ennemi ; arguments : p. Emprunte le passage demandé selon les règles du modèle ; arguments : "arsenal". Renvoie la variable p.
        Partie p = new Partie(new HasardReproductible(3)); p.deplacer("ossuaire"); gagnerCombat(p); p.deplacer("arsenal"); return p;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Joue un vrai combat de test en attaquant, soignant ou défendant selon la situation.
    private static void gagnerCombat(Partie p) {
        // Initialise « actions » avec 0.
        int actions = 0;
        // Répète ce bloc tant que si un combat est actif de la variable p.
        while (p.estEnCombat()) {
            // Vérifie que la condition est vraie : actions++ < 100, le texte "Le combat doit se terminer.".
            assertTrue(actions++ < 100, "Le combat doit se terminer.");
            // Initialise la variable e de type Ennemi avec p.getSalleActuelle().getEnnemi().
            Ennemi e = p.getSalleActuelle().getEnnemi();
            // Initialise « degats » avec GestionnaireCombat.calculerDegats(p.getHeros().getAttaque(), e.getArmure()).
            int degats = GestionnaireCombat.calculerDegats(p.getHeros().getAttaque(), e.getArmure());
            // Si les PV actuels de la variable e est inférieur ou égal à degats, Joue une attaque du héros et l’éventuelle réponse ennemie.
            if (e.getVie() <= degats) p.attaquer();
            // Si (p.getHeros().getVie() est inférieur ou égal à 40 et si une potion peut être consommée de la variable p), Consomme une potion et joue l’éventuelle réponse ennemie.
            else if (p.getHeros().getVie() <= 40 && p.peutBoire()) p.boirePotion();
            // Si (e instanceof Golem et la puissance du prochain coup du résolveur simple de la variable e est supérieur à 0), Joue une défense pour la réponse qui suit.
            else if (e instanceof Golem && e.puissanceProchaineAttaque() > 0) p.defendre();
            // Dans le cas contraire, Joue une attaque du héros et l’éventuelle réponse ennemie.
            else p.attaquer();
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Vérifie que les deux résultats sont différents : EtatPartie.DEFAITE ; l'issue de la partie de la variable p.
        assertNotEquals(EtatPartie.DEFAITE, p.getEtat());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
