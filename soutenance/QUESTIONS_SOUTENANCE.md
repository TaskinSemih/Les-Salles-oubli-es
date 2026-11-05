# Préparation aux questions du jury

Pour répéter à deux, couvrir la réponse, répondre en vingt secondes puis demander la relance. Les références de classes renvoient au package `fr.utbm.sallesoubliees` dans les sources ou les tests.

## 1. Pourquoi parler d’un graphe pour seulement neuf salles ?

**Question :** Pourquoi parler d’un graphe pour seulement neuf salles ?

**Réponse orale :** Les sommets sont les salles, les arêtes leurs passages. Depuis l’ossuaire, rejoindre la forge est légal, sauter directement au Gardien est refusé.

**Explication approfondie :** Donjon indexe les salles par identifiant. relier inscrit chaque voisin chez l’autre. Partie.peutDeplacer combine appartenance aux passages, partie en cours et absence de combat.

**Référence dans notre projet :** Donjon.relier, Partie.peutDeplacer, ExplorationTest.

**Relance possible :** Calculez-vous un plus court chemin ?

**Réponse à la relance :** Le jeu vérifie un voisin immédiat. Le trajet reste une décision du joueur.

## 2. La marche au clavier avance-t-elle le combat ?

**Question :** La marche au clavier avance-t-elle le combat ?

**Réponse orale :** Non. La scène déplace le sprite dans un plan local. Franchir un passage autorisé est une commande métier distincte.

**Explication approfondie :** PlanSalle décrit le repère visuel et les obstacles. Une entrée hostile déclenche le choix d’une intention. Un pas dans la salle ne doit modifier ni le tour ni le hasard.

**Référence dans notre projet :** PlanSalle, SceneJeu, InterfaceTest.raccourcisDeMarcheNeConsommentPasDeTour.

**Relance possible :** Comment le prouver ?

**Réponse à la relance :** Le test compare les instantanés métier avant et après les commandes de marche.

## 3. Où voyez-vous héritage et composition ?

**Question :** Où voyez-vous héritage et composition ?

**Réponse orale :** Un golem est un Ennemi, donc aussi un Personnage. Un héros possède un Inventaire. C’est une relation de possession, avec une responsabilité distincte.

**Explication approfondie :** Personnage regroupe les statistiques communes. Heros calcule l’attaque avec son arme équipée. La sous-classe Golem fournit son comportement tactique. Partie compose héros, donjon et moteur de combat.

**Référence dans notre projet :** Personnage, Heros.getAttaque, Golem, Partie.

**Relance possible :** Équiper deux fois l’acier double-t-il le bonus ?

**Réponse à la relance :** Le calcul lit une seule arme équipée. Il ne rajoute pas un bonus permanent à la statistique de base.

## 4. Expliquez l’expression e.decider(...) sur la diapositive.

**Question :** Expliquez l’expression e.decider(...) sur la diapositive.

**Réponse orale :** Le type déclaré de e est Ennemi. À l’exécution, Java appelle la méthode redéfinie par la classe effective du monstre.

**Explication approfondie :** CombatTactique construit un ContexteCombat avec Élan, trois dernières commandes au maximum, réponse précédente et soin utilisé. Chaque ennemi applique ses propres règles et peut demander un tirage à la source injectée.

**Référence dans notre projet :** CombatTactique.selectionner, Ennemi.decider, Golem.decider.

**Relance possible :** Tout est-il polymorphe ?

**Réponse à la relance :** Non. La victoire distingue le Boss et le moteur connaît ses phases. Ajouter un nouveau boss demande de revoir ces points.

## 5. Quelle différence entre l’ancien résolveur et le combat joué ?

**Question :** Quelle différence entre l’ancien résolveur et le combat joué ?

**Réponse orale :** Le trajet courant est Partie.resoudre puis CombatTactique.resoudre. GestionnaireCombat fournit notamment le calcul commun des dégâts.

**Explication approfondie :** Les anciennes méthodes d’intention simple et le résolveur historique existent encore. Pour expliquer l’écran actuel, il faut suivre DecisionEnnemi et les événements produits par le moteur tactique.

**Référence dans notre projet :** Partie.resoudre, CombatTactique.degats, GestionnaireCombat, Golem.getIntention.

