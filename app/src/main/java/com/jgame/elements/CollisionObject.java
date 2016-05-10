package com.jgame.elements;

import com.jgame.util.Square;
import com.jgame.util.Vector2;

/**
 * Objeto que interactua con otros CollisionObject para desatar eventos en el juego.
 * Created by jose on 21/04/16.
 */
public class CollisionObject extends GameObject {

    public static final int TYPE_ATTACK = 0;
    public static final int TYPE_HITTABLE = 1;
    public static final int TYPE_SMASHED = 2;
    public final Square bounds;
    public int type;

    public CollisionObject(Vector2 relativePosition, int id, float length, float height, GameObject parent, int type) {
        super(relativePosition, id);
        bounds = new Square(position, length, height, 0);
        this.type = type;
        setParent(parent);
    }

    /**
     * Se agrega esta funcion para que en caso de tener una base X negativa, ajustar la posicion en la que se dibuja.
     */
    public void updatePosition(){
        super.updatePosition();
        //Se supone que este tipo de objeto necesita un parent. Esta parte no me gusta nada
        //Esto funciona asumiendo que solo cambiara este vector base
        if(parent.baseX.x < 0)
            position.sub(bounds.lenX, 0);
    }

    /**
     * Checa si el otro enemigo colisiona con el objeto en caso de que sea del tipo TYPE_ATTACK
     * @param other
     */
    public void checkCollision(Character other){
        if(type != TYPE_ATTACK)
            return;

        for(CollisionObject c : other.getActiveCollisionBoxes()){
            if(c.type != TYPE_HITTABLE)
                continue;
            if(bounds.collides(c.bounds))
                c.type = TYPE_SMASHED;
        }
    }
}
