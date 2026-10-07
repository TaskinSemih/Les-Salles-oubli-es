package fr.utbm.sallesoubliees;

import fr.utbm.sallesoubliees.vue.FenetreJeu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Lance l'application desktop sur le fil événementiel de Swing. */
public final class Application {
    /** Point d'entrée uniquement. */
    private Application() { }
    /**
     * Démarre une fenêtre sans dépendance à un serveur.
     * @param args arguments non utilisés
     */
    public static void main(String[] args) {
        var ressources = new fr.utbm.sallesoubliees.vue.GestionnaireRessources();
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
            catch (Exception ignored) { /* Le thème Swing par défaut reste utilisable. */ }
            new FenetreJeu(ressources).setVisible(true);
        });
    }
}
