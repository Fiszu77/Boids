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
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.Pool;

import Interfaces.Collideable;
import Interfaces.Healable;

import static com.badlogic.gdx.Gdx.input;
import static com.badlogic.gdx.Input.Keys.SPACE;
import static com.badlogic.gdx.math.MathUtils.PI;
import static com.drop.game.MainMenuScreen.scl;


/**
 * Created by fiszu on 010.08.2017.
 */

public class MotherBoid implements Healable{
    private Controls controls;
    private HealthBar healthBar;
    private Array<Gun> guns;
    private Rectangle boid, boidsCollision;
    private Vector2 position, velocity, contrCollisionVelocity, collisionVelocity, boidsRotationVector
            ,tangent;
    private float degrees, gunSclWidth = 0.3f, gunSclHeight = 0.5f;
    private float cohScl, alScl, sepScl;
    private float[] vertices=new float[8];
    private Behaviour behavoiur;
    private TextureAtlas propAtlas,expAtlas;
    private Animation<TextureRegion> propAnim,expAnim;
    private float timePassed, mass = 60.0f,  accumulated = 0, period = 0.18f;
    private ShapeRenderer shapeRenderer;
    private Polygon collider;
    private int hp = 200, maxHp=200, choosenGun = 0;
    private Physics physics;
    private static BulletManager bulletManager;
    private Intersector.MinimumTranslationVector mtv;
    private Intersector intersector;
    private boolean startExp = false, isAlive = true, autoShoot;

    private Sprite sprite;

    MotherBoid(float startX, float startY, BulletManager bullets,Controls controls)
    {
        this.controls = controls;
        bulletManager = bullets;
        mtv = new Intersector.MinimumTranslationVector();
        boid = new Rectangle();
        boidsCollision = new Rectangle();
        boid.height = 16;
        boidsRotationVector = new Vector2(0,1);
        boid.width = 16;
        //velocity = new Vector2(random(10.0f)-5.0f,random(10.0f)-5.0f);
        velocity = new Vector2();
        //position = new Vector2(random(Gdx.graphics.getWidth()),random(Gdx.graphics.getHeight()));
        position = new Vector2(startX,startY);
        contrCollisionVelocity = new Vector2();
        collisionVelocity = new Vector2();
        boid.x = position.x;
        boid.y = position.y;
        behavoiur = new Behaviour();
        cohScl = 24.0f;
        alScl = 22.0f;
        sepScl = 54.0f;
        boid.width=boid.width* scl*8.5f;
        boid.height=boid.height*scl*8.5f;
        boidsCollision.width = boid.width*0.6f;
        boidsCollision.height = boid.height;
        vertices[0]=boidsCollision.x;
        vertices[1]=boidsCollision.y;
        vertices[2]=boidsCollision.x+boidsCollision.width;
        vertices[3]=boidsCollision.y;
        vertices[4]=boidsCollision.x+boidsCollision.width;
        vertices[5]=boidsCollision.y+boidsCollision.height;
        vertices[6]=boidsCollision.x;
        vertices[7]=boidsCollision.y+boidsCollision.height;
        healthBar = new HealthBar(maxHp,0.7f,1.0f, Color.GREEN);
        collider = new Polygon();
        collider.setVertices(vertices);
        propAtlas = new TextureAtlas(Gdx.files.internal("prop.atlas"));
        propAnim = new Animation<TextureRegion>(0.25f,propAtlas.getRegions());
        timePassed = 0.0f;
        expAtlas = new TextureAtlas(Gdx.files.internal("explosion2.atlas"));
        expAnim = new Animation<TextureRegion>(0.085f,expAtlas.getRegions());
        shapeRenderer = new ShapeRenderer();
        intersector = new Intersector();
        collider.setVertices(vertices);
        collider.setOrigin(boidsCollision.width/2,boidsCollision.height/2);
        guns = new Array<Gun>();
        //guns.add(new RocketGun(bulletManager));
        guns.add(new Gun(bulletManager));
        sprite = new Sprite(TextureLoader.textures.findRegion("spaceship3"));
        sprite.setScale(scl*8.5f);

    }

