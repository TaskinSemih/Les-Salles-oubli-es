package fr.utbm.sallesoubliees.modele;

/** Base polymorphe des adversaires, avec un compteur persistant de réponses jouées. */
public abstract class Ennemi extends Personnage {
    private int tours;
    /**
     * Construit un adversaire.
     * @param nom nom affiché
     * @param vie maximum de vie
     * @param attaque puissance normale
     * @param armure armure permanente
     */
    protected Ennemi(String nom, int vie, int attaque, int armure) { super(nom, vie, attaque, armure); }
    /** @return nombre de réponses ennemies déjà effectuées */
    public final int getTours() { return tours; }
    /** @return identifiant stable du type d'ennemi */
    public abstract String getType();
    /** @return puissance du prochain coup, ou zéro pour une préparation */
    public abstract int puissanceProchaineAttaque();
    /** @return intention lisible avant le choix du joueur */
    public abstract String getIntention();
    /** @return vrai si l'attaque traverse l'armure permanente */
    public boolean ignoreArmure() { return false; }
    /** Comptabilise une réponse après son exécution. */
    final void terminerTour() { tours++; }
    /** Restaure un compteur positif ou nul et raisonnablement borné. */
    final void restaurerTours(int valeur) {
        if (valeur < 0 || valeur > 1_000_000) throw new IllegalArgumentException("Compteur de tours invalide.");
        tours = valeur;
    }
}
