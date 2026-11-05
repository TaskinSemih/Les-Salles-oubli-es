// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;
// Rend disponible List depuis java.util.
import java.util.List;
// Rend disponible Map depuis java.util.
import java.util.Map;
/** Données tactiques persistantes, les coordonnées et durées visuelles sont exclues.
 *
 * @param hasard état sur 48 bits
 * @param elan réserve bornée
 * @param decision intention déjà tirée
 *
 * @param historique trois dernières actions
 * @param precedente réponse précédente
 *
 * @param soinUtilise soin du combat consommé
 * @param effets altérations actives
 *
 * @param choix choix de salles déjà résolus */
// Déclare EtatTactique, instantané du hasard, de l'élan, de l'intention, de l'historique, des effets et des découvertes. Le record fournit les accesseurs, equals et hashCode à partir de ses données.
public record EtatTactique(long hasard, int elan, DecisionEnnemi decision, List<ActionCombat> historique,
                          // Conserve la dernière réponse, l’usage du soin unique et les altérations actives pour reprendre le combat.
                          ActionEnnemi precedente, boolean soinUtilise, List<EffetCombat> effets,
                          // Conserve par identifiant de salle les découvertes déjà résolues.
                          Map<String, ChoixEvenement> choix) {
    /** Copie et vérifie les données locales, les relations sont vérifiées à la restauration. */
    // Constructeur compact du record : vérifie ou copie défensivement les données reçues.
    public EtatTactique {
        // Met à jour les trois dernières actions du héros avec une copie non modifiable des trois dernières actions du héros. Met à jour les altérations actives avec une copie non modifiable des altérations actives. Met à jour le choix courant avec une copie non modifiable du choix courant.
        historique = List.copyOf(historique); effets = List.copyOf(effets); choix = Map.copyOf(choix);
        // Poursuit la condition de contrôle sur les lignes suivantes.
        if (hasard < 0 || hasard >= (1L << 48) || elan < 0 || elan > 3 || historique.size() > 3
                // Refuse plus de douze effets ainsi que plusieurs effets de même type sur la même cible.
                || effets.size() > 12 || effets.stream().map(e -> e.type() + ":" + e.cible()).distinct().count() != effets.size())
            // Signale l’erreur par une exception : "État tactique invalide.".
            throw new IllegalArgumentException("État tactique invalide.");
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
