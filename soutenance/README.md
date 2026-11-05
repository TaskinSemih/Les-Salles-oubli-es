# Présenter Les Salles oubliées

**Ouvrir [la présentation](presentation/index.html)** dans Chrome ou Edge, puis appuyer sur **F** pour le plein écran. Sous Windows, un double-clic sur `presentation/index.html` suffit. Pour transporter la présentation, copier tout le dossier `soutenance` en conservant son organisation. Le navigateur lit les images, le CSS et le JavaScript localement. Aucun serveur, compte ni installation Java n’est nécessaire pour projeter les diapositives.

| Action | Commande |
|---|---|
| Avancer | →, Espace ou Page suivante |
| Revenir | ← ou Page précédente |
| Aller au début / à la fin | Début / Fin |
| Activer ou quitter le plein écran | F ou le bouton central ; Échap quitte aussi le plein écran |
| Rejouer la baisse des deux barres de vie sur la diapo 3 | T ou le bouton « Rejouer l’échange » |
| Ouvrir directement une diapositive | Ajouter `#5` à l’adresse pour la cinquième, par exemple |

Les transitions démarrent à l’entrée dans chaque diapositive. Aucune diapositive n’avance seule. Le tracé doré sur la carte désigne un chemin possible. Sur la troisième, l’image est une capture authentique de Swing ; les valeurs et les barres à droite expliquent le calcul du coup suivant. L’option système de réduction des animations conserve les informations finales.

## Répéter

1. Lire le [texte exact et ses repères de temps](SCRIPT_SOUTENANCE.md). Cible **9 min 51 s** à environ 141 mots par minute, gestes compris. Chronométrer une répétition à voix haute ; les relais proposés conviennent à trois ou quatre personnes.
2. Pour une réponse individuelle, préparer un exemple de contribution réellement effectué. Les noms et la répartition effective restent à renseigner par le groupe.
3. Travailler les [24 questions et leurs relances](QUESTIONS_SOUTENANCE.md). Consulter [l’analyse et les références Java](ANALYSE_PROJET.md) pour retrouver la ligne de raisonnement derrière chaque diapositive.
4. Garder ouvert [le PDF de secours](SOUTENANCE_SECOURS.pdf) dans un second onglet. Il contient une page par diapositive et se projette sans JavaScript. Pour le réimprimer depuis l’HTML, utiliser Ctrl+P et activer les arrière-plans.

Pour montrer le jeu lui-même en réponse à une question, depuis la racine du dépôt, lancer `java -jar target/les-salles-oubliees-1.0.0.jar` après `./mvnw.cmd verify` sous Java 21. L’oral prévu tient sans basculer vers l’application.

## Crédits visuels

Capture d’interface issue de `InterfaceTest`. Illustrations issues de l’atlas de personnages du projet. Carte et schémas reconstruits à partir des classes Java. Canvas fixe 1920 × 1080 du skill `frontend-slides` ; entrée des surtitres issue du catalogue local Animista, avec licence conservée dans `presentation/assets/animations.css`. La police et les commandes utilisent les ressources du système.
