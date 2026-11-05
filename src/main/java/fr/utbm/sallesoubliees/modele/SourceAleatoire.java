// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;
/** Source injectable dont l'état suffit à reprendre exactement les tirages. */
// Déclare SourceAleatoire, contrat d'une source aléatoire injectable dont l'état peut être sauvegardé.
public interface SourceAleatoire {
    /** Définit les données de cette opération.
     * @param borne limite exclusive positive
 * @return entier entre zéro et borne exclue */
    // Produit le prochain entier reproductible entre zéro et la borne exclue.
    int prochainEntier(int borne);
    /** {@return état sérialisable du générateur} */
    // Permet de consulter l'état sauvegardable.
    long etat();
// Ferme le bloc ou la liste de données commencé plus haut.
}
