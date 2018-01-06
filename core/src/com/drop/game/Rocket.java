package com.drop.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

import static com.drop.game.MainMenuScreen.scl;

/**
 * Created by fiszu on 14.11.2017.
 */

public class Rocket extends Bullet {
    protected int markedOne;
    protected float minDist, dist;
    protected boolean isAnyMarked;
    protected Random random;
    Rocket()
    {
        random = new Random();
        power=random.nextInt(100)+300;
        scale = 2f;
        speed = 40f*scl;
        initSpeed = 30f;
        sprite = new Sprite(TextureLoader.textures.findRegion("rocket"));
        forEach();
        prepare();
    }

    @Override
    public void reset() {
        power=random.nextInt(100)+300;
        super.reset();
    }

    protected void behave(Array<Obstacle> obstacles)
    {
        minDist = 10000000*scl;
        isAnyMarked=false;
        dist = minDist;
        for(int i = 0; i<obstacles.size;i++)
        {

            float angle = new Vector2(obstacles.get(i).getPosition()).sub(position).angle(velocity);

            if (angle < 0) {
                angle += 360.0f;
            }

            if (!(angle >= 90.0f && angle <= 270.0f)) {

                dist = obstacles.get(i).getPosition().dst2(position);
                if (dist < minDist) {
                    //obstacles.get(i).setMarked(true);
                    markedOne = i;
                    minDist = dist;
                    isAnyMarked = true;
                }
            }
                if (i == obstacles.size - 1 && isAnyMarked) {
                    velocity.add(follow(obstacles.get(markedOne).getPosition(), position, velocity));
                }

        }
        velocity.limit2(speed*speed);
    }
    protected Vector2 follow (Vector2 desired, Vector2 location, Vector2 velocity) {
        float d = desired.dst2(location);

        desired.set(desired.sub(location));
        desired.nor();
        desired.scl(speed);
        desired.sub(velocity);
        desired.limit2(0.1f * d);
        desired.limit2(speed*speed*0.0004f);

        return desired;

    }
}
