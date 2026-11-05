// Place cette classe dans la vue, responsable de la présentation Swing et des dessins.
package fr.utbm.sallesoubliees.vue;

// Rend disponible EvenementJeu depuis fr.utbm.sallesoubliees.modele.
import fr.utbm.sallesoubliees.modele.EvenementJeu;
// Rend disponibles les types ou constantes de java.awt sans répéter leur nom complet.
import java.awt.*;

/** Mise en scène latérale : poses dessinées, fentes, projectile, impact et nombres flottants. */
// Déclare RenduCombat, dessin du combat latéral à partir des événements et de leur progression visuelle. final interdit de créer une sous-classe.
final class RenduCombat {
    // Déclare le cache d'images de type GestionnaireRessources.
    private final GestionnaireRessources ressources;
    /** Réutilise le cache des illustrations. */
    // Construit un objet RenduCombat : dessin du combat latéral à partir des événements et de leur progression visuelle. Met à jour le cache d'images avec le cache d'images.
    RenduCombat(GestionnaireRessources ressources) { this.ressources = ressources; }

    /** Dessine un instant de combat à partir des seuls faits et valeurs de présentation. */
    // Dessine un instant du combat avec les poses et effets correspondant aux faits déjà calculés.
    void dessiner(Graphics2D g, String type, ControleurAnimation animation, boolean rageStable, boolean herosMort, boolean ennemiMort, boolean garde) {
        // Initialise la variable e de type EvenementJeu avec le fait visuel courant du lecteur d'événements visuels. Initialise la variable p de type double avec la progression visuelle entre zéro et un du lecteur d'événements visuels.
        EvenementJeu e = animation.courant(); double p = animation.progression();
        // Initialise le héros avec e != null && e.cible() == EvenementJeu.Cible.HEROS.
        boolean heros = e != null && e.cible() == EvenementJeu.Cible.HEROS;
        // Initialise « pose h » avec herosMort ? 5 : animation.estDefense() ? 4 : 0, poseE = ennemiMort ? 5 : garde ? 4 : 0.
        int poseH = herosMort ? 5 : animation.estDefense() ? 4 : 0, poseE = ennemiMort ? 5 : garde ? 4 : 0;
        // Initialise « x h » avec 285, xE = 695, yH = 446, yE = 446.
        double xH = 285, xE = 695, yH = 446, yE = 446;
        // Initialise « alpha h » avec herosMort ? .2f : 1, alphaE = ennemiMort ? 0 : 1.
        float alphaH = herosMort ? .2f : 1, alphaE = ennemiMort ? 0 : 1;
        // Si la variable e est différent de null, entre dans ce traitement conditionnel.
        if (e != null) {
            // Sélectionne le traitement correspondant à la valeur examinée dans le switch.
            switch (e.type()) {
                // Pour ATTAQUE : ouvre le traitement de cette branche.
                case ATTAQUE -> {
                    // L’attaque du héros choisit la pose 3 et, hors mode réduit, effectue une fente sinusoïdale vers l’ennemi.
                    if (heros) { poseH = 3; if (!animation.estReduite()) xH += 250 * Math.sin(p * Math.PI); }
                    // L’ennemi choisit sa pose d’attaque ; il avance hors mode réduit, sauf le mage qui lance un projectile à distance.
                    else { poseE = 3; if (!animation.estReduite() && !type.equals("mage")) xE -= 240 * Math.sin(p * Math.PI); }
                // Ferme le bloc ou la liste de données commencé plus haut.
                }
                // Les dégâts choisissent la pose blessée de la cible et un léger recul sinusoïdal.
                case DEGATS -> { if (heros) { poseH = 5; xH -= Math.sin(p * Math.PI) * 12; } else { poseE = 5; xE += Math.sin(p * Math.PI) * 12; } }
                // Pour PREPARATION, RAGE : Met à jour « pose e » avec 4.
                case PREPARATION, RAGE -> poseE = 4;
                // Pour DEFENSE : Met à jour « pose h » avec 4.
                case DEFENSE -> poseH = 4;
                // Le soin choisit la pose 4 du personnage concerné.
                case SOIN -> { if (heros) poseH = 4; else poseE = 4; }
                // Pour CONTRE_ATTAQUE : Met à jour « pose e » avec 3.
                case CONTRE_ATTAQUE -> poseE = 3;
                // Un effet, une observation ou un gain d’élan choisit la pose 4 du personnage concerné.
                case EFFET, OBSERVATION, ELAN -> { if (heros) poseH = 4; else poseE = 4; }
                // Pour INTERRUPTION, BRISE_GARDE : Met à jour « pose e » avec 5.
                case INTERRUPTION, BRISE_GARDE -> poseE = 5;
                // La mort choisit la pose blessée et fait descendre et disparaître progressivement le personnage ; le héros garde une opacité de 20 %.
                case MORT -> { if (heros) { poseH = 5; alphaH = (float)(1 - p * .8); yH += p * 12; } else { poseE = 5; alphaE = (float)(1 - p); yE += p * 15; } }
                // Pour les autres cas : aucun traitement particulier n’est nécessaire.
                default -> { }
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Si (si la séquence visuelle est en cours du lecteur d'événements visuels et le type de la variable e est différent de EvenementJeu.Type.MORT), entre dans ce traitement conditionnel.
        if (animation.estActive() && e.type() != EvenementJeu.Type.MORT) {
            // Si si la mort ennemie a été présentée du lecteur d'événements visuels, Met à jour « alpha e » avec 0.
            if (animation.estMortEnnemi()) alphaE = 0;
            // Si si la mort du héros a été présentée du lecteur d'événements visuels, Met à jour « alpha h » avec .2f.
            if (animation.estMortHeros()) alphaH = .2f;
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Initialise le marqueur de rage du Gardien avec animation.estActive() ? animation.estRage() : rageStable.
        boolean rage = animation.estActive() ? animation.estRage() : rageStable;
        // Si (le marqueur de rage du Gardien et type.equals("boss")), entre dans ce traitement conditionnel.
        if (rage && type.equals("boss")) {
            // Dessine un halo lumineux translucide ; arguments : g, xE, yE - 85, 150, new Color(255, 66, 110), .45f.
            RenduSalle.lueur(g, xE, yE - 85, 150, new Color(255, 66, 110), .45f);
            // Choisit la couleur du prochain dessin ; arguments : new Color(255, 154, 132). Définit la police de caractères ; arguments : Theme.CORPS.deriveFont(Font.BOLD, 15). Dessine le texte à la position calculée ; arguments : "GARDIEN ENRAGÉ", 625, 207.
            g.setColor(new Color(255, 154, 132)); g.setFont(Theme.CORPS.deriveFont(Font.BOLD, 15)); g.drawString("GARDIEN ENRAGÉ", 625, 207);
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Dessine l’ombre au sol du personnage ; arguments : g, xH, yH, 95.
        ombre(g, xH, yH, 95);
        // Si alphaE est supérieur à 0, Dessine l’ombre au sol du personnage ; arguments : g, xE, yE, type.equals("boss") || type.equals("golem") ? 130 : 95.
        if (alphaE > 0) ombre(g, xE, yE, type.equals("boss") || type.equals("golem") ? 130 : 95);
        // Initialise l'instantané avant l'action avec g.getComposite(). Règle la transparence et la composition des dessins ; arguments : AlphaComposite.SrcOver.derive(alphaH).
        Composite avant = g.getComposite(); g.setComposite(AlphaComposite.SrcOver.derive(alphaH));
        // Dessine la pose du personnage à partir de l’atlas en mémoire ; arguments : g, "heros", poseH, xH, yH, 160, false.
        ressources.personnage(g, "heros", poseH, xH, yH, 160, false);
        // Règle la transparence et la composition des dessins ; arguments : AlphaComposite.SrcOver.derive(alphaE).
        g.setComposite(AlphaComposite.SrcOver.derive(alphaE));
        // Les poses d'attaque générées sont orientées à droite ; retournées vers le héros.
        // Dessine la pose du personnage à partir de l’atlas en mémoire ; arguments : g, type, poseE, xE, yE, type.equals("boss") ? 205 : type.equals("golem") ? 182 : 160, poseE == 3.
        ressources.personnage(g, type, poseE, xE, yE, type.equals("boss") ? 205 : type.equals("golem") ? 182 : 160, poseE == 3);
        // Règle la transparence et la composition des dessins ; arguments : avant.
        g.setComposite(avant);
        // Si si la défense est affichée du lecteur d'événements visuels, Dessine un halo lumineux translucide ; arguments : g, xH, 355, 85, new Color(119, 197, 247), .25f. Dessine un objet à partir de l’atlas en mémoire ; arguments : g, 15, (int)xH - 65, 367, 48.
        if (animation.estDefense()) { RenduSalle.lueur(g, xH, 355, 85, new Color(119, 197, 247), .25f); ressources.objet(g, 15, (int)xH - 65, 367, 48); }
        // Si la variable e est différent de null, Dessine les effets et les textes du fait en cours sans appliquer de règle métier. Paramètres : g, e, p, xH, xE, type.
        if (e != null) effets(g, e, p, xH, xE, type);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    /** Effets courts qui complètent les poses plutôt que de remplacer les dessins. */
    // Dessine les effets et les textes du fait en cours sans appliquer de règle métier.
    private void effets(Graphics2D g, EvenementJeu e, double p, double xH, double xE, String type) {
        // Initialise la cible avec e.cible() == EvenementJeu.Cible.HEROS ? xH : xE.
        double cible = e.cible() == EvenementJeu.Cible.HEROS ? xH : xE;
        // Poursuit la condition de contrôle sur les lignes suivantes.
        if (e.type() == EvenementJeu.Type.EFFET || e.type() == EvenementJeu.Type.INTERRUPTION
                // Inclut les faits de garde brisée et d’élan parmi ceux qui reçoivent un texte flottant.
                || e.type() == EvenementJeu.Type.BRISE_GARDE || e.type() == EvenementJeu.Type.ELAN
                // Inclut l’observation et le changement de phase dans ces effets de texte.
                || e.type() == EvenementJeu.Type.OBSERVATION || e.type() == EvenementJeu.Type.PHASE
                // Inclut aussi la contre-attaque et ouvre le dessin de son message.
                || e.type() == EvenementJeu.Type.CONTRE_ATTAQUE) {
            // Initialise le texte à afficher avec switch (e.type()) {.
            String message = switch (e.type()) {
                // Retrouve le nom de l’effet à partir de son indice et remplace les soulignements par des espaces.
                case EFFET -> fr.utbm.sallesoubliees.modele.EffetCombat.Type.values()[e.valeur()].name().replace('_', ' ');
                // Pour ELAN : fournit le texte "ÉLAN " + e.valeur() + "/3".
                case ELAN -> "ÉLAN " + e.valeur() + "/3";
                // Pour PHASE : fournit le texte "PHASE " + e.valeur().
                case PHASE -> "PHASE " + e.valeur();
                // Pour BRISE_GARDE : fournit le texte "GARDE BRISÉE".
                case BRISE_GARDE -> "GARDE BRISÉE";
                // Pour CONTRE_ATTAQUE : fournit le texte "RIPOSTE".
                case CONTRE_ATTAQUE -> "RIPOSTE";
                // Pour OBSERVATION : fournit le texte "INTENTION RÉVÉLÉE".
                case OBSERVATION -> "INTENTION RÉVÉLÉE";
                // Pour les autres cas : fournit le texte "INTERROMPU".
                default -> "INTERROMPU";
            // Ferme le bloc et termine l’instruction ou l’appel englobant.
            };
            // Dessine un halo lumineux translucide ; arguments : g, cible, 350, 90, new Color(130, 206, 240), (float)(.35 * (1 - p)).
            RenduSalle.lueur(g, cible, 350, 90, new Color(130, 206, 240), (float)(.35 * (1 - p)));
            // Choisit la couleur du prochain dessin ; arguments : new Color(239, 203, 126). Définit la police de caractères ; arguments : Theme.CORPS.deriveFont(Font.BOLD, 18).
            g.setColor(new Color(239, 203, 126)); g.setFont(Theme.CORPS.deriveFont(Font.BOLD, 18));
            // Dessine le texte à la position calculée ; arguments : message, (int)cible - g.getFontMetrics().stringWidth(message) / 2, 260 - (int)(p * 20).
            g.drawString(message, (int)cible - g.getFontMetrics().stringWidth(message) / 2, 260 - (int)(p * 20));
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Si (le type de la variable e est égal à EvenementJeu.Type.ATTAQUE et la cible de la variable e est égal à EvenementJeu.Cible.ENNEMI et type.equals("mage")), entre dans ce traitement conditionnel.
        if (e.type() == EvenementJeu.Type.ATTAQUE && e.cible() == EvenementJeu.Cible.ENNEMI && type.equals("mage")) {
            // Initialise « x » avec xE - (xE - xH) * p.
            double x = xE - (xE - xH) * p;
            // Dessine un halo lumineux translucide ; arguments : g, x, 332, 45, new Color(97, 240, 211), .7f.
            RenduSalle.lueur(g, x, 332, 45, new Color(97, 240, 211), .7f);
            // Choisit la couleur du prochain dessin ; arguments : new Color(135, 255, 234). Remplit une ellipse ; arguments : (int)x - 8, 324, 16, 16.
            g.setColor(new Color(135, 255, 234)); g.fillOval((int)x - 8, 324, 16, 16);
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Si (le type de la variable e est égal à EvenementJeu.Type.DEGATS ou le type de la variable e est égal à EvenementJeu.Type.SOIN), entre dans ce traitement conditionnel.
        if (e.type() == EvenementJeu.Type.DEGATS || e.type() == EvenementJeu.Type.SOIN) {
            // Initialise « soin » avec e.type() == EvenementJeu.Type.SOIN.
            boolean soin = e.type() == EvenementJeu.Type.SOIN;
            // Initialise « couleur » avec soin ? new Color(119, 246, 201) : new Color(255, 141, 132).
            Color couleur = soin ? new Color(119, 246, 201) : new Color(255, 141, 132);
            // Dessine un halo lumineux translucide ; arguments : g, cible, 350, 95, couleur, (float)(.25 * (1 - p)).
            RenduSalle.lueur(g, cible, 350, 95, couleur, (float)(.25 * (1 - p)));
            // Choisit l’épaisseur des contours ; arguments : new BasicStroke(3). Choisit la couleur du prochain dessin ; arguments : couleur.
            g.setStroke(new BasicStroke(3)); g.setColor(couleur);
            // Répète le traitement selon l’initialisation, la condition et l’avancement de cette boucle : int i = 0; i < 6; i++.
            for (int i = 0; i < 6; i++) {
                // Initialise la variable a de type double avec i * Math.PI / 3. Initialise la variable r de type int avec 15 + (int)(p * 35).
                double a = i * Math.PI / 3; int r = 15 + (int)(p * 35);
                // Initialise « x » avec (int)cible + (int)(Math.cos(a) * r), y = 345 + (int)(Math.sin(a) * r).
                int x = (int)cible + (int)(Math.cos(a) * r), y = 345 + (int)(Math.sin(a) * r);
                // Si soin, Trace un segment entre les deux points indiqués ; arguments : x - 4, y, x + 4, y. Trace un segment entre les deux points indiqués ; arguments : x, y - 4, x, y + 4.
                if (soin) { g.drawLine(x - 4, y, x + 4, y); g.drawLine(x, y - 4, x, y + 4); }
                // Dans le cas contraire, Trace un segment entre les deux points indiqués ; arguments : x, y, x + (int)(Math.cos(a) * 16), y + (int)(Math.sin(a) * 16).
                else g.drawLine(x, y, x + (int)(Math.cos(a) * 16), y + (int)(Math.sin(a) * 16));
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
            // Initialise « texte » avec (soin ? "+" : "−") + e.valeur().
            String texte = (soin ? "+" : "−") + e.valeur();
            // Définit la police de caractères ; arguments : Theme.CORPS.deriveFont(Font.BOLD, 31). Initialise « tx » avec (int)cible - g.getFontMetrics().stringWidth(texte) / 2.
            g.setFont(Theme.CORPS.deriveFont(Font.BOLD, 31)); int tx = (int)cible - g.getFontMetrics().stringWidth(texte) / 2;
            // Initialise « ty » avec 260 - (int)(p * 35). Choisit la couleur du prochain dessin ; arguments : new Color(12, 13, 23). Dessine le texte à la position calculée ; arguments : texte, tx + 2, ty + 3.
            int ty = 260 - (int)(p * 35); g.setColor(new Color(12, 13, 23)); g.drawString(texte, tx + 2, ty + 3);
            // Choisit la couleur du prochain dessin ; arguments : couleur. Dessine le texte à la position calculée ; arguments : texte, tx, ty.
            g.setColor(couleur); g.drawString(texte, tx, ty);
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Ombre des pieds, stable et séparée du sprite transparent. */
    // Dessine une ellipse sombre au niveau des pieds du personnage. Choisit la couleur du prochain dessin ; arguments : new Color(10, 10, 24, 135). Remplit une ellipse ; arguments : (int)x - largeur / 2, (int)y - 9, largeur, 19.
    static void ombre(Graphics2D g, double x, double y, int largeur) { g.setColor(new Color(10, 10, 24, 135)); g.fillOval((int)x - largeur / 2, (int)y - 9, largeur, 19); }
// Ferme le bloc ou la liste de données commencé plus haut.
}
