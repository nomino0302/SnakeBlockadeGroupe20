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
Gère en grande partie la partie graphique de l'application (menu, bannière, ...)
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
    TextField nField, codeChannelField;
    TextButton radioJVJ, radioJVIA, radioIAVIA, playButton;
    ButtonGroup<Button> radioGroup;

    // Instances d'objet qui seront utiles pour communiquer avec les autres classes
    int n = 2;
    String codeChannel = "";

    boolean isSoundOn = true, isStrawberryOn = true, isRockOn = true;
    boolean isGameOn = false;
    boolean isLeftPlaying = false;
    boolean isBlinking = false;
    String mainText = "Snake Blockade";
    String selectedMod = null;
    String leftName = "", rightName = "";
    String playerWon = null;
    long lastNanoBlink = TimeUtils.nanoTime();

    // Objets libGDX redondants
    Rectangle board = new Rectangle(0, 0, Global.WIDTH, Global.BOARD_HEIGHT);
    Rectangle ban = new Rectangle(0, Global.BOARD_HEIGHT, Global.WIDTH, Global.BAN_HEIGHT);
    Rectangle sound = new Rectangle((ban.width / 2) - ((float) 30 / 2) - 30 - 10, board.height + 5, 30, 30);
    Color darkGreen = Color.valueOf("547436"); // Hexadécimal
    Color transparentBlack = new Color(0, 0, 0, 0.8f);

    // Constructeur pour les objets lourds non instanciés
    Scene(SpriteBatch batch, Assets assets) {
        this.batch = batch;
        this.assets = assets;
        this.shapeRenderer = new ShapeRenderer();
        this.parameter = new FreeTypeFontParameter();

        this.titleFont = createFont("fonts/joystix_monospace.otf", 30, Color.WHITE);
        this.writingFont = createFont("fonts/arial.ttf", 24, Color.WHITE);
        this.gameInfosFont = createFont("fonts/joystix_monospace.otf", 14, Color.WHITE);
        this.leftPlayerFont = createFont("fonts/joystix_monospace.otf", 20, Color.SKY);
        this.rightPlayerFont = createFont("fonts/joystix_monospace.otf", 20, Color.RED);
        this.layout = new GlyphLayout(); // Pour mesurer la taille du texte

        // Pour la création de Widget interactifs
        this.stage = new Stage();
        // Skin proposé par la communauté libGDX (https://github.com/czyzby/gdx-skins/)
        this.skin = new Skin(Gdx.files.internal("skins/gdx-holo/skin/uiskin.json"));
        // On instancie ici nos Widgets
        createWidgets();
    }

    // Design de menu (quand le jeu ne joue pas)
    public void menuDesign() {
        drawBan();
        drawMenuBoard();
    }

    public void gameDesign() {
        drawBan();
        drawBoard();
    }

    public void drawBan() {
        drawRect(ban.x, ban.y, ban.width, ban.height, darkGreen);
        gameInfos();
        drawGameIcons();
        showPlayersAvatars();
        showPlayersNames(leftName, rightName);
        if (!isGameOn && playerWon != null) {
            showPlayerTrophy(playerWon); // LEFT OU RIGHT
        }
    }

    public void drawMenuBoard() {
        drawBoard();
        drawRect(0, 0, board.width, board.height, transparentBlack);
        drawLabels();
    }

    public void drawBoard() {
        batch.draw(assets.get("design/bg.png", Texture.class), board.x, board.y);
    }

    public void drawLabels() {
        mainTitle();
        showControls();
        showControls();
        modsText();
        nFieldText();
        channelFieldText();
        drawStage(); // Dessiner tous les Widgets interactifs
    }

    public void gameInfos() {
        layout.setText(gameInfosFont, "N = " + n);
        float last_layout_height = layout.height;
        gameInfosFont.draw(batch, layout, (ban.width / 2) - (layout.width / 2), Global.HEIGHT - layout.height);
        layout.setText(gameInfosFont, codeChannel);
        gameInfosFont.draw(batch, layout, (ban.width / 2) - (layout.width / 2), Global.HEIGHT - last_layout_height - layout.height - 8);
        drawRect((ban.width / 2) - 80, board.height + 6, 1, ban.height - 12, Color.WHITE);
        drawRect((ban.width / 2) + 80, board.height + 6, 1, ban.height - 12, Color.WHITE);
    }

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

    public void showPlayersAvatars() {
        leftPlayerAvatar();
        rightPlayerAvatar();
    }

    public void leftPlayerAvatar() {
        Texture texture = assets.get("snake/blue_headBas.png", Texture.class);
        batch.draw(texture, 85, Global.HEIGHT - 40);
    }

    public void rightPlayerAvatar() {
        Texture texture = assets.get("snake/red_headBas.png", Texture.class);
        batch.draw(texture, ban.width - texture.getWidth() - 85, Global.HEIGHT - 40);
    }

    public void showPlayersNames(String leftName, String rightName) {
        leftPlayerName(leftName);
        rightPlayerName(rightName);
    }

    public void leftPlayerName(String name) {
        if (!isBlinking || !isLeftPlaying) {
            layout.setText(leftPlayerFont, name);
            leftPlayerFont.draw(batch, layout, 105 - layout.width / 2, Global.HEIGHT - 40 - layout.height + 5);
        }
        // Si temps qui s'est écroulé > 0.5 seconde
        if (TimeUtils.nanoTime() - lastNanoBlink > 500000000) {
            isBlinking = !isBlinking;
            lastNanoBlink = TimeUtils.nanoTime();
        }
    }

    public void rightPlayerName(String name) {
        if (!isBlinking || isLeftPlaying) {
            layout.setText(rightPlayerFont, name);
            rightPlayerFont.draw(batch, layout, ban.width - 105 - layout.width / 2, Global.HEIGHT - 40 - layout.height + 5);
        }
        if (TimeUtils.nanoTime() - lastNanoBlink > 500000000) {
            isBlinking = !isBlinking;
            lastNanoBlink = TimeUtils.nanoTime();
        }
    }

    public void showPlayerTrophy(String cote) {
        Texture texture = assets.get("design/trophy.png", Texture.class);
        if (cote.equals("LEFT")) {
            batch.draw(texture, 15, board.height + 22);
        }
        if (cote.equals("RIGHT")) {
            batch.draw(texture, ban.width - 15 - texture.getWidth(), board.height + 22);
        }
    }

    public void mainTitle() {
        layout.setText(titleFont, mainText);
        titleFont.draw(batch, layout, (board.width / 2) - (layout.width / 2), board.height - 30);
    }

    public void showControls() {
        j1ControlsImage();
        j2ControlsImage();
    }

    public void j1ControlsImage() {
        layout.setText(leftPlayerFont, "J1");
        leftPlayerFont.draw(batch, layout, 50, board.height - 110);
        Texture img = assets.get("design/zqsd.png", Texture.class);
        float ratio = (float) img.getHeight() / img.getWidth();
        batch.draw(img, 40, board.height - 220, 200, 200 * ratio);
    }

    public void j2ControlsImage() {
        layout.setText(rightPlayerFont, "J2");
        rightPlayerFont.draw(batch, layout, board.width - 50 - layout.width, board.height - 110);
        Texture img = assets.get("design/fleches.png", Texture.class);
        float ratio = (float) img.getHeight() / img.getWidth();
        batch.draw(img, board.width - 40 - ((float) img.getWidth() / 2), board.height - 220, 200, 200 * ratio);
    }

    public void modsText() {
        layout.setText(writingFont, "Modes :");
        writingFont.draw(batch, layout, (board.width / 2) - (layout.width / 2),board.height - 260);
    }

    public void nFieldText() {
        layout.setText(writingFont, "N =");
        writingFont.draw(batch, layout, (board.width / 4) + 50,board.height - 390);
        drawRect((board.width / 4) + 110, board.height - 415, 150, 1, Color.WHITE);
    }

    public void channelFieldText() {
        layout.setText(writingFont, "Channel =");
        writingFont.draw(batch, layout, (board.width / 4) - layout.width / 4,board.height - 440);
        drawRect((board.width / 4) + 110, board.height - 465, 150, 1, Color.WHITE);
    }

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

    // Retourne la nouvelle police d'écriture en effaçant l'ancienne
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

    // Fonction qui regroupe la création de tous les Widgets
    private void createWidgets() {
        createModSelectionButtons();
        nField = createTextField("2", (board.width / 4) + 100, board.height - 415, 150, 30);
        addNFieldEventListener(nField);
        codeChannelField = createTextField("", (board.width / 4) + 100, board.height - 465, 150, 30);
        createPlayButton();
    }

    // Affichage des Widget interactifs
    private void drawStage() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    private TextField createTextField(String baseText, float x, float y, float width, float height) {
        TextField textField = new TextField(baseText, skin);
        textField.setColor(Color.WHITE);
        textField.setPosition(x, y);
        textField.setSize(width, height);
        stage.addActor(textField);
        Gdx.input.setInputProcessor(stage); // On ajoute le Widget au Stage qu'on lancera dans la boucle de jeu
        return textField;
    }

    private void createModSelectionButtons() {
        // Boutons radio (seul 1 peut être sélectionné)
        radioJVJ = new TextButton("Joueur VS Joueur", skin, "toggle");
        radioJVIA = new TextButton("Joueur VS IA", skin, "toggle");
        radioIAVIA = new TextButton("[EN LIGNE] IA VS IA", skin, "toggle");

        // On place d'abord le bouton du milieu pour bien placer les autres selon lui
        radioJVIA.setPosition((board.width / 2) - (radioJVIA.getWidth() / 2), board.height - 350);
        radioJVJ.setPosition(radioJVIA.getX() - 10 - radioJVJ.getWidth(), board.height - 350);
        radioIAVIA.setPosition(radioJVIA.getX() + radioJVIA.getWidth() + 10, board.height - 350);

        // Groupe de boutons pour que seulement un bouton soit sélectionnable à la fois
        radioGroup = new ButtonGroup<>();
        radioGroup.add(radioJVJ, radioJVIA, radioIAVIA);
        radioGroup.setMaxCheckCount(1);
        radioGroup.setMinCheckCount(1);
        radioGroup.setUncheckLast(true);

        stage.addActor(radioJVJ);
        stage.addActor(radioJVIA);
        stage.addActor(radioIAVIA);
    }

    private void createPlayButton() {
        playButton = new TextButton("Jouer", skin);
        playButton.setSize(150, 50);
        playButton.setPosition((board.width / 2) - (playButton.getWidth() / 2), board.height - 540);

        stage.addActor(playButton);
        addPlayButtonEventListener();
    }

    /*
    Déclarations des events listeners
    --------------------------------
    Ce sont des fonctions qui vont ajouter d'autres fonctions à nos Widgets
    qui vont écouter des évenements utilisateur (click gauche, touche pressée) et qui vont
    exécuter des actions précises quand elles vont se déclencher
     */

    // Pour nField
    // Charactère tapé --> Verification si chiffre
    private void addNFieldEventListener(TextField textField) {
        textField.setTextFieldFilter(new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                return Character.isDigit(c);
            }
        });
    }

    // Pour playButton
    // Bouton appuyé --> Lancement de la partie
    private void addPlayButtonEventListener() {
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (radioGroup.getChecked() == radioJVJ) {
                    selectedMod = Global.JVJ;
                    isStrawberryOn = true;
                    isRockOn = true;
                } else if (radioGroup.getChecked() == radioIAVIA) {
                    selectedMod = Global.IAVIA;
                    isStrawberryOn = false;
                    isRockOn = false;
                } else {
                    selectedMod = Global.JVIA;
                    isStrawberryOn = false;
                    isRockOn = false;
                }
                // On récupère le contenu des inputs
                n = Integer.parseInt(nField.getText());
                codeChannel = codeChannelField.getText();

                isLeftPlaying = true;
                isGameOn = true;
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
