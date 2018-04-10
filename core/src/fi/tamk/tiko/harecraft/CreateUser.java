package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.StretchViewport;

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
    Boolean mainMenuWithoutSaving = false;

    public CreateUser(GameMain game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 800);
        stage = new Stage(new StretchViewport(1280, 800, camera));
        skin = new Skin(Gdx.files.internal("json/glassy-ui.json"));
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

        stage.addActor(textField);

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
            }
                game.setScreen(new MainMenu(game));
        }


        game.sBatch.begin();
        //Assets.font.draw(game.sBatch, "Difficulty", 730,750);
        game.sBatch.end();
    }
}
