package com.jgame.elements;

import com.jgame.game.GameFlow;
import com.jgame.util.SimpleDrawer;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

/**
 * Objeto que sirve para representar los estados de un movimiento.
 * Created by jose on 26/04/16.
 */
public class AttackData {

    public enum CollisionState {
        STARTUP, ACTIVE, RECOVERY, FINISHED
    }

    public final CollisionObject [] startup;
    public final CollisionObject [] active;
    public final CollisionObject [] recovery;
    public TimeCounter startupCounter;
    public TimeCounter activeCounter;
    public TimeCounter recoveryCounter;
    public CollisionState currentState;

    public AttackData(CollisionObject[] startup, CollisionObject[] active, CollisionObject [] recovery){
        currentState = CollisionState.STARTUP;
        this.startup = startup;
        this.active = active;
        this.recovery = recovery;
    }

    public AttackData(float startupTime, float activeTime, float recoveryTime,
                      CollisionObject[] startup, CollisionObject[] active, CollisionObject [] recovery){
        startupCounter = new TimeCounter(startupTime);
        activeCounter = new TimeCounter(activeTime);
        recoveryCounter = new TimeCounter(recoveryTime);
        currentState = CollisionState.STARTUP;
        this.startup = startup;
        this.active = active;
        this.recovery = recovery;
    }

    public void reset(){
        startupCounter.reset();
        activeCounter.reset();
        recoveryCounter.reset();
        currentState = CollisionState.STARTUP;
        for(CollisionObject o : startup)
            o.updatePosition();
        for(CollisionObject o : active)
            o.updatePosition();
        for(CollisionObject o : recovery)
            o.updatePosition();
    }

    public boolean completed(){
        return currentState == CollisionState.FINISHED;
    }

    public void update(GameFlow.UpdateInterval timeDifference){
        if(currentState == CollisionState.STARTUP){
            startupCounter.accum(timeDifference);
            if(startupCounter.completed())
                currentState = CollisionState.ACTIVE;
        } else if (currentState == CollisionState.ACTIVE){
            activeCounter.accum(timeDifference);
            if(activeCounter.completed())
                currentState = CollisionState.RECOVERY;
        } else if (currentState == CollisionState.RECOVERY){
            recoveryCounter.accum(timeDifference);
            if(recoveryCounter.completed())
                currentState = CollisionState.FINISHED;
        }
    }
}
