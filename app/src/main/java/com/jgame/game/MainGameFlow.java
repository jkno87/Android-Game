package com.jgame.game;

import com.jgame.characters.DistanceAttack;
import com.jgame.characters.MainCharacter;
import com.jgame.definitions.CharacterInformation;
import com.jgame.elements.ElementCreator;
import com.jgame.elements.GameElement;
import com.jgame.elements.Organism;
import com.jgame.util.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ej-jose on 12/08/15.
 */
public class MainGameFlow extends GameFlow {

    public final static float FRUSTUM_HEIGHT = 480f;
    public final static float FRUSTUM_WIDTH = 320f;
    public final static float BAIT_TIME = 0.5f;
    public final CharacterInformation characterInfo;
    public final ElementCreator elementCreator;
    public final List<GameElement> levelElements;

    public MainGameFlow(CharacterInformation characterInfo, ElementCreator elementCreator){
        this.characterInfo = characterInfo;
        this.elementCreator = elementCreator;
        levelElements = new ArrayList<GameElement>();
        elementCreator.start();
    }

    @Override
    public void handleDrag(float x, float y){
    }

    @Override
    public void handleUp(float x, float y){
        float gameX = FRUSTUM_WIDTH * x;
        float gameY = FRUSTUM_HEIGHT * y;

        levelElements.add(new Organism(BAIT_TIME, new Vector2(gameX, gameY)));
    }

    @Override
    public void handleDown(float x, float y){
    }

    @Override
    public void update(float interval){
        levelElements.addAll(elementCreator.createElements(interval));
        Iterator<GameElement> itElements = levelElements.iterator();
        while(itElements.hasNext()){
            GameElement e = itElements.next();
            e.update(levelElements, interval);
            if(!e.vivo())
                itElements.remove();
        }
    }

    @Override
    public void pause(){

    }

    @Override
    public void unpause(){

    }

}
