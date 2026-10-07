# Règles du jeu — version 1

Toutes les valeurs sont déterministes. L'objectif de 10 à 15 minutes reste à mesurer avec des joueurs.

## Personnages et combat

Le héros commence avec 100/100 PV, 12 d'attaque de base, 3 d'armure, une épée rouillée (+0) et 3 potions (+35 PV chacune). Les PV sont toujours compris entre zéro et le maximum.

| Ennemi | PV | Attaque | Armure | Comportement |
| --- | ---: | ---: | ---: | --- |
| Squelette | 30 | 9 | 1 | Attaque normale chaque tour. |
| Golem | 48 | 14 | 4 | Prépare son coup aux tours impairs, frappe à puissance 22 aux tours pairs. |
| Mage | 36 | 11 | 1 | Ignore l'armure de base du héros, mais pas la défense temporaire. |
| Gardien (boss) | 90 | 16 | 3 | Puissance 24 à partir de 45 PV restants. |

Les dégâts sont `max(1, puissance - armure effective)`. Une attaque emploie l'attaque de base plus le bonus de l'arme équipée. Défendre ajoute 8 d'armure pour la seule réponse ennemie qui suit, puis expire, même si le golem prépare son coup. L'intention du prochain tour ennemi est visible.

Chaque action valide en combat (attaquer, défendre, boire une potion) est suivie exactement d'un tour ennemi, sauf si l'ennemi vient de mourir. Boire à pleine vie ou sans potion est invalide. Aucun effet ni tour ennemi pour une action invalide. Les potions soignent au plus jusqu'au maximum et disparaissent après utilisation. Hors combat, boire ne provoque aucune attaque.

Chaque ennemi ordinaire vaincu donne une potion, une seule fois. Le boss ne donne pas de récompense : sa mort termine immédiatement la partie par une victoire. À zéro PV du héros, la défaite termine immédiatement la partie. Aucune action métier n'est autorisée ensuite ; sauvegarde, chargement et nouvelle partie restent possibles.

## Carte et exploration

Neuf salles fixes, passages bidirectionnels (aucune diagonale implicite) :

```text
Entrée (entree) — Ossuaire (ossuaire) — Arsenal (arsenal)
                      |                    |
                 Forge (forge) — Sanctuaire (repos) — Bibliothèque (bibliotheque)
                                                        |
                    Trésor (tresor) — Antichambre (antichambre) — Gardien (boss)
```

Passages exacts : entree–ossuaire, ossuaire–arsenal, ossuaire–forge, arsenal–repos, forge–repos, repos–bibliotheque, bibliotheque–antichambre, antichambre–tresor, antichambre–boss.

L'ossuaire contient un squelette, la forge un golem, la bibliothèque un mage et l'antichambre un golem. Entrer dans une salle avec un ennemi vivant démarre le combat ; impossible d'en sortir avant sa mort. Revisiter une salle ne ressuscite rien. Seules les salles reliées à la salle actuelle sont accessibles. La carte entière et les passages sont visibles.

L'arsenal donne une épée d'acier (+5) et deux potions ; le trésor donne une lame runique (+9) et une potion. Chaque coffre nécessite l'action Ouvrir et ne fonctionne qu'une fois. Les armes restent dans l'inventaire ; équiper remplace le bonus, sans cumul. Changer d'arme est réservé à l'exploration ; équiper une arme déjà équipée est invalide. Le sanctuaire restaure tous les PV une seule fois, sur demande et uniquement si le héros est blessé.

Parcours conseillé à vérifier par test : entrée → ossuaire (vaincre) → arsenal (ouvrir, équiper acier) → repos (garder le soin si possible) → bibliothèque (vaincre) → antichambre (alterner attaque lors des préparations et défense avant les frappes) → trésor (ouvrir, équiper runique) → antichambre → boss. Les détours vers la forge et le repos sont possibles. Boire avant d'être en danger ; la potion en combat donne aussi un tour à l'ennemi.

## Sauvegarde

La refonte 2D ne change pas ces règles. Marcher dans une salle ne consomme aucun tour. Une entrée hostile impose une courte présentation puis le combat, sans possibilité de contourner l'ennemi. La vue lit les événements d'une action déjà résolue et bloque les nouvelles commandes jusqu'à la fin de sa présentation. La position visuelle est réinitialisée à un point sûr au chargement ; elle ne fait pas partie du JSON.

La version 1 du JSON décrit les statistiques, l'inventaire, l'équipement, la salle actuelle, chaque salle et son ennemi (PV et nombre de tours ennemis déjà joués), les visites, coffres, repos et l'état global. Les actions et réponses ennemies sont atomiques : une sauvegarde intervient entre deux actions, lorsque la défense temporaire a expiré. Aucun état transitoire de défense n'est donc à enregistrer. Le journal est informatif et n'est pas conservé.
