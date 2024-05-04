package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

// Classe principale qui sert de point de départ pour notre jeu, elle est appelée dans DesktopLauncher:main
// Notre classe hérite de ApplicationAdapter, qui facilite l'implémentation du cycle de vie de l'application
public class SnakeBlockade extends ApplicationAdapter {
	SpriteBatch batch;
	Assets assets;
	Scene scene;

	// Cette fonction est exécuté lors de la création de l'application, pas besoin de l'appeler
	@Override // Réécrit la méthode parente (de ApplicationAdapter)
	public void create () {
		batch = new SpriteBatch(); // "Toile" sur laquelle on va pouvoir mettre nos textures
		assets = new Assets();
		scene = new Scene(batch, assets);
	}

	// Fonction boucle de jeu, elle agit comme une boucle infinie, pas besoin de l'appeler
	// "Véritable" fonction main
	@Override
	public void render () {
		batch.begin();
		scene.baseDesign();
		batch.end();
	}

	// Fonction exécuté lorsque l'application se ferme, pas besoin de l'appeler
	@Override
	public void dispose () {
		// Libère l'espace alloué aux ressources
		batch.dispose();
		assets.dispose();
		scene.dispose();
	}
}
