package com.drop.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import static com.drop.game.MainMenuScreen.scl;


/**
 * Created by fiszu on 07.08.2017.
 */

public class Behaviour {

    private Vector2 rule1, rule2, rule3, mass, centerOfMass, antiCollide, sumOfVel, diff, desired;
    private int count;
    private float maxSpeed;

    public float getCohR() {
        return cohR;
    }

    private float cohR;
    private float followR;
    private float alR;
    private float sepR;
    private float followSepBuff;
    private float distToPlayer;
    public float getDistToPlayer() {
        return distToPlayer;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void setCohR(float cohR) {
        this.cohR = cohR;
    }

    public void setFollowR(float followR) {
        this.followR = followR;
    }

    public void setAlR(float alR) {
        this.alR = alR;
    }

    public void setSepR(float sepR) {
        this.sepR = sepR;
    }

    Behaviour() {
        rule1 = new Vector2();
        rule2 = new Vector2();
        rule3 = new Vector2();
        centerOfMass = new Vector2();
        antiCollide = new Vector2();
        sumOfVel = new Vector2();
        diff = new Vector2();
        mass = new Vector2();
        desired = new Vector2();
        count = 0;
        maxSpeed = 10.0f * scl;
        cohR = 500.0f * scl;
        alR = 500.0f * scl;
        sepR = 130.0f * scl;

        /*cohR=cohR*cohR;
        alR=alR*alR;
        sepR=sepR*sepR;*/
        followR = 150.0f * scl;
        followSepBuff = 1.0f * scl;
    }

    Vector2 follow(Vector2 desired, SimpleBoid theBoid) {
        float d = desired.dst(theBoid.getLocationVector());

        desired.set(desired.sub(theBoid.getLocationVector()));
        desired.nor();
        desired.scl(maxSpeed);
        desired.sub(theBoid.getVelocityVector());
        desired.limit(0.001f * d);
        desired.limit(0.1f);

        return desired;

    }

    Vector2 cohesion(Array<SimpleBoid> boids,SimpleBoid theBoid) {
        mass.set(0, 0);
        count = 0;
        for (int i = 0; i < boids.size; i++) {
            if (!boids.get(i).equals(theBoid)) {
                if ((boids.get(i).getLocationVector().dst(theBoid.getLocationVector())) < cohR) {
                    if (boids.get(i).type() == "MotherBoid") {
                        mass.add(new Vector2(boids.get(i).getLocationVector()).scl(10.0f));
                        count += 1;
                    } else {
                        mass.add(boids.get(i).getLocationVector());
                        count++;
                    }

                }
            }
        }
        if (count > 0) {
            mass.scl(1.0f / (float) count);
            desired.set(mass.sub(theBoid.getLocationVector()));
            desired.nor();
            desired.scl(maxSpeed);
            desired.sub(theBoid.getVelocityVector());
            desired.limit(0.07f);
            return desired;
        } else
            return new Vector2();
    }

    Vector2 alignment(Array<SimpleBoid> boids, SimpleBoid theBoid) {

        count = 0;
        sumOfVel.set(0, 0);
        for (int i = 0; i < boids.size; i++) {
            if (!boids.get(i).equals(theBoid)) {
                if ((theBoid.getLocationVector().dst(boids.get(i).getLocationVector())) < alR) {
                    count++;
                    sumOfVel.add(boids.get(i).getVelocityVector());
                }
            }
        }
        if (count > 0) {
            sumOfVel.scl(1.0f / (float) count);
            sumOfVel.nor();
            sumOfVel.scl(maxSpeed);
            sumOfVel.sub(theBoid.getVelocityVector());
            sumOfVel.limit(0.07f);
            return sumOfVel;
            //System.out.println(sumOfVel);
        } else
            return new Vector2();
    }

    Vector2 separation(Array<SimpleBoid> boids, SimpleBoid theBoid, float sepBoost) {
        count = 0;
        antiCollide.set(0, 0);
        for (int i = 0; i < boids.size; i++) {
            if (!boids.get(i).equals(theBoid)) {
                float d = boids.get(i).getLocationVector().dst(theBoid.getLocationVector());
                if (d < sepR*sepBoost) {
                    diff.set(theBoid.getLocationVector().sub(boids.get(i).getLocationVector()));
                    diff.nor();
                    diff.scl(1.0f / d);
                    theBoid.getLocationVector().add(boids.get(i).getLocationVector());
                    antiCollide.add(diff);
                    count++;
                }
            }
        }
        if (count > 0) {
            antiCollide.scl(1.0f / (float) count);

        }
        if (antiCollide.len2() > 0) {
            antiCollide.nor();
            antiCollide.scl(maxSpeed);
            antiCollide.sub(theBoid.getVelocityVector());
            antiCollide.limit(0.08f);
            return antiCollide;
        } else
            return new Vector2();
    }

    Vector2 cohTowPlayer(MotherBoid player, SimpleBoid theBoid) {
        mass.set(0, 0);

        if ((player.getLocationVector().dst(theBoid.getLocationVector())) < cohR*1.2f) {
            mass.add(player.getLocationVector().sub(theBoid.getVelocityVector().setLength2(scl*6.0f)));
            desired.set(mass.sub(theBoid.getLocationVector()));
            desired.nor();
            desired.scl(maxSpeed);
            desired.sub(theBoid.getVelocityVector());
            desired.limit(0.05f);
            return desired;
        } else
            return new Vector2();
    }

    Vector2 sepAgaPlayer(MotherBoid player, SimpleBoid theBoid) {
        antiCollide.set(0, 0);

        distToPlayer = player.getLocationVector().dst(theBoid.getLocationVector());
        if (distToPlayer < sepR) {
            diff.set(theBoid.getLocationVector().sub(player.getLocationVector()));
            diff.nor();
            diff.scl(1.0f / distToPlayer);
            theBoid.getLocationVector().add(player.getLocationVector());
            antiCollide.add(diff);
            count++;
        }
        if (antiCollide.len2() > 0) {
            antiCollide.nor();
            antiCollide.scl(maxSpeed);
            antiCollide.sub(theBoid.getVelocityVector());
            antiCollide.limit(0.08f);
            return antiCollide;
        } else
            return new Vector2();
    }

    Vector2 alTowPlayer(MotherBoid player, SimpleBoid theBoid) {
        count = 0;
        sumOfVel.set(0, 0);
        if ((theBoid.getLocationVector().dst(player.getLocationVector())) < alR) {
            count++;
            sumOfVel.add(player.getVelocityVector());
        }
        if (count > 0) {
            sumOfVel.scl(1.0f / (float) count);
            sumOfVel.nor();
            sumOfVel.scl(maxSpeed);
            sumOfVel.sub(theBoid.getVelocityVector());
            sumOfVel.limit(0.05f);
            return sumOfVel;
            //System.out.println(sumOfVel);
        } else
            return new Vector2();
    }

    void moveBoids(Array<SimpleBoid> boids) {
        for (SimpleBoid boid : boids) {
            boid.move();
        }
    }

    void drawBoids(Array<SimpleBoid> boids, Batch batch) {
        for (SimpleBoid boid : boids) {
            boid.show(batch);
        }
    }
}
