package fr.utbm.sallesoubliees.vue;

import fr.utbm.sallesoubliees.controleur.ControleurJeu;
import fr.utbm.sallesoubliees.modele.*;
import fr.utbm.sallesoubliees.persistance.ExceptionSauvegarde;
import fr.utbm.sallesoubliees.persistance.GestionnaireSauvegarde;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Fenêtre Swing française ; toutes les mutations d'interface ont lieu sur l'EDT.
 * @serial exclude
 */
public final class FenetreJeu extends JFrame {
    private final ControleurJeu controleur = new ControleurJeu();
    private final GestionnaireSauvegarde sauvegardes = new GestionnaireSauvegarde();
    private final CardLayout pages = new CardLayout();
    private final JPanel contenu = new JPanel(pages);
    private final JTextArea journal = new JTextArea();
    private final JLabel statistiques = Theme.texte("");
    private final JLabel salle = Theme.texte("");
    private final JLabel adversaire = Theme.texte("");
    private final JLabel intention = Theme.texte("");
    private final JLabel statut = Theme.texte("Prêt à explorer.");
    private final JProgressBar vieHeros = barre(Theme.VERT);
    private final JProgressBar vieEnnemi = barre(Theme.ROUGE);
    private final JComboBox<Arme> armes = new JComboBox<>();
    private final JButton attaquer = Theme.bouton("Attaquer");
    private final JButton defendre = Theme.bouton("Défendre");
    private final JButton potion = Theme.bouton("Boire une potion");
    private final JButton coffre = Theme.bouton("Ouvrir le coffre");
    private final JButton repos = Theme.bouton("Se reposer");
    private final JButton equiper = Theme.bouton("Équiper");
    private final JButton sauver = Theme.bouton("Sauvegarder");
    private final JButton charger = Theme.bouton("Charger");
    private final JButton menu = Theme.bouton("Retour au menu");
    private final JButton nouvelle = Theme.bouton("Nouvelle partie");
    private final JButton chargerAccueil = Theme.bouton("Charger une partie");
    private final JButton quitter = Theme.bouton("Quitter");
    private final JButton recommencer = Theme.bouton("Nouvelle partie");
    private final CarteDonjon carte = new CarteDonjon(id -> agir(() -> controleur.deplacer(id)));
    private boolean occupe;

