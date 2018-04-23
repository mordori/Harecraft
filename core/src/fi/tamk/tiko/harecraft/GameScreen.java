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

    static float DIFFICULTYSENSITIVITY; // 0-EASY 2-MEDIUM 4-HARD

    enum GameState {
        START, RACE, FINISH, END
    }

    GameMain game;
    static World world;
    WorldBuilder builder;
    WorldRenderer worldRenderer;
    HUD HUD;
    FPSLogger logger = new FPSLogger();
    static GameState gameState;

    static float fieldOfView = 45f;
    static float gameStateTime;
    static float global_Speed = -13f;
    static float global_Multiplier = 1f;
    float volume;
    float cameraPanY = 60f;
    float panAccelY = 1f;

    static boolean countdown;
    boolean newGame;

    public GameScreen(GameMain game, int index) {
        this.game = game;
        DIFFICULTYSENSITIVITY = ProfileInfo.selectedDifficulty;
        selectWorld(index);
        builder = new WorldBuilder(world);
        worldRenderer = new WorldRenderer(world);
        HUD = new HUD(world);

        gameState = GameState.START;
        gameStateTime = 0f;
        Assets.music_course_1.play();
        Assets.music_course_1.setVolume(0f);
        volume = 0.3f;
        Assets.sound_airplane_engine.loop(volume);
    }

    public void selectWorld(int index) {
        switch (index) {
            case 0:
                world = new WorldForest();
                break;
            case 1:
                world = new WorldTundra();
                break;
            case 2:
                world = new WorldSea();
                break;
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        worldRenderer.renderWorld();
        HUD.draw();

        if(newGame) game.setScreen(new MainMenu(game));
        //if(newGame) game.setScreen(new GameScreen(game, MathUtils.random(0,1)));
        //System.out.println(player.velocity.z);
    }

    public void update(float delta) {
        logger.log();
        updateState(delta);
        builder.update(delta);
        updateCameras(delta);
        HUD.update(delta);
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

            float x = MathUtils.random(-7f, 7f);
            float y = MathUtils.random(-7.2f, 4.2f);
            world.rings.add(new Ring(x, y, spawnDistance/3.25f));
            x = MathUtils.random(-7f, 7f);
            y = MathUtils.random(-7.2f, 4.2f);
            world.rings.add(new Ring(x, y, spawnDistance/1.35f));

            Assets.sound_airplane_engine.stop();
            Assets.music_course_1.setPosition(0f);
            Assets.music_course_1.setVolume(0.7f);
            for(Opponent o : world.opponents) {
                o.position.z = o.spawnZ;
            }

            player.avarageY = player.sumY / player.countY;
            player.ACCEL_Y_OFFSET = player.avarageY - 1.8f;
            System.out.println(player.ACCEL_Y_OFFSET);
        }
        else if(gameState == START) {
            global_Multiplier = 3f;

            if(gameStateTime > 5.4f) {
                if(volume > 0f) volume -= 0.15f * delta;
                if(volume <= 0f) {
                    volume = 0f;
                }
            }
            else if(gameStateTime > 2f && !countdown) {
                Assets.sound_countdown.play(0.45f);
                countdown = true;
            }
            Assets.sound_airplane_engine.setVolume(0,volume);
            //Assets.sound_airplane_engine.setPitch(0, 3f);
        }
        else if(gameState == RACE && player.distance > world.finish) {
            gameState = FINISH;
            gameStateTime = 0f;
        }
        else if(gameState == FINISH && player.distance > world.end) {
            gameState = END;
            gameStateTime = 0f;
            Assets.sound_applause.play(0.4f);
        }
        else if(gameState == END && gameStateTime > 5.5f) {
            //Assets.music_course_1.stop();
            sBatch.setShader(shader2D_default);
            newGame = true;
        }
        else if(gameState == END && gameStateTime > 4f) {
            Assets.music_course_1.setVolume(1f-(gameStateTime-4f));
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
        else if(gameState == END && gameStateTime > 2f) {
            panAccelY += delta / 2f;
            if (panAccelY > 1f) panAccelY = 1f;
            cameraPanY += (delta * 40f) * panAccelY;
            if (cameraPanY > 80f) cameraPanY = 80f;
        }

        /*if(gameState == START || gameState == END) {
            camera.position.set(player.decal.getPosition().x/1.1f, player.decal.getPosition().y/1.1f,-5f);
            camera.lookAt(0f, cameraPanY, spawnDistance/2f);
        }
        else {
            camera.position.set(player.decal.getPosition().x, player.decal.getPosition().y,-5f);
            camera.lookAt(player.decal.getPosition());
        }
        camera.up.set(player.getRotationAverage(), 20f, 0f);
        */

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

        /*
        shader2D_vignette.begin();
        shader2D_vignette.setUniformf("u_resolution", width, height);
        shader2D_vignette.end();
        */
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() { world.dispose(); }
}
