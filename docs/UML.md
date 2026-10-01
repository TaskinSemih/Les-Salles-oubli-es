# Diagrammes de classes

Ces diagrammes décrivent les classes du code Java. Pour rester lisibles, ils séparent le modèle de jeu, les données de sauvegarde et l'intégration Swing ; les classes répétées désignent les mêmes types. Les accesseurs et méthodes privées secondaires sont omis. `+` désigne une opération publique, `-` un attribut privé, `<|--` l'héritage, `*--` la composition et `..>` une dépendance d'utilisation.

## Modèle de jeu

Toutes ces classes appartiennent à `fr.utbm.sallesoubliees.modele`.

```mermaid
classDiagram
    direction TB
    class Partie {
        -Heros heros
        -Donjon donjon
        -Salle salleActuelle
        -EtatPartie etat
        +attaquer() String
        +defendre() String
        +boirePotion() String
        +deplacer(String id) String
        +equiper(Arme arme) String
        +ouvrirCoffre() String
        +seReposer() String
        +estEnCombat() boolean
    }
    class EtatPartie {
        <<enumeration>>
        EN_COURS
        VICTOIRE
        DEFAITE
    }
    class Donjon {
        -Map~String,Salle~ salles
        +getSalle(String id) Salle
        +getSalles() Collection~Salle~
    }
    class Salle {
        -String id
        -Set~String~ passages
        -boolean visitee
        -boolean utilisee
        -boolean repos
        +estHostile() boolean
    }
    class Personnage {
        <<abstract>>
        -String nom
        -int vie
        -int vieMax
        -int attaque
        -int armure
        +estMort() boolean
    }
    class Heros {
        +getAttaque() int
        +getInventaire() Inventaire
    }
    class Ennemi {
        <<abstract>>
        -int tours
        +getType() String
        +puissanceProchaineAttaque() int
        +getIntention() String
        +ignoreArmure() boolean
    }
    class Squelette
    class Golem
    class Mage
    class Boss
    class Inventaire {
        -Set~Arme~ armes
        -Arme equipee
        -int potions
        +getArmes() Set~Arme~
        +getEquipee() Arme
        +getPotions() int
    }
    class Arme {
        <<enumeration>>
        ROUILLEE
        ACIER
        RUNIQUE
        -String nom
        -int bonus
        +getBonus() int
    }
    class Potion {
        +int SOIN = 35$
    }
    class GestionnaireCombat {
        +int BONUS_DEFENSE = 8$
        +calculerDegats(int puissance, int armure) int$
        ~attaquer(Heros heros, Ennemi ennemi) String$
        ~repondre(Heros heros, Ennemi ennemi, boolean defense) String$
    }
    Partie "1" *-- "1" Heros
    Partie "1" *-- "1" Donjon
    Partie --> "1" Salle : salle actuelle
    Partie --> EtatPartie
    Partie ..> GestionnaireCombat
    Partie ..> Potion : soin
    Donjon "1" *-- "9" Salle
    Salle "1" *-- "0..1" Ennemi
    Salle --> "0..1" Arme : coffre
    Personnage <|-- Heros
    Personnage <|-- Ennemi
    Ennemi <|-- Squelette
    Ennemi <|-- Golem
    Ennemi <|-- Mage
    Ennemi <|-- Boss
    Heros "1" *-- "1" Inventaire
    Inventaire --> "1..3" Arme : armes possedees
    Inventaire --> "1" Arme : equipee
    GestionnaireCombat ..> Heros
    GestionnaireCombat ..> Ennemi
```

Les passages sont stockés comme identifiants de salles (`Set<String>`), pas comme une seconde collection d'objets `Salle`. La salle actuelle référence une salle du donjon. Les valeurs d'`Arme` sont partagées et immuables : ce sont des associations, pas des objets créés et détruits avec l'inventaire.

`Potion` contient uniquement la constante de soin ; les potions restantes sont un nombre dans `Inventaire`. Aucune classe `Objet` n'existe : les besoins actuels ne justifient pas une hiérarchie d'objets générique. Les classes utilitaires `Potion` et `GestionnaireCombat` ont un constructeur privé. Dans le diagramme, `$` indique un membre statique et `~` une méthode accessible uniquement dans le package.

## Instantané et persistance

`EtatSauvegarde` et `SauvegardePartie` sont dans `modele`. Les trois records `EtatHeros`, `EtatSalle` et `EtatEnnemi` sont déclarés à l'intérieur d'`EtatSauvegarde`. Seuls `GestionnaireSauvegarde` et `ExceptionSauvegarde` appartiennent à `persistance`.

