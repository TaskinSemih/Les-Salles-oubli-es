// Place cette classe dans la vue, responsable de la présentation Swing et des dessins.
package fr.utbm.sallesoubliees.vue;

// Rend disponible Graphics2D depuis java.awt.
import java.awt.Graphics2D;
// Rend disponible RenderingHints depuis java.awt.
import java.awt.RenderingHints;
// Rend disponible BufferedImage depuis java.awt.image.
import java.awt.image.BufferedImage;
// Rend disponible IOException depuis java.io.
import java.io.IOException;
// Rend disponible ImageIO depuis javax.imageio.
import javax.imageio.ImageIO;

/** Charge les trois atlas locaux une fois ; aucune lecture n'a lieu pendant le rendu. */
// Déclare GestionnaireRessources, cache des images embarquées : charge les atlas, découpe les sprites et les dessine. final interdit de créer une sous-classe.
public final class GestionnaireRessources {
    // Initialise les poses découpées des cinq personnages avec new BufferedImage[5][6].
    private final BufferedImage[][] personnages = new BufferedImage[5][6];
    // Initialise les seize images des objets avec new BufferedImage[16].
    private final BufferedImage[] objets = new BufferedImage[16];
    // Initialise les images ou le temps de marche avec new BufferedImage[4][3].
    private final BufferedImage[][] marche = new BufferedImage[4][3];

