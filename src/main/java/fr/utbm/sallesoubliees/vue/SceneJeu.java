package fr.utbm.sallesoubliees.vue;

import fr.utbm.sallesoubliees.modele.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.*;

/**
 * Scène en coordonnées logiques : déplacement, rencontre et horloge visuelle sur l'EDT.
 * Le timer ne tourne que pendant un mouvement, une présentation ou une séquence.
 * @serial exclude
 */
public final class SceneJeu extends JPanel {
    private final GestionnaireRessources ressources;
    private final RenduSalle renduSalle;
    private final RenduCombat renduCombat;
    private final ControleurAnimation animation = new ControleurAnimation();
    private final Consumer<String> commande;
    private final Runnable changement;
    private final javax.swing.Timer timer;
    private final Set<Integer> touches = new HashSet<>();
    private final Deque<Point2D.Double> trajet = new ArrayDeque<>();
    private Partie partie;
    private PlanSalle plan = PlanSalle.de("entree");
    private BufferedImage fondExploration;
    private BufferedImage fondCombat;
    private String id = "entree";
    private String destination;
    private String interactionAutomatique;
    private String survol = "";
    private double x = 410, y = 360;
    private int orientation = KeyEvent.VK_RIGHT;
    private long precedent;
    private double marche;
    private double immobilite;
    private double presentation;
    private boolean combat;
    private boolean suspendue;
    private boolean accueil = true;
    private Runnable finSequence;

