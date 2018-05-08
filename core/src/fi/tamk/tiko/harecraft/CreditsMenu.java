package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
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

        Label.LabelStyle style2 = new Label.LabelStyle(
                Assets.font6,
                new Color(1f,1f,1f,1f)
        );

        Label label1 = new Label(localizationBundle.get("miikaCreditsText"), style2);
        label1.setPosition(640-label1.getWidth()/2, 560);

        Label label2 = new Label(localizationBundle.get("mikaCreditsText"), style2);
        label2.setPosition(640-label2.getWidth()/2, 480);

        Label label3 = new Label(localizationBundle.get("mikkoCreditsText"), style2);
        label3.setPosition(640-label3.getWidth()/2, 400);

        Label label4 = new Label(localizationBundle.get("juusoCreditsText"), style2);
        label4.setPosition(640-label4.getWidth()/2, 320);

        Label label5 = new Label(localizationBundle.get("henriCreditsText"), style2);
        label5.setPosition(640-label5.getWidth()/2, 240);

        Label twoDots = new Label("..", style2);
        if (ProfileInfo.gameLanguage.toString().equals("fi_FI")) {
            twoDots.setPosition(828, 270);
        }
        else {
            twoDots.setPosition(808, 270);
        }

        style2 = new Label.LabelStyle(
                Assets.font3,
                new Color(1f,1f,1f,1f)
        );

        Label creditLabel = new Label(localizationBundle.get("creditsText"), style2);
        creditLabel.setPosition(640-creditLabel.getWidth()/2, 650);


        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(
                Assets.skin_menu.getDrawable("button"),
                Assets.skin_menu.getDrawable("button pressed"),
                Assets.skin_menu.getDrawable("button"),
                Assets.font6);

        style.pressedOffsetX = 4;
        style.pressedOffsetY = -4;
        style.downFontColor = new Color(0.59f,0.59f,0.59f,1f);
        style.fontColor = new Color(1f,1f,1f,1f);

        TextButton returnButton = new TextButton(localizationBundle.get("backButtonText"), style);
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

        Gdx.gl.glClearColor(68f/255f, 153f/255f, 223f/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();

        if (mainMenu) {
            game.setScreen(new MainMenu(game, false));
        }
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
