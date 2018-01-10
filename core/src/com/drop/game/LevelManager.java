package com.drop.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import static com.badlogic.gdx.math.MathUtils.random;
import static com.drop.game.GameScreen.center;
import static com.drop.game.MainMenuScreen.SCREEN_HEIGHT;
import static com.drop.game.MainMenuScreen.SCREEN_WIDTH;

/**
 * Created by fiszu on 21.08.2017.
 */

public class LevelManager {

    private boolean debug = false;
    private Array<Obstacle> obstacles;
    private Array<SimpleBoid> boids;
    private Particles particles;
    private World world;
    private static BulletManager bulletManager;
    public MotherBoid playerShip;
    private Behaviour behaviour;
    ShapeRenderer shapeRenderer;
    private Spawner spawner;
    //private Array<SimpleBoid> simpleBoids;
    OrthographicCamera camera;
    private int len = 0, simpleIndex, objAm;
    private Controls controls;

    LevelManager(OrthographicCamera camera, ShapeRenderer shapeRenderer) {

        boids = new Array<SimpleBoid>();
        particles = new Particles();
        shapeRenderer = new ShapeRenderer();
        this.camera = camera;
        controls = new Controls(camera);
        bulletManager = new BulletManager();
        obstacles = new Array<Obstacle>();
        //simpleBoids = new Array<SimpleBoid>();
        behaviour = new Behaviour();
        playerShip = new MotherBoid(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2, bulletManager, controls);
        //boids.add(playerShip);
        world = new World(camera);
        spawner = new Spawner(camera, obstacles, boids, playerShip, bulletManager);
    }

    public void logic() {
        if (controls.newMeteor || Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            obstacles.add(spawner.meteoritesPool.obtain());
            obstacles.peek().init();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            boids.add(spawner.simpleBoidPool.obtain().init());
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            debug = !debug;
        }
        world.logic();
        controls.controlsLoop();
        moveObstacles(obstacles);
        manageBoidPools();
        manageObstaclesPools();
        manageHeartsPool();
        bulletManager.bulletsLogic(obstacles,particles);

        if (playerShip.isAlive()) {
            for (int i = 0; i < obstacles.size; i++) {
                playerShip.isColliding(obstacles.get(i));
            }
            for (Heart heart : spawner.hearts) {
                heart.heal(playerShip, particles);
            }
        }
        if (controls.getSteeringVelocity().len() > 0.0f) {
            playerShip.steer(controls.getSteeringVelocity());
        } else {
            playerShip.slowDown();
        }
        for (SimpleBoid boid : boids) {
            boid.applyRules(boids, playerShip);
            boid.isColliding(obstacles);
        }
        boolean collidingPairs[][] = new boolean[obstacles.size][obstacles.size];
        for (int i = 0; i < obstacles.size; i++) {
            for (int j = 0; j < obstacles.size; j++) {
                collidingPairs[i][j] = false;
            }
        }
        for (int i = 0; i < obstacles.size; i++) {
            for (int j = 0; j < obstacles.size; j++) {
                if (!(collidingPairs[i][j] || i == j)) {
                    if (obstacles.get(i).collide(obstacles.get(j))) {
                        Vector2 touchSpot = new Vector2(obstacles.get(j).getPosition()).sub(obstacles.get(i).getPosition());
                        touchSpot.scl(0.5f);
                        touchSpot.add(obstacles.get(i).getPosition());
                        particles.addMeteorSliver(touchSpot.x, touchSpot.y, new Vector2((obstacles.get(i).getVelocity()).add(obstacles.get(j).getVelocity())).scl(0.5f));
                        collidingPairs[i][j] = collidingPairs[j][i] = true;
                    }
                }
            }
        }
        behaviour.moveBoids(boids);
        playerShip.move();
        spawner.spawn();
        if (obstacles.size + boids.size != objAm) {
            objAm = obstacles.size + boids.size;
            //System.out.println("Objects amount: " + (objAm));
            //System.out.println("Bullets amount: " + (bulletManager.bulletsAlive));
        }
    }

    public void renderShapes(ShapeRenderer shapeRenderer) {
        playerShip.renderShape(shapeRenderer);
        for (SimpleBoid boid : boids) {
            boid.renderShape(shapeRenderer);
        }
        for (Obstacle obstacle : obstacles) {
            obstacle.renderShapes(shapeRenderer);
        }
        if (debug)
            debug(camera);
    }

    public void render(Batch batch) {
        world.show(batch);
        renderHearts(spawner.hearts, batch);
        renderObstacles(obstacles, batch);
        particles.draw(batch);
        playerShip.show(batch);
        behaviour.drawBoids(boids, batch);
        bulletManager.renderBullets(batch);
        controls.showJoys(batch);
    }

    public void debug(OrthographicCamera camera) {
        for (Obstacle obstacle : obstacles) {
            obstacle.debug(camera);
        }
        for (SimpleBoid boid : boids) {
            boid.passCamera(camera);
        }
        for (Heart heart : spawner.hearts) {
            heart.debug(camera);
        }
    }

    private void manageObstaclesPools() {
        len = obstacles.size;
        for (int i = 0; i < len; i++) {
            Obstacle obstacle = obstacles.get(i);
            if (!obstacles.get(i).isAlive()) {
                spawner.meteoritesPool.free(obstacle);
                obstacles.removeIndex(i);
                len--;
            }
        }
    }

    private void manageBoidPools() {
        len = boids.size;
        for (int i = 0; i < len; i++) {
            SimpleBoid boid = boids.get(i);
            if (boid.type() == "SimpleBoid") {
                if (!boids.get(i).isAlive()) {
                    spawner.simpleBoidPool.free(boid.getSimple());
                    boids.removeIndex(i);
                    len--;
                }

            }
        }
    }

    private void manageHeartsPool() {
        len = spawner.hearts.size;
        for (int i = 0; i < len; i++) {
            Heart heart = spawner.hearts.get(i);
            heart.move();
            if (!spawner.hearts.get(i).isAlive()) {
                spawner.heartsPool.free(heart);
                spawner.hearts.removeIndex(i);
                len--;
            }

        }
    }

    private void renderObstacles(Array<Obstacle> obstacles, Batch batch) {
        for (Obstacle obstacle : obstacles) {
            obstacle.show(batch);
        }
    }

    public void renderHearts(Array<Heart> hearts, Batch batch) {
        for (Heart heart : hearts) {
            heart.show(batch);
        }
    }

    private void moveObstacles(Array<Obstacle> obstacles) {
        for (Obstacle obstacle : obstacles) {
            obstacle.move(particles);
        }
    }
}
