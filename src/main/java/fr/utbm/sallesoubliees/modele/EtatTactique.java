package fr.utbm.sallesoubliees.modele;
import java.util.List;
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
public record EtatTactique(long hasard, int elan, DecisionEnnemi decision, List<ActionCombat> historique,
                          ActionEnnemi precedente, boolean soinUtilise, List<EffetCombat> effets,
                          Map<String, ChoixEvenement> choix) {
    /** Copie et vérifie les données locales, les relations sont vérifiées à la restauration. */
    public EtatTactique {
        historique = List.copyOf(historique); effets = List.copyOf(effets); choix = Map.copyOf(choix);
        if (hasard < 0 || hasard >= (1L << 48) || elan < 0 || elan > 3 || historique.size() > 3
                || effets.size() > 12 || effets.stream().map(e -> e.type() + ":" + e.cible()).distinct().count() != effets.size())
            throw new IllegalArgumentException("État tactique invalide.");
    }
}
