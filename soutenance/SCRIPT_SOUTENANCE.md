# Texte à prononcer

1351 mots de discours. À 141 mots/minute, avec environ deux secondes par diapositive pour les gestes et les relais, la cible est **9:51**. Les repères sont cumulés. Ne pas lire les indications en italique. À trois voix, répartir 1–3 / 4–5 / 6–8 ; à quatre, deux diapositives par voix. Ces relais sont des propositions de parole, sans attribution de contributions.

## 1. Les Salles oubliées · 0:00–0:54 (54 s)

*Objectif : Ouvrir sur le choix du joueur face au squelette. Geste : Titre et Gardien issu de l’atlas du jeu. Laisser apparaître le personnage. Les animations partent à l’entrée.*

Bonjour. Dans notre jeu, on entre dans l’ossuaire avec cent points de vie. Un squelette nous attend. L’écran indique qu’il va attaquer avec une puissance de neuf. On peut frapper, se défendre ou prendre le temps d’observer. Avant même le premier clic, il y a donc une promesse faite au joueur : l’information qu’on lui donne doit rester vraie quand il agit.

C’est par cette situation que nous allons présenter Les Salles oubliées, notre projet de programmation orientée objet en Java. Il fallait construire un jeu de donjon local, jouable jusqu’à la victoire ou à la défaite, et capable de reprendre une partie enregistrée. Nous allons suivre ce premier choix depuis l’écran jusqu’au fichier de sauvegarde. Commençons par situer l’ossuaire dans la partie.

*→ Passer à « Neuf salles, neuf passages ».*

## 2. Neuf salles, neuf passages · 0:54–2:08 (74 s)

*Objectif : Expliquer la progression et le rôle de la composition. Geste : Suivre le chemin doré sur la carte, puis montrer la boucle par la forge. Les animations partent à l’entrée.*

On commence à gauche de la carte, à l’entrée. Le premier passage mène à l’ossuaire. Tant que le squelette y est vivant, les sorties sont bloquées. Une fois le combat gagné, on peut prendre la branche de l’arsenal. Le coffre donne l’épée d’acier et deux potions. Il faut ensuite équiper l’épée pour bénéficier de son bonus. Si on revient dans cette salle, le coffre est déjà ouvert.

Cette carte se lit aussi comme un morceau de notre modèle objet. Le donjon contient des salles. Chaque salle connaît les identifiants de ses voisines et peut contenir un ennemi. Pour accepter un déplacement, la partie vérifie notamment que la destination appartient aux passages de la salle actuelle. Marcher de quelques pixels à l’intérieur d’une salle est une opération distincte, qui ne consomme aucun tour.

Sur la carte, la forge offre un détour. Les deux branches se rejoignent au sanctuaire, où un héros blessé peut se reposer une seule fois. Voyons maintenant ce qui se passe avant d’y arriver, dans le premier combat.

*→ Passer à « Le premier échange ».*

## 3. Le premier échange · 2:08–3:22 (74 s)

*Objectif : Lire une capture puis calculer les deux impacts. Geste : Montrer la ligne d’intention sur la capture. Appuyer sur T en commençant le calcul pour rejouer les deux impacts à côté de la capture. Les animations partent à l’entrée.*

Voici l’interface Swing, capturée pendant le test du jeu. Le héros est à gauche, le squelette à droite. En bas, on lit « attaque annoncée : neuf ». Le neuf est une puissance. Ce serait une erreur de le lire directement comme le nombre de points de vie que le héros va perdre.

Prenons l’attaque rapide avec l’équipement initial. Le héros a douze en attaque, le squelette un point d’armure. Le coup retire donc onze points au squelette, qui passe de trente à dix-neuf. Comme il survit, il répond. Ses neuf points de puissance rencontrent les trois points d’armure du héros. Il reste six dégâts, et le héros passe de cent à quatre-vingt-quatorze. L’attaque rapide lui a aussi donné un point d’Élan.

On peut maintenant expliquer pourquoi le bouton de frappe puissante est grisé au départ : il faut deux points d’Élan. Même si on appelait la commande directement dans le code, le modèle la refuserait. C’est ce trajet entre le bouton et la règle que nous allons regarder.

*→ Passer à « Qui modifie les PV ? ».*

## 4. Qui modifie les PV ? · 3:22–4:41 (79 s)

*Objectif : Distinguer commande, résolution et lecture animée. Geste : Pointer de gauche à droite, puis lire la branche du refus. Les animations partent à l’entrée.*

Le clic arrive dans la vue Swing. Le contrôleur transmet la commande à la partie, qui prend un instantané de l’état de départ. Si la commande est impossible, elle rend un résultat invalide. Avant et après désignent alors le même instantané, et la liste des événements est vide. Par exemple, demander une frappe puissante sans Élan ne donne pas un tour gratuit au squelette et ne fait pas avancer le hasard.

Pour une commande valide en combat, la partie délègue à CombatTactique. C’est là que les dégâts sont appliqués et que les faits du tour sont produits dans l’ordre. Si le premier coup tue l’adversaire, le moteur s’arrête avant sa réponse. La partie règle ensuite la récompense ou la fin du jeu.

Le résultat revient avec l’état de départ, les événements et l’état final. Le lecteur d’animation peut maintenant faire descendre la barre de vie sur plusieurs images. Les vrais points de vie ont déjà changé une seule fois. Cette séparation nous permet de vérifier un tour entier sans ouvrir de fenêtre. Reste à comprendre qui choisit la réponse de l’ennemi.

