package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;

import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.FINISH;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.World.player;
import static fi.tamk.tiko.harecraft.WorldBuilder.spawnDistance;

/**
 * Created by musta on 23.2.2018.
 */

public class GameScreen extends ScreenAdapter {
    FPSLogger logger = new FPSLogger();

    public static final float SCREEN_WIDTH = Gdx.graphics.getWidth() / 100f;
    public static final float SCREEN_HEIGHT = Gdx.graphics.getHeight() / 100f;

    enum GameState {
        START, RACE, FINISH, END
    }

    GameMain game;
    World world;
    WorldBuilder builder;
    WorldRenderer renderer;
    static DecalBatch dBatch;
    static PerspectiveCamera camera;
    static float fieldOfView = 45f;
    static float cameraRotation = 0f;

    static GameState gameState;
    static float gameStateTime;
    static float global_Speed = -13f;
    static float global_Multiplier = 1f;

    public GameScreen(GameMain game, World world) {
        this.game = game;
        this.world = world;
        builder = new WorldBuilder(world);
        renderer = new WorldRenderer(world, game);

        camera = new PerspectiveCamera(fieldOfView, SCREEN_WIDTH, SCREEN_HEIGHT);
        camera.near = 0.1f;
        camera.far = 400f;
        camera.position.set(0f,0f,-5f);

        dBatch = new DecalBatch(new MyGroupStrategy(camera));

        gameState = GameState.START;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1.f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        update(delta);
        renderer.renderWorld();
    }

    public void update(float delta) {
        logger.log();

        gameStateTime += delta;
        if(global_Multiplier > 1f) global_Multiplier -= 0.35f * delta;
        else global_Multiplier = 1f;

        if(gameState == START) {
            global_Multiplier = 3f;
        }

        if(gameState == START && gameStateTime >= 6f) {
            gameState = RACE;
            gameStateTime = 0f;
            player.distance = 0f;
        }
        else if(gameState == RACE && player.distance > world.finish) {
            gameState = FINISH;
            gameStateTime = 0f;
        }
        else if(gameState == FINISH && player.distance > world.end) {
            gameState = END;
            gameStateTime = 0f;
        }

        builder.update(delta);
        updateCamera();
    }

    public void updateCamera() {
        camera.position.set(player.decal.getPosition().x/1.15f, player.decal.getPosition().y/1.05f,-5f);
        //Needs work
        //camera.rotate(player.velocity.x / 20f,1f,1f,1f);
        camera.lookAt(0f,0f, spawnDistance/2f);
        camera.up.set(0f, 100f, 0f);
        camera.fieldOfView = fieldOfView;
        camera.update();
    }

    @Override
    public void dispose() {
        dBatch.dispose();
        world.dispose();
    }
}
