package com.drop.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;


import Interfaces.Healable;

import static com.drop.game.MainMenuScreen.scl;

/**
 * Created by fiszu on 01.11.2017.
 */

public class Heart implements Pool.Poolable
{
    //protected Rectangle heart;
    protected Vector2 position, velocity, contrVel;
    protected ShapeRenderer shapeRenderer;
    protected Intersector intersector;
    protected Polygon collider;
    protected float[] vertices=new float[12];
    protected float percentage = 0.5f;
    protected boolean isAlive = true;
    protected Sprite sprite;
    protected Sound healthUp;
    Heart()
    {
        sprite = new Sprite(TextureLoader.textures.findRegion("heart"));
        sprite.setScale(8.5f*scl);
        shapeRenderer = new ShapeRenderer();
        velocity = new Vector2();
        position = new Vector2(40,40);
        collider = new Polygon();
        sprite.setPosition(position.x-sprite.getWidth()/2,position.y-sprite.getHeight()/2);
        prepare();
        healthUp = TextureLoader.healthUp;
    }

    void prepare()
    {
        vertices[0] = sprite.getWidth()*sprite.getScaleX()*9.0f/16f;
        vertices[1] = 0;
        vertices[2] = sprite.getWidth()*sprite.getScaleX();
        vertices[3] = sprite.getHeight()*sprite.getScaleX()*10.0f/16f;
        vertices[4] = sprite.getWidth()*sprite.getScaleX()*13.0f/16f;
        vertices[5] = sprite.getHeight()*sprite.getScaleX();
        vertices[6] = sprite.getWidth()*sprite.getScaleX()*3f/16f;
        vertices[7] = sprite.getHeight()*sprite.getScaleX();
        vertices[8] = 0;
        vertices[9] = sprite.getHeight()*sprite.getScaleX()*10.0f/16f;
        vertices[10] = sprite.getWidth()*sprite.getScaleX()*7f/16f;
        vertices[11] = 0;
        collider.setVertices(vertices);
    }

    public void init(Vector2 position, Vector2 velocity)
    {
        this.velocity=velocity;
        contrVel = new Vector2(velocity);
        contrVel.scl(-0.01f);
        this.position = position;
        sprite.setPosition(position.x-sprite.getWidth()/2,position.y-sprite.getHeight()/2);
        isAlive = true;
    }

    public boolean isAlive()
    {
        return isAlive;
    }

    public void move()
    {
        position.add(velocity);
        if(velocity.len2()>0.001)
        velocity.add(contrVel);
        else
            velocity.set(0,0);

        sprite.setPosition(position.x-sprite.getWidth()/2,position.y-sprite.getHeight()/2);
        collider.setOrigin(sprite.getWidth()*sprite.getScaleX()/2,sprite.getHeight()*sprite.getScaleX()/2);
        collider.setPosition(position.x-sprite.getWidth()*sprite.getScaleX()/2,position.y-sprite.getHeight()*sprite.getScaleX()/2);
    }
    public void heal(Healable healable, Particles particles)
    {
        if(intersector.overlapConvexPolygons(healable.getCollider(),collider)&&isAlive)
        {
            healable.heal(percentage);
            particles.addHeart(healable);
            isAlive=false;
            healthUp.play();
        }

    }
    public void show(Batch batch) {
        if(isAlive)
        //batch.draw(TextureLoader.heartTex, position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2, sprite.getWidth() / 2, sprite.getHeight() / 2, sprite.getWidth(), sprite.getHeight(), 1.0f, 1.0f, 0, 0, 0, 16, 16, false, false);
        sprite.draw(batch);
        //particle.draw(batch);
    }
    public void debug(Camera camera)
    {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.polygon(collider.getTransformedVertices());
        shapeRenderer.end();
    }

    @Override
    public void reset() {

    }
}
