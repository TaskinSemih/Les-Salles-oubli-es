package fr.utbm.sallesoubliees.modele;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/** Possessions du héros ; seules les règles du modèle peuvent les modifier. */
public final class Inventaire {
    private final Set<Arme> armes = EnumSet.of(Arme.ROUILLEE);
    private int potions = 3;
    private Arme equipee = Arme.ROUILLEE;

    /** Initialise l'équipement de départ et trois potions. */
    Inventaire() { }
    /** {@return vue non modifiable des armes possédées} */
    public Set<Arme> getArmes() { return Collections.unmodifiableSet(armes); }
    /** {@return nombre de potions restantes} */
    public int getPotions() { return potions; }
    /** {@return arme actuellement équipée} */
    public Arme getEquipee() { return equipee; }
    /** Ajoute une arme, sans doublon. */
    void ajouterArme(Arme arme) { armes.add(java.util.Objects.requireNonNull(arme)); }
    /** Ajoute une récompense de potions strictement positive. */
    void ajouterPotions(int nombre) {
        if (nombre <= 0) throw new IllegalArgumentException("Quantité invalide.");
        potions = Math.addExact(potions, nombre);
    }
    /** Consomme une potion existante. */
    void consommerPotion() {
        if (potions == 0) throw new IllegalStateException("Vous n'avez plus de potion.");
        potions--;
    }
    /** Remplace l'arme équipée, jamais le bonus de base. */
    void equiper(Arme arme) {
        if (arme == null || !armes.contains(arme)) throw new IllegalStateException("Arme absente de l'inventaire.");
        if (equipee == arme) throw new IllegalStateException("Cette arme est déjà équipée.");
        equipee = arme;
    }
    /** Reconstruit un inventaire dont les relations ont été validées. */
    void restaurer(Set<Arme> nouvellesArmes, Arme arme, int nombre) {
        if (nombre < 0 || !nouvellesArmes.contains(Arme.ROUILLEE) || !nouvellesArmes.contains(arme)) {
            throw new IllegalArgumentException("Inventaire incohérent.");
        }
        armes.clear(); armes.addAll(nouvellesArmes); equipee = arme; potions = nombre;
    }
}
