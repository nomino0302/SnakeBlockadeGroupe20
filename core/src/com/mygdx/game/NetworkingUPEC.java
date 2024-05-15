package com.mygdx.game;

import java.util.ArrayList;
import java.util.Random;

/*
Classe NetworkingUPEC.java
Permet de faire communiquer 2 programme à l'aide du serveur Padiflac
Vu que la classe channel est bloquante, nous utilisons des Threads
*/

public class NetworkingUPEC {
    String channelCode;
    String mod;

    Random random;
    Channel channel; // Classe Channel pour Padiflac
    Thread reader;

    boolean netActivated = false;

    // Variable volatile = variable modifiable et accessible par tous les Threads
    volatile Snake firstSnake, secondSnake;
    volatile ArrayList<ArrayList<Integer>> updatedSnakeList;
    volatile ArrayList<String> receivedHistoty;
    volatile String received;
    volatile boolean setupEnded = false;
    volatile boolean gameStarted = false;
    volatile boolean finishedPlaying = false;
    volatile boolean areYouFirst = false;
    volatile boolean myTurn = false;
    volatile boolean freshlyUpdated = false;
    volatile boolean blocked = false;
    volatile String globalWatermark = "SnakeBlockade";
    volatile String myWatermark = "SnakeBlockadeG20-";

    public void startChannel(Snake firstSnake, Snake secondSnake, String channelCode, String mod) {
        this.channelCode = channelCode;
        this.mod = mod;

        this.random = new Random();
        this.channel = new Channel(channelCode);
        // :: = référence de méthode, pour éviter de tout écrire dans des expressions lambda
        reader = new Thread(this::startReading);

        this.netActivated = true;

        this.firstSnake = firstSnake;
        this.secondSnake = secondSnake;
        this.updatedSnakeList = new ArrayList<>();
        this.receivedHistoty = new ArrayList<>();
        this.received = "";
        this.setupEnded = false;
        this.gameStarted = false;
        this.finishedPlaying = false;
        this.areYouFirst = false;
        this.myTurn = false;
        this.freshlyUpdated = false;
        this.blocked = false;
        this.globalWatermark = "SnakeBlockade";
        this.myWatermark = "SnakeBlockadeG20-" + random.nextInt(10000, 100000);
    }

    // Lecture des coups pour le mode IA VS IA (seule la position du snake est attendu)
    // Le format attendu à recevoir : 0,15 0,14 1,14 ...
    public void startReading() {
        while (!Thread.currentThread().isInterrupted() && !received.equals(Global.STOPTHREAD)) {
            received = channel.getNext();
            receivedHistoty.add(received);
            System.out.println(received);

            if (gameStarted) {
                if (!received.contains(globalWatermark) && !received.equals(Global.STOPTHREAD)) {
                    // Update du snake ennemi si c'était son tour
                    if (received.equals(Global.BLOCKED)) blocked = true;
                    else if (!myTurn) formatToUpdatedSnakeList(received);
                    myTurn = !myTurn;
                    finishedPlaying = false;
                    if (myTurn) freshlyUpdated = true;
                }
            } else {
                // Choix du snake selon le premier message
                if (received.contains(globalWatermark)) {
                    if (received.equals(myWatermark)) setMySnakeFirst();
                    else setOtherSnakeFirst();
                    setupEnded = true;
                    gameStarted = true;
                }
            }
        }
    }

    public void setMySnakeFirst() {
        if (mod.equals(Global.IAVIA)) {
            firstSnake.name = Global.IAG20;
            secondSnake.name = Global.IA;
        } else if (mod.equals(Global.JVJONLINE)) {
            firstSnake.name = Global.J1TOI;
            secondSnake.name = Global.J2;
        }
        areYouFirst = true;
        myTurn = true;
    }

    public void setOtherSnakeFirst() {
        if (mod.equals(Global.IAVIA)) {
            firstSnake.name = Global.IA;
            secondSnake.name = Global.IAG20;
        } else if (mod.equals(Global.JVJONLINE)) {
            firstSnake.name = Global.J1;
            secondSnake.name = Global.J2TOI;
        }
        areYouFirst = false;
        myTurn = false;
    }

    // Permet de transformer un objet snake en un String pour la communication
    public String snakeListToFormat(ArrayList<ArrayList<Integer>> snakeList) {
        StringBuilder format = new StringBuilder();
        for (ArrayList<Integer> pos : snakeList) {
            format.append(pos.get(0)).append(",").append(pos.get(1)).append(" ");
        }
        if (format.length() > 0) {
            format.setLength(format.length() - 1);
        }
        return format.toString();
    }

    public void formatToUpdatedSnakeList(String format) {
        ArrayList<ArrayList<Integer>> newSnakeList = new ArrayList<>();
        String[] splitedFormat = format.split(" ");
        for (String stringPos: splitedFormat) {
            String[] pos = stringPos.split(",");
            newSnakeList.add(Global.tuple(Integer.parseInt(pos[0]), Integer.parseInt(pos[1])));
        }
        updatedSnakeList = newSnakeList;
    }

    // Fonction pour stopper les threads courants
    public void stopChannel() {
        channel.send(Global.STOPTHREAD);
        reader.interrupt();
        netActivated = false;
        setupEnded = false;
        gameStarted = false;
        finishedPlaying = false;
        areYouFirst = false;
        myTurn = false;
        freshlyUpdated = false;
        blocked = false;
    }
}
