package fr.utbm.sallesoubliees.persistance;

import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import fr.utbm.sallesoubliees.modele.EtatSauvegarde;
import fr.utbm.sallesoubliees.modele.Partie;
import fr.utbm.sallesoubliees.modele.SauvegardePartie;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Lit un JSON strict et écrit par fichier temporaire dans le même répertoire. */
public final class GestionnaireSauvegarde {
    private static final int TAILLE_MAX = 128 * 1024;
    private final ObjectMapper mapper;

    /** Configure Jackson sans coercition silencieuse ni désérialisation de classes arbitraires. */
    public GestionnaireSauvegarde() {
        mapper = JsonMapper.builder()
                .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
                .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .enable(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS)
                .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
                .disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT).build();
    }

    /**
     * Écrit un instantané validé, sans toucher au fichier cible avant la fin de l'écriture.
     * @param fichier chemin choisi par le joueur
     * @param etat instantané capturé sur le fil de l'interface
     * @throws ExceptionSauvegarde si le fichier ne peut pas être enregistré
     */
    public void sauvegarder(Path fichier, EtatSauvegarde etat) throws ExceptionSauvegarde {
        Path temporaire = null;
        try {
            SauvegardePartie.restaurer(etat);
            Path cible = fichier.toAbsolutePath();
            Files.createDirectories(cible.getParent());
            temporaire = Files.createTempFile(cible.getParent(), ".salles-", ".tmp");
            mapper.writerWithDefaultPrettyPrinter().writeValue(temporaire.toFile(), etat);
            try {
                Files.move(temporaire, cible, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaire, cible, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | RuntimeException e) {
            throw new ExceptionSauvegarde("Impossible d'enregistrer la partie. Vérifiez le dossier et les droits d'écriture.", e);
        } finally {
            if (temporaire != null) {
                try { Files.deleteIfExists(temporaire); } catch (IOException ignored) { /* Ne masque pas l'erreur initiale. */ }
            }
        }
    }

    /**
     * Charge et valide une partie neuve ; aucune partie existante n'est modifiée.
     * @param fichier fichier JSON à lire
     * @return partie entièrement validée
     * @throws ExceptionSauvegarde si le fichier est absent, invalide ou incompatible
     */
    public Partie charger(Path fichier) throws ExceptionSauvegarde {
        try (var entree = Files.newInputStream(fichier)) {
            byte[] contenu = entree.readNBytes(TAILLE_MAX + 1);
            if (contenu.length > TAILLE_MAX) throw new IOException("Fichier trop volumineux.");
            return SauvegardePartie.restaurer(mapper.readValue(contenu, EtatSauvegarde.class));
        } catch (IOException | RuntimeException e) {
            throw new ExceptionSauvegarde("Chargement impossible : fichier absent, illisible, corrompu ou incompatible (version 1 requise).", e);
        }
    }
}
