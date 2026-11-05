// Place cette classe dans la vue, responsable de la présentation Swing et des dessins.
package fr.utbm.sallesoubliees.vue;

// Rend disponibles les types ou constantes de java.awt sans répéter leur nom complet.
import java.awt.*;
// Rend disponible BufferedImage depuis java.awt.image.
import java.awt.image.BufferedImage;
// Rend disponible Random depuis java.util.
import java.util.Random;

/** Maçonnerie, éclairage, portes et objets : rendu séparé du déplacement et des règles. */
// Déclare RenduSalle, dessin des fonds, lumières, portes et objets, indépendant des règles de jeu. final interdit de créer une sous-classe.
final class RenduSalle {
    // Déclare le cache d'images de type GestionnaireRessources.
    private final GestionnaireRessources ressources;
    /** Associe le cache d'images partagé. */
    // Construit un objet RenduSalle : dessin des fonds, lumières, portes et objets, indépendant des règles de jeu. Met à jour le cache d'images avec le cache d'images.
    RenduSalle(GestionnaireRessources ressources) { this.ressources = ressources; }

    /** Prépare un fond à l'entrée d'une salle, jamais depuis paintComponent. */
    // Précalcule l’image fixe de la salle avec un décor reproductible.
    BufferedImage fond(String id, boolean combat) {
        // Initialise « image » avec new BufferedImage(960, 540, BufferedImage.TYPE_INT_RGB).
        BufferedImage image = new BufferedImage(960, 540, BufferedImage.TYPE_INT_RGB);
        // Initialise la variable g de type Graphics2D avec image.createGraphics(). Initialise la source de hasard avec new Random(id.hashCode()).
        Graphics2D g = image.createGraphics(); Random hasard = new Random(id.hashCode());
        // Choisit la couleur du prochain dessin ; arguments : new Color(16, 19, 31). Remplit un rectangle aux coordonnées données ; arguments : 0, 0, 960, 540.
        g.setColor(new Color(16, 19, 31)); g.fillRect(0, 0, 960, 540);
        // Initialise « haut sol » avec combat ? 315 : 166.
        int hautSol = combat ? 315 : 166;
        // Murs de pierres taillées, joints irréguliers et liserés éclairés.
        // Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int y = 25; y < hautSol; y += 28. Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int x = 30 - (y / 28 % 2) * 34; x < 935; x += 68.
        for (int y = 25; y < hautSol; y += 28) for (int x = 30 - (y / 28 % 2) * 34; x < 935; x += 68) {
            // Initialise « v » avec hasard.nextInt(12).
            int v = hasard.nextInt(12);
            // Choisit la couleur du prochain dessin ; arguments : new Color(38 + v, 41 + v, 59 + v). Remplit un rectangle aux coordonnées données ; arguments : x, y, 65, 25.
            g.setColor(new Color(38 + v, 41 + v, 59 + v)); g.fillRect(x, y, 65, 25);
            // Choisit la couleur du prochain dessin ; arguments : new Color(65 + v, 68 + v, 84 + v). Trace un segment entre les deux points indiqués ; arguments : x + 2, y, x + 62, y.
            g.setColor(new Color(65 + v, 68 + v, 84 + v)); g.drawLine(x + 2, y, x + 62, y);
            // Choisit la couleur du prochain dessin ; arguments : new Color(25, 23, 40). Trace un segment entre les deux points indiqués ; arguments : x, y + 24, x + 64, y + 24.
            g.setColor(new Color(25, 23, 40)); g.drawLine(x, y + 24, x + 64, y + 24);
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Tuiles communes, déterministes et variées ; jamais de bruit recalculé à chaque frame.
        // Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int y = hautSol; y < 510; y += 32. Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int x = 66; x < 897; x += 32.
        for (int y = hautSol; y < 510; y += 32) for (int x = 66; x < 897; x += 32) {
            // Initialise « v » avec hasard.nextInt(15).
            int v = hasard.nextInt(15);
            // Choisit la couleur du prochain dessin ; arguments : new Color(45 + v, 51 + v, 68 + v). Remplit un rectangle aux coordonnées données ; arguments : x, y, 31, 31.
            g.setColor(new Color(45 + v, 51 + v, 68 + v)); g.fillRect(x, y, 31, 31);
            // Choisit la couleur du prochain dessin ; arguments : new Color(74 + v, 79 + v, 93 + v). Trace un segment entre les deux points indiqués ; arguments : x + 1, y + 1, x + 29, y + 1.
            g.setColor(new Color(74 + v, 79 + v, 93 + v)); g.drawLine(x + 1, y + 1, x + 29, y + 1);
            // Choisit la couleur du prochain dessin ; arguments : new Color(32, 32, 48). Trace un segment entre les deux points indiqués ; arguments : x + 31, y, x + 31, y + 31.
            g.setColor(new Color(32, 32, 48)); g.drawLine(x + 31, y, x + 31, y + 31);
            // Si hasard.nextInt(5) est égal à 0, Choisit la couleur du prochain dessin ; arguments : new Color(36, 37, 53). Trace un segment entre les deux points indiqués ; arguments : x + 7, y + 10, x + 13, y + 16. Trace un segment entre les deux points indiqués ; arguments : x + 13, y + 16, x + 11, y + 24.
            if (hasard.nextInt(5) == 0) { g.setColor(new Color(36, 37, 53)); g.drawLine(x + 7, y + 10, x + 13, y + 16); g.drawLine(x + 13, y + 16, x + 11, y + 24); }
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Choisit la couleur du prochain dessin ; arguments : new Color(28, 26, 43). Remplit un rectangle aux coordonnées données ; arguments : 35, 160, 42, 355. Remplit un rectangle aux coordonnées données ; arguments : 886, 160, 42, 355.
        g.setColor(new Color(28, 26, 43)); g.fillRect(35, 160, 42, 355); g.fillRect(886, 160, 42, 355);
        // Choisit la couleur du prochain dessin ; arguments : new Color(87, 82, 100). Remplit un rectangle aux coordonnées données ; arguments : 65, hautSol, 830, 5.
        g.setColor(new Color(87, 82, 100)); g.fillRect(65, hautSol, 830, 5);
        // Choisit la couleur du prochain dessin ; arguments : new Color(17, 17, 29). Remplit un rectangle aux coordonnées données ; arguments : 35, 502, 893, 15.
        g.setColor(new Color(17, 17, 29)); g.fillRect(35, 502, 893, 15);
        // Niches, motifs et éclairages composés sur le fond fixe : le lieu a sa propre ambiance.
        // Si id.equals("ossuaire"), Parcourt new int[]{320, 480, 640} ; chaque élément est placé dans int x.
        if (id.equals("ossuaire")) for (int x : new int[]{320, 480, 640}) {
            // Choisit la couleur du prochain dessin ; arguments : new Color(18, 18, 32). Remplit un rectangle à coins arrondis ; arguments : x - 29, 70, 58, combat ? 165 : 78, 32, 32.
            g.setColor(new Color(18, 18, 32)); g.fillRoundRect(x - 29, 70, 58, combat ? 165 : 78, 32, 32);
            // Choisit la couleur du prochain dessin ; arguments : new Color(77, 69, 92). Dessine un contour à coins arrondis ; arguments : x - 29, 70, 58, combat ? 165 : 78, 32, 32.
            g.setColor(new Color(77, 69, 92)); g.drawRoundRect(x - 29, 70, 58, combat ? 165 : 78, 32, 32);
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Si (id.equals("repos") ou id.equals("antichambre") ou id.equals("boss")), entre dans ce traitement conditionnel.
        if (id.equals("repos") || id.equals("antichambre") || id.equals("boss")) {
            // Initialise « centre y » avec combat ? 410 : 357.
            int centreY = combat ? 410 : 357;
            // Choisit la couleur du prochain dessin ; arguments : new Color(112, 95, 139, 90). Choisit l’épaisseur des contours ; arguments : new BasicStroke(2).
            g.setColor(new Color(112, 95, 139, 90)); g.setStroke(new BasicStroke(2));
            // Dessine le contour d’une ellipse ; arguments : 339, centreY - 52, 282, 105. Dessine le contour d’une ellipse ; arguments : 353, centreY - 44, 254, 89.
            g.drawOval(339, centreY - 52, 282, 105); g.drawOval(353, centreY - 44, 254, 89);
            // Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int i = 0; i < 8; i++.
            for (int i = 0; i < 8; i++) {
                // Initialise la variable a de type double avec i * Math.PI / 4. Initialise « px » avec 480 + (int)(Math.cos(a) * 124), py = centreY + (int)(Math.sin(a) * 43).
                double a = i * Math.PI / 4; int px = 480 + (int)(Math.cos(a) * 124), py = centreY + (int)(Math.sin(a) * 43);
                // Trace le polygone défini par les sommets ; arguments : new int[]{px, px + 5, px, px - 5}, new int[]{py - 6, py, py + 6, py}, 4.
                g.drawPolygon(new int[]{px, px + 5, px, px - 5}, new int[]{py - 6, py, py + 6, py}, 4);
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Parcourt new int[]{150, 807} ; chaque élément est placé dans int x.
        for (int x : new int[]{150, 807}) {
            // Dessine un halo lumineux translucide ; arguments : g, x, combat ? 190 : 145, 125, PlanSalle.de(id).lumiere(), .25f.
            lueur(g, x, combat ? 190 : 145, 125, PlanSalle.de(id).lumiere(), .25f);
            // Dessine un objet à partir de l’atlas en mémoire ; arguments : g, 2, x, combat ? 260 : 190, 82.
            ressources.objet(g, 2, x, combat ? 260 : 190, 82);
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Si id.equals("boss"), entre dans ce traitement conditionnel.
        if (id.equals("boss")) {
            // Choisit la couleur du prochain dessin ; arguments : new Color(15, 13, 27). Remplit un rectangle à coins arrondis ; arguments : 370, 70, 220, combat ? 240 : 95, 100, 100.
            g.setColor(new Color(15, 13, 27)); g.fillRoundRect(370, 70, 220, combat ? 240 : 95, 100, 100);
            // Choisit la couleur du prochain dessin ; arguments : new Color(131, 106, 103). Choisit l’épaisseur des contours ; arguments : new BasicStroke(5). Dessine un contour à coins arrondis ; arguments : 370, 70, 220, combat ? 240 : 95, 100, 100.
            g.setColor(new Color(131, 106, 103)); g.setStroke(new BasicStroke(5)); g.drawRoundRect(370, 70, 220, combat ? 240 : 95, 100, 100);
            // Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int x = 400; x < 580; x += 25. Choisit la couleur du prochain dessin ; arguments : new Color(52, 40, 66). Remplit un rectangle aux coordonnées données ; arguments : x, 85, 8, combat ? 220 : 75.
            for (int x = 400; x < 580; x += 25) { g.setColor(new Color(52, 40, 66)); g.fillRect(x, 85, 8, combat ? 220 : 75); }
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Si le mode de présentation du combat, entre dans ce traitement conditionnel.
        if (combat) {
            // Parcourt PlanSalle.de(id).decors() ; chaque élément est placé dans PlanSalle.Decor d.
            for (PlanSalle.Decor d : PlanSalle.de(id).decors()) {
                // Si d.y() est inférieur à 300, Dessine un objet à partir de l’atlas en mémoire ; arguments : g, d.objet(), d.x(), 331, Math.min(145, d.hauteur()).
                if (d.y() < 300) ressources.objet(g, d.objet(), d.x(), 331, Math.min(145, d.hauteur()));
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
            // Dessine un halo lumineux translucide ; arguments : g, 695, 390, 210, PlanSalle.de(id).lumiere(), .13f.
            lueur(g, 695, 390, 210, PlanSalle.de(id).lumiere(), .13f);
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Dessine un halo lumineux translucide ; arguments : g, 230, hautSol + 70, 220, id.equals("forge") ? new Color(255, 110, 47) : new Color(229, 174, 85), .13f.
        lueur(g, 230, hautSol + 70, 220, id.equals("forge") ? new Color(255, 110, 47) : new Color(229, 174, 85), .13f);
        // Dessine un halo lumineux translucide ; arguments : g, 740, 390, 250, PlanSalle.de(id).lumiere(), .12f.
        lueur(g, 740, 390, 250, PlanSalle.de(id).lumiere(), .12f);
        // Ombres violettes aux angles, conservation d'un centre praticable bien lisible.
        // Choisit la peinture ou le dégradé du prochain dessin ; arguments : new GradientPaint(0, 0, new Color(15, 12, 31, 180), 250, 0, new Color(15, 12, 31, 0)).
        g.setPaint(new GradientPaint(0, 0, new Color(15, 12, 31, 180), 250, 0, new Color(15, 12, 31, 0)));
        // Remplit un rectangle aux coordonnées données ; arguments : 0, 0, 250, 540.
        g.fillRect(0, 0, 250, 540);
        // Choisit la peinture ou le dégradé du prochain dessin ; arguments : new GradientPaint(960, 0, new Color(15, 12, 31, 180), 710, 0, new Color(15, 12, 31, 0)).
        g.setPaint(new GradientPaint(960, 0, new Color(15, 12, 31, 180), 710, 0, new Color(15, 12, 31, 0)));
        // Remplit un rectangle aux coordonnées données ; arguments : 710, 0, 250, 540.
        g.fillRect(710, 0, 250, 540);
        // Choisit la peinture ou le dégradé du prochain dessin ; arguments : new GradientPaint(0, 540, new Color(10, 9, 24, 180), 0, 450, new Color(10, 9, 24, 0)).
        g.setPaint(new GradientPaint(0, 540, new Color(10, 9, 24, 180), 0, 450, new Color(10, 9, 24, 0)));
        // Remplit un rectangle aux coordonnées données ; arguments : 0, 450, 960, 90.
        g.fillRect(0, 450, 960, 90);
        // Libère le contexte graphique ou la fenêtre utilisé. Renvoie image.
        g.dispose(); return image;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    /** Dessine les objets selon leur état, après le fond en cache. */
    // Dessine les objets selon leur état et, si demandé, leur profondeur par rapport au héros.
    void objets(Graphics2D g, PlanSalle plan, boolean utilisee, String id) {
        // Dessine les objets selon leur état et, si demandé, leur profondeur par rapport au héros. Paramètres : g, plan, utilisee, id, Double.POSITIVE_INFINITY, false.
        objets(g, plan, utilisee, id, Double.POSITIVE_INFINITY, false);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Dessine un groupe d'objets trié par profondeur autour de la ligne des pieds du héros. */
    // Dessine les objets selon leur état et, si demandé, leur profondeur par rapport au héros.
    void objets(Graphics2D g, PlanSalle plan, boolean utilisee, String id, double profondeur, boolean devant) {
        // Parcourt plan.decors() ; chaque élément est placé dans PlanSalle.Decor d.
        for (PlanSalle.Decor d : plan.decors()) {
            // Initialise « au premier plan » avec d.objet() != 11 && d.y() > profondeur.
            boolean auPremierPlan = d.objet() != 11 && d.y() > profondeur;
            // Si auPremierPlan est égal à devant, Dessine un objet à partir de l’atlas en mémoire ; arguments : g, d.objet(), d.x(), d.y(), d.hauteur().
            if (auPremierPlan == devant) ressources.objet(g, d.objet(), d.x(), d.y(), d.hauteur());
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Si (307 > profondeur) est différent de devant, Quitte la méthode sans poursuivre.
        if ((307 > profondeur) != devant) return;
        // Si (id.equals("arsenal") ou id.equals("tresor")), Dessine un objet à partir de l’atlas en mémoire ; arguments : g, utilisee ? 1 : 0, 585, 307, 78.
        if (id.equals("arsenal") || id.equals("tresor")) ressources.objet(g, utilisee ? 1 : 0, 585, 307, 78);
        // Si id.equals("repos"), entre dans ce traitement conditionnel.
        if (id.equals("repos")) {
            // Si non (le marqueur de coffre ou repos déjà utilisé), Dessine un halo lumineux translucide ; arguments : g, 585, 265, 100, new Color(80, 235, 211), .35f.
            if (!utilisee) lueur(g, 585, 265, 100, new Color(80, 235, 211), .35f);
            // Initialise l'instantané avant l'action avec g.getComposite().
            Composite avant = g.getComposite();
            // Si le marqueur de coffre ou repos déjà utilisé, Règle la transparence et la composition des dessins ; arguments : AlphaComposite.SrcOver.derive(.45f).
            if (utilisee) g.setComposite(AlphaComposite.SrcOver.derive(.45f));
            // Dessine un objet à partir de l’atlas en mémoire ; arguments : g, 9, 585, 307, 107. Règle la transparence et la composition des dessins ; arguments : avant.
            ressources.objet(g, 9, 585, 307, 107); g.setComposite(avant);
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    /** Dessine une porte réelle et son seuil, avec un retour visuel au survol. */
    // Prépare ou dessine une porte et son point d’approche selon son côté.
    void porte(Graphics2D g, PlanSalle.Porte porte, boolean survol, String nom) {
        // Initialise « x » avec porte.x(), y = porte.y().
        int x = porte.x(), y = porte.y();
        // Si porte.cote() est égal à 'N', Met à jour « y » avec 149.
        if (porte.cote() == 'N') y = 149;
        // Si porte.cote() est égal à 'S', Met à jour « y » avec 491.
        if (porte.cote() == 'S') y = 491;
        // Si porte.cote() est égal à 'O', Met à jour « x » avec 80.
        if (porte.cote() == 'O') x = 80;
        // Si porte.cote() est égal à 'E', Met à jour « x » avec 880.
        if (porte.cote() == 'E') x = 880;
        // Choisit la couleur du prochain dessin ; arguments : new Color(12, 13, 24). Remplit un rectangle à coins arrondis ; arguments : x - 27, y - 43, 54, 65, 27, 27.
        g.setColor(new Color(12, 13, 24)); g.fillRoundRect(x - 27, y - 43, 54, 65, 27, 27);
        // Choisit la couleur du prochain dessin ; arguments : new Color(70, 65, 82).
        g.setColor(new Color(70, 65, 82));
        // Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int pierre = 0; pierre < 4; pierre++.
        for (int pierre = 0; pierre < 4; pierre++) {
            // Remplit un rectangle aux coordonnées données ; arguments : x - 37, y - 32 + pierre * 15, 9, 12. Remplit un rectangle aux coordonnées données ; arguments : x + 29, y - 32 + pierre * 15, 9, 12.
            g.fillRect(x - 37, y - 32 + pierre * 15, 9, 12); g.fillRect(x + 29, y - 32 + pierre * 15, 9, 12);
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Choisit la couleur du prochain dessin ; arguments : new Color(127, 108, 87). Remplit un rectangle aux coordonnées données ; arguments : x - 9, y - 49, 18, 9.
        g.setColor(new Color(127, 108, 87)); g.fillRect(x - 9, y - 49, 18, 9);
        // Choisit la couleur du prochain dessin ; arguments : survol ? new Color(255, 220, 132) : new Color(135, 117, 95).
        g.setColor(survol ? new Color(255, 220, 132) : new Color(135, 117, 95));
        // Choisit l’épaisseur des contours ; arguments : new BasicStroke(survol ? 4 : 3). Dessine un contour à coins arrondis ; arguments : x - 27, y - 43, 54, 65, 27, 27.
        g.setStroke(new BasicStroke(survol ? 4 : 3)); g.drawRoundRect(x - 27, y - 43, 54, 65, 27, 27);
        // Choisit la couleur du prochain dessin ; arguments : new Color(213, 172, 102, survol ? 100 : 40). Remplit une ellipse ; arguments : porte.x() - 30, porte.y() - 8, 60, 18.
        g.setColor(new Color(213, 172, 102, survol ? 100 : 40)); g.fillOval(porte.x() - 30, porte.y() - 8, 60, 18);
        // Définit la police de caractères ; arguments : Theme.CORPS.deriveFont(11f). Choisit la couleur du prochain dessin ; arguments : Theme.OR.
        g.setFont(Theme.CORPS.deriveFont(11f)); g.setColor(Theme.OR);
        // Initialise « tx » avec porte.cote() == 'O' ? 112 : porte.cote() == 'E' ? 843 - g.getFontMetrics().stringWidth(nom) : x - g.getFontMetrics().stringWidth(nom) / 2.
        int tx = porte.cote() == 'O' ? 112 : porte.cote() == 'E' ? 843 - g.getFontMetrics().stringWidth(nom) : x - g.getFontMetrics().stringWidth(nom) / 2;
        // Dessine le texte à la position calculée ; arguments : nom, tx, porte.cote() == 'N' ? 172 : porte.cote() == 'S' ? 520 : y + 28.
        g.drawString(nom, tx, porte.cote() == 'N' ? 172 : porte.cote() == 'S' ? 520 : y + 28);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    /** Dessine un halo translucide sans altérer l'image source. */
    // Dessine un halo radial translucide, puis restaure la peinture précédente.
    static void lueur(Graphics2D g, double x, double y, int rayon, Color couleur, float force) {
        // Initialise l'instantané avant l'action avec g.getPaint().
        Paint avant = g.getPaint();
        // Définit un dégradé radial centré sur la lumière, avec une couleur au centre et une autre au bord.
        g.setPaint(new RadialGradientPaint((float)x, (float)y, rayon, new float[]{0, 1},
                // La couleur centrale utilise l’intensité demandée ; le bord est totalement transparent.
                new Color[]{new Color(couleur.getRed(), couleur.getGreen(), couleur.getBlue(), (int)(force * 255)), new Color(0, 0, 0, 0)}));
        // Remplit une ellipse ; arguments : (int)x - rayon, (int)y - rayon, rayon * 2, rayon * 2. Choisit la peinture ou le dégradé du prochain dessin ; arguments : avant.
        g.fillOval((int)x - rayon, (int)y - rayon, rayon * 2, rayon * 2); g.setPaint(avant);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
