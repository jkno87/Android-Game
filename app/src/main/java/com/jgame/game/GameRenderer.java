package com.jgame.game;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.jgame.definitions.GameIds;
import com.jgame.elements.Decoration;
import com.jgame.elements.GameElement;
import com.jgame.characters.MainCharacter;
import com.jgame.elements.Projectile;
import com.jgame.util.Drawer;
import com.jgame.util.GameText;
import com.jgame.util.TextureData;
import com.jgame.elements.Enemy;
import com.jgame.game.GameLogic.GameState;
import com.jgame.util.Square;
import com.jgame.util.TimeCounter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

public class GameRenderer implements Renderer {

    private static final int NO_TEXTURE = 0;
    private final float FRUSTUM_HEIGHT = 480f;
    private final float FRUSTUM_WIDTH = 320f;
    private final float FRAME_INTERVAL = 0.015384615f;
    private final float NANO_SCALE = 1000000000.0f;//1000000000.0f;
    public static float[][] TEXTURE_DIGITS = TextureData.createTextureArray(0.0625f, 10);

    private GameLogic logic;
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

    private void drawMainGameFlow(GameFlow flow){
        MainGameFlow gameFlow = (MainGameFlow) flow;
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
        Drawer mothershipDrawer = new Drawer(gl10, 1, true, false);
        mothershipDrawer.addJavaVertex(new Square(gameFlow.mainCharacter.getPosition(), 50, 50, 0)
                .getTextureCoords(gameFlow.characterInfo.textureInfo));
        mothershipDrawer.draw();

        gl10.glLoadIdentity();
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, NO_TEXTURE);
        Drawer bannerDrawer = new Drawer(gl10, 5 + 1, false, true);
        bannerDrawer.addJavaVertex(Square.getSimpleCoords(0, FRUSTUM_HEIGHT - 20, FRUSTUM_WIDTH, 20,
                new float[]{0.75f, 0.98f, 0.7f, 1}));

        int i = 0;
        float currX = 10;
        float barY = FRUSTUM_HEIGHT - 20;
        while (i < 5) {
            bannerDrawer.addJavaVertex(Square.getSimpleCoords(currX, barY, 5, 5, new float[]{1,0,0,1}));
            currX += 15;
            i++;
        }

        bannerDrawer.draw();
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
        shipDrawer.addJavaVertex(new Square(FRUSTUM_WIDTH /2, FRUSTUM_HEIGHT / 2,
                100, 100, 0)
                .getTextureColorCoords(TextureData.USE_WHOLE_IMAGE, new float[]{1,1,1,1}));

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

    private void drawGameOver() {

        gl10.glViewport(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);

        gl10.glMatrixMode(GL10.GL_PROJECTION);
        gl10.glLoadIdentity();
        gl10.glOrthof(0, FRUSTUM_WIDTH, 0, FRUSTUM_HEIGHT, 1, -1);

        gl10.glMatrixMode(GL10.GL_MODELVIEW);
        gl10.glEnable(GL10.GL_BLEND);
        gl10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl10.glEnable(GL10.GL_TEXTURE_2D);

        gl10.glBindTexture(GL10.GL_TEXTURE_2D, gameOverId);
        gl10.glLoadIdentity();
        Drawer bannerDrawer = new Drawer(gl10, 1, true, false);
        bannerDrawer.addJavaVertex(new Square(FRUSTUM_WIDTH / 2, FRUSTUM_HEIGHT / 2,
                35 * logic.endGameDuration.pctCharged(),100 * logic.endGameDuration.pctCharged())
                .getTextureCoords(TextureData.USE_WHOLE_IMAGE));
        bannerDrawer.draw();

        if(logic.endGameDuration.completed()) {
            gl10.glLoadIdentity();
            gl10.glBindTexture(GL10.GL_TEXTURE_2D, NO_TEXTURE);
            Drawer gameOverMenu = new Drawer(gl10, 3, false, true);
            gameOverMenu.addJavaVertex(Square.getSimpleCoords(FRUSTUM_WIDTH / 2, FRUSTUM_HEIGHT / 2,
                    80, 80, new float[]{0.95f, 0.98f, 0.85f, 1}));
            gameOverMenu.addJavaVertex(logic.continueButton.size.getSimpleCoords(
                    new float[]{0, 1, 0, 1}));
            gameOverMenu.addJavaVertex(logic.quitButton.size.getSimpleCoords(
                    new float[]{1, 0, 0, 1}));
            gameOverMenu.draw();
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


    private void drawGame() {

        gl10.glViewport(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);

        gl10.glMatrixMode(GL10.GL_PROJECTION);
        gl10.glLoadIdentity();
        gl10.glOrthof(0, FRUSTUM_WIDTH, 0, FRUSTUM_HEIGHT, 1, -1);

        gl10.glMatrixMode(GL10.GL_MODELVIEW);
        gl10.glEnable(GL10.GL_BLEND);
        gl10.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        gl10.glEnable(GL10.GL_TEXTURE_2D);
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, mothershipId);
        gl10.glLoadIdentity();


        float [] shipColor = new float[]{0,0,1,1};
        if(logic.shipDamaged){
            shipColor = new float[]{1,0.5f,0.5f,1};
            logic.shipDamaged = false;
        }

        Drawer mothershipDrawer = new Drawer(gl10, 1, true, true);
        mothershipDrawer.addJavaVertex(new Square(FRUSTUM_WIDTH / 2, 50, FRUSTUM_WIDTH / 2, 50).
                getTextureColorCoords(TextureData.USE_WHOLE_IMAGE, shipColor));
        mothershipDrawer.draw();


        gl10.glEnable(GL10.GL_TEXTURE_2D);
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, shipId);
        gl10.glLoadIdentity();

        float[] characterColor = logic.mainCharacter.state == MainCharacter.CharacterState.NORMAL ?
                new float[]{1,1,1,1} : new float[]{0,0,1,1};

        Drawer characterDrawer = new Drawer(gl10, 1, true, true);
        characterDrawer.addJavaVertex(new Square(logic.mainCharacter.getPosition(),
                logic.CHARACTER_SIZE, logic.CHARACTER_SIZE, logic.mainCharacter.getAngle())
                .getTextureColorCoords(TextureData.USE_WHOLE_IMAGE, characterColor));
        characterDrawer.draw();
        characterDrawer.reset();
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, mainCharId);
        characterDrawer.addJavaVertex(new Square(logic.mainCharacter.getPosition(),
                logic.CHARACTER_SIZE, logic.CHARACTER_SIZE, 0)
                .getTextureColorCoords(TextureData.USE_WHOLE_IMAGE, characterColor));
        characterDrawer.draw();

