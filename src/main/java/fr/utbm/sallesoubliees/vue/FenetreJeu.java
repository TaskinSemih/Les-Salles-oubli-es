// Place cette classe dans la vue, responsable de la présentation Swing et des dessins.
package fr.utbm.sallesoubliees.vue;

// Rend disponible ControleurJeu depuis fr.utbm.sallesoubliees.controleur.
import fr.utbm.sallesoubliees.controleur.ControleurJeu;
// Rend disponibles les types ou constantes de fr.utbm.sallesoubliees.modele sans répéter leur nom complet.
import fr.utbm.sallesoubliees.modele.*;
// Rend disponibles les types ou constantes de fr.utbm.sallesoubliees.persistance sans répéter leur nom complet.
import fr.utbm.sallesoubliees.persistance.*;
// Rend disponibles les types ou constantes de java.awt sans répéter leur nom complet.
import java.awt.*;
// Rend disponibles les types ou constantes de java.awt.event sans répéter leur nom complet.
import java.awt.event.*;
// Rend disponible Files depuis java.nio.file.
import java.nio.file.Files;
// Rend disponible Path depuis java.nio.file.
import java.nio.file.Path;
// Rend disponible Callable depuis java.util.concurrent.
import java.util.concurrent.Callable;
// Rend disponible ExecutionException depuis java.util.concurrent.
import java.util.concurrent.ExecutionException;
// Rend disponible Consumer depuis java.util.function.
import java.util.function.Consumer;
// Rend disponible Supplier depuis java.util.function.
import java.util.function.Supplier;
// Rend disponibles les types ou constantes de javax.swing sans répéter leur nom complet.
import javax.swing.*;
// Rend disponible FileNameExtensionFilter depuis javax.swing.filechooser.
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Fenêtre du RPG : scène dominante, commandes contextuelles et panneaux superposés.
 * Les fichiers passent par SwingWorker ; actions et interface restent sur l'EDT.
 * @serial exclude
 */
// Déclare FenetreJeu, fenêtre Swing : boutons, menus, journal et opérations de fichiers en arrière-plan. Hérite de JFrame. final interdit de créer une sous-classe.
public final class FenetreJeu extends JFrame {
    // Initialise le contrôleur de jeu avec un nouvel objet ControleurJeu.
    private final ControleurJeu controleur = new ControleurJeu();
    // Initialise le gestionnaire de fichiers avec un nouvel objet GestionnaireSauvegarde.
    private final GestionnaireSauvegarde sauvegardes = new GestionnaireSauvegarde();
    // Déclare le cache d'images de type GestionnaireRessources.
    private final GestionnaireRessources ressources;
    // Déclare la scène du jeu de type SceneJeu.
    private final SceneJeu scene;
    // Déclare le conteneur de panneaux superposés de type Couches.
    private final Couches couches;
    // Initialise le panneau entete avec Theme.panneau().
    private final JPanel entete = Theme.panneau();
    // Initialise le panneau commandes avec Theme.panneau().
    private final JPanel commandes = Theme.panneau();
    // Initialise l’étiquette lieu avec Theme.texte("LES SALLES OUBLIÉES").
    private final JLabel lieu = Theme.texte("LES SALLES OUBLIÉES");
    // Initialise l’étiquette intention avec Theme.texte("").
    private final JLabel intention = Theme.texte("");
    // Initialise l’étiquette evenement avec Theme.texte("Un serment oublié. Neuf salles à traverser.").
    private final JLabel evenement = Theme.texte("Un serment oublié. Neuf salles à traverser.");
    // Initialise le bouton attaque avec Theme.bouton("1 · Attaquer").
    private final JButton attaque = Theme.bouton("1 · Attaquer");
    // Initialise le bouton defense avec Theme.bouton("2 · Défendre").
    private final JButton defense = Theme.bouton("2 · Défendre");
    // Initialise le bouton potion avec Theme.bouton("3 · Potion").
    private final JButton potion = Theme.bouton("3 · Potion");
    // Initialise le bouton interaction avec Theme.bouton("E · Interagir").
    private final JButton interaction = Theme.bouton("E · Interagir");
    // Initialise le bouton puissante avec Theme.bouton("4 · Puissante (2 Élan)").
    private final JButton puissante = Theme.bouton("4 · Puissante (2 Élan)");
    // Initialise le bouton observer avec Theme.bouton("5 · Observer").
    private final JButton observer = Theme.bouton("5 · Observer");
    // Initialise le bouton choixSalle avec Theme.bouton("F · Découverte").
    private final JButton choixSalle = Theme.bouton("F · Découverte");
    // Initialise le bouton inventaire avec Theme.bouton("I · Inventaire").
    private final JButton inventaire = Theme.bouton("I · Inventaire");
    // Initialise le bouton carte avec Theme.bouton("M · Carte").
    private final JButton carte = Theme.bouton("M · Carte");
    // Initialise le bouton journalBouton avec Theme.bouton("J · Journal").
    private final JButton journalBouton = Theme.bouton("J · Journal");
    // Initialise le bouton pause avec Theme.bouton("Échap · Pause").
    private final JButton pause = Theme.bouton("Échap · Pause");
    // Initialise la zone de texte journal avec new JTextArea().
    private final JTextArea journal = new JTextArea();
    // Déclare le nom du panneau actuellement ouvert de type String.
    private String panneauOuvert;
    // Déclare le verrou d'opération de fichier de type boolean.
    private boolean fichierEnCours;

