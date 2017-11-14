package com.jgame.util;

/**
 * Objeto que sirve como timer para realizar ciertas operaciones de accum
 * Created by jose on 29/01/15.
 */
public class TimeCounter {

    private float accumTime;
    private float time;

    public TimeCounter(float time){
        this.time = time;
    }

    public void setInterval(float time){
        this.time = time;
        accumTime = 0;
    }

    public boolean completed(){
        return accumTime >= time;
    }

    public void reset(){
        this.accumTime = 0.0f;
    }

    public float pctCharged(){
        if(accumTime > time)
            return 1;
        else
            return accumTime / time;
    }

}
