# Schéma de conception POO — Les Salles oubliées

Ce document présente la conception générale du jeu sous une forme volontairement simple. Les attributs et méthodes secondaires sont omis afin de faire apparaître les responsabilités et les relations entre objets.

## Diagramme principal

```mermaid
classDiagram
    direction TB

    class FenetreJeu {
        <<Vue Swing>>
        +afficher les menus et commandes
        +transmettre les actions du joueur
    }

    class SceneJeu {
        <<Vue Swing>>
        +dessiner la salle et les personnages
        +animer les déplacements et combats
    }

    class ControleurJeu {
        <<Contrôleur>>
        -Partie partie
        +nouvellePartie()
        +resoudre(ActionCombat)
        +deplacer(String)
        +equiper(Arme)
    }

    class Partie {
        <<Modèle>>
        -Heros heros
        -Donjon donjon
        -Salle salleActuelle
        -EtatPartie etat
        +resoudre(ActionCombat)
        +deplacer(String)
        +ouvrirCoffre()
        +seReposer()
    }

    class Donjon {
        -Map salles
        +getSalle(String)
    }

    class Salle {
        -String id
        -Set passages
        -Ennemi ennemi
        -Arme coffre
        +estHostile()
    }

    class Personnage {
        <<classe abstraite>>
        -String nom
        -int vie
        -int attaque
        -int armure
        +estMort()
    }

    class Heros {
        -Inventaire inventaire
        +getAttaque()
    }

    class Ennemi {
        <<classe abstraite>>
        -int tours
        +getIntention()
        +puissanceProchaineAttaque()
    }

    class Squelette
    class Golem
    class Mage
    class Boss

    class Inventaire {
        -Set armes
        -Arme equipee
        -int potions
        +equiper(Arme)
        +consommerPotion()
    }

    class GestionnaireCombat {
        <<service métier>>
        +attaquer(Heros, Ennemi)
        +repondre(Heros, Ennemi, defense)
    }

    class ResultatAction {
        <<record immuable>>
        +EtatSauvegarde avant
        +EtatSauvegarde apres
        +List evenements
    }

    class GestionnaireSauvegarde {
        <<Persistance JSON>>
        +sauvegarder(Path, EtatSauvegarde)
        +charger(Path) Partie
    }

    FenetreJeu *-- SceneJeu : contient
    FenetreJeu *-- ControleurJeu : contient
    FenetreJeu *-- GestionnaireSauvegarde : contient
    ControleurJeu --> Partie : commande

    Partie "1" *-- "1" Heros : possède
    Partie "1" *-- "1" Donjon : possède
    Partie --> "1" Salle : salle actuelle
    Donjon "1" *-- "9" Salle : contient
    Salle --> "0..1" Ennemi : rencontre

    Personnage <|-- Heros
    Personnage <|-- Ennemi
    Ennemi <|-- Squelette
    Ennemi <|-- Golem
    Ennemi <|-- Mage
    Ennemi <|-- Boss
    Heros "1" *-- "1" Inventaire : possède

    Partie ..> GestionnaireCombat : utilise
    Partie ..> ResultatAction : produit
    SceneJeu ..> ResultatAction : anime
    GestionnaireSauvegarde ..> Partie : restaure
```

## Comment lire le schéma

| Symbole | Signification | Exemple dans le projet |
| --- | --- | --- |
| `*--` | Composition : l'objet fait partie d'un autre | Une `Partie` possède un `Heros` et un `Donjon`. |
| `<|--` | Héritage : une classe spécialisée hérite d'une classe générale | `Heros` et `Ennemi` héritent de `Personnage`. |
| `-->` | Association : un objet connaît ou commande un autre objet | Le contrôleur commande la `Partie`. |
| `..>` | Dépendance : une classe utilise ponctuellement une autre | `Partie` utilise `GestionnaireCombat`. |
| `1`, `9`, `0..1` | Cardinalités | Un donjon contient exactement neuf salles ; une salle a zéro ou un ennemi. |

## Répartition des responsabilités

```mermaid
flowchart LR
    J[Joueur] --> V[Vue<br/>FenetreJeu et SceneJeu]
    V --> C[Contrôleur<br/>ControleurJeu]
    C --> M[Modèle<br/>Partie et objets métier]
    M --> R[Résultat immuable<br/>événements + états avant/après]
    R --> V
    V --> P[Persistance<br/>GestionnaireSauvegarde]
    P --> F[(Fichier JSON)]
```

- **Vue** : affiche le donjon, reçoit les clics et anime les résultats. Elle ne calcule pas les dégâts.
- **Contrôleur** : fait le lien entre l'interface et la partie courante.
- **Modèle** : contient toutes les règles, les personnages, les salles, le combat et les invariants.
- **Persistance** : transforme un instantané validé en JSON et reconstruit une partie valide.

## Exemple : une attaque

```mermaid
sequenceDiagram
    actor Joueur
    participant Vue as FenetreJeu
    participant Controleur as ControleurJeu
    participant Modele as Partie
    participant Combat as GestionnaireCombat
    participant Scene as SceneJeu

    Joueur->>Vue: clique « Attaquer »
    Vue->>Controleur: resoudre(ATTAQUER)
    Controleur->>Modele: resoudre(ATTAQUER)
    Modele->>Combat: calculer attaque et réponse
    Combat-->>Modele: dégâts appliqués une seule fois
    Modele-->>Controleur: ResultatAction immuable
    Controleur-->>Vue: résultat
    Vue->>Scene: jouer les événements
    Note over Scene: animation seulement<br/>aucun nouveau dégât
```

L'idée importante est que le modèle résout le tour **une seule fois**. La scène lit ensuite les événements pour montrer l'attaque, les dégâts et la réponse ennemie sans rappeler les méthodes métier.

## Points POO à présenter à la prof

1. **Encapsulation** : les attributs sont privés et les collections exposées ne sont pas modifiables directement.
2. **Héritage** : `Personnage` factorise la vie, l'attaque et l'armure ; `Ennemi` ajoute le comportement commun des adversaires.
3. **Polymorphisme** : squelette, golem, mage et boss répondent différemment grâce aux méthodes redéfinies de `Ennemi`.
4. **Composition** : une partie est composée d'un héros et d'un donjon ; le donjon est composé de neuf salles.
5. **Séparation des responsabilités** : le modèle ne dépend ni de Swing ni de Jackson. La vue affiche, le contrôleur coordonne et la persistance gère le JSON.
6. **Immutabilité** : `ResultatAction`, `EvenementJeu` et les instantanés de sauvegarde sont des records protégés contre les modifications accidentelles.

Pour une étude exhaustive avec tous les attributs, les records de sauvegarde et les classes de rendu, consulter [UML.md](UML.md).
