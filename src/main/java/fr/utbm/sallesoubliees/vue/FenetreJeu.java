package fr.utbm.sallesoubliees.vue;

import fr.utbm.sallesoubliees.controleur.ControleurJeu;
import fr.utbm.sallesoubliees.modele.*;
import fr.utbm.sallesoubliees.persistance.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Fenêtre du RPG : scène dominante, commandes contextuelles et panneaux superposés.
 * Les fichiers passent par SwingWorker ; actions et interface restent sur l'EDT.
 * @serial exclude
 */
public final class FenetreJeu extends JFrame {
    private final ControleurJeu controleur = new ControleurJeu();
    private final GestionnaireSauvegarde sauvegardes = new GestionnaireSauvegarde();
    private final GestionnaireRessources ressources;
    private final SceneJeu scene;
    private final Couches couches;
    private final JPanel entete = Theme.panneau();
    private final JPanel commandes = Theme.panneau();
    private final JLabel lieu = Theme.texte("LES SALLES OUBLIÉES");
    private final JLabel intention = Theme.texte("");
    private final JLabel evenement = Theme.texte("Un serment oublié. Neuf salles à traverser.");
    private final JButton attaque = Theme.bouton("1 · Attaquer");
    private final JButton defense = Theme.bouton("2 · Défendre");
    private final JButton potion = Theme.bouton("3 · Potion");
    private final JButton interaction = Theme.bouton("E · Interagir");
    private final JButton inventaire = Theme.bouton("I · Inventaire");
    private final JButton carte = Theme.bouton("M · Carte");
    private final JButton journalBouton = Theme.bouton("J · Journal");
    private final JButton pause = Theme.bouton("Échap · Pause");
    private final JTextArea journal = new JTextArea();
    private String panneauOuvert;
    private boolean fichierEnCours;

