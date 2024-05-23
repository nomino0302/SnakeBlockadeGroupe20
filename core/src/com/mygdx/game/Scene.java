package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.ArrayList;

/*
Classe Scene.java
Gère en grande partie la partie graphique de l'application (menu, bannière, ...) et les interactions utilisateur (Widgets)
Les fonctions libGDX étant parfois complexes, des fonctions de + de 10 lignes peuvent être présentes
*/

public class Scene {
    SpriteBatch batch;
    Assets assets;
    ShapeRenderer shapeRenderer;
    FreeTypeFontGenerator generator;
    FreeTypeFontParameter parameter;

    // Polices d'écritures
    BitmapFont titleFont, writingFont, gameInfosFont, littleGameInfosFont, leftPlayerFont, rightPlayerFont;
    GlyphLayout layout;

    // Widgets interactifs
    Stage stage;
    Skin skin;
    ArrayList<Actor> widgets = new ArrayList<>();
    TextField columnsField, linesField, nField, codeChannelReadField, codeChannelWriteField;
    TextButton radioJVJ, radioJVIA, radioIAVIA, radioJ1, radioJ2, playButton;
    ButtonGroup<Button> radioGroup, playerGroup;

    // Instances d'objet qui seront utiles pour communiquer avec les autres classes
    int lines = 16;
    int columns = 16;
    int min = 16;
    int max = 16;
    int n = 2;
    String codeChannelRead = "";
    String codeChannelWrite = "";
    boolean onlinePlayFirst = true;

    boolean isSoundOn = true, isStrawberryOn = true, isRockOn = true;
    boolean firstMenuToggle = true;
    boolean menuToggle = true;
    boolean gameOn = false;
    boolean playButtonPressed = false;
    boolean isLeftPlaying = false;
    boolean isBlinking = false;
    String leftPlayer = "", rightPlayer = "";
    String mainText = "Snake Blockade";
    String selectedMod = null;
    String playerWon = null;
    long lastNanoBlink = TimeUtils.nanoTime(); // long car ça peut être un grand nombre
    float pixelsForTile = (float) Global.WIDTH / max;
    float xOff = (pixelsForTile * (max - columns)) / 2; // Pour centrer le plateau
    float yOff = (pixelsForTile * (max - lines)) / 2;
    ArrayList<ArrayList<Float>> crossList = new ArrayList<>();

    // Objets libGDX redondants
    Rectangle board = new Rectangle(0, 0, Global.WIDTH, Global.BOARD_HEIGHT);
    Rectangle ban = new Rectangle(0, Global.BOARD_HEIGHT, Global.WIDTH, Global.BAN_HEIGHT);
    Rectangle menu = new Rectangle((ban.width / 2) - ((float) (25 * 4 + 10 * 3) / 2), board.height + 5, 25, 25);
    Rectangle sound = new Rectangle(menu.x + 25 + 10, menu.y, menu.width, menu.height);
    Color darkGreen = Color.valueOf("547436"); // Hexadécimal
    Color transparentBlack = new Color(0, 0, 0, 0.8f);
    Color clearTileGreen = Color.valueOf("ADDD46");
    Color darkTileGreen = Color.valueOf("98CB40");

