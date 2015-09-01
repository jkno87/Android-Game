package com.jgame.definitions;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jgame.characters.Attack;
import com.jgame.characters.MovementController;
import com.jgame.elements.BossEnemy;
import com.jgame.elements.Enemy;
import com.jgame.elements.EnemyAccelerator;
import com.jgame.elements.EnemyParticle;
import com.jgame.elements.EnemySpawner;
import com.jgame.elements.EnemySpawner.PositionGenerator;
import com.jgame.elements.HomingEnemy;
import com.jgame.elements.SimpleEnemy;
import com.jgame.elements.SimpleEnemy.SnakeBody;
import com.jgame.elements.EnemySpawner.SpawnElement;
import com.jgame.game.GameLogic;
import com.jgame.game.GameLogic.Level;
import com.jgame.util.AnimationData;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

public class GameLevels {

    public static final float FRUSTUM_HEIGHT = 480f;
    public static final float FRUSTUM_WIDTH = 320f;
    public static final int FAST_SIMPLE_HP = 1;
    public static final int FAST_SIMPLE_POINTS = 1;
    public static final int FAST_SIMPLE_DAMAGE = 1;
    public static final float FAST_SIMPLE_SIZE = 15;
    public static final Vector2 FAST_SIMPLE_SPEED = new Vector2(0, -1.05f);
    public static final int SIMPLE_POINTS = 5;
    public static final int METEOR_POINTS = 15;
    public static final int HOMING_POINTS = 5;
    public static final int COMET_POINTS = 20;
    public static final int BOSS_POINTS = 100;
    public static final int SIMPLE_HP = 8;
    public static final int SIMPLE_DAMAGE = 1;
    public static final float SIMPLE_SIZE = 42f;
    public static final Vector2 SIMPLE_SPEED = new Vector2(0, -0.38f);
    public static final int METEOR_HP = 1;
    public static final int METEOR_DAMAGE = 1;
    public static final float METEOR_SIZE = 10f;
    public static final Vector2 METEOR_SPEED = new Vector2(0, -3f);
    public static final int ACC_HP = 2;
    public static final float ACC_SIZE = 15f;
    public static final float ACC_SPEED = 2.5f;
    public static final Vector2 COMET_SPEED = new Vector2(3, 1.5f);
    public static final float COMET_SIZE = 25f;
    public static final float HOMING_REFRESH_RATE = 0.45f;
    public static final float HOMING_SPEED = 2.25f;
    public static final float HOMING_SIZE = 10f;
    public static final float[] HOMING_COLOR = new float[]{1,0,0.35f,1};
    public static final Enemy.StunInfo HOMING_STUN = new Enemy.StunInfo(1.5f, 5);
    public static final int ID_FENCE = 0;
    public static final int ID_LION = 1;


    public static final MovementController CIRCLE_CONTROLLER = new MovementController(new Vector2(), 0) {
        private boolean stunned;
        private TimeCounter stunCounter;
        private Vector2 stunDirection;
        private float size = 50;

        @Override
        public boolean stunned() {
            return stunned;
        }

        @Override
        public void stun(Vector2 stunPosition, Enemy.StunInfo stunInfo) {
            stunCounter = new TimeCounter(stunInfo.time);
            stunDirection = new Vector2(position).sub(stunPosition).nor().mul(stunInfo.force);
            stunned = true;
        }

        @Override
        public boolean collision(Enemy enemy) {
            return position.dist(enemy.position) < enemy.size + size;
        }

        @Override
        public boolean containsPoint(float x, float y) {
            return position.dist(x, y) <= size;
        }

        @Override
        public void move(float x, float y) {
            position.set(x, y);
        }

        @Override
        public void updateDirection(float x, float y){
            angle = new Vector2(x,y).sub(position).angle();
        }

        @Override
        public void update(GameLogic gameInstance, float timeDifference) {
            if(stunned){
                stunCounter.accum(timeDifference);
                if(stunCounter.completed())
                    stunned = false;

                if(!gameInstance.withinXBounds(position.x, size))
                    stunDirection.x *= -1;

                if(!gameInstance.withinYBounds(position.y, size))
                    stunDirection.y *= -1;

                position.add(stunDirection);

                return;
            }
        }
    };

