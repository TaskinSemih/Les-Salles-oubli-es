// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

// Rend disponibles les types ou constantes de java.util sans répéter leur nom complet.
import java.util.*;
// Rend disponibles les types ou constantes de fr.utbm.sallesoubliees.modele.EvenementJeu.Cible sans répéter leur nom complet.
import static fr.utbm.sallesoubliees.modele.EvenementJeu.Cible.*;
// Rend disponibles les types ou constantes de fr.utbm.sallesoubliees.modele.EvenementJeu.Type sans répéter leur nom complet.
import static fr.utbm.sallesoubliees.modele.EvenementJeu.Type.*;
// Rend disponibles les types ou constantes de fr.utbm.sallesoubliees.modele.EffetCombat.Type sans répéter leur nom complet.
import static fr.utbm.sallesoubliees.modele.EffetCombat.Type.*;

/** État et résolution tactiques : choix annoncés, effets, hasard et réactions atomiques. */
// Déclare CombatTactique, résolveur d'un tour complet : élan, effets, action du héros et réponse ennemie. final interdit de créer une sous-classe.
public final class CombatTactique {
    // Déclare la source de hasard de type SourceAleatoire.
    private final SourceAleatoire hasard;
    // Déclare la réserve d'élan de type int.
    private int elan;
    // Déclare l'intention ennemie annoncée de type DecisionEnnemi.
    private DecisionEnnemi decision;
    // Déclare la réponse ennemie précédente de type ActionEnnemi.
    private ActionEnnemi precedente;
    // Déclare le marqueur du soin ennemi déjà consommé de type boolean.
    private boolean soinUtilise;
    // Initialise les trois dernières actions du héros avec une nouvelle collection indépendante et vide.
    private final List<ActionCombat> historique = new ArrayList<>();
    // Initialise les altérations actives avec une nouvelle collection indépendante et vide.
    private final List<EffetCombat> effets = new ArrayList<>();
    // Initialise le choix courant avec une nouvelle collection indépendante et vide.
    private final Map<String, ChoixEvenement> choix = new HashMap<>();