    // Constructeur pour les objets lourds non instanciés
    Scene(SpriteBatch batch, Assets assets) {
        this.batch = batch;
        this.assets = assets;
        this.shapeRenderer = new ShapeRenderer();
        this.parameter = new FreeTypeFontParameter();

        this.titleFont = createFont("fonts/joystix_monospace.otf", 30, Color.WHITE);
        this.writingFont = createFont("fonts/arial.ttf", 18, Color.WHITE);
        this.gameInfosFont = createFont("fonts/joystix_monospace.otf", 14, Color.WHITE);
        this.littleGameInfosFont = createFont("fonts/joystix_monospace.otf", 14, Color.LIGHT_GRAY);
        this.leftPlayerFont = createFont("fonts/joystix_monospace.otf", 20, Color.SKY);
        this.rightPlayerFont = createFont("fonts/joystix_monospace.otf", 20, Color.RED);
        this.layout = new GlyphLayout(); // Pour mesurer la taille du texte

        // Pour la création de Widget interactifs
        this.stage = new Stage();
        Gdx.input.setInputProcessor(this.stage);
        // Skin proposé par la communauté libGDX (https://github.com/czyzby/gdx-skins/)
        this.skin = new Skin(Gdx.files.internal("skins/gdx-holo/skin/uiskin.json"));

        // Setup radioGroup
        radioGroup = new ButtonGroup<Button>();
        radioGroup.setMaxCheckCount(1);
        radioGroup.setMinCheckCount(1);
        radioGroup.setUncheckLast(true);

        playerGroup = new ButtonGroup<Button>();
        playerGroup.setMaxCheckCount(1);
        playerGroup.setMinCheckCount(1);
        radioGroup.setUncheckLast(true);

        // On instancie ici nos Widgets
        createWidgets();
    }

    /*
    Fonctions publiques
     */

    /*
    Partie affichage des différents états du jeu
     */

    // Design de menu (quand le jeu ne joue pas)
    public void menuDesign() {
        drawRect(0, 0, board.width, board.height, transparentBlack, true, true);
        drawLabels();

        // Fonction exécuté 1 seule fois (quand on ouvre le menu)
        if (firstMenuToggle) {
            addToStage(stage, widgets);
            firstMenuToggle = false;
        }
    }

    /*
    Partie affichage plateau de jeu
    */

    // Plateau en damier
    public void drawBoard() {
        // On draw le fond du terrain d'abord
        batch.draw(assets.get("design/bg.png", Texture.class), 0, 0, board.width, board.height);

        batch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        Color columnFirstColor = clearTileGreen;
        Color rowFirstColor;
        for (int i = 0; i < columns; i++) {
            rowFirstColor = columnFirstColor;
            for (int j = 0; j < lines; j++) {
                drawRect(xOff + i * pixelsForTile, yOff + j * pixelsForTile, pixelsForTile, pixelsForTile, rowFirstColor, false, false);
                rowFirstColor = changeTileColor(rowFirstColor);
            }
            columnFirstColor = changeTileColor(columnFirstColor);
        }
        shapeRenderer.end();
        batch.begin();
    }

    // Fonction qui permet de dessiner les croix sur le plateau (la où le snake s'est crashé)
    public void drawCross() {
        for (ArrayList<Float> pos: crossList) {
            batch.draw(assets.get("design/cross.png", Texture.class), xOff + pos.get(0) * pixelsForTile, yOff + pos.get(1) * pixelsForTile, pixelsForTile, pixelsForTile);
        }
    }

    /*
    Partie affichage bannière
     */

    // Design de la bannière
    public void drawBan() {
        drawRect(ban.x, ban.y, ban.width, ban.height, darkGreen, true, false);
        gameInfos();
        drawGameIcons();
        showPlayersAvatars();
        showPlayersNames();
        if (!gameOn && playerWon != null) {
            showPlayerTrophy(playerWon); // LEFT OU RIGHT
        }
    }

    // Infos au milieu de la bannière
    public void gameInfos() {
        layout.setText(gameInfosFont, "N = " + n);
        gameInfosFont.draw(batch, layout, (ban.width / 2) - (layout.width / 2), Global.HEIGHT - 5);
        layout.setText(littleGameInfosFont, "R: " + codeChannelRead);
        littleGameInfosFont.draw(batch, layout, (ban.width / 2) - (layout.width / 2), Global.HEIGHT - layout.height - 10);
        layout.setText(littleGameInfosFont, "W: " + codeChannelWrite);
        littleGameInfosFont.draw(batch, layout, (ban.width / 2) - (layout.width / 2), Global.HEIGHT - layout.height * 2 - 15);
        drawRect((ban.width / 2) - 80, board.height + 6, 1, ban.height - 12, Color.WHITE, true, false);
        drawRect((ban.width / 2) + 80, board.height + 6, 1, ban.height - 12, Color.WHITE, true, false);
    }

