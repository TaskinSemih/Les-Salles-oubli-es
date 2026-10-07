# Direction artistique — Les Salles oubliées

Un explorateur au manteau turquoise traverse une forteresse oubliée. Les scènes évoquent la composition des RPG 2D vus du dessus et les combats latéraux classiques, sans reprendre leurs personnages, décors ou musiques.

## Palette

| Usage | Couleur |
| --- | --- |
| Fond profond | `#10131f` |
| Pierre | `#39465b`, `#596477` |
| Ombres violettes | `#252139` |
| Torches | `#f4b75d` |
| Interactions et titre | `#e5c481` |
| Héros et soins | `#66d5c4` |
| Dégâts et rage | `#f08083` |
| Texte | `#f1eadc` |

## Composition et échelle

Une scène logique de 960 × 540 conserve ses proportions, indépendamment de la fenêtre. Les tuiles font 32 unités. Les sprites sont découpés une fois, puis dessinés avec une interpolation au plus proche voisin. Exploration : héros à une hauteur de référence de 78 unités ; combat : héros 160, gardien 205. Les pieds servent d'ancrage stable pour toutes les poses. Les objets sont répartis en couches, avec des collisions rectangulaires uniquement à leur base.

La scène domine l'écran. HUD compact dans les coins, bandeau de lieu en haut, actions et bref événement en bas. Mini-carte discrète ; carte, inventaire, pause, options et journal en superposition. Les labels utilisent une police sans empattement lisible, pas une police pixelisée.

## Assets et animation

Sprites originaux générés : héros turquoise avec épée, squelette armé, golem de pierre, mage pourpre, gardien cuirassé. Six poses par personnage : repos, deux marches, attaque, défense/préparation, blessure. La chute réutilise la pose blessée avec une courte disparition. Les poses dessinées sont complétées par des déplacements courts, effets d'impact, projectiles et nombres flottants.

L'exploration dispose d'un troisième atlas original, `marche.png`, généré avec l'outil intégré imagegen à partir de l'identité du héros. Ses douze sprites présentent quatre directions : sud face au joueur, nord de dos avec le manteau visible, est et ouest. Chaque direction possède une pose au repos et deux pas alternés. Le héros utilise ainsi de véritables vues directionnelles, dont une vue de dos, avec des proportions compactes adaptées à la lecture de la salle. Le prompt est conservé dans `docs/prompts/marche.txt`.

Les trois atlas sont des PNG RGBA transparents ; leurs dimensions réelles et bornes de découpe sont dans [ASSETS.md](ASSETS.md). La génération n'ayant pas produit une grille parfaite, le chargement conserve la composante alpha connexe principale de chaque cadre pour écarter les fragments des sprites voisins. Cette opération porte sur des copies en mémoire et préserve les fichiers originaux de personnages et de décors. Les particules séparées, ombres et effets lumineux sont rendus par Java2D.

Le décor partage une maçonnerie déterministe mais chaque salle reçoit une composition propre. Les portes suivent une table explicite du graphe métier. Aucun passage n'est déduit de la position sur la mini-carte.

Conventions : PNG RGBA sous `src/main/resources/graphismes/`, cadres de découpe documentés dans le chargeur de ressources. Pas d'accès fichier dans `paintComponent`. Les prompts exacts sont conservés dans `docs/prompts/`. Les détails de provenance et les limitations du générateur sont décrits dans `ASSETS.md`.

La planche des trente poses `target/captures/atlas-validation.png` a été inspectée après correction, avec des captures d'accueil, d'exploration, des combats contre le squelette et le boss, ainsi que de l'inventaire. Cette revue porte sur ces représentations ; elle ne clôt pas la validation des déplacements, des douze poses directionnelles et de toutes les séquences animées. Les captures de contrôle restent des fichiers générés hors Git.
