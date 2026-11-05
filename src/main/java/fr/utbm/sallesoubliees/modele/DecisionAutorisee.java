// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;
// Rend disponible Set depuis java.util.
import java.util.Set;
/** Validation du catalogue des décisions lors d'une restauration non fiable. */
// Déclare DecisionAutorisee, validation de l'action, de la puissance et de la précision d'une intention chargée. final interdit de créer une sous-classe.
final class DecisionAutorisee {
    /** Classe utilitaire. */
    // Empêche la création extérieure de cette classe utilitaire grâce à un constructeur privé.
    private DecisionAutorisee() { }
    /** Vérifie type, puissance et télégraphie pour l'adversaire concerné. */
    // Contrôle la cohérence des valeurs reçues avant leur utilisation.
    static boolean verifier(Ennemi e, DecisionEnnemi d) {
        // Initialise « autorisees » avec switch (e.getType()) {.
        Set<ActionEnnemi> autorisees = switch (e.getType()) {
            // Pour le squelette, seules l’attaque et la riposte sont des intentions chargeables.
            case "squelette" -> Set.of(ActionEnnemi.ATTAQUE, ActionEnnemi.RIPOSTE);
            // Pour le golem, accepte préparation, frappe lourde, armure, onde et récupération.
            case "golem" -> Set.of(ActionEnnemi.PREPARATION, ActionEnnemi.FRAPPE_LOURDE, ActionEnnemi.ARMURE, ActionEnnemi.ONDE, ActionEnnemi.RECUPERATION);
            // Pour le mage, accepte attaque, malédiction, armure et soin.
            case "mage" -> Set.of(ActionEnnemi.ATTAQUE, ActionEnnemi.MALEDICTION, ActionEnnemi.ARMURE, ActionEnnemi.SOIN);
            // Pour le cas restant du catalogue, accepte les sept intentions du Gardien.
            default -> Set.of(ActionEnnemi.ATTAQUE, ActionEnnemi.RIPOSTE, ActionEnnemi.PREPARATION, ActionEnnemi.FRAPPE_LOURDE, ActionEnnemi.ONDE, ActionEnnemi.MARQUE, ActionEnnemi.RECUPERATION);
        // Ferme le bloc et termine l’instruction ou l’appel englobant.
        };
        // Si non (autorisees.contains(d.action())), renvoie faux.
        if (!autorisees.contains(d.action())) return false;
        // Initialise « puissance » avec switch (d.action()) {.
        int puissance = switch (d.action()) {
            // Fixe 9 pour attaque/riposte du squelette et 11 pour le mage ; -1 indique que la puissance sera vérifiée dans le catalogue du Gardien.
            case ATTAQUE, RIPOSTE -> e.getType().equals("squelette") ? 9 : e.getType().equals("mage") ? 11 : -1;
            // La frappe lourde du golem vaut 22 ; les autres cas passent au contrôle de puissances autorisées du Gardien.
            case FRAPPE_LOURDE -> e.getType().equals("golem") ? 22 : -1;
            // L’onde du golem vaut 16 ; les autres cas passent au contrôle de puissances autorisées du Gardien.
            case ONDE -> e.getType().equals("golem") ? 16 : -1;
            // Les actions sans impact direct doivent annoncer une puissance nulle.
            default -> 0;
        // Ferme le bloc et termine l’instruction ou l’appel englobant.
        };
        // Si (puissance est supérieur ou égal à 0 et la puissance annoncée de « d » est différent de puissance), renvoie faux.
        if (puissance >= 0 && d.puissance() != puissance) return false;
        // Si (puissance est égal à -1 et non (Set.of(15, 16, 18, 20, 21, 23, 26, 30).contains(d.puissance()))), renvoie faux.
        if (puissance == -1 && !Set.of(15, 16, 18, 20, 21, 23, 26, 30).contains(d.puissance())) return false;
        // Renvoie d.precise() || e.getType().equals("mage") && (d.action() == ActionEnnemi.ATTAQUE || d.action() == ActionEnnemi.MALEDICTION).
        return d.precise() || e.getType().equals("mage") && (d.action() == ActionEnnemi.ATTAQUE || d.action() == ActionEnnemi.MALEDICTION);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
