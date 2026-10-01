# Conventions du projet

- Java 21, Maven Wrapper, français pour l'interface et les explications.
- Garder les règles dans `modele`, sans dépendance Swing ou Jackson.
- Préserver l'encapsulation, les collections en lecture seule et les invariants au chargement.
- Documenter les classes, constructeurs et méthodes en Javadoc ; maintenir les règles et UML.
- Utiliser des versions épinglées. Aucun secret, sauvegarde personnelle ou fichier généré dans Git.
- Avant livraison : `./mvnw verify` et `./mvnw javadoc:javadoc` (Windows : `./mvnw.cmd`).
- Préserver le travail existant. Pas de reset destructif, force push ni fusion automatique.
- Les tests doivent couvrir les règles et les erreurs de fichiers, avec un parcours gagnant réel.
