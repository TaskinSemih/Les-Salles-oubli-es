// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;
// Rend disponible List depuis java.util.
import java.util.List;
/** Informations consultables par les stratégies, sans référence mutable.
 *
 * @param elan ressource du héros
 * @param historique trois dernières commandes
 *
 * @param precedente dernière réponse
 * @param soinUtilise soin ennemi déjà consommé */
// Déclare ContexteCombat, instantané immuable des informations consultées par les stratégies ennemies. Le record fournit les accesseurs, equals et hashCode à partir de ses données.
public record ContexteCombat(int elan, List<ActionCombat> historique, ActionEnnemi precedente, boolean soinUtilise) {
    /** Protège l'historique. */
    // Constructeur compact du record : vérifie ou copie défensivement les données reçues. Met à jour les trois dernières actions du héros avec une copie non modifiable des trois dernières actions du héros.
    public ContexteCombat { historique = List.copyOf(historique); }
    /** {@return trois commandes identiques consécutives} */
    // Détecte exactement trois commandes identiques : l’historique doit avoir trois éléments et une seule valeur distincte.
    public boolean repetition() { return historique.size() == 3 && historique.stream().distinct().count() == 1; }
    /** {@return potions parmi les dernières commandes} */
    // Compte les commandes POTION parmi les trois dernières actions ; ce nombre aidu mage à adapter sa stratégie.
    public long potions() { return historique.stream().filter(a -> a == ActionCombat.POTION).count(); }
// Ferme le bloc ou la liste de données commencé plus haut.
}
