package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import java.util.ArrayList;
import java.util.Arrays;

/*
Classe Snake.java
Classe permettant l'implémentation d'un serpent sur le plateau de jeu grâce à une liste de liste
Les fonctions usuelles permettent au snake de grandir, rétrécir, bouger, ...
*/

public class Snake {
    SpriteBatch batch;
    Assets assets;
    Scene scene;
    Board board;
    Objects objects;

    // ArrayMap = Hashmap mais optimisé pour garder des Textures
    ArrayMap<String, ArrayMap<String, Texture>> snakeAssets = new ArrayMap<>();
    ArrayList<ArrayList<Integer>> snake = new ArrayList<>();
    Array<Rectangle> snakeRectangles = new Array<>();

    String side; // "LEFT" ou "RIGHT"
    String direction;
    String lastDirection;
    String gameOverDirection;
    String color;

    Snake(SpriteBatch batch, Assets assets, Scene scene, Board board, Objects objects, String side) {
        this.batch = batch;
        this.assets = assets;
        this.scene = scene;
        this.board = board;
        this.objects = objects;

        // Organisation des assets
        for (String color: assets.snakeColors) {
            snakeAssets.put(color, new ArrayMap<>());
            for (String part: assets.snakeParts) {
                snakeAssets.get(color).put(part, assets.get("snake/" + color + "_" + part + ".png", Texture.class));
            }
        }

        this.side = side;
        if (this.side.equals(Global.LEFT)) {
            this.direction = Global.DROITE;
            this.color = Global.BLUE;
        } else {
            this.direction = Global.GAUCHE;
            this.color = Global.RED;
        }
        this.lastDirection = this.direction;
        this.gameOverDirection = this.direction;
    }

    // Fonction de départ pour créer le snake, on le met manuellement en haut à gauche ou en bas à droite
    public void initSnake() {
        if (side.equals(Global.LEFT)) {
            addPart(0, scene.boardTilesRatio - 1);
        } else {
            addPart(scene.boardTilesRatio - 1, 0);
        }
    }

    // Fonction permettant au snake de bouger dans une direction
    // Elle permet également de faire grandir le snake d'une case et de vérifier si il se heurte à quelque chose
    // Elle renvoie true si le snake a bien bougé, false sinon (donc game over)
    public boolean move(boolean grow) {
        ArrayList<Integer> futureHead = futureHead(direction);
        String futureResult = previewGameOver(futureHead);
        if (futureResult.equals(Global.NOTHING) || futureResult.equals(Global.STRAWBERRY) || (futureHead.equals(snake.get(snake.size() - 1)) && snake.size() != 2 && !grow)) {
            if (futureResult.equals(Global.STRAWBERRY)) {
                objects.removeStrawberry(futureHead);
                if (snake.size() > 1) removeLast();
            }
            addPart(futureHead.get(0), futureHead.get(1));
            if (!grow) removeLast();
            return true;
        } else {
            gameOverDirection = direction;
            direction = lastDirection;
            return false;
        }
    }

    // Fonction permettant de vérifier si le snake va heurter quelque chose avec son futur coup
    public String previewGameOver(ArrayList<Integer> futureHead) {
        if (board.usedPositions.contains(futureHead)) {
            if (objects.strawberries.contains(futureHead)) return Global.STRAWBERRY;
            else return Global.TOUCHED;
        } else if (board.outsideLimits.contains(futureHead)) {
            return Global.TOUCHED;
        }
        return Global.NOTHING;
    }

    // Fonction permettant de savoir, avant que le joueur fasse son coup, si il est dans une situation dans lequel son snake est bloqué
    public boolean isBlocked(boolean grow) {
        ArrayList<ArrayList<Integer>> tupleList = new ArrayList<>(Arrays.asList(futureHead(Global.GAUCHE), futureHead(Global.DROITE), futureHead(Global.HAUT), futureHead(Global.BAS)));
        for (ArrayList<Integer> pos: tupleList) {
            if (board.usedPositions.contains(pos)) {
                if (!objects.strawberries.contains(pos)) {
                    // Si le snake ne grandit pas et qu'il veut aller sur sa queue, il peut
                    if (snake.size() != 2 && pos.equals(snake.get(snake.size() - 1))) {
                        if (!grow) return false; // Dès qu'il y a un potentiel endroit libre on renvoie false
                    }
                } else return false;
            } else if (board.outsideLimits.contains(pos)) {}
            else return false;
        }
        return true;
    }

