// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

// Rend disponible ArrayDeque depuis java.util.
import java.util.ArrayDeque;
// Rend disponible ArrayList depuis java.util.
import java.util.ArrayList;
// Rend disponible EnumSet depuis java.util.
import java.util.EnumSet;
// Rend disponible HashSet depuis java.util.
import java.util.HashSet;
// Rend disponible Set depuis java.util.
import java.util.Set;
// Rend disponibles les types ou constantes de fr.utbm.sallesoubliees.modele.EtatSauvegarde sans répéter leur nom complet.
import static fr.utbm.sallesoubliees.modele.EtatSauvegarde.*;

/** Conversion entre le modèle encapsulé et les données, avec validation avant publication. */
// Déclare SauvegardePartie, conversion entre partie et instantané, avec contrôle des règles avant restauration. final interdit de créer une sous-classe.
public final class SauvegardePartie {
    /** Version actuellement acceptée. */
    // Initialise la version du format de sauvegarde avec 2.
    public static final int VERSION = 2;
    /** Classe utilitaire. */
    // Empêche la création extérieure de cette classe utilitaire grâce à un constructeur privé.
    private SauvegardePartie() { }

    /**
     * Capture la partie entre deux actions métier.
     *
 * @param partie partie à photographier
     *
 * @return instantané indépendant des mutations suivantes
     */
    // Produit un instantané indépendant pour décrire ou sauvegarder l’état courant.
    public static EtatSauvegarde capturer(Partie partie) {
        // Initialise la variable h de type Heros avec le héros de la partie courante. Initialise la variable i de type Inventaire avec l'inventaire du héros.
        Heros h = partie.getHeros(); Inventaire i = h.getInventaire();
        // Initialise le héros avec new EtatHeros(h.getVie(), h.getVieMax(), h.getAttaqueBase(), h.getArmure(),.
        EtatHeros heros = new EtatHeros(h.getVie(), h.getVieMax(), h.getAttaqueBase(), h.getArmure(),
                // Copie les armes possédées et enregistre séparément l’arme équipée et le stock de potions.
                new ArrayList<>(i.getArmes()), i.getEquipee(), i.getPotions());
        // Initialise les salles avec une nouvelle collection indépendante et vide.
        var salles = new ArrayList<EtatSalle>();
        // Parcourt partie.getDonjon().getSalles() ; chaque élément est placé dans Salle salle.
        for (Salle salle : partie.getDonjon().getSalles()) {
            // Initialise la variable e de type Ennemi avec salle.getEnnemi().
            Ennemi e = salle.getEnnemi();
            // Commence l’instantané de salle avec son identifiant et ses marqueurs de visite et d’utilisation.
            salles.add(new EtatSalle(salle.getId(), salle.estVisitee(), salle.estUtilisee(),
                    // Enregistre null dans une salle sans ennemi ; sinon capture type, PV et compteur de réponses.
                    e == null ? null : new EtatEnnemi(e.getType(), e.getVie(), e.getTours())));
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Renvoie un nouvel objet EtatSauvegarde avec VERSION, heros, partie.getSalleActuelle().getId(), partie.getEtat(), salles, partie.getTactique().capturer().
        return new EtatSauvegarde(VERSION, heros, partie.getSalleActuelle().getId(), partie.getEtat(), salles, partie.getTactique().capturer());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    /**
     * Reconstruit une nouvelle partie seulement si toutes les relations sont cohérentes.
     *
 * @param donnees instantané non fiable provenant d'un fichier
     *
 * @return nouvelle partie validée
     *
 * @throws IllegalArgumentException si une donnée ou relation est invalide
     */
    // Reconstruit l’état reçu en respectant les contrôles de chargement.
    public static Partie restaurer(EtatSauvegarde donnees) {
        // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : "Version de sauvegarde incompatible.".
        verifier(donnees != null && donnees.version() == VERSION, "Version de sauvegarde incompatible.");
        // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : "État incomplet.".
        verifier(donnees.heros() != null && donnees.etat() != null, "État incomplet.");
        // Initialise le héros avec un nouvel objet Heros. Initialise le graphe des salles avec un nouvel objet Donjon.
        Heros heros = new Heros(); Donjon donjon = new Donjon();
        // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : "Liste de salles incomplète.".
        verifier(donnees.salles().size() == donjon.getSalles().size(), "Liste de salles incomplète.");
        // Initialise « ids » avec une nouvelle collection indépendante et vide.
        Set<String> ids = new HashSet<>();
        // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : "Tactique absente. Les sauvegardes version 1 ne sont plus compatibles.".
        verifier(donnees.tactique() != null, "Tactique absente. Les sauvegardes version 1 ne sont plus compatibles.");
        // Initialise « potions disponibles » avec 3.
        int potionsDisponibles = 3;
        // Parcourt donnees.tactique().choix().entrySet() ; chaque élément est placé dans var choix.
        for (var choix : donnees.tactique().choix().entrySet()) {
            // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : "arsenal".
            verifier(Set.of("arsenal", "bibliotheque", "tresor").contains(choix.getKey()), "Événement inconnu.");
            // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : donnees.salles().stream().anyMatch(s -> s.id().equals(choix.getKey()) && s.visitee(.
            verifier(donnees.salles().stream().anyMatch(s -> s.id().equals(choix.getKey()) && s.visitee()
                    // Exige aussi que l’ennemi de la salle découverte soit absent ou vaincu, sinon refuse l’événement inaccessible.
                    && (s.ennemi() == null || s.ennemi().vie() == 0)), "Événement inaccessible.");
            // Commence le calcul des potions qu’un événement déjà résolu a pu fournir.
            potionsDisponibles += choix.getKey().equals("bibliotheque") || choix.getValue() == ChoixEvenement.IGNORER
                    // La bibliothèque et le choix ignoré donnent zéro potion ; le risque en donne deux, le choix prudent une.
                    ? 0 : choix.getValue() == ChoixEvenement.RISQUER ? 2 : 1;
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Initialise « armes attendues » avec EnumSet.of(Arme.ROUILLEE).
        Set<Arme> armesAttendues = EnumSet.of(Arme.ROUILLEE);
        // Parcourt les données sauvegardées des salles de « donnees » ; chaque élément est placé dans EtatSalle sauvegardee.
        for (EtatSalle sauvegardee : donnees.salles()) {
            // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : "Salle absente ou dupliquée.".
            verifier(sauvegardee != null && ids.add(sauvegardee.id()), "Salle absente ou dupliquée.");
            // Initialise la salle courante avec donjon.getSalle(sauvegardee.id()).
            Salle salle = donjon.getSalle(sauvegardee.id());
            // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : "Utilisation de salle incohérente.".
            verifier(!sauvegardee.utilisee() || (sauvegardee.visitee() && (salle.getCoffre() != null || salle.estRepos())), "Utilisation de salle incohérente.");
            // Si sauvegardee.visitee(), Marque la salle comme visitée.
            if (sauvegardee.visitee()) salle.visiter();
            // Si sauvegardee.utilisee(), entre dans ce traitement conditionnel.
            if (sauvegardee.utilisee()) {
                // Consomme définitivement le coffre ou le repos de la salle.
                salle.utiliser();
                // Si l'arme du coffre de la salle courante est différent de null, entre dans ce traitement conditionnel.
                if (salle.getCoffre() != null) {
                    // Ajoute l’élément à la collection ou au conteneur ; arguments : salle.getCoffre().
                    armesAttendues.add(salle.getCoffre());
                    // Augmente « potions disponibles » avec salle.getCoffre() == Arme.ACIER ? 2 : 1.
                    potionsDisponibles += salle.getCoffre() == Arme.ACIER ? 2 : 1;
                // Ferme le bloc ou la liste de données commencé plus haut.
                }
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
            // Initialise l'ennemi avec salle.getEnnemi(). Initialise la variable e de type EtatEnnemi avec sauvegardee.ennemi().
            Ennemi ennemi = salle.getEnnemi(); EtatEnnemi e = sauvegardee.ennemi();
            // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : "Ennemi absent ou inattendu.".
            verifier((ennemi == null) == (e == null), "Ennemi absent ou inattendu.");
            // Si l'ennemi est différent de null, entre dans ce traitement conditionnel.
            if (ennemi != null) {
                // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : "Type d'ennemi inconnu pour cette salle.".
                verifier(ennemi.getType().equals(e.type()), "Type d'ennemi inconnu pour cette salle.");
                // Restaure les PV après contrôle de leurs bornes ; arguments : e.vie(). Restaure le nombre de réponses ennemies après contrôle de ses bornes ; arguments : e.tours().
                ennemi.restaurerVie(e.vie()); ennemi.restaurerTours(e.tours());
                // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : "Ennemi blessé sans tour de combat.".
                verifier(e.tours() != 0 || e.vie() == ennemi.getVieMax(), "Ennemi blessé sans tour de combat.");
                // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : "Ennemi modifié sans visite.".
                verifier(salle.estVisitee() || (e.vie() == ennemi.getVieMax() && e.tours() == 0), "Ennemi modifié sans visite.");
                // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : "Combat abandonné dans une autre salle.".
                verifier(!salle.estVisitee() || ennemi.estMort() || salle.getId().equals(donnees.salleActuelle()), "Combat abandonné dans une autre salle.");
                // Si (si les PV valent zéro de l'ennemi et non ((ennemi instanceof Boss))), Incrémente « potions disponibles » de un.
                if (ennemi.estMort() && !(ennemi instanceof Boss)) potionsDisponibles++;
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Initialise la variable h de type EtatHeros avec les données sauvegardées du héros de « donnees ».
        EtatHeros h = donnees.heros();
        // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : "Statistiques incompatibles avec les règles.".
        verifier(h.vieMax() == heros.getVieMax() && h.attaque() == heros.getAttaqueBase() && h.armure() == heros.getArmure(), "Statistiques incompatibles avec les règles.");
        // Initialise les armes possédées avec une nouvelle collection indépendante initialisée avec h.armes().
        Set<Arme> armes = new HashSet<>(h.armes());
        // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : "Armes et coffres incohérents.".
        verifier(armes.size() == h.armes().size() && armes.equals(armesAttendues) && armes.contains(h.equipee()), "Armes et coffres incohérents.");
        // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : "Quantité de potions incohérente.".
        verifier(h.potions() >= 0 && h.potions() <= potionsDisponibles, "Quantité de potions incohérente.");
        // Restaure les PV après contrôle de leurs bornes ; arguments : h.vie(). Recharge les valeurs reçues dans cet objet après validation ; arguments : armes, h.equipee(), h.potions().
        heros.restaurerVie(h.vie()); heros.getInventaire().restaurer(armes, h.equipee(), h.potions());
        // Initialise « actuelle » avec donjon.getSalle(donnees.salleActuelle()).
        Salle actuelle = donjon.getSalle(donnees.salleActuelle());
        // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : "entree".
        verifier(actuelle.estVisitee() && donjon.getSalle("entree").estVisitee(), "Salle actuelle ou entrée non visitée.");
        // Contrôle la cohérence des salles visitées par un parcours depuis l’entrée ; arguments : donjon.
        verifierParcours(donjon);
        // Initialise « boss mort » avec donjon.getSalle("boss").getEnnemi().estMort().
        boolean bossMort = donjon.getSalle("boss").getEnnemi().estMort();
        // Initialise « attendu » avec heros.estMort() ? EtatPartie.DEFAITE : bossMort ? EtatPartie.VICTOIRE : EtatPartie.EN_COURS.
        EtatPartie attendu = heros.estMort() ? EtatPartie.DEFAITE : bossMort ? EtatPartie.VICTOIRE : EtatPartie.EN_COURS;
        // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : "Issue incompatible avec les points de vie.".
        verifier(donnees.etat() == attendu, "Issue incompatible avec les points de vie.");
        // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : "boss".
        verifier(!bossMort || (!heros.estMort() && actuelle.getId().equals("boss")), "Victoire située hors du combat final.");
        // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : "Défaite hors combat.".
        verifier(!heros.estMort() || actuelle.estHostile(), "Défaite hors combat.");
        // Initialise la partie courante avec un nouvel objet Partie avec heros, donjon, actuelle.getId(), donnees.etat().
        Partie partie = new Partie(heros, donjon, actuelle.getId(), donnees.etat());
        // Initialise « t » avec l'état tactique sauvegardé de « donnees ».
        EtatTactique t = donnees.tactique();
        // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : "Intention incompatible avec le combat.".
        verifier((t.decision() != null) == partie.estEnCombat(), "Intention incompatible avec le combat.");
        // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : (si un combat est actif de la partie courante ou (t.elan() == 0 && t.historique().isEmpty() && t.effets().isEmpty().
        verifier(partie.estEnCombat() || (t.elan() == 0 && t.historique().isEmpty() && t.effets().isEmpty()
                // Exige aussi l’absence de réponse précédente et de soin consommé lorsqu’aucun combat n’est actif.
                && t.precedente() == null && !t.soinUtilise()), "États de combat hors combat.");
        // Si si un combat est actif de la partie courante, entre dans ce traitement conditionnel.
        if (partie.estEnCombat()) {
            // Initialise la variable e de type Ennemi avec actuelle.getEnnemi().
            Ennemi e = actuelle.getEnnemi();
            // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : "mage".
            verifier(!t.soinUtilise() || e.getType().equals("mage"), "Soin réservé au mage.");
            // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : "Décision ennemie incohérente.".
            verifier(DecisionAutorisee.verifier(e, t.decision()), "Décision ennemie incohérente.");
            // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : t.effets().stream().anyMatch(v -> v.type() == EffetCombat.Type.GARDE.
            verifier(t.effets().stream().anyMatch(v -> v.type() == EffetCombat.Type.GARDE)
                    // Exige une garde exactement lorsque l’intention annoncée est une riposte.
                    == (t.decision().action() == ActionEnnemi.RIPOSTE), "Garde incompatible avec l'intention.");
            // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : t.effets().stream().allMatch(v -> v.cible() == EvenementJeu.Cible.HEROS.
            verifier(t.effets().stream().allMatch(v -> v.cible() == EvenementJeu.Cible.HEROS
                    // Pour une cible héros, seuls affaiblissement, marque et lucidité sont des effets autorisés.
                    ? Set.of(EffetCombat.Type.AFFAIBLI, EffetCombat.Type.MARQUE, EffetCombat.Type.LUCIDE).contains(v.type())
                    // Pour une cible ennemi, seuls armure renforcée, vulnérabilité et garde sont autorisés.
                    : Set.of(EffetCombat.Type.ARMURE_RENFORCEE, EffetCombat.Type.VULNERABLE, EffetCombat.Type.GARDE).contains(v.type())),
                    // Refuse la sauvegarde si un effet a été attribué à une cible incompatible.
                    "Cible d'altération incohérente.");
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Recharge l’intention, les ressources et les effets tactiques fournis ; arguments : t.
        partie.restaurerTactique(t);
        // Renvoie la partie courante.
        return partie;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    /** Vérifie que les visites sont accessibles depuis l'entrée sans traverser un ennemi vivant. */
    // Vérifie par parcours du graphe que les visites sont possibles sans traverser un ennemi vivant.
    private static void verifierParcours(Donjon donjon) {
        // Prépare l’ensemble des salles atteintes ; il évite les boucles et permet de contrôler ensuite toutes les visites.
        Set<String> atteintes = new HashSet<>();
        // Initialise une file de parcours en largeur en partant de l’entrée du donjon.
        var attente = new ArrayDeque<String>(); attente.add("entree");
        // Répète ce bloc tant que non (si la collection ou le texte est vide de « attente »).
        while (!attente.isEmpty()) {
            // Retire la plus ancienne salle en attente et retrouve l’objet correspondant dans le donjon.
            Salle salle = donjon.getSalle(attente.remove());
            // Ne poursuit pas au-delà d’une salle non visitée, déjà atteinte ou encore hostile ; une salle hostile visitée est marquée atteinte, mais ses voisins ne sont pas explorés.
            if (!salle.estVisitee() || !atteintes.add(salle.getId()) || salle.estHostile()) continue;
            // Ajoute les voisins de cette salle paisible à la file ; les doublons seront ignorés à leur prochain retrait.
            attente.addAll(salle.getPassages());
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Parcourt donjon.getSalles() ; chaque élément est placé dans Salle salle.
        for (Salle salle : donjon.getSalles()) {
            // Vérifie l’invariant et refuse la sauvegarde en cas d’échec : "Parcours de visites impossible.".
            verifier(!salle.estVisitee() || atteintes.contains(salle.getId()), "Parcours de visites impossible.");
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Transforme une violation en erreur explicite de validation. */
    // Contrôle la cohérence des valeurs reçues avant leur utilisation.
    private static void verifier(boolean condition, String message) {
        // Si non (condition), refuse l’opération : une exception signale l’incohérence.
        if (!condition) throw new IllegalArgumentException(message);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
