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

    enum CollisionState {
        STARTUP, ACTIVE, RECOVERY, FINISHED
    }

    public CollisionObject [] startup;
    public CollisionObject [] active;
    public CollisionObject [] recovery;
    private final TimeCounter startupCounter;
    private final TimeCounter activeCounter;
    private final TimeCounter recoveryCounter;
    public CollisionState currentState;

    public AttackData(float startup, float active, float recovery){
        startupCounter = new TimeCounter(startup);
        activeCounter = new TimeCounter(active);
        recoveryCounter = new TimeCounter(recovery);
        currentState = CollisionState.STARTUP;
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
            startupCounter.accum(timeDifference.delta);
            for(CollisionObject o : startup)
                o.update(null, timeDifference);
            if(startupCounter.completed())
                currentState = CollisionState.ACTIVE;
        } else if (currentState == CollisionState.ACTIVE){
            activeCounter.accum(timeDifference.delta);
            for(CollisionObject o : active)
                o.update(null, timeDifference);
            if(activeCounter.completed())
                currentState = CollisionState.RECOVERY;
        } else if (currentState == CollisionState.RECOVERY){
            recoveryCounter.accum(timeDifference.delta);
            for(CollisionObject o : recovery)
                o.update(null, timeDifference);
            if(recoveryCounter.completed())
                currentState = CollisionState.FINISHED;
        }
    }
}
