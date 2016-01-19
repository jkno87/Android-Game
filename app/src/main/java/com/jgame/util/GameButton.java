package com.jgame.util;

/**
 * Created by ej-jose on 5/10/15.
 */
public class GameButton {
    public final Square bounds;
    public final GameText label;

    public GameButton(Square bounds, String label){
        this.bounds = bounds;
        this.label = new GameText(label, bounds.position.x + bounds.drawLengthX, bounds.position.y + bounds.drawLengthY, 10);
    }
}
