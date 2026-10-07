package fr.utbm.sallesoubliees.modele;

import java.util.List;

/**
 * Résolution complète d'une seule commande ; le modèle est déjà dans l'état après.
 * @param valide vrai si la commande a eu un effet
 * @param message récit destiné au journal
 * @param avant état stable avant la commande
 * @param apres état stable après la réponse éventuelle
 * @param evenements faits à présenter dans cet ordre
 */
public record ResultatAction(boolean valide, String message, EtatSauvegarde avant,
                             EtatSauvegarde apres, List<EvenementJeu> evenements) {
    /** Empêche la modification de la séquence par la vue. */
    public ResultatAction { evenements = List.copyOf(evenements); }
}
