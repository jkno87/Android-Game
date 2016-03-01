package com.jgame.util;

/**
 * Created by jose on 23/02/16.
 */
public class IdGenerator {

    private int currentId;

    public synchronized int getId(){
        return currentId++;
    }

}
