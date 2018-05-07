package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.utils.I18NBundle;

import static fi.tamk.tiko.harecraft.GameMain.sBatch;
import static fi.tamk.tiko.harecraft.GameMain.shapeRenderer;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.EXIT;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.FINISH;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.GameScreen.gameStateTime;
import static fi.tamk.tiko.harecraft.GameScreen.countdown;
import static fi.tamk.tiko.harecraft.GameScreen.isTransition;
import static fi.tamk.tiko.harecraft.GameScreen.paused;
import static fi.tamk.tiko.harecraft.GameScreen.playerPlacement;
import static fi.tamk.tiko.harecraft.GameScreen.playerScore;
import static fi.tamk.tiko.harecraft.GameScreen.layout;
import static fi.tamk.tiko.harecraft.GameScreen.selectedScreen;
import static fi.tamk.tiko.harecraft.GameScreen.stage;
import static fi.tamk.tiko.harecraft.GameScreen.game;
import static fi.tamk.tiko.harecraft.MainMenu.localizationBundle;
import static fi.tamk.tiko.harecraft.Shaders2D.shader2D_default;
import static fi.tamk.tiko.harecraft.World.player;

/**
 * Created by Mika on 15/03/2018.
 *
 * HUD class.
 */

public class HUD {
    World world;
    final GameScreen gameScreen;

    float progressline_x;
    float progressline_y = SCREEN_HEIGHT - 75f;
    float progressline_color_red = 255f;
    float progressline_color_green = 130f;
    float progressline_width = 300f;
    float progressline_arc_radius = 9f;
    float HUD_opacity;
    float yPos = SCREEN_HEIGHT/1.5f;

    Sprite text_gameStates = new Sprite();
    Sprite text_placementNumber = new Sprite();

    float text_opacity = 0f;
    int index = 0;

    public HUD(World world, final GameScreen gameScreen) {
        this.world = world;
        this.gameScreen = gameScreen;
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(
                Assets.skin_menu.getDrawable("listbutton"),
                Assets.skin_menu.getDrawable("listbutton pressed"),
                Assets.skin_menu.getDrawable("listbutton"),
                Assets.font4);
        style.pressedOffsetX = 4;
        style.pressedOffsetY = -4;
        style.downFontColor = new Color(0.59f,0.59f,0.59f,1f);
        style.fontColor = new Color(1f,1f,1f,1f);
        TextButton btnResume = new TextButton(localizationBundle.get("btnResumeText"), style);
        if(localizationBundle.get("btnResumeText").equals("continue")) btnResume.setWidth(360f);
        else btnResume.setWidth(300f);
        btnResume.setHeight(160f);
        if(SCREEN_WIDTH >= 1280f) btnResume.setPosition(SCREEN_WIDTH/2f - btnResume.getWidth()/2f, 0.85f/2f * SCREEN_HEIGHT);
        else btnResume.setPosition(SCREEN_WIDTH/2f - btnResume.getWidth()/2f, 0.55f/2f * SCREEN_HEIGHT);
        btnResume.setName("btnResume");
        btnResume.addListener(new InputListener() {
            Boolean touched = false;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { //touchdown täytyy palauttaa true jotta touchup voi toimia
                touched = true;
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (touched) {
                    paused = false;
                    AssetsAudio.resumeSound(AssetsAudio.SOUND_AIRPLANE_ENGINE);
                    Gdx.input.setInputProcessor(new GestureDetector(gameScreen));
                }
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor button)
            {
                touched = false;
            }
        });

        style = new TextButton.TextButtonStyle(
                Assets.skin_menu.getDrawable("button"),
                Assets.skin_menu.getDrawable("button pressed"),
                Assets.skin_menu.getDrawable("button"),
                Assets.font6);
        style.pressedOffsetX = 4;
        style.pressedOffsetY = -4;
        style.downFontColor = new Color(0.59f,0.59f,0.59f,1f);
        style.fontColor = new Color(1f,1f,1f,1f);
        TextButton btnReset = new TextButton(localizationBundle.get("btnResetText"), style);
        btnReset.setWidth(210f);
        btnReset.setHeight(100f);
        if(SCREEN_WIDTH >= 1280f) btnReset.setPosition(SCREEN_WIDTH/2f - btnReset.getWidth()/2f, 0.5f/2f * SCREEN_HEIGHT);
        else btnReset.setPosition(2f/3f*SCREEN_WIDTH - btnReset.getWidth()/2f, 0.15f/2f * SCREEN_HEIGHT);
        btnReset.setName("btnReset");
        btnReset.addListener(new InputListener() {
            Boolean touched = false;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { //touchdown täytyy palauttaa true jotta touchup voi toimia
                touched = true;
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (touched) {
                    paused = false;
                    gameState = EXIT;
                    gameStateTime = 0f;
                    selectedScreen = GameScreen.NEW_GAME;
                    Gdx.input.setInputProcessor(null);
                }
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor button)
            {
                touched = false;
            }
        });

        TextButton btnQuit = new TextButton(localizationBundle.get("btnQuitText"), style);
        btnQuit.setWidth(210f);
        btnQuit.setHeight(100f);
        if(SCREEN_WIDTH >= 1280f) btnQuit.setPosition(SCREEN_WIDTH/2f - btnQuit.getWidth()/2f, 0.2f/2f * SCREEN_HEIGHT);
        else btnQuit.setPosition(1f/3f*SCREEN_WIDTH - btnReset.getWidth()/2f, 0.15f/2f * SCREEN_HEIGHT);
        btnQuit.setName("btnQuit");
        btnQuit.addListener(new InputListener() {
            Boolean touched = false;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { //touchdown täytyy palauttaa true jotta touchup voi toimia
                touched = true;
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (touched) {
                    paused = false;
                    gameState = EXIT;
                    gameStateTime = 0f;
                    selectedScreen = GameScreen.MAIN_MENU;
                    Gdx.input.setInputProcessor(null);
                }
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor button)
            {
                touched = false;
            }
        });

        stage.addActor(btnResume);
        stage.addActor(btnReset);
        stage.addActor(btnQuit);
    }

