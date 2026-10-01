# Contribuer aux Salles oubliées

## Récupérer et lancer le projet

Utiliser Git et un JDK 21. Maven est fourni par le Wrapper ; le premier lancement télécharge Maven et les dépendances.

```sh
git clone https://github.com/TaskinSemih/Les-Salles-oubli-es.git
cd Les-Salles-oubli-es
git status
git switch -c feat/ma-modification
```

Si le dépôt existe déjà, commencer par `git status`, `git branch` et `git remote -v`, puis `git fetch origin`. Conserver les modifications locales et vérifier l'historique avant d'intégrer une branche distante. Ne pas remplacer un remote ni un dossier existant par automatisme.

Sous Windows PowerShell :

```powershell
./mvnw.cmd verify
./mvnw.cmd javadoc:javadoc
java -jar target/les-salles-oubliees-1.0.0.jar
```

Sous Linux :

```sh
./mvnw verify
./mvnw javadoc:javadoc
java -jar target/les-salles-oubliees-1.0.0.jar
```

## Conventions

Lire [AGENTS.md](AGENTS.md), [les règles](docs/REGLES_DU_JEU.md) et [l'architecture](docs/ARCHITECTURE.md) avant de modifier le comportement. Le package racine est `fr.utbm.sallesoubliees`. Écrire les textes d'interface, explications et Javadoc en français.

Garder les règles dans `modele`, sans Swing ni Jackson. Préserver attributs privés, collections en lecture seule et invariants, y compris au chargement. Documenter classes, constructeurs et méthodes ; maintenir les règles et [l'UML](docs/UML.md). Préférer des classes explicites à des abstractions sans besoin concret.

Épingler les versions. Ne pas committer `target/`, fichiers compilés, sauvegardes personnelles, journaux, secrets, fichiers privés d'environnement ou réglages personnels d'IDE. Conserver les fichiers nécessaires au Maven Wrapper. Ne jamais mettre un mot de passe ou un token dans une commande publiée, un fichier du dépôt ou une conversation.

## Branches, commits et revue

La branche initiale prévue est `feat/initial-game` ou une variante disponible. Pour les évolutions, utiliser une branche courte et descriptive telle que `feat/carte` ou `fix/chargement`. En cas de développement simultané, isoler les branches ou copies de travail et annoncer la responsabilité de chacun.

Créer des commits cohérents et vérifier exactement ce qui sera ajouté :

```sh
git status
git diff
git add chemin/du/fichier
git diff --cached
git commit -m "Corrige la validation des sauvegardes"
git push -u origin feat/ma-modification
```

Adapter le nom de branche et les chemins à la modification réelle. Vérifier l'identité avec `git config user.name` et `git config user.email`. Si elle manque, demander au contributeur son nom et son adresse avant de committer ; ne pas inventer d'identité et ne pas modifier sa configuration globale.

Ouvrir une pull request vers `main`. Décrire le problème, le comportement obtenu, les vérifications réellement exécutées et les limites restantes. Demander une relecture, puis laisser le groupe décider de la fusion. Pas de fusion automatique, de reset destructif ni de force push.

Si l'authentification manque, conserver les commits locaux, se connecter à GitHub avec le gestionnaire d'identifiants disponible ou `gh auth login` si GitHub CLI est installé, puis relancer le push. Ne jamais demander de transmettre un token dans le chat.

## Avant une livraison

- Exécuter `./mvnw verify` et `./mvnw javadoc:javadoc` ; sous Windows, remplacer `./mvnw` par `./mvnw.cmd`.
- Couvrir les règles modifiées et les erreurs de fichiers avec des tests pertinents ; préserver un parcours gagnant complet.
- Effectuer les scénarios d'interface concernés de [TESTS_MANUELS.md](docs/TESTS_MANUELS.md), ou signaler explicitement ceux qui restent à réaliser.
- Vérifier les fichiers suivis, la documentation et l'absence de fichiers générés ou sensibles.
- Consigner uniquement les résultats constatés. Un workflow CI configuré ne signifie pas qu'une exécution distante a réussi.

La soutenance est fixée au 19 novembre 2026 et le dépôt au 21 novembre 2026 ; préparer le livrable et sa démonstration avant ces échéances.
