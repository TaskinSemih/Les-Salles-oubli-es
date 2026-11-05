// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

/** Valeur commune aux potions identiques, stockées par quantité dans l'inventaire. */
// Déclare Potion, catalogue utilitaire contenant la quantité de soin d'une potion. final interdit de créer une sous-classe.
public final class Potion {
    /** Nombre maximal de PV rendus par une potion. */
    // Initialise le soin maximal de 35 PV par potion avec 35.
    public static final int SOIN = 35;
    /** Empêche l'instanciation d'un simple catalogue de constantes. */
    // Empêche la création extérieure de cette classe utilitaire grâce à un constructeur privé.
    private Potion() { }
// Ferme le bloc ou la liste de données commencé plus haut.
}