    /** Construit les écrans ; l'appelant doit être sur l'EDT. */
    public FenetreJeu() {
        super("Les Salles oubliées");
        if (!SwingUtilities.isEventDispatchThread()) throw new IllegalStateException("Construire la fenêtre sur l'EDT.");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            /** Confirme la fermeture d'une partie en cours. */
            @Override public void windowClosing(WindowEvent e) { fermer(); }
        });
        setContentPane(contenu); contenu.add(creerAccueil(), "accueil"); contenu.add(creerJeu(), "jeu");
        connecterActions(); pages.show(contenu, "accueil");
        setMinimumSize(new Dimension(1080, 760)); setSize(1240, 880); setLocationRelativeTo(null);
    }

    /** Construit l'accueil avec des explications accessibles avant le premier combat. */
    private JPanel creerAccueil() {
        JPanel fond = new JPanel(new GridBagLayout()); fond.setBackground(Theme.FOND);
        JPanel bloc = Theme.panneau(); bloc.setLayout(new BoxLayout(bloc, BoxLayout.Y_AXIS));
        bloc.setBorder(BorderFactory.createEmptyBorder(44, 52, 44, 52));
        JLabel surtitre = Theme.texte("UN DONJON · NEUF SALLES · UN GARDIEN"); surtitre.setForeground(Theme.OR);
        JLabel titre = Theme.texte("Les Salles oubliées"); titre.setFont(Theme.CORPS.deriveFont(Font.BOLD, 36));
        JLabel texte = Theme.texte("<html>Explorez les passages, trouvez des armes et affrontez le Gardien.<br><br>Les combats sont au tour par tour : prenez le temps de décider.<br>L'intention ennemie vous aide à attaquer, défendre ou vous soigner.<br>Vous pouvez sauvegarder entre deux actions, même en combat.</html>");
        for (JComponent c : new JComponent[]{surtitre, titre, texte, nouvelle, chargerAccueil, quitter}) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT); bloc.add(c); bloc.add(Box.createVerticalStrut(20));
        }
        fond.add(bloc); return fond;
    }

    /** Assemble carte, combat, inventaire et journal avec des espacements constants. */
    private JPanel creerJeu() {
        JPanel fond = new JPanel(new BorderLayout(10, 10)); fond.setBackground(Theme.FOND);
        fond.setBorder(BorderFactory.createEmptyBorder(12, 16, 10, 16));
        JPanel entete = Theme.panneau(); entete.setLayout(new BorderLayout(12, 8));
        JLabel titre = Theme.texte("LES SALLES OUBLIÉES"); titre.setFont(Theme.CORPS.deriveFont(Font.BOLD, 22));
        titre.setForeground(Theme.OR); entete.add(titre, BorderLayout.WEST);
        JPanel outils = Theme.panneau(); outils.add(sauver); outils.add(charger); outils.add(menu); entete.add(outils, BorderLayout.EAST);
        fond.add(entete, BorderLayout.NORTH);

        JPanel centre = new JPanel(new BorderLayout(10, 8)); centre.setBackground(Theme.FOND);
        JPanel exploration = Theme.panneau(); exploration.setLayout(new BorderLayout(0, 5)); exploration.setBorder(Theme.bordure("CARTE DU DONJON"));
        exploration.add(salle, BorderLayout.NORTH); exploration.add(carte, BorderLayout.CENTER);
        JLabel legende = Theme.texte("Or : position actuelle  ·  Vert : passage accessible  ·  Cliquez une salle pour avancer.");
        legende.setFont(Theme.CORPS.deriveFont(12f)); exploration.add(legende, BorderLayout.SOUTH);
        centre.add(exploration, BorderLayout.CENTER);

        JPanel cote = Theme.panneau(); cote.setLayout(new BoxLayout(cote, BoxLayout.Y_AXIS)); cote.setBorder(Theme.bordure("L'EXPLORATEUR"));
        cote.setPreferredSize(new Dimension(290, 350));
        statistiques.setAlignmentX(Component.LEFT_ALIGNMENT); vieHeros.setAlignmentX(Component.LEFT_ALIGNMENT);
        armes.setAlignmentX(Component.LEFT_ALIGNMENT); armes.setFont(Theme.CORPS); armes.setMaximumSize(new Dimension(280, 36));
        JPanel lieux = Theme.panneau(); lieux.setLayout(new GridLayout(1, 2, 5, 0));
        coffre.setMargin(new Insets(5, 4, 5, 4)); repos.setMargin(new Insets(5, 4, 5, 4));
        lieux.add(coffre); lieux.add(repos); lieux.setMaximumSize(new Dimension(280, 36));
        for (JComponent c : new JComponent[]{statistiques, vieHeros, armes, equiper, potion, lieux}) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT); cote.add(c); cote.add(Box.createVerticalStrut(6));
        }
        centre.add(cote, BorderLayout.EAST);

        JPanel bas = Theme.panneau(); bas.setLayout(new BorderLayout(6, 4)); bas.setBorder(Theme.bordure("SALLE & COMBAT"));
        JPanel details = Theme.panneau(); details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        adversaire.setAlignmentX(Component.LEFT_ALIGNMENT); vieEnnemi.setAlignmentX(Component.LEFT_ALIGNMENT); intention.setAlignmentX(Component.LEFT_ALIGNMENT);
        details.add(adversaire); details.add(Box.createVerticalStrut(5)); details.add(vieEnnemi);
        details.add(Box.createVerticalStrut(6)); details.add(intention);
        JPanel actions = Theme.panneau(); actions.setLayout(new FlowLayout(FlowLayout.LEFT));
        actions.add(attaquer); actions.add(defendre); actions.add(recommencer);
        bas.add(details, BorderLayout.CENTER); bas.add(actions, BorderLayout.SOUTH); centre.add(bas, BorderLayout.SOUTH);
        fond.add(centre, BorderLayout.CENTER);

        JPanel pied = Theme.panneau(); pied.setLayout(new BorderLayout(0, 8)); pied.setBorder(Theme.bordure("JOURNAL D'EXPLORATION"));
        journal.setEditable(false); journal.setLineWrap(true); journal.setWrapStyleWord(true); journal.setFont(Theme.CORPS);
        journal.setBackground(Theme.FOND); journal.setForeground(Theme.TEXTE); journal.setMargin(new Insets(8, 10, 8, 10));
        JScrollPane defilement = new JScrollPane(journal); defilement.setPreferredSize(new Dimension(800, 90));
        pied.add(defilement, BorderLayout.CENTER); pied.add(statut, BorderLayout.SOUTH); fond.add(pied, BorderLayout.SOUTH);
        return fond;
    }

    /** Branche chaque bouton sur une action métier ou une opération de fichier. */
    private void connecterActions() {
        nouvelle.addActionListener(e -> demarrer()); recommencer.addActionListener(e -> demarrer());
        quitter.addActionListener(e -> fermer()); chargerAccueil.addActionListener(e -> charger());
        attaquer.addActionListener(e -> agir(controleur::attaquer)); defendre.addActionListener(e -> agir(controleur::defendre));
        potion.addActionListener(e -> agir(controleur::boire)); coffre.addActionListener(e -> agir(controleur::ouvrir));
        repos.addActionListener(e -> agir(controleur::reposer));
        equiper.addActionListener(e -> agir(() -> controleur.equiper((Arme) armes.getSelectedItem())));
        armes.addActionListener(e -> actualiserEquipement());
        sauver.addActionListener(e -> sauvegarder()); charger.addActionListener(e -> charger());
        menu.addActionListener(e -> {
            if (confirmerAbandon()) { controleur.abandonner(); pages.show(contenu, "accueil"); }
        });
    }

    /** Démarre une partie après confirmation si nécessaire. */
    private void demarrer() {
        if (!confirmerAbandon()) return;
        controleur.nouvellePartie(); journal.setText("");
        ajouterJournal("Vous franchissez l'entrée du donjon. Vainquez le Gardien pour libérer les salles.\nChoisissez l'Ossuaire sur la carte. Pensez à ouvrir les coffres et à équiper vos armes.");
        pages.show(contenu, "jeu"); actualiser();
    }
    /** Confirme l'abandon uniquement si une partie en cours existe. */
    private boolean confirmerAbandon() {
        return controleur.getPartie() == null || !controleur.getPartie().estEnCours()
                || JOptionPane.showConfirmDialog(this, "Abandonner la partie actuelle ? Les progrès non sauvegardés seront perdus.",
                "Confirmer l'abandon", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    /** Ferme après confirmation, sans interrompre une opération de fichier en cours. */
    private void fermer() { if (!occupe && confirmerAbandon()) dispose(); }
    /** Exécute une commande rapide et rafraîchit immédiatement l'interface. */
    private void agir(Supplier<String> action) {
        if (occupe) return;
        try { ajouterJournal(action.get()); }
        catch (IllegalStateException e) { JOptionPane.showMessageDialog(this, e.getMessage(), "Action indisponible", JOptionPane.INFORMATION_MESSAGE); }
        actualiser();
    }
    /** Ajoute un événement et limite le journal en mémoire. */
    private void ajouterJournal(String message) {
        journal.append(message + "\n\n");
        if (journal.getDocument().getLength() > 30_000) journal.setText(journal.getText().substring(10_000));
        journal.setCaretPosition(journal.getDocument().getLength());
    }
    /** Synchronise toutes les commandes sur leurs préconditions métier. */
    private void actualiser() {
        nouvelle.setEnabled(!occupe); chargerAccueil.setEnabled(!occupe); quitter.setEnabled(!occupe);
        Partie p = controleur.getPartie();
        if (p == null) return;
        Heros h = p.getHeros();
        statistiques.setText("<html><b>" + h.getNom() + "</b><br>Attaque : " + h.getAttaque() + " &nbsp; Armure : " + h.getArmure()
                + "<br>Potions : " + h.getInventaire().getPotions() + " (+35 PV)</html>");
        afficherVie(vieHeros, h); salle.setText("Position : " + p.getSalleActuelle().getNom());
        Arme selection = (Arme) armes.getSelectedItem();
        armes.removeAllItems(); h.getInventaire().getArmes().forEach(armes::addItem);
        armes.setSelectedItem(selection != null && h.getInventaire().getArmes().contains(selection) ? selection : h.getInventaire().getEquipee());
        armes.setToolTipText("Arme équipée : " + h.getInventaire().getEquipee());
        equiper.setText("Équiper"); actualiserEquipement();
        attaquer.setEnabled(!occupe && p.estEnCombat()); defendre.setEnabled(!occupe && p.estEnCombat());
        potion.setEnabled(!occupe && p.peutBoire()); coffre.setEnabled(!occupe && p.peutOuvrir()); repos.setEnabled(!occupe && p.peutReposer());
        sauver.setEnabled(!occupe); charger.setEnabled(!occupe); menu.setEnabled(!occupe); recommencer.setEnabled(!occupe);
        recommencer.setVisible(!p.estEnCours());
        Ennemi e = p.getSalleActuelle().getEnnemi(); vieEnnemi.setVisible(e != null);
        if (e != null) afficherVie(vieEnnemi, e);
        if (!p.estEnCours()) {
            adversaire.setText(p.getEtat() == EtatPartie.VICTOIRE ? "VICTOIRE — Le Gardien est vaincu !" : "DÉFAITE — Votre exploration s'achève ici.");
            adversaire.setForeground(p.getEtat() == EtatPartie.VICTOIRE ? Theme.VERT : Theme.ROUGE);
            intention.setText("Vous pouvez sauvegarder cette fin, charger une partie ou recommencer.");
        } else {
            adversaire.setForeground(Theme.TEXTE);
            adversaire.setText(p.estEnCombat() ? "Combat : " + e.getNom() + " — Armure " + e.getArmure() : "La salle est paisible. Les passages sont libres.");
            intention.setText(p.estEnCombat() ? e.getIntention() : p.peutOuvrir() ? "Un coffre vous attend. Ouvrez-le depuis l'inventaire." : p.getSalleActuelle().estRepos() ? "Le sanctuaire offre un soin complet unique, si vous êtes blessé." : "Explorez la carte. Équipez vos armes et préparez-vous avant d'avancer.");
        }
        statut.setText(occupe ? "Opération de fichier en cours…" : p.estEnCombat() ? "À vous de jouer. Chaque action valide donne un tour à l'ennemi survivant." : "Arme équipée : " + h.getInventaire().getEquipee());
        carte.afficher(p, occupe);
    }
    /** Met à jour l'équipement sans reconstruire la liste de sélection. */
    private void actualiserEquipement() {
        Partie p = controleur.getPartie(); if (p == null) return;
        armes.setEnabled(!occupe && p.estEnCours() && !p.estEnCombat());
        equiper.setEnabled(armes.isEnabled() && armes.getSelectedItem() != null && armes.getSelectedItem() != p.getHeros().getInventaire().getEquipee());
    }
    /** Crée une barre avec valeurs textuelles, indépendante des couleurs pour la lecture. */
    private static JProgressBar barre(Color couleur) {
        JProgressBar barre = new JProgressBar(); barre.setStringPainted(true); barre.setForeground(couleur);
        barre.setBackground(Theme.FOND); barre.setFont(Theme.CORPS.deriveFont(Font.BOLD));
        barre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25)); barre.setPreferredSize(new Dimension(220, 25));
        return barre;
    }
    /** Affiche les points de vie bornés du modèle. */
    private static void afficherVie(JProgressBar barre, Personnage personnage) {
        barre.setMaximum(personnage.getVieMax()); barre.setValue(personnage.getVie());
        barre.setString(personnage.getVie() + " / " + personnage.getVieMax() + " PV");
    }
    /** Prépare un sélecteur avec le dossier local des sauvegardes et un filtre JSON. */
    private JFileChooser choisirFichier() {
        JFileChooser choix = new JFileChooser(); choix.setDialogTitle("Sauvegarde des Salles oubliées");
        choix.setFileFilter(new FileNameExtensionFilter("Sauvegardes JSON", "json"));
        choix.setSelectedFile(Path.of("sauvegardes", "partie.save.json").toFile()); return choix;
    }
    /** Confirme l'écrasement et capture un instantané avant de quitter l'EDT. */
    private void sauvegarder() {
        JFileChooser choix = choisirFichier();
        if (choix.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        Path fichier = choix.getSelectedFile().toPath();
        if (!fichier.getFileName().toString().toLowerCase(java.util.Locale.ROOT).endsWith(".json")) fichier = fichier.resolveSibling(fichier.getFileName() + ".save.json");
        if (Files.exists(fichier) && JOptionPane.showConfirmDialog(this, "Remplacer cette sauvegarde existante ?", "Confirmer le remplacement", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        Path cible = fichier; EtatSauvegarde etat = controleur.capturer();
        executerFichier(() -> { sauvegardes.sauvegarder(cible, etat); return cible; },
                resultat -> ajouterJournal("Partie sauvegardée : " + resultat.getFileName()));
    }
    /** Charge à l'arrière-plan puis confirme avant de remplacer la partie courante. */
    private void charger() {
        JFileChooser choix = choisirFichier();
        if (choix.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        executerFichier(() -> sauvegardes.charger(choix.getSelectedFile().toPath()), partie -> {
            if (!confirmerAbandon()) return;
            controleur.adopter(partie); journal.setText(""); ajouterJournal("Partie chargée. Vous reprenez dans : " + partie.getSalleActuelle().getNom() + ".");
            pages.show(contenu, "jeu");
        });
    }
    /** Exécute les entrées/sorties hors EDT ; publie le résultat ou l'erreur sur l'EDT. */
    private <T> void executerFichier(Callable<T> travail, Consumer<T> succes) {
        occupe = true; actualiser();
        new SwingWorker<T, Void>() {
            /** Effectue uniquement le travail lent dans le fil de fond. */
            @Override protected T doInBackground() throws Exception { return travail.call(); }
            /** Publie uniquement un résultat réussi, sans modifier l'ancienne partie en cas d'erreur. */
            @Override protected void done() {
                try { succes.accept(get()); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); erreurFichier("Opération interrompue. Réessayez."); }
                catch (ExecutionException e) {
                    erreurFichier(e.getCause() instanceof ExceptionSauvegarde ? e.getCause().getMessage() : "L'opération a échoué. La partie actuelle est conservée.");
                } finally { occupe = false; actualiser(); }
            }
        }.execute();
    }
    /** Affiche une explication française sans trace technique. */
    private void erreurFichier(String message) { JOptionPane.showMessageDialog(this, message, "Sauvegarde / chargement", JOptionPane.ERROR_MESSAGE); }
}
