// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

/** Catalogue fermé d'armes immuables ; leur identité est stable dans les sauvegardes. */
// Déclare Arme, catalogue des armes : chaque valeur associe un nom et un bonus immuables.
public enum Arme {
    /** Arme initiale. */
    // Définit cette arme du catalogue avec son nom français et son bonus d’attaque : ROUILLEE("Épée rouillée", 0).
    ROUILLEE("Épée rouillée", 0),
    /** Récompense de l'arsenal. */
    // Définit cette arme du catalogue avec son nom français et son bonus d’attaque : ACIER("Épée d'acier", 5).
    ACIER("Épée d'acier", 5),
    /** Récompense du trésor. */
    // Définit cette arme du catalogue avec son nom français et son bonus d’attaque : RUNIQUE("Lame runique", 9).
    RUNIQUE("Lame runique", 9);

    // Déclare le nom de type String.
    private final String nom;
    // Déclare « bonus » de type int.
    private final int bonus;
    /** Associe un nom et un bonus constants. */
    // Construit un objet Arme : catalogue des armes : chaque valeur associe un nom et un bonus immuables. Met à jour le nom avec le nom. Met à jour « bonus » avec bonus.
    Arme(String nom, int bonus) { this.nom = nom; this.bonus = bonus; }
    /** {@return bonus ajouté une seule fois à l'attaque} */
    // Permet de consulter le bonus de l'arme. Renvoie bonus.
    public int getBonus() { return bonus; }
    /** {@return nom et bonus lisibles} */
    // { Renvoie nom + " (+" + bonus + ")".
    @Override public String toString() { return nom + " (+" + bonus + ")"; }
// Ferme le bloc ou la liste de données commencé plus haut.
}
