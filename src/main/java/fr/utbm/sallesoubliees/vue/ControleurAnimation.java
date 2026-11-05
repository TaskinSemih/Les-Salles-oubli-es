// Place cette classe dans la vue, responsable de la présentation Swing et des dessins.
package fr.utbm.sallesoubliees.vue;

// Rend disponibles les types ou constantes de fr.utbm.sallesoubliees.modele sans répéter leur nom complet.
import fr.utbm.sallesoubliees.modele.*;

/** Horloge de présentation testable sans Swing ; ne possède aucune commande métier. */
// Déclare ControleurAnimation, lecteur des événements déjà résolus, qui modifie uniquement une copie visuelle. final interdit de créer une sous-classe.
public final class ControleurAnimation {
    // Déclare le résultat de l'action de type ResultatAction.
    private ResultatAction resultat;
    // Déclare l'indice de l'événement présenté de type int.
    private int index;
    // Déclare le temps écoulé dans l'événement de type double.
    private double temps;
    // Déclare la vie affichée du héros de type int.
    private int vieHeros;
    // Déclare la vie affichée de l'ennemi de type int.
    private int vieEnnemi;
    // Déclare le stock de potions de type int.
    private int potions;
    // Déclare la réserve d'élan de type int.
    private int elan;
    // Initialise les altérations actives avec une nouvelle collection indépendante et vide.
    private java.util.List<EffetCombat> effets = new java.util.ArrayList<>();
    /** {@return Élan de présentation} */
    // Permet de consulter l'élan disponible. Renvoie la réserve d'élan.
    public int getElan() { return elan; }
    /** {@return altérations de présentation} */
    // Permet de consulter les effets actifs. Renvoie java.util.List.copyOf(effets).
    public java.util.List<EffetCombat> getEffets() { return java.util.List.copyOf(effets); }
    // Déclare le marqueur de défense de type boolean.
    private boolean defense;
    // Déclare le marqueur de rage du Gardien de type boolean.
    private boolean rage;
    // Déclare l'option de réduction des animations de type boolean.
    private boolean reduite;
    // Déclare la mort visuelle du héros de type boolean.
    private boolean mortHeros;
    // Déclare la mort visuelle de l'ennemi de type boolean.
    private boolean mortEnnemi;

