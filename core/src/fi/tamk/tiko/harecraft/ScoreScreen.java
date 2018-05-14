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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.ArrayList;

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

/**
 * Created by Mika on 28/02/2018.
 *
 * Not a lot of love went into making this class. It brought tears of sorrow and anguish to its maker. Lots of spilled spaghetti that stinks like sour milk in here.
 */

public class ScoreScreen extends ScreenAdapter {
    Stage stage;
    OrthographicCamera camera;
    Group grpLevelScore;
    Group grpTotalScore;
    Group grpObjects;
    Group grpButton;
    Group grpHighscore;
    Group grpHighscoreBox;
    GlyphLayout layout = new GlyphLayout();

    HighscoreBox highscoreBox;

    String[] top3Names = new String[3];
    int[] top3Score = new int[3];
    Table highScoreTable = new Table();

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
    boolean isTotalScoreCounted = true;
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
    float anotherShittyCounter;
    String playerName = ProfileInfo.selectedPlayerProfile;

    Label ringAmount;
    Label balloonAmount;
    Label scoreCourse;
    Label scoreTotal;

    Image imgPlacement;
    Image imgDifficulty;
    Image imgLength;
    Image imgHighscore;

    boolean flip;
    float lastScore;
    int shitcountererere = 11;
    int oldTotalScore;
    boolean isTimeToTransfer = false;
    float scoreOpacity = 1f;
    float highscoreOpacity = 0f;
    float highscoreTableOpacity = 0f;
    boolean isHighScored = true;

    ParticleEffect pfx_points = Assets.pfx_points;

