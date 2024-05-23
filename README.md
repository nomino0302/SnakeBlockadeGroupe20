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