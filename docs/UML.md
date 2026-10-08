# Diagrammes de classes

Pour une présentation rapide devant un enseignant, commencer par le [schéma de conception POO simplifié](SCHEMA_CONCEPTION_POO.md). La présente page sert de référence technique détaillée.

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
        +resoudre(ActionCombat action) ResultatAction
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

## Résolution et faits métier

Ces types appartiennent également à `modele`. Les énumérations `Type` et `Cible` sont imbriquées dans `EvenementJeu`.

```mermaid
classDiagram
    direction LR
    class Partie {
        +resoudre(ActionCombat action) ResultatAction
    }
    class ActionCombat {
        <<enumeration>>
        ATTAQUER
        DEFENDRE
        POTION
    }
    class ResultatAction {
        <<record>>
        boolean valide
        String message
        EtatSauvegarde avant
        EtatSauvegarde apres
        List~EvenementJeu~ evenements
    }
    class EvenementJeu {
        <<record>>
        Type type
        Cible cible
        int valeur
        int vieApres
    }
    class Type {
        <<enumeration>>
        ATTAQUE
        DEFENSE
        SOIN
        DEGATS
        PREPARATION
        RAGE
        MORT
        RECOMPENSE
        VICTOIRE
        DEFAITE
    }
    class Cible {
        <<enumeration>>
        HEROS
        ENNEMI
    }
    class EtatSauvegarde
    Partie ..> ActionCombat
    Partie ..> ResultatAction : produit
    ResultatAction --> "0..*" EvenementJeu : liste copiee
    ResultatAction --> EtatSauvegarde : avant et apres
    EvenementJeu --> Type
    EvenementJeu --> Cible
```

Le résultat décrit une action déjà entièrement résolue. Une commande invalide conserve le même état et produit zéro événement. `cible` désigne le personnage concerné par le fait : attaquant pour `ATTAQUE`, victime pour `DEGATS`. Ces données n'ont aucune dépendance à la durée ou aux coordonnées d'une animation et n'étendent pas le format JSON version 1.

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
    class SceneJeu {
        +afficher(Partie nouvelle, String provenance) void
        +jouer(ResultatAction resultat, Runnable fin) void
        +marcherVersPorte(String salle) void
        +suspendre(boolean valeur) void
        +stabiliser() void
        +arreter() void
    }
    class GestionnaireRessources
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
        +resoudre(ActionCombat action) ResultatAction
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
    JPanel <|-- SceneJeu
    FenetreJeu *-- "1" ControleurJeu
    FenetreJeu *-- "1" GestionnaireSauvegarde
    FenetreJeu *-- "1" CarteDonjon
    FenetreJeu *-- "1" SceneJeu
    FenetreJeu *-- "1" GestionnaireRessources
    SceneJeu --> GestionnaireRessources
    FenetreJeu ..> Theme
    CarteDonjon ..> Theme
    FenetreJeu ..> SwingWorker : sous-classe anonyme
    FenetreJeu ..> Partie : consultation
    CarteDonjon --> "0..1" Partie : affichage
    SceneJeu --> "0..1" Partie : consultation
    ControleurJeu --> "0..1" Partie : partie courante
    ControleurJeu ..> SauvegardePartie : capture
    GestionnaireSauvegarde ..> SauvegardePartie : restauration
```

`Application` est dans le package racine ; les classes de présentation sont dans `vue`, `ControleurJeu` dans `controleur`. Le contrôleur conserve zéro partie à l'accueil, puis la partie active. `FenetreJeu` compose une scène centrale et les panneaux superposés. `CarteDonjon` et `SceneJeu` transmettent leurs intentions au moyen de fonctions de rappel ; elles ne possèdent pas leur propre contrôleur métier. La carte peut demander à la scène de marcher vers une porte, mais le modèle conserve la validation du changement de salle.

`FenetreJeu` capture l'instantané sur l'EDT avant une sauvegarde. Sa sous-classe anonyme de `SwingWorker` réalise les entrées/sorties et la reconstruction en arrière-plan, puis publie le résultat dans `done` sur l'EDT. Le contrôleur n'adopte la nouvelle partie qu'après succès et confirmation d'abandon si nécessaire. L'ancienne partie reste intacte en cas d'erreur. Les règles de jeu restent dans le modèle même lorsque la vue désactive préventivement un bouton.

## Présentation de la salle et du combat

```mermaid
classDiagram
    direction TB
    class SceneJeu {
        -double x
        -double y
        +estOccupe() boolean
        +estTimerActif() boolean
        +setReduite(boolean valeur) void
    }
    class PlanSalle {
        <<record>>
        String sousTitre
        Color lumiere
        +de(String id) PlanSalle$
        +praticable(double x, double y) boolean
    }
    class Porte {
        <<record>>
        String destination
        char cote
        int x
        int y
    }
    class Decor {
        <<record>>
        int objet
        int x
        int y
        int hauteur
        boolean solide
        +obstacle() Rectangle2D
    }
    class RenduSalle
    class RenduCombat
    class GestionnaireRessources {
        -BufferedImage[][] personnages
        -BufferedImage[] objets
        +icone(int id, int taille) ImageIcon
    }
    class ControleurAnimation {
        -int vieHeros
        -int vieEnnemi
        -int potions
        +demarrer(ResultatAction resolution) void
        +avancer(double millisecondes) void
        +finirImmediatement() void
        +courant() EvenementJeu
        +progression() double
    }
    class ResultatAction
    class EvenementJeu
    class Timer {
        <<Swing>>
    }
    SceneJeu *-- "1" RenduSalle
    SceneJeu *-- "1" RenduCombat
    SceneJeu *-- "1" ControleurAnimation
    SceneJeu *-- "1" Timer
    SceneJeu --> "1" PlanSalle
    PlanSalle --> "1..3" Porte : portes
    PlanSalle --> "0..*" Decor : decors
    RenduSalle --> GestionnaireRessources
    RenduSalle ..> PlanSalle
    RenduCombat --> GestionnaireRessources
    RenduCombat ..> ControleurAnimation
    ControleurAnimation --> "0..1" ResultatAction
    ControleurAnimation ..> EvenementJeu
```

`Porte` et `Decor` sont des records imbriqués dans `PlanSalle` ; les listes du plan sont copiées. `RenduSalle` et `RenduCombat` sont internes au package `vue`. Les images sont chargées une fois puis réutilisées. Les atlas générés nécessitent une découpe explicite documentée dans [ASSETS.md](ASSETS.md).

Le timer appartient uniquement à la scène et avance une horloge de présentation sur l'EDT. `ControleurAnimation` ne dépend pas de Swing et ne modifie jamais `Partie` : ses PV affichés progressent entre les instantanés avant/après déjà produits par le modèle. Les durées, poses et déplacements restent dans `vue`.

Le modèle ne conserve que la salle courante. La position locale `(x, y)`, les collisions de décor et les trajets appartiennent à `SceneJeu`/`PlanSalle`, ne sont pas sauvegardés et ne changent pas les règles de déplacement entre salles. Un chargement replace le héros visuel à `(425, 370)` ; JSON reste en version 1.
