package com.jgame.game;

import java.util.PriorityQueue;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

public class SoundManager implements Runnable {

    private SoundPool soundPool;
    private int soundId;
    private PriorityQueue<Integer> sonidosQueue;
    private volatile Boolean vivo;

    public SoundManager(Context context){
        sonidosQueue = new PriorityQueue<Integer>();
        this.soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        this.soundId = soundPool.load(context, R.raw.eat, 0);
        vivo = true;
    }

    public void testSonido(){
        sonidosQueue.add(soundId);
    }

    public void terminar(){
        synchronized(vivo){
            vivo = false;
            Log.d("Game", "Terminando thread de sonido");
        }
    }

    public void iniciar(){
        synchronized(vivo){
            vivo = true;
        }
    }

    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(200L);
                synchronized(vivo){
                    if(!vivo)
                        break;
                }

                if(!sonidosQueue.isEmpty())
                    soundPool.play(sonidosQueue.poll(),1,1,0,0,1);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}