    // Icones au milieu de la bannière
    public void drawGameIcons() {
        batch.draw(assets.get("design/esc.png", Texture.class), menu.x, menu.y, menu.width, menu.height);
        batch.draw(assets.get("design/sound.png", Texture.class), sound.x, sound.y, sound.width, sound.height);
        batch.draw(assets.get("objects/strawberry.png", Texture.class), sound.x + 25 + 10, sound.y, sound.width, sound.height);
        batch.draw(assets.get("objects/rock.png", Texture.class), sound.x + 50 + 20, sound.y, sound.width, sound.height);
        Texture stop = assets.get("design/cross.png", Texture.class);
        if (!isSoundOn) {
            batch.draw(stop, sound.x, sound.y, sound.width, sound.height);
        }
        if (!isStrawberryOn) {
            batch.draw(stop, sound.x + 25 + 10, sound.y, sound.width, sound.height);
        }
        if (!isRockOn) {
            batch.draw(stop, sound.x + 50 + 20, sound.y, sound.width, sound.height);
        }
    }

    // Avatars dans la bannière
    public void showPlayersAvatars() {
        // Image tête snake = 35px
        batch.draw(assets.get("snake/blue_headBas.png", Texture.class), (ban.width / 4) - (35f / 2) - 35, Global.HEIGHT - 40);
        batch.draw(assets.get("snake/red_headBas.png", Texture.class), (ban.width  / 4 * 3) - (35f / 2) + 35, Global.HEIGHT - 40);
    }

    // Noms des joueurs dans la bannière
    public void showPlayersNames() {
        if (!isBlinking || !isLeftPlaying || !gameOn) {
            layout.setText(leftPlayerFont, leftPlayer);
            leftPlayerFont.draw(batch, layout, (ban.width / 4) - (layout.width / 2) - 35, Global.HEIGHT - 40 - layout.height + 5);
        }
        if (!isBlinking || isLeftPlaying || !gameOn) {
            layout.setText(rightPlayerFont, rightPlayer);
            rightPlayerFont.draw(batch, layout, (ban.width  / 4 * 3) - (layout.width / 2) + 35, Global.HEIGHT - 40 - layout.height + 5);
        }
        // Si temps qui s'est écroulé > 0.5 seconde
        if (TimeUtils.nanoTime() - lastNanoBlink > 500000000) {
            isBlinking = !isBlinking;
            lastNanoBlink = TimeUtils.nanoTime();
        }
    }

    // Trophée du vainqueur dans la bannière
    public void showPlayerTrophy(String side) {
        Texture texture = assets.get("design/trophy.png", Texture.class);
        if (side.equals(Global.LEFT)) {
            batch.draw(texture, 15, ((float) Global.HEIGHT - (ban.height / 2)) - ((float) texture.getHeight() / 2));
        }
        if (side.equals(Global.RIGHT)) {
            batch.draw(texture, ban.width - texture.getWidth() - 15, ((float) Global.HEIGHT - (ban.height / 2)) - ((float) texture.getHeight() / 2));
        }
    }

    /*
    Partie affichage menu
     */

    // Les champs présents dans le menu
    public void drawLabels() {
        mainTitle();
        showControls();
        showControls();
        modsText();
        drawFieldsTexts();
        drawStage(); // Dessiner tous les Widgets interactifs
    }

    // Titre du menu, peut être personnalisé
    public void mainTitle() {
        layout.setText(titleFont, mainText);
        titleFont.draw(batch, layout, (board.width / 2) - (layout.width / 2), board.height - 20);
    }

