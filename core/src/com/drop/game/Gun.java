package com.drop.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;



/**
 * Created by fiszu on 26.11.2017.
 */

public class Gun {
    protected float delay = 0.18f;
    protected BulletManager bulletManager;

    Gun(BulletManager bulletManager)
    {
        this.bulletManager = bulletManager;
    }
    public boolean shoot(Vector2 position, Vector2 velocity, float accumulated)
    {
        if(accumulated>=delay)
        {
            bulletManager.shootGreenBullet(position, velocity);
            return true;
        }
        return false;
    }
    public boolean hasAmmo()
    {
        return true;
    }
}
