package com.mygdx.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
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
        ArrayList<String> possibleDirections = getPossibleDirections(); // Récupère les directions possibles

        if (possibleDirections.isEmpty()) {
            // Si toutes les directions mènent à une collision, choisir une direction aléatoire pour se suicider
            possibleDirections = getAllDirections(); // Si aucune direction n'est sûre, toutes sont considérées possibles
        }

        // Choisir la meilleure direction possible en fonction de l'espace disponible et de la position de l'autre serpent
        String bestDirection = chooseBestDirection(possibleDirections);
        this.setDirection(bestDirection); 
    }

    // Renvoie les directions possibles qui ne mènent pas à une collision.
    ArrayList<String> getPossibleDirections() {
        ArrayList<String> directions = getAllDirections(); 
        directions.removeIf(direction -> !isValidDirection(direction)); // Enlève les directions non valides
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

    // Vérifie si une direction est valide (une collision avec les murs ou les serpents sont des directions non valides).
    boolean isValidDirection(String direction) {
        ArrayList<Integer> futureHead = futureHead(direction); 
        String result = previewGameOver(futureHead, false); 
        
        // Vérifie la collision avec le corps du serpent actuel
        boolean collideSelf = this.snake.stream().skip(1).anyMatch(part -> part.equals(futureHead));
        
        // Vérifie la collision avec le corps de l'autre serpent
        boolean collideOtherSnake = otherSnake != null && otherSnake.snake.stream().anyMatch(part -> part.equals(futureHead));

        // Retourne vrai si la position ne mène à aucune collision
        return result.equals(Global.NOTHING) && !collideSelf && !collideOtherSnake;
    }

        // Choisit la meilleure direction pour entourer l'autre serpent et éviter les collisions
    String chooseBestDirection(ArrayList<String> possibleDirections) {
        ArrayList<String> bestDirections = new ArrayList<>(); // Liste pour stocker les meilleures directions
        int maxFreeSpace = -1; // Variable pour stocker l'espace libre maximum

        // Parcourt toutes les directions possibles
        for (String direction : possibleDirections) {
            ArrayList<Integer> futureHead = futureHead(direction); // Calcule la future position de la tête
            int freeSpace = countFreeSpaces(futureHead); // Calcule l'espace libre pour cette direction

            // Choisit la direction offrant le plus d'espace libre
                if (freeSpace > maxFreeSpace) {
                    maxFreeSpace = freeSpace; // Met à jour l'espace libre maximum
                    bestDirections.clear(); // Vide la liste des meilleures directions
                    bestDirections.add(direction); // Ajoute la nouvelle meilleure direction
                } else if (freeSpace == maxFreeSpace) {
                    bestDirections.add(direction); // Ajoute la direction en cas d'égalité
                }
            }
    
            // Si plusieurs directions ont le même espace libre, on choisit celle qui minimise la distance Manhattan à la tête de l'autre serpent
            if (bestDirections.size() > 1) {
                int minDistance = Integer.MAX_VALUE;
                ArrayList<String> bestDirectionsWithMinDistance = new ArrayList<>();
    
                // Parcourt toutes les meilleures directions
                for (String direction : bestDirections) {
                    ArrayList<Integer> futureHead = futureHead(direction); // Calcule la future position de la tête
                    int distance = calculateManhattanDistance(futureHead, otherSnake.snake.get(0)); // Calcule la distance de Manhattan à la tête de l'autre serpent
    
                    // Si la distance est inférieure à la distance minimale, met à jour la distance minimale et la liste des meilleures directions
                    if (distance < minDistance) {
                        minDistance = distance;
                        bestDirectionsWithMinDistance.clear();
                        bestDirectionsWithMinDistance.add(direction);
                    } else if (distance == minDistance) {
                        bestDirectionsWithMinDistance.add(direction); // Ajoute la direction en cas d'égalité de distance
                    }
                }
    
                // Retourne une direction aléatoire parmi les meilleures directions avec la distance minimale
                return bestDirectionsWithMinDistance.get(random.nextInt(bestDirectionsWithMinDistance.size()));
            } else {
                // Retourne la seule direction disponible s'il n'y en a qu'une
                return bestDirections.get(0);
            }
        }
    
        // Calcule la distance de Manhattan entre deux positions
        int calculateManhattanDistance(ArrayList<Integer> position1, ArrayList<Integer> position2) {
            return Math.abs(position1.get(0) - position2.get(0)) + Math.abs(position1.get(1) - position2.get(1));
        }
    

    // Compte l'espace libre autour d'une position donnée en utilisant BFS
    int countFreeSpaces(ArrayList<Integer> start) {
        Set<ArrayList<Integer>> visited = new HashSet<>(); // Ensemble pour stocker les positions visitées
        ArrayList<ArrayList<Integer>> queue = new ArrayList<>(); // Liste pour le parcours BFS
        queue.add(start); // Ajoute la position de départ à la liste
        visited.add(start); // Marque la position de départ comme visitée

        int freeSpaces = 0; 

        while (!queue.isEmpty()) {
            ArrayList<Integer> position = queue.remove(0); // Récupère la prochaine position à explorer
            freeSpaces++; 

            // Parcourt toutes les directions possibles
            for (String direction : getAllDirections()) {
                ArrayList<Integer> nextPos = futureHeadFromPosition(position, direction); // Calcule la prochaine position
                if (isPositionFree(nextPos) && !visited.contains(nextPos)) {
                    queue.add(nextPos);
                    visited.add(nextPos);
                }
            }
        }

        return freeSpaces;
    }

    // Vérifie si une position est libre (pas de collision avec les murs ou les serpents)
    boolean isPositionFree(ArrayList<Integer> position) {
        String result = previewGameOver(position, false); // Vérifie l'état du jeu pour cette position

        // Vérifie la collision avec le corps du serpent actuel
        boolean collideSelf = this.snake.stream().anyMatch(part -> part.equals(position));
        // Vérifie la collision avec le corps de l'autre serpent
        boolean collideOtherSnake = otherSnake != null && otherSnake.snake.stream().anyMatch(part -> part.equals(position));

        return result.equals(Global.NOTHING) && !collideSelf && !collideOtherSnake;
    }

    // Calcule la future position de la tête du serpent en fonction de la direction
    ArrayList<Integer> futureHeadFromPosition(ArrayList<Integer> position, String direction) {
        ArrayList<Integer> newHead = new ArrayList<>(position); 

        switch (direction) {
            case Global.HAUT:
                newHead.set(1, newHead.get(1) + 1);
                break;
            case Global.BAS:
                newHead.set(1, newHead.get(1) - 1);
                break;
            case Global.GAUCHE:
                newHead.set(0, newHead.get(0) - 1);
                break;
            case Global.DROITE:
                newHead.set(0, newHead.get(0) + 1);
                break;
        }
        return newHead; 
    }

    @Override
    public boolean move(boolean grow) {
        evaluate(); 
        return super.move(grow);
    }
}
