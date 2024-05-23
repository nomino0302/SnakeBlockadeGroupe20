package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/*
Classe Objects.java
Classe permettant d'ajouter, retirer et dessiner les objets sur notre plateau de jeu
*/

public class Objects {
    SpriteBatch batch;
    Assets assets;
    Scene scene;
    Board board;
    Random random;

    ArrayList<ArrayList<Integer>> strawberries = new ArrayList<>(); // Pour les coordonnées en cases
    Array<Rectangle> strawberriesRectangles = new Array<>(); // Pour les coordonnées en pixels (pour l'affichage)
    ArrayList<ArrayList<Integer>> rocks = new ArrayList<>();
    Array<Rectangle> rocksRectangles = new Array<>();

    Texture strawberryTexture, rockTexture;

    boolean objectsEnabled = true; // Si le mode de jeu interdit les objets

    Objects(SpriteBatch batch, Assets assets, Scene scene, Board board) {
        this.batch = batch;
        this.assets = assets;
        this.scene = scene;
        this.board = board;
        this.random = new Random();

        strawberryTexture = assets.get("objects/strawberry.png", Texture.class);
        rockTexture = assets.get("objects/rock.png", Texture.class);
    }

    // Fonction de début de partie pour remplir le plateau d'objets
    public void initObjects(int nbStrawberries, int nbRocks) {
        for (int i = 0; i < nbRocks; i++) addRock();
        for (int i = 0; i < nbStrawberries; i++) addStrawberry();
    }

    // Fonction de dessin d'objets
    public void drawObjects() {
        drawStrawberries();
        drawRocks();
    }

    // Fonction de dessin de fraises
    public void drawStrawberries() {
        for (Rectangle strawberryRect: strawberriesRectangles) {
            batch.draw(strawberryTexture, strawberryRect.x, strawberryRect.y, strawberryRect.width, strawberryRect.height);
        }
    }

    // Fonction de dessin de rochers
    public void drawRocks() {
        for (Rectangle rockRect: rocksRectangles) {
            batch.draw(rockTexture, rockRect.x, rockRect.y, rockRect.width, rockRect.height);
        }
    }

    // Fonction qui met à jour les tableaux (celui des fraises et du plateau général de notre objet Board)
    public void addStrawberry() {
        if (objectsEnabled) {
            try {
                ArrayList<Integer> coor = board.unusedPositionsWIP.get(random.nextInt(board.unusedPositionsWIP.size()));
                strawberries.add(coor);
                board.addElement(coor);
                strawberriesRectangles.add(new Rectangle(scene.xOff + scene.pixelsForTile * coor.get(0), scene.yOff + scene.pixelsForTile * coor.get(1),
                        scene.pixelsForTile, scene.pixelsForTile));
            } catch (IllegalArgumentException ignored) {} // Si board.unusedPositionsWIP est vide (nextInt ne prend que des nombres positifs)
        }
    }

    // Fonction qui met à jour les tableaux (celui des rochers et du plateau général de notre objet Board)
    public void addRock() {
        if (objectsEnabled) {
            try {
                ArrayList<Integer> coor = board.unusedPositionsWIP.get(random.nextInt(board.unusedPositionsWIP.size()));
                rocks.add(coor);
                board.addElement(coor);
                rocksRectangles.add(new Rectangle(scene.xOff + scene.pixelsForTile * coor.get(0), scene.yOff + scene.pixelsForTile * coor.get(1),
                        scene.pixelsForTile, scene.pixelsForTile));
            } catch (IllegalArgumentException ignored) {}
        }
    }

    // Setup des rocks pour le tournois avec les coordonnées suivantes : (4, 4), (5, 4), (6, 4), (7, 4), (4, 17), (5, 17), (6, 17), (7, 17)
    public void addRocksTournament() {
        objectsEnabled = true;
        ArrayList<ArrayList<Integer>> listOfRocks = new ArrayList<>(Arrays.asList(Global.tuple(4, 4), Global.tuple(4, 5),
                Global.tuple(4, 6), Global.tuple(4, 7), Global.tuple(17, 4), Global.tuple(17, 5),
                Global.tuple(17, 6), Global.tuple(17, 7)));
        for (ArrayList<Integer> rock: listOfRocks) {
            rocks.add(rock);
            board.addElement(rock);
            rocksRectangles.add(new Rectangle(scene.xOff + scene.pixelsForTile * rock.get(0), scene.yOff + scene.pixelsForTile * rock.get(1),
                    scene.pixelsForTile, scene.pixelsForTile));
        }
        objectsEnabled = false;
    }

    // Fonction qui met à jour les tableaux pour supprimer les fraises (mangées)
    public void removeStrawberry(ArrayList<Integer> coor) {
        if (objectsEnabled) {
            strawberries.remove(coor);
            board.removeElement(coor);
            for (Rectangle strawberryRect: strawberriesRectangles) {
                if (scene.xOff + coor.get(0) * scene.pixelsForTile == strawberryRect.x && scene.yOff + coor.get(1) * scene.pixelsForTile == strawberryRect.y) {
                    strawberriesRectangles.removeValue(strawberryRect, true);
                }
            }
        }
    }
}
