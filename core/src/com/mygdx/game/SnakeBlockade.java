package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.HashMap;

/*
Classe SnakeBlockade.java
Classe principale qui sert de point de départ pour notre jeu, elle est appelée dans DesktopLauncher:main
Notre classe hérite de ApplicationAdapter, qui facilite l'implémentation du cycle de vie de l'application
*/

public class SnakeBlockade extends ApplicationAdapter {
	SpriteBatch batch;
	Assets assets;
	Scene scene;
	Board board;
	Objects objects;
	Snake currentSnake, snake1, snake2;

	int lapWithoutStrawberry = 0;
	int lapWithoutGrow = 2;
	HashMap<Snake, Boolean> haveToGrow;
	boolean gameOver = false;
	boolean justGameOver = false;

	// Cette fonction est exécuté lors de la création de l'application, pas besoin de l'appeler
	@Override // Réécrit la méthode parente (de ApplicationAdapter)
	public void create () {
		batch = new SpriteBatch(); // "Toile" sur laquelle on va pouvoir mettre nos textures
		assets = new Assets();
		scene = new Scene(batch, assets);
	}

	// Fonction boucle de jeu, elle agit comme une boucle infinie, pas besoin de l'appeler
	// "Véritable" fonction main, exécuté 30 fois par seconde (car 30 FPS)
	@Override
	public void render () {
		// Nettoyage de l'écran
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		scene.drawBoard();

		if (scene.playButtonPressed) initNewGame();

		manageInput();

		if (scene.gameOn || gameOver) {
			objects.drawObjects();
			snake1.drawSnake();
			snake2.drawSnake();
			scene.drawCross();

			// Si Game over
			if (justGameOver) {
				changeSnake(); // Celui qui a fait le coup perdant est le snake d'avant
				justGameOver = false;
				addCross(currentSnake.futureHead(currentSnake.gameOverDirection));
				setGameOver();
			}

			// Si snake bloqué (sans faire de coup)
			if (currentSnake.isBlocked(haveToGrow.get(currentSnake)) && !gameOver) {
				gameOver = true;
				crossIsBlocked();
				setGameOver();
			}

			if (lapWithoutStrawberry >= 8 * 2) { // Car 2 snakes
				objects.addStrawberry();
				lapWithoutStrawberry = 0;
			}
			if (lapWithoutGrow >= scene.n * 2) {
				haveToGrow.replace(snake1, true);
				haveToGrow.replace(snake2, true);
				lapWithoutGrow = 0;
			}
		}

		scene.drawBan();
		if (scene.menuToggle) scene.menuDesign();
		batch.end();
	}

	// Fonction qui permet d'initialiser une nouvelle partie selon les infos données (objets, variables, ...)
	public void initNewGame() {
		board = new Board(scene);

		objects = new Objects(batch, assets, scene, board);
        objects.objectsEnabled = scene.selectedMod.equals(Global.JVJ) || scene.selectedMod.equals(Global.JVJONLINE);
		objects.initObjects(scene.boardTilesRatio / 8, (int) (scene.boardTilesRatio / 1.5));

		// Paramètrage par rapport au mode choisi
		if (scene.selectedMod.equals(Global.JVJ)) {
			snake1 = new Snake(batch, assets, scene, board, objects, Global.LEFT);
			snake2 = new Snake(batch, assets, scene, board, objects, Global.RIGHT);
			scene.setPlayersNames(Global.J1, Global.J2);
		} else if (scene.selectedMod.equals(Global.JVIA)) {
			snake1 = new Snake(batch, assets, scene, board, objects, Global.LEFT);
			snake2 = new IAG20(batch, assets, scene, board, objects, Global.RIGHT);
			((IAG20) snake2).otherSnake = snake1; // Appel à une instance spécifique de IAG20 grâce au casting
			System.out.println(snake2);
			scene.setPlayersNames(Global.J1, Global.IAG20);
		} else if (scene.selectedMod.equals(Global.IAVIA)) {
			// Networking
		} else {
			// Networking
		}
		snake1.initSnake();
		snake2.initSnake();
		currentSnake = snake1;

		scene.playButtonPressed = false;
		lapWithoutStrawberry = 0;
		lapWithoutGrow = 2;
		haveToGrow = new HashMap<Snake, Boolean>() {{put(snake1, false); put(snake2, false);}};
		gameOver = false;
		justGameOver = false;
	}

