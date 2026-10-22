package fr.utbm.sallesoubliees.modele;

import java.util.ArrayList;
import java.util.List;
import static fr.utbm.sallesoubliees.modele.EvenementJeu.Cible.*;
import static fr.utbm.sallesoubliees.modele.EvenementJeu.Type.*;

/** Point d'entrée des actions métier. Chaque action valide est résolue entièrement. */
public final class Partie {
    private final Heros heros;
    private final Donjon donjon;
    private Salle salleActuelle;
    private EtatPartie etat;
    private CombatTactique tactique = new CombatTactique(new HasardReproductible(System.nanoTime()));
    /** Définit les données de cette opération.
     * @param hasard source injectable pour les simulations et tests */
    public Partie(SourceAleatoire hasard) { this(); tactique = new CombatTactique(hasard); }
    /** {@return tactique consultable sans commandes publiques de mutation} */
    public CombatTactique getTactique() { return tactique; }
    /** Restaure la tactique après vérification des relations. */
    void restaurerTactique(EtatTactique e) { tactique = CombatTactique.restaurer(e); }
    /** {@return événement disponible dans la salle paisible, ou null} */
    public EvenementSalle getEvenementSalle() { return estEnCours() && !estEnCombat() ? tactique.evenement(salleActuelle.getId()) : null; }
    /** Définit les données de cette opération.
     * @param choix décision proposée par le modèle
 * @return conséquence unique */
    public String choisirEvenement(ChoixEvenement choix) {
        if (getEvenementSalle() == null) throw new IllegalStateException("Aucun événement disponible.");
        return tactique.choisir(salleActuelle.getId(), choix, heros);
    }

    /** Commence une partie indépendante à l'entrée du donjon. */
    public Partie() {
        this(new Heros(), new Donjon(), "entree", EtatPartie.EN_COURS);
        salleActuelle.visiter();
    }
    /** Assemble un état restauré, déjà intégralement validé. */
    Partie(Heros heros, Donjon donjon, String salle, EtatPartie etat) {
        this.heros = heros; this.donjon = donjon; this.salleActuelle = donjon.getSalle(salle); this.etat = etat;
    }
    /** {@return héros consultable} */
    public Heros getHeros() { return heros; }
    /** {@return donjon consultable} */
    public Donjon getDonjon() { return donjon; }
    /** {@return salle occupée} */
    public Salle getSalleActuelle() { return salleActuelle; }
    /** {@return issue de la partie} */
    public EtatPartie getEtat() { return etat; }
    /** {@return vrai tant que la partie permet des actions} */
    public boolean estEnCours() { return etat == EtatPartie.EN_COURS; }
    /** {@return vrai pendant un combat actif} */
    public boolean estEnCombat() { return estEnCours() && salleActuelle.estHostile(); }
    /** {@return possibilité de boire une potion maintenant} */
    public boolean peutBoire() { return estEnCours() && heros.getVie() < heros.getVieMax() && heros.getInventaire().getPotions() > 0; }
    /** {@return possibilité d'ouvrir le coffre actuel} */
    public boolean peutOuvrir() { return estEnCours() && !estEnCombat() && salleActuelle.getCoffre() != null && !salleActuelle.estUtilisee(); }
    /** {@return possibilité d'utiliser le repos maintenant} */
    public boolean peutReposer() { return estEnCours() && !estEnCombat() && salleActuelle.estRepos() && !salleActuelle.estUtilisee() && heros.getVie() < heros.getVieMax(); }
    /**
     * Indique si un passage peut être emprunté maintenant.
     *
 * @param id destination
     *
 * @return vrai pour une salle voisine hors combat
     */
    public boolean peutDeplacer(String id) { return estEnCours() && !estEnCombat() && salleActuelle.getPassages().contains(id); }

    /** Refuse toute action après une issue finale. */
    private void exigerEnCours() { if (!estEnCours()) throw new IllegalStateException("Cette partie est terminée. Commencez une nouvelle partie."); }
    /** Refuse les actions de combat dans une salle paisible. */
    private void exigerCombat() { exigerEnCours(); if (!estEnCombat()) throw new IllegalStateException("Aucun ennemi à combattre ici."); }

    /**
     * Emprunte un passage et engage l'ennemi éventuel sans attaque gratuite.
     *
 * @param id salle voisine
     *
 * @return événement à afficher
     */
    public String deplacer(String id) {
        exigerEnCours();
        if (!peutDeplacer(id)) throw new IllegalStateException("Déplacement impossible : terminez le combat et suivez les passages.");
        salleActuelle = donjon.getSalle(id); salleActuelle.visiter();
        if (estEnCombat()) tactique.commencer(salleActuelle.getEnnemi());
        return "Vous entrez dans : " + salleActuelle.getNom() + (estEnCombat() ? ". Un ennemi bloque les passages !" : ".");
    }
    /** {@return récit d'une attaque et de sa réponse éventuelle} */
    public String attaquer() {
        return messageOuErreur(resoudre(ActionCombat.ATTAQUER));
    }
    /** {@return récit d'une défense valable pour une seule réponse} */
    public String defendre() { return messageOuErreur(resoudre(ActionCombat.DEFENDRE)); }
    /** {@return récit du soin et de la réponse éventuelle} */
    public String boirePotion() {
        return messageOuErreur(resoudre(ActionCombat.POTION));
    }

