// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

/** Personnage dont les points de vie restent bornés entre zéro et leur maximum. */
// Déclare Personnage, base abstraite des personnages : nom, statistiques et vie bornée. Cette classe abstraite ne peut pas être instanciée directement.
public abstract class Personnage {
    // Encapsule le nom : private interdit l’accès direct extérieur et final empêche sa réaffectation après construction.
    private final String nom;
    // Fixe une fois pour toutes le plafond de vie de ce personnage.
    private final int vieMax;
    // Conserve la puissance de base, indépendante du bonus d’arme du héros.
    private final int attaque;
    // Conserve la protection permanente, indépendante d’une défense temporaire.
    private final int armure;
    // Conserve les PV qui changent pendant la partie ; les méthodes du modèle maintiennent leurs bornes.
    private int vie;

    /**
     * Construit un personnage en pleine santé.
     * @param nom nom affiché
     * @param vieMax maximum strictement positif
     * @param attaque puissance positive
     * @param armure protection positive ou nulle
     */
    // Construit un objet Personnage : base abstraite des personnages : nom, statistiques et vie bornée.
    protected Personnage(String nom, int vieMax, int attaque, int armure) {
        // Si (le nom est égal à l’absence de valeur (null) ou nom.isBlank() ou le maximum de points de vie est inférieur ou égal à 0 ou l’attaque ou sa commande est inférieur ou égal à 0 ou l'armure permanente est inférieur à 0), entre dans ce traitement conditionnel.
        if (nom == null || nom.isBlank() || vieMax <= 0 || attaque <= 0 || armure < 0) {
            // Signale l’erreur par une exception : "Statistiques invalides.".
            throw new IllegalArgumentException("Statistiques invalides.");
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Met à jour le nom avec le nom. Met à jour le maximum de points de vie avec le maximum de points de vie. Met à jour les points de vie actuels avec le maximum de points de vie.
        this.nom = nom; this.vieMax = vieMax; this.vie = vieMax;
        // Met à jour l’attaque ou sa commande avec l’attaque ou sa commande. Met à jour l'armure permanente avec l'armure permanente.
        this.attaque = attaque; this.armure = armure;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    /** {@return nom affiché} */
    // Permet de consulter le nom affiché. Renvoie le nom.
    public final String getNom() { return nom; }
    /** {@return points de vie actuels} */
    // Permet de consulter les PV actuels. Renvoie les points de vie actuels.
    public final int getVie() { return vie; }
    /** {@return maximum des points de vie} */
    // Permet de consulter le maximum de PV. Renvoie le maximum de points de vie.
    public final int getVieMax() { return vieMax; }
    /** {@return puissance sans équipement} */
    // Permet de consulter l'attaque sans équipement. Renvoie l’attaque ou sa commande.
    public final int getAttaqueBase() { return attaque; }
    /** {@return armure permanente} */
    // Permet de consulter l'armure permanente. Renvoie l'armure permanente.
    public final int getArmure() { return armure; }
    /** {@return vrai si le personnage est mort} */
    // Indique si les PV valent zéro. Renvoie vie == 0.
    public final boolean estMort() { return vie == 0; }

    /** Applique des dégâts non négatifs sans descendre sous zéro. */
    // Applique des dégâts non négatifs sans descendre sous zéro.
    final void subirDegats(int degats) {
        // Si degats est inférieur à 0, refuse l’opération : "Dégâts négatifs.".
        if (degats < 0) throw new IllegalArgumentException("Dégâts négatifs.");
        // Met à jour les points de vie actuels avec les PV diminués des dégâts, sans descendre sous zéro.
        vie = Math.max(0, vie - degats);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    /** Soigne sans dépasser le maximum, même pour une grande quantité. */
    // Soigne sans dépasser le maximum, même pour une grande quantité.
    final void soigner(int soin) {
        // Si soin est inférieur à 0, refuse l’opération : "Soin négatif.".
        if (soin < 0) throw new IllegalArgumentException("Soin négatif.");
        // Ajoute le plus petit montant entre le soin demandé et les PV manquants, pour ne jamais dépasser le maximum ni provoquer un dépassement avec un énorme soin.
        vie += Math.min(soin, vieMax - vie);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    /** Restaure uniquement une valeur bornée, pendant un chargement validé. */
    // Restaure uniquement une valeur bornée, pendant un chargement validé.
    final void restaurerVie(int valeur) {
        // Si (valeur est inférieur à 0 ou valeur est supérieur à le maximum de points de vie), refuse l’opération : "Vie invalide.".
        if (valeur < 0 || valeur > vieMax) throw new IllegalArgumentException("Vie invalide.");
        // Met à jour les points de vie actuels avec valeur.
        vie = valeur;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
