package fr.utbm.sallesoubliees.modele;

/** Adversaire régulier, utile pour apprendre le combat. */
public final class Squelette extends Ennemi {
    /** {@inheritDoc} */
    @Override public DecisionEnnemi decider(ContexteCombat c, SourceAleatoire r) {
        if (getTours() < 2 || c.precedente() == ActionEnnemi.RIPOSTE)
                    return new DecisionEnnemi(ActionEnnemi.ATTAQUE, 9, true);
                return new DecisionEnnemi(r.prochainEntier(100) < 40 ? ActionEnnemi.RIPOSTE : ActionEnnemi.ATTAQUE, 9, true);
    }
    /** Crée un squelette en pleine santé. */
    public Squelette() { super("Squelette", 30, 9, 1); }
    /** {@inheritDoc} */
    @Override public String getType() { return "squelette"; }
    /** {@inheritDoc} */
    @Override public int puissanceProchaineAttaque() { return getAttaqueBase(); }
    /** {@inheritDoc} */
    @Override public String getIntention() { return "Coup d'épée : puissance 9."; }
}
