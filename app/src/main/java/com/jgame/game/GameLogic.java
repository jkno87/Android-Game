package com.jgame.game;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.jgame.characters.DistanceAttack;
import com.jgame.characters.RangedAttack;
import com.jgame.definitions.GameLevels;
import com.jgame.elements.Decoration;
import com.jgame.elements.Enemy;
import com.jgame.elements.EnemySpawner;
import com.jgame.characters.MainCharacter;
import com.jgame.elements.Projectile;
import com.jgame.util.Square;
import com.jgame.util.TextureData;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

public class GameLogic {

    public static abstract class Level{
        public abstract EnemySpawner[] getSpawners();
    }

    public enum GameState {
        GAME_OVER, PLAYING, STAGE_CLEARED, CHARACTER_SELECT, PAUSED
    }

    public static class SelectButton {
        public boolean selected;
        public Square size;
        public TextureData textureData;

        public SelectButton(Square size){
            this.size = size;
        }

        public SelectButton(Square size, TextureData textureData){
           this.size = size;
           this.textureData = textureData;
        }

        public void toggleSelect() {
            selected = selected ? false : true;
        }
    }

    public static class PinButton {
        public Square size;
        public Vector2 currentPosition;

        public PinButton(Square info){
            this.size = info;
            currentPosition = new Vector2(info.position);
        }
    }

    private final int CHARACTER_NONE = -1;
    private final int CHARACTER_HP = 5;
    public final float CHARACTER_SIZE = 30f;
    public final int CHARACTER_STAMINA = 5;
    public final static float FRUSTUM_HEIGHT = 480f;
    public final static float FRUSTUM_WIDTH = 320f;
    public final static float SPECIAL_BUTTON_SIZE = 15;
    private final float TOUCH_ADJUSTMENT = 5f;
    private final Object proyectilesLock = new Object();
    private final Object characterLock = new Object();
    private final ArrayList<Projectile> bufferProjectiles;
    private final ArrayList<Projectile> projectiles;
    private boolean specialSelected;
    public final ArrayList<Decoration> decorations;
    private EnemySpawner[] spawners;
    private final List<Enemy> bufferEnemies;
    public final List<Enemy> enemies;
    private EnemySpawner currentWave;
    private int spawnerIndex;
    public MainCharacter mainCharacter;
    public GameState state;
    public int characterHp;
    public int totalPoints;
    public Vector2 specialButton1;
    public final Square confirmButton;
    public boolean shipDamaged;
    public final SelectButton continueButton;
    public final SelectButton quitButton;
    public final TimeCounter endGameDuration;
    private GameState stashedState;
    public final PinButton [] availableCharacters;
    private int characterSelected;

    public GameLogic(){
        bufferProjectiles = new ArrayList<Projectile>();
        projectiles = new ArrayList<Projectile>();
        mainCharacter = new MainCharacter(new Vector2(FRUSTUM_WIDTH / 2, 50), CHARACTER_SIZE, CHARACTER_STAMINA, new DistanceAttack());
        enemies = new ArrayList<Enemy>();
        state = GameState.CHARACTER_SELECT;
        decorations = new ArrayList<Decoration>();
        bufferEnemies = new ArrayList<Enemy>();
        specialButton1 = new Vector2(FRUSTUM_WIDTH - 75, FRUSTUM_HEIGHT - 20);
        confirmButton = new Square(FRUSTUM_WIDTH - 50, 20, 35, 15);
        continueButton = new SelectButton(new Square(FRUSTUM_WIDTH/2, FRUSTUM_HEIGHT/2 + 40, 60, 20));
        quitButton = new SelectButton(new Square(FRUSTUM_WIDTH/2, FRUSTUM_HEIGHT/2 - 40, 60, 20));
        endGameDuration = new TimeCounter(1.2f);
        availableCharacters = new PinButton[]{ new PinButton(new Square(40, 40, 20, 20))};
        characterSelected = CHARACTER_NONE;
    }
    
    public void start(){
        spawnerIndex = 0;
        enemies.clear();
        bufferEnemies.clear();
        projectiles.clear();
        bufferProjectiles.clear();
        spawners = GameLevels.TEST_LEVEL.getSpawners();
        currentWave = spawners[spawnerIndex];
        state = GameState.PLAYING;
        mainCharacter = new MainCharacter(new Vector2(FRUSTUM_WIDTH / 2, 50), CHARACTER_SIZE, CHARACTER_STAMINA, new DistanceAttack());
        characterHp = CHARACTER_HP;
        totalPoints = 0;
    }

    public void addProjectile(Projectile p){

        int currVal = totalPoints;

        while(currVal > 0){
            int x = currVal / 10;
            int rem = currVal % 10;
            currVal = x;
        }

        synchronized (proyectilesLock){
            bufferProjectiles.add(p);
        }
    }


    public boolean withinBounds(float x, float y){
        return x >= 0 && x <= FRUSTUM_WIDTH && y >= 0 && y <= FRUSTUM_HEIGHT;
    }

    public boolean withinXBounds(float x, float size){
        return x - size >= 0 && x + size <= FRUSTUM_WIDTH;
    }

