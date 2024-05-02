package com.mygdx.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
// Classe qui permet de configurer et lancer une application de bureau avec le code écrit dans core/src/
public class DesktopLauncher {
	// Fonction main, le véritable coeur de l'application se trouve dans SnakeBlockade:render
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("Snake Blockade - Groupe 20");
		// Lance une instance de notre classe de jeu personnalisé
		new Lwjgl3Application(new SnakeBlockade(), config);
	}
}
