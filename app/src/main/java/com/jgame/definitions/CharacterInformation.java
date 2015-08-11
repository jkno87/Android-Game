package com.jgame.definitions;

import com.jgame.characters.Attack;

/**
 * Created by jose on 11/08/15.
 */
public abstract class CharacterInformation {
    public final float size;
    public final int stamina;
    public final int hp;
    public final int id;
    public final float[] textureInfo;

    public CharacterInformation(float size, int stamina, int hp, int id, float[] textureInfo){
        this.size = size;
        this.stamina = stamina;
        this.hp = hp;
        this.id = id;
        this.textureInfo = textureInfo;
    }

    public abstract Attack getPrimaryAttack();
    public abstract Attack getSecondaryAttack();

}
