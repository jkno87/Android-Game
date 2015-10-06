package com.jgame.game;

import com.jgame.definitions.CharacterInformation;
import com.jgame.definitions.GameLevels;
import com.jgame.elements.ElementCreator;
import com.jgame.elements.GameElement;
import com.jgame.elements.Organism;
import com.jgame.elements.Trap;
import com.jgame.util.Circle;
import com.jgame.util.Square;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    private final float FOOD_SIZE = 10;
    private final float OVERSIZED_TIME = 2.0f;
    private final float SPECIAL_SIZE = 10;
    private final TimeCounter GAME_OVER_UPDATE_INTERVAL = new TimeCounter(0.02f);
    public final static float FRUSTUM_HEIGHT = 480f;
    public final static float FRUSTUM_WIDTH = 320f;
    public final static float BAIT_TIME = 0.5f;
    public final ElementCreator elementCreator;
    public final List<GameElement> levelElements;
    public final List<GameElement> capturedElements;
    public final float timeLimit;
    public float timeElapsed;
    public GameState currentState;
    public int timePoints;
    public final Circle inputBasic;
    public final Circle inputSecondary;
    public BaitSelected currentBait;
    public Square dragElement;

    public MainGameFlow(ElementCreator elementCreator, float timeLimit){
        this.elementCreator = elementCreator;
        this.timeLimit = timeLimit;
        levelElements = new ArrayList<GameElement>();
        capturedElements = new ArrayList<GameElement>();
        currentState = GameState.PLAYING;
        elementCreator.start();
        inputBasic = new Circle(FRUSTUM_WIDTH / 2 - 30, FRUSTUM_HEIGHT - 50, 25);
        inputSecondary = new Circle(FRUSTUM_WIDTH / 2 + 30, FRUSTUM_HEIGHT - 50, 25);
        currentBait = BaitSelected.NONE;
    }

    @Override
    public void handleDrag(float x, float y){
        if(currentBait != BaitSelected.NONE)
            dragElement.position.set(FRUSTUM_WIDTH * x, FRUSTUM_HEIGHT * y);
    }

    @Override
    public void handleUp(float x, float y){
        if(currentState != GameState.PLAYING)
            return;

        float gameX = FRUSTUM_WIDTH * x;
        float gameY = FRUSTUM_HEIGHT * y;

        //Se obtiene el lock del objeto levelElements
        if(currentBait != BaitSelected.NONE && dragElement.position.y < GameLevels.MAX_PLAYING_HEIGHT) {
            synchronized (levelElements) {
                if (currentBait == BaitSelected.PRIMARY)
                    levelElements.add(new Organism(BAIT_TIME, dragElement.position, FOOD_SIZE));
                else
                    levelElements.add(new Trap(dragElement.position, SPECIAL_SIZE));
            }
            dragElement = null;
        }

        currentBait = BaitSelected.NONE;
    }

    @Override
    public void handleDown(float x, float y){
        float gameX = FRUSTUM_WIDTH * x;
        float gameY = FRUSTUM_HEIGHT * y;

        if(inputBasic.contains(gameX, gameY)) {
            currentBait = BaitSelected.PRIMARY;
            dragElement = new Square(gameX, gameY, inputBasic.radius, inputBasic.radius, 0);
        } else if (inputSecondary.contains(gameX, gameY)) {
            currentBait = BaitSelected.SECONDARY;
            dragElement = new Square(gameX, gameY, inputSecondary.radius, inputSecondary.radius, 0);
        }

    }

    @Override
    public void update(float interval){
        if(currentState == GameState.PLAYING) {
            timeElapsed += interval;
            synchronized (levelElements) {
                levelElements.addAll(elementCreator.createElements(interval));
                Iterator<GameElement> itElements = levelElements.iterator();
                while (itElements.hasNext()) {
                    GameElement e = itElements.next();
                    e.update(levelElements, interval);
                    if (!e.vivo())
                        itElements.remove();
                }
            }

            if(timeElapsed >= timeLimit)
                currentState = GameState.FINISHED;
        } else if (currentState == GameState.FINISHED){
            updateFinishedGame(interval);
        }
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

    }

}
