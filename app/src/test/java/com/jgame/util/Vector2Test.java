package com.jgame.util;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Created by jose on 3/03/16.
 */
public class Vector2Test {

    @Test
    public void testChangeBase(){
        Vector2 v = new Vector2(2,1);
        v.changeBase(new Vector2(0,-1));
        assertEquals(1.0f, v.x, 0.0f);
        assertEquals(-2.0f, v.y, 0.0f);
        v = new Vector2(2,1);
        v.changeBase(new Vector2(-1,0));
        assertEquals(-2.0f, v.x, 0.0f);
        assertEquals(-1.0f, v.y, 0.0f);
    }
}
