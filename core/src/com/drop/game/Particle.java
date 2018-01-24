package com.drop.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import static com.drop.game.MainMenuScreen.scl;

/**
 * Created by fiszu on 08.01.2018.
 */

public class Particle {
    ParticleEffect particle;
    ParticleEffectPool particlePool;
    Array<ParticleEffectPool.PooledEffect> particleEffects;
    Array<Vector2> particlePos, particleVel;
    Particle(String path)
    {
        particlePos = new Array<Vector2>();
        particleVel = new Array<Vector2>();
        particleEffects= new Array<ParticleEffectPool.PooledEffect>();
        particle = new ParticleEffect();
        particle.setEmittersCleanUpBlendFunction(false);
        particlePool = new ParticleEffectPool(particle,1,5);
        particle.load(Gdx.files.internal(path), TextureLoader.textures);
    }
    void addParticle(float x, float y, Vector2 veloctiy)
    {
        ParticleEffectPool.PooledEffect effect = particlePool.obtain();
        particlePos.add(new Vector2(x,y));
        particleVel.add(veloctiy);
        effect.setPosition(x,y);
        effect.scaleEffect(scl*0.5f);
        effect.start();
        particleEffects.add(effect);
    }
    void addFixedParticle(Vector2 position, Vector2 veloctiy)
    {
        ParticleEffectPool.PooledEffect effect = particlePool.obtain();
        particlePos.add(position);
        particleVel.add(new Vector2());
        effect.scaleEffect(scl);
        effect.start();
        particleEffects.add(effect);
    }
    void draw(Batch batch)
    {
        for(int i=particleEffects.size-1; i>=0; i--)
        {
            particlePos.get(i).add(particleVel.get(i));
            ParticleEffectPool.PooledEffect effect = particleEffects.get(i);
            effect.draw(batch, Gdx.graphics.getDeltaTime());
            effect.setPosition(particlePos.get(i).x,particlePos.get(i).y);
            if(effect.isComplete())
            {
                particlePos.removeIndex(i);
                particleVel.removeIndex(i);
                effect.free();
                particleEffects.removeIndex(i);
            }
        }
    }

}
