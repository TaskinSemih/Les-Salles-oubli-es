package fr.utbm.sallesoubliees.modele;

/** Ignore l'armure permanente, tout en respectant la défense temporaire. */
public final class Mage extends Ennemi {
    /** {@inheritDoc} */
    @Override public DecisionEnnemi decider(ContexteCombat c, SourceAleatoire r) {
        if (!c.soinUtilise() && getVie() <= 14) return new DecisionEnnemi(ActionEnnemi.SOIN, 0, true);
                if (c.precedente() == ActionEnnemi.MALEDICTION || c.precedente() == ActionEnnemi.ARMURE || c.precedente() == ActionEnnemi.SOIN)
                    return new DecisionEnnemi(ActionEnnemi.ATTAQUE, 11, false);
                int choix = r.prochainEntier(100);
                ActionEnnemi a = choix < (c.elan() >= 2 || c.potions() >= 2 ? 60 : 25) ? ActionEnnemi.MALEDICTION : choix < 75 ? ActionEnnemi.ATTAQUE : ActionEnnemi.ARMURE;
                return new DecisionEnnemi(a, a == ActionEnnemi.ATTAQUE ? 11 : 0, a == ActionEnnemi.ARMURE);
    }
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
