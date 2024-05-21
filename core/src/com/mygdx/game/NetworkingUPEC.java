package com.mygdx.game;

import java.util.ArrayList;

/*
Classe NetworkingUPEC.java
Permet de faire communiquer 2 programme à l'aide du serveur Padiflac
Vu que la classe channel est bloquante, nous utilisons des Threads
*/

public class NetworkingUPEC {
    String readChannelCode;
    String writeChannelCode;

    Channel readChannel, writeChannel; // Classe Channel pour Padiflac
    Thread reader;

    boolean netActivated = false;

    // Variable volatile = variable modifiable et accessible par tous les Threads
    volatile boolean myTurn = false;
    volatile boolean receivedSomething = false;
    volatile String receivedInput;
    volatile String direction;
    volatile ArrayList<String> receivedHistoty;

    public void startChannel(String readerCode, String writerCode, boolean playingFirst) {
        this.readChannelCode = readerCode;
        this.writeChannelCode = writerCode;
        this.myTurn = playingFirst; // Le J1 joue en premier

        this.readChannel = new Channel(readerCode);
        this.writeChannel = new Channel(writerCode);
        // :: = référence de méthode, pour éviter de tout écrire dans des expressions lambda
        this.reader = new Thread(this::startReading);

        this.netActivated = true;

        this.receivedSomething = false;
        this.receivedInput = "";
        this.direction = "";
        this.receivedHistoty = new ArrayList<>();
    }

    // Lecture des coups pour le mode IA VS IA
    // Le format attendu à recevoir : U ou R ou D ou L ou STOPTHREAD
    public void startReading() {
        while (!Thread.currentThread().isInterrupted() && !receivedInput.equals(Global.STOPTHREAD)) {
            receivedInput = readChannel.getNext();
            receivedHistoty.add(receivedInput);
            System.out.println(receivedInput);

            if (!myTurn) {
                direction = letterToDirection(receivedInput);
                receivedSomething = true;
            }
        }
    }

    // Fonction qui permet de transformer une direction reconnu par le jeu en une lettre reconnu par la communication
    public String directionToLetter(String direction) {
        switch (direction) {
            case Global.HAUT:
                return "U";
            case Global.DROITE:
                return "R";
            case Global.BAS:
                return "D";
            case Global.GAUCHE:
                return "L";
            default:
                return Global.INVALID;
        }
    }

    // Fonction qui permet de transformer une direction reconnu par la communication en une lettre reconnu par le jeu
    public String letterToDirection(String letter) {
        switch (letter) {
            case "U":
                return Global.HAUT;
            case "R":
                return Global.DROITE;
            case "D":
                return Global.BAS;
            case "L":
                return Global.GAUCHE;
            default:
                return Global.INVALID;
        }
    }

    // Fonction pour stopper les threads courants
    public void stopChannel() {
        readChannel.send(Global.STOPTHREAD);
        reader.interrupt();
        myTurn = false;
        receivedSomething = false;
        netActivated = false;
    }
}
