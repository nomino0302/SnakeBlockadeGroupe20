package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import java.util.HashMap;

// Classe regroupant toutes les textures utilisées, on utilise un peu plus que 10 lignes pour mapper tout les assets
public class Assets {
    Texture backgroundTexture, strawberryTexture, rockTexture;
    HashMap<String, HashMap<String, Texture>> snakeTexture = new HashMap<>();

    Assets() {
        // libGDX utilise la classe Texture pour dessiner des pixels sur l'écran
        this.backgroundTexture = new Texture("design/bg.png");
        this.strawberryTexture = new Texture("objects/strawberry.png");
        this.rockTexture = new Texture("objects/rock.png");

        // Textures du serpent, on utilise un dictionnaire pour séparer les textures bleues et rouges
        String[] parts = new String[] {"headHaut", "headDroite", "headBas", "headGauche", // Textures têtes
                "droiteHaut", "droiteBas", "gaucheHaut", "gaucheBas", "hori", "verti", "tailHaut", // Textures corps
                "tailDroite", "tailBas", "tailGauche"}; // Textures queues
        this.snakeTexture.put("blue", new HashMap<>());
        this.snakeTexture.put("red", new HashMap<>());
        for (String color: snakeTexture.keySet()) {
            for (String part: parts) {
                this.snakeTexture.get(color).put(part, new Texture("snake/" + color + "/" + part + ".png"));
            }
        }
    }
}
