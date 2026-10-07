package fr.utbm.sallesoubliees.vue;

import fr.utbm.sallesoubliees.modele.Donjon;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlanSalleTest {
    @Test void portesExactementConformesAuGrapheEtPointsAccessibles() {
        for (var salle : new Donjon().getSalles()) {
            PlanSalle plan = PlanSalle.de(salle.getId());
            assertEquals(salle.getPassages(), plan.portes().stream().map(PlanSalle.Porte::destination).collect(Collectors.toSet()));
            assertTrue(plan.praticable(425, 370));
            for (var porte : plan.portes()) assertTrue(plan.praticable(porte.x(), porte.y()), salle.getId() + " vers " + porte.destination());
            for (var decor : plan.decors()) if (decor.solide()) assertFalse(plan.praticable(decor.x(), decor.y()));
        }
    }
    @Test void mursLimitentLaMarche() {
        var plan = PlanSalle.de("entree"); assertFalse(plan.praticable(0, 350)); assertFalse(plan.praticable(500, 530));
    }
    @Test void atlasEmbarquesEtDecoupagesLisibles() {
        var ressources = new GestionnaireRessources();
        for (int i = 0; i < 16; i++) { var icone = ressources.icone(i, 48); assertEquals(48, icone.getIconWidth()); }
    }
    @Test void plancheDesPosesPourInspection() throws Exception {
        var ressources = new GestionnaireRessources();
        var image = new java.awt.image.BufferedImage(1080, 720, java.awt.image.BufferedImage.TYPE_INT_RGB);
        var g = image.createGraphics(); g.setColor(new java.awt.Color(55, 60, 75)); g.fillRect(0, 0, 1080, 720);
        String[] types = {"heros", "squelette", "golem", "mage", "boss"};
        for (int row = 0; row < 5; row++) for (int pose = 0; pose < 6; pose++) {
            g.setColor(java.awt.Color.LIGHT_GRAY); g.drawRect(pose * 180, row * 144, 179, 143);
            ressources.personnage(g, types[row], pose, pose * 180 + 90, row * 144 + 132, 102, false);
        }
        g.dispose(); java.nio.file.Files.createDirectories(java.nio.file.Path.of("target/captures"));
        javax.imageio.ImageIO.write(image, "png", java.nio.file.Path.of("target/captures/atlas-validation.png").toFile());
    }
}
