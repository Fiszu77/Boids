package com.drop.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import static com.badlogic.gdx.Input.Keys.SPACE;
import static com.drop.game.MainMenuScreen.SCREEN_WIDTH;
import static com.drop.game.MainMenuScreen.scl;

/**
 * Created by fiszu on 22.08.2017.
 */

public class Controls {

    private Rectangle joy2Rec;
    private Rectangle joy1Rec;
    private Vector3[] firstTouch;
    public static boolean shoot, newMeteor;

    public Vector2 getSteeringVelocity() {
        return new Vector2(steeringVelocity).scl(1.0f / (joy1Rec.width * 0.55f));

    }

    private Vector2 steeringVelocity, holdCam, joyFixPos;
    private Vector2[] firstTouchCamera;
    private int joyIndex, shootIndex;
    private Vector3[] moved;
    OrthographicCamera camera;
    private boolean isJoyActive;

    Controls(OrthographicCamera camera) {
        steeringVelocity = new Vector2();
        firstTouchCamera = new Vector2[20];
        joyFixPos = new Vector2();
        holdCam = new Vector2();
        firstTouch = new Vector3[20];
        moved = new Vector3[20];
        for (int i = 0; i < firstTouchCamera.length; i++) {
            firstTouchCamera[i] = new Vector2();
        }
        for (int i = 0; i < moved.length; i++) {
            moved[i] = new Vector3();
        }
        for (int i = 0; i < firstTouch.length; i++) {
            firstTouch[i] = new Vector3();
        }
        this.camera = camera;
        joy1Rec = new Rectangle();
        joy1Rec.width = TextureLoader.joyTex.getWidth() * scl * 1.4f;
        joy1Rec.height = TextureLoader.joyTex.getHeight() * scl * 1.4f;
        joy2Rec = new Rectangle();
        joy2Rec.width = TextureLoader.joy2Tex.getWidth() * scl * 1.4f;
        joy2Rec.height = TextureLoader.joy2Tex.getHeight() * scl * 1.4f;
    }

    void controlsLoop() {

        for (int i = 0; i < 20; i++) {
            if (Gdx.input.isTouched(i)) {
                //Jesli to ma się nie bugować to tu powinien byc if(shootindex=i)... ale to będzie i tak inaczej wiec jebać
                newMeteor=false;
                if (Gdx.input.justTouched()) {
                    if (i != joyIndex || !isJoyActive) {
                        firstTouch[i].set(Gdx.input.getX(i), Gdx.input.getY(i), 0);
                        camera.unproject(firstTouch[i]);
                        if (camera.position.y-firstTouch[i].y >0&& camera.position.x-firstTouch[i].x <0)
                        {
                            shootIndex=i;
                        }//firstTouchCamera.set(camera.position.x,camera.position.y);

                        if (camera.position.y-firstTouch[i].y <0&& camera.position.x-firstTouch[i].x <0 ) {
                            newMeteor=true;
                        }
                    }
                    if (!isJoyActive && camera.position.x-firstTouch[i].x >0) {
                        joy1Rec.setPosition(firstTouch[i].x - joy1Rec.width / 2, firstTouch[i].y - joy1Rec.height / 2);
                        isJoyActive = true;
                        joyIndex = i;
                        firstTouchCamera[i].set(camera.position.x,camera.position.y);
                    }
                }

                moved[i].set(Gdx.input.getX(i), Gdx.input.getY(i), 0);

                camera.unproject(moved[i]);
                if (camera.position.y-moved[i].y >0&& camera.position.x-moved[i].x <0 &&i==shootIndex){
                    shoot=true;
                }



               //firstTouch[i].add(-holdCam.x+firstTouchCamera.x,-holdCam.y+firstTouchCamera.y,0);
                if (isJoyActive&& i == joyIndex) {
                    joyFixPos.set(firstTouch[i].x,firstTouch[i].y);
                    holdCam.set(camera.position.x,camera.position.y);
                    joyFixPos.add(holdCam.add(firstTouchCamera[i].scl(-1)));
                    joy1Rec.setPosition(joyFixPos.x - joy2Rec.width / 2 , joyFixPos.y - joy2Rec.height / 2);
                    firstTouch[i].set(joyFixPos.x,joyFixPos.y,0);
                    moved[i].sub(firstTouch[i]);
                    if (moved[i].len() > joy1Rec.width * 0.55f) {
                        moved[i].nor().scl(joy1Rec.width * 0.55f);
                    }
                    steeringVelocity.set(moved[i].x, moved[i].y);
                    moved[i].add(firstTouch[i]);
                    joy2Rec.setPosition(moved[i].x - joy2Rec.width / 2 , moved[i].y - joy2Rec.height / 2);

                }
                firstTouchCamera[i].set(camera.position.x,camera.position.y);
            }
             if (!Gdx.input.isTouched(i)&&i == joyIndex) {
                steeringVelocity.set(0.0f, 0.0f);
                isJoyActive = false;
            }
            if(!Gdx.input.isTouched(i)&&i==shootIndex)
            {
                shoot=false;
                shootIndex=77;
            }

        }
    }

    void showJoys(Batch batch) {
            if (isJoyActive) {
                batch.draw(TextureLoader.joyTex, joy1Rec.x, joy1Rec.y, joy1Rec.width, joy1Rec.height);
                batch.draw(TextureLoader.joy2Tex, joy2Rec.x, joy2Rec.y, joy2Rec.width, joy2Rec.height);
            }
    }
}
