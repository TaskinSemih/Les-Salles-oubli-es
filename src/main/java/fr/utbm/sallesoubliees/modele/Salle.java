package fr.utbm.sallesoubliees.modele;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/** Salle fixe avec son état persistant d'exploration et son éventuel ennemi. */
public final class Salle {
    private final String id;
    private final String nom;
    private final int colonne;
    private final int ligne;
    private final Ennemi ennemi;
    private final Arme coffre;
    private final boolean repos;
    private final Set<String> passages = new LinkedHashSet<>();
    private boolean visitee;
    private boolean utilisee;

    /** Construit une salle du catalogue interne. */
    Salle(String id, String nom, int colonne, int ligne, Ennemi ennemi, Arme coffre, boolean repos) {
        this.id = id; this.nom = nom; this.colonne = colonne; this.ligne = ligne;
        this.ennemi = ennemi; this.coffre = coffre; this.repos = repos;
    }
    /** {@return identifiant de sauvegarde} */
    public String getId() { return id; }
    /** {@return nom français} */
    public String getNom() { return nom; }
    /** {@return colonne dans la carte} */
    public int getColonne() { return colonne; }
    /** {@return ligne dans la carte} */
    public int getLigne() { return ligne; }
    /** {@return adversaire, ou null pour une salle paisible} */
    public Ennemi getEnnemi() { return ennemi; }
    /** {@return arme du coffre, ou null sans coffre} */
    public Arme getCoffre() { return coffre; }
    /** {@return vrai pour le sanctuaire} */
    public boolean estRepos() { return repos; }
    /** {@return vrai après la première entrée} */
    public boolean estVisitee() { return visitee; }
    /** {@return vrai après ouverture du coffre ou utilisation du repos} */
    public boolean estUtilisee() { return utilisee; }
    /** {@return passages non modifiables} */
    public Set<String> getPassages() { return Collections.unmodifiableSet(passages); }
    /** {@return présence d'un ennemi vivant} */
    public boolean estHostile() { return ennemi != null && !ennemi.estMort(); }
    /** Ajoute une arête lors de la création du donjon. */
    void relier(String destination) { passages.add(destination); }
    /** Marque l'entrée dans la salle. */
    void visiter() { visitee = true; }
    /** Consomme le coffre ou le repos. */
    void utiliser() { utilisee = true; }
}
