package com.jgame.util;

public abstract class TextureData {

    public static float[] MAIN_CHARACTER_1 = new float[]{0, 0.5f, 0.5f, 0.5f, 0.5f, 0, 0, 0};
    public static float[] MAIN_CHARACTER_2 = new float[]{0.5f, 0.5f, 1, 0.5f, 1, 0, 0.5f, 0};
    public static float[] MAIN_CHARACTER_3 = new float[]{0, 1, 0.5f, 1, 0.5f, 0.5f, 0, 0.5f};
    public static float[] MAIN_CHARACTER_4 = new float[]{0.5f, 1, 1, 1, 1, 0.5f, 0.5f, 0.5f };

    public static TextureData MAIN_CHARACTER_TEXTURES = new TextureData(){
        private int state = 0;
        private int frames = 0;

        @Override
        public float[] getTextCoords() {
            if(frames > 10){
                state++;
                frames = 0;
            }

            if(state == 0){
                frames++;
                return MAIN_CHARACTER_1;
            } else if(state == 1){
                frames++;
                return MAIN_CHARACTER_2;
            } else if(state == 2){
                frames++;
                return MAIN_CHARACTER_3;
            } else {
                state = 0;
                return MAIN_CHARACTER_4;
            }
        }

    };

    public static TextureData USE_WHOLE_IMAGE = new TextureData(){

        @Override
        public float[] getTextCoords() {
            return new float[]{0,1,1,1,1,0,0,0};
        }

    };

    /**
     * Genera un arreglo que contiene diferentes coordenadas dentro de un atlas de textura.
     * Solo funciona con el caso de tener un atlas totalmente horizontal.
     * @param xDifference Diferencia entre cada pedazo de textura horizontalmente.
     * @param pieces Numero de elementos en los que se dividira la textura.
     * @return Arreglo con las diferentes coordenadas del atlas de la textura.
     * @todo Esta redaccion esta horrorosa y tampoco me gusta la solucion para generar las coordenadas
     */
    public static float[][] createTextureArray(float xDifference, int pieces){
        float[][] textureArray = new float[pieces][8];
        float currX = 0;
        for(int i = 0; i < pieces; i++){
            textureArray[i] = new float[]{currX, 1, currX + xDifference, 1, currX + xDifference, 0, currX, 0};
            currX += xDifference;
        }

        return textureArray;
    }

    public abstract float[] getTextCoords();

}