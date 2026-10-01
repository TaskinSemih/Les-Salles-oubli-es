package fr.utbm.sallesoubliees.modele;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Carte fixe : les passages explicites sont la seule source de déplacements autorisés. */
public final class Donjon {
    private final Map<String, Salle> salles = new LinkedHashMap<>();
    /** Construit neuf salles et leurs passages bidirectionnels. */
    public Donjon() {
        ajouter(new Salle("entree", "Entrée", 0, 0, null, null, false));
        ajouter(new Salle("ossuaire", "Ossuaire", 1, 0, new Squelette(), null, false));
        ajouter(new Salle("arsenal", "Arsenal", 2, 0, null, Arme.ACIER, false));
        ajouter(new Salle("forge", "Forge", 1, 1, new Golem(), null, false));
        ajouter(new Salle("repos", "Sanctuaire", 2, 1, null, null, true));
        ajouter(new Salle("bibliotheque", "Bibliothèque", 3, 1, new Mage(), null, false));
        ajouter(new Salle("tresor", "Trésor", 2, 2, null, Arme.RUNIQUE, false));
        ajouter(new Salle("antichambre", "Antichambre", 3, 2, new Golem(), null, false));
        ajouter(new Salle("boss", "Gardien", 4, 2, new Boss(), null, false));
        relier("entree", "ossuaire"); relier("ossuaire", "arsenal"); relier("ossuaire", "forge");
        relier("arsenal", "repos"); relier("forge", "repos"); relier("repos", "bibliotheque");
        relier("bibliotheque", "antichambre"); relier("antichambre", "tresor"); relier("antichambre", "boss");
    }
    /** Ajoute une salle au catalogue ordonné. */
    private void ajouter(Salle salle) { salles.put(salle.getId(), salle); }
    /** Construit un passage réciproque. */
    private void relier(String a, String b) { getSalle(a).relier(b); getSalle(b).relier(a); }
    /**
     * Recherche une salle connue.
     * @param id identifiant stable
     * @return salle correspondante
     * @throws IllegalArgumentException si l'identifiant est inconnu
     */
    public Salle getSalle(String id) {
        Salle salle = salles.get(id);
        if (salle == null) throw new IllegalArgumentException("Salle inconnue : " + id);
        return salle;
    }
    /** {@return collection non modifiable des neuf salles} */
    public Collection<Salle> getSalles() { return Collections.unmodifiableCollection(salles.values()); }
}
