package com.jgame.elements;

import com.jgame.util.SimpleDrawer;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

/**
 * Objeto que sirve para representar los estados de un movimiento.
 * Created by jose on 26/04/16.
 */
public class CollisionState {

    enum CollisionSt {
        STARTUP, ACTIVE, RECOVERY, FINISHED
    }

    public CollisionObject [] startup;
    public CollisionObject [] active;
    public CollisionObject [] recovery;
    private final TimeCounter startupCounter;
    private final TimeCounter activeCounter;
    private final TimeCounter recoveryCounter;
    public CollisionSt currentState;

    public CollisionState(float startup, float active, float recovery){
        startupCounter = new TimeCounter(startup);
        activeCounter = new TimeCounter(active);
        recoveryCounter = new TimeCounter(recovery);
        currentState = CollisionSt.STARTUP;
    }

    public void setStartupBoxes(CollisionObject [] startup){
        this.startup = startup;
    }

    public void setActiveBoxes(CollisionObject [] active){
        this.active = active;
    }

    public void setRecoveryBoxes(CollisionObject [] recovery){
        this.recovery = recovery;
    }

    public void reset(){
        startupCounter.reset();
        activeCounter.reset();
        recoveryCounter.reset();
        currentState = CollisionSt.STARTUP;
        for(CollisionObject o : startup)
            o.updatePosition();
        for(CollisionObject o : active)
            o.updatePosition();
        for(CollisionObject o : recovery)
            o.updatePosition();
    }

    public boolean completed(){
        return currentState == CollisionSt.FINISHED;
    }

    public void update(float timeDifference){
        if(currentState == CollisionSt.STARTUP){
            startupCounter.accum(timeDifference);
            for(CollisionObject o : startup)
                o.update(null, timeDifference);
            if(startupCounter.completed())
                currentState = CollisionSt.ACTIVE;
        } else if (currentState == CollisionSt.ACTIVE){
            activeCounter.accum(timeDifference);
            for(CollisionObject o : active)
                o.update(null, timeDifference);
            if(activeCounter.completed())
                currentState = CollisionSt.RECOVERY;
        } else if (currentState == CollisionSt.RECOVERY){
            recoveryCounter.accum(timeDifference);
            for(CollisionObject o : recovery)
                o.update(null, timeDifference);
            if(recoveryCounter.completed())
                currentState = CollisionSt.FINISHED;
        }
    }
}
