package com.jgame.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;
import android.util.Log;

import com.jgame.elements.GameCharacter;
import com.jgame.elements.CollisionObject;
import com.jgame.elements.MainCharacter;
import com.jgame.util.DigitsDisplay;
import com.jgame.util.GameText;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer;
import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.util.TextureDrawer.ColorData;
import com.jgame.game.GameData.GameState;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import com.jgame.util.Decoration;
import com.jgame.util.Vector2;
import com.jgame.game.GameActivity.Difficulty;
import java.util.Random;

public class GameRenderer implements Renderer {

    class BackgroundWindow {
        private final TextureData[] tData;
        private int currentTexture;

        public BackgroundWindow(TextureData[] tData){
            this.tData = tData;
        }

        public TextureData getCurrentTexture(){
            TextureData td = tData[currentTexture];
            currentTexture++;
            return td;
        }

        public boolean isFinished(){
            return tData.length <= currentTexture;
        }

        public void reset(){
            currentTexture = 0;
        }
    }

    class Background {
        private final int NUMBER_OF_BACKGROUNDS = 2;
        private final float BACKGROUND_TILE_WIDTH = GameActivity.FRUSTUM_WIDTH / 2;
        private final float BACKGROUND_TILE_HEIGHT = GameActivity.PLAYING_HEIGHT - GameActivity.CONTROLS_HEIGHT;
        private final float SCREEN_EDGE = BACKGROUND_TILE_WIDTH * 2;
        private final float BACKGROUND_X_POSITION_1 = 0;
        private final float BACKGROUND_X_POSITION_2 = BACKGROUND_TILE_WIDTH;
        private final float BACKGROUND_X_POSITION_3 = BACKGROUND_TILE_WIDTH*2;
        private final Square backgroundContainer1 = new Square(new Vector2(BACKGROUND_X_POSITION_1, GameActivity.CONTROLS_HEIGHT),
                BACKGROUND_TILE_WIDTH + 1, BACKGROUND_TILE_HEIGHT, 0);
        private final Square backgroundContainer2 = new Square(new Vector2(BACKGROUND_X_POSITION_2, GameActivity.CONTROLS_HEIGHT),
                BACKGROUND_TILE_WIDTH + 1, BACKGROUND_TILE_HEIGHT, 0);
        private final Square backgroundContainer3 = new Square(new Vector2(BACKGROUND_X_POSITION_3, GameActivity.CONTROLS_HEIGHT),
                BACKGROUND_TILE_WIDTH + 1, BACKGROUND_TILE_HEIGHT, 0);
        public TextureData tData1;
        public TextureData tData2;
        public TextureData tData3;
        private Vector2 scrollSpeed;
        private BackgroundWindow currentWindow;
        private BackgroundWindow[] windows;
        private final Random r = new Random();

        public Background(Vector2 scrollSpeed){
            TextureData[] i1 = new TextureData[]{new TextureData(0,0,0.0625f,0.25f), new TextureData(0.0625f,0,0.125f,0.25f), new TextureData(0.125f,0,0.1875f,0.25f)};
            TextureData[] i2 = new TextureData[]{new TextureData(0.1875f,0,0.25f,0.25f), new TextureData(0.25f,0,0.3125f,0.25f),
                    new TextureData(0.3125f,0,0.375f,0.25f), new TextureData(0.375f,0,0.4375f,0.25f)};
            windows = new BackgroundWindow[NUMBER_OF_BACKGROUNDS];
            windows[0] = new BackgroundWindow(i1);
            windows[1] = new BackgroundWindow(i2);
            updateWindows();
            this.scrollSpeed = scrollSpeed;
            //Esto asume que la primer ventana tiene minimo 3 texturas
            tData1 = currentWindow.getCurrentTexture();
            tData2 = currentWindow.getCurrentTexture();
            tData3 = currentWindow.getCurrentTexture();
        }

        private void updateWindows(){
            int n = r.nextInt(NUMBER_OF_BACKGROUNDS);
            currentWindow = windows[n];
            currentWindow.reset();
        }

