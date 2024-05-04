package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Scene {
    SpriteBatch batch;
    Assets assets;
    ShapeRenderer shapeRenderer;
    Rectangle board = new Rectangle(0, 0, Global.WIDTH, Global.BOARD_HEIGHT);
    Rectangle ban = new Rectangle(0, Global.BOARD_HEIGHT, Global.WIDTH, Global.BAN_HEIGHT);

    Scene(SpriteBatch batch, Assets assets) {
        this.batch = batch;
        this.assets = assets;
        this.shapeRenderer = new ShapeRenderer(); // Pour dessiner des forme (la bannière dans notre cas)
    }

    public void baseDesign() {
        drawRect(ban.x, ban.y, ban.width, ban.height, Color.valueOf("298522"));
        batch.draw(assets.get("design/bg.png", Texture.class), board.x, board.y);
    }

    // Fonction pour dessiner des rectangles de couleur
    public void drawRect(float x, float y, float width, float height, Color color) {
        try {
            batch.end(); // On ferme le batch sauf si il est déjà fermé
        } catch (IllegalStateException ignored) {}
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();
        batch.begin();
    }

    // Disposer le shapeRenderer
    public void dispose() {
        shapeRenderer.dispose();
    }
}
