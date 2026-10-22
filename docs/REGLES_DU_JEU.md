# Règles du jeu — version 2 tactique

Le héros commence à 100 PV, attaque 12, armure 3, trois potions (+35 PV plafonnés) et une épée rouillée. Acier : +5 attaque ; runique : +9. Les PV sont bornés et les actions invalides ne consomment ni tour ni hasard.

| Action | Coût et effet |
| --- | --- |
| 1 Attaquer (rapide) | Dégâts max(1, attaque − armure), +1 Élan ; interrompt une préparation fragile. |
| 2 Défendre | +8 armure pour cette réponse seulement (+4 contre une onde). +1 Élan si la protection réduit effectivement l'impact. |
| 3 Potion | Consomme une potion, soin réel jusqu'à +35 ; réponse ennemie en combat. |
| 4 Puissante | Coûte 2 Élan, +10 puissance ; ignore l'armure temporaire et la garde. Une riposte reste dangereuse si l'ennemi survit. |
| 5 Observer | Consomme le tour ; LUCIDE pendant les deux intentions suivantes. N'attaque pas une garde, donc évite sa riposte et crée une ouverture. |

Élan : 0 à 3, remis à zéro à chaque entrée et fin de combat. Une action valide est résolue entièrement une fois avant son animation. Un ennemi tué ne répond pas et ne reçoit pas de nouvelle décision.

## Identités ennemies et choix

PV / attaque de base / armure : squelette 30/9/1, golem 48/14/4, mage 36/11/1, Gardien 90/16/3.

- **Squelette** : les deux premières réponses sont des attaques 9 pour apprendre. Ensuite 40 % de garde/riposte et 60 % d'attaque ; après une garde, attaque obligatoire. Sa garde ajoute 6 armure ; riposte uniquement si le héros attaque. Une riposte évitée ouvre une vulnérabilité.
- **Golem** : 55 % préparation fragile, 25 % armure renforcée, 20 % onde 16. Préparation laissée intacte → frappe 22 ; rapide → interruption et vulnérabilité. Après frappe lourde/onde : récupération obligatoire.
- **Mage** : projectile 11 ignorant l'armure permanente, malédiction, armure ou soin unique (+10 sous 15 PV). Après une capacité défensive, de soin ou de malédiction, projectile obligatoire. Malédiction : 25 % habituellement, 60 % si Élan ≥2 ou deux potions dans les trois actions récentes ; les autres poids résultent des seuils 75/100. Une intention d'attaque/malédiction est vague sauf avec LUCIDE.
- **Gardien** : phase 1 >45 PV, phase 2 de 23 à 45, phase 3 ≤22. Frappe normale : 15/18/21 ; riposte : 16/18/20 ; onde (phases 2/3) : 20/23 ; frappe lourde préparée : 26 (phases 1/2), 30 en phase finale. Tirage : attaque 35 %, préparation 20 %, garde 20 %, marque (phase 1) ou onde 25 %. Trois mêmes commandes récentes portent la probabilité de riposte à 65 %. Pas deux ripostes consécutives ; récupération après frappe lourde ou onde.

L'ennemi choisit **avant** l'action du héros. Une décision annoncée conserve sa puissance même si le coup du joueur fait franchir un seuil ; la nouvelle phase influence les décisions suivantes. Le héros peut donc faire confiance à l'intention annoncée.

## Altérations

- GARDE : liée à l'intention de riposte, +6 armure.
- ARMURE_RENFORCEE : +6 armure pendant deux attaques/actions du héros, brisée par Puissante.
- VULNERABLE : prochain coup du héros +6 puissance, consommé au coup ou expiré après une action.
- AFFAIBLI : attaque du héros −4 pendant deux actions.
- LUCIDE : intentions exactes pendant deux choix ; Observer le rafraîchit.
- MARQUE : prochain impact ennemi +4 puissance ; expire après trois actions sans impact.

Les effets anciens vieillissent après l'action du héros, avant l'application des nouveaux effets de la réponse ennemie. Les effets ne se cumulent pas avec eux-mêmes. Une frappe puissante brise la protection mais n'annule pas une riposte. Toutes les ressources et altérations temporaires sont effacées à la fin du combat.

## Exploration et choix

Les neuf salles et leurs neuf passages restent identiques : entrée–ossuaire, ossuaire–arsenal, ossuaire–forge, arsenal–repos, forge–repos, repos–bibliothèque, bibliothèque–antichambre, antichambre–trésor, antichambre–Gardien. Une entrée hostile impose le combat ; marcher dans une salle ne consomme aucun tour. Un ennemi ordinaire vaincu donne une potion une seule fois ; le Gardien mort termine la partie.

L'arsenal donne acier et deux potions, le trésor donne runique et une potion. Coffres et repos complet sont utilisables une seule fois ; le repos n'est pas consommé à pleine vie. Changement d'arme hors combat uniquement.

F / Découverte propose des choix facultatifs, résolus une seule fois, indépendamment du coffre :
- Arsenal et trésor : +1 potion sûre, +2 potions avec 40 % de piège, ou ignorer définitivement.
- Bibliothèque après le mage : rune stable +15 PV, rune interdite +35 PV avec 40 % de brûlure après le soin, ou ignorer.
- Piège/brûlure : perte d'au plus 12 PV, laisse toujours au moins 1 PV. Les soins sont plafonnés à 100. L'option risquée consomme un tirage ; les autres aucun.

## Hasard, sauvegarde et limites

Le générateur sur 48 bits est injectable et sauvegardé. La production utilise une graine initiale variable ; les tests utilisent des graines fixes et des situations contrôlées. Les dégâts ne varient pas au hasard. La même sauvegarde et les mêmes commandes produisent les mêmes décisions futures.

Le JSON **version 2** conserve statistiques, inventaire, état des salles, Élan, intention sélectionnée, trois commandes récentes, dernière réponse, soin ennemi utilisé, effets et choix résolus. Les versions 1 sont refusées avec un message français : aucune migration silencieuse. Chargement validé avant publication, fichiers uniquement entre deux séquences. Position graphique et journal non sauvegardés ; reprise au point sûr (425, 370).

L'équilibrage automatique vérifie 200 parcours tactiques (100 graines, route directe et neuf salles). Cela prouve ces parcours et cette politique de jeu, sans garantir toutes les graines ni une durée de 10–15 minutes. Des essais avec joueurs restent nécessaires.
