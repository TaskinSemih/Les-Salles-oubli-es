package fr.utbm.sallesoubliees.modele;

/** Héros unique ; l'attaque effective est calculée à partir d'une seule arme. */
public final class Heros extends Personnage {
    private final Inventaire inventaire = new Inventaire();
    /** Crée le héros avec les statistiques de la version 1. */
    public Heros() { super("L'explorateur", 100, 12, 3); }
    /** {@return inventaire consultable, modifié uniquement par le modèle} */
    public Inventaire getInventaire() { return inventaire; }
    /** {@return attaque de base plus le bonus de l'arme équipée} */
    public int getAttaque() { return getAttaqueBase() + inventaire.getEquipee().getBonus(); }
}
