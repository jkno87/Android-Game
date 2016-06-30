package com.jgame.game;

import com.jgame.util.GameText;
import com.jgame.util.Square;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

/**
 * Created by jose on 16/06/16.
 */
public class MenuFlow extends GameFlow {

    private final GameActivity gameActivity;
    private final TimeCounter titleInterval;
    public boolean renderMessage;
    public final GameText message;

    public MenuFlow(GameActivity gameActivity){
        this.gameActivity = gameActivity;
        this.titleInterval = new TimeCounter(0.5f);
        renderMessage = true;
        message = new GameText("start", new Square(new Vector2(50,50), 150,50,0),10);
    }

    @Override
    public void handleDrag(float x, float y) {

    }

    @Override
    public void handleDown(float x, float y) {
        //gameActivity.setGameFlow(new FightingGameFlow(gameActivity));
    }

    @Override
    public void handleUp(float x, float y) {

    }

    @Override
    public void handlePointerUp(float x, float y) {

    }

    @Override
    public void handlePointerDown(float x, float y) {

    }

    @Override
    public void update(UpdateInterval interval) {
        titleInterval.accum(interval);
        if(titleInterval.completed()){
            renderMessage = !renderMessage;
            titleInterval.reset();
        }

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }
}
