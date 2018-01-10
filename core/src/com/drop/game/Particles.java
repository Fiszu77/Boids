package com.drop.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import Interfaces.Collideable;

import static com.drop.game.MainMenuScreen.scl;

/**
 * Created by fiszu on 12.11.2017.
 */

public class Particles {

    private ParticleEffect heart, meteorSliver,meteorExp, rocketProp;
    private  ParticleEffectPool heartPool, mSliverPool,mExpPool, rocketPool;
    private  Array<ParticleEffectPool.PooledEffect> heartEffects, mSliverEffects,mExpEffects,rocketEffects;
    private  Collideable heartHolder;
    private  Rocket rocket;
    private  Array<Vector2> mSliverPos, mSliverVel,mExpPos,mExpVel;
    private  Array<Particle> particles;
    Particles()
    {

        particles = new Array<Particle>();

        heartEffects = new Array<ParticleEffectPool.PooledEffect>();
        heart = new ParticleEffect();
        heart.setEmittersCleanUpBlendFunction(false);
        heartPool = new ParticleEffectPool(heart,1,5);
        heart.load(Gdx.files.internal("crossPart.p"), TextureLoader.textures);


        particles.add(new Particle("meteorExp.p"));
        particles.add(new Particle("meteorbang.p"));
        particles.add(new Particle("meteorShoot.p"));

        rocketEffects= new Array<ParticleEffectPool.PooledEffect>();
        rocketProp = new ParticleEffect();
        rocketProp.setEmittersCleanUpBlendFunction(false);
        rocketPool = new ParticleEffectPool(meteorSliver,1,5);
        rocketProp.load(Gdx.files.internal("meteorbang.p"), TextureLoader.textures);
    }
    void addHeart(Collideable heartHolder)
    {
        this.heartHolder = heartHolder;
        ParticleEffectPool.PooledEffect effect = heartPool.obtain();
        effect.setPosition(heartHolder.position().x-heartHolder.getSprite().getWidth()*heartHolder.getSprite().getScaleX()/2,heartHolder.position().y-heartHolder.getSprite().getHeight()/2);
        effect.scaleEffect(scl);
        effect.start();
        heartEffects.add(effect);
    }
    void addMeteorSliver(float x, float y, Vector2 veloctiy)
    {
        particles.get(1).addParticle(x,y,veloctiy);
    }
    void addMeteorExp(float x, float y, Vector2 veloctiy)
    {
        particles.get(0).addParticle(x,y,veloctiy);
    }
    void addMeteorShoot(float x, float y, Vector2 veloctiy)
    {
        particles.get(2).addParticle(x,y,veloctiy);
    }
    void draw(Batch batch)
    {
        for(int i=heartEffects.size-1; i>=0; i--)
        {
            ParticleEffectPool.PooledEffect effect = heartEffects.get(i);
            effect.setPosition(heartHolder.position().x,heartHolder.position().y);
            effect.draw(batch, Gdx.graphics.getDeltaTime());
            if(effect.isComplete())
            {
                effect.free();
                heartEffects.removeIndex(i);
            }
        }
        for (Particle particle : particles) {
            particle.draw(batch);
        }
    }
}
