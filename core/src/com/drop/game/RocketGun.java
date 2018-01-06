package com.drop.game;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by fiszu on 06.01.2018.
 */

public class RocketGun extends Gun {
    protected float delay = 0.5f;
    RocketGun(BulletManager bulletManager)
    {
        super(bulletManager);
    }
    public boolean shoot(Vector2 position, Vector2 velocity, float accumulated)
    {
        if(accumulated>=delay)
        {
            bulletManager.shootRocket(position, velocity);
            return true;
        }
        return false;
    }
}
