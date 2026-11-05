// Place cette classe dans le contrôleur, qui coordonne les demandes de l’interface.
package fr.utbm.sallesoubliees.controleur;

// Rend disponible Arme depuis fr.utbm.sallesoubliees.modele.
import fr.utbm.sallesoubliees.modele.Arme;
// Rend disponible Partie depuis fr.utbm.sallesoubliees.modele.
import fr.utbm.sallesoubliees.modele.Partie;
// Rend disponible EtatSauvegarde depuis fr.utbm.sallesoubliees.modele.
import fr.utbm.sallesoubliees.modele.EtatSauvegarde;
// Rend disponible SauvegardePartie depuis fr.utbm.sallesoubliees.modele.
import fr.utbm.sallesoubliees.modele.SauvegardePartie;
// Rend disponible Objects depuis java.util.
import java.util.Objects;
// Rend disponible ActionCombat depuis fr.utbm.sallesoubliees.modele.
import fr.utbm.sallesoubliees.modele.ActionCombat;
// Rend disponible ResultatAction depuis fr.utbm.sallesoubliees.modele.
import fr.utbm.sallesoubliees.modele.ResultatAction;

/** Point de coordination de l'interface : conserve uniquement la partie actuellement affichée. */
// Déclare ControleurJeu, intermédiaire entre les commandes de l'interface et la partie courante. final interdit de créer une sous-classe.
public final class ControleurJeu {
    // Déclare la partie courante de type Partie.
    private Partie partie;
    /** Crée le contrôleur sur l'accueil, sans partie. */
    // Construit un objet ControleurJeu : intermédiaire entre les commandes de l'interface et la partie courante.
    public ControleurJeu() { }
    /** {@return partie actuelle ou null sur l'accueil initial} */
    // Permet de consulter la partie conservée par le contrôleur. Renvoie la partie courante.
    public Partie getPartie() { return partie; }
    /** Démarre une partie indépendante. */
    // Crée une partie indépendante et initialise son affichage. Met à jour la partie courante avec un nouvel objet Partie.
    public void nouvellePartie() { partie = new Partie(); }
    /**
     * Publie un chargement après validation complète et confirmation de l'interface.
     * @param chargee nouvelle partie validée
     */
    // Publie un chargement après validation complète et confirmation de l'interface. Met à jour la partie courante avec la référence reçue après refus de null.
    public void adopter(Partie chargee) { partie = Objects.requireNonNull(chargee); }
    /** Abandonne la partie après confirmation. */
    // Abandonne la partie après confirmation. Met à jour la partie courante avec l’absence de valeur (null).
    public void abandonner() { partie = null; }
    /** {@return instantané capturé avant de lancer le travail de fichier} */
    // Produit un instantané indépendant pour décrire ou sauvegarder l’état courant. Renvoie SauvegardePartie.capturer(partie).
    public EtatSauvegarde capturer() { return SauvegardePartie.capturer(partie); }
    /** {@return événements de l'attaque} */
    // { Renvoie partie.attaquer().
    public String attaquer() { return partie.attaquer(); }
    /** {@return événements de la défense} */
    // { Renvoie partie.defendre().
    public String defendre() { return partie.defendre(); }
    /** {@return événements de la potion} */
    // { Renvoie partie.boirePotion().
    public String boire() { return partie.boirePotion(); }
    /** {@return récompense du coffre} */
    // { Renvoie partie.ouvrirCoffre().
    public String ouvrir() { return partie.ouvrirCoffre(); }
    /** {@return effet du repos} */
    // { Renvoie partie.seReposer().
    public String reposer() { return partie.seReposer(); }
    /**
     * Résout exactement un tour, à rejouer uniquement dans la présentation.
     * @param action commande du joueur
     * @return faits et instantanés indépendants
     */
    // Valide ou résout une commande complète et fournit les faits dans leur ordre métier. Renvoie partie.resoudre(action).
    public ResultatAction resoudre(ActionCombat action) { return partie.resoudre(action); }
    /**
     * Déplace le héros selon les règles.
     * @param salle destination
     * @return événement
     */
    // Demande ou applique un déplacement conforme aux passages du donjon. Renvoie partie.deplacer(salle).
    public String deplacer(String salle) { return partie.deplacer(salle); }
    /**
     * Équipe une arme possédée.
     * @param arme arme sélectionnée
     * @return événement
     */
    // Change l’arme équipée si les règles autorisent son utilisation. Renvoie partie.equiper(arme).
    public String equiper(Arme arme) { return partie.equiper(arme); }
// Ferme le bloc ou la liste de données commencé plus haut.
}
