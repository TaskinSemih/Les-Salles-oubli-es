// Place cette classe dans la vue, responsable de la présentation Swing et des dessins.
package fr.utbm.sallesoubliees.vue;

// Rend disponibles les types ou constantes de fr.utbm.sallesoubliees.modele sans répéter leur nom complet.
import fr.utbm.sallesoubliees.modele.*;
// Rend disponibles les types ou constantes de java.awt sans répéter leur nom complet.
import java.awt.*;
// Rend disponibles les types ou constantes de java.awt.event sans répéter leur nom complet.
import java.awt.event.*;
// Rend disponible Point2D depuis java.awt.geom.
import java.awt.geom.Point2D;
// Rend disponible BufferedImage depuis java.awt.image.
import java.awt.image.BufferedImage;
// Rend disponibles les types ou constantes de java.util sans répéter leur nom complet.
import java.util.*;
// Rend disponible List depuis java.util.
import java.util.List;
// Rend disponible Consumer depuis java.util.function.
import java.util.function.Consumer;
// Rend disponibles les types ou constantes de javax.swing sans répéter leur nom complet.
import javax.swing.*;

/**
 * Scène en coordonnées logiques : déplacement, rencontre et horloge visuelle sur l'EDT.
 * Le timer ne tourne que pendant un mouvement, une présentation ou une séquence.
 * @serial exclude
 */
// Déclare SceneJeu, scène Swing : marche, collisions, interactions, raccourcis et horloge de présentation. Hérite de JPanel. final interdit de créer une sous-classe.
public final class SceneJeu extends JPanel {
    // Déclare le cache d'images de type GestionnaireRessources.
    private final GestionnaireRessources ressources;
    // Déclare le dessinateur des salles de type RenduSalle.
    private final RenduSalle renduSalle;
    // Déclare le dessinateur des combats de type RenduCombat.
    private final RenduCombat renduCombat;
    // Initialise le lecteur d'événements visuels avec un nouvel objet ControleurAnimation.
    private final ControleurAnimation animation = new ControleurAnimation();
    // Déclare le rappel transmettant une commande de type Consumer<String>.
    private final Consumer<String> commande;
    // Déclare le rappel de rafraîchissement des commandes de type Runnable.
    private final Runnable changement;
    // Déclare l'horloge Swing de la scène de type javax.swing.Timer.
    private final javax.swing.Timer timer;
    // Initialise les touches maintenues enfoncées avec une nouvelle collection indépendante et vide.
    private final Set<Integer> touches = new HashSet<>();
    // Initialise les points de la marche automatique avec une nouvelle collection indépendante et vide.
    private final Deque<Point2D.Double> trajet = new ArrayDeque<>();
    // Déclare la partie courante de type Partie.
    private Partie partie;
    // Initialise le plan visuel de la salle avec PlanSalle.de("entree").
    private PlanSalle plan = PlanSalle.de("entree");
    // Déclare le fond précalculé d'exploration de type BufferedImage.
    private BufferedImage fondExploration;
    // Déclare le fond précalculé du combat de type BufferedImage.
    private BufferedImage fondCombat;
    // Initialise l'identifiant de salle avec le texte "entree".
    private String id = "entree";
    // Déclare la salle à rejoindre après la marche de type String.
    private String destination;
    // Déclare l'interaction à effectuer à l'arrivée de type String.
    private String interactionAutomatique;
    // Initialise l'élément sous la souris avec le texte "".
    private String survol = "";
    // Initialise « x » avec 410, y = 360.
    private double x = 410, y = 360;
    // Initialise la direction du héros avec KeyEvent.VK_RIGHT.
    private int orientation = KeyEvent.VK_RIGHT;
    // Déclare l'instant de la dernière mise à jour de type long.
    private long precedent;
    // Déclare les images ou le temps de marche de type double.
    private double marche;
    // Déclare le temps sans déplacement sur un trajet de type double.
    private double immobilite;
    // Déclare le temps restant de présentation du combat de type double.
    private double presentation;
    // Déclare le mode de présentation du combat de type boolean.
    private boolean combat;
    // Déclare le marqueur de suspension visuelle de type boolean.
    private boolean suspendue;
    // Initialise le marqueur d'affichage de l'accueil avec vrai.
    private boolean accueil = true;
    // Déclare le rappel de fin de séquence de type Runnable.
    private Runnable finSequence;

