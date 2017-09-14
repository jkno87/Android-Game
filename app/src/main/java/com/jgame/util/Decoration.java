package com.jgame.util;

import com.jgame.elements.AnimationData;

public abstract class Decoration {

    public static class StaticDecoration extends Decoration {
        public final TextureDrawer.TextureData sprite;
        public int preDrawFrames;
        public int drawFrames;
        private final int initialDrawFrames;
        private final boolean fade;
        public float shrinkRateX = 0.0f;

        public StaticDecoration(TextureDrawer.TextureData sprite, Square size, SimpleDrawer.ColorData color, boolean inverted, int preDrawFrames, int drawFrames, boolean fade){
            this.sprite = sprite;
            this.size = size;
            this.color = color;
            this.inverted = inverted;
            this.preDrawFrames = preDrawFrames;
            this.drawFrames = drawFrames;
            initialDrawFrames = drawFrames;
            this.fade = fade;
        }

        public StaticDecoration(TextureDrawer.TextureData sprite, Square size, boolean inverted, int preDrawFrames, int drawFrames, boolean fade){
            this.sprite = sprite;
            this.size = size;
            this.inverted = inverted;
            this.preDrawFrames = preDrawFrames;
            this.drawFrames = drawFrames;
            initialDrawFrames = drawFrames;
            this.fade = fade;
        }

        @Override
        public boolean completed(){
            return drawFrames < 0;
        }

        @Override
        public TextureDrawer.TextureData getSprite(){
            return sprite;
        }

        @Override
        public void update() {
            if(preDrawFrames > 0)
                preDrawFrames--;
            else {
                drawFrames--;
                if(fade)
                    color.a = color.a * ((float) drawFrames) / initialDrawFrames;

                if(shrinkRateX > 0.0) {
                    size.scaleX(shrinkRateX);
                }

            }

        }

        @Override
        public boolean drawable(){
            return preDrawFrames <= 0 && drawFrames > 0;
        }
    }

    public static abstract class AnimatedDecoration extends Decoration {
        public AnimationData animation;
        public int preDrawFrames;

        public AnimatedDecoration(AnimationData animation, Square size, SimpleDrawer.ColorData color, boolean inverted){
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

        public boolean completed(){
            return animation.completed();
        }

        public synchronized TextureDrawer.TextureData getSprite(){
            return animation.getCurrentSprite();
        }

        public boolean drawable(){
            return preDrawFrames <= 0 && !animation.completed();
        }
    }

    public boolean inverted;
    public Square size;
    public SimpleDrawer.ColorData color = TextureDrawer.DEFAULT_COLOR;
    public abstract boolean drawable();
    public abstract void update();
    public abstract boolean completed();
    public abstract TextureDrawer.TextureData getSprite();
}