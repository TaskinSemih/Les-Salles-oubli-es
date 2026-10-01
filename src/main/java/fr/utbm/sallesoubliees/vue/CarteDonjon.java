package fr.utbm.sallesoubliees.vue;

import fr.utbm.sallesoubliees.modele.Partie;
import fr.utbm.sallesoubliees.modele.Salle;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Carte dessinée localement, avec de vrais boutons accessibles au clavier pour les salles.
 * @serial exclude
 */
public final class CarteDonjon extends JPanel {
    private final Map<String, JButton> boutons = new LinkedHashMap<>();
    private Partie partie;

    /**
     * Crée une carte dont les clics passent par le contrôleur.
     * @param deplacement commande de déplacement
     */
    public CarteDonjon(Consumer<String> deplacement) {
        setLayout(null); setBackground(Theme.FOND); setPreferredSize(new Dimension(680, 290));
        setMinimumSize(new Dimension(500, 240));
        for (Salle salle : new fr.utbm.sallesoubliees.modele.Donjon().getSalles()) {
            JButton bouton = Theme.bouton(salle.getNom());
            bouton.setName("salle-" + salle.getId()); bouton.setMargin(new java.awt.Insets(3, 2, 3, 2));
            bouton.addActionListener(e -> deplacement.accept(salle.getId()));
            boutons.put(salle.getId(), bouton); add(bouton);
        }
    }

    /**
     * Met à jour visites, salle actuelle et déplacements autorisés.
     * @param partie partie courante
     * @param occupe opération fichier en cours
     */
    public void afficher(Partie partie, boolean occupe) {
        this.partie = partie;
        for (Salle salle : partie.getDonjon().getSalles()) {
            JButton b = boutons.get(salle.getId());
            boolean actuelle = salle == partie.getSalleActuelle();
            String statut = actuelle ? "VOUS ÊTES ICI" : partie.peutDeplacer(salle.getId()) ? "ACCESSIBLE" : salle.estVisitee() ? "VISITÉE" : "INEXPLORÉE";
            String contenu = salle.getEnnemi() != null ? (salle.estHostile() ? salle.getEnnemi().getNom() : "Ennemi vaincu") : salle.getCoffre() != null ? (salle.estUtilisee() ? "Coffre ouvert" : "Coffre") : salle.estRepos() ? (salle.estUtilisee() ? "Repos épuisé" : "Repos unique") : "Départ";
            b.setText("<html><center><b>" + salle.getNom() + "</b><br><small>" + statut + "</small></center></html>");
            b.setToolTipText(salle.getNom() + " — " + statut + " — " + contenu);
            b.getAccessibleContext().setAccessibleName(salle.getNom() + " : " + statut);
            b.setEnabled(!occupe && partie.peutDeplacer(salle.getId()));
            b.setBackground(actuelle ? new java.awt.Color(90, 76, 49) : salle.estVisitee() ? new java.awt.Color(43, 65, 61) : Theme.PANNEAU);
            b.setBorder(BorderFactory.createLineBorder(actuelle ? Theme.OR : partie.peutDeplacer(salle.getId()) ? Theme.VERT : new java.awt.Color(75, 84, 94), actuelle ? 3 : 1));
        }
        revalidate(); repaint();
    }

    /** Place les salles sur cinq colonnes et trois lignes en respectant le redimensionnement. */
    @Override public void doLayout() {
        if (partie == null) return;
        int celluleX = getWidth() / 5, celluleY = getHeight() / 3;
        for (Salle salle : partie.getDonjon().getSalles()) {
            boutons.get(salle.getId()).setBounds(salle.getColonne() * celluleX + 7,
                    salle.getLigne() * celluleY + 6, celluleX - 14, celluleY - 12);
        }
    }

    /** Dessine uniquement les passages réellement présents dans le modèle. */
    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (partie == null) return;
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new java.awt.Color(99, 109, 118)); g.setStroke(new BasicStroke(3));
        for (Salle salle : partie.getDonjon().getSalles()) {
            JButton depart = boutons.get(salle.getId());
            for (String id : salle.getPassages()) {
                if (salle.getId().compareTo(id) < 0) {
                    JButton arrivee = boutons.get(id);
                    g.drawLine(depart.getX() + depart.getWidth() / 2, depart.getY() + depart.getHeight() / 2,
                            arrivee.getX() + arrivee.getWidth() / 2, arrivee.getY() + arrivee.getHeight() / 2);
                }
            }
        }
        g.dispose();
    }
}
