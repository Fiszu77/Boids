package com.drop.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import java.util.Random;

/**
 * Created by fiszu on 07.10.2017.
 */

public class Spawner {
    OrthographicCamera camera;
    Random random;
    BulletManager bulletManager;
    Array<Obstacle> obstacles;
    Array<SimpleBoid> boids;
    MotherBoid player;
    public Array<Heart> hearts;
    public final Pool<Obstacle> meteoritesPool = new Pool<Obstacle>() {
        @Override
        protected Obstacle newObject() {
            return new Obstacle(spawner());
        }
    };
    public final Pool<SimpleBoid> simpleBoidPool = new Pool<SimpleBoid>() {
        @Override
        protected SimpleBoid newObject() {
            return new SimpleBoid(0, 0, bulletManager);
        }
    };
    public final Pool<Heart> heartsPool = new Pool<Heart>() {
        @Override
        protected Heart newObject() {
            return new Heart();
        }
    };
    int level = 1, meteorChance = 3, boidChance = 6,heartChance = 7;
    float meteorTimeInterwal = 0.0f, boidTimeInterwal = 0f;

    Spawner(OrthographicCamera camera, Array<Obstacle> obstacles,  Array<SimpleBoid> boids,  MotherBoid player, BulletManager bulletManager) {
        this.player = player;
        this.bulletManager = bulletManager;
        this.boids = boids;
        this.obstacles = obstacles;
        this.camera = camera;
        random = new Random();
        hearts = new Array<Heart>();
    }

    void spawn() {
        meteorTimeInterwal += Gdx.graphics.getDeltaTime();
        if (meteorTimeInterwal >= 2f) {
            if (random.nextInt(meteorChance) == 0) {
                Obstacle meteorite = meteoritesPool.obtain();
                meteorite.init();
                obstacles.add(meteorite);
            }
            meteorTimeInterwal = 0f;
        }

        boidTimeInterwal += Gdx.graphics.getDeltaTime();
        if (boidTimeInterwal >= 3.0f) {
            if (random.nextInt(boidChance) == 0) {
                SimpleBoid boid = simpleBoidPool.obtain();
                boid.init();
                boids.add(boid);
            }
            boidTimeInterwal = 0f;
        }
    }
    void spawnFromMeteor(Vector2 position, Vector2 velocity)
    {
        if(random.nextInt(heartChance) == 0)
        {
            Heart heart = heartsPool.obtain();
            heart.init(position, velocity);
            hearts.add(heart);
        }
    }
    Spawner spawner()
    {
        return this;
    }
}
