package com.jgame.util;

/**
 * Created by jose on 24/11/16.
 */
public class DigitsDisplay {

    private final static Drawer.TextureData[] DIGITS = new Drawer.TextureData[]{Drawer.generarTextureData(3,26,4,28,102.4f),
            Drawer.generarTextureData(4,26,5,28,102.4f),Drawer.generarTextureData(5,26,6,28,102.4f),Drawer.generarTextureData(6,26,7,28,102.4f),
            Drawer.generarTextureData(7,26,8,28,102.4f),Drawer.generarTextureData(8,26,9,28,102.4f),Drawer.generarTextureData(9,26,10,28,102.4f),
            Drawer.generarTextureData(10,26,11,28,102.4f),Drawer.generarTextureData(11,26,12,28,102.4f),Drawer.generarTextureData(12,26,13,28,102.4f)};
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
    public void addDigitsTexture(Drawer drawer){
        currentX = position.x;
        digitsDrawn = 0;

        while(digitsDrawn < digitsAvailable){
            int nVal = number / 10;
            int rem = number % 10;
            number = nVal;
            drawer.addTexturedSquare(currentX, position.y, digitSizeX, digitSizeY, DIGITS[rem]);
            currentX -= digitSizeX;
            digitsDrawn++;
        }
    }

}
