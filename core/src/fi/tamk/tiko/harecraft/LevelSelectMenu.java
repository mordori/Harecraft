package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.ArrayList;
import java.util.Locale;

import static fi.tamk.tiko.harecraft.GameMain.fbo;
import static fi.tamk.tiko.harecraft.GameMain.musicVolume;
import static fi.tamk.tiko.harecraft.GameMain.sBatch;
import static fi.tamk.tiko.harecraft.GameMain.texture;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;

/**
 * Created by musta on 29.4.2018.
 */

public class LevelSelectMenu extends ScreenAdapter {
    static GameMain game;
    Skin skin;
    Stage stage;
    OrthographicCamera camera;
    Locale locale;
    ArrayList<String> profiles;
    String[] top3Names = new String[3];
    int[] top3Score = new int[3];
    Table highScoreTable = new Table();
    Preferences profilesData;

    Boolean mainMenu = false;
    Boolean startGame = false;
    I18NBundle localizationBundle;

    static int selectedLevelNumber;

    float opacity = 1f;
    boolean isTransition = false;
    boolean isTransitionComplete = false;
    boolean isTransitionFromComplete = false;
    boolean isFaded;
    float timer;

    public LevelSelectMenu(GameMain game, ArrayList<String> profs, boolean isFaded) {
        this.game = game;
        this.isFaded = isFaded;
        if(isFaded) opacity = 0f;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 800);
        stage = new Stage(new StretchViewport(1280, 800, camera));
        skin = Assets.skin_menu;
        Gdx.input.setInputProcessor(stage);
        profiles = profs;
        profilesData = Gdx.app.getPreferences("ProfileFile"); // KEY ja VALUE

        ProfileInfo.determineGameLanguage(); //check language data
        locale = ProfileInfo.gameLanguage;
        localizationBundle = I18NBundle.createBundle(Gdx.files.internal("Localization"), locale);

        selectedLevelNumber = profilesData.getInteger(ProfileInfo.selectedPlayerProfile +"LastLevel", 1);

        makeHighScores();
        highScoreTable.setPosition(1000,630);

        LevelButton levelOneButton = new LevelButton(Assets.texR_stage1, Assets.texR_stage1_pressed, 1);
        levelOneButton.setPosition(240 -levelOneButton.getWidth()/2,220);

        LevelButton levelTwoButton = new LevelButton(Assets.texR_stage2, Assets.texR_stage2_pressed, 2);
        levelTwoButton.setPosition(640 -levelTwoButton.getWidth()/2,220);

        LevelButton levelThreeButton = new LevelButton(Assets.texR_stage3, Assets.texR_stage3_pressed, 0);
        levelThreeButton.setPosition(1040 -levelThreeButton.getWidth()/2,220);

        InstructionsBox instructionsBox = new InstructionsBox();
        instructionsBox.setPosition(50, 610);


        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(
                Assets.skin_menu.getDrawable("button"),
                Assets.skin_menu.getDrawable("button pressed"),
                Assets.skin_menu.getDrawable("button"),
                Assets.font5
        );
        style.pressedOffsetX = 4;
        style.pressedOffsetY = -4;
        style.downFontColor = new Color(0.59f,0.59f,0.59f,1f);
        style.fontColor = new Color(1f,1f,1f,1f);

