package fr.utbm.sallesoubliees.modele;
import java.util.List;
/** Informations consultables par les stratégies, sans référence mutable.
 *
 * @param elan ressource du héros
 * @param historique trois dernières commandes
 *
 * @param precedente dernière réponse
 * @param soinUtilise soin ennemi déjà consommé */
public record ContexteCombat(int elan, List<ActionCombat> historique, ActionEnnemi precedente, boolean soinUtilise) {
    /** Protège l'historique. */
    public ContexteCombat { historique = List.copyOf(historique); }
    /** {@return trois commandes identiques consécutives} */
    public boolean repetition() { return historique.size() == 3 && historique.stream().distinct().count() == 1; }
    /** {@return potions parmi les dernières commandes} */
    public long potions() { return historique.stream().filter(a -> a == ActionCombat.POTION).count(); }
}
