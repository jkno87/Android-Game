package com.jgame.elements;

import com.jgame.game.GameFlow;
import com.jgame.util.TextureDrawer.TextureData;
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
    public AnimationData[] animationInfo = new AnimationData[3];
    public CollisionState currentState;

    public AttackData(CollisionObject[] startup, CollisionObject[] active, CollisionObject [] recovery) {
        currentState = CollisionState.STARTUP;
        this.startup = startup;
        this.active = active;
        this.recovery = recovery;
    }

    public void setStartupAnimation(AnimationData data){
        animationInfo[STARTUP_TIMER] = data;
    }

    public void setActiveAnimation(AnimationData data){
        animationInfo[ACTIVE_TIMER] = data;
    }

    public void setRecoveryAnimation(AnimationData data){
        animationInfo[RECOVERY_TIMER] = data;
    }

    /**
     * Actualiza la informacion de las animaciones para que tengan una duracion diferente
     * @param frameDataSet nueva informacion de frames que se utilizara
     */
    public void updateFrameData(int[] frameDataSet){
        animationInfo[STARTUP_TIMER].updateFrameData(frameDataSet[0]);
        animationInfo[ACTIVE_TIMER].updateFrameData(frameDataSet[1]);
        animationInfo[RECOVERY_TIMER].updateFrameData(frameDataSet[2]);
    }

    public void reset(){
        animationInfo[STARTUP_TIMER].reset();
        animationInfo[ACTIVE_TIMER].reset();
        animationInfo[RECOVERY_TIMER].reset();
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

    public void update(){
        if(currentState == CollisionState.STARTUP){
            animationInfo[STARTUP_TIMER].updateFrame();
            if(animationInfo[STARTUP_TIMER].completed())
                currentState = CollisionState.ACTIVE;
        } else if (currentState == CollisionState.ACTIVE){
            animationInfo[ACTIVE_TIMER].updateFrame();
            if(animationInfo[ACTIVE_TIMER].completed())
                currentState = CollisionState.RECOVERY;
        } else if (currentState == CollisionState.RECOVERY){
            animationInfo[RECOVERY_TIMER].updateFrame();
            if(animationInfo[RECOVERY_TIMER].completed())
                currentState = CollisionState.FINISHED;
        }
    }

    public AnimationData getCurrentAnimation(){
        if(currentState == CollisionState.STARTUP)
            return animationInfo[STARTUP_TIMER];
        else if(currentState == CollisionState.ACTIVE)
            return animationInfo[ACTIVE_TIMER];
        else
            return animationInfo[RECOVERY_TIMER];
    }

}
