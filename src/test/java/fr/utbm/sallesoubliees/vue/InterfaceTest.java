package fr.utbm.sallesoubliees.vue;

import fr.utbm.sallesoubliees.controleur.ControleurJeu;
import fr.utbm.sallesoubliees.modele.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BooleanSupplier;
import javax.imageio.ImageIO;
import javax.swing.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import static org.junit.jupiter.api.Assertions.*;

/** Scénarios réels sur Swing ; captures de composants rendus, jamais maquettes. */
@EnabledIfSystemProperty(named = "tests.interface", matches = "true")
class InterfaceTest {
    @Test void raccourcisDeMarcheNeConsommentPasDeTour() throws Exception {
        Partie partie = new Partie();
        EtatSauvegarde avant = SauvegardePartie.capturer(partie);
        SceneJeu scene = edt(() -> {
            SceneJeu s = new SceneJeu(new GestionnaireRessources(), v -> {}, () -> {}); s.afficher(partie, null);
            Object nom = s.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(KeyStroke.getKeyStroke("pressed D"));
            s.getActionMap().get(nom).actionPerformed(null); return s;
        });
        try {
            attendre(() -> { try { return champ(scene, "x", Double.class) > 450; } catch (Exception e) { throw new AssertionError(e); } });
            edt(() -> {
                Object nom = scene.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(KeyStroke.getKeyStroke("released D"));
                scene.getActionMap().get(nom).actionPerformed(null); return null;
            });
            attendre(() -> !scene.estTimerActif());
            assertEquals(avant, SauvegardePartie.capturer(partie));
        } finally { edt(() -> { scene.arreter(); return null; }); }
    }

    @Test void dialoguesSauvegardentEtReprennentLeCombat(@org.junit.jupiter.api.io.TempDir Path dossier) throws Exception {
        FenetreJeu f = edt(FenetreJeu::new);
        try {
            edt(() -> { f.setAutoRequestFocus(false); f.setLocation(-10000, -10000); f.setVisible(true); cliquer(f, "Nouvelle partie"); return null; });
            SceneJeu scene = champ(f, "scene", SceneJeu.class);
            ControleurJeu c = champ(f, "controleur", ControleurJeu.class);
            edt(() -> {
                c.deplacer("ossuaire"); c.attaquer(); scene.afficher(c.getPartie(), null); scene.stabiliser();
                cliquer(f, "Échap · Pause"); return null;
            });
            EtatSauvegarde avant = edt(c::capturer); Path fichier = dossier.resolve("combat.save.json");
            SwingUtilities.invokeLater(() -> cliquer(f, "Sauvegarder"));
            attendre(() -> dialogue(JFileChooser.class) != null);
            edt(() -> { JFileChooser choix = dialogue(JFileChooser.class); choix.setSelectedFile(fichier.toFile()); choix.approveSelection(); return null; });
            attendre(() -> Files.exists(fichier) && !operationFichier(f));
            edt(() -> { c.attaquer(); cliquer(f, "Échap · Pause"); return null; });
            assertNotEquals(avant, edt(c::capturer));
            SwingUtilities.invokeLater(() -> cliquer(f, "Charger"));
            attendre(() -> dialogue(JFileChooser.class) != null);
            edt(() -> { JFileChooser choix = dialogue(JFileChooser.class); choix.setSelectedFile(fichier.toFile()); choix.approveSelection(); return null; });
            attendre(() -> dialogue(JOptionPane.class) != null);
            edt(() -> { dialogue(JOptionPane.class).setValue(JOptionPane.YES_OPTION); return null; });
            attendre(() -> !operationFichier(f));
            assertEquals(avant, edt(c::capturer));
            edt(() -> { scene.suspendre(false); return null; }); attendre(() -> !scene.estOccupe());
            edt(() -> {
                cliquer(f, "1 · Attaquer"); assertTrue(scene.estOccupe()); f.dispose(); assertFalse(scene.estTimerActif()); return null;
            });
        } finally { edt(() -> { for (Window w : f.getOwnedWindows()) w.dispose(); f.dispose(); return null; }); }
    }

    private static boolean operationFichier(FenetreJeu f) {
        try { return champ(f, "fichierEnCours", Boolean.class); } catch (Exception e) { throw new AssertionError(e); }
    }
    private static <T extends Component> T dialogue(Class<T> type) {
        for (Window w : Window.getWindows()) if (w instanceof JDialog && w.isShowing())
            for (Component c : composants(w)) if (type.isInstance(c)) return type.cast(c);
        return null;
    }