**Relance possible :** Pourquoi cette distinction importe-t-elle ?

**Réponse à la relance :** L’ancien golem alterne selon un compteur. Le golem tactique combine tirages, priorités et interruptions.

## 6. Que deviennent les PV au premier échange ?

**Question :** Que deviennent les PV au premier échange ?

**Réponse orale :** Avec l’arme initiale, le héros inflige onze dégâts. Le squelette survivant en inflige six. Leurs PV finaux sont dix-neuf et quatre-vingt-quatorze.

**Explication approfondie :** La formule retire l’armure à la puissance, avec un minimum de dégâts pour un impact. Personnage borne les PV. Le fait DEGATS enregistre la perte réelle, même si la cible avait moins de vie que les dégâts calculés.

**Référence dans notre projet :** Heros, Squelette, GestionnaireCombat.calculerDegats, CombatTactique.degats.

**Relance possible :** Si le squelette avait un seul PV ?

**Réponse à la relance :** La perte enregistrée serait un. Il mourrait avant de répondre.

## 7. Pourquoi vérifier l’action si le bouton est déjà désactivé ?

**Question :** Pourquoi vérifier l’action si le bouton est déjà désactivé ?

**Réponse orale :** Le bouton guide le joueur. L’autorité reste le modèle, qui peut aussi être appelé par un test ou par une autre interface.

**Explication approfondie :** Partie capture avant validation. Pour une commande refusée, ResultatAction contient valide=false, un motif, deux références au même instantané et une liste vide. La résolution tactique ne démarre pas.

**Référence dans notre projet :** Partie.resoudre, TactiqueTest.coutRefuseSansConsommerHasardPuisFrappeBriseLaGarde.

**Relance possible :** Un refus consomme-t-il un tirage ?

**Réponse à la relance :** Non. Le test compare les instantanés complets, dont l’état du générateur.

## 8. Comment évitez-vous de retirer deux fois les dégâts pendant l’animation ?

**Question :** Comment évitez-vous de retirer deux fois les dégâts pendant l’animation ?

**Réponse orale :** Le moteur résout d’abord le tour. Le lecteur anime des valeurs d’affichage à partir des événements, puis rejoint l’état final.

**Explication approfondie :** ResultatAction sépare instantanés et faits ordonnés. L’animation peut ralentir ou se terminer immédiatement sans rappeler la résolution. Les commandes de la vue sont bloquées pendant la séquence.

**Référence dans notre projet :** ResultatAction, ControleurAnimation, EvenementsTest.lecteurAnimeLaCopieEtNeRejoueJamaisLeModele.

**Relance possible :** Que vérifie-t-on à la dernière image ?

**Réponse à la relance :** Les valeurs visuelles stabilisées correspondent à l’instantané final, alors que le modèle a déjà atteint cet état.

## 9. Une frappe puissante interrompt-elle le golem ou annule-t-elle une riposte ?

**Question :** Une frappe puissante interrompt-elle le golem ou annule-t-elle une riposte ?

**Réponse orale :** L’interruption d’une préparation demande l’attaque rapide. La puissante coûte deux points d’Élan et traverse la protection temporaire, mais un ennemi survivant peut riposter.

**Explication approfondie :** Dans resoudre, interrompu et riposte dépendent de l’action et de la décision déjà sélectionnée. Briser la protection modifie le calcul d’armure. Cela ne supprime pas la décision RIPOSTE.

**Référence dans notre projet :** CombatTactique.resoudre, TactiqueTest.

**Relance possible :** Que gagne-t-on avec Observer devant une garde ?

**Réponse à la relance :** On ne frappe pas. La riposte ne touche pas le héros, l’adversaire devient vulnérable et le héros obtient Lucide.

## 10. La défense protège-t-elle encore après une préparation sans dégâts ?

**Question :** La défense protège-t-elle encore après une préparation sans dégâts ?

**Réponse orale :** La défense appartient à l’action du tour. Son bonus ne persiste pas jusqu’au prochain coup.

**Explication approfondie :** frapper calcule la protection à partir de l’action courante : huit points pour une défense ordinaire, quatre contre une onde. L’Élan de défense dépend d’une réduction effective selon le calcul du moteur.