    /** Définit les données de cette opération.
     * @param hasard source dont l'état permet une reprise fidèle */
    // Construit un objet CombatTactique : résolveur d'un tour complet : élan, effets, action du héros et réponse ennemie. Met à jour la source de hasard avec la référence reçue après refus de null.
    public CombatTactique(SourceAleatoire hasard) { this.hasard = Objects.requireNonNull(hasard); }
    /** {@return copie immuable du combat et des choix de salles} */
    // Produit un instantané indépendant pour décrire ou sauvegarder l’état courant. Renvoie un nouvel objet EtatTactique avec hasard.etat(), elan, decision, historique, precedente, soinUtilise, effets, choix.
    public EtatTactique capturer() { return new EtatTactique(hasard.etat(), elan, decision, historique, precedente, soinUtilise, effets, choix); }
    /** Définit les données de cette opération.
     * @param e données déjà validées
 * @return combat reconstruit avec le même futur aléatoire */
    // Reconstruit l’état reçu en respectant les contrôles de chargement.
    static CombatTactique restaurer(EtatTactique e) {
        // Initialise la variable c de type CombatTactique avec un nouvel objet CombatTactique avec new HasardReproductible(e.hasard()).
        CombatTactique c = new CombatTactique(new HasardReproductible(e.hasard()));
        // Recopie les ressources et la réponse annoncée ; le chargement ne retire donc pas une nouvelle intention.
        c.elan = e.elan(); c.decision = e.decision(); c.precedente = e.precedente(); c.soinUtilise = e.soinUtilise();
        // Recharge historique, effets et découvertes, puis renvoie le combat reconstruit.
        c.historique.addAll(e.historique()); c.effets.addAll(e.effets()); c.choix.putAll(e.choix()); return c;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Définit les données de cette opération.
     * @param ennemi adversaire rencontré */
    // Initialise un nouveau combat et son intention annoncée.
    void commencer(Ennemi ennemi) {
        // Efface le combat précédent, puis tire une première intention pour le nouvel adversaire.
        terminer(); selectionner(ennemi);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Nettoie les ressources et effets de combat, conserve les événements de salles. */
    // Nettoie le combat ou aligne la copie visuelle finale, selon cette classe. Met à jour la réserve d'élan avec 0. Met à jour l'intention ennemie annoncée avec l’absence de valeur (null). Met à jour la réponse ennemie précédente avec l’absence de valeur (null). Met à jour le marqueur du soin ennemi déjà consommé avec faux. Vide la collection courante. Vide la collection courante.
    void terminer() { elan = 0; decision = null; precedente = null; soinUtilise = false; historique.clear(); effets.clear(); }
    /** Choisit une seule intention par tour. */
    // Demande à la stratégie ennemie une intention unique pour le prochain tour.
    private void selectionner(Ennemi e) {
        // Fournit le contexte courant à la stratégie propre à cet ennemi et conserve son unique décision du tour.
        decision = e.decider(new ContexteCombat(elan, historique, precedente, soinUtilise), hasard);
        // Retire la garde ennemie de l’intention précédente avant d’installer la nouvelle.
        effets.removeIf(v -> v.type() == GARDE && v.cible() == ENNEMI);
        // Associe à la riposte annoncée une garde ennemie d’une action.
        if (decision.action() == ActionEnnemi.RIPOSTE) effets.add(new EffetCombat(GARDE, ENNEMI, 1));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** {@return réserve actuelle} */
    // Permet de consulter l'élan disponible. Renvoie la réserve d'élan.
    public int getElan() { return elan; }
    /** {@return intention courante, éventuellement null hors combat} */
    // Permet de consulter l'intention annoncée. Renvoie l'intention ennemie annoncée.
    public DecisionEnnemi getDecision() { return decision; }
    /** {@return effets consultables} */
    // Permet de consulter les effets actifs. Renvoie une copie non modifiable des altérations actives.
    public List<EffetCombat> getEffets() { return List.copyOf(effets); }
    /** {@return intention avec révélation éventuelle} */
    // {
    public String intention() {
        // Renvoie decision == null ? "" : decision.decrire(possede(LUCIDE, HEROS)).
        return decision == null ? "" : decision.decrire(possede(LUCIDE, HEROS));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Teste une altération active. */
    // Recherche dans les effets un couple type/cible correspondant ; anyMatch s’arrête dès qu’il le trouve.
    private boolean possede(EffetCombat.Type t, EvenementJeu.Cible c) { return effets.stream().anyMatch(e -> e.type() == t && e.cible() == c); }
    /** Ajoute un effet et son fait, remplace seulement le même type/cible. */
    // Met à jour les effets ou les valeurs du fait courant dans cette couche.
    private void appliquer(EffetCombat.Type t, EvenementJeu.Cible c, int duree, List<EvenementJeu> f) {
        // Remplace uniquement l’effet du même type sur la même cible, sans empiler les doublons.
        effets.removeIf(e -> e.type() == t && e.cible() == c); effets.add(new EffetCombat(t, c, duree));
        // Publie l’application de l’effet ; sa valeur est l’indice du type dans l’énumération.
        f.add(new EvenementJeu(EFFET, c, t.ordinal(), 0));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Consomme une altération. */
    // Consomme un effet du type et de la cible demandés et signale son expiration.
    private void retirer(EffetCombat.Type t, EvenementJeu.Cible c, List<EvenementJeu> f) {
        // Supprime l’effet visé et ne publie son expiration que s’il était réellement présent.
        if (effets.removeIf(e -> e.type() == t && e.cible() == c)) f.add(new EvenementJeu(EXPIRATION, c, t.ordinal(), 0));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Avance les effets anciens avant d'ajouter ceux de la réponse. */
    // Diminue la durée des anciens effets et publie ceux qui expirent.
    private void vieillir(List<EvenementJeu> f) {
        // Photographie les anciens effets puis vide la liste pour reconstruire leurs durées sans modifier la collection parcourue.
        var copie = List.copyOf(effets); effets.clear();
        // Parcourt copie ; chaque élément est placé dans EffetCombat e.
        for (EffetCombat e : copie) {
            // Conserve l’effet ancien en retirant une action à sa durée restante si celle-ci dépassait un.
            if (e.duree() > 1) effets.add(new EffetCombat(e.type(), e.cible(), e.duree() - 1));
            // Pour une durée arrivée à son dernier tour, publie l’expiration au lieu de conserver l’effet.
            else f.add(new EvenementJeu(EXPIRATION, e.cible(), e.type().ordinal(), 0));
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Change l'élan en publiant sa nouvelle valeur. */
    // Borne l’élan entre zéro et trois et publie les changements.
    private void elan(int valeur, List<EvenementJeu> f) {
        // Borne la nouvelle réserve d’élan entre 0 et 3.
        int nouveau = Math.max(0, Math.min(3, valeur));
        // Ne modifie et ne publie l’élan que si sa valeur bornée change réellement.
        if (nouveau != elan) { elan = nouveau; f.add(new EvenementJeu(ELAN, HEROS, elan, 0)); }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Applique une perte réelle de vie. */
    // Calcule les dégâts, modifie les PV et publie leur perte réelle.
    private void degats(Personnage p, int puissance, int armure, EvenementJeu.Cible c, List<EvenementJeu> f) {
        // Mémorise les PV puis applique les dégâts calculés, avec plancher de un et vie bornée à zéro.
        int avant = p.getVie(); p.subirDegats(GestionnaireCombat.calculerDegats(puissance, armure));
        // Publie les PV réellement perdus et les PV restants, plutôt que des dégâts théoriques dépassant la vie.
        f.add(new EvenementJeu(DEGATS, c, avant - p.getVie(), p.getVie()));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Résout héros et réponse sans réappliquer les règles à l'animation.
     *
 * @param h héros
 * @param e ennemi vivant
 * @param a commande validée
 * @param f faits ordonnés
     *
 * @return résumé court */
    // Valide ou résout une commande complète et fournit les faits dans leur ordre métier.
    String resoudre(Heros h, Ennemi e, ActionCombat a, List<EvenementJeu> f) {
        // Fige l’intention déjà annoncée pour résoudre ce tour sans nouveau tirage.
        DecisionEnnemi d = decision;
        // Mémorise la phase avant le coup du héros pour détecter le franchissement d’un seuil de vie.
        int phaseAvant = phase(e);
        // Repère les deux commandes qui portent un coup : attaque rapide et frappe puissante.
        boolean frappe = a == ActionCombat.ATTAQUER || a == ActionCombat.PUISSANTE;
        // Une attaque rapide interrompt une préparation ; la frappe puissante ne déclenche pas cette interruption.
        boolean interrompu = frappe && a == ActionCombat.ATTAQUER && d.action() == ActionEnnemi.PREPARATION;
        // La garde prépare une contre-attaque seulement si le héros choisit de frapper.
        boolean riposte = frappe && d.action() == ActionEnnemi.RIPOSTE;
        // Si frappe, entre dans ce traitement conditionnel.
        if (frappe) {
            // Part de l’attaque équipée du héros et retire 4 si la malédiction AFFAIBLI est active.
            int puissance = h.getAttaque() - (possede(AFFAIBLI, HEROS) ? 4 : 0);
            // Additionne armure de base, 6 d’armure renforcée éventuelle et 6 de garde en cas de riposte.
            int armure = e.getArmure() + (possede(ARMURE_RENFORCEE, ENNEMI) ? 6 : 0) + (riposte ? 6 : 0);
            // Si a est égal à ActionCombat.PUISSANTE, entre dans ce traitement conditionnel.
            if (a == ActionCombat.PUISSANTE) {
                // La frappe puissante dépense 2 élans, gagne 10 de puissance et ignore les protections temporaires, en conservant l’armure de base adverse.
                elan(elan - 2, f); puissance += 10; armure = e.getArmure();
                // Si (riposte ou possede(ARMURE_RENFORCEE, ENNEMI)), entre dans ce traitement conditionnel.
                if (riposte || possede(ARMURE_RENFORCEE, ENNEMI)) {
                    // Supprime l’armure renforcée et signale la protection brisée ; une riposte reste possible si l’ennemi survit.
                    retirer(ARMURE_RENFORCEE, ENNEMI, f); f.add(new EvenementJeu(BRISE_GARDE, ENNEMI, 0, e.getVie()));
                // Ferme le bloc ou la liste de données commencé plus haut.
                }
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
            // Une vulnérabilité ennemie ajoute 6 au coup puis est consommée immédiatement.
            if (possede(VULNERABLE, ENNEMI)) { puissance += 6; retirer(VULNERABLE, ENNEMI, f); }
            // Annonce le geste du héros puis résout immédiatement les dégâts sur l’ennemi, dans cet ordre.
            f.add(new EvenementJeu(ATTAQUE, HEROS, 0, h.getVie())); degats(e, puissance, armure, ENNEMI, f);
            // Une attaque rapide rapporte 1 élan, dans la limite de 3.
            if (a == ActionCombat.ATTAQUER) elan(elan + 1, f);
        // Si a est égal à ActionCombat.POTION, entre dans ce traitement conditionnel.
        } else if (a == ActionCombat.POTION) {
            // Mémorise la vie, retire une potion et soigne jusqu’à 35 PV sans dépasser le maximum.
            int avant = h.getVie(); h.getInventaire().consommerPotion(); h.soigner(Potion.SOIN);
            // Publie uniquement le soin réellement reçu par le héros, avec ses PV après soin.
            f.add(new EvenementJeu(SOIN, HEROS, h.getVie() - avant, h.getVie()));
        // Pour la défense, publie une protection de 8 pour la réponse qui suit ; la protection de l’onde sera réduite à 4.
        } else if (a == ActionCombat.DEFENDRE) f.add(new EvenementJeu(DEFENSE, HEROS, 8, h.getVie()));
        // La dernière commande possible est l’observation : publie le fait de regarder l’intention.
        else f.add(new EvenementJeu(OBSERVATION, HEROS, 0, h.getVie()));
        // Fait expirer ou diminuer les anciens effets après l’action du héros, avant les nouveaux effets de la réponse.
        vieillir(f);
        // Observer applique la lucidité pendant deux actions pour révéler les intentions imprécises.
        if (a == ActionCombat.OBSERVER) appliquer(LUCIDE, HEROS, 2, f);
        // Enregistre l’action et ne conserve que les trois plus récentes pour l’adaptation ennemie.
        historique.add(a); if (historique.size() > 3) historique.removeFirst();
        // Un ennemi tué par le héros ne joue aucune réponse : le tour tactique s’arrête ici.
        if (e.estMort()) return "L'adversaire est vaincu.";
        // Si le coup a franchi un seuil de vie du Gardien, annonce son changement de phase avant la réponse.
        if (phase(e) != phaseAvant) {
            // Ajoute l’élément à la collection ou au conteneur ; arguments : new EvenementJeu(PHASE, ENNEMI, phase(e), e.getVie()).
            f.add(new EvenementJeu(PHASE, ENNEMI, phase(e), e.getVie()));
            // Ajoute l’élément à la collection ou au conteneur ; arguments : new EvenementJeu(RAGE, ENNEMI, phase(e), e.getVie()).
            f.add(new EvenementJeu(RAGE, ENNEMI, phase(e), e.getVie()));
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Incrémente le compteur de réponses pour cet ennemi survivant.
        e.terminerTour();
        // Part de l’action annoncée ; une interruption pourra la remplacer par une récupération.
        ActionEnnemi executee = d.action();
        // Si interrompu, entre dans ce traitement conditionnel.
        if (interrompu) {
            // Ajoute l’élément à la collection ou au conteneur ; arguments : new EvenementJeu(INTERRUPTION, ENNEMI, 0, e.getVie()).
            f.add(new EvenementJeu(INTERRUPTION, ENNEMI, 0, e.getVie()));
            // L’interruption rend l’ennemi vulnérable et mémorise une récupération à la place de sa préparation.
            appliquer(VULNERABLE, ENNEMI, 1, f); executee = ActionEnnemi.RECUPERATION;
        // Sélectionne le traitement correspondant à la valeur examinée dans le switch.
        } else switch (d.action()) {
            // Pour RIPOSTE : ouvre le traitement de cette branche.
            case RIPOSTE -> {
                // Si le héros a frappé dans la garde, annonce la contre-attaque puis applique le coup ennemi.
                if (riposte) { f.add(new EvenementJeu(CONTRE_ATTAQUE, ENNEMI, 0, e.getVie())); frapper(h, e, d, a, f); }
                // Si le héros n’a pas frappé dans la garde, l’ennemi n’attaque pas et devient vulnérable.
                else appliquer(VULNERABLE, ENNEMI, 1, f);
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
            // Pour ATTAQUE, FRAPPE_LOURDE, ONDE : ouvre le traitement de cette branche.
            case ATTAQUE, FRAPPE_LOURDE, ONDE -> {
                // Résout la frappe ennemie et ses effets associés ; arguments : h, e, d, a, f.
                frapper(h, e, d, a, f);
                // Après une frappe lourde ou une onde, ouvre une vulnérabilité d’une action.
                if (d.action() == ActionEnnemi.FRAPPE_LOURDE || d.action() == ActionEnnemi.ONDE) appliquer(VULNERABLE, ENNEMI, 1, f);
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
            // Pour PREPARATION : Ajoute l’élément à la collection ou au conteneur ; arguments : new EvenementJeu(PREPARATION, ENNEMI, 0, e.getVie()).
            case PREPARATION -> f.add(new EvenementJeu(PREPARATION, ENNEMI, 0, e.getVie()));
            // Pour ARMURE : Applique l’effet ou le fait courant dans cette couche ; arguments : ARMURE_RENFORCEE, ENNEMI, 2, f.
            case ARMURE -> appliquer(ARMURE_RENFORCEE, ENNEMI, 2, f);
            // Pour MALEDICTION : Applique l’effet ou le fait courant dans cette couche ; arguments : AFFAIBLI, HEROS, 2, f.
            case MALEDICTION -> appliquer(AFFAIBLI, HEROS, 2, f);
            // Pour MARQUE : Applique l’effet ou le fait courant dans cette couche ; arguments : MARQUE, HEROS, 3, f.
            case MARQUE -> appliquer(MARQUE, HEROS, 3, f);
            // Pour RECUPERATION : Applique l’effet ou le fait courant dans cette couche ; arguments : VULNERABLE, ENNEMI, 1, f.
            case RECUPERATION -> appliquer(VULNERABLE, ENNEMI, 1, f);
            // Pour SOIN : ouvre le traitement de cette branche.
            case SOIN -> {
                // Le soin ennemi restaure jusqu’à 10 PV et consomme définitivement son unique soin de ce combat.
                int avant = e.getVie(); e.soigner(10); soinUtilise = true;
                // Ajoute l’élément à la collection ou au conteneur ; arguments : new EvenementJeu(SOIN, ENNEMI, e.getVie() - avant, e.getVie()).
                f.add(new EvenementJeu(SOIN, ENNEMI, e.getVie() - avant, e.getVie()));
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Conserve la réponse effectivement jouée pour orienter la prochaine décision ennemie.
        precedente = executee;
        // Si le héros survit, tire exactement une intention suivante et publie son annonce.
        if (!h.estMort()) { selectionner(e); f.add(new EvenementJeu(INTENTION, ENNEMI, 0, e.getVie())); }
        // Compose le récit de l’intention réellement révélée, de l’interruption éventuelle et de la répétition de trois commandes identiques.
        return d.decrire(true) + (interrompu ? " Préparation interrompue !" : "") + (historique.size() == 3 && historique.stream().distinct().count() == 1 ? " Il reconnaît vos habitudes." : "");
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Phase déduite des PV, sans état contradictoire à sauvegarder. */
    // Le Gardien passe en phase 3 à 22 PV, en phase 2 à 45 PV, sinon reste en phase 1 ; les autres ennemis restent en phase 1.
    private int phase(Ennemi e) { return e.getType().equals("boss") ? e.getVie() <= 22 ? 3 : e.getVie() <= 45 ? 2 : 1 : 1; }
    /** Résout une seule frappe et les déclenchements associés. */
    // Applique une frappe ennemie avec défense temporaire, marque et éventuel gain d’élan.
    private void frapper(Heros h, Ennemi e, DecisionEnnemi d, ActionCombat a, List<EvenementJeu> f) {
        // La défense donne 8 contre une frappe normale, 4 contre une onde, et zéro si le héros n’a pas défendu.
        int protection = a == ActionCombat.DEFENDRE ? d.action() == ActionEnnemi.ONDE ? 4 : 8 : 0;
        // Une marque sur le héros augmente de 4 la puissance du prochain impact ennemi.
        int bonus = possede(MARQUE, HEROS) ? 4 : 0;
        // Consomme la marque avant les dégâts pour qu’elle ne renforce qu’un seul impact.
        retirer(MARQUE, HEROS, f);
        // Ajoute l’élément à la collection ou au conteneur ; arguments : new EvenementJeu(ATTAQUE, ENNEMI, 0, e.getVie()).
        f.add(new EvenementJeu(ATTAQUE, ENNEMI, 0, e.getVie()));
        // Frappe le héros avec puissance annoncée et marque ; le mage ignore l’armure de base, mais pas la défense temporaire.
        degats(h, d.puissance() + bonus, (e.ignoreArmure() ? 0 : h.getArmure()) + protection, HEROS, f);
        // Accorde 1 élan si la défense a réellement réduit les dégâts au-delà du plancher de un.
        if (protection > 0 && d.puissance() + bonus > (e.ignoreArmure() ? 0 : h.getArmure()) + 1) elan(elan + 1, f);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Définit les données de cette opération.
     * @param salle identifiant
 * @return événement non résolu ou null */
    // Construit la découverte encore disponible pour la salle demandée.
    public EvenementSalle evenement(String salle) {
        // Une découverte déjà décidée ne doit plus être proposée dans cette salle.
        if (choix.containsKey(salle)) return null;
        // Associe un titre aux trois salles de découverte ; les autres identifiants ne proposent aucun événement.
        String titre = switch (salle) { case "arsenal" -> "Le mécanisme du coffre"; case "bibliotheque" -> "La rune interdite"; case "tresor" -> "L'or sous les dalles"; default -> null; };
        // Renvoie null sans découverte, sinon commence la construction d’une proposition immuable.
        return titre == null ? null : new EvenementSalle(salle, titre,
                // La bibliothèque utilise une description de runes qui soignent, plutôt qu’une récompense en potions.
                salle.equals("bibliotheque")
                    // Décrit les deux soins de rune et le risque de brûlure non mortelle du choix risqué.
                    ? "Rune stable : +15 PV. Rune interdite : +35 PV, 40 % de brûlure (-12 PV après le soin, jamais mortelle). Ignorer : aucun effet."
                    // Pour l’arsenal ou le trésor, décrit les potions gagnées et le piège possible du choix risqué.
                    : "Prudent : +1 potion. Risquer : +2 potions, 40 % de piège (-12 PV, jamais mortel). Ignorer : aucun bonus.",
                // Fournit les trois valeurs de ChoixEvenement dans une liste non modifiable.
                List.of(ChoixEvenement.values()));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Résout une fois un choix dont les options ont été vérifiées. */
    // Résout une découverte une seule fois, avec récompense et éventuel piège non mortel.
    String choisir(String salle, ChoixEvenement choixEffectue, Heros h) {
        // Si (choixEffectue est égal à l’absence de valeur (null) ou evenement(salle) est égal à l’absence de valeur (null)), refuse l’opération : "Événement déjà résolu ou absent.".
        if (choixEffectue == null || evenement(salle) == null) throw new IllegalStateException("Événement déjà résolu ou absent.");
        // Mémorise le choix avant d’appliquer sa conséquence, pour interdire une seconde récompense.
        choix.put(salle, choixEffectue);
        // Si choixEffectue est égal à ChoixEvenement.IGNORER, renvoie le texte "Vous laissez le mystère derrière vous.".
        if (choixEffectue == ChoixEvenement.IGNORER) return "Vous laissez le mystère derrière vous.";
        // Initialise le stock de potions avec choixEffectue == ChoixEvenement.RISQUER ? 2 : 1.
        int potions = choixEffectue == ChoixEvenement.RISQUER ? 2 : 1;
        // Si salle.equals("bibliotheque"), Restaure les PV sans dépasser le maximum du personnage ; arguments : choixEffectue == ChoixEvenement.RISQUER ? 35 : 15.
        if (salle.equals("bibliotheque")) h.soigner(choixEffectue == ChoixEvenement.RISQUER ? 35 : 15);
        // Dans le cas contraire, Ajoute la quantité de potions à l’inventaire ; arguments : potions.
        else h.getInventaire().ajouterPotions(potions);
        // Seul le choix risqué effectue un tirage : sous 40 sur 100, un piège se déclenche.
        boolean piege = choixEffectue == ChoixEvenement.RISQUER && hasard.prochainEntier(100) < 40;
        // Le piège retire au maximum 12 PV et laisse toujours au moins 1 PV au héros.
        if (piege) h.subirDegats(Math.min(12, h.getVie() - 1));
        // Construit le début du récit : soin de rune à la bibliothèque, nombre de potions ailleurs.
        return (salle.equals("bibliotheque") ? "La rune restaure vos PV." : "+" + potions + " potion(s).")
                // Complète le récit selon le déclenchement du piège, sans effectuer un autre tirage.
                + (piege ? " Un piège vous blesse (au plus 12 PV)." : " Vous repartez indemne.");
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
