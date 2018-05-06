package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    public LevelSelectMenu(GameMain game, ArrayList<String> profs) {
        this.game = game;
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
        highScoreTable.setPosition(240,630);

        LevelButton levelOneButton = new LevelButton(new Texture(Gdx.files.internal("textures/stage1.png")), new Texture(Gdx.files.internal("textures/stage1p.png")), 1);
        levelOneButton.setPosition(240 -levelOneButton.getWidth()/2,220);

        LevelButton levelTwoButton = new LevelButton(new Texture(Gdx.files.internal("textures/stage2.png")), new Texture(Gdx.files.internal("textures/stage2p.png")), 2);
        levelTwoButton.setPosition(640 -levelTwoButton.getWidth()/2,220);

        LevelButton levelThreeButton = new LevelButton(new Texture(Gdx.files.internal("textures/stage3.png")), new Texture(Gdx.files.internal("textures/stage3p.png")), 0);
        levelThreeButton.setPosition(1040 -levelThreeButton.getWidth()/2,220);

        InstructionsBox instructionsBox = new InstructionsBox();
        instructionsBox.setPosition(320 + 140, 610);

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
                if (touched)
                    mainMenu = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor button)
            {
                touched = false;
            }
        });

        style = new TextButton.TextButtonStyle(
                Assets.skin_menu.getDrawable("listbutton"),
                Assets.skin_menu.getDrawable("listbutton pressed"),
                Assets.skin_menu.getDrawable("listbutton"),
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
                if (touched)
                    startGame = true;
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
        instructionsLabel1.setPosition(830 -instructionsLabel1.getWidth()/2,585);

        //Label instructionsLabel2 = new Label(localizationBundle.get("instructionText2"), instructionStyle);
        //instructionsLabel2.setPosition(800 -instructionsLabel2.getWidth()/2, 565);

        Label topPilotsLabel = new Label(localizationBundle.get("top3pilots"), style2);
        topPilotsLabel.setPosition(230 -topPilotsLabel.getWidth()/2,710);

        Label minLabel = new Label("Min", instructionStyle);
        minLabel.setPosition(640 - 250 ,120);

        Label maxLabel = new Label("Max", instructionStyle);
        maxLabel.setPosition(640 + 250 -maxLabel.getWidth() , 120);

        stage.addActor(levelOneButton);
        stage.addActor(levelTwoButton);
        stage.addActor(levelThreeButton);
        stage.addActor(returnButton);
        stage.addActor(startButton);
        stage.addActor(instructionsBox);

        stage.addActor(instructionsLabel1);
        //stage.addActor(instructionsLabel2);
        stage.addActor(minLabel);
        stage.addActor(maxLabel);

        stage.addActor(topPilotsLabel);
        stage.addActor(highScoreTable);

        stage.addActor(durationLabel);
        stage.addActor(durationSlider);
    }

    public void render (float delta) {
        Gdx.gl.glClearColor(0.16f, 0.45f, 0.6f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();

        if (mainMenu) {

            saveLastSelectedLevel();

            Slider tmpActor = stage.getRoot().findActor("durationslider");
            int tmpInt = (int) tmpActor.getValue();
            profilesData.putInteger(ProfileInfo.selectedPlayerProfile+"Duration", tmpInt );
            profilesData.flush();

            game.setScreen(new MainMenu(game, false));
        }
        if (startGame) {

            saveLastSelectedLevel();

            Slider tmpActor = stage.getRoot().findActor("durationslider");
            int tmpInt = (int) tmpActor.getValue();
            profilesData.putInteger(ProfileInfo.selectedPlayerProfile+"Duration", tmpInt );
            profilesData.flush();

            ProfileInfo.load();

            game.setScreen(new GameScreen(game, selectedLevelNumber));
        }
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
    Texture buttonNotPressed;
    Texture buttonPressed;
    Texture textureToDraw;
    int levelToGo;

    public LevelButton(Texture texture1, Texture texture2, int levelNumber) {
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
    Texture instructionTexture;

    public InstructionsBox() {
        instructionTexture = new Texture(Gdx.files.internal("textures/help.png"));
    }

    public void draw(Batch batch, float alpha) {
        batch.draw(instructionTexture, getX(),getY(), 720 , 128);
    }
}
