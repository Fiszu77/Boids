package com.drop.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;


/**
 * Created by fiszu on 12.11.2017.
 */

public class Particles {

    private  Array<Particle> particles;
    Particles()
    {
        particles = new Array<Particle>();
        particles.add(new Particle("meteorExp.p"));
        particles.add(new Particle("meteorbang.p"));
        particles.add(new Particle("meteorShoot.p"));
        particles.add(new Particle("crossPart.p"));

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
    void addHealthPicked(Vector2 position, Vector2 veloctiy)
    {
        particles.get(3).addFixedParticle(position,veloctiy);
    }
    void draw(Batch batch)
    {
        for (Particle particle : particles) {
            particle.draw(batch);
        }
    }
}
