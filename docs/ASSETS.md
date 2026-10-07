# Ressources graphiques

La refonte visuelle utilise trois atlas originaux générés pour « Les Salles oubliées » avec l'outil intégré **imagegen**. Aucune image de site tiers, banque d'images ou franchise existante n'a été utilisée comme source. L'ajustement des personnages et le nouvel atlas de marche prennent comme référence le héros généré pour ce même projet. Les consignes de génération sont conservées dans `docs/prompts` ; elles décrivent les intentions, pas une garantie de conformité du résultat.

## Fichiers livrés

| Ressource | Dimensions réelles | Format | Usage |
| --- | --- | --- | --- |
| [personnages.png](../src/main/resources/graphismes/personnages.png) | 1 374 × 1 145 pixels | PNG RGBA avec transparence | Héros, squelette, golem, mage et Gardien, six poses par personnage. |
| [objets.png](../src/main/resources/graphismes/objets.png) | 1 427 × 1 102 pixels | PNG RGBA avec transparence | Seize accessoires de décor et icônes. |
| [marche.png](../src/main/resources/graphismes/marche.png) | 1 254 × 1 254 pixels | PNG RGBA avec transparence | Héros vu du dessus : quatre directions, trois poses chacune. |

Les PNG sont embarqués dans le JAR sous `/graphismes/`. Le jeu n'effectue aucun téléchargement d'image. Les murs, sols, portes, ombres, halos, projectiles et nombres flottants sont dessinés localement avec Java2D ; aucune texture tierce supplémentaire n'est requise.

## Prompts conservés

- [personnages-initial.txt](prompts/personnages-initial.txt) : création des cinq personnages originaux et des six poses, avec palette, transparence et composition souhaitées.
- [personnages-ajustement.txt](prompts/personnages-ajustement.txt) : tentative de régularisation de la mise en page et des marges en conservant les personnages générés.
- [objets.txt](prompts/objets.txt) : création des seize accessoires dans un style cohérent.
- [marche.txt](prompts/marche.txt) : génération supplémentaire du héros en quatre directions, dont une véritable vue de dos vers le nord, avec repos et deux poses de marche.

La consigne des personnages demandait notamment 1 536 × 1 280 pixels et des cellules de 256 pixels ; celle de marche demandait 1 024 × 1 024 pixels. Les fichiers exploités conservent les dimensions réelles indiquées ci-dessus. Les atlas générés **ne forment pas une grille parfaite** : le chargeur utilise des cadres explicites et retire les fragments voisins au chargement.

## Catalogue et découpe

`GestionnaireRessources` lit les images avec `ImageIO` à la création, vérifie leur présence et un canal alpha, puis conserve les découpes en mémoire. Les coordonnées de cadres sont définies explicitement ; il ne faut pas diviser simplement la largeur et la hauteur par un nombre de colonnes.

Pour les personnages, les lignes sont héros, squelette, golem, mage, Gardien. Les colonnes sont repos, marche 1, marche 2, attaque, défense/préparation et blessure. La mort est présentée à partir de la pose de blessure et d'effets visuels, sans pose de mort dédiée. Certaines poses sont retournées horizontalement pour orienter l'action.

L'exploration utilise désormais l'atlas `marche.png`, avec douze sprites distincts. Les lignes correspondent au sud, au nord, à l'est et à l'ouest ; les trois colonnes sont repos, marche avec une jambe avancée et marche avec l'autre. Le nord montre le dos et le manteau du héros, sans simuler cette direction par une flèche sur une pose latérale. Les personnages du premier atlas restent utilisés pour la présentation et le combat.

Bornes de découpe actuelles, en pixels de l'atlas :

| Atlas | Bornes horizontales | Bornes verticales |
| --- | --- | --- |
| Personnages | `8, 232, 451, 674, 956, 1154, 1368` | `20, 253, 466, 704, 906, 1135` |
| Objets | `24, 386, 735, 1090, 1420` | `4, 281, 558, 825, 1100` |
| Marche | `155, 460, 810, 1130` | `38, 322, 617, 914, 1200` |

Dans chaque cadre, le chargeur identifie les composantes connexes à huit voisins parmi les pixels dont l'alpha dépasse 100. Il conserve la composante contenant le plus de pixels, puis crée une image ajustée à ses limites. Les fragments isolés provenant des sprites voisins sont ainsi supprimés de la copie en mémoire. Les PNG originaux de personnages et d'objets restent intacts ; aucune retouche destructive n'est appliquée aux fichiers. Les particules détachées sont dessinées séparément par les effets Java2D.

La planche des trente poses du premier atlas a été inspectée après cette correction. Cette méthode suppose que le corps, l'arme et les éléments à conserver forment la silhouette principale ; lors d'un remplacement d'asset, vérifier qu'un détail utile détaché n'est pas éliminé et qu'un fragment voisin n'est pas relié à la silhouette. La découpe n'est donc plus laissée sans correction, mais ses contrôles restent nécessaires après chaque changement de source.

Les objets sont indexés de gauche à droite puis de haut en bas :

| Index | Objet | Index | Objet |
| ---: | --- | ---: | --- |
| 0 | Coffre fermé | 8 | Enclume |
| 1 | Coffre ouvert | 9 | Autel à cristal |
| 2 | Torche | 10 | Bibliothèque |
| 3 | Pilier | 11 | Rune |
| 4 | Escalier | 12 | Or et bijoux |
| 5 | Ossements | 13 | Gravats |
| 6 | Râtelier d'armes | 14 | Potion |
| 7 | Fourneau | 15 | Bouclier |

Les images sont ancrées à leur base. La hauteur de référence au repos fixe l'échelle d'un personnage afin de conserver ses proportions entre poses. Pour la marche, la référence est de 78 unités logiques, calculée sur la pose au repos de chaque direction. Le rendu utilise l'interpolation au plus proche voisin pour garder des contours nets.

## Vérification et entretien

Une inspection visuelle a porté sur la planche locale des trente poses `target/captures/atlas-validation.png` après correction de la découpe, ainsi que sur les captures d'accueil, d'exploration, du squelette, du boss et de l'inventaire (`01-accueil.png`, `02-exploration.png`, `03-squelette.png`, `04-boss.png`, `05-inventaire.png` dans le même dossier). Ces images sont des artefacts de vérification sous `target/`, pas des ressources à embarquer ou à versionner.

Ces observations ne constituent pas une validation complète de toutes les animations, interactions ou configurations d'affichage. Après modification d'un PNG ou de ses bornes, vérifier chaque cadre isolé, l'absence de pixels d'un voisin, l'intégrité des armes et effets, la transparence, l'ancrage des pieds et la stabilité de taille entre poses. Pour le nouvel atlas de marche, contrôler les douze poses, les quatre directions et les changements de direction en jeu. Vérifier ensuite les salles et les combats aux tailles de fenêtre prévues.

Conserver les prompts avec l'asset retenu et actualiser cette table si la découpe change. Une génération respectant mieux la grille peut nécessiter de nouvelles coordonnées : les dimensions demandées dans un prompt ne remplacent jamais celles du fichier produit.
