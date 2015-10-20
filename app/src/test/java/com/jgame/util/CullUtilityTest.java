package com.jgame.util;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * Created by jose on 12/10/15.
 */
public class CullUtilityTest {

    @Test
    public void simpleTest(){
        //TODO: Implementar el constructor de CullUtility utilizando las dimensiones de la grid 10 x 10 y el size original
        CullUtility testInstance = new CullUtility(10, 320f, 480f);
        System.out.println(testInstance);
        assertTrue(true);
    }
}
