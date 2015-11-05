package com.jgame.util;

import com.jgame.elements.GameElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose on 4/10/15.
 */
public class CullUtility {

    public static class Region {
        private float centerX;
        private float centerY;
        private Region upperLeft;
        private Region upperRight;
        private Region lowerLeft;
        private Region lowerRight;
        private boolean regionSet;
        private List<GameElement> elements;

        public Region(float centerX, float centerY){
            this.centerX = centerX;
            this.centerY = centerY;
            elements = new ArrayList<>();
        }

        public void setRegions(Region upperLeft, Region upperRight, Region lowerLeft, Region lowerRight){
            this.upperLeft = upperLeft;
            this.upperRight = upperRight;
            this.lowerLeft = lowerLeft;
            this.lowerRight = lowerRight;
            regionSet = true;
        }

        /**
         * Genera una lista con las coordenadas del centro de cada region
         * @return Lista con vectores que representan el centro de cada region
         */
        public List<Vector2> getCenters(){
            ArrayList<Vector2> result = new ArrayList<>();
            if(regionSet){
                result.addAll(upperLeft.getCenters());
                result.addAll(lowerLeft.getCenters());
                result.addAll(upperRight.getCenters());
                result.addAll(lowerRight.getCenters());
            } else
                result.add(new Vector2(centerX, centerY));

            return result;
        }

        /**
         * Agrega un GameElement a la region
         * @param e
         */
        public void addElement(GameElement e){
            if(!regionSet) {
                elements.add(e);
                return;
            }

            float x = e.getPosition().x;
            float y = e.getPosition().y;

            if(x > centerX){
                if(y > centerY)
                    upperRight.addElement(e);
                else
                    lowerRight.addElement(e);
            } else {
                if(y > centerY)
                    upperLeft.addElement(e);
                else
                    lowerLeft.addElement(e);
            }
        }

        /**
         * Regresa el numero de elementos contenidos en la coleccion
         * @return int con el numero de elementos
         */
        public int getSize(){
            if(!regionSet)
                return elements.size();
            else
                return upperLeft.getSize() + upperRight.getSize()
                + lowerLeft.getSize() + lowerRight.getSize();
        }

        public void clear(){
            if(!regionSet) {
                elements.clear();
                return;
            }

            upperLeft.clear();
            upperRight.clear();
            lowerLeft.clear();
            lowerRight.clear();
        }

        /**
         * Regresa los GameElements que se encuentran en el cuadrante del punto x,y
         * @param x coordenada x
         * @param y coordenada y
         * @return Lista con GameElements que se encuentrane en el mismo cuadrante del punto x,y
         */
        public List<GameElement> findNeighbors(float x, float y){
            if(!regionSet)
                return elements;

            if(x > centerX){
                if(y > centerY)
                    return upperRight.findNeighbors(x, y);
                else if(y < centerY)
                    return lowerRight.findNeighbors(x, y);
                else {
                    ArrayList<GameElement> combined = new ArrayList<>();
                    combined.addAll(upperRight.findNeighbors(x, y));
                    combined.addAll(lowerRight.findNeighbors(x, y));
                    return combined;
                }
            } else {
                if(y > centerY)
                    return upperLeft.findNeighbors(x, y);
                else if(y < centerY)
                    return lowerLeft.findNeighbors(x, y);
                else {
                    ArrayList<GameElement> combined = new ArrayList<>();
                    combined.addAll(upperLeft.findNeighbors(x, y));
                    combined.addAll(lowerLeft.findNeighbors(x, y));
                    return combined;
                }
            }
        }

        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            if(regionSet){
                sb.append(upperLeft);
                sb.append(upperRight);
                sb.append(lowerLeft);
                sb.append(lowerRight);
            } else {
                sb.append(centerX);
                sb.append(",");
                sb.append(centerY);
                sb.append(" (");
                sb.append(elements.size());
                sb.append(")\n");
            }


            return sb.toString();

        }

    }

    /**
     * Genera cuatro sub regiones tomando x, y como el centro.
     * @param r Region que se pretende dividir en otras regiones
     * @param minArea area mas pequena que se pretende tener en la division
     * @return Lista con CullZones que representa las regiones contenidas en el centro x,y
     */
    private static List<Region> getSubRegions(Region r, float cullSizeX, float cullSizeY ,float minArea){
        ArrayList<Region> subRegions = new ArrayList<>();

        if(cullSizeX > minArea && cullSizeY > minArea) {
            r.setRegions(new Region(r.centerX - cullSizeX / 2, r.centerY + cullSizeY / 2),
                    new Region(cullSizeX / 2 + r.centerX, r.centerY + cullSizeY / 2),
                    new Region(r.centerX - cullSizeX / 2, r.centerY - cullSizeY / 2),
                    new Region(r.centerX + cullSizeX / 2, r.centerY - cullSizeY / 2));
            subRegions.add(r.lowerLeft);
            subRegions.add(r.lowerRight);
            subRegions.add(r.upperLeft);
            subRegions.add(r.upperRight);
        }

        return subRegions;
    }

    /**
     * Genera una region que se forma dividiendo el total del mapa hasta que se llega a una extension menor a la especificada por minLength
     * @param minLength longitud minima que debe cumplir la region basica
     * @param totalX longitud total del eje X
     * @param totalY longitud total del eje Y
     * @return Cabeza de la region
     */
    public static Region getCull(int minLength, float totalX, float totalY){
        float cullSizeX = totalX / 2;
        float cullSizeY = totalY / 2;
        Region head = new Region(cullSizeX, cullSizeY);
        ArrayList<Region> currentRegions = new ArrayList<>();
        currentRegions.addAll(getSubRegions(head, cullSizeX, cullSizeY ,minLength));

        while(!currentRegions.isEmpty()){
            cullSizeX = cullSizeX / 2;
            cullSizeY = cullSizeY / 2;
            ArrayList<Region> newRegions = new ArrayList<>();
            for(Region r : currentRegions)
                newRegions.addAll(getSubRegions(r, cullSizeX, cullSizeY, minLength));
            currentRegions.clear();
            currentRegions.addAll(newRegions);
        }

        return head;
    }


    /**
     * Genera las regiones que se utilizaran para dividir el mapa en regiones mas pequenas
     * @param lenX longitud total del mapa en el eje X
     * @param lenY longitud total del mapa en el eje Y
     * @param minX longitud minima de la region en el eje X
     * @param minY longitud minima de la region en el eje Y
     * @return Lista que contiene las diferentes regiones del mapa
     */
    public static List<GameElement> generateRegions(float lenX, float lenY, float minX, float minY){
        int gridColumns = (int) (lenX / minX);
        int gridRows = (int) (lenY / minY);

        float gridSizeX = minX + (lenX % minX) / gridColumns;
        float gridSizeY = minY + (lenY % minY) / gridRows;

        ArrayList<GameElement> regions = new ArrayList<>(gridColumns * gridRows);

        System.out.println(gridSizeX);
        System.out.println(gridSizeY);

        return regions;
    }

}
