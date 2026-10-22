package fr.utbm.sallesoubliees.modele;
import java.util.Objects;
/** Intention choisie une fois avant l'action du joueur.
 *
 * @param action réponse préparée
 * @param puissance puissance annoncée
 * @param precise information initialement complète */
public record DecisionEnnemi(ActionEnnemi action, int puissance, boolean precise) {
    /** Contrôle les bornes communes. */
    public DecisionEnnemi {
        Objects.requireNonNull(action);
        if (puissance < 0 || puissance > 32) throw new IllegalArgumentException("Puissance ennemie invalide.");
    }
    /** Définit les données de cette opération.
     * @param lucide révélation par observation
 * @return intention destinée au joueur */
    public String decrire(boolean lucide) {
        if (!precise && !lucide) return "Énergie instable : attaque ou malédiction. Observer révèle le prochain tour.";
        return switch (action) {
            case ATTAQUE -> "Attaque annoncée : " + puissance + ".";
            case RIPOSTE -> "Garde : riposte " + puissance + " si vous frappez. Observer crée une ouverture.";
            case PREPARATION -> "Préparation fragile : attaque rapide pour interrompre.";
            case FRAPPE_LOURDE -> "Frappe lourde : " + puissance + ". Défendez-vous !";
            case ARMURE -> "Renforce son armure ; une frappe puissante la brise.";
            case ONDE -> "Onde : " + puissance + ", traverse la moitié de la défense.";
            case RECUPERATION -> "Récupération : vulnérable, aucune attaque.";
            case MALEDICTION -> "Malédiction : attaque réduite pendant deux actions.";
            case SOIN -> "Soin unique : +10 PV. Profitez de ce répit.";
            case MARQUE -> "Marque : prochain impact renforcé de 4.";
        };
    }
}
