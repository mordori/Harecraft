package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.FINISH;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.GameScreen.gameStateTime;
import static fi.tamk.tiko.harecraft.GameScreen.isCountdown;
import static fi.tamk.tiko.harecraft.GameScreen.orthoCamera;
import static fi.tamk.tiko.harecraft.World.WORLD_WIDTH;
import static fi.tamk.tiko.harecraft.World.player;

/**
 * Created by Mika on 15/03/2018.
 */

public class HUD {
    World world;
    GameMain game;
    ShapeRenderer shapeRenderer;
    float x;
    float red = 240f;
    float green = 130f;
    float opacity;

    Sprite icon_hare = new Sprite(Assets.texR_character_hare_head);
    Sprite portrait_hare = new Sprite(Assets.flip(Assets.texR_portrait_hare));
    Sprite speedometer = new Sprite(Assets.flip(Assets.texR_speedometer));
    Sprite text_gameStates = new Sprite();
    Sprite text_racePlacement = new Sprite();

    float text_opacity = 0f;
    int index = 0;

    public HUD(World world, GameMain game) {
        this.game = game;
        this.world = world;
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(orthoCamera.combined);

        portrait_hare.setBounds(0f,0f,portrait_hare.getWidth()/2f,portrait_hare.getHeight()/2f);
        portrait_hare.setPosition(25f, 700f - portrait_hare.getHeight());

        //speedometer.setBounds(0f,0f,speedometer.getWidth()/2f,speedometer.getHeight()/2f);


        //speedometer.setBounds(0f,0f,speedometer.getWidth()/1.5f,speedometer.getHeight()/1.5f);
        speedometer.setPosition(SCREEN_WIDTH*100f - speedometer.getWidth(),0f);
    }

    public void update(float delta) {
        //game.sBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        if(gameState == RACE || gameState == FINISH) {
            x = 300f * (player.distance / world.end);
            green = 130f + (110f * (player.distance / world.end)); //110 + 160 = 270
            red = 240f - ((240f - 80f) * (player.distance / world.end));

            if(red < 240f) red = 240f;
            if(green > 240f) red = 240f;

            opacity += delta;
            if(opacity > 1f) opacity = 1f;
            //game.sBatch.setColor(1f,1f,1f,opacity);
        }
        if(gameState == END) {
            opacity -= delta;
            if(opacity < 0f) opacity = 0f;
        }
    }

    public void draw() {
        drawCountdown();
        if(gameState != START) {
            drawProgressLine();
            //drawPortrait();
            drawRacePlacement();
            drawSpeedometer();
        }
    }

    public void drawProgressLine() {
        Gdx.gl.glEnable(Gdx.gl20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f,0f,0f,0.3f * opacity);
        shapeRenderer.arc(SCREEN_WIDTH*100f/2f - 150f,SCREEN_HEIGHT*100f - 75f + 12f,12f,90f,180f);
        shapeRenderer.arc(SCREEN_WIDTH*100f/2f + 150f,SCREEN_HEIGHT*100f - 75f + 12f,12f,270f,180f);
        shapeRenderer.rect(SCREEN_WIDTH*100f/2f - 150f,SCREEN_HEIGHT*100f - 75f,300f,24f);

        shapeRenderer.setColor(red/240f,green/240f,44f/240f,0.7f * opacity);
        shapeRenderer.arc(SCREEN_WIDTH*100f/2f - 150f,SCREEN_HEIGHT*100f - 75f + 12f,9f,90f,180f);
        shapeRenderer.rect(SCREEN_WIDTH*100f/2f - 150f,SCREEN_HEIGHT*100f - 75f + 3f,x,18f);
        shapeRenderer.arc(SCREEN_WIDTH*100f/2f - 150f + x,SCREEN_HEIGHT*100f - 75f + 12f,9f,270f,180f);

        shapeRenderer.setColor(240f,240f,240f,0.6f * opacity);
        shapeRenderer.circle(SCREEN_WIDTH*100f/2f - 150f + x,SCREEN_HEIGHT*100f - 75f + 12f,9f);
        shapeRenderer.end();
    }

    public void drawCountdown() {
        game.sBatch.begin();
        //Countdown numbers
        if(isCountdown || (gameState == END && gameStateTime < 4f)) {
            if(gameState == START) {
                if (gameStateTime < 3.3f) {
                    if(gameStateTime < 2f) text_opacity = 0f;
                    index = 2;
                } else if (gameStateTime < 4.6f) {
                    if(index == 2) text_opacity = 0f;
                    index = 1;
                } else if (gameStateTime < 5.9f) {
                    if(index == 1) text_opacity = 0f;
                    index = 0;
                } else {
                    if(index == 0) text_opacity = 0f;
                    index = 4;
                }
                text_opacity += Gdx.graphics.getDeltaTime();
                if(text_opacity > 1f) text_opacity = 1f;
            }
            else if(gameState == END) {
                if (index == 4) text_opacity = 0f;
                index = 3;

                if(gameStateTime < 1.7f) {
                    text_opacity += Gdx.graphics.getDeltaTime();
                    if (text_opacity > 1f) text_opacity = 1f;
                }
                else {
                    text_opacity -= Gdx.graphics.getDeltaTime();
                    if(text_opacity < 0f) text_opacity = 0f;
                }
            }
            if(gameState == RACE) {
                text_opacity -= Gdx.graphics.getDeltaTime();
                if(text_opacity < 0f) text_opacity = 0f;
                if(text_opacity == 0f) isCountdown = false;
            }

            text_gameStates = Assets.sprites_text_race_states.get(index);
            float width = text_gameStates.getRegionHeight()/1.5f;
            float height = text_gameStates.getRegionWidth()/1.5f;

            if(index == 3) {
                float temp = width;
                width = height;
                height = temp;
            }

            text_gameStates.setOriginCenter();
            text_gameStates.scale(gameStateTime/300f);
            text_gameStates.setColor(1f,1f,1f, text_opacity);
            //text_gameStates.rotate(200f / (gameStateTime*20f)+0.01f);

            text_gameStates.setBounds(0f,0f, width, height);
            text_gameStates.setPosition(SCREEN_WIDTH*100f/2f - width/2f, SCREEN_HEIGHT*100f/2f - height/2f);

            text_gameStates.draw(game.sBatch);
        }

        //icon_hare.draw(game.sBatch);
        game.sBatch.end();
    }

    public void drawPortrait() {
        game.sBatch.begin();
        portrait_hare.draw(game.sBatch);
        game.sBatch.end();
    }

    public void drawRacePlacement() {
        text_racePlacement = Assets.sprites_text_race_positions.get(index);
        text_racePlacement.setPosition(10f,10f);
        text_racePlacement.setColor(1f,1f,1f,opacity);
        game.sBatch.begin();
        text_racePlacement.draw(game.sBatch);
        game.sBatch.end();
    }

    public void drawSpeedometer() {
        speedometer.setColor(1f,1f,1f,opacity);
        game.sBatch.begin();
        speedometer.draw(game.sBatch);
        game.sBatch.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
