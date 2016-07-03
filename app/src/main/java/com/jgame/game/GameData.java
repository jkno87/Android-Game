package com.jgame.game;

/**
 * Objeto que contiene la informacion del juego
 * Created by jose on 3/07/16.
 */
public class GameData {

    public boolean gameOver;
    public boolean paused;
    public int score;
    public int highScore;

    public void copy(GameData other){
        synchronized (other){
            gameOver = other.gameOver;
            paused = other.paused;
            score = other.score;
            highScore = other.highScore;
        }
    }

}