    public void init(float startX,float startY)
    {
        velocity = new Vector2();
        position = new Vector2(startX,startY);
        collider = new Polygon();
        boid.x = position.x;
        boid.y = position.y;
        boidsCollision.x = 0;
        boidsCollision.y = 0;
        vertices[0]=boidsCollision.x;
        vertices[1]=boidsCollision.y;
        vertices[2]=boidsCollision.x+boidsCollision.width;
        vertices[3]=boidsCollision.y;
        vertices[4]=boidsCollision.x+boidsCollision.width;
        vertices[5]=boidsCollision.y+boidsCollision.height;
        vertices[6]=boidsCollision.x;
        vertices[7]=boidsCollision.y+boidsCollision.height;
        collider.setVertices(vertices);
        collider.setOrigin(boidsCollision.width/2,boidsCollision.height/2);
        timePassed = 0.0f;
        startExp = false;
        isAlive = true;
    }


    public String type() {
        return "MotherBoid";
    }


    public SimpleBoid getSimple() {
        return null;
    }


    public MotherBoid getMother() {
        return this;
    }


    public void setCohScl(float cohScl) { this.cohScl = cohScl; }

    public void setAlScl(float alScl) {
        this.alScl = alScl;
    }

    public void setSepScl(float sepScl) {
        this.sepScl = sepScl;
    }



    public void addVelocity(Vector2 velocity) {

        velocity.add(velocity.scl(1.0f/mass).limit(10.0f).scl(scl));

    }

    public void addVelocity(float x, float y) {
        Vector2 acc = new Vector2(x,y);
        acc.scl(0.01f);
        velocity.add(acc.limit(10.0f));
    }


    public void steer(Vector2 steering)
    {
        Vector2 acc = steering;
        acc.scl(10.0f*scl);
        //System.out.println(acc.len());
        velocity.set(acc);
    }

    public void slowDown()
    {
        velocity.sub(new Vector2(velocity).scl(0.02f));
    }


    public void move() {

        //System.out.println(Gdx.graphics.getWidth());
        if (isAlive()) {
            if (!startExp && hp <= 0) {
                kill();
            }
            if(Gdx.input.isKeyPressed(Input.Keys.A)&&choosenGun<guns.size-1)
                choosenGun++;

            if(Gdx.input.isKeyPressed(Input.Keys.S)&&choosenGun>=1)
                choosenGun--;

            position.add(collisionVelocity);
            accumulated += Gdx.graphics.getDeltaTime();
            if ((Gdx.input.isKeyPressed(Input.Keys.SPACE) || controls.shoot)) {
                boidsRotationVector.nor();
                //needs optimalization

                Vector2 tangent = new Vector2(boidsRotationVector.y, -boidsRotationVector.x);
                if(guns.get(choosenGun).shoot(new Vector2(position).add(new Vector2(boidsRotationVector).scl(boid.height * gunSclHeight).add(tangent.scl(boid.width * gunSclWidth))),boidsRotationVector,accumulated)) {
                    gunSclWidth = -gunSclWidth;
                    accumulated = 0;
                }
            }
            if (collisionVelocity.len2() > 0.014f) {
                velocity.set(0.0f, 0.0f);
                collisionVelocity.add(contrCollisionVelocity);
            } else {
                collisionVelocity.set(0.0f, 0.0f);
                position.add(velocity);
            }
            boid.x = position.x;
            boid.y = position.y;
            boidsCollision.x = boid.x - boid.width / 2 + boid.width * 0.2f;
            boidsCollision.y = boid.y - boid.height / 2;
            collider.setPosition(boidsCollision.x, boidsCollision.y);
            collider.setRotation(degrees - 90.0f);
            if (velocity.len2() < 0.0005f) {
                velocity.set(0.0f, 0.0f);
            } else {
                boidsRotationVector.set(velocity);
            }
            healthBar.logic(hp, getLocationVector());
        }
    }

