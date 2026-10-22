package fr.utbm.sallesoubliees.modele;

import java.util.*;
import static fr.utbm.sallesoubliees.modele.EvenementJeu.Cible.*;
import static fr.utbm.sallesoubliees.modele.EvenementJeu.Type.*;
import static fr.utbm.sallesoubliees.modele.EffetCombat.Type.*;

/** État et résolution tactiques : choix annoncés, effets, hasard et réactions atomiques. */
public final class CombatTactique {
    private final SourceAleatoire hasard;
    private int elan;
    private DecisionEnnemi decision;
    private ActionEnnemi precedente;
    private boolean soinUtilise;
    private final List<ActionCombat> historique = new ArrayList<>();
    private final List<EffetCombat> effets = new ArrayList<>();
    private final Map<String, ChoixEvenement> choix = new HashMap<>();

    /** Définit les données de cette opération.
     * @param hasard source dont l'état permet une reprise fidèle */
    public CombatTactique(SourceAleatoire hasard) { this.hasard = Objects.requireNonNull(hasard); }
    /** {@return copie immuable du combat et des choix de salles} */
    public EtatTactique capturer() { return new EtatTactique(hasard.etat(), elan, decision, historique, precedente, soinUtilise, effets, choix); }
    /** Définit les données de cette opération.
     * @param e données déjà validées
 * @return combat reconstruit avec le même futur aléatoire */
    static CombatTactique restaurer(EtatTactique e) {
        CombatTactique c = new CombatTactique(new HasardReproductible(e.hasard()));
        c.elan = e.elan(); c.decision = e.decision(); c.precedente = e.precedente(); c.soinUtilise = e.soinUtilise();
        c.historique.addAll(e.historique()); c.effets.addAll(e.effets()); c.choix.putAll(e.choix()); return c;
    }
    /** Définit les données de cette opération.
     * @param ennemi adversaire rencontré */
    void commencer(Ennemi ennemi) {
        terminer(); selectionner(ennemi);
    }
    /** Nettoie les ressources et effets de combat, conserve les événements de salles. */
    void terminer() { elan = 0; decision = null; precedente = null; soinUtilise = false; historique.clear(); effets.clear(); }
    /** Choisit une seule intention par tour. */
    private void selectionner(Ennemi e) {
        decision = e.decider(new ContexteCombat(elan, historique, precedente, soinUtilise), hasard);
        effets.removeIf(v -> v.type() == GARDE && v.cible() == ENNEMI);
        if (decision.action() == ActionEnnemi.RIPOSTE) effets.add(new EffetCombat(GARDE, ENNEMI, 1));
    }
    /** {@return réserve actuelle} */
    public int getElan() { return elan; }
    /** {@return intention courante, éventuellement null hors combat} */
    public DecisionEnnemi getDecision() { return decision; }
    /** {@return effets consultables} */
    public List<EffetCombat> getEffets() { return List.copyOf(effets); }
    /** {@return intention avec révélation éventuelle} */
    public String intention() {
        return decision == null ? "" : decision.decrire(possede(LUCIDE, HEROS));
    }
    /** Teste une altération active. */
    private boolean possede(EffetCombat.Type t, EvenementJeu.Cible c) { return effets.stream().anyMatch(e -> e.type() == t && e.cible() == c); }
    /** Ajoute un effet et son fait, remplace seulement le même type/cible. */
    private void appliquer(EffetCombat.Type t, EvenementJeu.Cible c, int duree, List<EvenementJeu> f) {
        effets.removeIf(e -> e.type() == t && e.cible() == c); effets.add(new EffetCombat(t, c, duree));
        f.add(new EvenementJeu(EFFET, c, t.ordinal(), 0));
    }
    /** Consomme une altération. */
    private void retirer(EffetCombat.Type t, EvenementJeu.Cible c, List<EvenementJeu> f) {
        if (effets.removeIf(e -> e.type() == t && e.cible() == c)) f.add(new EvenementJeu(EXPIRATION, c, t.ordinal(), 0));
    }
    /** Avance les effets anciens avant d'ajouter ceux de la réponse. */
    private void vieillir(List<EvenementJeu> f) {
        var copie = List.copyOf(effets); effets.clear();
        for (EffetCombat e : copie) {
            if (e.duree() > 1) effets.add(new EffetCombat(e.type(), e.cible(), e.duree() - 1));
            else f.add(new EvenementJeu(EXPIRATION, e.cible(), e.type().ordinal(), 0));
        }
    }
    /** Change l'élan en publiant sa nouvelle valeur. */
    private void elan(int valeur, List<EvenementJeu> f) {
        int nouveau = Math.max(0, Math.min(3, valeur));
        if (nouveau != elan) { elan = nouveau; f.add(new EvenementJeu(ELAN, HEROS, elan, 0)); }
    }
    /** Applique une perte réelle de vie. */
    private void degats(Personnage p, int puissance, int armure, EvenementJeu.Cible c, List<EvenementJeu> f) {
        int avant = p.getVie(); p.subirDegats(GestionnaireCombat.calculerDegats(puissance, armure));
        f.add(new EvenementJeu(DEGATS, c, avant - p.getVie(), p.getVie()));
    }
    /** Résout héros et réponse sans réappliquer les règles à l'animation.
     *
 * @param h héros
 * @param e ennemi vivant
 * @param a commande validée
 * @param f faits ordonnés
     *
 * @return résumé court */
    String resoudre(Heros h, Ennemi e, ActionCombat a, List<EvenementJeu> f) {
        DecisionEnnemi d = decision;
        int phaseAvant = phase(e);
        boolean frappe = a == ActionCombat.ATTAQUER || a == ActionCombat.PUISSANTE;
        boolean interrompu = frappe && a == ActionCombat.ATTAQUER && d.action() == ActionEnnemi.PREPARATION;
        boolean riposte = frappe && d.action() == ActionEnnemi.RIPOSTE;
        if (frappe) {
            int puissance = h.getAttaque() - (possede(AFFAIBLI, HEROS) ? 4 : 0);
            int armure = e.getArmure() + (possede(ARMURE_RENFORCEE, ENNEMI) ? 6 : 0) + (riposte ? 6 : 0);
            if (a == ActionCombat.PUISSANTE) {
                elan(elan - 2, f); puissance += 10; armure = e.getArmure();
                if (riposte || possede(ARMURE_RENFORCEE, ENNEMI)) {
                    retirer(ARMURE_RENFORCEE, ENNEMI, f); f.add(new EvenementJeu(BRISE_GARDE, ENNEMI, 0, e.getVie()));
                }
            }
            if (possede(VULNERABLE, ENNEMI)) { puissance += 6; retirer(VULNERABLE, ENNEMI, f); }
            f.add(new EvenementJeu(ATTAQUE, HEROS, 0, h.getVie())); degats(e, puissance, armure, ENNEMI, f);
            if (a == ActionCombat.ATTAQUER) elan(elan + 1, f);
        } else if (a == ActionCombat.POTION) {
            int avant = h.getVie(); h.getInventaire().consommerPotion(); h.soigner(Potion.SOIN);
            f.add(new EvenementJeu(SOIN, HEROS, h.getVie() - avant, h.getVie()));
        } else if (a == ActionCombat.DEFENDRE) f.add(new EvenementJeu(DEFENSE, HEROS, 8, h.getVie()));
        else f.add(new EvenementJeu(OBSERVATION, HEROS, 0, h.getVie()));
        vieillir(f);
        if (a == ActionCombat.OBSERVER) appliquer(LUCIDE, HEROS, 2, f);
        historique.add(a); if (historique.size() > 3) historique.removeFirst();
        if (e.estMort()) return "L'adversaire est vaincu.";
        if (phase(e) != phaseAvant) {
            f.add(new EvenementJeu(PHASE, ENNEMI, phase(e), e.getVie()));
            f.add(new EvenementJeu(RAGE, ENNEMI, phase(e), e.getVie()));
        }
        e.terminerTour();
        ActionEnnemi executee = d.action();
        if (interrompu) {
            f.add(new EvenementJeu(INTERRUPTION, ENNEMI, 0, e.getVie()));
            appliquer(VULNERABLE, ENNEMI, 1, f); executee = ActionEnnemi.RECUPERATION;
        } else switch (d.action()) {
            case RIPOSTE -> {
                if (riposte) { f.add(new EvenementJeu(CONTRE_ATTAQUE, ENNEMI, 0, e.getVie())); frapper(h, e, d, a, f); }
                else appliquer(VULNERABLE, ENNEMI, 1, f);
            }
            case ATTAQUE, FRAPPE_LOURDE, ONDE -> {
                frapper(h, e, d, a, f);
                if (d.action() == ActionEnnemi.FRAPPE_LOURDE || d.action() == ActionEnnemi.ONDE) appliquer(VULNERABLE, ENNEMI, 1, f);
            }
            case PREPARATION -> f.add(new EvenementJeu(PREPARATION, ENNEMI, 0, e.getVie()));
            case ARMURE -> appliquer(ARMURE_RENFORCEE, ENNEMI, 2, f);
            case MALEDICTION -> appliquer(AFFAIBLI, HEROS, 2, f);
            case MARQUE -> appliquer(MARQUE, HEROS, 3, f);
            case RECUPERATION -> appliquer(VULNERABLE, ENNEMI, 1, f);
            case SOIN -> {
                int avant = e.getVie(); e.soigner(10); soinUtilise = true;
                f.add(new EvenementJeu(SOIN, ENNEMI, e.getVie() - avant, e.getVie()));
            }
        }
        precedente = executee;
        if (!h.estMort()) { selectionner(e); f.add(new EvenementJeu(INTENTION, ENNEMI, 0, e.getVie())); }
        return d.decrire(true) + (interrompu ? " Préparation interrompue !" : "") + (historique.size() == 3 && historique.stream().distinct().count() == 1 ? " Il reconnaît vos habitudes." : "");
    }
    /** Phase déduite des PV, sans état contradictoire à sauvegarder. */
    private int phase(Ennemi e) { return e.getType().equals("boss") ? e.getVie() <= 22 ? 3 : e.getVie() <= 45 ? 2 : 1 : 1; }
    /** Résout une seule frappe et les déclenchements associés. */
    private void frapper(Heros h, Ennemi e, DecisionEnnemi d, ActionCombat a, List<EvenementJeu> f) {
        int protection = a == ActionCombat.DEFENDRE ? d.action() == ActionEnnemi.ONDE ? 4 : 8 : 0;
        int bonus = possede(MARQUE, HEROS) ? 4 : 0;
        retirer(MARQUE, HEROS, f);
        f.add(new EvenementJeu(ATTAQUE, ENNEMI, 0, e.getVie()));
        degats(h, d.puissance() + bonus, (e.ignoreArmure() ? 0 : h.getArmure()) + protection, HEROS, f);
        if (protection > 0 && d.puissance() + bonus > (e.ignoreArmure() ? 0 : h.getArmure()) + 1) elan(elan + 1, f);
    }
    /** Définit les données de cette opération.
     * @param salle identifiant
 * @return événement non résolu ou null */
    public EvenementSalle evenement(String salle) {
        if (choix.containsKey(salle)) return null;
        String titre = switch (salle) { case "arsenal" -> "Le mécanisme du coffre"; case "bibliotheque" -> "La rune interdite"; case "tresor" -> "L'or sous les dalles"; default -> null; };
        return titre == null ? null : new EvenementSalle(salle, titre,
                salle.equals("bibliotheque")
                    ? "Rune stable : +15 PV. Rune interdite : +35 PV, 40 % de brûlure (-12 PV après le soin, jamais mortelle). Ignorer : aucun effet."
                    : "Prudent : +1 potion. Risquer : +2 potions, 40 % de piège (-12 PV, jamais mortel). Ignorer : aucun bonus.",
                List.of(ChoixEvenement.values()));
    }
    /** Résout une fois un choix dont les options ont été vérifiées. */
    String choisir(String salle, ChoixEvenement choixEffectue, Heros h) {
        if (choixEffectue == null || evenement(salle) == null) throw new IllegalStateException("Événement déjà résolu ou absent.");
        choix.put(salle, choixEffectue);
        if (choixEffectue == ChoixEvenement.IGNORER) return "Vous laissez le mystère derrière vous.";
        int potions = choixEffectue == ChoixEvenement.RISQUER ? 2 : 1;
        if (salle.equals("bibliotheque")) h.soigner(choixEffectue == ChoixEvenement.RISQUER ? 35 : 15);
        else h.getInventaire().ajouterPotions(potions);
        boolean piege = choixEffectue == ChoixEvenement.RISQUER && hasard.prochainEntier(100) < 40;
        if (piege) h.subirDegats(Math.min(12, h.getVie() - 1));
        return (salle.equals("bibliotheque") ? "La rune restaure vos PV." : "+" + potions + " potion(s).")
                + (piege ? " Un piège vous blesse (au plus 12 PV)." : " Vous repartez indemne.");
    }
}