*→ Passer à « Le golem annonce son coup ».*

## 5. Le golem annonce son coup · 4:41–6:03 (82 s)

*Objectif : Faire entendre le polymorphisme à travers deux issues concrètes. Geste : Comparer la branche interrompue à la branche préparée. Lire l’appel Java à voix haute en français. Les animations partent à l’entrée.*

Imaginons maintenant un golem qui annonce une préparation. C’est précisément le moment où l’attaque rapide a un intérêt particulier. Elle interrompt la préparation et rend le golem vulnérable. Le prochain coup du héros peut profiter d’un bonus de six. Si, au contraire, on laisse le golem se préparer, son intention suivante est une frappe lourde de puissance vingt-deux. On la voit venir et on peut choisir de se défendre.

Dans le code projeté, la variable e est de type Ennemi. Le moteur appelle sa méthode decider. Java exécute la version qui correspond à l’objet présent dans la salle. Pour le golem, une préparation réussie impose la frappe lourde. Pour le squelette, les deux premières réponses sont des attaques simples. Le mage peut masquer la nature précise de son intention. Le Gardien, lui, peut réagir à trois commandes identiques dans l’historique récent.

Ces comportements combinent des priorités et des tirages. Une fois la décision choisie, sa puissance est conservée, même si notre coup fait changer le Gardien de phase. Cette promesse pose une question pratique : que doit-on garder quand on quitte le jeu à cet instant ?

*→ Passer à « Reprendre le même tour ».*

## 6. Reprendre le même tour · 6:03–7:28 (85 s)

*Objectif : Relier intention, générateur et validation du chargement. Geste : Suivre la chaîne capture → fichier → nouvelle partie ; désigner la sortie de rejet. Les animations partent à l’entrée.*

Si je sauvegarde face au golem et que je reviens demain, je veux retrouver la même menace. Enregistrer seulement mes points de vie et le nom de la salle serait insuffisant. Le fichier garde notamment l’intention déjà sélectionnée, l’Élan, les effets actifs et les trois commandes récentes qui peuvent influencer la suite. Il garde aussi l’état courant du générateur pseudo-aléatoire. Avec les mêmes commandes après la reprise, on retrouve donc les mêmes décisions futures.

Le fichier est du JSON en version deux. Jackson vérifie sa structure. Le modèle vérifie ensuite que les données peuvent former une partie cohérente. Par exemple, une arme provenant d’un coffre doit être compatible avec l’état de ce coffre. On construit une partie distincte avant de la proposer au contrôleur. Si la lecture échoue, le joueur garde la partie qu’il avait à l’écran.

Côté interface, la lecture du disque se fait en arrière-plan, puis Swing publie le résultat sur son fil d’affichage. La position exacte du personnage dans le décor et le journal restent des éléments de présentation. Au chargement, le héros revient à un point praticable de sa salle. Comment vérifier que tout cela fonctionne au-delà d’un seul exemple ?

*→ Passer à « Gagner par les commandes du jeu ».*

## 7. Gagner par les commandes du jeu · 7:28–8:47 (79 s)

*Objectif : Montrer exactement les preuves et leur portée. Geste : Lire la multiplication puis les décisions du joueur de test. Les animations partent à l’entrée.*

Notre test le plus parlant joue réellement jusqu’au Gardien. Il démarre une partie, fait gagner le combat de l’ossuaire, ouvre le coffre, équipe l’acier et continue en passant par les salles autorisées. À la fin, il vérifie l’état victoire. Il utilise les commandes du modèle, comme le ferait un joueur à travers l’interface.

Pour décider, le test suit une politique simple. À quarante-cinq points de vie ou moins, il essaie de boire s’il le peut. Face à une préparation, il attaque vite. Face à une onde ou à une frappe lourde, il se défend. Nous exécutons deux routes pour chacune des cent graines, de zéro à quatre-vingt-dix-neuf. Cela donne deux cents parcours gagnants, dont cent avec le détour par la forge.

Cette expérience permet de reproduire une régression avec sa graine. Elle montre que ces routes sont gagnables avec cette politique. Elle a aussi une limite intéressante : le test consulte l’action exacte de la décision ennemie, alors qu’un joueur peut voir une intention vague chez le mage. Il reste donc une question que ces victoires automatiques ne tranchent pas.

*→ Passer à « Ce qu’il reste à mesurer ».*

## 8. Ce qu’il reste à mesurer · 8:47–9:51 (64 s)

*Objectif : Conclure sur une preuve, puis sur une observation à mener avec un joueur. Geste : Montrer l’intention et la question finale ; terminer sans changer de diapositive. Les animations partent à l’entrée.*

La question, c’est de savoir si quelqu’un qui découvre le jeu comprend cette information à temps pour s’en servir. Pour le vérifier, on pourrait observer un premier combat sans donner de conseil, puis demander au joueur pourquoi il a choisi d’attaquer ou de défendre. On pourrait aussi chronométrer sa partie. Dix à quinze minutes est la durée visée pour le jeu ; nous la présentons comme un objectif à mesurer.

Pour conclure, revenons au squelette. On sait maintenant expliquer le neuf affiché, calculer les six points réellement perdus et dire quelle classe a le droit de les retirer. On sait aussi pourquoi recharger une partie conserve la menace qui avait été annoncée. C’est ce lien entre une règle visible pour le joueur, un comportement d’objet Java et une preuve que l’on peut reproduire qui résume notre projet. Merci, nous sommes prêts à répondre à vos questions.

*Fin. Garder cette diapositive à l’écran.*
