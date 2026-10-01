package fr.utbm.sallesoubliees.vue;

import java.awt.Component;
import java.awt.Container;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import static org.junit.jupiter.api.Assertions.*;

/** Test facultatif sur un bureau graphique ; les captures restent dans target. */
@EnabledIfSystemProperty(named = "tests.interface", matches = "true")
class InterfaceTest {
    @Test void accueilCombatCompletEtCoffreAvecCaptures() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            FenetreJeu fenetre = new FenetreJeu();
            try {
                fenetre.setVisible(true); fenetre.validate();
                capturer(fenetre, "01-accueil");
                bouton(fenetre, "Nouvelle partie").doClick(); fenetre.validate();
                assertFalse(bouton(fenetre, "Attaquer").isEnabled());
                nom(fenetre, "salle-ossuaire").doClick(); fenetre.validate();
                assertTrue(bouton(fenetre, "Attaquer").isEnabled());
                assertFalse(nom(fenetre, "salle-entree").isEnabled());
                assertFalse(bouton(fenetre, "Boire une potion").isEnabled());
                bouton(fenetre, "Attaquer").doClick();
                assertTrue(bouton(fenetre, "Boire une potion").isEnabled());
                fenetre.setSize(1080, 760); fenetre.validate();
                verifierCommandesVisibles(fenetre);
                capturer(fenetre, "02-combat");
                bouton(fenetre, "Attaquer").doClick(); bouton(fenetre, "Attaquer").doClick();
                assertFalse(bouton(fenetre, "Attaquer").isEnabled());
                assertTrue(nom(fenetre, "salle-arsenal").isEnabled());
                nom(fenetre, "salle-arsenal").doClick(); bouton(fenetre, "Ouvrir le coffre").doClick();
                assertFalse(bouton(fenetre, "Ouvrir le coffre").isEnabled());
                fenetre.setSize(1080, 760); fenetre.validate();
                verifierCommandesVisibles(fenetre);
                capturer(fenetre, "03-arsenal-taille-minimale");
            } finally { fenetre.dispose(); }
        });
    }

    private static JButton bouton(Container parent, String texte) {
        return composants(parent).stream().filter(c -> c instanceof JButton b && b.isShowing() && texte.equals(b.getText()))
                .map(c -> (JButton)c).findFirst().orElseThrow(() -> new AssertionError("Bouton absent : " + texte));
    }
    private static JButton nom(Container parent, String nom) {
        return composants(parent).stream().filter(c -> c instanceof JButton && nom.equals(c.getName()))
                .map(c -> (JButton)c).findFirst().orElseThrow();
    }
    private static List<Component> composants(Container parent) {
        List<Component> resultat = new ArrayList<>();
        for (Component c : parent.getComponents()) { resultat.add(c); if (c instanceof Container enfant) resultat.addAll(composants(enfant)); }
        return resultat;
    }
    private static void capturer(FenetreJeu fenetre, String nom) {
        try {
            fenetre.validate();
            BufferedImage image = new BufferedImage(fenetre.getWidth(), fenetre.getHeight(), BufferedImage.TYPE_INT_RGB);
            var g = image.createGraphics(); fenetre.paintAll(g); g.dispose();
            Path dossier = Path.of("target", "captures"); Files.createDirectories(dossier);
            ImageIO.write(image, "png", dossier.resolve(nom + ".png").toFile());
        } catch (Exception e) { throw new AssertionError(e); }
    }

    private static void verifierCommandesVisibles(FenetreJeu fenetre) {
        for (Component c : composants(fenetre)) {
            if (c instanceof JButton b && b.isShowing()) {
                Container parent = b.getParent();
                assertTrue(b.getY() >= 0 && b.getY() + b.getHeight() <= parent.getHeight(), "Bouton coupé : " + b.getText());
                if (b.getName() != null && b.getName().startsWith("salle-")) assertTrue(b.getHeight() >= 44, "Nom de salle coupé.");
            }
        }
    }
}