    public void setPosition(Vector2 newPosition) {
        position.set(newPosition);
    }


    public void setVelocity(Vector2 newVelocity)
    {
            collisionVelocity.set(newVelocity);
            contrCollisionVelocity = new Vector2().set(collisionVelocity).scl(-0.01f);
    }


    public float getMass() {
        return mass;
    }


    public void passCamera(Camera camera){

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.polygon(collider.getTransformedVertices());
        shapeRenderer.end();
    }


    public boolean isColliding(Obstacle obstacle) {


        if(intersector.overlapConvexPolygons(collider,obstacle.getPolygon(),mtv))
        {
            physics.collideWithObstacle(this,obstacle,mtv);
            return true;
        }
        return false;
    }



    public void kill() {
        startExp = true;

        timePassed=0;
    }


    public Vector2 getLocationVector() {
        return new Vector2(position);
    }


    public Vector2 getVelocityVector(){
        return new Vector2(velocity);
    }


    public void show(Batch batch) {
        propAnim.setFrameDuration(1.0f/getVelocityVector().len2());
        degrees = (((float)Math.atan2((double)boidsRotationVector.y,boidsRotationVector.x))*180.0f) /PI;
        //System.out.println(velocity);

        if(degrees<0)
        {
            degrees+=360;
        }
        //System.out.println(degrees);

        timePassed += Gdx.graphics.getDeltaTime();
        if(!startExp) {
            batch.draw(propAnim.getKeyFrame(timePassed, true), boid.x - boid.width / 2, boid.y - boid.height * 1.5f, boid.width / 2, boid.height * 1.5f, boid.width, boid.height, 1.0f, 1.0f, degrees - 90.0f);
           // batch.draw(boidsTexture, boid.x - boid.width / 2, boid.y - boid.height / 2, boid.width / 2, boid.height / 2, boid.width, boid.height, 1.0f, 1.0f, degrees - 90.0f, 0, 0, 16, 16, false, false);
            sprite.setPosition(position.x-sprite.getWidth()/2,position.y-sprite.getHeight()/2);
            sprite.setRotation(degrees-90f);
            sprite.draw(batch);
        }
        else
        {

            if(!expAnim.isAnimationFinished(timePassed)) {
                batch.draw(expAnim.getKeyFrame(timePassed, true), boid.x - boid.width / 2, boid.y - boid.height / 2, boid.width / 2, boid.height / 2, boid.width, boid.height, 1.0f, 1.0f, degrees - 90.0f);
                velocity.limit(2.0f);
            }
            else
                isAlive=false;
        }
    }


    public Vector2 getRotation() {
        return new Vector2(boidsRotationVector);
    }


    public void damage(int power) {
        hp-=power;
        if(hp<0)
            hp=0;

        System.out.println("dmg:"+power);
        healthBar.changeHp(hp);
    }


    public void dispose() {
        expAtlas.dispose();
        propAtlas.dispose();
    }



    public Vector2 getCollisionVelocity() {
        return new Vector2(collisionVelocity);
    }



    public boolean isAlive() {
        return isAlive;
    }


    public void passPoint(Vector2 point) {

            if(intersector.isPointInPolygon(collider.getTransformedVertices(),0,8,point.x,point.y))
            {
                kill();
            }
    }

    public void renderShape(ShapeRenderer shapeRenderer)
    {
        if(isAlive)
        healthBar.render(shapeRenderer);
    }
    public void passPolygon(Polygon polygon) {
        if(intersector.overlapConvexPolygons(polygon,collider))
        kill();
    }


    @Override
    public void heal(float percentage) {
        hp += (int)(maxHp*percentage);
        if(hp>maxHp)
            hp=maxHp;

        healthBar.changeHp(hp);
    }


    @Override
    public Polygon getCollider() {
        Polygon collider = this.collider;
        return collider;
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public Vector2 position() {
        return position;
    }
}
