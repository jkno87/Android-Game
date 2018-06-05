package com.jgame.util;

import com.jgame.elements.AnimationData;

public abstract class Decoration {

    public boolean inverted;
    public Square size;
    public Drawer.ColorData color = Drawer.DEFAULT_COLOR;
    public abstract void terminate();
    public abstract boolean drawable();
    public abstract void update(Vector2 backgroundMoveDelta);
    public abstract boolean completed();
    public abstract Drawer.TextureData getSprite();


    public static class BoundedDecoration extends Decoration {
        private boolean finished;
        private final Drawer.TextureData sprite;

        public BoundedDecoration (Square size, Vector2 position, Drawer.TextureData sprite){
            this.size = size;
            this.size.position = position;
            this.sprite = sprite;
        }

        @Override
        public void terminate() {
            finished = true;
        }

        @Override
        public boolean drawable() {
            return !finished;
        }

        @Override
        public boolean completed() {
            return finished;
        }

        @Override
        public void update(Vector2 backgroundMoveDelta){

        }

        @Override
        public Drawer.TextureData getSprite(){
            return sprite;
        }

        public void reset(){
            finished = false;
        }

    }

    /*public static class StaticDecoration extends Decoration {
        public final TextureDrawer.TextureData sprite;
        public int preDrawFrames;
        public int drawFrames;
        private float framesToChangeColor;
        public float shrinkRateX = 0.0f;

        public StaticDecoration(TextureDrawer.TextureData sprite, Square size, boolean inverted, int preDrawFrames, int drawFrames, TextureDrawer.ColorData color,
                                float sizeDelta, float framesToChangeColor){
            this.sprite = sprite;
            this.size = size;
            this.inverted = inverted;
            this.color = color;
            this.preDrawFrames = preDrawFrames;
            this.drawFrames = drawFrames;
            this.shrinkRateX = 1.005f;
            this.framesToChangeColor = framesToChangeColor;
        }

        @Override
        public void terminate(){
            drawFrames = 0;
        }

        @Override
        public boolean completed(){
            return drawFrames == 0;
        }

        @Override
        public TextureDrawer.TextureData getSprite(){
            return sprite;
        }

        @Override
        public void update(Vector2 backgroundMoveDelta) {
            if(preDrawFrames > 0)
                preDrawFrames--;
            else {
                drawFrames--;

                if(framesToChangeColor == 0){
                    color.b = 0;
                    color.r = 1;
                    size.scaleX(shrinkRateX);
                    return;
                } else
                    framesToChangeColor--;
*/
                /*if(shrinkRateX > 0.0) {
                    size.scaleX(shrinkRateX);
                }*/

/*            }

        }

        @Override
        public boolean drawable(){
            return preDrawFrames <= 0 && drawFrames > 0;
        }
    }*/

    public static class AnimatedDecoration extends Decoration {
        public AnimationData animation;
        public int preDrawFrames;

        public AnimatedDecoration(AnimationData animation, Square size, Drawer.ColorData color, boolean inverted){
            this.animation = animation;
            this.size = size;
            this.color = color;
            this.inverted = inverted;
        }

        public AnimatedDecoration(int preDrawFrames, AnimationData animation, Square size, boolean inverted){
            this.animation = animation;
            this.size = size;
            this.inverted = inverted;
            this.preDrawFrames = preDrawFrames;
        }

        public void terminate(){
        }

        public boolean completed(){
            return animation.completed();
        }

        public synchronized Drawer.TextureData getSprite(){
            return animation.getCurrentSprite();
        }

        public boolean drawable(){
            return preDrawFrames <= 0 && !animation.completed();
        }

        public void update(Vector2 backgroundMoveDelta){
            if(preDrawFrames > 0)
                preDrawFrames--;
            else
                animation.updateFrame();
        }
    }

    public static class TransitionDecoration extends Decoration {
        private Drawer.TextureData sprite;

        public TransitionDecoration(Drawer.TextureData sprite, Square size, boolean inverted){
            this.sprite = sprite;
            this.size = size;
            this.inverted = inverted;
        }

        public TransitionDecoration(Drawer.TextureData sprite, Square size, boolean inverted, Drawer.ColorData color){
            this.sprite = sprite;
            this.size = size;
            this.inverted = inverted;
            this.color = color;
        }

        @Override
        public void terminate(){
            size.position.x = -10;
        }

        @Override
        public boolean completed(){
            return size.position.x < 0;
        }

        @Override
        public boolean drawable(){
            return true;
        }

        @Override
        public Drawer.TextureData getSprite(){
            return sprite;
        }

        @Override
        public void update(Vector2 backgroundMoveDelta){
            size.position.add(backgroundMoveDelta);
        }

    }
}