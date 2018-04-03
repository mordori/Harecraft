package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.MathUtils;

import static fi.tamk.tiko.harecraft.GameMain.camera;
import static fi.tamk.tiko.harecraft.GameMain.orthoCamera;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.FINISH;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.MyGroupStrategy.SHADER_SEA;
import static fi.tamk.tiko.harecraft.MyGroupStrategy.SHADER_VIGNETTE;
import static fi.tamk.tiko.harecraft.MyGroupStrategy.activeShader;
import static fi.tamk.tiko.harecraft.MyGroupStrategy.shader_sea;
import static fi.tamk.tiko.harecraft.MyGroupStrategy.shader_vignette;
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
    WorldRenderer worldRenderer;
    HUD HUD;

    static GameState gameState;

    static float fieldOfView = 45f;
    static float gameStateTime;
    static float global_Speed = -13f;
    static float global_Multiplier = 1f;

    static boolean isCountdown;
    float volume = 0.15f;

    static float tick;
    static float velocity;

    public GameScreen(GameMain game, World world) {
        this.game = game;
        this.world = world;
        builder = new WorldBuilder(world);
        worldRenderer = new WorldRenderer(world, game);
        HUD = new HUD(world, game);

        gameState = GameState.START;
        gameStateTime = 0f;

        Assets.music_course_1.play();
        Assets.music_course_1.setVolume(0f);
        Assets.sound_airplane_engine.loop(volume);
    }

    @Override
    public void render(float delta) {
        update(delta);
        worldRenderer.renderWorld();
        HUD.update(delta);
        HUD.draw();

        if(gameState == END && gameStateTime > 5f) {
            game.setScreen(new MainMenu(game));
        }
    }

    public void update(float delta) {
        logger.log();
        updateState(delta);
        builder.update(delta);
        updateCameras(delta);
        updateShaders(delta);
    }

    public void updateShaders(float delta) {
        tick += delta;
        velocity += player.velocity.z;
        velocity %= 3000f;

        shader_sea.begin();
        shader_sea.setUniformf("time", tick);
        shader_sea.setUniformf("velocity", velocity);
        shader_sea.end();

        shader_vignette.begin();
        shader_vignette.setUniformi("u_texture", 0);
        if (GameScreen.gameStateTime > 2f && GameScreen.gameState == START) {
            shader_vignette.setUniformf("u_stateTime", (GameScreen.gameStateTime - 2f) / 4f);
            if((gameStateTime - 2f) / 4f > 0.8f) shader_vignette.setUniformf("u_stateTime", 0.8f);
        }
        if (GameScreen.gameStateTime > 0.5f && GameScreen.gameState == END) {
            shader_vignette.setUniformf("u_stateTime", 0.8f -(GameScreen.gameStateTime - 0.5f) / 3f);
            if(0.8f -(GameScreen.gameStateTime - 0.5f) / 3f < 0f) shader_vignette.setUniformf("u_stateTime", 0f);
        }
        shader_vignette.end();
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

        camera.position.set(player.decal.getPosition().x/1.15f, player.decal.getPosition().y/1.05f,-5f);
        camera.lookAt(0f,0f, spawnDistance/2f);
        camera.up.set(player.getRotationAverage(), 20f, 0f);
        camera.fieldOfView = fieldOfView;
        camera.update();
        orthoCamera.update();
    }

    @Override
    public void resize (int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        orthoCamera.viewportWidth = width;
        orthoCamera.viewportHeight = height;

        shader_vignette.begin();
        shader_vignette.setUniformf("u_resolution", width, height);
        shader_vignette.end();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        world.dispose();
        HUD.dispose();
    }
}
