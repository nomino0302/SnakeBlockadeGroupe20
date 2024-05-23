package com.mygdx.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

/*
Classe DesktopLauncher.java
Point de départ de notre application (fonction main)
Classe qui permet de configurer et lancer une application de bureau avec le code écrit dans core/src/
*/

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	// Fonction main, le véritable coeur de l'application se trouve dans SnakeBlockade:render
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(30);
		config.setTitle("Snake Blockade - Groupe 20");

		// Taille de la fenêtre, valeurs dans le fichier Constants.java (même package donc pas besoin d'importer la classe)
		config.setWindowedMode(Global.WIDTH, Global.HEIGHT); // Taille de la fenêtre en pixels
		config.setResizable(false);

		// Arguments passés au script
		boolean args = false;
		String writeArg = "";
		String readArg = "";
		String playerArg = "";
		if (arg.length >= 3) {
			args = true;
			writeArg = arg[0];
			readArg = arg[1];
			playerArg = arg[2];
		}

		// Lance une instance de notre classe de jeu personnalisé
		new Lwjgl3Application(new SnakeBlockade(args, writeArg, readArg, playerArg), config);
	}
}
