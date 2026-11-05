// Place cette classe dans la persistance, qui lit et écrit les fichiers.
package fr.utbm.sallesoubliees.persistance;

// Rend disponible ObjectMapper depuis com.fasterxml.jackson.databind.
import com.fasterxml.jackson.databind.ObjectMapper;
// Rend disponible ObjectNode depuis com.fasterxml.jackson.databind.node.
import com.fasterxml.jackson.databind.node.ObjectNode;
// Rend disponible ControleurJeu depuis fr.utbm.sallesoubliees.controleur.
import fr.utbm.sallesoubliees.controleur.ControleurJeu;
// Rend disponibles les types ou constantes de fr.utbm.sallesoubliees.modele sans répéter leur nom complet.
import fr.utbm.sallesoubliees.modele.*;
// Rend disponible Files depuis java.nio.file.
import java.nio.file.Files;
// Rend disponible Path depuis java.nio.file.
import java.nio.file.Path;
// Rend disponible Consumer depuis java.util.function.
import java.util.function.Consumer;
// Importe l’annotation JUnit qui identifie une méthode de test.
import org.junit.jupiter.api.Test;
// Importe l’annotation qui confie à JUnit la création et le nettoyage d’un dossier temporaire.
import org.junit.jupiter.api.io.TempDir;
// Rend disponible ParameterizedTest depuis org.junit.jupiter.params.
import org.junit.jupiter.params.ParameterizedTest;
// Rend disponible ValueSource depuis org.junit.jupiter.params.provider.
import org.junit.jupiter.params.provider.ValueSource;
// Importe les assertions JUnit pour comparer les résultats et vérifier les erreurs attendues.
import static org.junit.jupiter.api.Assertions.*;

// Déclare SauvegardeTest, tests des sauvegardes valides, fichiers incorrects et invariants de restauration.
class SauvegardeTest {
    // JUnit fournit ce dossier temporaire et le nettoie après le test.
    @TempDir Path dossier;
    // Initialise « gestionnaire » avec un nouvel objet GestionnaireSauvegarde.
    private final GestionnaireSauvegarde gestionnaire = new GestionnaireSauvegarde();
    // Initialise le convertisseur JSON utilisé par le test avec new ObjectMapper().
    private final ObjectMapper json = new ObjectMapper();

