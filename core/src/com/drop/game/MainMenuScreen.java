package com.drop.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
public class MainMenuScreen implements Screen {

   // public static final int SCREEN_WIDTH = 2560;
    //public static final int SCREEN_HEIGHT = 1440;
   public static int SCREEN_WIDTH = Gdx.graphics.getWidth();
    public static int SCREEN_HEIGHT = Gdx.graphics.getHeight();
    public static float scl = SCREEN_WIDTH / 2560f, reverseScl=640f/SCREEN_WIDTH;
    final MyGdxGame game;
    public static Music music;
    OrthographicCamera camera;
    public MainMenuScreen(final MyGdxGame game)
    {
        music = Gdx.audio.newMusic(Gdx.files.internal("Waiting For Launch.mp3"));
        music.setLooping(true);
        music.play();
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH,SCREEN_HEIGHT);
        TextureLoader.loadAssets();
    }
    @Override
    public void show() {
        // TODO Auto-generated method stub

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "Flying ships ", 100, 150);
        game.font.draw(game.batch, "Touch anywhere to start", 100, 100);
        game.batch.end();
        if(Gdx.input.isTouched())
        {
            game.setScreen(new GameScreen(game));
            dispose();
        }


    }

    @Override
    public void resize(int width, int height) {
         SCREEN_WIDTH = Gdx.graphics.getWidth();
        SCREEN_HEIGHT = Gdx.graphics.getHeight();
        scl = SCREEN_WIDTH/ 2560f;
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
        // TODO Auto-generated method stub

    }

}
