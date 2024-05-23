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
    ArrayList<ArrayList<Integer>> unusedPositionsWIP = new ArrayList<>(); // WIP = Without Illegal Positions (pour ne pas bloquer le spawn du snake)
    ArrayList<ArrayList<Integer>> outsideLimits = new ArrayList<>();

    ArrayList<ArrayList<Integer>> illegalPos;

    Board(Scene scene) {
        this.scene = scene;

        illegalPos = new ArrayList<>(Arrays.asList(Global.tuple(0, scene.lines - 1), Global.tuple(0, scene.lines - 2),
                Global.tuple(1, scene.lines - 1), Global.tuple(scene.columns - 1, 0),
                Global.tuple(scene.columns - 2, 0), Global.tuple(scene.columns - 1, 1)));

        // Tableau des positions possibles
        for (int x = 0; x < scene.columns; x++) {
            for (int y = 0; y < scene.lines; y++) {
                possiblePositions.add(Global.tuple(x, y));
                unusedPositions.add(Global.tuple(x, y));
                if (!illegalPos.contains(Global.tuple(x, y))) unusedPositionsWIP.add(Global.tuple(x, y));
            }
        }

        // Limites du plateau
        for (int i = 0; i < scene.lines; i++) {
            outsideLimits.add(Global.tuple(-1, i)); // Gauche
            outsideLimits.add(Global.tuple(scene.columns, i)); // Droite
        }
        for (int i = 0; i < scene.columns; i++) {
            outsideLimits.add(Global.tuple(i, -1)); // Bas
            outsideLimits.add(Global.tuple(i, scene.lines)); // Haut
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
