# Tests manuels de l'interface

Cette checklist décrit les essais à réaliser ; elle ne constitue pas un compte rendu d'exécution. Pour chaque scénario, renseigner date, système, version Java, commit, résultat observé et éventuelle anomalie. La validation visuelle reste à effectuer tant qu'aucun compte rendu ne l'atteste.

## Préparation

Un JDK 21 et un environnement graphique sont nécessaires. Depuis la racine :

```powershell
# Windows PowerShell
./mvnw.cmd verify
./mvnw.cmd javadoc:javadoc
java -jar target/les-salles-oubliees-1.0.0.jar
```

```sh
# Linux
./mvnw verify
./mvnw javadoc:javadoc
java -jar target/les-salles-oubliees-1.0.0.jar
```

Utiliser un dossier temporaire de test pour les sauvegardes. Garder une copie intacte avant de modifier un fichier pour un essai d'erreur. Ne pas versionner ces fichiers.

## Parcours et ergonomie

| Cas | Manipulation | Résultat attendu |
| --- | --- | --- |
| Accueil | Lancer le jeu puis créer une partie. | Accueil français, commandes compréhensibles ; héros à 100 PV, entrée sélectionnée, inventaire initial conforme aux règles. |
| Lisibilité | Redimensionner la fenêtre, parcourir les commandes au clavier et consulter le journal. | Textes et statistiques lisibles ; focus visible ; journal défilant ; actions essentielles utilisables. |
| Carte | Observer la carte, entrer dans l'ossuaire. | Salles, passages, position et visites sont identifiables ; seules les destinations autorisées sont proposées. Le combat apparaît immédiatement. |
| Verrouillage | Pendant le combat, tenter de se déplacer ou de changer d'arme. | Commandes indisponibles ; aucune modification de position ou d'équipement. |
| Tours | Attaquer, défendre, puis attaquer. | PV, intention ennemie et journal sont rafraîchis après chaque action ; la défense ne protège qu'une réponse ennemie. |
| Potion | Boire en étant blessé, puis essayer à pleine vie ou avec zéro potion. | Soin plafonné, consommation visible ; une potion valide en combat entraîne la réponse ennemie ; les actions invalides restent indisponibles. |
| Mort ennemie | Achever le squelette. | Aucune contre-attaque finale ; une potion de récompense ; déplacements de nouveau disponibles. |
| Coffre | Aller à l'arsenal et ouvrir le coffre, repartir puis revenir. | Épée d'acier et deux potions reçues une fois ; coffre désormais indisponible. |
| Équipement | Équiper l'acier, essayer de l'équiper à nouveau, puis revenir à l'arme initiale. | Attaque 17 avec acier, 12 avec arme initiale ; aucun cumul. Rééquiper la même arme est indisponible. |
| Repos | Arriver blessé au sanctuaire, se reposer et revenir ultérieurement. | PV restaurés au maximum une seule fois ; à pleine vie, le repos ne doit pas être consommé. |
| Ennemis | Affronter un golem, un mage et le Gardien. | Intentions et dégâts conformes aux règles : préparation/frappe, magie ignorant l'armure de base, rage du boss à 45 PV ou moins. |
| Reprise de salles | Revisiter une salle après victoire contre son ennemi. | Aucun nouvel ennemi et aucune nouvelle récompense. |
| Abandon | Demander le retour au menu ou la fermeture pendant une partie, puis annuler ; recommencer et confirmer. | Annulation conserve la partie ; confirmation réalise l'action annoncée. |

## Sauvegarde et erreurs

