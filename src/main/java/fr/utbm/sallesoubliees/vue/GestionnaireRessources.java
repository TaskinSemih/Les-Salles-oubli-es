package fr.utbm.sallesoubliees.vue;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/** Charge les trois atlas locaux une fois ; aucune lecture n'a lieu pendant le rendu. */
public final class GestionnaireRessources {
    private final BufferedImage[][] personnages = new BufferedImage[5][6];
    private final BufferedImage[] objets = new BufferedImage[16];
    private final BufferedImage[][] marche = new BufferedImage[4][3];

    /** Charge les PNG du JAR et extrait leurs cadres explicites à fond transparent. */
    public GestionnaireRessources() {
        BufferedImage acteurs = lire("personnages.png");
        // L'atlas généré n'est pas une grille parfaite : bornes mesurées sur le PNG 1374 × 1145.
        int[] lignes = {20, 253, 466, 704, 906, 1135};
        int[] colonnes = {8, 232, 451, 674, 956, 1154, 1368};
        for (int ligne = 0; ligne < 5; ligne++) for (int pose = 0; pose < 6; pose++) {
            personnages[ligne][pose] = decouper(acteurs, colonnes[pose], lignes[ligne],
                    colonnes[pose + 1] - colonnes[pose], lignes[ligne + 1] - lignes[ligne]);
        }
        BufferedImage decor = lire("objets.png");
        int[] xs = {24, 386, 735, 1090, 1420};
        int[] ys = {4, 281, 558, 825, 1100};
        for (int ligne = 0; ligne < 4; ligne++) for (int col = 0; col < 4; col++) {
            objets[ligne * 4 + col] = decouper(decor, xs[col], ys[ligne], xs[col + 1] - xs[col], ys[ligne + 1] - ys[ligne]);
        }
        BufferedImage marcheAtlas = lire("marche.png");
        int[] mx = {155, 460, 810, 1130}, my = {38, 322, 617, 914, 1200};
        for (int direction = 0; direction < 4; direction++) for (int pose = 0; pose < 3; pose++)
            marche[direction][pose] = decouper(marcheAtlas, mx[pose], my[direction], mx[pose + 1] - mx[pose], my[direction + 1] - my[direction]);
    }

    /** Lit un fichier embarqué et refuse une ressource absente ou sans transparence. */
    private static BufferedImage lire(String nom) {
        try (var flux = GestionnaireRessources.class.getResourceAsStream("/graphismes/" + nom)) {
            if (flux == null) throw new IOException("Ressource absente : " + nom);
            BufferedImage image = ImageIO.read(flux);
            if (image == null || !image.getColorModel().hasAlpha()) throw new IOException("PNG RGBA requis : " + nom);
            return image;
        } catch (IOException erreur) { throw new IllegalStateException("Les illustrations du jeu ne peuvent pas être chargées.", erreur); }
    }

    /** Retire uniquement les marges transparentes du cadre, sans déformer son contenu. */
    private static BufferedImage decouper(BufferedImage atlas, int x, int y, int w, int h) {
        // Écarte les fragments d'un voisin présents dans les marges de l'atlas généré.
        // La silhouette principale est connexe ; les particules détachées sont rendues par les effets Java.
        int[] groupes = new int[w * h]; int groupe = 0, principal = 0, maximum = 0;
        int[] file = new int[w * h];
        for (int depart = 0; depart < groupes.length; depart++) {
            if (groupes[depart] != 0 || (atlas.getRGB(x + depart % w, y + depart / w) >>> 24) <= 100) continue;
            groupe++; int debut = 0, fin = 1; file[0] = depart; groupes[depart] = groupe;
            while (debut < fin) {
                int n = file[debut++], nx = n % w, ny = n / w;
                for (int dy = -1; dy <= 1; dy++) for (int dx = -1; dx <= 1; dx++) {
                    int px = nx + dx, py = ny + dy;
                    if (px < 0 || px >= w || py < 0 || py >= h) continue;
                    int voisin = py * w + px;
                    if (groupes[voisin] == 0 && (atlas.getRGB(x + px, y + py) >>> 24) > 100) { groupes[voisin] = groupe; file[fin++] = voisin; }
                }
            }
            if (fin > maximum) { maximum = fin; principal = groupe; }
        }
        int gauche = w, haut = h, droite = -1, bas = -1;
        for (int j = 0; j < h; j++) for (int i = 0; i < w; i++) {
            if (groupes[j * w + i] == principal && principal != 0) {
                gauche = Math.min(gauche, i); droite = Math.max(droite, i);
                haut = Math.min(haut, j); bas = Math.max(bas, j);
            }
        }
        if (droite < gauche) throw new IllegalStateException("Cadre de sprite vide.");
        BufferedImage image = new BufferedImage(droite - gauche + 1, bas - haut + 1, BufferedImage.TYPE_INT_ARGB);
        for (int j = haut; j <= bas; j++) for (int i = gauche; i <= droite; i++)
            if (groupes[j * w + i] == principal) image.setRGB(i - gauche, j - haut, atlas.getRGB(x + i, y + j));
        return image;
    }