    public ScoreScreen() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 800);
        stage = new Stage(new StretchViewport(1280, 800, camera));

        Gdx.input.setInputProcessor(stage);

        profilesData = Gdx.app.getPreferences("ProfileFile"); // KEY ja VALUE //diu
        oldTotalScore = profilesData.getInteger(ProfileInfo.selectedPlayerProfile +"Score", 0);
        oldTotalScore -= playerScore;

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

        layout.setText(difficulty.getStyle().font, difficulty.getText());
        width = layout.width;
        height = layout.height;

        region = Assets.atlas_1.findRegion(Integer.toString(ProfileInfo.selectedDifficulty + 1) + "x");
        imgDifficulty = new Image(region);
        imgDifficulty.setWidth(region.getRegionWidth()/2f);
        imgDifficulty.setHeight(region.getRegionHeight()/2f);
        imgDifficulty.setPosition(0f + width + 20, -371);

        layout.setText(length.getStyle().font, length.getText());
        width = layout.width;
        height = layout.height;

        region = Assets.atlas_1.findRegion(Integer.toString(ProfileInfo.selectedDuration/10) + "km");
        imgLength = new Image(region);
        imgLength.setWidth(region.getRegionWidth()/2.1f);
        imgLength.setHeight(region.getRegionHeight()/2.1f);
        imgLength.setPosition(0f + width + 20, -445);

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
        grpObjects.addActor(imgDifficulty);
        grpObjects.addActor(imgLength);
        stage.addActor(grpObjects);


        makeHighScores();
        highScoreTable.setPosition(200,80);

        style2 = new Label.LabelStyle(
                Assets.font5,
                new Color(1f,1f,1f,1f)
        );

        Label topPilotsLabel = new Label(localizationBundle.get("top3pilots"), style2);
        topPilotsLabel.setPosition(75,160);

        highscoreBox = new HighscoreBox();
        highscoreBox.setPosition(0, 0);
        highscoreBox.setColor(1f,1f,1f,0f);

        grpHighscoreBox = new Group();
        grpHighscoreBox.setPosition(670, 500f);

        grpHighscoreBox.addActor(highscoreBox);
        grpHighscoreBox.addActor(topPilotsLabel);
        grpHighscoreBox.addActor(highScoreTable);
        grpHighscoreBox.setColor(1f,1f,1f,0f);
        stage.addActor(grpHighscoreBox);


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

        Label total = new Label(localizationBundle.get("txtTotalScore"), style2);
        layout.setText(total.getStyle().font, total.getText());
        width = layout.width;
        height = layout.height;
        total.setPosition(0f - width/2f,0f - height);


        style2 = new Label.LabelStyle(
                Assets.font12,
                new Color(1f,1f,1f,1f)
        );
        scoreCourse = new Label(("0"), style2);
        layout.setText(scoreCourse.getStyle().font, scoreCourse.getText());
        width = layout.width;
        height = layout.height;
        scoreCourse.setPosition(0f - width/2f,0f - height -90);

        scoreTotal = new Label(Integer.toString(oldTotalScore), style2);
        layout.setText(scoreTotal.getStyle().font, scoreTotal.getText());
        width = layout.width;
        height = layout.height;
        scoreTotal.setPosition(0f - width/2f,0f - height -90);

        grpLevelScore.addActor(levelScore);
        grpLevelScore.addActor(scoreCourse);
        grpLevelScore.addActor(total);
        grpLevelScore.addActor(scoreTotal);
        grpLevelScore.setPosition(880, 700);
        stage.addActor(grpLevelScore);

        grpTotalScore = new Group();
        grpTotalScore.addActor(total);
        grpTotalScore.addActor(scoreTotal);
        grpTotalScore.setPosition(880, 450);
        stage.addActor(grpTotalScore);


        grpButton = new Group();
        btnMainMenu.setPosition(0 - btnMainMenu.getWidth()/2f - btnNewGame.getWidth(),0);
        btnCourseSelect.setPosition(0 - btnCourseSelect.getWidth()/2f + btnNewGame.getWidth(),0);
        btnNewGame.setPosition(0f - btnNewGame.getWidth()/2f,0);
        grpButton.setPosition(640, 40);
        grpButton.addActor(btnNewGame);
        grpButton.addActor(btnCourseSelect);
        grpButton.addActor(btnMainMenu);
        stage.addActor(grpButton);


        region = Assets.atlas_1.findRegion("high" + language);
        imgHighscore = new Image(region);
        imgHighscore.setWidth(region.getRegionWidth()/2f);
        imgHighscore.setHeight(region.getRegionHeight()/2f);
        imgHighscore.setPosition(0f - width/2f, 0f - height/2f);

        grpHighscore = new Group();
        if(language.equals("_fi")) {
            grpHighscore.setPosition(650, 490);
            System.out.println("FI");
        }
        else {
            grpHighscore.setPosition(700, 490);
            System.out.println("EN");
        }
        grpHighscore.setOrigin(width/2f, height/2f);
        grpHighscore.setScale(10f);
        grpHighscore.setRotation(5f);
        grpHighscore.setColor(1f,1f,1f,0f);
        grpHighscore.addActor(imgHighscore);
        stage.addActor(grpHighscore);


        pfx_points.setPosition(SCREEN_WIDTH/1.48f, SCREEN_HEIGHT/1.27f);
        pfx_points.getEmitters().first().getXScale().setHigh(50f * (SCREEN_WIDTH/1920f));
        pfx_points.allowCompletion();
    }

    public void countScore(float delta) {
        if(scoreCounterTime>=0.5f && !flip) flip = true;

        if(!flip) shitCounter = scoreCounterTime;
        else shitCounter = 0.5f + (0.5f - scoreCounterTime);

        countingScore = (int)MathUtils.floor(gainedScore * (shitCounter));

        System.out.println(shitCounter);


        if(countingScore >= playerScore) {
            isCourseScoreCounted = true;
            countingScore = playerScore;
            if(playerScore > 0) AssetsAudio.playSound(AssetsAudio.SOUND_POINTS_COUNTING, 0.8f);
            accelerator = 1f;
            scoreCounterTime = 0f;
            stateTime = 0f;
        }
        else if(countingScore > lastScore && (playerScore > 0)) {
            if(shitCounter < 0.9f && shitcountererere > 1) {
                shitcountererere = 0;
                AssetsAudio.playSound(AssetsAudio.SOUND_POINTS_COUNTING, 0.3f);
            }
            else if(shitCounter>0.9f) AssetsAudio.playSound(AssetsAudio.SOUND_POINTS_COUNTING, 0.3f);
            shitcountererere++;
        }

        lastScore = countingScore;

        layout.setText(scoreCourse.getStyle().font, scoreCourse.getText());
        width = layout.width;
        height = layout.height;
        scoreCourse.setPosition(0 -width/2f, 0f - height - 90);
        scoreCourse.setText(Integer.toString(countingScore));
    }

    public void countTotalScore(float delta) {
        if(scoreCounterTime>=0.5f && !flip) flip = true;

        if(!flip) anotherShittyCounter = scoreCounterTime;
        else anotherShittyCounter = 0.5f + (0.5f - scoreCounterTime);

        countingScore = (int)MathUtils.floor(gainedScore * (1f - anotherShittyCounter));


        if(countingScore <= 0) {
            isTotalScoreCounted = true;
            countingScore = 0;
            if(playerScore > 0) AssetsAudio.playSound(AssetsAudio.SOUND_POINTS_COUNTING, 0.8f);
            stateTime = 0f;
            pfx_points.allowCompletion();
        }
        else if(countingScore < lastScore && (playerScore > 0)) {
            if(anotherShittyCounter < 0.9f && shitcountererere > 1) {
                shitcountererere = 0;
                AssetsAudio.playSound(AssetsAudio.SOUND_POINTS_COUNTING, 0.3f);
            }
            else if(anotherShittyCounter>0.9f) AssetsAudio.playSound(AssetsAudio.SOUND_POINTS_COUNTING, 0.3f);
            shitcountererere++;
        }

        lastScore = countingScore;

        scoreCourse.setText(Integer.toString(countingScore));
        layout.setText(scoreCourse.getStyle().font, scoreCourse.getText());
        width = layout.width;
        height = layout.height;
        scoreCourse.setPosition(0 -width/2f, 0f - height - 90);

        scoreTotal.setText(Integer.toString(oldTotalScore + gainedScore - countingScore));
        layout.setText(scoreTotal.getStyle().font, scoreTotal.getText());
        width = layout.width;
        height = layout.height;
        scoreTotal.setPosition(0 -width/2f, 0f - height - 90);
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw(delta);
    }

    public void update(float delta) {
        if(isTransitionFromComplete) stateTime += delta;

        if(!isCourseScoreCounted) {
            accelerator -= delta / 4f;
            if (accelerator < 0f) accelerator = 0f;

            scoreCounterTime += delta * 2.9f;
            scoreCounterTime *= accelerator;

            countScore(delta);
        }

        if(isCourseScoreCounted && stateTime > 1.5f && !isTimeToTransfer) {
            pfx_points.reset();
            isTotalScoreCounted = false;
            isTimeToTransfer = true;
            flip = false;
        }

        if(!isTotalScoreCounted){
            accelerator -= delta / 4f;
            if (accelerator < 0f) accelerator = 0f;

            scoreCounterTime += delta * 2.9f;
            scoreCounterTime *= accelerator;

            countTotalScore(delta);
        }

        if(isTotalScoreCounted && isCourseScoreCounted && isTimeToTransfer && stateTime > 1f && scoreOpacity != 0f) {
            scoreOpacity -= delta;
            if(scoreOpacity < 0f) {
                scoreOpacity = 0f;
                stateTime = 0f;
            }
            grpLevelScore.setColor(1f,1f,1f, scoreOpacity);
            grpLevelScore.setPosition(880, 700 + (-1f + stateTime) * 50f);
        }

        if(scoreOpacity == 0 && stateTime > 0.5f && highscoreTableOpacity != 1f) {
            highscoreTableOpacity += delta * 3f;
            if(highscoreTableOpacity > 1f) {
                highscoreTableOpacity = 1f;
                stateTime = 0f;
            }
            grpHighscoreBox.setColor(1f,1f,1f, highscoreTableOpacity);
            highscoreBox.setColor(1f,1f,1f,highscoreTableOpacity);
        }


        if(gainedScore + oldTotalScore > top3Score[2]) {
            isHighScored = true;
        }
        else isHighScored = false;

        if (highscoreTableOpacity == 1f && stateTime > 0.5f) {
            if (highscoreOpacity == 0f && isHighScored) {
                AssetsAudio.playSound(AssetsAudio.SOUND_POINTS_HIGHSCORE, 1f);
            }

            highscoreOpacity += delta * 3f;
            if (highscoreOpacity > 1f) {
                highscoreOpacity = 1f;
            }
            if(isHighScored) grpHighscore.setColor(1f, 1f, 1f, highscoreOpacity);
            else grpHighscore.setColor(1f, 1f, 1f, 0f);
            grpHighscore.setScale(10f - 9f * highscoreOpacity);
        }

        stage.act();
        if(!isTransitionFromComplete) transitionFromScreen(delta);
        if(isTransition) transitionToScreen(delta);
        if(isTransitionComplete) {
            pfx_points.allowCompletion();
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

        pfx_points.getEmitters().get(0).getEmission().setHigh(20f*(1f - shitCounter/1.65f));
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
        }
    }

    public void renderToTexture() {
        texture.setTexture(fbo.getColorBufferTexture());

        //------------------------------------------------
        sBatch.begin();
        texture.draw(sBatch, opacity);
        sBatch.end();
    }

    public void makeHighScores() {
        ArrayList<String> tempProfileList = new ArrayList<String>(profiles);
        int tempScoreInt;

        //profilesData.putInteger("Mikko"+"Score", 987);
        //profilesData.putInteger("Miika"+"Score", 200);
        //profilesData.putInteger("Mika"+"Score", 400);
        //profilesData.flush();

        for (int i = 0; i <= 2 ; i++) { //Järjestelee top3name top3scores muttujiin 3 parasta profiilia ja coret

            int highestScoreFound = 0;
            String highestProfileName = "----------";

            for (String y : tempProfileList) { //haetaan profiilien scooret
                Gdx.app.log("username: ", "" +y);
                tempScoreInt = profilesData.getInteger(y + "Score", 0);

                if (tempScoreInt > highestScoreFound) {
                    highestScoreFound = tempScoreInt;
                    highestProfileName = y;
                }
            }
            tempProfileList.remove(highestProfileName);
            //if (highestProfileName.length() > 10) {     //limit String to 10 chars on highscore board
            //    highestProfileName = highestProfileName.substring(0,10);
            //}
            top3Names[i] = highestProfileName;
            top3Score[i] = highestScoreFound;
            Gdx.app.log("paras score oli pelaajalla ", "" +highestProfileName +" lukemalla " +highestScoreFound);
        }

        Label.LabelStyle style2 = new Label.LabelStyle(
                Assets.font6,
                new Color(1f,1f,1f,1f)
        );

        Label name1 = new Label(top3Names[0], style2);
        Label name2 = new Label(top3Names[1], style2);
        Label name3 = new Label(top3Names[2], style2);

        Label score1 = new Label("" +top3Score[0], style2);
        Label score2 = new Label("" +top3Score[1], style2);
        Label score3 = new Label("" +top3Score[2], style2);

        Label emptySpaceLabel = new Label("   ", style2);

        highScoreTable.add(name1);
        highScoreTable.add(emptySpaceLabel);
        highScoreTable.add(score1);
        highScoreTable.row();
        highScoreTable.add(name2);
        highScoreTable.add(emptySpaceLabel);
        highScoreTable.add(score2);
        highScoreTable.row();
        highScoreTable.add(name3);
        highScoreTable.add(emptySpaceLabel);
        highScoreTable.add(score3);
    }

    public void hide() {
        dispose();
    }

    public void dispose() {
        stage.dispose();
    }
}
