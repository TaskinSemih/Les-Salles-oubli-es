package fr.utbm.sallesoubliees.modele;

/** Résout une attaque ou une réponse ennemie sans dépendre de l'interface. */
public final class GestionnaireCombat {
    /** Protection valable uniquement pour la réponse immédiatement suivante. */
    public static final int BONUS_DEFENSE = 8;
    /** Classe utilitaire sans état transitoire entre les actions. */
    private GestionnaireCombat() { }
    /**
     * Calcule des dégâts avec un plancher de un.
     * @param puissance force positive ou nulle
     * @param armure protection positive ou nulle
     * @return dégâts à appliquer
     */
    public static int calculerDegats(int puissance, int armure) {
        if (puissance < 0 || armure < 0) throw new IllegalArgumentException("Valeur de combat négative.");
        return Math.max(1, puissance - armure);
    }
    /** Frappe l'ennemi avec l'arme actuelle. */
    static String attaquer(Heros heros, Ennemi ennemi) {
        int degats = calculerDegats(heros.getAttaque(), ennemi.getArmure());
        ennemi.subirDegats(degats);
        return "Vous infligez " + degats + " dégâts à " + ennemi.getNom() + ".";
    }
    /** Joue exactement une réponse vivante ; la défense n'est jamais stockée. */
    static String repondre(Heros heros, Ennemi ennemi, boolean defense) {
        if (heros.estMort() || ennemi.estMort()) return "";
        int puissance = ennemi.puissanceProchaineAttaque();
        ennemi.terminerTour();
        if (puissance == 0) return ennemi.getNom() + " prépare sa prochaine frappe.";
        int armure = (ennemi.ignoreArmure() ? 0 : heros.getArmure()) + (defense ? BONUS_DEFENSE : 0);
        int degats = calculerDegats(puissance, armure);
        heros.subirDegats(degats);
        return ennemi.getNom() + " vous inflige " + degats + " dégâts.";
    }
}
