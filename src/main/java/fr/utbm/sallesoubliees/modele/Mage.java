// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

/** Ignore l'armure permanente, tout en respectant la défense temporaire. */
// Déclare Mage, ennemi magique qui ignore l'armure permanente et peut maudire, se protéger ou se soigner. Hérite de Ennemi. final interdit de créer une sous-classe.
public final class Mage extends Ennemi {
    /** {@inheritDoc} */
    // Choisit une intention ennemie à partir du contexte et du hasard injecté.
    @Override public DecisionEnnemi decider(ContexteCombat c, SourceAleatoire r) {
        // À 14 PV ou moins, annonce son soin unique de combat si celui-ci n’a pas encore été consommé.
        if (!c.soinUtilise() && getVie() <= 14) return new DecisionEnnemi(ActionEnnemi.SOIN, 0, true);
                // Après une malédiction, une protection ou un soin, impose une attaque pour éviter de répéter un soutien.
                if (c.precedente() == ActionEnnemi.MALEDICTION || c.precedente() == ActionEnnemi.ARMURE || c.precedente() == ActionEnnemi.SOIN)
                    // Renvoie un nouvel objet DecisionEnnemi avec ActionEnnemi.ATTAQUE, 11, false.
                    return new DecisionEnnemi(ActionEnnemi.ATTAQUE, 11, false);
                // Initialise le résultat du tirage aléatoire avec un tirage reproductible entre 0 et 99.
                int choix = r.prochainEntier(100);
                // Choisit la malédiction sous le seuil 60 si le héros a au moins 2 élans ou a bu au moins deux des trois dernières actions ; sinon seuil 25. Entre ce seuil et 74 : attaque ; dès 75 : armure.
                ActionEnnemi a = choix < (c.elan() >= 2 || c.potions() >= 2 ? 60 : 25) ? ActionEnnemi.MALEDICTION : choix < 75 ? ActionEnnemi.ATTAQUE : ActionEnnemi.ARMURE;
                // Construit l’intention : puissance 11 pour l’attaque, zéro sinon ; seule l’armure est annoncée précisément sans observation.
                return new DecisionEnnemi(a, a == ActionEnnemi.ATTAQUE ? 11 : 0, a == ActionEnnemi.ARMURE);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Crée un mage en pleine santé. */
    // Crée le mage avec le nom Mage, 36 PV maximum, 11 de puissance de base et 1 d’armure permanente.
    public Mage() { super("Mage", 36, 11, 1); }
    /** {@inheritDoc} */
    // Permet de consulter l'identifiant du type d'ennemi. Renvoie le texte "mage".
    @Override public String getType() { return "mage"; }
    /** {@inheritDoc} */
    // Permet de consulter la puissance du prochain coup du résolveur simple. Renvoie l'attaque sans équipement.
    @Override public int puissanceProchaineAttaque() { return getAttaqueBase(); }
    /** {@inheritDoc} */
    // Indique si les frappes ignorent l'armure permanente. Renvoie vrai.
    @Override public boolean ignoreArmure() { return true; }
    /** {@inheritDoc} */
    // Permet de consulter le texte d'intention du résolveur simple. Renvoie le texte "Sort : puissance 11, ignore l'armure. La défense reste efficace.".
    @Override public String getIntention() { return "Sort : puissance 11, ignore l'armure. La défense reste efficace."; }
// Ferme le bloc ou la liste de données commencé plus haut.
}
