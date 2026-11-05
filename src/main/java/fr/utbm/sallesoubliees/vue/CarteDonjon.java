// Place cette classe dans la vue, responsable de la présentation Swing et des dessins.
package fr.utbm.sallesoubliees.vue;

// Rend disponible Partie depuis fr.utbm.sallesoubliees.modele.
import fr.utbm.sallesoubliees.modele.Partie;
// Rend disponible Salle depuis fr.utbm.sallesoubliees.modele.
import fr.utbm.sallesoubliees.modele.Salle;
// Rend disponible BasicStroke depuis java.awt.
import java.awt.BasicStroke;
// Rend disponible Dimension depuis java.awt.
import java.awt.Dimension;
// Rend disponible Graphics depuis java.awt.
import java.awt.Graphics;
// Rend disponible Graphics2D depuis java.awt.
import java.awt.Graphics2D;
// Rend disponible RenderingHints depuis java.awt.
import java.awt.RenderingHints;
// Rend disponible LinkedHashMap depuis java.util.
import java.util.LinkedHashMap;
// Rend disponible Map depuis java.util.
import java.util.Map;
// Rend disponible Consumer depuis java.util.function.
import java.util.function.Consumer;
// Rend disponible BorderFactory depuis javax.swing.
import javax.swing.BorderFactory;
// Rend disponible JButton depuis javax.swing.
import javax.swing.JButton;
// Rend disponible JPanel depuis javax.swing.
import javax.swing.JPanel;

/**
 * Carte dessinée localement, avec de vrais boutons accessibles au clavier pour les salles.
 * @serial exclude
 */
// Déclare CarteDonjon, carte Swing dont les boutons respectent les déplacements autorisés par le modèle. Hérite de JPanel. final interdit de créer une sous-classe.
public final class CarteDonjon extends JPanel {
    // Initialise les boutons associés aux salles avec une nouvelle collection indépendante et vide.
    private final Map<String, JButton> boutons = new LinkedHashMap<>();
    // Déclare la partie courante de type Partie.
    private Partie partie;

