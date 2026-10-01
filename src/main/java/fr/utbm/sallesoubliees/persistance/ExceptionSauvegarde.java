package fr.utbm.sallesoubliees.persistance;

/**
 * Erreur de fichier traduite en message français destiné au joueur.
 * @serial exclude
 */
public final class ExceptionSauvegarde extends Exception {
    /**
     * Conserve la cause pour le diagnostic sans l'afficher au joueur.
     * @param message explication accessible
     * @param cause erreur technique
     */
    public ExceptionSauvegarde(String message, Throwable cause) { super(message, cause); }
}
