# Compte rendu de validation — 1er octobre 2026

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
