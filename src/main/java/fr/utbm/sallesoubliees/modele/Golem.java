// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

/** Alterne préparation sans dégâts et frappe lourde ; la parité survit au chargement. */
// Déclare Golem, ennemi qui choisit préparation, frappe lourde, armure ou onde, avec des phases de récupération. Hérite de Ennemi. final interdit de créer une sous-classe.
public final class Golem extends Ennemi {
    /** {@inheritDoc} */
    // Choisit une intention ennemie à partir du contexte et du hasard injecté.
    @Override public DecisionEnnemi decider(ContexteCombat c, SourceAleatoire r) {
        // Après une préparation non interrompue, annonce obligatoirement une frappe lourde de puissance 22.
        if (c.precedente() == ActionEnnemi.PREPARATION) return new DecisionEnnemi(ActionEnnemi.FRAPPE_LOURDE, 22, true);
                // Après une frappe lourde ou une onde, impose un tour de récupération sans dégâts.
                if (c.precedente() == ActionEnnemi.FRAPPE_LOURDE || c.precedente() == ActionEnnemi.ONDE)
                    // Renvoie un nouvel objet DecisionEnnemi avec ActionEnnemi.RECUPERATION, 0, true.
                    return new DecisionEnnemi(ActionEnnemi.RECUPERATION, 0, true);
                // Initialise le résultat du tirage aléatoire avec un tirage reproductible entre 0 et 99.
                int choix = r.prochainEntier(100);
                // Répartit le tirage : 55 % préparation, 25 % armure et 20 % onde ; seule l’onde porte ici une puissance de 16.
                return new DecisionEnnemi(choix < 55 ? ActionEnnemi.PREPARATION : choix < 80 ? ActionEnnemi.ARMURE : ActionEnnemi.ONDE, choix >= 80 ? 16 : 0, true);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Crée un golem en pleine santé. */
    // Crée le golem avec 48 PV maximum, 14 de puissance de base et 4 d’armure permanente.
    public Golem() { super("Golem", 48, 14, 4); }
    /** {@inheritDoc} */
    // Permet de consulter l'identifiant du type d'ennemi. Renvoie le texte "golem".
    @Override public String getType() { return "golem"; }
    /** {@inheritDoc} */
    // Permet de consulter la puissance du prochain coup du résolveur simple. Renvoie getTours() % 2 == 0 ? 0 : getAttaqueBase() + 8.
    @Override public int puissanceProchaineAttaque() { return getTours() % 2 == 0 ? 0 : getAttaqueBase() + 8; }
    /** {@inheritDoc} */
    // Permet de consulter le texte d'intention du résolveur simple. Renvoie puissanceProchaineAttaque() == 0 ? "Prépare sa frappe : aucun dégât ce tour." : "Frappe lourde : puissance 22. Défendez-vous !".
    @Override public String getIntention() { return puissanceProchaineAttaque() == 0 ? "Prépare sa frappe : aucun dégât ce tour." : "Frappe lourde : puissance 22. Défendez-vous !"; }
// Ferme le bloc ou la liste de données commencé plus haut.
}
