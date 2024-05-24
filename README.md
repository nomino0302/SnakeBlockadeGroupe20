# SnakeBlockade Groupe 20
#### Par Arnaud M. et Luc L.
#### 🔗 GitHub : https://github.com/nomino0302/SnakeBlockadeGroupe20

## À propos :
Pour notre projet de fin de L1 à l'UPEC, nous avons du concevoir
un jeu type Snake Blockade. Cela nous a permis de pratiquer plusieurs
technologies et concepts Java : GUI (libGDX), Thread, POO, Algo d'IA, ...

3 modes sont actuellement disponibles :
- Joueur VS Joueur
- Joueur VS IA
- IA VS IA (en ligne)

## Usage
Notre projet utilisant libGDX, nous utilisons l'outil de gestion de dépendances
Gradle. Il permet de lancer un programme sans se soucier des classpaths.

**⚠️ Versions de Java à utiliser :**
- Au minimum : **Java 9** --> Le mode en ligne requiert une syntaxe introduite à partir
de la version 9 de Java


- Au maximum : **Java 19** --> Le projet Gradle de libGDX ne supporte pas les nouvelles versions.
Si vous voulez tout de même utiliser une version au dessus de 19, veuillez lancer le programme en .jar
(voir plus bas)

### Méthode 1 : Exécuter le projet Gradle (normal)

Windows, macOS & Linux :
```shell
# Commande de base sans arguments
./gradlew desktop:run

# Avec arguments
./gradlew desktop:run --args="{channel_ecriture} {channel_lecture} {numero_joueur}"
# Exemples (lancement IA VS IA en ligne, les 2 lignes = 2 joueurs en VS)
./gradlew desktop:run --args="ch1 ch2 1"
./gradlew desktop:run --args="ch2 ch1 2"
```
Le tout premier lancement de cette commande peut prendre un moment :
Gradle télécharge les dépendances sur votre machine, soyez bien connectés à Internet.

### Méthode 2 : Exécuter le .jar

Windows & Linux :
```shell
java -jar desktop/build/libs/SnakeBlockadeG20.jar
java -jar desktop/build/libs/SnakeBlockadeG20.jar arg1 arg2 arg3
```

macOS :
```shell
java -XstartOnFirstThread -jar desktop/build/libs/SnakeBlockadeG20.jar
java -XstartOnFirstThread -jar desktop/build/libs/SnakeBlockadeG20.jar arg1 arg2 arg3
```

## Structure du projet
libGDX est conçu pour créer des jeux multiplatformes. Il possède donc une architecture
de dossiers et de fichiers précis :
```shell
core/ # Code source utilisé par toutes les plateformes
├── build/
└── src/
    └── com/mygdx/game/
        ├── Assets.java            # Charge les assets (images, sons, ...)
        ├── Board.java             # Représente le plateau
        ├── Channel.java           # Permet la connexion à Padiflac
        ├── Global.java            # Constantes et fonctions globales (utilisés par tous les fichiers)
        ├── IAG20.java             # Snake avec la logique IA du groupe 20
        ├── NetworkingUPEC.java    # Permet la communication entre 2 joueurs
        ├── Objects.java           # Représente les objets (fraises et rochers)
        ├── Scene.java             # Permet de mettre en place l'interface graphique (menu, bannière, plateau, ...)
        ├── Snake.java             # Représente le snake du joueur (longueur, positions, ...)
        └── SnakeBlockade.java     # Classe principale du jeu (boucle du jeu qui est exécutée toutes les frames notamment)
    └── build.gradle

desktop/
├── build/
│   ├── classes/
│   ├── generated/
│   ├── libs/
│   │   └── SnakeBlockadeG20.jar # .jar à exécuter si ./gradlew desktop:run pose problème
│   ├── resources/
│   └── tmp/
└── src/
    └── com/mygdx/game/
        └── DesktopLauncher.java # Fichier exécuté en premier lors du lancement du programme (fonction "main")
    └── build.gradle

assets/ # Dossier contenant les assets utilisées par le jeu (.png, .mp3, .wav), quand les .java demandent un fichier, le chemin part de la
.gitignore
README.md

# Dossiers/fichiers spécifiques à Gradle
gradle/
build.gradle
gradle.properties
gradlew
gradlew.bat
settings.gradle
```

⚠️ Le projet, utilisant libGDX et Gradle, peut mener à la détection
de fausses erreurs dans Visual Studio Code.
Il est conseillé d'utiliser IntelliJ IDEA.