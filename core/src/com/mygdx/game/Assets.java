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
        this.snakeTexture.put("blue", new HashMap<>());
        this.snakeTexture.put("red", new HashMap<>());
        for (String key: snakeTexture.keySet()) {
            // Textures têtes
            this.snakeTexture.get(key).put("headHaut", new Texture("snake/" + key + "/snake_head_haut.png"));
            this.snakeTexture.get(key).put("headDroite", new Texture("snake/" + key + "/snake_head_droite.png"));
            this.snakeTexture.get(key).put("headBas", new Texture("snake/" + key + "/snake_head_bas.png"));
            this.snakeTexture.get(key).put("headGauche", new Texture("snake/" + key + "/snake_head_gauche.png"));

            // Textures corps
            this.snakeTexture.get(key).put("droiteHaut", new Texture("snake/" + key + "/snake_droite_haut.png"));
            this.snakeTexture.get(key).put("droiteBas", new Texture("snake/" + key + "/snake_droite_bas.png"));
            this.snakeTexture.get(key).put("gaucheHaut", new Texture("snake/" + key + "/snake_gauche_haut.png"));
            this.snakeTexture.get(key).put("gaucheBas", new Texture("snake/" + key + "/snake_gauche_bas.png"));
            this.snakeTexture.get(key).put("hori", new Texture("snake/" + key + "/snake_hori.png"));
            this.snakeTexture.get(key).put("verti", new Texture("snake/" + key + "/snake_verti.png"));

            // Textures queues
            this.snakeTexture.get(key).put("tailHaut", new Texture("snake/" + key + "/snake_tail_haut.png"));
            this.snakeTexture.get(key).put("tailDroite", new Texture("snake/" + key + "/snake_tail_droite.png"));
            this.snakeTexture.get(key).put("tailBas", new Texture("snake/" + key + "/snake_tail_bas.png"));
            this.snakeTexture.get(key).put("tailGauche", new Texture("snake/" + key + "/snake_tail_gauche.png"));
        }
    }
}
