package com.jgame.definitions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jgame.elements.FoodOrganism;
import com.jgame.game.LevelInformation.LevelInfoCreator;
import com.jgame.characters.MovementController;
import com.jgame.elements.ElementCreator;
import com.jgame.elements.GameElement;
import com.jgame.elements.MovingOrganism;
import com.jgame.elements.Organism;
import com.jgame.elements.StunInfo;
import com.jgame.game.GameFlow;
import com.jgame.game.LevelInformation;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

public class GameLevels {

    public static final float FRUSTUM_HEIGHT = 320f;
    public static final float FRUSTUM_WIDTH = 480f;
    //public static final float MAX_PLAYING_HEIGHT = FRUSTUM_HEIGHT - 80f;
    public static final int ID_FENCE = 0;
    public static final int ID_LION = 1;


/*    public static final CharacterInformation CHARACTER_INFO_FENCE =
            new CharacterInformation(30f, 5, 5, ID_FENCE, new float[]{0,0.5f,1,0.5f,1,0,0,0}, CIRCLE_CONTROLLER){
            };

    public static final CharacterInformation CHARACTER_INFO_LION =
            new CharacterInformation(30f, 5, 5, ID_LION, new float[]{0,1,1,1,1,0.5f,0,0.5f}, CIRCLE_CONTROLLER){
            };


    /*public static PositionGenerator createRandomGenerator(final boolean horizontal, final int elements, final float offset, final float length){
        //variables locales para que se ahorre el tiempo de acceso a los campos estáticos.
        final float frustumHeight = GameLogic.FRUSTUM_HEIGHT;
        final float frustumWidth = GameLogic.FRUSTUM_WIDTH;

        return new PositionGenerator(){
            private Random rand = new Random();
            //final float range = horizontal ? frustumWidth : frustumHeight;

            @Override
            public List<Vector2> getPositions() {
                List<Vector2> positions = new ArrayList<>();

                for(int i = 0; i < elements; i++) {
                    float position = (rand.nextFloat() * length) + offset;
                    if(horizontal)
                        positions.add(new Vector2(position, frustumHeight));
                    else
                        positions.add(rand.nextFloat() > 0.5f ?
                                new Vector2(0, position) : new Vector2(frustumWidth, position));
                }

                return positions;
            }
        };
    }*/


    public static final LevelInfoCreator TUTORIAL_CREATOR =
            new LevelInfoCreator().addObjective(GameIds.EVOLVED_ORGANISM_ID, 1)
            .addObjective(GameIds.MOVING_ORGANISM_ID, 3);

    public static final ElementCreator.ElementWave SIMPLE_WAVE =
            new ElementCreator.ElementWave(){
                private TimeCounter spawnTimer = new TimeCounter();
                private Random random = new Random();
                private List<GameElement> generatedElements = new ArrayList<>(10);
                private float avgLifespan;
                private float maxDistanceX;
                private float maxDistanceY;
                private float originX;
                private float originY;
                private int currentId = 2000;
                //private float avgHp;


                public void initialize(){
                    initialize(5, 50, 50, GameLevels.FRUSTUM_WIDTH, GameLevels.FRUSTUM_HEIGHT, 6);
                }

                /**
                 * Metodo que se requiere para que funcione correctamente la instancia. Establece los valores que se utilizaran para
                 * generar la ola de GameElements
                 * @param avgLifespan
                 * @param maxDistanceX
                 * @param maxDistanceY
                 * @param originX
                 * @param originY
                 * @param interval
                 */
                public void initialize(float avgLifespan, float maxDistanceX, float maxDistanceY, float originX, float originY, float interval){
                    spawnTimer.setInterval(interval);
                    this.avgLifespan = avgLifespan;
                    this.maxDistanceX = maxDistanceX;
                    this.maxDistanceY = maxDistanceY;
                    this.originX = originX;
                    this.originY = originY;
                }

                @Override
                public List<GameElement> generate(float interval){
                    generatedElements.clear();
                    spawnTimer.accum(interval);

                    if(!spawnTimer.completed())
                        return generatedElements;

                    for(int i = 0; i < random.nextInt(10) + 1; i++)
                        generatedElements.add(new MovingOrganism(avgLifespan+((random.nextFloat() - 0.5f) * avgLifespan),
                                new Vector2(originX + random.nextFloat()*maxDistanceX, originY+random.nextFloat()*maxDistanceY)
                                , 15, 20, currentId++));

                    spawnTimer.reset();

                    return generatedElements;
                }
            };

    public static final ElementCreator TEST_CREATOR = new ElementCreator(new ElementCreator.ElementWave[]{SIMPLE_WAVE});

}