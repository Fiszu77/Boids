package com.drop.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

import static com.badlogic.gdx.math.MathUtils.PI;
import static com.badlogic.gdx.math.MathUtils.random;
import static com.drop.game.GameScreen.center;
import static com.drop.game.MainMenuScreen.SCREEN_HEIGHT;
import static com.drop.game.MainMenuScreen.SCREEN_WIDTH;
import static com.drop.game.MainMenuScreen.reverseScl;
import static com.drop.game.MainMenuScreen.scl;

/**
 * Created by fiszu on 22.08.2017.
 */

public class Obstacle implements Pool.Poolable {
    private Vector2 meteorLocation;
    private ShapeRenderer shapeRenderer;
    private java.util.Random random;
    private Vector2 meteorVelocity;
    private boolean isAlive = true, startExp,marked;
    private Polygon collider;
    private HealthBar healthBar;
    Spawner spawner;
    private Rectangle meteorite;
    private Intersector.MinimumTranslationVector mtv;
    private Intersector intersector;
    private Animation<TextureRegion> expAnim;
    private float mass = 240.0f, timePassed, angularVelocity=0f, phi=0f, radius = 0;  //=random(3)-1.5f
    private int hp = 700, maxHp = 700;
    private float[] vertices = new float[22];
    private Sound lilExp, hitMeteor;

    private Sprite sprite;
    Obstacle(Spawner spawner) {
        healthBar = new HealthBar(maxHp, 0.7f, 1.0f, Color.RED);
        random = new java.util.Random();
        mtv = new Intersector.MinimumTranslationVector();
        shapeRenderer = new ShapeRenderer();
        meteorite = new Rectangle();
        meteorite.width = 32 * scl * 8.5f;
        meteorite.height = 32 * scl * 8.5f;
        collider = new Polygon();
        meteorLocation = new Vector2(random(MainMenuScreen.SCREEN_WIDTH), MainMenuScreen.SCREEN_HEIGHT / 2.0f);
        meteorVelocity = new Vector2(random(MainMenuScreen.SCREEN_WIDTH), 0.0f).sub(meteorLocation).scl(0.25f);
        meteorVelocity.nor().scl(1f);
        intersector = new Intersector();
        expAnim = new Animation<TextureRegion>(0.10f, TextureLoader.meteorExpAtlas.getRegions());

        vertices[0] = meteorite.width * 1 / 32;
        vertices[1] = meteorite.height * 13 / 32;
        vertices[2] = meteorite.width * 6 / 32;
        vertices[3] = meteorite.height * 5 / 32;
        vertices[4] = meteorite.width * 13 / 32;
        vertices[5] = meteorite.height * 0 / 32;
        vertices[6] = meteorite.width * 20 / 32;
        vertices[7] = meteorite.height * 0 / 32;
        vertices[8] = meteorite.width * 28 / 32;
        vertices[9] = meteorite.height * 7 / 32;
        vertices[10] = meteorite.width * 32 / 32;
        vertices[11] = meteorite.height * 18 / 32;
        vertices[12] = meteorite.width * 26 / 32;
        vertices[13] = meteorite.height * 28 / 32;
        vertices[14] = meteorite.width * 20 / 32;
        vertices[15] = meteorite.height * 32 / 32;
        vertices[16] = meteorite.width * 14 / 32;
        vertices[17] = meteorite.height * 32 / 32;
        vertices[18] = meteorite.width * 7 / 32;
        vertices[19] = meteorite.height * 27 / 32;
        vertices[20] = meteorite.width * 1 / 32;
        vertices[21] = meteorite.height * 20 / 32;
        collider.setVertices(vertices);
        this.spawner = spawner;
        sprite = new Sprite(TextureLoader.textures.findRegion("meteorite"));
        sprite.setScale(scl*8.5f);
        radius = sprite.getWidth()/2;
        lilExp = TextureLoader.lilExp;
        hitMeteor = TextureLoader.hitMeteor;

    }

