package com.drop.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import static com.drop.game.MainMenuScreen.scl;

/**
 * Created by fiszu on 15.09.2017.
 */

public class BulletManager {
    public int bulletsAlive=0;
    private static Array<Bullet> redBullets;
    private static Array<Bullet> greenBullets;
    private static Array<Bullet> rockets;
    private static float gbSpeed = 30.0f * scl;
    private int len = 0;
    private static Pool<Bullet> basicBulletsPool;
    private static Pool<Bullet> greenBulletsPool;
    private static Pool<Bullet> rocketsPool;
    Bullet temp;

    BulletManager() {
        basicBulletsPool = new Pool<Bullet>() {
            @Override
            protected Bullet newObject() {
                return new RedBullet();
            }
        };
        redBullets = new Array<Bullet>();
        greenBulletsPool = new Pool<Bullet>() {
            @Override
            protected Bullet newObject() {
                return new Bullet();
            }
        };
        greenBullets = new Array<Bullet>();
        rocketsPool = new Pool<Bullet>() {
            @Override
            protected Bullet newObject() {
                return new Rocket();
            }
        };
        rockets = new Array<Bullet>();
    }

    public void bulletsLogic(Array<Obstacle> obstacles) {
        for (Bullet bullet : redBullets) {
            bullet.move(obstacles);

        }
        for (Bullet bullet : greenBullets) {
            bullet.move(obstacles);

        }
        for (Bullet bullet : rockets) {
            bullet.move(obstacles);

        }
        checkDeaths();
        bulletsAlive = redBullets.size+greenBullets.size;
    }

    public void renderBullets(Batch batch) {
        for (Bullet bullet : redBullets) {
            bullet.show(batch);
        }
        for (Bullet bullet : greenBullets) {
            bullet.show(batch);
        }
        for (Bullet bullet : rockets) {
            bullet.show(batch);
        }
    }

    public void shootGreenBullet(Vector2 position, Vector2 velocity) {
        velocity.nor();
        temp = greenBulletsPool.obtain();
        temp.init(position, velocity);
        greenBullets.add(temp);
    }
    public void shootRedBullet(Vector2 position, Vector2 velocity) {
        velocity.nor();
        temp = basicBulletsPool.obtain();
        temp.init(position, velocity);
        redBullets.add(temp);
    }
    public void shootRocket(Vector2 position, Vector2 velocity) {
        velocity.nor();
        temp = rocketsPool.obtain();
        temp.init(position, velocity);
        rockets.add(temp);
    }

    private void checkDeaths() {
        len = redBullets.size;
        for (int i = 0; i < len; i++) {
            if (!redBullets.get(i).isAlive()) {
                basicBulletsPool.free(redBullets.get(i));
                redBullets.removeIndex(i);
                len--;
            }
        }
        len = greenBullets.size;
        for (int i = 0; i < len; i++) {
            if (!greenBullets.get(i).isAlive()) {
                greenBulletsPool.free(greenBullets.get(i));
                greenBullets.removeIndex(i);
                len--;
            }
        }
        len = rockets.size;
        for (int i = 0; i < len; i++) {
            if (!rockets.get(i).isAlive()) {
                rocketsPool.free(rockets.get(i));
                rockets.removeIndex(i);
                len--;
            }
        }
    }
}
