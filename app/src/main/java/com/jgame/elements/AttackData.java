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
    private final TimeCounter startupCounter;
    private final TimeCounter activeCounter;
    private final TimeCounter recoveryCounter;
    public CollisionState currentState;

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

    /*public synchronized void fillDrawer(SimpleDrawer d, Vector2 origin, Vector2 baseX){
        if(currentState == CollisionState.STARTUP)
            for(CollisionObject o : startup)
                d.addSquare(o.bounds, MainCharacter.INPUT_A_COLOR, origin, baseX);
        else if(currentState == CollisionState.ACTIVE)
            for(CollisionObject o : active)
                d.addSquare(o.bounds, MainCharacter.INPUT_A_COLOR, origin, baseX);
        else if(currentState == CollisionState.RECOVERY)
            for(CollisionObject o : recovery)
                d.addSquare(o.bounds, MainCharacter.INPUT_A_COLOR, origin, baseX);
    }*/

    public void update(GameFlow.UpdateInterval timeDifference){
        if(currentState == CollisionState.STARTUP){
            startupCounter.accum(timeDifference);
            for(CollisionObject o : startup)
                o.update(null, timeDifference);
            if(startupCounter.completed())
                currentState = CollisionState.ACTIVE;
        } else if (currentState == CollisionState.ACTIVE){
            activeCounter.accum(timeDifference);
            for(CollisionObject o : active)
                o.update(null, timeDifference);
            if(activeCounter.completed())
                currentState = CollisionState.RECOVERY;
        } else if (currentState == CollisionState.RECOVERY){
            recoveryCounter.accum(timeDifference);
            for(CollisionObject o : recovery)
                o.update(null, timeDifference);
            if(recoveryCounter.completed())
                currentState = CollisionState.FINISHED;
        }
    }
}
