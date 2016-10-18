package com.jgame.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;
import com.jgame.definitions.GameLevels;
import com.jgame.elements.GameCharacter;
import com.jgame.elements.CollisionObject;
import com.jgame.util.GameText;
import com.jgame.util.SimpleDrawer;
import com.jgame.util.SimpleDrawer.ColorData;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer;
import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.game.GameData.GameState;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GameRenderer implements Renderer {

    private final int SCORE_SIZE = 15;
    private final static boolean RENDER_HITBOXES = false;
    public final static ColorData DASHBOARD_COLOR = new ColorData(0.0664f,0.1367f,0.16f,1);
    public final static ColorData NON_HIGHLIGHT = new ColorData(1,1,1,0.45f);
    public final static ColorData BACKGROUND_MODIFIER_2 = new ColorData(0,0.5f,1f,0.25f);
    public final static ColorData BACKGROUND_MODIFIER = new ColorData(1,1,0,0.5f);
    public final static TextureData NO_TEXTURE_COORDS = new TextureData(0.546875f,0.625f,0.5859375f,0.6640625f);
    public final static TextureData SPEAKER_TEXTURE = new TextureData(0.75f,0.875f,0.875f,1);
    public final static TextureData SOUND_TEXTURE = new TextureData(0.875f, 0.875f, 1, 1);
    public final static TextureData BUTTON_TEXTURE = new TextureData(0,0.75f,0.125f,0.875f);
    public final static TextureData ARROW_TEXTURE = new TextureData(0,0.875f,0.125f,1f);
    public final static TextureData LEFT_ARROW_TEXTURE = new TextureData(0.125f,1,0,0.875f);
    public final static TextureData SOUND_SWITCH_ON_TEXTURE = new TextureData(0.75f,0.75f,0.875f,0.875f);
    public final static TextureData SOUND_SWITCH_OFF_TEXTURE = new TextureData(0.875f,0.875f,0.75f,0.75f);
    public final static TextureData BACKGROUND_TEXTURE = new TextureData(0,0.5f,1,1);
    public final static TextureData BACKGROUND_DETAILS = new TextureData(0,0,1,0.5f);
    public static final ColorData ATTACK_COLOR = new SimpleDrawer.ColorData(0.85f,0.109f,0.207f,0.65f);
    public static final ColorData HITTABLE_COLOR = new SimpleDrawer.ColorData(0,0.75f,0,0.65f);
    public static final ColorData SMASHED_COLOR = new SimpleDrawer.ColorData(0,0,0.65f,0.65f);
    private final TextureData[] DIGITS = new TextureData[]{new TextureData(0.125f,0.9375f,0.1875f,1),new TextureData(0.1875f,0.9375f,0.25f,1),new TextureData(0.25f,0.9375f,0.3125f,1),
            new TextureData(0.3125f,0.9375f,0.375f,1),new TextureData(0.375f,0.9375f,0.4375f,1),new TextureData(0.4375f,0.9375f,0.5f,1),new TextureData(0.5f,0.9375f,0.5625f,1),
            new TextureData(0.5625f,0.9375f,0.625f,1),new TextureData(0.625f,0.9375f,0.6875f,1),new TextureData(0.6875f,0.9375f,0.75f,1)};
    public static final Square GAME_FLOOR = new Square(0, 0, GameActivity.PLAYING_WIDTH, GameActivity.CONTROLS_HEIGHT);
    private static final Square PAUSE_LAYER = new Square(0, 0, GameActivity.PLAYING_WIDTH, GameActivity.PLAYING_HEIGHT);
    public static final GameText HIGHSCORE_TEXT = new GameText("highscore", new Square(160, GameLevels.FRUSTUM_HEIGHT - 35, 50, 18), 2);
    public static final GameText TITLE_TEXT = new GameText("start", new Square(160, GameLevels.FRUSTUM_HEIGHT - 150, 150, 50), 5);
    public static final GameText SOUND_LABEL = new GameText("sound", new Square(160, 85, 150, 35), 0);
    public static final GameText ON_LABEL = new GameText("on", new Square(160, 50, 50, 20), 1);
    public static final GameText OFF_LABEL = new GameText("off", new Square(260, 50, 50, 20), 1);
    public static final Square SOUND_SWITCH_SPRITE = new Square(210, 40, 50, 50);
    public static final Square BACKGROUND_SIZE = new Square(0,0,GameActivity.PLAYING_WIDTH, GameActivity.PLAYING_HEIGHT);
    private GameSurfaceView surfaceView;
    private GameActivity gameActivity;
    private GL10 gl10;
    int personajesId;
    int backgroundId;
    private TextureDrawer mainTextureDrawer;
    SimpleDrawer.ColorData pauseOverlay;
    SimpleDrawer.ColorData menuBase;
    private final GameData gameData;

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

    /**
     * Se dibuja en pantalla el numero proporcionado a la funcion
     * @param x coordenada x en la que se inicia el dibujo
     * @param y cooordenada y en la que se dibuja el numero
     * @param number numero que se dibujara en pantalla
     */
    private void addDigitsTexture(float x, float y, int number, TextureDrawer tdrawer){
        //TODO: no utilizar ese tama;o arbitrario de los numeros
        float currentX = x - SCORE_SIZE;

        while(true){
            int nVal = number / 10;
            int rem = number % 10;
            number = nVal;
            tdrawer.addTexturedSquare(currentX, y, SCORE_SIZE, SCORE_SIZE, DIGITS[rem]);
            currentX -= SCORE_SIZE;
            if(number == 0)
                break;
        }
    }

    @Override
    public void onDrawFrame(GL10 arg0) {
        gameData.copy(gameActivity.gameData);
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
            if(gameData.score > GameActivity.EASY_DIFFICULTY_POINTS)
                mainTextureDrawer.addColoredSquare(BACKGROUND_SIZE, BACKGROUND_DETAILS, BACKGROUND_MODIFIER_2);
            else
                mainTextureDrawer.addColoredSquare(BACKGROUND_SIZE, BACKGROUND_DETAILS, BACKGROUND_MODIFIER);
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

            mainTextureDrawer.addColoredSquare(GAME_FLOOR, NO_TEXTURE_COORDS, DASHBOARD_COLOR);
            gl10.glBindTexture(GL10.GL_TEXTURE_2D, personajesId);

            mainTextureDrawer.addTexturedSquare(GameActivity.INPUT_LEFT_BOUNDS, LEFT_ARROW_TEXTURE);
            mainTextureDrawer.addTexturedSquare(GameActivity.INPUT_RIGHT_BOUNDS, ARROW_TEXTURE);
            mainTextureDrawer.addTexturedSquare(GameActivity.INPUT_A_BOUNDS, BUTTON_TEXTURE);

            if (characterAlive) {
                addDigitsTexture(250, 35, gameData.score, mainTextureDrawer);
                addDigitsTexture(250, GameLevels.FRUSTUM_HEIGHT - 35, gameData.highScore, mainTextureDrawer);
                HIGHSCORE_TEXT.addLetterTexture(mainTextureDrawer);
            }

            if (gameData.state == GameState.RESTART_SCREEN) {
                gameActivity.restartButton.label.addLetterTexture(mainTextureDrawer);
                gameActivity.quitButton.label.addLetterTexture(mainTextureDrawer);
            }

            if (gameData.paused) {
                mainTextureDrawer.addColoredSquare(PAUSE_LAYER, NO_TEXTURE_COORDS, pauseOverlay);
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

        TITLE_TEXT.addLetterTexture(mainTextureDrawer);
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