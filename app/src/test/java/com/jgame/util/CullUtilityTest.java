package com.jgame.util;

import com.jgame.elements.GameElement;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.assertEquals;

/**
 * Created by jose on 12/10/15.
 */
public class CullUtilityTest {

    class GameElementTest implements GameElement {

        Vector2 position;
        int id;

        GameElementTest(float x, float y, int id){
            position = new Vector2(x,y);
            this.id = id;
        }

        @Override
        public boolean vivo() {
            return false;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public Vector2 getPosition() {
            return position;
        }

        @Override
        public float getPctAlive() {
            return 0;
        }

        @Override
        public void interact(GameElement other) {

        }

        @Override
        public float getSize() {
            return 0;
        }

        @Override
        public void update(List<GameElement> otherElements, float timeDifference) {

        }
    }

    @Test
    public void simpleTest(){
        //TODO: Implementar el constructor de CullUtility utilizando las dimensiones de la grid 10 x 10 y el size original
        CullUtility testInstance = new CullUtility(40, 320f, 480f);
        testInstance.addElement(new GameElementTest(275, 300, 0));
        testInstance.addElement(new GameElementTest(268, 301, 1));
        System.out.println(testInstance);
        assertEquals(2, testInstance.getSize());
        List<GameElement> neighbors = testInstance.getNeighbors(275, 300);
        assertEquals(2, neighbors.size());
        assertEquals(0, neighbors.get(0).getId());
        assertEquals(1, neighbors.get(1).getId());
    }
}
