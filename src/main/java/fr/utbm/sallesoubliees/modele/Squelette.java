// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

/** Adversaire régulier, utile pour apprendre le combat. */
// Déclare Squelette, premier ennemi, qui attaque puis peut adopter une garde avec riposte. Hérite de Ennemi. final interdit de créer une sous-classe.
public final class Squelette extends Ennemi {
    /** {@inheritDoc} */
    // Choisit une intention ennemie à partir du contexte et du hasard injecté.
    @Override public DecisionEnnemi decider(ContexteCombat c, SourceAleatoire r) {
        // Force une attaque pendant les deux premières réponses et après une riposte, pour éviter deux gardes de suite.
        if (getTours() < 2 || c.precedente() == ActionEnnemi.RIPOSTE)
                    // Renvoie un nouvel objet DecisionEnnemi avec ActionEnnemi.ATTAQUE, 9, true.
                    return new DecisionEnnemi(ActionEnnemi.ATTAQUE, 9, true);
                // Tire une garde avec riposte dans 40 % des cas, sinon une attaque ; puissance 9 et annonce précise.
                return new DecisionEnnemi(r.prochainEntier(100) < 40 ? ActionEnnemi.RIPOSTE : ActionEnnemi.ATTAQUE, 9, true);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Crée un squelette en pleine santé. */
    // Crée le squelette avec 30 PV maximum, 9 de puissance de base et 1 d’armure permanente.
    public Squelette() { super("Squelette", 30, 9, 1); }
    /** {@inheritDoc} */
    // Permet de consulter l'identifiant du type d'ennemi. Renvoie le texte "squelette".
    @Override public String getType() { return "squelette"; }
    /** {@inheritDoc} */
    // Permet de consulter la puissance du prochain coup du résolveur simple. Renvoie l'attaque sans équipement.
    @Override public int puissanceProchaineAttaque() { return getAttaqueBase(); }
    /** {@inheritDoc} */
    // Permet de consulter le texte d'intention du résolveur simple. Renvoie le texte "Coup d'épée : puissance 9.".
    @Override public String getIntention() { return "Coup d'épée : puissance 9."; }
// Ferme le bloc ou la liste de données commencé plus haut.
}
