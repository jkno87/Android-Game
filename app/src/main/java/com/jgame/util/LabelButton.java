package com.jgame.util;

/**
 * Created by ej-jose on 5/10/15.
 */
public class LabelButton {
    private final float MARGIN = 20f;
    public final Square bounds;
    public final GameText label;

    public LabelButton(Square bounds, String label){
        this.bounds = bounds;
        this.label = new GameText(label, bounds, MARGIN);
    }
}
