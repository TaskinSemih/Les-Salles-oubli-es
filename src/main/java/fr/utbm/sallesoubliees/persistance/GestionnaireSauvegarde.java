// Place cette classe dans la persistance, qui lit et écrit les fichiers.
package fr.utbm.sallesoubliees.persistance;

// Rend disponible StreamReadFeature depuis com.fasterxml.jackson.core.
import com.fasterxml.jackson.core.StreamReadFeature;
// Rend disponible DeserializationFeature depuis com.fasterxml.jackson.databind.
import com.fasterxml.jackson.databind.DeserializationFeature;
// Rend disponible MapperFeature depuis com.fasterxml.jackson.databind.
import com.fasterxml.jackson.databind.MapperFeature;
// Rend disponible ObjectMapper depuis com.fasterxml.jackson.databind.
import com.fasterxml.jackson.databind.ObjectMapper;
// Rend disponible JsonMapper depuis com.fasterxml.jackson.databind.json.
import com.fasterxml.jackson.databind.json.JsonMapper;
// Rend disponible EtatSauvegarde depuis fr.utbm.sallesoubliees.modele.
import fr.utbm.sallesoubliees.modele.EtatSauvegarde;
// Rend disponible Partie depuis fr.utbm.sallesoubliees.modele.
import fr.utbm.sallesoubliees.modele.Partie;
// Rend disponible SauvegardePartie depuis fr.utbm.sallesoubliees.modele.
import fr.utbm.sallesoubliees.modele.SauvegardePartie;
// Rend disponible IOException depuis java.io.
import java.io.IOException;
// Rend disponible AtomicMoveNotSupportedException depuis java.nio.file.
import java.nio.file.AtomicMoveNotSupportedException;
// Rend disponible Files depuis java.nio.file.
import java.nio.file.Files;
// Rend disponible Path depuis java.nio.file.
import java.nio.file.Path;
// Rend disponible StandardCopyOption depuis java.nio.file.
import java.nio.file.StandardCopyOption;

/** Lit un JSON strict et écrit par fichier temporaire dans le même répertoire. */
// Déclare GestionnaireSauvegarde, lecture JSON stricte et écriture par fichier temporaire ; les règles restent dans le modèle. final interdit de créer une sous-classe.
public final class GestionnaireSauvegarde {
    // Initialise la limite de lecture de 128 Kio avec 128 * 1024.
    private static final int TAILLE_MAX = 128 * 1024;
    // Déclare le convertisseur JSON Jackson de type ObjectMapper.
    private final ObjectMapper mapper;