    /**
     * Dessine le héros de dessus avec une véritable vue de dos pour le nord.
     * @param g contexte
     * @param direction sud=0, nord=1, est=2, ouest=3
     * @param pose repos=0, marches=1/2
     * @param x centre des pieds
     * @param y ligne des pieds
     */
    public void marche(Graphics2D g, int direction, int pose, double x, double y) {
        BufferedImage image = marche[direction][pose];
        double echelle = 78.0 / marche[direction][0].getHeight();
        int w = (int)(image.getWidth() * echelle), h = (int)(image.getHeight() * echelle);
        g.drawImage(image, (int)x - w / 2, (int)y - h, w, h, null);
    }

    /**
     * Dessine une pose ancrée au centre des pieds, en conservant les proportions.
     * @param g contexte de rendu
     * @param type heros, squelette, golem, mage ou boss
     * @param pose repos=0, marches=1/2, attaque=3, défense=4, blessure=5
     * @param x centre horizontal
     * @param pieds ligne des pieds
     * @param hauteur hauteur de référence du personnage au repos
     * @param miroir retourne horizontalement la pose
     */
    public void personnage(Graphics2D g, String type, int pose, double x, double pieds, int hauteur, boolean miroir) {
        int ligne = switch (type) { case "squelette" -> 1; case "golem" -> 2; case "mage" -> 3; case "boss" -> 4; default -> 0; };
        BufferedImage image = personnages[ligne][pose];
        double echelle = (double) hauteur / personnages[ligne][0].getHeight();
        int w = (int) Math.round(image.getWidth() * echelle), h = (int) Math.round(image.getHeight() * echelle);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(image, (int)x + (miroir ? w / 2 : -w / 2), (int)pieds - h, miroir ? -w : w, h, null);
    }

    /**
     * Dessine un objet du catalogue : coffre 0/1, torche 2, pilier 3, escalier 4,
     * os 5, râtelier 6, fourneau 7, enclume 8, autel 9, livres 10, rune 11,
     * or 12, gravats 13, potion 14, bouclier 15.
     * @param g contexte
     * @param id index du catalogue
     * @param x centre horizontal
     * @param pieds base de l'objet
     * @param hauteur hauteur affichée
     */
    public void objet(Graphics2D g, int id, int x, int pieds, int hauteur) {
        BufferedImage image = objets[id];
        int largeur = image.getWidth() * hauteur / image.getHeight();
        g.drawImage(image, x - largeur / 2, pieds - hauteur, largeur, hauteur, null);
    }

    /**
     * Fournit une icône locale pour les composants Swing.
     * @param id objet du catalogue
     * @param taille côté du carré transparent
     * @return icône indépendante
     */
    public javax.swing.ImageIcon icone(int id, int taille) {
        BufferedImage image = new BufferedImage(taille, taille, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        objet(g, id, taille / 2, taille - 2, taille - 4); g.dispose();
        return new javax.swing.ImageIcon(image);
    }
}
