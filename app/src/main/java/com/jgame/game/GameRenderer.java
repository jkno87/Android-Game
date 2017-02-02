package com.jgame.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;
import com.jgame.definitions.GameLevels;
import com.jgame.elements.GameCharacter;
import com.jgame.elements.CollisionObject;
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

public class GameRenderer implements Renderer {

    private final static int SCORE_SIZE_X = 15;
    private final static int SCORE_SIZE_Y = 20;
    private final static int PAUSE_X_SIZE = 100;
    private final static int PAUSE_Y_SIZE = 70;
    private final static boolean RENDER_HITBOXES = false;
    public final static ColorData DASHBOARD_COLOR = new ColorData(0.0664f,0.1367f,0.16f,1);
    public final static ColorData NON_HIGHLIGHT = new ColorData(1,1,1,0.45f);
    public final static ColorData BACKGROUND_OVERLAY = new ColorData(1,1,1,0.6f);
    public final static TextureData NO_TEXTURE_COORDS = new TextureData(0.96875f,0.96875f,1.0f,1.0f);
    public final static TextureData SPEAKER_TEXTURE = new TextureData(0.4375f, 0.375f, 0.5f, 0.4375f);
    public final static TextureData SOUND_TEXTURE = new TextureData(0.4375f,0.4375f,0.5f,0.5f);
    public final static TextureData BUTTON_TEXTURE = TextureDrawer.genTextureData(1.0f,7.05f,16);
    public final static TextureData ARROW_TEXTURE = TextureDrawer.genTextureData(1.0f,8.05f,16);
    public final static TextureData START_BUTTON_TEXTURE = new TextureData(0, 0.5625f, 0.125f, 0.625f);
    public final static TextureData LEFT_ARROW_TEXTURE = new TextureData(0.0625f,0.5f,0,0.4375f);
    public final static TextureData SOUND_SWITCH_ON_TEXTURE = new TextureData(0.375f,0.375f,0.4375f,0.4375f);
    public final static TextureData SOUND_SWITCH_OFF_TEXTURE = new TextureData(0.4375f,0.4375f,0.375f,0.375f);
    public final static TextureData BACKGROUND_TEXTURE = new TextureData(0,0,1,0.247f);
    public final static TextureData LEVEL_2_BACKGROUND = new TextureData(0, 0.2478f, 1, 0.4956f);
    public final static TextureData LEVEL_3_BACKGROUND = new TextureData(0, 0.499f, 1, 0.7449f);
    public static final ColorData ATTACK_COLOR = new SimpleDrawer.ColorData(0.85f,0.109f,0.207f,0.65f);
    public static final ColorData HITTABLE_COLOR = new SimpleDrawer.ColorData(0,0.75f,0,0.65f);
    public static final ColorData SMASHED_COLOR = new SimpleDrawer.ColorData(0,0,0.65f,0.65f);
    public static final Square CONTROLS_RECT = new Square(0, 0, GameActivity.PLAYING_WIDTH, GameActivity.CONTROLS_HEIGHT);
    public static final Square PAUSE_RECTANGLE = new Square(GameLevels.FRUSTUM_WIDTH/2 - PAUSE_X_SIZE, GameLevels.FRUSTUM_HEIGHT/2 - PAUSE_Y_SIZE,
            PAUSE_X_SIZE * 2, PAUSE_Y_SIZE * 2);
    private static final Square PAUSE_LAYER = new Square(0, 0, GameActivity.PLAYING_WIDTH, GameActivity.PLAYING_HEIGHT);
    public static final GameText HIGHSCORE_TEXT = new GameText("highscore", new Square(160, GameLevels.FRUSTUM_HEIGHT - 35, 50, 18), 2);
    public static final GameText SOUND_LABEL = new GameText("sound", new Square(160, 85, 150, 35), 0);
    public static final GameText ON_LABEL = new GameText("on", new Square(160, 50, 50, 20), 20);
    public static final GameText OFF_LABEL = new GameText("off", new Square(260, 50, 50, 20), 20);
    public static final Square SOUND_SWITCH_SPRITE = new Square(210, 40, 50, 50);
    public static final Square BACKGROUND_SIZE = new Square(0, GameActivity.CONTROLS_HEIGHT,GameActivity.PLAYING_WIDTH, GameActivity.PLAYING_HEIGHT - GameActivity.CONTROLS_HEIGHT);
    private static final DigitsDisplay CURRENT_SCORE = new DigitsDisplay(SCORE_SIZE_X, SCORE_SIZE_Y, 5, new Vector2(250,35));
    private static final ColorData PAUSE_MENU_COLOR = new ColorData(0,1,0,1);
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
        //synchronized (gameActivity.decorationsBuffer){
            for(int i = 0; i < decorations.length; i++){
                if(decorations[i] == null || decorations[i].completed()) {
                    if (!gameActivity.decorationsBuffer.isEmpty())
                        decorations[i] = gameActivity.decorationsBuffer.removeFirst();
                } else
                    decorations[i].update();

            }
        //}