**Référence dans notre projet :** CombatTactique.frapper, SauvegardeTest.repriseApresDefenseNeConservePasDeBonusTemporaire.

**Relance possible :** Que conserve-t-on à la place ?

**Réponse à la relance :** Les altérations qui durent plusieurs actions font partie de l’état tactique enregistré.

## 11. Quand les altérations expirent-elles ?

**Question :** Quand les altérations expirent-elles ?

**Réponse orale :** Le moteur applique le geste du héros, fait vieillir les effets présents, puis traite les nouveaux effets de la réponse ennemie.

**Explication approfondie :** vieillir parcourt une copie, réinsère les effets de durée restante positive et émet les expirations. appliquer remplace l’effet du même type sur la même cible. Un soin ou une observation peut donc aussi faire passer du temps.

**Référence dans notre projet :** CombatTactique.vieillir, appliquer et resoudre, EffetCombat.

**Relance possible :** Observer perd-il immédiatement une unité de Lucide ?

**Réponse à la relance :** Lucide est appliqué après le vieillissement, avec une durée de deux.

## 12. Le Gardien frappe-t-il plus fort dès qu’on lui fait franchir 45 PV ?

**Question :** Le Gardien frappe-t-il plus fort dès qu’on lui fait franchir 45 PV ?

**Réponse orale :** La phase change, mais le coup annoncé garde sa puissance. Les choix suivants utilisent la nouvelle phase.

**Explication approfondie :** La résolution garde la DecisionEnnemi choisie avant le geste du héros. Le moteur constate le changement de phase et en émet le fait, puis joue la décision conservée. Une nouvelle intention est choisie si le combat continue.

**Référence dans notre projet :** Boss.decider, CombatTactique.resoudre, DecisionEnnemi.

**Relance possible :** Quels sont les seuils ?

**Réponse à la relance :** Phase un au-dessus de 45 PV, phase deux de 23 à 45, phase trois à 22 ou moins.

## 13. Votre IA apprend-elle la façon de jouer ?

**Question :** Votre IA apprend-elle la façon de jouer ?

**Réponse orale :** Elle applique des règles explicites. Le Gardien peut réagir à la répétition des trois dernières commandes. Le mage tient compte de l’Élan ou des potions récentes.

**Explication approfondie :** L’historique est borné et les seuils des tirages sont écrits dans les sous-classes. Les décisions peuvent varier sans entraînement ni modification des règles.

**Référence dans notre projet :** ContexteCombat, Boss.decider, Mage.decider.

**Relance possible :** Comment peut-on la tester sans tests intermittents ?

**Réponse à la relance :** On injecte une source de hasard et on fixe la graine ou le contexte pour atteindre la branche voulue.

## 14. Pourquoi enregistrer l’état du hasard et l’intention ?

**Question :** Pourquoi enregistrer l’état du hasard et l’intention ?

**Réponse orale :** L’intention est déjà choisie ; le générateur détermine les tirages à venir. Les deux sont nécessaires pour reprendre le même tour et la même suite.

**Explication approfondie :** HasardReproductible conserve un état sur 48 bits. Chaque tirage le fait avancer. Repartir de la graine initiale redémarrerait la séquence au lieu de reprendre à l’état courant.

**Référence dans notre projet :** SourceAleatoire, HasardReproductible.prochainEntier, EtatTactique, CombatTactique.restaurer.

**Relance possible :** Les dégâts tirent-ils aussi au hasard ?

**Réponse à la relance :** La puissance et l’armure déterminent les dégâts. Les choix ennemis et certains pièges utilisent le générateur.

## 15. Un record est-il automatiquement immuable en profondeur ?

**Question :** Un record est-il automatiquement immuable en profondeur ?

**Réponse orale :** Ses composants ne peuvent pas être réaffectés, mais une liste passée sans précaution pourrait encore être modifiée par son propriétaire.

**Explication approfondie :** Les instantanés réalisent des copies défensives des collections et contiennent des valeurs, par exemple des identifiants et des records. Les objets vivants du donjon restent séparés de la capture.

**Référence dans notre projet :** EtatSauvegarde, EtatTactique, ResultatAction, SauvegardeTest.allerRetourNouvellePartieEtInstantaneIndependant.