        public void advanceBackground(){
            //Se suma la posicion original con cualquier cambio nuevo
            backgroundContainer1.position.add(scrollSpeed);
            backgroundContainer2.position.add(scrollSpeed);
            backgroundContainer3.position.add(scrollSpeed);

            if(BACKGROUND_TILE_WIDTH + backgroundContainer1.position.x < 0) {
                backgroundContainer1.position.set(SCREEN_EDGE, GameActivity.CONTROLS_HEIGHT);
                if(currentWindow.isFinished())
                    updateWindows();
                tData1 = currentWindow.getCurrentTexture();

            }
            if(BACKGROUND_TILE_WIDTH + backgroundContainer2.position.x < 0) {
                backgroundContainer2.position.set(SCREEN_EDGE, GameActivity.CONTROLS_HEIGHT);
                if(currentWindow.isFinished())
                    updateWindows();
                tData2 = currentWindow.getCurrentTexture();
            }
            if(BACKGROUND_TILE_WIDTH + backgroundContainer3.position.x < 0) {
                backgroundContainer3.position.set(SCREEN_EDGE, GameActivity.CONTROLS_HEIGHT);
                if(currentWindow.isFinished())
                    updateWindows();
                tData3 = currentWindow.getCurrentTexture();
            }
        }

        public void resetBackground(){
            backgroundContainer1.position.x = BACKGROUND_X_POSITION_1;
            backgroundContainer2.position.x = BACKGROUND_X_POSITION_2;
            backgroundContainer3.position.x = BACKGROUND_X_POSITION_3;
            currentWindow.reset();
            tData1 = currentWindow.getCurrentTexture();
            tData2 = currentWindow.getCurrentTexture();
            tData3 = currentWindow.getCurrentTexture();
        }

    }


    private final static int SCORE_SIZE_X = 15;
    private final static int SCORE_SIZE_Y = 20;
    private final static int PAUSE_X_SIZE = 100;
    private final static int PAUSE_Y_SIZE = 70;
    private final static int SCORE_LEDS = 5;
    private final static int TITLE_SCREEN_INTERVAL = 40;
    private final static boolean RENDER_HITBOXES = false;
    public final static ColorData DASHBOARD_COLOR = new ColorData(0.0664f,0.1367f,0.16f,1);
    public final static ColorData NON_HIGHLIGHT = new ColorData(1,1,1,0.45f);
    public final static ColorData BACKGROUND_OVERLAY = new ColorData(1,1,1,0.6f);
    //Texturas varias
    public final static TextureData NO_TEXTURE_COORDS = new TextureData(0.4375f,0.4375f,0.46875f,0.46875f);
    public final static TextureData GAME_OVER_LABEL = TextureDrawer.generarTextureData(11,4,13,5,32);
    public final static TextureData CLOSING_MESSAGE = TextureDrawer.generarTextureData(11,5,13,6,32);
    public final static TextureData LOADING_MESSAGE = TextureDrawer.generarTextureData(5,4,8,5,32);
    //Texturas de la pantalla inicial
    public final static TextureData TITLE_BACKGROUND = new TextureData(0,0.25f,0.125f,0.5f);
    public final static TextureData TITLE_MESSAGE = TextureDrawer.generarTextureData(5,5,8,6,32);
    public final static TextureData TITLE_LOGO = new TextureData(0, 0.125f, 0.125f, 0.1875f);
    //Texturas del menu principal
    public final static TextureData START_BUTTON_TEXTURE = new TextureData(0f,0.28125f,0.0625f,0.3125f);
    public final static TextureData SOUND_BUTTON =  new TextureData(0f,0.3125f,0.0625f,0.34375f);
    public final static TextureData EASY_SPRITE = new TextureData(0f,0.34375f,0.0625f,0.375f);
    public final static TextureData MEDIUM_SPRITE = new TextureData(0f,0.375f,0.0625f,0.40625f);
    public final static TextureData HARD_SPRITE = new TextureData(0f,0.40625f,0.0625f,0.4375f);
    public final static TextureData RECORDS_BUTTON_SPRITE = new TextureData(0f, 0.5f, 0.0625f, 0.53125f);
    public final static TextureData SOUND_SWITCH_ON_TEXTURE = new TextureData(0.375f,0.375f,0.4375f,0.4375f);
    public final static TextureData SOUND_SWITCH_OFF_TEXTURE = new TextureData(0.4375f,0.4375f,0.375f,0.375f);
    //Texturas del menu de records
    public final static TextureData RETURN_BUTTON = new TextureData(0f, 0.53125f, 0.0625f, 0.5625f);
    public final static TextureData HIGHSCORE_LABEL = new TextureData(0.375f, 0.625f, 0.625f, 0.75f);
    //Texturas del menu de pausa
    public final static TextureData SOUND_CANCELLED_SPRITE = new TextureData(0.25f, 0.6875f, 0.375f, 0.75f);
    public final static TextureData QUIT_BUTTON = new TextureData(0f,0.4375f,0.0625f,0.46875f);
    public final static TextureData CONTINUE_BUTTON = new TextureData(0f,0.46875f,0.0625f,0.5f);
    //Texturas de controles
    public final static TextureData NEUTRAL_JOYSTICK_TEX = new TextureData(0.25f,0.1875f,0.28125f,0.21875f);
    public final static TextureData LEFT_JOYSTICK_TEX = new TextureData(0.28125f,0.1875f,0.3125f,0.21875f);
    public final static TextureData RIGHT_JOYSTICK_TEX = new TextureData(0.3125f,0.1875f,0.34375f,0.21875f);
    public final static TextureData ATTACK_LABEL_TEX = TextureDrawer.generarTextureData(2,10,4,11,32);
    public final static TextureData SCORE_LABEL_TEX = TextureDrawer.generarTextureData(2,11,4,12,32);
    public final static TextureData BUTTON_TEXTURE = new TextureData(0.125f, 0.28125f, 0.1875f, 0.34375f);
    public final static TextureData ARROW_TEXTURE = new TextureData(0,0.21875f,0.03125f,0.25f);
    public final static TextureData LEFT_ARROW_TEXTURE = new TextureData(0.03125f,0.25f,0,0.21875f);

