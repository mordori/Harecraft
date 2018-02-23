package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;

/**
 * Created by musta on 23.2.2018.
 */

public class GameScreen  extends InputAdapter implements Screen {

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
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        camera.update();

        checkInput();

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

    @Override
    public boolean keyDown (int keycode) {
        if (keycode == Input.Keys.SPACE)
        Gdx.app.log("TAG","input prosessor spae");
        return false;
    }

    public void checkInput() {
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            Gdx.app.log("TAG","Vasen");
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            Gdx.app.log("TAG","Oikea");
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            Gdx.app.log("TAG","Ylös");
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            Gdx.app.log("TAG","Alas");
        }

    }
}
