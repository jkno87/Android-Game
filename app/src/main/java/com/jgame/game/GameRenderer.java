package com.jgame.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;
import com.jgame.definitions.GameLevels;
import com.jgame.elements.Character;
import com.jgame.elements.CollisionObject;
import com.jgame.elements.Player;
import com.jgame.game.MainGameFlow.GameState;
import com.jgame.util.SimpleDrawer;
import com.jgame.util.SimpleDrawer.ColorData;
import com.jgame.util.TextureDrawer;
import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GameRenderer implements Renderer {

    private static final int NO_TEXTURE = 0;
    //private final float FRUSTUM_HEIGHT = 480f;
    //private final float FRUSTUM_WIDTH = 320f;
    private final int SCORE_SIZE = 15;
    private final boolean RENDER_HITBOXES = true;
    private final float FRAME_INTERVAL = 0.015384615f;
    private final float NANO_SCALE = 1000000000.0f;
    public static final ColorData ATTACK_COLOR = new SimpleDrawer.ColorData(0.85f,0.109f,0.207f,0.65f);
    public static final ColorData HITTABLE_COLOR = new SimpleDrawer.ColorData(0,0.75f,0,0.65f);
    public static final ColorData SMASHED_COLOR = new SimpleDrawer.ColorData(0,0,0.65f,0.65f);
    private final TextureData[] DIGITS = new TextureData[]{new TextureData(0,0,0.0625f,1),new TextureData(0.0625f,0,0.125f,1),new TextureData(0.125f,0,0.1825f,1),
            new TextureData(0.1825f,0,0.25f,1),new TextureData(0.25f,0,0.3125f,1),new TextureData(0.3125f,0,0.375f,1),new TextureData(0.375f,0,0.4375f,1),
            new TextureData(0.4375f,0,0.5f,1),new TextureData(0.5f,0,0.5625f,1),new TextureData(0.5625f,0,0.625f,1)};
    /*private final float OBJECTIVES_X_DRAW = FRUSTUM_WIDTH - 65;
    private final float OBJECTIVES_AMOUNT_X = FRUSTUM_WIDTH - 35;
    private final float OBJECTIVES_Y_DRAW = FRUSTUM_HEIGHT - 15;
    private final float OBJECTIVES_SIZE = 10f;
    private final float ENDGAME_LABELS_X = FRUSTUM_WIDTH / 2 - 35;
    private final float ENDGAME_NUMBERS_X = FRUSTUM_WIDTH / 2 + 20;
    private final Vector2 TIMER_POSITION = new Vector2(15, FRUSTUM_HEIGHT - 15);*/
    //public static float[][] TEXTURE_DIGITS = TextureData.createTextureArray(0.0625f, 10);
    private GameSurfaceView surfaceView;
    private long lastUpdate;
    private final TimeCounter updateCounter;
    private GameActivity gameActivity;
    private GL10 gl10;
    int personajesId;
    int alfabetoId;
    private int digitsId;
    private TextureDrawer mainTextureDrawer;
    private TextureDrawer levelSelectDrawer;
    private TextureDrawer pauseTextureDrawer;
    private SimpleDrawer basicDrawer;
    SimpleDrawer.ColorData pauseOverlay;
    SimpleDrawer.ColorData menuBase;
    private Vector2 currentOrigin;
    private final GameFlow.UpdateInterval interval;

    public GameRenderer(GameActivity gameActivity){
        updateCounter = new TimeCounter(FRAME_INTERVAL);
        lastUpdate = System.nanoTime();
        this.gameActivity = gameActivity;
        levelSelectDrawer = new TextureDrawer(false);
        pauseTextureDrawer = new TextureDrawer(false);
        mainTextureDrawer = new TextureDrawer(false);
        basicDrawer = new SimpleDrawer(true);
        pauseOverlay = new SimpleDrawer.ColorData(0,0,0,0.5f);
        menuBase = new SimpleDrawer.ColorData(0,0.75f,0.5f,1);
        currentOrigin = new Vector2();
        interval = new GameFlow.UpdateInterval();
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
        GameFlow gameFlow = gameActivity.getGameFlow();
        long newTime = System.nanoTime();
        interval.delta = (newTime - lastUpdate) / NANO_SCALE;
        lastUpdate = newTime;
        boolean isPaused = gameActivity.isPaused(); //Se copia el valor para soltar el lock

        if(!isPaused) {

            updateCounter.accum(interval);

            if (updateCounter.completed()) {
                updateCounter.reset();
                gameFlow.update(interval);
            }
        }

        if (gameFlow instanceof MainGameFlow)
            drawMainGameFlow(gameFlow);
        else if (gameFlow instanceof LevelSelectFlow)
            drawLevelSelect((LevelSelectFlow) gameFlow);
        else if (gameFlow instanceof FightingGameFlow)
            drawFightingGameFlow((FightingGameFlow) gameFlow);
        if(isPaused)
            drawPauseMenu();

    }

    private void drawMainGameFlow(GameFlow flow){
        MainGameFlow mainGameFlow = (MainGameFlow) flow;
        if(mainGameFlow.currentState == GameState.PLAYING)
            drawPlayingGame(mainGameFlow);
        /*else if(gameFlow.currentState == GameState.FINISHED)
            drawGameFinished(gameFlow);*/
    }

    /**
     * Se encarga de agregar la informacion de las collisionBoxes a SimpleDrawer
     * @param c
     * @param drawer
     * @param currentOrigin
     */
    private void renderEnemy(Character c, SimpleDrawer drawer, Vector2 currentOrigin){
        if(!c.hittable())
            return;

        for(CollisionObject o : c.getActiveCollisionBoxes())
            if(o.type == CollisionObject.TYPE_ATTACK)
                drawer.addSquare(o.bounds, ATTACK_COLOR, currentOrigin);
            else if(o.type == CollisionObject.TYPE_SMASHED)
                drawer.addSquare(o.bounds, SMASHED_COLOR, currentOrigin);
            else
                drawer.addSquare(o.bounds, HITTABLE_COLOR, currentOrigin);
    }

    /**
     * Asigna el sprite que se debe de dibujar para Character c. Requiere que al menos uno de los
     * activeCollisionBoxes sea del tipo TYPE_SPRITE_CONTAINER
     * @param c Character que se va a dibujar
     * @param drawer TextureDrawer al que se le agregara la informacion del personaje.
     */
    private void renderCharacter(Character c, TextureDrawer drawer){
        if(!c.alive())
            return;

        if(c.baseX.x < 0)
            drawer.addInvertedTexturedSquare(c.spriteContainer, c.getCurrentTexture());
        else
            drawer.addTexturedSquare(c.spriteContainer, c.getCurrentTexture());
    }


    private void drawFightingGameFlow(FightingGameFlow flow){
        gl10.glViewport(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);

        gl10.glMatrixMode(GL10.GL_PROJECTION);
        gl10.glLoadIdentity();
        gl10.glOrthof(0, GameLevels.FRUSTUM_WIDTH, 0, GameLevels.FRUSTUM_HEIGHT, 1, -1);

        gl10.glMatrixMode(GL10.GL_MODELVIEW);
        gl10.glEnable(GL10.GL_BLEND);
        gl10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        gl10.glEnable(GL10.GL_TEXTURE_2D);
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, NO_TEXTURE);

        basicDrawer.reset();
        basicDrawer.addSquare(flow.gameFloor, Player.REGULAR_COLOR, currentOrigin);
        for(int i = 0; i < flow.gameButtons.length; i++)
            basicDrawer.addSquare(flow.gameButtons[i].bounds, flow.gameButtons[i].getCurrentColor(), currentOrigin);

        if(RENDER_HITBOXES) {
            renderEnemy(flow.mainCharacter, basicDrawer, currentOrigin);
            renderEnemy(flow.currentEnemy, basicDrawer, currentOrigin);
        }
        basicDrawer.draw(gl10);

        mainTextureDrawer.reset();
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, personajesId);
        renderCharacter(flow.mainCharacter, mainTextureDrawer);
        renderCharacter(flow.currentEnemy, mainTextureDrawer);
        mainTextureDrawer.draw(gl10);

        if(flow.mainCharacter.alive()) {
            mainTextureDrawer.reset();
            gl10.glBindTexture(GL10.GL_TEXTURE_2D, digitsId);
            addDigitsTexture(150, GameLevels.FRUSTUM_HEIGHT - 35, flow.score, mainTextureDrawer);
            mainTextureDrawer.draw(gl10);
        }

        if(flow.currentState == FightingGameFlow.GameState.GAME_OVER){
            gl10.glLoadIdentity();
            gl10.glBindTexture(GL10.GL_TEXTURE_2D, NO_TEXTURE);

            basicDrawer.reset();
            basicDrawer.addColoredRectangle(0, flow.CONTROLS_HEIGHT, GameLevels.FRUSTUM_WIDTH, GameLevels.FRUSTUM_HEIGHT, pauseOverlay);
            basicDrawer.draw(gl10);

            gl10.glLoadIdentity();
            gl10.glBindTexture(GL10.GL_TEXTURE_2D, alfabetoId);

            mainTextureDrawer.reset();
            flow.restartButton.label.addLetterTexture(mainTextureDrawer);
            mainTextureDrawer.draw(gl10);
        }



    }

    private void drawPauseMenu(){

        gl10.glLoadIdentity();
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, NO_TEXTURE);

        basicDrawer.reset();
        basicDrawer.addColoredRectangle(0, 0, GameLevels.FRUSTUM_WIDTH, GameLevels.FRUSTUM_HEIGHT, pauseOverlay);
        basicDrawer.draw(gl10);

        gl10.glLoadIdentity();
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, alfabetoId);

        pauseTextureDrawer.reset();
        gameActivity.continueButton.label.addLetterTexture(pauseTextureDrawer);
        gameActivity.quitButton.label.addLetterTexture(pauseTextureDrawer);
        pauseTextureDrawer.draw(gl10);
    }

    private void drawLevelSelect(LevelSelectFlow flow){
        gl10.glViewport(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);

        gl10.glMatrixMode(GL10.GL_PROJECTION);
        gl10.glLoadIdentity();
        gl10.glOrthof(0, GameLevels.FRUSTUM_WIDTH, 0, GameLevels.FRUSTUM_HEIGHT, 1, -1);

        gl10.glMatrixMode(GL10.GL_MODELVIEW);
        gl10.glEnable(GL10.GL_BLEND);
        gl10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        gl10.glEnable(GL10.GL_TEXTURE_2D);
        gl10.glLoadIdentity();
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, alfabetoId);

        levelSelectDrawer.reset();

        for(int i=0;i<flow.levels.size();i++)
            flow.levels.get(i).label.addLetterTexture(levelSelectDrawer);

        levelSelectDrawer.draw(gl10);
    }

    private void drawPlayingGame(MainGameFlow gameFlow){
        gl10.glViewport(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);

        gl10.glMatrixMode(GL10.GL_PROJECTION);
        gl10.glLoadIdentity();
        gl10.glOrthof(0, GameLevels.FRUSTUM_WIDTH, 0, GameLevels.FRUSTUM_HEIGHT, 1, -1);

        gl10.glMatrixMode(GL10.GL_MODELVIEW);
        gl10.glEnable(GL10.GL_BLEND);
        gl10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        gl10.glBindTexture(GL10.GL_TEXTURE_2D, NO_TEXTURE);
    }

    @Override
    public void onSurfaceChanged(GL10 arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
        gl10 = arg0;
        gl10.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        alfabetoId = loadTexture(R.raw.alfabeto);
        personajesId = loadTexture(R.raw.atlas);
        digitsId = loadTexture(R.raw.digits);
    }
}