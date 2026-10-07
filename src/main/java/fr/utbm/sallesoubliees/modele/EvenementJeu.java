package fr.utbm.sallesoubliees.modele;

/**
 * Fait métier immuable, sans durée, sprite ni coordonnées.
 * @param type nature du fait
 * @param cible personnage concerné
 * @param valeur dégâts réellement subis, soin réel ou récompense
 * @param vieApres PV du personnage après ce fait
 */
public record EvenementJeu(Type type, Cible cible, int valeur, int vieApres) {
    /** Nature d'un événement dans l'ordre de résolution. */
    public enum Type {
        /** Début d'une frappe. */ ATTAQUE,
        /** Protection du héros. */ DEFENSE,
        /** Soin du héros. */ SOIN,
        /** Perte de points de vie. */ DEGATS,
        /** Préparation sans dégâts du golem. */ PREPARATION,
        /** Entrée du gardien dans sa phase renforcée. */ RAGE,
        /** Mort d'un personnage. */ MORT,
        /** Potion gagnée. */ RECOMPENSE,
        /** Fin victorieuse. */ VICTOIRE,
        /** Fin par mort du héros. */ DEFAITE
    }
    /** Identité du personnage concerné, indépendante de la présentation. */
    public enum Cible {
        /** Explorateur. */ HEROS,
        /** Ennemi de la salle. */ ENNEMI
    }
}
