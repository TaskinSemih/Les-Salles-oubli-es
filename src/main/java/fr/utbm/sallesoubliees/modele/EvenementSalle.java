// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;
// Rend disponible List depuis java.util.
import java.util.List;
/** Choix proposé par le modèle.
 *
 * @param salle identifiant
 * @param titre titre
 * @param description conséquences annoncées
 * @param choix options */
// Déclare EvenementSalle, description immuable d'une découverte et de ses choix proposés. Le record fournit les accesseurs, equals et hashCode à partir de ses données.
public record EvenementSalle(String salle, String titre, String description, List<ChoixEvenement> choix) {
    /** Copie les options. */
    // Constructeur compact du record : vérifie ou copie défensivement les données reçues. Met à jour le choix courant avec une copie non modifiable du choix courant.
    public EvenementSalle { choix = List.copyOf(choix); }
// Ferme le bloc ou la liste de données commencé plus haut.
}
