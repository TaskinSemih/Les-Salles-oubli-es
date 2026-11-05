// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

// Rend disponible ArrayList depuis java.util.
import java.util.ArrayList;
// Rend disponible List depuis java.util.
import java.util.List;
// Rend disponibles les types ou constantes de fr.utbm.sallesoubliees.modele.EvenementJeu.Cible sans répéter leur nom complet.
import static fr.utbm.sallesoubliees.modele.EvenementJeu.Cible.*;
// Rend disponibles les types ou constantes de fr.utbm.sallesoubliees.modele.EvenementJeu.Type sans répéter leur nom complet.
import static fr.utbm.sallesoubliees.modele.EvenementJeu.Type.*;

/** Point d'entrée des actions métier. Chaque action valide est résolue entièrement. */
// Déclare Partie, point d'entrée des règles : valides actions, coordonne exploration et combat, puis fixe l'issue. final interdit de créer une sous-classe.
public final class Partie {
    // Déclare le héros de type Heros.
    private final Heros heros;
    // Déclare le graphe des salles de type Donjon.
    private final Donjon donjon;
    // Déclare la salle occupée de type Salle.
    private Salle salleActuelle;
    // Déclare l'état courant de type EtatPartie.
    private EtatPartie etat;
    // Crée le résolveur tactique avec une graine issue de l’horloge ; son état pourra ensuite être capturé pour une reprise fidèle.
    private CombatTactique tactique = new CombatTactique(new HasardReproductible(System.nanoTime()));
    /** Définit les données de cette opération.
     * @param hasard source injectable pour les simulations et tests */
    // Construit un objet Partie : point d'entrée des règles : valides actions, coordonne exploration et combat, puis fixe l'issue. Délègue à l’autre constructeur avec .
    public Partie(SourceAleatoire hasard) { this(); tactique = new CombatTactique(hasard); }
    /** {@return tactique consultable sans commandes publiques de mutation} */
    // Permet de consulter l'état du combat tactique. Renvoie tactique.
    public CombatTactique getTactique() { return tactique; }
    /** Restaure la tactique après vérification des relations. */
    // Recharge la tactique dont les relations ont déjà été vérifiées. Met à jour « tactique » avec CombatTactique.restaurer(e).
    void restaurerTactique(EtatTactique e) { tactique = CombatTactique.restaurer(e); }
    /** {@return événement disponible dans la salle paisible, ou null} */
    // Ne propose une découverte que dans une partie en cours et hors combat ; renvoie null sinon.
    public EvenementSalle getEvenementSalle() { return estEnCours() && !estEnCombat() ? tactique.evenement(salleActuelle.getId()) : null; }
    /** Définit les données de cette opération.
     * @param choix décision proposée par le modèle
 * @return conséquence unique */
    // Définit les données de cette opération.
    public String choisirEvenement(ChoixEvenement choix) {
        // Si la découverte disponible est égal à l’absence de valeur (null), refuse l’opération : "Aucun événement disponible.".
        if (getEvenementSalle() == null) throw new IllegalStateException("Aucun événement disponible.");
        // Renvoie tactique.choisir(salleActuelle.getId(), choix, heros).
        return tactique.choisir(salleActuelle.getId(), choix, heros);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    /** Commence une partie indépendante à l'entrée du donjon. */
    // Construit un objet Partie : point d'entrée des règles : valides actions, coordonne exploration et combat, puis fixe l'issue.
    public Partie() {
        // Délègue l’initialisation à l’autre constructeur avec les arguments fournis.
        this(new Heros(), new Donjon(), "entree", EtatPartie.EN_COURS);
        // Marque la salle comme visitée.
        salleActuelle.visiter();
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Assemble un état restauré, déjà intégralement validé. */
    // Construit un objet Partie : point d'entrée des règles : valides actions, coordonne exploration et combat, puis fixe l'issue.
    Partie(Heros heros, Donjon donjon, String salle, EtatPartie etat) {
        // Met à jour le héros avec le héros. Met à jour le graphe des salles avec le graphe des salles. Met à jour la salle occupée avec donjon.getSalle(salle). Met à jour l'état courant avec l'état courant.
        this.heros = heros; this.donjon = donjon; this.salleActuelle = donjon.getSalle(salle); this.etat = etat;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** {@return héros consultable} */
    // Permet de consulter le héros. Renvoie le héros.
    public Heros getHeros() { return heros; }
    /** {@return donjon consultable} */
    // Permet de consulter le donjon. Renvoie le graphe des salles.
    public Donjon getDonjon() { return donjon; }
    /** {@return salle occupée} */
    // Permet de consulter la salle occupée. Renvoie la salle occupée.
    public Salle getSalleActuelle() { return salleActuelle; }
    /** {@return issue de la partie} */
    // Permet de consulter l'issue de la partie. Renvoie l'état courant.
    public EtatPartie getEtat() { return etat; }
    /** {@return vrai tant que la partie permet des actions} */
    // Indique si la partie autorise encore des actions. Renvoie etat == EtatPartie.EN_COURS.
    public boolean estEnCours() { return etat == EtatPartie.EN_COURS; }
    /** {@return vrai pendant un combat actif} */
    // Un combat est actif seulement si la partie est en cours et que la salle contient un ennemi vivant.
    public boolean estEnCombat() { return estEnCours() && salleActuelle.estHostile(); }
    /** {@return possibilité de boire une potion maintenant} */
    // Autorise une potion seulement si la partie est active, que le héros est blessé et qu’il possède au moins une potion.
    public boolean peutBoire() { return estEnCours() && heros.getVie() < heros.getVieMax() && heros.getInventaire().getPotions() > 0; }
    /** {@return possibilité d'ouvrir le coffre actuel} */
    // Autorise un coffre présent et inutilisé seulement hors combat, dans une partie encore active.
    public boolean peutOuvrir() { return estEnCours() && !estEnCombat() && salleActuelle.getCoffre() != null && !salleActuelle.estUtilisee(); }
    /** {@return possibilité d'utiliser le repos maintenant} */
    // Autorise le repos seulement dans une partie active, hors combat, au sanctuaire inutilisé et si le héros est blessé.
    public boolean peutReposer() { return estEnCours() && !estEnCombat() && salleActuelle.estRepos() && !salleActuelle.estUtilisee() && heros.getVie() < heros.getVieMax(); }
    /**
     * Indique si un passage peut être emprunté maintenant.
     *
 * @param id destination
     *
 * @return vrai pour une salle voisine hors combat
     */
    // Autorise le déplacement seulement pendant une partie active, hors combat et vers une salle réellement voisine.
    public boolean peutDeplacer(String id) { return estEnCours() && !estEnCombat() && salleActuelle.getPassages().contains(id); }

    /** Refuse toute action après une issue finale. */
    // Refuse toute action après une issue finale. Si non (si la partie autorise encore des actions), refuse l’opération : "Cette partie est terminée. Commencez une nouvelle partie.".
    private void exigerEnCours() { if (!estEnCours()) throw new IllegalStateException("Cette partie est terminée. Commencez une nouvelle partie."); }
    /** Refuse les actions de combat dans une salle paisible. */
    // Refuse les actions de combat dans une salle paisible. Refuse l’action si la partie est déjà terminée. Si non (si un combat est actif), refuse l’opération : "Aucun ennemi à combattre ici.".
    private void exigerCombat() { exigerEnCours(); if (!estEnCombat()) throw new IllegalStateException("Aucun ennemi à combattre ici."); }

    /**
     * Emprunte un passage et engage l'ennemi éventuel sans attaque gratuite.
     *
 * @param id salle voisine
     *
 * @return événement à afficher
     */
    // Demande ou applique un déplacement conforme aux passages du donjon.
    public String deplacer(String id) {
        // Refuse l’action si la partie est déjà terminée.
        exigerEnCours();
        // Si non (peutDeplacer(id)), refuse l’opération : "Déplacement impossible : terminez le combat et suivez les passages.".
        if (!peutDeplacer(id)) throw new IllegalStateException("Déplacement impossible : terminez le combat et suivez les passages.");
        // Met à jour la salle occupée avec donjon.getSalle(id). Marque la salle comme visitée.
        salleActuelle = donjon.getSalle(id); salleActuelle.visiter();
        // Si si un combat est actif, Initialise la tactique de ce nouveau combat ; arguments : salleActuelle.getEnnemi().
        if (estEnCombat()) tactique.commencer(salleActuelle.getEnnemi());
        // Renvoie "Vous entrez dans : " + salleActuelle.getNom() + (estEnCombat() ? ". Un ennemi bloque les passages !" : ".").
        return "Vous entrez dans : " + salleActuelle.getNom() + (estEnCombat() ? ". Un ennemi bloque les passages !" : ".");
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** {@return récit d'une attaque et de sa réponse éventuelle} */
    // {
    public String attaquer() {
        // Renvoie messageOuErreur(resoudre(ActionCombat.ATTAQUER)).
        return messageOuErreur(resoudre(ActionCombat.ATTAQUER));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** {@return récit d'une défense valable pour une seule réponse} */
    // { Renvoie messageOuErreur(resoudre(ActionCombat.DEFENDRE)).
    public String defendre() { return messageOuErreur(resoudre(ActionCombat.DEFENDRE)); }
    /** {@return récit du soin et de la réponse éventuelle} */
    // {
    public String boirePotion() {
        // Renvoie messageOuErreur(resoudre(ActionCombat.POTION)).
        return messageOuErreur(resoudre(ActionCombat.POTION));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    /**
     * Applique une seule fois la commande et produit ses faits, même si la vue les anime plus tard.
     * Une commande refusée garde exactement le même état et ne produit aucun événement.
     *
 * @param action commande du joueur
     *
 * @return résolution immuable
     */
    // Valide ou résout une commande complète et fournit les faits dans leur ordre métier.
    public ResultatAction resoudre(ActionCombat action) {
        // Initialise l'instantané avant l'action avec SauvegardePartie.capturer(this).
        EtatSauvegarde avant = SauvegardePartie.capturer(this);
        // Commence l’opération dont les erreurs sont prises en charge par les blocs catch suivants.
        try {
            // Refuse l’action si la partie est déjà terminée.
            exigerEnCours();
            // Si action est égal à l’absence de valeur (null), refuse l’opération : "Action inconnue.".
            if (action == null) throw new IllegalStateException("Action inconnue.");
            // Si (action est égal à ActionCombat.PUISSANTE et l'élan disponible de « tactique » est inférieur à 2), refuse l’opération : "Il faut 2 points d’élan.".
            if (action == ActionCombat.PUISSANTE && tactique.getElan() < 2) throw new IllegalStateException("Il faut 2 points d’élan.");
            // Si action est différent de ActionCombat.POTION, Refuse l’action si la partie est terminée ou si aucun combat n’est actif.
            if (action != ActionCombat.POTION) exigerCombat();
            // Si non (si une potion peut être consommée), refuse l’opération : "Potion impossible : vie déjà pleine ou inventaire vide.".
            else if (!peutBoire()) throw new IllegalStateException("Potion impossible : vie déjà pleine ou inventaire vide.");
        // Intercepte IllegalStateException erreur pour traiter l’échec. Ne contient aucune instruction supplémentaire.
        } catch (IllegalStateException erreur) {
            // Une commande refusée renvoie le même instantané avant et après, sans aucun fait ni consommation de tour.
            return new ResultatAction(false, erreur.getMessage(), avant, avant, List.of());
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Initialise la liste ordonnée d'événements avec une nouvelle collection indépendante et vide.
        List<EvenementJeu> faits = new ArrayList<>();
        // Déclare le texte à afficher de type String.
        String message;
        // Si si un combat est actif, entre dans ce traitement conditionnel.
        if (estEnCombat()) {
            // Met à jour le texte à afficher avec tactique.resoudre(heros, salleActuelle.getEnnemi(), action, faits).
            message = tactique.resoudre(heros, salleActuelle.getEnnemi(), action, faits);
            // Met à jour le texte à afficher avec finirAction(message, false, faits).
            message = finirAction(message, false, faits);
            // Si non (si un combat est actif), Termine le combat ou la lecture visuelle courante.
            if (!estEnCombat()) tactique.terminer();
        // Traite le cas où la condition précédente n’est pas satisfaite.
        } else {
            // Initialise « avant soin » avec les PV actuels du héros. Retire une potion disponible de l’inventaire. Restaure les PV sans dépasser le maximum du personnage ; arguments : Potion.SOIN.
            int avantSoin = heros.getVie(); heros.getInventaire().consommerPotion(); heros.soigner(Potion.SOIN);
            // Ajoute l’élément à la collection ou au conteneur ; arguments : new EvenementJeu(SOIN, HEROS, heros.getVie() - avantSoin, heros.getVie()).
            faits.add(new EvenementJeu(SOIN, HEROS, heros.getVie() - avantSoin, heros.getVie()));
            // Met à jour le texte à afficher avec le texte "Potion consommée.".
            message = "Potion consommée.";
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Initialise le récit détaillé des faits du tour avec faits.stream().map(f -> switch (f.type()) {.
        String details = faits.stream().map(f -> switch (f.type()) {
            // Transforme un fait de dégâts en phrase, en distinguant le héros de l’adversaire.
            case DEGATS -> (f.cible() == HEROS ? "Vous perdez " : "L'adversaire perd ") + f.valeur() + " PV.";
            // Transforme un fait de soin en phrase avec le montant réellement récupéré par la cible.
            case SOIN -> (f.cible() == HEROS ? "Vous récupérez " : "L'adversaire récupère ") + f.valeur() + " PV.";
            // Pour ELAN : fournit le texte "Élan : " + f.valeur() + "/3.".
            case ELAN -> "Élan : " + f.valeur() + "/3.";
            // Pour INTERRUPTION : fournit le texte "La préparation est interrompue.".
            case INTERRUPTION -> "La préparation est interrompue.";
            // Pour BRISE_GARDE : fournit le texte "La protection est brisée.".
            case BRISE_GARDE -> "La protection est brisée.";
            // Pour CONTRE_ATTAQUE : fournit le texte "L'adversaire riposte.".
            case CONTRE_ATTAQUE -> "L'adversaire riposte.";
            // Pour EFFET : fournit le texte "Altération : " + EffetCombat.Type.values()[f.valeur()] + " (" + f.cible() + ").".
            case EFFET -> "Altération : " + EffetCombat.Type.values()[f.valeur()] + " (" + f.cible() + ").";
            // Pour PHASE : fournit le texte "Le Gardien entre en phase " + f.valeur() + ".".
            case PHASE -> "Le Gardien entre en phase " + f.valeur() + ".";
            // Pour les autres cas : fournit le texte "".
            default -> "";
        // Écarte les phrases vides et rassemble les autres avec un saut de ligne entre chaque fait.
        }).filter(s -> !s.isEmpty()).collect(java.util.stream.Collectors.joining("\n"));
        // Renvoie la résolution complète : succès, récit enrichi, état avant, état final capturé et faits ordonnés.
        return new ResultatAction(true, message + (details.isEmpty() ? "" : "\n" + details), avant, SauvegardePartie.capturer(this), faits);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    /** Préserve l'API historique et ses exceptions, utilisée par les tests et l'exploration. */
    // Renvoie le récit ou transforme le refus en exception pour l’ancienne API.
    private String messageOuErreur(ResultatAction resultat) {
        // Si non (si la commande est valide du résultat de l'action), refuse l’opération : une exception signale l’incohérence.
        if (!resultat.valide()) throw new IllegalStateException(resultat.message());
        // Renvoie le récit de l'action du résultat de l'action.
        return resultat.message();
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /**
     * Change d'arme hors combat, sans jamais cumuler les bonus.
     *
 * @param arme arme possédée
     *
 * @return événement à afficher
     */
    // Change l’arme équipée si les règles autorisent son utilisation.
    public String equiper(Arme arme) {
        // Refuse l’action si la partie est déjà terminée.
        exigerEnCours();
        // Si si un combat est actif, refuse l’opération : "Changez d'arme avant le combat.".
        if (estEnCombat()) throw new IllegalStateException("Changez d'arme avant le combat.");
        // Équipe l’arme demandée en passant par les contrôles du modèle ; arguments : arme.
        heros.getInventaire().equiper(arme);
        // Renvoie "Arme équipée : " + arme + ".".
        return "Arme équipée : " + arme + ".";
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** {@return récompense du coffre consommé une seule fois} */
    // {
    public String ouvrirCoffre() {
        // Refuse l’action si la partie est déjà terminée.
        exigerEnCours();
        // Si non (si le coffre actuel peut être ouvert), refuse l’opération : "Aucun coffre à ouvrir ici.".
        if (!peutOuvrir()) throw new IllegalStateException("Aucun coffre à ouvrir ici.");
        // Initialise « arme » avec l'arme du coffre de la salle occupée.
        Arme arme = salleActuelle.getCoffre();
        // Ajoute l’arme aux possessions sans doublon ; arguments : arme.
        heros.getInventaire().ajouterArme(arme);
        // Initialise le stock de potions avec arme == Arme.ACIER ? 2 : 1.
        int potions = arme == Arme.ACIER ? 2 : 1;
        // Ajoute la quantité de potions à l’inventaire ; arguments : potions. Consomme définitivement le coffre ou le repos de la salle.
        heros.getInventaire().ajouterPotions(potions); salleActuelle.utiliser();
        // Renvoie "Coffre ouvert : " + arme + " et " + potions + " potion(s). Pensez à équiper l'arme.".
        return "Coffre ouvert : " + arme + " et " + potions + " potion(s). Pensez à équiper l'arme.";
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** {@return événement de repos, consommé seulement si le héros est blessé} */
    // {
    public String seReposer() {
        // Refuse l’action si la partie est déjà terminée.
        exigerEnCours();
        // Si non (si le sanctuaire peut être utilisé), refuse l’opération : "Repos indisponible : sanctuaire déjà utilisé, absent ou vie pleine.".
        if (!peutReposer()) throw new IllegalStateException("Repos indisponible : sanctuaire déjà utilisé, absent ou vie pleine.");
        // Restaure les PV sans dépasser le maximum du personnage ; arguments : heros.getVieMax(). Consomme définitivement le coffre ou le repos de la salle.
        heros.soigner(heros.getVieMax()); salleActuelle.utiliser();
        // Renvoie le texte "Le sanctuaire restaure tous vos PV. Son pouvoir est épuisé.".
        return "Le sanctuaire restaure tous vos PV. Son pouvoir est épuisé.";
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Termine atomiquement l'action ; récompense et issue précèdent toute contre-attaque. */
    // Attribue récompense, victoire ou défaite à partir des PV après le tour.
    private String finirAction(String message, boolean defense, List<EvenementJeu> faits) {
        // Initialise l'ennemi avec salleActuelle.getEnnemi().
        Ennemi ennemi = salleActuelle.getEnnemi();
        // Si l'ennemi est égal à l’absence de valeur (null), renvoie le texte à afficher.
        if (ennemi == null) return message;
        // Si si les PV valent zéro de l'ennemi, entre dans ce traitement conditionnel.
        if (ennemi.estMort()) {
            // Ajoute l’élément à la collection ou au conteneur ; arguments : new EvenementJeu(MORT, ENNEMI, 0, 0).
            faits.add(new EvenementJeu(MORT, ENNEMI, 0, 0));
            // Si ennemi instanceof Boss, entre dans ce traitement conditionnel.
            if (ennemi instanceof Boss) {
                // Met à jour l'état courant avec EtatPartie.VICTOIRE.
                etat = EtatPartie.VICTOIRE;
                // Ajoute l’élément à la collection ou au conteneur ; arguments : new EvenementJeu(VICTOIRE, HEROS, 0, heros.getVie()).
                faits.add(new EvenementJeu(VICTOIRE, HEROS, 0, heros.getVie()));
                // Renvoie message + "\nVictoire ! Le Gardien est vaincu. Les salles sont libérées.".
                return message + "\nVictoire ! Le Gardien est vaincu. Les salles sont libérées.";
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
            // Ajoute la quantité de potions à l’inventaire ; arguments : 1.
            heros.getInventaire().ajouterPotions(1);
            // Ajoute l’élément à la collection ou au conteneur ; arguments : new EvenementJeu(RECOMPENSE, HEROS, 1, heros.getVie()).
            faits.add(new EvenementJeu(RECOMPENSE, HEROS, 1, heros.getVie()));
            // Renvoie message + "\n" + ennemi.getNom() + " est vaincu. Vous trouvez une potion.".
            return message + "\n" + ennemi.getNom() + " est vaincu. Vous trouvez une potion.";
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Si si les PV valent zéro du héros, entre dans ce traitement conditionnel.
        if (heros.estMort()) {
            // Met à jour l'état courant avec EtatPartie.DEFAITE.
            etat = EtatPartie.DEFAITE;
            // Ajoute l’élément à la collection ou au conteneur ; arguments : new EvenementJeu(MORT, HEROS, 0, 0). Ajoute l’élément à la collection ou au conteneur ; arguments : new EvenementJeu(DEFAITE, HEROS, 0, 0).
            faits.add(new EvenementJeu(MORT, HEROS, 0, 0)); faits.add(new EvenementJeu(DEFAITE, HEROS, 0, 0));
            // Augmente le texte à afficher avec le texte "\nDéfaite. Votre exploration s'achève ici.".
            message += "\nDéfaite. Votre exploration s'achève ici.";
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Renvoie le texte à afficher.
        return message;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
