package fr.utbm.sallesoubliees.modele;

/** Catalogue fermé d'armes immuables ; leur identité est stable dans les sauvegardes. */
public enum Arme {
    /** Arme initiale. */
    ROUILLEE("Épée rouillée", 0),
    /** Récompense de l'arsenal. */
    ACIER("Épée d'acier", 5),
    /** Récompense du trésor. */
    RUNIQUE("Lame runique", 9);

    private final String nom;
    private final int bonus;
    /** Associe un nom et un bonus constants. */
    Arme(String nom, int bonus) { this.nom = nom; this.bonus = bonus; }
    /** @return bonus ajouté une seule fois à l'attaque */
    public int getBonus() { return bonus; }
    /** @return nom et bonus lisibles */
    @Override public String toString() { return nom + " (+" + bonus + ")"; }
}
