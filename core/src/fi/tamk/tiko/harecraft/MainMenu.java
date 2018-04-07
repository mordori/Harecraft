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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.ArrayList;
import java.util.Collections;

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
    Boolean scoresMenu = false;
    Preferences profilesData;
    ArrayList<String> profiles;
    float opacity = 0f;

    public MainMenu(GameMain game) {
        this.game = game;
        logo = new Texture("textures/logo.png");
        skin = new Skin(Gdx.files.internal("json/glassy-ui.json"));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 800);
        stage = new Stage(new StretchViewport(1280, 800, camera));
        profilesData = Gdx.app.getPreferences("ProfileFile"); // KEY ja VALUE
        profiles = new ArrayList<String>();
        for (int i = 0; i<200; i++) {
            String tempName;
            tempName = profilesData.getString("username" +i, "novalue");
            if (!tempName.equals("novalue")) {
                profiles.add(tempName);
            }
            else {
                break;
            }
        }

        profilesData.putString("username" +0,"Mikko");   //create profiles on disk
        profilesData.putString("username" +1,"Henna");
        profilesData.flush();

        //profiles.add("Mikko"); //profiileissa 1 järjestysnumero 2 profiilin nimi
        Gdx.input.setInputProcessor(stage);

        TextButton playButton = new TextButton("Play", skin);
        playButton.setPosition(640 -playButton.getWidth()/2,290);
        playButton.setName("playbutton");

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

        TextButton settingsButton = new TextButton("Settings", skin);
        settingsButton.setPosition(640 -settingsButton.getWidth()/2,170);
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

        TextButton scoresButton = new TextButton("Scores", skin);
        scoresButton.setPosition(640 -scoresButton.getWidth()/2,50);
        scoresButton.setName("scoresbutton");

        skin.getFont("font").getData().setScale(1f); //set skin font size
        SelectBox profileBox = new SelectBox(skin);
        profileBox.setName("profileBox");
        //profileBox.setItems(new String[] {"Mikko ", "Mika","Miika","Henri", "Juuso", "tyyppi", "tyyppi2", "tyyppi3", "tyypi4"});
        Collections.sort(profiles);
        profileBox.setItems(profiles.toArray());
        profileBox.setPosition(850,50);
        //profileBox.setScale(100f); isontaa buttonia mutta ei grafiikkaa
        profileBox.setWidth(400);
        profileBox.setSelected(ProfileInfo.selectedPlayerProfile);
        //profileBox.setSelected("MikkoK");
        //profileBox.getSelected();

        stage.addActor(playButton);
        stage.addActor(settingsButton);
        stage.addActor(scoresButton);
        stage.addActor(profileBox);

        Gdx.gl.glClearColor(42/255f, 116/255f, 154/255f, 1f);
    }

    public void render (float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        fbo.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        sBatch.begin();
        sBatch.draw(logo, Gdx.graphics.getWidth()/8 , Gdx.graphics.getHeight()/2,Gdx.graphics.getWidth() - (Gdx.graphics.getWidth()/8) *2,Gdx.graphics.getHeight()/2);
        sBatch.end();
        stage.act();
        stage.draw();
        fbo.end();
        renderToTexture();


        if (startGame) {
            opacity -= Gdx.graphics.getDeltaTime() * 1.5f;
            if(opacity < 0f) opacity = 0f;
            if(opacity == 0f) {
                setCurrentPlayerProfile();      //käynnistyksessä asetetaan Profileinfo.selectedPlayerProfile voimaan
                ProfileInfo.load();
                game.setScreen(new GameScreen(game, MathUtils.random(0,1)));
            }
        }
        else {
            opacity += Gdx.graphics.getDeltaTime();
            if(opacity > 1f) opacity = 1f;
        }
        if (settingsMenu) {
            setCurrentPlayerProfile();
            game.setScreen(new SettingsMenu(game));
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
