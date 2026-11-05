// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

// Rend disponible List depuis java.util.
import java.util.List;

/**
 * Instantané explicite et immuable, indépendant du format de fichier.
 *
 * @param version version des règles et du format
 *
 * @param heros statistiques et possessions
 *
 * @param salleActuelle identifiant de la salle occupée
 *
 * @param etat issue globale
 *
 * @param salles état des neuf salles
 *
 * @param tactique état persistant du hasard et des combats
 */
// Déclare EtatSauvegarde, instantané indépendant du JSON regroupant héros, salles, issue et tactique. Le record fournit les accesseurs, equals et hashCode à partir de ses données.
public record EtatSauvegarde(int version, EtatHeros heros, String salleActuelle,
                            // Complète l’instantané avec l’issue globale, la liste des salles et les données tactiques.
                            EtatPartie etat, List<EtatSalle> salles, EtatTactique tactique) {
    /** Copie défensivement la collection reçue. */
    // Constructeur compact du record : vérifie ou copie défensivement les données reçues. Met à jour les salles avec une copie non modifiable des salles.
    public EtatSauvegarde { salles = List.copyOf(salles); }

    /**
     * Données du héros.
     *
 * @param vie points de vie actuels
     *
 * @param vieMax maximum prévu par les règles
     *
 * @param attaque attaque de base
     *
 * @param armure armure permanente
     *
 * @param armes identifiants des armes possédées
     *
 * @param equipee arme équipée
     *
 * @param potions quantité restante
     */
    // Déclare EtatHeros, données sauvegardées du héros, de ses armes et de ses potions. Le record fournit les accesseurs, equals et hashCode à partir de ses données.
    public record EtatHeros(int vie, int vieMax, int attaque, int armure,
                           // Complète les données du héros avec ses armes, celle équipée et son nombre de potions.
                           List<Arme> armes, Arme equipee, int potions) {
        /** Copie défensivement les armes reçues. */
        // Copie la liste des armes pour que l’auteur de la liste reçue ne puisse plus modifier cet instantané.
        public EtatHeros { armes = List.copyOf(armes); }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    /**
     * Données d'une salle ; la carte et les récompenses sont définies par le catalogue.
     *
 * @param id identifiant de la salle
     *
 * @param visitee entrée déjà effectuée
     *
 * @param utilisee coffre ouvert ou repos utilisé
     *
 * @param ennemi état de l'ennemi, null si aucun
     */
    // Déclare EtatSalle, données sauvegardées d'une salle et de son éventuel ennemi. Le record fournit les accesseurs, equals et hashCode à partir de ses données.
    public record EtatSalle(String id, boolean visitee, boolean utilisee, EtatEnnemi ennemi) { }

    /**
     * Données nécessaires à la reprise exacte d'un combat entre deux actions.
     *
 * @param type identifiant du type, jamais un nom de classe
     *
 * @param vie vie actuelle, zéro si vaincu
     *
 * @param tours nombre de réponses déjà jouées (préparation comprise)
     */
    // Déclare EtatEnnemi, données sauvegardées d'un ennemi : type, vie et compteur de tours. Le record fournit les accesseurs, equals et hashCode à partir de ses données.
    public record EtatEnnemi(String type, int vie, int tours) { }
// Ferme le bloc ou la liste de données commencé plus haut.
}
