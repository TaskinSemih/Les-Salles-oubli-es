package fr.utbm.sallesoubliees.persistance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.utbm.sallesoubliees.controleur.ControleurJeu;
import fr.utbm.sallesoubliees.modele.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class SauvegardeTest {
    @TempDir Path dossier;
    private final GestionnaireSauvegarde gestionnaire = new GestionnaireSauvegarde();
    private final ObjectMapper json = new ObjectMapper();

    @Test void allerRetourNouvellePartieEtInstantaneIndependant() throws Exception {
        Partie p = new Partie(); EtatSauvegarde instantane = SauvegardePartie.capturer(p);
        p.deplacer("ossuaire");
        Path fichier = dossier.resolve("sous-dossier/partie.json");
        gestionnaire.sauvegarder(fichier, instantane);
        assertEquals(instantane, SauvegardePartie.capturer(gestionnaire.charger(fichier)));
        assertThrows(UnsupportedOperationException.class, () -> instantane.salles().clear());
        assertThrows(UnsupportedOperationException.class, () -> instantane.heros().armes().clear());
    }

    @Test void repriseGolemEntrePreparationEtFrappeEstExacte() throws Exception {
        Partie original = combatGolem(); original.attaquer();
        Path fichier = dossier.resolve("golem.json");
        gestionnaire.sauvegarder(fichier, SauvegardePartie.capturer(original));
        Partie reprise = gestionnaire.charger(fichier);
        assertEquals(22, reprise.getSalleActuelle().getEnnemi().puissanceProchaineAttaque());
        assertEquals(original.defendre(), reprise.defendre());
        assertEquals(original.attaquer(), reprise.attaquer());
        assertEquals(SauvegardePartie.capturer(original), SauvegardePartie.capturer(reprise));
    }

    @Test void repriseApresDefenseNeConservePasDeBonusTemporaire() throws Exception {
        Partie p = new Partie(); p.deplacer("ossuaire"); p.defendre();
        Path fichier = dossier.resolve("defense.json"); gestionnaire.sauvegarder(fichier, SauvegardePartie.capturer(p));
        Partie chargee = gestionnaire.charger(fichier); chargee.attaquer();
        assertEquals(93, chargee.getHeros().getVie());
    }

    @Test void victoireEtDefaiteSeRechargentSansReprendreLeJeu() throws Exception {
        Partie victoire = new Partie(); victoire.deplacer("ossuaire"); gagner(victoire);
        victoire.deplacer("arsenal"); victoire.ouvrirCoffre(); victoire.equiper(Arme.ACIER);
        victoire.deplacer("repos"); victoire.seReposer(); victoire.deplacer("bibliotheque"); gagner(victoire);
        victoire.deplacer("antichambre"); gagner(victoire); victoire.deplacer("tresor");
        victoire.ouvrirCoffre(); victoire.equiper(Arme.RUNIQUE); victoire.deplacer("antichambre"); victoire.deplacer("boss"); gagner(victoire);
        assertEquals(EtatPartie.VICTOIRE, victoire.getEtat());
        Partie defaite = new Partie(); defaite.deplacer("ossuaire");
        while (defaite.estEnCours()) defaite.defendre();
        for (Partie fin : new Partie[]{victoire, defaite}) {
            Path fichier = dossier.resolve(fin.getEtat() + ".json");
            gestionnaire.sauvegarder(fichier, SauvegardePartie.capturer(fin));
            Partie reprise = gestionnaire.charger(fichier);
            assertEquals(SauvegardePartie.capturer(fin), SauvegardePartie.capturer(reprise));
            assertThrows(IllegalStateException.class, reprise::attaquer);
        }
    }

    @Test void coffresEquipementReposEtEnnemisVaincusConserves() throws Exception {
        Partie p = combatGolem();
        while (p.estEnCombat()) p.attaquer();
        p.deplacer("repos"); p.seReposer(); p.deplacer("arsenal"); p.ouvrirCoffre(); p.equiper(Arme.ACIER);
        Path fichier = dossier.resolve("exploration.json");
        gestionnaire.sauvegarder(fichier, SauvegardePartie.capturer(p));
        assertEquals(SauvegardePartie.capturer(p), SauvegardePartie.capturer(gestionnaire.charger(fichier)));
    }

    @Test void remplacementReussiNeLaissePasDeFichierTemporaire() throws Exception {
        Path fichier = dossier.resolve("partie.json"); Partie p = new Partie();
        gestionnaire.sauvegarder(fichier, SauvegardePartie.capturer(p)); p.deplacer("ossuaire");
        gestionnaire.sauvegarder(fichier, SauvegardePartie.capturer(p));
        assertEquals("ossuaire", gestionnaire.charger(fichier).getSalleActuelle().getId());
        try (var fichiers = Files.list(dossier)) { assertEquals(1, fichiers.count()); }
    }

    @Test void echecEcritureConserveAncienFichierEtNettoieTemporaire() throws Exception {
        Path cible = dossier.resolve("repertoire.json"); Files.createDirectory(cible); Files.writeString(cible.resolve("important.txt"), "conserver");
        assertThrows(ExceptionSauvegarde.class, () -> gestionnaire.sauvegarder(cible, SauvegardePartie.capturer(new Partie())));
        assertEquals("conserver", Files.readString(cible.resolve("important.txt")));
        try (var fichiers = Files.list(dossier)) { assertEquals(1, fichiers.count()); }
    }

    @Test void fichierAbsentOuRepertoireRefuseEtPartieCouranteIntacte() {
        ControleurJeu c = new ControleurJeu(); c.nouvellePartie(); c.deplacer("ossuaire");
        Partie avant = c.getPartie(); EtatSauvegarde etat = c.capturer();
        assertThrows(ExceptionSauvegarde.class, () -> c.adopter(gestionnaire.charger(dossier.resolve("absent.json"))));
        assertThrows(ExceptionSauvegarde.class, () -> c.adopter(gestionnaire.charger(dossier)));
        assertSame(avant, c.getPartie()); assertEquals(etat, c.capturer());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "{", "null", "{}", "[]", "{\"version\":1,\"version\":1}", "1"})
    void jsonCorrompuRefuse(String texte) throws Exception {
        Path fichier = dossier.resolve("invalide.json"); Files.writeString(fichier, texte);
        assertThrows(ExceptionSauvegarde.class, () -> gestionnaire.charger(fichier));
    }

    @Test void donneesEtRelationsIncoherentesRefusees() throws Exception {
        refuser(n -> n.put("version", 99));
        refuser(n -> n.put("version", "1"));
        refuser(n -> n.put("version", 1.5));
        refuser(n -> n.put("inconnu", true));
        refuser(n -> n.remove("etat"));
        refuser(n -> n.putNull("heros"));
        refuser(n -> n.put("salleActuelle", "inconnue"));
        refuser(n -> n.put("salleActuelle", "boss"));
        refuser(n -> n.put("etat", "VICTOIRE"));
        refuser(n -> n.put("etat", "DEFAITE"));
        refuser(n -> n.put("etat", 0));
        refuser(n -> ((ObjectNode)n.get("heros")).put("vie", -1));
        refuser(n -> ((ObjectNode)n.get("heros")).put("vie", 101));
        refuser(n -> ((ObjectNode)n.get("heros")).putNull("vie"));
        refuser(n -> ((ObjectNode)n.get("heros")).put("attaque", 999));
        refuser(n -> ((ObjectNode)n.get("heros")).put("potions", 999));
        refuser(n -> ((ObjectNode)n.get("heros")).put("equipee", "RUNIQUE"));
        refuser(n -> ((ObjectNode)n.get("heros")).putArray("armes").add("inconnue"));
        refuser(n -> ((ObjectNode)n.get("heros")).putArray("armes").add("ROUILLEE").add("ROUILLEE"));
        refuser(n -> ((ObjectNode)n.get("salles").get(0)).put("visitee", false));
        refuser(n -> ((ObjectNode)n.get("salles").get(0)).put("utilisee", true));
        refuser(n -> ((ObjectNode)n.get("salles").get(1)).put("id", "entree"));
        refuser(n -> ((ObjectNode)n.get("salles").get(1)).putNull("ennemi"));
        refuser(n -> ((ObjectNode)n.get("salles").get(1).get("ennemi")).put("type", "dragon"));
        refuser(n -> ((ObjectNode)n.get("salles").get(1).get("ennemi")).put("tours", -1));
        refuser(n -> ((ObjectNode)n.get("salles").get(1).get("ennemi")).put("vie", 1));
        refuser(n -> ((ObjectNode)n.get("salles").get(2)).put("visitee", true)); // Arsenal au-delà du squelette vivant.
        refuser(n -> ((ObjectNode)n.get("salles").get(2)).put("utilisee", true));
    }

    @Test void golemBlesseSansTourEstRefuse() throws Exception {
        Partie p = combatGolem(); p.attaquer();
        ObjectNode n = json.valueToTree(SauvegardePartie.capturer(p));
        ((ObjectNode)n.get("salles").get(3).get("ennemi")).put("tours", 0);
        Path fichier = dossier.resolve("invalide.json"); json.writeValue(fichier.toFile(), n);
        assertThrows(ExceptionSauvegarde.class, () -> gestionnaire.charger(fichier));
    }

    @Test void donneesSurnumerairesEtFichierTropGrosRefuses() throws Exception {
        Path fichier = dossier.resolve("invalide.json");
        String valide = json.writeValueAsString(SauvegardePartie.capturer(new Partie()));
        Files.writeString(fichier, valide + " {}"); assertThrows(ExceptionSauvegarde.class, () -> gestionnaire.charger(fichier));
        Files.writeString(fichier, " ".repeat(128 * 1024 + 1)); assertThrows(ExceptionSauvegarde.class, () -> gestionnaire.charger(fichier));
    }

    private void refuser(Consumer<ObjectNode> mutation) throws Exception {
        ObjectNode n = json.valueToTree(SauvegardePartie.capturer(new Partie())); mutation.accept(n);
        Path fichier = dossier.resolve("invalide.json"); json.writeValue(fichier.toFile(), n);
        assertThrows(ExceptionSauvegarde.class, () -> gestionnaire.charger(fichier), n.toString());
    }

    private static Partie combatGolem() {
        Partie p = new Partie(); p.deplacer("ossuaire"); p.attaquer(); p.attaquer(); p.attaquer(); p.deplacer("forge"); return p;
    }

    private static void gagner(Partie p) {
        int actions = 0;
        while (p.estEnCombat()) {
            assertTrue(actions++ < 100);
            if (p.getHeros().getVie() <= 40 && p.peutBoire()) p.boirePotion(); else p.attaquer();
        }
        assertNotEquals(EtatPartie.DEFAITE, p.getEtat());
    }
}
