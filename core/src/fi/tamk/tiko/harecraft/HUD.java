package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import static fi.tamk.tiko.harecraft.GameMain.sBatch;
import static fi.tamk.tiko.harecraft.GameMain.shapeRenderer;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.EXIT;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.FINISH;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.balloonsCollected;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.GameScreen.gameStateTime;
import static fi.tamk.tiko.harecraft.GameScreen.countdown;
import static fi.tamk.tiko.harecraft.GameScreen.paused;
import static fi.tamk.tiko.harecraft.GameScreen.playerPlacement;
import static fi.tamk.tiko.harecraft.GameScreen.layout;
import static fi.tamk.tiko.harecraft.GameScreen.ringsCollected;
import static fi.tamk.tiko.harecraft.GameScreen.selectedScreen;
import static fi.tamk.tiko.harecraft.GameScreen.stage;
import static fi.tamk.tiko.harecraft.GameScreen.game;
import static fi.tamk.tiko.harecraft.GameScreen.worldIndex;
import static fi.tamk.tiko.harecraft.MainMenu.localizationBundle;
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
    float progressline_y = SCREEN_HEIGHT/(1.105f);
    float progressline_color_red = 255f;
    float progressline_color_green = 130f;
    float progressline_width = SCREEN_WIDTH/4f;
    float progressline_arc_radius = 11.5f * (SCREEN_HEIGHT/800f);
    float progressline_arc_radius2 = 3.5f * (SCREEN_HEIGHT/800f);
    float opacity = 0f;
    float stateOpacity = 0f;
    int count;
    float yPos = SCREEN_HEIGHT/1.15f;
    float yPos2 = 0;

    float width;
    float height;

    float x;
    float y;
    float y2 = 0;
    float backdropWidth = SCREEN_WIDTH/6.2f;
    float backdropRadius = SCREEN_HEIGHT/30f;
    float backdropHeight = backdropRadius*2f;
    float adjustOpacity = 0f;

    ParticleEffect pfx_placement = Assets.pfx_placement;
    ParticleEffect pfx_placement1 = Assets.pfx_placement1;
    ParticleEffect pfx_placement2 = Assets.pfx_placement2;
    ParticleEffect pfx_placement3 = Assets.pfx_placement3;

    TextureRegion stateRegion;
    TextureRegion placementRegion;
    TextureRegion ringRegion;
    TextureRegion balloonRegion;
    TextureRegion arrowsRegion;
    TextureRegion adjustingRegion;
    TextureRegion pauseRegion = Assets.texR_pause;
    String language;

    public HUD(World world, final GameScreen gameScreen) {
        this.world = world;
        this.gameScreen = gameScreen;

        if(SCREEN_WIDTH < 1280f) {
            backdropWidth = SCREEN_WIDTH/5.5f;
            progressline_arc_radius = 9f * (SCREEN_HEIGHT/800f) * 1.4f;
            progressline_arc_radius2 = 3f * (SCREEN_HEIGHT/800f) * 1.4f;
            progressline_y = SCREEN_HEIGHT/(1.115f);
        }
        if(SCREEN_HEIGHT < 800f) {
            yPos = SCREEN_HEIGHT/1.1f;
        }

        switch (worldIndex) {
            case 0:
                ringRegion = Assets.texR_ring0;
                balloonRegion = Assets.texR_balloon_red;
                arrowsRegion = Assets.texR_ring_arrows2;
                break;
            case 1:
                ringRegion = Assets.texR_ring2;
                balloonRegion = Assets.texR_balloon_orange;
                arrowsRegion = Assets.texR_ring_arrows0;
                break;
            case 2:
                ringRegion = Assets.texR_ring1;
                balloonRegion = Assets.texR_balloon_blue;
                arrowsRegion = Assets.texR_ring_arrows1;
                break;
            default:
                break;
        }

        if(Gdx.app.getPreferences("ProfileFile").getString("Language").contains("Finnish")) {
            adjustingRegion = Assets.texR_asettaa;
            language = "_fi";
        }
        else{
            adjustingRegion = Assets.texR_adjusting;
            language = "_en";
        }


        pfx_placement.allowCompletion();
        pfx_placement.getEmitters().get(0).getXScale().setHighMin(120f * (SCREEN_WIDTH/1280f) * 1.25f);
        pfx_placement.getEmitters().get(0).getXScale().setHighMax(240f * (SCREEN_WIDTH/1280f) * 1.25f);

        pfx_placement1.allowCompletion();
        pfx_placement1.getEmitters().get(0).getXScale().setHighMin(90f * (SCREEN_WIDTH/1280f) * 1.25f);
        pfx_placement1.getEmitters().get(0).getXScale().setHighMax(200f * (SCREEN_WIDTH/1280f) * 1.25f);

        pfx_placement2.allowCompletion();
        pfx_placement2.getEmitters().get(0).getXScale().setHighMin(90f * (SCREEN_WIDTH/1280f) * 1.25f);
        pfx_placement2.getEmitters().get(0).getXScale().setHighMax(200f * (SCREEN_WIDTH/1280f) * 1.25f);

        pfx_placement3.allowCompletion();
        pfx_placement3.getEmitters().get(0).getXScale().setHighMin(90f * (SCREEN_WIDTH/1280f) * 1.25f);
        pfx_placement3.getEmitters().get(0).getXScale().setHighMax(200f * (SCREEN_WIDTH/1280f) * 1.25f);

        TextButton.TextButtonStyle style;
        if(SCREEN_WIDTH >= 1600f) {
            style = new TextButton.TextButtonStyle(
                    Assets.skin_menu.getDrawable("listbutton"),
                    Assets.skin_menu.getDrawable("listbutton pressed"),
                    Assets.skin_menu.getDrawable("listbutton"),
                    Assets.font2);
            style.pressedOffsetX = 4;
            style.pressedOffsetY = -4;
            style.downFontColor = new Color(0.59f, 0.59f, 0.59f, 1f);
            style.fontColor = new Color(1f, 1f, 1f, 1f);
        }
        else {
            style = new TextButton.TextButtonStyle(
                    Assets.skin_menu.getDrawable("listbutton"),
                    Assets.skin_menu.getDrawable("listbutton pressed"),
                    Assets.skin_menu.getDrawable("listbutton"),
                    Assets.font4);
            style.pressedOffsetX = 4;
            style.pressedOffsetY = -4;
            style.downFontColor = new Color(0.59f, 0.59f, 0.59f, 1f);
            style.fontColor = new Color(1f, 1f, 1f, 1f);
        }
        layout.setText(style.font, localizationBundle.get("btnResumeText"));
        width = layout.width;
        height = layout.height;

        TextButton btnResume = new TextButton(localizationBundle.get("btnResumeText"), style);
        btnResume.setWidth(width * 1.25f);
        btnResume.setHeight(height * 3.2f);
        if(SCREEN_WIDTH >= 1280f) btnResume.setPosition(SCREEN_WIDTH/2f - btnResume.getWidth()/2f, 0.8f/2f * SCREEN_HEIGHT);
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

        if(SCREEN_WIDTH >= 1600f) {
            style = new TextButton.TextButtonStyle(
                    Assets.skin_menu.getDrawable("button"),
                    Assets.skin_menu.getDrawable("button pressed"),
                    Assets.skin_menu.getDrawable("button"),
                    Assets.font5);
            style.pressedOffsetX = 4;
            style.pressedOffsetY = -4;
            style.downFontColor = new Color(0.59f, 0.59f, 0.59f, 1f);
            style.fontColor = new Color(1f, 1f, 1f, 1f);
        }
        else {
            style = new TextButton.TextButtonStyle(
                    Assets.skin_menu.getDrawable("button"),
                    Assets.skin_menu.getDrawable("button pressed"),
                    Assets.skin_menu.getDrawable("button"),
                    Assets.font6);
            style.pressedOffsetX = 4;
            style.pressedOffsetY = -4;
            style.downFontColor = new Color(0.59f, 0.59f, 0.59f, 1f);
            style.fontColor = new Color(1f, 1f, 1f, 1f);
        }
        layout.setText(style.font, localizationBundle.get("btnResetText"));
        width = layout.width;
        height = layout.height;

        TextButton btnReset = new TextButton(localizationBundle.get("btnResetText"), style);
        btnReset.setWidth(width * 1.4f);
        btnReset.setHeight(height * 3.4f);
        if(SCREEN_WIDTH >= 1280f) btnReset.setPosition(SCREEN_WIDTH/2f - btnReset.getWidth()/2f, 0.5f/2f * SCREEN_HEIGHT);
        else btnReset.setPosition(1.85f/3f*SCREEN_WIDTH - btnReset.getWidth()/2f, 0.15f/2f * SCREEN_HEIGHT);
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
        btnQuit.setWidth(width * 1.4f);
        btnQuit.setHeight(height * 3.4f);
        if(SCREEN_WIDTH >= 1280f) btnQuit.setPosition(SCREEN_WIDTH/2f - btnQuit.getWidth()/2f, 0.2f/2f * SCREEN_HEIGHT);
        else btnQuit.setPosition(1.15f/3f*SCREEN_WIDTH - btnReset.getWidth()/2f, 0.15f/2f * SCREEN_HEIGHT);
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
            opacity += delta;
            if(opacity > 1f) opacity = 1f;
        }
        else if(gameState == END) {
            opacity -= delta;
            if(opacity < 0f) opacity = 0f;
            if(opacity == 0f && gameStateTime > 5f) {
                sBatch.setShader(null);
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
        else if(gameState != EXIT) {
            if(gameState != START) {
                drawProgressLine();
                drawBackdrop();
            }
            sBatch.begin();
                drawState();
                if(gameState != START) {
                    drawPlacementNumber();
                    drawCollectables();
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
        if(SCREEN_WIDTH >= 1600f) {
            Assets.font0.setColor(1f, 1f, 1f, 1f);
            layout.setText(Assets.font0, localizationBundle.get("pauseText"));
            width = layout.width;
            Assets.font0.draw(sBatch, localizationBundle.get("pauseText"), SCREEN_WIDTH / 2f - width / 2f, 6.3f / 7f * SCREEN_HEIGHT);
        }
        else {
            Assets.font1.setColor(1f, 1f, 1f, 1f);
            layout.setText(Assets.font1, localizationBundle.get("pauseText"));
            width = layout.width;
            Assets.font1.draw(sBatch, localizationBundle.get("pauseText"), SCREEN_WIDTH / 2f - width / 2f, 6f / 7f * SCREEN_HEIGHT);
        }
        sBatch.end();
    }

    public void updateProgressLine(float delta) {
        progressline_x = SCREEN_WIDTH/4f * (player.distance / world.end);
        progressline_color_green = (130f + (125f * (player.distance / world.end))) / 255f; //110 + 160 = 270
        progressline_color_red = (255f - ((255f - 95f) * (player.distance / world.end))) / 255f;

        if(progressline_color_red < 0f) progressline_color_red = 0f;
        if(progressline_color_green > 1f) progressline_color_green = 1f;
    }

    public void drawProgressLine() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //Black
        shapeRenderer.setColor(0f, 0f, 0f, 0.3f * opacity);
        shapeRenderer.arc(SCREEN_WIDTH/2f - progressline_width/2f, progressline_y + progressline_arc_radius + progressline_arc_radius2, progressline_arc_radius + progressline_arc_radius2, 90f, 180f);
        shapeRenderer.arc(SCREEN_WIDTH/2f + progressline_width/2f, progressline_y + progressline_arc_radius + progressline_arc_radius2, progressline_arc_radius + progressline_arc_radius2, 270f, 180f);
        shapeRenderer.rect(SCREEN_WIDTH/2f - progressline_width/2f, progressline_y, progressline_width, (progressline_arc_radius + progressline_arc_radius2)*2f);

        //Color
        shapeRenderer.setColor(progressline_color_red, progressline_color_green, 44f/255f, 0.7f * opacity);
        shapeRenderer.arc(SCREEN_WIDTH/2f - progressline_width/2f, progressline_y + progressline_arc_radius + progressline_arc_radius2, progressline_arc_radius, 90f, 180f);
        shapeRenderer.rect(SCREEN_WIDTH/2f - progressline_width/2f, progressline_y + progressline_arc_radius2, progressline_x, progressline_arc_radius*2f);
        shapeRenderer.arc(SCREEN_WIDTH/2f - progressline_width/2f + progressline_x, progressline_y + progressline_arc_radius + progressline_arc_radius2, progressline_arc_radius, 270f, 180f);

        //White
        shapeRenderer.setColor(1f, 1f, 1f, 0.6f * opacity);
        shapeRenderer.circle(SCREEN_WIDTH/2f - progressline_width/2f + progressline_x, progressline_y + progressline_arc_radius + progressline_arc_radius2, progressline_arc_radius);
        shapeRenderer.end();
    }

    private void resetY() {
        yPos = SCREEN_HEIGHT/1.15f;
        if(SCREEN_HEIGHT < 800f) yPos = SCREEN_HEIGHT/1.1f;
        if(gameState == END) {
            yPos = SCREEN_HEIGHT/1.3f;
            if(SCREEN_HEIGHT < 800f) yPos = SCREEN_HEIGHT/1.2f;
        }
        stateOpacity = 0f;
        count++;
    }

    public void drawBackdrop() {
        width = ringRegion.getRegionWidth()/10f * SCREEN_WIDTH/1280f;
        height = ringRegion.getRegionHeight()/10f * SCREEN_WIDTH/1280f;

        x = SCREEN_WIDTH/1.308f;
        y = SCREEN_HEIGHT/1.1f;
        y2 = 0;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.3f * opacity);
        shapeRenderer.arc(x + backdropRadius/4f, y + backdropHeight/4f, backdropRadius, 90f, 180f);
        shapeRenderer.arc(x + backdropWidth + backdropRadius/4f, y + backdropHeight/4f, backdropRadius, 270f, 180f);
        shapeRenderer.rect(x + backdropRadius/4f, y - backdropRadius + backdropHeight/4f, backdropWidth, backdropHeight);
        shapeRenderer.end();
    }

    public void drawCollectables() {
        width = ringRegion.getRegionWidth()/6f * SCREEN_WIDTH/1920f;
        height = ringRegion.getRegionHeight()/6f * SCREEN_WIDTH/1920f;

        x = SCREEN_WIDTH/1.3f;
        y = SCREEN_HEIGHT/1.1f;
        if(SCREEN_HEIGHT < 800f) y = SCREEN_HEIGHT/1.105f;
        y2 = 0;

        sBatch.setColor(1f,1f,1f, opacity);

        sBatch.draw(ringRegion, x, SCREEN_HEIGHT/1.108f, width, height);
        sBatch.draw(arrowsRegion, x, SCREEN_HEIGHT/1.108f, width, height);
        String rings = Integer.toString(ringsCollected);

        if(SCREEN_WIDTH >= 1600f) {
            Assets.font5.setColor(1f, 1f, 1f, opacity);
            layout.setText(Assets.font5, rings);
            float layoutHeight = layout.height;
            y2 = layoutHeight;
            Assets.font5.draw(sBatch, rings, x + width * 1.5f, y + y2);
            Assets.font5.setColor(1f, 1f, 1f, 1f);
        }
        else {
            Assets.font6.setColor(1f, 1f, 1f, opacity);
            layout.setText(Assets.font6, rings);
            float layoutHeight = layout.height;
            y2 = layoutHeight;
            Assets.font6.draw(sBatch, rings, x + width * 1.5f, y + y2);
            Assets.font5.setColor(1f, 1f, 1f, 1f);
        }

        width = balloonRegion.getRegionWidth()/7f * SCREEN_WIDTH/1920f;
        height = balloonRegion.getRegionHeight()/7f * SCREEN_WIDTH/1920f;
        x = SCREEN_WIDTH/1.16f;

        sBatch.draw(balloonRegion, x, y - height/4f, width, height);
        String balloons = Integer.toString(balloonsCollected) + "/3";
        if(SCREEN_WIDTH > 1600f) {
            Assets.font5.setColor(1f, 1f, 1f, opacity);
            Assets.font5.draw(sBatch, balloons, x + width * 1.5f, y + y2);
            Assets.font5.setColor(1f, 1f, 1f, 1f);
        }
        else {
            Assets.font6.setColor(1f, 1f, 1f, opacity);
            Assets.font6.draw(sBatch, balloons, x + width * 1.5f, y + y2);
            Assets.font6.setColor(1f, 1f, 1f, 1f);
        }

        width = pauseRegion.getRegionWidth()/1.5f * SCREEN_WIDTH/1920f;
        height = pauseRegion.getRegionHeight()/1.5f * SCREEN_WIDTH/1920f;
        x = SCREEN_WIDTH/1.11f;
        y = SCREEN_HEIGHT/30f;

        sBatch.draw(pauseRegion, x, y, width, height);

        sBatch.setColor(1f,1f,1f,1f);
    }

    public void drawState() {
        stateRegion = Assets.atlas_1.findRegion("go" + language);
        if(countdown) {
            if(gameState == START) {
                if(gameStateTime < 3.3f) {
                    if(count == 0) {
                        resetY();
                    }
                    stateRegion = Assets.atlas_1.findRegion("3");

                }else if (gameStateTime < 4.6f) {
                    if(count == 1) {
                        resetY();
                        AssetsAudio.playSound(AssetsAudio.SOUND_COUNTDOWN,0.6f);
                    }
                    stateRegion = Assets.atlas_1.findRegion("2");

                }else if (gameStateTime < 5.9f) {
                    if(count == 2) {
                        resetY();
                        AssetsAudio.playSound(AssetsAudio.SOUND_COUNTDOWN,0.6f);
                    }
                    stateRegion = Assets.atlas_1.findRegion("1");

                }else {
                    if(count == 3) {
                        resetY();
                        AssetsAudio.playSound(AssetsAudio.SOUND_COUNTDOWN_END,1f);
                    }
                    stateRegion = Assets.atlas_1.findRegion("go" + language);
                }

                stateOpacity += Gdx.graphics.getDeltaTime();
                if(stateOpacity > 1f) stateOpacity = 1f;
            }
            if(gameState == RACE) {
                stateOpacity -= Gdx.graphics.getDeltaTime();
                if(stateOpacity < 0f) stateOpacity = 0f;
                if(stateOpacity == 0f) countdown = false;
            }
        }
        else if(gameState == END && gameStateTime < 4f) {
            if(count == 4) {
                resetY();
            }
            stateRegion = Assets.atlas_1.findRegion("finish" + language);

            if(gameStateTime < 1.7f) {
                stateOpacity += Gdx.graphics.getDeltaTime();
                if (stateOpacity > 1f) stateOpacity = 1f;
            }
            else {
                stateOpacity -= Gdx.graphics.getDeltaTime();
                if(stateOpacity < 0f) stateOpacity = 0f;
            }
        }

        yPos -= Gdx.graphics.getDeltaTime() * 20f / stateOpacity;
        width = stateRegion.getRegionWidth() * 1.65f * (SCREEN_WIDTH/1280f);
        height = stateRegion.getRegionHeight() * 1.65f * (SCREEN_WIDTH/1280f);

        x = SCREEN_WIDTH/2f - width/2f;
        y = yPos - height/2f;

        sBatch.setColor(1f,1f,1f, stateOpacity);
        sBatch.draw(stateRegion, x, y, width, height);
        sBatch.setColor(1f,1f,1f, 1f);

        adjustOpacity = 0f;

        if(gameState == START || gameState == RACE) {
            if(count == 1) adjustOpacity = stateOpacity;
            else if(gameState == RACE){
                adjustOpacity = stateOpacity;
                yPos2 -= Gdx.graphics.getDeltaTime() * 20f / stateOpacity;
            }
            else if(count == 2 || count == 3 || count == 4) adjustOpacity = 1f;
            else adjustOpacity = 0f;

        }

        sBatch.setColor(1f,1f,1f, adjustOpacity);
        sBatch.draw(adjustingRegion, SCREEN_WIDTH/2f - adjustingRegion.getRegionWidth()* SCREEN_WIDTH/1920f/2f, SCREEN_HEIGHT/4f - adjustingRegion.getRegionHeight()* SCREEN_WIDTH/1920f/2f + yPos2, adjustingRegion.getRegionWidth() * SCREEN_WIDTH/1920f, adjustingRegion.getRegionHeight() * SCREEN_WIDTH/1920f);
        sBatch.setColor(1f,1f,1f, 1f);
    }

    public void updatePlacementNumber(float delta) {
        if(gameState == RACE || gameState == FINISH) placementRegion = Assets.atlas_1.findRegion(playerPlacement + language);

        if(gameState != END && gameState != EXIT && gameState != START) {
            if(playerPlacement == 1) {
                pfx_placement.start();
                pfx_placement1.start();
                pfx_placement2.allowCompletion();
            }
            else if(playerPlacement == 2) {
                pfx_placement.start();
                pfx_placement2.start();
                pfx_placement1.allowCompletion();
                pfx_placement3.allowCompletion();
            }
            else if(playerPlacement == 3) {
                pfx_placement.start();
                pfx_placement3.start();
                pfx_placement2.allowCompletion();
            }
            else {
                pfx_placement.allowCompletion();
                pfx_placement1.allowCompletion();
                pfx_placement2.allowCompletion();
                pfx_placement3.allowCompletion();
            }
        }
        else {
            pfx_placement.allowCompletion();
            pfx_placement1.allowCompletion();
            pfx_placement2.allowCompletion();
            pfx_placement3.allowCompletion();
        }

        pfx_placement.update(delta);
        pfx_placement1.update(delta);
        pfx_placement2.update(delta);
        pfx_placement3.update(delta);
    }

    public void drawPlacementNumber() {
        pfx_placement1.draw(sBatch);
        pfx_placement2.draw(sBatch);
        pfx_placement3.draw(sBatch);
        pfx_placement.draw(sBatch);

        width = placementRegion.getRegionWidth() * 1.65f * (SCREEN_WIDTH/1280f);
        height = placementRegion.getRegionHeight() * 1.65f * (SCREEN_WIDTH/1280f);

        sBatch.setColor(1f,1f,1f, opacity);
        sBatch.draw(placementRegion,30f,15f, width, height);
        sBatch.setColor(1f,1f,1f, 1f);

        pfx_placement.setPosition(width/2.75f, height/2f);
        pfx_placement1.setPosition(width/2.75f, height/2f);
        pfx_placement2.setPosition(width/2.75f, height/2f);
        pfx_placement3.setPosition(width/2.75f, height/2f);
    }
}
