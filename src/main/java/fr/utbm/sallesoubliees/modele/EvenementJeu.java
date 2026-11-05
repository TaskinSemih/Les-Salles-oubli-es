// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

/**
 * Fait métier immuable, sans durée, sprite ni coordonnées.
 *
 * @param type nature du fait
 *
 * @param cible personnage concerné
 *
 * @param valeur dégâts réellement subis, soin réel ou récompense
 *
 * @param vieApres PV du personnage après ce fait
 */
// Déclare EvenementJeu, fait immuable produit par le modèle et consommé ensuite par l'animation. Le record fournit les accesseurs, equals et hashCode à partir de ses données.
public record EvenementJeu(Type type, Cible cible, int valeur, int vieApres) {
    /** Nature d'un événement dans l'ordre de résolution. */
    // Énumère les natures d’effets ou de faits admises par ce type, sans chaîne libre.
    public enum Type {
        // Définit la valeur ATTAQUE : début d'une frappe. */ attaque,.
        /** Début d'une frappe. */ ATTAQUE,
        // Définit la valeur DEFENSE : protection du héros. */ defense,.
        /** Protection du héros. */ DEFENSE,
        // Définit la valeur SOIN : soin du héros. */ soin,.
        /** Soin du héros. */ SOIN,
        // Définit la valeur DEGATS : perte de points de vie. */ degats,.
        /** Perte de points de vie. */ DEGATS,
        // Définit la valeur PREPARATION : préparation sans dégâts du golem. */ preparation,.
        /** Préparation sans dégâts du golem. */ PREPARATION,
        // Définit la valeur RAGE : entrée du gardien dans sa phase renforcée. */ rage,.
        /** Entrée du gardien dans sa phase renforcée. */ RAGE,
        // Définit la valeur MORT : mort d'un personnage. */ mort,.
        /** Mort d'un personnage. */ MORT,
        // Définit la valeur RECOMPENSE : potion gagnée. */ recompense,.
        /** Potion gagnée. */ RECOMPENSE,
        // Définit la valeur VICTOIRE : fin victorieuse. */ victoire,.
        /** Fin victorieuse. */ VICTOIRE,
        // Définit la valeur DEFAITE : fin par mort du héros. */ defaite,.
        /** Fin par mort du héros. */ DEFAITE,
        // Définit la valeur ELAN : nouvelle réserve d'élan. */ elan,.
        /** Nouvelle réserve d'élan. */ ELAN,
        // Définit la valeur EFFET : application d'une altération, valeur = ordinal du type. */ effet,.
        /** Application d'une altération, valeur = ordinal du type. */ EFFET,
        // Définit la valeur EXPIRATION : expiration d'une altération. */ expiration,.
        /** Expiration d'une altération. */ EXPIRATION,
        // Définit la valeur INTERRUPTION : préparation annulée. */ interruption,.
        /** Préparation annulée. */ INTERRUPTION,
        // Définit la valeur BRISE_GARDE : protection brisée. */ brise_garde,.
        /** Protection brisée. */ BRISE_GARDE,
        // Définit la valeur CONTRE_ATTAQUE : riposte déclenchée. */ contre_attaque,.
        /** Riposte déclenchée. */ CONTRE_ATTAQUE,
        // Définit la valeur OBSERVATION : information révélée. */ observation,.
        /** Information révélée. */ OBSERVATION,
        // Définit la valeur INTENTION : intention suivante sélectionnée. */ intention,.
        /** Intention suivante sélectionnée. */ INTENTION,
        // Définit la valeur PHASE : phase du gardien. */ phase.
        /** Phase du Gardien. */ PHASE
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Identité du personnage concerné, indépendante de la présentation. */
    // Énumère les deux cibles métier possibles : héros et ennemi.
    public enum Cible {
        // Définit la valeur HEROS : explorateur. */ heros,.
        /** Explorateur. */ HEROS,
        // Définit la valeur ENNEMI : ennemi de la salle. */ ennemi.
        /** Ennemi de la salle. */ ENNEMI
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