    public static final CharacterInformation CHARACTER_INFO_FENCE =
            new CharacterInformation(30f, 5, 5, ID_FENCE, new float[]{0,0.5f,1,0.5f,1,0,0,0}, CIRCLE_CONTROLLER){

                @Override
                public Attack getPrimaryAttack() {
                    return null;
                }

                @Override
                public Attack getSecondaryAttack() {
                    return null;
                }
            };

    public static final CharacterInformation CHARACTER_INFO_LION =
            new CharacterInformation(30f, 5, 5, ID_LION, new float[]{0,1,1,1,1,0.5f,0,0.5f}, CIRCLE_CONTROLLER){

                @Override
                public Attack getPrimaryAttack() {
                    return null;
                }

                @Override
                public Attack getSecondaryAttack() {
                    return null;
                }
            };


    public enum EnemyFactory {
        SIMPLE {
            @Override
            public List<Enemy> produce(Vector2 position) {
                ArrayList<Enemy> l = new ArrayList<>();
                l.add(new SimpleEnemy(SIMPLE_HP, SIMPLE_DAMAGE, position, SIMPLE_SIZE, SIMPLE_SPEED, SIMPLE_POINTS));
                return l;
            }
        },

        FAST_SIMPLE {
            @Override
            public List<Enemy> produce(Vector2 position) {
                ArrayList<Enemy> l = new ArrayList<>();
                l.add(new SimpleEnemy(FAST_SIMPLE_HP, FAST_SIMPLE_DAMAGE, position, FAST_SIMPLE_SIZE,
                        FAST_SIMPLE_SPEED, FAST_SIMPLE_POINTS));
                return l;
            }
        },

        COVERED {
            @Override
            public List<Enemy> produce(Vector2 position) {
                ArrayList<Enemy> l = new ArrayList<>();
                SimpleEnemy e = new SimpleEnemy(SIMPLE_HP, SIMPLE_DAMAGE, position, SIMPLE_SIZE, SIMPLE_SPEED, SIMPLE_POINTS);
                float distance = 10;
                float rotation = 0;
                float rotationGap = 33;
                for(int i = 0; i < 40; i++){
                    if(rotation > 360){
                        distance += 10;
                        rotationGap -= 5;
                        rotation = 0;
                    }
                    rotation += rotationGap;
                    EnemyParticle ep = new EnemyParticle(e, 1, 1, 5, new AnimationData(1, 0.5f, 0, 0.5f, 0), 1, distance, rotation);
                    l.add(ep);
                    e.addSubordinate(ep);
                    //e.addSubordinate(new EnemyParticle(e, 1, 1, 5, new AnimationData(1, 0.5f, 0, 0.5f, 0), 1, distance, rotation));
                }

                l.add(e);
                return l;
            }
        },

        SNAKE {
            @Override
            public List<Enemy> produce(Vector2 position) {
                float distance = 25;
                float xDifference = -20;
                List<Enemy> l = new ArrayList<Enemy>();
                SnakeBody currentEnemy = new SimpleEnemy.SnakeHead(SIMPLE_HP, SIMPLE_DAMAGE,
                        position, new Vector2(0, -1), 0.85f, 5f, new AnimationData(1, 0.5f, 0, 0.5f, 0), 1);

                l.add(currentEnemy);

                for(int i = 0; i < 6; i++){
                    l.add(new SnakeBody(SIMPLE_HP, SIMPLE_DAMAGE,
                            new Vector2(position).add(xDifference, distance), 5f, new AnimationData(1, 0.5f, 0, 0.5f, 0), 1, currentEnemy));
                    xDifference += 5;
                }

                return l;
            }
        },

        METEOR {
            @Override
            public List<Enemy> produce(Vector2 position) {
                List<Enemy> l = new ArrayList<>();
                l.add(new SimpleEnemy(METEOR_HP, METEOR_DAMAGE, position, METEOR_SIZE, METEOR_SPEED, METEOR_POINTS));

                return l;
            }
        },

