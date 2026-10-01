package fr.utbm.sallesoubliees.modele;

/** Gardien final qui devient enragé à la moitié de sa vie. */
public final class Boss extends Ennemi {
    /** Crée le gardien final. */
    public Boss() { super("Gardien des oubliés", 90, 16, 3); }
    /** {@inheritDoc} */
    @Override public String getType() { return "boss"; }
    /** {@inheritDoc} */
    @Override public int puissanceProchaineAttaque() { return getVie() <= 45 ? 24 : getAttaqueBase(); }
    /** {@inheritDoc} */
    @Override public String getIntention() { return "Frappe : puissance " + puissanceProchaineAttaque() + ". Rage (24) dès 45 PV, y compris après votre coup."; }
}
