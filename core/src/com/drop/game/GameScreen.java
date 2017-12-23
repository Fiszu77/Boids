package com.drop.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

import static com.drop.game.MainMenuScreen.SCREEN_HEIGHT;
import static com.drop.game.MainMenuScreen.SCREEN_WIDTH;
import static com.drop.game.MainMenuScreen.scl;


public class GameScreen implements Screen {
    public static Vector2 center;
    final MyGdxGame game;
    private Intersector intersector;
    private FPSLogger fpsLogger;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Texture background;
    private Vector3 touchPos, diff;
    private boolean wasJustTouched;
    private LevelManager levelManager;
    ShapeRenderer shapeRenderer;

    public GameScreen(final MyGdxGame game) {
        center = new Vector2();
        background = new Texture(Gdx.files.internal("background.png"));
        fpsLogger = new FPSLogger();
        shapeRenderer = new ShapeRenderer();
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, MainMenuScreen.SCREEN_HEIGHT);
        levelManager = new LevelManager(camera, shapeRenderer);
    batch = new SpriteBatch();
    //behaviour = new Behaviour();
    touchPos = new Vector3();
    diff = new Vector3();
    wasJustTouched = false;
    shapeRenderer = new ShapeRenderer();
    intersector = new Intersector();
}

    @Override
    public void show() {
    }




    @Override
    public void render(float delta) {
        camera.position.set(levelManager.playerShip.getLocationVector().x,levelManager.playerShip.getLocationVector().y,0);
        center.set(camera.position.x,camera.position.y);
        fpsLogger.log();
        levelManager.logic();
        Gdx.gl.glClearColor(0,0,0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        levelManager.render(batch);
        batch.end();
        shapeRenderer.setAutoShapeType(true);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin();

        levelManager.renderShapes(shapeRenderer);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        //debug();
    }
    private void debug()
    {
        levelManager.debug(camera);
    }
    @Override
    public void resize(int width, int height) {


    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {

    }

}
