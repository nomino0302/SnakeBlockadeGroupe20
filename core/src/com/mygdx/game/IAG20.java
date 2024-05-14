package com.mygdx.game;
import java.util.ArrayList;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class IAG20 extends Snake {
    Snake otherSnake;
    Board board;


    IAG20(SpriteBatch batch, Assets assets, Scene scene, Board board, Objects objects, String side) {
        super(batch, assets, scene, board, objects, side);
        this.otherSnake = null;
        this.board = board;
    }

    void evaluate() {
        // Algo pour l'IA
            ArrayList<String> possibleDirections = getPossibleDirections();
            if (possibleDirections.isEmpty()) {
                // Si toutes les directions mènent à une collision, choisir une direction aléatoire pour se sucider
                possibleDirections = getAllDirections();
            }
            // Choisir la meilleure direction
            String bestDirection = possibleDirections.get(0);
            this.setDirection(bestDirection);
        }
        //Renvoi les directions possibles qui ne mènent pas à une collision.

        ArrayList<String> getPossibleDirections() {
            ArrayList<String> directions = getAllDirections();
            directions.removeIf(this::isValidDirection);
            return directions;
        }
        // Renvoi toutes les directions possibles.

        ArrayList<String> getAllDirections() {
            ArrayList<String> directions = new ArrayList<>();
            directions.add(Global.HAUT);
            directions.add(Global.BAS);
            directions.add(Global.GAUCHE);
            directions.add(Global.DROITE);
            return directions;
        }
        boolean isValidDirection(String direction) {
            ArrayList<Integer> futureHead = futureHead(direction);
            String result = previewGameOver(futureHead, false);
            return !result.equals(Global.TOUCHED);
        }

    @Override
    public boolean move(boolean grow) {
        evaluate();

        return super.move(grow);
    }

}