    @Test void marcheContourneLePilierSansBloquerLesCommandes() throws Exception {
        List<String> commandes = new ArrayList<>();
        SceneJeu scene = edt(() -> {
            Partie p = new Partie(); p.deplacer("ossuaire"); while (p.estEnCombat()) p.attaquer();
            SceneJeu s = new SceneJeu(new GestionnaireRessources(), commandes::add, () -> {});
            s.afficher(p, null);
            for (String nom : List.of("x", "y")) {
                var champ = SceneJeu.class.getDeclaredField(nom); champ.setAccessible(true);
                champ.setDouble(s, nom.equals("x") ? 698.9 : 218);
            }
            s.marcherVersPorte("arsenal"); return s;
        });
        try {
            attendre(() -> !scene.estOccupe());
            assertEquals(List.of("porte:arsenal"), edt(() -> List.copyOf(commandes)));
            assertFalse(edt(scene::estTimerActif));
        } finally { edt(() -> { scene.arreter(); return null; }); }
    }

    @Test void parcoursVisuelCompletEtCaptures() throws Exception {
        FenetreJeu f = edt(FenetreJeu::new);
        try {
            edt(() -> {
                // Le parcours par composants reste indépendant des clics de l'utilisateur sur son bureau.
                for (var ecouteur : f.getWindowFocusListeners()) f.removeWindowFocusListener(ecouteur);
                f.setAutoRequestFocus(false); f.setLocation(-10000, -10000);
                f.setSize(1280, 720); f.setVisible(true); f.validate(); capture(f, "01-accueil"); cliquer(f, "Nouvelle partie"); return null;
            });
            SceneJeu scene = champ(f, "scene", SceneJeu.class);
            ControleurJeu c = champ(f, "controleur", ControleurJeu.class);
            edt(() -> { scene.suspendre(false); capture(f, "02-exploration"); return null; });
            aller(f, scene, c, "ossuaire");
            edt(() -> { capture(f, "03-squelette"); cliquer(f, "1 · Attaquer"); cliquer(f, "1 · Attaquer"); return null; });
            assertEquals(19, edt(() -> c.getPartie().getSalleActuelle().getEnnemi().getVie()));
            edt(() -> {
                cliquer(f, "Échap · Pause"); assertFalse(bouton(f, "Sauvegarder").isEnabled()); assertFalse(scene.estTimerActif());
                cliquer(f, "Reprendre"); scene.suspendre(false); return null;
            });
            attendre(() -> !scene.estOccupe());
            assertEquals(94, edt(() -> c.getPartie().getHeros().getVie()));
            edt(() -> { scene.setReduite(true); return null; });
            vaincre(f, scene, c);
            aller(f, scene, c, "arsenal"); objet(scene); attendre(() -> c.getPartie().getSalleActuelle().estUtilisee());
            edt(() -> {
                cliquer(f, "I · Inventaire");
                List<JButton> choix = composants(f).stream().filter(v -> v instanceof JButton b && b.isShowing() && "Équiper".equals(b.getText()) && b.isEnabled()).map(v -> (JButton)v).toList();
                choix.getFirst().doClick(); capture(f, "05-inventaire"); cliquer(f, "Fermer · Échap"); return null;
            });
            assertEquals(17, edt(() -> c.getPartie().getHeros().getAttaque()));
            aller(f, scene, c, "repos"); aller(f, scene, c, "forge"); vaincre(f, scene, c);
            aller(f, scene, c, "repos"); objet(scene); attendre(() -> c.getPartie().getSalleActuelle().estUtilisee());
            assertEquals(100, edt(() -> c.getPartie().getHeros().getVie()));
            aller(f, scene, c, "bibliotheque"); vaincre(f, scene, c);
            aller(f, scene, c, "antichambre"); vaincre(f, scene, c);
            aller(f, scene, c, "tresor"); objet(scene); attendre(() -> c.getPartie().getSalleActuelle().estUtilisee());
            edt(() -> {
                cliquer(f, "I · Inventaire");
                List<JButton> choix = composants(f).stream().filter(v -> v instanceof JButton b && b.isShowing() && "Équiper".equals(b.getText()) && b.isEnabled()).map(v -> (JButton)v).toList();
                choix.getLast().doClick(); cliquer(f, "Fermer · Échap"); return null;
            });
            assertEquals(21, edt(() -> c.getPartie().getHeros().getAttaque()));
            aller(f, scene, c, "antichambre"); aller(f, scene, c, "boss");
            edt(() -> { capture(f, "04-boss"); return null; }); vaincre(f, scene, c);
            assertEquals(EtatPartie.VICTOIRE, edt(() -> c.getPartie().getEtat()));
            edt(() -> {
                capture(f, "06-victoire"); assertFalse(scene.estTimerActif());
                for (Dimension taille : List.of(new Dimension(900, 620), new Dimension(1920, 1080))) {
                    f.setSize(taille); f.validate(); capture(f, "resolution-" + taille.width + "x" + taille.height);
                    for (Component v : composants(f)) if (v instanceof JButton b && b.isShowing())
                        assertTrue(b.getX() >= 0 && b.getX() + b.getWidth() <= b.getParent().getWidth(), "Commande coupée : " + b.getText());
                }
                return null;
            });
        } finally { edt(() -> { f.dispose(); return null; }); }
    }

