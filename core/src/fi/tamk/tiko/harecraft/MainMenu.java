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
import static fi.tamk.tiko.harecraft.GameMain.sBatch;
import static fi.tamk.tiko.harecraft.GameMain.texture;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
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

    public MainMenu(GameMain game, boolean isLaunched) {
        this.game = game;
        this.isLaunched = isLaunched;

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
        localizationBundle = I18NBundle.createBundle(Gdx.files.internal("Localization"), locale);

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

        Assets.music_course_1.play();
        Assets.music_course_1.setVolume(0.45f);
    }

    public void render (float delta) {
        if(isLaunched) Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        else Gdx.gl.glClearColor(32/255f, 137/255f, 198/255f, 1f);
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
            y = SCREEN_HEIGHT/2f - sprite_plane.getHeight()/2f + MathUtils.random(-SCREEN_HEIGHT/10f, SCREEN_HEIGHT/10f);

            Assets.flip(sprites_menu_plane);
            animation_plane.isFlipped = true;
            randomTime = MathUtils.random(8f, 11f);
        }
        else if(stateTime > randomTime && Assets.animation_menu_plane.isFlipped) {
            x = SCREEN_WIDTH;
            stateTime = 0f;
            y = SCREEN_HEIGHT/2f - sprite_plane.getHeight()/2f + MathUtils.random(-SCREEN_HEIGHT/10f, SCREEN_HEIGHT/10f);

            Assets.flip(sprites_menu_plane);
            animation_plane.isFlipped = false;
            randomTime = MathUtils.random(8f, 11f);
        }

        sprite_plane = sprites_menu_plane.get(animation_plane.getKeyFrameIndex(stateTime));
        sprite_plane.setScale(SCREEN_WIDTH/1280f);
        sprite_plane.setPosition(x, y);
        stateTime += delta;

        fbo.begin();
        Gdx.gl.glClearColor(32/255f, 137/255f, 198/255f, 1f);
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
                game.setScreen(new LevelSelectMenu(game, profiles));
                //game.setScreen(new ScoreScreen());
            }
        }
        else {
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