    // Teste le scénario suivant : aller retour nouvelle partie et instantane independant.
    @Test void allerRetourNouvellePartieEtInstantaneIndependant() throws Exception {
        // Initialise la variable p de type Partie avec un nouvel objet Partie avec new HasardReproductible(3). Initialise « instantane » avec SauvegardePartie.capturer(p).
        Partie p = new Partie(new HasardReproductible(3)); EtatSauvegarde instantane = SauvegardePartie.capturer(p);
        // Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire".
        p.deplacer("ossuaire");
        // Initialise le chemin du fichier avec dossier.resolve("sous-dossier/partie.json").
        Path fichier = dossier.resolve("sous-dossier/partie.json");
        // Enregistre l’instantané dans le fichier demandé ; arguments : fichier, instantane.
        gestionnaire.sauvegarder(fichier, instantane);
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : instantane ; SauvegardePartie.capturer(gestionnaire.charger(fichier)).
        assertEquals(instantane, SauvegardePartie.capturer(gestionnaire.charger(fichier)));
        // Vérifie que l’opération lève l’exception attendue (UnsupportedOperationException) lors de () -> instantane.salles().clear().
        assertThrows(UnsupportedOperationException.class, () -> instantane.salles().clear());
        // Vérifie que l’opération lève l’exception attendue (UnsupportedOperationException) lors de () -> instantane.heros().armes().clear().
        assertThrows(UnsupportedOperationException.class, () -> instantane.heros().armes().clear());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : reprise golem entre preparation et frappe est exacte.
    @Test void repriseGolemEntrePreparationEtFrappeEstExacte() throws Exception {
        // Initialise « original » avec combatGolem(). Résout entièrement la commande avec ses faits et états avant/après ; arguments : ActionCombat.OBSERVER.
        Partie original = combatGolem(); original.resoudre(ActionCombat.OBSERVER);
        // Initialise le chemin du fichier avec dossier.resolve("golem.json").
        Path fichier = dossier.resolve("golem.json");
        // Enregistre l’instantané dans le fichier demandé ; arguments : fichier, SauvegardePartie.capturer(original).
        gestionnaire.sauvegarder(fichier, SauvegardePartie.capturer(original));
        // Initialise « reprise » avec gestionnaire.charger(fichier).
        Partie reprise = gestionnaire.charger(fichier);
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 22 ; reprise.getSalleActuelle().getEnnemi().puissanceProchaineAttaque().
        assertEquals(22, reprise.getSalleActuelle().getEnnemi().puissanceProchaineAttaque());
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : original.defendre() ; reprise.defendre().
        assertEquals(original.defendre(), reprise.defendre());
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : original.attaquer() ; reprise.attaquer().
        assertEquals(original.attaquer(), reprise.attaquer());
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : SauvegardePartie.capturer(original) ; SauvegardePartie.capturer(reprise).
        assertEquals(SauvegardePartie.capturer(original), SauvegardePartie.capturer(reprise));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : reprise apres defense ne conserve pas de bonus temporaire.
    @Test void repriseApresDefenseNeConservePasDeBonusTemporaire() throws Exception {
        // Initialise la variable p de type Partie avec un nouvel objet Partie avec new HasardReproductible(3). Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire". Joue une défense pour la réponse qui suit.
        Partie p = new Partie(new HasardReproductible(3)); p.deplacer("ossuaire"); p.defendre();
        // Initialise le chemin du fichier avec dossier.resolve("defense.json"). Enregistre l’instantané dans le fichier demandé ; arguments : fichier, SauvegardePartie.capturer(p).
        Path fichier = dossier.resolve("defense.json"); gestionnaire.sauvegarder(fichier, SauvegardePartie.capturer(p));
        // Initialise « chargee » avec gestionnaire.charger(fichier). Joue une attaque du héros et l’éventuelle réponse ennemie.
        Partie chargee = gestionnaire.charger(fichier); chargee.attaquer();
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : 93 ; chargee.getHeros().getVie().
        assertEquals(93, chargee.getHeros().getVie());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : victoire et defaite se rechargent sans reprendre le jeu.
    @Test void victoireEtDefaiteSeRechargentSansReprendreLeJeu() throws Exception {
        // Initialise « victoire » avec un nouvel objet Partie avec new HasardReproductible(3). Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire". Joue le combat de test jusqu’à vaincre l’ennemi ; arguments : victoire.
        Partie victoire = new Partie(new HasardReproductible(3)); victoire.deplacer("ossuaire"); gagner(victoire);
        // Emprunte le passage demandé selon les règles du modèle ; arguments : "arsenal". Ouvre une fois le coffre et attribue ses récompenses. Équipe l’arme demandée en passant par les contrôles du modèle ; arguments : Arme.ACIER.
        victoire.deplacer("arsenal"); victoire.ouvrirCoffre(); victoire.equiper(Arme.ACIER);
        // Emprunte le passage demandé selon les règles du modèle ; arguments : "repos". Utilise le sanctuaire pour restaurer la vie si le repos est disponible. Emprunte le passage demandé selon les règles du modèle ; arguments : "bibliotheque". Joue le combat de test jusqu’à vaincre l’ennemi ; arguments : victoire.
        victoire.deplacer("repos"); victoire.seReposer(); victoire.deplacer("bibliotheque"); gagner(victoire);
        // Emprunte le passage demandé selon les règles du modèle ; arguments : "antichambre". Joue le combat de test jusqu’à vaincre l’ennemi ; arguments : victoire. Emprunte le passage demandé selon les règles du modèle ; arguments : "tresor".
        victoire.deplacer("antichambre"); gagner(victoire); victoire.deplacer("tresor");
        // Ouvre une fois le coffre et attribue ses récompenses. Équipe l’arme demandée en passant par les contrôles du modèle ; arguments : Arme.RUNIQUE. Emprunte le passage demandé selon les règles du modèle ; arguments : "antichambre". Emprunte le passage demandé selon les règles du modèle ; arguments : "boss". Joue le combat de test jusqu’à vaincre l’ennemi ; arguments : victoire.
        victoire.ouvrirCoffre(); victoire.equiper(Arme.RUNIQUE); victoire.deplacer("antichambre"); victoire.deplacer("boss"); gagner(victoire);
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : EtatPartie.VICTOIRE ; l'issue de la partie de « victoire ».
        assertEquals(EtatPartie.VICTOIRE, victoire.getEtat());
        // Initialise « defaite » avec un nouvel objet Partie avec new HasardReproductible(3). Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire".
        Partie defaite = new Partie(new HasardReproductible(3)); defaite.deplacer("ossuaire");
        // Répète ce bloc tant que defaite.estEnCours()) defaite.defendre(.
        while (defaite.estEnCours()) defaite.defendre();
        // Parcourt new Partie[]{victoire, defaite} ; chaque élément est placé dans Partie fin.
        for (Partie fin : new Partie[]{victoire, defaite}) {
            // Initialise le chemin du fichier avec dossier.resolve(fin.getEtat() + ".json").
            Path fichier = dossier.resolve(fin.getEtat() + ".json");
            // Enregistre l’instantané dans le fichier demandé ; arguments : fichier, SauvegardePartie.capturer(fin).
            gestionnaire.sauvegarder(fichier, SauvegardePartie.capturer(fin));
            // Initialise « reprise » avec gestionnaire.charger(fichier).
            Partie reprise = gestionnaire.charger(fichier);
            // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : SauvegardePartie.capturer(fin) ; SauvegardePartie.capturer(reprise).
            assertEquals(SauvegardePartie.capturer(fin), SauvegardePartie.capturer(reprise));
            // Vérifie que l’opération lève l’exception attendue (IllegalStateException) lors de reprise::attaquer.
            assertThrows(IllegalStateException.class, reprise::attaquer);
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : coffres equipement repos et ennemis vaincus conserves.
    @Test void coffresEquipementReposEtEnnemisVaincusConserves() throws Exception {
        // Initialise la variable p de type Partie avec combatGolem().
        Partie p = combatGolem();
        // Répète ce bloc tant que p.estEnCombat()) p.attaquer(.
        while (p.estEnCombat()) p.attaquer();
        // Emprunte le passage demandé selon les règles du modèle ; arguments : "repos". Utilise le sanctuaire pour restaurer la vie si le repos est disponible. Emprunte le passage demandé selon les règles du modèle ; arguments : "arsenal". Ouvre une fois le coffre et attribue ses récompenses. Équipe l’arme demandée en passant par les contrôles du modèle ; arguments : Arme.ACIER.
        p.deplacer("repos"); p.seReposer(); p.deplacer("arsenal"); p.ouvrirCoffre(); p.equiper(Arme.ACIER);
        // Initialise le chemin du fichier avec dossier.resolve("exploration.json").
        Path fichier = dossier.resolve("exploration.json");
        // Enregistre l’instantané dans le fichier demandé ; arguments : fichier, SauvegardePartie.capturer(p).
        gestionnaire.sauvegarder(fichier, SauvegardePartie.capturer(p));
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : SauvegardePartie.capturer(p) ; SauvegardePartie.capturer(gestionnaire.charger(fichier)).
        assertEquals(SauvegardePartie.capturer(p), SauvegardePartie.capturer(gestionnaire.charger(fichier)));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : remplacement reussi ne laisse pas de fichier temporaire.
    @Test void remplacementReussiNeLaissePasDeFichierTemporaire() throws Exception {
        // Initialise le chemin du fichier avec dossier.resolve("partie.json"). Initialise la variable p de type Partie avec un nouvel objet Partie avec new HasardReproductible(3).
        Path fichier = dossier.resolve("partie.json"); Partie p = new Partie(new HasardReproductible(3));
        // Enregistre l’instantané dans le fichier demandé ; arguments : fichier, SauvegardePartie.capturer(p). Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire".
        gestionnaire.sauvegarder(fichier, SauvegardePartie.capturer(p)); p.deplacer("ossuaire");
        // Enregistre l’instantané dans le fichier demandé ; arguments : fichier, SauvegardePartie.capturer(p).
        gestionnaire.sauvegarder(fichier, SauvegardePartie.capturer(p));
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : le texte "ossuaire" ; gestionnaire.charger(fichier).getSalleActuelle().getId().
        assertEquals("ossuaire", gestionnaire.charger(fichier).getSalleActuelle().getId());
        // Ouvre une ressource dans un try-with-resources : Java la fermera automatiquement à la sortie du bloc.
        try (var fichiers = Files.list(dossier)) { assertEquals(1, fichiers.count()); }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : echec ecriture conserve ancien fichier et nettoie temporaire.
    @Test void echecEcritureConserveAncienFichierEtNettoieTemporaire() throws Exception {
        // Initialise la cible avec dossier.resolve("repertoire.json"). Crée le dossier utilisé comme cas de test ; arguments : cible. Écrit le texte dans le fichier du scénario ; arguments : cible.resolve("important.txt"), "conserver".
        Path cible = dossier.resolve("repertoire.json"); Files.createDirectory(cible); Files.writeString(cible.resolve("important.txt"), "conserver");
        // Vérifie que l’opération lève l’exception attendue (ExceptionSauvegarde) lors de () -> gestionnaire.sauvegarder(cible, SauvegardePartie.capturer(new Partie(new HasardReproductible(3)))).
        assertThrows(ExceptionSauvegarde.class, () -> gestionnaire.sauvegarder(cible, SauvegardePartie.capturer(new Partie(new HasardReproductible(3)))));
        // Vérifie l’égalité entre la valeur attendue et la valeur obtenue : le texte "conserver" ; Files.readString(cible.resolve("important.txt")).
        assertEquals("conserver", Files.readString(cible.resolve("important.txt")));
        // Ouvre une ressource dans un try-with-resources : Java la fermera automatiquement à la sortie du bloc.
        try (var fichiers = Files.list(dossier)) { assertEquals(1, fichiers.count()); }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : fichier absent ou repertoire refuse et partie courante intacte.
    @Test void fichierAbsentOuRepertoireRefuseEtPartieCouranteIntacte() {
        // Initialise la variable c de type ControleurJeu avec un nouvel objet ControleurJeu. Crée une partie indépendante et initialise son affichage. Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire".
        ControleurJeu c = new ControleurJeu(); c.nouvellePartie(); c.deplacer("ossuaire");
        // Initialise l'instantané avant l'action avec la partie conservée par le contrôleur de la variable c. Initialise l'état courant avec c.capturer().
        Partie avant = c.getPartie(); EtatSauvegarde etat = c.capturer();
        // Vérifie que l’opération lève l’exception attendue (ExceptionSauvegarde) lors de () -> c.adopter(gestionnaire.charger(dossier.resolve("absent.json"))).
        assertThrows(ExceptionSauvegarde.class, () -> c.adopter(gestionnaire.charger(dossier.resolve("absent.json"))));
        // Vérifie que l’opération lève l’exception attendue (ExceptionSauvegarde) lors de () -> c.adopter(gestionnaire.charger(dossier)).
        assertThrows(ExceptionSauvegarde.class, () -> c.adopter(gestionnaire.charger(dossier)));
        // Vérifie que les deux références désignent le même objet : l'instantané avant l'action ; la partie conservée par le contrôleur de la variable c. Vérifie l’égalité entre la valeur attendue et la valeur obtenue : l'état courant ; c.capturer().
        assertSame(avant, c.getPartie()); assertEquals(etat, c.capturer());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Demande à JUnit de répéter le test avec chaque donnée fournie.
    @ParameterizedTest
    // Fournit plusieurs textes JSON incorrects au test paramétré.
    @ValueSource(strings = {"", "{", "null", "{}", "[]", "{\"version\":1,\"version\":1}", "1"})
    // Teste chaque texte JSON incorrect fourni par ValueSource et attend une erreur de chargement.
    void jsonCorrompuRefuse(String texte) throws Exception {
        // Initialise le chemin du fichier avec dossier.resolve("invalide.json"). Écrit le texte dans le fichier du scénario ; arguments : fichier, texte.
        Path fichier = dossier.resolve("invalide.json"); Files.writeString(fichier, texte);
        // Vérifie que l’opération lève l’exception attendue (ExceptionSauvegarde) lors de () -> gestionnaire.charger(fichier).
        assertThrows(ExceptionSauvegarde.class, () -> gestionnaire.charger(fichier));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : donnees et relations incoherentes refusees.
    @Test void donneesEtRelationsIncoherentesRefusees() throws Exception {
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> n.put("version", 99).
        refuser(n -> n.put("version", 99));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> n.put("version", "1").
        refuser(n -> n.put("version", "1"));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> n.put("version", 1.5).
        refuser(n -> n.put("version", 1.5));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> n.put("inconnu", true).
        refuser(n -> n.put("inconnu", true));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> n.remove("etat").
        refuser(n -> n.remove("etat"));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> n.putNull("heros").
        refuser(n -> n.putNull("heros"));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> n.put("salleActuelle", "inconnue").
        refuser(n -> n.put("salleActuelle", "inconnue"));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> n.put("salleActuelle", "boss").
        refuser(n -> n.put("salleActuelle", "boss"));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> n.put("etat", "VICTOIRE").
        refuser(n -> n.put("etat", "VICTOIRE"));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> n.put("etat", "DEFAITE").
        refuser(n -> n.put("etat", "DEFAITE"));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> n.put("etat", 0).
        refuser(n -> n.put("etat", 0));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> ((ObjectNode)n.get("heros")).put("vie", -1).
        refuser(n -> ((ObjectNode)n.get("heros")).put("vie", -1));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> ((ObjectNode)n.get("heros")).put("vie", 101).
        refuser(n -> ((ObjectNode)n.get("heros")).put("vie", 101));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> ((ObjectNode)n.get("heros")).putNull("vie").
        refuser(n -> ((ObjectNode)n.get("heros")).putNull("vie"));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> ((ObjectNode)n.get("heros")).put("attaque", 999).
        refuser(n -> ((ObjectNode)n.get("heros")).put("attaque", 999));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> ((ObjectNode)n.get("heros")).put("potions", 999).
        refuser(n -> ((ObjectNode)n.get("heros")).put("potions", 999));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> ((ObjectNode)n.get("heros")).put("equipee", "RUNIQUE").
        refuser(n -> ((ObjectNode)n.get("heros")).put("equipee", "RUNIQUE"));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> ((ObjectNode)n.get("heros")).putArray("armes").add("inconnue").
        refuser(n -> ((ObjectNode)n.get("heros")).putArray("armes").add("inconnue"));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> ((ObjectNode)n.get("heros")).putArray("armes").add("ROUILLEE").add("ROUILLEE").
        refuser(n -> ((ObjectNode)n.get("heros")).putArray("armes").add("ROUILLEE").add("ROUILLEE"));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> ((ObjectNode)n.get("salles").get(0)).put("visitee", false).
        refuser(n -> ((ObjectNode)n.get("salles").get(0)).put("visitee", false));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> ((ObjectNode)n.get("salles").get(0)).put("utilisee", true).
        refuser(n -> ((ObjectNode)n.get("salles").get(0)).put("utilisee", true));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> ((ObjectNode)n.get("salles").get(1)).put("id", "entree").
        refuser(n -> ((ObjectNode)n.get("salles").get(1)).put("id", "entree"));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> ((ObjectNode)n.get("salles").get(1)).putNull("ennemi").
        refuser(n -> ((ObjectNode)n.get("salles").get(1)).putNull("ennemi"));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> ((ObjectNode)n.get("salles").get(1).get("ennemi")).put("type", "dragon").
        refuser(n -> ((ObjectNode)n.get("salles").get(1).get("ennemi")).put("type", "dragon"));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> ((ObjectNode)n.get("salles").get(1).get("ennemi")).put("tours", -1).
        refuser(n -> ((ObjectNode)n.get("salles").get(1).get("ennemi")).put("tours", -1));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> ((ObjectNode)n.get("salles").get(1).get("ennemi")).put("vie", 1).
        refuser(n -> ((ObjectNode)n.get("salles").get(1).get("ennemi")).put("vie", 1));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> ((ObjectNode)n.get("salles").get(2)).put("visitee", true).
        refuser(n -> ((ObjectNode)n.get("salles").get(2)).put("visitee", true)); // Arsenal au-delà du squelette vivant.
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> ((ObjectNode)n.get("salles").get(2)).put("utilisee", true).
        refuser(n -> ((ObjectNode)n.get("salles").get(2)).put("utilisee", true));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> ((ObjectNode)n.get("tactique")).put("elan", 4).
        refuser(n -> ((ObjectNode)n.get("tactique")).put("elan", 4));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> ((ObjectNode)n.get("tactique")).put("elan", -1).
        refuser(n -> ((ObjectNode)n.get("tactique")).put("elan", -1));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> ((ObjectNode)n.get("tactique")).put("hasard", -1).
        refuser(n -> ((ObjectNode)n.get("tactique")).put("hasard", -1));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> ((ObjectNode)n.get("tactique")).put("hasard", 1L << 48).
        refuser(n -> ((ObjectNode)n.get("tactique")).put("hasard", 1L << 48));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> n.putNull("tactique").
        refuser(n -> n.putNull("tactique"));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> ((ObjectNode)n.get("tactique")).putArray("historique").add("ATTAQUER").
        refuser(n -> ((ObjectNode)n.get("tactique")).putArray("historique").add("ATTAQUER"));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> ((ObjectNode)n.get("tactique").get("choix")).put("boss", "RISQUER").
        refuser(n -> ((ObjectNode)n.get("tactique").get("choix")).put("boss", "RISQUER"));
        // Construit un JSON volontairement incohérent avec cette mutation et vérifie son refus : n -> ((ObjectNode)n.get("tactique").get("choix")).put("tresor", "PRUDENT").
        refuser(n -> ((ObjectNode)n.get("tactique").get("choix")).put("tresor", "PRUDENT"));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : golem blesse sans tour est refuse.
    @Test void golemBlesseSansTourEstRefuse() throws Exception {
        // Initialise la variable p de type Partie avec combatGolem(). Joue une attaque du héros et l’éventuelle réponse ennemie.
        Partie p = combatGolem(); p.attaquer();
        // Initialise « n » avec json.valueToTree(SauvegardePartie.capturer(p)).
        ObjectNode n = json.valueToTree(SauvegardePartie.capturer(p));
        // Falsifie le compteur de réponses du golem blessé en le remettant à zéro, un état interdit par les règles.
        ((ObjectNode)n.get("salles").get(3).get("ennemi")).put("tours", 0);
        // Initialise le chemin du fichier avec dossier.resolve("invalide.json"). Écrit les données sous forme JSON dans le fichier ; arguments : fichier.toFile(), n.
        Path fichier = dossier.resolve("invalide.json"); json.writeValue(fichier.toFile(), n);
        // Vérifie que l’opération lève l’exception attendue (ExceptionSauvegarde) lors de () -> gestionnaire.charger(fichier).
        assertThrows(ExceptionSauvegarde.class, () -> gestionnaire.charger(fichier));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Teste le scénario suivant : donnees surnumeraires et fichier trop gros refuses.
    @Test void donneesSurnumerairesEtFichierTropGrosRefuses() throws Exception {
        // Initialise le chemin du fichier avec dossier.resolve("invalide.json").
        Path fichier = dossier.resolve("invalide.json");
        // Initialise « valide » avec json.writeValueAsString(SauvegardePartie.capturer(new Partie(new HasardReproductible(3)))).
        String valide = json.writeValueAsString(SauvegardePartie.capturer(new Partie(new HasardReproductible(3))));
        // Écrit le texte dans le fichier du scénario ; arguments : fichier, valide + " {}". Vérifie que l’opération lève l’exception attendue (ExceptionSauvegarde) lors de () -> gestionnaire.charger(fichier).
        Files.writeString(fichier, valide + " {}"); assertThrows(ExceptionSauvegarde.class, () -> gestionnaire.charger(fichier));
        // Écrit le texte dans le fichier du scénario ; arguments : fichier, " ".repeat(128 * 1024 + 1). Vérifie que l’opération lève l’exception attendue (ExceptionSauvegarde) lors de () -> gestionnaire.charger(fichier).
        Files.writeString(fichier, " ".repeat(128 * 1024 + 1)); assertThrows(ExceptionSauvegarde.class, () -> gestionnaire.charger(fichier));
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Altère volontairement un JSON de référence et vérifie que son chargement est refusé.
    private void refuser(Consumer<ObjectNode> mutation) throws Exception {
        // Initialise « n » avec json.valueToTree(SauvegardePartie.capturer(new Partie(new HasardReproductible(3)))). Transmet la valeur au rappel fourni ; arguments : n.
        ObjectNode n = json.valueToTree(SauvegardePartie.capturer(new Partie(new HasardReproductible(3)))); mutation.accept(n);
        // Initialise le chemin du fichier avec dossier.resolve("invalide.json"). Écrit les données sous forme JSON dans le fichier ; arguments : fichier.toFile(), n.
        Path fichier = dossier.resolve("invalide.json"); json.writeValue(fichier.toFile(), n);
        // Vérifie que l’opération lève l’exception attendue (ExceptionSauvegarde) lors de () -> gestionnaire.charger(fichier), n.toString().
        assertThrows(ExceptionSauvegarde.class, () -> gestionnaire.charger(fichier), n.toString());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Prépare une partie de test qui bat le squelette puis atteint le golem de la forge.
    private static Partie combatGolem() {
        // Initialise la variable p de type Partie avec un nouvel objet Partie avec new HasardReproductible(3). Emprunte le passage demandé selon les règles du modèle ; arguments : "ossuaire". Joue une attaque du héros et l’éventuelle réponse ennemie. Joue une attaque du héros et l’éventuelle réponse ennemie. Joue une attaque du héros et l’éventuelle réponse ennemie. Emprunte le passage demandé selon les règles du modèle ; arguments : "forge". Renvoie la variable p.
        Partie p = new Partie(new HasardReproductible(3)); p.deplacer("ossuaire"); p.attaquer(); p.attaquer(); p.attaquer(); p.deplacer("forge"); return p;
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    // Joue un vrai combat de test jusqu’à sa fin, puis vérifie l’absence de défaite.
    private static void gagner(Partie p) {
        // Initialise « actions » avec 0.
        int actions = 0;
        // Répète ce bloc tant que si un combat est actif de la variable p.
        while (p.estEnCombat()) {
            // Vérifie que la condition est vraie : actions++ < 100.
            assertTrue(actions++ < 100);
            // Si (p.getHeros().getVie() est inférieur ou égal à 40 et si une potion peut être consommée de la variable p), Consomme une potion et joue l’éventuelle réponse ennemie. Dans le cas contraire, Joue une attaque du héros et l’éventuelle réponse ennemie.
            if (p.getHeros().getVie() <= 40 && p.peutBoire()) p.boirePotion(); else p.attaquer();
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
        // Vérifie que les deux résultats sont différents : EtatPartie.DEFAITE ; l'issue de la partie de la variable p.
        assertNotEquals(EtatPartie.DEFAITE, p.getEtat());
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
