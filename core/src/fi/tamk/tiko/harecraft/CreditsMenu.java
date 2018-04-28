package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.Locale;

/**
 * Created by musta on 28.4.2018.
 */

public class CreditsMenu extends ScreenAdapter {

    GameMain game;
    Skin skin;
    Stage stage;
    OrthographicCamera camera;
    Locale locale;
    Boolean mainMenu = false;

    public CreditsMenu(GameMain game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 800);
        stage = new Stage(new StretchViewport(1280, 800, camera));
        skin = Assets.skin_menu;
        Gdx.input.setInputProcessor(stage);

        ProfileInfo.determineGameLanguage(); //check language data
        locale = ProfileInfo.gameLanguage;
        I18NBundle localizationBundle = I18NBundle.createBundle(Gdx.files.internal("Localization"), locale);

        Label label1 = new Label(localizationBundle.get("miikaCreditsText"), skin);
        label1.setPosition(640-label1.getWidth()/2, 560);

        Label label2 = new Label(localizationBundle.get("mikaCreditsText"), skin);
        label2.setPosition(640-label2.getWidth()/2, 480);

        Label label3 = new Label(localizationBundle.get("mikkoCreditsText"), skin);
        label3.setPosition(640-label3.getWidth()/2, 400);

        Label label4 = new Label(localizationBundle.get("juusoCreditsText"), skin);
        label4.setPosition(640-label4.getWidth()/2, 320);

        Label label5 = new Label(localizationBundle.get("henriCreditsText"), skin);
        label5.setPosition(640-label5.getWidth()/2, 240);

        Label twoDots = new Label("..", skin);
        if (ProfileInfo.gameLanguage.toString().equals("fi_FI")) {
            twoDots.setPosition(843, 270);
        }
        else {
            twoDots.setPosition(822, 270);
        }

        Label creditLabel = new Label(localizationBundle.get("creditsText"), skin);
        creditLabel.setPosition(640-creditLabel.getWidth()/2, 650);

        TextButton returnButton = new TextButton(localizationBundle.get("backButtonText"), skin);
        returnButton.setWidth(270f);
        returnButton.setHeight(120f);
        returnButton.setPosition(640 -returnButton.getWidth()/2,50); //y170 x640
        returnButton.setName("settingsbutton");

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

        stage.addActor(creditLabel);
        stage.addActor(label1);
        stage.addActor(label2);
        stage.addActor(label3);
        stage.addActor(label4);
        stage.addActor(label5);
        stage.addActor(twoDots);
        stage.addActor(returnButton);
    }

    public void render (float delta) {

        Gdx.gl.glClearColor(0.16f, 0.45f, 0.6f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();

        if (mainMenu) {
            game.setScreen(new MainMenu(game));
        }
    }
}
