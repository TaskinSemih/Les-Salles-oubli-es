// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

/** Résout une attaque ou une réponse ennemie sans dépendre de l'interface. */
// Déclare GestionnaireCombat, calculs de dégâts et ancien résolveur simple, également utilisé par des tests. final interdit de créer une sous-classe.
public final class GestionnaireCombat {
    /** Protection valable uniquement pour la réponse immédiatement suivante. */
    // Initialise les huit points de protection temporaire avec 8.
    public static final int BONUS_DEFENSE = 8;
    /** Classe utilitaire sans état transitoire entre les actions. */
    // Empêche la création extérieure de cette classe utilitaire grâce à un constructeur privé.
    private GestionnaireCombat() { }
    /**
     * Calcule des dégâts avec un plancher de un.
     * @param puissance force positive ou nulle
     * @param armure protection positive ou nulle
     * @return dégâts à appliquer
     */
    // Calcule des dégâts avec un plancher de un.
    public static int calculerDegats(int puissance, int armure) {
        // Si (puissance est inférieur à 0 ou l'armure permanente est inférieur à 0), refuse l’opération : "Valeur de combat négative.".
        if (puissance < 0 || armure < 0) throw new IllegalArgumentException("Valeur de combat négative.");
        // Renvoie la puissance moins l’armure, avec au moins 1 dégât.
        return Math.max(1, puissance - armure);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Frappe l'ennemi avec l'arme actuelle. */
    // Frappe l'ennemi avec l'arme actuelle.
    static String attaquer(Heros heros, Ennemi ennemi) {
        // Initialise « degats » avec calculerDegats(heros.getAttaque(), ennemi.getArmure()).
        int degats = calculerDegats(heros.getAttaque(), ennemi.getArmure());
        // Applique les dégâts au personnage en bornant sa vie à zéro ; arguments : degats.
        ennemi.subirDegats(degats);
        // Renvoie "Vous infligez " + degats + " dégâts à " + ennemi.getNom() + ".".
        return "Vous infligez " + degats + " dégâts à " + ennemi.getNom() + ".";
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Joue exactement une réponse vivante ; la défense n'est jamais stockée. */
    // Joue exactement une réponse vivante ; la défense n'est jamais stockée.
    static String repondre(Heros heros, Ennemi ennemi, boolean defense) {
        // Si (si les PV valent zéro du héros ou si les PV valent zéro de l'ennemi), renvoie le texte "".
        if (heros.estMort() || ennemi.estMort()) return "";
        // Initialise « puissance » avec la puissance du prochain coup du résolveur simple de l'ennemi.
        int puissance = ennemi.puissanceProchaineAttaque();
        // Compte une réponse supplémentaire de l’ennemi.
        ennemi.terminerTour();
        // Si puissance est égal à 0, renvoie ennemi.getNom() + " prépare sa prochaine frappe.".
        if (puissance == 0) return ennemi.getNom() + " prépare sa prochaine frappe.";
        // Initialise l'armure permanente avec (ennemi.ignoreArmure() ? 0 : heros.getArmure()) + (defense ? BONUS_DEFENSE : 0).
        int armure = (ennemi.ignoreArmure() ? 0 : heros.getArmure()) + (defense ? BONUS_DEFENSE : 0);
        // Initialise « degats » avec calculerDegats(puissance, armure).
        int degats = calculerDegats(puissance, armure);
        // Applique les dégâts au personnage en bornant sa vie à zéro ; arguments : degats.
        heros.subirDegats(degats);
        // Renvoie ennemi.getNom() + " vous inflige " + degats + " dégâts.".
        return ennemi.getNom() + " vous inflige " + degats + " dégâts.";
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
