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
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import static fi.tamk.tiko.harecraft.GameMain.camera;
import static fi.tamk.tiko.harecraft.GameMain.dBatch;
import static fi.tamk.tiko.harecraft.GameMain.sBatch;
import static fi.tamk.tiko.harecraft.GameMain.orthoCamera;
import static fi.tamk.tiko.harecraft.MainMenu.localizationBundle;

/**
 * Created by musta on 27.4.2018.
 */

public class SplashScreen extends ScreenAdapter {
    GameMain game;
    OrthographicCamera localCamera;
    Sprite tamkSprite;
    Sprite exeriumSprite;
    Sprite projectileSprite;
    Sprite tikoSprite;
    Sprite loadingSprite;

    float alpha;
    float timer;
    boolean isAudioLoaded;
    boolean isAssetsLoaded;

    public SplashScreen(GameMain game) {
        this.game = game;
        localCamera = new OrthographicCamera();
        localCamera.setToOrtho(false, 1280, 800);
        localCamera.update();
        isAudioLoaded = false;
        isAssetsLoaded = false;

        float yOffset = 40f;
        float size = 1.2f;


        Assets.tex_lataa = Assets.loadTexture("tex_lataa.png");
        Assets.tex_loading = Assets.loadTexture("tex_loading.png");
        Assets.tex_projectile = Assets.loadTexture("projectile.png");
        Assets.tex_tamk = Assets.loadTexture("tamk.png");
        Assets.tex_tiko = Assets.loadTexture("tiko.png");
        Assets.tex_exerium = Assets.loadTexture("exerium.png");


        if(Gdx.app.getPreferences("ProfileFile").getString("Language").contains("Finnish")) {
            loadingSprite = new Sprite(Assets.tex_lataa);
        }
        else loadingSprite = new Sprite(Assets.tex_loading);

        loadingSprite.setPosition(1280/2 - loadingSprite.getWidth()/2, 800/1.21f - loadingSprite.getHeight()/2);

        tamkSprite = new Sprite(Assets.tex_tamk);
        tamkSprite.setSize(488/size,215/size);
        tamkSprite.setPosition(920 -tamkSprite.getWidth()/2 -25,533 - tamkSprite.getHeight()/2 -20 - yOffset*3f);

        exeriumSprite = new Sprite(Assets.tex_exerium);
        exeriumSprite.setSize(575/size, 187/size);
        exeriumSprite.setPosition(370 -exeriumSprite.getWidth()/2,100 - yOffset);

        projectileSprite = new Sprite(Assets.tex_projectile);
        projectileSprite.setSize(514/size,329/size);
        projectileSprite.setPosition(360 - projectileSprite.getWidth()/2, 520 - projectileSprite.getHeight()/2 - yOffset*3f);

        tikoSprite = new Sprite(Assets.tex_tiko);
        tikoSprite.setSize(423/size,166/size);
        tikoSprite.setPosition(910 - tikoSprite.getWidth()/2, 233 -tikoSprite.getHeight()/2 -35 - yOffset);

        timer = 0f;
        alpha = 1f;
        sBatch.setProjectionMatrix(localCamera.combined);
    }

    public void render (float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sBatch.begin();
        loadingSprite.draw(sBatch, alpha);
        tamkSprite.draw(sBatch, alpha);
        exeriumSprite.draw(sBatch, alpha);
        projectileSprite.draw(sBatch, alpha);
        tikoSprite.draw(sBatch, alpha);
        sBatch.end();

        if(alpha == 0f && timer > 2f) {
            dBatch = new DecalBatch(new MyGroupStrategy(camera));
            sBatch.setProjectionMatrix(orthoCamera.combined); //palautetaan projektion matrix alkuperäiseksi
            game.setScreen(new MainMenu(game,true));
        }

        if(timer != 0f) {
            if(isAudioLoaded && isAssetsLoaded) alpha -= 0.02f;
            else {
                if (!isAssetsLoaded) isAssetsLoaded = Assets.load();
                if (!isAudioLoaded) isAudioLoaded = AssetsAudio.load();
            }
            if(alpha < 0f) alpha = 0f;
        }

        timer += delta;
    }

    @Override
    public void hide() { dispose(); }

    @Override
    public void dispose() {
    }
}