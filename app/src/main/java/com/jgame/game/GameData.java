package com.jgame.game;

/**
 * Objeto que contiene la informacion del juego
 * Created by jose on 3/07/16.
 */
public class GameData {

    public enum GameState {
        GAME_OVER, PLAYING, RESTART_SCREEN, STARTING
    }

    public int score;
    public int highScore;
    public GameState state;
    public boolean paused;

    public void copy(GameData other){
        synchronized (other){
            state = other.state;
            score = other.score;
            highScore = other.highScore;
            paused = other.paused;
        }
    }

}
