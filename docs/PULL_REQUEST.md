# Combats tactiques et décisions ennemies reproductibles

Base : `feat/visual-rpg`. Branche : `feat/tactical-combat`.

Les combats proposent désormais Élan (0–3), frappe puissante et observation en complément des actions existantes. Les ennemis annoncent une décision choisie par leur stratégie : garde/riposte du squelette, préparation interruptible du golem, malédiction et soin unique du mage, trois phases et adaptation aux répétitions pour le Gardien. Altérations, coûts, réactions et phases apparaissent dans la scène et dans des événements structurés.

La source aléatoire est injectable ; son état, l'intention, l'historique et les effets sont conservés dans le JSON version 2. Les sauvegardes version 1 sont refusées explicitement. Les découvertes de l'arsenal, de la bibliothèque et du trésor ajoutent des choix facultatifs et uniques avec des risques annoncés.

Validation : 67 tests avec Swing, 200 parcours gagnants sur 100 graines, reprise JSON, fichiers invalides, double clic, pause et fermeture ; JAR et Javadoc stricte. Captures réelles inspectées, documentation et UML mis à jour.

Limites : essais utilisateurs et équilibrage ressenti à poursuivre ; aucune garantie sur toutes les graines ou toutes les politiques de jeu ; bureau limité pour le vrai 1920×1080 et essais DPI à compléter. Le PNG de conception antérieur illustre le socle visuel ; le nouveau diagramme tactique est dans la documentation Mermaid.

Aucune fusion automatique.
