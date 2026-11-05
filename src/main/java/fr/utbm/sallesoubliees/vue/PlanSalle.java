// Place cette classe dans la vue, responsable de la présentation Swing et des dessins.
package fr.utbm.sallesoubliees.vue;

// Rend disponible Color depuis java.awt.
import java.awt.Color;
// Rend disponible Rectangle2D depuis java.awt.geom.
import java.awt.geom.Rectangle2D;
// Rend disponible List depuis java.util.
import java.util.List;
// Rend disponible Map depuis java.util.
import java.util.Map;

/**
 * Composition visuelle explicite des neuf salles ; ne remplace jamais le graphe métier.
 * @param sousTitre ambiance de la salle
 * @param lumiere couleur d'accent
 * @param portes passages visuels conformes au graphe
 * @param decors objets disposés dans l'espace
 */
// Déclare PlanSalle, plan visuel immuable des portes, décors, couleurs et obstacles d'une salle. Le record fournit les accesseurs, equals et hashCode à partir de ses données.
public record PlanSalle(String sousTitre, Color lumiere, List<Porte> portes, List<Decor> decors) {
    /** Copie les listes pour empêcher une modification accidentelle du décor. */
    // Constructeur compact du record : vérifie ou copie défensivement les données reçues. Met à jour « portes » avec une copie non modifiable de portes. Met à jour « decors » avec une copie non modifiable de decors.
    public PlanSalle { portes = List.copyOf(portes); decors = List.copyOf(decors); }

    /**
     * Porte et point d'approche dans les coordonnées logiques.
     * @param destination identifiant métier exact
     * @param cote N, S, E ou O
     * @param x approche du héros
     * @param y approche du héros
     */
    // Déclare Porte, données d'une porte : destination, côté et point d'approche du héros. Le record fournit les accesseurs, equals et hashCode à partir de ses données.
    public record Porte(String destination, char cote, int x, int y) { }
    /**
     * Objet de décor, dont seule la base peut bloquer le déplacement.
     * @param objet index de l'atlas
     * @param x centre
     * @param y pieds
     * @param hauteur hauteur dessinée
     * @param solide collision à la base
     */
    // Déclare Decor, données d'un objet de décor et de sa collision éventuelle au sol. Le record fournit les accesseurs, equals et hashCode à partir de ses données.
    public record Decor(int objet, int x, int y, int hauteur, boolean solide) {
        /** {@return rectangle de collision au sol, indépendant de la hauteur du dessin} */
        // Construit le rectangle de collision à la base de cet objet de décor. Renvoie new Rectangle2D.Double(x - 31, y - 18, 62, 30).
        public Rectangle2D obstacle() { return new Rectangle2D.Double(x - 31, y - 18, 62, 30); }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Initialise « ambre » avec new Color(235, 167, 75).
    private static final Color AMBRE = new Color(235, 167, 75);
    // Initialise « turquoise » avec new Color(69, 197, 191).
    private static final Color TURQUOISE = new Color(69, 197, 191);
    // Initialise « violet » avec new Color(157, 109, 217).
    private static final Color VIOLET = new Color(157, 109, 217);
    // Initialise « plans » avec Map.ofEntries(.
    private static final Map<String, PlanSalle> PLANS = Map.ofEntries(
        // Associe l’identifiant de salle à son ambiance, ses portes et ses décors visuels.
        Map.entry("entree", new PlanSalle("Le seuil des oubliés", AMBRE,
                // Définit les portes visuelles : chaque destination et chaque côté correspondent à un vrai passage.
                List.of(porte("ossuaire", 'E')), List.of(d(4, 205, 295, 125), d(13, 280, 430, 48), d(3, 730, 230, 120), d(13, 760, 460, 45)))),
        // Associe l’identifiant de salle à son ambiance, ses portes et ses décors visuels.
        Map.entry("ossuaire", new PlanSalle("Les pierres ont une mémoire", VIOLET,
                // Définit les portes visuelles : chaque destination et chaque côté correspondent à un vrai passage.
                List.of(porte("entree", 'O'), porte("arsenal", 'E'), porte("forge", 'S')),
                // Dispose les décors : indice d’objet, abscisse, position des pieds et hauteur dessinée.
                List.of(d(5, 250, 245, 75), d(5, 720, 460, 65), d(3, 730, 220, 130), d(13, 240, 450, 50)))),
        // Associe l’identifiant de salle à son ambiance, ses portes et ses décors visuels.
        Map.entry("arsenal", new PlanSalle("L'acier attend son porteur", AMBRE,
                // Définit les portes visuelles : chaque destination et chaque côté correspondent à un vrai passage.
                List.of(porte("ossuaire", 'O'), porte("repos", 'S')),
                // Dispose les décors : indice d’objet, abscisse, position des pieds et hauteur dessinée.
                List.of(d(6, 240, 260, 110), d(6, 740, 250, 110), d(15, 250, 440, 70)))),
        // Associe l’identifiant de salle à son ambiance, ses portes et ses décors visuels.
        Map.entry("forge", new PlanSalle("La braise sous la cendre", new Color(234, 105, 58),
                // Définit les portes visuelles : chaque destination et chaque côté correspondent à un vrai passage.
                List.of(porte("ossuaire", 'N'), porte("repos", 'E')),
                // Dispose les décors : indice d’objet, abscisse, position des pieds et hauteur dessinée.
                List.of(d(7, 260, 265, 140), d(8, 680, 260, 85), d(13, 730, 450, 50), d(8, 270, 440, 80)))),
        // Associe l’identifiant de salle à son ambiance, ses portes et ses décors visuels.
        Map.entry("repos", new PlanSalle("Une dernière lueur de paix", TURQUOISE,
                // Définit les portes visuelles : chaque destination et chaque côté correspondent à un vrai passage.
                List.of(porte("arsenal", 'N'), porte("forge", 'O'), porte("bibliotheque", 'E')),
                // Dispose les décors : indice d’objet, abscisse, position des pieds et hauteur dessinée.
                List.of(d(3, 240, 250, 120), d(3, 740, 250, 120), d(11, 480, 390, 90)))),
        // Associe l’identifiant de salle à son ambiance, ses portes et ses décors visuels.
        Map.entry("bibliotheque", new PlanSalle("Les mots que nul ne doit lire", VIOLET,
                // Définit les portes visuelles : chaque destination et chaque côté correspondent à un vrai passage.
                List.of(porte("repos", 'O'), porte("antichambre", 'S')),
                // Dispose les décors : indice d’objet, abscisse, position des pieds et hauteur dessinée.
                List.of(d(10, 245, 260, 135), d(10, 700, 250, 135), d(10, 760, 440, 110), d(11, 300, 425, 70)))),
        // Associe l’identifiant de salle à son ambiance, ses portes et ses décors visuels.
        Map.entry("tresor", new PlanSalle("L'or ne protège pas des ombres", AMBRE,
                // Définit les portes visuelles : chaque destination et chaque côté correspondent à un vrai passage.
                List.of(porte("antichambre", 'E')),
                // Dispose les décors : indice d’objet, abscisse, position des pieds et hauteur dessinée.
                List.of(d(12, 240, 260, 75), d(12, 730, 450, 75), d(3, 720, 250, 120), d(12, 260, 430, 65)))),
        // Associe l’identifiant de salle à son ambiance, ses portes et ses décors visuels.
        Map.entry("antichambre", new PlanSalle("Le silence avant le Gardien", VIOLET,
                // Définit les portes visuelles : chaque destination et chaque côté correspondent à un vrai passage.
                List.of(porte("bibliotheque", 'N'), porte("tresor", 'O'), porte("boss", 'E')),
                // Dispose les décors : indice d’objet, abscisse, position des pieds et hauteur dessinée.
                List.of(d(3, 260, 255, 145), d(3, 720, 255, 145), d(3, 260, 445, 125), d(3, 720, 445, 125), d(11, 480, 325, 75)))),
        // Associe l’identifiant de salle à son ambiance, ses portes et ses décors visuels.
        Map.entry("boss", new PlanSalle("Le serment brisé", new Color(186, 74, 112),
                // Définit les portes visuelles : chaque destination et chaque côté correspondent à un vrai passage.
                List.of(porte("antichambre", 'O')),
                // Dispose les décors : indice d’objet, abscisse, position des pieds et hauteur dessinée.
                List.of(d(3, 245, 250, 150), d(3, 745, 250, 150), d(11, 630, 410, 135), d(5, 240, 450, 55))))
    // Ferme le bloc et termine l’instruction ou l’appel englobant.
    );

