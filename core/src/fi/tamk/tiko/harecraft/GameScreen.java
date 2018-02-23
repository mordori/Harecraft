package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by musta on 23.2.2018.
 */

public class GameScreen implements Screen {

    GameMain game;
    Texture tex_backgroundTest = Assets.tex_backgroundTest;

    public GameScreen(GameMain game) {
        this.game = game;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        game.sBatch.begin();
        game.sBatch.draw(tex_backgroundTest,0f,0f);
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