        TextButton returnButton = new TextButton(localizationBundle.get("backButtonText"), style);
        returnButton.setWidth(270);
        returnButton.setHeight(120);
        returnButton.setPosition(200 -returnButton.getWidth()/2,50);
        returnButton.addListener(new InputListener() {
            Boolean touched = false;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { //touchdown täytyy palauttaa true jotta touchup voi toimia
                touched = true;
                return true;
            }
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(touched) {
                    Gdx.input.setInputProcessor(null);
                    isTransitionFromComplete = true;
                    mainMenu = true;
                }
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor button)
            {
                touched = false;
            }
        });

        style = new TextButton.TextButtonStyle(
                Assets.skin_menu.getDrawable("buttongreen"),
                Assets.skin_menu.getDrawable("buttongreenpressed"),
                Assets.skin_menu.getDrawable("buttongreen"),
                Assets.font5
        );
        style.pressedOffsetX = 4;
        style.pressedOffsetY = -4;
        style.downFontColor = new Color(0.59f,0.59f,0.59f,1f);
        style.fontColor = new Color(1f,1f,1f,1f);

        TextButton startButton = new TextButton(localizationBundle.get("startButtonText"), style);
        startButton.setHeight(120);
        startButton.setWidth(270);
        startButton.setPosition(1080 -startButton.getWidth()/2,50);
        startButton.addListener(new InputListener() {
            Boolean touched = false;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { //touchdown täytyy palauttaa true jotta touchup voi toimia
                touched = true;
                return true;
            }
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(touched) {
                    Gdx.input.setInputProcessor(null);
                    isTransitionFromComplete = true;
                    startGame = true;
                }
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor button)
            {
                touched = false;
            }
        });

        Slider durationSlider = new Slider(1000,3000, 1000, false,skin);
        durationSlider.setPosition(640 - 250, 70);;
        durationSlider.setValue(profilesData.getInteger(""+ProfileInfo.selectedPlayerProfile +"Duration", 2000));
        durationSlider.setWidth(500);
        durationSlider.setName("durationslider");

        Label.LabelStyle style2 = new Label.LabelStyle(
            Assets.font6,
            new Color(1f,1f,1f,1f)
        );

        Label.LabelStyle instructionStyle = new Label.LabelStyle(
            Assets.font7,
            new Color(1f,1f,1f,1f)
        );

        Label durationLabel = new Label(localizationBundle.get("durationLabelText"), style2);
        durationLabel.setPosition(640 -durationLabel.getWidth()/2, 120);

        Label instructionsLabel1 = new Label(localizationBundle.get("instructionText1"), instructionStyle);
        instructionsLabel1.setPosition(115,565);

        //Label instructionsLabel2 = new Label(localizationBundle.get("instructionText2"), instructionStyle);
        //instructionsLabel2.setPosition(800 -instructionsLabel2.getWidth()/2, 565);

        style2 = new Label.LabelStyle(
            Assets.font5,
            new Color(1f,1f,1f,1f)
        );

        Label topPilotsLabel = new Label(localizationBundle.get("top3pilots"), style2);
        topPilotsLabel.setPosition(875,710);

        Label minLabel = new Label("Min", instructionStyle);
        minLabel.setPosition(640 - 250 ,120);

        Label maxLabel = new Label("Max", instructionStyle);
        maxLabel.setPosition(640 + 250 -maxLabel.getWidth() , 120);

        HighscoreBox highscoreBox = new HighscoreBox();
        highscoreBox.setPosition(800f, 550);

        stage.addActor(levelOneButton);
        stage.addActor(levelTwoButton);
        stage.addActor(levelThreeButton);
        stage.addActor(returnButton);
        stage.addActor(startButton);
        stage.addActor(instructionsBox);

        stage.addActor(instructionsLabel1);
        stage.addActor(minLabel);
        stage.addActor(maxLabel);

        stage.addActor(highscoreBox);
        stage.addActor(topPilotsLabel);
        stage.addActor(highScoreTable);

        stage.addActor(durationLabel);
        stage.addActor(durationSlider);

        AssetsAudio.playMusic(AssetsAudio.MUSIC_COURSE_2);
        AssetsAudio.setMusicVolume(musicVolume);
    }

    public void render (float delta) {
        if(isFaded && !isTransitionFromComplete) {
            transitionFromScreen(delta);
        }

        if(isTransition) transitionToScreen(delta);

        switch (selectedLevelNumber) {
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
        if(isFaded && !isTransitionFromComplete) Gdx.gl.glClearColor(68f/255f, 153f/255f, 223f/255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();

        fbo.begin();
            Gdx.gl.glClearColor(68f/255f, 153f/255f, 223f/255f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            stage.draw();
        fbo.end();
        renderToTexture();

        if (mainMenu) {
            saveLastSelectedLevel();

            Slider tmpActor = stage.getRoot().findActor("durationslider");
            int tmpInt = (int) tmpActor.getValue();
            profilesData.putInteger(ProfileInfo.selectedPlayerProfile+"Duration", tmpInt );
            profilesData.flush();

            game.setScreen(new MainMenu(game, false));
        }

        if(startGame && !isTransition) {
            saveLastSelectedLevel();

            Slider tmpActor = stage.getRoot().findActor("durationslider");
            int tmpInt = (int) tmpActor.getValue();
            profilesData.putInteger(ProfileInfo.selectedPlayerProfile+"Duration", tmpInt );
            profilesData.flush();

            ProfileInfo.load();
            isTransition = true;
        }

        if(isTransitionComplete) timer += delta;

        if(isTransitionComplete && timer > 0.05f) {
            AssetsAudio.stopMusic();
            game.setScreen(new GameScreen(game, selectedLevelNumber));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            mainMenu = true;
        }
    }

    public void transitionFromScreen(float delta) {
        opacity += delta;
        if(opacity >= 1f) {
            opacity = 1f;
            isTransitionFromComplete = true;
        }
    }

    public void transitionToScreen(float delta) {
        opacity -= delta;
        if(opacity <= 0f) {
            opacity = 0f;
            isTransitionComplete = true;
        }

        AssetsAudio.setMusicVolume(musicVolume * opacity);
    }

    public void renderToTexture() {
        texture.setTexture(fbo.getColorBufferTexture());

        //------------------------------------------------
        sBatch.begin();
        texture.draw(sBatch, opacity);
        sBatch.end();
    }

    public void saveLastSelectedLevel() {
        profilesData.putInteger(ProfileInfo.selectedPlayerProfile+"LastLevel", selectedLevelNumber);
        profilesData.flush();
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

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}

class LevelButton extends Actor {
    TextureRegion buttonNotPressed;
    TextureRegion buttonPressed;
    TextureRegion textureToDraw;
    int levelToGo;

    public LevelButton(TextureRegion texture1, TextureRegion texture2, int levelNumber) {
        buttonNotPressed = texture1;
        buttonPressed = texture2;
        textureToDraw = buttonNotPressed;
        levelToGo = levelNumber;

        addListener(new InputListener() {
            Boolean touched = false;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { //touchdown täytyy palauttaa true jotta touchup voi toimia
                touched = true;
                //textureToDraw = buttonPressed;
                return true;
            }
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (touched) {
                    //LevelSelectMenu.game.setScreen(new GameScreen(LevelSelectMenu.game, levelToGo));
                    LevelSelectMenu.selectedLevelNumber = levelToGo;
                }
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor button)
            {
                //textureToDraw = buttonNotPressed;
                touched = false;
            }
        });

        setBounds(getX(),getY(),350,300);
    }

    public void draw(Batch batch, float alpha) {
        if (levelToGo == LevelSelectMenu.selectedLevelNumber) {
            batch.draw(buttonPressed, getX(), getY(), 350, 300);
        }
        else {
            batch.draw(buttonNotPressed, getX(), getY(), 350, 300);
        }
    }
}

class InstructionsBox extends Actor {
    TextureRegion instructionTexture;

    public InstructionsBox() {
        instructionTexture = Assets.texR_instructions;
    }

    public void draw(Batch batch, float alpha) {
        batch.draw(instructionTexture, getX(),getY());
    }
}

class HighscoreBox extends Actor {
    TextureRegion highscore;

    public HighscoreBox() {
        highscore = Assets.texR_highscoreList;
    }

    @Override
    public void draw(Batch batch, float alpha) {
        batch.setColor(1f,1f,1f,alpha);
        batch.draw(highscore, getX(),getY());
        batch.setColor(1f,1f,1f,1f);
    }
}
