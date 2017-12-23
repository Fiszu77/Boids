package Interfaces;


import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by fiszu on 03.11.2017.
 */

public interface Collideable {
    Polygon getCollider();
    Sprite getSprite();
    Vector2 position();
}
