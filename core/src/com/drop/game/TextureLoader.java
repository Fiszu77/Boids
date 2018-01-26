package com.drop.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Created by fiszu on 29/08/17.
 */

public class TextureLoader {
    public static Texture lilSpaceshipTex, biggerSpaceship,redLaserTex, meteoriteTex, joyTex, joy2Tex, basicGreenBulletTex, starsBack, heartTex;
    public static TextureAtlas lilExpAtlas, lilPropAtlas, meteorExpAtlas, textures;
    public static AssetManager assets;
    public static Sound lilExp, lilLaser, healthUp, hitMeteor;
    public static void loadAssets()
    {
        assets = new AssetManager();
        assets.load("textures.pack",TextureAtlas.class);
        assets.load("lilExp.mp3",Sound.class);
        assets.load("lilLaser.mp3",Sound.class);
        assets.load("healthUp.mp3",Sound.class);
        assets.load("hitMeteor.mp3",Sound.class);
        assets.finishLoading();
        lilExp = assets.get("lilExp.mp3");
        lilLaser = assets.get("lilLaser.mp3");
        textures = assets.get("textures.pack");
        healthUp = assets.get("healthUp.mp3");
        hitMeteor = assets.get("hitMeteor.mp3");
        heartTex = new Texture(Gdx.files.internal("heart.png"));
        meteorExpAtlas = new TextureAtlas(Gdx.files.internal("meteorite_expl.atlas"));
        starsBack = new Texture(Gdx.files.internal("background.png"));
        redLaserTex = new Texture(Gdx.files.internal("redlaser.png"));
        lilPropAtlas = new TextureAtlas(Gdx.files.internal("cprop.atlas"));
        lilSpaceshipTex = new Texture(Gdx.files.internal("chaser2.png"));
        lilExpAtlas = new TextureAtlas(Gdx.files.internal("chaserexplosion.atlas"));
        basicGreenBulletTex = new Texture(Gdx.files.internal("greenlaser.png"));
        meteoriteTex = new Texture(Gdx.files.internal("meteorite.png"));
        joyTex = new Texture(Gdx.files.internal("joy.png"));
        joy2Tex = new Texture(Gdx.files.internal("joy2.png"));
    }
}
