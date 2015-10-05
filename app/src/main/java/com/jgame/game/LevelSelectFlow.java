package com.jgame.game;

import com.jgame.definitions.GameLevels;
import com.jgame.util.GameButton;
import com.jgame.util.Square;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ej-jose on 5/10/15.
 */
public class LevelSelectFlow extends GameFlow {

    public List<GameButton> levels;
    private GameActivity gameActivity;

    public LevelSelectFlow(GameActivity gameActivity){
        levels = new ArrayList<>();
        levels.add(new GameButton(new Square(GameLevels.FRUSTUM_WIDTH/2, GameLevels.FRUSTUM_HEIGHT/2, 50, 15)
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

        for(GameButton gb : levels)
            if(gb.bounds.contains(gameX, gameY)){
                float sampleTime = 60;//TODO: Esto solo se hace para que se entienda el codigo. Quitar tan pronto como se definan los niveles del juego final.
                gameActivity.setGameFlow(new MainGameFlow(GameLevels.TEST_CREATOR, sampleTime));
                break;
            }
    }

    @Override
    public void update(float interval) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }
}
