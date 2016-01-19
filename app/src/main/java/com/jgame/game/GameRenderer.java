package com.jgame.game;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.jgame.definitions.GameIds;
import com.jgame.definitions.GameLevels;
import com.jgame.elements.GameElement;
import com.jgame.elements.MovingOrganism;
import com.jgame.elements.Organism;
import com.jgame.elements.Trap;
import com.jgame.util.Drawer;
import com.jgame.util.GameButton;
import com.jgame.util.GameText;
import com.jgame.util.SimpleDrawer;
import com.jgame.util.TextureData;
import com.jgame.game.MainGameFlow.GameState;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

public class GameRenderer implements Renderer {

    private static final int NO_TEXTURE = 0;
    //private final float FRUSTUM_HEIGHT = 480f;
    //private final float FRUSTUM_WIDTH = 320f;
    private final float FRAME_INTERVAL = 0.015384615f;
    private final float NANO_SCALE = 1000000000.0f;
    /*private final float OBJECTIVES_X_DRAW = FRUSTUM_WIDTH - 65;
    private final float OBJECTIVES_AMOUNT_X = FRUSTUM_WIDTH - 35;
    private final float OBJECTIVES_Y_DRAW = FRUSTUM_HEIGHT - 15;
    private final float OBJECTIVES_SIZE = 10f;
    private final float ENDGAME_LABELS_X = FRUSTUM_WIDTH / 2 - 35;
    private final float ENDGAME_NUMBERS_X = FRUSTUM_WIDTH / 2 + 20;
    private final Vector2 TIMER_POSITION = new Vector2(15, FRUSTUM_HEIGHT - 15);*/
    public static float[][] TEXTURE_DIGITS = TextureData.createTextureArray(0.0625f, 10);
    private GameSurfaceView surfaceView;
    private long lastUpdate;
    private final TimeCounter updateCounter;
    private GameActivity gameActivity;
    private GL10 gl10;
    int proyectileId;
    int enemyId;
    int mainCharId;
    int decorationId;
    int gameOverId;
    int shipId;
    int digitsId;
    int specialButtonId;
    int mothershipId;
    int personaje2Id;
    int personajesId;
    int alfabetoId;
    private TextureDrawer levelSelectDrawer;
    private TextureDrawer pauseTextureDrawer;
    private SimpleDrawer basicDrawer;
    SimpleDrawer.ColorData pauseOverlay;
    SimpleDrawer.ColorData menuBase;
    private Vector2 currentOrigin;

    public GameRenderer(GameActivity gameActivity){
        updateCounter = new TimeCounter(FRAME_INTERVAL);
        lastUpdate = System.nanoTime();
        this.gameActivity = gameActivity;
        levelSelectDrawer = new TextureDrawer(false);
        pauseTextureDrawer = new TextureDrawer(false);
        basicDrawer = new SimpleDrawer(true);
        pauseOverlay = new SimpleDrawer.ColorData(0,0,0,0.5f);
        menuBase = new SimpleDrawer.ColorData(0,0.75f,0.5f,1);
        currentOrigin = new Vector2();
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
    private void drawDigits(float x, float y, int number){
        //TODO: no utilizar ese tama;o arbitrario de los numeros
        /*gl10.glLoadIdentity();
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, digitsId);
        ArrayList <float[]> textures = new ArrayList<float[]>();

        while(true){
            int nVal = number / 10;
            int rem = number % 10;
            number = nVal;
            textures.add(TEXTURE_DIGITS[rem]);
            if(number == 0)
                break;
        }

        Drawer digitsDrawer = new Drawer(true, true);
        float currentX = x;

        for(int i = textures.size() - 1; i >= 0; i--) {
            digitsDrawer.addTexturedSquare(currentX, y, 10, textures.get(i), GameElement.DEFAULT_COLOR);
            currentX += 22;
        }

        digitsDrawer.draw(gl10);*/
    }

    @Override
    public void onDrawFrame(GL10 arg0) {
        GameFlow gameFlow = gameActivity.getGameFlow();
        long newTime = System.nanoTime();
        float interval = (newTime - lastUpdate) / NANO_SCALE;
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

        if(isPaused)
            drawPauseMenu();

    }

    private float[] getOrganismColor(int id, float pctAlive){
        switch(id){
            case GameIds.FOOD_ORGANISM_ID :
                return new float[]{1, 0, 0, pctAlive};
            case GameIds.MOVING_ORGANISM_ID :
                return new float[]{0,0,1, pctAlive};
            case GameIds.EVOLVED_ORGANISM_ID:
                return new float[]{0,1,0, pctAlive};
            case GameIds.TRAP_ID:
                return new float[]{1,0,1,1};
        }

        return new float[]{0,0,0,0};
    }

    private void drawMainGameFlow(GameFlow flow){
        MainGameFlow mainGameFlow = (MainGameFlow) flow;
        if(mainGameFlow.currentState == GameState.PLAYING)
            drawPlayingGame(mainGameFlow);
        /*else if(gameFlow.currentState == GameState.FINISHED)
            drawGameFinished(gameFlow);*/
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

        /*basicDrawer.reset();
        basicDrawer.addColoredRectangle(FRUSTUM_WIDTH / 2, FRUSTUM_HEIGHT - 40, FRUSTUM_WIDTH / 2, 40,menuBase);
        basicDrawer.draw(gl10);

        synchronized (gameFlow.levelElements) {
            if (!gameFlow.levelElements.isEmpty()) {
                gl10.glLoadIdentity();
                gl10.glBindTexture(GL10.GL_TEXTURE_2D, NO_TEXTURE);
                Drawer bannerDrawer = new Drawer(false, true);
                float[] drawArray = new float[4];
                for (GameElement e : gameFlow.levelElements) {
                    e.getBounds().fillDrawRect(drawArray);
                    bannerDrawer.addColoredRectangle(drawArray[0], drawArray[1],
                            drawArray[2], drawArray[3], getOrganismColor(e.getId(), 1));
                }

                bannerDrawer.draw(gl10);
            }
        }*/

        gl10.glLoadIdentity();
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, NO_TEXTURE);
        basicDrawer.reset();
        //Mutabilidad T_T
        gameFlow.setCurrentOrigin(currentOrigin);

        for(GameElement e : gameFlow.elementsInSight)
            e.getBounds().fillSimpleDrawer(basicDrawer,menuBase,currentOrigin);

        basicDrawer.draw(gl10);

        /*Drawer infoDrawer = new Drawer(false, true);
        infoDrawer.addColoredRectangle(FRUSTUM_WIDTH / 2, FRUSTUM_HEIGHT - 40, FRUSTUM_WIDTH / 2, 40, new float[]{0, 0.75f, 0.5f, 1});
        infoDrawer.addColoredRectangle(gameFlow.inputBasic.position, gameFlow.inputBasic.radius, gameFlow.inputBasic.radius, new float[]{1, 0, 0, 1});
        infoDrawer.addColoredRectangle(gameFlow.inputSecondary.position, gameFlow.inputSecondary.radius, gameFlow.inputSecondary.radius, new float[]{1, 0, 1, 1});
        if(lastUpdatedBait != MainGameFlow.BaitSelected.NONE)
            infoDrawer.addColoredRectangle(gameFlow.dragElement.position, gameFlow.dragElement.lenX, gameFlow.dragElement.lenY,
                    lastUpdatedBait == MainGameFlow.BaitSelected.PRIMARY ? new float[]{1,0,0,1} : new float[]{1,0,1,1});

        infoDrawer.draw(gl10);
        float objCurrentY = OBJECTIVES_Y_DRAW;

        infoDrawer = new Drawer(false, true);

        for(LevelInformation.LevelObjective o : gameFlow.levelObjectives) {
            infoDrawer.addColoredRectangle(OBJECTIVES_X_DRAW, objCurrentY, OBJECTIVES_SIZE,
                    OBJECTIVES_SIZE, getOrganismColor(o.id, 1));
            drawDigits(OBJECTIVES_AMOUNT_X, objCurrentY, o.count);
            objCurrentY -= OBJECTIVES_SIZE + 20;
        }

        gl10.glLoadIdentity();
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, NO_TEXTURE);
        infoDrawer.draw(gl10);

        drawDigits(TIMER_POSITION.x, TIMER_POSITION.y, gameFlow.getTimeRemaining());*/
    }

