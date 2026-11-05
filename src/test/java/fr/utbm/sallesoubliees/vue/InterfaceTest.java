// Place cette classe dans la vue, responsable de la présentation Swing et des dessins.
package fr.utbm.sallesoubliees.vue;

// Rend disponible ControleurJeu depuis fr.utbm.sallesoubliees.controleur.
import fr.utbm.sallesoubliees.controleur.ControleurJeu;
// Rend disponibles les types ou constantes de fr.utbm.sallesoubliees.modele sans répéter leur nom complet.
import fr.utbm.sallesoubliees.modele.*;
// Rend disponibles les types ou constantes de java.awt sans répéter leur nom complet.
import java.awt.*;
// Rend disponible MouseEvent depuis java.awt.event.
import java.awt.event.MouseEvent;
// Rend disponible BufferedImage depuis java.awt.image.
import java.awt.image.BufferedImage;
// Rend disponible Files depuis java.nio.file.
import java.nio.file.Files;
// Rend disponible Path depuis java.nio.file.
import java.nio.file.Path;
// Rend disponible ArrayList depuis java.util.
import java.util.ArrayList;
// Rend disponible List depuis java.util.
import java.util.List;
// Rend disponible Callable depuis java.util.concurrent.
import java.util.concurrent.Callable;
// Rend disponible BooleanSupplier depuis java.util.function.
import java.util.function.BooleanSupplier;
// Rend disponible ImageIO depuis javax.imageio.
import javax.imageio.ImageIO;
// Rend disponibles les types ou constantes de javax.swing sans répéter leur nom complet.
import javax.swing.*;
// Importe l’annotation JUnit qui identifie une méthode de test.
import org.junit.jupiter.api.Test;
// Rend disponible EnabledIfSystemProperty depuis org.junit.jupiter.api.condition.
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
// Importe les assertions JUnit pour comparer les résultats et vérifier les erreurs attendues.
import static org.junit.jupiter.api.Assertions.*;