    /**
     * Crée une carte dont les clics passent par le contrôleur.
     * @param deplacement commande de déplacement
     */
    // Construit un objet CarteDonjon : carte Swing dont les boutons respectent les déplacements autorisés par le modèle.
    public CarteDonjon(Consumer<String> deplacement) {
        // Choisit le gestionnaire de placement des composants ; arguments : null. Définit la couleur de fond ; arguments : Theme.FOND. Fixe la taille souhaitée pour le placement ; arguments : new Dimension(680, 290).
        setLayout(null); setBackground(Theme.FOND); setPreferredSize(new Dimension(680, 290));
        // Fixe la taille minimale de la fenêtre ou du composant ; arguments : new Dimension(500, 240).
        setMinimumSize(new Dimension(500, 240));
        // Parcourt un nouvel objet Donjon avec ).getSalles( ; chaque élément est placé dans Salle salle.
        for (Salle salle : new fr.utbm.sallesoubliees.modele.Donjon().getSalles()) {
            // Initialise le bouton bouton avec Theme.bouton(salle.getNom()).
            JButton bouton = Theme.bouton(salle.getNom());
            // Attribue un nom stable au composant pour le retrouver dans les tests ; arguments : "salle-" + salle.getId(). Définit les marges intérieures ; arguments : new java.awt.Insets(3, 2, 3, 2).
            bouton.setName("salle-" + salle.getId()); bouton.setMargin(new java.awt.Insets(3, 2, 3, 2));
            // Relie l’action de l’utilisateur au rappel indiqué ; arguments : e -> deplacement.accept(salle.getId()).
            bouton.addActionListener(e -> deplacement.accept(salle.getId()));
            // Associe une clé à sa valeur dans la table ; arguments : salle.getId(), bouton. Ajoute l’élément à la collection ou au conteneur ; arguments : bouton.
            boutons.put(salle.getId(), bouton); add(bouton);
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    /**
     * Met à jour visites, salle actuelle et déplacements autorisés.
     * @param partie partie courante
     * @param occupe opération fichier en cours
     */
    // Associe la partie reçue à la vue et rafraîchit sa présentation.
    public void afficher(Partie partie, boolean occupe) {
        // Met à jour la partie courante avec la partie courante.
        this.partie = partie;
        // Parcourt partie.getDonjon().getSalles() ; chaque élément est placé dans Salle salle.
        for (Salle salle : partie.getDonjon().getSalles()) {
            // Initialise le bouton b avec boutons.get(salle.getId()).
            JButton b = boutons.get(salle.getId());
            // Initialise « actuelle » avec salle == partie.getSalleActuelle().
            boolean actuelle = salle == partie.getSalleActuelle();
            // Initialise « statut » avec actuelle ? "VOUS ÊTES ICI" : partie.peutDeplacer(salle.getId()) ? "ACCESSIBLE" : salle.estVisitee() ? "VISITÉE" : "INEXPLORÉE".
            String statut = actuelle ? "VOUS ÊTES ICI" : partie.peutDeplacer(salle.getId()) ? "ACCESSIBLE" : salle.estVisitee() ? "VISITÉE" : "INEXPLORÉE";
            // Initialise le contenu lu avec salle.getEnnemi() != null ? (salle.estHostile() ? salle.getEnnemi().getNom() : "Ennemi vaincu") : salle.getCoffre() != null ? (salle.estUtilisee() ? "Coffre ouvert" : "Coffre") : salle.estRepos() ? (salle.estUtilisee() ? "Repos épuisé" : "Repos unique") : "Départ".
            String contenu = salle.getEnnemi() != null ? (salle.estHostile() ? salle.getEnnemi().getNom() : "Ennemi vaincu") : salle.getCoffre() != null ? (salle.estUtilisee() ? "Coffre ouvert" : "Coffre") : salle.estRepos() ? (salle.estUtilisee() ? "Repos épuisé" : "Repos unique") : "Départ";
            // Met à jour le texte du composant ; arguments : "<html><center><b>" + salle.getNom() + "</b><br><small>" + statut + "</small></center></html>".
            b.setText("<html><center><b>" + salle.getNom() + "</b><br><small>" + statut + "</small></center></html>");
            // Définit l’aide affichée au survol ; arguments : salle.getNom() + " — " + statut + " — " + contenu.
            b.setToolTipText(salle.getNom() + " — " + statut + " — " + contenu);
            // Fournit un nom explicite aux outils d’accessibilité ; arguments : salle.getNom() + " : " + statut.
            b.getAccessibleContext().setAccessibleName(salle.getNom() + " : " + statut);
            // Active ou désactive le composant selon la condition fournie ; arguments : !occupe && partie.peutDeplacer(salle.getId()).
            b.setEnabled(!occupe && partie.peutDeplacer(salle.getId()));
            // Définit la couleur de fond ; arguments : actuelle ? new java.awt.Color(90, 76, 49) : salle.estVisitee() ? new java.awt.Color(43, 65, 61) : Theme.PANNEAU.
            b.setBackground(actuelle ? new java.awt.Color(90, 76, 49) : salle.estVisitee() ? new java.awt.Color(43, 65, 61) : Theme.PANNEAU);
            // Définit la bordure et les marges ; arguments : BorderFactory.createLineBorder(actuelle ? Theme.OR : partie.peutDeplacer(salle.getId()) ? Theme.VERT : new java.awt.Color(75, 84, 94), actuelle ? 3 : 1).
            b.setBorder(BorderFactory.createLineBorder(actuelle ? Theme.OR : partie.peutDeplacer(salle.getId()) ? Theme.VERT : new java.awt.Color(75, 84, 94), actuelle ? 3 : 1));
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Demande à Swing de recalculer le placement des composants. Demande à Swing un nouveau dessin.
        revalidate(); repaint();
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    /** Place les salles sur cinq colonnes et trois lignes en respectant le redimensionnement. */
    // Replace les composants en fonction de la taille disponible.
    @Override public void doLayout() {
        // Si la partie courante est égal à l’absence de valeur (null), Quitte la méthode sans poursuivre.
        if (partie == null) return;
        // Initialise « cellule x » avec getWidth() / 5, celluleY = getHeight() / 3.
        int celluleX = getWidth() / 5, celluleY = getHeight() / 3;
        // Parcourt partie.getDonjon().getSalles() ; chaque élément est placé dans Salle salle.
        for (Salle salle : partie.getDonjon().getSalles()) {
            // Place le bouton horizontalement selon la colonne de la salle, avec une marge de sept pixels.
            boutons.get(salle.getId()).setBounds(salle.getColonne() * celluleX + 7,
                    // Complète son placement vertical et sa taille avec des marges dans la cellule de grille.
                    salle.getLigne() * celluleY + 6, celluleX - 14, celluleY - 12);
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    /** Dessine uniquement les passages réellement présents dans le modèle. */
    // Redessine le composant à partir de son état courant, sans modifier les règles.
    @Override protected void paintComponent(Graphics graphics) {
        // Redessine d’abord le fond du composant parent ; arguments : graphics.
        super.paintComponent(graphics);
        // Si la partie courante est égal à l’absence de valeur (null), Quitte la méthode sans poursuivre.
        if (partie == null) return;
        // Initialise la variable g de type Graphics2D avec (Graphics2D) graphics.create().
        Graphics2D g = (Graphics2D) graphics.create();
        // Configure l’interpolation ou le lissage du rendu ; arguments : RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON.
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Choisit la couleur du prochain dessin ; arguments : new java.awt.Color(99, 109, 118). Choisit l’épaisseur des contours ; arguments : new BasicStroke(3).
        g.setColor(new java.awt.Color(99, 109, 118)); g.setStroke(new BasicStroke(3));
        // Parcourt partie.getDonjon().getSalles() ; chaque élément est placé dans Salle salle.
        for (Salle salle : partie.getDonjon().getSalles()) {
            // Initialise le bouton depart avec boutons.get(salle.getId()).
            JButton depart = boutons.get(salle.getId());
            // Parcourt les passages autorisés de la salle courante ; chaque élément est placé dans String id.
            for (String id : salle.getPassages()) {
                // Si salle.getId().compareTo(id) est inférieur à 0, entre dans ce traitement conditionnel.
                if (salle.getId().compareTo(id) < 0) {
                    // Initialise le bouton arrivee avec boutons.get(id).
                    JButton arrivee = boutons.get(id);
                    // Commence le segment de passage au centre du bouton de la salle de départ.
                    g.drawLine(depart.getX() + depart.getWidth() / 2, depart.getY() + depart.getHeight() / 2,
                            // Termine le segment au centre du bouton voisin, pour relier les deux salles sur la carte.
                            arrivee.getX() + arrivee.getWidth() / 2, arrivee.getY() + arrivee.getHeight() / 2);
                // Ferme le bloc ou la liste de données commencé plus haut.
                }
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Libère le contexte graphique ou la fenêtre utilisé.
        g.dispose();
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
