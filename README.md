# Les Salles oubliées

Jeu de donjon local en Java 21 et Swing, réalisé pour un projet universitaire de programmation orientée objet. Explorez neuf salles, affrontez squelettes, golems et mage, trouvez deux armes puis vainquez le Gardien. Combats déterministes au tour par tour, potions, coffre, repos unique et sauvegardes JSON, y compris en combat.

L'interface et la documentation sont en français. Aucune image distante, aucun compte joueur ni serveur. L'objectif de durée est de 10 à 15 minutes ; il reste à mesurer auprès de joueurs. Soutenance : **19 novembre 2026** ; dépôt : **21 novembre 2026**.

## Une scène de donjon 2D

Héros animé dans quatre directions, neuf salles décorées et combats latéraux avec poses, dégâts flottants, soins et effets magiques. La carte (M), l'inventaire (I) et le journal (J) sont des panneaux secondaires. En combat : 1 attaque, 2 défense, 3 potion. Échap ouvre la pause et les options d'animations réduites. Cette version est silencieuse.

Les trois atlas originaux sont embarqués : [direction artistique](docs/DIRECTION_ARTISTIQUE.md), [ressources et prompts](docs/ASSETS.md). Les captures réelles sont régénérées par le test Swing dans `target/captures/`. La position visuelle n'est pas sauvegardée : au chargement, le héros revient au point praticable (425, 370) de la salle enregistrée.

## Prérequis

- Un **JDK 21**, avec `java -version` et `javac -version` disponibles.
- Git pour récupérer le dépôt ; un bureau graphique pour lancer Swing.
- Une connexion au premier build pour Maven et ses dépendances. Maven 3.9.12 est fourni par le Wrapper 3.3.4 : aucune installation globale de Maven n'est nécessaire.
- Windows : PowerShell. Linux : shell POSIX et `curl` ou `wget`, avec `unzip` pour le Wrapper.