    public boolean withinYBounds(float y, float size){
        return y - size >= 0 && y + size <= FRUSTUM_HEIGHT;
    }

    public void inputDown(float x, float y){
        float gameX = FRUSTUM_WIDTH * x;
        float gameY = FRUSTUM_HEIGHT * y;

        if(state == GameState.CHARACTER_SELECT) {

            for(int i = 0; i < availableCharacters.length; i++){
                PinButton character = availableCharacters[i];
                if(character.size.within(x,y) &&
                        (characterSelected == CHARACTER_NONE || characterSelected == i))
                    characterSelected = i;
            }

            if(confirmButton.within(gameX, gameY)) {
                boolean selected = false;

                if(selected)
                    mainCharacter = new MainCharacter(new Vector2(FRUSTUM_WIDTH / 2, 50), CHARACTER_SIZE, CHARACTER_STAMINA, new RangedAttack());

                start();
            }

            return;
        }

        if(specialButton1.dist(gameX, gameY) <= SPECIAL_BUTTON_SIZE + TOUCH_ADJUSTMENT) {
            specialSelected = true;
            return;
        }

        synchronized(characterLock){
            mainCharacter.receiveInputDown(gameX, gameY);
        }
    }

    public void drag(float x, float y){
        if(state == GameState.CHARACTER_SELECT && characterSelected != CHARACTER_NONE) {
            availableCharacters[characterSelected].size.position.set(FRUSTUM_WIDTH * x, FRUSTUM_HEIGHT * y);
        }

        float gameX = FRUSTUM_WIDTH * x;
        float gameY = FRUSTUM_HEIGHT * y;

        synchronized(characterLock){
            mainCharacter.receiveInputDrag(gameX, gameY);
        }
    }

    public void release(float x, float y){
        if(state == GameState.CHARACTER_SELECT) {
            characterSelected = CHARACTER_NONE;
            return;
        }

        if(state == GameState.GAME_OVER){
            return;
        }

        float gameX = FRUSTUM_WIDTH * x;
        float gameY = FRUSTUM_HEIGHT * y;

        if(specialSelected){
            specialSelected = false;
            mainCharacter.changeState();
            return;
        }

        Projectile p = null;

        synchronized(characterLock){
            p = mainCharacter.receiveInputUp(gameX, gameY);
        }
        if(p != null)
            addProjectile(p);
    }

    public void updateGameOver(float interval){
        endGameDuration.accum(interval);
    }

    public void updatePaused(){

    }

    public void  pause(){
        stashedState = state;
        state = GameState.PAUSED;
    }

    public void unpause(){
        state = stashedState;
        stashedState = null;
    }

    private void updateSpawner(){
        spawnerIndex++;
        if(spawnerIndex < spawners.length)
            currentWave = spawners[spawnerIndex];
    }

    private boolean gameFinished(){
        return spawnerIndex > spawners.length;
    }

    private void addEnemies(float interval){
        if(currentWave.enemiesRemaining()) {
            enemies.addAll(currentWave.spawnWave(interval));
        } else if(enemies.isEmpty())
            updateSpawner();
    }

    public void addDecoration(Decoration d){
        decorations.add(d);
    }


    public void updateGame(float interval){
        if(state == GameState.GAME_OVER || state == GameState.STAGE_CLEARED){
            updateGameOver(interval);
            return;
        }

        if(state != GameState.PLAYING)
            return;

        addEnemies(interval);

        synchronized(characterLock){
            mainCharacter.update(this, interval);
            mainCharacter.detectCollision(enemies);
        }

        synchronized(proyectilesLock){
            projectiles.addAll(bufferProjectiles);
            bufferProjectiles.clear();
        }

        enemies.addAll(bufferEnemies);
        bufferEnemies.clear();
        if(enemies.isEmpty() && gameFinished()){
            state = GameState.GAME_OVER;
        }


        synchronized (proyectilesLock) {
            for (int i = 0; i < projectiles.size(); i++) {
                Projectile p = projectiles.get(i);
                p.update(this, interval);
                if (p.position.x + p.size < 0 || p.position.x - p.size > FRUSTUM_WIDTH ||
                        p.position.y + p.size < 0 || p.position.y - p.size > FRUSTUM_HEIGHT) {
                    projectiles.remove(i);
                } else {
                    p.detectCollision(enemies);

                    if (!p.vivo()) {
                        projectiles.remove(i);
                    }
                }
            }
        }

        for(int i = 0; i < enemies.size(); i++){
            Enemy e = enemies.get(i);

            if(!e.vivo()){
                totalPoints += e.points;
                enemies.remove(i);
                continue;
            }

            e.update(this, interval);

            if(e.position.y + e.size < 0){
                enemies.remove(i);
                characterHp--;
                shipDamaged = true;
                if(characterHp <= 0)
                    state = GameState.GAME_OVER;
            }
        }

        for(int i = 0; i < decorations.size(); i++){
            Decoration d = decorations.get(i);
            d.update(this, interval);
            if(!d.vivo())
                decorations.remove(i);
        }


    }

    public ArrayList<Projectile> getProjectiles(){
        synchronized(proyectilesLock){
            return projectiles;
        }
    }

    public void addEnemy(Enemy e){
        bufferEnemies.add(e);
    }
}