    /** Charge les ressources locales puis construit l'interface sur l'EDT. */
    // Construit un objet FenetreJeu : fenêtre Swing : boutons, menus, journal et opérations de fichiers en arrière-plan. Délègue à l’autre constructeur avec new GestionnaireRessources(.
    public FenetreJeu() { this(new GestionnaireRessources()); }
    /**
     * Construit l'interface avec un cache préparé par le lanceur.
     *
 * @param ressources images chargées avant l'ouverture de la fenêtre
     */
    // Construit un objet FenetreJeu : fenêtre Swing : boutons, menus, journal et opérations de fichiers en arrière-plan.
    public FenetreJeu(GestionnaireRessources ressources) {
        // Transmet au constructeur parent les paramètres "Les Salles oubliées · Le serment du Gardien".
        super("Les Salles oubliées · Le serment du Gardien");
        // Si non (si le code tourne sur le fil événementiel Swing), refuse l’opération : "Fenêtre à construire sur l'EDT.".
        if (!SwingUtilities.isEventDispatchThread()) throw new IllegalStateException("Fenêtre à construire sur l'EDT.");
        // Met à jour le cache d'images avec le cache d'images.
        this.ressources = ressources;
        // Met à jour la scène du jeu avec un nouvel objet SceneJeu avec ressources, this::commander, this::actualiser.
        scene = new SceneJeu(ressources, this::commander, this::actualiser);
        // Met à jour le conteneur de panneaux superposés avec un nouvel objet Couches avec scene.
        couches = new Couches(scene);
        // Initialise le panneau racine avec new JPanel(new BorderLayout()). Définit la couleur de fond ; arguments : Theme.FOND.
        JPanel racine = new JPanel(new BorderLayout()); racine.setBackground(Theme.FOND);
        // Choisit le gestionnaire de placement des composants ; arguments : new BorderLayout(12, 0). Définit la bordure et les marges ; arguments : BorderFactory.createEmptyBorder(9, 18, 9, 18).
        entete.setLayout(new BorderLayout(12, 0)); entete.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        // Définit la police de caractères ; arguments : Theme.CORPS.deriveFont(Font.BOLD, 17). Définit la couleur du texte ; arguments : Theme.OR. Ajoute l’élément à la collection ou au conteneur ; arguments : lieu, BorderLayout.CENTER.
        lieu.setFont(Theme.CORPS.deriveFont(Font.BOLD, 17)); lieu.setForeground(Theme.OR); entete.add(lieu, BorderLayout.CENTER);
        // Initialise le panneau outils avec Theme.panneau(). Choisit le gestionnaire de placement des composants ; arguments : new FlowLayout(FlowLayout.RIGHT, 6, 0).
        JPanel outils = Theme.panneau(); outils.setLayout(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        // Parcourt new JButton[]{inventaire, carte, journalBouton, pause} ; chaque élément est placé dans JButton b. Ajoute l’élément à la collection ou au conteneur ; arguments : b.
        for (JButton b : new JButton[]{inventaire, carte, journalBouton, pause}) outils.add(b);
        // Ajoute l’élément à la collection ou au conteneur ; arguments : outils, BorderLayout.EAST.
        entete.add(outils, BorderLayout.EAST);
        // Choisit le gestionnaire de placement des composants ; arguments : new BorderLayout(8, 5). Définit la bordure et les marges ; arguments : BorderFactory.createEmptyBorder(7, 18, 10, 18).
        commandes.setLayout(new BorderLayout(8, 5)); commandes.setBorder(BorderFactory.createEmptyBorder(7, 18, 10, 18));
        // Initialise le panneau actions avec Theme.panneau(). Choisit le gestionnaire de placement des composants ; arguments : new GridLayout(2, 4, 9, 4).
        JPanel actions = Theme.panneau(); actions.setLayout(new GridLayout(2, 4, 9, 4));
        // Parcourt new JButton[]{attaque, defense, potion, interaction, puissante, observer, choixSalle} ; chaque élément est placé dans JButton b. Ajoute l’élément à la collection ou au conteneur ; arguments : b.
        for (JButton b : new JButton[]{attaque, defense, potion, interaction, puissante, observer, choixSalle}) actions.add(b);
        // Ajoute l’élément à la collection ou au conteneur ; arguments : intention, BorderLayout.NORTH. Ajoute l’élément à la collection ou au conteneur ; arguments : actions, BorderLayout.CENTER.
        commandes.add(intention, BorderLayout.NORTH); commandes.add(actions, BorderLayout.CENTER);
        // Définit la police de caractères ; arguments : Theme.CORPS.deriveFont(12f). Définit la couleur du texte ; arguments : Theme.SECONDAIRE. Ajoute l’élément à la collection ou au conteneur ; arguments : evenement, BorderLayout.SOUTH.
        evenement.setFont(Theme.CORPS.deriveFont(12f)); evenement.setForeground(Theme.SECONDAIRE); commandes.add(evenement, BorderLayout.SOUTH);
        // Ajoute l’élément à la collection ou au conteneur ; arguments : entete, BorderLayout.NORTH. Ajoute l’élément à la collection ou au conteneur ; arguments : couches, BorderLayout.CENTER. Ajoute l’élément à la collection ou au conteneur ; arguments : commandes, BorderLayout.SOUTH.
        racine.add(entete, BorderLayout.NORTH); racine.add(couches, BorderLayout.CENTER); racine.add(commandes, BorderLayout.SOUTH);
        // Installe le panneau racine dans la fenêtre ; arguments : racine. Définit le comportement demandé à la fermeture ; arguments : DO_NOTHING_ON_CLOSE.
        setContentPane(racine); setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        // Installe la gestion des événements de fermeture de fenêtre ; arguments : new WindowAdapter() { @Override public void windowClosing(WindowEvent e) { fermer(); } }.
        addWindowListener(new WindowAdapter() { @Override public void windowClosing(WindowEvent e) { fermer(); } });
        // Installe les deux rappels de focus de la fenêtre pour suspendre et reprendre la scène.
        addWindowFocusListener(new WindowAdapter() {
            // Suspend la scène lorsque la fenêtre perd le focus. Suspend ou reprend la présentation visuelle ; arguments : true.
            @Override public void windowLostFocus(WindowEvent e) { scene.suspendre(true); }
            // Reprend la scène au retour du focus seulement si aucun panneau ni fichier ne la bloque. Suspend ou reprend la présentation visuelle ; arguments : panneauOuvert != null || fichierEnCours.
            @Override public void windowGainedFocus(WindowEvent e) { scene.suspendre(panneauOuvert != null || fichierEnCours); }
        // Ferme le bloc et termine l’instruction ou l’appel englobant.
        });
        // Définit l’aide affichée au survol ; arguments : "Attaque de l'arme équipée. L'ennemi survivant répond une seule fois.".
        attaque.setToolTipText("Attaque de l'arme équipée. L'ennemi survivant répond une seule fois.");
        // Définit l’aide affichée au survol ; arguments : "+8 d'armure pour la seule réponse ennemie qui suit.".
        defense.setToolTipText("+8 d'armure pour la seule réponse ennemie qui suit.");
        // Définit l’aide affichée au survol ; arguments : "Rend jusqu'à 35 PV ; consomme une réponse en combat.".
        potion.setToolTipText("Rend jusqu'à 35 PV ; consomme une réponse en combat.");
        // Définit l’aide affichée au survol ; arguments : "Attaque rapide : +1 Élan ; interrompt les préparations fragiles.".
        attaque.setToolTipText("Attaque rapide : +1 Élan ; interrompt les préparations fragiles.");
        // Définit l’aide affichée au survol ; arguments : "+10 puissance, coûte 2 Élan, brise la garde et l'armure renforcée. Attention aux ripostes.".
        puissante.setToolTipText("+10 puissance, coûte 2 Élan, brise la garde et l'armure renforcée. Attention aux ripostes.");
        // Définit l’aide affichée au survol ; arguments : "Révèle deux intentions et évite la riposte en créant une ouverture. Consomme un tour.".
        observer.setToolTipText("Révèle deux intentions et évite la riposte en créant une ouverture. Consomme un tour.");
        // Relie l’action de l’utilisateur au rappel indiqué ; arguments : e -> actionCombat(ActionCombat.PUISSANTE).
        puissante.addActionListener(e -> actionCombat(ActionCombat.PUISSANTE));
        // Relie l’action de l’utilisateur au rappel indiqué ; arguments : e -> actionCombat(ActionCombat.OBSERVER).
        observer.addActionListener(e -> actionCombat(ActionCombat.OBSERVER));
        // Relie l’action de l’utilisateur au rappel indiqué ; arguments : e -> ouvrirEvenement().
        choixSalle.addActionListener(e -> ouvrirEvenement());
        // Définit l’aide affichée au survol ; arguments : "Approchez une porte, un coffre ou le sanctuaire. E ou Entrée.".
        interaction.setToolTipText("Approchez une porte, un coffre ou le sanctuaire. E ou Entrée.");
        // Relie l’action de l’utilisateur au rappel indiqué ; arguments : e -> actionCombat(ActionCombat.ATTAQUER). Relie l’action de l’utilisateur au rappel indiqué ; arguments : e -> actionCombat(ActionCombat.DEFENDRE).
        attaque.addActionListener(e -> actionCombat(ActionCombat.ATTAQUER)); defense.addActionListener(e -> actionCombat(ActionCombat.DEFENDRE));
        // Relie l’action de l’utilisateur au rappel indiqué ; arguments : e -> actionCombat(ActionCombat.POTION). Relie l’action de l’utilisateur au rappel indiqué ; arguments : e -> scene.interagir().
        potion.addActionListener(e -> actionCombat(ActionCombat.POTION)); interaction.addActionListener(e -> scene.interagir());
        // Relie l’action de l’utilisateur au rappel indiqué ; arguments : e -> ouvrirInventaire(). Relie l’action de l’utilisateur au rappel indiqué ; arguments : e -> ouvrirCarte().
        inventaire.addActionListener(e -> ouvrirInventaire()); carte.addActionListener(e -> ouvrirCarte());
        // Relie l’action de l’utilisateur au rappel indiqué ; arguments : e -> ouvrirJournal(). Relie l’action de l’utilisateur au rappel indiqué ; arguments : e -> ouvrirPause().
        journalBouton.addActionListener(e -> ouvrirJournal()); pause.addActionListener(e -> ouvrirPause());
        // Autorise ou interdit la saisie dans la zone de texte ; arguments : false. Configure le retour automatique à la ligne ; arguments : true. Configure les coupures de lignes entre les mots ; arguments : true. Définit la police de caractères ; arguments : Theme.CORPS.
        journal.setEditable(false); journal.setLineWrap(true); journal.setWrapStyleWord(true); journal.setFont(Theme.CORPS);
        // Définit la couleur de fond ; arguments : Theme.FOND. Définit la couleur du texte ; arguments : Theme.TEXTE. Définit les marges intérieures ; arguments : new Insets(12, 12, 12, 12).
        journal.setBackground(Theme.FOND); journal.setForeground(Theme.TEXTE); journal.setMargin(new Insets(12, 12, 12, 12));
        // Fixe la taille minimale de la fenêtre ou du composant ; arguments : new Dimension(900, 620). Fixe les dimensions du composant ; arguments : 1280, 760. Centre la fenêtre par rapport à l’écran ou au composant fourni ; arguments : null.
        setMinimumSize(new Dimension(900, 620)); setSize(1280, 760); setLocationRelativeTo(null);
        // Affiche le menu initial.
        ouvrirAccueil();
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    /** Convertit une intention clavier/souris de la scène en commande contrôlée. */
    // Aiguille les commandes clavier et souris vers les actions et panneaux correspondants.
    private void commander(String commande) {
        // Si commande.startsWith("message:"), Met à jour le texte du composant ; arguments : commande.substring(8). Quitte la méthode sans poursuivre.
        if (commande.startsWith("message:")) { evenement.setText(commande.substring(8)); return; }
        // Si commande.equals("pause"), entre dans ce traitement conditionnel.
        if (commande.equals("pause")) {
            // Si le verrou d'opération de fichier, Quitte la méthode sans poursuivre.
            if (fichierEnCours) return;
            // Si (le nom du panneau actuellement ouvert est différent de null et non (panneauOuvert.equals("accueil"))), Ferme le panneau et rend la scène disponible. Dans le cas contraire, Affiche le menu de pause.
            if (panneauOuvert != null && !panneauOuvert.equals("accueil")) fermerPanneau(); else ouvrirPause();
            // Quitte la méthode sans poursuivre.
            return;
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Si (le verrou d'opération de fichier ou le nom du panneau actuellement ouvert est différent de null), Quitte la méthode sans poursuivre.
        if (fichierEnCours || panneauOuvert != null) return;
        // Sélectionne le traitement correspondant à la valeur examinée dans le switch.
        switch (commande) {
            // Pour "attaque" : Résout l’action une fois puis lance sa présentation visuelle. Paramètres : ActionCombat.ATTAQUER.
            case "attaque" -> actionCombat(ActionCombat.ATTAQUER);
            // Pour "defense" : Résout l’action une fois puis lance sa présentation visuelle. Paramètres : ActionCombat.DEFENDRE.
            case "defense" -> actionCombat(ActionCombat.DEFENDRE);
            // Pour "potion" : Résout l’action une fois puis lance sa présentation visuelle. Paramètres : ActionCombat.POTION.
            case "potion" -> actionCombat(ActionCombat.POTION);
            // Pour "puissante" : Résout l’action une fois puis lance sa présentation visuelle. Paramètres : ActionCombat.PUISSANTE.
            case "puissante" -> actionCombat(ActionCombat.PUISSANTE);
            // Pour "observer" : Résout l’action une fois puis lance sa présentation visuelle. Paramètres : ActionCombat.OBSERVER.
            case "observer" -> actionCombat(ActionCombat.OBSERVER);
            // Pour "decouverte" : Affiche les choix de découverte proposés par le modèle pour la salle actuelle.
            case "decouverte" -> ouvrirEvenement();
            // Pour "inventaire" : Affiche les armes possédées, leurs bonus et les boutons d’équipement hors combat.
            case "inventaire" -> ouvrirInventaire();
            // Pour "carte" : Affiche la carte et relie son choix de salle à une marche vers la porte.
            case "carte" -> ouvrirCarte();
            // Pour "journal" : Ouvre le journal détaillé dans un panneau défilant.
            case "journal" -> ouvrirJournal();
            // Pour "objet" : Dessine un objet à partir de l’atlas en mémoire.
            case "objet" -> objet();
            // Pour les autres cas : Si commande.startsWith("porte:"), Emprunte le passage demandé selon les règles du modèle ; arguments : commande.substring(6).
            default -> { if (commande.startsWith("porte:")) deplacer(commande.substring(6)); }
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Résout une fois l'action puis bloque les commandes jusqu'à la dernière animation. */
    // Résout l’action une fois puis lance sa présentation visuelle.
    private void actionCombat(ActionCombat action) {
        // Si non (libre()), Quitte la méthode sans poursuivre.
        if (!libre()) return;
        // Initialise le résultat de l'action avec controleur.resoudre(action).
        ResultatAction resultat = controleur.resoudre(action);
        // Si non (si la commande est valide du résultat de l'action), Met à jour le texte du composant ; arguments : resultat.message(). Quitte la méthode sans poursuivre.
        if (!resultat.valide()) { evenement.setText(resultat.message()); return; }
        // Lit les événements d’une commande déjà appliquée au modèle. Paramètres : resultat, () -> { ajouterJournal(resultat.message()); actualiser(); }.
        scene.jouer(resultat, () -> { ajouterJournal(resultat.message()); actualiser(); });
        // Rafraîchit les textes et les boutons de la fenêtre.
        actualiser();
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** {@return possibilité de lancer une nouvelle commande de jeu} */
    // Indique si une nouvelle commande peut partir sans panneau, fichier ni animation en cours. Renvoie controleur.getPartie() != null && panneauOuvert == null && !fichierEnCours && !scene.estOccupe().
    private boolean libre() { return controleur.getPartie() != null && panneauOuvert == null && !fichierEnCours && !scene.estOccupe(); }
    /** Présente uniquement les options de l'événement proposé par le modèle. */
    // Affiche les choix de découverte proposés par le modèle pour la salle actuelle.
    private void ouvrirEvenement() {
        // Si non (libre()), Quitte la méthode sans poursuivre.
        if (!libre()) return;
        // Initialise la variable e de type EvenementSalle avec controleur.getPartie().getEvenementSalle(). Si la variable e est égal à l’absence de valeur (null), quitte la méthode sans poursuivre.
        EvenementSalle e = controleur.getPartie().getEvenementSalle(); if (e == null) return;
        // Initialise le panneau corps avec colonne().
        JPanel corps = colonne();
        // Initialise la zone de texte texte avec new JTextArea(e.description()). Autorise ou interdit la saisie dans la zone de texte ; arguments : false. Configure le retour automatique à la ligne ; arguments : true. Configure les coupures de lignes entre les mots ; arguments : true.
        JTextArea texte = new JTextArea(e.description()); texte.setEditable(false); texte.setLineWrap(true); texte.setWrapStyleWord(true);
        // Définit le nombre de lignes visibles de texte ; arguments : 3. Définit la largeur souhaitée de la zone de texte en colonnes ; arguments : 40. Définit la police de caractères ; arguments : Theme.CORPS. Ajoute l’élément à la collection ou au conteneur ; arguments : texte.
        texte.setRows(3); texte.setColumns(40); texte.setFont(Theme.CORPS); corps.add(texte);
        // Définit la couleur de fond ; arguments : Theme.PANNEAU. Définit la couleur du texte ; arguments : Theme.TEXTE.
        texte.setBackground(Theme.PANNEAU); texte.setForeground(Theme.TEXTE);
        // Parcourt les choix autorisés de la découverte et crée pour chacun un bouton dont le libellé dépend du choix.
        for (ChoixEvenement c : e.choix()) corps.add(bouton(switch (c) {
            // Pour le choix prudent, affiche le soin de 15 PV à la bibliothèque ou la potion ailleurs.
            case PRUDENT -> e.salle().equals("bibliotheque") ? "Rune stable : +15 PV" : "Prudent : +1 potion";
            // Pour le choix risqué, affiche le soin de 35 PV ou les deux potions, avec le risque de 40 %.
            case RISQUER -> e.salle().equals("bibliotheque") ? "Rune interdite : +35 PV, brûlure 40 %" : "Risquer : +2 potions, piège 40 %";
            // Pour IGNORER : fournit le texte "Ignorer définitivement".
            case IGNORER -> "Ignorer définitivement";
        // Relie chaque choix à la fermeture du panneau puis à sa résolution unique par le modèle.
        }, () -> { fermerPanneau(); executerSimple(() -> controleur.getPartie().choisirEvenement(c)); }));
        // Affiche le panneau demandé au-dessus de la scène ; arguments : "evenement", e.titre(), corps.
        montrer("evenement", e.titre(), corps);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Emprunte seulement un passage réel après la marche visuelle. */
    // Demande ou applique un déplacement conforme aux passages du donjon.
    private void deplacer(String id) {
        // Si non (libre()), Quitte la méthode sans poursuivre.
        if (!libre()) return;
        // Initialise « ancienne » avec controleur.getPartie().getSalleActuelle().getId().
        String ancienne = controleur.getPartie().getSalleActuelle().getId();
        // Exécute ce travail sous protection d’un traitement d’erreur : Ajoute le récit au journal et actualise la dernière phrase affichée ; arguments : controleur.deplacer(id). Met à jour la vue pour la partie reçue ; arguments : controleur.getPartie(), ancienne.
        try { ajouterJournal(controleur.deplacer(id)); scene.afficher(controleur.getPartie(), ancienne); }
        // Intercepte IllegalStateException e pour traiter l’échec. Met à jour le texte du composant ; arguments : e.getMessage().
        catch (IllegalStateException e) { evenement.setText(e.getMessage()); }
        // Rafraîchit les textes et les boutons de la fenêtre.
        actualiser();
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Ouvre un coffre ou soigne au sanctuaire ; explique les refus de réutilisation. */
    // Interagit avec le coffre ou le sanctuaire selon la salle courante.
    private void objet() {
        // Si non (libre()), Quitte la méthode sans poursuivre.
        if (!libre()) return;
        // Exécute une interaction d’exploration, affiche son récit et présente les refus. Paramètres : controleur.getPartie().getSalleActuelle().estRepos() ? controleur::reposer : controleur::ouvrir.
        executerSimple(controleur.getPartie().getSalleActuelle().estRepos() ? controleur::reposer : controleur::ouvrir);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Applique une commande d'exploration sans tour supplémentaire et garde sa position visuelle. */
    // Exécute une interaction d’exploration, affiche son récit et présente les refus.
    private void executerSimple(Supplier<String> action) {
        // Exécute ce travail sous protection d’un traitement d’erreur : Ajoute le récit au journal et actualise la dernière phrase affichée ; arguments : action.get().
        try { ajouterJournal(action.get()); }
        // Intercepte IllegalStateException e pour traiter l’échec. Met à jour le texte du composant ; arguments : e.getMessage().
        catch (IllegalStateException e) { evenement.setText(e.getMessage()); }
        // Rafraîchit les textes et les boutons de la fenêtre. Demande à Swing un nouveau dessin.
        actualiser(); scene.repaint();
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Rafraîchit uniquement les contrôles, sans interrompre une séquence visuelle. */
    // Rafraîchit les textes et l’activation des boutons selon la partie et les verrous visuels.
    private void actualiser() {
        // Si la scène du jeu est égal à l’absence de valeur (null), Quitte la méthode sans poursuivre.
        if (scene == null) return;
        // Initialise la variable p de type Partie avec la partie conservée par le contrôleur du contrôleur de jeu. Initialise « existe » avec p != null.
        Partie p = controleur.getPartie(); boolean existe = p != null;
        // Initialise « libre » avec existe && panneauOuvert == null && !fichierEnCours && !scene.estOccupe().
        boolean libre = existe && panneauOuvert == null && !fichierEnCours && !scene.estOccupe();
        // Active ou désactive le composant selon la condition fournie ; arguments : libre && p.estEnCombat() && scene.estCombatAffiche().
        attaque.setEnabled(libre && p.estEnCombat() && scene.estCombatAffiche());
        // Active ou désactive le composant selon la condition fournie ; arguments : attaque.isEnabled(). Active ou désactive le composant selon la condition fournie ; arguments : libre && p.peutBoire().
        defense.setEnabled(attaque.isEnabled()); potion.setEnabled(libre && p.peutBoire());
        // Active ou désactive le composant selon la condition fournie ; arguments : attaque.isEnabled() && p.getTactique().getElan() >= 2.
        puissante.setEnabled(attaque.isEnabled() && p.getTactique().getElan() >= 2);
        // Active ou désactive le composant selon la condition fournie ; arguments : attaque.isEnabled().
        observer.setEnabled(attaque.isEnabled());
        // Active ou désactive le composant selon la condition fournie ; arguments : libre && p.getEvenementSalle() != null.
        choixSalle.setEnabled(libre && p.getEvenementSalle() != null);
        // Active ou désactive le composant selon la condition fournie ; arguments : libre && p.estEnCours() && !p.estEnCombat().
        interaction.setEnabled(libre && p.estEnCours() && !p.estEnCombat());
        // Active ou désactive le composant selon la condition fournie ; arguments : existe && panneauOuvert == null && !fichierEnCours && !scene.estOccupe().
        inventaire.setEnabled(existe && panneauOuvert == null && !fichierEnCours && !scene.estOccupe());
        // Active ou désactive le composant selon la condition fournie ; arguments : existe && panneauOuvert == null && !fichierEnCours && !scene.estOccupe().
        carte.setEnabled(existe && panneauOuvert == null && !fichierEnCours && !scene.estOccupe());
        // Active ou désactive le composant selon la condition fournie ; arguments : existe && panneauOuvert == null && !fichierEnCours.
        journalBouton.setEnabled(existe && panneauOuvert == null && !fichierEnCours);
        // Active ou désactive le composant selon la condition fournie ; arguments : existe && panneauOuvert == null && !fichierEnCours.
        pause.setEnabled(existe && panneauOuvert == null && !fichierEnCours);
        // Si existe, entre dans ce traitement conditionnel.
        if (existe) {
            // Met à jour le texte du composant ; arguments : p.getSalleActuelle().getNom().toUpperCase(java.util.Locale.ROOT) + "  /  " + (p.estEnCombat() ? "RENCONTRE" : "EXPLORATION").
            lieu.setText(p.getSalleActuelle().getNom().toUpperCase(java.util.Locale.ROOT) + "  /  " + (p.estEnCombat() ? "RENCONTRE" : "EXPLORATION"));
            // Si le verrou d'opération de fichier, Met à jour le texte du composant ; arguments : "Opération de fichier en cours…".
            if (fichierEnCours) intention.setText("Opération de fichier en cours…");
            // Si si une présentation, animation ou marche automatique bloque la scène de la scène du jeu, Met à jour le texte du composant ; arguments : "L'action se déroule…".
            else if (scene.estOccupe()) intention.setText("L'action se déroule…");
            // Si non (si la partie autorise encore des actions de la variable p), Met à jour le texte du composant ; arguments : p.getEtat() == EtatPartie.VICTOIRE ? "Victoire · Les salles sont libérées." : "Défaite · Votre voyage peut recommencer.".
            else if (!p.estEnCours()) intention.setText(p.getEtat() == EtatPartie.VICTOIRE ? "Victoire · Les salles sont libérées." : "Défaite · Votre voyage peut recommencer.");
            // Si si un combat est actif de la variable p, Met à jour le texte du composant ; arguments : "À VOUS  ·  " + p.getTactique().intention().
            else if (p.estEnCombat()) intention.setText("À VOUS  ·  " + p.getTactique().intention());
            // Dans le cas contraire, Met à jour le texte du composant ; arguments : "Flèches / ZQSD : marcher   ·   E / Entrée : interagir   ·   Cliquez une porte pour la rejoindre".
            else intention.setText("Flèches / ZQSD : marcher   ·   E / Entrée : interagir   ·   Cliquez une porte pour la rejoindre");
            // Définit l’aide affichée au survol ; arguments : intention.getText().
            intention.setToolTipText(intention.getText());
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Conserve le détail du tour et affiche seulement sa dernière phrase dans la scène. */
    // Ajoute le récit au journal et affiche sa dernière phrase sous les commandes.
    private void ajouterJournal(String message) {
        // Ajoute le récit à la fin du journal ; arguments : message + "\n\n".
        journal.append(message + "\n\n");
        // Si journal.getDocument().getLength() est supérieur à 30_000, Met à jour le texte du composant ; arguments : journal.getText().substring(10_000).
        if (journal.getDocument().getLength() > 30_000) journal.setText(journal.getText().substring(10_000));
        // Place le curseur à la fin pour montrer le dernier récit ; arguments : journal.getDocument().getLength().
        journal.setCaretPosition(journal.getDocument().getLength());
        // Met à jour le texte du composant ; arguments : message.substring(message.lastIndexOf('\n') + 1). Définit l’aide affichée au survol ; arguments : message.replace('\n', ' ').
        evenement.setText(message.substring(message.lastIndexOf('\n') + 1)); evenement.setToolTipText(message.replace('\n', ' '));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Ouvre l'accueil illustré en laissant le héros et le décor visibles à droite. */
    // Construit le menu initial sur le décor du jeu.
    private void ouvrirAccueil() {
        // Revient au décor initial et retire toute partie de la scène. Modifie la visibilité du composant ; arguments : false. Modifie la visibilité du composant ; arguments : false. Met à jour le nom du panneau actuellement ouvert avec le texte "accueil".
        scene.accueil(); entete.setVisible(false); commandes.setVisible(false); panneauOuvert = "accueil";
        // Initialise le panneau panneau avec Theme.panneau(). Choisit le gestionnaire de placement des composants ; arguments : new BoxLayout(panneau, BoxLayout.Y_AXIS).
        JPanel panneau = Theme.panneau(); panneau.setLayout(new BoxLayout(panneau, BoxLayout.Y_AXIS));
        // Définit la bordure et les marges ; arguments : BorderFactory.createEmptyBorder(25, 28, 25, 28).
        panneau.setBorder(BorderFactory.createEmptyBorder(25, 28, 25, 28));
        // Initialise l’étiquette titre avec Theme.texte("<html>LES SALLES<br>OUBLIÉES</html>"). Définit la police de caractères ; arguments : new Font(Font.SERIF, Font.BOLD, 39). Définit la couleur du texte ; arguments : Theme.OR.
        JLabel titre = Theme.texte("<html>LES SALLES<br>OUBLIÉES</html>"); titre.setFont(new Font(Font.SERIF, Font.BOLD, 39)); titre.setForeground(Theme.OR);
        // Initialise l’étiquette sousTitre avec Theme.texte("LE SERMENT DU GARDIEN"). Définit la police de caractères ; arguments : Theme.CORPS.deriveFont(12f).
        JLabel sousTitre = Theme.texte("LE SERMENT DU GARDIEN"); sousTitre.setFont(Theme.CORPS.deriveFont(12f));
        // Initialise l’étiquette texte avec Theme.texte("<html>Une forteresse endormie.<br>Une lame. Une dernière lumière.</html>").
        JLabel texte = Theme.texte("<html>Une forteresse endormie.<br>Une lame. Une dernière lumière.</html>");
        // Parcourt new JComponent[]{sousTitre, titre, texte, bouton("Nouvelle partie", this::nouvellePartie), bouton("Charger une partie", this::charger), bouton("Quitter", this::fermer)} ; chaque élément est placé dans JComponent c.
        for (JComponent c : new JComponent[]{sousTitre, titre, texte, bouton("Nouvelle partie", this::nouvellePartie), bouton("Charger une partie", this::charger), bouton("Quitter", this::fermer)}) {
            // Définit l’alignement horizontal dans le panneau ; arguments : LEFT_ALIGNMENT. Ajoute l’élément à la collection ou au conteneur ; arguments : c. Ajoute l’élément à la collection ou au conteneur ; arguments : Box.createVerticalStrut(16).
            c.setAlignmentX(LEFT_ALIGNMENT); panneau.add(c); panneau.add(Box.createVerticalStrut(16));
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Affiche le panneau demandé au-dessus de la scène ; arguments : panneau, true. Rafraîchit les textes et les boutons de la fenêtre.
        couches.montrer(panneau, true); actualiser();
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Démarre une partie après une confirmation si un état existe déjà. */
    // Crée une partie indépendante et initialise son affichage.
    private void nouvellePartie() {
        // Si non (confirmerAbandon()), Quitte la méthode sans poursuivre.
        if (!confirmerAbandon()) return;
        // Arrête les travaux visuels et leur horloge. Crée une partie indépendante et initialise son affichage. Met à jour le texte du composant ; arguments : "".
        scene.arreter(); controleur.nouvellePartie(); journal.setText("");
        // Modifie la visibilité du composant ; arguments : true. Modifie la visibilité du composant ; arguments : true. Ferme le panneau et rend la scène disponible.
        entete.setVisible(true); commandes.setVisible(true); fermerPanneau();
        // Ajoute le récit au journal et actualise la dernière phrase affichée ; arguments : "L'air est froid. Rejoignez la porte de l'Ossuaire, à l'est.".
        ajouterJournal("L'air est froid. Rejoignez la porte de l'Ossuaire, à l'est.");
        // Met à jour la vue pour la partie reçue ; arguments : controleur.getPartie(), null. Redonne le focus clavier à la scène. Rafraîchit les textes et les boutons de la fenêtre.
        scene.afficher(controleur.getPartie(), null); scene.requestFocusInWindow(); actualiser();
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Présente l'inventaire en cartes avec icônes, bonus et équipement courant. */
    // Affiche les armes possédées, leurs bonus et les boutons d’équipement hors combat.
    private void ouvrirInventaire() {
        // Si (la partie conservée par le contrôleur du contrôleur de jeu est égal à l’absence de valeur (null) ou si une présentation, animation ou marche automatique bloque la scène de la scène du jeu ou le verrou d'opération de fichier), Quitte la méthode sans poursuivre.
        if (controleur.getPartie() == null || scene.estOccupe() || fichierEnCours) return;
        // Initialise le panneau corps avec colonne(). Initialise la variable h de type Heros avec controleur.getPartie().getHeros().
        JPanel corps = colonne(); Heros h = controleur.getPartie().getHeros();
        // Parcourt h.getInventaire().getArmes() ; chaque élément est placé dans Arme arme.
        for (Arme arme : h.getInventaire().getArmes()) {
            // Initialise le panneau ligne avec Theme.panneau(). Choisit le gestionnaire de placement des composants ; arguments : new BorderLayout(12, 0). Définit la bordure et les marges ; arguments : Theme.bordure(arme == h.getInventaire().getEquipee() ? "ÉQUIPÉE" : "ARME").
            JPanel ligne = Theme.panneau(); ligne.setLayout(new BorderLayout(12, 0)); ligne.setBorder(Theme.bordure(arme == h.getInventaire().getEquipee() ? "ÉQUIPÉE" : "ARME"));
            // Initialise l’étiquette nom avec Theme.texte("<html><b>" + arme + "</b><br>Attaque totale : " + (h.getAttaqueBase() + arme.getBonus()) + "</html>").
            JLabel nom = Theme.texte("<html><b>" + arme + "</b><br>Attaque totale : " + (h.getAttaqueBase() + arme.getBonus()) + "</html>");
            // Associe au composant une icône extraite des images locales ; arguments : ressources.icone(6, 60). Ajoute l’élément à la collection ou au conteneur ; arguments : nom, BorderLayout.CENTER.
            nom.setIcon(ressources.icone(6, 60)); ligne.add(nom, BorderLayout.CENTER);
            // Initialise le bouton choix avec bouton("Équiper", () -> { executerSimple(() -> controleur.equiper(arme)); ouvrirInventaire(); }).
            JButton choix = bouton("Équiper", () -> { executerSimple(() -> controleur.equiper(arme)); ouvrirInventaire(); });
            // Active ou désactive le composant selon la condition fournie ; arguments : controleur.getPartie().estEnCours() && !controleur.getPartie().estEnCombat() && arme != h.getInventaire().getEquipee().
            choix.setEnabled(controleur.getPartie().estEnCours() && !controleur.getPartie().estEnCombat() && arme != h.getInventaire().getEquipee());
            // Définit l’aide affichée au survol ; arguments : "Hors combat : remplace le bonus, sans cumul.". Ajoute l’élément à la collection ou au conteneur ; arguments : choix, BorderLayout.EAST. Ajoute l’élément à la collection ou au conteneur ; arguments : ligne. Ajoute l’élément à la collection ou au conteneur ; arguments : Box.createVerticalStrut(8).
            choix.setToolTipText("Hors combat : remplace le bonus, sans cumul."); ligne.add(choix, BorderLayout.EAST); corps.add(ligne); corps.add(Box.createVerticalStrut(8));
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Initialise l’étiquette potions avec Theme.texte(h.getInventaire().getPotions() + " potions  ·  +35 PV, sans dépasser 100"). Associe au composant une icône extraite des images locales ; arguments : ressources.icone(14, 42). Ajoute l’élément à la collection ou au conteneur ; arguments : potions.
        JLabel potions = Theme.texte(h.getInventaire().getPotions() + " potions  ·  +35 PV, sans dépasser 100"); potions.setIcon(ressources.icone(14, 42)); corps.add(potions);
        // Si controleur.getPartie().estEnCombat(), Ajoute l’élément à la collection ou au conteneur ; arguments : Theme.texte("Terminez le combat pour changer d'arme.").
        if (controleur.getPartie().estEnCombat()) corps.add(Theme.texte("Terminez le combat pour changer d'arme."));
        // Affiche le panneau demandé au-dessus de la scène ; arguments : "inventaire", "VOTRE ÉQUIPEMENT", corps.
        montrer("inventaire", "VOTRE ÉQUIPEMENT", corps);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Affiche la carte complète en panneau secondaire ; son choix lance une marche réelle. */
    // Affiche la carte et relie son choix de salle à une marche vers la porte.
    private void ouvrirCarte() {
        // Si (la partie conservée par le contrôleur du contrôleur de jeu est égal à l’absence de valeur (null) ou si une présentation, animation ou marche automatique bloque la scène de la scène du jeu ou le verrou d'opération de fichier), Quitte la méthode sans poursuivre.
        if (controleur.getPartie() == null || scene.estOccupe() || fichierEnCours) return;
        // Initialise « carte complete » avec un nouvel objet CarteDonjon avec id -> { fermerPanneau(); scene.marcherVersPorte(id); }.
        CarteDonjon carteComplete = new CarteDonjon(id -> { fermerPanneau(); scene.marcherVersPorte(id); });
        // Met à jour la vue pour la partie reçue ; arguments : controleur.getPartie(), false. Fixe la taille souhaitée pour le placement ; arguments : new Dimension(680, 300).
        carteComplete.afficher(controleur.getPartie(), false); carteComplete.setPreferredSize(new Dimension(680, 300));
        // Affiche le panneau demandé au-dessus de la scène ; arguments : "carte", "LES NEUF SALLES", carteComplete.
        montrer("carte", "LES NEUF SALLES", carteComplete);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Affiche le journal détaillé seulement à la demande. */
    // Ouvre le journal détaillé dans un panneau défilant.
    private void ouvrirJournal() {
        // Si (la partie conservée par le contrôleur du contrôleur de jeu est égal à l’absence de valeur (null) ou le verrou d'opération de fichier), Quitte la méthode sans poursuivre.
        if (controleur.getPartie() == null || fichierEnCours) return;
        // Initialise le panneau défilant scroll avec new JScrollPane(journal). Fixe la taille souhaitée pour le placement ; arguments : new Dimension(620, 300).
        JScrollPane scroll = new JScrollPane(journal); scroll.setPreferredSize(new Dimension(620, 300));
        // Affiche le panneau demandé au-dessus de la scène ; arguments : "journal", "JOURNAL D'EXPLORATION", scroll.
        montrer("journal", "JOURNAL D'EXPLORATION", scroll);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Réunit fichiers, options et abandon dans un menu de pause. */
    // Affiche reprise, fichiers, options et abandon en suspendant la scène.
    private void ouvrirPause() {
        // Si (la partie conservée par le contrôleur du contrôleur de jeu est égal à l’absence de valeur (null) ou le verrou d'opération de fichier), Quitte la méthode sans poursuivre.
        if (controleur.getPartie() == null || fichierEnCours) return;
        // Initialise le panneau corps avec colonne().
        JPanel corps = colonne();
        // Initialise le bouton sauver avec bouton("Sauvegarder", this::sauvegarder), charger = bouton("Charger", this::charger).
        JButton sauver = bouton("Sauvegarder", this::sauvegarder), charger = bouton("Charger", this::charger);
        // Active ou désactive le composant selon la condition fournie ; arguments : !scene.estOccupe(). Active ou désactive le composant selon la condition fournie ; arguments : !scene.estOccupe().
        sauver.setEnabled(!scene.estOccupe()); charger.setEnabled(!scene.estOccupe());
        // Si si une présentation, animation ou marche automatique bloque la scène de la scène du jeu, Ajoute l’élément à la collection ou au conteneur ; arguments : Theme.texte("Reprenez pour terminer l'animation avant une opération de fichier.").
        if (scene.estOccupe()) corps.add(Theme.texte("Reprenez pour terminer l'animation avant une opération de fichier."));
        // Parcourt new JButton[]{bouton("Reprendre", this::fermerPanneau), sauver, charger, bouton("Options", this::ouvrirOptions), bouton("Nouvelle partie", this::nouvellePartie), bouton("Retour au menu", () -> { ; chaque élément est placé dans JButton b.
        for (JButton b : new JButton[]{bouton("Reprendre", this::fermerPanneau), sauver, charger, bouton("Options", this::ouvrirOptions), bouton("Nouvelle partie", this::nouvellePartie), bouton("Retour au menu", () -> {
            // Si confirmerAbandon(), Arrête les travaux visuels et leur horloge. Retire la partie courante du contrôleur. Affiche le menu initial.
            if (confirmerAbandon()) { scene.arreter(); controleur.abandonner(); ouvrirAccueil(); }
        // Ajoute le bouton Quitter, puis place les boutons du menu verticalement avec un espacement.
        }), bouton("Quitter", this::fermer)}) { b.setAlignmentX(LEFT_ALIGNMENT); corps.add(b); corps.add(Box.createVerticalStrut(7)); }
        // Affiche le panneau demandé au-dessus de la scène ; arguments : "pause", "UNE HALTE DANS L'OMBRE", corps.
        montrer("pause", "UNE HALTE DANS L'OMBRE", corps);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Propose la vitesse réduite sans modifier les règles ni le format de sauvegarde. */
    // Affiche l’option de réduction des animations, indépendante des règles.
    private void ouvrirOptions() {
        // Initialise le panneau corps avec colonne(). Initialise la case à cocher reduites avec new JCheckBox("Animations réduites et accélérées", scene.estReduite()).
        JPanel corps = colonne(); JCheckBox reduites = new JCheckBox("Animations réduites et accélérées", scene.estReduite());
        // Définit la couleur de fond ; arguments : Theme.PANNEAU. Définit la couleur du texte ; arguments : Theme.TEXTE. Définit la police de caractères ; arguments : Theme.CORPS.
        reduites.setBackground(Theme.PANNEAU); reduites.setForeground(Theme.TEXTE); reduites.setFont(Theme.CORPS);
        // Relie l’action de l’utilisateur au rappel indiqué ; arguments : e -> scene.setReduite(reduites.isSelected()). Ajoute l’élément à la collection ou au conteneur ; arguments : reduites. Ajoute l’élément à la collection ou au conteneur ; arguments : Box.createVerticalStrut(16).
        reduites.addActionListener(e -> scene.setReduite(reduites.isSelected())); corps.add(reduites); corps.add(Box.createVerticalStrut(16));
        // Ajoute l’élément à la collection ou au conteneur ; arguments : Theme.texte("<html>Les règles et les dégâts restent identiques.<br>Cette version est silencieuse : aucun son n'est inclus.</html>").
        corps.add(Theme.texte("<html>Les règles et les dégâts restent identiques.<br>Cette version est silencieuse : aucun son n'est inclus.</html>"));
        // Affiche le panneau demandé au-dessus de la scène ; arguments : "options", "OPTIONS", corps.
        montrer("options", "OPTIONS", corps);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Construit un panneau modal dans la fenêtre, avec un bouton de fermeture accessible. */
    // Installe le panneau demandé au-dessus de la scène.
    private void montrer(String nom, String titre, JComponent corps) {
        // Met à jour le nom du panneau actuellement ouvert avec le nom. Suspend ou reprend la présentation visuelle ; arguments : true.
        panneauOuvert = nom; scene.suspendre(true);
        // Initialise le panneau panneau avec Theme.panneau(). Choisit le gestionnaire de placement des composants ; arguments : new BorderLayout(14, 14). Définit la bordure et les marges ; arguments : BorderFactory.createCompoundBorder(Theme.bordure(titre), BorderFactory.createEmptyBorder(8, 10, 8, 10)).
        JPanel panneau = Theme.panneau(); panneau.setLayout(new BorderLayout(14, 14)); panneau.setBorder(BorderFactory.createCompoundBorder(Theme.bordure(titre), BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        // Initialise le panneau défilant scroll avec new JScrollPane(corps). Définit la bordure et les marges ; arguments : null. Définit la couleur de fond ; arguments : Theme.PANNEAU.
        JScrollPane scroll = new JScrollPane(corps); scroll.setBorder(null); scroll.getViewport().setBackground(Theme.PANNEAU);
        // Fixe la taille souhaitée pour le placement ; arguments : new Dimension(650, Math.min(380, Math.max(160, corps.getPreferredSize().height + 8))).
        scroll.setPreferredSize(new Dimension(650, Math.min(380, Math.max(160, corps.getPreferredSize().height + 8))));
        // Ajoute l’élément à la collection ou au conteneur ; arguments : scroll, BorderLayout.CENTER. Ajoute l’élément à la collection ou au conteneur ; arguments : bouton("Fermer · Échap", this::fermerPanneau), BorderLayout.SOUTH.
        panneau.add(scroll, BorderLayout.CENTER); panneau.add(bouton("Fermer · Échap", this::fermerPanneau), BorderLayout.SOUTH);
        // Affiche le panneau demandé au-dessus de la scène ; arguments : panneau, false. Rafraîchit les textes et les boutons de la fenêtre.
        couches.montrer(panneau, false); actualiser();
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Ferme un panneau et reprend l'horloge sans créer un nouveau timer. */
    // Retire le panneau et reprend la scène selon le focus et l’état des fichiers.
    private void fermerPanneau() {
        // Met à jour le nom du panneau actuellement ouvert avec l’absence de valeur (null). Retire le panneau superposé sans détruire la scène. Suspend ou reprend la présentation visuelle ; arguments : !isFocused() || fichierEnCours. Redonne le focus clavier à la scène. Rafraîchit les textes et les boutons de la fenêtre.
        panneauOuvert = null; couches.cacher(); scene.suspendre(!isFocused() || fichierEnCours); scene.requestFocusInWindow(); actualiser();
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Crée un panneau vertical homogène. */
    // Crée un panneau dont les composants seront empilés verticalement. Initialise le panneau p avec Theme.panneau(). Choisit le gestionnaire de placement des composants ; arguments : new BoxLayout(p, BoxLayout.Y_AXIS). Renvoie la variable p.
    private static JPanel colonne() { JPanel p = Theme.panneau(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)); return p; }
    /** Crée un bouton d'action de menu. */
    // Crée ou recherche un bouton selon le contexte de cette classe. Initialise le bouton b avec Theme.bouton(texte). Relie l’action de l’utilisateur au rappel indiqué ; arguments : e -> action.run(). Renvoie b.
    private static JButton bouton(String texte, Runnable action) { JButton b = Theme.bouton(texte); b.addActionListener(e -> action.run()); return b; }
    /** Confirme l'abandon même si la dernière action est encore animée. */
    // Suspend la scène, demande confirmation de l’abandon et renvoie la réponse du joueur.
    private boolean confirmerAbandon() {
        // Si la partie conservée par le contrôleur du contrôleur de jeu est égal à l’absence de valeur (null), renvoie vrai.
        if (controleur.getPartie() == null) return true;
        // Suspend ou reprend la présentation visuelle ; arguments : true.
        scene.suspendre(true);
        // Initialise « accord » avec JOptionPane.showConfirmDialog(this, "Abandonner cette partie ? Les progrès non sauvegardés seront perdus.", "Confirmer l'abandon", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION.
        boolean accord = JOptionPane.showConfirmDialog(this, "Abandonner cette partie ? Les progrès non sauvegardés seront perdus.", "Confirmer l'abandon", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        // Suspend ou reprend la présentation visuelle ; arguments : panneauOuvert != null || fichierEnCours || !isFocused(). Renvoie accord.
        scene.suspendre(panneauOuvert != null || fichierEnCours || !isFocused()); return accord;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Ferme après confirmation et laisse finir une écriture déjà engagée. */
    // Ferme la fenêtre si aucun fichier n’est en cours et si l’abandon est confirmé. Si (non (le verrou d'opération de fichier) et confirmerAbandon()), Libère le contexte graphique ou la fenêtre utilisé.
    private void fermer() { if (!fichierEnCours && confirmerAbandon()) dispose(); }
    /** Arrête le timer également lors d'une fermeture programmatique. */
    // Arrête les travaux visuels avant de libérer la fenêtre Swing. Si la scène du jeu est différent de null, Arrête les travaux visuels et leur horloge. Libère le contexte graphique ou la fenêtre utilisé.
    @Override public void dispose() { if (scene != null) scene.arreter(); super.dispose(); }
    /** Prépare un chemin par défaut dans le dossier de sauvegardes ignoré par Git. */
    // Prépare un sélecteur limité aux sauvegardes JSON avec un chemin par défaut.
    private JFileChooser choisirFichier() {
        // Initialise le choix courant avec new JFileChooser(). Définit le titre du sélecteur de fichier ; arguments : "Les Salles oubliées · Sauvegarde".
        JFileChooser choix = new JFileChooser(); choix.setDialogTitle("Les Salles oubliées · Sauvegarde");
        // Limite la sélection aux fichiers correspondant au filtre ; arguments : new FileNameExtensionFilter("Sauvegardes JSON", "json").
        choix.setFileFilter(new FileNameExtensionFilter("Sauvegardes JSON", "json"));
        // Prépare le fichier sélectionné dans le dialogue ; arguments : Path.of("sauvegardes", "partie.save.json").toFile(). Renvoie le choix courant.
        choix.setSelectedFile(Path.of("sauvegardes", "partie.save.json").toFile()); return choix;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Capture le seul état stable du modèle après confirmation de remplacement. */
    // Enregistre un instantané validé en passant par un fichier temporaire ou une tâche de fond.
    private void sauvegarder() {
        // Si (si une présentation, animation ou marche automatique bloque la scène de la scène du jeu ou le verrou d'opération de fichier), Quitte la méthode sans poursuivre.
        if (scene.estOccupe() || fichierEnCours) return;
        // Initialise le choix courant avec choisirFichier(). Si choix.showSaveDialog(this) est différent de JFileChooser.APPROVE_OPTION, quitte la méthode sans poursuivre.
        JFileChooser choix = choisirFichier(); if (choix.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        // Initialise le chemin du fichier avec choix.getSelectedFile().toPath().
        Path fichier = choix.getSelectedFile().toPath();
        // Si non (fichier.getFileName().toString().toLowerCase(java.util.Locale.ROOT).endsWith(".json")), Met à jour le chemin du fichier avec fichier.resolveSibling(fichier.getFileName() + ".save.json").
        if (!fichier.getFileName().toString().toLowerCase(java.util.Locale.ROOT).endsWith(".json")) fichier = fichier.resolveSibling(fichier.getFileName() + ".save.json");
        // Si (Files.exists(fichier) et JOptionPane.showConfirmDialog(this, "Remplacer cette sauvegarde existante ?", "Remplacer", JOptionPane.YES_NO_OPTION) est différent de JOptionPane.YES_OPTION), Quitte la méthode sans poursuivre.
        if (Files.exists(fichier) && JOptionPane.showConfirmDialog(this, "Remplacer cette sauvegarde existante ?", "Remplacer", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        // Initialise la cible avec le chemin du fichier. Initialise l'état courant avec controleur.capturer().
        Path cible = fichier; EtatSauvegarde etat = controleur.capturer();
        // Écrit en arrière-plan l’instantané capturé ; au succès, ferme le panneau et inscrit le fichier dans le journal.
        executerFichier(() -> { sauvegardes.sauvegarder(cible, etat); return cible; }, p -> { fermerPanneau(); ajouterJournal("Partie sauvegardée : " + p.getFileName()); });
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Reconstruit hors EDT et adopte uniquement après validation et confirmation. */
    // Lit puis valide une nouvelle partie avant de l’adopter dans l’interface.
    private void charger() {
        // Si (si une présentation, animation ou marche automatique bloque la scène de la scène du jeu ou le verrou d'opération de fichier), Quitte la méthode sans poursuivre.
        if (scene.estOccupe() || fichierEnCours) return;
        // Initialise le choix courant avec choisirFichier(). Si choix.showOpenDialog(this) est différent de JFileChooser.APPROVE_OPTION, quitte la méthode sans poursuivre.
        JFileChooser choix = choisirFichier(); if (choix.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        // Initialise le chemin du fichier avec choix.getSelectedFile().toPath().
        Path fichier = choix.getSelectedFile().toPath();
        // Prépare le rappel qui sera exécuté pour l’événement ou l’élément trouvé.
        executerFichier(() -> sauvegardes.charger(fichier), chargee -> {
            // Si non (confirmerAbandon()), Quitte la méthode sans poursuivre.
            if (!confirmerAbandon()) return;
            // Remplace la partie courante par la partie chargée et validée ; arguments : chargee. Met à jour le texte du composant ; arguments : "". Modifie la visibilité du composant ; arguments : true. Modifie la visibilité du composant ; arguments : true.
            controleur.adopter(chargee); journal.setText(""); entete.setVisible(true); commandes.setVisible(true);
            // Ferme le panneau et rend la scène disponible. Met à jour la vue pour la partie reçue ; arguments : chargee, null. Ajoute le récit au journal et actualise la dernière phrase affichée ; arguments : "Partie chargée. Vous reprenez sur un emplacement sûr de la salle.".
            fermerPanneau(); scene.afficher(chargee, null); ajouterJournal("Partie chargée. Vous reprenez sur un emplacement sûr de la salle.");
        // Ferme le bloc et termine l’instruction ou l’appel englobant.
        });
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Exécute les I/O hors EDT ; le panneau est bloqué jusqu'à la publication du résultat. */
    // Lance les entrées-sorties hors du fil Swing et publie leur résultat sur ce fil.
    private <T> void executerFichier(Callable<T> travail, Consumer<T> succes) {
        // Met à jour le verrou d'opération de fichier avec vrai. Suspend ou reprend la présentation visuelle ; arguments : true. Active ou désactive le panneau superposé et ses composants enfants. Paramètres : false. Rafraîchit les textes et les boutons de la fenêtre.
        fichierEnCours = true; scene.suspendre(true); couches.setEnabledRecursif(false); actualiser();
        // Crée une tâche SwingWorker : travail de fichier en arrière-plan, résultat publié sur le fil Swing.
        new SwingWorker<T, Void>() {
            // Exécute l’opération lente de fichier sur le fil de travail SwingWorker. Renvoie travail.call().
            @Override protected T doInBackground() throws Exception { return travail.call(); }
            // Récupère le résultat sur le fil Swing, présente l’erreur éventuelle et libère les commandes.
            @Override protected void done() {
                // Exécute ce travail sous protection d’un traitement d’erreur : Transmet la valeur au rappel fourni ; arguments : get().
                try { succes.accept(get()); }
                // Intercepte InterruptedException e pour traiter l’échec. Rétablit le marqueur d’interruption du fil courant. Présente une boîte de dialogue avec un message de fichier compréhensible. Paramètres : "Opération interrompue.".
                catch (InterruptedException e) { Thread.currentThread().interrupt(); afficherErreur("Opération interrompue."); }
                // Intercepte ExecutionException e pour traiter l’échec. Présente une boîte de dialogue avec un message de fichier compréhensible. Paramètres : e.getCause() instanceof ExceptionSauvegarde ? e.getCause().getMessage() : "L'opération a échoué. La partie actuelle est conservée.".
                catch (ExecutionException e) { afficherErreur(e.getCause() instanceof ExceptionSauvegarde ? e.getCause().getMessage() : "L'opération a échoué. La partie actuelle est conservée."); }
                // Exécute ce nettoyage dans tous les cas, même après une erreur ou un retour anticipé.
                finally {
                    // Met à jour le verrou d'opération de fichier avec faux. Active ou désactive le panneau superposé et ses composants enfants. Paramètres : true.
                    fichierEnCours = false; couches.setEnabledRecursif(true);
                    // Si "pause".equals(panneauOuvert), Affiche le menu de pause.
                    if ("pause".equals(panneauOuvert)) ouvrirPause();
                    // Suspend ou reprend la présentation visuelle ; arguments : panneauOuvert != null || !isFocused(). Rafraîchit les textes et les boutons de la fenêtre.
                    scene.suspendre(panneauOuvert != null || !isFocused()); actualiser();
                // Ferme le bloc ou la liste de données commencé plus haut.
                }
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
        // Démarre la tâche SwingWorker préparée ; les entrées-sorties quittent ainsi le fil de l’interface.
        }.execute();
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Affiche une erreur courte sans trace technique. */
    // Présente une boîte de dialogue avec un message de fichier compréhensible. Affiche le message d’erreur dans une boîte de dialogue Swing ; arguments : this, message, "Sauvegarde / chargement", JOptionPane.ERROR_MESSAGE.
    private void afficherErreur(String message) { JOptionPane.showMessageDialog(this, message, "Sauvegarde / chargement", JOptionPane.ERROR_MESSAGE); }

    /**
     * Deux couches redimensionnées : scène et panneau scrollable, sans autre timer.
     * @serial exclude
     */
    // Déclare Couches, conteneur superposant la scène et un panneau de menu, avec placement au redimensionnement. Hérite de JLayeredPane. final interdit de créer une sous-classe.
    private static final class Couches extends JLayeredPane {
        // Déclare la scène du jeu de type SceneJeu.
        private final SceneJeu scene;
        // Déclare « panneau » de type JPanel.
        private JPanel panneau;
        // Déclare le marqueur d'affichage de l'accueil de type boolean.
        private boolean accueil;
        /** Installe la scène dans la couche basse. */
        // Construit un objet Couches : conteneur superposant la scène et un panneau de menu, avec placement au redimensionnement. Met à jour la scène du jeu avec la scène du jeu. Ajoute l’élément à la collection ou au conteneur ; arguments : scene, Integer.valueOf(0).
        Couches(SceneJeu scene) { this.scene = scene; add(scene, Integer.valueOf(0)); }
        /** Remplace le seul panneau superposé. */
        // Installe le panneau demandé au-dessus de la scène. Retire le panneau superposé sans détruire la scène. Met à jour « panneau » avec nouveau. Met à jour le marqueur d'affichage de l'accueil avec le marqueur d'affichage de l'accueil. Ajoute l’élément à la collection ou au conteneur ; arguments : panneau, Integer.valueOf(100). Demande à Swing de recalculer le placement des composants. Demande à Swing un nouveau dessin.
        void montrer(JPanel nouveau, boolean accueil) { cacher(); panneau = nouveau; this.accueil = accueil; add(panneau, Integer.valueOf(100)); revalidate(); repaint(); }
        /** Retire le panneau sans détruire la scène. */
        // Retire le panneau superposé sans détruire la scène. Si panneau est différent de null, Retire l’élément ou le composant demandé ; arguments : panneau. Met à jour « panneau » avec l’absence de valeur (null). Demande à Swing de recalculer le placement des composants. Demande à Swing un nouveau dessin.
        void cacher() { if (panneau != null) remove(panneau); panneau = null; revalidate(); repaint(); }
        /** Bloque temporairement les boutons lors des I/O. */
        // Active ou désactive le panneau superposé et ses composants enfants. Si panneau est différent de null, Parcourt récursivement les composants et active ou désactive leurs commandes. Paramètres : panneau, actif.
        void setEnabledRecursif(boolean actif) { if (panneau != null) activer(panneau, actif); }
        /** Parcourt les composants imbriqués du panneau. */
        // Parcourt chaque enfant, change son activation et recommence récursivement si cet enfant est lui-même un conteneur.
        private void activer(Container p, boolean actif) { for (Component c : p.getComponents()) { c.setEnabled(actif); if (c instanceof Container conteneur) activer(conteneur, actif); } }
        /** Conserve une marge autour des panneaux, y compris aux petites résolutions. */
        // Replace les composants en fonction de la taille disponible.
        @Override public void doLayout() {
            // Positionne et dimensionne le composant ; arguments : 0, 0, getWidth(), getHeight().
            scene.setBounds(0, 0, getWidth(), getHeight());
            // Si panneau est différent de null, entre dans ce traitement conditionnel.
            if (panneau != null) {
                // Initialise « w » avec Math.min(accueil ? 430 : 740, getWidth() - 40), h = Math.min(panneau.getPreferredSize().height, getHeight() - 30).
                int w = Math.min(accueil ? 430 : 740, getWidth() - 40), h = Math.min(panneau.getPreferredSize().height, getHeight() - 30);
                // Positionne et dimensionne le composant ; arguments : accueil ? Math.max(20, getWidth() / 12) : (getWidth() - w) / 2, (getHeight() - h) / 2, w, h.
                panneau.setBounds(accueil ? Math.max(20, getWidth() / 12) : (getWidth() - w) / 2, (getHeight() - h) / 2, w, h);
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
