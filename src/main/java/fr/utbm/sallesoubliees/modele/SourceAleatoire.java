package fr.utbm.sallesoubliees.modele;
/** Source injectable dont l'état suffit à reprendre exactement les tirages. */
public interface SourceAleatoire {
    /** Définit les données de cette opération.
     * @param borne limite exclusive positive
 * @return entier entre zéro et borne exclue */
    int prochainEntier(int borne);
    /** {@return état sérialisable du générateur} */
    long etat();
}
