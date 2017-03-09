package com.jgame.game;

import java.io.IOException;
import java.util.PriorityQueue;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

public class SoundManager implements Runnable {

    private SoundPool soundPool;
    private MediaPlayer mediaPlayer;
    private PriorityQueue<Integer> sonidosQueue;
    private volatile Boolean vivo;
    private Context context;

    public SoundManager(Context context){
        sonidosQueue = new PriorityQueue<Integer>(10);
        this.soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        vivo = true;
        this.context = context;
    }

    /**
     * Pausa la musica que se encuentra siendo reproducida
     */
    public void pauseMusic(){
        mediaPlayer.pause();
    }

    /**
     * Inicia la reproduccion de musica
     */
    public void startMusic(){
        mediaPlayer.start();
    }

    public int loadSound(Context context, int resId){
        return soundPool.load(context, resId, 0);
    }

    public void playSound(int soundId){
        sonidosQueue.add(soundId);
    }

    public void terminar(){
        synchronized(vivo){
            vivo = false;
            Log.d("Game", "Terminando thread de sonido");
        }

        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void iniciar(){
        Log.d("Game", "iniciar() soundManager");
        synchronized(vivo){
            vivo = true;
        }
        mediaPlayer = MediaPlayer.create(context, R.raw.music);
    }

    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(200L);
                synchronized(vivo){
                    if(!vivo) {
                        break;
                    }
                }

                if(!sonidosQueue.isEmpty())
                    soundPool.play(sonidosQueue.poll(),1,1,0,0,1);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}