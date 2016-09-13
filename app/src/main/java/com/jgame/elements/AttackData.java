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


    public static final int STARTUP_TIMER = 0;
    public static final int ACTIVE_TIMER = 1;
    public static final int RECOVERY_TIMER = 2;
    public final CollisionObject [] startup;
    public final CollisionObject [] active;
    public final CollisionObject [] recovery;
    public TimeCounter[] attackDuration = new TimeCounter[3];
    public CollisionState currentState;

    public AttackData(CollisionObject[] startup, CollisionObject[] active, CollisionObject [] recovery){
        currentState = CollisionState.STARTUP;
        this.startup = startup;
        this.active = active;
        this.recovery = recovery;
    }

    public AttackData(float startupTime, float activeTime, float recoveryTime,
                      CollisionObject[] startup, CollisionObject[] active, CollisionObject [] recovery){
        attackDuration[STARTUP_TIMER] = new TimeCounter(startupTime);
        attackDuration[ACTIVE_TIMER] = new TimeCounter(activeTime);
        attackDuration[RECOVERY_TIMER] = new TimeCounter(recoveryTime);

        currentState = CollisionState.STARTUP;
        this.startup = startup;
        this.active = active;
        this.recovery = recovery;
    }

    public void reset(){
        attackDuration[STARTUP_TIMER].reset();
        attackDuration[ACTIVE_TIMER].reset();
        attackDuration[RECOVERY_TIMER].reset();
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
            attackDuration[STARTUP_TIMER].accum(timeDifference);
            if(attackDuration[STARTUP_TIMER].completed())
                currentState = CollisionState.ACTIVE;
        } else if (currentState == CollisionState.ACTIVE){
            attackDuration[ACTIVE_TIMER].accum(timeDifference);
            if(attackDuration[ACTIVE_TIMER].completed())
                currentState = CollisionState.RECOVERY;
        } else if (currentState == CollisionState.RECOVERY){
            attackDuration[RECOVERY_TIMER].accum(timeDifference);
            if(attackDuration[RECOVERY_TIMER].completed())
                currentState = CollisionState.FINISHED;
        }
    }
}
