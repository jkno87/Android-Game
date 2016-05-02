package com.jgame.elements;

import com.jgame.util.SimpleDrawer;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;
import java.util.List;

/**
 * Objeto que representa los enemigos que apareceran en el juego
 * Created by jose on 2/05/16.
 */
public class Enemy extends GameObject {

    enum State {
        IDLE, ATTACKING
    }

    private final Vector2 baseX = new Vector2(1,0);
    private final Vector2 baseY = new Vector2(0,1);
    public final CollisionObject idleCollisionBox;
    private final Vector2 positionOffset;
    private final MainCharacter mainCharacter;
    public AttackData activeAttack;
    public final AttackData[] attacks;
    public TimeCounter idleTimer;
    public State currentState;

    public Enemy(float sizeX, float sizeY, Vector2 position, int id, MainCharacter mainCharacter, int moves) {
        super(position, id);
        positionOffset = new Vector2(-sizeX/2,0);
        idleCollisionBox = new CollisionObject(new Vector2(positionOffset), id, sizeX, sizeY, this);
        this.mainCharacter = mainCharacter;
        idleTimer = new TimeCounter(1.5f);
        currentState = State.IDLE;
        attacks = new AttackData[moves];
    }

    public synchronized void fillDrawer(SimpleDrawer d, Vector2 origin){
        if(currentState == State.IDLE)
            d.addSquare(idleCollisionBox.bounds, MainCharacter.INPUT_A_COLOR, origin, baseX);
        else {
            activeAttack.fillDrawer(d, origin, baseX);
        }
    }

    public synchronized void changeDirection(){
        baseX.set(baseX.x * -1, 0);
    }

    @Override
    public void update(List<GameElement> objects, float timeDifference){
        if(currentState == State.IDLE) {
            idleTimer.accum(timeDifference);
            if (idleTimer.completed()) {
                currentState = State.ATTACKING;
                activeAttack = attacks[0];
                activeAttack.reset();
            }
        }

        if(currentState == State.ATTACKING) {
            activeAttack.update(timeDifference);
            if(activeAttack.completed()){
                currentState = State.IDLE;
                idleTimer.reset();
            }
        }
    }
}
