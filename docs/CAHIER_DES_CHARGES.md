# Cahier des charges

## Objectif et contraintes

« Les Salles oubliées » est un jeu de donjon local pour un projet de programmation orientée objet de niveau I, réalisé par un groupe de trois ou quatre étudiants. La soutenance est prévue le **19 novembre 2026**, le dépôt du livrable le **21 novembre 2026**. Les noms et contributions effectives des membres sont à renseigner par le groupe.

Le joueur explore neuf salles fixes en 2D, affronte des ennemis au tour par tour et cherche à vaincre le Gardien. Une durée de 10 à 15 minutes est un objectif à mesurer lors des essais utilisateurs, et non un résultat acquis.

Le programme utilise Java 21, Maven Wrapper, Swing, JUnit 5 et une sauvegarde JSON versionnée. Il fonctionne sans serveur et sans téléchargement d'images. Une connexion est nécessaire pour récupérer initialement Maven et les dépendances ; leur présence locale permet ensuite de jouer hors ligne.

## Périmètre fonctionnel et acceptation

| Besoin | Critère d'acceptation |
| --- | --- |
| Accueil et cycle de partie | Nouvelle partie, chargement et sortie sont disponibles ; victoire et défaite sont annoncées et permettent de recommencer. |
| Exploration | Les neuf salles et les passages sont visibles ; position, visites et destinations accessibles se distinguent. Un déplacement suit un passage existant et reste interdit en combat. |
| Combats | Squelette, golem, mage et boss ont les comportements définis dans les règles. Une action valide déclenche au plus une réponse ennemie, aucune après sa mort ou la fin de partie. |
| Inventaire | Armes et potions sont consultables ; équiper remplace le bonus ; les potions consommées disparaissent et le soin respecte le maximum de PV. |
| Ressources du donjon | Chaque coffre et le repos fonctionnent une seule fois. Les ennemis vaincus ne réapparaissent pas. |
| Interface | Textes français, barres de vie, intention ennemie et journal défilant sont lisibles. Les actions impossibles sont désactivées. L'abandon d'une partie et le remplacement d'un fichier demandent confirmation. |
| Sauvegarde | Le JSON conserve les informations nécessaires à une reprise fidèle, y compris entre deux actions en combat. Une écriture utilise un fichier temporaire et un remplacement sûr lorsque possible. |
| Chargement | Les versions incompatibles, fichiers invalides et états incohérents sont refusés avec un message compréhensible. Un échec conserve la partie courante. |
| Équilibrage | Un test réalise une victoire complète avec des actions de jeu normales ; la durée fait l'objet d'une mesure manuelle distincte. |

Les valeurs et comportements de référence sont dans [REGLES_DU_JEU.md](REGLES_DU_JEU.md). Les cas d'interface sont dans [TESTS_MANUELS.md](TESTS_MANUELS.md).

## Qualité et critères techniques

- Les règles appartiennent à `modele`, sans dépendance Swing ou Jackson ; contrôleur, vue et persistance ont des responsabilités séparées.
- Les attributs sont privés, les collections exposées sont en lecture seule et le chargement respecte les invariants métier.
- L'héritage et le polymorphisme expriment notamment les comportements des ennemis ; aucune abstraction n'est ajoutée uniquement pour satisfaire un critère.
- Classes, constructeurs et méthodes sont documentés en Javadoc. Architecture, règles et UML suivent le code livré.
- Les versions des dépendances et plugins sont épinglées. Les sauvegardes personnelles, secrets et fichiers générés restent hors Git.
- Le Maven Wrapper fonctionne sous Windows et Linux. La CI Java 21 compile et teste le projet ; le packaging produit `target/les-salles-oubliees-1.0.0.jar`.
- `./mvnw verify` et `./mvnw javadoc:javadoc` réussissent avant livraison ; sous Windows, utiliser `./mvnw.cmd`.

Les tests automatiques doivent couvrir dégâts et PV, défense, particularités des ennemis, objets, déplacements, actions invalides, victoire et défaite, persistance fidèle, erreurs de fichiers et parcours gagnant. Leur présence ne remplace pas la validation visuelle ; seuls les essais réellement exécutés sont présentés comme validés.

## Hors périmètre

Pas de multijoueur, serveur, compte, monde ouvert, temps réel, carte aléatoire, éditeur de niveaux ni système complexe de quêtes.

## Livrables attendus

Sources Java et ressources locales, tests, `pom.xml`, Maven Wrapper, CI, `.gitignore`, `AGENTS.md`, README, guide de contribution et documentation du dossier `docs`. Le livrable inclut un historique conservé, une branche de travail revue et les résultats réels des vérifications. La branche ne doit pas être fusionnée automatiquement dans `main`.
