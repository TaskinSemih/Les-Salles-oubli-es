package fr.utbm.sallesoubliees.modele;
/** Catalogue des réponses, leurs propriétés sont définies dans le résolveur. */
public enum ActionEnnemi {
    /** Frappe normale. */ ATTAQUE,
    /** Protection et riposte sur une attaque. */ RIPOSTE,
    /** Préparation interruptible. */ PREPARATION,
    /** Coup lourd annoncé. */ FRAPPE_LOURDE,
    /** Renforcement temporaire. */ ARMURE,
    /** Dégâts traversant partiellement la défense. */ ONDE,
    /** Temps faible. */ RECUPERATION,
    /** Réduction temporaire d'attaque. */ MALEDICTION,
    /** Récupération limitée de PV. */ SOIN,
    /** Marque pour la prochaine frappe. */ MARQUE
}
