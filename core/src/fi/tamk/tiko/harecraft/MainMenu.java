package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
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

import static fi.tamk.tiko.harecraft.Assets.sprites_menu_plane;
import static fi.tamk.tiko.harecraft.GameMain.fbo;
import static fi.tamk.tiko.harecraft.GameMain.musicVolume;
import static fi.tamk.tiko.harecraft.GameMain.sBatch;
import static fi.tamk.tiko.harecraft.GameMain.texture;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.GameScreen.worldIndex;
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
    Boolean creditsMenu = false;
    Preferences profilesData;
    static ArrayList<String> profiles;
    float opacity = 0f;
    Locale locale;
    String[] top3Names = new String[3];
    int[] top3Score = new int[3];
    Table highScoreTable = new Table();

    Sprite sprite_plane;
    float stateTime;
    float randomTime;
    float x, y;
    boolean isLaunched;
    MyAnimation<TextureRegion> animation_plane = Assets.animation_menu_plane;
    static I18NBundle localizationBundle;

    Texture background;

    public MainMenu(GameMain game, boolean isLaunched) {
        this.game = game;
        this.isLaunched = isLaunched;

        logo = new Texture("textures/logo.png");
        //skin = new Skin(Gdx.files.internal("json/glassy-ui.json"));
        //skin = new Skin(Gdx.files.internal("harejson/hare.json"));
        skin = Assets.skin_menu;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        //stage = new Stage(new StretchViewport(1280, 800, camera));
        stage = new Stage(new StretchViewport(1280f, 800f, camera));
        profilesData = Gdx.app.getPreferences("ProfileFile"); // KEY ja VALUE
        ProfileInfo.determineGameLanguage(); //check language data
        locale = ProfileInfo.gameLanguage;
        localizationBundle = I18NBundle.createBundle(Gdx.files.internal("Localization"), locale);

        background = new Texture(Gdx.files.internal("textures/menubg.png"));

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


        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(
                Assets.skin_menu.getDrawable("button"),
                Assets.skin_menu.getDrawable("button pressed"),
                Assets.skin_menu.getDrawable("button"),
                Assets.font3);
        style.pressedOffsetX = 4;
        style.pressedOffsetY = -4;
        style.downFontColor = new Color(0.59f,0.59f,0.59f,1f);
        style.fontColor = new Color(1f,1f,1f,1f);

        TextButton playButton = new TextButton(localizationBundle.get("playButtonText"), style);
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

        style = new TextButton.TextButtonStyle(
                Assets.skin_menu.getDrawable("button"),
                Assets.skin_menu.getDrawable("button pressed"),
                Assets.skin_menu.getDrawable("button"),
                Assets.font6);
        style.pressedOffsetX = 4;
        style.pressedOffsetY = -4;
        style.downFontColor = new Color(0.59f,0.59f,0.59f,1f);
        style.fontColor = new Color(1f,1f,1f,1f);

        TextButton settingsButton = new TextButton(localizationBundle.get("settingsButtonText"), style);
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

        TextButton profilesButton = new TextButton(localizationBundle.get("profilesButtonText"), style);
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


        com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle listStyle = new com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle(
                Assets.font6,
                new Color(1f,1f,1f,1f),
                new Color(1f,1f,1f,1f),
                Assets.skin_menu.getDrawable("round-dark-gray")
        );
        listStyle.background = Assets.skin_menu.getDrawable("round-gray");

        SelectBox.SelectBoxStyle style2 = new SelectBox.SelectBoxStyle(
                Assets.font6,
                new Color(1f,1f,1f,1f),
                Assets.skin_menu.getDrawable("listbutton"),
                Assets.skin_menu.get("default", ScrollPane.ScrollPaneStyle.class),
                listStyle
                );
        style2.backgroundOpen = Assets.skin_menu.getDrawable("listbutton pressed");


        //User valintaboxin luominen alkaa
        //skin.getFont("font").getData().setScale(1f); //set skin font size
        skin.getFont("komika").getData().setScale(1f); //set skin font size
        SelectBox profileBox = new SelectBox(style2);
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
        languageButton.setPosition(1140,60);
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

        BananaButton bananaButton = new BananaButton();
        bananaButton.setPosition(40, 55);
        bananaButton.addListener(new InputListener() {
            Boolean touched = false;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) { //touchdown täytyy palauttaa true jotta touchup voi toimia
                touched = true;
                return true;
            }
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (touched) {
                    creditsMenu = true;
                }
            }
            public void exit(InputEvent event, float x, float y, int pointer, Actor button)
            {
                touched = false;
            }
        });

        stage.addActor(settingsButton);
        stage.addActor(profilesButton);
        stage.addActor(playButton);
        stage.addActor(profileBox);
        stage.addActor(languageButton);
        stage.addActor(highScoreTable);
        stage.addActor(bananaButton);

        //Gdx.gl.glClearColor(32/255f, 137/255f, 198/255f, 1f);

        randomTime = MathUtils.random(5f, 8f);
        sprite_plane = new Sprite((TextureRegion) animation_plane.getKeyFrame(0));
        if(!animation_plane.isFlipped) x = -100f;
        else x = 1300;
        y = 0f;
        sprite_plane.setPosition(x, y);

        AssetsAudio.playMusic(AssetsAudio.MUSIC_COURSE_2);
        musicVolume = 0.5f;
        AssetsAudio.setMusicVolume(musicVolume);
    }

    public void render (float delta) {
        if(isLaunched) {
            switch(worldIndex) {
                case 0:
                    Gdx.gl.glClearColor(32/255f, 137/255f, 198/255f, 1f);
                    break;
                case 1:
                    Gdx.gl.glClearColor(137/255f, 189/255f, 255/255f, 1f);
                    break;
                case 2:
                    Gdx.gl.glClearColor(60/255f, 140/255f, 208/255f, 1f);
                    break;
                default:
                    Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
                    break;
            }
        }
        else Gdx.gl.glClearColor(68f/255f, 153f/255f, 223f/255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(!animation_plane.isFlipped) {
            x -= 6f * (SCREEN_WIDTH/1280f);
            y += 0.2f * (SCREEN_WIDTH/1280f);
        }
        else{
            x += 6f * (SCREEN_WIDTH/1280f);
            y -= 0.2f * (SCREEN_WIDTH/1280f);
        }

        if(stateTime > randomTime && !Assets.animation_menu_plane.isFlipped) {
            x = 0f - sprite_plane.getWidth();
            stateTime = 0f;
            y = SCREEN_HEIGHT/2.2f - MathUtils.random(SCREEN_HEIGHT/9f, 0f);

            Assets.flip(sprites_menu_plane);
            animation_plane.isFlipped = true;
            randomTime = MathUtils.random(8f, 11f);
        }
        else if(stateTime > randomTime && Assets.animation_menu_plane.isFlipped) {
            x = SCREEN_WIDTH;
            stateTime = 0f;
            y = SCREEN_HEIGHT/2.2f - MathUtils.random(SCREEN_HEIGHT/9f, 0f);

            Assets.flip(sprites_menu_plane);
            animation_plane.isFlipped = false;
            randomTime = MathUtils.random(8f, 11f);
        }

        sprite_plane = sprites_menu_plane.get(animation_plane.getKeyFrameIndex(stateTime));
        sprite_plane.setScale(SCREEN_WIDTH/1280f);
        sprite_plane.setPosition(x, y);
        stateTime += delta;

        float size = SCREEN_WIDTH/1920f;

        fbo.begin();
            Gdx.gl.glClearColor(68f/255f, 153f/255f, 223f/255f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            sBatch.begin();
                sBatch.draw(background, 0 , 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                sprite_plane.draw(sBatch);
                sBatch.draw(logo,
                        Gdx.graphics.getWidth()/2f - (logo.getWidth()/1.25f * size / 2f),
                        Gdx.graphics.getHeight() - (logo.getHeight()/1.25f * size) - Gdx.graphics.getHeight()/35f,
                        logo.getWidth()/1.25f * size,
                        logo.getHeight()/1.25f * size);
            sBatch.end();
            stage.act();
            stage.draw();
        fbo.end();
        renderToTexture();

        if(startGame) {
            setCurrentPlayerProfile();      //käynnistyksessä asetetaan Profileinfo.selectedPlayerProfile voimaan
            ProfileInfo.load();
            game.setScreen(new LevelSelectMenu(game, profiles));
        }
        else if(!isLaunched) opacity = 1f;
        else if(isLaunched) {
            opacity += delta;
            if(opacity > 1f) {
                isLaunched = false;
                opacity = 1f;
            }
        }
        if (settingsMenu) {
            setCurrentPlayerProfile();
            game.setScreen(new SettingsMenu(game));
        }
        if (profilesMenu) {
            setCurrentPlayerProfile();
            game.setScreen(new ProfileMenu(game));
        }
        if (createUser) {       //force profile creation
            setCurrentPlayerProfile();
            game.setScreen(new CreateUser(game));
        }
        if (reloadMainMenu) {
            setCurrentPlayerProfile();
            game.setScreen(new MainMenu(game,false));
        }
        if (creditsMenu) {
            setCurrentPlayerProfile();
            game.setScreen(new CreditsMenu(game));
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

class BananaButton extends Actor {
    Texture banana;

    public BananaButton() {
        banana = new Texture(Gdx.files.internal("textures/banana.png"));
        setBounds(getX(),getY(),100,100);
    }

    public void draw(Batch batch, float alpha) {
        batch.draw(banana, getX(),getY(),100,100);
    }
}
