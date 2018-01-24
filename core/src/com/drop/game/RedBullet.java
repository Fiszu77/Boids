package com.drop.game;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by fiszu on 16.09.2017.
 */

public class RedBullet extends Bullet {
    RedBullet()
    {
        power=500;
        scale = 1.0f;
        sprite = new Sprite(TextureLoader.textures.findRegion("redlaser"));
        forEach();
        prepare();
    }
}