    // Images indiquant les touches pour contrôler les snakes
    public void showControls() {
        j1ControlsImage();
        j2ControlsImage();
    }

    // Touches joueur 1
    public void j1ControlsImage() {
        layout.setText(leftPlayerFont, Global.J1);
        leftPlayerFont.draw(batch, layout, 50, board.height - 80);
        Texture img = assets.get("design/zqsd.png", Texture.class);
        float ratio = (float) img.getHeight() / img.getWidth();
        batch.draw(img, 40, board.height - 200, 190, 200 * ratio);
    }

    // Touches joueur 2
    public void j2ControlsImage() {
        layout.setText(rightPlayerFont, Global.J2);
        rightPlayerFont.draw(batch, layout, board.width - 50 - layout.width, board.height - 80);
        Texture img = assets.get("design/fleches.png", Texture.class);
        float ratio = (float) img.getHeight() / img.getWidth();
        batch.draw(img, board.width - 40 - ((float) img.getWidth() / 2), board.height - 190, 200, 200 * ratio);
    }

    // Texte "Modes : "
    public void modsText() {
        layout.setText(writingFont, "Modes :");
        writingFont.draw(batch, layout, (board.width / 2) - (layout.width / 2),board.height - 215);
    }

    // Textes avant les inputs
    public void drawFieldsTexts() {
        layout.setText(writingFont, "X*X cases =");
        writingFont.draw(batch, layout, 10, board.height - 370);
        drawRect(140, board.height - 370 - layout.height - 2, 50, 1, Color.WHITE, true, false);
        layout.setText(writingFont, "x");
        writingFont.draw(batch, layout, 190 + 15 - (layout.width / 2), board.height - 370);
        drawRect(220, board.height - 370 - layout.height - 2, 50, 1, Color.WHITE, true, false);

        layout.setText(writingFont, "N =");
        writingFont.draw(batch, layout, (board.width / 2) + 10,board.height - 370);
        drawRect((board.width / 2) + 140, board.height - 370 - layout.height - 2, 130, 1, Color.WHITE, true, false);

        layout.setText(writingFont, "Channel read =");
        writingFont.draw(batch, layout, 10,board.height - 410);
        drawRect(140, board.height - 410 - layout.height - 2, 130, 1, Color.WHITE, true, false);

        layout.setText(writingFont, "Channel write =");
        writingFont.draw(batch, layout, (board.width / 2) + 10,board.height - 410);
        drawRect((board.width / 2) + 140, board.height - 410 - layout.height - 2, 130, 1, Color.WHITE, true, false);
    }

    // Affichage des Widget interactifs
    public void drawStage() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    // Fonction qui regroupe la création de tous les Widgets (appelé par le constructeur)
    public void createWidgets() {
        // Boutons radios
        radioJVJ = createRadioButton(widgets, radioGroup, "Joueur VS Joueur", (board.width / 2) - 250 - 10, board.height - 300, 250);
        radioJVIA = createRadioButton(widgets, radioGroup, "Joueur VS IA", (board.width / 2) - 250 - 10, board.height - 350, 250);
        radioIAVIA = createRadioButton(widgets, radioGroup, "[EN LIGNE] IA VS IA", (board.width / 2) + 10, board.height - 300, 250);

        // TextField (inputs)
        columnsField = createTextField(widgets, Integer.toString(columns), 140 - 10, board.height - 370 - 20, 70, 30);
        onlyNumbersFilter(columnsField);
        linesField = createTextField(widgets, Integer.toString(lines), 220 - 10, board.height - 370 - 20, 70, 30);
        onlyNumbersFilter(linesField);
        nField = createTextField(widgets, Integer.toString(n), (board.width / 2) + 140 - 10, board.height - 370 - 20, 150, 30);
        onlyNumbersFilter(nField);
        codeChannelReadField = createTextField(widgets, "", 140 - 10, board.height - 410 - 20, 150, 30);
        codeChannelWriteField = createTextField(widgets, "", (board.width / 2) + 140 - 10, board.height - 410 - 20, 150, 30);

        // Boutons radios joueurs
        radioJ1 = createRadioButton(widgets, playerGroup, "J1 (2, 2)", (board.width / 2) - 250 - 10, board.height - 490, 250);
        radioJ2 = createRadioButton(widgets, playerGroup, "J2 (9, 19)", (board.width / 2) + 10, board.height - 490, 250);

        // Bouton cliquable
        playButton = createClickableButton(widgets, "Jouer", (board.width / 2) - ((float) 150 / 2), 10, 150, 50);
        startGameButtonEventListener(playButton);
    }

