# Proposition de pull request

Titre : **Ajoute le jeu Java Swing Les Salles oubliées**

Le dépôt initialement vide dispose maintenant d'un jeu desktop local : neuf salles reliées, combats au tour par tour avec quatre comportements ennemis, armes, potions, coffres, repos, victoire et défaite. L'interface française affiche carte, statistiques, intentions ennemies, inventaire et journal. Les actions impossibles sont désactivées.

Les sauvegardes JSON versionnées reprennent exactement les combats entre deux actions. Les fichiers et leurs relations sont validés avant remplacement de la partie ; les écritures utilisent un temporaire et un remplacement atomique lorsque disponible. Les entrées/sorties tournent hors de l'EDT.

Le livrable comprend Maven Wrapper Windows/Linux, JAR avec dépendances, CI Java 21 Windows/Ubuntu, Javadoc, règles chiffrées, UML et supports de collaboration/soutenance.

## Validation

- 35 tests locaux réussis : 34 métier/persistance et un scénario graphique Swing facultatif.
- Parcours gagnant complet des neuf salles, plus parcours direct ; reprises de combat, victoire et défaite testées.
- Packaging et Javadoc stricte réussis ; JAR démarré.
- Script POSIX vérifié hors ligne sous Git Bash Windows.
- Captures accueil/combat/arsenal inspectées ; défauts de disposition corrigés à la taille minimale.
- Résultats détaillés et exécutions CI dans [VALIDATION.md](VALIDATION.md).

## Limites

La durée de 10 à 15 minutes reste à mesurer avec des joueurs. Les confirmations, sélecteurs de fichiers, navigation clavier et autres configurations d'écran restent à compléter selon la checklist manuelle. Le journal n'est pas sauvegardé. Les sauvegardes ne constituent pas un mécanisme anti-triche.

## État de publication

Branche livrée : `feat/initial-game`. Le dépôt ne possédait aucun commit ni branche `main` à l'origine ; seule la branche de travail a été poussée. Aucune fusion ni création de `main` n'a été effectuée. Une pull request vers `main` nécessite d'abord une branche de base, à initialiser selon la convention retenue par l'équipe. GitHub CLI n'est pas installé dans l'environnement de travail.

Pour une revue des ajouts d'interface/persistance/tests, l'équipe peut choisir le premier commit `39ce1f8` (socle compilable) comme base `main` :

```sh
git fetch origin
git branch main 39ce1f8
git push origin main
```

Ces commandes sont proposées, **pas exécutées**. Elles créent une base contenant déjà le modèle et les règles ; le reste sera comparé dans la PR. Ne pas les utiliser si une branche `main` a entre-temps été créée : examiner d'abord son historique. Ensuite ouvrir la comparaison GitHub avec base `main` et branche `feat/initial-game`, puis utiliser le titre et le corps ci-dessus. Ne pas fusionner sans la revue de l'équipe.
