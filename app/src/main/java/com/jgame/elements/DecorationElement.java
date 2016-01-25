package com.jgame.elements;

import com.jgame.util.GeometricElement;
import com.jgame.util.Grid;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer;

import java.util.List;

/**
 * Este objeto representa Elementos del juego que solo se utilizaran para dibujarse en pantalla pero no afectan el gameplay.
 * Created by jose on 25/01/16.
 */
public abstract class DecorationElement implements GameElement {

    public static void initializeGrid(Grid grid, int elements, TextureDrawer.TextureData tdata){
        for(int i = 0; i < elements; i++)
            grid.addElement(new DecorationElement(tdata, new Square(0,0,0,0), 0) {

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
}
