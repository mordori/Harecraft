package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static fi.tamk.tiko.harecraft.GameMain.fbo;
import static fi.tamk.tiko.harecraft.GameMain.sBatch;
import static fi.tamk.tiko.harecraft.GameMain.texture;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.Shaders2D.shader2D_vignette;

/**
 * Created by musta on 26.3.2018.
 */

public class MainMenu extends ScreenAdapter {

    GameMain game;
    Skin skin;
    Stage stage;
    OrthographicCamera camera;
    Texture logo;
    Boolean startGame = false;
    Boolean settingsMenu = false;
    Boolean profilesMenu = false;
    Boolean createUser = false;
    Boolean reloadMainMenu = false;
    Preferences profilesData;
    ArrayList<String> profiles;
    float opacity = 0f;
    Locale locale;
    String[] top3Names = new String[3];
    int[] top3Score = new int[3];
    Table highScoreTable = new Table();

    Sprite sprite_plane;
    float stateTime;
    float x = 1280f;
    float y = 360f;
    MyAnimation<TextureRegion> animation_plane = Assets.animation_menu_plane;

    public MainMenu(GameMain game) {
        this.game = game;


        logo = new Texture("textures/logo.png");
        //skin = new Skin(Gdx.files.internal("json/glassy-ui.json"));
        //skin = new Skin(Gdx.files.internal("harejson/hare.json"));
        skin = Assets.skin_menu;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 800);
        stage = new Stage(new StretchViewport(1280, 800, camera));
        profilesData = Gdx.app.getPreferences("ProfileFile"); // KEY ja VALUE
        ProfileInfo.determineGameLanguage(); //check language data
        locale = ProfileInfo.gameLanguage;
        I18NBundle localizationBundle = I18NBundle.createBundle(Gdx.files.internal("Localization"), locale);

        profiles = new ArrayList<String>();
        for (int i = 0; i<200; i++) {
            String tempName;
            tempName = profilesData.getString("username" +i, "novalue");
            if (!tempName.equals("novalue")) {
                profiles.add(tempName);
            }
        }

        if (profiles.size() == 0) {
            createUser = true;
        }

        //profilesData.putString("username" +0,"Mikko");   //create profiles on disk
        //profilesData.putString("username" +3,"Henna");
        //profilesData.flush();

        //profiles.add("Mikko"); //profiileissa 1 järjestysnumero 2 profiilin nimi
        Gdx.input.setInputProcessor(stage);

        TextButton playButton = new TextButton(localizationBundle.get("playButtonText"), skin);
        playButton.setPosition(640 -playButton.getWidth()/2,50); //y290 x640
        playButton.setName("playbutton");
        playButton.setHeight(150f);

