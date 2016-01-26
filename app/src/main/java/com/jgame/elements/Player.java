package com.jgame.elements;

import com.jgame.util.GeometricElement;
import com.jgame.util.SimpleDrawer;
import com.jgame.util.Square;
import com.jgame.util.Vector2;
import com.jgame.util.SimpleDrawer.ColorData;
import java.util.List;

/**
 * Created by jose on 24/01/16.
 */
public class Player implements GameElement {

    public enum PlayerState {
        STOPPED, INPUT_SELECTION, MOVING
    }

    public static final ColorData REGULAR_COLOR = new ColorData(0.75f,0.5f,1,1);
    public static final ColorData SELECTED_COLOR = new ColorData(1,0.9f,1,0.85f);
    public static final ColorData INPUT_COLOR = new ColorData(1,0,0,0.75f);

    public final Vector2 direction;
    public final Square inputArea;
    private Square bounds;
    public PlayerState state;

    public Player(Vector2 position, float length) {
        bounds = new Square(position, length, length, 0);
        direction = new Vector2();
        state = PlayerState.STOPPED;
        inputArea = new Square(new Vector2(position).sub(length,length), length*3, length*3,0);
    }

    public void setStateInputSelection(){
        this.state = PlayerState.INPUT_SELECTION;
        direction.x = 0;
        direction.y = 0;
    }

    public void setStoppedState(){
        this.state = PlayerState.STOPPED;
        direction.x = 0;
        direction.y = 0;
    }

    public void changeDirection(float x, float y){
        direction.x = ((int)((x - inputArea.position.x) / bounds.lenX)) - 1;
        direction.y = ((int)((y - inputArea.position.y) / bounds.lenY)) - 1;
        this.state = PlayerState.MOVING;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public GeometricElement getBounds() {
        return bounds;
    }

    @Override
    public void update(List<GameElement> others, float timeDifference) {
        bounds.position.add(direction);
        inputArea.position.add(direction);
    }

    @Override
    public boolean alive() {
        return true;
    }
}
