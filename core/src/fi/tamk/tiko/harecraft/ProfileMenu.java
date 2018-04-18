package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.ArrayList;

/**
 * Created by musta on 10.4.2018.
 */

public class ProfileMenu extends ScreenAdapter {

    GameMain game;
    Skin skin;
    Stage stage;
    OrthographicCamera camera;
    Preferences profilesData;
    Boolean mainMenu = false;
    Boolean createUser = false;
    Boolean deleteUser = false;
    ArrayList<String> profiles;

    public ProfileMenu(GameMain game) {
        this.game = game;
        skin = new Skin(Gdx.files.internal("json/glassy-ui.json"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 800);
        stage = new Stage(new StretchViewport(1280, 800, camera));
        profilesData = Gdx.app.getPreferences("ProfileFile");
        profiles = new ArrayList<String>();

        for (int i = 0; i<200; i++) {
            String tempName;
            tempName = profilesData.getString("username" +i, "novalue");
            if (!tempName.equals("novalue")) {
                profiles.add(tempName);
            }
        }

        Gdx.input.setInputProcessor(stage);

        TextButton mainMenuButton = new TextButton("Return Main", skin);
        mainMenuButton.setPosition(100,100);
        mainMenuButton.addListener(new InputListener() {
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

        TextButton createUserButton = new TextButton("Create user", skin);
        createUserButton.setPosition(100,300);
        createUserButton.addListener(new InputListener() {
            Boolean touched = false;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { //touchdown täytyy palauttaa true jotta touchup voi toimia
                touched = true;
                return true;
            }
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (touched)
                    createUser = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor button)
            {
                touched = false;
            }
        });

        TextButton deleteUserButton = new TextButton("Delete user", skin);
        deleteUserButton.setPosition(100,500);
        deleteUserButton.addListener(new InputListener() {
            Boolean touched = false;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { //touchdown täytyy palauttaa true jotta touchup voi toimia
                touched = true;
                return true;
            }
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (touched)
                    deleteUser = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor button)
            {
                touched = false;
            }
        });


        List listBox = new List(skin);
        listBox.setItems(profiles.toArray());
        listBox.setName("listbox");
        ScrollPane scrollBox = new ScrollPane(listBox);
        scrollBox.setBounds(0,0,300,300);
        scrollBox.setSmoothScrolling(false);
        scrollBox.setOverscroll(false,false);
        scrollBox.setClamp(true);
        scrollBox.setPosition(800,300);

        stage.addActor(mainMenuButton);
        stage.addActor(createUserButton);
        stage.addActor(deleteUserButton);
        stage.addActor(scrollBox);
    }

    public void render (float delta) {
        Gdx.gl.glClearColor(0.16f, 0.45f, 0.6f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();

        if (mainMenu) {
            game.setScreen(new MainMenu(game));
        }
        if (createUser) {
            game.setScreen(new CreateUser(game));
        }
        if (deleteUser) { //delete things here
            List tempActor = stage.getRoot().findActor("listbox");  //Set selected playerprofile to gamescreen.
            String tempString = (String) tempActor.getSelected();

            for (int i = 0; i < 200; i++) { //selvitetään KEY
                if(tempString.equals(profilesData.getString("username" +i, "novalue"))) {
                    profilesData.remove("username"+i);
                }
            }
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
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        skin.dispose();
        stage.dispose();
    }
}
