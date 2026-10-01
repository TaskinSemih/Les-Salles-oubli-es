package fr.utbm.sallesoubliees.modele;

/** Personnage dont les points de vie restent bornés entre zéro et leur maximum. */
public abstract class Personnage {
    private final String nom;
    private final int vieMax;
    private final int attaque;
    private final int armure;
    private int vie;

    /**
     * Construit un personnage en pleine santé.
     * @param nom nom affiché
     * @param vieMax maximum strictement positif
     * @param attaque puissance positive
     * @param armure protection positive ou nulle
     */
    protected Personnage(String nom, int vieMax, int attaque, int armure) {
        if (nom == null || nom.isBlank() || vieMax <= 0 || attaque <= 0 || armure < 0) {
            throw new IllegalArgumentException("Statistiques invalides.");
        }
        this.nom = nom; this.vieMax = vieMax; this.vie = vieMax;
        this.attaque = attaque; this.armure = armure;
    }

    /** @return nom affiché */
    public final String getNom() { return nom; }
    /** @return points de vie actuels */
    public final int getVie() { return vie; }
    /** @return maximum des points de vie */
    public final int getVieMax() { return vieMax; }
    /** @return puissance sans équipement */
    public final int getAttaqueBase() { return attaque; }
    /** @return armure permanente */
    public final int getArmure() { return armure; }
    /** @return vrai si le personnage est mort */
    public final boolean estMort() { return vie == 0; }

    /** Applique des dégâts non négatifs sans descendre sous zéro. */
    final void subirDegats(int degats) {
        if (degats < 0) throw new IllegalArgumentException("Dégâts négatifs.");
        vie = Math.max(0, vie - degats);
    }

    /** Soigne sans dépasser le maximum, même pour une grande quantité. */
    final void soigner(int soin) {
        if (soin < 0) throw new IllegalArgumentException("Soin négatif.");
        vie += Math.min(soin, vieMax - vie);
    }

    /** Restaure uniquement une valeur bornée, pendant un chargement validé. */
    final void restaurerVie(int valeur) {
        if (valeur < 0 || valeur > vieMax) throw new IllegalArgumentException("Vie invalide.");
        vie = valeur;
    }
}
