// Place cette classe dans le modèle : les règles restent indépendantes de Swing et de Jackson.
package fr.utbm.sallesoubliees.modele;

// Rend disponible Collections depuis java.util.
import java.util.Collections;
// Rend disponible EnumSet depuis java.util.
import java.util.EnumSet;
// Rend disponible Set depuis java.util.
import java.util.Set;

/** Possessions du héros ; seules les règles du modèle peuvent les modifier. */
// Déclare Inventaire, possessions encapsulées du héros : armes, arme équipée et stock de potions. final interdit de créer une sous-classe.
public final class Inventaire {
    // Crée l’ensemble des armes possédées avec l’épée rouillée ; EnumSet empêche les doublons et convient aux valeurs d’une énumération.
    private final Set<Arme> armes = EnumSet.of(Arme.ROUILLEE);
    // Initialise le stock de potions avec 3.
    private int potions = 3;
    // Initialise l'arme équipée avec Arme.ROUILLEE.
    private Arme equipee = Arme.ROUILLEE;

    /** Initialise l'équipement de départ et trois potions. */
    // Construit un objet Inventaire : possessions encapsulées du héros : armes, arme équipée et stock de potions.
    Inventaire() { }
    /** {@return vue non modifiable des armes possédées} */
    // Permet de consulter les armes possédées. Renvoie une vue en lecture seule de la collection interne.
    public Set<Arme> getArmes() { return Collections.unmodifiableSet(armes); }
    /** {@return nombre de potions restantes} */
    // Permet de consulter le stock de potions. Renvoie le stock de potions.
    public int getPotions() { return potions; }
    /** {@return arme actuellement équipée} */
    // Permet de consulter l'arme équipée. Renvoie l'arme équipée.
    public Arme getEquipee() { return equipee; }
    /** Ajoute une arme, sans doublon. */
    // Ajoute une arme non nulle sans créer de doublon. Ajoute l’élément à la collection ou au conteneur ; arguments : java.util.Objects.requireNonNull(arme).
    void ajouterArme(Arme arme) { armes.add(java.util.Objects.requireNonNull(arme)); }
    /** Ajoute une récompense de potions strictement positive. */
    // Ajoute une quantité strictement positive au stock, en détectant les dépassements d’entier.
    void ajouterPotions(int nombre) {
        // Si nombre est inférieur ou égal à 0, refuse l’opération : "Quantité invalide.".
        if (nombre <= 0) throw new IllegalArgumentException("Quantité invalide.");
        // Ajoute la récompense au stock ; addExact signale un dépassement de capacité d’entier au lieu de produire une quantité négative.
        potions = Math.addExact(potions, nombre);
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Consomme une potion existante. */
    // Retire une potion et refuse l’opération si le stock est vide.
    void consommerPotion() {
        // Si le stock de potions est égal à 0, refuse l’opération : "Vous n'avez plus de potion.".
        if (potions == 0) throw new IllegalStateException("Vous n'avez plus de potion.");
        // Décrémente le stock de potions de un.
        potions--;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Remplace l'arme équipée, jamais le bonus de base. */
    // Change l’arme équipée si les règles autorisent son utilisation.
    void equiper(Arme arme) {
        // Si (arme est égal à l’absence de valeur (null) ou non (armes.contains(arme))), refuse l’opération : "Arme absente de l'inventaire.".
        if (arme == null || !armes.contains(arme)) throw new IllegalStateException("Arme absente de l'inventaire.");
        // Si l'arme équipée est égal à arme, refuse l’opération : "Cette arme est déjà équipée.".
        if (equipee == arme) throw new IllegalStateException("Cette arme est déjà équipée.");
        // Met à jour l'arme équipée avec arme.
        equipee = arme;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
    /** Reconstruit un inventaire dont les relations ont été validées. */
    // Reconstruit l’état reçu en respectant les contrôles de chargement.
    void restaurer(Set<Arme> nouvellesArmes, Arme arme, int nombre) {
        // Si (nombre est inférieur à 0 ou non (nouvellesArmes.contains(Arme.ROUILLEE)) ou non (nouvellesArmes.contains(arme))), entre dans ce traitement conditionnel.
        if (nombre < 0 || !nouvellesArmes.contains(Arme.ROUILLEE) || !nouvellesArmes.contains(arme)) {
            // Signale l’erreur par une exception : "Inventaire incohérent.".
            throw new IllegalArgumentException("Inventaire incohérent.");
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Remplace les possessions par les armes validées, puis restaure l’arme équipée et le nombre de potions.
        armes.clear(); armes.addAll(nouvellesArmes); equipee = arme; potions = nombre;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
