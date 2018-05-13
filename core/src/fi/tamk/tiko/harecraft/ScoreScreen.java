package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import static fi.tamk.tiko.harecraft.GameMain.fbo;
import static fi.tamk.tiko.harecraft.GameMain.musicVolume;
import static fi.tamk.tiko.harecraft.GameMain.orthoCamera;
import static fi.tamk.tiko.harecraft.GameMain.sBatch;
import static fi.tamk.tiko.harecraft.GameMain.shapeRenderer;
import static fi.tamk.tiko.harecraft.GameMain.texture;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.EXIT;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.balloonsCollected;
import static fi.tamk.tiko.harecraft.GameScreen.game;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.GameScreen.gameStateTime;
import static fi.tamk.tiko.harecraft.GameScreen.layout;
import static fi.tamk.tiko.harecraft.GameScreen.paused;
import static fi.tamk.tiko.harecraft.GameScreen.playerPlacement;
import static fi.tamk.tiko.harecraft.GameScreen.playerScore;
import static fi.tamk.tiko.harecraft.GameScreen.ringsCollected;
import static fi.tamk.tiko.harecraft.GameScreen.stage;
import static fi.tamk.tiko.harecraft.GameScreen.world;
import static fi.tamk.tiko.harecraft.GameScreen.worldIndex;
import static fi.tamk.tiko.harecraft.GameScreen.worldScore;
import static fi.tamk.tiko.harecraft.MainMenu.localizationBundle;
import static fi.tamk.tiko.harecraft.MainMenu.profiles;


public class ScoreScreen extends ScreenAdapter {
    Stage stage;
    OrthographicCamera camera;
    Group grpLevelScore;
    Group grpObjects;
    Group grpButton;
    GlyphLayout layout = new GlyphLayout();

    float scoreboard_opacity;
    float yPos = SCREEN_HEIGHT/1.5f;
    float originalLineHeight3 = Assets.font5.getLineHeight();
    float originalLineHeight4 = Assets.font6.getLineHeight();

    float width, height;
    float opacity = 0f;
    float stateTime;
    float accelerator = 1f;
    float scoreCounterTime = 0f;
    boolean isCourseScoreCounted = true;
    boolean isTotalScoreCounted;
    boolean isTransition = false;
    boolean isTransitionComplete = false;
    boolean isTransitionFromComplete = false;
    boolean isLastTime;

    private final int MAIN_MENU = 0;
    private final int LEVEL_MENU = 1;
    private final int NEW_GAME = 2;

    private int selectedScreen = -1;

    Preferences profilesData; //diu
    int countingTotalScore;
    int countingScore = 1;

    float lastTime;
    int gainedScore = playerScore;
    float shitCounter;

    Label ringAmount;
    Label balloonAmount;
    Label scoreCourse;
    Label scoreTotal;
    Image imgPlacement;
    Image imgDifficulty;

    boolean flip;
    float lastScore;
    int shitcountererere = 11;

    ParticleEffect pfx_points = Assets.pfx_points;

