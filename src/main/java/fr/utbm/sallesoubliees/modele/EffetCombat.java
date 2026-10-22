package fr.utbm.sallesoubliees.modele;
import java.util.Objects;
/** Altération immuable mesurée en actions restantes.
 *
 * @param type effet
 * @param cible personnage
 * @param duree actions restantes */
public record EffetCombat(Type type, EvenementJeu.Cible cible, int duree) {
    /** Vérifie la durée bornée. */
    public EffetCombat { Objects.requireNonNull(type); Objects.requireNonNull(cible);
        if (duree < 1 || duree > 3) throw new IllegalArgumentException("Durée invalide."); }
    /** Effets génériques du combat. */
    public enum Type {
        /** Protection. */ GARDE,
        /** Prochain impact augmenté. */ VULNERABLE,
        /** Attaque réduite de quatre. */ AFFAIBLI,
        /** Information exacte. */ LUCIDE,
        /** Armure supplémentaire. */ ARMURE_RENFORCEE,
        /** Prochain impact augmenté de quatre. */ MARQUE
    }
}
