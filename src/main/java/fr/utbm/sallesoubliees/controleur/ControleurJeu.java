package fr.utbm.sallesoubliees.controleur;

import fr.utbm.sallesoubliees.modele.Arme;
import fr.utbm.sallesoubliees.modele.Partie;
import fr.utbm.sallesoubliees.modele.EtatSauvegarde;
import fr.utbm.sallesoubliees.modele.SauvegardePartie;
import java.util.Objects;
import fr.utbm.sallesoubliees.modele.ActionCombat;
import fr.utbm.sallesoubliees.modele.ResultatAction;

/** Point de coordination de l'interface : conserve uniquement la partie actuellement affichée. */
public final class ControleurJeu {
    private Partie partie;
    /** Crée le contrôleur sur l'accueil, sans partie. */
    public ControleurJeu() { }
    /** {@return partie actuelle ou null sur l'accueil initial} */
    public Partie getPartie() { return partie; }
    /** Démarre une partie indépendante. */
    public void nouvellePartie() { partie = new Partie(); }
    /**
     * Publie un chargement après validation complète et confirmation de l'interface.
     * @param chargee nouvelle partie validée
     */
    public void adopter(Partie chargee) { partie = Objects.requireNonNull(chargee); }
    /** Abandonne la partie après confirmation. */
    public void abandonner() { partie = null; }
    /** {@return instantané capturé avant de lancer le travail de fichier} */
    public EtatSauvegarde capturer() { return SauvegardePartie.capturer(partie); }
    /** {@return événements de l'attaque} */
    public String attaquer() { return partie.attaquer(); }
    /** {@return événements de la défense} */
    public String defendre() { return partie.defendre(); }
    /** {@return événements de la potion} */
    public String boire() { return partie.boirePotion(); }
    /** {@return récompense du coffre} */
    public String ouvrir() { return partie.ouvrirCoffre(); }
    /** {@return effet du repos} */
    public String reposer() { return partie.seReposer(); }
    /**
     * Résout exactement un tour, à rejouer uniquement dans la présentation.
     * @param action commande du joueur
     * @return faits et instantanés indépendants
     */
    public ResultatAction resoudre(ActionCombat action) { return partie.resoudre(action); }
    /**
     * Déplace le héros selon les règles.
     * @param salle destination
     * @return événement
     */
    public String deplacer(String salle) { return partie.deplacer(salle); }
    /**
     * Équipe une arme possédée.
     * @param arme arme sélectionnée
     * @return événement
     */
    public String equiper(Arme arme) { return partie.equiper(arme); }
}
