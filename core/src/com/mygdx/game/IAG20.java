package com.mygdx.game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

public class IAG20 extends Snake {
    Snake otherSnake;
    Random random;

    IAG20(SpriteBatch batch, Assets assets, Scene scene, Board board, Objects objects, String side) {
        super(batch, assets, scene, board, objects, side);

        this.random = new Random();

    }

    public void evaluate() {
        // Algo pour l'IA

        // Fin de la fonction : elle change l'instance direction avec this.setDirection(...)
    }

    @Override
    public boolean move(boolean grow) {
        evaluate();

        return super.move(grow);
    }
}
