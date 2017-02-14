package com.jgame.game;

import com.jgame.util.Vector2;

/**
 * Objeto que contiene la informacion del juego
 * Created by jose on 3/07/16.
 */
public class GameData {

    public enum GameState {
        GAME_OVER, PLAYING, RESTART_SCREEN, STARTING, MENU
    }

    public enum Event {
        QUAKE, NONE
    }

    public int score;
    public int highScore;
    public boolean soundEnabled;
    public GameState state;
    public boolean paused;
    public GameActivity.Difficulty currentDifficulty;
    public Vector2 backgroundModifier = new Vector2();

    public void copy(GameData other){
        synchronized (other){
            state = other.state;
            score = other.score;
            highScore = other.highScore;
            paused = other.paused;
            soundEnabled = other.soundEnabled;
            this.currentDifficulty = other.currentDifficulty;
            this.backgroundModifier.set(other.backgroundModifier);
        }
    }

}
