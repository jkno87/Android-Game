package com.jgame.game;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.jgame.elements.GameElement;
import com.jgame.elements.MovingOrganism;
import com.jgame.elements.Organism;
import com.jgame.util.Drawer;
import com.jgame.util.TextureData;
import com.jgame.game.MainGameFlow.GameState;
import com.jgame.util.Square;
import com.jgame.util.TimeCounter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;

public class GameRenderer implements Renderer {

    private static final int NO_TEXTURE = 0;
    private final float FRUSTUM_HEIGHT = 480f;
    private final float FRUSTUM_WIDTH = 320f;
    private final float FRAME_INTERVAL = 0.015384615f;
    private final float NANO_SCALE = 1000000000.0f;
    public static float[][] TEXTURE_DIGITS = TextureData.createTextureArray(0.0625f, 10);
    private GameSurfaceView surfaceView;
    private long lastUpdate;
    private final TimeCounter updateCounter;
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

    public GameRenderer(){
        updateCounter = new TimeCounter(FRAME_INTERVAL);
        lastUpdate = System.nanoTime();
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
        gl10.glEnable(GL10.GL_TEXTURE_2D);
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, digitsId);
        gl10.glLoadIdentity();
        ArrayList <float[]> textures = new ArrayList<float[]>();

        while(true){
            int nVal = number / 10;
            int rem = number % 10;
            number = nVal;
            textures.add(TEXTURE_DIGITS[rem]);
            if(number == 0)
                break;
        }

        Drawer digitsDrawer = new Drawer(gl10, textures.size(), true, true);
        float currentX = x;

        for(int i = textures.size() - 1; i >= 0; i--) {
            digitsDrawer.addJavaVertex(new Square(currentX, y, 10, 10)
                    .getTextureColorCoords(textures.get(i), GameElement.DEFAULT_COLOR));
            currentX += 22;
        }