**Relance possible :** Une collection en lecture seule est-elle forcément une copie figée ?

**Réponse à la relance :** Non. Une vue non modifiable peut refléter les changements internes. Un instantané indépendant doit aussi isoler son contenu dans le temps.

## 16. Pourquoi ne pas sérialiser directement toute la fenêtre ?

**Question :** Pourquoi ne pas sérialiser directement toute la fenêtre ?

**Réponse orale :** Le format décrit l’état nécessaire au jeu. Les composants Swing, images et minuteries se reconstruisent à partir du programme.

**Explication approfondie :** La version deux contient notamment les salles, l’inventaire et la tactique. Le journal et la position graphique locale sont omis ; la reprise place le héros au point sûr de la salle. Le fichier est traité entre séquences.

**Référence dans notre projet :** SauvegardePartie.capturer, EtatSauvegarde, FenetreJeu, PlanSalle.

**Relance possible :** Peut-on ouvrir une version un ?

**Réponse à la relance :** Le chargeur la refuse explicitement. Une migration demanderait de définir les données tactiques manquantes.

## 17. Un JSON bien écrit peut-il être refusé ?

**Question :** Un JSON bien écrit peut-il être refusé ?

**Réponse orale :** Oui. La syntaxe peut être correcte alors que les données contredisent les règles, par exemple un golem blessé sans aucun tour enregistré.

**Explication approfondie :** Jackson contrôle la forme et les types, puis restaurer vérifie les relations métier avant de rendre une nouvelle Partie. Les champs supplémentaires, clés dupliquées et octets au-delà de la limite sont aussi refusés.

**Référence dans notre projet :** GestionnaireSauvegarde.charger, SauvegardePartie.restaurer, SauvegardeTest.golemBlesseSansTourEstRefuse.

**Relance possible :** Détectez-vous toute triche ?

**Réponse à la relance :** Non. Ces contrôles ne rejouent pas l’historique. Une modification qui respecte les invariants peut être acceptée.

## 18. Pourquoi écrire dans un fichier temporaire ?

**Question :** Pourquoi écrire dans un fichier temporaire ?

**Réponse orale :** On prépare le contenu complet dans le même dossier avant de remplacer la cible. Une erreur pendant la préparation laisse l’ancien fichier disponible.

**Explication approfondie :** Le service valide la capture, écrit le JSON, tente ATOMIC_MOVE puis se replie sur un remplacement ordinaire si le système ne le permet pas. finally tente d’effacer le temporaire.

**Référence dans notre projet :** GestionnaireSauvegarde.sauvegarder, SauvegardeTest.echecEcritureConserveAncienFichierEtNettoieTemporaire.

**Relance possible :** Garantissez-vous la résistance à toute coupure ?

**Réponse à la relance :** Non. Le remplacement ordinaire n’offre pas la même atomicité et le nettoyage reste une tentative.

## 19. Quel fil a le droit de modifier la fenêtre ?

**Question :** Quel fil a le droit de modifier la fenêtre ?

**Réponse orale :** Les composants Swing sont gérés sur l’Event Dispatch Thread. L’entrée-sortie potentiellement lente est confiée à un SwingWorker.

**Explication approfondie :** Pour sauvegarder, on capture l’état sur l’EDT avant l’écriture. Pour charger, le fil de fond reconstruit une nouvelle partie. done revient sur l’EDT, où la vue traite le succès ou l’erreur.

**Référence dans notre projet :** Application, FenetreJeu, ControleurJeu, InterfaceTest.dialoguesSauvegardentEtReprennentLeCombat.

**Relance possible :** Si le fichier est illisible, la partie en cours est-elle perdue ?

**Réponse à la relance :** Non. Le contrôleur n’adopte la partie reconstruite qu’après réussite ; l’erreur est présentée au joueur.

## 20. Comment empêchez-vous les récompenses infinies ?

**Question :** Comment empêchez-vous les récompenses infinies ?

**Réponse orale :** Chaque salle conserve l’état du coffre ou du repos. L’ennemi vaincu reste mort et le choix facultatif est mémorisé séparément.

