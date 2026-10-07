package fr.utbm.sallesoubliees.vue;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;

/**
 * Composition visuelle explicite des neuf salles ; ne remplace jamais le graphe métier.
 * @param sousTitre ambiance de la salle
 * @param lumiere couleur d'accent
 * @param portes passages visuels conformes au graphe
 * @param decors objets disposés dans l'espace
 */
public record PlanSalle(String sousTitre, Color lumiere, List<Porte> portes, List<Decor> decors) {
    /** Copie les listes pour empêcher une modification accidentelle du décor. */
    public PlanSalle { portes = List.copyOf(portes); decors = List.copyOf(decors); }

    /**
     * Porte et point d'approche dans les coordonnées logiques.
     * @param destination identifiant métier exact
     * @param cote N, S, E ou O
     * @param x approche du héros
     * @param y approche du héros
     */
    public record Porte(String destination, char cote, int x, int y) { }
    /**
     * Objet de décor, dont seule la base peut bloquer le déplacement.
     * @param objet index de l'atlas
     * @param x centre
     * @param y pieds
     * @param hauteur hauteur dessinée
     * @param solide collision à la base
     */
    public record Decor(int objet, int x, int y, int hauteur, boolean solide) {
        /** {@return rectangle de collision au sol, indépendant de la hauteur du dessin} */
        public Rectangle2D obstacle() { return new Rectangle2D.Double(x - 31, y - 18, 62, 30); }
    }

    private static final Color AMBRE = new Color(235, 167, 75);
    private static final Color TURQUOISE = new Color(69, 197, 191);
    private static final Color VIOLET = new Color(157, 109, 217);
    private static final Map<String, PlanSalle> PLANS = Map.ofEntries(
        Map.entry("entree", new PlanSalle("Le seuil des oubliés", AMBRE,
                List.of(porte("ossuaire", 'E')), List.of(d(4, 205, 295, 125), d(13, 280, 430, 48), d(3, 730, 230, 120), d(13, 760, 460, 45)))),
        Map.entry("ossuaire", new PlanSalle("Les pierres ont une mémoire", VIOLET,
                List.of(porte("entree", 'O'), porte("arsenal", 'E'), porte("forge", 'S')),
                List.of(d(5, 250, 245, 75), d(5, 720, 460, 65), d(3, 730, 220, 130), d(13, 240, 450, 50)))),
        Map.entry("arsenal", new PlanSalle("L'acier attend son porteur", AMBRE,
                List.of(porte("ossuaire", 'O'), porte("repos", 'S')),
                List.of(d(6, 240, 260, 110), d(6, 740, 250, 110), d(15, 250, 440, 70)))),
        Map.entry("forge", new PlanSalle("La braise sous la cendre", new Color(234, 105, 58),
                List.of(porte("ossuaire", 'N'), porte("repos", 'E')),
                List.of(d(7, 260, 265, 140), d(8, 680, 260, 85), d(13, 730, 450, 50), d(8, 270, 440, 80)))),
        Map.entry("repos", new PlanSalle("Une dernière lueur de paix", TURQUOISE,
                List.of(porte("arsenal", 'N'), porte("forge", 'O'), porte("bibliotheque", 'E')),
                List.of(d(3, 240, 250, 120), d(3, 740, 250, 120), d(11, 480, 390, 90)))),
        Map.entry("bibliotheque", new PlanSalle("Les mots que nul ne doit lire", VIOLET,
                List.of(porte("repos", 'O'), porte("antichambre", 'S')),
                List.of(d(10, 245, 260, 135), d(10, 700, 250, 135), d(10, 760, 440, 110), d(11, 300, 425, 70)))),
        Map.entry("tresor", new PlanSalle("L'or ne protège pas des ombres", AMBRE,
                List.of(porte("antichambre", 'E')),
                List.of(d(12, 240, 260, 75), d(12, 730, 450, 75), d(3, 720, 250, 120), d(12, 260, 430, 65)))),
        Map.entry("antichambre", new PlanSalle("Le silence avant le Gardien", VIOLET,
                List.of(porte("bibliotheque", 'N'), porte("tresor", 'O'), porte("boss", 'E')),
                List.of(d(3, 260, 255, 145), d(3, 720, 255, 145), d(3, 260, 445, 125), d(3, 720, 445, 125), d(11, 480, 325, 75)))),
        Map.entry("boss", new PlanSalle("Le serment brisé", new Color(186, 74, 112),
                List.of(porte("antichambre", 'O')),
                List.of(d(3, 245, 250, 150), d(3, 745, 250, 150), d(11, 630, 410, 135), d(5, 240, 450, 55))))
    );

    /** Crée un objet solide, sauf les runes et petits débris. */
    private static Decor d(int objet, int x, int y, int hauteur) { return new Decor(objet, x, y, hauteur, objet != 11 && objet != 13 && objet != 5 && objet != 12); }
    /** Crée une porte avec un point de marche situé à l'intérieur du sol. */
    private static Porte porte(String destination, char cote) {
        return switch (cote) {
            case 'O' -> new Porte(destination, cote, 111, 340);
            case 'E' -> new Porte(destination, cote, 849, 340);
            case 'N' -> new Porte(destination, cote, 480, 200);
            default -> new Porte(destination, cote, 480, 466);
        };
    }
    /**
     * Recherche la composition d'une salle connue.
     * @param id identifiant métier
     * @return plan visuel immuable
     */
    public static PlanSalle de(String id) {
        PlanSalle plan = PLANS.get(id);
        if (plan == null) throw new IllegalArgumentException("Salle visuelle inconnue.");
        return plan;
    }
    /**
     * Teste les murs et bases solides ; le coffre et l'autel ont une base dédiée.
     * @param x centre des pieds
     * @param y centre des pieds
     * @return possibilité de se tenir à ce point
     */
    public boolean praticable(double x, double y) {
        if (x < 104 || x > 856 || y < 192 || y > 472) return false;
        return decors.stream().noneMatch(d -> d.solide() && d.obstacle().contains(x, y));
    }
}