    /** Charge les PNG du JAR et extrait leurs cadres explicites à fond transparent. */
    // Construit un objet GestionnaireRessources : cache des images embarquées : charge les atlas, découpe les sprites et les dessine.
    public GestionnaireRessources() {
        // Initialise « acteurs » avec lire("personnages.png").
        BufferedImage acteurs = lire("personnages.png");
        // L'atlas généré n'est pas une grille parfaite : bornes mesurées sur le PNG 1374 × 1145.
        // Initialise « lignes » avec {20, 253, 466, 704, 906, 1135}.
        int[] lignes = {20, 253, 466, 704, 906, 1135};
        // Initialise « colonnes » avec {8, 232, 451, 674, 956, 1154, 1368}.
        int[] colonnes = {8, 232, 451, 674, 956, 1154, 1368};
        // Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int ligne = 0; ligne < 5; ligne++. Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int pose = 0; pose < 6; pose++.
        for (int ligne = 0; ligne < 5; ligne++) for (int pose = 0; pose < 6; pose++) {
            // Découpe la pose du personnage à partir de ses bornes mesurées dans l’atlas.
            personnages[ligne][pose] = decouper(acteurs, colonnes[pose], lignes[ligne],
                    // Calcule largeur et hauteur du cadre par différence entre deux bornes voisines.
                    colonnes[pose + 1] - colonnes[pose], lignes[ligne + 1] - lignes[ligne]);
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Initialise « decor » avec lire("objets.png").
        BufferedImage decor = lire("objets.png");
        // Initialise « xs » avec {24, 386, 735, 1090, 1420}.
        int[] xs = {24, 386, 735, 1090, 1420};
        // Initialise « ys » avec {4, 281, 558, 825, 1100}.
        int[] ys = {4, 281, 558, 825, 1100};
        // Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int ligne = 0; ligne < 4; ligne++. Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int col = 0; col < 4; col++.
        for (int ligne = 0; ligne < 4; ligne++) for (int col = 0; col < 4; col++) {
            // Découpe chaque objet et l’enregistre dans le tableau linéaire à l’indice ligne × 4 + colonne.
            objets[ligne * 4 + col] = decouper(decor, xs[col], ys[ligne], xs[col + 1] - xs[col], ys[ligne + 1] - ys[ligne]);
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Initialise « marche atlas » avec lire("marche.png").
        BufferedImage marcheAtlas = lire("marche.png");
        // Initialise « mx » avec {155, 460, 810, 1130}, my = {38, 322, 617, 914, 1200}.
        int[] mx = {155, 460, 810, 1130}, my = {38, 322, 617, 914, 1200};
        // Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int direction = 0; direction < 4; direction++. Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int pose = 0; pose < 3; pose++.
        for (int direction = 0; direction < 4; direction++) for (int pose = 0; pose < 3; pose++)
            // Découpe et mémorise chacune des trois poses de chaque direction de marche.
            marche[direction][pose] = decouper(marcheAtlas, mx[pose], my[direction], mx[pose + 1] - mx[pose], my[direction + 1] - my[direction]);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    /** Lit un fichier embarqué et refuse une ressource absente ou sans transparence. */
    // Lit une image embarquée et refuse une ressource absente ou sans transparence.
    private static BufferedImage lire(String nom) {
        // Ouvre une ressource dans un try-with-resources : Java la fermera automatiquement à la sortie du bloc.
        try (var flux = GestionnaireRessources.class.getResourceAsStream("/graphismes/" + nom)) {
            // Si flux est égal à l’absence de valeur (null), refuse l’opération : "Ressource absente : ".
            if (flux == null) throw new IOException("Ressource absente : " + nom);
            // Initialise « image » avec ImageIO.read(flux).
            BufferedImage image = ImageIO.read(flux);
            // Si (image est égal à l’absence de valeur (null) ou non (image.getColorModel().hasAlpha())), refuse l’opération : "PNG RGBA requis : ".
            if (image == null || !image.getColorModel().hasAlpha()) throw new IOException("PNG RGBA requis : " + nom);
            // Renvoie image.
            return image;
        // Intercepte IOException erreur pour traiter l’échec. Signale l’erreur par une exception : "Les illustrations du jeu ne peuvent pas être chargées.".
        } catch (IOException erreur) { throw new IllegalStateException("Les illustrations du jeu ne peuvent pas être chargées.", erreur); }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    /** Retire uniquement les marges transparentes du cadre, sans déformer son contenu. */
    // Isole la silhouette principale d’un cadre et retire ses marges transparentes.
    private static BufferedImage decouper(BufferedImage atlas, int x, int y, int w, int h) {
        // Écarte les fragments d'un voisin présents dans les marges de l'atlas généré.
        // La silhouette principale est connexe ; les particules détachées sont rendues par les effets Java.
        // Initialise « groupes » avec new int[w * h]. Initialise « groupe » avec 0, principal = 0, maximum = 0.
        int[] groupes = new int[w * h]; int groupe = 0, principal = 0, maximum = 0;
        // Alloue une file d’indices de pixels pour explorer une composante connexe sans récursion.
        int[] file = new int[w * h];
        // Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int depart = 0; depart < groupes.length; depart++.
        for (int depart = 0; depart < groupes.length; depart++) {
            // Ignore un pixel déjà classé ou dont l’opacité ne dépasse pas 100 sur 255.
            if (groupes[depart] != 0 || (atlas.getRGB(x + depart % w, y + depart / w) >>> 24) <= 100) continue;
            // Ouvre un nouveau groupe de pixels et place son premier pixel dans la file d’exploration.
            groupe++; int debut = 0, fin = 1; file[0] = depart; groupes[depart] = groupe;
            // Répète ce bloc tant que debut est inférieur à fin.
            while (debut < fin) {
                // Retire le prochain pixel de la file et traduit son indice linéaire en coordonnées locales.
                int n = file[debut++], nx = n % w, ny = n / w;
                // Examine les huit voisins du pixel, diagonales comprises ; le pixel central sera déjà marqué.
                for (int dy = -1; dy <= 1; dy++) for (int dx = -1; dx <= 1; dx++) {
                    // Initialise « px » avec nx + dx, py = ny + dy.
                    int px = nx + dx, py = ny + dy;
                    // Ignore les voisins qui sortent du cadre en cours de découpe.
                    if (px < 0 || px >= w || py < 0 || py >= h) continue;
                    // Traduit les coordonnées du voisin en indice linéaire pour accéder aux tableaux.
                    int voisin = py * w + px;
                    // Un voisin assez opaque non visité rejoint le groupe courant et la file d’exploration.
                    if (groupes[voisin] == 0 && (atlas.getRGB(x + px, y + py) >>> 24) > 100) { groupes[voisin] = groupe; file[fin++] = voisin; }
                // Ferme le bloc ou la liste de données commencé plus haut.
                }
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
            // Retient le groupe contenant le plus de pixels : il correspond à la silhouette principale.
            if (fin > maximum) { maximum = fin; principal = groupe; }
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Initialise une boîte englobante vide qui sera resserrée autour de la silhouette conservée.
        int gauche = w, haut = h, droite = -1, bas = -1;
        // Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int j = 0; j < h; j++. Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int i = 0; i < w; i++.
        for (int j = 0; j < h; j++) for (int i = 0; i < w; i++) {
            // Si (groupes[j * w + i] est égal à principal et principal est différent de 0), entre dans ce traitement conditionnel.
            if (groupes[j * w + i] == principal && principal != 0) {
                // Étend les bornes horizontales jusqu’aux pixels du groupe principal.
                gauche = Math.min(gauche, i); droite = Math.max(droite, i);
                // Étend les bornes verticales jusqu’aux pixels du groupe principal.
                haut = Math.min(haut, j); bas = Math.max(bas, j);
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Si droite est inférieur à gauche, refuse l’opération : "Cadre de sprite vide.".
        if (droite < gauche) throw new IllegalStateException("Cadre de sprite vide.");
        // Crée une image transparente aux dimensions exactes de la silhouette principale.
        BufferedImage image = new BufferedImage(droite - gauche + 1, bas - haut + 1, BufferedImage.TYPE_INT_ARGB);
        // Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int j = haut; j <= bas; j++. Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int i = gauche; i <= droite; i++.
        for (int j = haut; j <= bas; j++) for (int i = gauche; i <= droite; i++)
            // Copie uniquement les pixels du groupe principal en recalant leur origine dans l’image découpée.
            if (groupes[j * w + i] == principal) image.setRGB(i - gauche, j - haut, atlas.getRGB(x + i, y + j));
        // Renvoie image.
        return image;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    /**
     * Dessine le héros de dessus avec une véritable vue de dos pour le nord.
     * @param g contexte
     * @param direction sud=0, nord=1, est=2, ouest=3
     * @param pose repos=0, marches=1/2
     * @param x centre des pieds
     * @param y ligne des pieds
     */
    // Dessine le héros de dessus avec une véritable vue de dos pour le nord.
    public void marche(Graphics2D g, int direction, int pose, double x, double y) {
        // Initialise « image » avec marche[direction][pose].
        BufferedImage image = marche[direction][pose];
        // Initialise « echelle » avec 78.0 / marche[direction][0].getHeight().
        double echelle = 78.0 / marche[direction][0].getHeight();
        // Initialise « w » avec (int)(image.getWidth() * echelle), h = (int)(image.getHeight() * echelle).
        int w = (int)(image.getWidth() * echelle), h = (int)(image.getHeight() * echelle);
        // Dessine l’image à la position et à la taille indiquées ; arguments : image, (int)x - w / 2, (int)y - h, w, h, null.
        g.drawImage(image, (int)x - w / 2, (int)y - h, w, h, null);
    // Ferme le bloc ou la liste de données commencé plus haut.
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
    // Dessine une pose ancrée au centre des pieds, en conservant les proportions.
    public void personnage(Graphics2D g, String type, int pose, double x, double pieds, int hauteur, boolean miroir) {
        // Initialise la ligne courante avec switch (type) { case "squelette" -> 1; case "golem" -> 2; case "mage" -> 3; case "boss" -> 4; default -> 0; }.
        int ligne = switch (type) { case "squelette" -> 1; case "golem" -> 2; case "mage" -> 3; case "boss" -> 4; default -> 0; };
        // Initialise « image » avec personnages[ligne][pose].
        BufferedImage image = personnages[ligne][pose];
        // Initialise « echelle » avec (double) hauteur / personnages[ligne][0].getHeight().
        double echelle = (double) hauteur / personnages[ligne][0].getHeight();
        // Initialise « w » avec (int) Math.round(image.getWidth() * echelle), h = (int) Math.round(image.getHeight() * echelle).
        int w = (int) Math.round(image.getWidth() * echelle), h = (int) Math.round(image.getHeight() * echelle);
        // Configure l’interpolation ou le lissage du rendu ; arguments : RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR.
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        // Dessine l’image à la position et à la taille indiquées ; arguments : image, (int)x + (miroir ? w / 2 : -w / 2), (int)pieds - h, miroir ? -w : w, h, null.
        g.drawImage(image, (int)x + (miroir ? w / 2 : -w / 2), (int)pieds - h, miroir ? -w : w, h, null);
    // Ferme le bloc ou la liste de données commencé plus haut.
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
    // Interagit avec le coffre ou le sanctuaire selon la salle courante.
    public void objet(Graphics2D g, int id, int x, int pieds, int hauteur) {
        // Initialise « image » avec objets[id].
        BufferedImage image = objets[id];
        // Initialise « largeur » avec image.getWidth() * hauteur / image.getHeight().
        int largeur = image.getWidth() * hauteur / image.getHeight();
        // Dessine l’image à la position et à la taille indiquées ; arguments : image, x - largeur / 2, pieds - hauteur, largeur, hauteur, null.
        g.drawImage(image, x - largeur / 2, pieds - hauteur, largeur, hauteur, null);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    /**
     * Fournit une icône locale pour les composants Swing.
     * @param id objet du catalogue
     * @param taille côté du carré transparent
     * @return icône indépendante
     */
    // Fournit une icône locale pour les composants Swing.
    public javax.swing.ImageIcon icone(int id, int taille) {
        // Initialise « image » avec new BufferedImage(taille, taille, BufferedImage.TYPE_INT_ARGB).
        BufferedImage image = new BufferedImage(taille, taille, BufferedImage.TYPE_INT_ARGB);
        // Initialise la variable g de type Graphics2D avec image.createGraphics().
        Graphics2D g = image.createGraphics();
        // Configure l’interpolation ou le lissage du rendu ; arguments : RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR.
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        // Dessine un objet à partir de l’atlas en mémoire ; arguments : g, id, taille / 2, taille - 2, taille - 4. Libère le contexte graphique ou la fenêtre utilisé.
        objet(g, id, taille / 2, taille - 2, taille - 4); g.dispose();
        // Renvoie new javax.swing.ImageIcon(image).
        return new javax.swing.ImageIcon(image);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
