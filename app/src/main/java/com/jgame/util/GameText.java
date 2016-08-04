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
    private static int LETTERS_COLUMN = 10;
    private static int LETTERS_ROW = 3;
    public static final TextureData[] LETTERS = generateAlphabet(0.625f, 0.1875f, 0.125f, 0.75f,LETTERS_COLUMN, LETTERS_ROW);

    /**
     * Genera la informacion de las texturas dependiendo del numero de columnas y filas que se proporcionan
     * @param lengthX longitud X que ocupa en el atlas de textura
     * @param lengthY longitud Y que ocupa en el atlas de texutra
     * @param startX coordenada X en la que se inicia el calculo
     * @param startY coordenada Y en la que se inicia el calculo de la textura
     * @param columns numero de columnas que tiene la textura
     * @param rows numero de filas que tiene la textura
     * @return arreglo con la informacion de las regiones de las texturas
     */
    private static TextureData[] generateAlphabet(float lengthX, float lengthY, float startX, float startY, int columns, int rows){
        TextureData[] alphabet = new TextureData[columns * rows];
        int i = 0;
        int j = 0;

        while(j < rows){
            alphabet[i+j*columns] = new TextureData(((float) i/columns) * lengthX + startX, ((float)j/rows) * lengthY + startY,
                    ((float)(i+1)/columns) * lengthX + startX, ((float)(j+1)/rows) * lengthY + startY);
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

        for (int i = 0; i < texto.length; i++) {
            textureDrawer.addTexturedSquare(currentX, bounds.position.y + margin,
                    letterSize, bounds.lenY, LETTERS[(int) texto[i] - 97]);
            currentX += letterSize;
        }

    }

}
