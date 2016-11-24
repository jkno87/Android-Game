package com.jgame.util;

/**
 * Created by jose on 24/11/16.
 */
public class DigitsDisplay {

    private final static TextureDrawer.TextureData[] DIGITS = new TextureDrawer.TextureData[]{new TextureDrawer.TextureData(0.057f,0.51f,0.077f,0.5449f),new TextureDrawer.TextureData(0.077f,0.51f,0.097f,0.5449f),
            new TextureDrawer.TextureData(0.097f,0.51f,0.117f,0.5449f), new TextureDrawer.TextureData(0.117f,0.51f,0.136f,0.5449f),new TextureDrawer.TextureData(0.136f,0.51f,0.156f,0.5449f),
            new TextureDrawer.TextureData(0.156f,0.51f,0.175f,0.5449f),new TextureDrawer.TextureData(0.175f,0.51f,0.195f,0.5449f), new TextureDrawer.TextureData(0.195f,0.51f,0.214f,0.5449f),
            new TextureDrawer.TextureData(0.214f,0.51f,0.233f,0.5449f),new TextureDrawer.TextureData(0.233f,0.51f,0.255f,0.5449f)};
    private final float digitSizeX;
    private final float digitSizeY;
    private final int digitsAvailable;
    public final Vector2 position;
    public int number;
    private int digitsDrawn;
    private float currentX;

    public DigitsDisplay(int digitSizeX, int digitSizeY, int digitsAvailable, Vector2 position){
        this.digitSizeX = digitSizeX;
        this.digitSizeY = digitSizeY;
        this.digitsAvailable = digitsAvailable;
        this.position = position;
        number = 0;
    }

    /**
     * Se dibuja en pantalla el numero proporcionado a la funcion
     */
    public void addDigitsTexture(TextureDrawer tdrawer){
        currentX = position.x;
        digitsDrawn = 0;

        while(digitsDrawn < digitsAvailable){
            int nVal = number / 10;
            int rem = number % 10;
            number = nVal;
            tdrawer.addTexturedSquare(currentX, position.y, digitSizeX, digitSizeY, DIGITS[rem]);
            currentX -= digitSizeX;
            digitsDrawn++;
        }
    }

}
