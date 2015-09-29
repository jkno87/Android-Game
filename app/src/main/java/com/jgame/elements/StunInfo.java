package com.jgame.elements;

/**
 * Esta clase se encarga de representar la informacion que utiliza la logica del juego para calcular las colisiones
 * Created by ej-jose on 25/09/15.
 */
public class StunInfo {
    public final float time;
    public final float force;

    public StunInfo(float time, float force){
        this.time = time;
        this.force = force;
    }
}
