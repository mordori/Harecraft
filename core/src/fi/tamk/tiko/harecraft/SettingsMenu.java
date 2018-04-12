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

        Label difficultyLabel = new Label("Difficulty", skin);
        difficultyLabel.setPosition(900 -difficultyLabel.getWidth()/2,700);
        difficultyLabel.setFontScale(1);

        Slider slider2 = new Slider(0,5, 1, false,skin);
        slider2.setPosition(650, 500);
        slider2.setValue(profilesData.getInteger(""+ProfileInfo.selectedPlayerProfile +"StaticHolds", 1));
        slider2.setWidth(500);
        slider2.setName("staticholdsslider");

        Label staticsLabel = new Label("Static Holds", skin);
        staticsLabel.setPosition(900 - staticsLabel.getWidth()/2,550);
        staticsLabel.setFontScale(1);

        Label durationLabel = new Label("Duration", skin);
        durationLabel.setPosition(900 - durationLabel.getWidth()/2,400);
        durationLabel.setFontScale(1);

        Slider durationSlider = new Slider(1000,3000, 1000, false,skin);
        durationSlider.setPosition(650, 350);;
        durationSlider.setValue(profilesData.getInteger(""+ProfileInfo.selectedPlayerProfile +"Duration", 2000));
        durationSlider.setWidth(500);
        durationSlider.setName("durationslider");

        //skin.getFont("font").getData().setScale(1.5f); //set skin font size
        //SelectBox profileBox = new SelectBox(skin);
        //profileBox.setItems(new String[] {"Mikko ", "Mika","Miika","Henri", "Juuso", "tyyppi", "tyyppi2", "tyyppi3", "tyypi4"});
        //profileBox.setPosition(100,50);
        //profileBox.setScale(100f); isontaa buttonia mutta ei grafiikkaa
        //profileBox.setWidth(400);

        stage.addActor(slider);
        stage.addActor(slider2);
        stage.addActor(durationSlider);
        stage.addActor(difficultyLabel);
        stage.addActor(staticsLabel);
        stage.addActor(durationLabel);
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
        Gdx.gl.glClearColor(0.16f, 0.45f, 0.6f, 1);
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

            tmpActor = stage.getRoot().findActor("durationslider");    //etsi static holds sliderin value ja tallenna
            tmpInt = (int) tmpActor.getValue();
            profilesData.putInteger(ProfileInfo.selectedPlayerProfile+"Duration", tmpInt );
            profilesData.flush();

            game.setScreen(new MainMenu(game));
        }
        Gdx.app.log("Kenen profiili on valittuna", ""+ProfileInfo.selectedPlayerProfile);
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