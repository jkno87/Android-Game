package com.jgame.util;

import com.jgame.definitions.GameLevels;
import com.jgame.elements.GameElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose on 4/10/15.
 */
public class Grid {

    private static final int INITIAL_CELL_SIZE = 25;
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
    private final ArrayList<GameElement> neighbors;
    private final int[] elementIndices;

    public Grid(float lenX, float lenY, float minX, float minY){
        this.lenX = lenX;
        this.lenY = lenY;
        gridColumns = (int)(lenX / minX);
        gridRows = (int) (lenY / minY);
        gridSizeX = minX + (lenX % minX) / gridColumns;
        gridSizeY = minY + (lenY % minY) / gridRows;
        elementIndices = new int[4];
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
        setElementIndexes(e.getBounds());
        for(int i = 0; i < elementIndices.length; i++){
            if(elementIndices[i] != EMPTY_CELL_INDEX)
                cells.get(elementIndices[i]).add(e);
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
        setElementIndexes(e.getBounds());
        for(int i = 0; i < elementIndices.length; i++){
            if(elementIndices[i] != EMPTY_CELL_INDEX)
                for(GameElement n : cells.get(elementIndices[i])){
                    if(e.getId() != n.getId() && !neighbors.contains(n))
                        neighbors.add(n);
                }
        }

        return neighbors;
    }

    /**
     * Regresa una lista con los elementos que se encuentran en el area del GeometricElement
     * @param e GameElement que buscara vecinos en el grid.
     * @param elements Lista a la que se agregaran los elementos
     * @return List con los elementos que se encuentran en las mismas celdas.
     */
    public void getElementsIn(GeometricElement e, List<GameElement> elements){
        setElementIndexes(e);
        for(int i = 0; i < elementIndices.length; i++){
            if(elementIndices[i] != EMPTY_CELL_INDEX)
                for(GameElement n : cells.get(elementIndices[i])){
                    if(!elements.contains(n))
                        elements.add(n);
                }
        }
    }

    /**
     * Quita el GameElement g de la coleccion de objetos.
     * @param g Elemento que se desea remover del Grid
     */
    public void remove(GameElement g){
        setElementIndexes(g.getBounds());
        for(int i = 0; i < elementIndices.length; i++)
            if(elementIndices[i] != EMPTY_CELL_INDEX)
                cells.get(elementIndices[i]).remove(g);
    }


    /**
     * Genera los indices del GeomentricElement e que se agrega a la grid. Se asignan
     * a la variable global indices para ahorrar memoria.
     * @param e GeometricElement que se agregara al grid
     */
    private void setElementIndexes(GeometricElement e){
        if(e instanceof Circle)
            setIndicesCircle((Circle) e);
        else if(e instanceof Square)
            setIndicesSquare((Square) e);
        else
            throw new UnsupportedOperationException("Este GeomentricElement no ha sido implementado en CullUtility");
    }

    /**
     * Asigna los indices del rectangulo en la variable global indices.
     * Asumiendo que el elemento mas grande no excede las dimensiones de las celdas,
     * un circulo puede ocupar maximo 4 celdas de la grid.
     * @param s square del que se obtendran los indices
     */
    public void setIndicesSquare(Square s){
        int i1 = getSingleCell(s.position.x, s.position.y);
        int i2 = getSingleCell(s.position.x + s.lenX, s.position.y);
        int i3 = getSingleCell(s.position.x, s.position.y + s.lenY);
        int i4 = getSingleCell(s.position.x + s.lenX, s.position.y + s.lenY);

        //caso en el que se encuentra dentro de una celda
        if(i1 == i4){
            elementIndices[CENTER_INDEX] = i1;
            elementIndices[HORIZONTAL_NEIGHBOR] = EMPTY_CELL_INDEX;
            elementIndices[VERTICAL_NEIGHBOR] = EMPTY_CELL_INDEX;
            elementIndices[DIAGONAL_NEIGHBOR] = EMPTY_CELL_INDEX;
        } else if(i3 == i4 && i3 != EMPTY_CELL_INDEX){
            //caso en el que tiene un vecino vertical
            elementIndices[CENTER_INDEX] = i1;
            elementIndices[HORIZONTAL_NEIGHBOR] = EMPTY_CELL_INDEX;
            elementIndices[VERTICAL_NEIGHBOR] = i3;
            elementIndices[DIAGONAL_NEIGHBOR] = EMPTY_CELL_INDEX;
        } else if(i2 == i4 && i2 != EMPTY_CELL_INDEX){
            //caso en el que tiene un vecino horizontal
            elementIndices[CENTER_INDEX] = i1;
            elementIndices[HORIZONTAL_NEIGHBOR] = i2;
            elementIndices[VERTICAL_NEIGHBOR] = EMPTY_CELL_INDEX;
            elementIndices[DIAGONAL_NEIGHBOR] = EMPTY_CELL_INDEX;
        } else {
            elementIndices[CENTER_INDEX] = i1;
            elementIndices[HORIZONTAL_NEIGHBOR] = i2;
            elementIndices[VERTICAL_NEIGHBOR] = i3;
            elementIndices[DIAGONAL_NEIGHBOR] = i4;
        }
    }

    /**
     * Asigna los indices del rectangulo en la variable global indices.
     * Asumiendo que el elemento mas grande no excede las dimensiones de las celdas,
     * un circulo puede ocupar maximo 4 celdas de la grid.
     * @param c circulo del cual se obtendran los indices
     */
    public void setIndicesCircle(Circle c){
        elementIndices[CENTER_INDEX] = getSingleCell(c.position.x, c.position.y);
        int i1 = getSingleCell(c.position.x + c.radius, c.position.y);
        int i2 = getSingleCell(c.position.x - c.radius, c.position.y);
        int i3 = getSingleCell(c.position.x, c.position.y + c.radius);
        int i4 = getSingleCell(c.position.x, c.position.y - c.radius);

        //Se busca si tiene un vecino lateral
        if(i1 > elementIndices[CENTER_INDEX])
            elementIndices[HORIZONTAL_NEIGHBOR] = i1;
        else if(i2 > EMPTY_CELL_INDEX && i2 < elementIndices[CENTER_INDEX])
            elementIndices[HORIZONTAL_NEIGHBOR] = i2;
        else
            elementIndices[HORIZONTAL_NEIGHBOR] = EMPTY_CELL_INDEX;

        //Se busca si tiene un vecino vertical
        if(i3 > elementIndices[CENTER_INDEX])
            elementIndices[VERTICAL_NEIGHBOR] = i3;
        else if(i4 > EMPTY_CELL_INDEX && i4 < elementIndices[CENTER_INDEX])
            elementIndices[VERTICAL_NEIGHBOR] = i4;
        else
            elementIndices[VERTICAL_NEIGHBOR] = EMPTY_CELL_INDEX;

        //Se checa si cae en el vecino diagonal (no es completamente preciso el calculo, por ahora esta bien)
        if(elementIndices[HORIZONTAL_NEIGHBOR] != EMPTY_CELL_INDEX && elementIndices[VERTICAL_NEIGHBOR] != EMPTY_CELL_INDEX){
            if(elementIndices[HORIZONTAL_NEIGHBOR] > elementIndices[CENTER_INDEX])
                elementIndices[DIAGONAL_NEIGHBOR] = elementIndices[VERTICAL_NEIGHBOR] + 1;
            else
                elementIndices[DIAGONAL_NEIGHBOR] = elementIndices[VERTICAL_NEIGHBOR] - 1;
        } else
            elementIndices[DIAGONAL_NEIGHBOR] = -1;
    }

    /**
     * Regresa el indice de la celda en la que se encuentra el punto x,y
     * @param x coordenada X donde se encuentra el punto
     * @param y coordenada Y donde se encuentra el punto
     * @return Indice de la celda en la que se encuentra el punto x,y
     */
    public int getSingleCell(float x, float y){
        if(x < 0 || y < 0 || x >= lenX || y >= lenY)
            return EMPTY_CELL_INDEX;

        return (int)(x / gridSizeX) + ((int)(y / gridSizeY)) * gridColumns;
    }
}