package com.drop.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import static com.badlogic.gdx.math.MathUtils.PI;
import static com.badlogic.gdx.math.MathUtils.random;
import static com.drop.game.Controls.shoot;
import static com.drop.game.GameScreen.center;
import static com.drop.game.LevelManager.isInBorders;
import static com.drop.game.LevelManager.keepWithinBorders;
import static com.drop.game.MainMenuScreen.SCREEN_HEIGHT;
import static com.drop.game.MainMenuScreen.SCREEN_WIDTH;
import static com.drop.game.MainMenuScreen.scl;

/**
 * Created by fiszu on 07.08.2017.
 */

public class
SimpleBoid implements Pool.Poolable {
    private final Intersector intersector;
    private ShapeRenderer shapeRenderer;
    private Rectangle boid;
    private int hp = 100, maxHp = 100;
    private BulletManager bullets;
    private Vector2 position, boidsVelocity, collisionVelocity, contrCollisionVelocity, boidsRotation;
    private float degrees, accumulated = 0, period = 0.3f;
    private float cohScl, alScl, sepScl, boostedSepScl, gunSclWidth = 0.39f, gunSclHeight = 0.15f;
    private Behaviour behaviour;
    private Animation<TextureRegion> propAnim, expAnim;
    private float timePassed;
    private float[] vertices = new float[6];
    private Intersector.MinimumTranslationVector mtv;
    private Physics physics;
    private Polygon collider;
    private HealthBar healthBar;
    private boolean startExp = false, isAlive = true, autoShoot, noObstacles = false;
    private float mass = 40.0f;
    private boolean entered = false;


    private Sprite sprite;

    SimpleBoid(float startX, float startY, BulletManager bullets) {
        this.bullets = bullets;
        mtv = new Intersector.MinimumTranslationVector();
        shapeRenderer = new ShapeRenderer();
        collider = new Polygon();
        boid = new Rectangle();
        physics = new Physics();
        boidsRotation = new Vector2(0, 1);
        boid.height = 16;
        boid.width = 16;
        //boidsVelocity = new Vector2(random(10.0f)-5.0f,random(10.0f)-5.0f);
        boidsVelocity = new Vector2();
        //boidsLocation = new Vector2(random(Gdx.graphics.getWidth()),random(Gdx.graphics.getHeight()));
        position = new Vector2(startX, startY);
        boid.x = position.x;
        boid.y = position.y;
        behaviour = new Behaviour();
        cohScl = 24.0f;
        alScl = 22.0f;
        sepScl = 54.0f;
        boostedSepScl = sepScl * 5.8f;
        contrCollisionVelocity = new Vector2();
        collisionVelocity = new Vector2();
        boid.width = boid.width * scl * 8.5f;
        boid.height = boid.height * scl * 8.5f;
        propAnim = new Animation<TextureRegion>(0.25f, TextureLoader.lilPropAtlas.getRegions());
        expAnim = new Animation<TextureRegion>(0.085f, TextureLoader.lilExpAtlas.getRegions());
        intersector = new Intersector();
        timePassed = 0.0f;
        healthBar = new HealthBar(maxHp, 0.5f, 1.0f, Color.GREEN);

        sprite = new Sprite(TextureLoader.textures.findRegion("chaser2"));
        sprite.setScale(scl*8.5f);
    }


    public SimpleBoid init() {
        boidsVelocity = new Vector2(0.1f, 0);
        float angle = random(2 * PI);
        float xdir;
        if (random.nextBoolean())
            xdir = 1.0f;
        else
            xdir = -1.0f;
        float ydir;
        if (random.nextBoolean())
            ydir = 1.0f;
        else
            ydir = -1.0f;
        position = new Vector2(xdir * (float) java.lang.Math.sqrt(1.0 / (1.0 + java.lang.Math.tan(angle) * java.lang.Math.tan(angle))) * SCREEN_WIDTH / 1.7f, ydir * (float) java.lang.Math.sqrt(1.0 / (1.0 + 1.0 / (java.lang.Math.tan(angle) * java.lang.Math.tan(angle)))) * SCREEN_WIDTH / 1.7f);
        position.add(center);
        boid.x = position.x;
        boid.y = position.y;
        boidsVelocity = new Vector2(center).sub(position);
        boidsVelocity.nor().scl(((random(100.0f) + 200.0f) / mass) * scl);
        vertices[0] = 0;
        vertices[1] = 0;
        vertices[2] = boid.width ;
        vertices[3] = 0;
        vertices[4] = boid.width * 0.5f;
        vertices[5] = boid.height ;


        collider.setVertices(vertices);
        collider.setOrigin(boid.width * 0.75f / 2, boid.height * 10.0f / 32);
        timePassed = 0.0f;
        startExp = false;
        isAlive = true;
        hp = maxHp;
        healthBar = new HealthBar(maxHp, 0.5f, 1.0f, Color.GREEN);
        entered = false;
        return this;
    }


    public String type() {
        return "SimpleBoid";
    }


    public SimpleBoid getSimple() {
        return this;
    }


    public MotherBoid getMother() {
        return null;
    }


    public void setCohScl(float cohScl) {
        this.cohScl = cohScl;
    }

    public void setAlScl(float alScl) {
        this.alScl = alScl;
    }

    public void setSepScl(float sepScl) {
        this.sepScl = sepScl;
    }


    public void noObstacles(boolean noObstacles) {
        this.noObstacles = noObstacles;

    }


    public void addVelocity(Vector2 velocity) {
        boidsVelocity.add(velocity.limit(10.0f).scl(scl).scl(1.0f / mass));
    }


    public void addVelocity(float x, float y) {
        Vector2 acc = new Vector2(x, y);
        acc.scl(0.013f);
        boidsVelocity.add(acc.limit(10.0f));
    }


    public void setVelocity(Vector2 newVelocity) {
        collisionVelocity.set(newVelocity);
        contrCollisionVelocity = new Vector2().set(collisionVelocity).scl(-0.01f);

    }


    public void setPosition(Vector2 newPosition) {
        position.set(newPosition);
    }


    public void move() {

        accumulated += Gdx.graphics.getDeltaTime();

        if(!entered)
        {
            if(isInBorders(position))
            {
                entered = true;
            }
        }
        else
        {
            keepWithinBorders(position,sprite);
        }
        if (autoShoot && accumulated >= period)//&&!noObstacles)
        {
            boidsRotation.nor();
            Vector2 tangent = new Vector2(boidsRotation.y, -boidsRotation.x);
            bullets.shootGreenBullet(new Vector2(position).add(new Vector2(boidsRotation).scl(boid.height * gunSclHeight).add(tangent.scl(boid.width * gunSclWidth))), new Vector2(boidsRotation));
            gunSclWidth = -gunSclWidth;
            accumulated = 0;
        }

        if (!startExp && hp <= 0) {
            kill();
        }

        position.add(collisionVelocity);
        if (collisionVelocity.len2() > 0.0014f) {
            collisionVelocity.add(contrCollisionVelocity);
            boidsVelocity.set(0, 0);
        } else {
            collisionVelocity.set(0.0f, 0.0f);
            position.add(new Vector2(boidsVelocity));
        }
        boid.x = position.x;
        boid.y = position.y;


        collider.setPosition(boid.x - boid.width * 3.0f / 8.0f, boid.y - boid.height * 5.0f / 16.0f);
        collider.setRotation(degrees - 90.0f);
        if (boidsVelocity.len2() < 0.0005f) {
            boidsVelocity.set(0.0f, 0.0f);
        } else {
            boidsRotation.set(boidsVelocity);
        }
        autoShoot = false;
        healthBar.logic(hp, getLocationVector());
    }


    public float getMass() {
        return this.mass;
    }


    public Vector2 getLocationVector() {
        return new Vector2().set(position);
    }


    public Vector2 getVelocityVector() {
        return new Vector2().set(boidsVelocity);
    }


    public void show(Batch batch) {

        if (position.x > SCREEN_WIDTH * 2f + center.x)
            isAlive = false;
        if (position.x < center.x - SCREEN_WIDTH * 2f)
            isAlive = false;
        if (position.y > center.y + SCREEN_HEIGHT * 2f)
            isAlive = false;
        if (position.y < center.y - SCREEN_HEIGHT * 2f)
            isAlive = false;

        propAnim.setFrameDuration(1.0f / getVelocityVector().len2());
        if (boidsVelocity.len2() > 0) {
            degrees = (((float) Math.atan2((double) boidsRotation.y, boidsRotation.x)) * 180.0f) / PI;
            //System.out.println("przed"+degrees);
            if (degrees < 0) {
                degrees += 360;
            }
        }
        //System.out.println(degrees);
        timePassed += Gdx.graphics.getDeltaTime();
        if (!startExp) {
            batch.draw(propAnim.getKeyFrame(timePassed, true), boid.x - boid.width / 2, boid.y - boid.height * 1.32f, boid.width / 2, boid.height * 1.32f, boid.width, boid.height, 1.0f, 1.0f, degrees - 90.0f);
            sprite.setRotation(degrees-90f);
            sprite.setPosition(position.x-sprite.getWidth()/2,position.y-sprite.getHeight()/2);
            sprite.draw(batch);
        } else {
            if (!expAnim.isAnimationFinished(timePassed)) {
                batch.draw(expAnim.getKeyFrame(timePassed, true), boid.x - boid.width / 2, boid.y - boid.height / 2, boid.width / 2, boid.height / 2, boid.width, boid.height, 1.0f, 1.0f, degrees - 90.0f);
                boidsVelocity.limit(2.0f);
            } else
                isAlive = false;
        }
    }


    public Vector2 getRotation() {
        return new Vector2(boidsRotation);
    }


    public void damage(int power) {
        hp -= power;
        if (hp < 0)
            hp = 0;
        healthBar.changeHp(hp);
    }


    public void dispose() {
    }


    public void applyRules(Array<SimpleBoid> boids, MotherBoid player) {
        this.addVelocity(behaviour.cohesion(boids, this).scl(cohScl));
        this.addVelocity(behaviour.alignment(boids, this).scl(alScl));
        float dist = behaviour.getDistToPlayer();
        if (player.isAlive()) {
            if (player.getVelocityVector().len() > scl) {
                if (dist < behaviour.getCohR() * 2f) {
                    this.addVelocity(behaviour.separation(boids, this,1.3f).scl(boostedSepScl));
                } else
                    this.addVelocity(behaviour.separation(boids, this,1.0f).scl(sepScl));
                this.addVelocity(behaviour.cohTowPlayer(player, this).scl(cohScl * 7.0f));
                this.addVelocity(behaviour.alTowPlayer(player, this).scl(alScl * 4.0f));
                this.addVelocity(behaviour.sepAgaPlayer(player, this).scl(sepScl * 4.0f));
            }
        }
        else
            this.addVelocity(behaviour.separation(boids, this,1.0f).scl(sepScl));
        //else if(dist < behaviour.getCohR() * 2.0f)
        {
            //boidsVelocity.set(0.0f,0.0f);
        }

    }


    public Vector2 getCollisionVelocity() {
        return new Vector2().set(collisionVelocity);
    }


    public void follow(Vector2 desired) {
        this.addVelocity(behaviour.follow(desired, this));
    }


    public boolean isAlive() {
        return isAlive;
    }


    public void passPoint(Vector2 point) {

    }


    public void passPolygon(Polygon polygon) {
        if (intersector.overlapConvexPolygons(polygon, collider)) {
            kill();

        }
    }


    public void passCamera(Camera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.polygon(collider.getTransformedVertices());
        shapeRenderer.end();
    }


    public boolean isColliding(Array<Obstacle> obstacles) {


        for (Obstacle obstacle : obstacles) {
            float angle = new Vector2(obstacle.getPosition()).sub(position).angle(boidsRotation);

            if (angle < 0) {
                angle += 360.0f;
            }

            if (!(angle >= 20.0f && angle <= 340.0f)) {
                autoShoot = true;
            }
            if (startExp)
                autoShoot = false;

            if (intersector.overlapConvexPolygons(collider, obstacle.getPolygon(), mtv)) {
                physics.collideWithObstacle(this, obstacle, mtv);
                return true;
            }
        }

        return false;
    }


    public void kill() {
        startExp = true;
        timePassed = 0.0f;
    }

    public void renderShape(ShapeRenderer shapeRenderer) {
        healthBar.render(shapeRenderer);
    }

    public void reset() {

    }
}
