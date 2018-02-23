package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;

import java.util.ArrayList;

/**
 * Created by musta on 23.2.2018.
 */

public class GameScreen extends ScreenAdapter {

    public static final float WORLD_WIDTH = Gdx.graphics.getWidth() / 100f;
    public static final float WORLD_HEIGHT = Gdx.graphics.getHeight() / 100f;

    GameMain game;
    DecalBatch dBatch;
    PerspectiveCamera camera;

    Decal decal_background;

    Player player;
    ArrayList<Cloud> clouds = new ArrayList<Cloud>();

    public GameScreen(GameMain game) {
        this.game = game;

        camera = new PerspectiveCamera(50f, WORLD_WIDTH, WORLD_HEIGHT);
        dBatch = new DecalBatch(new MyGroupStrategy(camera));

        decal_background = Decal.newDecal(Assets.texR_background, true);
        decal_background.setPosition(0f,0f,300f);

        player = new Player(0f,0f,0f);

        clouds.add(new Cloud(1f,-1f,40f));
        clouds.add(new Cloud(-4f,2f,60f));
        clouds.add(new Cloud(-1f,2f,80f));
        clouds.add(new Cloud(3f,1f,110f));

        clouds.add(new Cloud(5f,-6f,70f));
        clouds.add(new Cloud(-5f,-6f,60f));
        clouds.add(new Cloud(2f,-6f,80f));
        clouds.add(new Cloud(3f,-6f,120f));
        clouds.add(new Cloud(-4f,-6f,90f));
        clouds.add(new Cloud(0f,-6f,100f));

        camera.near = 0.1f;
        camera.far = 400f;
        camera.position.set(0f,0f,-5f);
        //camera.lookAt(decal_background.getPosition());
        camera.lookAt(0f,0f,50f);

        //Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        update(delta);
        drawDecals();
        drawHUD();
    }

    public void update(float delta) {
        checkInput(delta);
        camera.position.set(player.decal.getPosition().x,player.decal.getPosition().y,-5f);
        camera.update();
        for(Cloud c : clouds) {
            c.update(delta);
        }
    }

    public void drawDecals() {
        dBatch.add(decal_background);
        for(Cloud c : clouds) {
            dBatch.add(c.decal);
        }
        dBatch.add(player.decal);
        dBatch.flush();
    }

    public void drawHUD() {
        game.sBatch.begin();
        game.sBatch.end();
    }

    public void checkInput(float delta) {
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            //Gdx.app.log("TAG","LEFT");
            player.decal.translateX(2f * delta);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            //Gdx.app.log("TAG","RIGHT");
            player.decal.translateX(-2f * delta);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            //Gdx.app.log("TAG","UP");
            player.decal.translateY(2f * delta);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            //Gdx.app.log("TAG","DOWN");
            player.decal.translateY(-2f * delta);
        }
    }

    @Override
    public void dispose() {
        dBatch.dispose();
    }
}
