package com.jgame.elements;

import com.jgame.definitions.GameLevels;
import com.jgame.game.MainGameFlow;
import com.jgame.util.GeometricElement;
import com.jgame.util.Grid;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer;
import java.util.List;
import java.util.Random;

/**
 * Este objeto representa Elementos del juego que solo se utilizaran para dibujarse en pantalla pero no afectan el gameplay.
 * Created by jose on 25/01/16.
 */
public abstract class DecorationElement implements GameElement {

    public static final float VERTICAL_OFFSET = 10.0f;
    public static final float HORIZONTAL_OFFSET = 10.0f;

    public static void initializeGrid(Grid grid, int elements, float size, TextureDrawer.TextureData tdata){
        Random r = new Random();
        float maxX = MainGameFlow.PLAYING_WIDTH - HORIZONTAL_OFFSET*2;
        float maxY = MainGameFlow.PLAYING_HEIGHT - VERTICAL_OFFSET*2;

        for(int i = 0; i < elements; i++)
            grid.addElement(new DecorationElement(tdata,
                    new Square(r.nextFloat() * maxX + HORIZONTAL_OFFSET,r.nextFloat() * maxY + HORIZONTAL_OFFSET,
                            size,size), i) {
                @Override
                public void update(List<GameElement> others, float timeDifference) {

                }

                @Override
                public boolean alive() {
                    return true;
                }
            });
    }

    public final TextureDrawer.TextureData tData;
    public final Square bounds;
    public final int id;

    public DecorationElement(TextureDrawer.TextureData tData, Square bounds, int id){
        this.tData = tData;
        this.bounds = bounds;
        this.id = id;
    }

    @Override
    public int getId(){
        return id;
    }

    @Override
    public GeometricElement getBounds(){
        return bounds;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof DecorationElement){
            return ((DecorationElement) o).getId() == getId();
        } else
            return false;
    }

    @Override
    public int hashCode(){
        return getId();
    }

}
