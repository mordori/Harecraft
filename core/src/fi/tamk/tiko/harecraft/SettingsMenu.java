package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
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
    //String currentProfile;
    Preferences profilesData;

    public SettingsMenu(GameMain game) {
        this.game = game;
        skin = new Skin(Gdx.files.internal("json/glassy-ui.json"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 800);
        stage = new Stage(new StretchViewport(1280, 800, camera));
        profilesData = Gdx.app.getPreferences("ProfileFile"); //aseta tiedosto preferencesislle
        //currentProfile = ProfileInfo.selectedPlayerProfile;    //aseta aktiivinen pelaajaprofiili muutujaan

        Gdx.input.setInputProcessor(stage);

        TextButton button = new TextButton("Save", skin);
        button.setPosition(900,50);
        button.setName("backbutton");
        //button.getLabel().setFontScale(2f);
        //button.setStyle(skin.get("small", TextButton.TextButtonStyle.class));
        //button.setHeight(50);
        //button.setWidth(100);

        Slider slider = new Slider(0,4,1,false,skin);
        slider.setPosition(650,650);
        //profilesData.getInteger(""+currentProfile +"Difficulty", 2);
        slider.setValue(profilesData.getInteger(""+ProfileInfo.selectedPlayerProfile +"Difficulty", 2));     //set value to current difficultylevel when loading
        slider.setWidth(500);
        slider.setName("difficultyslider");

        Label tarra1 = new Label("Difficulty", skin);
        tarra1.setPosition(800,700);
        tarra1.setFontScale(1);

        Slider slider2 = new Slider(0,5, 1, false,skin);
        slider2.setPosition(650, 500);;
        slider2.setValue(profilesData.getInteger(""+ProfileInfo.selectedPlayerProfile +"StaticHolds", 1));
        slider2.setWidth(500);
        slider2.setName("staticholdsslider");

        Label tarra2 = new Label("Static Holds", skin);
        tarra2.setPosition(770,550);
        tarra2.setFontScale(1);

        //skin.getFont("font").getData().setScale(1.5f); //set skin font size
        //SelectBox profileBox = new SelectBox(skin);
        //profileBox.setItems(new String[] {"Mikko ", "Mika","Miika","Henri", "Juuso", "tyyppi", "tyyppi2", "tyyppi3", "tyypi4"});
        //profileBox.setPosition(100,50);
        //profileBox.setScale(100f); isontaa buttonia mutta ei grafiikkaa
        //profileBox.setWidth(400);

        stage.addActor(slider);
        stage.addActor(slider2);
        stage.addActor(tarra1);
        stage.addActor(tarra2);
        stage.addActor(button);
        //stage.addActor(profileBox);

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
        if (returnToMainMenu) { //Tallennetaan muutokset aktiiviseen profiiliin

            Slider tmpActor = stage.getRoot().findActor("difficultyslider");    //etsi difficulty sliderin value ja tallenna
            int tmpInt = (int) tmpActor.getValue();
            profilesData.putInteger(ProfileInfo.selectedPlayerProfile+"Difficulty", tmpInt );
            profilesData.flush();

            tmpActor = stage.getRoot().findActor("staticholdsslider");    //etsi static holds sliderin value ja tallenna
            tmpInt = (int) tmpActor.getValue();
            profilesData.putInteger(ProfileInfo.selectedPlayerProfile+"StaticHolds", tmpInt );
            profilesData.flush();

            game.setScreen(new MainMenu(game));
        }
        Gdx.app.log("Kenen profiili on valittuna", ""+ProfileInfo.selectedPlayerProfile);
    }
}