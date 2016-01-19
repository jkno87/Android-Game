package com.jgame.util;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * Created by jose on 19/01/16.
 */
public class SquareTest {

    @Test
    public void testCollides(){
        //expected to collide
        Square instance = new Square(new Vector2(10,10), 5, 5, 0);
        Square s1 = new Square(new Vector2(13, 13), 2.5f, 2.5f,0);
        Square s2 = new Square(new Vector2(13, 12), 2.5f, 2.5f,0);
        Square s3 = new Square(new Vector2(8, 8), 2.5f, 2.5f,0);
        Square s4 = new Square(new Vector2(8, 13), 2.5f, 2.5f,0);

        assertTrue(instance.collides(s1));
        assertTrue(instance.collides(s2));
        assertTrue(instance.collides(s3));
        assertTrue(instance.collides(s4));

        //expected to not collide
        s1 = new Square(new Vector2(16, 16), 2.5f, 2.5f,0);
        s2 = new Square(new Vector2(18, 10), 2.5f, 2.5f,0);
        s3 = new Square(new Vector2(0, 0), 2.5f, 2.5f,0);
        s4 = new Square(new Vector2(0, 20), 2.5f, 2.5f,0);

        assertTrue(!instance.collides(s1));
        assertTrue(!instance.collides(s2));
        assertTrue(!instance.collides(s3));
        assertTrue(!instance.collides(s4));

        //test with a bigger square
        s1 = new Square(new Vector2(0,0),20,20,0);
        assertTrue(instance.collides(s1));
    }

    @Test
    public void testContains(){
        Square instance = new Square(new Vector2(10,10), 5, 5,0);

        assertTrue(instance.contains(11, 12));
        assertTrue(instance.contains(13, 13));
        assertTrue(instance.contains(14, 14.7f));

        assertTrue(!instance.contains(0, 0));
        assertTrue(!instance.contains(16, 16));
        assertTrue(!instance.contains(16, 10));
        assertTrue(!instance.contains(18, 10));

    }
}
