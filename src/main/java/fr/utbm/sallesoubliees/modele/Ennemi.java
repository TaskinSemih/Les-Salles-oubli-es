// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

/** Base polymorphe des adversaires, avec un compteur persistant de réponses jouées. */
// Déclare Ennemi, base abstraite des ennemis ; chaque sous-classe définit sa stratégie par polymorphisme. Hérite de Personnage. Cette classe abstraite ne peut pas être instanciée directement.
public abstract class Ennemi extends Personnage {
    // Déclare le nombre de réponses ennemies de type int.
    private int tours;
    /**
     * Construit un adversaire.
     *
 * @param nom nom affiché
     *
 * @param vie maximum de vie
     *
 * @param attaque puissance normale
     *
 * @param armure armure permanente
     */
    // Construit un objet Ennemi : base abstraite des ennemis ; chaque sous-classe définit sa stratégie par polymorphisme. Initialise la classe mère avec nom, vie, attaque, armure.
    protected Ennemi(String nom, int vie, int attaque, int armure) { super(nom, vie, attaque, armure); }
    /** {@return nombre de réponses ennemies déjà effectuées} */
    // Permet de consulter le compteur de réponses. Renvoie le nombre de réponses ennemies.
    public final int getTours() { return tours; }
    /** {@return identifiant stable du type d'ennemi} */
    // Permet de consulter l'identifiant du type d'ennemi.
    public abstract String getType();
    /** {@return puissance du prochain coup, ou zéro pour une préparation} */
    // Permet de consulter la puissance du prochain coup du résolveur simple.
    public abstract int puissanceProchaineAttaque();
    /** {@return intention lisible avant le choix du joueur} */
    // Permet de consulter le texte d'intention du résolveur simple.
    public abstract String getIntention();
    /** Choisit une intention selon sa stratégie propre.
     *
 * @param contexte informations du combat
 * @param hasard tirage injectable
 * @return décision annoncée */
    // Choisit une intention ennemie à partir du contexte et du hasard injecté.
    public abstract DecisionEnnemi decider(ContexteCombat contexte, SourceAleatoire hasard);
    /** {@return vrai si l'attaque traverse l'armure permanente} */
    // Indique si les frappes ignorent l'armure permanente. Renvoie faux.
    public boolean ignoreArmure() { return false; }
    /** Comptabilise une réponse après son exécution. */
    // Comptabilise une réponse après son exécution. Incrémente le nombre de réponses ennemies de un.
    final void terminerTour() { tours++; }
    /** Restaure un compteur positif ou nul et raisonnablement borné. */
    // Restaure un compteur positif ou nul et raisonnablement borné.
    final void restaurerTours(int valeur) {
        // Si (valeur est inférieur à 0 ou valeur est supérieur à 1_000_000), refuse l’opération : "Compteur de tours invalide.".
        if (valeur < 0 || valeur > 1_000_000) throw new IllegalArgumentException("Compteur de tours invalide.");
        // Met à jour le nombre de réponses ennemies avec valeur.
        tours = valeur;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
