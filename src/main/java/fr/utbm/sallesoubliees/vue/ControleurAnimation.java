package fr.utbm.sallesoubliees.vue;

import fr.utbm.sallesoubliees.modele.*;

/** Horloge de présentation testable sans Swing ; ne possède aucune commande métier. */
public final class ControleurAnimation {
    private ResultatAction resultat;
    private int index;
    private double temps;
    private int vieHeros;
    private int vieEnnemi;
    private int potions;
    private boolean defense;
    private boolean rage;
    private boolean reduite;
    private boolean mortHeros;
    private boolean mortEnnemi;

    /** Construit un lecteur au repos. */
    public ControleurAnimation() { }
    /**
     * Prépare la copie visuelle de l'état avant sans modifier la partie réelle.
     * @param resolution action déjà résolue
     */
    public void demarrer(ResultatAction resolution) {
        if (estActive()) throw new IllegalStateException("Une séquence est déjà active.");
        if (!resolution.valide() || resolution.evenements().isEmpty()) throw new IllegalArgumentException("Séquence vide.");
        resultat = resolution; index = 0; temps = 0; defense = false; mortHeros = false; mortEnnemi = false;
        vieHeros = resolution.avant().heros().vie(); potions = resolution.avant().heros().potions();
        var ennemi = resolution.avant().salles().stream().filter(s -> s.id().equals(resolution.avant().salleActuelle())).findFirst().orElseThrow().ennemi();
        vieEnnemi = ennemi == null ? 0 : ennemi.vie(); rage = ennemi != null && ennemi.type().equals("boss") && ennemi.vie() <= 45;
        appliquer();
    }
    /**
     * Avance l'horloge de présentation ; accepte un grand pas sans perdre de fait.
     * @param millisecondes temps écoulé positif
     */
    public void avancer(double millisecondes) {
        if (millisecondes < 0) throw new IllegalArgumentException("Temps négatif.");
        temps += millisecondes;
        while (estActive() && temps >= duree()) {
            temps -= duree(); index++;
            if (estActive()) appliquer(); else terminer();
        }
    }
    /** Applique uniquement les nombres de la copie visuelle au début du fait courant. */
    private void appliquer() {
        EvenementJeu e = courant();
        switch (e.type()) {
            case DEGATS, SOIN -> {
                if (e.cible() == EvenementJeu.Cible.HEROS) vieHeros = e.vieApres(); else vieEnnemi = e.vieApres();
                if (e.type() == EvenementJeu.Type.SOIN) potions--;
            }
            case DEFENSE -> defense = true;
            case RAGE -> rage = true;
            case RECOMPENSE -> potions += e.valeur();
            case MORT -> { if (e.cible() == EvenementJeu.Cible.HEROS) mortHeros = true; else mortEnnemi = true; }
            default -> { }
        }
    }
    /** Aligne exactement les dernières valeurs et expire la défense. */
    private void terminer() {
        vieHeros = resultat.apres().heros().vie(); potions = resultat.apres().heros().potions(); defense = false;
        var ennemi = resultat.apres().salles().stream().filter(s -> s.id().equals(resultat.apres().salleActuelle())).findFirst().orElseThrow().ennemi();
        vieEnnemi = ennemi == null ? 0 : ennemi.vie();
    }
    /** Termine visuellement l'action déjà résolue, sans rejouer aucun fait métier. */
    public void finirImmediatement() { if (resultat != null) { index = resultat.evenements().size(); terminer(); } }
    /** {@return fait actuellement présenté ou null au repos} */
    public EvenementJeu courant() { return estActive() ? resultat.evenements().get(index) : null; }
    /** {@return vrai pendant la lecture de la séquence} */
    public boolean estActive() { return resultat != null && index < resultat.evenements().size(); }
    /** {@return progression normalisée du fait courant} */
    public double progression() { return estActive() ? Math.min(1, temps / duree()) : 1; }
    /** {@return PV affichés du héros} */
    public int getVieHeros() { return vieHeros; }
    /** {@return PV affichés de l'ennemi} */
    public int getVieEnnemi() { return vieEnnemi; }
    /** {@return stock affiché} */
    public int getPotions() { return potions; }
    /** {@return défense encore montrée dans cette séquence} */
    public boolean estDefense() { return defense; }
    /** {@return phase renforcée déjà présentée} */
    public boolean estRage() { return rage; }
    /** {@return mort du héros déjà présentée} */
    public boolean estMortHeros() { return mortHeros; }
    /** {@return mort ennemie déjà présentée} */
    public boolean estMortEnnemi() { return mortEnnemi; }
    /**
     * Réduit les déplacements et la durée des séquences.
     * @param valeur option d'accessibilité
     */
    public void setReduite(boolean valeur) { reduite = valeur; }
    /** {@return option d'animations réduites} */
    public boolean estReduite() { return reduite; }
    /** Durées courtes, exclusivement dans la présentation. */
    private double duree() {
        if (reduite) return 65;
        return switch (courant().type()) { case ATTAQUE -> 260; case DEGATS, SOIN -> 330; case MORT -> 420; case PREPARATION, RAGE -> 330; default -> 180; };
    }
}
