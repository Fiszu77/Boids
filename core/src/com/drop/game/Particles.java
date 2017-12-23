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

    ParticleEffect heart, meteorSliver, rocketProp;
    ParticleEffectPool heartPool, mSliverPool, rocketPool;
    Array<ParticleEffectPool.PooledEffect> heartEffects, mSliverEffects,rocketEffects;
    Collideable heartHolder;
    Rocket rocket;
    Array<Vector2> mSliverPos, mSliverVel;
    Particles()
    {
        mSliverPos = new Array<Vector2>();
        mSliverVel = new Array<Vector2>();
        heartEffects = new Array<ParticleEffectPool.PooledEffect>();
        heart = new ParticleEffect();
        heart.setEmittersCleanUpBlendFunction(false);
        heartPool = new ParticleEffectPool(heart,1,5);
        heart.load(Gdx.files.internal("crossPart.p"), TextureLoader.textures);

        mSliverEffects= new Array<ParticleEffectPool.PooledEffect>();
        meteorSliver = new ParticleEffect();
        meteorSliver.setEmittersCleanUpBlendFunction(false);
        mSliverPool = new ParticleEffectPool(meteorSliver,1,5);
        meteorSliver.load(Gdx.files.internal("meteorbang.p"), TextureLoader.textures);

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
        ParticleEffectPool.PooledEffect effect = mSliverPool.obtain();
        mSliverPos.add(new Vector2(x,y));
        mSliverVel.add(veloctiy);
        //mSliverVel.peek();
        effect.setPosition(x,y);
        effect.scaleEffect(scl*0.5f);
        effect.start();
        mSliverEffects.add(effect);
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
        for(int i=mSliverEffects.size-1; i>=0; i--)
        {
            mSliverPos.get(i).add(mSliverVel.get(i));
            ParticleEffectPool.PooledEffect effect = mSliverEffects.get(i);
            effect.draw(batch, Gdx.graphics.getDeltaTime());
            effect.setPosition(mSliverPos.get(i).x,mSliverPos.get(i).y);
            if(effect.isComplete())
            {
                mSliverPos.removeIndex(i);
                mSliverVel.removeIndex(i);
                effect.free();
                mSliverEffects.removeIndex(i);
            }
        }
    }
}
