package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
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
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.Locale;

/**
 * Created by musta on 6.4.2018.
 */


public class CreateUser extends ScreenAdapter {

    GameMain game;
    Skin skin;
    Stage stage;
    OrthographicCamera camera;
    Preferences profilesData;
    TextField textField;
    Boolean mainMenuSaving = false;
    Boolean profilesMenuWithoutSaving = false;
    Locale locale;

    public CreateUser(GameMain game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 800);
        stage = new Stage(new StretchViewport(1280, 800, camera));
        //skin = new Skin(Gdx.files.internal("json/glassy-ui.json"));
        skin = Assets.skin_menu;
        profilesData = Gdx.app.getPreferences("ProfileFile");
        textField = new TextField("" ,skin);
        textField.setWidth(500);
        //button.getLabel().setFontScale(2f);
        //textField.setHeight(200);
        textField.setPosition(1280/2 - textField.getWidth()/2,800/1.5f);
        textField.setName("textfield");
        //Gdx.input.setOnscreenKeyboardVisible(true);
        textField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if ((c == '\r' || c == '\n')){
                    mainMenuSaving = true;
                }
            }
        });
        textField.setAlignment(Align.center);
        textField.setMaxLength(10);

        ProfileInfo.determineGameLanguage(); //check language data
        locale = ProfileInfo.gameLanguage;
        I18NBundle localizationBundle = I18NBundle.createBundle(Gdx.files.internal("Localization"), locale);

        TextButton backButton = new TextButton(localizationBundle.get("backButtonText"), skin);
        backButton.setPosition(450 -backButton.getWidth()/2,400);
        backButton.setName("backbutton");
        backButton.addListener(new InputListener() {
            Boolean touched = false;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { //touchdown täytyy palauttaa true jotta touchup voi toimia
                touched = true;
                return true;
            }
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (touched)
                    Gdx.input.setOnscreenKeyboardVisible(false);
                    profilesMenuWithoutSaving = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor button)
            {
                touched = false;
            }
        });

        TextButton acceptButton = new TextButton(localizationBundle.get("acceptButtonText"), skin);
        acceptButton.setPosition(830 -backButton.getWidth()/2,400);
        acceptButton.setName("acceptbutton");
        acceptButton.addListener(new InputListener() {
            Boolean touched = false;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { //touchdown täytyy palauttaa true jotta touchup voi toimia
                touched = true;
                return true;
            }
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (touched)
                    Gdx.input.setOnscreenKeyboardVisible(false);
                    mainMenuSaving = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor button)
            {
                touched = false;
            }
        });

        Label label1 = new Label(localizationBundle.get("createUserLabel"), skin);
        label1.setPosition(640 -label1.getWidth()/2,670);
        label1.setFontScale(1);

        stage.addActor(textField);
        stage.addActor(acceptButton);
        stage.addActor(backButton);
        stage.addActor(label1);

        Gdx.input.setInputProcessor(stage);
    }

    public void render (float delta) {
        Gdx.gl.glClearColor(0.16f, 0.45f, 0.6f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //Gdx.app.log("nabbi", Gdx.input.isKeyPressed());

        stage.act();
        stage.draw();

        TextField tempActor = stage.getRoot().findActor("textfield");  //Set selected playerprofile to gamescreen.
        String tmpTxt = tempActor.getText();
        //if (tmpTxt.length() > 10) {
        //    tmpTxt = tmpTxt.substring(0 , 10);
        //    tempActor.setText(tmpTxt);
        //    tempActor.setCursorPosition(10);
        //}
        //tempActor.getDefaultInputListener().enter();
        //textField.getDefaultInputListener().keyDown(Input.Keys.ENTER);
        //String string = (String) tempActor.getSelected();

        if (mainMenuSaving) {
            Boolean isThereDuplicate = false;
            int firstAvailableID = 0;
            for (int i = 0; i < 200; i++) {     //duplicate username checkkaus
                if (profilesData.getString("username"+i, "novalue").equals(tmpTxt)  ) {
                    isThereDuplicate = true;
                }
            }
            for (int i = 0; i < 200; i++) {     //find first available id
                if(profilesData.getString("username"+i, "novalue").equals("novalue") ) {
                    firstAvailableID = i;
                    break;
                }
            }

            if (isThereDuplicate == false) {
                profilesData.putString("username" + firstAvailableID, "" + tmpTxt); //muista flushaa
                profilesData.flush();
                ProfileInfo.selectedPlayerProfile = tmpTxt;
            }
                game.setScreen(new MainMenu(game,false));
        }

        if (profilesMenuWithoutSaving) {
            game.setScreen(new ProfileMenu(game));
        }


        game.sBatch.begin();
        //Assets.font.draw(game.sBatch, "Difficulty", 730,750);
        game.sBatch.end();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        //skin.dispose();
        stage.dispose();
    }
}