        ACCELERATOR {
            @Override
            public List<Enemy> produce(Vector2 position) {
                List<Enemy> l = new ArrayList<>();
                l.add(new EnemyAccelerator(ACC_HP, position, ACC_SIZE, ACC_SPEED, 0));
                return l;
            }
        },

        COMET {
            @Override
            public List<Enemy> produce(Vector2 position) {
                Vector2 speed = COMET_SPEED;
                List<Enemy> l = new ArrayList<>();
                if(position.x == GameLogic.FRUSTUM_WIDTH)
                    speed = new Vector2(-COMET_SPEED.x, COMET_SPEED.y);

                l.add(new SimpleEnemy(ACC_HP, METEOR_DAMAGE, position, COMET_SIZE, new Vector2(speed), METEOR_POINTS){
                    private Vector2 gravity = new Vector2(0, -0.05f);
                    @Override
                    public void update(GameLogic gameInstance, float timeDiff){
                        super.update(gameInstance, timeDiff);
                        position.add(direction);
                        direction.add(gravity);
                    }
                });

                return l;
            }
        },

        LEVEL_ONE_BOSS {
            @Override
            public List<Enemy> produce(Vector2 position) {
                List<Enemy> l = new ArrayList<>();
                position.sub(new Vector2(0, 75f));
                l.add(new BossEnemy(35, 1, position, new Vector2(1, 0), 65f, new AnimationData(2, 0.5f, 0, 0, 0.5f), BOSS_POINTS));
                return l;
            }
        },

        HOMING {
            @Override
            public List<Enemy> produce(Vector2 position) {
                List<Enemy> enemies = new ArrayList<>();
                Enemy e = new HomingEnemy(1, SIMPLE_DAMAGE, position, HOMING_SIZE, HOMING_SPEED, HOMING_COLOR, HOMING_REFRESH_RATE,
                        HOMING_POINTS);
                e.stunInfo = HOMING_STUN;
                enemies.add(e);
                return enemies;
            }
        };

        public abstract List<Enemy> produce(Vector2 position);
    }

    public static PositionGenerator createRandomGenerator(final boolean horizontal, final int elements, final float offset, final float length){
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
    }


    public static final Level TEST_LEVEL =
            new Level(){



                private PositionGenerator createGenerator(final float gap){
                    return new PositionGenerator() {
                        private float currentX = gap;

                        @Override
                        public List<Vector2> getPositions() {
                            List<Vector2> positions = new ArrayList<>();
                            positions.add(new Vector2(currentX, 480f));
                            currentX += gap;
                            if (currentX > 320)
                                currentX = gap;

                            return positions;
                        }
                    };
                }

                @Override
                public EnemySpawner[] getSpawners() {
                    /*PositionGenerator fixedPosition = new PositionGenerator(){
                        private float currentY = 20f;

                        @Override
                        public List<Vector2> getPositions() {
                            List<Vector2> positions = new ArrayList<>();
                            positions.add(new Vector2(currentY,480f));
                            currentY += 10;
                            if(currentY > 300)
                                currentY = 20f;

                            return positions;
                        }
                    };*/


                    return new EnemySpawner[]{
                            new EnemySpawner(
                                    new SpawnElement[]{
                                            new SpawnElement(createRandomGenerator(true, 1, 45, GameLogic.FRUSTUM_WIDTH - 90),
                                            EnemyFactory.SIMPLE, 5f, 9),
                                            new SpawnElement(createRandomGenerator(true, 1, 10, GameLogic.FRUSTUM_WIDTH - 20),
                                            EnemyFactory.FAST_SIMPLE, 0.35f, 162)
                                    }
                                            //new SpawnElement(createRandomGenerator(true, 1, 45, GameLogic.FRUSTUM_WIDTH - 90),
                                            //EnemyFactory.COVERED, 6, 10)}
                                    //new SpawnElement(createRandomGenerator(true, 2), EnemyFactory.HOMING, 5, 5),
                                    //new SpawnElement(createRandomGenerator(false, 1), EnemyFactory.COMET, 2.5f, 8)}
                            ),
                            new EnemySpawner(
                                    new SpawnElement[]{
                                            new SpawnElement(createGenerator(20f), EnemyFactory.LEVEL_ONE_BOSS, 2, 1)
                                    }
                            )
                    };
                }
            };

}