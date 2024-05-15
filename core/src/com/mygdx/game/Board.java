package com.mygdx.game;

import java.util.ArrayList;
import java.util.Arrays;

/*
Classe Board.java
Classe représentant le plateau de jeu actuel
Elle permet de savoir si il y a des collisions, et de spawn les items dans des cases libres
*/

public class Board {
    Scene scene;

    // Positions générales (tous les éléments)
    ArrayList<ArrayList<Integer>> possiblePositions = new ArrayList<>();
    ArrayList<ArrayList<Integer>> usedPositions = new ArrayList<>();
    ArrayList<ArrayList<Integer>> unusedPositions = new ArrayList<>();
    ArrayList<ArrayList<Integer>> unusedPositionsWIP = new ArrayList<>(); // WIP = Without Illegal Positions
    ArrayList<ArrayList<Integer>> outsideLimits = new ArrayList<>();

    ArrayList<ArrayList<Integer>> illegalPos;

    Board(Scene scene) {
        this.scene = scene;

        illegalPos = new ArrayList<>(Arrays.asList(Global.tuple(0, scene.boardTilesRatio - 1), Global.tuple(0, scene.boardTilesRatio - 2),
                Global.tuple(1, scene.boardTilesRatio - 1), Global.tuple(scene.boardTilesRatio - 1, 0),
                Global.tuple(scene.boardTilesRatio - 2, 0), Global.tuple(scene.boardTilesRatio - 1, 1)));

        // Tableau des positions possibles
        for (int x = 0; x < scene.boardTilesRatio; x++) {
            for (int y = 0; y < scene.boardTilesRatio; y++) {
                possiblePositions.add(Global.tuple(x, y));
                unusedPositions.add(Global.tuple(x, y));
                if (!illegalPos.contains(Global.tuple(x, y))) unusedPositionsWIP.add(Global.tuple(x, y));
            }
        }

        // Limites du plateau
        for (int i = 0; i < scene.boardTilesRatio; i++) {
            outsideLimits.add(Global.tuple(-1, i)); // Haut
            outsideLimits.add(Global.tuple(scene.boardTilesRatio, i)); // Bas
        }
        for (int i = 0; i < scene.boardTilesRatio; i++) {
            outsideLimits.add(Global.tuple(i, -1)); // Gauche
            outsideLimits.add(Global.tuple(i, scene.boardTilesRatio)); // Droit
        }
    }

    // Fonction qui permet d'ajouter des élements sur le plateau, en mettant à jour les listes
    public void addElement(ArrayList<Integer> givenTuple) {
        unusedPositions.remove(givenTuple);
        usedPositions.add(givenTuple);
        unusedPositionsWIP.remove(givenTuple);
    }

    // Fonction qui permet d'enlever des élements sur le plateau, en mettant à jour les listes
    public void removeElement(ArrayList<Integer> givenTuple) {
        usedPositions.remove(givenTuple);
        unusedPositions.add(givenTuple);
        if (!illegalPos.contains(givenTuple)) unusedPositionsWIP.add(givenTuple);
    }
}
