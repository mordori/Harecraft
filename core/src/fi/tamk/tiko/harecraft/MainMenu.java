package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.InputProcessor;
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
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by musta on 26.3.2018.
 */

public class MainMenu extends ScreenAdapter implements InputProcessor {

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
}
