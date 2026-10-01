package fr.utbm.sallesoubliees.modele;

import java.util.List;

/**
 * Instantané explicite et immuable, indépendant du format de fichier.
 * @param version version des règles et du format
 * @param heros statistiques et possessions
 * @param salleActuelle identifiant de la salle occupée
 * @param etat issue globale
 * @param salles état des neuf salles
 */
public record EtatSauvegarde(int version, EtatHeros heros, String salleActuelle,
                            EtatPartie etat, List<EtatSalle> salles) {
    /** Copie défensivement la collection reçue. */
    public EtatSauvegarde { salles = List.copyOf(salles); }

    /**
     * Données du héros.
     * @param vie points de vie actuels
     * @param vieMax maximum prévu par les règles
     * @param attaque attaque de base
     * @param armure armure permanente
     * @param armes identifiants des armes possédées
     * @param equipee arme équipée
     * @param potions quantité restante
     */
    public record EtatHeros(int vie, int vieMax, int attaque, int armure,
                           List<Arme> armes, Arme equipee, int potions) {
        /** Copie défensivement les armes reçues. */
        public EtatHeros { armes = List.copyOf(armes); }
    }

    /**
     * Données d'une salle ; la carte et les récompenses sont définies par le catalogue.
     * @param id identifiant de la salle
     * @param visitee entrée déjà effectuée
     * @param utilisee coffre ouvert ou repos utilisé
     * @param ennemi état de l'ennemi, null si aucun
     */
    public record EtatSalle(String id, boolean visitee, boolean utilisee, EtatEnnemi ennemi) { }

    /**
     * Données nécessaires à la reprise exacte d'un combat entre deux actions.
     * @param type identifiant du type, jamais un nom de classe
     * @param vie vie actuelle, zéro si vaincu
     * @param tours nombre de réponses déjà jouées (préparation comprise)
     */
    public record EtatEnnemi(String type, int vie, int tours) { }
}
