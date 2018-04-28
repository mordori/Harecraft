package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import static fi.tamk.tiko.harecraft.GameMain.sBatch;
import static fi.tamk.tiko.harecraft.GameMain.orthoCamera;

/**
 * Created by musta on 27.4.2018.
 */

public class SplashScreen extends ScreenAdapter {

    GameMain game;
    Stage stage;
    OrthographicCamera camera;
    Sprite tamkSprite;
    Sprite exeriumSprite;
    Sprite projectileSprite;
    Sprite tikoSprite;

    float alpha;
    float timer;

    public SplashScreen(GameMain game) {
        this.game = game;
        //camera = new OrthographicCamera();
        //camera.setToOrtho(false, 1280, 800);
        //stage = new Stage(new StretchViewport(1280, 800, camera));
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 800);
        camera.update();

        tamkSprite = new Sprite(new Texture(Gdx.files.internal("textures/tamk_eng_pysty_NEGA.png")));
        tamkSprite.setSize(488,215);
        tamkSprite.setPosition(960 -tamkSprite.getWidth()/2,533 - tamkSprite.getHeight()/2 -20);

        exeriumSprite = new Sprite(new Texture(Gdx.files.internal("textures/exerium_logo_low_pixel.png")));
        exeriumSprite.setSize(575, 187);
        exeriumSprite.setPosition(320 -exeriumSprite.getWidth()/2,100);

        projectileSprite = new Sprite((new Texture(Gdx.files.internal("textures/projectile.png"))));
        projectileSprite.setSize(514,329 );
        projectileSprite.setPosition(320 - projectileSprite.getWidth()/2, 533 - projectileSprite.getHeight()/2);

        tikoSprite = new Sprite(new Texture(Gdx.files.internal("textures/tiko.png")));
        tikoSprite.setSize(423,166);
        tikoSprite.setPosition(960 - tikoSprite.getWidth()/2, 233 -tikoSprite.getHeight()/2 -20);
        //skin = Assets.skin_menu;
        timer = 0f;
        alpha = 1f;
        sBatch.setProjectionMatrix(camera.combined);
    }

    public void render (float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sBatch.begin();
        tamkSprite.draw(sBatch, alpha);
        exeriumSprite.draw(sBatch, alpha);
        projectileSprite.draw(sBatch, alpha);
        tikoSprite.draw(sBatch, alpha);
        sBatch.end();

        if (alpha > 0.01f && timer >= 100f)
        alpha -= 0.012f;

        if (timer > 200f) {
            sBatch.setProjectionMatrix(orthoCamera.combined); //palautetaan projektion matrix alkuperäiseksi
            game.setScreen(new MainMenu(game));
        }

        timer++;
    }
}