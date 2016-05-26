package com.jgame.game;

import com.jgame.definitions.GameLevels;
import com.jgame.util.LabelButton;
import com.jgame.util.Square;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ej-jose on 5/10/15.
 */
public class LevelSelectFlow extends GameFlow {

    public List<LabelButton> levels;
    private GameActivity gameActivity;

    public LevelSelectFlow(GameActivity gameActivity){
        levels = new ArrayList<>(5);
        levels.add(new LabelButton(new Square(GameLevels.FRUSTUM_WIDTH/2 - 50, GameLevels.FRUSTUM_HEIGHT/2, 100, 35)
                , "tutorial"));

        this.gameActivity = gameActivity;
    }

    @Override
    public void handleDrag(float x, float y) {

    }

    @Override
    public void handleDown(float x, float y) {

    }

    @Override
    public void handleUp(float x, float y) {
        float gameX = GameLevels.FRUSTUM_WIDTH * x;
        float gameY = GameLevels.FRUSTUM_HEIGHT * y;

        for(LabelButton gb : levels)
            if(gb.bounds.contains(gameX, gameY)){
                float sampleTime = 60;//TODO: Esto solo se hace para que se entienda el codigo. Quitar tan pronto como se definan los niveles del juego final.
                gameActivity.setGameFlow(new MainGameFlow(GameLevels.TUTORIAL_CREATOR.create(), GameLevels.TEST_CREATOR, sampleTime, gameActivity));
                break;
            }
    }

    @Override
    public void update(UpdateInterval interval) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    public void handlePointerDown(float x, float y){

    }

    public void handlePointerUp(float x, float y){

    }

}
