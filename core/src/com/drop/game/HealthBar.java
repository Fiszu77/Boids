package com.drop.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import static com.drop.game.GameScreen.center;
import static com.drop.game.MainMenuScreen.scl;

/**
 * Created by fiszu on 07.10.2017.
 */

public class HealthBar {
    int maxHp, hp;
    Color color;
    Rectangle outerFrame, bar;
    Vector2 position;
    float barWidth, alpha=0.0f, heightScl = 1.0f;
    HealthBar(int maxHp,float size, float height, Color color)
    {
        position = new Vector2();
        this.color = color;
        heightScl = height;
        this.maxHp = maxHp;
        hp=maxHp;
        barWidth=size*120.0f*scl;
        outerFrame = new Rectangle(0,0,(size*120.0f+4f)*scl,(size*20f+4f)*scl);
        bar = new Rectangle(4f*scl,0,size*120.0f*scl,size*20f*scl);
    }
    void logic(int hp, Vector2 position)
    {
        this.hp=hp;
        this.position.set(position).add(4f*scl,scl*90*heightScl);
        outerFrame.setPosition(this.position);
        outerFrame.setPosition(outerFrame.x-outerFrame.width/2,outerFrame.y);
        bar.setPosition(this.position.add(-bar.width/2,2f*scl));
        if(alpha>0)
        alpha-=0.005f;
    }
    void changeHp(int newHp)
    {
        hp=newHp;
        barWidth=bar.width*((float)hp/(float)maxHp);
        //System.out.println("bareidth: "+barWidth);
        alpha=1.0f;
    }
    void render(ShapeRenderer shapeRenderer)
    {
        if(alpha>0.0f && hp>0) {
            color.set(color.r,color.g,color.b,alpha);
            shapeRenderer.set(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(1,1,1,alpha);
            shapeRenderer.rect(outerFrame.x, outerFrame.y, outerFrame.width, outerFrame.height);
            shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(color);
            shapeRenderer.rect(bar.x, bar.y, barWidth, bar.height);
        }
    }
}
