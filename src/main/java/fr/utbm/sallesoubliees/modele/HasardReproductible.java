// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;
/** Générateur congruentiel sur 48 bits, indépendant de la présentation. */
// Déclare HasardReproductible, générateur déterministe sur 48 bits ; un même état reproduit les mêmes tirages. final interdit de créer une sous-classe.
public final class HasardReproductible implements SourceAleatoire {
    // Conserve l’état interne du générateur ; cette valeur suffit à reprendre les mêmes tirages après chargement.
    private long etat;
    /** Définit les données de cette opération.
     * @param graine état initial, tronqué à 48 bits */
    // Initialise le générateur en conservant uniquement les 48 bits de poids faible de la graine, grâce au masque binaire.
    public HasardReproductible(long graine) { etat = graine & ((1L << 48) - 1); }
    /** {@inheritDoc} */
    // Produit le prochain entier reproductible entre zéro et la borne exclue.
    @Override public int prochainEntier(int borne) {
        // Si borne est inférieur ou égal à 0, refuse l’opération : "Borne positive requise.".
        if (borne <= 0) throw new IllegalArgumentException("Borne positive requise.");
        // Avance le générateur congruentiel : multiplie l’état, ajoute 11 puis conserve 48 bits. Le prochain état dépend uniquement du précédent.
        etat = (etat * 25214903917L + 11) & ((1L << 48) - 1);
        // Décale l’état de 16 bits sans signe puis prend le reste modulo la borne ; renvoie un entier de zéro à borne moins un.
        return (int)((etat >>> 16) % borne);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** {@inheritDoc} */
    // Permet de consulter l'état sauvegardable. Renvoie l'état courant.
    @Override public long etat() { return etat; }
// Ferme le bloc ou la liste de données commencé plus haut.
}
