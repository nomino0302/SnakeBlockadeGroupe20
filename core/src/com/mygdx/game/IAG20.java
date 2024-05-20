package com.mygdx.game;

import java.util.ArrayList;
import java.util.Random;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class IAG20 extends Snake {
    Snake otherSnake;
    Board board;
    Random random;

    IAG20(SpriteBatch batch, Assets assets, Scene scene, Board board, Objects objects, String side) {
        super(batch, assets, scene, board, objects, side);
        this.otherSnake = null; 
        this.board = board;
        this.random = new Random();
    }

    void evaluate() {
        // Algo pour l'IA
        ArrayList<String> possibleDirections = getPossibleDirections();

        if (possibleDirections.isEmpty()) {
            // Si toutes les directions mènent à une collision, choisir une direction aléatoire pour se suicider
            possibleDirections = getAllDirections();
        }

        // Choisir une direction aléatoire parmi les directions possibles
        String bestDirection = possibleDirections.get(random.nextInt(possibleDirections.size()));
        this.setDirection(bestDirection);
    }

    // Renvoie les directions possibles qui ne mènent pas à une collision.
    ArrayList<String> getPossibleDirections() {
        ArrayList<String> directions = getAllDirections();
        directions.removeIf(direction -> !isValidDirection(direction)); //-> c'est une utilisation de reference
        return directions;
    }

    // Renvoie toutes les directions possibles.
    ArrayList<String> getAllDirections() {
        ArrayList<String> directions = new ArrayList<>();
        directions.add(Global.HAUT);
        directions.add(Global.BAS);
        directions.add(Global.GAUCHE);
        directions.add(Global.DROITE);
        return directions;
    }

    // Vérifie si une direction est valide (ne mène pas à une collision avec les murs ou les serpents).
    boolean isValidDirection(String direction) {
        ArrayList<Integer> futureHead = futureHead(direction);
        String result = previewGameOver(futureHead, false);
        
        // Vérifie la collision avec le corps du serpent actuel
        boolean collideSelf = this.snake.stream().skip(1).anyMatch(part -> part.equals(futureHead));
        
        // Vérifie la collision avec le corps de l'autre serpent
        boolean collideOtherSnake = otherSnake.snake.stream().anyMatch(part -> part.equals(futureHead));

        return result.equals(Global.NOTHING) && !collideSelf && !collideOtherSnake;
    }
    

    @Override
    public boolean move(boolean grow) {
        evaluate();
        return super.move(grow);
    }
}
