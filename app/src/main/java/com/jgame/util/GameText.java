package com.jgame.util;

import com.jgame.util.TextureDrawer.TextureData;

/**
 * Created by ej-jose on 17/08/15.
 */
public class GameText {

    private final byte[] texto;
    private float x;
    private float y;
    private float size;
    private static int LETTERS_COLUMN = 16;
    private static int LETTERS_ROW = 16;
    public static final TextureData[] LETTERS = generateAlphabet(LETTERS_COLUMN, LETTERS_ROW);

    /**
     * Genera la informacion de las texturas dependiendo del numero de columnas y filas que se proporcionan
     * @param columns numero de columnas que tiene la textura
     * @param rows numero de filas que tiene la textura
     * @return arreglo con la informacion de las regiones de las texturas
     */
    private static TextureData[] generateAlphabet(int columns, int rows){
        TextureData[] alphabet = new TextureData[columns * rows];
        int i = 0;
        int j = 0;

        while(j < rows){
            alphabet[i+j*columns] = new TextureData((float)i/columns, (float)j/rows,
                    (float)(i+1)/columns, (float)(j+1)/columns);
            i++;
            if(i == columns){
                i = 0;
                j++;
            }
        }

        return alphabet;
    }


    public GameText(String texto, float x, float y, float size){
        this.texto = texto.getBytes();
        this.x = x;
        this.y = y;
        this.size = size;
    }

    /**
     * Agrega los vertices a letterDrawer para dibujar el GameText.
     * @param textureDrawer Drawer al que se agregaran los vertices
     */
    public void addLetterTexture(TextureDrawer textureDrawer){
        float currentX = x - size * (texto.length / 2);
        float offset = texto.length * 0.2f;

        for(int i = 0; i < texto.length; i++){
            textureDrawer.addTexturedSquare(currentX, y, size, LETTERS[(int)texto[i] - 97]);
            currentX += size + offset;
        }
    }

}
