package com.jgame.elements;

import com.jgame.util.SimpleDrawer;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

/**
 * Objeto que sirve para representar los estados de un movimiento.
 * Created by jose on 26/04/16.
 */
public class CollisionState {

    enum State {
        STARTUP, ACTIVE, RECOVERY, FINISHED
    }

    public CollisionObject [] startup;
    public CollisionObject [] active;
    public CollisionObject [] recovery;
    private final TimeCounter startupCounter;
    private final TimeCounter activeCounter;
    private final TimeCounter recoveryCounter;
    public State currentState;

    public CollisionState(float startup, float active, float recovery){
        startupCounter = new TimeCounter(startup);
        activeCounter = new TimeCounter(active);
        recoveryCounter = new TimeCounter(recovery);
        currentState = State.STARTUP;
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
        currentState = State.STARTUP;
        for(CollisionObject o : startup)
            o.updatePosition();
        for(CollisionObject o : active)
            o.updatePosition();
        for(CollisionObject o : recovery)
            o.updatePosition();
    }

    public boolean completed(){
        return currentState == State.FINISHED;
    }

    public void update(float timeDifference){
        if(currentState == State.STARTUP){
            startupCounter.accum(timeDifference);
            for(CollisionObject o : startup)
                o.update(null, timeDifference);
            if(startupCounter.completed())
                currentState = State.ACTIVE;
        } else if (currentState == State.ACTIVE){
            activeCounter.accum(timeDifference);
            for(CollisionObject o : active)
                o.update(null, timeDifference);
            if(activeCounter.completed())
                currentState = State.RECOVERY;
        } else if (currentState == State.RECOVERY){
            recoveryCounter.accum(timeDifference);
            for(CollisionObject o : recovery)
                o.update(null, timeDifference);
            if(recoveryCounter.completed())
                currentState = State.FINISHED;
        }
    }

    public void fillDrawer(SimpleDrawer d, Vector2 origin){
        if(currentState == State.STARTUP)
            for(CollisionObject o : startup)
                o.bounds.fillSimpleDrawer(d, MainCharacter.INPUT_A_COLOR, origin);
        else if(currentState == State.ACTIVE)
            for(CollisionObject o : active)
                o.bounds.fillSimpleDrawer(d, MainCharacter.INPUT_A_COLOR, origin);
        else if(currentState == State.RECOVERY)
            for(CollisionObject o : recovery)
                o.bounds.fillSimpleDrawer(d, MainCharacter.INPUT_A_COLOR, origin);
    }

}
