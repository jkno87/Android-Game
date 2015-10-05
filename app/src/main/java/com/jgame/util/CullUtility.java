package com.jgame.util;

import com.jgame.elements.GameElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose on 4/10/15.
 */
public class CullUtility {

    private abstract class CullZone {
        public abstract List<GameElement> findNeighbors(float x, float y);
        public abstract void addElement(GameElement e);
        public abstract void clear();

        public CullZone(){}
    }


    private class BasicRegion extends CullZone{
        private ArrayList<GameElement> containedElements;

        public BasicRegion (){
            containedElements = new ArrayList<>();
        }

        @Override
        public void addElement(GameElement e){
            containedElements.add(e);
        }

        @Override
        public List<GameElement> findNeighbors(float x, float y){
            return containedElements;
        }

        @Override
        public void clear(){
            containedElements.clear();
        }

    }

    private class Region {
        private float centerX;
        private float centerY;
        private CullZone upperLeft;
        private CullZone upperRight;
        private CullZone lowerLeft;
        private CullZone lowerRight;

        public Region(float centerX, float centerY){
            this.centerX = centerX;
            this.centerY = centerY;
        }

        public List<GameElement> getElements(float x, float y){
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

    }



}
