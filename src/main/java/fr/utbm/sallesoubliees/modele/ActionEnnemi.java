// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;
/** Catalogue des réponses, leurs propriétés sont définies dans le résolveur. */
// Déclare ActionEnnemi, ensemble fermé des réponses possibles des ennemis.
public enum ActionEnnemi {
    // Définit la valeur ATTAQUE : frappe normale. */ attaque,.
    /** Frappe normale. */ ATTAQUE,
    // Définit la valeur RIPOSTE : protection et riposte sur une attaque. */ riposte,.
    /** Protection et riposte sur une attaque. */ RIPOSTE,
    // Définit la valeur PREPARATION : préparation interruptible. */ preparation,.
    /** Préparation interruptible. */ PREPARATION,
    // Définit la valeur FRAPPE_LOURDE : coup lourd annoncé. */ frappe_lourde,.
    /** Coup lourd annoncé. */ FRAPPE_LOURDE,
    // Définit la valeur ARMURE : renforcement temporaire. */ armure,.
    /** Renforcement temporaire. */ ARMURE,
    // Définit la valeur ONDE : dégâts traversant partiellement la défense. */ onde,.
    /** Dégâts traversant partiellement la défense. */ ONDE,
    // Définit la valeur RECUPERATION : temps faible. */ recuperation,.
    /** Temps faible. */ RECUPERATION,
    // Définit la valeur MALEDICTION : réduction temporaire d'attaque. */ malediction,.
    /** Réduction temporaire d'attaque. */ MALEDICTION,
    // Définit la valeur SOIN : récupération limitée de pv. */ soin,.
    /** Récupération limitée de PV. */ SOIN,
    // Définit la valeur MARQUE : marque pour la prochaine frappe. */ marque.
    /** Marque pour la prochaine frappe. */ MARQUE
// Ferme le bloc ou la liste de données commencé plus haut.
}
