package com.jgame.game;

import android.util.Log;

import com.jgame.definitions.GameLevels;
import com.jgame.elements.FoodOrganism;
import com.jgame.game.LevelInformation.LevelObjective;
import com.jgame.elements.ElementCreator;
import com.jgame.elements.GameElement;
import com.jgame.elements.Organism;
import com.jgame.elements.Trap;
import com.jgame.util.Circle;
import com.jgame.util.GameButton;
import com.jgame.util.Square;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by ej-jose on 12/08/15.
 */
public class MainGameFlow extends GameFlow {

    public enum GameState {
        PLAYING, FINISHED
    }

    public enum BaitSelected {
        NONE, PRIMARY, SECONDARY
    }

    private final int POINTS_PER_SECOND = 10;
    private final float FOOD_SIZE = 5;
    private final float SPECIAL_SIZE = 10;
    private final TimeCounter GAME_OVER_UPDATE_INTERVAL = new TimeCounter(0.02f);
    public final static float BAIT_TIME = 3f;
    public final static int BAIT_HP = 1;
    public final static int BAIT_FP = 1;
    public final ElementCreator elementCreator;
    public final List<GameElement> levelElements;
    public final List<GameElement> elementsInSight;
    public final List<LevelObjective> levelObjectives;
    public final float timeLimit;
    public float timeElapsed;
    public GameState currentState;
    public int timePoints;
    //public final Circle inputBasic;
    //public final Circle inputSecondary;
    public BaitSelected currentBait;
    public Square dragElement;
    public boolean stageCleared;
    //public final GameButton retryButton;
    //public final GameButton quitButton;
    private final GameActivity gameActivity;
    private final LevelInformation levelInfo;
    public final float PLAYING_WIDTH = GameLevels.FRUSTUM_WIDTH * 3;
    public final float PLAYING_HEIGHT = GameLevels.FRUSTUM_HEIGHT * 3;
    private final float PLAYING_LENGTH_X = GameLevels.FRUSTUM_WIDTH / 2;
    private final float PLAYING_LENGTH_Y = GameLevels.FRUSTUM_HEIGHT / 2;
    public Square sightArea;

    public MainGameFlow(LevelInformation levelInfo, ElementCreator elementCreator, float timeLimit, GameActivity gameActivity){
        this.levelInfo = levelInfo;
        this.gameActivity = gameActivity;
        this.elementCreator = elementCreator;
        this.timeLimit = timeLimit;
        levelElements = new ArrayList<GameElement>(15);
        elementsInSight = new ArrayList<GameElement>(15);
        currentState = GameState.PLAYING;
        elementCreator.start();
        //inputBasic = new Circle(FRUSTUM_WIDTH / 2 - 30, FRUSTUM_HEIGHT - 50, 25);
        //inputSecondary = new Circle(FRUSTUM_WIDTH / 2 + 30, FRUSTUM_HEIGHT - 50, 25);
        currentBait = BaitSelected.NONE;
        levelObjectives = levelInfo.getObjectives();
        //retryButton = new GameButton(new Square(FRUSTUM_WIDTH / 2, 100, 60, 25), "retry");
        //quitButton = new GameButton(new Square(FRUSTUM_WIDTH / 2, 50, 60, 25), "return");
        dragElement = new Square(0,0,0,0);
        sightArea = new Square(PLAYING_WIDTH/2, PLAYING_HEIGHT/2, PLAYING_LENGTH_X, PLAYING_LENGTH_Y);
        levelElements.add(new FoodOrganism(BAIT_TIME, new Vector2(PLAYING_WIDTH/2, PLAYING_HEIGHT/2), FOOD_SIZE, BAIT_HP, BAIT_FP));
    }


    /**
     * Se encarga de actualizar la lista de objetivos. Recibe un GameElement e y si se encuentra en la lista
     * de objetivos, actualiza el numero de elementos restantes
     * @param e
     */
    private void updateObjectives(GameElement e){
        int remainingObjectives = 0;
        for(LevelObjective o : levelObjectives){
            if(o.id == e.getId() && o.count > 0)
                o.count--;
            remainingObjectives += o.count;
        }

        if(remainingObjectives == 0) {
            stageCleared = true;
            currentState = GameState.FINISHED;
        }

    }

    @Override
    public void handleDrag(float x, float y){
        /*if(currentBait != BaitSelected.NONE) {
            synchronized (dragElement) {
                dragElement.position.set(FRUSTUM_WIDTH * x, FRUSTUM_HEIGHT * y);
            }
        }*/
    }

