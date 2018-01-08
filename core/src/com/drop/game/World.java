package com.drop.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

import static com.drop.game.MainMenuScreen.SCREEN_HEIGHT;
import static com.drop.game.MainMenuScreen.SCREEN_WIDTH;

/**
 * Created by fiszu on 30.09.2017.
 */

public class World {
    OrthographicCamera camera;
    public int offsetX=0, offsetY=0;
    Array<Field> fields = new Array<Field>();
    World(OrthographicCamera camera){
        this.camera = camera;
        fields.add(new Field(-1,1));
        fields.add(new Field(-1,0));
        fields.add(new Field(-1,-1));
        fields.add(new Field(0,1));
        fields.add(new Field(0,0));
        fields.add(new Field(0,-1));
        fields.add(new Field(1,1));
        fields.add(new Field(1,0));
        fields.add(new Field(1,-1));
    }
    void logic()
    {
        if(camera.position.x<offsetX*SCREEN_WIDTH)
        {
            offsetX--;
            for(Field field : fields)
            {
                field.offsetX--;

            }
        }
        if(camera.position.x>(offsetX+1)*SCREEN_WIDTH)
        {
            offsetX++;
            for(Field field : fields)
            {
                field.offsetX++;
            }
        }
        if(camera.position.y<offsetY*SCREEN_HEIGHT)
        {
            offsetY--;
            for(Field field : fields)
            {
                field.offsetY--;
            }
        }
        if(camera.position.y>(offsetY+1)*SCREEN_HEIGHT)
        {
            offsetY++;
            for(Field field : fields)
            {
                field.offsetY++;
            }
        }
    }
    void show(Batch batch)
    {
        for(Field field : fields)
        {
            field.show(batch);
        }
    }
}
