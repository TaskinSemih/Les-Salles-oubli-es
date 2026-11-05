// Place le lanceur dans le paquet racine de l’application.
package fr.utbm.sallesoubliees;

// Rend disponible FenetreJeu depuis fr.utbm.sallesoubliees.vue.
import fr.utbm.sallesoubliees.vue.FenetreJeu;
// Rend disponible SwingUtilities depuis javax.swing.
import javax.swing.SwingUtilities;
// Rend disponible UIManager depuis javax.swing.
import javax.swing.UIManager;

/** Lance l'application desktop sur le fil événementiel de Swing. */
// Déclare Application, point d'entrée qui prépare les images puis ouvre la fenêtre sur le fil Swing. final interdit de créer une sous-classe.
public final class Application {
    /** Point d'entrée uniquement. */
    // Empêche la création extérieure de cette classe utilitaire grâce à un constructeur privé.
    private Application() { }
    /**
     * Démarre une fenêtre sans dépendance à un serveur.
     * @param args arguments non utilisés
     */
    // Point d’entrée Java : prépare les ressources puis programme l’ouverture de la fenêtre Swing.
    public static void main(String[] args) {
        // Initialise le cache d'images avec un nouvel objet GestionnaireRessources.
        var ressources = new fr.utbm.sallesoubliees.vue.GestionnaireRessources();
        // Programme le rappel sur le fil événementiel Swing sans bloquer le fil courant.
        SwingUtilities.invokeLater(() -> {
            // Essaie d’appliquer le thème Swing multiplateforme ; le thème par défaut reste disponible si cela échoue.
            try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
            // Intercepte Exception ignored pour traiter l’échec. Ne contient aucune instruction supplémentaire.
            catch (Exception ignored) { /* Le thème Swing par défaut reste utilisable. */ }
            // Construit la fenêtre avec les images déjà chargées, puis la rend visible.
            new FenetreJeu(ressources).setVisible(true);
        // Ferme le bloc et termine l’instruction ou l’appel englobant.
        });
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
