package fr.utbm.sallesoubliees.modele;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import static fr.utbm.sallesoubliees.modele.EtatSauvegarde.*;

/** Conversion entre le modèle encapsulé et les données, avec validation avant publication. */
public final class SauvegardePartie {
    /** Version actuellement acceptée. */
    public static final int VERSION = 1;
    /** Classe utilitaire. */
    private SauvegardePartie() { }

    /**
     * Capture la partie entre deux actions métier.
     * @param partie partie à photographier
     * @return instantané indépendant des mutations suivantes
     */
    public static EtatSauvegarde capturer(Partie partie) {
        Heros h = partie.getHeros(); Inventaire i = h.getInventaire();
        EtatHeros heros = new EtatHeros(h.getVie(), h.getVieMax(), h.getAttaqueBase(), h.getArmure(),
                new ArrayList<>(i.getArmes()), i.getEquipee(), i.getPotions());
        var salles = new ArrayList<EtatSalle>();
        for (Salle salle : partie.getDonjon().getSalles()) {
            Ennemi e = salle.getEnnemi();
            salles.add(new EtatSalle(salle.getId(), salle.estVisitee(), salle.estUtilisee(),
                    e == null ? null : new EtatEnnemi(e.getType(), e.getVie(), e.getTours())));
        }
        return new EtatSauvegarde(VERSION, heros, partie.getSalleActuelle().getId(), partie.getEtat(), salles);
    }

    /**
     * Reconstruit une nouvelle partie seulement si toutes les relations sont cohérentes.
     * @param donnees instantané non fiable provenant d'un fichier
     * @return nouvelle partie validée
     * @throws IllegalArgumentException si une donnée ou relation est invalide
     */
    public static Partie restaurer(EtatSauvegarde donnees) {
        verifier(donnees != null && donnees.version() == VERSION, "Version de sauvegarde incompatible.");
        verifier(donnees.heros() != null && donnees.etat() != null, "État incomplet.");
        Heros heros = new Heros(); Donjon donjon = new Donjon();
        verifier(donnees.salles().size() == donjon.getSalles().size(), "Liste de salles incomplète.");
        Set<String> ids = new HashSet<>();
        int potionsDisponibles = 3;
        Set<Arme> armesAttendues = EnumSet.of(Arme.ROUILLEE);
        for (EtatSalle sauvegardee : donnees.salles()) {
            verifier(sauvegardee != null && ids.add(sauvegardee.id()), "Salle absente ou dupliquée.");
            Salle salle = donjon.getSalle(sauvegardee.id());
            verifier(!sauvegardee.utilisee() || (sauvegardee.visitee() && (salle.getCoffre() != null || salle.estRepos())), "Utilisation de salle incohérente.");
            if (sauvegardee.visitee()) salle.visiter();
            if (sauvegardee.utilisee()) {
                salle.utiliser();
                if (salle.getCoffre() != null) {
                    armesAttendues.add(salle.getCoffre());
                    potionsDisponibles += salle.getCoffre() == Arme.ACIER ? 2 : 1;
                }
            }
            Ennemi ennemi = salle.getEnnemi(); EtatEnnemi e = sauvegardee.ennemi();
            verifier((ennemi == null) == (e == null), "Ennemi absent ou inattendu.");
            if (ennemi != null) {
                verifier(ennemi.getType().equals(e.type()), "Type d'ennemi inconnu pour cette salle.");
                ennemi.restaurerVie(e.vie()); ennemi.restaurerTours(e.tours());
                verifier(e.tours() != 0 || e.vie() == ennemi.getVieMax(), "Ennemi blessé sans tour de combat.");
                verifier(salle.estVisitee() || (e.vie() == ennemi.getVieMax() && e.tours() == 0), "Ennemi modifié sans visite.");
                verifier(!salle.estVisitee() || ennemi.estMort() || salle.getId().equals(donnees.salleActuelle()), "Combat abandonné dans une autre salle.");
                if (ennemi.estMort() && !(ennemi instanceof Boss)) potionsDisponibles++;
            }
        }
        EtatHeros h = donnees.heros();
        verifier(h.vieMax() == heros.getVieMax() && h.attaque() == heros.getAttaqueBase() && h.armure() == heros.getArmure(), "Statistiques incompatibles avec les règles.");
        Set<Arme> armes = new HashSet<>(h.armes());
        verifier(armes.size() == h.armes().size() && armes.equals(armesAttendues) && armes.contains(h.equipee()), "Armes et coffres incohérents.");
        verifier(h.potions() >= 0 && h.potions() <= potionsDisponibles, "Quantité de potions incohérente.");
        heros.restaurerVie(h.vie()); heros.getInventaire().restaurer(armes, h.equipee(), h.potions());
        Salle actuelle = donjon.getSalle(donnees.salleActuelle());
        verifier(actuelle.estVisitee() && donjon.getSalle("entree").estVisitee(), "Salle actuelle ou entrée non visitée.");
        verifierParcours(donjon);
        boolean bossMort = donjon.getSalle("boss").getEnnemi().estMort();
        EtatPartie attendu = heros.estMort() ? EtatPartie.DEFAITE : bossMort ? EtatPartie.VICTOIRE : EtatPartie.EN_COURS;
        verifier(donnees.etat() == attendu, "Issue incompatible avec les points de vie.");
        verifier(!bossMort || (!heros.estMort() && actuelle.getId().equals("boss")), "Victoire située hors du combat final.");
        verifier(!heros.estMort() || actuelle.estHostile(), "Défaite hors combat.");
        return new Partie(heros, donjon, actuelle.getId(), donnees.etat());
    }

    /** Vérifie que les visites sont accessibles depuis l'entrée sans traverser un ennemi vivant. */
    private static void verifierParcours(Donjon donjon) {
        Set<String> atteintes = new HashSet<>();
        var attente = new ArrayDeque<String>(); attente.add("entree");
        while (!attente.isEmpty()) {
            Salle salle = donjon.getSalle(attente.remove());
            if (!salle.estVisitee() || !atteintes.add(salle.getId()) || salle.estHostile()) continue;
            attente.addAll(salle.getPassages());
        }
        for (Salle salle : donjon.getSalles()) {
            verifier(!salle.estVisitee() || atteintes.contains(salle.getId()), "Parcours de visites impossible.");
        }
    }
    /** Transforme une violation en erreur explicite de validation. */
    private static void verifier(boolean condition, String message) {
        if (!condition) throw new IllegalArgumentException(message);
    }
}
