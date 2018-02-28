package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

/**
 * Created by musta on 23.2.2018.
 */

public class GameScreen extends ScreenAdapter {

    enum State {
        START, RACE, FINISH
    }

    FPSLogger logger = new FPSLogger();

    public static final float WORLD_WIDTH = Gdx.graphics.getWidth() / 100f;
    public static final float WORLD_HEIGHT = Gdx.graphics.getHeight() / 100f;

    GameMain game;
    DecalBatch dBatch;
    static PerspectiveCamera camera;
    static float fieldOfView = 47.5f;
    float cameraRotation = 0f;
    static State state;

    Decal decal_background;

    static Player player;
    ArrayList<Cloud> cloudsLeftUp = new ArrayList<Cloud>();
    ArrayList<Cloud> cloudsLeftDown = new ArrayList<Cloud>();
    ArrayList<Cloud> cloudsRightUp = new ArrayList<Cloud>();
    ArrayList<Cloud> cloudsRightDown = new ArrayList<Cloud>();
    ArrayList<LifeRing> lifeRings = new ArrayList<LifeRing>();
    ArrayList<Tree> trees = new ArrayList<Tree>();
    ArrayList<Opponent> opponents = new ArrayList<Opponent>();

    float cloudsLeftUpSpawnTimer = 1f;
    float cloudsLeftDownSpawnTimer = 1f;
    float cloudsRightUpSpawnTimer = 1f;
    float cloudsRightDownSpawnTimer = 1f;
    float lifeRingsSpawnTimer = 5f;
    float TreeSpawnTimer = 1f;

    static float spawnDistanceSky = 100f;
    static float spawnDistanceGround = 125f;

    static float global_Speed = -13f;
    static float global_Multiplier = 1f;

    static float timer;

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

        opponents.add(new Opponent(-2f, 4f, -45f, 30f));
        opponents.add(new Opponent(2f, 2f, -50f, 65f));
        opponents.add(new Opponent(-3f, -1f, -55f, 100f));

        state = State.START;
    }

    @Override
    public void render(float delta) {
        update(delta);
        drawDecals();
        drawHUD();
    }

    public void update(float delta) {
        //logger.log();
        if(global_Multiplier > 1f) global_Multiplier -= 0.35f * delta;
        else global_Multiplier = 1f;

        if(state == State.START && timer > 5f) {
            state = State.RACE;
            lifeRings.add(new LifeRing(0f, 0f, spawnDistanceSky/1.3f));
            Assets.music_default.play();
            for(Opponent o : opponents) {
                o.position.z = o.spawnPositionZ;
            }
            timer = 0;
        }

        player.update(delta, Gdx.input.getAccelerometerY(), Gdx.input.getAccelerometerZ());
        updateOpponents(delta);
        updateClouds(delta);
        updateLifeRings(delta);
        updateTrees(delta);
        updateCamera();

        timer += delta;
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
        for(LifeRing l : lifeRings) {
            dBatch.add(l.decal);
        }
        for(Tree t : trees) {
            dBatch.add(t.decal);
        }
        for(Opponent o : opponents) {
            if(o.position.z < spawnDistanceSky/3.5f) {
                o.isDrawing = true;
                dBatch.add(o.decal);
            }
            else if(o.opacity != 0f) {
                o.isDrawing = false;
                dBatch.add(o.decal);
            }
        }
        dBatch.add(player.decal);

        dBatch.flush();
    }

    public void drawHUD() {
        game.sBatch.begin();
        game.sBatch.end();
    }

    public void updateCamera() {
        camera.position.set(player.decal.getPosition().x/1.15f, player.decal.getPosition().y/1.15f,-5f);
        //Needs work
        //camera.rotate(player.velocity.x / 20f,1f,1f,1f);
        camera.lookAt(0f,0f,spawnDistanceSky/2f);
        camera.fieldOfView = fieldOfView;
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
        if(cloudsLeftUp.isEmpty() || cloudsLeftUp.get(cloudsLeftUp.size() - 1).stateTime >= cloudsLeftUpSpawnTimer) {
            x = MathUtils.random(-30f,0f);
            y = MathUtils.random(0f,6.2f);
            cloudsLeftUp.add(new Cloud(x, y, spawnDistanceSky));
            cloudsLeftUpSpawnTimer = MathUtils.random(0.1f, 0.7f - global_Multiplier *  0.1f);
        }
        if(cloudsLeftDown.isEmpty() || cloudsLeftDown.get(cloudsLeftDown.size() - 1).stateTime >= cloudsLeftDownSpawnTimer) {
            x = MathUtils.random(-30f,0f);
            y = MathUtils.random(0f,-6.2f);
            cloudsLeftDown.add(new Cloud(x, y, spawnDistanceSky));
            cloudsLeftDownSpawnTimer = MathUtils.random(0.1f, 0.7f - global_Multiplier *  0.1f);
        }
        if(cloudsRightUp.isEmpty() || cloudsRightUp.get(cloudsRightUp.size() - 1).stateTime >= cloudsRightUpSpawnTimer) {
            x = MathUtils.random(0f,30f);
            y = MathUtils.random(0f,6.2f);
            cloudsRightUp.add(new Cloud(x, y, spawnDistanceSky));
            cloudsRightUpSpawnTimer = MathUtils.random(0.1f, 0.7f - global_Multiplier *  0.1f);
        }
        if(cloudsRightDown.isEmpty() || cloudsRightDown.get(cloudsRightDown.size() - 1).stateTime >= cloudsRightDownSpawnTimer) {
            x = MathUtils.random(0f,30f);
            y = MathUtils.random(0f,-6.2f);
            cloudsRightDown.add(new Cloud(x, y, spawnDistanceSky));
            cloudsRightDownSpawnTimer = MathUtils.random(0.1f, 0.7f - global_Multiplier *  0.1f);
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

    public void updateLifeRings(float delta) {
        addLifeRings();
        for(LifeRing l : lifeRings) {
            l.update(delta);
        }
        disposeLifeRings();
    }

    public void addLifeRings() {
        float x;
        float y;
        if(state == State.RACE && (lifeRings.isEmpty() || lifeRings.get(lifeRings.size() - 1).stateTime >= lifeRingsSpawnTimer)) {
            x = MathUtils.random(-10f, 10f);
            y = MathUtils.random(-6.2f, 6.2f);
            lifeRings.add(new LifeRing(x, y, spawnDistanceSky));
        }
    }

    public void disposeLifeRings() {
        if(!lifeRings.isEmpty() && lifeRings.get(0).decal.getPosition().z < camera.position.z) {
            lifeRings.remove(0);
        }
    }

    public void updateTrees(float delta) {
        addTrees();
        for(Tree t : trees) {
            t.update(delta);
        }
        disposeTrees();
    }

    public void addTrees() {
        float x;
        float y;
        if(trees.isEmpty() || trees.get(trees.size() - 1).stateTime >= TreeSpawnTimer) {
            x = MathUtils.random(-100f, 100f);
            y = -17f;
            trees.add(new Tree(x, y, spawnDistanceGround));
            TreeSpawnTimer = MathUtils.random(0.05f, 0.2f - global_Multiplier * 0.025f);
        }
    }

    public void disposeTrees() {
        if(!trees.isEmpty() && trees.get(0).decal.getPosition().z < camera.position.z) {
            trees.remove(0);
        }
    }
    public void updateOpponents(float delta) {
        for(Opponent o : opponents) {
            o.update(delta);
        }
    }

    @Override
    public void dispose() {
        dBatch.dispose();
    }
}
