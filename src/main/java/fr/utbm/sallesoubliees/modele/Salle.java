// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

// Rend disponible Collections depuis java.util.
import java.util.Collections;
// Rend disponible LinkedHashSet depuis java.util.
import java.util.LinkedHashSet;
// Rend disponible Set depuis java.util.
import java.util.Set;

/** Salle fixe avec son état persistant d'exploration et son éventuel ennemi. */
// Déclare Salle, salle du donjon avec ses voisins, son contenu et son état de visite ou d'utilisation. final interdit de créer une sous-classe.
public final class Salle {
    // Déclare l'identifiant de salle de type String.
    private final String id;
    // Déclare le nom de type String.
    private final String nom;
    // Déclare la colonne de la salle sur la carte de type int.
    private final int colonne;
    // Déclare la ligne courante de type int.
    private final int ligne;
    // Déclare l'ennemi de type Ennemi.
    private final Ennemi ennemi;
    // Déclare l'arme du coffre de type Arme.
    private final Arme coffre;
    // Déclare la présence du sanctuaire de type boolean.
    private final boolean repos;
    // Initialise les identifiants des salles voisines avec une nouvelle collection indépendante et vide.
    private final Set<String> passages = new LinkedHashSet<>();
    // Déclare le marqueur de visite de type boolean.
    private boolean visitee;
    // Déclare le marqueur de coffre ou repos déjà utilisé de type boolean.
    private boolean utilisee;

    /** Construit une salle du catalogue interne. */
    // Construit un objet Salle : salle du donjon avec ses voisins, son contenu et son état de visite ou d'utilisation.
    Salle(String id, String nom, int colonne, int ligne, Ennemi ennemi, Arme coffre, boolean repos) {
        // Met à jour l'identifiant de salle avec l'identifiant de salle. Met à jour le nom avec le nom. Met à jour la colonne de la salle sur la carte avec la colonne de la salle sur la carte. Met à jour la ligne courante avec la ligne courante.
        this.id = id; this.nom = nom; this.colonne = colonne; this.ligne = ligne;
        // Met à jour l'ennemi avec l'ennemi. Met à jour l'arme du coffre avec l'arme du coffre. Met à jour la présence du sanctuaire avec la présence du sanctuaire.
        this.ennemi = ennemi; this.coffre = coffre; this.repos = repos;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** {@return identifiant de sauvegarde} */
    // Permet de consulter l'identifiant. Renvoie l'identifiant de salle.
    public String getId() { return id; }
    /** {@return nom français} */
    // Permet de consulter le nom affiché. Renvoie le nom.
    public String getNom() { return nom; }
    /** {@return colonne dans la carte} */
    // Permet de consulter la colonne. Renvoie la colonne de la salle sur la carte.
    public int getColonne() { return colonne; }
    /** {@return ligne dans la carte} */
    // Permet de consulter la ligne. Renvoie la ligne courante.
    public int getLigne() { return ligne; }
    /** {@return adversaire, ou null pour une salle paisible} */
    // { Renvoie l'ennemi.
    public Ennemi getEnnemi() { return ennemi; }
    /** {@return arme du coffre, ou null sans coffre} */
    // Permet de consulter l'arme du coffre. Renvoie l'arme du coffre.
    public Arme getCoffre() { return coffre; }
    /** {@return vrai pour le sanctuaire} */
    // Indique si la salle est le sanctuaire. Renvoie la présence du sanctuaire.
    public boolean estRepos() { return repos; }
    /** {@return vrai après la première entrée} */
    // Indique si la salle a été visitée. Renvoie le marqueur de visite.
    public boolean estVisitee() { return visitee; }
    /** {@return vrai après ouverture du coffre ou utilisation du repos} */
    // Indique si le coffre ou le repos a déjà servi. Renvoie le marqueur de coffre ou repos déjà utilisé.
    public boolean estUtilisee() { return utilisee; }
    /** {@return passages non modifiables} */
    // Permet de consulter les passages autorisés. Renvoie une vue en lecture seule de la collection interne.
    public Set<String> getPassages() { return Collections.unmodifiableSet(passages); }
    /** {@return présence d'un ennemi vivant} */
    // Indique si la salle contient un ennemi vivant. Renvoie ennemi != null && !ennemi.estMort().
    public boolean estHostile() { return ennemi != null && !ennemi.estMort(); }
    /** Ajoute une arête lors de la création du donjon. */
    // Ajoute le lien de passage demandé ; Donjon construit les liens dans les deux sens. Ajoute l’élément à la collection ou au conteneur ; arguments : destination.
    void relier(String destination) { passages.add(destination); }
    /** Marque l'entrée dans la salle. */
    // Marque la salle comme déjà visitée. Met à jour le marqueur de visite avec vrai.
    void visiter() { visitee = true; }
    /** Consomme le coffre ou le repos. */
    // Marque le coffre ou le sanctuaire comme définitivement utilisé. Met à jour le marqueur de coffre ou repos déjà utilisé avec vrai.
    void utiliser() { utilisee = true; }
// Ferme le bloc ou la liste de données commencé plus haut.
}
