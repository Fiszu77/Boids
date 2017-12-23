package com.drop.game;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;

import static com.drop.game.MainMenuScreen.reverseScl;
import static com.drop.game.MainMenuScreen.scl;

/**
 * Created by fiszu on 26/08/17.
 */

public class Physics {
    public static void collideWithObstacle(SimpleBoid boid, Obstacle obstacle, Intersector.MinimumTranslationVector mtv) {
        if(!obstacle.isStartExp()) {
            Vector2 boidsVelocity = new Vector2(boid.getVelocityVector()).add(boid.getCollisionVelocity());
            Vector2 unitNormal = new Vector2().set(boid.getLocationVector()).sub(obstacle.getPosition());
            unitNormal.nor();
            Vector2 unitTangent = new Vector2(-unitNormal.y, unitNormal.x);
            float v1NormalBefore = unitNormal.dot(boidsVelocity);
            float v1Tangent = unitTangent.dot(boidsVelocity);
            float v2NormalBefore = unitNormal.dot(obstacle.getVelocity());
            float v2Tangent = unitTangent.dot(obstacle.getVelocity());
            float v1NormalAfter = (v1NormalBefore * (boid.getMass() - obstacle.getMass()) + 2 * obstacle.getMass() * v2NormalBefore) / (boid.getMass() + obstacle.getMass());
            float v2NormalAfter = (v2NormalBefore * (obstacle.getMass() - boid.getMass()) + 2 * boid.getMass() * v1NormalBefore) / (boid.getMass() + obstacle.getMass());
            Vector2 v1TangentVector = new Vector2().set(unitTangent).scl(v1Tangent);
            Vector2 v2TangentVector = new Vector2().set(unitTangent).scl(v2Tangent);
            Vector2 v1NormalVector = new Vector2().set(unitNormal).scl(v1NormalAfter);
            Vector2 v2NormalVector = new Vector2().set(unitNormal).scl(v2NormalAfter);
            Vector2 v1After = new Vector2().set(v1TangentVector.add(v1NormalVector));
            Vector2 v2After = new Vector2().set(v2TangentVector.add(v2NormalVector));
            boid.setPosition(boid.getLocationVector().add(mtv.normal.scl(mtv.depth * 1.2f)));
            boid.setVelocity(v1After);
            obstacle.hit((int) ((v2After.sub(obstacle.getVelocity())).len() * reverseScl) * 60);
            v2After.add(obstacle.getVelocity());
            obstacle.setVelocity(v2After);
            boid.damage((int) ((v1After.sub(boidsVelocity).len() * reverseScl)) * 10);
        }
    }
    public static void collideWithObstacle(MotherBoid boid, Obstacle obstacle, Intersector.MinimumTranslationVector mtv) {
        if (!obstacle.isStartExp()) {
            Vector2 boidsVelocity = new Vector2(boid.getVelocityVector()).add(boid.getCollisionVelocity());
            Vector2 unitNormal = new Vector2().set(boid.getLocationVector()).sub(obstacle.getPosition());
            unitNormal.nor();
            Vector2 unitTangent = new Vector2(-unitNormal.y, unitNormal.x);
            float v1NormalBefore = unitNormal.dot(boidsVelocity);
            float v1Tangent = unitTangent.dot(boidsVelocity);
            float v2NormalBefore = unitNormal.dot(obstacle.getVelocity());
            float v2Tangent = unitTangent.dot(obstacle.getVelocity());
            float v1NormalAfter = (v1NormalBefore * (boid.getMass() - obstacle.getMass()) + 2 * obstacle.getMass() * v2NormalBefore) / (boid.getMass() + obstacle.getMass());
            float v2NormalAfter = (v2NormalBefore * (obstacle.getMass() - boid.getMass()) + 2 * boid.getMass() * v1NormalBefore) / (boid.getMass() + obstacle.getMass());
            Vector2 v1TangentVector = new Vector2().set(unitTangent).scl(v1Tangent);
            Vector2 v2TangentVector = new Vector2().set(unitTangent).scl(v2Tangent);
            Vector2 v1NormalVector = new Vector2().set(unitNormal).scl(v1NormalAfter);
            Vector2 v2NormalVector = new Vector2().set(unitNormal).scl(v2NormalAfter);
            Vector2 v1After = new Vector2().set(v1TangentVector.add(v1NormalVector));
            Vector2 v2After = new Vector2().set(v2TangentVector.add(v2NormalVector));
            boid.setPosition(boid.getLocationVector().add(mtv.normal.scl(mtv.depth * 1.2f)));
            boid.setVelocity(v1After);
            obstacle.hit((int) ((v2After.sub(obstacle.getVelocity())).len() * reverseScl) * 60);
            v2After.add(obstacle.getVelocity());
            obstacle.setVelocity(v2After);
            boid.damage((int) ((v1After.sub(boidsVelocity).len() * reverseScl)) * 10);
        }
    }
    public static void collideWithObstacle(Obstacle obstacle1, Obstacle obstacle2, Intersector.MinimumTranslationVector mtv)
    {
        Vector2 unitNormal = new Vector2().set(obstacle1.getPosition()).sub(obstacle2.getPosition());
        unitNormal.nor();
        Vector2 unitTangent = new Vector2(-unitNormal.y, unitNormal.x);
        float v1NormalBefore =unitNormal.dot(obstacle1.getVelocity());
        float v1Tangent = unitTangent.dot(obstacle1.getVelocity());
        float v2NormalBefore = unitNormal.dot(obstacle2.getVelocity());
        float v2Tangent = unitTangent.dot(obstacle2.getVelocity());
        float v1NormalAfter = (v1NormalBefore*(obstacle1.getMass()-obstacle2.getMass())+ 2*obstacle2.getMass()*v2NormalBefore)/(obstacle1.getMass()+obstacle2.getMass());
        float v2NormalAfter = (v2NormalBefore*(obstacle2.getMass()-obstacle1.getMass())+ 2*obstacle1.getMass()*v1NormalBefore)/(obstacle1.getMass()+obstacle2.getMass());
        Vector2 v1TangentVector = new Vector2().set(unitTangent).scl(v1Tangent);
        Vector2 v2TangentVector = new Vector2().set(unitTangent).scl(v2Tangent);
        Vector2 v1NormalVector = new Vector2().set(unitNormal).scl(v1NormalAfter);
        Vector2 v2NormalVector = new Vector2().set(unitNormal).scl(v2NormalAfter);
        Vector2 v1After = new Vector2().set( v1TangentVector.add(v1NormalVector));
        Vector2 v2After = new Vector2().set(v2TangentVector.add(v2NormalVector));
        obstacle1.setPosition(obstacle1.getPosition().add(mtv.normal.scl(mtv.depth)));
        obstacle2.hit((int) ((v2After.sub(obstacle2.getVelocity())).len() * reverseScl) * 20);
        v2After.add(obstacle2.getVelocity());
        obstacle2.setVelocity(v2After);
        obstacle1.hit((int) ((v1After.sub(obstacle1.getVelocity())).len() * reverseScl) * 20);
        v1After.add(obstacle1.getVelocity());
        obstacle2.setVelocity(v2After);
        obstacle1.setVelocity(v1After);
    }
}
