package fr.utbm.sallesoubliees.modele;
import java.util.Set;
/** Validation du catalogue des décisions lors d'une restauration non fiable. */
final class DecisionAutorisee {
    /** Classe utilitaire. */
    private DecisionAutorisee() { }
    /** Vérifie type, puissance et télégraphie pour l'adversaire concerné. */
    static boolean verifier(Ennemi e, DecisionEnnemi d) {
        Set<ActionEnnemi> autorisees = switch (e.getType()) {
            case "squelette" -> Set.of(ActionEnnemi.ATTAQUE, ActionEnnemi.RIPOSTE);
            case "golem" -> Set.of(ActionEnnemi.PREPARATION, ActionEnnemi.FRAPPE_LOURDE, ActionEnnemi.ARMURE, ActionEnnemi.ONDE, ActionEnnemi.RECUPERATION);
            case "mage" -> Set.of(ActionEnnemi.ATTAQUE, ActionEnnemi.MALEDICTION, ActionEnnemi.ARMURE, ActionEnnemi.SOIN);
            default -> Set.of(ActionEnnemi.ATTAQUE, ActionEnnemi.RIPOSTE, ActionEnnemi.PREPARATION, ActionEnnemi.FRAPPE_LOURDE, ActionEnnemi.ONDE, ActionEnnemi.MARQUE, ActionEnnemi.RECUPERATION);
        };
        if (!autorisees.contains(d.action())) return false;
        int puissance = switch (d.action()) {
            case ATTAQUE, RIPOSTE -> e.getType().equals("squelette") ? 9 : e.getType().equals("mage") ? 11 : -1;
            case FRAPPE_LOURDE -> e.getType().equals("golem") ? 22 : -1;
            case ONDE -> e.getType().equals("golem") ? 16 : -1;
            default -> 0;
        };
        if (puissance >= 0 && d.puissance() != puissance) return false;
        if (puissance == -1 && !Set.of(15, 16, 18, 20, 21, 23, 26, 30).contains(d.puissance())) return false;
        return d.precise() || e.getType().equals("mage") && (d.action() == ActionEnnemi.ATTAQUE || d.action() == ActionEnnemi.MALEDICTION);
    }
}
