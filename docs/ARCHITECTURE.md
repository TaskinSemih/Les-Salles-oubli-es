# Architecture

Java 21, Maven Wrapper, Swing, JUnit 5 et Jackson. Le package racine est `fr.utbm.sallesoubliees`. Les relations entre classes sont détaillées dans [UML.md](UML.md).

## Responsabilités

| Package | Classes principales | Responsabilité |
| --- | --- | --- |
| Racine | `Application` | Lance la fenêtre avec `SwingUtilities.invokeLater`. |
| `modele` | `Partie`, `GestionnaireCombat`, `EtatPartie` | Vérifient les actions, résolvent les tours et déterminent victoire ou défaite. |
| `modele` | `Donjon`, `Salle` | Construisent les neuf salles fixes et conservent passages, visites, ennemis et utilisation des ressources. |
| `modele` | `Personnage`, `Heros`, `Ennemi`, `Squelette`, `Golem`, `Mage`, `Boss` | Encapsulent les statistiques et les comportements polymorphes des ennemis. |
| `modele` | `Inventaire`, `Arme`, `Potion` | Gèrent les armes possédées, l'équipement et la quantité de potions. |
| `modele` | `EtatSauvegarde`, `SauvegardePartie` | Définissent l'instantané indépendant du JSON, sa capture et la reconstruction validée du modèle. |
| `controleur` | `ControleurJeu` | Conserve la partie courante, délègue les commandes et adopte une partie chargée avec succès. |
| `persistance` | `GestionnaireSauvegarde`, `ExceptionSauvegarde` | Lisent et écrivent le JSON, encadrent les erreurs de fichiers et appellent la validation métier. |
| `vue` | `FenetreJeu`, `CarteDonjon`, `Theme` | Affichent l'état, recueillent les actions, dessinent la carte et centralisent la présentation. |

Le modèle n'importe aucune classe Swing ou Jackson. Le contrôleur dépend du modèle. La persistance dépend du modèle et de Jackson. La vue dépend du contrôleur, du modèle consultable et du gestionnaire de fichiers : `FenetreJeu` organise elle-même les opérations asynchrones de sauvegarde et de chargement. Ce découpage reste explicite et ne prétend pas imposer un passage de toutes les lectures ou entrées/sorties par le contrôleur.

## Encapsulation, héritage et objets

`Partie` est l'API publique des actions de jeu. Les modifications de PV, d'inventaire, de visites et de compteurs ennemis sont réservées au package `modele`. Ses attributs sont privés ; les accesseurs permettent la consultation sans exposer de setters publics contournant les règles. Les collections de salles, passages et armes sont des vues non modifiables : elles suivent les mutations internes mais leur appelant ne peut pas les modifier.

`Personnage` rassemble statistiques et bornes des PV. `Ennemi` ajoute le compteur de réponses et déclare les méthodes polymorphes d'intention, de puissance et de type. Les quatre sous-classes implémentent leurs particularités ; le mage redéfinit également l'ignorance de l'armure. `GestionnaireCombat` demande ce comportement à l'ennemi sans reproduire chaque règle dans la fenêtre.

`Arme` est une énumération immuable de trois valeurs, avec nom et bonus. `Inventaire` conserve un ensemble d'armes, une arme équipée et un entier pour les potions identiques. `Potion` est une classe utilitaire contenant la constante de soin `SOIN`, sans instance. Il n'existe donc pas de classe `Objet` : une hiérarchie commune n'apporterait ici aucun comportement partagé utile. Le polymorphisme est utilisé là où les comportements diffèrent réellement, chez les ennemis.

## Résolution d'une action

Une action de bouton appelle le contrôleur, qui délègue à `Partie`. Le modèle vérifie les préconditions avant de modifier l'état. Une action de combat valide et la réponse ennemie éventuelle sont résolues dans le même appel. `Partie` contrôle la mort ennemie avant toute réponse et la mort du héros immédiatement après ; une partie terminée refuse les actions de jeu.

La défense est un paramètre de la réponse ennemie, valable pour cet appel seulement. Aucun drapeau de défense ne reste actif entre deux commandes. Le combat est déduit de l'état global et de la présence d'un ennemi vivant dans la salle actuelle ; il n'existe pas d'objet combat indépendant à sérialiser. La vue rafraîchit ensuite statistiques, carte, intentions et boutons, puis ajoute le message de l'action au journal.

## Instantané et validation

`EtatSauvegarde` est un **record du package `modele`**, avec les records imbriqués `EtatHeros`, `EtatSalle` et `EtatEnnemi`. Ce sont des données explicites, sans annotation Jackson ni nom de classe Java provenant du fichier. Les listes reçues sont copiées avec `List.copyOf`. Leurs éléments sont des records, des énumérations ou des valeurs immuables : l'instantané ne conserve aucune référence vers un personnage ou une salle modifiable.

`SauvegardePartie.capturer` construit ces données à partir de la partie entre deux actions. Les modifications de jeu suivantes ne changent pas la capture. `SauvegardePartie.restaurer` construit un héros et un donjon neufs, vérifie version, statistiques, identifiants et nombre de salles, bornes des PV et compteurs, coffres et armes, quantité maximale de potions acquises, visites accessibles et cohérence de la fin de partie. Elle retourne la nouvelle `Partie` seulement après ces contrôles. Ces vérifications assurent les invariants implémentés ; elles ne rejouent pas un historique complet de toutes les actions.

La carte, les récompenses et les statistiques fixes viennent du catalogue Java. Le JSON décrit leur état variable et les statistiques attendues du héros, dont la conformité est contrôlée. Le compteur de réponses des ennemis préserve notamment l'alternance du golem. Le journal est informatif et n'est pas enregistré. La défense ayant expiré avant chaque capture, aucun état temporaire supplémentaire n'est nécessaire.

## Fichiers et interface asynchrone

`GestionnaireSauvegarde` utilise un `ObjectMapper` configuré strictement : champs inconnus ou manquants, clés dupliquées, types incompatibles et contenu supplémentaire sont refusés. La lecture est limitée à 128 Kio. Après décodage, la validation métier est obligatoire. Les exceptions de fichier ou de validation sont enveloppées dans `ExceptionSauvegarde`, avec une explication française pour l'interface.

Avant une écriture, l'instantané est également validé. Le JSON est écrit dans un fichier temporaire du même répertoire, puis déplacé vers la cible avec remplacement atomique si le système le permet. Un remplacement ordinaire sert de repli si le déplacement atomique n'est pas pris en charge ; ce repli ne garantit pas la même résistance à une interruption système. Le temporaire est nettoyé dans la mesure du possible.

La création et les mises à jour des composants se font sur l'Event Dispatch Thread (EDT). Lors d'une sauvegarde, la capture a lieu sur l'EDT puis son écriture passe dans `SwingWorker.doInBackground`. Lors d'un chargement, lecture et reconstruction portent sur une nouvelle partie dans le fil de fond. `done` revient sur l'EDT, présente le résultat ou l'erreur et, après confirmation d'abandon si nécessaire, demande au contrôleur d'adopter la partie chargée. Un échec conserve la référence actuelle. Les commandes sont désactivées pendant l'opération, puis rafraîchies même en cas d'erreur.
