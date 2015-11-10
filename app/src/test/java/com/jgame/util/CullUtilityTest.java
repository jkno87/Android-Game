package com.jgame.util;

import com.jgame.elements.GameElement;
import com.jgame.util.CullUtility;
import com.jgame.util.CullUtility.Grid;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

/**
 * Created by jose on 12/10/15.
 */
public class CullUtilityTest {

    class GameElementTest implements GameElement {

        Circle bounds;
        int id;

        GameElementTest(float x, float y, float radius,int id){
            bounds = new Circle(x,y,radius);
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
            return bounds.position;
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
        public GeometricElement getBounds() {
            return bounds;
        }

        @Override
        public void update(List<GameElement> otherElements, float timeDifference) {

        }
    }

    @Test
    public void pointIndexTest(){
        //TODO: Implementar el constructor de CullUtility utilizando las dimensiones de la grid 10 x 10 y el size original
        Grid instance = new Grid(320, 480, 60, 60);
        assertEquals(40, instance.cells.size());
        assertEquals(64.0f, instance.gridSizeX, 0.01f);
        assertEquals(60f, instance.gridSizeY, 0.01f);
        assertEquals(0, instance.getSingleCell(10, 10));
        assertEquals(4, instance.getSingleCell(300, 10));
        assertEquals(5, instance.getSingleCell(10, 70));
        assertEquals(35, instance.getSingleCell(10, 470));
        assertEquals(-1, instance.getSingleCell(-10, 0));
        assertEquals(-1, instance.getSingleCell(0, -10));
        assertEquals(-1, instance.getSingleCell(0, 500));
        assertEquals(-1, instance.getSingleCell(400, 0));
    }

    @Test
    public void circleIndexTest(){
        Grid instance = new Grid(320, 480, 60, 60);
        assertArrayEquals(new int[]{0, -1, -1, -1}, instance.getCells(new Circle(25, 25, 10)));
        assertArrayEquals(new int[]{0, 1, -1, -1}, instance.getCells(new Circle(60, 25, 10)));
        assertArrayEquals(new int[]{0, -1, 5, -1}, instance.getCells(new Circle(25, 59, 10)));
        assertArrayEquals(new int[]{0, 1, 5, 6}, instance.getCells(new Circle(60, 59, 10)));
    }

}