    // Donner des noms aux joueurs
    public void setPlayersNames(String leftName, String rightName) {
        leftPlayer = leftName;
        rightPlayer = rightName;
    }

    // Actions quand le bouton "Jouer" est pressé
    public void startGameButtonActions() {
        // On récupère le contenu des inputs
        if (!columnsField.getText().isEmpty()) {
            int tempNb = Integer.parseInt(columnsField.getText());
            if (tempNb >= 2 && tempNb <= 300) setColumns(tempNb);
        }
        if (!linesField.getText().isEmpty()) {
            int tempNb = Integer.parseInt(linesField.getText());
            if (tempNb >= 2 && tempNb <= 300) setLines(tempNb);
        }
        if (!nField.getText().isEmpty()) {
            int tempNb = Integer.parseInt(nField.getText());
            if (tempNb >= 1) n = tempNb;
        }
        if (!codeChannelReadField.getText().isEmpty()) codeChannelRead = codeChannelReadField.getText();
        if (!codeChannelWriteField.getText().isEmpty()) codeChannelWrite = codeChannelWriteField.getText();

        // Paramètres spécifiques aux modes
        disableObjects();
        if (radioGroup.getChecked() == radioJVJ) {
            selectedMod = Global.JVJ;
            enableObjects();
        } else if (radioGroup.getChecked() == radioJVIA) {
            selectedMod = Global.JVIA;
        } else if (radioGroup.getChecked() == radioIAVIA) {
            selectedMod = Global.IAVIA;
            isRockOn = true;
            n = 4;
            setColumns(22);
            setLines(12);
            // J1 ou J2 ? (pour le mode en ligne seulement)
            onlinePlayFirst = playerGroup.getChecked() == radioJ1;
        }

        playButtonPressed = true;
        menuToggle = false;
        gameOn = true;
        isLeftPlaying = true;
        mainText = "Pause";
        crossList.clear();
        // On supprime le contenu de Stage pour qu'on ne puisse pas cliquer sur les Widgets quand ils ne sont pas visibles
        stage.clear();
    }

    /*
    Fonctions privées
    Seulement accessible par les méthodes de la classe
    Les méthodes les plus générales, moins orientées vers l'affichage, se trouvent ici
     */

    // Fonction pour dessiner des rectangles de couleur
    private void drawRect(float x, float y, float width, float height, Color color, boolean autoSetShape, boolean enableBlend) {
        if (autoSetShape) {
            batch.end(); // On ferme le batch pour que SpriteBatch et ShapeRenderer ne soient pas ouvert en même temps
            if (enableBlend) {
                // Pour activer la transparence
                Gdx.gl.glEnable(GL20.GL_BLEND);
                Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            }
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        }

        shapeRenderer.setColor(color);
        shapeRenderer.rect(x, y, width, height);

        if (autoSetShape) {
            shapeRenderer.end();
            if (enableBlend) {
                // Pour désactiver la transparence
                Gdx.gl.glDisable(GL20.GL_BLEND);
            }
            batch.begin();
        }
    }

    // Fonction qui retourne un input de type TextField
    private TextField createTextField(ArrayList<Actor> actors, String baseText, float x, float y, float width, float height) {
        TextField textField = new TextField(baseText, skin);
        textField.setColor(Color.WHITE);
        textField.setPosition(x, y);
        textField.setSize(width, height);
        actors.add(textField); // On ajoute cet Actor (Widget) à la liste des Actors
        return textField;
    }

