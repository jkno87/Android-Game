package com.jgame.elements;

import com.jgame.util.GeometricElement;
import com.jgame.util.Square;
import com.jgame.util.TextureDrawer;

/**
 * Este objeto representa Elementos del juego que solo se utilizaran para dibujarse en pantalla pero no afectan el gameplay.
 * Created by jose on 25/01/16.
 */
public abstract class DecorationElement implements GameElement {

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
