// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;
// Rend disponible Objects depuis java.util.
import java.util.Objects;
/** Intention choisie une fois avant l'action du joueur.
 *
 * @param action réponse préparée
 * @param puissance puissance annoncée
 * @param precise information initialement complète */
// Déclare DecisionEnnemi, intention immuable annoncée avant le tour : action, puissance et précision. Le record fournit les accesseurs, equals et hashCode à partir de ses données.
public record DecisionEnnemi(ActionEnnemi action, int puissance, boolean precise) {
    /** Contrôle les bornes communes. */
    // Constructeur compact du record : vérifie ou copie défensivement les données reçues.
    public DecisionEnnemi {
        // Refuse une référence null avant de poursuivre.
        Objects.requireNonNull(action);
        // Si (puissance est inférieur à 0 ou puissance est supérieur à 32), refuse l’opération : "Puissance ennemie invalide.".
        if (puissance < 0 || puissance > 32) throw new IllegalArgumentException("Puissance ennemie invalide.");
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Définit les données de cette opération.
     * @param lucide révélation par observation
 * @return intention destinée au joueur */
    // Définit les données de cette opération.
    public String decrire(boolean lucide) {
        // Si (non (precise) et non (lucide)), renvoie le texte "Énergie instable : attaque ou malédiction. Observer révèle le prochain tour.".
        if (!precise && !lucide) return "Énergie instable : attaque ou malédiction. Observer révèle le prochain tour.";
        // Renvoie la valeur produite par le switch selon le cas rencontré.
        return switch (action) {
            // Pour ATTAQUE : fournit le texte "Attaque annoncée : " + puissance + ".".
            case ATTAQUE -> "Attaque annoncée : " + puissance + ".";
            // Pour RIPOSTE : fournit le texte "Garde : riposte " + puissance + " si vous frappez. Observer crée une ouverture.".
            case RIPOSTE -> "Garde : riposte " + puissance + " si vous frappez. Observer crée une ouverture.";
            // Pour PREPARATION : fournit le texte "Préparation fragile : attaque rapide pour interrompre.".
            case PREPARATION -> "Préparation fragile : attaque rapide pour interrompre.";
            // Pour FRAPPE_LOURDE : fournit le texte "Frappe lourde : " + puissance + ". Défendez-vous !".
            case FRAPPE_LOURDE -> "Frappe lourde : " + puissance + ". Défendez-vous !";
            // Pour ARMURE : fournit le texte "Renforce son armure ; une frappe puissante la brise.".
            case ARMURE -> "Renforce son armure ; une frappe puissante la brise.";
            // Pour ONDE : fournit le texte "Onde : " + puissance + ", traverse la moitié de la défense.".
            case ONDE -> "Onde : " + puissance + ", traverse la moitié de la défense.";
            // Pour RECUPERATION : fournit le texte "Récupération : vulnérable, aucune attaque.".
            case RECUPERATION -> "Récupération : vulnérable, aucune attaque.";
            // Pour MALEDICTION : fournit le texte "Malédiction : attaque réduite pendant deux actions.".
            case MALEDICTION -> "Malédiction : attaque réduite pendant deux actions.";
            // Pour SOIN : fournit le texte "Soin unique : +10 PV. Profitez de ce répit.".
            case SOIN -> "Soin unique : +10 PV. Profitez de ce répit.";
            // Pour MARQUE : fournit le texte "Marque : prochain impact renforcé de 4.".
            case MARQUE -> "Marque : prochain impact renforcé de 4.";
        // Ferme le bloc et termine l’instruction ou l’appel englobant.
        };
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
