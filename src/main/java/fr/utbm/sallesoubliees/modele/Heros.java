// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

/** Héros unique ; l'attaque effective est calculée à partir d'une seule arme. */
// Déclare Heros, personnage du joueur, avec son inventaire et l'attaque de son arme équipée. Hérite de Personnage. final interdit de créer une sous-classe.
public final class Heros extends Personnage {
    // Initialise l'inventaire du héros avec un nouvel objet Inventaire.
    private final Inventaire inventaire = new Inventaire();
    /** Crée le héros avec les statistiques de la version 1. */
    // Crée l’explorateur avec 100 PV maximum, 12 de puissance de base et 3 d’armure ; son inventaire est initialisé séparément.
    public Heros() { super("L'explorateur", 100, 12, 3); }
    /** {@return inventaire consultable, modifié uniquement par le modèle} */
    // Permet de consulter l'inventaire. Renvoie l'inventaire du héros.
    public Inventaire getInventaire() { return inventaire; }
    /** {@return attaque de base plus le bonus de l'arme équipée} */
    // Permet de consulter l'attaque avec le bonus de l'arme équipée. Renvoie l’attaque de base plus le seul bonus de l’arme équipée.
    public int getAttaque() { return getAttaqueBase() + inventaire.getEquipee().getBonus(); }
// Ferme le bloc ou la liste de données commencé plus haut.
}
