package fr.utbm.sallesoubliees.vue;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/** Palette et composants partagés pour garder une interface homogène et lisible. */
final class Theme {
    static final Color FOND = new Color(23, 28, 35);
    static final Color PANNEAU = new Color(34, 41, 50);
    static final Color TEXTE = new Color(238, 232, 216);
    static final Color SECONDAIRE = new Color(178, 188, 195);
    static final Color OR = new Color(222, 184, 105);
    static final Color VERT = new Color(109, 181, 148);
    static final Color ROUGE = new Color(215, 125, 117);
    static final Font CORPS = new Font(Font.SANS_SERIF, Font.PLAIN, 14);

    /** Empêche la création d'un thème avec un état distinct. */
    private Theme() { }
    /** Crée une bordure avec marge et titre. */
    static Border bordure(String titre) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(70, 79, 88)), titre,
                        0, 0, CORPS.deriveFont(Font.BOLD), OR), BorderFactory.createEmptyBorder(6, 8, 6, 8));
    }
    /** Crée un panneau au fond commun. */
    static JPanel panneau() { JPanel p = new JPanel(); p.setBackground(PANNEAU); return p; }
    /** Crée une étiquette lisible. */
    static JLabel texte(String texte) { JLabel l = new JLabel(texte); l.setForeground(TEXTE); l.setFont(CORPS); return l; }
    /** Crée un bouton à taille et couleurs cohérentes. */
    static JButton bouton(String texte) {
        JButton b = new JButton(texte); b.setFont(CORPS); b.setBackground(new Color(57, 67, 79));
        b.setForeground(TEXTE); b.setFocusPainted(true); b.setMargin(new java.awt.Insets(5, 10, 5, 10));
        return b;
    }
}