/** Scénarios réels sur Swing ; captures de composants rendus, jamais maquettes. */
// N’active ces tests Swing que si la propriété tests.interface vaut true.
@EnabledIfSystemProperty(named = "tests.interface", matches = "true")
// Déclare InterfaceTest, scénarios pilotant les vrais composants Swing lorsque les tests d'interface sont activés.
class InterfaceTest {
    // Teste le scénario suivant : raccourcis de marche ne consomment pas de tour.
    @Test void raccourcisDeMarcheNeConsommentPasDeTour() throws Exception {
        // Initialise la partie courante avec un nouvel objet Partie.
        Partie partie = new Partie();
        // Initialise l'instantané avant l'action avec SauvegardePartie.capturer(partie).
        EtatSauvegarde avant = SauvegardePartie.capturer(partie);
        // Initialise la scène du jeu avec edt(() -> {.
        SceneJeu scene = edt(() -> {
            // Initialise « s » avec un nouvel objet SceneJeu avec new GestionnaireRessources(), v -> {}, () -> {}. Met à jour la vue pour la partie reçue ; arguments : partie, null.
            SceneJeu s = new SceneJeu(new GestionnaireRessources(), v -> {}, () -> {}); s.afficher(partie, null);
            // Initialise le nom avec s.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(KeyStroke.getKeyStroke("pressed D")).
            Object nom = s.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(KeyStroke.getKeyStroke("pressed D"));
            // Déclenche le rappel Swing associé à cette touche dans le test ; arguments : null. Renvoie s.
            s.getActionMap().get(nom).actionPerformed(null); return s;
        // Ferme le bloc et termine l’instruction ou l’appel englobant.
        });
        // Commence l’opération dont les erreurs sont prises en charge par les blocs catch suivants.
        try {
            // Attend une condition Swing avec un délai limite pour détecter une transition bloquée. Paramètres : () -> { try { return champ(scene, "x", Double.class) > 450; } catch (Exception e) { throw new AssertionError(e); } }.
            attendre(() -> { try { return champ(scene, "x", Double.class) > 450; } catch (Exception e) { throw new AssertionError(e); } });
            // Prépare le rappel qui sera exécuté pour l’événement ou l’élément trouvé.
            edt(() -> {
                // Initialise le nom avec scene.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(KeyStroke.getKeyStroke("released D")).
                Object nom = scene.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).get(KeyStroke.getKeyStroke("released D"));
                // Déclenche le rappel Swing associé à cette touche dans le test ; arguments : null. Renvoie l’absence de valeur (null).
                scene.getActionMap().get(nom).actionPerformed(null); return null;
            // Ferme le bloc et termine l’instruction ou l’appel englobant.
            });
            // Attend une condition Swing avec un délai limite pour détecter une transition bloquée. Paramètres : () -> !scene.estTimerActif().
            attendre(() -> !scene.estTimerActif());
            // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : l'instantané avant l'action ; SauvegardePartie.capturer(partie).
            assertEquals(avant, SauvegardePartie.capturer(partie));
        // Exécute ce nettoyage dans tous les cas, même après une erreur ou un retour anticipé.
        } finally { edt(() -> { scene.arreter(); return null; }); }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : dialogues sauvegardent et reprennent le combat.
    @Test void dialoguesSauvegardentEtReprennentLeCombat(@org.junit.jupiter.api.io.TempDir Path dossier) throws Exception {
        // Initialise la variable f de type FenetreJeu avec edt(FenetreJeu::new).
        FenetreJeu f = edt(FenetreJeu::new);
        // Commence l’opération dont les erreurs sont prises en charge par les blocs catch suivants.
        try {
            // Exécute le travail sur le fil Swing et attend son résultat à l’aide d’une FutureTask. Paramètres : () -> { f.setAutoRequestFocus(false); f.setLocation(-10000, -10000); f.setVisible(true); cliquer(f, "Nouvelle partie"); return null; }.
            edt(() -> { f.setAutoRequestFocus(false); f.setLocation(-10000, -10000); f.setVisible(true); cliquer(f, "Nouvelle partie"); return null; });
            // Initialise la scène du jeu avec champ(f, "scene", SceneJeu.class).
            SceneJeu scene = champ(f, "scene", SceneJeu.class);
            // Initialise la variable c de type ControleurJeu avec champ(f, "controleur", ControleurJeu.class).
            ControleurJeu c = champ(f, "controleur", ControleurJeu.class);
            // Prépare le rappel qui sera exécuté pour l’événement ou l’élément trouvé.
            edt(() -> {
                // Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire". Joue une attaque du héros et l’éventuelle réponse ennemie. Met à jour la vue pour la partie reçue ; arguments : c.getPartie(), null. Termine immédiatement la présentation visuelle.
                c.deplacer("ossuaire"); c.attaquer(); scene.afficher(c.getPartie(), null); scene.stabiliser();
                // Déclenche le bouton identifié par son texte ; arguments : f, "Échap · Pause". Renvoie l’absence de valeur (null).
                cliquer(f, "Échap · Pause"); return null;
            // Ferme le bloc et termine l’instruction ou l’appel englobant.
            });
            // Initialise l'instantané avant l'action avec edt(c::capturer). Initialise le chemin du fichier avec dossier.resolve("combat.save.json").
            EtatSauvegarde avant = edt(c::capturer); Path fichier = dossier.resolve("combat.save.json");
            // Programme le rappel sur le fil événementiel Swing sans bloquer le fil courant.
            SwingUtilities.invokeLater(() -> cliquer(f, "Sauvegarder"));
            // Attend une condition Swing avec un délai limite pour détecter une transition bloquée. Paramètres : () -> dialogue(JFileChooser.class) != null.
            attendre(() -> dialogue(JFileChooser.class) != null);
            // Exécute le travail sur le fil Swing et attend son résultat à l’aide d’une FutureTask. Paramètres : () -> { JFileChooser choix = dialogue(JFileChooser.class); choix.setSelectedFile(fichier.toFile()); choix.approveSelection(); return null; }.
            edt(() -> { JFileChooser choix = dialogue(JFileChooser.class); choix.setSelectedFile(fichier.toFile()); choix.approveSelection(); return null; });
            // Attend une condition Swing avec un délai limite pour détecter une transition bloquée. Paramètres : () -> Files.exists(fichier) && !operationFichier(f).
            attendre(() -> Files.exists(fichier) && !operationFichier(f));
            // Exécute le travail sur le fil Swing et attend son résultat à l’aide d’une FutureTask. Paramètres : () -> { c.attaquer(); cliquer(f, "Échap · Pause"); return null; }.
            edt(() -> { c.attaquer(); cliquer(f, "Échap · Pause"); return null; });
            // Vérifie que les deux résultats sont différents : l'instantané avant l'action ; edt(c::capturer).
            assertNotEquals(avant, edt(c::capturer));
            // Programme le rappel sur le fil événementiel Swing sans bloquer le fil courant.
            SwingUtilities.invokeLater(() -> cliquer(f, "Charger"));
            // Attend une condition Swing avec un délai limite pour détecter une transition bloquée. Paramètres : () -> dialogue(JFileChooser.class) != null.
            attendre(() -> dialogue(JFileChooser.class) != null);
            // Exécute le travail sur le fil Swing et attend son résultat à l’aide d’une FutureTask. Paramètres : () -> { JFileChooser choix = dialogue(JFileChooser.class); choix.setSelectedFile(fichier.toFile()); choix.approveSelection(); return null; }.
            edt(() -> { JFileChooser choix = dialogue(JFileChooser.class); choix.setSelectedFile(fichier.toFile()); choix.approveSelection(); return null; });
            // Attend une condition Swing avec un délai limite pour détecter une transition bloquée. Paramètres : () -> dialogue(JOptionPane.class) != null.
            attendre(() -> dialogue(JOptionPane.class) != null);
            // Exécute le travail sur le fil Swing et attend son résultat à l’aide d’une FutureTask. Paramètres : () -> { dialogue(JOptionPane.class).setValue(JOptionPane.YES_OPTION); return null; }.
            edt(() -> { dialogue(JOptionPane.class).setValue(JOptionPane.YES_OPTION); return null; });
            // Attend une condition Swing avec un délai limite pour détecter une transition bloquée. Paramètres : () -> !operationFichier(f).
            attendre(() -> !operationFichier(f));
            // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : l'instantané avant l'action ; edt(c::capturer).
            assertEquals(avant, edt(c::capturer));
            // Exécute le travail sur le fil Swing et attend son résultat à l’aide d’une FutureTask. Paramètres : () -> { scene.suspendre(false); return null; }. Attend une condition Swing avec un délai limite pour détecter une transition bloquée. Paramètres : () -> !scene.estOccupe().
            edt(() -> { scene.suspendre(false); return null; }); attendre(() -> !scene.estOccupe());
            // Prépare le rappel qui sera exécuté pour l’événement ou l’élément trouvé.
            edt(() -> {
                // Déclenche le bouton identifié par son texte ; arguments : f, "1 · Attaquer". Vérifie que la condition est vraie : si une présentation, animation ou marche automatique bloque la scène de la scène du jeu. Libère le contexte graphique ou la fenêtre utilisé. Vérifie que la condition est fausse : scene.estTimerActif(). Renvoie l’absence de valeur (null).
                cliquer(f, "1 · Attaquer"); assertTrue(scene.estOccupe()); f.dispose(); assertFalse(scene.estTimerActif()); return null;
            // Ferme le bloc et termine l’instruction ou l’appel englobant.
            });
        // Exécute ce nettoyage dans tous les cas, même après une erreur ou un retour anticipé.
        } finally { edt(() -> { for (Window w : f.getOwnedWindows()) w.dispose(); f.dispose(); return null; }); }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Lit le verrou interne de fichier pour synchroniser le test Swing.
    private static boolean operationFichier(FenetreJeu f) {
        // Exécute ce travail sous protection d’un traitement d’erreur : Renvoie champ(f, "fichierEnCours", Boolean.class).
        try { return champ(f, "fichierEnCours", Boolean.class); } catch (Exception e) { throw new AssertionError(e); }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    // Cherche un composant du type demandé dans les dialogues Swing affichés.
    private static <T extends Component> T dialogue(Class<T> type) {
        // Parcourt Window.getWindows() ; chaque élément est placé dans Window w. Si (w instanceof JDialog et si le composant est visible à l’écran de « w »), entre dans ce traitement conditionnel.
        for (Window w : Window.getWindows()) if (w instanceof JDialog && w.isShowing())
            // Parcourt composants(w) ; chaque élément est placé dans Component c. Si type.isInstance(c), renvoie type.cast(c).
            for (Component c : composants(w)) if (type.isInstance(c)) return type.cast(c);
        // Renvoie l’absence de valeur (null).
        return null;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : marche contourne le pilier sans bloquer les commandes.
    @Test void marcheContourneLePilierSansBloquerLesCommandes() throws Exception {
        // Initialise « commandes » avec une nouvelle collection indépendante et vide.
        List<String> commandes = new ArrayList<>();
        // Initialise la scène du jeu avec edt(() -> {.
        SceneJeu scene = edt(() -> {
            // Initialise la variable p de type Partie avec un nouvel objet Partie. Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire". Répète ce bloc tant que p.estEnCombat()) p.attaquer(.
            Partie p = new Partie(); p.deplacer("ossuaire"); while (p.estEnCombat()) p.attaquer();
            // Initialise « s » avec un nouvel objet SceneJeu avec new GestionnaireRessources(), commandes::add, () -> {}.
            SceneJeu s = new SceneJeu(new GestionnaireRessources(), commandes::add, () -> {});
            // Met à jour la vue pour la partie reçue ; arguments : p, null.
            s.afficher(p, null);
            // Parcourt la liste non modifiable contenant le texte "x", le texte "y" ; chaque élément est placé dans String nom.
            for (String nom : List.of("x", "y")) {
                // Initialise « champ » avec SceneJeu.class.getDeclaredField(nom). Autorise la réflexion à accéder au champ privé pour le test ; arguments : true.
                var champ = SceneJeu.class.getDeclaredField(nom); champ.setAccessible(true);
                // Fixe la coordonnée privée de scène pour préparer le scénario de collision du test ; arguments : s, nom.equals("x") ? 698.9 : 218.
                champ.setDouble(s, nom.equals("x") ? 698.9 : 218);
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
            // Lance le trajet visuel vers la porte demandée ; arguments : "arsenal". Renvoie s.
            s.marcherVersPorte("arsenal"); return s;
        // Ferme le bloc et termine l’instruction ou l’appel englobant.
        });
        // Commence l’opération dont les erreurs sont prises en charge par les blocs catch suivants.
        try {
            // Attend une condition Swing avec un délai limite pour détecter une transition bloquée. Paramètres : () -> !scene.estOccupe().
            attendre(() -> !scene.estOccupe());
            // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : la liste non modifiable contenant le texte "porte:arsenal" ; edt(() -> List.copyOf(commandes)).
            assertEquals(List.of("porte:arsenal"), edt(() -> List.copyOf(commandes)));
            // Vérifie que la condition est fausse : edt(scene::estTimerActif).
            assertFalse(edt(scene::estTimerActif));
        // Exécute ce nettoyage dans tous les cas, même après une erreur ou un retour anticipé.
        } finally { edt(() -> { scene.arreter(); return null; }); }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : parcours visuel complet et captures.
    @Test void parcoursVisuelCompletEtCaptures() throws Exception {
        // Initialise la variable f de type FenetreJeu avec edt(FenetreJeu::new).
        FenetreJeu f = edt(FenetreJeu::new);
        // Commence l’opération dont les erreurs sont prises en charge par les blocs catch suivants.
        try {
            // Prépare le rappel qui sera exécuté pour l’événement ou l’élément trouvé.
            edt(() -> {
                // Le parcours par composants reste indépendant des clics de l'utilisateur sur son bureau.
                // Parcourt f.getWindowFocusListeners() ; chaque élément est placé dans var ecouteur. Retire cet écouteur de focus pour isoler le scénario de test des clics extérieurs à la fenêtre ; arguments : ecouteur.
                for (var ecouteur : f.getWindowFocusListeners()) f.removeWindowFocusListener(ecouteur);
                // Configure la demande automatique de focus de la fenêtre de test ; arguments : false. Place la fenêtre, ici hors écran dans les tests ; arguments : -10000, -10000.
                f.setAutoRequestFocus(false); f.setLocation(-10000, -10000);
                // Fixe les dimensions du composant ; arguments : 1280, 720. Modifie la visibilité du composant ; arguments : true. Calcule le placement des composants avant leur inspection ou leur capture. Enregistre une capture de la vraie fenêtre sous target ; arguments : f, "01-accueil". Déclenche le bouton identifié par son texte ; arguments : f, "Nouvelle partie". Renvoie l’absence de valeur (null).
                f.setSize(1280, 720); f.setVisible(true); f.validate(); capture(f, "01-accueil"); cliquer(f, "Nouvelle partie"); return null;
            // Ferme le bloc et termine l’instruction ou l’appel englobant.
            });
            // Initialise la scène du jeu avec champ(f, "scene", SceneJeu.class).
            SceneJeu scene = champ(f, "scene", SceneJeu.class);
            // Initialise la variable c de type ControleurJeu avec champ(f, "controleur", ControleurJeu.class).
            ControleurJeu c = champ(f, "controleur", ControleurJeu.class);
            // Exécute le travail sur le fil Swing et attend son résultat à l’aide d’une FutureTask. Paramètres : () -> { c.adopter(new Partie(new HasardReproductible(3))); scene.afficher(c.getPartie(), null); scene.suspendre(false); capture(f, "02-exploration"); return null; }.
            edt(() -> { c.adopter(new Partie(new HasardReproductible(3))); scene.afficher(c.getPartie(), null); scene.suspendre(false); capture(f, "02-exploration"); return null; });
            // Pilote un déplacement réel par la carte et la marche ; arguments : f, scene, c, "ossuaire".
            aller(f, scene, c, "ossuaire");
            // Exécute le travail sur le fil Swing et attend son résultat à l’aide d’une FutureTask. Paramètres : () -> { capture(f, "03-squelette"); cliquer(f, "1 · Attaquer"); cliquer(f, "1 · Attaquer"); return null; }.
            edt(() -> { capture(f, "03-squelette"); cliquer(f, "1 · Attaquer"); cliquer(f, "1 · Attaquer"); return null; });
            // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 19 ; edt(() -> c.getPartie().getSalleActuelle().getEnnemi().getVie()).
            assertEquals(19, edt(() -> c.getPartie().getSalleActuelle().getEnnemi().getVie()));
            // Prépare le rappel qui sera exécuté pour l’événement ou l’élément trouvé.
            edt(() -> {
                // Déclenche le bouton identifié par son texte ; arguments : f, "Échap · Pause". Vérifie que la condition est fausse : bouton(f, "Sauvegarder").isEnabled(). Vérifie que la condition est fausse : scene.estTimerActif().
                cliquer(f, "Échap · Pause"); assertFalse(bouton(f, "Sauvegarder").isEnabled()); assertFalse(scene.estTimerActif());
                // Déclenche le bouton identifié par son texte ; arguments : f, "Reprendre". Suspend ou reprend la présentation visuelle ; arguments : false. Renvoie l’absence de valeur (null).
                cliquer(f, "Reprendre"); scene.suspendre(false); return null;
            // Ferme le bloc et termine l’instruction ou l’appel englobant.
            });
            // Attend une condition Swing avec un délai limite pour détecter une transition bloquée. Paramètres : () -> !scene.estOccupe().
            attendre(() -> !scene.estOccupe());
            // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 94 ; edt(() -> c.getPartie().getHeros().getVie()).
            assertEquals(94, edt(() -> c.getPartie().getHeros().getVie()));
            // Exécute le travail sur le fil Swing et attend son résultat à l’aide d’une FutureTask. Paramètres : () -> { scene.setReduite(true); return null; }.
            edt(() -> { scene.setReduite(true); return null; });
            // Pilote les commandes Swing jusqu’à la fin du combat ; arguments : f, scene, c.
            vaincre(f, scene, c);
            // Pilote un déplacement réel par la carte et la marche ; arguments : f, scene, c, "arsenal". Dessine un objet à partir de l’atlas en mémoire ; arguments : scene. Attend une condition Swing avec un délai limite pour détecter une transition bloquée. Paramètres : () -> c.getPartie().getSalleActuelle().estUtilisee().
            aller(f, scene, c, "arsenal"); objet(scene); attendre(() -> c.getPartie().getSalleActuelle().estUtilisee());
            // Prépare le rappel qui sera exécuté pour l’événement ou l’élément trouvé.
            edt(() -> {
                // Déclenche le bouton identifié par son texte ; arguments : f, "F · Découverte". Enregistre une capture de la vraie fenêtre sous target ; arguments : f, "tactique-evenement".
                cliquer(f, "F · Découverte"); capture(f, "tactique-evenement");
                // Déclenche le bouton identifié par son texte ; arguments : f, "Prudent : +1 potion".
                cliquer(f, "Prudent : +1 potion");
                // Vérifie que la condition est fausse : bouton(f, "F · Découverte").isEnabled(). Suspend ou reprend la présentation visuelle ; arguments : false. Renvoie l’absence de valeur (null).
                assertFalse(bouton(f, "F · Découverte").isEnabled()); scene.suspendre(false); return null;
            // Ferme le bloc et termine l’instruction ou l’appel englobant.
            });
            // Prépare le rappel qui sera exécuté pour l’événement ou l’élément trouvé.
            edt(() -> {
                // Déclenche le bouton identifié par son texte ; arguments : f, "I · Inventaire".
                cliquer(f, "I · Inventaire");
                // Initialise le choix courant avec composants(f).stream().filter(v -> v instanceof JButton b && b.isShowing() && "Équiper".equals(b.getText()) && b.isEnabled()).map(v -> (JButton)v).toList().
                List<JButton> choix = composants(f).stream().filter(v -> v instanceof JButton b && b.isShowing() && "Équiper".equals(b.getText()) && b.isEnabled()).map(v -> (JButton)v).toList();
                // Déclenche programmatiquement le bouton Swing. Enregistre une capture de la vraie fenêtre sous target ; arguments : f, "05-inventaire". Déclenche le bouton identifié par son texte ; arguments : f, "Fermer · Échap". Renvoie l’absence de valeur (null).
                choix.getFirst().doClick(); capture(f, "05-inventaire"); cliquer(f, "Fermer · Échap"); return null;
            // Ferme le bloc et termine l’instruction ou l’appel englobant.
            });
            // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 17 ; edt(() -> c.getPartie().getHeros().getAttaque()).
            assertEquals(17, edt(() -> c.getPartie().getHeros().getAttaque()));
            // Pilote un déplacement réel par la carte et la marche ; arguments : f, scene, c, "repos". Pilote un déplacement réel par la carte et la marche ; arguments : f, scene, c, "forge". Pilote les commandes Swing jusqu’à la fin du combat ; arguments : f, scene, c.
            aller(f, scene, c, "repos"); aller(f, scene, c, "forge"); vaincre(f, scene, c);
            // Pilote un déplacement réel par la carte et la marche ; arguments : f, scene, c, "repos". Dessine un objet à partir de l’atlas en mémoire ; arguments : scene. Attend une condition Swing avec un délai limite pour détecter une transition bloquée. Paramètres : () -> c.getPartie().getSalleActuelle().estUtilisee().
            aller(f, scene, c, "repos"); objet(scene); attendre(() -> c.getPartie().getSalleActuelle().estUtilisee());
            // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 100 ; edt(() -> c.getPartie().getHeros().getVie()).
            assertEquals(100, edt(() -> c.getPartie().getHeros().getVie()));
            // Pilote un déplacement réel par la carte et la marche ; arguments : f, scene, c, "bibliotheque". Pilote les commandes Swing jusqu’à la fin du combat ; arguments : f, scene, c.
            aller(f, scene, c, "bibliotheque"); vaincre(f, scene, c);
            // Pilote un déplacement réel par la carte et la marche ; arguments : f, scene, c, "antichambre". Pilote les commandes Swing jusqu’à la fin du combat ; arguments : f, scene, c.
            aller(f, scene, c, "antichambre"); vaincre(f, scene, c);
            // Pilote un déplacement réel par la carte et la marche ; arguments : f, scene, c, "tresor". Dessine un objet à partir de l’atlas en mémoire ; arguments : scene. Attend une condition Swing avec un délai limite pour détecter une transition bloquée. Paramètres : () -> c.getPartie().getSalleActuelle().estUtilisee().
            aller(f, scene, c, "tresor"); objet(scene); attendre(() -> c.getPartie().getSalleActuelle().estUtilisee());
            // Prépare le rappel qui sera exécuté pour l’événement ou l’élément trouvé.
            edt(() -> {
                // Déclenche le bouton identifié par son texte ; arguments : f, "I · Inventaire".
                cliquer(f, "I · Inventaire");
                // Initialise le choix courant avec composants(f).stream().filter(v -> v instanceof JButton b && b.isShowing() && "Équiper".equals(b.getText()) && b.isEnabled()).map(v -> (JButton)v).toList().
                List<JButton> choix = composants(f).stream().filter(v -> v instanceof JButton b && b.isShowing() && "Équiper".equals(b.getText()) && b.isEnabled()).map(v -> (JButton)v).toList();
                // Déclenche programmatiquement le bouton Swing. Déclenche le bouton identifié par son texte ; arguments : f, "Fermer · Échap". Renvoie l’absence de valeur (null).
                choix.getLast().doClick(); cliquer(f, "Fermer · Échap"); return null;
            // Ferme le bloc et termine l’instruction ou l’appel englobant.
            });
            // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 21 ; edt(() -> c.getPartie().getHeros().getAttaque()).
            assertEquals(21, edt(() -> c.getPartie().getHeros().getAttaque()));
            // Pilote un déplacement réel par la carte et la marche ; arguments : f, scene, c, "antichambre". Pilote un déplacement réel par la carte et la marche ; arguments : f, scene, c, "boss".
            aller(f, scene, c, "antichambre"); aller(f, scene, c, "boss");
            // Exécute le travail sur le fil Swing et attend son résultat à l’aide d’une FutureTask. Paramètres : () -> { capture(f, "04-boss"); return null; }. Pilote les commandes Swing jusqu’à la fin du combat ; arguments : f, scene, c.
            edt(() -> { capture(f, "04-boss"); return null; }); vaincre(f, scene, c);
            // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : EtatPartie.VICTOIRE ; edt(() -> c.getPartie().getEtat()).
            assertEquals(EtatPartie.VICTOIRE, edt(() -> c.getPartie().getEtat()));
            // Prépare le rappel qui sera exécuté pour l’événement ou l’élément trouvé.
            edt(() -> {
                // Enregistre une capture de la vraie fenêtre sous target ; arguments : f, "06-victoire". Vérifie que la condition est fausse : scene.estTimerActif().
                capture(f, "06-victoire"); assertFalse(scene.estTimerActif());
                // Parcourt la liste non modifiable contenant new Dimension(900, 620), new Dimension(1920, 1080) ; chaque élément est placé dans Dimension taille.
                for (Dimension taille : List.of(new Dimension(900, 620), new Dimension(1920, 1080))) {
                    // Fixe les dimensions du composant ; arguments : taille. Calcule le placement des composants avant leur inspection ou leur capture. Enregistre une capture de la vraie fenêtre sous target ; arguments : f, "resolution-" + taille.width + "x" + taille.height.
                    f.setSize(taille); f.validate(); capture(f, "resolution-" + taille.width + "x" + taille.height);
                    // Parcourt composants(f) ; chaque élément est placé dans Component v. Si (v instanceof JButton b et si le composant est visible à l’écran de « b »), entre dans ce traitement conditionnel.
                    for (Component v : composants(f)) if (v instanceof JButton b && b.isShowing())
                        // Vérifie que la condition est vraie : b.getX() >= 0 && b.getX() + b.getWidth() <= b.getParent().getWidth(), "Commande coupée : " + b.getText().
                        assertTrue(b.getX() >= 0 && b.getX() + b.getWidth() <= b.getParent().getWidth(), "Commande coupée : " + b.getText());
                // Ferme le bloc ou la liste de données commencé plus haut.
                }
                // Renvoie l’absence de valeur (null).
                return null;
            // Ferme le bloc et termine l’instruction ou l’appel englobant.
            });
        // Exécute ce nettoyage dans tous les cas, même après une erreur ou un retour anticipé.
        } finally { edt(() -> { f.dispose(); return null; }); }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : defaite et reouvertures arretent le timer.
    @Test void defaiteEtReouverturesArretentLeTimer() throws Exception {
        // Initialise le cache d'images avec un nouvel objet GestionnaireRessources.
        var ressources = new GestionnaireRessources();
        // Prépare le rappel qui sera exécuté pour l’événement ou l’élément trouvé.
        edt(() -> {
            // Initialise la scène du jeu avec un nouvel objet SceneJeu avec ressources, s -> {}, () -> {}.
            SceneJeu scene = new SceneJeu(ressources, s -> {}, () -> {});
            // Initialise la variable p de type Partie avec un nouvel objet Partie. Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire". Met à jour la vue pour la partie reçue ; arguments : p, null. Vérifie que la condition est vraie : scene.estTimerActif().
            Partie p = new Partie(); p.deplacer("ossuaire"); scene.afficher(p, null); assertTrue(scene.estTimerActif());
            // Revient au décor initial et retire toute partie de la scène. Vérifie que la condition est fausse : scene.estTimerActif().
            scene.accueil(); assertFalse(scene.estTimerActif());
            // Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int i = 0; i < 5; i++. Met à jour la vue pour la partie reçue ; arguments : new Partie(), null. Revient au décor initial et retire toute partie de la scène. Vérifie que la condition est fausse : scene.estTimerActif().
            for (int i = 0; i < 5; i++) { scene.afficher(new Partie(), null); scene.accueil(); assertFalse(scene.estTimerActif()); }
            // Répète ce bloc tant que p.estEnCours()) p.defendre(.
            while (p.estEnCours()) p.defendre();
            // Met à jour la vue pour la partie reçue ; arguments : p, null. Fixe les dimensions du composant ; arguments : 1280, 720.
            scene.afficher(p, null); scene.setSize(1280, 720);
            // Initialise « image » avec new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB). Initialise la variable g de type var avec image.createGraphics(). Dessine le composant dans le contexte graphique de la capture ; arguments : g. Libère le contexte graphique ou la fenêtre utilisé.
            BufferedImage image = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB); var g = image.createGraphics(); scene.paint(g); g.dispose();
            // Exécute ce travail sous protection d’un traitement d’erreur : Crée les dossiers parents manquants ; arguments : Path.of("target/captures"). Enregistre l’image de capture au format PNG dans le fichier indiqué ; arguments : image, "png", Path.of("target/captures/07-defaite.png").toFile().
            try { Files.createDirectories(Path.of("target/captures")); ImageIO.write(image, "png", Path.of("target/captures/07-defaite.png").toFile()); }
            // Intercepte Exception e pour traiter l’échec. Signale l’erreur par une exception : new AssertionError(e).
            catch (Exception e) { throw new AssertionError(e); }
            // Vérifie que la condition est fausse : scene.estTimerActif(). Arrête les travaux visuels et leur horloge. Renvoie l’absence de valeur (null).
            assertFalse(scene.estTimerActif()); scene.arreter(); return null;
        // Ferme le bloc et termine l’instruction ou l’appel englobant.
        });
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Pilote la carte puis attend que la marche et l’entrée dans la salle soient terminées.
    private static void aller(FenetreJeu f, SceneJeu scene, ControleurJeu c, String id) throws Exception {
        // Prépare le rappel qui sera exécuté pour l’événement ou l’élément trouvé.
        edt(() -> {
            // Le test pilote les composants sans dépendre de la fenêtre active du bureau.
            // Déclenche le bouton identifié par son texte ; arguments : f, "M · Carte".
            cliquer(f, "M · Carte");
            // Initialise le bouton b avec composants(f).stream().filter(v -> v instanceof JButton && ("salle-" + id).equals(v.getName())).map(v -> (JButton)v).findFirst().orElseThrow().
            JButton b = composants(f).stream().filter(v -> v instanceof JButton && ("salle-" + id).equals(v.getName())).map(v -> (JButton)v).findFirst().orElseThrow();
            // Vérifie que la condition est vraie : si le composant est activé de « b ». Déclenche programmatiquement le bouton Swing. Suspend ou reprend la présentation visuelle ; arguments : false. Lance le trajet visuel vers la porte demandée ; arguments : id. Renvoie l’absence de valeur (null).
            assertTrue(b.isEnabled()); b.doClick(); scene.suspendre(false); scene.marcherVersPorte(id); return null;
        // Ferme le bloc et termine l’instruction ou l’appel englobant.
        });
        // Attend une condition Swing avec un délai limite pour détecter une transition bloquée. Paramètres : () -> c.getPartie().getSalleActuelle().getId().equals(id) && !scene.estOccupe().
        attendre(() -> c.getPartie().getSalleActuelle().getId().equals(id) && !scene.estOccupe());
        // Exécute le travail sur le fil Swing et attend son résultat à l’aide d’une FutureTask. Paramètres : () -> { capture(f, "salle-" + id); return null; }.
        edt(() -> { capture(f, "salle-" + id); return null; });
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    // Interagit avec le coffre ou le sanctuaire selon la salle courante.
    private static void objet(SceneJeu scene) throws Exception {
        // Prépare le rappel qui sera exécuté pour l’événement ou l’élément trouvé.
        edt(() -> {
            // Suspend ou reprend la présentation visuelle ; arguments : false.
            scene.suspendre(false);
            // Initialise la variable e de type double avec Math.min(scene.getWidth() / 960.0, scene.getHeight() / 540.0).
            double e = Math.min(scene.getWidth() / 960.0, scene.getHeight() / 540.0);
            // Initialise « x » avec (int)((scene.getWidth() - 960 * e) / 2 + 585 * e), y = (int)((scene.getHeight() - 540 * e) / 2 + 280 * e).
            int x = (int)((scene.getWidth() - 960 * e) / 2 + 585 * e), y = (int)((scene.getHeight() - 540 * e) / 2 + 280 * e);
            // Envoie à la scène le clic de souris construit par le test ; arguments : new MouseEvent(scene, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, x, y, 1, false). Renvoie l’absence de valeur (null).
            scene.dispatchEvent(new MouseEvent(scene, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, x, y, 1, false)); return null;
        // Ferme le bloc et termine l’instruction ou l’appel englobant.
        });
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    // Pilote les boutons de combat jusqu’à la victoire contre l’adversaire courant.
    private static void vaincre(FenetreJeu f, SceneJeu scene, ControleurJeu c) throws Exception {
        // Initialise « actions » avec 0.
        int actions = 0;
        // Répète ce bloc tant que edt(() -> c.getPartie().estEnCombat()).
        while (edt(() -> c.getPartie().estEnCombat())) {
            // Vérifie que la condition est vraie : actions++ < 100.
            assertTrue(actions++ < 100);
            // Prépare le rappel qui sera exécuté pour l’événement ou l’élément trouvé.
            edt(() -> {
                // Initialise la variable p de type Partie avec la partie conservée par le contrôleur de la variable c. Initialise la variable e de type Ennemi avec p.getSalleActuelle().getEnnemi().
                Partie p = c.getPartie(); Ennemi e = p.getSalleActuelle().getEnnemi();
                // Initialise « d » avec p.getTactique().getDecision().action().
                ActionEnnemi d = p.getTactique().getDecision().action();
                // Initialise « action » avec p.getHeros().getVie() <= 45 && p.peutBoire() ? "3 · Potion".
                String action = p.getHeros().getVie() <= 45 && p.peutBoire() ? "3 · Potion"
                        // Face à une préparation, le pilote du test choisit l’attaque rapide pour l’interrompre.
                        : d == ActionEnnemi.PREPARATION ? "1 · Attaquer"
                        // Face à une riposte sans assez d’élan, choisit Observer pour éviter la contre-attaque.
                        : d == ActionEnnemi.RIPOSTE && p.getTactique().getElan() < 2 ? "5 · Observer"
                        // Face à une frappe lourde ou une onde, choisit Défendre.
                        : d == ActionEnnemi.FRAPPE_LOURDE || d == ActionEnnemi.ONDE ? "2 · Défendre"
                        // Sinon, choisit la frappe puissante avec au moins deux élans, ou l’attaque rapide.
                        : p.getTactique().getElan() >= 2 ? "4 · Puissante (2 Élan)" : "1 · Attaquer";
                // Enregistre une capture de la vraie fenêtre sous target ; arguments : f, "tactique-" + e.getType() + "-intention".
                capture(f, "tactique-" + e.getType() + "-intention");
                // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : p.getTactique().getElan() >= 2 ; bouton(f, "4 · Puissante (2 Élan)").isEnabled().
                assertEquals(p.getTactique().getElan() >= 2, bouton(f, "4 · Puissante (2 Élan)").isEnabled());
                // Déclenche le bouton identifié par son texte ; arguments : f, action. Renvoie l’absence de valeur (null).
                cliquer(f, action); return null;
            // Ferme le bloc et termine l’instruction ou l’appel englobant.
            });
            // Prépare le rappel qui sera exécuté pour l’événement ou l’élément trouvé.
            attendre(() -> {
                // Commence l’opération dont les erreurs sont prises en charge par les blocs catch suivants.
                try {
                    // Initialise la variable a de type ControleurAnimation avec champ(scene, "animation", ControleurAnimation.class).
                    ControleurAnimation a = champ(scene, "animation", ControleurAnimation.class);
                    // Poursuit la condition de contrôle sur les lignes suivantes.
                    if (a.courant() != null && List.of(EvenementJeu.Type.INTERRUPTION, EvenementJeu.Type.CONTRE_ATTAQUE,
                            // Complète les types de faits tactiques dont le test enregistre une capture pendant l’animation.
                            EvenementJeu.Type.EFFET, EvenementJeu.Type.PHASE, EvenementJeu.Type.ELAN, EvenementJeu.Type.BRISE_GARDE).contains(a.courant().type()))
                        // Enregistre une capture de la vraie fenêtre sous target ; arguments : f, "tactique-animation-" + a.courant().type().name().
                        capture(f, "tactique-animation-" + a.courant().type().name());
                // Intercepte Exception ex pour traiter l’échec. Signale l’erreur par une exception : new AssertionError(ex).
                } catch (Exception ex) { throw new AssertionError(ex); }
                // Renvoie !scene.estOccupe().
                return !scene.estOccupe();
            // Ferme le bloc et termine l’instruction ou l’appel englobant.
            });
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Vérifie que les deux résultats sont différents : EtatPartie.DEFAITE ; edt(() -> c.getPartie().getEtat()).
        assertNotEquals(EtatPartie.DEFAITE, edt(() -> c.getPartie().getEtat()));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    // Attend une condition Swing avec un délai limite pour détecter une transition bloquée.
    private static void attendre(BooleanSupplier condition) throws Exception {
        // Initialise « fin » avec l’instant monotone en nanosecondes utilisé dans ce calcul de temps.
        long fin = System.nanoTime() + 12_000_000_000L;
        // Répète ce bloc tant que non (edt(condition::getAsBoolean)) { if (System.nanoTime() > fin) fail("La transition ou animation n'aboutit pas."); Thread.sleep(20).
        while (!edt(condition::getAsBoolean)) { if (System.nanoTime() > fin) fail("La transition ou animation n'aboutit pas."); Thread.sleep(20); }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    // Déclenche programmatiquement le bouton identifié par son texte. Déclenche programmatiquement le bouton Swing.
    private static void cliquer(Container parent, String texte) { bouton(parent, texte).doClick(); }
    // Crée ou recherche un bouton selon le contexte de cette classe.
    private static JButton bouton(Container parent, String texte) {
        // Renvoie composants(parent).stream().filter(c -> c instanceof JButton b && b.isShowing() && texte.equals(b.getText())).map(c -> (JButton)c).findFirst().orElseThrow(() -> new AssertionError("Bouton absent : " + texte)).
        return composants(parent).stream().filter(c -> c instanceof JButton b && b.isShowing() && texte.equals(b.getText())).map(c -> (JButton)c).findFirst().orElseThrow(() -> new AssertionError("Bouton absent : " + texte));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    // Rassemble récursivement les composants d’un conteneur pour piloter le vrai affichage.
    private static List<Component> composants(Container parent) {
        // Initialise le résultat de l'action avec une nouvelle collection indépendante et vide.
        List<Component> resultat = new ArrayList<>();
        // Parcourt parent.getComponents() ; chaque élément est placé dans Component c. Ajoute l’élément à la collection ou au conteneur ; arguments : c. Si c instanceof Container enfant, Recopie tous les éléments dans la collection courante ; arguments : composants(enfant).
        for (Component c : parent.getComponents()) { resultat.add(c); if (c instanceof Container enfant) resultat.addAll(composants(enfant)); }
        // Renvoie le résultat de l'action.
        return resultat;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    // Rend la fenêtre dans une image et écrit la capture sous target.
    private static void capture(FenetreJeu f, String nom) {
        // Commence l’opération dont les erreurs sont prises en charge par les blocs catch suivants.
        try {
            // Calcule le placement des composants avant leur inspection ou leur capture. Initialise « image » avec new BufferedImage(f.getWidth(), f.getHeight(), BufferedImage.TYPE_INT_RGB).
            f.validate(); BufferedImage image = new BufferedImage(f.getWidth(), f.getHeight(), BufferedImage.TYPE_INT_RGB);
            // Initialise la variable g de type var avec image.createGraphics(). Dessine toute la fenêtre dans l’image de capture ; arguments : g. Libère le contexte graphique ou la fenêtre utilisé. Crée les dossiers parents manquants ; arguments : Path.of("target/captures").
            var g = image.createGraphics(); f.paintAll(g); g.dispose(); Files.createDirectories(Path.of("target/captures"));
            // Enregistre l’image de capture au format PNG dans le fichier indiqué ; arguments : image, "png", Path.of("target/captures/" + nom + ".png").toFile().
            ImageIO.write(image, "png", Path.of("target/captures/" + nom + ".png").toFile());
        // Intercepte Exception e pour traiter l’échec. Signale l’erreur par une exception : new AssertionError(e).
        } catch (Exception e) { throw new AssertionError(e); }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    // Lit un champ privé par réflexion uniquement pour les besoins du test. Initialise « champ » avec objet.getClass().getDeclaredField(nom). Autorise la réflexion à accéder au champ privé pour le test ; arguments : true. Renvoie type.cast(champ.get(objet)).
    private static <T> T champ(Object objet, String nom, Class<T> type) throws Exception { var champ = objet.getClass().getDeclaredField(nom); champ.setAccessible(true); return type.cast(champ.get(objet)); }
    // Exécute le travail sur le fil Swing et attend son résultat à l’aide d’une FutureTask.
    private static <T> T edt(Callable<T> action) throws Exception {
        // Initialise « tache » avec new java.util.concurrent.FutureTask<>(action). Exécute le rappel sur le fil Swing et attend sa fin. Renvoie tache.get().
        var tache = new java.util.concurrent.FutureTask<>(action); SwingUtilities.invokeAndWait(tache); return tache.get();
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
