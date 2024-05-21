package com.mygdx.game;

/*
Classe Global.java
Classe contenant les variables constantes qui peuvent être utilisées par tout les fichiers sans importer ladite classe
*/

import java.util.ArrayList;

public class Global {
    // public static final rend la constante globale
    public static final int WIDTH = 560;
    public static final int HEIGHT = 640;
    public static final int BAN_HEIGHT = 80;
    public static final int BOARD_HEIGHT = HEIGHT - BAN_HEIGHT;

    // Pour faciliter l'écriture de chaines de caractères souvent utilisées
    public static final String LEFT = "LEFT";
    public static final String RIGHT = "RIGHT";
    public static final String JVJ = "JVJ";
    public static final String JVIA = "JVIA";
    public static final String IAVIA = "IAVIA";
    public static final String J1 = "J1";
    public static final String J2 = "J2";
    public static final String IAG20 = "IAG20";
    public static final String IA = "IA";
    public static final String BLUE = "blue";
    public static final String RED = "red";
    public static final String HAUT = "HAUT";
    public static final String DROITE = "DROITE";
    public static final String BAS = "BAS";
    public static final String GAUCHE = "GAUCHE";

    // État du snake (dans son futur emplacement)
    public static final String TOUCHED = "TOUCHED";
    public static final String STRAWBERRY = "STRAWBERRY";
    public static final String NOTHING = "NOTHING";

    // Networking
    public static final String INVALID = "INVALID";
    public static final String STOPTHREAD = "STOPTHREAD";

    // Fonction qui retourne un "tuple" de int (pour la position par rapport aux cases)
    public static ArrayList<Integer> tuple(int x, int y) {
        ArrayList<Integer> newList = new ArrayList<>();
        newList.add(x);
        newList.add(y);
        return newList;
    }
}
