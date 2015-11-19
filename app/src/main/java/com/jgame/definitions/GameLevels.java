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

    public static final float FRUSTUM_HEIGHT = 480f;
    public static final float FRUSTUM_WIDTH = 320f;
    public static final float MAX_PLAYING_HEIGHT = FRUSTUM_HEIGHT - 80f;
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
                private TimeCounter spawnTimer;
                private Random random;
                private final static float AVG_LIFESPAN = 6;
                private final static float AVG_DISTANCE_X = 70;
                private final static float AVG_DISTANCE_Y = 120;
                private final static float AVG_X = 100;
                private final static float AVG_Y = 100;
                private final static float FOOD_HP = 12;

                public void initialize(){
                    spawnTimer = new TimeCounter(3f);
                    random = new Random();
                }

                private Vector2 generatePosition(float avgX, float avgY){
                    return new Vector2(((random.nextFloat() - 0.5f) * AVG_DISTANCE_X) + avgX,
                            ((random.nextFloat() - 0.5f) * AVG_DISTANCE_Y) + avgY);
                }

                @Override
                public List<GameElement> generate(float interval){
                    spawnTimer.accum(interval);
                    List<GameElement> elements = new ArrayList<GameElement>();

                    if(!spawnTimer.completed())
                        return elements;

                    for(int i = 0; i < random.nextInt(10) + 3; i++)
                        elements.add(new FoodOrganism(AVG_LIFESPAN + ((random.nextFloat() - 0.5f) * AVG_LIFESPAN), generatePosition(AVG_X, AVG_Y)
                        , 8.0f, 0, 0));

                    for(int i = 0; i < random.nextInt(5) + 1; i++)
                        elements.add(new MovingOrganism(13 + ((random.nextFloat() - 0.5f) * 13),
                                generatePosition(150,250), 35f, 5f));

                    spawnTimer.reset();

                    return elements;
                }
            };

    public static final ElementCreator TEST_CREATOR = new ElementCreator(new ElementCreator.ElementWave[]{SIMPLE_WAVE});

}