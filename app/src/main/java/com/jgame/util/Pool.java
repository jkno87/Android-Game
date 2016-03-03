package com.jgame.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose on 3/03/16.
 */
public class Pool<T> {

    public static abstract class ObjectFactory <T>{
        public abstract T create();
    }

    private final List<T> freeObjects;
    private final int maxSize;
    private final ObjectFactory<T> factory;

    public Pool(ObjectFactory<T> factory, int maxSize){
        this.factory = factory;
        this.maxSize = maxSize;
        freeObjects = new ArrayList<>(maxSize);
    }

    /**
     * Crea una instancia de un objeto T
     * @return
     */
    public T createObject(){
        T instance = null;
        if(freeObjects.isEmpty())
            instance = factory.create();
        else
            instance = freeObjects.remove(freeObjects.size() - 1);

        return instance;
    }

    /**
     * Libera el objeto T
     * @param object
     */
    public void free(T object){
        if(freeObjects.size() < maxSize)
            freeObjects.add(object);
    }


}
