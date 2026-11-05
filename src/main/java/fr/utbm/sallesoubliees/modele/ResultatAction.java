// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

// Rend disponible List depuis java.util.
import java.util.List;

/**
 * Résolution complète d'une seule commande ; le modèle est déjà dans l'état après.
 * @param valide vrai si la commande a eu un effet
 * @param message récit destiné au journal
 * @param avant état stable avant la commande
 * @param apres état stable après la réponse éventuelle
 * @param evenements faits à présenter dans cet ordre
 */
// Déclare ResultatAction, résultat immuable d'une commande : validité, message, états avant/après et événements. Le record fournit les accesseurs, equals et hashCode à partir de ses données.
public record ResultatAction(boolean valide, String message, EtatSauvegarde avant,
                             // Complète le résultat par l’instantané après résolution et la liste ordonnée des faits à présenter.
                             EtatSauvegarde apres, List<EvenementJeu> evenements) {
    /** Empêche la modification de la séquence par la vue. */
    // Constructeur compact du record : vérifie ou copie défensivement les données reçues. Met à jour « evenements » avec une copie non modifiable de evenements.
    public ResultatAction { evenements = List.copyOf(evenements); }
// Ferme le bloc ou la liste de données commencé plus haut.
}
