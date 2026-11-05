// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;
// Rend disponible Objects depuis java.util.
import java.util.Objects;
/** Altération immuable mesurée en actions restantes.
 *
 * @param type effet
 * @param cible personnage
 * @param duree actions restantes */
// Déclare EffetCombat, altération immuable avec un type, une cible et une durée en actions. Le record fournit les accesseurs, equals et hashCode à partir de ses données.
public record EffetCombat(Type type, EvenementJeu.Cible cible, int duree) {
    /** Vérifie la durée bornée. */
    // Constructeur compact du record : vérifie ou copie défensivement les données reçues.
    public EffetCombat { Objects.requireNonNull(type); Objects.requireNonNull(cible);
        // Si (duree est inférieur à 1 ou duree est supérieur à 3), refuse l’opération : "Durée invalide.".
        if (duree < 1 || duree > 3) throw new IllegalArgumentException("Durée invalide."); }
    /** Effets génériques du combat. */
    // Énumère les natures d’effets ou de faits admises par ce type, sans chaîne libre.
    public enum Type {
        // Définit la valeur GARDE : protection. */ garde,.
        /** Protection. */ GARDE,
        // Définit la valeur VULNERABLE : prochain impact augmenté. */ vulnerable,.
        /** Prochain impact augmenté. */ VULNERABLE,
        // Définit la valeur AFFAIBLI : attaque réduite de quatre. */ affaibli,.
        /** Attaque réduite de quatre. */ AFFAIBLI,
        // Définit la valeur LUCIDE : information exacte. */ lucide,.
        /** Information exacte. */ LUCIDE,
        // Définit la valeur ARMURE_RENFORCEE : armure supplémentaire. */ armure_renforcee,.
        /** Armure supplémentaire. */ ARMURE_RENFORCEE,
        // Définit la valeur MARQUE : prochain impact augmenté de quatre. */ marque.
        /** Prochain impact augmenté de quatre. */ MARQUE
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
