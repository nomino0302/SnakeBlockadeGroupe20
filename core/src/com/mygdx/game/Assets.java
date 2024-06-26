package com.mygdx.game;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.assets.AssetManager;

/*
Classe Assets.java
Classe regroupant toutes les textures utilisées, on utilise un peu plus que 10 lignes pour mapper tout les assets
AssetManager va nous permettre de libérer les assets efficacement lorsqu'on en aura plus besoin
*/

public class Assets extends AssetManager {
    String[] snakeParts = new String[] {"headHaut", "headDroite", "headBas", "headGauche", // Textures têtes
            "droiteHaut", "droiteBas", "gaucheHaut", "gaucheBas", "hori", "verti", "tailHaut", // Textures corps
            "tailDroite", "tailBas", "tailGauche"}; // Textures queues
    String[] snakeColors = new String[] {"blue", "red"};

    Assets() {
        // Textures
        this.load("design/bg.png", Texture.class);
        this.load("design/trophy.png", Texture.class);
        this.load("design/zqsd.png", Texture.class);
        this.load("design/sound.png", Texture.class);
        this.load("design/cross.png", Texture.class);
        this.load("design/fleches.png", Texture.class);
        this.load("design/esc.png", Texture.class);
        this.load("objects/strawberry.png", Texture.class);
        this.load("objects/rock.png", Texture.class);

        // Textures du serpent
        for (String color: snakeColors) {
            for (String part: snakeParts) {
                this.load("snake/" + color + "_" + part + ".png", Texture.class);
            }
        }

        // Musique et sons
        this.load("music/music.mp3", Music.class);
        this.load("music/step.wav", Sound.class);
        this.load("music/win.wav", Sound.class);
        this.load("music/loose.wav", Sound.class);

        // On load toutes les textures avant de lancer le jeu
        this.finishLoading();
    }
}
