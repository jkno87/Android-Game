package com.jgame.game;

import com.jgame.definitions.CharacterInformation;
import com.jgame.elements.ElementCreator;
import com.jgame.elements.GameElement;
import com.jgame.elements.Organism;
import com.jgame.util.Circle;
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

    private final int POINTS_PER_SECOND = 10;
    private final float FOOD_SIZE = 10;
    private final TimeCounter GAME_OVER_UPDATE_INTERVAL = new TimeCounter(0.02f);
    public final static float FRUSTUM_HEIGHT = 480f;
    public final static float FRUSTUM_WIDTH = 320f;
    public final static float BAIT_TIME = 0.5f;
    public final CharacterInformation characterInfo;
    public final ElementCreator elementCreator;
    public final List<GameElement> levelElements;
    public final List<GameElement> capturedElements;
    public final float timeLimit;
    public float timeElapsed;
    public GameState currentState;
    public int timePoints;
    private final Circle characterShip;

    public MainGameFlow(CharacterInformation characterInfo, ElementCreator elementCreator, float timeLimit){
        this.characterInfo = characterInfo;
        this.elementCreator = elementCreator;
        this.timeLimit = timeLimit;
        levelElements = new ArrayList<GameElement>();
        capturedElements = new ArrayList<GameElement>();
        currentState = GameState.PLAYING;
        elementCreator.start();
        characterShip = new Circle(50, FRUSTUM_HEIGHT - 50, 25);
    }

    @Override
    public void handleDrag(float x, float y){
    }

    @Override
    public void handleUp(float x, float y){
        if(currentState != GameState.PLAYING)
            return;

        float gameX = FRUSTUM_WIDTH * x;
        float gameY = FRUSTUM_HEIGHT * y;

        //Se obtiene el lock del objeto levelElements
        synchronized (levelElements) {
            levelElements.add(new Organism(BAIT_TIME, new Vector2(gameX, gameY), FOOD_SIZE));
        }
    }

    @Override
    public void handleDown(float x, float y){
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

                    if (characterShip.contains(e.getPosition())) {
                        capturedElements.add(e);
                        itElements.remove();
                    } else if (!e.vivo())
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
