package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.graphics.g3d.particles.batches.PointSpriteParticleBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
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
    static float fieldOfView = 45f;
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

    float lifeRingsSpawnTimer = 5f;
    float cloudsLeftUpSpawnTimer = 1f;
    float cloudsLeftDownSpawnTimer = 1f;
    float cloudsRightUpSpawnTimer = 1f;
    float cloudsRightDownSpawnTimer = 1f;
    float TreeSpawnTimer = 1f;

    static float spawnDistance = 175;

    static float global_Speed = -13f;
    static float global_Multiplier = 1f;

    static float timer;

    ModelBatch modelBatch = new ModelBatch();
    static AssetManager manager = new AssetManager();

    ParticleSystem particleSystem;
    static BillboardParticleBatch bPdParticleBatch = new BillboardParticleBatch();
    ParticleEffect effect;
    private ParticleEffect currentEffects;
    private Matrix4 targetMatrix;

    public GameScreen(GameMain game) {
        this.game = game;

        camera = new PerspectiveCamera(fieldOfView, WORLD_WIDTH, WORLD_HEIGHT);
        camera.near = 0.1f;
        camera.far = 400f;
        camera.position.set(0f,0f,-5f);

        dBatch = new DecalBatch(new MyGroupStrategy(camera));

        decal_background = Decal.newDecal(Assets.texR_background, true);
        decal_background.setPosition(0f,0f,300f);

        player = new Player(0f,-2f,0f);

        opponents.add(new Opponent(-2f, 4f, -75f, 30f));
        opponents.add(new Opponent(2f, 2f, -80f, 65f));
        opponents.add(new Opponent(-4f, -2f, -85f, 100f));

                /*ParticleSystem particleSystem = new ParticleSystem();
        bPdParticleBatch.setCamera(camera);
        particleSystem.add(bPdParticleBatch);*/
        /*targetMatrix = new Matrix4();


        particleSystem = new ParticleSystem();
        BillboardParticleBatch pointSpriteBatch = new BillboardParticleBatch();
        pointSpriteBatch.setCamera(camera);
        //particleSystem = new ParticleSystem();

        particleSystem.add(pointSpriteBatch);
        ParticleEffectLoader.ParticleEffectLoadParameter loadParam = new ParticleEffectLoader.ParticleEffectLoadParameter(particleSystem.getBatches());
        ParticleEffectLoader loader = new ParticleEffectLoader(new InternalFileHandleResolver());
        manager.setLoader(ParticleEffect.class, loader);
        manager.load("particles/pfx_scarf", ParticleEffect.class, loadParam);
        // halt the main thread until assets are loaded.
        // this is bad for actual games, but okay for demonstration purposes.
        manager.finishLoading();

        currentEffects=manager.get("particles/pfx_scarf",ParticleEffect.class).copy();
        currentEffects.init();
        particleSystem.add(currentEffects);*/

        state = State.START;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1.f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        update(delta);
        drawDecals();
        //renderParticleEffects(delta);
        drawHUD();
    }

    private void renderParticleEffects(float delta) {


        targetMatrix.idt();
        targetMatrix.translate(player.position.x,player.position.y,player.position.z);
        currentEffects.setTransform(targetMatrix);

        modelBatch.begin(camera);
        particleSystem.update(); // technically not necessary for rendering
        particleSystem.begin();
        particleSystem.draw();
        particleSystem.end();
        modelBatch.render(particleSystem);
        modelBatch.end();
    }

    public void update(float delta) {
        //logger.log();
        if(global_Multiplier > 1f) global_Multiplier -= 0.35f * delta;
        else global_Multiplier = 1f;

        if(state == State.START && timer > 6f) {
            state = State.RACE;
            lifeRings.add(new LifeRing(0f, 0f, spawnDistance/2.8f));
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
            dBatch.add(t.decal_shadow);
            dBatch.add(t.decal);
        }
        for(Opponent o : opponents) {
            if(o.position.z < spawnDistance/6f) {
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
        if(player.velocity.x != 0f || player.velocity.y != 0f)player.pfx_scarf.draw(game.sBatch);
        //player.pfx_windRight.draw(game.sBatch);
        game.sBatch.end();
    }

    public void updateCamera() {
        camera.position.set(player.decal.getPosition().x/1.15f, player.decal.getPosition().y/1.15f,-5f);
        //Needs work
        //camera.rotate(player.velocity.x / 20f,1f,1f,1f);
        camera.lookAt(0f,0f,spawnDistance/2f);
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
            x = MathUtils.random(-40f,0f);
            y = MathUtils.random(0f,6.2f);
            cloudsLeftUp.add(new Cloud(x, y, spawnDistance));
            cloudsLeftUpSpawnTimer = MathUtils.random(0.4f, 1f - global_Multiplier *  0.1f);
        }
        if(cloudsLeftDown.isEmpty() || cloudsLeftDown.get(cloudsLeftDown.size() - 1).stateTime >= cloudsLeftDownSpawnTimer) {
            x = MathUtils.random(-40f,0f);
            y = MathUtils.random(0f,-6.2f);
            cloudsLeftDown.add(new Cloud(x, y, spawnDistance));
            cloudsLeftDownSpawnTimer = MathUtils.random(0.4f, 1f - global_Multiplier *  0.1f);
        }
        if(cloudsRightUp.isEmpty() || cloudsRightUp.get(cloudsRightUp.size() - 1).stateTime >= cloudsRightUpSpawnTimer) {
            x = MathUtils.random(0f,40f);
            y = MathUtils.random(0f,6.2f);
            cloudsRightUp.add(new Cloud(x, y, spawnDistance));
            cloudsRightUpSpawnTimer = MathUtils.random(0.4f, 1f - global_Multiplier *  0.1f);
        }
        if(cloudsRightDown.isEmpty() || cloudsRightDown.get(cloudsRightDown.size() - 1).stateTime >= cloudsRightDownSpawnTimer) {
            x = MathUtils.random(0f,40f);
            y = MathUtils.random(0f,-6.2f);
            cloudsRightDown.add(new Cloud(x, y, spawnDistance));
            cloudsRightDownSpawnTimer = MathUtils.random(0.4f, 1f - global_Multiplier *  0.1f);
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
            lifeRings.add(new LifeRing(x, y, spawnDistance - 50f));
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
            y = -23f;
            trees.add(new Tree(x, y, spawnDistance));
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
