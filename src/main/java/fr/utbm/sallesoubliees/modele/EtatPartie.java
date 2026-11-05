// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

/** Issue globale d'une partie. */
// Déclare EtatPartie, ensemble des trois issues : partie en cours, victoire ou défaite.
public enum EtatPartie {
    /** Les actions sont permises. */
    // Définit la valeur EN_COURS : les actions sont permises.
    EN_COURS,
    /** Le gardien est mort. */
    // Définit la valeur VICTOIRE : le gardien est mort.
    VICTOIRE,
    /** Le héros est mort. */
    // Définit la valeur DEFAITE : le héros est mort.
    DEFAITE
// Ferme le bloc ou la liste de données commencé plus haut.
}
