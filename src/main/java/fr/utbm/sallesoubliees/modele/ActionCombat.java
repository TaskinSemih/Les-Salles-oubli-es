package fr.utbm.sallesoubliees.modele;

/** Commandes dont la résolution atomique peut être rejouée visuellement sans refaire le tour. */
public enum ActionCombat {
    /** Frappe avec l'arme équipée. */ ATTAQUER,
    /** Protège de la prochaine réponse. */ DEFENDRE,
    /** Consomme une potion. */ POTION,
    /** Coûte deux points d'élan et brise les protections. */ PUISSANTE,
    /** Révèle les intentions et évite les ripostes. */ OBSERVER
}
