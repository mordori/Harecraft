package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;

/**
 * Created by musta on 23.2.2018.
 */

public class GameScreen implements Screen {

    GameMain game;
    Decal dec_Background;
    DecalBatch dBatch;
    PerspectiveCamera camera;


    public GameScreen(GameMain game) {
        this.game = game;
        camera = new PerspectiveCamera(60f, Gdx.graphics.getWidth(),Gdx.graphics.getHeight() );
        dBatch = new DecalBatch(new MyGroupStrategy(camera));
        dec_Background = Decal.newDecal(new TextureRegion(Assets.tex_backgroundTest), true);
        dec_Background.setPosition(0f,0f,300f);
        camera.position.set(0f,0f,0f);
        camera.far = 500f;
        camera.lookAt(dec_Background.getPosition());
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        camera.update();

        dBatch.add(dec_Background);
        dBatch.flush();

        game.sBatch.begin();
        game.sBatch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
