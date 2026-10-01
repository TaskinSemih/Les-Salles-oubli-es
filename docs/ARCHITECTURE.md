# Architecture

Java 21, Maven, Swing, JUnit 5 et Jackson. Le package racine est `fr.utbm.sallesoubliees`.

- `modele` : `Partie` orchestre les règles d'exploration ; `GestionnaireCombat` résout les tours. `Donjon` construit les neuf `Salle`. `Personnage` est la base de `Heros` et `Ennemi` ; `Squelette`, `Golem`, `Mage` et `Boss` redéfinissent les intentions/attaques. `Inventaire` contient les `Arme` et le nombre de `Potion` identiques.
- `controleur` : `ControleurJeu` possède la partie et applique les commandes ; un chargement ne remplace la référence qu'après réussite.
- `persistance` : données JSON explicites (`EtatSauvegarde` et records imbriqués), conversion contrôlée dans le modèle et `GestionnaireSauvegarde` pour les entrées/sorties atomiques.
- `vue` : `FenetreJeu` et `CarteDonjon` affichent l'état et délèguent les actions. Les fichiers sont lus/écrits dans un `SwingWorker`. Les boutons sont rafraîchis après chaque action.

Le modèle ne dépend pas de Swing ni de Jackson. Ses collections sont exposées en lecture seule. Les mutations des personnages et salles restent internes au package métier ; seules les actions de `Partie` constituent l'API de jeu. La reconstruction d'une sauvegarde valide toutes les relations avant publication.

Les données de sauvegarde sont un instantané immuable partagé avec la persistance. Aucun typage polymorphique Jackson ni nom de classe Java n'est lu depuis un fichier utilisateur.