    /**
     * Crée la scène sans charger d'image pendant ses frames.
     *
 * @param ressources atlas déjà en mémoire
     *
 * @param commande commandes vers la fenêtre
     *
 * @param changement demande de rafraîchissement des contrôles
     */
    // Construit un objet SceneJeu : scène Swing : marche, collisions, interactions, raccourcis et horloge de présentation.
    public SceneJeu(GestionnaireRessources ressources, Consumer<String> commande, Runnable changement) {
        // Met à jour le cache d'images avec le cache d'images. Met à jour le rappel transmettant une commande avec le rappel transmettant une commande. Met à jour le rappel de rafraîchissement des commandes avec le rappel de rafraîchissement des commandes.
        this.ressources = ressources; this.commande = commande; this.changement = changement;
        // Met à jour le dessinateur des salles avec un nouvel objet RenduSalle avec ressources. Met à jour le dessinateur des combats avec un nouvel objet RenduCombat avec ressources.
        renduSalle = new RenduSalle(ressources); renduCombat = new RenduCombat(ressources);
        // Met à jour le fond précalculé d'exploration avec renduSalle.fond(id, false). Met à jour le fond précalculé du combat avec renduSalle.fond(id, true).
        fondExploration = renduSalle.fond(id, false); fondCombat = renduSalle.fond(id, true);
        // Définit la couleur de fond ; arguments : Theme.FOND. Autorise la scène à recevoir le focus ; arguments : true. Fixe la taille souhaitée pour le placement ; arguments : new Dimension(960, 540).
        setBackground(Theme.FOND); setFocusable(true); setPreferredSize(new Dimension(960, 540));
        // Définit l’aide affichée au survol ; arguments : "Flèches ou ZQSD : marcher · E / Entrée : interagir".
        setToolTipText("Flèches ou ZQSD : marcher · E / Entrée : interagir");
        // Met à jour l'horloge Swing de la scène avec new javax.swing.Timer(16, e -> tick()). Regroupe les notifications de timer en retard pour éviter leur accumulation ; arguments : true.
        timer = new javax.swing.Timer(16, e -> tick()); timer.setCoalesce(true);
        // Installe les raccourcis de déplacement et d’action.
        installerClavier();
        // Installe l’écouteur qui transforme les clics de souris en interactions de scène.
        addMouseListener(new MouseAdapter() {
            // Convertit le clic en coordonnées logiques après avoir redonné le focus à la scène. Redonne le focus clavier à la scène. Transforme le clic sur une cible en marche vers une porte ou un objet. Paramètres : point(e.getX(), e.getY()).
            @Override public void mousePressed(MouseEvent e) { requestFocusInWindow(); clic(point(e.getX(), e.getY())); }
        // Ferme le bloc et termine l’instruction ou l’appel englobant.
        });
        // Installe l’écouteur de déplacement de souris pour mettre à jour le survol.
        addMouseMotionListener(new MouseMotionAdapter() {
            // Met à jour la cible de survol, le curseur et le redessin si la cible change.
            @Override public void mouseMoved(MouseEvent e) {
                // Initialise la variable p de type Point2D.Double avec point(e.getX(), e.getY()). Initialise « nouveau » avec cible(p).
                Point2D.Double p = point(e.getX(), e.getY()); String nouveau = cible(p);
                // Si non (nouveau.equals(survol)), Met à jour l'élément sous la souris avec nouveau. Change le curseur pour signaler une cible interactive ; arguments : Cursor.getPredefinedCursor(nouveau.isEmpty() ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR). Demande à Swing un nouveau dessin.
                if (!nouveau.equals(survol)) { survol = nouveau; setCursor(Cursor.getPredefinedCursor(nouveau.isEmpty() ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR)); repaint(); }
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
        // Ferme le bloc et termine l’instruction ou l’appel englobant.
        });
        // Installe le nettoyage des touches lors de la perte de focus ; arguments : new FocusAdapter() { @Override public void focusLost(FocusEvent e) { touches.clear(); } }.
        addFocusListener(new FocusAdapter() { @Override public void focusLost(FocusEvent e) { touches.clear(); } });
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    /**
     * Entre dans la salle réelle ; au chargement le héros revient au point central praticable.
     *
 * @param nouvelle partie courante
     *
 * @param provenance identifiant de la salle précédente, null au chargement
     */
    // Associe la partie reçue à la vue et rafraîchit sa présentation.
    public void afficher(Partie nouvelle, String provenance) {
        // Arrête les travaux visuels et leur horloge. Met à jour la partie courante avec nouvelle. Met à jour le marqueur d'affichage de l'accueil avec faux. Met à jour l'identifiant de salle avec nouvelle.getSalleActuelle().getId(). Met à jour le plan visuel de la salle avec PlanSalle.de(id).
        arreter(); partie = nouvelle; accueil = false; id = nouvelle.getSalleActuelle().getId(); plan = PlanSalle.de(id);
        // Met à jour le fond précalculé d'exploration avec renduSalle.fond(id, false). Met à jour le fond précalculé du combat avec renduSalle.fond(id, true).
        fondExploration = renduSalle.fond(id, false); fondCombat = renduSalle.fond(id, true);
        // Met à jour « x » avec 425. Met à jour « y » avec 370.
        x = 425; y = 370;
        // Parcourt plan.portes() ; chaque élément est placé dans PlanSalle.Porte porte. Si porte.destination().equals(provenance), entre dans ce traitement conditionnel.
        for (PlanSalle.Porte porte : plan.portes()) if (porte.destination().equals(provenance)) {
            // Met à jour « x » avec porte.x() + (porte.cote() == 'O' ? 35 : porte.cote() == 'E' ? -35 : 0).
            x = porte.x() + (porte.cote() == 'O' ? 35 : porte.cote() == 'E' ? -35 : 0);
            // Met à jour « y » avec porte.y() + (porte.cote() == 'N' ? 30 : porte.cote() == 'S' ? -25 : 0).
            y = porte.y() + (porte.cote() == 'N' ? 30 : porte.cote() == 'S' ? -25 : 0);
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Met à jour le mode de présentation du combat avec !nouvelle.estEnCours(). Met à jour le temps restant de présentation du combat avec nouvelle.estEnCombat() ? (animation.estReduite() ? 180 : 850) : 0.
        combat = !nouvelle.estEnCours(); presentation = nouvelle.estEnCombat() ? (animation.estReduite() ? 180 : 850) : 0;
        // Si le temps restant de présentation du combat est supérieur à 0, Démarre si nécessaire l’horloge visuelle. Exécute le rappel sans argument. Demande à Swing un nouveau dessin.
        if (presentation > 0) demarrerTimer(); changement.run(); repaint();
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Retourne au décor d'accueil sans laisser de timer actif. */
    // Revient au décor initial et retire toute partie de la scène. Arrête les travaux visuels et leur horloge. Met à jour la partie courante avec l’absence de valeur (null). Met à jour le marqueur d'affichage de l'accueil avec vrai. Met à jour l'identifiant de salle avec le texte "entree". Met à jour le plan visuel de la salle avec PlanSalle.de(id). Met à jour le fond précalculé d'exploration avec renduSalle.fond(id, false). Demande à Swing un nouveau dessin.
    public void accueil() { arreter(); partie = null; accueil = true; id = "entree"; plan = PlanSalle.de(id); fondExploration = renduSalle.fond(id, false); repaint(); }
    /**
     * Lit une action atomique déjà appliquée au modèle.
     *
 * @param resultat événements ordonnés
     *
 * @param fin notification unique de fin
     */
    // Lit les événements d’une commande déjà appliquée au modèle.
    public void jouer(ResultatAction resultat, Runnable fin) {
        // Si si une présentation, animation ou marche automatique bloque la scène, refuse l’opération : "La scène est occupée.".
        if (estOccupe()) throw new IllegalStateException("La scène est occupée.");
        // Vide la collection courante. Vide la collection courante. Initialise la lecture visuelle du résultat déjà calculé ; arguments : resultat. Met à jour le rappel de fin de séquence avec fin.
        touches.clear(); trajet.clear(); animation.demarrer(resultat); finSequence = fin;
        // Démarre si nécessaire l’horloge visuelle. Exécute le rappel sans argument. Demande à Swing un nouveau dessin.
        demarrerTimer(); changement.run(); repaint();
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** {@return vrai tant qu'une animation ou un trajet empêche une nouvelle action} */
    // Indique si une présentation, animation ou marche automatique bloque la scène. Renvoie presentation > 0 || animation.estActive() || !trajet.isEmpty().
    public boolean estOccupe() { return presentation > 0 || animation.estActive() || !trajet.isEmpty(); }
    /** {@return vrai lorsque la vue latérale est affichée} */
    // { Renvoie le mode de présentation du combat.
    public boolean estCombatAffiche() { return combat; }
    /** {@return timer actif, utile pour vérifier son cycle de vie} */
    // { Renvoie si l'horloge est active de l'horloge Swing de la scène.
    public boolean estTimerActif() { return timer.isRunning(); }
    /** {@return option d'animations réduites} */
    // Indique si les animations sont réduites. Renvoie si les animations sont réduites du lecteur d'événements visuels.
    public boolean estReduite() { return animation.estReduite(); }
    /**
     * Change uniquement la vitesse de présentation.
     *
 * @param valeur option utilisateur
     */
    // Change uniquement la vitesse de présentation. Modifie seulement la vitesse et l’amplitude de présentation ; arguments : valeur.
    public void setReduite(boolean valeur) { animation.setReduite(valeur); }
    /**
     * Gèle mouvements et horloge lorsqu'un panneau ou un dialogue masque la scène.
     *
 * @param valeur panneau ouvert ou fenêtre inactive
     */
    // Gèle ou reprend les travaux visuels selon l’ouverture d’un panneau ou le focus. Met à jour le marqueur de suspension visuelle avec valeur. Vide la collection courante. Si valeur, Arrête l’horloge. Si si une présentation, animation ou marche automatique bloque la scène, Démarre si nécessaire l’horloge visuelle.
    public void suspendre(boolean valeur) { suspendue = valeur; touches.clear(); if (valeur) timer.stop(); else if (estOccupe()) demarrerTimer(); }
    /** Arrête tous les travaux visuels, en laissant intacte la résolution métier déjà terminée. */
    // Stoppe l’horloge et efface les travaux visuels sans annuler l’action métier déjà résolue.
    public void arreter() {
        // Arrête l’horloge. Vide la collection courante. Vide la collection courante. Met à jour la salle à rejoindre après la marche avec l’absence de valeur (null). Met à jour l'interaction à effectuer à l'arrivée avec l’absence de valeur (null).
        timer.stop(); touches.clear(); trajet.clear(); destination = null; interactionAutomatique = null;
        // Aligne immédiatement la présentation sur l’état final. Met à jour le rappel de fin de séquence avec l’absence de valeur (null). Met à jour le temps restant de présentation du combat avec 0. Met à jour le marqueur de suspension visuelle avec faux.
        animation.finirImmediatement(); finSequence = null; presentation = 0; suspendue = false;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Termine une séquence pour revenir à un point stable lors d'un abandon confirmé. */
    // Aligne immédiatement la présentation sur l’état final déjà calculé.
    public void stabiliser() {
        // Aligne immédiatement la présentation sur l’état final. Met à jour le temps restant de présentation du combat avec 0. Vide la collection courante. Met à jour la salle à rejoindre après la marche avec l’absence de valeur (null). Met à jour l'interaction à effectuer à l'arrivée avec l’absence de valeur (null).
        animation.finirImmediatement(); presentation = 0; trajet.clear(); destination = null; interactionAutomatique = null;
        // Met à jour le mode de présentation du combat avec partie != null && (partie.estEnCombat() || !partie.estEnCours()).
        combat = partie != null && (partie.estEnCombat() || !partie.estEnCours());
        // Initialise « fin » avec le rappel de fin de séquence. Met à jour le rappel de fin de séquence avec l’absence de valeur (null). Arrête l’horloge. Si fin est différent de null, Exécute le rappel sans argument. Demande à Swing un nouveau dessin.
        Runnable fin = finSequence; finSequence = null; timer.stop(); if (fin != null) fin.run(); repaint();
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /**
     * Déclenche une marche vers une porte réellement adjacente.
     *
 * @param salle destination demandée depuis la carte
     */
    // Démarre un trajet uniquement vers une porte adjacente autorisée par le modèle.
    public void marcherVersPorte(String salle) {
        // Si (la partie courante est égal à l’absence de valeur (null) ou si une présentation, animation ou marche automatique bloque la scène ou le marqueur de suspension visuelle ou non (partie.peutDeplacer(salle))), Quitte la méthode sans poursuivre.
        if (partie == null || estOccupe() || suspendue || !partie.peutDeplacer(salle)) return;
        // Prépare le rappel qui sera exécuté pour l’événement ou l’élément trouvé.
        plan.portes().stream().filter(p -> p.destination().equals(salle)).findFirst().ifPresent(p -> {
            // Si chemin(p.x(), p.y()), Met à jour la salle à rejoindre après la marche avec la salle courante. Démarre si nécessaire l’horloge visuelle. Exécute le rappel sans argument.
            if (chemin(p.x(), p.y())) { destination = salle; demarrerTimer(); changement.run(); }
        // Ferme le bloc et termine l’instruction ou l’appel englobant.
        });
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Interaction de proximité, appelée par le clavier ou le bouton contextuel. */
    // Cherche une porte ou un objet assez proche, puis transmet la commande appropriée.
    public void interagir() {
        // Si (la partie courante est égal à l’absence de valeur (null) ou si une présentation, animation ou marche automatique bloque la scène ou le marqueur de suspension visuelle ou non (si la partie autorise encore des actions de la partie courante) ou le mode de présentation du combat), Quitte la méthode sans poursuivre.
        if (partie == null || estOccupe() || suspendue || !partie.estEnCours() || combat) return;
        // Si (Math.hypot(x - 585, y - 337) est inférieur à 88 et (partie.getSalleActuelle().getCoffre() != null || partie.getSalleActuelle().estRepos())), entre dans ce traitement conditionnel.
        if (Math.hypot(x - 585, y - 337) < 88 && (partie.getSalleActuelle().getCoffre() != null || partie.getSalleActuelle().estRepos())) {
            // Transmet la valeur au rappel fourni ; arguments : "objet". Quitte la méthode sans poursuivre.
            commande.accept("objet"); return;
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Parcourt plan.portes() ; chaque élément est placé dans PlanSalle.Porte porte. Si Math.hypot(x - porte.x(), y - porte.y()) est inférieur à 65, entre dans ce traitement conditionnel.
        for (PlanSalle.Porte porte : plan.portes()) if (Math.hypot(x - porte.x(), y - porte.y()) < 65) {
            // Transmet la valeur au rappel fourni ; arguments : "porte:" + porte.destination(). Quitte la méthode sans poursuivre.
            commande.accept("porte:" + porte.destination()); return;
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Transmet la valeur au rappel fourni ; arguments : "message:Approchez une porte, le coffre ou l'autel pour interagir.".
        commande.accept("message:Approchez une porte, le coffre ou l'autel pour interagir.");
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Calcule une trajectoire à quatre voisins ; les bases des objets ne sont jamais traversées. */
    // Cherche un trajet à quatre voisins, puis reconstitue ses points par les parents du parcours.
    private boolean chemin(double cibleX, double cibleY) {
        // Vide la collection courante. Vide la collection courante. Met à jour le temps sans déplacement sur un trajet avec 0.
        trajet.clear(); touches.clear(); immobilite = 0;
        // Prépare une grille de recherche de chemin de 49 colonnes sur 19 lignes.
        int cols = 49, lignes = 19;
        // Convertit la position actuelle du héros en cellule de départ sur une grille de pas 16.
        int sx = (int)Math.round((x - 104) / 16), sy = (int)Math.round((y - 184) / 16);
        // Convertit le point visé en cellule d’arrivée sur cette même grille.
        int tx = (int)Math.round((cibleX - 104) / 16), ty = (int)Math.round((cibleY - 184) / 16);
        // Codes cellules de départ et d’arrivée par un indice unique ligne × colonnes + colonne.
        int debut = sy * cols + sx, fin = ty * cols + tx;
        // Prépare les liens vers les parents et la file du parcours en largeur.
        Map<Integer, Integer> parents = new HashMap<>(); ArrayDeque<Integer> file = new ArrayDeque<>();
        // Marque le départ comme déjà atteint et le place dans la file.
        parents.put(debut, debut); file.add(debut);
        // Explore tant qu’il reste des cellules en attente et que la cellule d’arrivée n’a pas été atteinte.
        while (!file.isEmpty() && !parents.containsKey(fin)) {
            // Retire la plus ancienne cellule en attente et retrouve sa colonne et sa ligne.
            int position = file.remove(); int cx = position % cols, cy = position / cols;
            // Essaie les quatre déplacements voisins : droite, gauche, bas et haut.
            for (int[] d : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                // Calcule les coordonnées du voisin et son indice unique.
                int nx = cx + d[0], ny = cy + d[1], n = ny * cols + nx;
                // Ne retient qu’un voisin dans la grille, non encore visité et praticable selon les collisions.
                if (nx >= 0 && nx < cols && ny >= 0 && ny < lignes && !parents.containsKey(n) && praticable(104 + nx * 16, 184 + ny * 16)) {
                    // Mémorise d’où vient ce voisin puis l’ajoute à la file du parcours en largeur.
                    parents.put(n, position); file.add(n);
                // Ferme le bloc ou la liste de données commencé plus haut.
                }
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Si non (parents.containsKey(fin)), Transmet la valeur au rappel fourni ; arguments : "message:Ce passage est inaccessible depuis cette position.". Renvoie faux.
        if (!parents.containsKey(fin)) { commande.accept("message:Ce passage est inaccessible depuis cette position."); return false; }
        // Remonte les parents de l’arrivée au départ et insère les points en tête pour reconstruire le trajet dans le bon sens.
        for (int n = fin; n != debut; n = parents.get(n)) trajet.addFirst(new Point2D.Double(104 + n % cols * 16, 184 + n / cols * 16));
        // Ajoute le point d’approche exact après les cellules de grille.
        trajet.add(new Point2D.Double(cibleX, cibleY));
        // Renvoie vrai.
        return true;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Vérifie les obstacles fixes et l'objet interactif central. */
    // Vérifie que le point est à l’intérieur du sol et hors des bases solides.
    private boolean praticable(double px, double py) {
        // Initialise « objet » avec partie != null && (partie.getSalleActuelle().getCoffre() != null || partie.getSalleActuelle().estRepos()).
        boolean objet = partie != null && (partie.getSalleActuelle().getCoffre() != null || partie.getSalleActuelle().estRepos());
        // Renvoie plan.praticable(px, py) && !(objet && px > 550 && px < 620 && py > 284 && py < 317).
        return plan.praticable(px, py) && !(objet && px > 550 && px < 620 && py > 284 && py < 317);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Démarre le seul timer de la scène et réinitialise son horloge monotone. */
    // Démarre l’unique horloge de scène si nécessaire et recale son temps monotone. Si (non (le marqueur de suspension visuelle) et non (si l'horloge est active de l'horloge Swing de la scène)), Met à jour l'instant de la dernière mise à jour avec l’instant monotone en nanosecondes. Démarre l’horloge.
    private void demarrerTimer() { if (!suspendue && !timer.isRunning()) { precedent = System.nanoTime(); timer.start(); } }
    /** Avance la présentation ; aucune opération lente et aucune règle de combat ici. */
    // Avance l’horloge visuelle, la présentation ou la marche, puis demande un redessin.
    private void tick() {
        // Initialise « maintenant » avec l’instant monotone en nanosecondes. Initialise « ms » avec Math.min(50, (maintenant - precedent) / 1_000_000.0). Met à jour l'instant de la dernière mise à jour avec maintenant.
        long maintenant = System.nanoTime(); double ms = Math.min(50, (maintenant - precedent) / 1_000_000.0); precedent = maintenant;
        // Si le temps restant de présentation du combat est supérieur à 0, entre dans ce traitement conditionnel.
        if (presentation > 0) {
            // Met à jour le temps restant de présentation du combat avec Math.max(0, presentation - ms).
            presentation = Math.max(0, presentation - ms);
            // Si le temps restant de présentation du combat est égal à 0, Met à jour le mode de présentation du combat avec vrai. Exécute le rappel sans argument.
            if (presentation == 0) { combat = true; changement.run(); }
        // Si si la séquence visuelle est en cours du lecteur d'événements visuels, entre dans ce traitement conditionnel.
        } else if (animation.estActive()) {
            // Avance l’horloge visuelle du nombre de millisecondes donné ; arguments : ms.
            animation.avancer(ms);
            // Si non (si la séquence visuelle est en cours du lecteur d'événements visuels), entre dans ce traitement conditionnel.
            if (!animation.estActive()) {
                // Met à jour le mode de présentation du combat avec partie.estEnCombat() || !partie.estEnCours().
                combat = partie.estEnCombat() || !partie.estEnCours();
                // Initialise « fin » avec le rappel de fin de séquence. Met à jour le rappel de fin de séquence avec l’absence de valeur (null). Si fin est différent de null, Exécute le rappel sans argument. Exécute le rappel sans argument.
                Runnable fin = finSequence; finSequence = null; if (fin != null) fin.run(); changement.run();
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
        // Si (non (le mode de présentation du combat) et la partie courante est différent de null et si la partie autorise encore des actions de la partie courante), entre dans ce traitement conditionnel.
        } else if (!combat && partie != null && partie.estEnCours()) {
            // Initialise « dx » avec 0, dy = 0.
            double dx = 0, dy = 0;
            // Si non (si la collection ou le texte est vide des points de la marche automatique), Initialise la variable p de type Point2D.Double avec trajet.peek(). Met à jour « dx » avec p.x - x. Met à jour « dy » avec p.y - y.
            if (!trajet.isEmpty()) { Point2D.Double p = trajet.peek(); dx = p.x - x; dy = p.y - y; }
            // Traite le cas où la condition précédente n’est pas satisfaite.
            else {
                // Met à jour « dx » avec (touches.contains(KeyEvent.VK_RIGHT) || touches.contains(KeyEvent.VK_D) ? 1 : 0) - (touches.contains(KeyEvent.VK_LEFT) || touches.contains(KeyEvent.VK_Q) ? 1 : 0).
                dx = (touches.contains(KeyEvent.VK_RIGHT) || touches.contains(KeyEvent.VK_D) ? 1 : 0) - (touches.contains(KeyEvent.VK_LEFT) || touches.contains(KeyEvent.VK_Q) ? 1 : 0);
                // Met à jour « dy » avec (touches.contains(KeyEvent.VK_DOWN) || touches.contains(KeyEvent.VK_S) ? 1 : 0) - (touches.contains(KeyEvent.VK_UP) || touches.contains(KeyEvent.VK_Z) ? 1 : 0).
                dy = (touches.contains(KeyEvent.VK_DOWN) || touches.contains(KeyEvent.VK_S) ? 1 : 0) - (touches.contains(KeyEvent.VK_UP) || touches.contains(KeyEvent.VK_Z) ? 1 : 0);
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
            // Initialise « distance » avec Math.hypot(dx, dy), pas = ms * .20.
            double distance = Math.hypot(dx, dy), pas = ms * .20;
            // Initialise « ancien x » avec x, ancienY = y.
            double ancienX = x, ancienY = y;
            // Si distance est supérieur à 0, entre dans ce traitement conditionnel.
            if (distance > 0) {
                // Met à jour la direction du héros avec Math.abs(dx) > Math.abs(dy) ? dx > 0 ? KeyEvent.VK_RIGHT : KeyEvent.VK_LEFT : dy > 0 ? KeyEvent.VK_DOWN : KeyEvent.VK_UP.
                orientation = Math.abs(dx) > Math.abs(dy) ? dx > 0 ? KeyEvent.VK_RIGHT : KeyEvent.VK_LEFT : dy > 0 ? KeyEvent.VK_DOWN : KeyEvent.VK_UP;
                // Initialise « facteur » avec !trajet.isEmpty() ? Math.min(pas, distance) / distance : pas / distance.
                double facteur = !trajet.isEmpty() ? Math.min(pas, distance) / distance : pas / distance;
                // Si praticable(x + dx * facteur, y), Augmente « x » avec dx * facteur.
                if (praticable(x + dx * facteur, y)) x += dx * facteur;
                // Si praticable(x, y + dy * facteur), Augmente « y » avec dy * facteur.
                if (praticable(x, y + dy * facteur)) y += dy * facteur;
                // Augmente les images ou le temps de marche avec ms.
                marche += ms;
                // Si (non (si la collection ou le texte est vide des points de la marche automatique) et Point2D.distance(x, y, trajet.peek().x, trajet.peek().y) est inférieur à 2), entre dans ce traitement conditionnel.
                if (!trajet.isEmpty() && Point2D.distance(x, y, trajet.peek().x, trajet.peek().y) < 2) {
                    // Initialise « point » avec trajet.peek().
                    Point2D.Double point = trajet.peek();
                    // Si praticable(point.x, point.y), Met à jour « x » avec point.x. Met à jour « y » avec point.y. Retire l’élément ou le composant demandé.
                    if (praticable(point.x, point.y)) { x = point.x; y = point.y; trajet.remove(); }
                // Ferme le bloc ou la liste de données commencé plus haut.
                }
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
            // Si non (si la collection ou le texte est vide des points de la marche automatique), entre dans ce traitement conditionnel.
            if (!trajet.isEmpty()) {
                // Met à jour le temps sans déplacement sur un trajet avec Point2D.distance(ancienX, ancienY, x, y) < .001 ? immobilite + ms : 0.
                immobilite = Point2D.distance(ancienX, ancienY, x, y) < .001 ? immobilite + ms : 0;
                // Si le temps sans déplacement sur un trajet est supérieur ou égal à 500, entre dans ce traitement conditionnel.
                if (immobilite >= 500) {
                    // Vide la collection courante. Met à jour la salle à rejoindre après la marche avec l’absence de valeur (null). Met à jour l'interaction à effectuer à l'arrivée avec l’absence de valeur (null). Exécute le rappel sans argument.
                    trajet.clear(); destination = null; interactionAutomatique = null; changement.run();
                    // Transmet la valeur au rappel fourni ; arguments : "message:Le trajet est bloqué. Déplacez-vous avec les flèches puis réessayez.".
                    commande.accept("message:Le trajet est bloqué. Déplacez-vous avec les flèches puis réessayez.");
                // Ferme le bloc ou la liste de données commencé plus haut.
                }
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
            // Si (si la collection ou le texte est vide des points de la marche automatique et la salle à rejoindre après la marche est différent de null), Initialise « suivante » avec la salle à rejoindre après la marche. Met à jour la salle à rejoindre après la marche avec l’absence de valeur (null). Exécute le rappel sans argument. Transmet la valeur au rappel fourni ; arguments : "porte:" + suivante.
            if (trajet.isEmpty() && destination != null) { String suivante = destination; destination = null; changement.run(); commande.accept("porte:" + suivante); }
            // Si (si la collection ou le texte est vide des points de la marche automatique et l'interaction à effectuer à l'arrivée est différent de null), Met à jour l'interaction à effectuer à l'arrivée avec l’absence de valeur (null). Exécute le rappel sans argument. Tente l’interaction avec une porte ou un objet proche.
            else if (trajet.isEmpty() && interactionAutomatique != null) { interactionAutomatique = null; changement.run(); interagir(); }
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Demande à Swing un nouveau dessin. Si (non (si une présentation, animation ou marche automatique bloque la scène) et si la collection ou le texte est vide des touches maintenues enfoncées), Arrête l’horloge.
        repaint(); if (!estOccupe() && touches.isEmpty()) timer.stop();
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Convertit les pixels de fenêtre en coordonnées du monde, bandes latérales comprises. */
    // Convertit les pixels de la fenêtre en coordonnées logiques de la scène.
    private Point2D.Double point(int px, int py) {
        // Initialise la variable e de type double avec Math.min(getWidth() / 960.0, getHeight() / 540.0).
        double e = Math.min(getWidth() / 960.0, getHeight() / 540.0);
        // Renvoie new Point2D.Double((px - (getWidth() - 960 * e) / 2) / e, (py - (getHeight() - 540 * e) / 2) / e).
        return new Point2D.Double((px - (getWidth() - 960 * e) / 2) / e, (py - (getHeight() - 540 * e) / 2) / e);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Renvoie l'interaction visée, sans effet métier. */
    // Permet de consulter la cible.
    private String cible(Point2D.Double p) {
        // Si (le marqueur d'affichage de l'accueil ou le mode de présentation du combat ou si une présentation, animation ou marche automatique bloque la scène ou le marqueur de suspension visuelle), renvoie le texte "".
        if (accueil || combat || estOccupe() || suspendue) return "";
        // Parcourt plan.portes() ; chaque élément est placé dans PlanSalle.Porte porte.
        for (PlanSalle.Porte porte : plan.portes()) {
            // Initialise « px » avec porte.cote() == 'O' ? 80 : porte.cote() == 'E' ? 880 : porte.x().
            int px = porte.cote() == 'O' ? 80 : porte.cote() == 'E' ? 880 : porte.x();
            // Initialise « py » avec porte.cote() == 'N' ? 150 : porte.cote() == 'S' ? 490 : porte.y().
            int py = porte.cote() == 'N' ? 150 : porte.cote() == 'S' ? 490 : porte.y();
            // Si (Math.abs(p.x - px) est inférieur à 48 et Math.abs(p.y - py) est inférieur à 58), renvoie porte.destination().
            if (Math.abs(p.x - px) < 48 && Math.abs(p.y - py) < 58) return porte.destination();
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Si (la partie courante est différent de null et (partie.getSalleActuelle().getCoffre() != null || partie.getSalleActuelle().estRepos()) et Math.abs(p.x - 585) est inférieur à 60 et p.y est supérieur à 205 et p.y est inférieur à 340), renvoie le texte "objet".
        if (partie != null && (partie.getSalleActuelle().getCoffre() != null || partie.getSalleActuelle().estRepos()) && Math.abs(p.x - 585) < 60 && p.y > 205 && p.y < 340) return "objet";
        // Renvoie le texte "".
        return "";
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Lance la marche contextuelle vers une porte ou un objet. */
    // Transforme le clic sur une cible en marche vers une porte ou un objet.
    private void clic(Point2D.Double p) {
        // Initialise la cible avec cible(p). Si si la collection ou le texte est vide de la cible, quitte la méthode sans poursuivre.
        String cible = cible(p); if (cible.isEmpty()) return;
        // Si cible.equals("objet"), Si chemin(585, 345), Met à jour l'interaction à effectuer à l'arrivée avec le texte "objet". Démarre si nécessaire l’horloge visuelle. Exécute le rappel sans argument.
        if (cible.equals("objet")) { if (chemin(585, 345)) { interactionAutomatique = "objet"; demarrerTimer(); changement.run(); } }
        // Dans le cas contraire, Lance le trajet visuel vers la porte demandée ; arguments : cible.
        else marcherVersPorte(cible);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Installe des key bindings sans dépendre d'un KeyListener et de son focus fragile. */
    // Associe appuis et relâchements aux déplacements et aux commandes du jeu.
    private void installerClavier() {
        // Parcourt new int[]{KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_Z, KeyEvent.VK_Q, KeyEvent.VK_S, KeyEvent.VK_D} ; chaque élément est placé dans int code.
        for (int code : new int[]{KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_Z, KeyEvent.VK_Q, KeyEvent.VK_S, KeyEvent.VK_D}) {
            // Initialise « appui » avec "appui" + code, relache = "relache" + code.
            String appui = "appui" + code, relache = "relache" + code;
            // Associe l’appui sur cette touche à une action identifiée, tant que la fenêtre a le focus.
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(code, 0, false), appui);
            // Associe le relâchement de la même touche à une autre action pour arrêter la marche.
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(code, 0, true), relache);
            // Définit le rappel Swing exécuté à l’appui sur la touche de déplacement.
            getActionMap().put(appui, new AbstractAction() { @Override public void actionPerformed(ActionEvent e) {
                // Si (non (le marqueur d'affichage de l'accueil) et non (le mode de présentation du combat) et non (si une présentation, animation ou marche automatique bloque la scène) et non (le marqueur de suspension visuelle) et si la partie autorise encore des actions de la partie courante), Ajoute l’élément à la collection ou au conteneur ; arguments : code. Démarre si nécessaire l’horloge visuelle.
                if (!accueil && !combat && !estOccupe() && !suspendue && partie.estEnCours()) { touches.add(code); demarrerTimer(); }
            // Ferme le bloc et termine l’instruction ou l’appel englobant.
            }});
            // Au relâchement, retire cette touche de l’ensemble des directions maintenues.
            getActionMap().put(relache, new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { touches.remove(code); } });
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Associe la touche à la commande de jeu ; arguments : KeyEvent.VK_E, "interaction". Associe la touche à la commande de jeu ; arguments : KeyEvent.VK_ENTER, "interaction".
        lier(KeyEvent.VK_E, "interaction"); lier(KeyEvent.VK_ENTER, "interaction");
        // Associe la touche à la commande de jeu ; arguments : KeyEvent.VK_I, "inventaire". Associe la touche à la commande de jeu ; arguments : KeyEvent.VK_M, "carte". Associe la touche à la commande de jeu ; arguments : KeyEvent.VK_J, "journal".
        lier(KeyEvent.VK_I, "inventaire"); lier(KeyEvent.VK_M, "carte"); lier(KeyEvent.VK_J, "journal");
        // Associe la touche à la commande de jeu ; arguments : KeyEvent.VK_ESCAPE, "pause". Associe la touche à la commande de jeu ; arguments : KeyEvent.VK_1, "attaque". Associe la touche à la commande de jeu ; arguments : KeyEvent.VK_2, "defense". Associe la touche à la commande de jeu ; arguments : KeyEvent.VK_3, "potion".
        lier(KeyEvent.VK_ESCAPE, "pause"); lier(KeyEvent.VK_1, "attaque"); lier(KeyEvent.VK_2, "defense"); lier(KeyEvent.VK_3, "potion");
        // Associe la touche à la commande de jeu ; arguments : KeyEvent.VK_4, "puissante". Associe la touche à la commande de jeu ; arguments : KeyEvent.VK_5, "observer". Associe la touche à la commande de jeu ; arguments : KeyEvent.VK_F, "decouverte".
        lier(KeyEvent.VK_4, "puissante"); lier(KeyEvent.VK_5, "observer"); lier(KeyEvent.VK_F, "decouverte");
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Associe un raccourci à une intention de commande. */
    // Relie une touche à une commande en respectant les suspensions de la scène.
    private void lier(int code, String action) {
        // Associe la combinaison clavier au nom de commande demandé.
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(code, 0), action);
        // Définit le rappel qui transmettra cette commande au jeu.
        getActionMap().put(action, new AbstractAction() { @Override public void actionPerformed(ActionEvent e) {
            // Si action.equals("pause"), Transmet la valeur au rappel fourni ; arguments : action.
            if (action.equals("pause")) commande.accept(action);
            // Si (non (le marqueur de suspension visuelle) et non (le marqueur d'affichage de l'accueil)), Si action.equals("interaction"), Tente l’interaction avec une porte ou un objet proche. Dans le cas contraire, Transmet la valeur au rappel fourni ; arguments : action.
            else if (!suspendue && !accueil) { if (action.equals("interaction")) interagir(); else commande.accept(action); }
        // Ferme le bloc et termine l’instruction ou l’appel englobant.
        }});
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Dessine le monde en conservant le ratio, sans aucune mutation métier. */
    // Redessine le composant à partir de son état courant, sans modifier les règles.
    @Override protected void paintComponent(Graphics graphics) {
        // Redessine d’abord le fond du composant parent ; arguments : graphics. Initialise la variable g de type Graphics2D avec (Graphics2D)graphics.create().
        super.paintComponent(graphics); Graphics2D g = (Graphics2D)graphics.create();
        // Initialise « echelle » avec Math.min(getWidth() / 960.0, getHeight() / 540.0).
        double echelle = Math.min(getWidth() / 960.0, getHeight() / 540.0);
        // Décale l’origine des coordonnées graphiques ; arguments : (getWidth() - 960 * echelle) / 2, (getHeight() - 540 * echelle) / 2. Applique le facteur d’échelle aux dessins suivants ; arguments : echelle, echelle.
        g.translate((getWidth() - 960 * echelle) / 2, (getHeight() - 540 * echelle) / 2); g.scale(echelle, echelle);
        // Configure l’interpolation ou le lissage du rendu ; arguments : RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR.
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        // Configure l’interpolation ou le lissage du rendu ; arguments : RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON.
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // Dessine l’image à la position et à la taille indiquées ; arguments : combat && !accueil ? fondCombat : fondExploration, 0, 0, null.
        g.drawImage(combat && !accueil ? fondCombat : fondExploration, 0, 0, null);
        // Si le marqueur d'affichage de l'accueil, entre dans ce traitement conditionnel.
        if (accueil) {
            // Dessine les objets selon leur état et, si demandé, leur profondeur par rapport au héros. Paramètres : g, plan, false, id.
            renduSalle.objets(g, plan, false, id);
            // Dessine un halo lumineux translucide ; arguments : g, 680, 365, 180, new Color(107, 205, 183), .18f.
            RenduSalle.lueur(g, 680, 365, 180, new Color(107, 205, 183), .18f);
            // Dessine l’ombre au sol du personnage ; arguments : g, 695, 467, 105. Dessine la pose du personnage à partir de l’atlas en mémoire ; arguments : g, "heros", 0, 695, 467, 205, false.
            RenduCombat.ombre(g, 695, 467, 105); ressources.personnage(g, "heros", 0, 695, 467, 205, false);
        // Si le mode de présentation du combat, entre dans ce traitement conditionnel.
        } else if (combat) {
            // Initialise l'ennemi avec partie.getSalleActuelle().getEnnemi().
            Ennemi ennemi = partie.getSalleActuelle().getEnnemi();
            // Commence le dessin du combat avec le type d’adversaire et le lecteur d’événements visuels.
            renduCombat.dessiner(g, ennemi == null ? "boss" : ennemi.getType(), animation,
                    // Fournit les marqueurs de rage du Gardien et de mort stable du héros après la fin de l’animation.
                    ennemi instanceof Boss && !ennemi.estMort() && ennemi.getVie() <= 45, !partie.estEnCours() && partie.getHeros().estMort() && !animation.estActive(),
                    // Fournit le marqueur de mort stable de l’ennemi après la fin de son animation.
                    ennemi != null && ennemi.estMort() && !animation.estActive(),
                    // Affiche une garde stable si aucune animation ne tourne et que l’intention suivante est une riposte.
                    !animation.estActive() && partie.getTactique().getDecision() != null && partie.getTactique().getDecision().action() == ActionEnnemi.RIPOSTE);
        // Traite le cas où la condition précédente n’est pas satisfaite.
        } else {
            // Parcourt plan.portes() ; chaque élément est placé dans PlanSalle.Porte porte. Prépare ou dessine une porte et son point d’approche selon son côté. Paramètres : g, porte, porte.destination().equals(survol), partie.getDonjon().getSalle(porte.destination()).getNom().
            for (PlanSalle.Porte porte : plan.portes()) renduSalle.porte(g, porte, porte.destination().equals(survol), partie.getDonjon().getSalle(porte.destination()).getNom());
            // Dessine les objets selon leur état et, si demandé, leur profondeur par rapport au héros. Paramètres : g, plan, partie.getSalleActuelle().estUtilisee(), id, y, false.
            renduSalle.objets(g, plan, partie.getSalleActuelle().estUtilisee(), id, y, false);
            // Si partie.getSalleActuelle().estHostile(), entre dans ce traitement conditionnel.
            if (partie.getSalleActuelle().estHostile()) {
                // Dessine la pose du personnage à partir de l’atlas en mémoire ; arguments : g, partie.getSalleActuelle().getEnnemi().getType(), 0, 675, 355, 110, true.
                ressources.personnage(g, partie.getSalleActuelle().getEnnemi().getType(), 0, 675, 355, 110, true);
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
            // Dessine l’ombre au sol du personnage ; arguments : g, x, y, 44.
            RenduCombat.ombre(g, x, y, 44);
            // Initialise « pose » avec timer.isRunning() && (!touches.isEmpty() || !trajet.isEmpty()) ? 1 + ((int)(marche / 130) % 2) : 0.
            int pose = timer.isRunning() && (!touches.isEmpty() || !trajet.isEmpty()) ? 1 + ((int)(marche / 130) % 2) : 0;
            // Initialise « direction » avec switch (orientation) { case KeyEvent.VK_UP -> 1; case KeyEvent.VK_RIGHT -> 2; case KeyEvent.VK_LEFT -> 3; default -> 0; }.
            int direction = switch (orientation) { case KeyEvent.VK_UP -> 1; case KeyEvent.VK_RIGHT -> 2; case KeyEvent.VK_LEFT -> 3; default -> 0; };
            // Dessine la direction et la pose de marche sélectionnées ; arguments : g, direction, pose, x, y.
            ressources.marche(g, direction, pose, x, y);
            // Dessine les objets selon leur état et, si demandé, leur profondeur par rapport au héros. Paramètres : g, plan, partie.getSalleActuelle().estUtilisee(), id, y, true.
            renduSalle.objets(g, plan, partie.getSalleActuelle().estUtilisee(), id, y, true);
            // Si (partie.getSalleActuelle().getCoffre() est différent de null ou partie.getSalleActuelle().estRepos()), entre dans ce traitement conditionnel.
            if (partie.getSalleActuelle().getCoffre() != null || partie.getSalleActuelle().estRepos()) {
                // Choisit la couleur du prochain dessin ; arguments : Theme.OR. Définit la police de caractères ; arguments : Theme.CORPS.deriveFont(12f).
                g.setColor(Theme.OR); g.setFont(Theme.CORPS.deriveFont(12f));
                // Dessine le texte à la position calculée ; arguments : partie.getSalleActuelle().estUtilisee() ? "Pouvoir épuisé" : "[E] Interagir", 549, 337.
                g.drawString(partie.getSalleActuelle().estUtilisee() ? "Pouvoir épuisé" : "[E] Interagir", 549, 337);
                // Si survol.equals("objet"), Choisit la couleur du prochain dessin ; arguments : new Color(239, 203, 126). Dessine le contour d’une ellipse ; arguments : 544, 296, 82, 21.
                if (survol.equals("objet")) { g.setColor(new Color(239, 203, 126)); g.drawOval(544, 296, 82, 21); }
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Si (non (le marqueur d'affichage de l'accueil) et la partie courante est différent de null), Dessine les informations de jeu superposées à la scène ; arguments : g.
        if (!accueil && partie != null) hud(g);
        // Si le temps restant de présentation du combat est supérieur à 0, entre dans ce traitement conditionnel.
        if (presentation > 0) {
            // Choisit la couleur du prochain dessin ; arguments : new Color(16, 19, 31, 195). Remplit un rectangle à coins arrondis ; arguments : 270, 200, 420, 70, 12, 12.
            g.setColor(new Color(16, 19, 31, 195)); g.fillRoundRect(270, 200, 420, 70, 12, 12);
            // Définit la police de caractères ; arguments : Theme.CORPS.deriveFont(Font.BOLD, 18). Choisit la couleur du prochain dessin ; arguments : Theme.OR. Dessine le texte à la position calculée ; arguments : "UNE PRÉSENCE VOUS BARRE LA ROUTE", 293, 241.
            g.setFont(Theme.CORPS.deriveFont(Font.BOLD, 18)); g.setColor(Theme.OR); g.drawString("UNE PRÉSENCE VOUS BARRE LA ROUTE", 293, 241);
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Libère le contexte graphique ou la fenêtre utilisé.
        g.dispose();
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Affiche les PV de présentation et une mini-carte compacte consultative. */
    // Dessine les PV, potions, élan, effets, carte et éventuelle issue finale.
    private void hud(Graphics2D g) {
        // Initialise les points de vie actuels avec animation.estActive() ? animation.getVieHeros() : partie.getHeros().getVie().
        int vie = animation.estActive() ? animation.getVieHeros() : partie.getHeros().getVie();
        // Initialise le stock de potions avec animation.estActive() ? animation.getPotions() : partie.getHeros().getInventaire().getPotions().
        int potions = animation.estActive() ? animation.getPotions() : partie.getHeros().getInventaire().getPotions();
        // Choisit la couleur du prochain dessin ; arguments : new Color(15, 18, 31, 232). Remplit un rectangle à coins arrondis ; arguments : 25, 18, 245, 98, 12, 12.
        g.setColor(new Color(15, 18, 31, 232)); g.fillRoundRect(25, 18, 245, 98, 12, 12);
        // Choisit la couleur du prochain dessin ; arguments : new Color(110, 103, 89). Dessine un contour à coins arrondis ; arguments : 25, 18, 245, 98, 12, 12.
        g.setColor(new Color(110, 103, 89)); g.drawRoundRect(25, 18, 245, 98, 12, 12);
        // Dessine la pose du personnage à partir de l’atlas en mémoire ; arguments : g, "heros", 0, 65, 102, 73, false.
        ressources.personnage(g, "heros", 0, 65, 102, 73, false);
        // Définit la police de caractères ; arguments : Theme.CORPS.deriveFont(Font.BOLD, 14). Choisit la couleur du prochain dessin ; arguments : Theme.TEXTE. Dessine le texte à la position calculée ; arguments : "L'EXPLORATEUR", 105, 44.
        g.setFont(Theme.CORPS.deriveFont(Font.BOLD, 14)); g.setColor(Theme.TEXTE); g.drawString("L'EXPLORATEUR", 105, 44);
        // Dessine la jauge de PV avec la proportion demandée ; arguments : g, 105, 55, 145, vie, 100, Theme.VERT.
        barre(g, 105, 55, 145, vie, 100, Theme.VERT);
        // Définit la police de caractères ; arguments : Theme.CORPS.deriveFont(12f). Choisit la couleur du prochain dessin ; arguments : Theme.TEXTE. Dessine le texte à la position calculée ; arguments : vie + " / 100 PV   ·   " + potions + " potions", 104, 90.
        g.setFont(Theme.CORPS.deriveFont(12f)); g.setColor(Theme.TEXTE); g.drawString(vie + " / 100 PV   ·   " + potions + " potions", 104, 90);
        // Si (si un combat est actif de la partie courante ou si la séquence visuelle est en cours du lecteur d'événements visuels), entre dans ce traitement conditionnel.
        if (partie.estEnCombat() || animation.estActive()) {
            // Choisit la couleur du prochain dessin ; arguments : new Color(15, 18, 31, 232). Remplit un rectangle à coins arrondis ; arguments : 25, 120, 380, 68, 8, 8.
            g.setColor(new Color(15, 18, 31, 232)); g.fillRoundRect(25, 120, 380, 68, 8, 8);
            // Choisit la couleur du prochain dessin ; arguments : Theme.OR. Définit la police de caractères ; arguments : Theme.CORPS.deriveFont(13f).
            g.setColor(Theme.OR); g.setFont(Theme.CORPS.deriveFont(13f));
            // Dessine le texte à la position calculée ; arguments : "ÉLAN  " + (animation.estActive() ? animation.getElan() : partie.getTactique().getElan()) + " / 3   ·   4 : Puissante   5 : Observer", 36, 141.
            g.drawString("ÉLAN  " + (animation.estActive() ? animation.getElan() : partie.getTactique().getElan()) + " / 3   ·   4 : Puissante   5 : Observer", 36, 141);
            // Initialise les altérations actives avec animation.estActive() ? animation.getEffets() : partie.getTactique().getEffets().
            var effets = animation.estActive() ? animation.getEffets() : partie.getTactique().getEffets();
            // Définit la police de caractères ; arguments : Theme.CORPS.deriveFont(11f). Choisit la couleur du prochain dessin ; arguments : Theme.TEXTE.
            g.setFont(Theme.CORPS.deriveFont(11f)); g.setColor(Theme.TEXTE);
            // Initialise « etats » avec effets.isEmpty() ? "Aucune altération" : effets.stream().map(e -> e.cible() + " : " + e.type() + " (" + e.duree() + ")").collect(java.util.stream.Collectors.joining("  /  ")).
            String etats = effets.isEmpty() ? "Aucune altération" : effets.stream().map(e -> e.cible() + " : " + e.type() + " (" + e.duree() + ")").collect(java.util.stream.Collectors.joining("  /  "));
            // Répète ce bloc tant que (g.getFontMetrics().stringWidth(etats) est supérieur à 355 et etats.length() est supérieur à 4) etats = etats.substring(0, etats.length() - 4).
            while (g.getFontMetrics().stringWidth(etats) > 355 && etats.length() > 4) etats = etats.substring(0, etats.length() - 4) + "…";
            // Dessine le texte à la position calculée ; arguments : etats, 36, 166.
            g.drawString(etats, 36, 166);
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Si (le mode de présentation du combat et partie.getSalleActuelle().getEnnemi() est différent de null), entre dans ce traitement conditionnel.
        if (combat && partie.getSalleActuelle().getEnnemi() != null) {
            // Initialise l'ennemi avec partie.getSalleActuelle().getEnnemi(). Initialise « pv » avec animation.estActive() ? animation.getVieEnnemi() : ennemi.getVie().
            Ennemi ennemi = partie.getSalleActuelle().getEnnemi(); int pv = animation.estActive() ? animation.getVieEnnemi() : ennemi.getVie();
            // Choisit la couleur du prochain dessin ; arguments : new Color(15, 18, 31, 232). Remplit un rectangle à coins arrondis ; arguments : 608, 18, 325, 100, 12, 12.
            g.setColor(new Color(15, 18, 31, 232)); g.fillRoundRect(608, 18, 325, 100, 12, 12);
            // Choisit la couleur du prochain dessin ; arguments : Theme.OR. Définit la police de caractères ; arguments : Theme.CORPS.deriveFont(Font.BOLD, 15). Dessine le texte à la position calculée ; arguments : ennemi.getNom().toUpperCase(Locale.ROOT), 625, 43.
            g.setColor(Theme.OR); g.setFont(Theme.CORPS.deriveFont(Font.BOLD, 15)); g.drawString(ennemi.getNom().toUpperCase(Locale.ROOT), 625, 43);
            // Dessine la jauge de PV avec la proportion demandée ; arguments : g, 625, 56, 285, pv, ennemi.getVieMax(), Theme.ROUGE.
            barre(g, 625, 56, 285, pv, ennemi.getVieMax(), Theme.ROUGE);
            // Choisit la couleur du prochain dessin ; arguments : Theme.TEXTE. Définit la police de caractères ; arguments : Theme.CORPS.deriveFont(12f). Dessine le texte à la position calculée ; arguments : pv + " / " + ennemi.getVieMax() + " PV  ·  Armure " + ennemi.getArmure(), 625, 92.
            g.setColor(Theme.TEXTE); g.setFont(Theme.CORPS.deriveFont(12f)); g.drawString(pv + " / " + ennemi.getVieMax() + " PV  ·  Armure " + ennemi.getArmure(), 625, 92);
        // Traite le cas où la condition précédente n’est pas satisfaite.
        } else {
            // Choisit la couleur du prochain dessin ; arguments : new Color(15, 18, 31, 215). Remplit un rectangle à coins arrondis ; arguments : 766, 20, 165, 96, 10, 10.
            g.setColor(new Color(15, 18, 31, 215)); g.fillRoundRect(766, 20, 165, 96, 10, 10);
            // Parcourt partie.getDonjon().getSalles() ; chaque élément est placé dans Salle s. Parcourt les passages autorisés de « s » ; chaque élément est placé dans String voisin.
            for (Salle s : partie.getDonjon().getSalles()) for (String voisin : s.getPassages()) {
                // Initialise « v » avec partie.getDonjon().getSalle(voisin). Choisit la couleur du prochain dessin ; arguments : new Color(92, 95, 115).
                Salle v = partie.getDonjon().getSalle(voisin); g.setColor(new Color(92, 95, 115));
                // Trace un segment entre les deux points indiqués ; arguments : 786 + s.getColonne() * 28, 39 + s.getLigne() * 26, 786 + v.getColonne() * 28, 39 + v.getLigne() * 26.
                g.drawLine(786 + s.getColonne() * 28, 39 + s.getLigne() * 26, 786 + v.getColonne() * 28, 39 + v.getLigne() * 26);
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
            // Parcourt partie.getDonjon().getSalles() ; chaque élément est placé dans Salle s.
            for (Salle s : partie.getDonjon().getSalles()) {
                // Choisit la couleur du prochain dessin ; arguments : s.getId().equals(id) ? Theme.OR : s.estVisitee() ? Theme.VERT : new Color(90, 95, 118).
                g.setColor(s.getId().equals(id) ? Theme.OR : s.estVisitee() ? Theme.VERT : new Color(90, 95, 118));
                // Remplit un rectangle aux coordonnées données ; arguments : 780 + s.getColonne() * 28, 33 + s.getLigne() * 26, 12, 12.
                g.fillRect(780 + s.getColonne() * 28, 33 + s.getLigne() * 26, 12, 12);
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
            // Choisit la couleur du prochain dessin ; arguments : Theme.TEXTE. Définit la police de caractères ; arguments : Theme.CORPS.deriveFont(10f). Dessine le texte à la position calculée ; arguments : "M  ·  Carte du donjon", 790, 108.
            g.setColor(Theme.TEXTE); g.setFont(Theme.CORPS.deriveFont(10f)); g.drawString("M  ·  Carte du donjon", 790, 108);
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Si (non (si la partie autorise encore des actions de la partie courante) et non (si la séquence visuelle est en cours du lecteur d'événements visuels)), entre dans ce traitement conditionnel.
        if (!partie.estEnCours() && !animation.estActive()) {
            // Choisit la couleur du prochain dessin ; arguments : new Color(12, 14, 27, 215). Remplit un rectangle à coins arrondis ; arguments : 258, 147, 444, 157, 16, 16.
            g.setColor(new Color(12, 14, 27, 215)); g.fillRoundRect(258, 147, 444, 157, 16, 16);
            // Choisit la couleur du prochain dessin ; arguments : Theme.OR. Définit la police de caractères ; arguments : Theme.CORPS.deriveFont(Font.BOLD, 42).
            g.setColor(Theme.OR); g.setFont(Theme.CORPS.deriveFont(Font.BOLD, 42));
            // Initialise « titre » avec partie.getEtat() == EtatPartie.VICTOIRE ? "LES SALLES S'ÉVEILLENT" : "DANS L'OUBLI".
            String titre = partie.getEtat() == EtatPartie.VICTOIRE ? "LES SALLES S'ÉVEILLENT" : "DANS L'OUBLI";
            // Définit la police de caractères ; arguments : Theme.CORPS.deriveFont(Font.BOLD, partie.getEtat() == EtatPartie.VICTOIRE ? 29 : 38).
            g.setFont(Theme.CORPS.deriveFont(Font.BOLD, partie.getEtat() == EtatPartie.VICTOIRE ? 29 : 38));
            // Dessine le texte à la position calculée ; arguments : titre, 480 - g.getFontMetrics().stringWidth(titre) / 2, 201.
            g.drawString(titre, 480 - g.getFontMetrics().stringWidth(titre) / 2, 201);
            // Définit la police de caractères ; arguments : Theme.CORPS.deriveFont(16f). Choisit la couleur du prochain dessin ; arguments : Theme.TEXTE.
            g.setFont(Theme.CORPS.deriveFont(16f)); g.setColor(Theme.TEXTE);
            // Initialise « sous titre » avec partie.getEtat() == EtatPartie.VICTOIRE ? "Victoire. Le serment du Gardien est rompu." : "Défaite. Un autre voyage vous attend.".
            String sousTitre = partie.getEtat() == EtatPartie.VICTOIRE ? "Victoire. Le serment du Gardien est rompu." : "Défaite. Un autre voyage vous attend.";
            // Dessine le texte à la position calculée ; arguments : sousTitre, 480 - g.getFontMetrics().stringWidth(sousTitre) / 2, 244.
            g.drawString(sousTitre, 480 - g.getFontMetrics().stringWidth(sousTitre) / 2, 244);
            // Définit la police de caractères ; arguments : Theme.CORPS.deriveFont(12f). Dessine le texte à la position calculée ; arguments : "Nouvelle partie ou chargement dans le menu de pause.", 324, 275.
            g.setFont(Theme.CORPS.deriveFont(12f)); g.drawString("Nouvelle partie ou chargement dans le menu de pause.", 324, 275);
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Dessine une jauge lisible et bornée. */
    // Dessine le fond et le remplissage proportionnel d’une jauge de vie.
    private static void barre(Graphics2D g, int x, int y, int w, int vie, int max, Color couleur) {
        // Choisit la couleur du prochain dessin ; arguments : new Color(40, 39, 55). Remplit un rectangle à coins arrondis ; arguments : x, y, w, 14, 6, 6.
        g.setColor(new Color(40, 39, 55)); g.fillRoundRect(x, y, w, 14, 6, 6);
        // Choisit la couleur du prochain dessin ; arguments : couleur. Remplit un rectangle à coins arrondis ; arguments : x, y, Math.max(0, w * vie / max), 14, 6, 6.
        g.setColor(couleur); g.fillRoundRect(x, y, Math.max(0, w * vie / max), 14, 6, 6);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
