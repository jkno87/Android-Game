package com.jgame.util;

import com.jgame.elements.GameElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose on 4/10/15.
 */
public class CullUtility {

    private class Region {
        private float centerX;
        private float centerY;
        private Region upperLeft;
        private Region upperRight;
        private Region lowerLeft;
        private Region lowerRight;
        private boolean regionSet;

        public Region(float centerX, float centerY){
            this.centerX = centerX;
            this.centerY = centerY;
        }

        public void setRegions(Region upperLeft, Region upperRight, Region lowerLeft, Region lowerRight){
            this.upperLeft = upperLeft;
            this.upperRight = upperRight;
            this.lowerLeft = lowerLeft;
            this.lowerRight = lowerRight;
            regionSet = true;
        }

        public void addElement(GameElement e){

        }

        public void clear(){
            upperLeft.clear();
            upperRight.clear();
            lowerLeft.clear();
            lowerRight.clear();
        }

        public List<GameElement> findNeighbors(float x, float y){
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
            sb.append(centerX);
            sb.append(",");
            sb.append(centerY);
            if(regionSet){
                sb.append(upperLeft);
                sb.append("\n");
                sb.append(upperRight);
                sb.append("\n");
                sb.append(lowerLeft);
                sb.append("\n");
                sb.append(lowerRight);
                sb.append("\n");
            }


            return sb.toString();

        }

    }


    private Region head;

    /**
     * Genera cuatro sub regiones tomando x, y como el centro.
     * @param r Region que se pretende dividir en otras regiones
     * @param minArea area mas pequena que se pretende tener en la division
     * @return Lista con CullZones que representa las regiones contenidas en el centro x,y
     */
    private List<Region> getSubRegions(Region r, float minArea){
        ArrayList<Region> subRegions = new ArrayList<>();
        float halfX = r.centerX / 2;
        float halfY = r.centerY / 2;

        System.out.println("Current Center " + halfX + ',' + halfY);
        System.out.println(minArea);

        if(halfX > minArea && halfY > minArea) {
            r.setRegions(new Region(halfX, r.centerY + halfY), new Region(halfX + r.centerX, r.centerY + halfY),
            new Region(halfX, halfY), new Region(r.centerX + halfX, halfY));
            subRegions.add(r.lowerLeft);
            subRegions.add(r.lowerRight);
            subRegions.add(r.upperLeft);
            subRegions.add(r.upperRight);
        }

        return subRegions;
    }

    public CullUtility(int minLength, float totalX, float totalY){
        float cullSizeX = totalX / 2;
        float cullSizeY = totalY / 2;
        this.head = new Region(cullSizeX, cullSizeY);
        ArrayList<Region> currentRegions = new ArrayList<>();
        currentRegions.addAll(getSubRegions(head, minLength));

        while(!currentRegions.isEmpty()){
            ArrayList<Region> newRegions = new ArrayList<>();
            for(Region r : currentRegions)
                newRegions.addAll(getSubRegions(r, minLength));
            currentRegions.clear();
            currentRegions.addAll(newRegions);
        }
    }

    @Override
    public String toString(){
        return head.toString();
    }

}