    /**
     * Applique une seule fois la commande et produit ses faits, même si la vue les anime plus tard.
     * Une commande refusée garde exactement le même état et ne produit aucun événement.
     *
 * @param action commande du joueur
     *
 * @return résolution immuable
     */
    public ResultatAction resoudre(ActionCombat action) {
        EtatSauvegarde avant = SauvegardePartie.capturer(this);
        try {
            exigerEnCours();
            if (action == null) throw new IllegalStateException("Action inconnue.");
            if (action == ActionCombat.PUISSANTE && tactique.getElan() < 2) throw new IllegalStateException("Il faut 2 points d’élan.");
            if (action != ActionCombat.POTION) exigerCombat();
            else if (!peutBoire()) throw new IllegalStateException("Potion impossible : vie déjà pleine ou inventaire vide.");
        } catch (IllegalStateException erreur) {
            return new ResultatAction(false, erreur.getMessage(), avant, avant, List.of());
        }
        List<EvenementJeu> faits = new ArrayList<>();
        String message;
        if (estEnCombat()) {
            message = tactique.resoudre(heros, salleActuelle.getEnnemi(), action, faits);
            message = finirAction(message, false, faits);
            if (!estEnCombat()) tactique.terminer();
        } else {
            int avantSoin = heros.getVie(); heros.getInventaire().consommerPotion(); heros.soigner(Potion.SOIN);
            faits.add(new EvenementJeu(SOIN, HEROS, heros.getVie() - avantSoin, heros.getVie()));
            message = "Potion consommée.";
        }
        String details = faits.stream().map(f -> switch (f.type()) {
            case DEGATS -> (f.cible() == HEROS ? "Vous perdez " : "L'adversaire perd ") + f.valeur() + " PV.";
            case SOIN -> (f.cible() == HEROS ? "Vous récupérez " : "L'adversaire récupère ") + f.valeur() + " PV.";
            case ELAN -> "Élan : " + f.valeur() + "/3.";
            case INTERRUPTION -> "La préparation est interrompue.";
            case BRISE_GARDE -> "La protection est brisée.";
            case CONTRE_ATTAQUE -> "L'adversaire riposte.";
            case EFFET -> "Altération : " + EffetCombat.Type.values()[f.valeur()] + " (" + f.cible() + ").";
            case PHASE -> "Le Gardien entre en phase " + f.valeur() + ".";
            default -> "";
        }).filter(s -> !s.isEmpty()).collect(java.util.stream.Collectors.joining("\n"));
        return new ResultatAction(true, message + (details.isEmpty() ? "" : "\n" + details), avant, SauvegardePartie.capturer(this), faits);
    }

    /** Préserve l'API historique et ses exceptions, utilisée par les tests et l'exploration. */
    private String messageOuErreur(ResultatAction resultat) {
        if (!resultat.valide()) throw new IllegalStateException(resultat.message());
        return resultat.message();
    }
    /**
     * Change d'arme hors combat, sans jamais cumuler les bonus.
     *
 * @param arme arme possédée
     *
 * @return événement à afficher
     */
    public String equiper(Arme arme) {
        exigerEnCours();
        if (estEnCombat()) throw new IllegalStateException("Changez d'arme avant le combat.");
        heros.getInventaire().equiper(arme);
        return "Arme équipée : " + arme + ".";
    }
    /** {@return récompense du coffre consommé une seule fois} */
    public String ouvrirCoffre() {
        exigerEnCours();
        if (!peutOuvrir()) throw new IllegalStateException("Aucun coffre à ouvrir ici.");
        Arme arme = salleActuelle.getCoffre();
        heros.getInventaire().ajouterArme(arme);
        int potions = arme == Arme.ACIER ? 2 : 1;
        heros.getInventaire().ajouterPotions(potions); salleActuelle.utiliser();
        return "Coffre ouvert : " + arme + " et " + potions + " potion(s). Pensez à équiper l'arme.";
    }
    /** {@return événement de repos, consommé seulement si le héros est blessé} */
    public String seReposer() {
        exigerEnCours();
        if (!peutReposer()) throw new IllegalStateException("Repos indisponible : sanctuaire déjà utilisé, absent ou vie pleine.");
        heros.soigner(heros.getVieMax()); salleActuelle.utiliser();
        return "Le sanctuaire restaure tous vos PV. Son pouvoir est épuisé.";
    }
    /** Termine atomiquement l'action ; récompense et issue précèdent toute contre-attaque. */
    private String finirAction(String message, boolean defense, List<EvenementJeu> faits) {
        Ennemi ennemi = salleActuelle.getEnnemi();
        if (ennemi == null) return message;
        if (ennemi.estMort()) {
            faits.add(new EvenementJeu(MORT, ENNEMI, 0, 0));
            if (ennemi instanceof Boss) {
                etat = EtatPartie.VICTOIRE;
                faits.add(new EvenementJeu(VICTOIRE, HEROS, 0, heros.getVie()));
                return message + "\nVictoire ! Le Gardien est vaincu. Les salles sont libérées.";
            }
            heros.getInventaire().ajouterPotions(1);
            faits.add(new EvenementJeu(RECOMPENSE, HEROS, 1, heros.getVie()));
            return message + "\n" + ennemi.getNom() + " est vaincu. Vous trouvez une potion.";
        }
        if (heros.estMort()) {
            etat = EtatPartie.DEFAITE;
            faits.add(new EvenementJeu(MORT, HEROS, 0, 0)); faits.add(new EvenementJeu(DEFAITE, HEROS, 0, 0));
            message += "\nDéfaite. Votre exploration s'achève ici.";
        }
        return message;
    }
}
