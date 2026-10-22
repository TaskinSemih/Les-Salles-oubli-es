package fr.utbm.sallesoubliees.modele;
/** Trois décisions disponibles sur un événement encore intact. */
public enum ChoixEvenement {
    /** Récompense sans risque. */ PRUDENT,
    /** Récompense accrue, piège possible. */ RISQUER,
    /** Renoncement définitif au bonus. */ IGNORER
}
