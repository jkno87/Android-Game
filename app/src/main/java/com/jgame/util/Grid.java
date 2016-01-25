package com.jgame.util;

import com.jgame.elements.GameElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose on 4/10/15.
 */
public class Grid {

    private static final int INITIAL_CELL_SIZE = 10;
    private static final int INITIAL_NEIGHBORS_SIZE = 15;
    private static final int EMPTY_CELL_INDEX = -1;
    private static final int CENTER_INDEX = 0;
    private static final int VERTICAL_NEIGHBOR = 2;
    private static final int HORIZONTAL_NEIGHBOR = 1;
    private static final int DIAGONAL_NEIGHBOR = 3;
    public ArrayList<List<GameElement>> cells;
    private final int gridColumns;
    private final int gridRows;
    private final float lenX;
    private final float lenY;
    public final float gridSizeX;
    public final float gridSizeY;
    private ArrayList<GameElement> neighbors;

    public Grid(float lenX, float lenY, float minX, float minY){
        this.lenX = lenX;
        this.lenY = lenY;
        gridColumns = (int)(lenX / minX);
        gridRows = (int) (lenY / minY);
        gridSizeX = minX + (lenX % minX) / gridColumns;
        gridSizeY = minY + (lenY % minY) / gridRows;

        int totalCells = gridColumns * gridRows;
        neighbors = new ArrayList<GameElement>(INITIAL_NEIGHBORS_SIZE);

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

    /**
     * Agrega un GameElement a la grid de elementos del mapa.
     * @param e GameElement que se agregara.
     */
    public void addElement(GameElement e){
        int[] indices = getElementIndexes(e.getBounds());
        for(int i = 0; i < indices.length; i++){
            if(indices[i] != EMPTY_CELL_INDEX)
                cells.get(indices[i]).add(e);
        }
    }

    /**
     * Regresa una lista con los elementos que se encuentran en las mismas celdas que ocupa
     * el GameElement e
     * @param e GameElement que buscara vecinos en el grid
     * @return List con los elementos que se encuentran en las mismas celdas.
     */
    public List<GameElement> getNeighbors(GameElement e){
        neighbors.clear();
        int[] indices = getElementIndexes(e.getBounds());
        for(int i = 0; i < indices.length; i++){
            if(indices[i] != EMPTY_CELL_INDEX)
                for(GameElement n : cells.get(indices[i])){
                    if(e.getId() != n.getId() && !neighbors.contains(n))
                        neighbors.add(n);
                }
        }

        return neighbors;
    }


    /**
     * Genera los indices del GeomentricElement e que se agrega a la grid
     * @param e GeometricElement que se agregara al grid
     * @return int[] con los indices
     */
    private int[] getElementIndexes(GeometricElement e){
        if(e instanceof Circle)
            return getCells((Circle) e);
        else if(e instanceof Square)
            return getCellsSquare((Square) e);
        else
            throw new UnsupportedOperationException("Este GeomentricElement no ha sido implementado en CullUtility");
    }

    /**
     * Regresa los indices que puede ocupar un circulo en la grid.
     * Asumiendo que el elemento mas grande no excede las dimensiones de las celdas,
     * un circulo puede ocupar maximo 4 celdas de la grid.
     * @param s square del que se obtendran los indices
     * @return int[] que contiene los indices a los que pertenece el rectangulo
     */
    public int[] getCellsSquare(Square s){
        int[] indices = new int[4];
        int i1 = getSingleCell(s.position.x, s.position.y);
        int i2 = getSingleCell(s.position.x + s.lenX, s.position.y);
        int i3 = getSingleCell(s.position.x, s.position.y + s.lenY);
        int i4 = getSingleCell(s.position.x + s.lenX, s.position.y + s.lenY);

        //caso en el que se encuentra dentro de una celda
        if(i1 == i4){
            indices[CENTER_INDEX] = i1;
            indices[HORIZONTAL_NEIGHBOR] = EMPTY_CELL_INDEX;
            indices[VERTICAL_NEIGHBOR] = EMPTY_CELL_INDEX;
            indices[DIAGONAL_NEIGHBOR] = EMPTY_CELL_INDEX;
        } else if(i3 == i4){
            //caso en el que tiene un vecino vertical
            indices[CENTER_INDEX] = i1;
            indices[HORIZONTAL_NEIGHBOR] = EMPTY_CELL_INDEX;
            indices[VERTICAL_NEIGHBOR] = i3;
            indices[DIAGONAL_NEIGHBOR] = EMPTY_CELL_INDEX;
        } else if(i2 == i4){
            //caso en el que tiene un vecino horizontal
            indices[CENTER_INDEX] = i1;
            indices[HORIZONTAL_NEIGHBOR] = i2;
            indices[VERTICAL_NEIGHBOR] = EMPTY_CELL_INDEX;
            indices[DIAGONAL_NEIGHBOR] = EMPTY_CELL_INDEX;
        } else {
            indices[CENTER_INDEX] = i1;
            indices[HORIZONTAL_NEIGHBOR] = i2;
            indices[VERTICAL_NEIGHBOR] = i3;
            indices[DIAGONAL_NEIGHBOR] = i4;
        }


        return indices;
    }

    /**
     * Regresa los indices que puede ocupar un circulo en la grid.
     * Asumiendo que el elemento mas grande no excede las dimensiones de las celdas,
     * un circulo puede ocupar maximo 4 celdas de la grid.
     * @param c circulo del cual se obtendran los indices
     * @return int[] que contiene los indices a los que pertenece el circulo
     */
    public int[] getCells(Circle c){
        int[] indices = new int[4];
        indices[CENTER_INDEX] = getSingleCell(c.position.x, c.position.y);
        int i1 = getSingleCell(c.position.x + c.radius, c.position.y);
        int i2 = getSingleCell(c.position.x - c.radius, c.position.y);
        int i3 = getSingleCell(c.position.x, c.position.y + c.radius);
        int i4 = getSingleCell(c.position.x, c.position.y - c.radius);

        //Se busca si tiene un vecino lateral
        if(i1 > indices[CENTER_INDEX])
            indices[HORIZONTAL_NEIGHBOR] = i1;
        else if(i2 > EMPTY_CELL_INDEX && i2 < indices[CENTER_INDEX])
            indices[HORIZONTAL_NEIGHBOR] = i2;
        else
            indices[HORIZONTAL_NEIGHBOR] = EMPTY_CELL_INDEX;

        //Se busca si tiene un vecino vertical
        if(i3 > indices[CENTER_INDEX])
            indices[VERTICAL_NEIGHBOR] = i3;
        else if(i4 > EMPTY_CELL_INDEX && i4 < indices[CENTER_INDEX])
            indices[VERTICAL_NEIGHBOR] = i4;
        else
            indices[VERTICAL_NEIGHBOR] = EMPTY_CELL_INDEX;

        //Se checa si cae en el vecino diagonal (no es completamente preciso el calculo, por ahora esta bien)
        if(indices[HORIZONTAL_NEIGHBOR] != EMPTY_CELL_INDEX && indices[VERTICAL_NEIGHBOR] != EMPTY_CELL_INDEX){
            if(indices[HORIZONTAL_NEIGHBOR] > indices[CENTER_INDEX])
                indices[DIAGONAL_NEIGHBOR] = indices[VERTICAL_NEIGHBOR] + 1;
            else
                indices[DIAGONAL_NEIGHBOR] = indices[VERTICAL_NEIGHBOR] - 1;
        } else
            indices[DIAGONAL_NEIGHBOR] = -1;

        return indices;
    }

    /**
     * Regresa el indice de la celda en la que se encuentra el punto x,y
     * @param x coordenada X donde se encuentra el punto
     * @param y coordenada Y donde se encuentra el punto
     * @return Indice de la celda en la que se encuentra el punto x,y
     */
    public int getSingleCell(float x, float y){
        if(x < 0 || y < 0 || x > lenX || y > lenY)
            return EMPTY_CELL_INDEX;

        return (int)(x / gridSizeX) + ((int)(y / gridSizeY)) * gridColumns;
    }
}