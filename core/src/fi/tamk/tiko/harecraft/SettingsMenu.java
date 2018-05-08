package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.Locale;

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
    Array<Actor> radarDotArray;
    Locale locale;
    Boolean clearDotsPressed = false;
    Boolean mainMenuNotSave = false;

    public SettingsMenu(GameMain game) {
        this.game = game;
        //skin = new Skin(Gdx.files.internal("json/glassy-ui.json"));
        skin = Assets.skin_menu;
        //skin = new Skin(Gdx.files.internal("harejson/hare.json"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 800);
        stage = new Stage(new StretchViewport(1280, 800, camera));
        profilesData = Gdx.app.getPreferences("ProfileFile"); //aseta tiedosto preferencesislle
        //currentProfile = ProfileInfo.selectedPlayerProfile;    //aseta aktiivinen pelaajaprofiili muutujaan

        Gdx.input.setInputProcessor(stage);

        ProfileInfo.determineGameLanguage(); //check language data
        locale = ProfileInfo.gameLanguage;
        I18NBundle localizationBundle = I18NBundle.createBundle(Gdx.files.internal("Localization"), locale);


        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(
            Assets.skin_menu.getDrawable("button"),
            Assets.skin_menu.getDrawable("button pressed"),
            Assets.skin_menu.getDrawable("button"),
            Assets.font5
        );
        style.pressedOffsetX = 4;
        style.pressedOffsetY = -4;
        style.downFontColor = new Color(0.59f,0.59f,0.59f,1f);
        style.fontColor = new Color(1f,1f,1f,1f);

        TextButton button = new TextButton(localizationBundle.get("saveButtonText"), style);
        button.setWidth(270);
        button.setHeight(120);
        button.setPosition(1060 -button.getWidth()/2,50);
        button.setName("backbutton");
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

        TextButton returnButton = new TextButton(localizationBundle.get("backButtonText"), style);
        returnButton.setWidth(270);
        returnButton.setHeight(120);
        returnButton.setName("returnbutton");
        returnButton.setPosition(760 -returnButton.getWidth()/2,50);
        returnButton.addListener(new InputListener() {
            Boolean touched = false;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { //touchdown täytyy palauttaa true jotta touchup voi toimia
                touched = true;
                return true;
            }
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (touched)
                    mainMenuNotSave = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor button)
            {
                touched = false;
            }
        });

        Slider slider = new Slider(0,4,1,false, skin);
        slider.setPosition(650,650);
        slider.setValue(profilesData.getInteger(""+ProfileInfo.selectedPlayerProfile +"Difficulty", 2));     //set value to current difficultylevel when loading
        slider.setWidth(500);
        slider.setName("difficultyslider");


        Label.LabelStyle style2 = new Label.LabelStyle(
            Assets.font6,
            new Color(1f,1f,1f,1f)
        );

        Label.LabelStyle instructionStyle = new Label.LabelStyle(
                Assets.font7,
                new Color(1f,1f,1f,1f)
        );

        Label difficultyLabel = new Label(localizationBundle.get("difficultyLabelText"), style2);
        difficultyLabel.setPosition(900 -difficultyLabel.getWidth()/2,700);
        difficultyLabel.setFontScale(1);
        difficultyLabel.setName("difficultylabel");

        Slider slider2 = new Slider(0,5, 1, false,skin);
        slider2.setPosition(650, 500);
        slider2.setValue(profilesData.getInteger(""+ProfileInfo.selectedPlayerProfile +"StaticHolds", 1));
        slider2.setWidth(500);
        slider2.setName("staticholdsslider");

        Label staticsLabel = new Label(localizationBundle.get("staticLabelText"), style2);
        staticsLabel.setPosition(900 - staticsLabel.getWidth()/2,550);
        staticsLabel.setFontScale(1);
        staticsLabel.setName("staticholdlabel");

        //Label durationLabel = new Label(localizationBundle.get("durationLabelText"), skin);
        //durationLabel.setPosition(900 - durationLabel.getWidth()/2,400);
        //durationLabel.setFontScale(1);
        //durationLabel.setName("durationlabel");

        //Slider durationSlider = new Slider(1000,3000, 1000, false,skin);
        //durationSlider.setPosition(650, 350);;
        //durationSlider.setValue(profilesData.getInteger(""+ProfileInfo.selectedPlayerProfile +"Duration", 2000));
        //durationSlider.setWidth(500);
        //durationSlider.setName("durationslider");

        Label sensitivityLabel = new Label(localizationBundle.get("sensitivityLabelText"), style2);
        sensitivityLabel.setPosition(900 - sensitivityLabel.getWidth()/2,400);
        sensitivityLabel.setFontScale(1);
        sensitivityLabel.setName("sensitivitylabel");

        Slider sensitivitySlider = new Slider(0.5f,1.5f, 0.1f, false,skin);
        sensitivitySlider.setPosition(650, 350);
        sensitivitySlider.setValue(profilesData.getFloat(""+ProfileInfo.selectedPlayerProfile +"Sensitivity", 1f));
        sensitivitySlider.setWidth(500);
        sensitivitySlider.setName("sensitivityslider");


        CheckBox.CheckBoxStyle style3 = new CheckBox.CheckBoxStyle(
                Assets.skin_menu.getDrawable("checkbox checked"),
                Assets.skin_menu.getDrawable("checkbox"),
                Assets.font6,
                new Color(1f,1f,1f,1f)
        );

        style = new TextButton.TextButtonStyle(
                Assets.skin_menu.getDrawable("button"),
                Assets.skin_menu.getDrawable("button pressed"),
                Assets.skin_menu.getDrawable("button"),
                Assets.font6
        );
        style.pressedOffsetX = 4;
        style.pressedOffsetY = -4;
        style.downFontColor = new Color(0.59f,0.59f,0.59f,1f);
        style.fontColor = new Color(1f,1f,1f,1f);

        TextButton clearDotsButton = new TextButton(localizationBundle.get("clearButtonText"), style);
        clearDotsButton.setWidth(320);
        clearDotsButton.setHeight(80);
        clearDotsButton.setPosition(330 - clearDotsButton.getWidth()/2,180);
        clearDotsButton.setName("cleardotbutton");
        clearDotsButton.addListener(new InputListener() {
            Boolean touched = false;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                touched = true;
                return true;
            }
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (touched)
                    clearDotsPressed = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor button)
            {
                touched = false;
            }
        });

        CheckBox invertYCheckBox = new CheckBox(localizationBundle.get("invertLabelText"), style3);
        invertYCheckBox.setChecked(profilesData.getBoolean(ProfileInfo.selectedPlayerProfile +"Invert",true));
        invertYCheckBox.setName("invertcheckbox");
        invertYCheckBox.getLabelCell().padLeft(50f);
        invertYCheckBox.setPosition(900 -invertYCheckBox.getWidth()/2, 235);


        //LISÄLABELIT ALKAA
        style2 = new Label.LabelStyle(
                Assets.font7,
                new Color(1f,1f,1f,1f)
        );

        Label radarInstructions = new Label(localizationBundle.get("radarInstructionsText"), instructionStyle);
        radarInstructions.setPosition(330 -radarInstructions.getWidth()/2, 90);
        radarInstructions.setName("radarInstructions");

        Label radarInstructions2 = new Label(localizationBundle.get("radarInstructionsText2"), instructionStyle);
        radarInstructions2.setPosition(330 -radarInstructions2.getWidth()/2, 50);
        radarInstructions2.setName("radarInstructions2");

        Label hardLabel = new Label(localizationBundle.get("hardText"), instructionStyle);
        hardLabel.setPosition(900 +250 -hardLabel.getWidth(),700);
        hardLabel.setName("hardtext");

        Label easyLabel = new Label(localizationBundle.get("easyText"), instructionStyle);
        easyLabel.setPosition(900 -250 ,700);
        easyLabel.setName("easytext");

        Label hundredLabel = new Label(localizationBundle.get("100Text"), instructionStyle);
        hundredLabel.setPosition(900 +250 -hundredLabel.getWidth(),550);
        hundredLabel.setName("hundredtext");

        Label zeroLabel = new Label(localizationBundle.get("0Text"), instructionStyle);
        zeroLabel.setPosition(900 -250 ,550);
        zeroLabel.setName("zerotext");

        Label hundredfiftyLabel = new Label(localizationBundle.get("150Text"), instructionStyle);
        hundredfiftyLabel.setPosition(900 + 250 -hundredfiftyLabel.getWidth(), 400);
        hundredfiftyLabel.setName("hundredfiftylabel");

        Label fiftyLabel = new Label(localizationBundle.get("50Text"), instructionStyle);
        fiftyLabel.setPosition(900 -250, 400);
        fiftyLabel.setName("fiftylabel");

        Label rehabilitationSettings = new Label(localizationBundle.get("rehabOptions"), instructionStyle);
        rehabilitationSettings.setPosition(320 -rehabilitationSettings.getWidth()/2,130);
        rehabilitationSettings.setName("rehablabel");


        stage.addActor(slider);
        stage.addActor(slider2);
        //stage.addActor(durationSlider);
        stage.addActor(difficultyLabel);
        stage.addActor(staticsLabel);
        //stage.addActor(durationLabel);
        stage.addActor(sensitivityLabel);
        stage.addActor(sensitivitySlider);
        stage.addActor(invertYCheckBox);

        stage.addActor(button);
        stage.addActor(returnButton);
        stage.addActor(new Radar());
        stage.addActor(clearDotsButton);

        stage.addActor(radarInstructions);
        stage.addActor(radarInstructions2);
        stage.addActor(hardLabel);
        stage.addActor(easyLabel);
        stage.addActor(zeroLabel);
        stage.addActor(hundredLabel);
        stage.addActor(fiftyLabel);
        stage.addActor(hundredfiftyLabel);
        stage.addActor(rehabilitationSettings);

        loadVectordata();
        //stage.addActor(profileBox);
    }

    @Override
    public void render (float delta) {
        //Gdx.gl.glClearColor(0.16f, 0.45f, 0.6f, 1);
        Gdx.gl.glClearColor(68f/255f, 153f/255f, 223f/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //removeRadarDots();

        stage.act();
        stage.draw();
        //game.sBatch.begin(); //onko tarpeellista
        //Assets.font.draw(game.sBatch, "Difficulty", 730,750);
        //game.sBatch.end(); //oliko tämä tarpeellinen???

        if (returnToMainMenu) { //Tallennetaan muutokset aktiiviseen profiiliin

            Slider tmpActor = stage.getRoot().findActor("difficultyslider");    //etsi difficulty sliderin value ja tallenna
            int tmpInt = (int) tmpActor.getValue();
            profilesData.putInteger(ProfileInfo.selectedPlayerProfile+"Difficulty", tmpInt );
            profilesData.flush();

            tmpActor = stage.getRoot().findActor("staticholdsslider");    //etsi static holds sliderin value ja tallenna
            tmpInt = (int) tmpActor.getValue();
            profilesData.putInteger(ProfileInfo.selectedPlayerProfile+"StaticHolds", tmpInt );
            profilesData.flush();

            //tmpActor = stage.getRoot().findActor("durationslider");    //etsi static holds sliderin value ja tallenna
            //tmpInt = (int) tmpActor.getValue();
            //profilesData.putInteger(ProfileInfo.selectedPlayerProfile+"Duration", tmpInt );
            //profilesData.flush();

            tmpActor = stage.getRoot().findActor("sensitivityslider");    //etsi static holds sliderin value ja tallenna
            float tmpFloat = tmpActor.getValue();
            profilesData.putFloat(ProfileInfo.selectedPlayerProfile+"Sensitivity", tmpFloat );
            profilesData.flush();

            CheckBox tmpBox = stage.getRoot().findActor("invertcheckbox");
            Boolean bool = tmpBox.isChecked();
            profilesData.putBoolean(ProfileInfo.selectedPlayerProfile+"Invert", bool);
            profilesData.flush();

            saveVectorData();

            game.setScreen(new MainMenu(game,false));
        }

        if (clearDotsPressed) {
            removeRadarDots2();
            clearDotsPressed = false;
        }
        if (mainMenuNotSave) {
            game.setScreen(new MainMenu(game,false));
        }
        //Gdx.app.log("Kenen profiili on valittuna", ""+ProfileInfo.selectedPlayerProfile);
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

    public int checkDotsAmount() {
        Array<Actor> stageActors = stage.getActors();
        int arraySize = stageActors.size;
        int dotAmount = 0;

        for (int i = 0; i < arraySize; i++) {
            Actor tmpActor = stageActors.get(i);
            if (tmpActor.getName().equals("dot")) {
                dotAmount++;
            }
        }
        Gdx.app.log("actors" , " found : " +dotAmount);
        return dotAmount;
    }

    public void removeRadarDots() { //was used to remove dots after 6th press
        if (checkDotsAmount() > 6) {
            Actor tmpActor;
            for (int i = 0; i < 7; i++) {
                tmpActor = stage.getRoot().findActor("dot");
                stage.getRoot().removeActor(tmpActor);
            }
        }
    }

    public void removeRadarDots2() {
            Actor tmpActor;
            int dots;
            dots = checkDotsAmount();

            for (int i = 0; i < dots; i++) {
                tmpActor = stage.getRoot().findActor("dot");
                stage.getRoot().removeActor(tmpActor);
            }
    }

    public void saveVectorData() {
        int dotAmount = checkDotsAmount();
        for (int i = 0; i < dotAmount; i++) {
            RadarDot tmpActor = stage.getRoot().findActor("dot");
            profilesData.putInteger(ProfileInfo.selectedPlayerProfile+"VectorX"+i, (int) tmpActor.getMyVectorX());
            profilesData.putInteger(ProfileInfo.selectedPlayerProfile+"VectorY"+i, (int) tmpActor.getMyVectorY());
            Gdx.app.log("save" , " ACTUAL dot");
            profilesData.flush();
            stage.getRoot().removeActor(tmpActor);
        }
        for (int i = 0 +dotAmount; i < 6; i++) {
            //RadarDot tmpActor = stage.getRoot().findActor("dot");
            profilesData.putInteger(ProfileInfo.selectedPlayerProfile+"VectorX"+i, 0);
            profilesData.putInteger(ProfileInfo.selectedPlayerProfile+"VectorY"+i, 0);
            Gdx.app.log("save" , " ZERO dot");
            profilesData.flush();
            //stage.getRoot().removeActor(tmpActor);
        }
    }

    public void loadVectordata() {
        for (int i = 0; i < 6; i++) {
            int x = profilesData.getInteger(ProfileInfo.selectedPlayerProfile +"VectorX"+i, 0);
            int y = profilesData.getInteger(ProfileInfo.selectedPlayerProfile +"VectorY"+i, 0);
            if ( !(x == 0f && y == 0) ) {
                stage.addActor(new RadarDot(x +250,  y +250));
            }
        }
    }
}

class Radar extends Actor {
    Circle circle;
    private TextureRegion radarTexture;

    public Radar() {
        radarTexture = Assets.texR_radar;
        setName("radar");
        setBounds(70,250,500,500);
        circle = new Circle();
        circle.setRadius(230);
        circle.setPosition(250,250);
        addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("Touch ","detected in actor");
                if (circle.contains(x,y) && calculateMyDots() < 6) {
                    Gdx.app.log("Touch ", "detected in circle   " +x +"     " +y);
                    getStage().addActor(new RadarDot((int)x, (int) y));
                }
                return true;
            }

        });
    }

    public int calculateMyDots() {

        Array<Actor> stageActors = super.getStage().getActors();
        int arraySize = stageActors.size;
        int dotAmount = 0;

        for (int i = 0; i < arraySize; i++) {
            Actor tmpActor = stageActors.get(i);
            if (tmpActor.getName().equals("dot")) {
                dotAmount++;
            }
        }
        Gdx.app.log("actors" , " found : " +dotAmount);
        return dotAmount;
    }

    @Override
    public void draw(Batch batch, float alpha) {
        batch.draw(radarTexture, getX(),getY(),getWidth(),getHeight());
    }
}

class RadarDot extends Actor {
    private TextureRegion dotTexture;

    public RadarDot(int x, int y) {
        setName("dot");
        dotTexture = Assets.texR_radar_dot;
        setBounds(x +70 -25,y +250 -25, 50,50);
    }

    @Override
    public void draw(Batch batch, float alpha) {
        batch.draw(dotTexture, getX(),getY(),getWidth(),getHeight());
        //getMyVectorX();
        //getMyVectorY();
    }

    public float getMyVectorX() {
        float number = getX() - 295f;
        //Gdx.app.log("X : ", "" +number );
        return number;
    }
    public float getMyVectorY() {
        float number = getY() - 475;
        //Gdx.app.log("Y : ", "" +number );
        return number;
    }
}