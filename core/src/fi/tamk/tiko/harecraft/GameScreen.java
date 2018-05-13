package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;

import static fi.tamk.tiko.harecraft.GameMain.camera;
import static fi.tamk.tiko.harecraft.GameMain.musicVolume;
import static fi.tamk.tiko.harecraft.GameMain.orthoCamera;
import static fi.tamk.tiko.harecraft.GameMain.sBatch;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.EXIT;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.FINISH;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.World.player;
import static fi.tamk.tiko.harecraft.WorldBuilder.spawnDistance;

/**
 * Created by musta on 23.2.2018.
 *
 * Game screen class.
 */

public class GameScreen extends ScreenAdapter implements GestureDetector.GestureListener {
    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        if(gameState != END && gameState != START) {
            if(rectPause.contains(x, y)) {
                if (!paused) {
                    paused = true;
                    Gdx.input.setInputProcessor(stage);
                }
            }
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

    public static float SCREEN_WIDTH = Gdx.graphics.getWidth();
    public static float SCREEN_HEIGHT = Gdx.graphics.getHeight();

    static float DIFFICULTYSENSITIVITY; // 0-EASY 2-MEDIUM 4-HARD

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
    float volume = 1f;
    float cameraPanY = 60f;
    float panAccelY = 1f;

    static boolean countdown;
    static boolean paused;
    static GlyphLayout layout;
    static int worldIndex = -2;

    static String strFlightRecord = "";
    static int renderCount = 0;
    static ArrayList<FileHandle> flights;

    static int worldScore;
    static int playerScore;
    static int playerPlacement;
    static int balloonsCollected;
    static int ringsCollected;

    static int recIndex = 9;

    static final int MAIN_MENU = 0;
    static final int NEW_GAME = 2;
    static int selectedScreen;

    float timer;
    int id_fanfaar = -1;

    static float opacity = 1f;
    static boolean isTransition = false;
    static boolean isTransitionComplete = false;
    int lastPlayerPlacement = 6;
    static float ringCollectTimer = 0f;
    float x;
    float y;

    Rectangle rectPause = new Rectangle(
            SCREEN_WIDTH/1.2f,
            SCREEN_HEIGHT/1.3f,
            Assets.texR_pause.getRegionWidth()/1.5f * SCREEN_WIDTH/1920f * 3f,
            Assets.texR_pause.getRegionWidth()/1.5f * SCREEN_WIDTH/1920f * 3f);


    public GameScreen(GameMain game, int worldIndex) {
        SCREEN_WIDTH = Gdx.graphics.getWidth();
        SCREEN_HEIGHT = Gdx.graphics.getHeight();
        isTransition = false;
        isTransitionComplete = false;
        opacity = 1f;
        countdown = false;
        layout = new GlyphLayout();

        this.game = game;
        this.worldIndex = worldIndex;
        flights = new ArrayList<FileHandle>(Assets.flightsSource);
        DIFFICULTYSENSITIVITY = ProfileInfo.selectedDifficulty;
        selectWorld();
        builder = new WorldBuilder(world);
        worldRenderer = new WorldRenderer(world);
        stage = new Stage(new ScreenViewport(orthoCamera));
        HUD = new HUD(world, this);
        Gdx.input.setInputProcessor(new GestureDetector(this));

        paused = false;
        gameState = GameState.START;
        gameStateTime = 0f;
        strFlightRecord = "";
        worldScore = 0;
        playerScore = 0;
        balloonsCollected = 0;
        ringsCollected = 0;

        AssetsAudio.playSound(AssetsAudio.SOUND_AIRPLANE_ENGINE, 0.28f);
    }

    public void selectWorld() {
        switch (worldIndex) {
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

        if(isTransition) transitionToScreen(delta);
        if(isTransitionComplete) {
            if(selectedScreen == NEW_GAME) {
                timer += delta;
                if(timer > 0.5f) endGame();
            }
            else endGame();
        }
    }

    public void update(float delta) {
        logger.log();
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            if(gameState != END && gameState != START) {
                if (!paused) {
                    paused = true;
                    Gdx.input.setInputProcessor(stage);
                }
            }
        }
        updateState(delta);
        builder.update(delta);
        updateCameras(delta);

        if(gameState == RACE || gameState == FINISH) {
            playerPlacement = 6;
            for (Opponent o : world.opponents) {
                if (player.distance > o.distance) playerPlacement--;
            }

            if (playerPlacement > lastPlayerPlacement)
                AssetsAudio.playSound(AssetsAudio.SOUND_UNDERTAKING, 0.2f);
            else if (playerPlacement < lastPlayerPlacement)
                AssetsAudio.playSound(AssetsAudio.SOUND_OVERTAKING, 0.18f);
            lastPlayerPlacement = playerPlacement;
        }

        HUD.update(delta);
    }

    public void updateState(float delta) {
        gameStateTime += delta;
        if(gameState != START) ringCollectTimer += delta * 0.2;
        if(ringCollectTimer > 1f) ringCollectTimer = 1f;
        if(global_Multiplier > 1f) global_Multiplier -= delta * 1.2f * ringCollectTimer;
        if(global_Multiplier < 2f) global_Multiplier = 2f;

        //System.out.println(player.velocity.z);

        renderCount++;

        if(gameState == START && gameStateTime >= 7) {
            gameState = RACE;
            gameStateTime = 0f;
            player.distance = 0f;
            player.acceleration = 0f;
            world.pfx_speed_lines.start();

            x = MathUtils.random(-7f, 7f);
            y = MathUtils.random(-7.2f, 4.2f);
            Ring ring = world.ringPool.obtain();
            ring.init(x, y, spawnDistance/3.25f);
            world.rings.add(ring);
            x = MathUtils.random(-7f, 7f);
            y = MathUtils.random(-7.2f, 4.2f);

            ring = world.ringPool.obtain();
            ring.init(x, y, spawnDistance/1.35f);
            world.rings.add(ring);

            AssetsAudio.stopSound(AssetsAudio.SOUND_AIRPLANE_ENGINE);
            AssetsAudio.playMusic(worldIndex);
            AssetsAudio.setMusicVolume(musicVolume);

            for(Opponent o : world.opponents) {
                o.position.z = o.spawnZ;
            }

            player.avarageY = player.sumY / player.countY;
            player.ACCEL_Y_OFFSET = player.avarageY - 1.8f;
            System.out.println(player.distance);
        }
        else if(gameState == START) {
            global_Multiplier = 3.5f;

            if(gameStateTime > 2f && !countdown) {
                AssetsAudio.playSound(AssetsAudio.SOUND_COUNTDOWN,0.6f);
                countdown = true;
            }
        }
        else if(gameState == RACE && player.distance > world.finish) {
            gameState = FINISH;
            gameStateTime = 0f;
        }
        else if(gameState == FINISH && player.distance > world.end) {
            gameState = END;
            gameStateTime = 0f;

            playFanfaar();
            calculateScore();

            //RECORD
            //recordFlight();
        }
        else if(gameState == END) {
            timer += delta;
            switch(id_fanfaar) {
                case AssetsAudio.SOUND_FANFAAR_6:
                    if(timer > 5.7f) fadeMusic(delta/5f);
                    break;
                case AssetsAudio.SOUND_FANFAAR_5:
                    if(timer > 2.6f) fadeMusic(delta/5f);
                    break;
                case AssetsAudio.SOUND_FANFAAR_4:
                    if(timer > 1.9f) fadeMusic(delta/5f);
                    break;
                case AssetsAudio.SOUND_FANFAAR_3:
                    if(timer > 1.2f) fadeMusic(delta/5f);
                    break;
                case AssetsAudio.SOUND_FANFAAR_2:
                    if(timer > 1.2f) fadeMusic(delta/5f);
                    break;
                case AssetsAudio.SOUND_FANFAAR_1:
                    if(timer > 2.5f) fadeMusic(delta/5f);
                    break;
            }
        }
    }

    public void calculateScore() {
        System.out.println(playerScore + " / " + worldScore);
        System.out.println((int)((double)playerScore/(double)worldScore * 100) + (balloonsCollected/3) + "%");

        playerScore *= (1f + balloonsCollected * 0.5f);

        int lengthMultiplier = ProfileInfo.selectedDuration/1000;
        if(lengthMultiplier == 2) lengthMultiplier = 3;
        else if(lengthMultiplier == 3) lengthMultiplier = 5;

        switch(playerPlacement) {
            case 1:
                playerScore += 25 * lengthMultiplier;
                break;
            case 2:
                playerScore += 17 * lengthMultiplier;
                break;
            case 3:
                playerScore += 10 * lengthMultiplier;
                break;
            case 4:
                playerScore += 5 * lengthMultiplier;
                break;
            case 5:
                playerScore += lengthMultiplier;
                break;
            case 6:
                playerScore += 0;
                break;
        }

        playerScore *= (ProfileInfo.selectedDifficulty + 1);

        int oldScore = ProfileInfo.profilesData.getInteger(ProfileInfo.selectedPlayerProfile +"Score", 0);
        int newScore = oldScore + playerScore;
        ProfileInfo.profilesData.putInteger(ProfileInfo.selectedPlayerProfile +"Score", newScore);
        ProfileInfo.profilesData.flush();
    }

    public void updateCameras(float delta) {
        fieldOfView -= delta;
        if(fieldOfView < 45f) fieldOfView = 45f;

        if(gameState == EXIT || (gameState == END && gameStateTime > 2f)) {
            panAccelY += delta / 2f;
            if (panAccelY > 1f) panAccelY = 1f;
            cameraPanY += (delta * 40f) * panAccelY;
            if (cameraPanY > 80f) cameraPanY = 80f;
            else if(gameState == EXIT && cameraPanY > 35f) {
                isTransition = true;
            }
        }
        else if(gameState == START) {
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

    private void recordFlight() {
        FileHandle file = Gdx.files.local("data/flights/"+ recIndex + ".txt");
        strFlightRecord += renderCount;
        file.writeString(strFlightRecord, false);

        recIndex++;
    }

    public void fadeMusic(float delta) {
        volume -= delta;
        if(volume <= 0f) volume = 0f;
        AssetsAudio.setMusicVolume(0.25f * volume);
    }

    public void transitionToScreen(float delta) {
        fadeMusic(delta);
        opacity -= delta;
        if(opacity <= 0f) opacity = 0f;
        if(opacity == 0f && volume == 0f) isTransitionComplete = true;
    }

    public void endGame() {
        sBatch.setShader(null);
        AssetsAudio.stopMusic();
        switch(selectedScreen) {
            case MAIN_MENU:
                game.setScreen(new MainMenu(game,true));
                break;
            case NEW_GAME:
                game.setScreen(new GameScreen(game, worldIndex));
                break;
        }
    }

    public void playFanfaar() {
        AssetsAudio.setMusicVolume(0f);
        switch(playerPlacement) {
            case 1:
                AssetsAudio.playSound(AssetsAudio.SOUND_FANFAAR_6,0.45f);
                id_fanfaar = AssetsAudio.SOUND_FANFAAR_6;
                break;
            case 2:
                AssetsAudio.playSound(AssetsAudio.SOUND_FANFAAR_5,0.45f);
                id_fanfaar = AssetsAudio.SOUND_FANFAAR_5;
                break;
            case 3:
                AssetsAudio.playSound(AssetsAudio.SOUND_FANFAAR_4,0.45f);
                id_fanfaar = AssetsAudio.SOUND_FANFAAR_4;
                break;
            case 4:
                AssetsAudio.playSound(AssetsAudio.SOUND_FANFAAR_3,0.45f);
                id_fanfaar = AssetsAudio.SOUND_FANFAAR_3;
                break;
            case 5:
                AssetsAudio.playSound(AssetsAudio.SOUND_FANFAAR_2,0.45f);
                id_fanfaar = AssetsAudio.SOUND_FANFAAR_2;
                break;
            case 6:
                AssetsAudio.playSound(AssetsAudio.SOUND_FANFAAR_1,0.45f);
                id_fanfaar = AssetsAudio.SOUND_FANFAAR_1;
                break;
        }
    }

    @Override
    public void resize (int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        orthoCamera.viewportWidth = width;
        orthoCamera.viewportHeight = height;
    }

    @Override
    public void hide() {
        dispose();
    }
}
