package fr.utbm.sallesoubliees.modele;

/** Adversaire régulier, utile pour apprendre le combat. */
public final class Squelette extends Ennemi {
    /** Crée un squelette en pleine santé. */
    public Squelette() { super("Squelette", 30, 9, 1); }
    /** {@inheritDoc} */
    @Override public String getType() { return "squelette"; }
    /** {@inheritDoc} */
    @Override public int puissanceProchaineAttaque() { return getAttaqueBase(); }
    /** {@inheritDoc} */
    @Override public String getIntention() { return "Coup d'épée : puissance 9."; }
}
