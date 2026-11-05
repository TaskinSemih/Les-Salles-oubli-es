// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

/** Commandes dont la résolution atomique peut être rejouée visuellement sans refaire le tour. */
// Déclare ActionCombat, ensemble fermé des cinq commandes de combat du héros.
public enum ActionCombat {
    // Définit la valeur ATTAQUER : frappe avec l'arme équipée. */ attaquer,.
    /** Frappe avec l'arme équipée. */ ATTAQUER,
    // Définit la valeur DEFENDRE : protège de la prochaine réponse. */ defendre,.
    /** Protège de la prochaine réponse. */ DEFENDRE,
    // Définit la valeur POTION : consomme une potion. */ potion,.
    /** Consomme une potion. */ POTION,
    // Définit la valeur PUISSANTE : coûte deux points d'élan et brise les protections. */ puissante,.
    /** Coûte deux points d'élan et brise les protections. */ PUISSANTE,
    // Définit la valeur OBSERVER : révèle les intentions et évite les ripostes. */ observer.
    /** Révèle les intentions et évite les ripostes. */ OBSERVER
// Ferme le bloc ou la liste de données commencé plus haut.
}