        if(gameData.state == GameState.MENU)
            drawMenu();
        else {

            boolean characterAlive = gameActivity.mainCharacter.alive();

            gl10.glViewport(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
            gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);
            gl10.glMatrixMode(GL10.GL_PROJECTION);
            gl10.glLoadIdentity();
            gl10.glOrthof(0, GameLevels.FRUSTUM_WIDTH, 0, GameLevels.FRUSTUM_HEIGHT, 1, -1);
            gl10.glMatrixMode(GL10.GL_MODELVIEW);
            gl10.glEnable(GL10.GL_BLEND);
            gl10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            gl10.glEnable(GL10.GL_TEXTURE_2D);

            mainTextureDrawer.reset();
            gl10.glBindTexture(GL10.GL_TEXTURE_2D, backgroundId);
            mainTextureDrawer.addTexturedSquare(BACKGROUND_SIZE, BACKGROUND_TEXTURE);
            if(gameData.currentDifficulty == GameActivity.Difficulty.MEDIUM)
                mainTextureDrawer.addColoredSquare(BACKGROUND_SIZE, LEVEL_3_BACKGROUND, BACKGROUND_OVERLAY);
            mainTextureDrawer.draw(gl10);


            mainTextureDrawer.reset();

            renderCharacter(gameActivity.mainCharacter, mainTextureDrawer);
            if (RENDER_HITBOXES)
                renderEnemy(gameActivity.mainCharacter, mainTextureDrawer);
            synchronized (gameActivity.enemyLock) {
                renderCharacter(gameActivity.currentEnemy, mainTextureDrawer);
                if (RENDER_HITBOXES)
                    renderEnemy(gameActivity.currentEnemy, mainTextureDrawer);
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

            if (characterAlive) {
                CURRENT_SCORE.number = gameData.score;
                CURRENT_SCORE.addDigitsTexture(mainTextureDrawer);
                HIGHSCORE_TEXT.addLetterTexture(mainTextureDrawer);
            }

            if (gameData.state == GameState.RESTART_SCREEN) {
                gameActivity.restartButton.label.addLetterTexture(mainTextureDrawer);
                gameActivity.quitButton.label.addLetterTexture(mainTextureDrawer);
            }

            if (gameData.paused) {
                mainTextureDrawer.addColoredSquare(PAUSE_LAYER, NO_TEXTURE_COORDS, pauseOverlay);
                mainTextureDrawer.addColoredSquare(PAUSE_RECTANGLE, NO_TEXTURE_COORDS, PAUSE_MENU_COLOR);
                gameActivity.continueButton.label.addLetterTexture(mainTextureDrawer);
                gameActivity.quitButton.label.addLetterTexture(mainTextureDrawer);
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
    private void renderEnemy(GameCharacter c, TextureDrawer drawer){
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
            drawer.addInvertedTexturedSquare(c.spriteContainer, c.getCurrentTexture());
        else
            drawer.addTexturedSquare(c.spriteContainer, c.getCurrentTexture());
    }

    private void drawMenu(){
        gl10.glViewport(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl10.glMatrixMode(GL10.GL_PROJECTION);
        gl10.glLoadIdentity();
        gl10.glOrthof(0, GameLevels.FRUSTUM_WIDTH, 0, GameLevels.FRUSTUM_HEIGHT, 1, -1);
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

        if(gameData.paused) {
            mainTextureDrawer.addColoredSquare(PAUSE_LAYER, NO_TEXTURE_COORDS, pauseOverlay);
            mainTextureDrawer.addColoredSquare(PAUSE_RECTANGLE, NO_TEXTURE_COORDS, PAUSE_MENU_COLOR);
            gameActivity.continueButton.label.addLetterTexture(mainTextureDrawer);
            gameActivity.quitButton.label.addLetterTexture(mainTextureDrawer);
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