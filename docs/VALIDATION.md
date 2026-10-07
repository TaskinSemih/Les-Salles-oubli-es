# Validation de la refonte — 7 octobre 2026

Branche `feat/visual-rpg`, issue de la version fonctionnelle `22354d6`. Java 21, Maven Wrapper et Swing sous Windows. Le compte rendu initial plus bas est historique et ne vaut pas validation de la refonte.

## Vérifications de la refonte exécutées

Dernière exécution complète : **51 tests, zéro échec, zéro erreur, aucun ignoré**, puis JAR et contrôle Javadoc réussis le 7 octobre 2026. Les échecs intermédiaires liés aux fenêtres de test interactives ont été corrigés avant cette exécution finale.

- Tests conservés : 34 tests métier et fichiers. Huit tests supplémentaires couvrent événements ordonnés, dégâts réellement infligés, invalidité sans effet, golem, rage, synchronisation et défense expirée. Quatre tests vérifient le graphe visuel, les zones praticables et les ressources embarquées.
- Parcours Swing réel des neuf salles jusqu'à la victoire : coffres, équipement acier puis runique, sanctuaire, tous les ennemis. Double clic limité à un tour, pause avec timer arrêté et sauvegarde verrouillée pendant la séquence. Captures générées pendant le parcours.
- Régression de marche près du pilier de l'Ossuaire : le waypoint est atteint exactement ; le trajet ne bloque plus les commandes. Une absence de progression annule aussi le trajet avec un message.
- Défaite et retours à l'accueil répétés : timer arrêté. Fermeture programmatique pendant une animation : timer arrêté.
- Bindings de marche D pressé/relâché exécutés : position modifiée, instantané métier identique. Cela ne remplace pas un parcours physique au clavier et avec Tab.
- Vrais dialogues Swing : sélection du fichier de sauvegarde en combat, sélection au chargement, confirmation d'abandon, reprise avec instantané strictement identique. Les composants sont pilotés sur l'EDT ; les fichiers sont écrits dans un dossier temporaire de test.
- `mvnw.cmd --batch-mode verify javadoc:javadoc -Dtests.interface=true` : packaging et Javadoc stricte vérifiés. Les tests graphiques sont facultatifs et nécessitent un bureau. Leurs fenêtres sont placées hors de la zone visible pour ne pas gêner la partie de l'utilisateur ; le parcours par composants neutralise les événements de focus externes.
- Captures réelles examinées : accueil, exploration, squelette, boss, inventaire, victoire, défaite et taille minimale 900×620. Planche des 30 poses inspectée après correction de découpe. Le boss vaincu reste invisible ; les éléments et commandes inspectés sont lisibles.
- Le bureau a limité la demande 1920×1080 à **1297×817** : la capture `resolution-1920x1080.png` décrit une taille demandée, pas un écran Full HD validé. Le plein écran réel et les autres DPI restent à tester.
- JAR lancé sur le bureau ; titre « Les Salles oubliées · Le serment du Gardien » et activation de la fenêtre confirmés. Aucun téléchargement d'assets à l'exécution.

## Limites actuelles

Version silencieuse. Restent les essais physiques Tab/clavier, DPI 125 % et 150 %, grand écran réel, confirmations d'écrasement/annulation dans toutes les combinaisons, perte de focus pendant un chargement et retours au menu confirmés pendant les animations. Les corrections de focus ont été relues ; le parcours automatisé ne valide pas les interactions avec toutes les autres fenêtres du bureau. La durée et l'ergonomie demandent des essais utilisateurs. La CI de la version initiale ne prouve pas la nouvelle branche ; consulter l'exécution associée au commit publié.

Sources, prompts et limites de découpe : [ASSETS.md](ASSETS.md). Captures et rapports sont régénérables dans `target/`, ignoré par Git. Base de revue retenue : `feat/initial-game`, seule branche distante avant publication ; aucune création de `main` ni fusion.

---

# Historique : validation initiale du 1er octobre 2026

## Environnement constaté

Dossier initial vide sous Windows 11, PowerShell ; dépôt GitHub également vide au clonage. Aucun historique préexistant à remplacer. Java et javac **21.0.7**, Maven installé **3.9.12**, Git **2.49.0.windows.1**. Identité Git déjà configurée, inchangée. Branche de travail : `feat/initial-game`.

## Vérifications exécutées

