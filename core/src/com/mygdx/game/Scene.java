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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;

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
    BitmapFont titleFont, writingFont, gameInfosFont, leftPlayerFont, rightPlayerFont;
    GlyphLayout layout;

    // Widgets interactifs
    Stage stage;
    Skin skin;
    TextField boardTilesRatioField, nField, codeChannelField;
    TextButton radioJVJ, radioJVIA, radioIAVIA, radioJVJOnline, playButton;
    ButtonGroup<Button> radioGroup;

    // Instances d'objet qui seront utiles pour communiquer avec les autres classes
    int boardTilesRatio = 16;
    int n = 2;
    String codeChannel = "";

    boolean isSoundOn = true, isStrawberryOn = true, isRockOn = true;
    boolean gameOn = false;
    boolean playButtonPressed = false;
    boolean isLeftPlaying = false;
    boolean isBlinking = false;
    String leftPlayer = "", rightPlayer = "";
    String mainText = "Snake Blockade";
    String selectedMod = null;
    String playerWon = null;
    long lastNanoBlink = TimeUtils.nanoTime(); // long car ça peut être un grand nombre
    float pixelsForTile = (float) Global.WIDTH / boardTilesRatio;

    // Objets libGDX redondants
    Rectangle board = new Rectangle(0, 0, Global.WIDTH, Global.BOARD_HEIGHT);
    Rectangle ban = new Rectangle(0, Global.BOARD_HEIGHT, Global.WIDTH, Global.BAN_HEIGHT);
    Rectangle sound = new Rectangle((ban.width / 2) - ((float) 30 / 2) - 30 - 10, board.height + 5, 30, 30);
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

        // On instancie ici nos Widgets
        createWidgets();
    }

    /*
    Fonctions publiques
     */

    /*
    Partie affichage des différents états du jeu
     */

    // Fonction principale de la classe, elle va être appelé par le programme principal
    public void drawScene() {
        if (gameOn) {
            gameDesign();
        } else {
            menuDesign();
        }
    }

    // Design du plateau de jeu
    public void gameDesign() {
        drawBan();
        drawBoard();
    }

    // Design de menu (quand le jeu ne joue pas)
    public void menuDesign() {
        gameDesign();
        drawMenuBoard();
    }

    /*
    Partie affichage plateau de jeu
     */

    // Plateau en damier
    public void drawBoard() {
        Color columnFirstColor = clearTileGreen;
        Color rowFirstColor;
        for (int i = 0; i < boardTilesRatio; i++) {
            rowFirstColor = columnFirstColor;
            for (int j = 0; j < boardTilesRatio; j++) {
                drawRect(i * pixelsForTile, j * pixelsForTile, pixelsForTile, pixelsForTile, rowFirstColor);
                rowFirstColor = changeTileColor(rowFirstColor);
            }
            columnFirstColor = changeTileColor(columnFirstColor);
        }
    }

    /*
    Partie affichage bannière
     */

    // Design de la bannière
    public void drawBan() {
        drawRect(ban.x, ban.y, ban.width, ban.height, darkGreen);
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
        float last_layout_height = layout.height;
        gameInfosFont.draw(batch, layout, (ban.width / 2) - (layout.width / 2), Global.HEIGHT - layout.height);
        layout.setText(gameInfosFont, codeChannel);
        gameInfosFont.draw(batch, layout, (ban.width / 2) - (layout.width / 2), Global.HEIGHT - last_layout_height - layout.height - 8);
        drawRect((ban.width / 2) - 80, board.height + 6, 1, ban.height - 12, Color.WHITE);
        drawRect((ban.width / 2) + 80, board.height + 6, 1, ban.height - 12, Color.WHITE);
    }

    // Icones au milieu de la bannière
    public void drawGameIcons() {
        batch.draw(assets.get("design/sound.png", Texture.class), sound.x, sound.y, sound.width, sound.height);
        batch.draw(assets.get("objects/strawberry.png", Texture.class), sound.x + 30 + 10, sound.y, sound.width, sound.height);
        batch.draw(assets.get("objects/rock.png", Texture.class), sound.x + 60 + 20, sound.y, sound.width, sound.height);
        Texture stop = assets.get("design/cross.png", Texture.class);
        if (!isSoundOn) {
            batch.draw(stop, sound.x, sound.y, sound.width, sound.height);
        }
        if (!isStrawberryOn) {
            batch.draw(stop, sound.x + 30 + 10, sound.y, sound.width, sound.height);
        }
        if (!isRockOn) {
            batch.draw(stop, sound.x + 60 + 20, sound.y, sound.width, sound.height);
        }
    }

    // Avatars dans la bannière
    public void showPlayersAvatars() {
        batch.draw(assets.get("snake/blue_headBas.png", Texture.class), 85, Global.HEIGHT - 40);
        Texture texture = assets.get("snake/red_headBas.png", Texture.class);
        batch.draw(texture, ban.width - texture.getWidth() - 85, Global.HEIGHT - 40);
    }

    // Noms des joueurs dans la bannière
    public void showPlayersNames() {
        if (!isBlinking || !isLeftPlaying) {
            layout.setText(leftPlayerFont, leftPlayer);
            leftPlayerFont.draw(batch, layout, 105 - layout.width / 2, Global.HEIGHT - 40 - layout.height + 5);
        }
        if (!isBlinking || isLeftPlaying) {
            layout.setText(rightPlayerFont, rightPlayer);
            rightPlayerFont.draw(batch, layout, ban.width - 105 - layout.width / 2, Global.HEIGHT - 40 - layout.height + 5);
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
            batch.draw(texture, 15, board.height + 22);
        }
        if (side.equals(Global.RIGHT)) {
            batch.draw(texture, ban.width - 15 - texture.getWidth(), board.height + 22);
        }
    }

    /*
    Partie affichage menu
     */

    // Plateau de jeu recouvert par le menu (sur un fond noir transparent)
    public void drawMenuBoard() {
        drawRect(0, 0, board.width, board.height, transparentBlack);
        drawLabels();
    }

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
        float maxFieldWidth = layout.width;
        writingFont.draw(batch, layout, 135, board.height - 370);
        drawRect((board.width / 4) + 120, board.height - 370 - layout.height - 2, 150, 1, Color.WHITE);

        layout.setText(writingFont, "N =");
        writingFont.draw(batch, layout, 135 + (maxFieldWidth - layout.width),board.height - 400);
        drawRect((board.width / 4) + 120, board.height - 400 - layout.height - 2, 150, 1, Color.WHITE);

        layout.setText(writingFont, "Channel =");
        writingFont.draw(batch, layout, 135 + (maxFieldWidth - layout.width),board.height - 430);
        drawRect((board.width / 4) + 120, board.height - 430 - layout.height - 2, 150, 1, Color.WHITE);
    }

    // Affichage des Widget interactifs
    public void drawStage() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    // Fonction qui regroupe la création de tous les Widgets (appelé par le constructeur)
    public void createWidgets() {
        // Boutons radios
        radioJVJ = createRadioButton(radioGroup, "Joueur VS Joueur", (board.width / 2) - 250 - 10, board.height - 300, 250);
        radioJVIA = createRadioButton(radioGroup, "Joueur VS IA", (board.width / 2) - 250 - 10, board.height - 350, 250);
        radioIAVIA = createRadioButton(radioGroup, "[EN LIGNE] IA VS IA", (board.width / 2) + 10, board.height - 300, 250);
        radioJVJOnline = createRadioButton(radioGroup, "[EN LIGNE] Joueur VS Joueur", (board.width / 2) + 10, board.height - 350, 250);

        // TextField (inputs)
        boardTilesRatioField = createTextField(Integer.toString(boardTilesRatio), (board.width / 4) + 100 + 10, board.height - 370 - 15 - 2, 150, 30);
        onlyNumbersFilter(boardTilesRatioField);
        nField = createTextField(Integer.toString(n), (board.width / 4) + 100 + 10, board.height - 400 - 15 - 2, 150, 30);
        onlyNumbersFilter(nField);
        codeChannelField = createTextField("", (board.width / 4) + 100 + 10, board.height - 430 - 15 - 2, 150, 30);

        // Bouton cliquable
        playButton = createClickableButton("Jouer", (board.width / 2) - ((float) 150 / 2), board.height - 540, 150, 50);
        startGameButtonEventListener(playButton);
    }

    /*
    Fonctions privées
    Seulement accessible par les méthodes de la classe
    Les méthodes les plus générales se trouvent ici
     */

    // Fonction pour dessiner des rectangles de couleur
    private void drawRect(float x, float y, float width, float height, Color color) {
        batch.end(); // On ferme le batch pour que SpriteBatch et ShapeRenderer ne soient pas ouvert en même temps

        // Pour activer la transparence
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();

        // Pour désactiver la transparence
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();
    }

    // Fonction qui retourne un input de type TextField
    private TextField createTextField(String baseText, float x, float y, float width, float height) {
        TextField textField = new TextField(baseText, skin);
        textField.setColor(Color.WHITE);
        textField.setPosition(x, y);
        textField.setSize(width, height);
        stage.addActor(textField); // On ajoute le Widget au Stage qu'on lancera dans la boucle de jeu
        return textField;
    }

    // Fonction qui permet la création de tous les boutons
    private TextButton createRadioButton(ButtonGroup<Button> buttonGroup, String baseText, float x, float y, float width) {
        TextButton radioButton = new TextButton(baseText, skin, "toggle");
        radioButton.setWidth(width);
        radioButton.setPosition(x, y);
        buttonGroup.add(radioButton);
        stage.addActor(radioButton);
        return radioButton;
    }

    private TextButton createClickableButton(String baseText, float x, float y, float width, float height) {
        TextButton clickableButton = new TextButton(baseText, skin);
        clickableButton.setSize(width, height);
        clickableButton.setPosition(x, y);
        stage.addActor(clickableButton);
        return clickableButton;
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

    // Modifier la taille du plateau de jeu
    private void setBoardTilesRatio(int val) {
        boardTilesRatio = val;
        pixelsForTile = (float) Global.WIDTH / boardTilesRatio;
    }

    // Donner des noms aux joueurs
    private void setPlayersNames(String leftName, String rightName) {
        leftPlayer = leftName;
        rightPlayer = rightName;
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
                enableObjects();
                if (radioGroup.getChecked() == radioJVJ) {
                    selectedMod = Global.JVJ;
                    setPlayersNames(Global.J1, Global.J2);
                } else if (radioGroup.getChecked() == radioJVIA) {
                    selectedMod = Global.JVIA;
                    setPlayersNames(Global.J1, Global.IAG20);
                    disableObjects();
                } else if (radioGroup.getChecked() == radioIAVIA) {
                    selectedMod = Global.IAVIA;
                    setPlayersNames(Global.IAG20, Global.IA);
                    disableObjects();
                } else {
                    selectedMod = Global.JVJONLINE;
                    setPlayersNames(Global.J1TOI, Global.J2);
                }
                // On récupère le contenu des inputs
                if (!boardTilesRatioField.getText().isEmpty()) setBoardTilesRatio(Integer.parseInt(boardTilesRatioField.getText()));
                if (!nField.getText().isEmpty()) n = Integer.parseInt(nField.getText());
                codeChannel = codeChannelField.getText();

                playButtonPressed = true;
                gameOn = true;
                isLeftPlaying = true;
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
