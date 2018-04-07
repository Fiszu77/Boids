package com.drop.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import static com.drop.game.GameScreen.center;
import static com.drop.game.MainMenuScreen.SCREEN_HEIGHT;
import static com.drop.game.MainMenuScreen.SCREEN_WIDTH;

/**
 * Created by fiszu on 21.08.2017.
 */

public class LevelManager {

    private boolean debug = true;
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
    private float time=0, period=0.03f;

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
            spawner.addMeteor();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            spawner.addBoid();
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
        bulletManager.bulletsLogic(obstacles, particles);
        time+=Gdx.graphics.getDeltaTime();
        if (controls.getSteeringVelocity().len() > 0.0f) {
            playerShip.steer(controls.getSteeringVelocity());
        } else {
            playerShip.slowDown();
        }
        for (SimpleBoid boid : boids) {
            boid.applyRules(boids, playerShip);

        }
        if(time>period) {
            if (playerShip.isAlive()) {
                for (int i = 0; i < obstacles.size; i++) {
                    playerShip.isColliding(obstacles.get(i));
                }
                for (Heart heart : spawner.hearts) {
                    heart.heal(playerShip, particles);
                }
            }

            for (SimpleBoid boid : boids) {

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
            time = 0;
        }
        behaviour.moveBoids(boids);
        playerShip.move();
        spawner.spawn();
        if (obstacles.size + boids.size != objAm) {
            objAm = obstacles.size + boids.size;
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
                spawner.freeMeteoor();
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
                    spawner.freeBoid();
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

    public static void keepWithinBorders(Vector2 position, Sprite sprite) {
        if (position.x < center.x - SCREEN_WIDTH / 2 - sprite.getHeight()) {
            position.x = SCREEN_WIDTH / 2 + center.x + sprite.getHeight();
        }
        if (position.x > center.x + SCREEN_WIDTH / 2 + sprite.getHeight()) {
            position.x = center.x - SCREEN_WIDTH / 2 - sprite.getHeight();
        }
        if (position.y > center.y + SCREEN_HEIGHT / 2 + sprite.getHeight()) {
            position.y = center.y - SCREEN_HEIGHT / 2 - sprite.getHeight();
        }
        if (position.y < center.y - SCREEN_HEIGHT / 2 - sprite.getHeight()) {
            position.y = center.y + SCREEN_HEIGHT / 2 + sprite.getHeight();
        }
    }

    public static boolean isInBorders(Vector2 position) {
        if (position.x > center.x - SCREEN_WIDTH / 2 && position.x < center.x + SCREEN_WIDTH / 2 && position.y < center.y + SCREEN_HEIGHT / 2 && position.y > center.y - SCREEN_HEIGHT / 2) {
            return true;
        } else return false;
    }

    public static boolean overlaps(Polygon polygon, Circle circle) {
        float[] vertices = polygon.getTransformedVertices();
        Vector2 center = new Vector2(circle.x, circle.y);
        float squareRadius = circle.radius * circle.radius;
        for (int i = 0; i < vertices.length; i += 2) {
            if (i == 0) {
                if (Intersector.intersectSegmentCircle(new Vector2(vertices[vertices.length - 2], vertices[vertices.length - 1]), new Vector2(vertices[i], vertices[i + 1]), center, squareRadius))
                    return true;
            } else {
                if (Intersector.intersectSegmentCircle(new Vector2(vertices[i - 2], vertices[i - 1]), new Vector2(vertices[i], vertices[i + 1]), center, squareRadius))
                    return true;
            }
        }
        return false;
    }

}
