package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.math.MathUtils;

import static fi.tamk.tiko.harecraft.GameMain.camera;
import static fi.tamk.tiko.harecraft.GameMain.orthoCamera;
import static fi.tamk.tiko.harecraft.GameMain.sBatch;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.FINISH;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.MyGroupStrategy.shader3D_sea;
import static fi.tamk.tiko.harecraft.Shaders2D.shader2D_default;
import static fi.tamk.tiko.harecraft.Shaders2D.shader2D_vignette;
import static fi.tamk.tiko.harecraft.World.player;
import static fi.tamk.tiko.harecraft.WorldBuilder.spawnDistance;

/**
 * Created by musta on 23.2.2018.
 */

public class GameScreen extends ScreenAdapter {
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
    FPSLogger logger = new FPSLogger();

    static GameState gameState;

    static float fieldOfView = 45f;
    static float gameStateTime;
    static float global_Speed = -13f;
    static float global_Multiplier = 1f;

    static boolean countdown;
    float volume;

    //Shader parameters
    static float tick;
    static float velocity;

    float cameraPanY = 60f;
    float panAccelY = 1f;

    public GameScreen(GameMain game, int index) {
        this.game = game;
        randomizeWorld(index);
        builder = new WorldBuilder(world);
        worldRenderer = new WorldRenderer(world);
        HUD = new HUD(world);

        gameState = GameState.START;
        gameStateTime = 0f;

        Assets.music_course_1.play();
        Assets.music_course_1.setVolume(0f);
        volume = 0.15f;
        Assets.sound_airplane_engine.loop(volume);
        System.out.println("QWDS");
    }

    public void randomizeWorld(int index) {
        switch (index) {
            case 0:
                world = new WorldForest();
                break;
            case 1:
                world = new WorldSea();
                break;
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        worldRenderer.renderWorld();
        HUD.draw();

        if(gameState == END && gameStateTime > 4f) {
            Assets.music_course_1.setVolume(1f-(gameStateTime-4f));
        }
        if(gameState == END && gameStateTime > 5f) {
            //Assets.music_course_1.stop();
            sBatch.setShader(shader2D_default);
            game.setScreen(new MainMenu(game));
        }
        System.out.println(player.velocity.z);
    }

    public void update(float delta) {
        logger.log();

        updateState(delta);
        builder.update(delta);
        updateCameras(delta);
        updateShaders(delta);
        HUD.update(delta);
    }

    public void updateShaders(float delta) {
        tick += delta;
        velocity += player.velocity.z;
        velocity %= 3000f;

        shader3D_sea.begin();
        shader3D_sea.setUniformf("time", tick);
        shader3D_sea.setUniformf("velocity", velocity);
        shader3D_sea.end();

        shader2D_vignette.begin();
        if (GameScreen.gameStateTime > 2f && GameScreen.gameState == START) {
            shader2D_vignette.setUniformf("u_stateTime", (GameScreen.gameStateTime - 2f) / 4f);
            if((gameStateTime - 2f) / 4f > 0.8f) shader2D_vignette.setUniformf("u_stateTime", 0.8f);
        }
        if (GameScreen.gameStateTime > 0.5f && GameScreen.gameState == END) {
            shader2D_vignette.setUniformf("u_stateTime", 0.8f -(GameScreen.gameStateTime - 0.5f) / 3f);
            if(0.8f -(GameScreen.gameStateTime - 0.5f) / 3f < 0f) shader2D_vignette.setUniformf("u_stateTime", 0f);
        }
        shader2D_vignette.end();
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
            world.pfx_speed_lines.start();
        }
        else if(gameState == START) {
            global_Multiplier = 3f;

            if(gameStateTime > 5.4f) {
                if(volume > 0f) volume -= 0.08f * delta;
                if(volume <= 0f) {
                    volume = 0f;
                }
            }
            else if(gameStateTime > 2f && !countdown) {
                Assets.sound_countdown.play(0.25f);
                countdown = true;
            }
            Assets.sound_airplane_engine.setVolume(0,volume);
            Assets.sound_airplane_engine.setPitch(0,1f + volume);
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

        if(gameState == START) {
            panAccelY -= delta / 3f;
            if (panAccelY < 0f) panAccelY = 0f;
            cameraPanY -= (delta * 40f) * panAccelY;
            if (cameraPanY < 0f) cameraPanY = 0f;
        }

        camera.position.set(player.decal.getPosition().x/1.1f, player.decal.getPosition().y/1.1f,-5f);
        camera.lookAt(0f, cameraPanY, spawnDistance/2f);
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

        shader2D_vignette.begin();
        shader2D_vignette.setUniformf("u_resolution", width, height);
        shader2D_vignette.end();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() { world.dispose(); }
}
