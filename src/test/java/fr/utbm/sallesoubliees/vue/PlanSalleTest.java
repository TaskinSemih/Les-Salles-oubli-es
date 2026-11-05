// Place cette classe dans la vue, responsable de la présentation Swing et des dessins.
package fr.utbm.sallesoubliees.vue;

// Rend disponible Donjon depuis fr.utbm.sallesoubliees.modele.
import fr.utbm.sallesoubliees.modele.Donjon;
// Rend disponible Collectors depuis java.util.stream.
import java.util.stream.Collectors;
// Importe l’annotation JUnit qui identifie une méthode de test.
import org.junit.jupiter.api.Test;
// Importe les assertions JUnit pour comparer les résultats et vérifier les erreurs attendues.
import static org.junit.jupiter.api.Assertions.*;

// Déclare PlanSalleTest, tests de cohérence des portes, des collisions et des images embarquées.
class PlanSalleTest {
    // Teste le scénario suivant : portes exactement conformes au graphe et points accessibles.
    @Test void portesExactementConformesAuGrapheEtPointsAccessibles() {
        // Parcourt un nouvel objet Donjon avec ).getSalles( ; chaque élément est placé dans var salle.
        for (var salle : new Donjon().getSalles()) {
            // Initialise le plan visuel de la salle avec PlanSalle.de(salle.getId()).
            PlanSalle plan = PlanSalle.de(salle.getId());
            // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : les passages autorisés de la salle courante ; plan.portes().stream().map(PlanSalle.Porte::destination).collect(Collectors.toSet()).
            assertEquals(salle.getPassages(), plan.portes().stream().map(PlanSalle.Porte::destination).collect(Collectors.toSet()));
            // Vérifie que la condition est vraie : plan.praticable(425, 370).
            assertTrue(plan.praticable(425, 370));
            // Parcourt plan.portes() ; chaque élément est placé dans var porte. Vérifie que la condition est vraie : plan.praticable(porte.x(), porte.y()), salle.getId() + " vers " + porte.destination().
            for (var porte : plan.portes()) assertTrue(plan.praticable(porte.x(), porte.y()), salle.getId() + " vers " + porte.destination());
            // Parcourt plan.decors() ; chaque élément est placé dans var decor. Si decor.solide(), Vérifie que la condition est fausse : plan.praticable(decor.x(), decor.y()).
            for (var decor : plan.decors()) if (decor.solide()) assertFalse(plan.praticable(decor.x(), decor.y()));
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    // Teste le scénario suivant : murs limitent la marche.
    @Test void mursLimitentLaMarche() {
        // Initialise le plan visuel de la salle avec PlanSalle.de("entree"). Vérifie que la condition est fausse : plan.praticable(0, 350). Vérifie que la condition est fausse : plan.praticable(500, 530).
        var plan = PlanSalle.de("entree"); assertFalse(plan.praticable(0, 350)); assertFalse(plan.praticable(500, 530));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    // Teste le scénario suivant : atlas embarques et decoupages lisibles.
    @Test void atlasEmbarquesEtDecoupagesLisibles() {
        // Initialise le cache d'images avec un nouvel objet GestionnaireRessources.
        var ressources = new GestionnaireRessources();
        // Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int i = 0; i < 16; i++. Initialise « icone » avec ressources.icone(i, 48). Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 48 ; icone.getIconWidth().
        for (int i = 0; i < 16; i++) { var icone = ressources.icone(i, 48); assertEquals(48, icone.getIconWidth()); }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    // Teste le scénario suivant : planche des poses pour inspection.
    @Test void plancheDesPosesPourInspection() throws Exception {
        // Initialise le cache d'images avec un nouvel objet GestionnaireRessources.
        var ressources = new GestionnaireRessources();
        // Initialise « image » avec new java.awt.image.BufferedImage(1080, 720, java.awt.image.BufferedImage.TYPE_INT_RGB).
        var image = new java.awt.image.BufferedImage(1080, 720, java.awt.image.BufferedImage.TYPE_INT_RGB);
        // Initialise la variable g de type var avec image.createGraphics(). Choisit la couleur du prochain dessin ; arguments : new java.awt.Color(55, 60, 75). Remplit un rectangle aux coordonnées données ; arguments : 0, 0, 1080, 720.
        var g = image.createGraphics(); g.setColor(new java.awt.Color(55, 60, 75)); g.fillRect(0, 0, 1080, 720);
        // Initialise « types » avec {"heros", "squelette", "golem", "mage", "boss"}.
        String[] types = {"heros", "squelette", "golem", "mage", "boss"};
        // Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int row = 0; row < 5; row++. Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int pose = 0; pose < 6; pose++.
        for (int row = 0; row < 5; row++) for (int pose = 0; pose < 6; pose++) {
            // Choisit la couleur du prochain dessin ; arguments : java.awt.Color.LIGHT_GRAY. Dessine un contour rectangulaire ; arguments : pose * 180, row * 144, 179, 143.
            g.setColor(java.awt.Color.LIGHT_GRAY); g.drawRect(pose * 180, row * 144, 179, 143);
            // Dessine la pose du personnage à partir de l’atlas en mémoire ; arguments : g, types[row], pose, pose * 180 + 90, row * 144 + 132, 102, false.
            ressources.personnage(g, types[row], pose, pose * 180 + 90, row * 144 + 132, 102, false);
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Libère le contexte graphique ou la fenêtre utilisé. Crée les dossiers parents manquants ; arguments : java.nio.file.Path.of("target/captures").
        g.dispose(); java.nio.file.Files.createDirectories(java.nio.file.Path.of("target/captures"));
        // Enregistre l’image de capture au format PNG dans le fichier indiqué ; arguments : image, "png", java.nio.file.Path.of("target/captures/atlas-validation.png").toFile().
        javax.imageio.ImageIO.write(image, "png", java.nio.file.Path.of("target/captures/atlas-validation.png").toFile());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