    // Fonction qui permet la création de les bouton radio (un seul sélectionné)
    private TextButton createRadioButton(ArrayList<Actor> actors, ButtonGroup<Button> buttonGroup, String baseText, float x, float y, float width) {
        TextButton radioButton = new TextButton(baseText, skin, "toggle");
        radioButton.setWidth(width);
        radioButton.setPosition(x, y);
        buttonGroup.add(radioButton);
        actors.add(radioButton);
        return radioButton;
    }

    // Fonction qui permet la création de bouton cliquable
    private TextButton createClickableButton(ArrayList<Actor> actors, String baseText, float x, float y, float width, float height) {
        TextButton clickableButton = new TextButton(baseText, skin);
        clickableButton.setSize(width, height);
        clickableButton.setPosition(x, y);
        actors.add(clickableButton);
        return clickableButton;
    }

    // Fonction permettant d'ajouter plusieurs Actors dans un Stage
    private void addToStage(Stage stage, ArrayList<Actor> actors) {
        for (Actor actor: actors) {
            stage.addActor(actor);
        }
    }

    // Retourne une nouvelle police d'écriture
    private BitmapFont createFont(String path, int size, Color color) {
        if (generator != null) {
            generator.dispose();
            generator = null;
        }
        generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        parameter.size = size;
        parameter.color = color;
        return generator.generateFont(parameter);
    }

    // Fonction pour alterner la couleur du damier
    private Color changeTileColor(Color color) {
        if (color == clearTileGreen) return darkTileGreen;
        else return clearTileGreen;
    }

    // Autoriser les objets
    private void enableObjects() {
        isStrawberryOn = true;
        isRockOn = true;
    }

    // Interdire les objets
    private void disableObjects() {
        isStrawberryOn = false;
        isRockOn = false;
    }

    // Modifier la taille du plateau de jeu (colonnes = x)
    private void setColumns(int val) {
        columns = val;
        updateRatios();
    }

    // Modifier la taille du plateau de jeu (lignes = y)
    private void setLines(int val) {
        lines = val;
        updateRatios();
    }

    private void updateRatios() {
        min = Math.min(columns, lines);
        max = Math.max(columns, lines);
        pixelsForTile = (float) Global.WIDTH / max;
        xOff = (pixelsForTile * (max - columns)) / 2;
        yOff = (pixelsForTile * (max - lines)) / 2;
    }

    /*
    Déclarations des events listeners
    --------------------------------
    Ce sont des fonctions qui vont ajouter d'autres fonctions à nos Widgets
    qui vont écouter des évenements utilisateur (click gauche, touche pressée) et qui vont
    exécuter des actions précises quand elles vont se déclencher
     */

    // Pour boardTilesRatioField et nField
    // Charactère tapé --> Verification si chiffre
    private void onlyNumbersFilter(TextField textField) {
        textField.setTextFieldFilter(new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                return Character.isDigit(c);
            }
        });
    }

    // Pour playButton
    // Bouton appuyé --> Lancement de la partie
    private void startGameButtonEventListener(TextButton button) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startGameButtonActions();
            }
        });
    }

    /*
    Fin des déclarations des events listeners
     */

    // Polices d'écritures à disposer
    private void disposeFonts() {
        titleFont.dispose();
        writingFont.dispose();
        gameInfosFont.dispose();
        littleGameInfosFont.dispose();
        leftPlayerFont.dispose();
        rightPlayerFont.dispose();
    }

    // Disposer le shapeRenderer et le générateur de polices d'écritures
    public void dispose() {
        shapeRenderer.dispose();
        if (generator != null) generator.dispose(); // if en 1 ligne
        disposeFonts();
        stage.dispose();
        skin.dispose();
    }
}
