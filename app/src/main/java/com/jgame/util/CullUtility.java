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
        private static final int EMPTY_CELL_INDEX = -1;
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

        /**
         * Regresa los indices que puede ocupar un circulo en la grid.
         * Asumiendo que el elemento mas grande no excede las dimensiones de las celdas,
         * un circulo puede ocupar maximo 4 celdas de la grid.
         * @param c circulo del cual se obtendran los indices
         * @return int[] que contiene los indices a los que pertenece el circulo
         */
        public int[] getCells(Circle c){
            //Utiliza el mismo calculo que getSingleCell. Usaria una funcion para evitar esto, pero asi mantenemos mas contento al GC
            int column = (int)(c.position.x / gridSizeX);
            int row = ((int)(c.position.y / gridSizeY));
            int[] indices = new int[4];
            indices[0] = column + row * gridColumns;

            if(column > 0 && c.intersectsX(column * gridSizeX))
                indices[1] = column - 1 + row * gridColumns;
            else if(column + 1 < gridColumns && c.intersectsX((column + 1) * gridSizeX)) //Se checa si contiene a la frontera superior
                indices[1] = column + 1 + row * gridColumns;
            else
                indices[1] = EMPTY_CELL_INDEX;

            if(row > 0 && c.intersectsX(row * gridSizeY))
                indices[2] = column + (row - 1) * gridColumns;
            else if(column + 1 < gridRows && c.intersectsX((row + 1) * gridSizeY)) //Se checa si contiene a la frontera superior
                indices[2] = column + (row + 1) * gridColumns;
            else
                indices[2] = EMPTY_CELL_INDEX;

            if(indices[1] != EMPTY_CELL_INDEX && indices[2] != EMPTY_CELL_INDEX){
                if(indices[1] > indices[0]){
                    if(indices[2] > indices[0])
                        indices[3] =
                }
            }


            return null;
        }

        /**
         * Regresa el indice de la celda en la que se encuentra el punto x,y
         * @param x coordenada X donde se encuentra el punto
         * @param y coordenada Y donde se encuentra el punto
         * @return Indice de la celda en la que se encuentra el punto x,y
         */
        public int getSingleCell(float x, float y){
            return (int)(x / gridSizeX) + ((int)(y / gridSizeY)) * gridColumns;
        }
    }

}
