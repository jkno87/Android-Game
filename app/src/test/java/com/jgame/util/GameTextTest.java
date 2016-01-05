package com.jgame.util;


import com.jgame.elements.GameElement;
import com.jgame.util.TextureDrawer.TextureData;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
/**
 * Created by jose on 5/01/16.
 */
public class GameTextTest {

    @Test
    public void alphabetTest(){
        TextureData a = GameText.LETTERS[0];
        assertEquals(0, a.v1, 0.0f);
        assertEquals(0, a.u1, 0.0f);
        assertEquals(0.0625f, a.v2, 0.0f);
        assertEquals(0.0625f, a.u2, 0.0f);
        TextureData z = GameText.LETTERS[27];
        assertEquals(0.6875f, z.v1, 0.0f);
        assertEquals(0.0625f, z.u1, 0.0f);
        assertEquals(0.75f, z.v2, 0.0f);
        assertEquals(0.125f, z.u2, 0.0f);
    }
}
