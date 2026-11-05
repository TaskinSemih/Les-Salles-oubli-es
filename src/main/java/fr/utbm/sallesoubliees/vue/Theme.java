// Place cette classe dans la vue, responsable de la présentation Swing et des dessins.
package fr.utbm.sallesoubliees.vue;

// Rend disponible Color depuis java.awt.
import java.awt.Color;
// Rend disponible Font depuis java.awt.
import java.awt.Font;
// Rend disponible BorderFactory depuis javax.swing.
import javax.swing.BorderFactory;
// Rend disponible JButton depuis javax.swing.
import javax.swing.JButton;
// Rend disponible JLabel depuis javax.swing.
import javax.swing.JLabel;
// Rend disponible JPanel depuis javax.swing.
import javax.swing.JPanel;
// Rend disponible Border depuis javax.swing.border.
import javax.swing.border.Border;

/** Palette et composants partagés pour garder une interface homogène et lisible. */
// Déclare Theme, palette, police et fabriques de composants partagées par l'interface. final interdit de créer une sous-classe.
final class Theme {
    // Initialise la couleur de fond avec new Color(23, 28, 35).
    static final Color FOND = new Color(23, 28, 35);
    // Initialise la couleur des panneaux avec new Color(34, 41, 50).
    static final Color PANNEAU = new Color(34, 41, 50);
    // Initialise la couleur du texte principal avec new Color(238, 232, 216).
    static final Color TEXTE = new Color(238, 232, 216);
    // Initialise la couleur du texte secondaire avec new Color(178, 188, 195).
    static final Color SECONDAIRE = new Color(178, 188, 195);
    // Initialise la couleur d'accent dorée avec new Color(222, 184, 105).
    static final Color OR = new Color(222, 184, 105);
    // Initialise la couleur verte des valeurs positives avec new Color(109, 181, 148).
    static final Color VERT = new Color(109, 181, 148);
    // Initialise la couleur rouge des dégâts avec new Color(215, 125, 117).
    static final Color ROUGE = new Color(215, 125, 117);
    // Initialise la police commune avec new Font(Font.SANS_SERIF, Font.PLAIN, 14).
    static final Font CORPS = new Font(Font.SANS_SERIF, Font.PLAIN, 14);

    /** Empêche la création d'un thème avec un état distinct. */
    // Empêche la création extérieure de cette classe utilitaire grâce à un constructeur privé.
    private Theme() { }
    /** Crée une bordure avec marge et titre. */
    // Crée une bordure titrée avec les marges et les couleurs communes.
    static Border bordure(String titre) {
        // Renvoie BorderFactory.createCompoundBorder(.
        return BorderFactory.createCompoundBorder(
                // Construit la bordure externe avec son contour, son titre et la police commune.
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(70, 79, 88)), titre,
                        // Applique le style gras doré du titre et ajoute une marge interne de six à huit pixels.
                        0, 0, CORPS.deriveFont(Font.BOLD), OR), BorderFactory.createEmptyBorder(6, 8, 6, 8));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Crée un panneau au fond commun. */
    // Crée un panneau avec la couleur de fond commune. Initialise le panneau p avec new JPanel(). Définit la couleur de fond ; arguments : PANNEAU. Renvoie la variable p.
    static JPanel panneau() { JPanel p = new JPanel(); p.setBackground(PANNEAU); return p; }
    /** Crée une étiquette lisible. */
    // Crée une étiquette avec la police et la couleur du thème. Initialise l’étiquette l avec new JLabel(texte). Définit la couleur du texte ; arguments : TEXTE. Définit la police de caractères ; arguments : CORPS. Renvoie l.
    static JLabel texte(String texte) { JLabel l = new JLabel(texte); l.setForeground(TEXTE); l.setFont(CORPS); return l; }
    /** Crée un bouton à taille et couleurs cohérentes. */
    // Crée ou recherche un bouton selon le contexte de cette classe.
    static JButton bouton(String texte) {
        // Initialise le bouton b avec new JButton(texte). Définit la police de caractères ; arguments : CORPS. Définit la couleur de fond ; arguments : new Color(57, 67, 79).
        JButton b = new JButton(texte); b.setFont(CORPS); b.setBackground(new Color(57, 67, 79));
        // Définit la couleur du texte ; arguments : TEXTE. Conserve un indicateur visible de focus sur le bouton ; arguments : true. Définit les marges intérieures ; arguments : new java.awt.Insets(5, 10, 5, 10).
        b.setForeground(TEXTE); b.setFocusPainted(true); b.setMargin(new java.awt.Insets(5, 10, 5, 10));
        // Renvoie b.
        return b;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