| Vérification | Résultat constaté |
| --- | --- |
| Compilation Java par `mvnw.cmd compile` | Succès, 24 fichiers applicatifs. |
| `mvnw.cmd verify javadoc:javadoc -Dtests.interface=true` | Succès : **35 tests**, zéro échec, zéro erreur, aucun ignoré. Packaging sans avertissement après correction des ressources communes du JAR. |
| Javadoc stricte (`doclint=all`, échec sur avertissement) | Générée dans `target/reports/apidocs/`, aucun avertissement lors de la génération corrigée. |
| Script POSIX `./mvnw --offline --batch-mode verify javadoc:javadoc` sous Git Bash Windows | Succès hors ligne ; 34 tests métier/fichiers réussis, test Swing facultatif ignoré comme prévu sans propriété d'activation. Ce contrôle n'est pas une exécution native Linux. |
| `java -jar target/les-salles-oubliees-1.0.0.jar` | Processus démarré, fenêtre « Les Salles oubliées » détectée, puis processus de test fermé. |
| Scénario Swing sur EDT | Accueil → nouvelle partie → ossuaire → trois attaques → ennemi vaincu → arsenal → coffre ouvert. Vérification des boutons disponibles et de leur visibilité à 1080 × 760. |
| Inspection visuelle | Captures Swing de l'accueil, du combat et de l'arsenal ouvertes et examinées. Coupures verticales initiales corrigées ; contrôle automatisé ajouté sur la hauteur des salles et les boutons. Captures régénérables dans `target/captures/`, non versionnées. |
| Relecture indépendante | Moteur puis persistance relus. État impossible « ennemi blessé avec zéro réponse jouée » détecté, refusé puis testé. |
| CI native Windows et Ubuntu | Les deux jobs de [l'exécution 36875651668](https://github.com/TaskinSemih/Les-Salles-oubli-es/actions/runs/36875651668) ont réussi sur le commit `276f37c` : tests, JAR et Javadoc. |
| Contrôle Git | Diff vérifié ; fichiers générés, sauvegardes et secrets exclus des fichiers destinés au commit. |

Les tests comprennent les dégâts et bornes de PV, la défense et son expiration, les comportements des quatre ennemis, les actions invalides sans effet, les potions, l'équipement sans cumul, les déplacements, les collections protégées, les coffres et repos uniques, les états terminaux, les instantanés indépendants, la reprise du golem, les erreurs JSON et relations incohérentes, les erreurs d'écriture et le nettoyage des temporaires.

Deux parcours gagnants sont exécutés : exploration des **neuf salles**, avec la forge facultative et le repos ; parcours direct avec coffre d'acier, mage, golem, trésor et boss. Victoire et défaite font également l'objet d'un aller-retour JSON.

## Limites restantes

- La durée de 10 à 15 minutes n'a pas été mesurée avec des joueurs ; le test automatique prouve la possibilité de gagner, pas la durée ni la qualité de l'équilibrage perçue.
- Les sélecteurs de fichiers, confirmations d'abandon/remplacement, navigation clavier et diversité des résolutions/DPI restent à parcourir manuellement selon [la checklist](TESTS_MANUELS.md). Les services de fichiers sont couverts automatiquement, mais pas toute la chaîne des dialogues Swing.
- La matrice CI Windows/Ubuntu a réussi sur `276f37c` ; le test Swing n'est pas activé en CI. Les exécutions suivantes sont consultables dans l'onglet Actions du dépôt.
- Les diagrammes Mermaid ont été relus par rapport au code ; leur rendu graphique n'a pas été exécuté ici.
- La sauvegarde valide les invariants et relations, sans rejouer tout l'historique pour détecter une modification volontaire qui resterait cohérente.
- La pull request reste à ouvrir après initialisation de `main`, absente dans le dépôt initial. La branche de travail a été poussée sans fusion. Le [texte de PR et les étapes restantes](PULL_REQUEST.md) sont préparés.

La dernière relecture Swing a confirmé les blocages de commandes, confirmations et publications sur l'EDT. Le chemin sélectionné au chargement est capturé sur l'EDT avant le lancement du `SwingWorker`.

## Reproduire

```powershell
.\mvnw.cmd --batch-mode verify javadoc:javadoc '-Dtests.interface=true'
java -jar target/les-salles-oubliees-1.0.0.jar
```

Le test graphique demande un bureau. Sans bureau, omettre `-Dtests.interface=true` : les tests métier et de fichiers continuent de fonctionner.