    @Test void defaiteEtReouverturesArretentLeTimer() throws Exception {
        var ressources = new GestionnaireRessources();
        edt(() -> {
            SceneJeu scene = new SceneJeu(ressources, s -> {}, () -> {});
            Partie p = new Partie(); p.deplacer("ossuaire"); scene.afficher(p, null); assertTrue(scene.estTimerActif());
            scene.accueil(); assertFalse(scene.estTimerActif());
            for (int i = 0; i < 5; i++) { scene.afficher(new Partie(), null); scene.accueil(); assertFalse(scene.estTimerActif()); }
            while (p.estEnCours()) p.defendre();
            scene.afficher(p, null); scene.setSize(1280, 720);
            BufferedImage image = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB); var g = image.createGraphics(); scene.paint(g); g.dispose();
            try { Files.createDirectories(Path.of("target/captures")); ImageIO.write(image, "png", Path.of("target/captures/07-defaite.png").toFile()); }
            catch (Exception e) { throw new AssertionError(e); }
            assertFalse(scene.estTimerActif()); scene.arreter(); return null;
        });
    }

    private static void aller(FenetreJeu f, SceneJeu scene, ControleurJeu c, String id) throws Exception {
        edt(() -> {
            // Le test pilote les composants sans dépendre de la fenêtre active du bureau.
            cliquer(f, "M · Carte");
            JButton b = composants(f).stream().filter(v -> v instanceof JButton && ("salle-" + id).equals(v.getName())).map(v -> (JButton)v).findFirst().orElseThrow();
            assertTrue(b.isEnabled()); b.doClick(); scene.suspendre(false); scene.marcherVersPorte(id); return null;
        });
        attendre(() -> c.getPartie().getSalleActuelle().getId().equals(id) && !scene.estOccupe());
        edt(() -> { capture(f, "salle-" + id); return null; });
    }
    private static void objet(SceneJeu scene) throws Exception {
        edt(() -> {
            scene.suspendre(false);
            double e = Math.min(scene.getWidth() / 960.0, scene.getHeight() / 540.0);
            int x = (int)((scene.getWidth() - 960 * e) / 2 + 585 * e), y = (int)((scene.getHeight() - 540 * e) / 2 + 280 * e);
            scene.dispatchEvent(new MouseEvent(scene, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, x, y, 1, false)); return null;
        });
    }
    private static void vaincre(FenetreJeu f, SceneJeu scene, ControleurJeu c) throws Exception {
        int actions = 0;
        while (edt(() -> c.getPartie().estEnCombat())) {
            assertTrue(actions++ < 100);
            edt(() -> {
                Partie p = c.getPartie(); Ennemi e = p.getSalleActuelle().getEnnemi();
                String action = p.getHeros().getVie() <= 40 && p.peutBoire() ? "3 · Potion" : e instanceof Golem && e.puissanceProchaineAttaque() > 0 ? "2 · Défendre" : "1 · Attaquer";
                cliquer(f, action); return null;
            });
            attendre(() -> !scene.estOccupe());
        }
        assertNotEquals(EtatPartie.DEFAITE, edt(() -> c.getPartie().getEtat()));
    }
    private static void attendre(BooleanSupplier condition) throws Exception {
        long fin = System.nanoTime() + 12_000_000_000L;
        while (!edt(condition::getAsBoolean)) { if (System.nanoTime() > fin) fail("La transition ou animation n'aboutit pas."); Thread.sleep(20); }
    }
    private static void cliquer(Container parent, String texte) { bouton(parent, texte).doClick(); }
    private static JButton bouton(Container parent, String texte) {
        return composants(parent).stream().filter(c -> c instanceof JButton b && b.isShowing() && texte.equals(b.getText())).map(c -> (JButton)c).findFirst().orElseThrow(() -> new AssertionError("Bouton absent : " + texte));
    }
    private static List<Component> composants(Container parent) {
        List<Component> resultat = new ArrayList<>();
        for (Component c : parent.getComponents()) { resultat.add(c); if (c instanceof Container enfant) resultat.addAll(composants(enfant)); }
        return resultat;
    }
    private static void capture(FenetreJeu f, String nom) {
        try {
            f.validate(); BufferedImage image = new BufferedImage(f.getWidth(), f.getHeight(), BufferedImage.TYPE_INT_RGB);
            var g = image.createGraphics(); f.paintAll(g); g.dispose(); Files.createDirectories(Path.of("target/captures"));
            ImageIO.write(image, "png", Path.of("target/captures/" + nom + ".png").toFile());
        } catch (Exception e) { throw new AssertionError(e); }
    }
    private static <T> T champ(Object objet, String nom, Class<T> type) throws Exception { var champ = objet.getClass().getDeclaredField(nom); champ.setAccessible(true); return type.cast(champ.get(objet)); }
    private static <T> T edt(Callable<T> action) throws Exception {
        var tache = new java.util.concurrent.FutureTask<>(action); SwingUtilities.invokeAndWait(tache); return tache.get();
    }
}