```mermaid
classDiagram
    direction LR
    class SauvegardePartie {
        +int VERSION = 1$
        +capturer(Partie partie) EtatSauvegarde$
        +restaurer(EtatSauvegarde donnees) Partie$
    }
    class EtatSauvegarde {
        <<record>>
        int version
        String salleActuelle
        EtatPartie etat
    }
    class EtatHeros {
        <<record imbrique>>
        int vie
        int vieMax
        int attaque
        int armure
        List~Arme~ armes
        Arme equipee
        int potions
    }
    class EtatSalle {
        <<record imbrique>>
        String id
        boolean visitee
        boolean utilisee
    }
    class EtatEnnemi {
        <<record imbrique>>
        String type
        int vie
        int tours
    }
    class GestionnaireSauvegarde {
        -ObjectMapper mapper
        +sauvegarder(Path fichier, EtatSauvegarde etat) void
        +charger(Path fichier) Partie
    }
    class ExceptionSauvegarde
    class Exception {
        <<JDK>>
    }
    class ObjectMapper {
        <<Jackson>>
    }
    class Partie
    SauvegardePartie ..> Partie : capture et reconstruit
    SauvegardePartie ..> EtatSauvegarde : valide
    EtatSauvegarde --> "1" EtatHeros : heros
    EtatSauvegarde --> "9" EtatSalle : salles valides
    EtatSalle --> "0..1" EtatEnnemi : ennemi
    GestionnaireSauvegarde --> ObjectMapper
    GestionnaireSauvegarde ..> EtatSauvegarde : JSON
    GestionnaireSauvegarde ..> SauvegardePartie : validation metier
    GestionnaireSauvegarde ..> ExceptionSauvegarde : signale les erreurs
    Exception <|-- ExceptionSauvegarde
```

Les cardinalités décrivent les instantanés valides. Les valeurs d'un record sont exposées par ses accesseurs générés, tandis que les champs Java restent privés et finaux. Les listes sont copiées défensivement ; l'instantané est indépendant de la partie vivante. La validation des relations incombe à `SauvegardePartie.restaurer`, et non aux seuls constructeurs de records. Le modèle peut donc capturer et restaurer un état sans connaître JSON ou Jackson.

## Application, contrôle et Swing

```mermaid
classDiagram
    direction TB
    class Application {
        +main(String[] args) void$
    }
    class FenetreJeu {
        -boolean occupe
    }
    class CarteDonjon {
        +afficher(Partie partie, boolean occupe) void
    }
    class Theme
    class ControleurJeu {
        -Partie partie
        +nouvellePartie() void
        +adopter(Partie chargee) void
        +abandonner() void
        +capturer() EtatSauvegarde
        +attaquer() String
        +deplacer(String salle) String
        +equiper(Arme arme) String
    }
    class Partie
    class SauvegardePartie
    class GestionnaireSauvegarde
    class JFrame {
        <<Swing>>
    }
    class JPanel {
        <<Swing>>
    }
    class SwingWorker {
        <<Swing>>
    }
    Application ..> FenetreJeu : creation sur EDT
    JFrame <|-- FenetreJeu
    JPanel <|-- CarteDonjon
    FenetreJeu *-- "1" ControleurJeu
    FenetreJeu *-- "1" GestionnaireSauvegarde
    FenetreJeu *-- "1" CarteDonjon
    FenetreJeu ..> Theme
    CarteDonjon ..> Theme
    FenetreJeu ..> SwingWorker : sous-classe anonyme
    FenetreJeu ..> Partie : consultation
    CarteDonjon --> "0..1" Partie : affichage
    ControleurJeu --> "0..1" Partie : partie courante
    ControleurJeu ..> SauvegardePartie : capture
    GestionnaireSauvegarde ..> SauvegardePartie : restauration
```

`Application` est dans le package racine ; les classes de présentation sont dans `vue`, `ControleurJeu` dans `controleur`. Le contrôleur conserve zéro partie à l'accueil, puis la partie active. `CarteDonjon` reçoit à sa construction une fonction `Consumer<String>` qui transmet les demandes de déplacement à la fenêtre ; elle ne possède pas son propre contrôleur.

`FenetreJeu` capture l'instantané sur l'EDT avant une sauvegarde. Sa sous-classe anonyme de `SwingWorker` réalise les entrées/sorties et la reconstruction en arrière-plan, puis publie le résultat dans `done` sur l'EDT. Le contrôleur n'adopte la nouvelle partie qu'après succès et confirmation d'abandon si nécessaire. L'ancienne partie reste intacte en cas d'erreur. Les règles de jeu restent dans le modèle même lorsque la vue désactive préventivement un bouton.
