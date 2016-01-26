package com.jgame.elements;

import org.junit.Test;
import com.jgame.elements.Player;
import com.jgame.util.Vector2;
import static org.junit.Assert.assertEquals;

/**
 * Created by jose on 26/01/16.
 */
public class PlayerTest {

    @Test
    public void testChangeDirection(){
        Player instance = new Player(new Vector2(50,50), 10);
        instance.changeDirection(45,45);
        assertEquals((int)instance.direction.x, -1);
        assertEquals((int)instance.direction.y, -1);
        instance.changeDirection(45,55);
        assertEquals((int)instance.direction.x, -1);
        assertEquals((int)instance.direction.y, 0);
        instance.changeDirection(45,65);
        assertEquals((int)instance.direction.x, -1);
        assertEquals((int)instance.direction.y, 1);
        instance.changeDirection(55,45);
        assertEquals((int)instance.direction.x, 0);
        assertEquals((int)instance.direction.y, -1);
        instance.changeDirection(55,55);
        assertEquals((int)instance.direction.x, 0);
        assertEquals((int)instance.direction.y, 0);
        instance.changeDirection(55,65);
        assertEquals((int)instance.direction.x, 0);
        assertEquals((int)instance.direction.y, 1);
        instance.changeDirection(65,45);
        assertEquals((int)instance.direction.x, 1);
        assertEquals((int)instance.direction.y, -1);
        instance.changeDirection(65,55);
        assertEquals((int)instance.direction.x, 1);
        assertEquals((int)instance.direction.y, 0);
        instance.changeDirection(65,65);
        assertEquals((int)instance.direction.x, 1);
        assertEquals((int)instance.direction.y, 1);
    }
}
