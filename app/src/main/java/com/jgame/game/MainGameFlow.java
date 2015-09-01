package com.jgame.game;

import com.jgame.characters.DistanceAttack;
import com.jgame.characters.MainCharacter;
import com.jgame.definitions.CharacterInformation;

/**
 * Created by ej-jose on 12/08/15.
 */
public class MainGameFlow extends GameFlow {

    public final static float FRUSTUM_HEIGHT = 480f;
    public final static float FRUSTUM_WIDTH = 320f;
    public final CharacterInformation characterInfo;
    public final MainCharacter mainCharacter;

    public MainGameFlow(CharacterInformation characterInfo){
        this.characterInfo = characterInfo;
        mainCharacter = new MainCharacter(characterInfo.movementController, 5, new DistanceAttack());
    }

    @Override
    public void handleDrag(float x, float y){
        float gameX = FRUSTUM_WIDTH * x;
        float gameY = FRUSTUM_HEIGHT * y;
        mainCharacter.receiveInputDrag(gameX, gameY);
    }

    @Override
    public void handleUp(float x, float y){
        float gameX = FRUSTUM_WIDTH * x;
        float gameY = FRUSTUM_HEIGHT * y;

        mainCharacter.receiveInputUp(gameX, gameY);
    }

    @Override
    public void handleDown(float x, float y){
        float gameX = FRUSTUM_WIDTH * x;
        float gameY = FRUSTUM_HEIGHT * y;

        mainCharacter.receiveInputDown(gameX, gameY);
    }

    @Override
    public void update(float interval){
    }

    @Override
    public void pause(){

    }

    @Override
    public void unpause(){

    }

}
