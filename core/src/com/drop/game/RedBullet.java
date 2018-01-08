package com.drop.game;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by fiszu on 16.09.2017.
 */

public class RedBullet extends Bullet {
    RedBullet()
    {
        power=50;
        sprite = new Sprite(TextureLoader.textures.findRegion("redlaser"));
        forEach();
        prepare();
    }
}
