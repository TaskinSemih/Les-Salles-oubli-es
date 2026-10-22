package fr.utbm.sallesoubliees.modele;
import java.util.List;
/** Choix proposé par le modèle.
 *
 * @param salle identifiant
 * @param titre titre
 * @param description conséquences annoncées
 * @param choix options */
public record EvenementSalle(String salle, String titre, String description, List<ChoixEvenement> choix) {
    /** Copie les options. */
    public EvenementSalle { choix = List.copyOf(choix); }
}
