package com.jgame.elements;

import com.jgame.util.Square;
import com.jgame.util.TextureDrawer;
import com.jgame.util.Vector2;

import java.util.List;

/**
 * Created by jose on 23/02/16.
 */
public class Particle extends DecorationElement {

    private final Vector2 particleDirection;
    private float timeToLive;

    public Particle(TextureDrawer.TextureData tData, Square bounds, int id, Vector2 initialDirection, float timeToLive){
        super(tData, bounds, id);
        this.particleDirection = initialDirection;
        this.timeToLive = timeToLive;
    }


    @Override
    public void update(List<GameElement> others, float timeDifference) {
        bounds.position.add(particleDirection);
        //Se quita la gravedad, estan en el espacio :P
        //particleDirection.add(GRAVITY_MAGNITUDE);
        timeToLive -= timeDifference;
    }

    @Override
    public boolean alive() {
        return timeToLive > 0;
    }
}
