// Place cette classe dans la persistance, qui lit et écrit les fichiers.
package fr.utbm.sallesoubliees.persistance;

/**
 * Erreur de fichier traduite en message français destiné au joueur.
 * @serial exclude
 */
// Déclare ExceptionSauvegarde, exception contrôlée qui conserve la cause technique et fournit un message français. Hérite de Exception. final interdit de créer une sous-classe.
public final class ExceptionSauvegarde extends Exception {
    /**
     * Conserve la cause pour le diagnostic sans l'afficher au joueur.
     * @param message explication accessible
     * @param cause erreur technique
     */
    // Construit un objet ExceptionSauvegarde : exception contrôlée qui conserve la cause technique et fournit un message français. Initialise la classe mère avec message, cause.
    public ExceptionSauvegarde(String message, Throwable cause) { super(message, cause); }
// Ferme le bloc ou la liste de données commencé plus haut.
}
