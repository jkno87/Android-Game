package com.jgame.game;

import com.jgame.definitions.CharacterInformation;
import com.jgame.definitions.GameLevels;
import com.jgame.util.GameText;
import com.jgame.util.Square;
import com.jgame.util.Vector2;

/**
 * Created by ej-jose on 12/08/15.
 */
public class CharacterSelectFlow extends GameFlow {

    public static class PinTarget {
        private final Vector2 position;
        private final float radius;
        private CharacterInformation characterSelected;

        public PinTarget (float x, float y, float radius){
            position = new Vector2(x, y);
            this.radius = radius;
        }

        public boolean within(float x, float y){
            return position.dist(x, y) <= radius;
        }

        public void fill(CharacterInformation characterSelected){
            this.characterSelected = characterSelected;
        }

        public void release(){
            characterSelected = null;
        }

        public boolean filled(){
            return characterSelected != null;
        }

    }

    public static class PinButton {
        public Square size;
        private final Vector2 originalPosition;
        public final CharacterInformation characterInfo;

        public PinButton(Square info, CharacterInformation characterInfo){
            this.size = info;
            originalPosition = new Vector2(info.position);
            this.characterInfo = characterInfo;
        }

        public void resetPosition(){
            size.position.set(originalPosition);
        }

    }

    private final int CHARACTER_NONE = -1;
    private final float frustumWidth = GameLevels.FRUSTUM_WIDTH;
    private final float frustumHeight = GameLevels.FRUSTUM_HEIGHT;
    public final PinTarget [] availableShips;
    public final PinButton [] availableCharacters;
    private int characterSelected;
    public final Square confirmButton;
    private GameActivity gameActivity;
    public GameText continueLabel;

    public CharacterSelectFlow(GameActivity gameActivity){
        availableShips = new PinTarget[]{new PinTarget(frustumWidth/2, frustumHeight/2, 30)};
        availableCharacters = new PinButton[]{ new PinButton(new Square(70, 70, 50, 50), GameLevels.CHARACTER_INFO_FENCE),
                new PinButton(new Square(frustumWidth - 70, 70, 50, 50), GameLevels.CHARACTER_INFO_LION)};
        characterSelected = CHARACTER_NONE;
        confirmButton = new Square(frustumWidth/2, frustumHeight - 75, 35, 15);
        this.gameActivity = gameActivity;
        continueLabel = new GameText("start", frustumWidth/2, frustumHeight - 75, 10);
    }

    /**
     * Checa que los targets que representan las naves se encuentren todos seleccionados.
     * @return Boolean con el estado de las naves en seleccion de nivel
     */
    public boolean shipsFilled(){
        for(PinTarget pin : availableShips)
            if(!pin.filled())
                return false;

        return true;
    }


    @Override
    public void handleUp(float x, float y) {
        if(characterSelected == CHARACTER_NONE)
            return;

        PinButton b = availableCharacters[characterSelected];
        for(PinTarget pinTarget : availableShips) {
            if (pinTarget.within(b.size.position.x, b.size.position.y) && !pinTarget.filled()) {
                pinTarget.fill(b.characterInfo);
                characterSelected = CHARACTER_NONE;
                return;
            }

            if(pinTarget.filled() && pinTarget.characterSelected.id == b.characterInfo.id)
                pinTarget.release();
        }

        availableCharacters[characterSelected].resetPosition();
        characterSelected = CHARACTER_NONE;
        return;
    }

    @Override
    public void handleDown(float x, float y){
        float gameX = frustumWidth * x;
        float gameY = frustumHeight * y;

        for(int i = 0; i < availableCharacters.length; i++){
            PinButton character = availableCharacters[i];
            if(character.size.contains(gameX, gameY) &&
                    (characterSelected == CHARACTER_NONE || characterSelected == i))
                characterSelected = i;
        }

        return;
    }

    @Override
    public void handleDrag(float x, float y){
        if(characterSelected != CHARACTER_NONE)
            availableCharacters[characterSelected].size.position.set(frustumWidth * x, frustumHeight * y);
    }

    @Override
    public void update(float interval) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }
}
