package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;

/**
 * Created by musta on 23.2.2018.
 */

public class GameScreen extends ScreenAdapter {

    public static final float WORLD_WIDTH = Gdx.graphics.getWidth() / 100f;
    public static final float WORLD_HEIGHT = Gdx.graphics.getHeight() / 100f;

    GameMain game;
    DecalBatch dBatch;
    PerspectiveCamera camera;

    Decal decal_background;

    static Player player;
    ArrayList<Cloud> clouds = new ArrayList<Cloud>();

    public GameScreen(GameMain game) {
        this.game = game;

        camera = new PerspectiveCamera(45f, WORLD_WIDTH, WORLD_HEIGHT);
        dBatch = new DecalBatch(new MyGroupStrategy(camera));

        decal_background = Decal.newDecal(Assets.texR_background, true);
        decal_background.setPosition(0f,0f,300f);

        player = new Player(0f,0f,0f);

        clouds.add(new Cloud(1f,-1f,40f));
        clouds.add(new Cloud(-4f,2f,60f));
        clouds.add(new Cloud(-1f,2f,80f));
        clouds.add(new Cloud(3f,1f,10f));

        camera.near = 0.1f;
        camera.far = 400f;
        camera.position.set(0f,0f,-5f);
        camera.lookAt(0f,0f,50f);
    }

    @Override
    public void render(float delta) {
        update(delta);
        drawDecals();
        drawHUD();
    }

    public void update(float delta) {
        addClouds();

        player.update(delta, Gdx.input.getAccelerometerY(), Gdx.input.getAccelerometerZ());

        camera.position.set(player.decal.getPosition().x, player.decal.getPosition().y,-5f);
        camera.lookAt(0f,0f,50f);
        camera.update();

        for(Cloud c : clouds) {
            c.update(delta);
        }

        disposeClouds();
    }

    public void drawDecals() {
        dBatch.add(decal_background);

        for(Cloud c : clouds) {
            dBatch.add(c.decal);
        }

        dBatch.add(player.decal);

        dBatch.flush();
    }

    public void drawHUD() {
        game.sBatch.begin();
        game.sBatch.end();
    }

    public void addClouds() {
        if(clouds.get(clouds.size() - 1).stateTime >= MathUtils.random(0.3f,1.5f)) {
            float x = MathUtils.random(-10f,10f);
            float y = MathUtils.random(-3f,3f);
            clouds.add(new Cloud(x,-y,100f));
        }
    }

    public void disposeClouds() {
        if(clouds.get(0).decal.getPosition().z < -5f) {
            clouds.remove(0);
            Gdx.app.log("TAG", "Cloud deleted");
        }
    }

    @Override
    public void dispose() {
        dBatch.dispose();
    }
}
