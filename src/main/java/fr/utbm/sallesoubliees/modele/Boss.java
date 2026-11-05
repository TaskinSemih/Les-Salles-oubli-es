// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

/** Gardien final qui devient enragé à la moitié de sa vie. */
// Déclare Boss, Gardien final dont les décisions dépendent de trois phases de vie et des habitudes du joueur. Hérite de Ennemi. final interdit de créer une sous-classe.
public final class Boss extends Ennemi {
    /** {@inheritDoc} */
    // Choisit une intention ennemie à partir du contexte et du hasard injecté.
    @Override public DecisionEnnemi decider(ContexteCombat c, SourceAleatoire r) {
        // Déduit la phase des PV : phase 3 à 22 ou moins, phase 2 à 45 ou moins, sinon phase 1.
        int phase = getVie() <= 22 ? 3 : getVie() <= 45 ? 2 : 1;
                // Après une préparation, annonce une frappe lourde de puissance 30 en phase 3, sinon 26.
                if (c.precedente() == ActionEnnemi.PREPARATION) return new DecisionEnnemi(ActionEnnemi.FRAPPE_LOURDE, phase == 3 ? 30 : 26, true);
                // Après une frappe lourde ou une onde, réserve la prochaine réponse à la récupération.
                if (c.precedente() == ActionEnnemi.FRAPPE_LOURDE || c.precedente() == ActionEnnemi.ONDE)
                    // Renvoie un nouvel objet DecisionEnnemi avec ActionEnnemi.RECUPERATION, 0, true.
                    return new DecisionEnnemi(ActionEnnemi.RECUPERATION, 0, true);
                // Initialise le résultat du tirage aléatoire avec un tirage reproductible entre 0 et 99.
                int choix = r.prochainEntier(100);
                // Face à trois commandes identiques, choisit la riposte pour un tirage sous 65, sauf si la réponse précédente était déjà une riposte.
                if (c.repetition() && c.precedente() != ActionEnnemi.RIPOSTE && choix < 65)
                    // Renvoie un nouvel objet DecisionEnnemi avec ActionEnnemi.RIPOSTE, 14 + phase * 2, true.
                    return new DecisionEnnemi(ActionEnnemi.RIPOSTE, 14 + phase * 2, true);
                // Sans réaction prioritaire : attaque sous 35, préparation sous 55, riposte sous 75 ; au-delà, onde dès la phase 2 ou marque en phase 1.
                ActionEnnemi a = choix < 35 ? ActionEnnemi.ATTAQUE : choix < 55 ? ActionEnnemi.PREPARATION : choix < 75 ? ActionEnnemi.RIPOSTE : phase >= 2 ? ActionEnnemi.ONDE : ActionEnnemi.MARQUE;
                // Remplace une seconde riposte consécutive par une attaque normale.
                if (a == ActionEnnemi.RIPOSTE && c.precedente() == a) a = ActionEnnemi.ATTAQUE;
                // Annonce précisément l’action choisie : attaque 12 + 3 × phase, onde 14 + 3 × phase, riposte 14 + 2 × phase, zéro pour les autres actions.
                return new DecisionEnnemi(a, a == ActionEnnemi.ATTAQUE ? 12 + phase * 3 : a == ActionEnnemi.ONDE ? 14 + phase * 3 : a == ActionEnnemi.RIPOSTE ? 14 + phase * 2 : 0, true);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Crée le gardien final. */
    // Crée le Gardien des oubliés avec 90 PV maximum, 16 de puissance de base et 3 d’armure permanente.
    public Boss() { super("Gardien des oubliés", 90, 16, 3); }
    /** {@inheritDoc} */
    // Permet de consulter l'identifiant du type d'ennemi. Renvoie le texte "boss".
    @Override public String getType() { return "boss"; }
    /** {@inheritDoc} */
    // Permet de consulter la puissance du prochain coup du résolveur simple. Renvoie getVie() <= 45 ? 24 : getAttaqueBase().
    @Override public int puissanceProchaineAttaque() { return getVie() <= 45 ? 24 : getAttaqueBase(); }
    /** {@inheritDoc} */
    // Permet de consulter le texte d'intention du résolveur simple. Renvoie "Frappe : puissance " + puissanceProchaineAttaque() + ". Rage (24) dès 45 PV, y compris après votre coup.".
    @Override public String getIntention() { return "Frappe : puissance " + puissanceProchaineAttaque() + ". Rage (24) dès 45 PV, y compris après votre coup."; }
// Ferme le bloc ou la liste de données commencé plus haut.
}
