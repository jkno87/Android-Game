package com.jgame.util;

import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.util.TextureDrawer.ColorData;

/**
 * Created by ej-jose on 17/08/15.
 */
public class GameText {

    private final int[] lettersId;
    private final float startX;
    private final float startY;
    private static int LETTERS_COLUMN = 10;
    private static int LETTERS_ROW = 3;
    public static final TextureData[] LETTERS = generateAlphabet(0.15625f, 0.0625f, 0.03125f, 0.1875f, LETTERS_COLUMN, LETTERS_ROW);
    private final Square currentLetter;

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


    public GameText(String texto, Square bounds, float letterSize){
        this.lettersId = new int[texto.length()];
        this.startX = (bounds.position.x + bounds.lenX/2) - lettersId.length * (letterSize/2);
        this.startY = bounds.position.y + bounds.lenY/2 - letterSize/2;
        this.currentLetter = new Square(0,0,letterSize, letterSize);

        for(int i = 0; i < lettersId.length; i++)
            lettersId[i] = (int) texto.charAt(i) - 97;
    }

    /**
     * Agrega los vertices a letterDrawer para dibujar el GameText.
     * @param textureDrawer Drawer al que se agregaran los vertices
     */
    public void addLetterTexture(TextureDrawer textureDrawer) {
        currentLetter.position.x = startX;
        currentLetter.position.y = startY;

        for (int i = 0; i < lettersId.length; i++) {
            textureDrawer.addTexturedSquare(currentLetter, LETTERS[lettersId[i]]);
            currentLetter.position.x += currentLetter.lenX;
        }
    }

    /**
     * Dibuja las letras utilizando el color colorData
     * @param textureDrawer drawer al que se le asignara la informacion de dibujo
     * @param colorData informacion de color
     */
    public void addLetterTexture(TextureDrawer textureDrawer, ColorData colorData){
        currentLetter.position.x = startX;
        currentLetter.position.y = startY;

        for (int i = 0; i < lettersId.length; i++) {
            textureDrawer.addColoredSquare(currentLetter, LETTERS[lettersId[i]], colorData);
            currentLetter.position.x += currentLetter.lenX;
        }
    }
}
