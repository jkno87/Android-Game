package com.jgame.elements;

import android.app.WallpaperInfo;
import android.util.Log;

import com.jgame.game.GameActivity;
import com.jgame.game.GameFlow;
import com.jgame.util.TextureDrawer.TextureData;
import com.jgame.util.TextureDrawer;
import com.jgame.util.TimeCounter;
import com.jgame.util.Vector2;

/**
 * Created by jose on 27/09/16.
 */
public class RobotEnemy extends GameCharacter {

    enum EnemyState {
        WAITING, EXPLODING, ATTACKING, DEAD
    }

    public final static TextureData TELEPORT_TEXTURE = new TextureDrawer.TextureData(0,0.625f,0.125f,0.75f);
    public final static TextureData IDLE_TEXTURE = new TextureDrawer.TextureData(0.5f,0,0.75f,0.25f);
    public final static TextureData STARTUP_ATTACK = new TextureDrawer.TextureData(0.5f,0.25f,0.75f,0.5f);
    public final static TextureData ATTACK_TEXTURE = new TextureDrawer.TextureData(0.5f,0.5f,0.75f,0.75f);
    public final static float DISTANCE_FROM_MAIN_CHARACTER = 150;
    public final static float ATTACK_DISTANCE = 90;
    private final EnemyAction[] actions;
    private final MainCharacter mainCharacter;
    private final TimeCounter timeToSelfDesctruct;
    private EnemyState currentState;
    private float attackRange;


    public RobotEnemy(float spriteSizeX, float spriteSizeY, float idleSizeX, float idleSizeY, float positionY, int id, final MainCharacter mainCharacter) {
        super(spriteSizeX, spriteSizeY, idleSizeX, idleSizeY, new Vector2(0, positionY), id);
        EnemyAction checkAttackDistance = new EnemyAction() {
            @Override
            public void act() {

            }
        };

        actions = new EnemyAction[]{checkAttackDistance};
        this.mainCharacter = mainCharacter;
        timeToSelfDesctruct = new TimeCounter(0);
        attackRange = ATTACK_DISTANCE + idleSizeX;

    }

    @Override
    public void reset(float x, float y) {
        timeToSelfDesctruct.reset();
        currentState = EnemyState.WAITING;
        setPosition(mainCharacter, DISTANCE_FROM_MAIN_CHARACTER);
    }

    @Override
    public boolean hittable() {
        return currentState != EnemyState.WAITING;
    }

    @Override
    public boolean alive() {
        return currentState != EnemyState.DEAD;
    }

    @Override
    public boolean attacking() {
        return currentState == EnemyState.ATTACKING;
    }

    @Override
    public TextureDrawer.TextureData getCurrentTexture() {
        if(currentState == EnemyState.ATTACKING)
            return ATTACK_TEXTURE;
        else
            return STARTUP_ATTACK;
    }

    @Override
    public void hit() {
        currentState = EnemyState.DEAD;
    }

    @Override
    public void update(GameCharacter foe, GameFlow.UpdateInterval interval, GameActivity.WorldData worldData) {
        adjustToFoePosition(foe);
        if(currentState == EnemyState.WAITING) {
            if (position.x > foe.position.x && (position.x - foe.position.x) < attackRange)
                currentState = EnemyState.DEAD;
            else if(position.x < foe.position.x && (position.x - foe.position.x) * -1 < attackRange)
                currentState = EnemyState.DEAD;
        }
    }

}