        digitsDrawer.draw();
    }

    @Override
    public void onDrawFrame(GL10 arg0) {
        long newTime = System.nanoTime();
        float interval = (newTime - lastUpdate) / NANO_SCALE;
        lastUpdate = newTime;
        updateCounter.accum(interval);
        GameFlow flow = surfaceView.getGameFlow();

        if(updateCounter.completed()) {
            updateCounter.reset();
            flow.update(interval);
        }

        if(flow instanceof CharacterSelectFlow)
            drawCharacterSelect(flow);
        else if (flow instanceof MainGameFlow)
            drawMainGameFlow(flow);


    }

    private float[] getOrganismColor(GameElement e){
        if(e instanceof Organism)
            return new float[]{1, 0, 0, e.getPctAlive()};
        else if(e instanceof  MovingOrganism){
            MovingOrganism m = (MovingOrganism) e;
            if(m.currentState == MovingOrganism.State.EVOLVED)
                return new float[]{0,1,0, e.getPctAlive()};
            else
                return new float[]{0,0,1, e.getPctAlive()};
        }

        else
            return new float[]{0,0,0,0};
    }

    private void drawMainGameFlow(GameFlow flow){
        MainGameFlow gameFlow = (MainGameFlow) flow;
        if(gameFlow.currentState == GameState.PLAYING)
            drawPlayingGame(gameFlow);
        else if(gameFlow.currentState == GameState.FINISHED)
            drawGameFinished(gameFlow);
    }


    private void drawPlayingGame(MainGameFlow gameFlow){
        gl10.glViewport(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);

        gl10.glMatrixMode(GL10.GL_PROJECTION);
        gl10.glLoadIdentity();
        gl10.glOrthof(0, FRUSTUM_WIDTH, 0, FRUSTUM_HEIGHT, 1, -1);

        gl10.glMatrixMode(GL10.GL_MODELVIEW);
        gl10.glEnable(GL10.GL_BLEND);
        gl10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        gl10.glEnable(GL10.GL_TEXTURE_2D);
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, personajesId);
        gl10.glLoadIdentity();

        Drawer characterDrawer = new Drawer(gl10, 1, true, true);
        characterDrawer.addJavaVertex(new Square(50, FRUSTUM_HEIGHT - 50, 25, 25)
                .getTextureColorCoords(TextureData.USE_WHOLE_IMAGE, new float[]{1,1,1,1}));
        characterDrawer.draw();

        if(!gameFlow.levelElements.isEmpty()) {
            gl10.glLoadIdentity();
            gl10.glBindTexture(GL10.GL_TEXTURE_2D, NO_TEXTURE);
            Drawer bannerDrawer = new Drawer(gl10, gameFlow.levelElements.size(), false, true);
            for(GameElement e : gameFlow.levelElements) {
                bannerDrawer.addJavaVertex(Square.getSimpleCoords(e.getPosition(), e.getSize(), e.getSize(),
                        getOrganismColor(e)));
            }

            bannerDrawer.draw();
        }
    }

    private void drawGameFinished(MainGameFlow gameFlow){
        gl10.glViewport(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);

        gl10.glMatrixMode(GL10.GL_PROJECTION);
        gl10.glLoadIdentity();
        gl10.glOrthof(0, FRUSTUM_WIDTH, 0, FRUSTUM_HEIGHT, 1, -1);

        gl10.glMatrixMode(GL10.GL_MODELVIEW);
        gl10.glEnable(GL10.GL_BLEND);
        gl10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        drawDigits(FRUSTUM_WIDTH / 2, FRUSTUM_HEIGHT / 2, gameFlow.getTimeRemaining());
    }

    private void drawCharacterSelect(GameFlow flow){
        CharacterSelectFlow cFlow = (CharacterSelectFlow) flow;
        gl10.glViewport(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);

        gl10.glMatrixMode(GL10.GL_PROJECTION);
        gl10.glLoadIdentity();
        gl10.glOrthof(0, FRUSTUM_WIDTH, 0, FRUSTUM_HEIGHT, 1, -1);

        gl10.glMatrixMode(GL10.GL_MODELVIEW);
        gl10.glEnable(GL10.GL_BLEND);
        gl10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        gl10.glEnable(GL10.GL_TEXTURE_2D);
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, shipId);
        gl10.glLoadIdentity();
        Drawer shipDrawer = new Drawer(gl10, 1, true, true);
        shipDrawer.addJavaVertex(new Square(FRUSTUM_WIDTH / 2, FRUSTUM_HEIGHT / 2,
                100, 100, 0)
                .getTextureColorCoords(TextureData.USE_WHOLE_IMAGE, new float[]{1, 1, 1, 1}));

        shipDrawer.draw();

        gl10.glEnable(GL10.GL_TEXTURE_2D);
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, personajesId);
        gl10.glLoadIdentity();

        Drawer characterDrawer = new Drawer(gl10, cFlow.availableCharacters.length, true, false);
        for(int i = 0; i < cFlow.availableCharacters.length; i++)
            characterDrawer.addJavaVertex(cFlow.availableCharacters[i].size.getTextureCoords(cFlow.availableCharacters[i].characterInfo.textureInfo));
        characterDrawer.draw();

        if(cFlow.shipsFilled()) {
            /*gl10.glLoadIdentity();
            gl10.glBindTexture(GL10.GL_TEXTURE_2D, NO_TEXTURE);
            Drawer buttonDrawer = new Drawer(gl10, 1, false, true);
            buttonDrawer.addJavaVertex(cFlow.confirmButton.getSimpleCoords(new float[]{0.75f, 0.55f, 0.7f, 1}));
            buttonDrawer.draw();*/

            gl10.glEnable(GL10.GL_TEXTURE_2D);
            gl10.glBindTexture(GL10.GL_TEXTURE_2D, alfabetoId);
            gl10.glLoadIdentity();

            float[][] glText = cFlow.continueLabel.getLettersTexture();

            Drawer textDrawer = new Drawer(gl10, glText.length, true, false);
            for(int i = 0; i < glText.length; i++)
                textDrawer.addJavaVertex(glText[i]);

            textDrawer.draw();

        }

    }

    private void drawStageCleared() {
        gl10.glViewport(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);

        gl10.glMatrixMode(GL10.GL_PROJECTION);
        gl10.glLoadIdentity();
        gl10.glOrthof(0, FRUSTUM_WIDTH, 0, FRUSTUM_HEIGHT, 1, -1);

        gl10.glMatrixMode(GL10.GL_MODELVIEW);
        gl10.glEnable(GL10.GL_BLEND);
        gl10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl10.glEnable(GL10.GL_TEXTURE_2D);
    }

    private List<Integer> getScoreTextures(int score){
        ArrayList<Integer> textureData = new ArrayList<Integer>();

        while(true){
            int x = score / 10;
            int rem = score % 10;
            score = x;
            textureData.add(rem);
            if(score == 0)
                break;
        }

        return textureData;
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