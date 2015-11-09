package com.jgame.util;

import com.jgame.elements.GameElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose on 4/10/15.
 */
public class CullUtility {

    public static class Grid {
        private static final int INITIAL_CELL_SIZE = 10;
        public ArrayList<List<GameElement>> cells;
        private final int gridColumns;
        private final int gridRows;
        public final float gridSizeX;
        public final float gridSizeY;

        public Grid(float lenX, float lenY, float minX, float minY){
            gridColumns = (int)(lenX / minX);
            gridRows = (int) (lenY / minY);
            gridSizeX = minX + (lenX % minX) / gridColumns;
            gridSizeY = minY + (lenY % minY) / gridRows;

            int totalCells = gridColumns * gridRows;

            cells = new ArrayList<>(totalCells);
            for(int i = 0; i < totalCells; i++)
                cells.add(new ArrayList<GameElement>(INITIAL_CELL_SIZE));
        }

        /**
         * Limpia todas las celdas del grid de cualquier elemento que contengan.
         */
        public void clear(){
            for(int i = 0; i < cells.size(); i++)
                cells.get(i).clear();
        }

        public void addElement(GameElement e){

        }

        public int[] getCells(Circle c){


            return null;
        }

        /**
         * Regresa el indice de la celda en la que se encuentra el punto x,y
         * @param x coordenada X donde se encuentra el punto
         * @param y coordenada Y donde se encuentra el punto
         * @return Indice de la celda en la que se encuentra el punto x,y
         */
        public int getSingleCell(float x, float y){
            System.out.println(gridSizeY);
            System.out.println(y / gridSizeY);
            return (int)(x / gridSizeX) + ((int)(y / gridSizeY)) * gridColumns;
        }
    }

}
