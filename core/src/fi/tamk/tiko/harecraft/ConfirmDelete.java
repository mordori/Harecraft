package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * Created by musta on 19.4.2018.
 */

public class ConfirmDelete extends ScreenAdapter {

    GameMain game;
    Skin skin;
    Stage stage;
    OrthographicCamera camera;
    Preferences profilesData;
    Boolean yesSelect = false;
    Boolean noSelect = false;
    String tempString;

    public ConfirmDelete(GameMain game, String deleteString) {

        this.game = game;
        skin = new Skin(Gdx.files.internal("json/glassy-ui.json"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 800);
        stage = new Stage(new StretchViewport(1280, 800, camera));
        profilesData = Gdx.app.getPreferences("ProfileFile");
        tempString = deleteString;

        Gdx.input.setInputProcessor(stage);

        Label questionLabel = new Label("Surely to delete " +tempString +" ? :E", skin);
        questionLabel.setPosition(640 -questionLabel.getWidth()/2,500);
        questionLabel.setFontScale(1);
        questionLabel.setName("difficultylabel");

        TextButton yesButton = new TextButton("Yes", skin);
        yesButton.setPosition(1280/2 -200 -yesButton.getWidth()/2,400 -yesButton.getHeight()/2);
        yesButton.addListener(new InputListener() {
            Boolean touched = false;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { //touchdown täytyy palauttaa true jotta touchup voi toimia
                touched = true;
                return true;
            }
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (touched)
                    yesSelect = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor button)
            {
                touched = false;
            }
        });

        TextButton noButton = new TextButton("No", skin);
        noButton.setPosition(1280/2 +200 -noButton.getWidth()/2,400 -noButton.getHeight()/2);
        noButton.addListener(new InputListener() {
            Boolean touched = false;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { //touchdown täytyy palauttaa true jotta touchup voi toimia
                touched = true;
                return true;
            }
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (touched)
                    noSelect = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor button)
            {
                touched = false;
            }
        });

        stage.addActor(noButton);
        stage.addActor(yesButton);
        stage.addActor(questionLabel);
    }

    public void render (float delta) {
        Gdx.gl.glClearColor(0.16f, 0.45f, 0.6f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();

        if (yesSelect) {    //Delete starts

            for (int i = 0; i < 200; i++) { //selvitetään KEY
                if(tempString.equals(profilesData.getString("username" +i, "novalue"))) {
                    profilesData.remove("username"+i);
                }
            }   //delete data
            profilesData.remove(tempString +"StaticHolds");
            profilesData.remove(tempString +"Difficulty");
            profilesData.remove(tempString + "Duration");
            profilesData.remove(tempString +"Sensitivity");
            for ( int i = 0; i < 6; i++) {
                profilesData.remove(tempString + "VectorX" + i);
                profilesData.remove(tempString + "VectorY" + i);
            }
            profilesData.flush();

            game.setScreen(new ProfileMenu(game));
        }
        if (noSelect) {
            game.setScreen(new ProfileMenu(game));
        }
    }
}