    public ScoreScreen() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 800);
        stage = new Stage(new StretchViewport(1280, 800, camera));

        Gdx.input.setInputProcessor(stage);

        profilesData = Gdx.app.getPreferences("ProfileFile"); // KEY ja VALUE //diu
        //oldTotalScore = profilesData.getInteger(ProfileInfo.selectedPlayerProfile +"Score", 0);
        //oldTotalScore -= playerScore;

        Assets.font5.getData().setLineHeight(Assets.font5.getLineHeight()/1.3f);
        Assets.font6.getData().setLineHeight(Assets.font6.getLineHeight()/1.3f);
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(
                Assets.skin_menu.getDrawable("listbutton"),
                Assets.skin_menu.getDrawable("listbutton pressed"),
                Assets.skin_menu.getDrawable("listbutton"),
                Assets.font5);
        style.pressedOffsetX = 4;
        style.pressedOffsetY = -4;
        style.downFontColor = new Color(0.59f,0.59f,0.59f,1f);
        style.fontColor = new Color(1f,1f,1f,1f);


        TextButton btnNewGame = new TextButton(localizationBundle.get("btnNewGameText"), style);
        if(localizationBundle.get("btnNewGameText").equals("uusi\nlento")) btnNewGame.setWidth(280f);
        else btnNewGame.setWidth(280f);
        btnNewGame.setHeight(160f);
        btnNewGame.setName("btnNewGame");
        btnNewGame.addListener(new InputListener() {
            Boolean touched = false;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { //touchdown täytyy palauttaa true jotta touchup voi toimia
                touched = true;
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(touched && !isTransition) {
                    Assets.font5.getData().setLineHeight(originalLineHeight3);
                    Assets.font6.getData().setLineHeight(originalLineHeight4);

                    selectedScreen = NEW_GAME;
                    isTransitionFromComplete = true;
                    Gdx.input.setInputProcessor(null);
                    isTransition = true;
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


        TextButton btnCourseSelect = new TextButton(localizationBundle.get("btnCourseSelectText"), style);
        if(localizationBundle.get("btnNewGameText").equals("uusi\nlento")) btnCourseSelect.setWidth(240f);
        else btnCourseSelect.setWidth(220f);
        btnCourseSelect.setHeight(120f);
        btnCourseSelect.setName("btnReset");
        btnCourseSelect.addListener(new InputListener() {
            Boolean touched = false;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { //touchdown täytyy palauttaa true jotta touchup voi toimia
                touched = true;
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(touched && !isTransition) {
                    Assets.font5.getData().setLineHeight(originalLineHeight4);
                    Assets.font6.getData().setLineHeight(originalLineHeight3);

                    selectedScreen = LEVEL_MENU;
                    isTransitionFromComplete = true;
                    Gdx.input.setInputProcessor(null);
                    isTransition = true;
                }
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor button)
            {
                touched = false;
            }
        });


        TextButton btnMainMenu = new TextButton(localizationBundle.get("btnMainMenuText"), style);
        if(localizationBundle.get("btnNewGameText").equals("uusi\nlento")) btnMainMenu.setWidth(240f);
        else btnMainMenu.setWidth(220f);
        btnMainMenu.setHeight(120f);
        btnMainMenu.setName("btnMainMenu");
        btnMainMenu.addListener(new InputListener() {
            Boolean touched = false;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { //touchdown täytyy palauttaa true jotta touchup voi toimia
                touched = true;
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(touched && !isTransition) {
                    Assets.font5.getData().setLineHeight(originalLineHeight4);
                    Assets.font6.getData().setLineHeight(originalLineHeight3);

                    selectedScreen = MAIN_MENU;
                    isTransitionFromComplete = true;
                    Gdx.input.setInputProcessor(null);
                    isTransition = true;
                }
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor button)
            {
                touched = false;
            }
        });


        TextureRegion region = Assets.texR_backdrop;
        Image backdrop = new Image(region);
        backdrop.setPosition(640 - region.getRegionWidth()/2f, 500 - region.getRegionHeight()/2f);
        stage.addActor(backdrop);


        TextureRegion ringRegion;
        TextureRegion arrowsRegion;
        TextureRegion balloonRegion;
        grpObjects = new Group();
        switch (worldIndex) {
            case 0:
                ringRegion = Assets.texR_ring0;
                arrowsRegion = Assets.texR_ring_arrows2;
                balloonRegion = Assets.texR_balloon_red;
                break;
            case 1:
                ringRegion = Assets.texR_ring2;
                arrowsRegion = Assets.texR_ring_arrows0;
                balloonRegion = Assets.texR_balloon_orange;
                break;
            case 2:
                ringRegion = Assets.texR_ring1;
                arrowsRegion = Assets.texR_ring_arrows1;
                balloonRegion = Assets.texR_balloon_blue;
                break;
            default:
                ringRegion = Assets.texR_ring2;
                arrowsRegion = Assets.texR_ring_arrows0;
                balloonRegion = Assets.texR_balloon_orange;
                break;
        }
        Image ring = new Image(ringRegion);
        ring.setWidth(ringRegion.getRegionWidth()/4f);
        ring.setHeight(ringRegion.getRegionHeight()/4f);
        Image arrows = new Image(arrowsRegion);
        arrows.setWidth(arrowsRegion.getRegionWidth()/4f);
        arrows.setHeight(arrowsRegion.getRegionHeight()/4f);
        Image balloon = new Image(balloonRegion);
        balloon.setWidth(balloonRegion.getRegionWidth()/6f);
        balloon.setHeight(balloonRegion.getRegionHeight()/6f);

        balloon.setPosition(0f + balloonRegion.getRegionWidth()/12f,0f - balloonRegion.getRegionHeight()/6f);
        ring.setPosition(0f,0f - ringRegion.getRegionHeight()/4f - 125);
        arrows.setPosition(0f,0f - arrowsRegion.getRegionHeight()/4f - 125);

        Label.LabelStyle style2 = new Label.LabelStyle(
                Assets.font4,
                new Color(1f,1f,1f,1f)
        );
        balloonAmount = new Label(Integer.toString(balloonsCollected) + "/3", style2);
        balloonAmount.setPosition(120,-90);
        ringAmount = new Label(Integer.toString(ringsCollected) + "/" + Integer.toString(worldScore), style2);
        ringAmount.setPosition(120, -205);


        style2 = new Label.LabelStyle(
                Assets.font5,
                new Color(1f,1f,1f,1f)
        );

        Label placement = new Label(localizationBundle.get("txtPlacement"), style2);
        placement.setPosition(0f, -300f);

        Label length = new Label(localizationBundle.get("txtLevelLength"), style2);
        length.setPosition(0f, - 450);

        Label difficulty = new Label(localizationBundle.get("txtDifficulty"), style2);
        difficulty.setPosition(0f, - 375);

        String language;
        if(Gdx.app.getPreferences("ProfileFile").getString("Language").contains("Finnish")) {
            language = "_fi";
        }
        else language = "_en";

        layout.setText(placement.getStyle().font, placement.getText());
        width = layout.width;
        height = layout.height;

        region = Assets.atlas_1.findRegion(playerPlacement + language);
        imgPlacement = new Image(region);
        imgPlacement.setWidth(region.getRegionWidth()/1.5f);
        imgPlacement.setHeight(region.getRegionHeight()/1.5f);
        imgPlacement.setPosition(0f + width + 20,-300);


        grpObjects.setPosition(160, 727);
        grpObjects.addActor(ring);
        grpObjects.addActor(arrows);
        grpObjects.addActor(balloon);
        grpObjects.addActor(balloonAmount);
        grpObjects.addActor(ringAmount);
        grpObjects.addActor(placement);
        grpObjects.addActor(length);
        grpObjects.addActor(difficulty);
        grpObjects.addActor(imgPlacement);
        stage.addActor(grpObjects);


        grpLevelScore = new Group();
        style2 = new Label.LabelStyle(
                Assets.font4,
                new Color(1f,1f,1f,1f)
        );
        Label levelScore = new Label(localizationBundle.get("txtLevelScore"), style2);
        layout.setText(levelScore.getStyle().font, levelScore.getText());
        width = layout.width;
        height = layout.height;
        levelScore.setPosition(0f - width/2f,0f - height);


        style2 = new Label.LabelStyle(
                Assets.font1,
                new Color(1f,1f,1f,1f)
        );
        scoreCourse = new Label(("0"), style2);
        layout.setText(scoreCourse.getStyle().font, scoreCourse.getText());
        width = layout.width;
        height = layout.height;
        scoreCourse.setPosition(0f - width/2f,0f - height - 100);

        grpLevelScore.addActor(levelScore);
        grpLevelScore.addActor(scoreCourse);
        grpLevelScore.setPosition(882, 592);
        stage.addActor(grpLevelScore);


        grpButton = new Group();
        btnMainMenu.setPosition(0 - btnMainMenu.getWidth()/2f - btnNewGame.getWidth(),0);
        btnCourseSelect.setPosition(0 - btnCourseSelect.getWidth()/2f + btnNewGame.getWidth(),0);
        btnNewGame.setPosition(0f - btnNewGame.getWidth()/2f,0);
        grpButton.setPosition(640, 40);
        grpButton.addActor(btnNewGame);
        grpButton.addActor(btnCourseSelect);
        grpButton.addActor(btnMainMenu);
        stage.addActor(grpButton);


        pfx_points.setPosition(SCREEN_WIDTH/1.36f, SCREEN_HEIGHT/1.8f);
        pfx_points.getEmitters().first().getXScale().setHigh(65f * (SCREEN_WIDTH/1920f));
        pfx_points.allowCompletion();
    }

    public void countScore(float delta) {
        if(scoreCounterTime>=0.5f && !flip) flip = true;

        if(!flip) shitCounter = scoreCounterTime;
        else shitCounter = 0.5f + (0.5f - scoreCounterTime);

        countingScore = (int)(gainedScore * (shitCounter));


        if(countingScore >= playerScore) {
            isCourseScoreCounted = true;
            countingScore = playerScore;
            if(playerScore > 15) AssetsAudio.playSound(AssetsAudio.SOUND_POINTS_COUNTING, 0.6f);
            pfx_points.allowCompletion();
        }
        else if(countingScore > lastScore && (playerScore > 15)) {
            if(shitCounter < 0.99f && shitcountererere > 1) {
                shitcountererere = 0;
                AssetsAudio.playSound(AssetsAudio.SOUND_POINTS_COUNTING, 0.3f);
            }
            else if(shitCounter>0.99f) AssetsAudio.playSound(AssetsAudio.SOUND_POINTS_COUNTING, 0.3f);
            shitcountererere++;
        }

        lastScore = countingScore;

        layout.setText(scoreCourse.getStyle().font, scoreCourse.getText());
        width = layout.width;
        height = layout.height;
        scoreCourse.setPosition(0 -width/2f, 0f - height - 100);
        scoreCourse.setText(Integer.toString(countingScore));
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw(delta);
    }

    public void update(float delta) {
        stateTime += delta;

        accelerator -= delta/4f;
        if(accelerator < 0f) accelerator = 0f;

        scoreCounterTime += delta*2.7f;
        scoreCounterTime *= accelerator;

        if(!isCourseScoreCounted) countScore(delta);


        stage.act();
        if(!isTransitionFromComplete) transitionFromScreen(delta);
        if(isTransition) transitionToScreen(delta);
        if(isTransitionComplete) {
            switch(selectedScreen) {
                case MAIN_MENU:
                    worldIndex = -1;
                    game.setScreen(new MainMenu(game,true));
                    break;
                case LEVEL_MENU:
                    game.setScreen(new LevelSelectMenu(game, profiles,true));
                    break;
                case NEW_GAME:
                    game.setScreen(new GameScreen(game, GameScreen.worldIndex));
                    break;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            selectedScreen = MAIN_MENU;
            isTransitionFromComplete = true;
            Gdx.input.setInputProcessor(null);
            isTransition = true;
        }

        pfx_points.getEmitters().get(0).getEmission().setHigh(20f*(1f - shitCounter/1.5f));
        pfx_points.update(delta);
    }

    public void draw(float delta) {
        switch (worldIndex) {
            case 0:
                Gdx.gl.glClearColor(32/255f, 137/255f, 198/255f, 1f);
                break;
            case 1:
                Gdx.gl.glClearColor(137/255f, 189/255f, 255/255f, 1f);
                break;
            case 2:
                Gdx.gl.glClearColor(60/255f, 140/255f, 208/255f, 1f);
                break;
            default:
                Gdx.gl.glClearColor(68f/255f, 153f/255f, 223f/255f, 1f);
                break;
        }
        if(selectedScreen == MAIN_MENU || selectedScreen == LEVEL_MENU) Gdx.gl.glClearColor(68f/255f, 153f/255f, 223f/255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        fbo.begin();
            Gdx.gl.glClearColor(68f/255f, 153f/255f, 223f/255f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            stage.draw();
            sBatch.begin();
            pfx_points.draw(sBatch);
            sBatch.end();
        fbo.end();
        renderToTexture();
    }

    public void transitionToScreen(float delta) {
        opacity -= delta;
        if(opacity <= 0f) {
            opacity = 0f;
            isTransitionComplete = true;
        }
    }

    public void transitionFromScreen(float delta) {
        opacity += delta;
        if(opacity >= 1f) {
            opacity = 1f;
            isTransitionFromComplete = true;
            scoreCounterTime = 0f;
            accelerator = 1f;
            isCourseScoreCounted = false;
            pfx_points.reset();
        }
    }

    public void renderToTexture() {
        texture.setTexture(fbo.getColorBufferTexture());

        //------------------------------------------------
        sBatch.begin();
        texture.draw(sBatch, opacity);
        sBatch.end();
    }

    public void hide() {
        dispose();
    }

    public void dispose() {
        stage.dispose();
    }
}
