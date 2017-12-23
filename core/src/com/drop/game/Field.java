package com.drop.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

import static com.drop.game.MainMenuScreen.SCREEN_HEIGHT;
import static com.drop.game.MainMenuScreen.SCREEN_WIDTH;

/**
 * Created by fiszu on 30.09.2017.
 */

public class Field implements Pool.Poolable{
    int offsetX=0, offsetY = 0;
    Texture background;

    Field(int offsetX, int offsetY)
    {
        background = TextureLoader.starsBack;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
    void init(int offsetX, int offsetY)
    {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
    void logic(){

    }
    void show(Batch batch)
    {
        batch.draw(background,offsetX*SCREEN_WIDTH,offsetY*SCREEN_HEIGHT,SCREEN_WIDTH,SCREEN_HEIGHT);
    }
    @Override
    public void reset() {

    }
}