    // Permet de renvoyer les coordonnées du futur emplacement du snake après son coup
    public ArrayList<Integer> futureHead(String givenDirection) {
        ArrayList<Integer> currentHead = snake.get(0);
        switch (givenDirection) {
            case Global.HAUT:
                return board.tuple(currentHead.get(0), currentHead.get(1) + 1);
            case Global.BAS:
                return board.tuple(currentHead.get(0), currentHead.get(1) - 1);
            case Global.GAUCHE:
                return board.tuple(currentHead.get(0) - 1, currentHead.get(1));
            default:
                return board.tuple(currentHead.get(0) + 1, currentHead.get(1));
        }
    }

    // Permet de changer la direction que va prendre le snake pour son prochain coup
    public void setDirection(String newDirection) {
        lastDirection = direction;
        direction = newDirection;
    }

    // Permet d'ajouter 1 case de longueur au snake (mis au tout début de la liste (tête))
    public void addPart(int x, int y) {
        snake.add(0, board.tuple(x, y));
        snakeRectangles.insert(0, new Rectangle(x * scene.pixelsForTile, y * scene.pixelsForTile, scene.pixelsForTile, scene.pixelsForTile));
        board.addElement(board.tuple(x, y));
    }

    // Permet d'enlever 1 case de longueur au snake (la queue)
    public void removeLast() {
        ArrayList<Integer> removed = snake.remove(snake.size() - 1);
        snakeRectangles.removeIndex(snakeRectangles.size - 1);
        board.removeElement(removed);
    }

    // Permet de dessiner le serpent en pixels
    public void drawSnake() {
        for (int i = 0; i < snakeRectangles.size; i++) {
            Rectangle part = snakeRectangles.get(i);
            Texture texture = getCorrectTexture(i);
            batch.draw(texture, part.x, part.y, part.width, part.height);
        }
    }

    // Renvoie la bonne Texture selon l'emplacement dans la liste (tête, corps ou queue)
    public Texture getCorrectTexture(int index) {
        if (index == 0) return getHead();
        if (index == snakeRectangles.size - 1) return getTail();
        else return getMiddlePart(index);
    }

    // Renvoie la bonne Texture pour la tête (selon la direction)
    public Texture getHead() {
        switch (direction) { // Switch case pour comparer les Strings
            case Global.HAUT:
                return snakeAssets.get(color).get("headHaut");
            case Global.DROITE:
                return snakeAssets.get(color).get("headDroite");
            case Global.BAS:
                return snakeAssets.get(color).get("headBas");
            default:
                return snakeAssets.get(color).get("headGauche");
        }
    }

    // Renvoie la bonne Texture pour la queue (selon la partie avant la queue)
    public Texture getTail() {
        Rectangle part = snakeRectangles.get(snakeRectangles.size - 1);
        Rectangle beforePart = snakeRectangles.get(snakeRectangles.size - 2);
        if (beforePart.x < part.x) return snakeAssets.get(color).get("tailGauche");
        else if (beforePart.x > part.x) return snakeAssets.get(color).get("tailDroite");
        else if (beforePart.y < part.y) return snakeAssets.get(color).get("tailBas");
        else return snakeAssets.get(color).get("tailHaut");
    }

    // Renvoie la bonne Texture pour le corps (selon la partie avant et après)
    public Texture getMiddlePart(int index) {
        Rectangle part = snakeRectangles.get(index);
        Rectangle beforePart = snakeRectangles.get(index - 1); // Plus près de la tête
        Rectangle afterPart = snakeRectangles.get(index + 1); // Plus près de la queue
        // Hori
        if ((beforePart.x < part.x && afterPart.x > part.x) || (beforePart.x > part.x && afterPart.x < part.x)) return snakeAssets.get(color).get("hori");
        // Verti
        else if ((beforePart.y < part.y && afterPart.y > part.y) || (beforePart.y > part.y && afterPart.y < part.y)) return snakeAssets.get(color).get("verti");
        // Gauche Haut
        else if ((beforePart.x < part.x && afterPart.y > part.y) || (beforePart.y > part.y && afterPart.x < part.x)) return snakeAssets.get(color).get("gaucheHaut");
        // Gauche Bas
        else if ((beforePart.x < part.x && afterPart.y < part.y) || (beforePart.y < part.y && afterPart.x < part.x)) return snakeAssets.get(color).get("gaucheBas");
        // Droite Haut
        else if ((beforePart.x > part.x && afterPart.y > part.y) || (beforePart.y > part.y && afterPart.x > part.x)) return snakeAssets.get(color).get("droiteHaut");
        // Droite Bas
        else return snakeAssets.get(color).get("droiteBas");
    }
}
