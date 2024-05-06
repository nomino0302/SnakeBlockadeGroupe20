package com.mygdx.game;

// Classe contenant les variables constantes qui peuvent être utilisées par tout les fichiers sans importer ladite classe
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
}