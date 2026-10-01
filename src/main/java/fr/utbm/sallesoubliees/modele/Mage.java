package fr.utbm.sallesoubliees.modele;

/** Ignore l'armure permanente, tout en respectant la défense temporaire. */
public final class Mage extends Ennemi {
    /** Crée un mage en pleine santé. */
    public Mage() { super("Mage", 36, 11, 1); }
    /** {@inheritDoc} */
    @Override public String getType() { return "mage"; }
    /** {@inheritDoc} */
    @Override public int puissanceProchaineAttaque() { return getAttaqueBase(); }
    /** {@inheritDoc} */
    @Override public boolean ignoreArmure() { return true; }
    /** {@inheritDoc} */
    @Override public String getIntention() { return "Sort : puissance 11, ignore l'armure. La défense reste efficace."; }
}