	// Ensemble de directives exécutés quand un snake se déplace
	public void actionsMove(String direction) {
		currentSnake.setDirection(direction);
		justGameOver = !(currentSnake.move(haveToGrow.get(currentSnake))); // Le snake bouge ici
		gameOver = justGameOver; // justGameOver = true sur 1 frame seulement
		lapWithoutStrawberry++;
		lapWithoutGrow++;
		haveToGrow.replace(currentSnake, false);

		Snake otherSnake = getOtherSnake();
		if (otherSnake instanceof IAG20) {
			((IAG20) otherSnake).otherSnake = currentSnake;
		}
		changeSnake(); // Au tour de l'autre snake
	}

	// Ensemble de directives exécutés quand un Game Over se produit
	public void setGameOver() {
		scene.gameOn = false;
		scene.menuToggle= true;
		scene.firstMenuToggle = true;
		if (currentSnake == snake1) {
			scene.playerWon = Global.RIGHT;
			scene.mainText = scene.rightPlayer + " wins!";
		} else {
			scene.playerWon = Global.LEFT;
			scene.mainText = scene.leftPlayer + " wins!";
		}
	}

	// Fonction exécuté 1 fois par frame (30 fois par seconde si 30 FPS) qui permet de gérer les évenements utilisateur (touches du clavier, click, ...)
	public void manageInput() {
		// Touches pour bouger
		if (scene.gameOn && !scene.menuToggle) { // LibGDX pense que notre clavier est QWERTY !
			if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) actionsMove(Global.HAUT);
			if (Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) actionsMove(Global.DROITE);
			if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) actionsMove(Global.BAS);
			if (Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) actionsMove(Global.GAUCHE);
		}

		// Touches générales
		// Menu
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			scene.menuToggle = !scene.menuToggle;
			scene.firstMenuToggle = true;
		}

		// Clicks
		if (Gdx.input.justTouched()) {
			float x = Gdx.input.getX();
			// On inverse Y car, pour les fenêtres, l'origine (0, 0) des clicks est en haut à gauche (alors que pour libGDX c'est en bas à gauche)
			float y = Gdx.graphics.getHeight() - Gdx.input.getY();

			if (scene.menu.contains(x, y)) {
				scene.menuToggle = !scene.menuToggle;
				scene.firstMenuToggle = true;
			}

			if (scene.sound.contains(x, y)) {
				scene.isSoundOn = !scene.isSoundOn;
			}
		}
	}

	// Ajout de croix tout autour de la tête du snake
	public void crossIsBlocked() {
		addCross(currentSnake.futureHead(Global.HAUT));
		addCross(currentSnake.futureHead(Global.DROITE));
		addCross(currentSnake.futureHead(Global.BAS));
		addCross(currentSnake.futureHead(Global.GAUCHE));
	}

	// Fonction qui ajoute une position de croix pour dire où le snake s'est planté
	public void addCross(ArrayList<Integer> pos) {
		ArrayList<Float> newPos = new ArrayList<>();
		newPos.add((float) pos.get(0));
		newPos.add((float) pos.get(1));
		if (board.outsideLimits.contains(pos)) {
			if (newPos.get(0) < 0) newPos.set(0, -1 + 0.5f);
			else if (newPos.get(0) > scene.boardTilesRatio - 1) newPos.set(0, (scene.boardTilesRatio - 1) + 0.5f);
			else if (newPos.get(1) < 0) newPos.set(1, -1 + 0.5f);
			else if (newPos.get(1) > scene.boardTilesRatio - 1) newPos.set(1, (scene.boardTilesRatio - 1) + 0.5f);
		}
		scene.crossList.add(newPos);
	}

	// Fonction permettant de changer de joueur (de permuter)
	public void changeSnake() {
		if (currentSnake == snake1) currentSnake = snake2;
		else currentSnake = snake1;
		scene.isLeftPlaying = !scene.isLeftPlaying;
	}

	// Fonction qui renvoie le snake ennemi
	public Snake getOtherSnake() {
		if (currentSnake == snake1) return snake2;
		else return snake1;
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
