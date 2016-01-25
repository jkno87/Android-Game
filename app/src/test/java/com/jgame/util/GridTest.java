package com.jgame.util;

import com.jgame.elements.GameElement;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by jose on 12/10/15.
 */
public class GridTest {

    class GameElementTest implements GameElement {

        Circle bounds;
        int id;

        GameElementTest(float x, float y, float radius,int id){
            bounds = new Circle(x,y,radius);
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public GeometricElement getBounds() {
            return bounds;
        }

        @Override
        public void update(List<GameElement> otherElements, float timeDifference) {
        }

        @Override
        public boolean alive() {
            return false;
        }

        @Override
        public boolean equals(Object o){
            if(!(o instanceof GameElementTest))
                return false;

            GameElementTest gt = (GameElementTest)o;
            return gt.getId() == getId();
        }

        public int hashCode(){
            return getId();
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

        assertArrayEquals(new int[]{4, -1, -1, -1}, instance.getCells(new Circle(300, 25, 10)));
        assertArrayEquals(new int[]{4, 3, -1, -1}, instance.getCells(new Circle(260, 25, 10)));
        assertArrayEquals(new int[]{4, -1, 9, -1}, instance.getCells(new Circle(300, 59, 10)));
        assertArrayEquals(new int[]{4, 3, 9, 8}, instance.getCells(new Circle(260, 59, 10)));

        assertArrayEquals(new int[]{35, -1, -1, -1}, instance.getCells(new Circle(25, 460, 10)));
        assertArrayEquals(new int[]{35, 36, -1, -1}, instance.getCells(new Circle(60, 460, 10)));
        assertArrayEquals(new int[]{35, -1, 30, -1}, instance.getCells(new Circle(25, 425, 10)));
        assertArrayEquals(new int[]{35, 36, 30, 31}, instance.getCells(new Circle(60, 425, 10)));
    }

    @Test
    public void squareIndexTest(){
        Grid instance = new Grid(320, 480, 60, 60);
        assertArrayEquals(new int[]{0, -1, -1, -1}, instance.getCellsSquare(new Square(new Vector2(25, 25), 10.0f, 10.0f, 0.0f)));
        assertArrayEquals(new int[]{0, 1, -1, -1}, instance.getCellsSquare(new Square(new Vector2(55, 25), 10.0f, 10.0f, 0.0f)));
        assertArrayEquals(new int[]{0, -1, 5, -1}, instance.getCellsSquare(new Square(new Vector2(25, 55), 10.0f, 10.0f, 0.0f)));
        assertArrayEquals(new int[]{0, 1, 5, 6}, instance.getCellsSquare(new Square(new Vector2(55, 55), 10.0f, 10.0f, 0.0f)));

        assertArrayEquals(new int[]{-1, -1, -1, 0}, instance.getCellsSquare(new Square(new Vector2(-5, -5), 10.0f, 10.0f, 0.0f)));
        assertArrayEquals(new int[]{-1, -1, -1, -1}, instance.getCellsSquare(new Square(new Vector2(-25, -25), 10.0f, 10.0f, 0.0f)));
        assertArrayEquals(new int[]{-1, -1, -1, -1}, instance.getCellsSquare(new Square(new Vector2(-25, -5), 10.0f, 10.0f, 0.0f)));
        assertArrayEquals(new int[]{-1, -1, -1, -1}, instance.getCellsSquare(new Square(new Vector2(-5, -25), 10.0f, 10.0f, 0.0f)));
    }

    @Test
    public void neighborsTest(){
        int currentId = 0;
        GameElementTest instance1 = new GameElementTest(25, 25, 10, currentId++);
        Grid gridInstance = new Grid(320, 480, 60, 60);
        List<GameElement> results = gridInstance.getNeighbors(instance1);
        assertTrue(results.isEmpty());
        //Se agrega un elemento y debe seguir vacio el resultado porque el elemento no puede ser su propio vecino
        gridInstance.addElement(instance1);
        results = gridInstance.getNeighbors(instance1);
        assertTrue(results.isEmpty());
        //Se agrega otro elemento
        GameElementTest instance2 = new GameElementTest(50,50, 5, currentId++);
        gridInstance.addElement(instance2);
        results = gridInstance.getNeighbors(instance1);
        assertEquals(1, results.size());
        //Se agrega otro elemento que no deberia aparecer cuando se busquen vecinos
        GameElementTest instance3 = new GameElementTest(300, 300, 19, currentId++);
        gridInstance.addElement(instance3);
        results = gridInstance.getNeighbors(instance1);
        assertEquals(1, results.size());
        //Se checa que cuando se haga clear, los vecinos sean cero
        gridInstance.clear();
        results = gridInstance.getNeighbors(instance1);
        assertTrue(results.isEmpty());

    }

}
