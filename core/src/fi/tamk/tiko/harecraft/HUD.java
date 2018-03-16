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
    float progressline_x;
    float progressline_y = SCREEN_HEIGHT - 75f;
    float progressline_color_red = 255f;
    float progressline_color_green = 130f;
    float progressline_width = 300f;
    float progressline_arc_radius = 9f;
    float HUD_opacity;

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

        portrait_hare.setBounds(0f, 0f, portrait_hare.getWidth()/2f, portrait_hare.getHeight()/2f);
        portrait_hare.setPosition(25f, 700f - portrait_hare.getHeight());

        //speedometer.setBounds(0f,0f,speedometer.getWidth()/2f,speedometer.getHeight()/2f);
        //speedometer.setBounds(0f,0f,speedometer.getWidth()/1.5f,speedometer.getHeight()/1.5f);
        speedometer.setPosition(SCREEN_WIDTH - speedometer.getWidth(),0f);
    }

    public void update(float delta) {
        //game.sBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        if(gameState == RACE || gameState == FINISH) {
            progressline_x = 300f * (player.distance / world.end);
            progressline_color_green = (130f + (125f * (player.distance / world.end))) / 255f; //110 + 160 = 270
            progressline_color_red = (255f - ((255f - 95f) * (player.distance / world.end))) / 255f;

            if(progressline_color_red < 0f) progressline_color_red = 0f;
            if(progressline_color_green > 1f) progressline_color_green = 1f;

            HUD_opacity += delta;
            if(HUD_opacity > 1f) HUD_opacity = 1f;
        }
        if(gameState == END) {
            HUD_opacity -= delta;
            if(HUD_opacity < 0f) HUD_opacity = 0f;
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
        //Black
        shapeRenderer.setColor(0f, 0f, 0f, 0.3f * HUD_opacity);
        shapeRenderer.arc(SCREEN_WIDTH/2f - progressline_width/2f, progressline_y + progressline_arc_radius + 3f, progressline_arc_radius + 3f, 90f, 180f);
        shapeRenderer.arc(SCREEN_WIDTH/2f + progressline_width/2f, progressline_y + progressline_arc_radius + 3f, progressline_arc_radius + 3f, 270f, 180f);
        shapeRenderer.rect(SCREEN_WIDTH/2f - progressline_width/2f, progressline_y, progressline_width, (progressline_arc_radius + 3)*2f);

        //Color
        shapeRenderer.setColor(progressline_color_red, progressline_color_green, 44f/255f, 0.7f * HUD_opacity);
        shapeRenderer.arc(SCREEN_WIDTH/2f - progressline_width/2f, progressline_y + progressline_arc_radius + 3f, progressline_arc_radius, 90f, 180f);
        shapeRenderer.rect(SCREEN_WIDTH/2f - progressline_width/2f, progressline_y + 3f, progressline_x, progressline_arc_radius*2f);
        shapeRenderer.arc(SCREEN_WIDTH/2f - progressline_width/2f + progressline_x, progressline_y + progressline_arc_radius + 3f, progressline_arc_radius, 270f, 180f);

        //White
        shapeRenderer.setColor(1f, 1f, 1f, 0.6f * HUD_opacity);
        shapeRenderer.circle(SCREEN_WIDTH/2f - progressline_width/2f + progressline_x, progressline_y + progressline_arc_radius + 3f, progressline_arc_radius);
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
            text_gameStates.setColor(1f, 1f, 1f, text_opacity);
            //text_gameStates.rotate(200f / (gameStateTime*20f)+0.01f);

            text_gameStates.setBounds(0f, 0f, width, height);
            text_gameStates.setPosition(SCREEN_WIDTH/2f - width/2f, SCREEN_HEIGHT/2f - height/2f);

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
        text_racePlacement.setPosition(10f, 10f);
        text_racePlacement.setColor(1f, 1f, 1f, HUD_opacity);
        game.sBatch.begin();
        text_racePlacement.draw(game.sBatch);
        game.sBatch.end();
    }

    public void drawSpeedometer() {
        speedometer.setColor(1f, 1f, 1f, HUD_opacity);
        game.sBatch.begin();
        speedometer.draw(game.sBatch);
        game.sBatch.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
