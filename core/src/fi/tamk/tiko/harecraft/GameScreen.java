package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;

/**
 * Created by musta on 23.2.2018.
 */

public class GameScreen extends ScreenAdapter {
    FPSLogger logger = new FPSLogger();

    public static final float WORLD_WIDTH = Gdx.graphics.getWidth() / 100f;
    public static final float WORLD_HEIGHT = Gdx.graphics.getHeight() / 100f;

    GameMain game;
    DecalBatch dBatch;
    static PerspectiveCamera camera;
    float fieldOfView = 45f;

    Decal decal_background;

    static Player player;
    ArrayList<Cloud> cloudsLeftUp = new ArrayList<Cloud>();
    ArrayList<Cloud> cloudsLeftDown = new ArrayList<Cloud>();
    ArrayList<Cloud> cloudsRightUp = new ArrayList<Cloud>();
    ArrayList<Cloud> cloudsRightDown = new ArrayList<Cloud>();


    float cloudsLeftUpSpawnTimer = 1f;
    float cloudsLeftDownSpawnTimer = 1f;
    float cloudsRightUpSpawnTimer = 1f;
    float cloudsRightDownSpawnTimer = 1f;

    float spawnDistance = 100f;

    public GameScreen(GameMain game) {
        this.game = game;

        camera = new PerspectiveCamera(fieldOfView, WORLD_WIDTH, WORLD_HEIGHT);
        camera.near = 0.1f;
        camera.far = 400f;
        camera.position.set(0f,0f,-5f);

        dBatch = new DecalBatch(new MyGroupStrategy(camera));

        decal_background = Decal.newDecal(Assets.texR_background, true);
        decal_background.setPosition(0f,0f,300f);

        player = new Player(0f,0f,0f);

        cloudsLeftUp.add(new Cloud(-5f,2f, spawnDistance));
        cloudsLeftDown.add(new Cloud(-10f,-1f, spawnDistance));
        cloudsRightUp.add(new Cloud(10f,1f, spawnDistance));
        cloudsRightDown.add(new Cloud(5f,-2f, spawnDistance));
    }

    @Override
    public void render(float delta) {
        update(delta);
        drawDecals();
        drawHUD();
    }

    public void update(float delta) {
        logger.log();
        player.update(delta, Gdx.input.getAccelerometerY(), Gdx.input.getAccelerometerZ());
        updateCamera();
        updateClouds(delta);
    }

    public void drawDecals() {
        dBatch.add(decal_background);
        for(Cloud c : cloudsLeftUp) {
            dBatch.add(c.decal);
        }
        for(Cloud c : cloudsLeftDown) {
            dBatch.add(c.decal);
        }
        for(Cloud c : cloudsRightUp) {
            dBatch.add(c.decal);
        }
        for(Cloud c : cloudsRightDown) {
            dBatch.add(c.decal);
        }
        dBatch.add(player.decal);

        dBatch.flush();
        //System.out.println(clouds.size() + cloudsLeft.size() + cloudsRight.size());
    }

    public void drawHUD() {
        game.sBatch.begin();
        game.sBatch.end();
    }

    public void updateCamera() {
        camera.position.set(player.decal.getPosition().x/1.15f, player.decal.getPosition().y/1.15f,-5f);
        camera.rotate(player.velocity.x / 15f,1f,1f,1f);
        //camera.rota
        camera.lookAt(0f,0f,spawnDistance/2f);
        camera.update();
    }

    public void updateClouds(float delta) {
        addClouds();
        for(Cloud c : cloudsLeftUp) {
            c.update(delta);
        }
        for(Cloud c : cloudsLeftDown) {
            c.update(delta);
        }
        for(Cloud c : cloudsRightUp) {
            c.update(delta);
        }
        for(Cloud c : cloudsRightDown) {
            c.update(delta);
        }
        disposeClouds();
    }

    public void addClouds() {
        float x;
        float y;

        if(cloudsLeftUp.get(cloudsLeftUp.size() - 1).stateTime >= cloudsLeftUpSpawnTimer) {
            x = MathUtils.random(-30f,0f);
            y = MathUtils.random(0f,6.2f);
            cloudsLeftUp.add(new Cloud(x, y, spawnDistance));
            cloudsLeftUpSpawnTimer = MathUtils.random(0.1f,0.25f);
        }
        if(cloudsLeftDown.get(cloudsLeftDown.size() - 1).stateTime >= cloudsLeftDownSpawnTimer) {
            x = MathUtils.random(-30f,0f);
            y = MathUtils.random(0f,-6.2f);
            cloudsLeftDown.add(new Cloud(x, y, spawnDistance));
            cloudsLeftDownSpawnTimer = MathUtils.random(0.1f,0.25f);
        }
        if(cloudsRightUp.get(cloudsRightUp.size() - 1).stateTime >= cloudsRightUpSpawnTimer) {
            x = MathUtils.random(0f,30f);
            y = MathUtils.random(0f,6.2f);
            cloudsRightUp.add(new Cloud(x, y, spawnDistance));
            cloudsRightUpSpawnTimer = MathUtils.random(0.1f,0.25f);
        }
        if(cloudsRightDown.get(cloudsRightDown.size() - 1).stateTime >= cloudsRightDownSpawnTimer) {
            x = MathUtils.random(0f,30f);
            y = MathUtils.random(0f,-6.2f);
            cloudsRightDown.add(new Cloud(x, y, spawnDistance));
            cloudsRightDownSpawnTimer = MathUtils.random(0.1f,0.25f);
        }
    }

    public void disposeClouds() {
        disposeCloud(cloudsLeftUp);
        disposeCloud(cloudsLeftDown);
        disposeCloud(cloudsRightUp);
        disposeCloud(cloudsRightDown);
    }

    public void disposeCloud(ArrayList<Cloud> cloudArray) {
        if(cloudArray.get(0).decal.getPosition().z < camera.position.z) {
            cloudArray.remove(0);
        }
    }

    @Override
    public void dispose() {
        dBatch.dispose();
    }
}