    @Override
    public void handleUp(float x, float y){
        if(currentState == GameState.PLAYING)
            handleUpPlaying(x, y);
        else if(currentState == GameState.FINISHED){
            /*float gameX = FRUSTUM_WIDTH * x;
            float gameY = FRUSTUM_HEIGHT * y;

            if(retryButton.bounds.contains(gameX, gameY)){
                gameActivity.setGameFlow(new MainGameFlow(levelInfo, elementCreator, timeLimit, gameActivity));
            } else if(quitButton.bounds.contains(gameX, gameY))
                gameActivity.setGameFlow(new LevelSelectFlow(gameActivity));
            */
        }
    }

    /**
     * Se encarga de manejar los inputs up cuando el juego se encuentra en el estado playing
     * @param x
     * @param y
     */
    public void handleUpPlaying(float x, float y){
        //float gameX = FRUSTUM_WIDTH * x;
        //float gameY = FRUSTUM_HEIGHT * y;

        //Se obtiene el lock del objeto levelElements
        /*if(currentBait != BaitSelected.NONE && dragElement.position.y < GameLevels.MAX_PLAYING_HEIGHT) {
            synchronized (levelElements) {
                if (currentBait == BaitSelected.PRIMARY)
                    levelElements.add(new FoodOrganism(BAIT_TIME, dragElement.position, FOOD_SIZE, BAIT_HP, BAIT_FP));
                else
                    levelElements.add(new Trap(dragElement.position, SPECIAL_SIZE));
            }
        }*/

        synchronized (currentBait) {
            currentBait = BaitSelected.NONE;
        }
    }

    @Override
    public void handleDown(float x, float y){
        //float gameX = FRUSTUM_WIDTH * x;
        //float gameY = FRUSTUM_HEIGHT * y;

        /*if(inputBasic.contains(gameX, gameY)) {
            currentBait = BaitSelected.PRIMARY;
            dragElement = new Square(gameX, gameY, inputBasic.radius, inputBasic.radius, 0);
        } else if (inputSecondary.contains(gameX, gameY)) {
            currentBait = BaitSelected.SECONDARY;
            dragElement = new Square(gameX, gameY, inputSecondary.radius, inputSecondary.radius, 0);
        }*/

    }

    @Override
    public void update(float interval){
        if(currentState == GameState.PLAYING) {
            timeElapsed += interval;
            elementsInSight.clear();
            for(GameElement e : levelElements){
                if(sightArea.collides(e.getBounds()))
                    elementsInSight.add(e);
            }
            /*List<GameElement> captured = new ArrayList<>(10);
            synchronized (levelElements) {
                levelElements.addAll(elementCreator.createElements(interval));
                Iterator<GameElement> itElements = levelElements.iterator();

                while (itElements.hasNext()) {
                    GameElement e = itElements.next();
                    e.update(levelElements, interval);
                    if(e instanceof Trap) {
                        Trap t = (Trap) e;
                        captured.addAll(t.capturedElements);
                        t.capturedElements.clear();
                    }
                    if (!e.alive())
                        itElements.remove();
                }

                for(GameElement e : captured)
                    levelElements.remove(e);
            }

            //Esto se vuelve hacer para hacer calculos que no dependen de levelElements y soltar el lock
            for(GameElement e : captured)
                updateObjectives(e);*/

            //if(timeElapsed >= timeLimit)
            //    currentState = GameState.FINISHED;
        } else if (currentState == GameState.FINISHED){
            updateFinishedGame(interval);
        }
    }


    public void setCurrentOrigin(Vector2 currentOrigin){
        currentOrigin.set(sightArea.getPosition()).sub(PLAYING_LENGTH_X, PLAYING_LENGTH_Y);
    }

    /**
     * Funcion que se llamara cuando el juego se encuentre en el estado game over.
     * Reduce el tiempo mostrado en pantalla para simular que se estan contando los segundos que restaron cuando termina el juego.
     * @param interval diferencia de tiempo que ha transcurrido desde el ultimo update.
     */
    private void updateFinishedGame(float interval){
        GAME_OVER_UPDATE_INTERVAL.accum(interval);
        if(!GAME_OVER_UPDATE_INTERVAL.completed())
            return;

        //TODO: Variables que deben agregarse a la clase para que se muestren al usuario. Ahorita solo estan como variables locales
        float speciesSaved = 0;
        float speciesPoints = 0;

        if(timeElapsed < timeLimit) {
            timeElapsed++;
            speciesSaved--;
            timePoints += POINTS_PER_SECOND;
        }

        GAME_OVER_UPDATE_INTERVAL.reset();
    }

    public int getTimeRemaining(){
        return (int) (timeLimit - timeElapsed);
    }

    @Override
    public void pause(){
    }

    @Override
    public void resume(){
        currentState = GameState.PLAYING;
    }

}
