package fr.utbm.sallesoubliees.vue;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/** Maçonnerie, éclairage, portes et objets : rendu séparé du déplacement et des règles. */
final class RenduSalle {
    private final GestionnaireRessources ressources;
    /** Associe le cache d'images partagé. */
    RenduSalle(GestionnaireRessources ressources) { this.ressources = ressources; }

    /** Prépare un fond à l'entrée d'une salle, jamais depuis paintComponent. */
    BufferedImage fond(String id, boolean combat) {
        BufferedImage image = new BufferedImage(960, 540, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics(); Random hasard = new Random(id.hashCode());
        g.setColor(new Color(16, 19, 31)); g.fillRect(0, 0, 960, 540);
        int hautSol = combat ? 315 : 166;
        // Murs de pierres taillées, joints irréguliers et liserés éclairés.
        for (int y = 25; y < hautSol; y += 28) for (int x = 30 - (y / 28 % 2) * 34; x < 935; x += 68) {
            int v = hasard.nextInt(12);
            g.setColor(new Color(38 + v, 41 + v, 59 + v)); g.fillRect(x, y, 65, 25);
            g.setColor(new Color(65 + v, 68 + v, 84 + v)); g.drawLine(x + 2, y, x + 62, y);
            g.setColor(new Color(25, 23, 40)); g.drawLine(x, y + 24, x + 64, y + 24);
        }
        // Tuiles communes, déterministes et variées ; jamais de bruit recalculé à chaque frame.
        for (int y = hautSol; y < 510; y += 32) for (int x = 66; x < 897; x += 32) {
            int v = hasard.nextInt(15);
            g.setColor(new Color(45 + v, 51 + v, 68 + v)); g.fillRect(x, y, 31, 31);
            g.setColor(new Color(74 + v, 79 + v, 93 + v)); g.drawLine(x + 1, y + 1, x + 29, y + 1);
            g.setColor(new Color(32, 32, 48)); g.drawLine(x + 31, y, x + 31, y + 31);
            if (hasard.nextInt(5) == 0) { g.setColor(new Color(36, 37, 53)); g.drawLine(x + 7, y + 10, x + 13, y + 16); g.drawLine(x + 13, y + 16, x + 11, y + 24); }
        }
        g.setColor(new Color(28, 26, 43)); g.fillRect(35, 160, 42, 355); g.fillRect(886, 160, 42, 355);
        g.setColor(new Color(87, 82, 100)); g.fillRect(65, hautSol, 830, 5);
        g.setColor(new Color(17, 17, 29)); g.fillRect(35, 502, 893, 15);
        // Niches, motifs et éclairages composés sur le fond fixe : le lieu a sa propre ambiance.
        if (id.equals("ossuaire")) for (int x : new int[]{320, 480, 640}) {
            g.setColor(new Color(18, 18, 32)); g.fillRoundRect(x - 29, 70, 58, combat ? 165 : 78, 32, 32);
            g.setColor(new Color(77, 69, 92)); g.drawRoundRect(x - 29, 70, 58, combat ? 165 : 78, 32, 32);
        }
        if (id.equals("repos") || id.equals("antichambre") || id.equals("boss")) {
            int centreY = combat ? 410 : 357;
            g.setColor(new Color(112, 95, 139, 90)); g.setStroke(new BasicStroke(2));
            g.drawOval(339, centreY - 52, 282, 105); g.drawOval(353, centreY - 44, 254, 89);
            for (int i = 0; i < 8; i++) {
                double a = i * Math.PI / 4; int px = 480 + (int)(Math.cos(a) * 124), py = centreY + (int)(Math.sin(a) * 43);
                g.drawPolygon(new int[]{px, px + 5, px, px - 5}, new int[]{py - 6, py, py + 6, py}, 4);
            }
        }
        for (int x : new int[]{150, 807}) {
            lueur(g, x, combat ? 190 : 145, 125, PlanSalle.de(id).lumiere(), .25f);
            ressources.objet(g, 2, x, combat ? 260 : 190, 82);
        }
        if (id.equals("boss")) {
            g.setColor(new Color(15, 13, 27)); g.fillRoundRect(370, 70, 220, combat ? 240 : 95, 100, 100);
            g.setColor(new Color(131, 106, 103)); g.setStroke(new BasicStroke(5)); g.drawRoundRect(370, 70, 220, combat ? 240 : 95, 100, 100);
            for (int x = 400; x < 580; x += 25) { g.setColor(new Color(52, 40, 66)); g.fillRect(x, 85, 8, combat ? 220 : 75); }
        }
        if (combat) {
            for (PlanSalle.Decor d : PlanSalle.de(id).decors()) {
                if (d.y() < 300) ressources.objet(g, d.objet(), d.x(), 331, Math.min(145, d.hauteur()));
            }
            lueur(g, 695, 390, 210, PlanSalle.de(id).lumiere(), .13f);
        }
        lueur(g, 230, hautSol + 70, 220, id.equals("forge") ? new Color(255, 110, 47) : new Color(229, 174, 85), .13f);
        lueur(g, 740, 390, 250, PlanSalle.de(id).lumiere(), .12f);
        // Ombres violettes aux angles, conservation d'un centre praticable bien lisible.
        g.setPaint(new GradientPaint(0, 0, new Color(15, 12, 31, 180), 250, 0, new Color(15, 12, 31, 0)));
        g.fillRect(0, 0, 250, 540);
        g.setPaint(new GradientPaint(960, 0, new Color(15, 12, 31, 180), 710, 0, new Color(15, 12, 31, 0)));
        g.fillRect(710, 0, 250, 540);
        g.setPaint(new GradientPaint(0, 540, new Color(10, 9, 24, 180), 0, 450, new Color(10, 9, 24, 0)));
        g.fillRect(0, 450, 960, 90);
        g.dispose(); return image;
    }

    /** Dessine les objets selon leur état, après le fond en cache. */
    void objets(Graphics2D g, PlanSalle plan, boolean utilisee, String id) {
        objets(g, plan, utilisee, id, Double.POSITIVE_INFINITY, false);
    }
    /** Dessine un groupe d'objets trié par profondeur autour de la ligne des pieds du héros. */
    void objets(Graphics2D g, PlanSalle plan, boolean utilisee, String id, double profondeur, boolean devant) {
        for (PlanSalle.Decor d : plan.decors()) {
            boolean auPremierPlan = d.objet() != 11 && d.y() > profondeur;
            if (auPremierPlan == devant) ressources.objet(g, d.objet(), d.x(), d.y(), d.hauteur());
        }
        if ((307 > profondeur) != devant) return;
        if (id.equals("arsenal") || id.equals("tresor")) ressources.objet(g, utilisee ? 1 : 0, 585, 307, 78);
        if (id.equals("repos")) {
            if (!utilisee) lueur(g, 585, 265, 100, new Color(80, 235, 211), .35f);
            Composite avant = g.getComposite();
            if (utilisee) g.setComposite(AlphaComposite.SrcOver.derive(.45f));
            ressources.objet(g, 9, 585, 307, 107); g.setComposite(avant);
        }
    }

    /** Dessine une porte réelle et son seuil, avec un retour visuel au survol. */
    void porte(Graphics2D g, PlanSalle.Porte porte, boolean survol, String nom) {
        int x = porte.x(), y = porte.y();
        if (porte.cote() == 'N') y = 149;
        if (porte.cote() == 'S') y = 491;
        if (porte.cote() == 'O') x = 80;
        if (porte.cote() == 'E') x = 880;
        g.setColor(new Color(12, 13, 24)); g.fillRoundRect(x - 27, y - 43, 54, 65, 27, 27);
        g.setColor(new Color(70, 65, 82));
        for (int pierre = 0; pierre < 4; pierre++) {
            g.fillRect(x - 37, y - 32 + pierre * 15, 9, 12); g.fillRect(x + 29, y - 32 + pierre * 15, 9, 12);
        }
        g.setColor(new Color(127, 108, 87)); g.fillRect(x - 9, y - 49, 18, 9);
        g.setColor(survol ? new Color(255, 220, 132) : new Color(135, 117, 95));
        g.setStroke(new BasicStroke(survol ? 4 : 3)); g.drawRoundRect(x - 27, y - 43, 54, 65, 27, 27);
        g.setColor(new Color(213, 172, 102, survol ? 100 : 40)); g.fillOval(porte.x() - 30, porte.y() - 8, 60, 18);
        g.setFont(Theme.CORPS.deriveFont(11f)); g.setColor(Theme.OR);
        int tx = porte.cote() == 'O' ? 112 : porte.cote() == 'E' ? 843 - g.getFontMetrics().stringWidth(nom) : x - g.getFontMetrics().stringWidth(nom) / 2;
        g.drawString(nom, tx, porte.cote() == 'N' ? 172 : porte.cote() == 'S' ? 520 : y + 28);
    }

    /** Dessine un halo translucide sans altérer l'image source. */
    static void lueur(Graphics2D g, double x, double y, int rayon, Color couleur, float force) {
        Paint avant = g.getPaint();
        g.setPaint(new RadialGradientPaint((float)x, (float)y, rayon, new float[]{0, 1},
                new Color[]{new Color(couleur.getRed(), couleur.getGreen(), couleur.getBlue(), (int)(force * 255)), new Color(0, 0, 0, 0)}));
        g.fillOval((int)x - rayon, (int)y - rayon, rayon * 2, rayon * 2); g.setPaint(avant);
    }
}