    /** Construit un lecteur au repos. */
    // Construit un objet ControleurAnimation : lecteur des événements déjà résolus, qui modifie uniquement une copie visuelle.
    public ControleurAnimation() { }
    /**
     * Prépare la copie visuelle de l'état avant sans modifier la partie réelle.
     *
 * @param resolution action déjà résolue
     */
    // Initialise la copie visuelle depuis l’état avant et présente le premier fait.
    public void demarrer(ResultatAction resolution) {
        // Si si la séquence visuelle est en cours, refuse l’opération : "Une séquence est déjà active.".
        if (estActive()) throw new IllegalStateException("Une séquence est déjà active.");
        // Si (non (si la commande est valide de « resolution ») ou resolution.evenements().isEmpty()), refuse l’opération : "Séquence vide.".
        if (!resolution.valide() || resolution.evenements().isEmpty()) throw new IllegalArgumentException("Séquence vide.");
        // Met à jour le résultat de l'action avec resolution. Met à jour l'indice de l'événement présenté avec 0. Met à jour le temps écoulé dans l'événement avec 0. Met à jour le marqueur de défense avec faux. Met à jour la mort visuelle du héros avec faux. Met à jour la mort visuelle de l'ennemi avec faux.
        resultat = resolution; index = 0; temps = 0; defense = false; mortHeros = false; mortEnnemi = false;
        // Met à jour la vie affichée du héros avec resolution.avant().heros().vie(). Met à jour le stock de potions avec resolution.avant().heros().potions().
        vieHeros = resolution.avant().heros().vie(); potions = resolution.avant().heros().potions();
        // Met à jour la réserve d'élan avec resolution.avant().tactique().elan(). Met à jour les altérations actives avec une nouvelle collection indépendante initialisée avec resolution.avant().tactique().effets().
        elan = resolution.avant().tactique().elan(); effets = new java.util.ArrayList<>(resolution.avant().tactique().effets());
        // Recherche dans l’état avant l’ennemi de la salle occupée, pour initialiser ses PV visuels.
        var ennemi = resolution.avant().salles().stream().filter(s -> s.id().equals(resolution.avant().salleActuelle())).findFirst().orElseThrow().ennemi();
        // Met à jour la vie affichée de l'ennemi avec ennemi == null ? 0 : ennemi.vie(). Met à jour le marqueur de rage du Gardien avec ennemi != null && ennemi.type().equals("boss") && ennemi.vie() <= 45.
        vieEnnemi = ennemi == null ? 0 : ennemi.vie(); rage = ennemi != null && ennemi.type().equals("boss") && ennemi.vie() <= 45;
        // Applique l’effet ou le fait courant dans cette couche.
        appliquer();
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /**
     * Avance l'horloge de présentation ; accepte un grand pas sans perdre de fait.
     *
 * @param millisecondes temps écoulé positif
     */
    // Consomme le temps écoulé et passe autant d’événements que nécessaire.
    public void avancer(double millisecondes) {
        // Si millisecondes est inférieur à 0, refuse l’opération : "Temps négatif.".
        if (millisecondes < 0) throw new IllegalArgumentException("Temps négatif.");
        // Augmente le temps écoulé dans l'événement avec millisecondes.
        temps += millisecondes;
        // Répète ce bloc tant que (si la séquence visuelle est en cours et le temps écoulé dans l'événement est supérieur ou égal à la durée restante).
        while (estActive() && temps >= duree()) {
            // Diminue le temps écoulé dans l'événement avec la durée restante. Incrémente l'indice de l'événement présenté de un.
            temps -= duree(); index++;
            // Si si la séquence visuelle est en cours, Applique l’effet ou le fait courant dans cette couche. Dans le cas contraire, Termine le combat ou la lecture visuelle courante.
            if (estActive()) appliquer(); else terminer();
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Applique uniquement les nombres de la copie visuelle au début du fait courant. */
    // Met à jour les effets ou les valeurs du fait courant dans cette couche.
    private void appliquer() {
        // Initialise la variable e de type EvenementJeu avec le fait visuel courant.
        EvenementJeu e = courant();
        // Sélectionne le traitement correspondant à la valeur examinée dans le switch.
        switch (e.type()) {
            // Pour DEGATS, SOIN : ouvre le traitement de cette branche.
            case DEGATS, SOIN -> {
                // Si la cible de la variable e est égal à EvenementJeu.Cible.HEROS, Met à jour la vie affichée du héros avec les PV après le fait de la variable e. Dans le cas contraire, Met à jour la vie affichée de l'ennemi avec les PV après le fait de la variable e.
                if (e.cible() == EvenementJeu.Cible.HEROS) vieHeros = e.vieApres(); else vieEnnemi = e.vieApres();
                // Si (le type de la variable e est égal à EvenementJeu.Type.SOIN et la cible de la variable e est égal à EvenementJeu.Cible.HEROS), Décrémente le stock de potions de un.
                if (e.type() == EvenementJeu.Type.SOIN && e.cible() == EvenementJeu.Cible.HEROS) potions--;
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
            // Pour DEFENSE : Met à jour le marqueur de défense avec vrai.
            case DEFENSE -> defense = true;
            // Pour ELAN : Met à jour la réserve d'élan avec la valeur portée par le fait de la variable e.
            case ELAN -> elan = e.valeur();
            // Pour EFFET : ouvre le traitement de cette branche.
            case EFFET -> {
                // Initialise « type » avec EffetCombat.Type.values()[e.valeur()].
                var type = EffetCombat.Type.values()[e.valeur()];
                // Retire les éléments qui vérifient le prédicat fourni ; arguments : v -> v.type() == type && v.cible() == e.cible().
                effets.removeIf(v -> v.type() == type && v.cible() == e.cible());
                // Ajoute l’élément à la collection ou au conteneur ; arguments : new EffetCombat(type, e.cible(), 1).
                effets.add(new EffetCombat(type, e.cible(), 1));
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
            // Pour EXPIRATION : Retire les éléments qui vérifient le prédicat fourni ; arguments : v -> v.type().ordinal() == e.valeur() && v.cible() == e.cible().
            case EXPIRATION -> effets.removeIf(v -> v.type().ordinal() == e.valeur() && v.cible() == e.cible());
            // Pour RAGE : Met à jour le marqueur de rage du Gardien avec vrai.
            case RAGE -> rage = true;
            // Pour RECOMPENSE : Augmente le stock de potions avec la valeur portée par le fait de la variable e.
            case RECOMPENSE -> potions += e.valeur();
            // Un fait de mort active uniquement le marqueur de présentation du personnage concerné.
            case MORT -> { if (e.cible() == EvenementJeu.Cible.HEROS) mortHeros = true; else mortEnnemi = true; }
            // Pour les autres cas : aucun traitement particulier n’est nécessaire.
            default -> { }
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Aligne exactement les dernières valeurs et expire la défense. */
    // Nettoie le combat ou aligne la copie visuelle finale, selon cette classe.
    private void terminer() {
        // Met à jour la vie affichée du héros avec resultat.apres().heros().vie(). Met à jour le stock de potions avec resultat.apres().heros().potions(). Met à jour le marqueur de défense avec faux.
        vieHeros = resultat.apres().heros().vie(); potions = resultat.apres().heros().potions(); defense = false;
        // Met à jour la réserve d'élan avec resultat.apres().tactique().elan(). Met à jour les altérations actives avec une nouvelle collection indépendante initialisée avec resultat.apres().tactique().effets().
        elan = resultat.apres().tactique().elan(); effets = new java.util.ArrayList<>(resultat.apres().tactique().effets());
        // Initialise l'ennemi avec resultat.apres().salles().stream().filter(s -> s.id().equals(resultat.apres().salleActuelle())).findFirst().orElseThrow().ennemi().
        var ennemi = resultat.apres().salles().stream().filter(s -> s.id().equals(resultat.apres().salleActuelle())).findFirst().orElseThrow().ennemi();
        // À la fin de la séquence, reprend la vie finale de l’ennemi ou zéro si la salle n’en contient pas.
        vieEnnemi = ennemi == null ? 0 : ennemi.vie();
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Termine visuellement l'action déjà résolue, sans rejouer aucun fait métier. */
    // Termine la lecture visuelle sans rejouer les actions du modèle. Si le résultat de l'action est différent de null, Met à jour l'indice de l'événement présenté avec resultat.evenements().size(). Termine le combat ou la lecture visuelle courante.
    public void finirImmediatement() { if (resultat != null) { index = resultat.evenements().size(); terminer(); } }
    /** {@return fait actuellement présenté ou null au repos} */
    // Permet de consulter le fait visuel courant. Renvoie estActive() ? resultat.evenements().get(index) : null.
    public EvenementJeu courant() { return estActive() ? resultat.evenements().get(index) : null; }
    /** {@return vrai pendant la lecture de la séquence} */
    // Indique si la séquence visuelle est en cours. Renvoie resultat != null && index < resultat.evenements().size().
    public boolean estActive() { return resultat != null && index < resultat.evenements().size(); }
    /** {@return progression normalisée du fait courant} */
    // Permet de consulter la progression visuelle entre zéro et un. Renvoie estActive() ? Math.min(1, temps / duree()) : 1.
    public double progression() { return estActive() ? Math.min(1, temps / duree()) : 1; }
    /** {@return PV affichés du héros} */
    // { Renvoie la vie affichée du héros.
    public int getVieHeros() { return vieHeros; }
    /** {@return PV affichés de l'ennemi} */
    // { Renvoie la vie affichée de l'ennemi.
    public int getVieEnnemi() { return vieEnnemi; }
    /** {@return stock affiché} */
    // Permet de consulter le stock de potions. Renvoie le stock de potions.
    public int getPotions() { return potions; }
    /** {@return défense encore montrée dans cette séquence} */
    // Indique si la défense est affichée. Renvoie le marqueur de défense.
    public boolean estDefense() { return defense; }
    /** {@return phase renforcée déjà présentée} */
    // Indique si la rage a été présentée. Renvoie le marqueur de rage du Gardien.
    public boolean estRage() { return rage; }
    /** {@return mort du héros déjà présentée} */
    // Indique si la mort du héros a été présentée. Renvoie la mort visuelle du héros.
    public boolean estMortHeros() { return mortHeros; }
    /** {@return mort ennemie déjà présentée} */
    // Indique si la mort ennemie a été présentée. Renvoie la mort visuelle de l'ennemi.
    public boolean estMortEnnemi() { return mortEnnemi; }
    /**
     * Réduit les déplacements et la durée des séquences.
     *
 * @param valeur option d'accessibilité
     */
    // Réduit les déplacements et la durée des séquences. Met à jour l'option de réduction des animations avec valeur.
    public void setReduite(boolean valeur) { reduite = valeur; }
    /** {@return option d'animations réduites} */
    // Indique si les animations sont réduites. Renvoie l'option de réduction des animations.
    public boolean estReduite() { return reduite; }
    /** Durées courtes, exclusivement dans la présentation. */
    // Permet de consulter la durée restante.
    private double duree() {
        // Si l'option de réduction des animations, renvoie 65.
        if (reduite) return 65;
        // Renvoie la valeur produite par le switch selon le cas rencontré.
        return switch (courant().type()) { case ATTAQUE -> 260; case DEGATS, SOIN -> 330; case MORT -> 420; case PREPARATION, RAGE -> 330; default -> 180; };
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
