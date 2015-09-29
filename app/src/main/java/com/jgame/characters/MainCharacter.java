package com.jgame.characters;

import com.jgame.elements.Enemy;
import com.jgame.elements.GameElement;
import com.jgame.elements.Projectile;
import com.jgame.util.Vector2;

import java.util.List;

/**
 * Created by jose on 10/02/15.
 */
public class MainCharacter implements GameElement {

    public enum CharacterState {
        NORMAL, STUNNED, SPECIAL
    }

    private boolean dragging;
    public CharacterState state;
    public final int stamina;
    private final Attack mainAttack;
    private MovementController movementController;

    public MainCharacter(MovementController movementController, int stamina, Attack mainAttack){
        this.stamina = stamina;
        state = CharacterState.NORMAL;
        this.mainAttack = mainAttack;
        this.movementController = movementController;
    }

    private void recoverCharacter(){
        state = CharacterState.NORMAL;
    }

    public void receiveInputDown(float sourceX, float sourceY){
        if(state == CharacterState.STUNNED)
            return;

        boolean withinCharacterRadius = movementController.containsPoint(sourceX, sourceY);
        dragging = withinCharacterRadius;
        movementController.updateDirection(sourceX, sourceY);

    }

    public void receiveInputDrag(float sourceX, float sourceY){
        movementController.updateDirection(sourceX, sourceY);

        if(dragging){
            movementController.move(sourceX, sourceY);
        }
    }

    public void changeState(){
        if(state == CharacterState.NORMAL)
            this.state = CharacterState.SPECIAL;
        else if(state == CharacterState.SPECIAL)
            this.state = CharacterState.NORMAL;
    }

    public Projectile receiveInputUp(float sourceX, float sourceY){
        Projectile created = null;

        if(!dragging && state != CharacterState.STUNNED) {
            if(state == CharacterState.SPECIAL)
                created = mainAttack.createSpecialAttack(sourceX, sourceY);
            else
                created = mainAttack.createAttack(sourceX, sourceY, movementController.position);

            movementController.updateDirection(sourceX, sourceY);
        }

        dragging = false;
        //charging = false;
        //specialAttackCounter.reset();



        return created;
    }

    @Override
    public void interact(GameElement e){

    }

    @Override
    public float getSize(){
        return 0;
    }

    @Override
    public void update(List<GameElement> others, float timeDifference){

    }

    @Override
    public boolean vivo() {
        return false;
    }

    /**
     * Regresa un vector con la posicion del maincharacter
     * @return Vector2 con la posicion actual del personaje
     */
    public Vector2 getPosition(){
        return new Vector2(movementController.position);
    }

    /**
     * Regresa el angulo del personaje principal
     * @return float con el angulo de direccion
     */
    public float getAngle(){
        return movementController.angle;
    }

    public float getPctAlive(){
        return 0;
    }
}