    public static final ColorData ATTACK_COLOR = new ColorData(0.85f,0.109f,0.207f,0.65f);
    public static final ColorData HITTABLE_COLOR = new ColorData(0,0.75f,0,0.65f);
    public static final ColorData SMASHED_COLOR = new ColorData(0,0,0.65f,0.65f);
    public static final Square CONTROLS_RECT = new Square(0, 0, GameActivity.PLAYING_WIDTH, GameActivity.CONTROLS_HEIGHT);
    public static final Square PAUSE_RECTANGLE = new Square(GameActivity.FRUSTUM_WIDTH/2 - PAUSE_X_SIZE, GameActivity.FRUSTUM_HEIGHT/2 - PAUSE_Y_SIZE,
            PAUSE_X_SIZE * 2, PAUSE_Y_SIZE * 2);
    private static final Square PAUSE_LAYER = new Square(0, 0, GameActivity.PLAYING_WIDTH, GameActivity.PLAYING_HEIGHT);
    public static final GameText SOUND_LABEL = new GameText("sound", new Square(160, 85, 150, 35), 0);
    public static final GameText ON_LABEL = new GameText("on", new Square(160, 50, 50, 20), 20);
    public static final GameText OFF_LABEL = new GameText("off", new Square(260, 50, 50, 20), 20);
    public static final Square JOYSTICK_BOUNDS = new Square(35, GameActivity.INPUT_LEFT_BOUNDS.position.y - 5, 75, 75);
    public static final Square SOUND_SWITCH_SPRITE = new Square(210, 40, 50, 50);
    public static final Square SCORE_LABEL_BOUNDS = new Square(195, 40, SCORE_SIZE_X * SCORE_LEDS, SCORE_SIZE_Y);
    public static final Square ATTACK_LABEL_BOUNDS = new Square(GameActivity.INPUT_A_BOUNDS.position.x + 70, 40, SCORE_LABEL_BOUNDS.lenX, SCORE_LABEL_BOUNDS.lenY);
    private static final DigitsDisplay CURRENT_SCORE = new DigitsDisplay(SCORE_SIZE_X, SCORE_SIZE_Y, SCORE_LEDS, new Vector2(250,15));
    private static final DigitsDisplay RECORDS_SCORE = new DigitsDisplay(SCORE_SIZE_X, SCORE_SIZE_Y, SCORE_LEDS, new Vector2(200, 100));
    private static final ColorData PAUSE_MENU_COLOR = new ColorData(0,1,0,1);
    private static final ColorData TRANSPARENCY_COLOR = new ColorData(1,1,1,0.65f);
    private static final Vector2 BACKGROUND_SCROLL_SPEED = new Vector2(-0.7f, 0);
    private static final Vector2 BACKGROUND_IDLE = new Vector2();
    private GameSurfaceView surfaceView;
    private GameActivity gameActivity;
    private GL10 gl10;
    int personajesId;
    int backgroundId;
    private TextureDrawer mainTextureDrawer;
    ColorData pauseOverlay;
    ColorData menuBase;
    private final GameData gameData;
    private final Decoration[] decorations = new Decoration[5];
    private final Background background = new Background(BACKGROUND_SCROLL_SPEED);
    private int titleScreenCounter = TITLE_SCREEN_INTERVAL;
    private boolean showTitleMessage = true;