    public void update(float delta) {
        if(gameState == RACE || gameState == FINISH) {
            HUD_opacity += delta;
            if(HUD_opacity > 1f) HUD_opacity = 1f;
        }
        else if(gameState == END) {
            HUD_opacity -= delta;
            if(HUD_opacity < 0f) HUD_opacity = 0f;
            if(text_opacity == 0f && gameStateTime > 5f) {
                sBatch.setShader(shader2D_default);
                AssetsAudio.stopMusic();
                game.setScreen(new ScoreScreen());
            }
        }

        if(gameState != START) {
            if(gameState != END) updateProgressLine(delta);
            updatePlacementNumber(delta);
        }
    }

    public void draw() {
        Gdx.gl.glEnable(Gdx.gl20.GL_BLEND);

        if(paused) {
            drawPauseShade();
            stage.act();
            stage.draw();
        }
        else {
            if (gameState != START) {
                drawProgressLine();
            }
            sBatch.begin();
            drawCountdown();
            if (gameState != START) {
                drawPlacementNumber();
            }
            sBatch.end();
        }
    }

    public void drawPauseShade() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.75f);
        shapeRenderer.rect(0f,0f, SCREEN_WIDTH, SCREEN_HEIGHT);
        shapeRenderer.end();

        sBatch.begin();
        Assets.font1.setColor(1f,1f,1f,1f);
        layout.setText(Assets.font1, localizationBundle.get("pauseText"));
        float width = layout.width;
        Assets.font1.draw(sBatch, localizationBundle.get("pauseText"), SCREEN_WIDTH/2f - width/2f,6f/7f*SCREEN_HEIGHT);
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
                    }
                    index = 2;
                } else if (gameStateTime < 4.6f) {
                    if(index == 2) {
                        yPos = SCREEN_HEIGHT/1.5f;
                        text_opacity = 0f;
                        AssetsAudio.playSound(AssetsAudio.SOUND_COUNTDOWN,0.6f);
                    }
                    index = 1;
                } else if (gameStateTime < 5.9f) {
                    if(index == 1) {
                        yPos = SCREEN_HEIGHT/1.5f;
                        text_opacity = 0f;
                        AssetsAudio.playSound(AssetsAudio.SOUND_COUNTDOWN,0.6f);
                    }
                    index = 0;
                } else {
                    if(index == 0) {
                        yPos = SCREEN_HEIGHT/1.5f;
                        text_opacity = 0f;
                        AssetsAudio.playSound(AssetsAudio.SOUND_COUNTDOWN_END,1f);
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
        }
    }

    public void updatePlacementNumber(float delta) {
        text_placementNumber = Assets.sprites_text_race_positions.get(playerPlacement - 1);
        text_placementNumber.setPosition(10f, 10f);
        text_placementNumber.setColor(1f, 1f, 1f, HUD_opacity);
    }

    public void drawPlacementNumber() {
        text_placementNumber.draw(sBatch);
    }
}
