package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

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

    public static final float SCREEN_WIDTH = Gdx.graphics.getWidth();
    public static final float SCREEN_HEIGHT = Gdx.graphics.getHeight();

    enum GameState {
        START, RACE, FINISH, END
    }

    GameMain game;
    World world;
    WorldBuilder builder;
    WorldRenderer renderer;
    HUD HUD;
    static DecalBatch dBatch;
    static PerspectiveCamera camera;
    static OrthographicCamera orthoCamera;
    static float fieldOfView = 45f;

    static GameState gameState;
    static float gameStateTime;
    static float global_Speed = -13f;
    static float global_Multiplier = 1f;

    static boolean isCountdown;
    float volume = 0.15f;


    public GameScreen(GameMain game, World world) {
        this.game = game;
        this.world = world;
        builder = new WorldBuilder(world);
        renderer = new WorldRenderer(world, game);

        orthoCamera = new OrthographicCamera();
        orthoCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera = new PerspectiveCamera(fieldOfView, SCREEN_WIDTH, SCREEN_HEIGHT);
        camera.near = 0.1f;
        camera.far = 400f;
        camera.position.set(0f,0f,-5f);

        HUD = new HUD(world, game);

        dBatch = new DecalBatch(new MyGroupStrategy(camera));
        game.sBatch.setProjectionMatrix(orthoCamera.combined);

        gameState = GameState.START;

        //Compressed audio files causes a slight delay when set to play, so better do it while the game is still loading
        //and reset the position and volume when it is actually supposed to play.
        Assets.music_course_1.play();
        Assets.music_course_1.setVolume(0f);
        Assets.sound_airplane_engine.loop(volume);
    }

    @Override
    public void render(float delta) {
        //Gdx.gl.glClearColor(126f/255f, 180f/255f, 41f/255f, 1f);
        Gdx.gl.glClearColor(42/255f, 116/255f, 154/255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        update(delta);
        renderer.renderWorld();

        HUD.update(delta);
        HUD.draw();
    }

    public void update(float delta) {
        logger.log();
        updateState(delta);
        builder.update(delta);
        updateCameras(delta);
    }

    public void updateState(float delta) {
        gameStateTime += delta;
        if(global_Multiplier > 1f) global_Multiplier -= 0.35f * delta;
        if(global_Multiplier < 1f) global_Multiplier = 1f;

        if(gameState == START && gameStateTime >= 7) {
            gameState = RACE;
            gameStateTime = 0f;
            player.distance = 0f;
            player.acceleration = 0f;
        }
        else if(gameState == START) {
            global_Multiplier = 3f;

            if(gameStateTime > 5.4f) {
                if(volume > 0f) volume -= 0.08f * delta;
                if(volume <= 0f) {
                    volume = 0f;
                }
            }
            else if(gameStateTime > 2f && !isCountdown) {
                Assets.sound_countdown.play(0.25f);
                isCountdown = true;
            }
            Assets.sound_airplane_engine.setVolume(0,volume);
        }
        else if(gameState == RACE && player.distance > world.finish) {
            gameState = FINISH;
            gameStateTime = 0f;
        }
        else if(gameState == FINISH && player.distance > world.end) {
            gameState = END;
            gameStateTime = 0f;
        }

        if(gameState == RACE && gameStateTime == 0f) {
            float x = MathUtils.random(-10f, 10f);
            float y = MathUtils.random(-9.2f, 6.2f);
            world.rings.add(new Ring(x, y, spawnDistance/3.25f));
            x = MathUtils.random(-10f, 10f);
            y = MathUtils.random(-9.2f, 6.2f);
            world.rings.add(new Ring(x, y, spawnDistance/1.35f));

            Assets.sound_airplane_engine.stop();
            Assets.music_course_1.setPosition(0f);
            Assets.music_course_1.setVolume(1f);
            for(Opponent o : world.opponents) {
                o.position.z = o.spawnZ;
            }
        }
    }

    public void updateCameras(float delta) {
        fieldOfView -= delta;
        if(fieldOfView < 45f) fieldOfView = 45f;
        //System.out.println(fieldOfView);

        camera.position.set(player.decal.getPosition().x/1.15f, player.decal.getPosition().y/1.05f,-5f);
        camera.lookAt(0f,0f, spawnDistance/2f);
        camera.up.set(player.getRotationAverage(), 20f, 0f); //camera.up.set(player.getRotationAverage(), 20f, 0f);
        camera.fieldOfView = fieldOfView;
        camera.update();
        orthoCamera.update();
    }

    @Override
    public void dispose() {
        HUD.dispose();
        dBatch.dispose();
        world.dispose();
    }

    @Override
    public void resize (int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight= height;

        MyGroupStrategy.myShader.begin();
        MyGroupStrategy.myShader.setUniformf("u_resolution", width, height);
        MyGroupStrategy.myShader.end();
    }
}