    public GameRenderer(GameActivity gameActivity){
        this.gameActivity = gameActivity;
        mainTextureDrawer = new TextureDrawer(true);
        pauseOverlay = new TextureDrawer.ColorData(0,0,0,0.5f);
        menuBase = new TextureDrawer.ColorData(0,0.75f,0.5f,1);
        gameData = new GameData();
    }

    public void setSurfaceView(GameSurfaceView surfaceView){
        this.surfaceView = surfaceView;
    }

    private int loadTexture(int resource) {
        Bitmap bitmap = BitmapFactory.decodeResource(surfaceView.getResources(), resource);
        int[] textureIds = new int[1];
        gl10.glGenTextures(1, textureIds, 0);
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, textureIds[0]);
        gl10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();

        return textureIds[0];
    }

    /**
     * Agrega el dibujo del menu de pausa a tDrawer
     * @param tDrawer
     */
    private void addPauseLayer(TextureDrawer tDrawer, boolean soundEnabled){
        tDrawer.addColoredSquare(PAUSE_LAYER, NO_TEXTURE_COORDS, pauseOverlay);
        tDrawer.addColoredSquare(PAUSE_RECTANGLE, NO_TEXTURE_COORDS, PAUSE_MENU_COLOR);

        tDrawer.addTexturedSquare(GameActivity.CONTINUE_BOUNDS, CONTINUE_BUTTON);
        tDrawer.addTexturedSquare(GameActivity.RESTART_BOUNDS, CONTINUE_BUTTON);
        tDrawer.addTexturedSquare(GameActivity.QUIT_BOUNDS, QUIT_BUTTON);
        tDrawer.addTexturedSquare(GameActivity.INPUT_SOUND_SPRITE, SOUND_BUTTON);
        if(!soundEnabled)
            tDrawer.addTexturedSquare(GameActivity.INPUT_SOUND_SPRITE, SOUND_CANCELLED_SPRITE);
    }

    /**
     * Asigna el sprite que se debe de dibujar para GameCharacter c. Requiere que al menos uno de los
     * activeCollisionBoxes sea del tipo TYPE_SPRITE_CONTAINER
     * @param c GameCharacter que se va a dibujar
     * @param drawer TextureDrawer al que se le agregara la informacion del personaje.
     */
    private void renderMainCharacter(MainCharacter c, TextureDrawer drawer){
        if(!c.alive())
            return;

        if(c.baseX.x < 0)
            drawer.addInvertedColoredSquare(c.spriteContainer, c.getCurrentTexture(), c.color);
        else {
            drawer.addColoredSquare(c.spriteContainer, c.getCurrentTexture(), c.colorModifier);
            drawer.addColoredSquare(c.spriteContainer, c.getCurrentTexture(), c.color);
        }
    }

    @Override
    public void onDrawFrame(GL10 arg0) {
        gameData.copy(gameActivity.gameData);
        Vector2 backgroundMoveSpeed = BACKGROUND_IDLE;

        if(!gameData.paused && gameData.backgroundMoving) {
            background.advanceBackground();
            backgroundMoveSpeed = GameActivity.ADVANCE_SPEED;
        }

        //Se actualiza la lista de decoraciones
        if(!gameData.paused) {
            for (int i = 0; i < decorations.length; i++) {
                if (decorations[i] == null || decorations[i].completed()) {
                    if (!gameActivity.decorationsBuffer.isEmpty()) {
                        decorations[i] = gameActivity.decorationsBuffer.removeFirst();
                    }
                } else {
                    decorations[i].update(backgroundMoveSpeed);
                    if (gameData.state != GameState.PLAYING)
                        decorations[i].terminate();
                }
            }
        }

        if(gameData.state ==  GameState.LOADING_SCREEN)
            drawMessage(LOADING_MESSAGE);
        else if(gameData.state == GameState.MENU || gameData.state == GameState.STARTING)
            drawMenu(gameData.currentDifficulty);
        else if(gameData.state == GameState.TITLE_SCREEN)
            drawTitleScreen();
        else if(gameData.state == GameState.TERMINATING)
            drawMessage(CLOSING_MESSAGE);
        else if(gameData.state == GameState.RECORDS)
            drawRecordsScreen();
        else if(gameData.state == GameState.GAME_OVER)
            drawGameOverScreen();
        else {

            gl10.glViewport(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
            gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);
            gl10.glMatrixMode(GL10.GL_PROJECTION);
            gl10.glLoadIdentity();
            gl10.glOrthof(0, GameActivity.FRUSTUM_WIDTH, 0, GameActivity.FRUSTUM_HEIGHT, 1, -1);
            gl10.glMatrixMode(GL10.GL_MODELVIEW);
            gl10.glEnable(GL10.GL_BLEND);
            gl10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            gl10.glEnable(GL10.GL_TEXTURE_2D);

            mainTextureDrawer.reset();
            gl10.glBindTexture(GL10.GL_TEXTURE_2D, backgroundId);
            mainTextureDrawer.addTexturedSquare(background.backgroundContainer1, background.tData1);
            mainTextureDrawer.addTexturedSquare(background.backgroundContainer2, background.tData2);
            mainTextureDrawer.addTexturedSquare(background.backgroundContainer3, background.tData3);

            /*if(gameData.currentDifficulty == Difficulty.MEDIUM)
                mainTextureDrawer.addColoredSquare(BACKGROUND_CONTAINER, LEVEL_2_BACKGROUND, BACKGROUND_OVERLAY);
            else if(gameData.currentDifficulty == Difficulty.HARD)
                mainTextureDrawer.addColoredSquare(BACKGROUND_CONTAINER, LEVEL_3_BACKGROUND, BACKGROUND_OVERLAY);
                */
            mainTextureDrawer.draw(gl10);


            mainTextureDrawer.reset();

            //renderCharacter(gameActivity.mainCharacter, mainTextureDrawer);
            renderMainCharacter(gameActivity.mainCharacter, mainTextureDrawer);

            synchronized (gameActivity.enemyLock) {
                renderCharacter(gameActivity.currentEnemy, mainTextureDrawer);
                if (RENDER_HITBOXES)
                    renderHitBoxes(gameActivity.currentEnemy, mainTextureDrawer);
            }

            for(Decoration d : decorations) {
                if(d == null || !d.drawable())
                    continue;
                if(d.inverted)
                    mainTextureDrawer.addInvertedColoredSquare(d.size, d.getSprite(), d.color);
                else
                    mainTextureDrawer.addColoredSquare(d.size, d.getSprite(), d.color);
            }


            mainTextureDrawer.addColoredSquare(CONTROLS_RECT, NO_TEXTURE_COORDS, DASHBOARD_COLOR);
            gl10.glBindTexture(GL10.GL_TEXTURE_2D, personajesId);

            mainTextureDrawer.addTexturedSquare(GameActivity.INPUT_LEFT_BOUNDS, LEFT_ARROW_TEXTURE);
            mainTextureDrawer.addTexturedSquare(GameActivity.INPUT_RIGHT_BOUNDS, ARROW_TEXTURE);
            mainTextureDrawer.addTexturedSquare(GameActivity.INPUT_A_BOUNDS, BUTTON_TEXTURE);
            mainTextureDrawer.addTexturedSquare(SCORE_LABEL_BOUNDS, SCORE_LABEL_TEX);
            mainTextureDrawer.addTexturedSquare(ATTACK_LABEL_BOUNDS, ATTACK_LABEL_TEX);

            CURRENT_SCORE.number = gameData.score;
            CURRENT_SCORE.addDigitsTexture(mainTextureDrawer);

            if(gameActivity.mainCharacter.state == MainCharacter.CharacterState.MOVING_FORWARD)
                mainTextureDrawer.addTexturedSquare(JOYSTICK_BOUNDS, RIGHT_JOYSTICK_TEX);
            else if(gameActivity.mainCharacter.state == MainCharacter.CharacterState.MOVING_BACKWARDS)
                mainTextureDrawer.addTexturedSquare(JOYSTICK_BOUNDS, LEFT_JOYSTICK_TEX);
            else
                mainTextureDrawer.addTexturedSquare(JOYSTICK_BOUNDS, NEUTRAL_JOYSTICK_TEX);

            if (RENDER_HITBOXES) {
                renderHitBoxes(gameActivity.mainCharacter, mainTextureDrawer);
                mainTextureDrawer.addColoredSquare(GameActivity.INPUT_LEFT_BOUNDS, NO_TEXTURE_COORDS, ATTACK_COLOR);
                mainTextureDrawer.addColoredSquare(GameActivity.INPUT_RIGHT_BOUNDS, NO_TEXTURE_COORDS, ATTACK_COLOR);
                mainTextureDrawer.addColoredSquare(GameActivity.INPUT_A_BOUNDS, NO_TEXTURE_COORDS, ATTACK_COLOR);
            }


            if (gameData.state == GameState.RESTART_SCREEN) {
                mainTextureDrawer.addTexturedSquare(GameActivity.RESTART_BOUNDS, CONTINUE_BUTTON);
                mainTextureDrawer.addTexturedSquare(GameActivity.QUIT_BOUNDS, QUIT_BUTTON);
            }

            //En caso de que el juego este iniciando, se establece la posicion inicial del background
            if (gameData.state == GameState.STARTING) {
                background.resetBackground();
                for(int i = 0; i < decorations.length; i++)
                    if(decorations[i] != null)
                        decorations[i].terminate();
            }

            if (gameData.paused)
                addPauseLayer(mainTextureDrawer, gameData.soundEnabled);


            mainTextureDrawer.draw(gl10);
        }

    }

    /**
     * Se encarga de agregar la informacion de las collisionBoxes a TextureDrawer
     * @param c
     * @param drawer
     */
    private void renderHitBoxes(GameCharacter c, TextureDrawer drawer){
        if(!c.hittable())
            return;

        for(CollisionObject o : c.activeCollisionBoxes)
            if(o.type == CollisionObject.TYPE_ATTACK)
                drawer.addColoredSquare(o.bounds, NO_TEXTURE_COORDS, ATTACK_COLOR);
            else if(o.type == CollisionObject.TYPE_SMASHED)
                drawer.addColoredSquare(o.bounds, NO_TEXTURE_COORDS ,SMASHED_COLOR);
            else
                drawer.addColoredSquare(o.bounds, NO_TEXTURE_COORDS, HITTABLE_COLOR);
    }

    /**
     * Asigna el sprite que se debe de dibujar para GameCharacter c. Requiere que al menos uno de los
     * activeCollisionBoxes sea del tipo TYPE_SPRITE_CONTAINER
     * @param c GameCharacter que se va a dibujar
     * @param drawer TextureDrawer al que se le agregara la informacion del personaje.
     */
    private void renderCharacter(GameCharacter c, TextureDrawer drawer){
        if(!c.alive())
            return;

       if(c.baseX.x < 0)
            drawer.addInvertedColoredSquare(c.spriteContainer, c.getCurrentTexture(), c.color);
        else
            drawer.addColoredSquare(c.spriteContainer, c.getCurrentTexture(), c.color);
    }

    /**
     * Sirve para dibujar en pantalla un mensaje
     * @param message
     */
    private void drawMessage(TextureData message){
        gl10.glViewport(0,0, surfaceView.getWidth(), surfaceView.getHeight());
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl10.glMatrixMode(GL10.GL_PROJECTION);
        gl10.glLoadIdentity();
        gl10.glOrthof(0, GameActivity.FRUSTUM_WIDTH, 0, GameActivity.FRUSTUM_HEIGHT, 1, -1);
        gl10.glMatrixMode(GL10.GL_MODELVIEW);
        gl10.glEnable(GL10.GL_BLEND);
        gl10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl10.glEnable(GL10.GL_TEXTURE_2D);

        mainTextureDrawer.reset();
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, personajesId);
        mainTextureDrawer.addTexturedSquare(120, 120, 150, 150, message);
        mainTextureDrawer.draw(gl10);
    }

    private void drawGameOverScreen(){
        gl10.glViewport(0,0, surfaceView.getWidth(), surfaceView.getHeight());
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl10.glMatrixMode(GL10.GL_PROJECTION);
        gl10.glLoadIdentity();
        gl10.glOrthof(0, GameActivity.FRUSTUM_WIDTH, 0, GameActivity.FRUSTUM_HEIGHT, 1, -1);
        gl10.glMatrixMode(GL10.GL_MODELVIEW);
        gl10.glEnable(GL10.GL_BLEND);
        gl10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl10.glEnable(GL10.GL_TEXTURE_2D);

        mainTextureDrawer.reset();
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, personajesId);
        mainTextureDrawer.addTexturedSquare(120, 120, 150, 150, GAME_OVER_LABEL);
        mainTextureDrawer.draw(gl10);
    }

    private void drawRecordsScreen(){
        gl10.glViewport(0,0, surfaceView.getWidth(), surfaceView.getHeight());
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl10.glMatrixMode(GL10.GL_PROJECTION);
        gl10.glLoadIdentity();
        gl10.glOrthof(0, GameActivity.FRUSTUM_WIDTH, 0, GameActivity.FRUSTUM_HEIGHT, 1, -1);
        gl10.glMatrixMode(GL10.GL_MODELVIEW);
        gl10.glEnable(GL10.GL_BLEND);
        gl10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl10.glEnable(GL10.GL_TEXTURE_2D);

        mainTextureDrawer.reset();
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, personajesId);
        mainTextureDrawer.addTexturedSquare(100, 170, 150, 75, HIGHSCORE_LABEL);
        mainTextureDrawer.addTexturedSquare(GameActivity.RETURN_BUTTON_BOUNDS, RETURN_BUTTON);
        RECORDS_SCORE.number = gameData.highScore;
        RECORDS_SCORE.addDigitsTexture(mainTextureDrawer);

        if(gameData.paused)
            addPauseLayer(mainTextureDrawer, gameData.soundEnabled);

        mainTextureDrawer.draw(gl10);
    }

    /**
     * Se encarga de dibujar la pantalla del titulo inicial
     */
    private void drawTitleScreen(){
        gl10.glViewport(0,0, surfaceView.getWidth(), surfaceView.getHeight());
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl10.glMatrixMode(GL10.GL_PROJECTION);
        gl10.glLoadIdentity();
        gl10.glOrthof(0, GameActivity.FRUSTUM_WIDTH, 0, GameActivity.FRUSTUM_HEIGHT, 1, -1);
        gl10.glMatrixMode(GL10.GL_MODELVIEW);
        gl10.glEnable(GL10.GL_BLEND);
        gl10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl10.glEnable(GL10.GL_TEXTURE_2D);

        mainTextureDrawer.reset();
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, backgroundId);
        mainTextureDrawer.addTexturedSquare(0,0,GameActivity.FRUSTUM_WIDTH,GameActivity.FRUSTUM_HEIGHT, TITLE_BACKGROUND);
        mainTextureDrawer.draw(gl10);

        mainTextureDrawer.reset();
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, personajesId);
        mainTextureDrawer.addTexturedSquare(120, 120, 150, 150, TITLE_LOGO);
        if(titleScreenCounter > 0) {
            if(showTitleMessage)
                mainTextureDrawer.addTexturedSquare(120, 10, 240, 80, TITLE_MESSAGE);
            titleScreenCounter--;
            if(titleScreenCounter == 0) {
                showTitleMessage = !showTitleMessage;
                titleScreenCounter = TITLE_SCREEN_INTERVAL;
            }
        }



        mainTextureDrawer.draw(gl10);
    }

    private void drawMenu(Difficulty currentDifficulty){
        gl10.glViewport(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl10.glMatrixMode(GL10.GL_PROJECTION);
        gl10.glLoadIdentity();
        gl10.glOrthof(0, GameActivity.FRUSTUM_WIDTH, 0, GameActivity.FRUSTUM_HEIGHT, 1, -1);
        gl10.glMatrixMode(GL10.GL_MODELVIEW);
        gl10.glEnable(GL10.GL_BLEND);
        gl10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl10.glEnable(GL10.GL_TEXTURE_2D);

        mainTextureDrawer.reset();
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, personajesId);

        if(RENDER_HITBOXES) {
            mainTextureDrawer.addColoredSquare(GameActivity.START_BUTTON_BOUNDS, NO_TEXTURE_COORDS, HITTABLE_COLOR);
            mainTextureDrawer.addColoredSquare(GameActivity.SOUND_SWITCH, NO_TEXTURE_COORDS, HITTABLE_COLOR);
        }

        mainTextureDrawer.addTexturedSquare(GameActivity.START_BUTTON_BOUNDS, START_BUTTON_TEXTURE);
        SOUND_LABEL.addLetterTexture(mainTextureDrawer);
        if(gameData.soundEnabled) {
            ON_LABEL.addLetterTexture(mainTextureDrawer);
            OFF_LABEL.addLetterTexture(mainTextureDrawer, NON_HIGHLIGHT);
            mainTextureDrawer.addTexturedSquare(SOUND_SWITCH_SPRITE, SOUND_SWITCH_ON_TEXTURE);
        } else {
            ON_LABEL.addLetterTexture(mainTextureDrawer, NON_HIGHLIGHT);
            OFF_LABEL.addLetterTexture(mainTextureDrawer);
            mainTextureDrawer.addTexturedSquare(SOUND_SWITCH_SPRITE, SOUND_SWITCH_OFF_TEXTURE);
        }

        //Dibuja los botones que representan la dificultad. Esto se puede mejorar, se ve horrible
        if(currentDifficulty == Difficulty.EASY) {
            mainTextureDrawer.addTexturedSquare(GameActivity.EASY_DIFF_BOUNDS, EASY_SPRITE);
            mainTextureDrawer.addColoredSquare(GameActivity.MEDIUM_DIFF_BOUNDS, MEDIUM_SPRITE, TRANSPARENCY_COLOR);
            mainTextureDrawer.addColoredSquare(GameActivity.HARD_DIFF_BOUNDS, HARD_SPRITE, TRANSPARENCY_COLOR);
        } else if(currentDifficulty == Difficulty.MEDIUM){
            mainTextureDrawer.addColoredSquare(GameActivity.EASY_DIFF_BOUNDS, EASY_SPRITE, TRANSPARENCY_COLOR);
            mainTextureDrawer.addTexturedSquare(GameActivity.MEDIUM_DIFF_BOUNDS, MEDIUM_SPRITE);
            mainTextureDrawer.addColoredSquare(GameActivity.HARD_DIFF_BOUNDS, HARD_SPRITE, TRANSPARENCY_COLOR);
        } else {
            mainTextureDrawer.addColoredSquare(GameActivity.EASY_DIFF_BOUNDS, EASY_SPRITE, TRANSPARENCY_COLOR);
            mainTextureDrawer.addColoredSquare(GameActivity.MEDIUM_DIFF_BOUNDS, MEDIUM_SPRITE, TRANSPARENCY_COLOR);
            mainTextureDrawer.addTexturedSquare(GameActivity.HARD_DIFF_BOUNDS, HARD_SPRITE);
        }

        mainTextureDrawer.addTexturedSquare(GameActivity.RECORDS_BUTTON_BOUNDS, RECORDS_BUTTON_SPRITE);

        if(gameData.paused) {
            mainTextureDrawer.addColoredSquare(PAUSE_LAYER, NO_TEXTURE_COORDS, pauseOverlay);
            mainTextureDrawer.addColoredSquare(PAUSE_RECTANGLE, NO_TEXTURE_COORDS, PAUSE_MENU_COLOR);
            mainTextureDrawer.addTexturedSquare(GameActivity.CONTINUE_BOUNDS, CONTINUE_BUTTON);
            mainTextureDrawer.addTexturedSquare(GameActivity.QUIT_BOUNDS, QUIT_BUTTON);
            mainTextureDrawer.addTexturedSquare(GameActivity.INPUT_SOUND_SPRITE, SOUND_BUTTON);
            if(!gameData.soundEnabled)
                mainTextureDrawer.addTexturedSquare(GameActivity.INPUT_SOUND_SPRITE, SOUND_CANCELLED_SPRITE);
        }

        mainTextureDrawer.draw(gl10);
    }




    @Override
    public void onSurfaceChanged(GL10 arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
        gl10 = arg0;
        gl10.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        personajesId = loadTexture(R.raw.atlas);
        backgroundId = loadTexture(R.raw.background);
    }
}