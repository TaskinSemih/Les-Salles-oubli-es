# Refonte visuelle RPG 2D des Salles oubliées

Branche : `feat/visual-rpg`. Base de revue : `feat/initial-game`, version fonctionnelle `22354d6`. Le dépôt ne possède pas de branche `main` ; cette PR compare la refonte à la version fonctionnelle sans créer ni fusionner de branche de base.

La scène remplace la présentation centrée sur les cases : héros animé dans quatre directions, neuf salles avec murs, sols, portes et décors propres, puis combats latéraux avec poses d'attaque, défense, soin, impacts, projectile du mage et rage du Gardien. Carte, inventaire, journal et pause deviennent des panneaux secondaires.

Trois atlas originaux locaux sont inclus dans le JAR. Leur génération assistée, leurs prompts et leur découpe sont documentés. Le moteur produit des événements immuables et des instantanés avant/après ; la vue anime une copie sans rappeler les règles. Le JSON version 1 et les règles chiffrées sont conservés.

## Validation

Tests métier, fichiers, événements, graphe visuel, parcours Swing jusqu'à la victoire, double clic, pause, trajet près d'un pilier, raccourcis et vrais dialogues de sauvegarde/reprise en combat. Packaging et Javadoc stricte vérifiés. Captures réelles examinées ; détails et limites dans [VALIDATION.md](VALIDATION.md).

## Limites

Version silencieuse. Essais physiques au clavier/Tab, diversité des DPI, grand écran réel 1920×1080, confirmations de remplacement et chargement avec perte de focus restent à compléter. La fenêtre de test grand écran a été limitée à 1297×817 par le bureau utilisé ; son nom de capture décrit la taille demandée. La durée de jeu reste à mesurer avec des joueurs. Aucune fusion automatique.