        playButton.addListener(new InputListener() {
            Boolean touched = false;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { //touchdown täytyy palauttaa true jotta touchup voi toimia
                touched = true;
                return true;
            }
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (touched)
                    startGame = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor button)
            {
                touched = false;
            }
        });

        TextButton settingsButton = new TextButton(localizationBundle.get("settingsButtonText"), skin);
        settingsButton.setWidth(270f);
        settingsButton.setHeight(120f);
        settingsButton.setPosition(310 -settingsButton.getWidth()/2,50); //y170 x640
        settingsButton.setName("settingsbutton");

        settingsButton.addListener(new InputListener() {
            Boolean touched = false;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { //touchdown täytyy palauttaa true jotta touchup voi toimia
                touched = true;
                return true;
            }
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (touched)
                    settingsMenu = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor button)
            {
                touched = false;
            }
        });

        TextButton profilesButton = new TextButton(localizationBundle.get("profilesButtonText"), skin);
        profilesButton.setHeight(120f);
        profilesButton.setWidth(270f);
        profilesButton.setPosition(970 -profilesButton.getWidth()/2,50); //y50 x640
        profilesButton.setName("profilesbutton");

        profilesButton.addListener(new InputListener() {
            Boolean touched = false;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { //touchdown täytyy palauttaa true jotta touchup voi toimia
                touched = true;
                return true;
            }
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (touched)
                    profilesMenu = true;
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor button)
            {
                touched = false;
            }
        });


        //User valintaboxin luominen alkaa
        //skin.getFont("font").getData().setScale(1f); //set skin font size
        skin.getFont("komika").getData().setScale(1f); //set skin font size
        SelectBox profileBox = new SelectBox(skin);
        profileBox.setName("profileBox");
        //profileBox.setItems(new String[] {"Mikko ", "Mika","Miika","Henri", "Juuso", "tyyppi", "tyyppi2", "tyyppi3", "tyypi4"});
        Collections.sort(profiles);
        profileBox.setAlignment(Align.center);
        profileBox.setItems(profiles.toArray());
        profileBox.setHeight(100f);
        profileBox.setPosition(490,220);
        //profileBox.setScale(100f); isontaa buttonia mutta ei grafiikkaa
        profileBox.setWidth(300);
        profileBox.setSelected(ProfileInfo.selectedPlayerProfile);
        //profileBox.setSelected("MikkoK");
        //profileBox.getSelected();

        LanguageButton languageButton = new LanguageButton();
        languageButton.setPosition(40,50);
        languageButton.addListener(new InputListener() {
            Boolean touched = false;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { //touchdown täytyy palauttaa true jotta touchup voi toimia
                touched = true;
                return true;
            }
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (touched) {
                    ProfileInfo.switchLanguage();
                    reloadMainMenu = true;
                }
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor button)
            {
                touched = false;
            }
        });

        makeHighScores();
        highScoreTable.setPosition(220,350);

        stage.addActor(settingsButton);
        stage.addActor(profilesButton);
        stage.addActor(playButton);
        stage.addActor(profileBox);
        stage.addActor(languageButton);
        stage.addActor(highScoreTable);

        Gdx.gl.glClearColor(32/255f, 137/255f, 198/255f, 1f);

        sprite_plane = new Sprite((TextureRegion) animation_plane.getKeyFrame(0));
        sprite_plane.setBounds(1280f,0f, sprite_plane.getWidth(), sprite_plane.getHeight());
        sprite_plane.setPosition(x, y);
    }

    public void render (float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(!animation_plane.isFlipped) x -= 7f;
        else x += 7f;

        if(x < -2500f && !animation_plane.isFlipped) {
            x = -100;
            stateTime = 0f;

            Assets.flip(animation_plane, animation_plane.getKeyFrames().length);
            animation_plane.isFlipped = true;
        }

        if(x > 2500f && Assets.animation_menu_plane.isFlipped) {
            x = 1280f;
            stateTime = 0f;

            Assets.flip(animation_plane, animation_plane.getKeyFrames().length);
            animation_plane.isFlipped = false;
        }

        sprite_plane.setRegion((TextureRegion) animation_plane.getKeyFrame(stateTime));
        sprite_plane.setBounds(x, y, sprite_plane.getRegionWidth(), sprite_plane.getRegionHeight());
        //sprite_plane.setPosition(x, y);
        stateTime += delta;

        fbo.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        sBatch.begin();
        sprite_plane.draw(sBatch);
        sBatch.draw(logo, Gdx.graphics.getWidth()/8 , Gdx.graphics.getHeight()/2,Gdx.graphics.getWidth() - (Gdx.graphics.getWidth()/8) *2,Gdx.graphics.getHeight()/2);
        sBatch.end();
        stage.act();
        stage.draw();
        fbo.end();
        renderToTexture();


        if (startGame) {
            opacity -= delta;
            if(opacity < 0f) opacity = 0f;
            if(opacity == 0f) {
                setCurrentPlayerProfile();      //käynnistyksessä asetetaan Profileinfo.selectedPlayerProfile voimaan
                ProfileInfo.load();
                game.setScreen(new GameScreen(game, MathUtils.random(0, 2)));
                //game.setScreen(new GameScreen(game, 1));
            }
        }
        else {
            opacity += delta;
            if(opacity > 1f) opacity = 1f;
        }
        if (settingsMenu) {
            setCurrentPlayerProfile();
            game.setScreen(new SettingsMenu(game));
        }
        if (profilesMenu) {
            game.setScreen(new ProfileMenu(game));
        }
        if (createUser) {       //force profile creation
            game.setScreen(new CreateUser(game));
        }
        if (reloadMainMenu) {
            game.setScreen(new MainMenu(game));
        }
    }

    private void setCurrentPlayerProfile() {
        SelectBox tempActor = stage.getRoot().findActor("profileBox");  //Set selected playerprofile to gamescreen.
        String string = (String) tempActor.getSelected();
        ProfileInfo.selectedPlayerProfile = string;
    }

    public void renderToTexture() {
        texture.setTexture(fbo.getColorBufferTexture());

        //------------------------------------------------
        sBatch.begin();
        texture.draw(sBatch, opacity);
        sBatch.end();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
        logo.dispose();
    }

    public void makeHighScores() {
        List<String> tempProfileList = profiles;
        int tempScoreInt;

        //profilesData.putInteger("Mikko"+"Score", 987);
        //profilesData.putInteger("Miika"+"Score", 200);
        //profilesData.flush();

        for (int i = 0; i <= 2 ; i++) { //Järjestelee top3name top3scores muttujiin 3 parasta profiilia ja coret

            int highestScoreFound = 0;
            String highestProfileName = "----------";

            for (String y : tempProfileList) { //haetaan profiilien scooret
                tempScoreInt = profilesData.getInteger(y + "Score", 0);

                if (tempScoreInt > highestScoreFound) {
                    highestScoreFound = tempScoreInt;
                    highestProfileName = y;
                }
            }
            tempProfileList.remove(highestProfileName);
            //if (highestProfileName.length() > 10) {     //limit String to 10 chars on highscore board
            //    highestProfileName = highestProfileName.substring(0,10);
            //}
            top3Names[i] = highestProfileName;
            top3Score[i] = highestScoreFound;
            Gdx.app.log("paras score oli pelaajalla ", "" +highestProfileName +" lukemalla " +highestScoreFound);

        }

        Label name1 = new Label(top3Names[0], skin);
        Label name2 = new Label(top3Names[1], skin);
        Label name3 = new Label(top3Names[2], skin);

        Label score1 = new Label("" +top3Score[0], skin);
        Label score2 = new Label("" +top3Score[1], skin);
        Label score3 = new Label("" +top3Score[2], skin);


        highScoreTable.add(name1);
        highScoreTable.add(score1);
        highScoreTable.row();
        highScoreTable.add(name2);
        highScoreTable.add(score2);
        highScoreTable.row();
        highScoreTable.add(name3);
        highScoreTable.add(score3);
    }
}

