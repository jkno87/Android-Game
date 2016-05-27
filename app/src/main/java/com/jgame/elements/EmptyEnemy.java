package com.jgame.elements;

import com.jgame.game.FightingGameFlow;
import com.jgame.game.GameFlow;
import com.jgame.util.TextureDrawer;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

/**
 * Enemigo que sirve nada mas para representar el intervalo de tiempo que existe en el juego cuando se elimina al enemigo principal.
 * Tambien se usa para evitar estar usando el horror de null
 * Created by jose on 24/05/16.
 */
public class EmptyEnemy extends Enemy {

    private final TimeCounter timeToLive;

    public EmptyEnemy(int id, float time) {
        super(0, 0, new Vector2(), id);
        currentState = CharacterState.IDLE;
        this.timeToLive = new TimeCounter(time);
    }

    @Override
    public void reset(){
        timeToLive.reset();
        currentState = CharacterState.IDLE;
    }

    @Override
    public void update(Character foe, GameFlow.UpdateInterval interval, FightingGameFlow.WorldData wData) {
        timeToLive.accum(interval);
        if(timeToLive.completed())
            currentState = CharacterState.DEAD;
    }
}