**Explication approfondie :** Les droits d’action sont vérifiés avant de modifier l’inventaire. Le sanctuaire ne se consume pas si le héros a déjà toute sa vie. Un événement ignoré est aussi marqué comme résolu.

**Référence dans notre projet :** Partie.ouvrirCoffre, seReposer, CombatTactique.choisir, ExplorationTest.

**Relance possible :** Un piège peut-il terminer la partie à un PV ?

**Réponse à la relance :** Le moteur borne la perte à min(12, vie − 1). Le joueur garde au moins un PV.

## 21. Deux cents parcours, est-ce deux cents tests JUnit ?

**Question :** Deux cents parcours, est-ce deux cents tests JUnit ?

**Réponse orale :** Non. Une méthode de test boucle sur cent graines et deux routes et vérifie l’état victoire à chaque parcours.

**Explication approfondie :** Le joueur de test passe par les commandes ordinaires. Sa politique lit cependant la décision exacte, ce qui peut lui donner davantage d’information qu’à un débutant. Les tests de fichiers et d’interface couvrent d’autres risques.

**Référence dans notre projet :** TactiqueTest.centGrainesParcoursDirectEtCompletGagnants, TactiqueTest.action.

**Relance possible :** Est-ce une preuve d’équilibrage pour tout le monde ?

**Réponse à la relance :** Cela prouve la victoire pour ces graines, ces routes et cette politique. Le rythme, la difficulté perçue et la compréhension réclament des essais joueurs.

## 22. Comment ajouteriez-vous un adversaire ?

**Question :** Comment ajouteriez-vous un adversaire ?

**Réponse orale :** Je créerais une sous-classe d’Ennemi, définirais decider et placerais cet adversaire dans une salle. Ensuite je vérifierais la restauration et le rendu.

**Explication approfondie :** Le contrat commun réutilise le moteur. Un nouveau type affecte aussi le catalogue, les décisions autorisées au chargement, le choix de sprite, les règles et l’UML. Une réponse inédite peut nécessiter une évolution du résolveur.

**Référence dans notre projet :** Ennemi, Donjon, DecisionAutorisee, GestionnaireRessources, docs/UML.md.

**Relance possible :** Quel test écririez-vous en premier ?

**Réponse à la relance :** Un tour avec un contexte fixé : vérifier la décision, son impact et la reprise identique après sauvegarde.

## 23. Qu’amélioreriez-vous en priorité ?

**Question :** Qu’amélioreriez-vous en priorité ?

**Réponse orale :** Je commencerais par observer un débutant devant le golem et le mage. Nous pourrions mesurer la durée et écouter comment il interprète l’intention affichée.

**Explication approfondie :** Distinguer une hypothèse de conception et une mesure : les preuves automatiques portent sur la correction des règles. L’expérience proposée mesure la compréhension sans donner la réponse à l’avance.

**Référence dans notre projet :** docs/CAHIER_DES_CHARGES.md, docs/TESTS_MANUELS.md, DecisionEnnemi.decrire.

**Relance possible :** Quel signe vous ferait modifier l’interface ?

**Réponse à la relance :** Si plusieurs joueurs confondent puissance annoncée et perte réelle, je testerais un libellé distinguant puissance et armure, puis je répéterais l’observation.

## 24. Qui a produit les graphismes et comment avez-vous réparti le travail ?

**Question :** Qui a produit les graphismes et comment avez-vous réparti le travail ?

**Réponse orale :** Les trois atlas sont documentés comme générés avec assistance. Les décors et effets sont dessinés dans le jeu avec Java2D. Chacun doit citer sa propre contribution vérifiable.

**Explication approfondie :** La répartition du dépôt donne des rôles possibles pour trois ou quatre personnes. Pour une réponse personnelle, préparer une modification réellement effectuée, sa référence et le test ou la relecture associé.

**Référence dans notre projet :** docs/ASSETS.md, docs/prompts/, docs/REPARTITION.md, RenduSalle, RenduCombat.

**Relance possible :** Quelle contribution pouvez-vous expliquer ligne par ligne ?

**Réponse à la relance :** À personnaliser avant la soutenance : choisir une méthode réellement travaillée, nommer son entrée, son invariant et le test qui les exerce. Les documents ne permettent pas de donner un nom à la place du groupe.