    private void drawGameFinished(MainGameFlow gameFlow){
        /*gl10.glViewport(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);

        gl10.glMatrixMode(GL10.GL_PROJECTION);
        gl10.glLoadIdentity();
        gl10.glOrthof(0, FRUSTUM_WIDTH, 0, FRUSTUM_HEIGHT, 1, -1);

        gl10.glMatrixMode(GL10.GL_MODELVIEW);
        gl10.glEnable(GL10.GL_BLEND);
        gl10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        float currentY = FRUSTUM_HEIGHT - 50;
        GameText endgameLabels = new GameText("time", ENDGAME_LABELS_X, currentY, 10);
        drawDigits(ENDGAME_NUMBERS_X, currentY, gameFlow.getTimeRemaining());

        gl10.glEnable(GL10.GL_TEXTURE_2D);
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, alfabetoId);
        gl10.glLoadIdentity();

        Drawer textDrawer = new Drawer(true, true);
        //endgameLabels.addLetterTexture(textDrawer);
        textDrawer.draw(gl10);

        currentY -= 40;
        textDrawer = new Drawer(false, true);

        for(LevelInformation.LevelObjective o : gameFlow.levelObjectives) {
            textDrawer.addColoredRectangle(ENDGAME_LABELS_X, currentY,
                    OBJECTIVES_SIZE * 2, OBJECTIVES_SIZE * 2, new float[]{0, 1, 0, 1});
            drawDigits(ENDGAME_NUMBERS_X, currentY, o.count);
            currentY -= 40;
        }

        gl10.glLoadIdentity();
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, NO_TEXTURE);
        textDrawer.draw(gl10);

        currentY -= 40;

        gl10.glEnable(GL10.GL_TEXTURE_2D);
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, alfabetoId);
        gl10.glLoadIdentity();

        textDrawer = new Drawer(true, true);
        //new GameText(gameFlow.stageCleared ? "win" : "lose", ENDGAME_LABELS_X, currentY, 15).addLetterTexture(textDrawer);

        textDrawer.draw(gl10);*/

    }

    @Override
    public void onSurfaceChanged(GL10 arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
        gl10 = arg0;
        gl10.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        proyectileId = loadTexture(R.raw.proyectil);
        alfabetoId = loadTexture(R.raw.alfabeto);
        enemyId = loadTexture(R.raw.enemigos);
        mainCharId = loadTexture(R.raw.personaje);
        shipId = loadTexture(R.raw.nave);
        decorationId = loadTexture(R.raw.bullseye);
        gameOverId = loadTexture(R.raw.gover);
        digitsId = loadTexture(R.raw.digits);
        specialButtonId = loadTexture(R.raw.special1);
        mothershipId = loadTexture(R.raw.mothership);
        personaje2Id = loadTexture(R.raw.personaje2);
        personajesId = loadTexture(R.raw.personajes);
    }
}