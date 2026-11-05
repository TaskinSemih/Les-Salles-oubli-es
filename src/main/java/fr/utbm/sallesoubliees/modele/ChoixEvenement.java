// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;
/** Trois décisions disponibles sur un événement encore intact. */
// Déclare ChoixEvenement, ensemble des choix prudent, risqué ou abandon pour une découverte de salle.
public enum ChoixEvenement {
    // Définit la valeur PRUDENT : récompense sans risque. */ prudent,.
    /** Récompense sans risque. */ PRUDENT,
    // Définit la valeur RISQUER : récompense accrue, piège possible. */ risquer,.
    /** Récompense accrue, piège possible. */ RISQUER,
    // Définit la valeur IGNORER : renoncement définitif au bonus. */ ignorer.
    /** Renoncement définitif au bonus. */ IGNORER
// Ferme le bloc ou la liste de données commencé plus haut.
}
