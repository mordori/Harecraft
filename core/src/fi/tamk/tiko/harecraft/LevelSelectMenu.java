package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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
        I18NBundle localizationBundle = I18NBundle.createBundle(Gdx.files.internal("Localization"), locale);

        makeHighScores();
        highScoreTable.setPosition(500,500);

        LevelButton levelOneButton = new LevelButton(new Texture(Gdx.files.internal("textures/stage1.png")), new Texture(Gdx.files.internal("textures/stage1p.png")), 1);
        levelOneButton.setPosition(400 -100,600);

        LevelButton levelTwoButton = new LevelButton(new Texture(Gdx.files.internal("textures/stage2.png")), new Texture(Gdx.files.internal("textures/stage2p.png")), 2);
        levelTwoButton.setPosition(640 -100,600);

        LevelButton levelThreeButton = new LevelButton(new Texture(Gdx.files.internal("textures/stage3.png")), new Texture(Gdx.files.internal("textures/stage3p.png")), 0);
        levelThreeButton.setPosition(880 -100,600);

        stage.addActor(levelOneButton);
        stage.addActor(levelTwoButton);
        stage.addActor(levelThreeButton);

        stage.addActor(highScoreTable);
    }


    public void render (float delta) {

        Gdx.gl.glClearColor(0.16f, 0.45f, 0.6f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }


    public void makeHighScores() {
        List<String> tempProfileList = profiles;
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

        Label name1 = new Label(top3Names[0], skin);
        Label name2 = new Label(top3Names[1], skin);
        Label name3 = new Label(top3Names[2], skin);

        Label score1 = new Label("" +top3Score[0], skin);
        Label score2 = new Label("" +top3Score[1], skin);
        Label score3 = new Label("" +top3Score[2], skin);

        highScoreTable.add(name1);
        highScoreTable.add(score1);
        highScoreTable.row();
        highScoreTable.add(name2);
        highScoreTable.add(score2);
        highScoreTable.row();
        highScoreTable.add(name3);
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
                textureToDraw = buttonPressed;
                return true;
            }
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (touched) {
                    LevelSelectMenu.game.setScreen(new GameScreen(LevelSelectMenu.game, levelToGo));
                }
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor button)
            {
                textureToDraw = buttonNotPressed;
                touched = false;
            }
        });

        setBounds(getX(),getY(),200,100);
    }

    public void draw(Batch batch, float alpha) {
        batch.draw(textureToDraw, getX(),getY(),200,100);
    }
}
