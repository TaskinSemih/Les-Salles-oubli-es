package fr.utbm.sallesoubliees.modele;

/** Alterne préparation sans dégâts et frappe lourde ; la parité survit au chargement. */
public final class Golem extends Ennemi {
    /** Crée un golem en pleine santé. */
    public Golem() { super("Golem", 48, 14, 4); }
    /** {@inheritDoc} */
    @Override public String getType() { return "golem"; }
    /** {@inheritDoc} */
    @Override public int puissanceProchaineAttaque() { return getTours() % 2 == 0 ? 0 : getAttaqueBase() + 8; }
    /** {@inheritDoc} */
    @Override public String getIntention() { return puissanceProchaineAttaque() == 0 ? "Prépare sa frappe : aucun dégât ce tour." : "Frappe lourde : puissance 22. Défendez-vous !"; }
}
