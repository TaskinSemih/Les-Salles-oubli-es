package fr.utbm.sallesoubliees.modele;

/** Alterne préparation sans dégâts et frappe lourde ; la parité survit au chargement. */
public final class Golem extends Ennemi {
    /** {@inheritDoc} */
    @Override public DecisionEnnemi decider(ContexteCombat c, SourceAleatoire r) {
        if (c.precedente() == ActionEnnemi.PREPARATION) return new DecisionEnnemi(ActionEnnemi.FRAPPE_LOURDE, 22, true);
                if (c.precedente() == ActionEnnemi.FRAPPE_LOURDE || c.precedente() == ActionEnnemi.ONDE)
                    return new DecisionEnnemi(ActionEnnemi.RECUPERATION, 0, true);
                int choix = r.prochainEntier(100);
                return new DecisionEnnemi(choix < 55 ? ActionEnnemi.PREPARATION : choix < 80 ? ActionEnnemi.ARMURE : ActionEnnemi.ONDE, choix >= 80 ? 16 : 0, true);
    }
    /** Crée un golem en pleine santé. */
    public Golem() { super("Golem", 48, 14, 4); }
    /** {@inheritDoc} */
    @Override public String getType() { return "golem"; }
    /** {@inheritDoc} */
    @Override public int puissanceProchaineAttaque() { return getTours() % 2 == 0 ? 0 : getAttaqueBase() + 8; }
    /** {@inheritDoc} */
    @Override public String getIntention() { return puissanceProchaineAttaque() == 0 ? "Prépare sa frappe : aucun dégât ce tour." : "Frappe lourde : puissance 22. Défendez-vous !"; }
}