Si Java manque, installer un JDK 21, par exemple [Eclipse Temurin](https://adoptium.net/temurin/releases/?version=21), puis ouvrir un nouveau terminal. `JAVA_HOME` doit désigner le dossier du JDK, et son dossier `bin` doit être dans `PATH`. Le projet ne modifie aucun réglage global.

## Installation et lancement

```sh
git clone https://github.com/TaskinSemih/Les-Salles-oubli-es.git
cd Les-Salles-oubli-es
git switch feat/visual-rpg
```

Windows PowerShell, depuis la racine :

```powershell
.\mvnw.cmd --batch-mode verify
java -jar target/les-salles-oubliees-1.0.0.jar
```

Linux :

```sh
./mvnw --batch-mode verify
java -jar target/les-salles-oubliees-1.0.0.jar
```

Le JAR contient les dépendances Jackson : il peut être copié puis exécuté hors ligne sur un ordinateur avec Java 21 et un bureau graphique. Utiliser le JAR sans préfixe `original-`. Pour reconstruire hors ligne après le premier build, ajouter `--offline` à la commande Wrapper ; les plugins doivent déjà être en cache.

## Jouer

1. Choisir **Nouvelle partie**, puis cliquer la porte est pour la rejoindre, ou marcher avec les flèches/ZQSD et interagir avec E ou Entrée.
2. **Attaquer** inflige des dégâts ; **Défendre** protège de la seule réponse suivante. Lire l'intention adverse, surtout face au golem.
3. Boire une potion rend jusqu'à 35 PV et consomme une réponse ennemie en combat. Une action impossible est désactivée.
4. Après le squelette, visiter l'arsenal, ouvrir le coffre, ouvrir l'inventaire avec I puis équiper l'acier. Changer d'arme se fait hors combat.
5. Le sanctuaire fournit un soin complet unique. La bibliothèque mène à l'antichambre, au trésor et au boss. Les passages sont bidirectionnels.
6. Dans **Échap · Pause**, **Sauvegarder** et **Charger** fonctionnent entre deux actions, même pendant un combat. Après victoire ou défaite, choisir **Nouvelle partie** ou charger une sauvegarde.

Les noms, positions et visites sont affichés sur la carte ; les infobulles précisent le contenu des salles. Le journal explique les résultats de chaque action. Le déplacement est bloqué tant que l'ennemi de la salle est vivant.

## Vérification et packaging

| Action | Windows PowerShell | Linux |
| --- | --- | --- |
| Tests JUnit | `.\mvnw.cmd test` | `./mvnw test` |
| Tests et JAR | `.\mvnw.cmd verify` | `./mvnw verify` |
| Reconstruction complète | `.\mvnw.cmd clean verify` | `./mvnw clean verify` |
| Javadoc stricte | `.\mvnw.cmd javadoc:javadoc` | `./mvnw javadoc:javadoc` |
| Test Swing facultatif, avec bureau | `.\mvnw.cmd test '-Dtest=InterfaceTest' '-Dtests.interface=true'` | `./mvnw test -Dtest=InterfaceTest -Dtests.interface=true` |

Le test Swing ouvre brièvement une fenêtre, utilise ses boutons et produit des captures dans `target/captures/`. Il est ignoré par défaut, notamment en CI sans écran. Les rapports JUnit sont dans `target/surefire-reports/` et la Javadoc dans `target/reports/apidocs/index.html`.

La CI compile, teste, construit le JAR et génère la Javadoc sur Windows et Ubuntu avec Java 21. Le [compte rendu de validation](docs/VALIDATION.md) distingue les commandes réellement exécutées des vérifications restant à faire.

## Sauvegardes

Le sélecteur propose `sauvegardes/partie.save.json`. Ce dossier et les fichiers `*.save.json` sont ignorés par Git. Une confirmation précède tout remplacement de fichier ou abandon de partie. Un chargement échoué conserve la partie affichée.

Le JSON version 1 stocke les statistiques, armes, potions, salle actuelle, visites, coffres, repos, PV et tours de chaque ennemi et issue de la partie. L'absence d'ennemi dans une salle est représentée explicitement par `null`. Les fichiers incohérents, inconnus ou supérieurs à 128 Kio sont refusés. La validation vise les invariants structurels et métier ; ce n'est pas un dispositif anti-triche prouvant l'historique complet de chaque action. Le journal n'est pas sauvegardé.

L'écriture utilise un temporaire voisin puis un remplacement atomique si le système de fichiers le permet, avec remplacement standard en secours. Elle ne garantit pas la résistance à une coupure électrique sur tous les systèmes.

## Organisation et documentation

```text
src/main/java/fr/utbm/sallesoubliees/
  Application.java       lancement sur l'EDT
  modele/                règles, personnages, donjon et instantanés
  controleur/            actions et partie affichée
  persistance/           fichiers JSON et erreurs contrôlées
  vue/                   fenêtre, carte et thème Swing
src/main/resources/      métadonnées locales
src/test/java/           tests métier, fichiers et interface facultative
docs/                    conception, UML et préparation de la soutenance
```

- [Cahier des charges](docs/CAHIER_DES_CHARGES.md), [règles chiffrées](docs/REGLES_DU_JEU.md).
- [Schéma de conception POO simplifié](docs/SCHEMA_CONCEPTION_POO.md), [architecture](docs/ARCHITECTURE.md), [diagrammes UML complets](docs/UML.md).
- [Tests manuels](docs/TESTS_MANUELS.md), [résultats de validation](docs/VALIDATION.md).
- [Répartition proposée](docs/REPARTITION.md), [soutenance](docs/SOUTENANCE.md).
- [Contribution Git](CONTRIBUTING.md), [conventions des agents](AGENTS.md).

Pour la soutenance, commencer par `Partie`, `GestionnaireCombat`, `Personnage` / `Ennemi` et ses sous-classes, `Inventaire`, `SauvegardePartie`, `GestionnaireSauvegarde`, puis `ControleurJeu` et `FenetreJeu`.

Références techniques : [Maven Wrapper](https://maven.apache.org/tools/wrapper/index.html) et [options de désérialisation Jackson](https://github.com/FasterXML/jackson-databind/wiki/Deserialization-Features). Les dépendances et plugins Maven sont épinglés dans `pom.xml`.