| Cas | Manipulation | Résultat attendu |
| --- | --- | --- |
| Exploration | Sauvegarder après un coffre et un changement d'arme, jouer davantage, puis charger. | Position, PV, inventaire, équipement et états des salles correspondent à la sauvegarde. |
| Combat | Face au golem, sauvegarder après sa préparation, continuer un tour, puis charger et reproduire l'action. | Même PV et même intention à la reprise ; le prochain tour est la frappe attendue et produit le même résultat. |
| Remplacement | Sauvegarder sur le même fichier, annuler puis confirmer l'écrasement. | Confirmation demandée ; annulation laisse le fichier intact. |
| Annulation | Annuler un sélecteur de fichier. | Aucun changement d'état ni erreur trompeuse. |
| JSON invalide | Charger une copie contenant un JSON tronqué. | Message français sans trace technique ; partie courante inchangée. |
| Version inconnue | Modifier la version dans une copie, puis charger. | Version refusée ; partie courante inchangée. |
| Données incohérentes | Sur des copies, modifier un identifiant de salle ou d'arme, mettre des PV hors bornes ou un état contradictoire. | Fichier refusé avant publication du nouvel état. |
| Fichier inaccessible | Charger un chemin devenu absent ou, lorsque le système permet de le préparer, un fichier illisible ; essayer d'écrire dans un dossier sans permission. | Erreur compréhensible, interface utilisable et partie courante conservée. Ne pas changer les permissions d'un dossier système pour ce test. |
| Réactivité | Pendant une opération de fichier, observer l'interface et ses commandes. | Pas de gel prolongé ; commandes empêchant des opérations concurrentes incohérentes ; état rétabli après succès ou erreur. |

## Parcours complet et fin de partie

1. Suivre entrée → ossuaire → arsenal ; vaincre le squelette, ouvrir le coffre et équiper l'acier.
2. Traverser le sanctuaire puis vaincre le mage de la bibliothèque. Conserver le repos tant qu'il n'est pas utile.
3. Vaincre le golem de l'antichambre en tenant compte de son intention : attaquer pendant la préparation, défendre avant la frappe si nécessaire. Boire avant que la prochaine attaque soit mortelle.
4. Visiter le trésor, ouvrir le coffre et équiper la lame runique. Retourner par l'antichambre vers le Gardien ; au besoin, revenir au sanctuaire avant le combat final.
5. Vaincre le Gardien avec les potions restantes. Vérifier l'annonce de victoire, l'absence de réponse après sa mort et le blocage des actions de jeu.
6. Sauvegarder cette victoire, la charger, puis recommencer une partie : l'état initial doit être restauré uniquement lors de la nouvelle partie.
7. Dans une autre partie, laisser un ennemi épuiser les PV, par exemple en défendant de manière répétée. Vérifier l'annonce de défaite, le blocage des actions et le chargement d'une sauvegarde de défaite.

Chronométrer séparément une partie complète sans interruption de test. Noter familiarité du joueur, détours, victoire ou défaite et durée réelle. Ne pas conclure que l'objectif de 10 à 15 minutes est atteint à partir du seul test automatique.

## Compte rendu à compléter

### Refonte 2D : contrôles complémentaires

- Flèches et ZQSD : marche orientée, murs et bases des piliers infranchissables, aucun tour consommé. E / Entrée interagit à proximité ; clic sur une porte et carte M lancent une marche vers un passage réel.
- Vérifier les neuf compositions, le coffre ouvert et l'autel éteint après utilisation. Près du pilier nord-est de l'Ossuaire, rejoindre la porte est sans blocage.
- Observer attaque, parade, soin plafonné, nombres flottants, préparation du golem, projectile du mage, rage et disparition définitive du Gardien. Double-cliquer Attaquer ne résout qu'un tour.
- Pendant une animation, ouvrir la pause : image figée et fichiers indisponibles. Reprendre termine la même séquence. Tester retour au menu et fermeture avec annulation puis confirmation.
- Perdre le focus pendant une animation ou un chargement : aucune progression jusqu'au retour. Recréer plusieurs parties sans multiplication des timers.
- Charger un combat : même état métier, héros au point sûr (425, 370) ; aucune animation sérialisée. Vérifier les vrais sélecteurs, annulations et confirmations.
- Tester 900×620, 1280×720, 1920×1080 et DPI 125 %/150 % : proportions, lisibilité, menus défilants, Tab, Espace et raccourcis.
- Comparer animations normales et réduites : résultats identiques, séquences accélérées. Aucun réglage audio car aucun son n'est inclus.

Les captures `01-accueil` à `07-defaite`, `salle-*` et `resolution-*` sont produites dans `target/captures/` par le rendu réel Swing. L'entrée hostile impose immédiatement la rencontre, avec une courte présentation avant la vue latérale.

| Date / système / JDK / commit | Scénarios exécutés | Observations | Statut et anomalies |
| --- | --- | --- | --- |
| À renseigner | À renseigner | À renseigner | Non exécuté |