    /** Charge les ressources locales puis construit l'interface sur l'EDT. */
    public FenetreJeu() { this(new GestionnaireRessources()); }
    /**
     * Construit l'interface avec un cache préparé par le lanceur.
     * @param ressources images chargées avant l'ouverture de la fenêtre
     */
    public FenetreJeu(GestionnaireRessources ressources) {
        super("Les Salles oubliées · Le serment du Gardien");
        if (!SwingUtilities.isEventDispatchThread()) throw new IllegalStateException("Fenêtre à construire sur l'EDT.");
        this.ressources = ressources;
        scene = new SceneJeu(ressources, this::commander, this::actualiser);
        couches = new Couches(scene);
        JPanel racine = new JPanel(new BorderLayout()); racine.setBackground(Theme.FOND);
        entete.setLayout(new BorderLayout(12, 0)); entete.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        lieu.setFont(Theme.CORPS.deriveFont(Font.BOLD, 17)); lieu.setForeground(Theme.OR); entete.add(lieu, BorderLayout.CENTER);
        JPanel outils = Theme.panneau(); outils.setLayout(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        for (JButton b : new JButton[]{inventaire, carte, journalBouton, pause}) outils.add(b);
        entete.add(outils, BorderLayout.EAST);
        commandes.setLayout(new BorderLayout(8, 5)); commandes.setBorder(BorderFactory.createEmptyBorder(7, 18, 10, 18));
        JPanel actions = Theme.panneau(); actions.setLayout(new GridLayout(1, 4, 9, 0));
        for (JButton b : new JButton[]{attaque, defense, potion, interaction}) actions.add(b);
        commandes.add(intention, BorderLayout.NORTH); commandes.add(actions, BorderLayout.CENTER);
        evenement.setFont(Theme.CORPS.deriveFont(12f)); evenement.setForeground(Theme.SECONDAIRE); commandes.add(evenement, BorderLayout.SOUTH);
        racine.add(entete, BorderLayout.NORTH); racine.add(couches, BorderLayout.CENTER); racine.add(commandes, BorderLayout.SOUTH);
        setContentPane(racine); setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() { @Override public void windowClosing(WindowEvent e) { fermer(); } });
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowLostFocus(WindowEvent e) { scene.suspendre(true); }
            @Override public void windowGainedFocus(WindowEvent e) { scene.suspendre(panneauOuvert != null || fichierEnCours); }
        });
        attaque.setToolTipText("Attaque de l'arme équipée. L'ennemi survivant répond une seule fois.");
        defense.setToolTipText("+8 d'armure pour la seule réponse ennemie qui suit.");
        potion.setToolTipText("Rend jusqu'à 35 PV ; consomme une réponse en combat.");
        interaction.setToolTipText("Approchez une porte, un coffre ou le sanctuaire. E ou Entrée.");
        attaque.addActionListener(e -> actionCombat(ActionCombat.ATTAQUER)); defense.addActionListener(e -> actionCombat(ActionCombat.DEFENDRE));
        potion.addActionListener(e -> actionCombat(ActionCombat.POTION)); interaction.addActionListener(e -> scene.interagir());
        inventaire.addActionListener(e -> ouvrirInventaire()); carte.addActionListener(e -> ouvrirCarte());
        journalBouton.addActionListener(e -> ouvrirJournal()); pause.addActionListener(e -> ouvrirPause());
        journal.setEditable(false); journal.setLineWrap(true); journal.setWrapStyleWord(true); journal.setFont(Theme.CORPS);
        journal.setBackground(Theme.FOND); journal.setForeground(Theme.TEXTE); journal.setMargin(new Insets(12, 12, 12, 12));
        setMinimumSize(new Dimension(900, 620)); setSize(1280, 760); setLocationRelativeTo(null);
        ouvrirAccueil();
    }

    /** Convertit une intention clavier/souris de la scène en commande contrôlée. */
    private void commander(String commande) {
        if (commande.startsWith("message:")) { evenement.setText(commande.substring(8)); return; }
        if (commande.equals("pause")) {
            if (fichierEnCours) return;
            if (panneauOuvert != null && !panneauOuvert.equals("accueil")) fermerPanneau(); else ouvrirPause();
            return;
        }
        if (fichierEnCours || panneauOuvert != null) return;
        switch (commande) {
            case "attaque" -> actionCombat(ActionCombat.ATTAQUER);
            case "defense" -> actionCombat(ActionCombat.DEFENDRE);
            case "potion" -> actionCombat(ActionCombat.POTION);
            case "inventaire" -> ouvrirInventaire();
            case "carte" -> ouvrirCarte();
            case "journal" -> ouvrirJournal();
            case "objet" -> objet();
            default -> { if (commande.startsWith("porte:")) deplacer(commande.substring(6)); }
        }
    }
    /** Résout une fois l'action puis bloque les commandes jusqu'à la dernière animation. */
    private void actionCombat(ActionCombat action) {
        if (!libre()) return;
        ResultatAction resultat = controleur.resoudre(action);
        if (!resultat.valide()) { evenement.setText(resultat.message()); return; }
        scene.jouer(resultat, () -> { ajouterJournal(resultat.message()); actualiser(); });
        actualiser();
    }
    /** {@return possibilité de lancer une nouvelle commande de jeu} */
    private boolean libre() { return controleur.getPartie() != null && panneauOuvert == null && !fichierEnCours && !scene.estOccupe(); }
    /** Emprunte seulement un passage réel après la marche visuelle. */
    private void deplacer(String id) {
        if (!libre()) return;
        String ancienne = controleur.getPartie().getSalleActuelle().getId();
        try { ajouterJournal(controleur.deplacer(id)); scene.afficher(controleur.getPartie(), ancienne); }
        catch (IllegalStateException e) { evenement.setText(e.getMessage()); }
        actualiser();
    }
    /** Ouvre un coffre ou soigne au sanctuaire ; explique les refus de réutilisation. */
    private void objet() {
        if (!libre()) return;
        executerSimple(controleur.getPartie().getSalleActuelle().estRepos() ? controleur::reposer : controleur::ouvrir);
    }
    /** Applique une commande d'exploration sans tour supplémentaire et garde sa position visuelle. */
    private void executerSimple(Supplier<String> action) {
        try { ajouterJournal(action.get()); }
        catch (IllegalStateException e) { evenement.setText(e.getMessage()); }
        actualiser(); scene.repaint();
    }
    /** Rafraîchit uniquement les contrôles, sans interrompre une séquence visuelle. */
    private void actualiser() {
        if (scene == null) return;
        Partie p = controleur.getPartie(); boolean existe = p != null;
        boolean libre = existe && panneauOuvert == null && !fichierEnCours && !scene.estOccupe();
        attaque.setEnabled(libre && p.estEnCombat() && scene.estCombatAffiche());
        defense.setEnabled(attaque.isEnabled()); potion.setEnabled(libre && p.peutBoire());
        interaction.setEnabled(libre && p.estEnCours() && !p.estEnCombat());
        inventaire.setEnabled(existe && panneauOuvert == null && !fichierEnCours && !scene.estOccupe());
        carte.setEnabled(existe && panneauOuvert == null && !fichierEnCours && !scene.estOccupe());
        journalBouton.setEnabled(existe && panneauOuvert == null && !fichierEnCours);
        pause.setEnabled(existe && panneauOuvert == null && !fichierEnCours);
        if (existe) {
            lieu.setText(p.getSalleActuelle().getNom().toUpperCase(java.util.Locale.ROOT) + "  /  " + (p.estEnCombat() ? "RENCONTRE" : "EXPLORATION"));
            if (fichierEnCours) intention.setText("Opération de fichier en cours…");
            else if (scene.estOccupe()) intention.setText("L'action se déroule…");
            else if (!p.estEnCours()) intention.setText(p.getEtat() == EtatPartie.VICTOIRE ? "Victoire · Les salles sont libérées." : "Défaite · Votre voyage peut recommencer.");
            else if (p.estEnCombat()) intention.setText("À VOUS  ·  " + p.getSalleActuelle().getEnnemi().getIntention());
            else intention.setText("Flèches / ZQSD : marcher   ·   E / Entrée : interagir   ·   Cliquez une porte pour la rejoindre");
            intention.setToolTipText(intention.getText());
        }
    }
    /** Conserve le détail du tour et affiche seulement sa dernière phrase dans la scène. */
    private void ajouterJournal(String message) {
        journal.append(message + "\n\n");
        if (journal.getDocument().getLength() > 30_000) journal.setText(journal.getText().substring(10_000));
        journal.setCaretPosition(journal.getDocument().getLength());
        evenement.setText(message.substring(message.lastIndexOf('\n') + 1)); evenement.setToolTipText(message.replace('\n', ' '));
    }
    /** Ouvre l'accueil illustré en laissant le héros et le décor visibles à droite. */
    private void ouvrirAccueil() {
        scene.accueil(); entete.setVisible(false); commandes.setVisible(false); panneauOuvert = "accueil";
        JPanel panneau = Theme.panneau(); panneau.setLayout(new BoxLayout(panneau, BoxLayout.Y_AXIS));
        panneau.setBorder(BorderFactory.createEmptyBorder(25, 28, 25, 28));
        JLabel titre = Theme.texte("<html>LES SALLES<br>OUBLIÉES</html>"); titre.setFont(new Font(Font.SERIF, Font.BOLD, 39)); titre.setForeground(Theme.OR);
        JLabel sousTitre = Theme.texte("LE SERMENT DU GARDIEN"); sousTitre.setFont(Theme.CORPS.deriveFont(12f));
        JLabel texte = Theme.texte("<html>Une forteresse endormie.<br>Une lame. Une dernière lumière.</html>");
        for (JComponent c : new JComponent[]{sousTitre, titre, texte, bouton("Nouvelle partie", this::nouvellePartie), bouton("Charger une partie", this::charger), bouton("Quitter", this::fermer)}) {
            c.setAlignmentX(LEFT_ALIGNMENT); panneau.add(c); panneau.add(Box.createVerticalStrut(16));
        }
        couches.montrer(panneau, true); actualiser();
    }
    /** Démarre une partie après une confirmation si un état existe déjà. */
    private void nouvellePartie() {
        if (!confirmerAbandon()) return;
        scene.arreter(); controleur.nouvellePartie(); journal.setText("");
        entete.setVisible(true); commandes.setVisible(true); fermerPanneau();
        ajouterJournal("L'air est froid. Rejoignez la porte de l'Ossuaire, à l'est.");
        scene.afficher(controleur.getPartie(), null); scene.requestFocusInWindow(); actualiser();
    }
    /** Présente l'inventaire en cartes avec icônes, bonus et équipement courant. */
    private void ouvrirInventaire() {
        if (controleur.getPartie() == null || scene.estOccupe() || fichierEnCours) return;
        JPanel corps = colonne(); Heros h = controleur.getPartie().getHeros();
        for (Arme arme : h.getInventaire().getArmes()) {
            JPanel ligne = Theme.panneau(); ligne.setLayout(new BorderLayout(12, 0)); ligne.setBorder(Theme.bordure(arme == h.getInventaire().getEquipee() ? "ÉQUIPÉE" : "ARME"));
            JLabel nom = Theme.texte("<html><b>" + arme + "</b><br>Attaque totale : " + (h.getAttaqueBase() + arme.getBonus()) + "</html>");
            nom.setIcon(ressources.icone(6, 60)); ligne.add(nom, BorderLayout.CENTER);
            JButton choix = bouton("Équiper", () -> { executerSimple(() -> controleur.equiper(arme)); ouvrirInventaire(); });
            choix.setEnabled(controleur.getPartie().estEnCours() && !controleur.getPartie().estEnCombat() && arme != h.getInventaire().getEquipee());
            choix.setToolTipText("Hors combat : remplace le bonus, sans cumul."); ligne.add(choix, BorderLayout.EAST); corps.add(ligne); corps.add(Box.createVerticalStrut(8));
        }
        JLabel potions = Theme.texte(h.getInventaire().getPotions() + " potions  ·  +35 PV, sans dépasser 100"); potions.setIcon(ressources.icone(14, 42)); corps.add(potions);
        if (controleur.getPartie().estEnCombat()) corps.add(Theme.texte("Terminez le combat pour changer d'arme."));
        montrer("inventaire", "VOTRE ÉQUIPEMENT", corps);
    }
    /** Affiche la carte complète en panneau secondaire ; son choix lance une marche réelle. */
    private void ouvrirCarte() {
        if (controleur.getPartie() == null || scene.estOccupe() || fichierEnCours) return;
        CarteDonjon carteComplete = new CarteDonjon(id -> { fermerPanneau(); scene.marcherVersPorte(id); });
        carteComplete.afficher(controleur.getPartie(), false); carteComplete.setPreferredSize(new Dimension(680, 300));
        montrer("carte", "LES NEUF SALLES", carteComplete);
    }
    /** Affiche le journal détaillé seulement à la demande. */
    private void ouvrirJournal() {
        if (controleur.getPartie() == null || fichierEnCours) return;
        JScrollPane scroll = new JScrollPane(journal); scroll.setPreferredSize(new Dimension(620, 300));
        montrer("journal", "JOURNAL D'EXPLORATION", scroll);
    }
    /** Réunit fichiers, options et abandon dans un menu de pause. */
    private void ouvrirPause() {
        if (controleur.getPartie() == null || fichierEnCours) return;
        JPanel corps = colonne();
        JButton sauver = bouton("Sauvegarder", this::sauvegarder), charger = bouton("Charger", this::charger);
        sauver.setEnabled(!scene.estOccupe()); charger.setEnabled(!scene.estOccupe());
        if (scene.estOccupe()) corps.add(Theme.texte("Reprenez pour terminer l'animation avant une opération de fichier."));
        for (JButton b : new JButton[]{bouton("Reprendre", this::fermerPanneau), sauver, charger, bouton("Options", this::ouvrirOptions), bouton("Nouvelle partie", this::nouvellePartie), bouton("Retour au menu", () -> {
            if (confirmerAbandon()) { scene.arreter(); controleur.abandonner(); ouvrirAccueil(); }
        }), bouton("Quitter", this::fermer)}) { b.setAlignmentX(LEFT_ALIGNMENT); corps.add(b); corps.add(Box.createVerticalStrut(7)); }
        montrer("pause", "UNE HALTE DANS L'OMBRE", corps);
    }
    /** Propose la vitesse réduite sans modifier les règles ni le format de sauvegarde. */
    private void ouvrirOptions() {
        JPanel corps = colonne(); JCheckBox reduites = new JCheckBox("Animations réduites et accélérées", scene.estReduite());
        reduites.setBackground(Theme.PANNEAU); reduites.setForeground(Theme.TEXTE); reduites.setFont(Theme.CORPS);
        reduites.addActionListener(e -> scene.setReduite(reduites.isSelected())); corps.add(reduites); corps.add(Box.createVerticalStrut(16));
        corps.add(Theme.texte("<html>Les règles et les dégâts restent identiques.<br>Cette version est silencieuse : aucun son n'est inclus.</html>"));
        montrer("options", "OPTIONS", corps);
    }
    /** Construit un panneau modal dans la fenêtre, avec un bouton de fermeture accessible. */
    private void montrer(String nom, String titre, JComponent corps) {
        panneauOuvert = nom; scene.suspendre(true);
        JPanel panneau = Theme.panneau(); panneau.setLayout(new BorderLayout(14, 14)); panneau.setBorder(BorderFactory.createCompoundBorder(Theme.bordure(titre), BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        JScrollPane scroll = new JScrollPane(corps); scroll.setBorder(null); scroll.getViewport().setBackground(Theme.PANNEAU);
        scroll.setPreferredSize(new Dimension(650, Math.min(380, Math.max(160, corps.getPreferredSize().height + 8))));
        panneau.add(scroll, BorderLayout.CENTER); panneau.add(bouton("Fermer · Échap", this::fermerPanneau), BorderLayout.SOUTH);
        couches.montrer(panneau, false); actualiser();
    }
    /** Ferme un panneau et reprend l'horloge sans créer un nouveau timer. */
    private void fermerPanneau() {
        panneauOuvert = null; couches.cacher(); scene.suspendre(!isFocused() || fichierEnCours); scene.requestFocusInWindow(); actualiser();
    }
    /** Crée un panneau vertical homogène. */
    private static JPanel colonne() { JPanel p = Theme.panneau(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)); return p; }
    /** Crée un bouton d'action de menu. */
    private static JButton bouton(String texte, Runnable action) { JButton b = Theme.bouton(texte); b.addActionListener(e -> action.run()); return b; }
    /** Confirme l'abandon même si la dernière action est encore animée. */
    private boolean confirmerAbandon() {
        if (controleur.getPartie() == null) return true;
        scene.suspendre(true);
        boolean accord = JOptionPane.showConfirmDialog(this, "Abandonner cette partie ? Les progrès non sauvegardés seront perdus.", "Confirmer l'abandon", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        scene.suspendre(panneauOuvert != null || fichierEnCours || !isFocused()); return accord;
    }
    /** Ferme après confirmation et laisse finir une écriture déjà engagée. */
    private void fermer() { if (!fichierEnCours && confirmerAbandon()) dispose(); }
    /** Arrête le timer également lors d'une fermeture programmatique. */
    @Override public void dispose() { if (scene != null) scene.arreter(); super.dispose(); }
    /** Prépare un chemin par défaut dans le dossier de sauvegardes ignoré par Git. */
    private JFileChooser choisirFichier() {
        JFileChooser choix = new JFileChooser(); choix.setDialogTitle("Les Salles oubliées · Sauvegarde");
        choix.setFileFilter(new FileNameExtensionFilter("Sauvegardes JSON", "json"));
        choix.setSelectedFile(Path.of("sauvegardes", "partie.save.json").toFile()); return choix;
    }
    /** Capture le seul état stable du modèle après confirmation de remplacement. */
    private void sauvegarder() {
        if (scene.estOccupe() || fichierEnCours) return;
        JFileChooser choix = choisirFichier(); if (choix.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        Path fichier = choix.getSelectedFile().toPath();
        if (!fichier.getFileName().toString().toLowerCase(java.util.Locale.ROOT).endsWith(".json")) fichier = fichier.resolveSibling(fichier.getFileName() + ".save.json");
        if (Files.exists(fichier) && JOptionPane.showConfirmDialog(this, "Remplacer cette sauvegarde existante ?", "Remplacer", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        Path cible = fichier; EtatSauvegarde etat = controleur.capturer();
        executerFichier(() -> { sauvegardes.sauvegarder(cible, etat); return cible; }, p -> { fermerPanneau(); ajouterJournal("Partie sauvegardée : " + p.getFileName()); });
    }
    /** Reconstruit hors EDT et adopte uniquement après validation et confirmation. */
    private void charger() {
        if (scene.estOccupe() || fichierEnCours) return;
        JFileChooser choix = choisirFichier(); if (choix.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        Path fichier = choix.getSelectedFile().toPath();
        executerFichier(() -> sauvegardes.charger(fichier), chargee -> {
            if (!confirmerAbandon()) return;
            controleur.adopter(chargee); journal.setText(""); entete.setVisible(true); commandes.setVisible(true);
            fermerPanneau(); scene.afficher(chargee, null); ajouterJournal("Partie chargée. Vous reprenez sur un emplacement sûr de la salle.");
        });
    }
    /** Exécute les I/O hors EDT ; le panneau est bloqué jusqu'à la publication du résultat. */
    private <T> void executerFichier(Callable<T> travail, Consumer<T> succes) {
        fichierEnCours = true; scene.suspendre(true); couches.setEnabledRecursif(false); actualiser();
        new SwingWorker<T, Void>() {
            @Override protected T doInBackground() throws Exception { return travail.call(); }
            @Override protected void done() {
                try { succes.accept(get()); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); afficherErreur("Opération interrompue."); }
                catch (ExecutionException e) { afficherErreur(e.getCause() instanceof ExceptionSauvegarde ? e.getCause().getMessage() : "L'opération a échoué. La partie actuelle est conservée."); }
                finally {
                    fichierEnCours = false; couches.setEnabledRecursif(true);
                    if ("pause".equals(panneauOuvert)) ouvrirPause();
                    scene.suspendre(panneauOuvert != null || !isFocused()); actualiser();
                }
            }
        }.execute();
    }
    /** Affiche une erreur courte sans trace technique. */
    private void afficherErreur(String message) { JOptionPane.showMessageDialog(this, message, "Sauvegarde / chargement", JOptionPane.ERROR_MESSAGE); }

    /**
     * Deux couches redimensionnées : scène et panneau scrollable, sans autre timer.
     * @serial exclude
     */
    private static final class Couches extends JLayeredPane {
        private final SceneJeu scene;
        private JPanel panneau;
        private boolean accueil;
        /** Installe la scène dans la couche basse. */
        Couches(SceneJeu scene) { this.scene = scene; add(scene, Integer.valueOf(0)); }
        /** Remplace le seul panneau superposé. */
        void montrer(JPanel nouveau, boolean accueil) { cacher(); panneau = nouveau; this.accueil = accueil; add(panneau, Integer.valueOf(100)); revalidate(); repaint(); }
        /** Retire le panneau sans détruire la scène. */
        void cacher() { if (panneau != null) remove(panneau); panneau = null; revalidate(); repaint(); }
        /** Bloque temporairement les boutons lors des I/O. */
        void setEnabledRecursif(boolean actif) { if (panneau != null) activer(panneau, actif); }
        /** Parcourt les composants imbriqués du panneau. */
        private void activer(Container p, boolean actif) { for (Component c : p.getComponents()) { c.setEnabled(actif); if (c instanceof Container conteneur) activer(conteneur, actif); } }
        /** Conserve une marge autour des panneaux, y compris aux petites résolutions. */
        @Override public void doLayout() {
            scene.setBounds(0, 0, getWidth(), getHeight());
            if (panneau != null) {
                int w = Math.min(accueil ? 430 : 740, getWidth() - 40), h = Math.min(panneau.getPreferredSize().height, getHeight() - 30);
                panneau.setBounds(accueil ? Math.max(20, getWidth() / 12) : (getWidth() - w) / 2, (getHeight() - h) / 2, w, h);
            }
        }
    }
}
