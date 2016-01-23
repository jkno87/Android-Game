package com.jgame.util;

import com.jgame.util.TextureDrawer.TextureData;

/**
 * Created by ej-jose on 17/08/15.
 */
public class GameText {

    private final byte[] texto;
    private Square bounds;
    private float letterSize;
    private float margin;
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


    public GameText(String texto, Square bounds, float margin){
        this.texto = texto.getBytes();
        this.bounds = bounds;
        this.margin = margin;
        this.letterSize = (bounds.lenX - margin*2) / this.texto.length;
    }

    /**
     * Agrega los vertices a letterDrawer para dibujar el GameText.
     * @param textureDrawer Drawer al que se agregaran los vertices
     */
    public void addLetterTexture(TextureDrawer textureDrawer) {
        float currentX = bounds.position.x;
        //float offset = texto.length * 0.2f;

        //textureDrawer.addTexturedSquare(bounds, LETTERS[0]);
        for (int i = 0; i < texto.length; i++) {
            textureDrawer.addTexturedSquare(currentX, bounds.position.y + margin,
                    letterSize, bounds.lenY, LETTERS[(int) texto[i] - 97]);
            currentX += letterSize;
        }

    }

}
