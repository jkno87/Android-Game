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
import com.jgame.util.SimpleDrawer;
import com.jgame.util.SimpleDrawer.ColorData;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer;
import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.game.GameData.GameState;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import com.jgame.util.Decoration;
import com.jgame.util.Vector2;
import com.jgame.game.GameActivity.Difficulty;

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
            return tData.length >= currentTexture;
        }

        public void reset(){
            currentTexture = 0;
        }
    }

    class Background {
        private final float BACKGROUND_TILE_WIDTH = GameActivity.FRUSTUM_WIDTH / 2;
        private final float BACKGROUND_TILE_HEIGHT = GameActivity.PLAYING_HEIGHT - GameActivity.CONTROLS_HEIGHT;
        private final float SCREEN_EDGE = BACKGROUND_TILE_WIDTH * 2;
        private final float BACKGROUND_X_POSITION_1 = 0;
        private final float BACKGROUND_X_POSITION_2 = BACKGROUND_TILE_WIDTH;
        private final float BACKGROUND_X_POSITION_3 = BACKGROUND_TILE_WIDTH*2;
        private final Square backgroundContainer1 = new Square(new Vector2(BACKGROUND_X_POSITION_1, GameActivity.CONTROLS_HEIGHT),
                BACKGROUND_TILE_WIDTH, BACKGROUND_TILE_HEIGHT, 0);
        private final Square backgroundContainer2 = new Square(new Vector2(BACKGROUND_X_POSITION_2, GameActivity.CONTROLS_HEIGHT),
                BACKGROUND_TILE_WIDTH, BACKGROUND_TILE_HEIGHT, 0);
        private final Square backgroundContainer3 = new Square(new Vector2(BACKGROUND_X_POSITION_3, GameActivity.CONTROLS_HEIGHT),
                BACKGROUND_TILE_WIDTH, BACKGROUND_TILE_HEIGHT, 0);
        public TextureData tData1;
        public TextureData tData2;
        public TextureData tData3;
        private Vector2 scrollSpeed;

        private BackgroundWindow currentWindow;
        private BackgroundWindow nextWindow;

        public Background(Vector2 scrollSpeed){
            TextureData[] i1 = new TextureData[]{new TextureData(0,0,0.0625f,0.25f), new TextureData(0.0625f,0,0.125f,0.25f), new TextureData(0.125f,0,0.1875f,0.25f)};
            TextureData[] i2 = new TextureData[]{new TextureData(0.75f,0,0.8125f,0.25f), new TextureData(0.8125f,0,0.875f,0.25f),
                    new TextureData(0.875f,0,0.9375f,0.25f), new TextureData(0.9375f,0,1,0.25f)};
            currentWindow = new BackgroundWindow(i1);
            nextWindow = new BackgroundWindow(i2);
            this.scrollSpeed = scrollSpeed;
            //Esto asume que la primer ventana tiene minimo 3 texturas
            tData1 = currentWindow.getCurrentTexture();
            tData2 = currentWindow.getCurrentTexture();
            tData3 = currentWindow.getCurrentTexture();
        }

        private void scrollWindows(){
            //Version preeliminar que solo cambia el orden de las ventanas
            BackgroundWindow current = currentWindow;
            currentWindow = nextWindow;
            nextWindow = current;
            nextWindow.reset();
        }


        public void advanceBackground(){
            //if(modifier.x == 0 && modifier.y == 0)
             //   return;

            //Se suma la posicion original con cualquier cambio nuevo
            backgroundContainer1.position.add(scrollSpeed);
            backgroundContainer2.position.add(scrollSpeed);
            backgroundContainer3.position.add(scrollSpeed);

            if(BACKGROUND_TILE_WIDTH + backgroundContainer1.position.x < 0) {
                backgroundContainer1.position.set(SCREEN_EDGE, GameActivity.CONTROLS_HEIGHT);
                if(currentWindow.isFinished())
                    scrollWindows();
                tData1 = currentWindow.getCurrentTexture();

            }
            if(BACKGROUND_TILE_WIDTH + backgroundContainer2.position.x < 0) {
                backgroundContainer2.position.set(SCREEN_EDGE, GameActivity.CONTROLS_HEIGHT);
                if(currentWindow.isFinished())
                    scrollWindows();
                tData2 = currentWindow.getCurrentTexture();
            }
            if(BACKGROUND_TILE_WIDTH + backgroundContainer3.position.x < 0) {
                backgroundContainer3.position.set(SCREEN_EDGE, GameActivity.CONTROLS_HEIGHT);
                if(currentWindow.isFinished())
                    scrollWindows();
                tData3 = currentWindow.getCurrentTexture();
            }
        }

        public void resetBackground(){
            backgroundContainer1.position.x = BACKGROUND_X_POSITION_1;
            backgroundContainer2.position.x = BACKGROUND_X_POSITION_2;
            backgroundContainer3.position.x = BACKGROUND_X_POSITION_3;
            currentWindow.reset();
        }

    }


    private final static int SCORE_SIZE_X = 15;
    private final static int SCORE_SIZE_Y = 20;
    private final static int PAUSE_X_SIZE = 100;
    private final static int PAUSE_Y_SIZE = 70;
    private final static int SCORE_LEDS = 5;
    private final static boolean RENDER_HITBOXES = false;
    public final static ColorData DASHBOARD_COLOR = new ColorData(0.0664f,0.1367f,0.16f,1);
    public final static ColorData NON_HIGHLIGHT = new ColorData(1,1,1,0.45f);
    public final static ColorData BACKGROUND_OVERLAY = new ColorData(1,1,1,0.6f);
    public final static TextureData NEUTRAL_JOYSTICK_TEX = new TextureData(0.5f,0.375f,0.5625f,0.4375f);
    public final static TextureData LEFT_JOYSTICK_TEX = new TextureData(0.5625f,0.375f,0.625f,0.4375f);
    public final static TextureData RIGHT_JOYSTICK_TEX = new TextureData(0.625f,0.375f,0.6875f,0.4375f);;
    public final static TextureData SCORE_LABEL_TEX = new TextureData(0.125f, 0.6875f, 0.25f, 0.75f);
    public final static TextureData ATTACK_LABEL_TEX = new TextureData(0.125f, 0.625f, 0.25f, 0.6875f);
    public final static TextureData CONTINUE_BUTTON = new TextureData(0f,1.9375f,0.125f,2f);
    public final static TextureData QUIT_BUTTON = new TextureData(0f,1.875f,0.125f,1.9375f);
    public final static TextureData DISAPPEAR_TEXTURE = new TextureData(0.1875f,0.625f,0.125f,0.5625f);
    public final static TextureData NO_TEXTURE_COORDS = new TextureData(0.96875f,0.96875f,1.0f,1.0f);
    public final static TextureData SPEAKER_TEXTURE = new TextureData(0.4375f, 0.375f, 0.5f, 0.4375f);
    public final static TextureData SOUND_TEXTURE = new TextureData(0.4375f,0.4375f,0.5f,0.5f);
    public final static TextureData BUTTON_TEXTURE = new TextureData(0.25f, 0.5625f, 0.375f, 0.6875f);
    public final static TextureData ARROW_TEXTURE = TextureDrawer.genTextureData(1.0f,8.05f,16);
    public final static TextureData START_BUTTON_TEXTURE = new TextureData(0, 0.5625f, 0.125f, 0.625f);
    public final static TextureData LEFT_ARROW_TEXTURE = new TextureData(0.0625f,0.5f,0,0.4372f);
    public final static TextureData SOUND_SWITCH_ON_TEXTURE = new TextureData(0.375f,0.375f,0.4375f,0.4375f);
    public final static TextureData SOUND_SWITCH_OFF_TEXTURE = new TextureData(0.4375f,0.4375f,0.375f,0.375f);
    public final static TextureData EASY_SPRITE = new TextureData(0f,0.6875f,0.125f,0.75f);
    public final static TextureData MEDIUM_SPRITE = new TextureData(0f,0.75f,0.125f,0.8125f);
    public final static TextureData HARD_SPRITE = new TextureData(0f,0.8125f,0.125f,0.875f);
    public final static TextureData BACKGROUND_1 = new TextureData(0,0,1,0.25f);
    //public final static TextureData BACKGROUND_2 = new TextureData(0.33f,0,0.66f,0.247f);
    //public final static TextureData BACKGROUND_3 = new TextureData(0.66f,0,1,0.247f);
    public static final ColorData ATTACK_COLOR = new SimpleDrawer.ColorData(0.85f,0.109f,0.207f,0.65f);
    public static final ColorData HITTABLE_COLOR = new SimpleDrawer.ColorData(0,0.75f,0,0.65f);
    public static final ColorData SMASHED_COLOR = new SimpleDrawer.ColorData(0,0,0.65f,0.65f);
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
    private static final ColorData PAUSE_MENU_COLOR = new ColorData(0,1,0,1);
    private static final ColorData TRANSPARENCY_COLOR = new ColorData(1,1,1,0.65f);
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
    private final Background background = new Background(new Vector2(-0.7f, 0));

    public GameRenderer(GameActivity gameActivity){
        this.gameActivity = gameActivity;
        mainTextureDrawer = new TextureDrawer(true);
        pauseOverlay = new SimpleDrawer.ColorData(0,0,0,0.5f);
        menuBase = new SimpleDrawer.ColorData(0,0.75f,0.5f,1);
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

    @Override
    public void onDrawFrame(GL10 arg0) {
        gameData.copy(gameActivity.gameData);

        if(!gameData.paused)
            background.advanceBackground();

        //Se actualiza la lista de decoraciones
        for(int i = 0; i < decorations.length; i++){
            if(decorations[i] == null || decorations[i].completed()) {
                if (!gameActivity.decorationsBuffer.isEmpty())
                    decorations[i] = gameActivity.decorationsBuffer.removeFirst();
            } else
                decorations[i].update();
        }

        if(gameData.state == GameState.MENU)
            drawMenu(gameData.currentDifficulty);
        else if(gameData.state == GameState.TITLE_SCREEN)
            drawTitleScreen();
        else {

            boolean characterAlive = gameActivity.mainCharacter.alive();

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

            renderCharacter(gameActivity.mainCharacter, mainTextureDrawer);

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
            }

            if (gameData.paused) {
                mainTextureDrawer.addColoredSquare(PAUSE_LAYER, NO_TEXTURE_COORDS, pauseOverlay);
                mainTextureDrawer.addColoredSquare(PAUSE_RECTANGLE, NO_TEXTURE_COORDS, PAUSE_MENU_COLOR);

                mainTextureDrawer.addTexturedSquare(GameActivity.CONTINUE_BOUNDS, CONTINUE_BUTTON);
                mainTextureDrawer.addTexturedSquare(GameActivity.QUIT_BOUNDS, QUIT_BUTTON);
                mainTextureDrawer.addTexturedSquare(GameActivity.INPUT_SOUND_SPRITE, SPEAKER_TEXTURE);
                if (gameData.soundEnabled)
                    mainTextureDrawer.addTexturedSquare(GameActivity.INPUT_SOUND_SPRITE, SOUND_TEXTURE);
            }

            mainTextureDrawer.draw(gl10);
        }

    }

    /**
     * Se encarga de agregar la informacion de las collisionBoxes a SimpleDrawer
     * @param c
     * @param drawer
     */
    private void renderHitBoxes(GameCharacter c, TextureDrawer drawer){
        if(!c.hittable())
            return;

        for(CollisionObject o : c.getActiveCollisionBoxes())
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
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, personajesId);
        mainTextureDrawer.addTexturedSquare(120, 120, 150, 150, START_BUTTON_TEXTURE);
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

        if(gameData.paused) {
            mainTextureDrawer.addColoredSquare(PAUSE_LAYER, NO_TEXTURE_COORDS, pauseOverlay);
            mainTextureDrawer.addColoredSquare(PAUSE_RECTANGLE, NO_TEXTURE_COORDS, PAUSE_MENU_COLOR);
            mainTextureDrawer.addTexturedSquare(GameActivity.CONTINUE_BOUNDS, CONTINUE_BUTTON);
            mainTextureDrawer.addTexturedSquare(GameActivity.QUIT_BOUNDS, QUIT_BUTTON);
            mainTextureDrawer.addTexturedSquare(GameActivity.INPUT_SOUND_SPRITE, SPEAKER_TEXTURE);
            if(gameData.soundEnabled)
                mainTextureDrawer.addTexturedSquare(GameActivity.INPUT_SOUND_SPRITE, SOUND_TEXTURE);
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