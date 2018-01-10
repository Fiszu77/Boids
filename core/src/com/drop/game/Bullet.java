package com.drop.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;


import static com.badlogic.gdx.math.MathUtils.PI;
import static com.drop.game.GameScreen.center;
import static com.drop.game.MainMenuScreen.SCREEN_HEIGHT;
import static com.drop.game.MainMenuScreen.SCREEN_WIDTH;
import static com.drop.game.MainMenuScreen.getPan;
import static com.drop.game.MainMenuScreen.getVolume;
import static com.drop.game.MainMenuScreen.scl;
import static com.drop.game.MainMenuScreen.soundVolume;

/**
 * Created by fiszu on 13.09.2017.
 */

public class Bullet implements Pool.Poolable{
    protected Rectangle bullet;
    protected Sprite sprite;
    protected Texture bulletsTexture;
    protected Vector2 position, velocity;
    protected float degrees, speed = 30.0f, scale=1.0f,initSpeed=30f;
    protected int power=30;
    protected Behaviour behaviour;
    protected ShapeRenderer shapeRenderer;
    protected Polygon collider;
    protected Intersector intersector;
    protected boolean startExp = false, isAlive = true;
    protected float[] vertices=new float[8];
    protected Sound shootingSound;
    Bullet()
    {
        //power = 30;
        sprite = new Sprite(TextureLoader.textures.findRegion("greenlaser"));
        forEach();
        prepare();
    }
    protected void forEach()
    {
        //sprite.setCenter(sprite.getWidth()/2,sprite.getHeight()/2);
        scale = scale*scl*3.0f;
        sprite.setScale(scale);
        shapeRenderer = new ShapeRenderer();
        bullet = new Rectangle();
        velocity = new Vector2();
        position = new Vector2(0,0);
        bullet.x = position.x;
        bullet.y = position.y;
        behaviour = new Behaviour();
        collider = new Polygon();
        shootingSound = TextureLoader.lilLaser;
    }
    public int getDmage()
    {
        return power;
    }
    void prepare()
    {

        bullet.height = sprite.getHeight();
        bullet.width = sprite.getWidth();
        bullet.width=bullet.width* scale;
        bullet.height=bullet.height*scale;
        vertices[0]=bullet.x;
        vertices[1]=bullet.y;
        vertices[2]=bullet.x+bullet.width;
        vertices[3]=bullet.y;
        vertices[4]=bullet.x+bullet.width;
        vertices[5]=bullet.y+bullet.height;
        vertices[6]=bullet.x;
        vertices[7]=bullet.y+bullet.height;
        collider.setVertices(vertices);
    }
    public void init(Vector2 position, Vector2 velocity) {
        velocity.scl(scl*initSpeed);
        this.velocity.set(velocity);
        this.position.set(position);
        bullet.x = position.x;
        bullet.y = position.y;
        isAlive=true;
        shootingSound.play(soundVolume*0.4f*getVolume(position),1.0f,getPan(position));
    }

    public void move(Array<Obstacle> obstacles,Particles particles) {
        behave(obstacles);
        position.add(velocity);
        bullet.x = position.x;
        bullet.y = position.y;
        if(position.x>SCREEN_WIDTH+center.x)
            isAlive=false;
        if(position.x<center.x-SCREEN_WIDTH)
            isAlive=false;
        if(position.y>center.y+SCREEN_HEIGHT)
            isAlive=false;
        if(position.y<center.y-SCREEN_HEIGHT)
            isAlive=false;
        for(Obstacle obstacle: obstacles) {
            if(!obstacle.isStartExp()) {
                if (intersector.isPointInPolygon(obstacle.getPolygon().getTransformedVertices(), 0, obstacle.getPolygon().getTransformedVertices().length, position.x, position.y)) {
                    isAlive = false;
                    obstacle.hit(power);
                    particles.addMeteorShoot(position.x,position.y,obstacle.getVelocity());
                    break;
                }
            }
        }
    }

    protected void behave(Array<Obstacle> obstacles)
    {}
     void show(Batch batch) {
        collider.setOrigin(bullet.width/2,bullet.height/2);
        collider.setPosition(bullet.x-bullet.width/2,bullet.y-bullet.height/2);
        collider.setRotation(degrees-90.0f);
        degrees = (((float)Math.atan2((double)getVelocityVector().y,getVelocityVector().x))*180.0f) /PI;
        if(degrees<0)
        {
            degrees+=360;
        }
        sprite.setPosition(bullet.x-sprite.getWidth()/2,bullet.y-sprite.getHeight()/2);
        sprite.setRotation(degrees-90.0f);
        sprite.draw(batch);
    }

    public void dispose() {
    bulletsTexture.dispose();
    }
    public boolean isAlive() {
        return isAlive;
    }

    public Vector2 getLocationVector() {
        return new Vector2().set(position);
    }

    public Polygon getPolygon() {
        return collider;
    }

    public float getPower() {
        return 0;
    }

    public Vector2 getVelocityVector() {
        return new Vector2().set(velocity);
    }

    @Override
    public void reset() {

    }
}
