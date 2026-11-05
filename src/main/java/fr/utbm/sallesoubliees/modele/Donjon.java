// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

// Rend disponible Collection depuis java.util.
import java.util.Collection;
// Rend disponible Collections depuis java.util.
import java.util.Collections;
// Rend disponible LinkedHashMap depuis java.util.
import java.util.LinkedHashMap;
// Rend disponible Map depuis java.util.
import java.util.Map;

/** Carte fixe : les passages explicites sont la seule source de déplacements autorisés. */
// Déclare Donjon, graphe fixe des neuf salles et de leurs passages réciproques. final interdit de créer une sous-classe.
public final class Donjon {
    // Initialise les salles avec une nouvelle collection indépendante et vide.
    private final Map<String, Salle> salles = new LinkedHashMap<>();
    /** Construit neuf salles et leurs passages bidirectionnels. */
    // Construit un objet Donjon : graphe fixe des neuf salles et de leurs passages réciproques.
    public Donjon() {
        // Ajoute cette salle au catalogue du donjon en utilisant son identifiant comme clé ; arguments : new Salle("entree", "Entrée", 0, 0, null, null, false).
        ajouter(new Salle("entree", "Entrée", 0, 0, null, null, false));
        // Ajoute cette salle au catalogue du donjon en utilisant son identifiant comme clé ; arguments : new Salle("ossuaire", "Ossuaire", 1, 0, new Squelette(), null, false).
        ajouter(new Salle("ossuaire", "Ossuaire", 1, 0, new Squelette(), null, false));
        // Ajoute cette salle au catalogue du donjon en utilisant son identifiant comme clé ; arguments : new Salle("arsenal", "Arsenal", 2, 0, null, Arme.ACIER, false).
        ajouter(new Salle("arsenal", "Arsenal", 2, 0, null, Arme.ACIER, false));
        // Ajoute cette salle au catalogue du donjon en utilisant son identifiant comme clé ; arguments : new Salle("forge", "Forge", 1, 1, new Golem(), null, false).
        ajouter(new Salle("forge", "Forge", 1, 1, new Golem(), null, false));
        // Ajoute cette salle au catalogue du donjon en utilisant son identifiant comme clé ; arguments : new Salle("repos", "Sanctuaire", 2, 1, null, null, true).
        ajouter(new Salle("repos", "Sanctuaire", 2, 1, null, null, true));
        // Ajoute cette salle au catalogue du donjon en utilisant son identifiant comme clé ; arguments : new Salle("bibliotheque", "Bibliothèque", 3, 1, new Mage(), null, false).
        ajouter(new Salle("bibliotheque", "Bibliothèque", 3, 1, new Mage(), null, false));
        // Ajoute cette salle au catalogue du donjon en utilisant son identifiant comme clé ; arguments : new Salle("tresor", "Trésor", 2, 2, null, Arme.RUNIQUE, false).
        ajouter(new Salle("tresor", "Trésor", 2, 2, null, Arme.RUNIQUE, false));
        // Ajoute cette salle au catalogue du donjon en utilisant son identifiant comme clé ; arguments : new Salle("antichambre", "Antichambre", 3, 2, new Golem(), null, false).
        ajouter(new Salle("antichambre", "Antichambre", 3, 2, new Golem(), null, false));
        // Ajoute cette salle au catalogue du donjon en utilisant son identifiant comme clé ; arguments : new Salle("boss", "Gardien", 4, 2, new Boss(), null, false).
        ajouter(new Salle("boss", "Gardien", 4, 2, new Boss(), null, false));
        // Crée le passage entre les salles demandées ; le constructeur du donjon relie les deux sens ; arguments : "entree", "ossuaire". Crée le passage entre les salles demandées ; le constructeur du donjon relie les deux sens ; arguments : "ossuaire", "arsenal". Crée le passage entre les salles demandées ; le constructeur du donjon relie les deux sens ; arguments : "ossuaire", "forge".
        relier("entree", "ossuaire"); relier("ossuaire", "arsenal"); relier("ossuaire", "forge");
        // Crée le passage entre les salles demandées ; le constructeur du donjon relie les deux sens ; arguments : "arsenal", "repos". Crée le passage entre les salles demandées ; le constructeur du donjon relie les deux sens ; arguments : "forge", "repos". Crée le passage entre les salles demandées ; le constructeur du donjon relie les deux sens ; arguments : "repos", "bibliotheque".
        relier("arsenal", "repos"); relier("forge", "repos"); relier("repos", "bibliotheque");
        // Crée le passage entre les salles demandées ; le constructeur du donjon relie les deux sens ; arguments : "bibliotheque", "antichambre". Crée le passage entre les salles demandées ; le constructeur du donjon relie les deux sens ; arguments : "antichambre", "tresor". Crée le passage entre les salles demandées ; le constructeur du donjon relie les deux sens ; arguments : "antichambre", "boss".
        relier("bibliotheque", "antichambre"); relier("antichambre", "tresor"); relier("antichambre", "boss");
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Ajoute une salle au catalogue ordonné. */
    // Ajoute une salle au catalogue ordonné. Associe une clé à sa valeur dans la table ; arguments : salle.getId(), salle.
    private void ajouter(Salle salle) { salles.put(salle.getId(), salle); }
    /** Construit un passage réciproque. */
    // Ajoute le lien de passage demandé ; Donjon construit les liens dans les deux sens. Crée le passage entre les salles demandées ; le constructeur du donjon relie les deux sens ; arguments : b. Crée le passage entre les salles demandées ; le constructeur du donjon relie les deux sens ; arguments : a.
    private void relier(String a, String b) { getSalle(a).relier(b); getSalle(b).relier(a); }
    /**
     * Recherche une salle connue.
     * @param id identifiant stable
     * @return salle correspondante
     * @throws IllegalArgumentException si l'identifiant est inconnu
     */
    // Recherche une salle connue.
    public Salle getSalle(String id) {
        // Initialise la salle courante avec salles.get(id).
        Salle salle = salles.get(id);
        // Si la salle courante est égal à l’absence de valeur (null), refuse l’opération : "Salle inconnue : ".
        if (salle == null) throw new IllegalArgumentException("Salle inconnue : " + id);
        // Renvoie la salle courante.
        return salle;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** {@return collection non modifiable des neuf salles} */
    // { Renvoie une vue en lecture seule de la collection interne.
    public Collection<Salle> getSalles() { return Collections.unmodifiableCollection(salles.values()); }
// Ferme le bloc ou la liste de données commencé plus haut.
}