        List<Projectile> ps = logic.getProjectiles();
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, proyectileId);
        if (!ps.isEmpty()) {
            gl10.glLoadIdentity();
            characterDrawer = new Drawer(gl10, ps.size(), true, false);
            for (Projectile p : ps)
                characterDrawer.addJavaVertex(new Square(p.position.x, p.position.y, p.size, p.size)
                        .getTextureCoords(TextureData.MAIN_CHARACTER_TEXTURES));

            characterDrawer.draw();
        }

        gl10.glBindTexture(GL10.GL_TEXTURE_2D, enemyId);
        //List<Enemy> enemies = logic.getEnemies();
        if (!logic.enemies.isEmpty()) {
            gl10.glLoadIdentity();
            characterDrawer = new Drawer(gl10, logic.enemies.size(), true, true);
            for (Enemy e : logic.enemies)
                characterDrawer.addJavaVertex(new Square(e.position.x, e.position.y, e.size, e.size, e.direction.angle())
                        .getTextureColorCoords(e.textData, e.hitFrames > 0 ? GameElement.HIT_COLOR : GameElement.DEFAULT_COLOR));

            characterDrawer.draw();
        }

        gl10.glBindTexture(GL10.GL_TEXTURE_2D, decorationId);
        if(!logic.decorations.isEmpty()){
            gl10.glLoadIdentity();
            characterDrawer = new Drawer(gl10, logic.decorations.size(), true, true);
            for (Decoration d : logic.decorations)
                characterDrawer.addJavaVertex(d.getDrawSquare().getTextureColorCoords(d.textureData, Decoration.colorData));

            characterDrawer.draw();
        }

        gl10.glLoadIdentity();
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, NO_TEXTURE);
        Drawer bannerDrawer = new Drawer(gl10, logic.characterHp + 1, false, true);
        bannerDrawer.addJavaVertex(Square.getSimpleCoords(0, FRUSTUM_HEIGHT - 20, FRUSTUM_WIDTH, 20,
                new float[]{0.75f, 0.98f, 0.7f, 1}));

        int i = 0;
        float currX = 10;
        float barY = FRUSTUM_HEIGHT - 20;
        while (i < logic.characterHp) {
            bannerDrawer.addJavaVertex(Square.getSimpleCoords(currX, barY, 5, 5, new float[]{1,0,0,1}));
            currX += 15;
            i++;
        }
        //i = 0;
        /*while (i < logic.mainCharacter.remainingSpecials) {
            bannerDrawer.addJavaVertex(Square.getSimpleCoords(currX, barY, 5, 5, new float[]{0,1,0,1}));
            currX += 15;
            i++;
        }*/
        bannerDrawer.draw();

        currX = FRUSTUM_WIDTH - 75;
        Drawer specialButtonDrawer = new Drawer(gl10, 1, true, true);
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, specialButtonId);
        specialButtonDrawer.addJavaVertex(new Square(logic.specialButton1.x, logic.specialButton1.y,
                GameLogic.SPECIAL_BUTTON_SIZE, GameLogic.SPECIAL_BUTTON_SIZE, 0)
                .getTextureColorCoords(TextureData.USE_WHOLE_IMAGE, new float[]{1,1,1,1}));
        specialButtonDrawer.draw();

        //DRAW SCORE
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, digitsId);
        List<Integer> scoreDigits = getScoreTextures(logic.totalPoints);
        bannerDrawer = new Drawer(gl10, scoreDigits.size(), true, true);
        i = scoreDigits.size() - 1;
        currX = 125;
        while(i >= 0){
            bannerDrawer.addJavaVertex(new Square(currX, barY, 10, 10)
                            .getTextureColorCoords(TEXTURE_DIGITS[scoreDigits.get(i)], GameElement.DEFAULT_COLOR));
            currX += 22;
            i--;
        }
        bannerDrawer.draw();
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