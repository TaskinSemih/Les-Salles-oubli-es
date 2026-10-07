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
     * @param id destination
     * @return vrai pour une salle voisine hors combat
     */
    public boolean peutDeplacer(String id) { return estEnCours() && !estEnCombat() && salleActuelle.getPassages().contains(id); }

    /** Refuse toute action après une issue finale. */
    private void exigerEnCours() { if (!estEnCours()) throw new IllegalStateException("Cette partie est terminée. Commencez une nouvelle partie."); }
    /** Refuse les actions de combat dans une salle paisible. */
    private void exigerCombat() { exigerEnCours(); if (!estEnCombat()) throw new IllegalStateException("Aucun ennemi à combattre ici."); }

    /**
     * Emprunte un passage et engage l'ennemi éventuel sans attaque gratuite.
     * @param id salle voisine
     * @return événement à afficher
     */
    public String deplacer(String id) {
        exigerEnCours();
        if (!peutDeplacer(id)) throw new IllegalStateException("Déplacement impossible : terminez le combat et suivez les passages.");
        salleActuelle = donjon.getSalle(id); salleActuelle.visiter();
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
     * @param action commande du joueur
     * @return résolution immuable
     */
    public ResultatAction resoudre(ActionCombat action) {
        EtatSauvegarde avant = SauvegardePartie.capturer(this);
        try {
            exigerEnCours();
            if (action == null) throw new IllegalStateException("Action inconnue.");
            if (action != ActionCombat.POTION) exigerCombat();
            else if (!peutBoire()) throw new IllegalStateException("Potion impossible : vie déjà pleine ou inventaire vide.");
        } catch (IllegalStateException erreur) {
            return new ResultatAction(false, erreur.getMessage(), avant, avant, List.of());
        }
        List<EvenementJeu> faits = new ArrayList<>();
        String message;
        if (action == ActionCombat.ATTAQUER) {
            Ennemi ennemi = salleActuelle.getEnnemi(); int vieAvant = ennemi.getVie();
            faits.add(new EvenementJeu(ATTAQUE, HEROS, 0, heros.getVie()));
            message = GestionnaireCombat.attaquer(heros, ennemi);
            faits.add(new EvenementJeu(DEGATS, ENNEMI, vieAvant - ennemi.getVie(), ennemi.getVie()));
            if (ennemi instanceof Boss && vieAvant > 45 && ennemi.getVie() <= 45 && !ennemi.estMort())
                faits.add(new EvenementJeu(RAGE, ENNEMI, 0, ennemi.getVie()));
        } else if (action == ActionCombat.DEFENDRE) {
            faits.add(new EvenementJeu(DEFENSE, HEROS, GestionnaireCombat.BONUS_DEFENSE, heros.getVie()));
            message = "Vous vous protégez (+8 d'armure pour cette réponse).";
        } else {
            int vieAvant = heros.getVie();
            heros.getInventaire().consommerPotion(); heros.soigner(Potion.SOIN);
            faits.add(new EvenementJeu(SOIN, HEROS, heros.getVie() - vieAvant, heros.getVie()));
            message = "Potion consommée : +" + (heros.getVie() - vieAvant) + " PV.";
        }
        if (salleActuelle.getEnnemi() != null && (action != ActionCombat.POTION || estEnCombat()))
            message = finirAction(message, action == ActionCombat.DEFENDRE, faits);
        return new ResultatAction(true, message, avant, SauvegardePartie.capturer(this), faits);
    }

    /** Préserve l'API historique et ses exceptions, utilisée par les tests et l'exploration. */
    private String messageOuErreur(ResultatAction resultat) {
        if (!resultat.valide()) throw new IllegalStateException(resultat.message());
        return resultat.message();
    }
    /**
     * Change d'arme hors combat, sans jamais cumuler les bonus.
     * @param arme arme possédée
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
        boolean preparation = ennemi.puissanceProchaineAttaque() == 0;
        faits.add(new EvenementJeu(preparation ? PREPARATION : ATTAQUE, ENNEMI, 0, ennemi.getVie()));
        int vieAvant = heros.getVie();
        message += "\n" + GestionnaireCombat.repondre(heros, ennemi, defense);
        if (!preparation) faits.add(new EvenementJeu(DEGATS, HEROS, vieAvant - heros.getVie(), heros.getVie()));
        if (heros.estMort()) {
            etat = EtatPartie.DEFAITE;
            faits.add(new EvenementJeu(MORT, HEROS, 0, 0)); faits.add(new EvenementJeu(DEFAITE, HEROS, 0, 0));
            message += "\nDéfaite. Votre exploration s'achève ici.";
        }
        return message;
    }
}
