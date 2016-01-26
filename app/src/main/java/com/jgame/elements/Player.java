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
        NORMAL, INPUT_SELECTION
    }

    public static final ColorData REGULAR_COLOR = new ColorData(0.75f,0.5f,1,1);
    public static final ColorData SELECTED_COLOR = new ColorData(1,0.9f,1,0.85f);

    private final Vector2 direction;
    private Square bounds;
    private Square[] inputs;
    public PlayerState state;

    public Player(Vector2 position, float length) {
        bounds = new Square(position, length, length, 0);
        direction = new Vector2();
        state = PlayerState.NORMAL;
        inputs = new Square[9];

        float currInputX = position.x - length;
        float currInputY = position.y - length;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                inputs[i + j] = new Square(new Vector2(currInputX, currInputY), length, length, 0);
                currInputX += length;
            }
            currInputY += length;
        }
    }

    public void setStateInputSelection(){
        this.state = PlayerState.INPUT_SELECTION;
    }

    public void setNormalState(float x, float y){
        direction.set(x,y);
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

    }

    @Override
    public boolean alive() {
        return true;
    }
}
