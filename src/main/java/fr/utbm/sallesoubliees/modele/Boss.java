package fr.utbm.sallesoubliees.modele;

/** Gardien final qui devient enragé à la moitié de sa vie. */
public final class Boss extends Ennemi {
    /** {@inheritDoc} */
    @Override public DecisionEnnemi decider(ContexteCombat c, SourceAleatoire r) {
        int phase = getVie() <= 22 ? 3 : getVie() <= 45 ? 2 : 1;
                if (c.precedente() == ActionEnnemi.PREPARATION) return new DecisionEnnemi(ActionEnnemi.FRAPPE_LOURDE, phase == 3 ? 30 : 26, true);
                if (c.precedente() == ActionEnnemi.FRAPPE_LOURDE || c.precedente() == ActionEnnemi.ONDE)
                    return new DecisionEnnemi(ActionEnnemi.RECUPERATION, 0, true);
                int choix = r.prochainEntier(100);
                if (c.repetition() && c.precedente() != ActionEnnemi.RIPOSTE && choix < 65)
                    return new DecisionEnnemi(ActionEnnemi.RIPOSTE, 14 + phase * 2, true);
                ActionEnnemi a = choix < 35 ? ActionEnnemi.ATTAQUE : choix < 55 ? ActionEnnemi.PREPARATION : choix < 75 ? ActionEnnemi.RIPOSTE : phase >= 2 ? ActionEnnemi.ONDE : ActionEnnemi.MARQUE;
                if (a == ActionEnnemi.RIPOSTE && c.precedente() == a) a = ActionEnnemi.ATTAQUE;
                return new DecisionEnnemi(a, a == ActionEnnemi.ATTAQUE ? 12 + phase * 3 : a == ActionEnnemi.ONDE ? 14 + phase * 3 : a == ActionEnnemi.RIPOSTE ? 14 + phase * 2 : 0, true);
    }
    /** Crée le gardien final. */
    public Boss() { super("Gardien des oubliés", 90, 16, 3); }
    /** {@inheritDoc} */
    @Override public String getType() { return "boss"; }
    /** {@inheritDoc} */
    @Override public int puissanceProchaineAttaque() { return getVie() <= 45 ? 24 : getAttaqueBase(); }
    /** {@inheritDoc} */
    @Override public String getIntention() { return "Frappe : puissance " + puissanceProchaineAttaque() + ". Rage (24) dès 45 PV, y compris après votre coup."; }
}
