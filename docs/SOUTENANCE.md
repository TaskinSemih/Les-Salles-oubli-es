# Préparer la soutenance

Soutenance : **19 novembre 2026**. Dépôt du livrable : **21 novembre 2026**. Le déroulé ci-dessous est une proposition à adapter au temps accordé par l'enseignant ; il ne décrit pas une présentation déjà réalisée.

## Préparation pratique

- Disposer d'un JDK 21, du dépôt et des dépendances Maven téléchargées sur l'ordinateur de démonstration.
- Exécuter `./mvnw.cmd verify` puis `./mvnw.cmd javadoc:javadoc` sous Windows ; utiliser `./mvnw` sous Linux. Conserver les résultats réels et identifier le commit présenté.
- Lancer `java -jar target/les-salles-oubliees-1.0.0.jar` et effectuer les essais de [TESTS_MANUELS.md](TESTS_MANUELS.md).
- Préparer des sauvegardes locales créées par le jeu : un combat contre un golem, l'approche du boss et une copie corrompue. Ne pas les inclure dans Git.
- Ouvrir les diagrammes de [UML.md](UML.md), les règles et les classes essentielles. Répartir la parole sans attribuer à un membre un travail qu'il n'a pas réalisé.

## Démonstration proposée

1. **Présenter le besoin.** Jeu local court, donjon fixe, choix de Swing et périmètre volontairement limité. La durée cible de 10 à 15 minutes n'est annoncée comme atteinte que si des mesures le montrent.
2. **Créer une partie.** Montrer la carte, les passages, les PV, l'inventaire et le journal. Expliquer qu'une action passe par le contrôleur puis par le modèle.
3. **Entrer dans l'ossuaire.** Montrer le verrouillage des déplacements et un tour de combat. Après victoire, ouvrir le coffre de l'arsenal et équiper l'acier ; expliquer pourquoi le bonus ne se cumule pas.
4. **Présenter le polymorphisme.** Charger la sauvegarde locale du golem. Montrer préparation, défense et frappe ; comparer les comportements du squelette, du mage et du boss dans le code.
5. **Sauvegarder en combat.** Noter PV et intention, sauvegarder, agir puis recharger. Retrouver le même état et expliquer la conservation du compteur de tours ennemis.
6. **Démontrer un échec de chargement.** Charger la copie corrompue ; montrer le message et la conservation de la partie courante. Expliquer validation puis remplacement de la référence.
7. **Montrer la fin.** Charger la sauvegarde d'approche du boss, terminer le combat si le temps le permet, montrer la victoire et la nouvelle partie. Les sauvegardes préparées doivent être annoncées comme telles.
8. **Présenter les preuves.** Tests réellement exécutés, parcours gagnant, Javadoc et CI disponible ; distinguer tests automatiques, essais visuels et limites restantes.

## Classes essentielles à comprendre

Pour la refonte, montrer d'abord le héros dans une salle et la marche ZQSD, puis un tour animé. `Partie.resoudre` applique les règles une fois et produit `ResultatAction` avec instantanés et événements. `ControleurAnimation` anime des copies des PV sans rejouer les dégâts. `SceneJeu`, `RenduSalle` et `RenduCombat` partagent un cache d'atlas et un timer qui s'arrête au repos. Présenter aussi les prompts et la provenance des images dans [ASSETS.md](ASSETS.md), en distinguant assistance graphique et contributions des étudiants.

| Classe ou ensemble | Sujet à expliquer |
| --- | --- |
| `Partie` | Point d'entrée des actions, états autorisés, exploration et fin de partie. |
| `GestionnaireCombat` | Ordre des actions, réponse ennemie, défense temporaire et arrêt à la mort. |
| `Personnage`, `Heros`, `Ennemi` et ses sous-classes | Héritage, encapsulation et sélection polymorphique du comportement. |
| `Donjon`, `Salle`, `Inventaire`, `Arme`, `Potion` | Composition, passages, ressources uniques et protection des collections. |
| `EtatSauvegarde`, `GestionnaireSauvegarde` | Instantané explicite, format versionné, validation et erreurs d'entrée/sortie. |
| `ControleurJeu` | Coordination entre interface, partie et persistance. |
| `FenetreJeu`, `CarteDonjon` | Affichage, boutons disponibles, Event Dispatch Thread et travail de fichier en arrière-plan. |

## Questions individuelles possibles

- Quelle différence faites-vous entre héritage et composition dans ce projet ? Où le polymorphisme intervient-il réellement ?
- Pourquoi le modèle n'importe-t-il ni Swing ni Jackson ? Comment pourrait-on tester une partie sans fenêtre ?
- Comment empêcher un appelant de modifier directement les PV, l'inventaire ou les salles ?
- Pourquoi une action invalide ne déclenche-t-elle pas de réponse ennemie ? Où cette règle est-elle garantie ?
- Comment la défense expire-t-elle lorsque le golem prépare son coup ? Pourquoi cet état temporaire n'a-t-il pas à être sauvegardé entre deux actions ?
- Comment prouver qu'un ennemi mort ne contre-attaque pas et qu'un coffre ne redonne pas sa récompense ?
- Pourquoi enregistrer des identifiants et des données explicites plutôt que des noms de classes Java ?
- Quelle différence entre JSON syntaxiquement correct et état métier valide ? Que se passe-t-il si le chargement échoue ?
- À quoi sert le fichier temporaire lors de l'écriture ? Quelles limites existent si le système ne fournit pas de déplacement atomique ?
- Qu'est-ce que l'Event Dispatch Thread ? Pourquoi utiliser `SwingWorker` pour les fichiers ?
- Que vérifie le parcours gagnant automatique, et que ne prouve-t-il pas sur l'ergonomie ou la durée d'une partie ?
- Comment ajouter un ennemi sans recopier le moteur de combat ? Quels fichiers et documents devraient être mis à jour ?

Chaque étudiant doit pouvoir relier ses réponses au code livré et distinguer sa contribution effective de celle des autres membres ou des outils d'assistance.