    public void init() {
        isAlive = true;
        hp = maxHp;
        marked = false;
        angularVelocity =0;
        phi=0;
        healthBar = new HealthBar(maxHp, 0.7f, 2.0f, Color.RED);
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
       // meteorLocation.set(xdir*SCREEN_WIDTH,ydir*SCREEN_HEIGHT);
        meteorLocation = new Vector2(xdir * (float) java.lang.Math.sqrt(1.0 / (1.0 + java.lang.Math.tan(angle) * java.lang.Math.tan(angle))) * SCREEN_WIDTH / 1.5f, ydir * (float) java.lang.Math.sqrt(1.0 / (1.0 + 1.0 / (java.lang.Math.tan(angle) * java.lang.Math.tan(angle)))) * SCREEN_WIDTH / 1.5f);
        meteorLocation.add(center);

        timePassed = 0;
        startExp = false;
        //meteorLocation = new Vector2(random(SCREEN_WIDTH),SCREEN_HEIGHT+meteorite.height);
        // meteorLocation = new Vector2(100.0f,100.0f);
        meteorVelocity = new Vector2(center).add(new Vector2(random(500.0f * scl) - 250.0f, random(500.0f * scl) - 250.0f)).sub(meteorLocation);
        //meteorVelocity = new Vector2(0.01f,0.01f);
        meteorVelocity.nor().scl((2000f / mass) * scl);//(((random(2000.0f)+2000.0f)/mass)*scl);
        vertices[0] = meteorite.width * 1 / 32;
        vertices[1] = meteorite.height * 13 / 32;
        vertices[2] = meteorite.width * 6 / 32;
        vertices[3] = meteorite.height * 5 / 32;
        vertices[4] = meteorite.width * 13 / 32;
        vertices[5] = meteorite.height * 0 / 32;
        vertices[6] = meteorite.width * 20 / 32;
        vertices[7] = meteorite.height * 0 / 32;
        vertices[8] = meteorite.width * 28 / 32;
        vertices[9] = meteorite.height * 7 / 32;
        vertices[10] = meteorite.width * 32 / 32;
        vertices[11] = meteorite.height * 18 / 32;
        vertices[12] = meteorite.width * 26 / 32;
        vertices[13] = meteorite.height * 28 / 32;
        vertices[14] = meteorite.width * 20 / 32;
        vertices[15] = meteorite.height * 32 / 32;
        vertices[16] = meteorite.width * 14 / 32;
        vertices[17] = meteorite.height * 32 / 32;
        vertices[18] = meteorite.width * 7 / 32;
        vertices[19] = meteorite.height * 27 / 32;
        vertices[20] = meteorite.width * 1 / 32;
        vertices[21] = meteorite.height * 20 / 32;
        collider.setVertices(vertices);
    }

    public void dispose() {

    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }
    public boolean getMarked()
    {
        return marked;
    }
    public void debug(OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.polygon(collider.getTransformedVertices());
        shapeRenderer.end();
    }

    public Vector2 getVelocity() {
        return new Vector2().set(meteorVelocity);
    }

    public Polygon getPolygon() {
        return collider;
    }

    public boolean collide(Obstacle menace) {
        if (Intersector.overlapConvexPolygons(collider, menace.getPolygon(), mtv)) {
            Physics.collideWithObstacle(this, menace, mtv);
            return true;
        } else
            return false;
    }

    public void setPosition(Vector2 newPosition) {
        meteorLocation.set(newPosition);
    }

    public Obstacle getMeteor() {
        return this;
    }

    public float getMass() {
        return this.mass;
    }

    public float getAngularVelocity(){return this.angularVelocity;}

    public Vector2 getPosition() {
        return new Vector2(meteorLocation);
    }

    public void setVelocity(Vector2 newVelocity) {
        meteorVelocity.set(newVelocity);
    }

    public void addAngVel(float angularVelocity)
    {
        this.angularVelocity += (angularVelocity*180)/PI;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public float getRadius(){return radius;}

    public void hit(int damage) {
        if(hp>0) {
            hp -= damage;
            healthBar.changeHp(hp);
            hitMeteor.play(0.5f);
        }
    }

    public boolean isStartExp()
    {
        return startExp;
    }

    public String type() {
        return "Meteorite";
    }

    public void move(Particles particles) {
        if (meteorLocation.x > SCREEN_WIDTH * 2f + center.x)
            isAlive = false;
        if (meteorLocation.x < center.x - SCREEN_WIDTH * 2f)
            isAlive = false;
        if (meteorLocation.y > center.y + SCREEN_HEIGHT * 2f)
            isAlive = false;
        if (meteorLocation.y < center.y - SCREEN_HEIGHT * 2f)
            isAlive = false;
        if (hp <= 0) {
            if(!startExp)
            {
                lilExp.play(1.0f);
                spawner.spawnFromMeteor(new Vector2(meteorLocation), new Vector2(meteorVelocity));
                particles.addMeteorExp(sprite.getX()+sprite.getWidth()/2,sprite.getY()+sprite.getHeight()/2,meteorVelocity);
            }
            startExp = true;

        }
        phi+=angularVelocity;
        while(phi>=360)
        {
            phi=phi-360;
        }
        meteorLocation.add(new Vector2().set(meteorVelocity));
        collider.setPosition(meteorLocation.x - meteorite.width / 2, meteorLocation.y - meteorite.height / 2);
        healthBar.logic(hp, getPosition());
    }

    public void show(Batch batch) {
        if (!startExp) {
            //batch.draw(TextureLoader.meteoriteTex, meteorLocation.x - meteorite.width / 2, meteorLocation.y - meteorite.height / 2, meteorite.width, meteorite.height);
            sprite.setPosition(meteorLocation.x - sprite.getWidth() / 2, meteorLocation.y - sprite.getHeight() / 2);
            sprite.setRotation(phi-90);
            sprite.draw(batch);
        }
        else {
            if (!expAnim.isAnimationFinished(timePassed)) {
                batch.draw(expAnim.getKeyFrame(timePassed, true), meteorLocation.x - meteorite.width / 2, meteorLocation.y - meteorite.height / 2, meteorite.width / 2, meteorite.height / 2, meteorite.width, meteorite.height, 1.0f, 1.0f, 0);
                timePassed += Gdx.graphics.getDeltaTime();
            }
            else
                isAlive = false;

        }
    }

    public void renderShapes(ShapeRenderer shapeRenderer) {
        if(!startExp)
        healthBar.render(shapeRenderer);
    }

    public void reset() {


    }
}
