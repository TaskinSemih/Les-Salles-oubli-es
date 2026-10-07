package fr.utbm.sallesoubliees.vue;

import fr.utbm.sallesoubliees.modele.EvenementJeu;
import java.awt.*;

/** Mise en scène latérale : poses dessinées, fentes, projectile, impact et nombres flottants. */
final class RenduCombat {
    private final GestionnaireRessources ressources;
    /** Réutilise le cache des illustrations. */
    RenduCombat(GestionnaireRessources ressources) { this.ressources = ressources; }

    /** Dessine un instant de combat à partir des seuls faits et valeurs de présentation. */
    void dessiner(Graphics2D g, String type, ControleurAnimation animation, boolean rageStable, boolean herosMort, boolean ennemiMort) {
        EvenementJeu e = animation.courant(); double p = animation.progression();
        boolean heros = e != null && e.cible() == EvenementJeu.Cible.HEROS;
        int poseH = herosMort ? 5 : animation.estDefense() ? 4 : 0, poseE = ennemiMort ? 5 : 0;
        double xH = 285, xE = 695, yH = 446, yE = 446;
        float alphaH = herosMort ? .2f : 1, alphaE = ennemiMort ? 0 : 1;
        if (e != null) {
            switch (e.type()) {
                case ATTAQUE -> {
                    if (heros) { poseH = 3; if (!animation.estReduite()) xH += 250 * Math.sin(p * Math.PI); }
                    else { poseE = 3; if (!animation.estReduite() && !type.equals("mage")) xE -= 240 * Math.sin(p * Math.PI); }
                }
                case DEGATS -> { if (heros) { poseH = 5; xH -= Math.sin(p * Math.PI) * 12; } else { poseE = 5; xE += Math.sin(p * Math.PI) * 12; } }
                case PREPARATION, RAGE -> poseE = 4;
                case DEFENSE -> poseH = 4;
                case SOIN -> poseH = 4;
                case MORT -> { if (heros) { poseH = 5; alphaH = (float)(1 - p * .8); yH += p * 12; } else { poseE = 5; alphaE = (float)(1 - p); yE += p * 15; } }
                default -> { }
            }
        }
        if (animation.estActive() && e.type() != EvenementJeu.Type.MORT) {
            if (animation.estMortEnnemi()) alphaE = 0;
            if (animation.estMortHeros()) alphaH = .2f;
        }
        boolean rage = animation.estActive() ? animation.estRage() : rageStable;
        if (rage && type.equals("boss")) {
            RenduSalle.lueur(g, xE, yE - 85, 150, new Color(255, 66, 110), .45f);
            g.setColor(new Color(255, 154, 132)); g.setFont(Theme.CORPS.deriveFont(Font.BOLD, 15)); g.drawString("GARDIEN ENRAGÉ", 625, 207);
        }
        ombre(g, xH, yH, 95);
        if (alphaE > 0) ombre(g, xE, yE, type.equals("boss") || type.equals("golem") ? 130 : 95);
        Composite avant = g.getComposite(); g.setComposite(AlphaComposite.SrcOver.derive(alphaH));
        ressources.personnage(g, "heros", poseH, xH, yH, 160, false);
        g.setComposite(AlphaComposite.SrcOver.derive(alphaE));
        // Les poses d'attaque générées sont orientées à droite ; retournées vers le héros.
        ressources.personnage(g, type, poseE, xE, yE, type.equals("boss") ? 205 : type.equals("golem") ? 182 : 160, poseE == 3);
        g.setComposite(avant);
        if (animation.estDefense()) { RenduSalle.lueur(g, xH, 355, 85, new Color(119, 197, 247), .25f); ressources.objet(g, 15, (int)xH - 65, 367, 48); }
        if (e != null) effets(g, e, p, xH, xE, type);
    }

    /** Effets courts qui complètent les poses plutôt que de remplacer les dessins. */
    private void effets(Graphics2D g, EvenementJeu e, double p, double xH, double xE, String type) {
        double cible = e.cible() == EvenementJeu.Cible.HEROS ? xH : xE;
        if (e.type() == EvenementJeu.Type.ATTAQUE && e.cible() == EvenementJeu.Cible.ENNEMI && type.equals("mage")) {
            double x = xE - (xE - xH) * p;
            RenduSalle.lueur(g, x, 332, 45, new Color(97, 240, 211), .7f);
            g.setColor(new Color(135, 255, 234)); g.fillOval((int)x - 8, 324, 16, 16);
        }
        if (e.type() == EvenementJeu.Type.DEGATS || e.type() == EvenementJeu.Type.SOIN) {
            boolean soin = e.type() == EvenementJeu.Type.SOIN;
            Color couleur = soin ? new Color(119, 246, 201) : new Color(255, 141, 132);
            RenduSalle.lueur(g, cible, 350, 95, couleur, (float)(.25 * (1 - p)));
            g.setStroke(new BasicStroke(3)); g.setColor(couleur);
            for (int i = 0; i < 6; i++) {
                double a = i * Math.PI / 3; int r = 15 + (int)(p * 35);
                int x = (int)cible + (int)(Math.cos(a) * r), y = 345 + (int)(Math.sin(a) * r);
                if (soin) { g.drawLine(x - 4, y, x + 4, y); g.drawLine(x, y - 4, x, y + 4); }
                else g.drawLine(x, y, x + (int)(Math.cos(a) * 16), y + (int)(Math.sin(a) * 16));
            }
            String texte = (soin ? "+" : "−") + e.valeur();
            g.setFont(Theme.CORPS.deriveFont(Font.BOLD, 31)); int tx = (int)cible - g.getFontMetrics().stringWidth(texte) / 2;
            int ty = 260 - (int)(p * 35); g.setColor(new Color(12, 13, 23)); g.drawString(texte, tx + 2, ty + 3);
            g.setColor(couleur); g.drawString(texte, tx, ty);
        }
    }
    /** Ombre des pieds, stable et séparée du sprite transparent. */
    static void ombre(Graphics2D g, double x, double y, int largeur) { g.setColor(new Color(10, 10, 24, 135)); g.fillOval((int)x - largeur / 2, (int)y - 9, largeur, 19); }
}
