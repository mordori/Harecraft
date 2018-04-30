package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

import static fi.tamk.tiko.harecraft.GameMain.sBatch;
import static fi.tamk.tiko.harecraft.GameMain.shapeRenderer;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.GameScreen.playerScore;

public class ScoreScreen extends ScreenAdapter implements GestureDetector.GestureListener {
    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

    float scoreboard_opacity;
    float yPos = SCREEN_HEIGHT/1.5f;

    public void drawScoreboard() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.3f * scoreboard_opacity);
        shapeRenderer.rect(SCREEN_WIDTH/6f,SCREEN_HEIGHT/6f,2f/3f*SCREEN_WIDTH,2f/3f*SCREEN_HEIGHT);
        shapeRenderer.arc(SCREEN_WIDTH/6f,SCREEN_HEIGHT/6f,10f, 180f, 90f);
        shapeRenderer.arc(SCREEN_WIDTH/6f,SCREEN_HEIGHT/6f + 2f/3f*SCREEN_HEIGHT,10f, 90f, 90f);
        shapeRenderer.arc(SCREEN_WIDTH/6f + 2f/3f*SCREEN_WIDTH,SCREEN_HEIGHT/6f,10f, 270f, 90f);
        shapeRenderer.arc(SCREEN_WIDTH/6f + 2f/3f*SCREEN_WIDTH,SCREEN_HEIGHT/6f + 2f/3f*SCREEN_HEIGHT,10f, 0f, 90f);
        shapeRenderer.rect(SCREEN_WIDTH/6f,SCREEN_HEIGHT/6f - 10f,2f/3f*SCREEN_WIDTH,10f);
        shapeRenderer.rect(SCREEN_WIDTH/6f,SCREEN_HEIGHT/6f + 2f/3f*SCREEN_HEIGHT,2f/3f*SCREEN_WIDTH,10f);
        shapeRenderer.rect(SCREEN_WIDTH/6f - 10f,SCREEN_HEIGHT/6f,10f,2f/3f*SCREEN_HEIGHT);
        shapeRenderer.rect(SCREEN_WIDTH/6f + 2f/3f*SCREEN_WIDTH,SCREEN_HEIGHT/6f,10f,2f/3f*SCREEN_HEIGHT);
        shapeRenderer.end();

        sBatch.begin();
        Assets.font3.setColor(1f,1f,1f, scoreboard_opacity);
        Assets.font3.draw(sBatch,"Score:", 300f, 300f);

        Assets.font1.setColor(1f,1f,1f, scoreboard_opacity);
        Assets.font1.draw(sBatch,"" + playerScore, 650f, 400f);
        sBatch.end();
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw(delta);
    }

    public void update(float delta) {

    }

    public void draw(float delta) {
        Gdx.gl.glEnable(Gdx.gl20.GL_BLEND);
        if(gameState == END) {
            drawScoreboard();
        }
        sBatch.begin();

        sBatch.end();
    }

    public void hide() {

    }

    public void dispose() {

    }
}