    /**
     * Crée la scène sans charger d'image pendant ses frames.
     * @param ressources atlas déjà en mémoire
     * @param commande commandes vers la fenêtre
     * @param changement demande de rafraîchissement des contrôles
     */
    public SceneJeu(GestionnaireRessources ressources, Consumer<String> commande, Runnable changement) {
        this.ressources = ressources; this.commande = commande; this.changement = changement;
        renduSalle = new RenduSalle(ressources); renduCombat = new RenduCombat(ressources);
        fondExploration = renduSalle.fond(id, false); fondCombat = renduSalle.fond(id, true);
        setBackground(Theme.FOND); setFocusable(true); setPreferredSize(new Dimension(960, 540));
        setToolTipText("Flèches ou ZQSD : marcher · E / Entrée : interagir");
        timer = new javax.swing.Timer(16, e -> tick()); timer.setCoalesce(true);
        installerClavier();
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { requestFocusInWindow(); clic(point(e.getX(), e.getY())); }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                Point2D.Double p = point(e.getX(), e.getY()); String nouveau = cible(p);
                if (!nouveau.equals(survol)) { survol = nouveau; setCursor(Cursor.getPredefinedCursor(nouveau.isEmpty() ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR)); repaint(); }
            }
        });
        addFocusListener(new FocusAdapter() { @Override public void focusLost(FocusEvent e) { touches.clear(); } });
    }

    /**
     * Entre dans la salle réelle ; au chargement le héros revient au point central praticable.
     * @param nouvelle partie courante
     * @param provenance identifiant de la salle précédente, null au chargement
     */
    public void afficher(Partie nouvelle, String provenance) {
        arreter(); partie = nouvelle; accueil = false; id = nouvelle.getSalleActuelle().getId(); plan = PlanSalle.de(id);
        fondExploration = renduSalle.fond(id, false); fondCombat = renduSalle.fond(id, true);
        x = 425; y = 370;
        for (PlanSalle.Porte porte : plan.portes()) if (porte.destination().equals(provenance)) {
            x = porte.x() + (porte.cote() == 'O' ? 35 : porte.cote() == 'E' ? -35 : 0);
            y = porte.y() + (porte.cote() == 'N' ? 30 : porte.cote() == 'S' ? -25 : 0);
        }
        combat = !nouvelle.estEnCours(); presentation = nouvelle.estEnCombat() ? (animation.estReduite() ? 180 : 850) : 0;
        if (presentation > 0) demarrerTimer(); changement.run(); repaint();
    }
    /** Retourne au décor d'accueil sans laisser de timer actif. */
    public void accueil() { arreter(); partie = null; accueil = true; id = "entree"; plan = PlanSalle.de(id); fondExploration = renduSalle.fond(id, false); repaint(); }
    /**
     * Lit une action atomique déjà appliquée au modèle.
     * @param resultat événements ordonnés
     * @param fin notification unique de fin
     */
    public void jouer(ResultatAction resultat, Runnable fin) {
        if (estOccupe()) throw new IllegalStateException("La scène est occupée.");
        touches.clear(); trajet.clear(); animation.demarrer(resultat); finSequence = fin;
        demarrerTimer(); changement.run(); repaint();
    }
    /** {@return vrai tant qu'une animation ou un trajet empêche une nouvelle action} */
    public boolean estOccupe() { return presentation > 0 || animation.estActive() || !trajet.isEmpty(); }
    /** {@return vrai lorsque la vue latérale est affichée} */
    public boolean estCombatAffiche() { return combat; }
    /** {@return timer actif, utile pour vérifier son cycle de vie} */
    public boolean estTimerActif() { return timer.isRunning(); }
    /** {@return option d'animations réduites} */
    public boolean estReduite() { return animation.estReduite(); }
    /**
     * Change uniquement la vitesse de présentation.
     * @param valeur option utilisateur
     */
    public void setReduite(boolean valeur) { animation.setReduite(valeur); }
    /**
     * Gèle mouvements et horloge lorsqu'un panneau ou un dialogue masque la scène.
     * @param valeur panneau ouvert ou fenêtre inactive
     */
    public void suspendre(boolean valeur) { suspendue = valeur; touches.clear(); if (valeur) timer.stop(); else if (estOccupe()) demarrerTimer(); }
    /** Arrête tous les travaux visuels, en laissant intacte la résolution métier déjà terminée. */
    public void arreter() {
        timer.stop(); touches.clear(); trajet.clear(); destination = null; interactionAutomatique = null;
        animation.finirImmediatement(); finSequence = null; presentation = 0; suspendue = false;
    }
    /** Termine une séquence pour revenir à un point stable lors d'un abandon confirmé. */
    public void stabiliser() {
        animation.finirImmediatement(); presentation = 0; trajet.clear(); destination = null; interactionAutomatique = null;
        combat = partie != null && (partie.estEnCombat() || !partie.estEnCours());
        Runnable fin = finSequence; finSequence = null; timer.stop(); if (fin != null) fin.run(); repaint();
    }
    /**
     * Déclenche une marche vers une porte réellement adjacente.
     * @param salle destination demandée depuis la carte
     */
    public void marcherVersPorte(String salle) {
        if (partie == null || estOccupe() || suspendue || !partie.peutDeplacer(salle)) return;
        plan.portes().stream().filter(p -> p.destination().equals(salle)).findFirst().ifPresent(p -> {
            if (chemin(p.x(), p.y())) { destination = salle; demarrerTimer(); changement.run(); }
        });
    }
    /** Interaction de proximité, appelée par le clavier ou le bouton contextuel. */
    public void interagir() {
        if (partie == null || estOccupe() || suspendue || !partie.estEnCours() || combat) return;
        if (Math.hypot(x - 585, y - 337) < 88 && (partie.getSalleActuelle().getCoffre() != null || partie.getSalleActuelle().estRepos())) {
            commande.accept("objet"); return;
        }
        for (PlanSalle.Porte porte : plan.portes()) if (Math.hypot(x - porte.x(), y - porte.y()) < 65) {
            commande.accept("porte:" + porte.destination()); return;
        }
        commande.accept("message:Approchez une porte, le coffre ou l'autel pour interagir.");
    }
    /** Calcule une trajectoire à quatre voisins ; les bases des objets ne sont jamais traversées. */
    private boolean chemin(double cibleX, double cibleY) {
        trajet.clear(); touches.clear(); immobilite = 0;
        int cols = 49, lignes = 19;
        int sx = (int)Math.round((x - 104) / 16), sy = (int)Math.round((y - 184) / 16);
        int tx = (int)Math.round((cibleX - 104) / 16), ty = (int)Math.round((cibleY - 184) / 16);
        int debut = sy * cols + sx, fin = ty * cols + tx;
        Map<Integer, Integer> parents = new HashMap<>(); ArrayDeque<Integer> file = new ArrayDeque<>();
        parents.put(debut, debut); file.add(debut);
        while (!file.isEmpty() && !parents.containsKey(fin)) {
            int position = file.remove(); int cx = position % cols, cy = position / cols;
            for (int[] d : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                int nx = cx + d[0], ny = cy + d[1], n = ny * cols + nx;
                if (nx >= 0 && nx < cols && ny >= 0 && ny < lignes && !parents.containsKey(n) && praticable(104 + nx * 16, 184 + ny * 16)) {
                    parents.put(n, position); file.add(n);
                }
            }
        }
        if (!parents.containsKey(fin)) { commande.accept("message:Ce passage est inaccessible depuis cette position."); return false; }
        for (int n = fin; n != debut; n = parents.get(n)) trajet.addFirst(new Point2D.Double(104 + n % cols * 16, 184 + n / cols * 16));
        trajet.add(new Point2D.Double(cibleX, cibleY));
        return true;
    }
    /** Vérifie les obstacles fixes et l'objet interactif central. */
    private boolean praticable(double px, double py) {
        boolean objet = partie != null && (partie.getSalleActuelle().getCoffre() != null || partie.getSalleActuelle().estRepos());
        return plan.praticable(px, py) && !(objet && px > 550 && px < 620 && py > 284 && py < 317);
    }
    /** Démarre le seul timer de la scène et réinitialise son horloge monotone. */
    private void demarrerTimer() { if (!suspendue && !timer.isRunning()) { precedent = System.nanoTime(); timer.start(); } }
    /** Avance la présentation ; aucune opération lente et aucune règle de combat ici. */
    private void tick() {
        long maintenant = System.nanoTime(); double ms = Math.min(50, (maintenant - precedent) / 1_000_000.0); precedent = maintenant;
        if (presentation > 0) {
            presentation = Math.max(0, presentation - ms);
            if (presentation == 0) { combat = true; changement.run(); }
        } else if (animation.estActive()) {
            animation.avancer(ms);
            if (!animation.estActive()) {
                combat = partie.estEnCombat() || !partie.estEnCours();
                Runnable fin = finSequence; finSequence = null; if (fin != null) fin.run(); changement.run();
            }
        } else if (!combat && partie != null && partie.estEnCours()) {
            double dx = 0, dy = 0;
            if (!trajet.isEmpty()) { Point2D.Double p = trajet.peek(); dx = p.x - x; dy = p.y - y; }
            else {
                dx = (touches.contains(KeyEvent.VK_RIGHT) || touches.contains(KeyEvent.VK_D) ? 1 : 0) - (touches.contains(KeyEvent.VK_LEFT) || touches.contains(KeyEvent.VK_Q) ? 1 : 0);
                dy = (touches.contains(KeyEvent.VK_DOWN) || touches.contains(KeyEvent.VK_S) ? 1 : 0) - (touches.contains(KeyEvent.VK_UP) || touches.contains(KeyEvent.VK_Z) ? 1 : 0);
            }
            double distance = Math.hypot(dx, dy), pas = ms * .20;
            double ancienX = x, ancienY = y;
            if (distance > 0) {
                orientation = Math.abs(dx) > Math.abs(dy) ? dx > 0 ? KeyEvent.VK_RIGHT : KeyEvent.VK_LEFT : dy > 0 ? KeyEvent.VK_DOWN : KeyEvent.VK_UP;
                double facteur = !trajet.isEmpty() ? Math.min(pas, distance) / distance : pas / distance;
                if (praticable(x + dx * facteur, y)) x += dx * facteur;
                if (praticable(x, y + dy * facteur)) y += dy * facteur;
                marche += ms;
                if (!trajet.isEmpty() && Point2D.distance(x, y, trajet.peek().x, trajet.peek().y) < 2) {
                    Point2D.Double point = trajet.peek();
                    if (praticable(point.x, point.y)) { x = point.x; y = point.y; trajet.remove(); }
                }
            }
            if (!trajet.isEmpty()) {
                immobilite = Point2D.distance(ancienX, ancienY, x, y) < .001 ? immobilite + ms : 0;
                if (immobilite >= 500) {
                    trajet.clear(); destination = null; interactionAutomatique = null; changement.run();
                    commande.accept("message:Le trajet est bloqué. Déplacez-vous avec les flèches puis réessayez.");
                }
            }
            if (trajet.isEmpty() && destination != null) { String suivante = destination; destination = null; changement.run(); commande.accept("porte:" + suivante); }
            else if (trajet.isEmpty() && interactionAutomatique != null) { interactionAutomatique = null; changement.run(); interagir(); }
        }
        repaint(); if (!estOccupe() && touches.isEmpty()) timer.stop();
    }
    /** Convertit les pixels de fenêtre en coordonnées du monde, bandes latérales comprises. */
    private Point2D.Double point(int px, int py) {
        double e = Math.min(getWidth() / 960.0, getHeight() / 540.0);
        return new Point2D.Double((px - (getWidth() - 960 * e) / 2) / e, (py - (getHeight() - 540 * e) / 2) / e);
    }
    /** Renvoie l'interaction visée, sans effet métier. */
    private String cible(Point2D.Double p) {
        if (accueil || combat || estOccupe() || suspendue) return "";
        for (PlanSalle.Porte porte : plan.portes()) {
            int px = porte.cote() == 'O' ? 80 : porte.cote() == 'E' ? 880 : porte.x();
            int py = porte.cote() == 'N' ? 150 : porte.cote() == 'S' ? 490 : porte.y();
            if (Math.abs(p.x - px) < 48 && Math.abs(p.y - py) < 58) return porte.destination();
        }
        if (partie != null && (partie.getSalleActuelle().getCoffre() != null || partie.getSalleActuelle().estRepos()) && Math.abs(p.x - 585) < 60 && p.y > 205 && p.y < 340) return "objet";
        return "";
    }
    /** Lance la marche contextuelle vers une porte ou un objet. */
    private void clic(Point2D.Double p) {
        String cible = cible(p); if (cible.isEmpty()) return;
        if (cible.equals("objet")) { if (chemin(585, 345)) { interactionAutomatique = "objet"; demarrerTimer(); changement.run(); } }
        else marcherVersPorte(cible);
    }
    /** Installe des key bindings sans dépendre d'un KeyListener et de son focus fragile. */
    private void installerClavier() {
        for (int code : new int[]{KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_Z, KeyEvent.VK_Q, KeyEvent.VK_S, KeyEvent.VK_D}) {
            String appui = "appui" + code, relache = "relache" + code;
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(code, 0, false), appui);
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(code, 0, true), relache);
            getActionMap().put(appui, new AbstractAction() { @Override public void actionPerformed(ActionEvent e) {
                if (!accueil && !combat && !estOccupe() && !suspendue && partie.estEnCours()) { touches.add(code); demarrerTimer(); }
            }});
            getActionMap().put(relache, new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { touches.remove(code); } });
        }
        lier(KeyEvent.VK_E, "interaction"); lier(KeyEvent.VK_ENTER, "interaction");
        lier(KeyEvent.VK_I, "inventaire"); lier(KeyEvent.VK_M, "carte"); lier(KeyEvent.VK_J, "journal");
        lier(KeyEvent.VK_ESCAPE, "pause"); lier(KeyEvent.VK_1, "attaque"); lier(KeyEvent.VK_2, "defense"); lier(KeyEvent.VK_3, "potion");
    }
    /** Associe un raccourci à une intention de commande. */
    private void lier(int code, String action) {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(code, 0), action);
        getActionMap().put(action, new AbstractAction() { @Override public void actionPerformed(ActionEvent e) {
            if (action.equals("pause")) commande.accept(action);
            else if (!suspendue && !accueil) { if (action.equals("interaction")) interagir(); else commande.accept(action); }
        }});
    }
    /** Dessine le monde en conservant le ratio, sans aucune mutation métier. */
    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics); Graphics2D g = (Graphics2D)graphics.create();
        double echelle = Math.min(getWidth() / 960.0, getHeight() / 540.0);
        g.translate((getWidth() - 960 * echelle) / 2, (getHeight() - 540 * echelle) / 2); g.scale(echelle, echelle);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.drawImage(combat && !accueil ? fondCombat : fondExploration, 0, 0, null);
        if (accueil) {
            renduSalle.objets(g, plan, false, id);
            RenduSalle.lueur(g, 680, 365, 180, new Color(107, 205, 183), .18f);
            RenduCombat.ombre(g, 695, 467, 105); ressources.personnage(g, "heros", 0, 695, 467, 205, false);
        } else if (combat) {
            Ennemi ennemi = partie.getSalleActuelle().getEnnemi();
            renduCombat.dessiner(g, ennemi == null ? "boss" : ennemi.getType(), animation,
                    ennemi instanceof Boss && !ennemi.estMort() && ennemi.getVie() <= 45, !partie.estEnCours() && partie.getHeros().estMort() && !animation.estActive(),
                    ennemi != null && ennemi.estMort() && !animation.estActive());
        } else {
            for (PlanSalle.Porte porte : plan.portes()) renduSalle.porte(g, porte, porte.destination().equals(survol), partie.getDonjon().getSalle(porte.destination()).getNom());
            renduSalle.objets(g, plan, partie.getSalleActuelle().estUtilisee(), id, y, false);
            if (partie.getSalleActuelle().estHostile()) {
                ressources.personnage(g, partie.getSalleActuelle().getEnnemi().getType(), 0, 675, 355, 110, true);
            }
            RenduCombat.ombre(g, x, y, 44);
            int pose = timer.isRunning() && (!touches.isEmpty() || !trajet.isEmpty()) ? 1 + ((int)(marche / 130) % 2) : 0;
            int direction = switch (orientation) { case KeyEvent.VK_UP -> 1; case KeyEvent.VK_RIGHT -> 2; case KeyEvent.VK_LEFT -> 3; default -> 0; };
            ressources.marche(g, direction, pose, x, y);
            renduSalle.objets(g, plan, partie.getSalleActuelle().estUtilisee(), id, y, true);
            if (partie.getSalleActuelle().getCoffre() != null || partie.getSalleActuelle().estRepos()) {
                g.setColor(Theme.OR); g.setFont(Theme.CORPS.deriveFont(12f));
                g.drawString(partie.getSalleActuelle().estUtilisee() ? "Pouvoir épuisé" : "[E] Interagir", 549, 337);
                if (survol.equals("objet")) { g.setColor(new Color(239, 203, 126)); g.drawOval(544, 296, 82, 21); }
            }
        }
        if (!accueil && partie != null) hud(g);
        if (presentation > 0) {
            g.setColor(new Color(16, 19, 31, 195)); g.fillRoundRect(270, 200, 420, 70, 12, 12);
            g.setFont(Theme.CORPS.deriveFont(Font.BOLD, 18)); g.setColor(Theme.OR); g.drawString("UNE PRÉSENCE VOUS BARRE LA ROUTE", 293, 241);
        }
        g.dispose();
    }
    /** Affiche les PV de présentation et une mini-carte compacte consultative. */
    private void hud(Graphics2D g) {
        int vie = animation.estActive() ? animation.getVieHeros() : partie.getHeros().getVie();
        int potions = animation.estActive() ? animation.getPotions() : partie.getHeros().getInventaire().getPotions();
        g.setColor(new Color(15, 18, 31, 232)); g.fillRoundRect(25, 18, 245, 98, 12, 12);
        g.setColor(new Color(110, 103, 89)); g.drawRoundRect(25, 18, 245, 98, 12, 12);
        ressources.personnage(g, "heros", 0, 65, 102, 73, false);
        g.setFont(Theme.CORPS.deriveFont(Font.BOLD, 14)); g.setColor(Theme.TEXTE); g.drawString("L'EXPLORATEUR", 105, 44);
        barre(g, 105, 55, 145, vie, 100, Theme.VERT);
        g.setFont(Theme.CORPS.deriveFont(12f)); g.setColor(Theme.TEXTE); g.drawString(vie + " / 100 PV   ·   " + potions + " potions", 104, 90);
        if (combat && partie.getSalleActuelle().getEnnemi() != null) {
            Ennemi ennemi = partie.getSalleActuelle().getEnnemi(); int pv = animation.estActive() ? animation.getVieEnnemi() : ennemi.getVie();
            g.setColor(new Color(15, 18, 31, 232)); g.fillRoundRect(608, 18, 325, 100, 12, 12);
            g.setColor(Theme.OR); g.setFont(Theme.CORPS.deriveFont(Font.BOLD, 15)); g.drawString(ennemi.getNom().toUpperCase(Locale.ROOT), 625, 43);
            barre(g, 625, 56, 285, pv, ennemi.getVieMax(), Theme.ROUGE);
            g.setColor(Theme.TEXTE); g.setFont(Theme.CORPS.deriveFont(12f)); g.drawString(pv + " / " + ennemi.getVieMax() + " PV  ·  Armure " + ennemi.getArmure(), 625, 92);
        } else {
            g.setColor(new Color(15, 18, 31, 215)); g.fillRoundRect(766, 20, 165, 96, 10, 10);
            for (Salle s : partie.getDonjon().getSalles()) for (String voisin : s.getPassages()) {
                Salle v = partie.getDonjon().getSalle(voisin); g.setColor(new Color(92, 95, 115));
                g.drawLine(786 + s.getColonne() * 28, 39 + s.getLigne() * 26, 786 + v.getColonne() * 28, 39 + v.getLigne() * 26);
            }
            for (Salle s : partie.getDonjon().getSalles()) {
                g.setColor(s.getId().equals(id) ? Theme.OR : s.estVisitee() ? Theme.VERT : new Color(90, 95, 118));
                g.fillRect(780 + s.getColonne() * 28, 33 + s.getLigne() * 26, 12, 12);
            }
            g.setColor(Theme.TEXTE); g.setFont(Theme.CORPS.deriveFont(10f)); g.drawString("M  ·  Carte du donjon", 790, 108);
        }
        if (!partie.estEnCours() && !animation.estActive()) {
            g.setColor(new Color(12, 14, 27, 215)); g.fillRoundRect(258, 147, 444, 157, 16, 16);
            g.setColor(Theme.OR); g.setFont(Theme.CORPS.deriveFont(Font.BOLD, 42));
            String titre = partie.getEtat() == EtatPartie.VICTOIRE ? "LES SALLES S'ÉVEILLENT" : "DANS L'OUBLI";
            g.setFont(Theme.CORPS.deriveFont(Font.BOLD, partie.getEtat() == EtatPartie.VICTOIRE ? 29 : 38));
            g.drawString(titre, 480 - g.getFontMetrics().stringWidth(titre) / 2, 201);
            g.setFont(Theme.CORPS.deriveFont(16f)); g.setColor(Theme.TEXTE);
            String sousTitre = partie.getEtat() == EtatPartie.VICTOIRE ? "Victoire. Le serment du Gardien est rompu." : "Défaite. Un autre voyage vous attend.";
            g.drawString(sousTitre, 480 - g.getFontMetrics().stringWidth(sousTitre) / 2, 244);
            g.setFont(Theme.CORPS.deriveFont(12f)); g.drawString("Nouvelle partie ou chargement dans le menu de pause.", 324, 275);
        }
    }
    /** Dessine une jauge lisible et bornée. */
    private static void barre(Graphics2D g, int x, int y, int w, int vie, int max, Color couleur) {
        g.setColor(new Color(40, 39, 55)); g.fillRoundRect(x, y, w, 14, 6, 6);
        g.setColor(couleur); g.fillRoundRect(x, y, Math.max(0, w * vie / max), 14, 6, 6);
    }
}