    /** Configure Jackson sans coercition silencieuse ni désérialisation de classes arbitraires. */
    // Construit un objet GestionnaireSauvegarde : lecture JSON stricte et écriture par fichier temporaire ; les règles restent dans le modèle.
    public GestionnaireSauvegarde() {
        // Met à jour le convertisseur JSON Jackson avec JsonMapper.builder().
        mapper = JsonMapper.builder()
                // Refuse les clés JSON répétées au lieu de conserver silencieusement la dernière.
                .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
                // Refuse les champs JSON absents du format prévu.
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                // Refuse un champ de constructeur manquant dans le JSON.
                .enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
                // Refuse null pour les nombres et booléens primitifs.
                .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                // Refuse les indices numériques à la place des noms des énumérations.
                .enable(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS)
                // Refuse les données supplémentaires après le document JSON attendu.
                .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                // Désactive les conversions implicites, comme un texte vers un nombre.
                .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
                // Refuse de tronquer un nombre décimal vers un entier, puis construit le lecteur configuré.
                .disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT).build();
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    /**
     * Écrit un instantané validé, sans toucher au fichier cible avant la fin de l'écriture.
     *
 * @param fichier chemin choisi par le joueur
     *
 * @param etat instantané capturé sur le fil de l'interface
     *
 * @throws ExceptionSauvegarde si le fichier ne peut pas être enregistré
     */
    // Enregistre un instantané validé en passant par un fichier temporaire ou une tâche de fond.
    public void sauvegarder(Path fichier, EtatSauvegarde etat) throws ExceptionSauvegarde {
        // Initialise le fichier temporaire avec l’absence de valeur (null).
        Path temporaire = null;
        // Commence l’opération dont les erreurs sont prises en charge par les blocs catch suivants.
        try {
            // Recharge les valeurs reçues dans cet objet après validation ; arguments : etat.
            SauvegardePartie.restaurer(etat);
            // Initialise la cible avec fichier.toAbsolutePath().
            Path cible = fichier.toAbsolutePath();
            // Crée les dossiers parents manquants ; arguments : cible.getParent().
            Files.createDirectories(cible.getParent());
            // Met à jour le fichier temporaire avec Files.createTempFile(cible.getParent(), ".salles-", ".tmp").
            temporaire = Files.createTempFile(cible.getParent(), ".salles-", ".tmp");
            // Écrit les données sous forme JSON dans le fichier ; arguments : temporaire.toFile(), etat.
            mapper.writerWithDefaultPrettyPrinter().writeValue(temporaire.toFile(), etat);
            // Commence l’opération dont les erreurs sont prises en charge par les blocs catch suivants.
            try {
                // Déplace le temporaire vers la sauvegarde cible avec les options indiquées ; arguments : temporaire, cible, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING.
                Files.move(temporaire, cible, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            // Intercepte AtomicMoveNotSupportedException e pour traiter l’échec. Ne contient aucune instruction supplémentaire.
            } catch (AtomicMoveNotSupportedException e) {
                // Déplace le temporaire vers la sauvegarde cible avec les options indiquées ; arguments : temporaire, cible, StandardCopyOption.REPLACE_EXISTING.
                Files.move(temporaire, cible, StandardCopyOption.REPLACE_EXISTING);
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
        // Intercepte IOException | RuntimeException e pour traiter l’échec. Ne contient aucune instruction supplémentaire.
        } catch (IOException | RuntimeException e) {
            // Signale l’erreur par une exception : "Impossible d'enregistrer la partie. Vérifiez le dossier et les droits d'écriture.".
            throw new ExceptionSauvegarde("Impossible d'enregistrer la partie. Vérifiez le dossier et les droits d'écriture.", e);
        // Exécute ce nettoyage dans tous les cas, même après une erreur ou un retour anticipé.
        } finally {
            // Si le fichier temporaire est différent de null, entre dans ce traitement conditionnel.
            if (temporaire != null) {
                // Essaie de supprimer le temporaire ; une erreur de nettoyage reste ignorée pour ne pas masquer l’erreur initiale.
                try { Files.deleteIfExists(temporaire); } catch (IOException ignored) { /* Ne masque pas l'erreur initiale. */ }
            // Ferme le bloc ou la liste de données commencé plus haut.
            }
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }

    /**
     * Charge et valide une partie neuve ; aucune partie existante n'est modifiée.
     *
 * @param fichier fichier JSON à lire
     *
 * @return partie entièrement validée
     *
 * @throws ExceptionSauvegarde si le fichier est absent, invalide ou incompatible
     */
    // Lit puis valide une nouvelle partie avant de l’adopter dans l’interface.
    public Partie charger(Path fichier) throws ExceptionSauvegarde {
        // Ouvre une ressource dans un try-with-resources : Java la fermera automatiquement à la sortie du bloc.
        try (var entree = Files.newInputStream(fichier)) {
            // Initialise le contenu lu avec entree.readNBytes(TAILLE_MAX + 1).
            byte[] contenu = entree.readNBytes(TAILLE_MAX + 1);
            // Si contenu.length est supérieur à la limite de lecture de 128 Kio, refuse l’opération : "Fichier trop volumineux.".
            if (contenu.length > TAILLE_MAX) throw new IOException("Fichier trop volumineux.");
            // Renvoie SauvegardePartie.restaurer(mapper.readValue(contenu, EtatSauvegarde.class)).
            return SauvegardePartie.restaurer(mapper.readValue(contenu, EtatSauvegarde.class));
        // Intercepte IOException | RuntimeException e pour traiter l’échec. Ne contient aucune instruction supplémentaire.
        } catch (IOException | RuntimeException e) {
            // Signale l’erreur par une exception : "Chargement impossible : fichier absent, illisible, corrompu ou incompatible (version 2 requise ; version 1 non prise en charge).".
            throw new ExceptionSauvegarde("Chargement impossible : fichier absent, illisible, corrompu ou incompatible (version 2 requise ; version 1 non prise en charge).", e);
        // Ferme le bloc ou la liste de données commencé plus haut.
        }
    // Ferme le bloc ou la liste de données commencé plus haut.
    }
// Ferme le bloc ou la liste de données commencé plus haut.
}
