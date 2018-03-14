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

    public static final float SCREEN_WIDTH = Gdx.graphics.getWidth() / 100f;
    public static final float SCREEN_HEIGHT = Gdx.graphics.getHeight() / 100f;

    enum GameState {
        START, RACE, FINISH, END
    }

    GameMain game;
    World world;
    WorldBuilder builder;
    WorldRenderer renderer;
    ShapeRenderer shapeRenderer;
    static DecalBatch dBatch;
    static PerspectiveCamera camera;
    static OrthographicCamera orthoCamera;
    static float fieldOfView = 45f;

    static GameState gameState;
    static float gameStateTime;
    static float global_Speed = -13f;
    static float global_Multiplier = 1f;

    static String string = "3";
    boolean isCountdown;
    float volume = 0.15f;

    float x;
    float red = 240f;
    float green = 130f;

    Sprite icon_hare = new Sprite(Assets.texR_character_hare_head);
    Sprite text_gameStates = new Sprite();
    float text_opacity = 0f;
    int index = 0;

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

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(orthoCamera.combined);

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
        Gdx.gl.glClearColor(126f/255f, 180f/255f, 241f/255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        update(delta);
        renderer.renderWorld();

        updateHUD(delta);
        drawHUD();
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
                string = "GO!";
                if(volume > 0f) volume -= 0.08f * delta;
                if(volume <= 0f) {
                    volume = 0f;
                }
            }
            else if(gameStateTime > 4.2f) string = "1";
            else if(gameStateTime > 3f) string = "2";
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

    public void updateHUD(float delta) {
        Gdx.gl.glEnable(Gdx.gl20.GL_BLEND);
        //game.sBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        if(gameState == RACE || gameState == FINISH) {
            x = 300f * (player.distance / world.end);
            green = 130f + (110f * (player.distance / world.end)); //110 + 160 = 270
            red = 240f - ((240f - 80f) * (player.distance / world.end));

            if(red < 240f) red = 240f;
            if(green > 240f) red = 240f;
        }

        //icon_hare.setPosition(500f + x + 1 - icon_hare.getWidth() / 2f, 642f - icon_hare.getHeight() + 44f);

    }

    public void drawHUD() {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f,0f,0f,0.3f);
        shapeRenderer.arc(500f,662f,12f,90f,180f);
        shapeRenderer.arc(800f,662f,12f,270f,180f);
        shapeRenderer.rect(500f,650f,300f,24f);

        shapeRenderer.setColor(red/240f,green/240f,44f/240f,0.7f);
        shapeRenderer.arc(500f,662f,9f,90f,180f);
        shapeRenderer.rect(500f,653f,x,18f);
        shapeRenderer.arc(500f + x,662f,9f,270f,180f);


        shapeRenderer.setColor(240f,240f,240f,0.45f);
        shapeRenderer.circle(500f + x,662f,9f);
        shapeRenderer.end();


        game.sBatch.begin();
        //Countdown numbers
        if(isCountdown || (gameState == END && gameStateTime < 2f)) {
            //Assets.font.draw(game.sBatch, string,orthoCamera.viewportWidth/2f - Assets.font.getSpaceWidth() * string.length(),orthoCamera.viewportHeight/2f + 150f);

            if(gameState == START) {
                if (gameStateTime < 3.3f) {
                    if(gameStateTime < 2f) text_opacity = 0f;
                    index = 2;

                } else if (gameStateTime < 4.6f) {
                    if(index == 2) text_opacity = 0f;
                    index = 1;

                } else if (gameStateTime < 5.9f) {
                    if(index == 1) text_opacity = 0f;
                    index = 0;

                } else {
                    if(index == 0) text_opacity = 0f;
                    index = 4;
                }
            }
            else if(gameState == END) {
                if(index == 4) text_opacity = 0f;
                index = 3;
            }
            if(gameState == START) {
                text_opacity += Gdx.graphics.getDeltaTime();
                if(text_opacity > 1f) text_opacity = 1f;
            }

            if(gameState == RACE) {
                text_opacity -= Gdx.graphics.getDeltaTime();
                if(text_opacity < 0f) text_opacity = 0f;
                if(text_opacity == 0f) isCountdown = false;
            }

            text_gameStates = Assets.sprites_text_race_states.get(index);
            float width = text_gameStates.getRegionHeight()/1.5f;
            float height = text_gameStates.getRegionWidth()/1.5f;

            if(index == 3) {
                float temp = width;
                width = height;
                height = temp;
            }

            text_gameStates.setOriginCenter();
            text_gameStates.scale(gameStateTime/300f);
            text_gameStates.setColor(1f,1f,1f, text_opacity);
            //text_gameStates.rotate(200f / (gameStateTime*2f)+0.01f);

            text_gameStates.setBounds(0f,0f, width, height);
            text_gameStates.setPosition(SCREEN_WIDTH*100f/2f - width/2f, SCREEN_HEIGHT*100f/2f - height/2f);

            text_gameStates.draw(game.sBatch);
        }

        //icon_hare.draw(game.sBatch);

        game.sBatch.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        dBatch.dispose();
        world.dispose();
    }
}
