package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import static fi.tamk.tiko.harecraft.GameMain.sBatch;
import static fi.tamk.tiko.harecraft.GameMain.shapeRenderer;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.FINISH;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.GameScreen.gameStateTime;
import static fi.tamk.tiko.harecraft.GameScreen.countdown;
import static fi.tamk.tiko.harecraft.World.player;

/**
 * Created by Mika on 15/03/2018.
 */

public class HUD {
    World world;

    float progressline_x;
    float progressline_y = SCREEN_HEIGHT - 75f;
    float progressline_color_red = 255f;
    float progressline_color_green = 130f;
    float progressline_width = 300f;
    float progressline_arc_radius = 9f;
    float HUD_opacity;
    float yPos = SCREEN_HEIGHT/1.5f;

    Sprite speedometer = new Sprite(Assets.texR_speedometer);
    Sprite text_gameStates = new Sprite();
    Sprite text_placementNumber = new Sprite();

    float text_opacity = 0f;
    int index = 0;

    public HUD(World world) {
        this.world = world;

        //speedometer.setBounds(0f,0f,speedometer.getWidth()/2f,speedometer.getHeight()/2f);
        //speedometer.setBounds(0f,0f,speedometer.getWidth()/1.5f,speedometer.getHeight()/1.5f);
        speedometer.setPosition(SCREEN_WIDTH - speedometer.getWidth(),0f);
    }

    public void update(float delta) {
        if(gameState == RACE || gameState == FINISH) {
            HUD_opacity += delta;
            if(HUD_opacity > 1f) HUD_opacity = 1f;
        }
        else if(gameState == END) {
            HUD_opacity -= delta;
            if(HUD_opacity < 0f) HUD_opacity = 0f;
        }

        if(gameState != START) {
            if(gameState != END) updateProgressLine(delta);
            updateSpeedometer(delta);
            updatePlacementNumber(delta);
        }
    }

    public void draw() {
        if(gameState != START) {
            drawProgressLine();
        }
        sBatch.begin();
        drawCountdown();
        if(gameState != START) {
            drawPlacementNumber();
            //drawSpeedometer();
        }
        sBatch.end();
    }

    public void updateProgressLine(float delta) {
        progressline_x = 300f * (player.distance / world.end);
        progressline_color_green = (130f + (125f * (player.distance / world.end))) / 255f; //110 + 160 = 270
        progressline_color_red = (255f - ((255f - 95f) * (player.distance / world.end))) / 255f;

        if(progressline_color_red < 0f) progressline_color_red = 0f;
        if(progressline_color_green > 1f) progressline_color_green = 1f;
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
        //Countdown numbers
        if(countdown || (gameState == END && gameStateTime < 4f)) {
            if(gameState == START) {
                if (gameStateTime < 3.3f) {
                    if(gameStateTime < 2f) {
                        yPos = SCREEN_HEIGHT/1.5f;
                        text_opacity = 0f;
                        //Assets.sound_countdown.play(0.45f);
                    }
                    index = 2;
                } else if (gameStateTime < 4.6f) {
                    if(index == 2) {
                        yPos = SCREEN_HEIGHT/1.5f;
                        text_opacity = 0f;
                        Assets.sound_countdown.play(0.45f);
                    }
                    index = 1;
                } else if (gameStateTime < 5.9f) {
                    if(index == 1) {
                        yPos = SCREEN_HEIGHT/1.5f;
                        text_opacity = 0f;
                        Assets.sound_countdown.play(0.45f);
                    }
                    index = 0;
                } else {
                    if(index == 0) {
                        yPos = SCREEN_HEIGHT/1.5f;
                        text_opacity = 0f;
                        Assets.sound_countdown_end.play(0.85f);
                    }
                    index = 4;
                }
                text_opacity += Gdx.graphics.getDeltaTime();
                if(text_opacity > 1f) text_opacity = 1f;
            }
            else if(gameState == END) {
                if (index == 4) {
                    yPos = SCREEN_HEIGHT/1.5f;
                    text_opacity = 0f;
                }
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
                if(text_opacity == 0f) countdown = false;
            }

            text_gameStates = Assets.sprites_text_race_states.get(index);
            float width = text_gameStates.getRegionHeight() * 1.25f;
            float height = text_gameStates.getRegionWidth() * 1.25f;

            if(index == 3) {
                float temp = width;
                width = height;
                height = temp;
            }

            yPos -= Gdx.graphics.getDeltaTime() * 20f / text_opacity;

            text_gameStates.setOriginCenter();
            text_gameStates.setColor(1f, 1f, 1f, text_opacity);
            text_gameStates.setBounds(0f, 0f, width, height);
            text_gameStates.setPosition(SCREEN_WIDTH/2f - width/2f, yPos - height/2f);

            text_gameStates.draw(sBatch);

            //Assets.font.draw(sBatch, "" + player.ACCEL_Y_OFFSET, 1150, 775);
        }
    }

    public void updatePlacementNumber(float delta) {
        int pos = 0;

        for(Opponent o : world.opponents) {
            if(o.distance > player.distance) pos++;
        }


        text_placementNumber = Assets.sprites_text_race_positions.get(pos);
        text_placementNumber.setPosition(10f, 10f);
        text_placementNumber.setColor(1f, 1f, 1f, HUD_opacity);
    }

    public void drawPlacementNumber() {
        text_placementNumber.draw(sBatch);
    }

    public void updateSpeedometer(float delta) {
        speedometer.setColor(1f, 1f, 1f, HUD_opacity);
    }

    public void drawSpeedometer() {
        speedometer.draw(sBatch);
    }
}
