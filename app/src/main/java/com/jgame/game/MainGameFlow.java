package com.jgame.game;

import android.util.Log;

import com.jgame.definitions.GameLevels;
import com.jgame.elements.DecorationElement;
import com.jgame.elements.FoodOrganism;
import com.jgame.elements.MovingOrganism;
import com.jgame.elements.Player;
import com.jgame.game.LevelInformation.LevelObjective;
import com.jgame.elements.ElementCreator;
import com.jgame.elements.GameElement;
import com.jgame.elements.Organism;
import com.jgame.elements.Trap;
import com.jgame.util.Circle;
import com.jgame.util.GameButton;
import com.jgame.util.Grid;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
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

    private final float PLAYER_SIZE = 85f;
    //private final int POINTS_PER_SECOND = 10;
    //private final float FOOD_SIZE = 5;
    //private final float SPECIAL_SIZE = 10;
    //private final TimeCounter GAME_OVER_UPDATE_INTERVAL = new TimeCounter(0.02f);
    //public final static float BAIT_TIME = 3f;
    //public final static int BAIT_HP = 1;
    //public final static int BAIT_FP = 1;
    public final ElementCreator elementCreator;
    public final List<GameElement> levelElements;
    public final List<GameElement> elementsInSight;
    public final List<LevelObjective> levelObjectives;
    public final float timeLimit;
    public float timeElapsed;
    public GameState currentState;
    //public int timePoints;
    public BaitSelected currentBait;
    public Square dragElement;
    public boolean stageCleared;
    //public final GameButton retryButton;
    //public final GameButton quitButton;
    //private final GameActivity gameActivity;
    private final LevelInformation levelInfo;
    public final static int NUMBER_OF_ROWS = 4;
    public final static int NUMBER_OF_COLUMNS = 3;
    public final static float PLAYING_WIDTH = GameLevels.FRUSTUM_WIDTH * NUMBER_OF_COLUMNS;
    public final static float PLAYING_HEIGHT = GameLevels.FRUSTUM_HEIGHT * NUMBER_OF_ROWS;
    public final Player player;
    public final Object elementsLock = new Object();
    public final Object playerStateLock = new Object();
    public final Grid constantElements;
    public final Grid dynamicElements;
    private final List<GameElement> interactiveElements;

    public MainGameFlow(LevelInformation levelInfo, ElementCreator elementCreator, float timeLimit, GameActivity gameActivity){
        this.levelInfo = levelInfo;
        //this.gameActivity = gameActivity;
        this.elementCreator = elementCreator;
        this.timeLimit = timeLimit;
        levelElements = new ArrayList<GameElement>(15);
        elementsInSight = new ArrayList<GameElement>(35);
        currentState = GameState.PLAYING;
        elementCreator.start();
        currentBait = BaitSelected.NONE;
        levelObjectives = levelInfo.getObjectives();
        //retryButton = new GameButton(new Square(FRUSTUM_WIDTH / 2, 100, 60, 25), "retry");
        //quitButton = new GameButton(new Square(FRUSTUM_WIDTH / 2, 50, 60, 25), "return");
        dragElement = new Square(0,0,0,0);
        player = new Player(new Vector2(PLAYING_WIDTH/2, PLAYING_HEIGHT/2), PLAYER_SIZE,
                GameLevels.FRUSTUM_WIDTH, GameLevels.FRUSTUM_HEIGHT);

        constantElements = new Grid(PLAYING_WIDTH, PLAYING_HEIGHT, GameLevels.FRUSTUM_WIDTH, GameLevels.FRUSTUM_HEIGHT);
        initializeGrid(constantElements,12,10.0f,null);
        dynamicElements = new Grid(PLAYING_WIDTH, PLAYING_HEIGHT, GameLevels.FRUSTUM_WIDTH, GameLevels.FRUSTUM_HEIGHT);
        interactiveElements = new ArrayList<>(50);
        interactiveElements.add(new MovingOrganism(50, new Vector2(PLAYING_WIDTH/2, PLAYING_HEIGHT/2), 15, 20, 1000));
    }


    /**
     * Se inicializa el grid con los elementos que van a funcionar como decoracion en el juego.
     * @param grid Grid que se llenara con los elementos decorativos
     * @param elementsPerRegion Numero de elementos que se generaran para cada region
     * @param decorationSize Tama;o de los elementos que se van a generar
     * @param tdata informacion de la textura de los elementos que se van a generar
     */
    private void initializeGrid(Grid grid, int elementsPerRegion, float decorationSize, TextureDrawer.TextureData tdata){
        Random r = new Random();
        float currentX = 0;
        float currentY = 0;

        for(int i = 1; i <= NUMBER_OF_ROWS * NUMBER_OF_COLUMNS; i++) {
            for(int j = 0; j < elementsPerRegion; j++)
                grid.addElement(new DecorationElement(tdata,
                    new Square(r.nextFloat() * GameLevels.FRUSTUM_WIDTH + currentX,
                            r.nextFloat() * GameLevels.FRUSTUM_HEIGHT + currentY,
                            decorationSize, decorationSize), j + elementsPerRegion * (i-1)) {
                    @Override
                    public void update(List<GameElement> others, float timeDifference) {

                    }

                    @Override
                    public boolean alive() {
                        return true;
                    }
                });

            currentX += GameLevels.FRUSTUM_WIDTH;

            if(i % NUMBER_OF_COLUMNS == 0){
                currentX = 0;
                currentY += GameLevels.FRUSTUM_HEIGHT;
            }
        }
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
        float gameX = GameLevels.FRUSTUM_WIDTH * x + player.sightArea.position.x;
        float gameY = GameLevels.FRUSTUM_HEIGHT * y + player.sightArea.position.y;

        synchronized (playerStateLock) {
            if (player.getBounds().contains(gameX, gameY)) {
                if (player.state == Player.PlayerState.STOPPED)
                    player.setStateInputSelection();
                else
                    player.setStoppedState();
            }

            else if(player.state == Player.PlayerState.INPUT_SELECTION){
                if(player.inputArea.contains(gameX, gameY))
                    player.changeDirection(gameX, gameY);
                else
                    player.setStoppedState();
            }
        }

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
            player.update(null, interval);
            dynamicElements.clear();

            for(GameElement e : interactiveElements) {
                e.update(null, interval);
                dynamicElements.addElement(e);
            }

            synchronized (elementsLock) {
                elementsInSight.clear();
                constantElements.getElementsIn(player.sightArea, elementsInSight);
                dynamicElements.getElementsIn(player.sightArea, elementsInSight);
            }
            /*for(GameElement e : levelElements){
                if(sightArea.collides(e.getBounds()))
                    elementsInSight.add(e);
            }*/
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

    /**
     * Recibe un Vector2 al cual le asigna el origen del juego y se dibuje correctamente en la pantalla.
     * Esto se hace para que se ahorre memoria y no se generen mas variables.
     * @param currentOrigin
     */
    public void setCurrentOrigin(Vector2 currentOrigin){
        currentOrigin.set(player.sightArea.getPosition());
    }

    /**
     * Funcion que se llamara cuando el juego se encuentre en el estado game over.
     * Reduce el tiempo mostrado en pantalla para simular que se estan contando los segundos que restaron cuando termina el juego.
     * @param interval diferencia de tiempo que ha transcurrido desde el ultimo update.
     */
    private void updateFinishedGame(float interval){
        /*GAME_OVER_UPDATE_INTERVAL.accum(interval);
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

        GAME_OVER_UPDATE_INTERVAL.reset();*/
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
