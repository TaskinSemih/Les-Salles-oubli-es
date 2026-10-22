package fr.utbm.sallesoubliees.modele;
/** Générateur congruentiel sur 48 bits, indépendant de la présentation. */
public final class HasardReproductible implements SourceAleatoire {
    private long etat;
    /** Définit les données de cette opération.
     * @param graine état initial, tronqué à 48 bits */
    public HasardReproductible(long graine) { etat = graine & ((1L << 48) - 1); }
    /** {@inheritDoc} */
    @Override public int prochainEntier(int borne) {
        if (borne <= 0) throw new IllegalArgumentException("Borne positive requise.");
        etat = (etat * 25214903917L + 11) & ((1L << 48) - 1);
        return (int)((etat >>> 16) % borne);
    }
    /** {@inheritDoc} */
    @Override public long etat() { return etat; }
}
