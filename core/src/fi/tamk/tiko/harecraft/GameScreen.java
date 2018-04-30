package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import static fi.tamk.tiko.harecraft.GameMain.camera;
import static fi.tamk.tiko.harecraft.GameMain.orthoCamera;
import static fi.tamk.tiko.harecraft.GameMain.sBatch;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.EXIT;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.FINISH;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.Shaders2D.shader2D_default;
import static fi.tamk.tiko.harecraft.World.player;
import static fi.tamk.tiko.harecraft.WorldBuilder.spawnDistance;

/**
 * Created by musta on 23.2.2018.
 *
 * Game screen class.
 */

public class GameScreen extends ScreenAdapter implements GestureDetector.GestureListener {
    public static final float SCREEN_WIDTH = Gdx.graphics.getWidth();
    public static final float SCREEN_HEIGHT = Gdx.graphics.getHeight();

    static float DIFFICULTYSENSITIVITY; // 0-EASY 2-MEDIUM 4-HARD

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        if(gameState != END && gameState != EXIT && !paused) {
            paused = true;
            Gdx.input.setInputProcessor(stage);
        }
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

    enum GameState {
        START, RACE, FINISH, END, EXIT
    }

    static GameMain game;
    static World world;
    WorldBuilder builder;
    WorldRenderer worldRenderer;
    HUD HUD;
    FPSLogger logger = new FPSLogger();
    static GameState gameState;
    static Stage stage;

    static float fieldOfView = 45f;
    static float gameStateTime;
    static float global_Speed = -13f;
    static float global_Multiplier = 1f;
    float volume;
    float cameraPanY = 60f;
    float panAccelY = 1f;

    static boolean countdown;
    static boolean newGame;
    static boolean paused;
    static GlyphLayout layout = new GlyphLayout();
    static int index;

    static String strFlightRecord = "";
    static FileHandle file = Gdx.files.local("myfile.txt");
    static FileHandle file2 = Gdx.files.local("myfile2.txt");
    static FileHandle file3 = Gdx.files.local("myfile3.txt");
    static int renderCount = 0;

    static int worldScore;
    static int playerScore;
    static int playerPlacement;
    static int balloonsCollected;

    public GameScreen(GameMain game, int index) {
        this.game = game;
        this.index = index;
        DIFFICULTYSENSITIVITY = ProfileInfo.selectedDifficulty;
        selectWorld(index);
        builder = new WorldBuilder(world);
        worldRenderer = new WorldRenderer(world);
        stage = new Stage(new StretchViewport(1280,800, orthoCamera));
        HUD = new HUD(world, this);
        Gdx.input.setInputProcessor(new GestureDetector(this));

        paused = false;
        newGame = false;
        gameState = GameState.START;
        gameStateTime = 0f;
        Assets.music_course_1.play();
        Assets.music_course_1.setVolume(0f);
        volume = 0.3f;
        Assets.sound_airplane_engine.loop(volume);

        strFlightRecord = "";
        worldScore = 0;
        playerScore = 0;
        balloonsCollected = 0;
    }

    public void selectWorld(int index) {
        switch (index) {
            case 0:
                world = new WorldSea();
                break;
            case 1:
                world = new WorldSummer();
                break;
            case 2:
                world = new WorldTundra();
                break;
        }
    }

    @Override
    public void render(float delta) {
        if(!paused) update(delta);
        worldRenderer.renderWorld();
        HUD.draw();

        if(newGame) game.setScreen(new MainMenu(game,false));
    }

    public void update(float delta) {
        //logger.log();
        updateState(delta);
        builder.update(delta);
        updateCameras(delta);

        playerPlacement = 6;
        for(Opponent o : world.opponents) {
            if(player.distance > o.distance) playerPlacement--;
        }

        HUD.update(delta);
    }
    public void updateState(float delta) {
        gameStateTime += delta;
        if(global_Multiplier > 1f) global_Multiplier -= 0.35f * delta;
        if(global_Multiplier < 1f) global_Multiplier = 1f;

        renderCount++;
        //renderCount %= 10;

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
            System.out.println(player.distance);
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
        }
        else if(gameState == RACE && player.distance > world.finish) {
            gameState = FINISH;
            gameStateTime = 0f;
        }
        else if(gameState == FINISH && player.distance > world.end) {
            gameState = END;
            gameStateTime = 0f;
            Assets.sound_applause.play(0.4f);

            worldScore += 12;
            switch(playerPlacement) {
                case 1:
                    playerScore += 12;
                    break;
                case 2:
                    playerScore += 8;
                    break;
                case 3:
                    playerScore += 6;
                    break;
                case 4:
                    playerScore += 4;
                    break;
                case 5:
                    playerScore += 2;
                    break;
                case 6:
                    playerScore += 0;
                    break;
            }

            System.out.println(playerScore + " / " + worldScore);

            System.out.println((int)((double)playerScore/(double)worldScore * 100) + balloonsCollected/3 + "%");

            if(balloonsCollected == 3) playerScore *= 2;


            //RECORD
            //strFlightRecord += renderCount;
            //file.writeString(strFlightRecord, false);
        }
        else if(gameState == EXIT && gameStateTime > 0.5f) {
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
