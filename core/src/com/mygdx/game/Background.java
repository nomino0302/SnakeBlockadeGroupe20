package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Background {
    SpriteBatch batch;
    Assets assets;
    int banHeight = 80;

    Background(SpriteBatch batch, Assets assets) {
        this.batch = batch;
        this.assets = assets;
        initBackground();
    }

    private void initBackground() {

    }
}
