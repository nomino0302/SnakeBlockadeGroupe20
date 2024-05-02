package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

// Classe principale qui sert de point de départ pour notre jeu, elle est appelée dans DesktopLauncher:main
// Notre classe hérite de ApplicationAdapter, qui facilite l'implémentation du cycle de vie de l'application
public class SnakeBlockade extends ApplicationAdapter {
	SpriteBatch batch;
	Assets assets;

	// Cette fonction est exécuté lors de la création de l'application, pas besoin de l'appeler
	@Override // Réécrit la méthode parente (de ApplicationAdapter)
	public void create () {
		batch = new SpriteBatch(); // "Toile" sur laquelle on va pouvoir mettre nos textures
		assets = new Assets();
	}

	// Fonction boucle de jeu, elle agit comme une boucle infinie, pas besoin de l'appeler
	// "Véritable" fonction main
	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1);
		batch.begin();
		batch.draw(assets.strawberryTexture, 0, 0);
		batch.end();
	}

	// Fonction exécuté lorsque l'application se ferme, pas besoin de l'appeler
	@Override
	public void dispose () {
		batch.dispose(); // Libère l'espace alloué à la ressource
		assets.strawberryTexture.dispose();
	}
}
