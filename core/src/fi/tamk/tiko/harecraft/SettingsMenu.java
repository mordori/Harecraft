package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * Created by musta on 29.3.2018.
 */

public class SettingsMenu extends ScreenAdapter {

    GameMain game;
    Skin skin;
    OrthographicCamera camera;
    Stage stage;
    Boolean returnToMainMenu = false;

    public SettingsMenu(GameMain game) {
        this.game = game;
        skin = new Skin(Gdx.files.internal("json/glassy-ui.json"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 800);
        stage = new Stage(new StretchViewport(1280, 800, camera));

        Gdx.input.setInputProcessor(stage);

        TextButton button = new TextButton("Back", skin);
        button.setPosition(900,50);
        button.setName("backbutton");

        Slider slider = new Slider(0,4,1,false,skin);
        slider.setPosition(650,650);
        slider.setValue(3);     //set value to current difficultylevel when loading
        slider.setWidth(500);
        slider.setName("difficultyslider");

        Label tarra1 = new Label("Difficulty", skin);
        tarra1.setPosition(830,700);
        tarra1.setFontScale(2);
        stage.addActor(tarra1);

        SelectBox profileBox = new SelectBox(skin);
        profileBox.setItems(new String[] {"Mikko ", "Mika","Miika","Henri", "Juuso"});
        profileBox.setPosition(100,500);
        profileBox.setWidth(200);

        stage.addActor(slider);
        stage.addActor(button);
        stage.addActor(profileBox);

        button.addListener(new InputListener() {
            Boolean touched = false;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                touched = true;
                return true;
            }
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (touched)
                returnToMainMenu = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor button)
            {
                touched = false;
            }
        });
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
        game.sBatch.begin();
        //Assets.font.draw(game.sBatch, "Difficulty", 730,750);
        game.sBatch.end();
        if (returnToMainMenu) game.setScreen(new MainMenu(game));
    }
}