class LanguageButton extends Actor {
    Texture englishFlag;
    Texture finnishFlag;

    public LanguageButton() {
        finnishFlag = new Texture(Gdx.files.internal("textures/fiFlag.png"));
        englishFlag = new Texture(Gdx.files.internal("textures/ukFlag.png"));
        setBounds(getX(),getY(),100,100);
    }

    public void draw(Batch batch, float alpha) {
        if (ProfileInfo.gameLanguage.toString().equals("fi_FI")) {
            batch.draw(englishFlag, getX(),getY(),100,100);
        }
        else {
            batch.draw(finnishFlag, getX(), getY(), 100, 100);
        }
        //if (ProfileInfo.gameLanguage.toString().equals("en_GB")) {
        //    batch.draw(englishFlag, getX(), getY(), 100, 100);
        //}
    }
}

/*
    GameMain game;
    Texture logo;
    Button playButton;
    Button settingsButton;
    Button scoreButton;
    Boolean touchUp = false;

    public MainMenu(GameMain game) {
        this.game = game;
        logo = new Texture("textures/logo.png");
        playButton = new Button("Play", 0);
        settingsButton = new Button("Settings", 100);
        scoreButton = new Button("Scores", 200);
        Gdx.input.setInputProcessor(this);
        Gdx.gl.glClearColor(0.3f, 0.6f, 1f, 1);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.sBatch.begin();
        game.sBatch.draw(logo, Gdx.graphics.getWidth()/8 , Gdx.graphics.getHeight()/2,Gdx.graphics.getWidth() - (Gdx.graphics.getWidth()/8) *2,Gdx.graphics.getHeight()/2);
        playButton.drawMe(game.sBatch);
        settingsButton.drawMe(game.sBatch);
        scoreButton.drawMe(game.sBatch);
        game.sBatch.end();
        if (playButton.getButtonRectangle().contains(Gdx.input.getX(), Gdx.input.getY()) && touchUp == true) {
            game.setScreen(new GameScreen(game, new World()));
        }
        if (settingsButton.getButtonRectangle().contains(Gdx.input.getX(), Gdx.input.getY()) && touchUp == true) {
            game.setScreen(new SettingsMenu(game));
        }
        touchUp = false;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {return false;}

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        touchUp = true;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}

class Button {
    Texture buttonTexture;
    Texture buttonTexturePressed;
    String buttonText = "";
    Rectangle buttonRectangle;
    Boolean isPressed;
    int offsetPosition;
    GlyphLayout textMeasures;
    Sound buttonSound;

    public Button(String bText, int offsetY) {
        buttonSound = Assets.sound_cloud_hit;
        textMeasures= new GlyphLayout();
        isPressed = false;
        buttonText = bText;
        offsetPosition = offsetY;
        buttonTexture = new Texture("textures/playbutton.png");
        buttonTexturePressed = new Texture("textures/playbutton2.png");
        textMeasures.setText(Assets.font, buttonText);
        buttonRectangle = new Rectangle(Gdx.graphics.getWidth()/2 -150,(Gdx.graphics.getHeight()/2 -50) +offsetPosition ,300,100);
    }

    public void drawMe(SpriteBatch batch) {

        if (Gdx.input.isTouched()) {
            if (buttonRectangle.contains(Gdx.input.getX(), Gdx.input.getY()) && isPressed == false) {
                buttonSound.play();
                isPressed = true;
            }
        }

        if (isPressed == true) {
            batch.draw(buttonTexturePressed, Gdx.graphics.getWidth() / 2 - 150, (Gdx.graphics.getHeight() / 2 - 50) -offsetPosition, 300, 100);
        }
        else {
            batch.draw(buttonTexture, Gdx.graphics.getWidth() / 2 - 150, (Gdx.graphics.getHeight() / 2 - 50) -offsetPosition, 300, 100);
        }
        if (!buttonRectangle.contains(Gdx.input.getX(), Gdx.input.getY())) {
            isPressed = false;
        }
        Assets.font.draw(batch,buttonText,Gdx.graphics.getWidth()/2 -textMeasures.width/2,Gdx.graphics.getHeight()/2 +75 -50 -offsetPosition );
    }

    public Rectangle getButtonRectangle() {
        return buttonRectangle;
    }
} */