    /** Crée un objet solide, sauf les runes et petits débris. */
    // Crée un décor dont la solidité dépend de l’indice d’objet. Renvoie un nouvel objet Decor avec objet, x, y, hauteur, objet != 11 && objet != 13 && objet != 5 && objet != 12.
    private static Decor d(int objet, int x, int y, int hauteur) { return new Decor(objet, x, y, hauteur, objet != 11 && objet != 13 && objet != 5 && objet != 12); }
    /** Crée une porte avec un point de marche situé à l'intérieur du sol. */
    // Prépare ou dessine une porte et son point d’approche selon son côté.
    private static Porte porte(String destination, char cote) {
        // Renvoie la valeur produite par le switch selon le cas rencontré.
        return switch (cote) {
            // Une porte ouest a son point d’approche en (111, 340), à l’intérieur du sol.
            case 'O' -> new Porte(destination, cote, 111, 340);
            // Une porte est a son point d’approche en (849, 340).
            case 'E' -> new Porte(destination, cote, 849, 340);
            // Une porte nord a son point d’approche en (480, 200).
            case 'N' -> new Porte(destination, cote, 480, 200);
            // Le dernier cas correspond au sud, avec un point d’approche en (480, 466).
            default -> new Porte(destination, cote, 480, 466);
        // Ferme le bloc et termine l’instruction ou l’appel englobant.
        };
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /**
     * Recherche la composition d'une salle connue.
     * @param id identifiant métier
     * @return plan visuel immuable
     */
    // Recherche le plan visuel correspondant à un identifiant de salle connu.
    public static PlanSalle de(String id) {
        // Initialise le plan visuel de la salle avec PLANS.get(id).
        PlanSalle plan = PLANS.get(id);
        // Si le plan visuel de la salle est égal à l’absence de valeur (null), refuse l’opération : "Salle visuelle inconnue.".
        if (plan == null) throw new IllegalArgumentException("Salle visuelle inconnue.");
        // Renvoie le plan visuel de la salle.
        return plan;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /**
     * Teste les murs et bases solides ; le coffre et l'autel ont une base dédiée.
     * @param x centre des pieds
     * @param y centre des pieds
     * @return possibilité de se tenir à ce point
     */
    // Vérifie que le point est à l’intérieur du sol et hors des bases solides.
    public boolean praticable(double x, double y) {
        // Si (x est inférieur à 104 ou x est supérieur à 856 ou y est inférieur à 192 ou y est supérieur à 472), renvoie faux.
        if (x < 104 || x > 856 || y < 192 || y > 472) return false;
        // Renvoie decors.stream().noneMatch(d -> d.solide() && d.obstacle().contains(x, y)).
        return decors.stream().noneMatch(d -> d.solide() && d.obstacle().contains(x, y));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
