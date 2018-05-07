package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;

import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.fieldOfView;
import static fi.tamk.tiko.harecraft.Shaders2D.create2DShaders;
import static fi.tamk.tiko.harecraft.Shaders2D.shader2D_default;

/**
 * Created by Mika and Mikko.
 *
 * Main class. Also, a friendly cow.
 *
 *       ^__^
 *      (oo)\_______
 *      (__)\       )\/\
 *         ||----w |
 *         ||     ||
 */

public class GameMain extends Game {
	static SpriteBatch sBatch;
    static DecalBatch dBatch;

    static PerspectiveCamera camera;
    static OrthographicCamera orthoCamera;

    static ShapeRenderer shapeRenderer;

    static FrameBuffer fbo;
    static FrameBuffer blurTargetA, blurTargetB;
    public static final int FBO_SIZE = 1024;
    static Sprite texture;
    static float musicVolume;
	
	@Override
	public void create () {
		Assets.load();
		AssetsAudio.load();

        orthoCamera = new OrthographicCamera();
        orthoCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera = new PerspectiveCamera(fieldOfView, SCREEN_WIDTH, SCREEN_HEIGHT);
        camera.near = 0.1f;
        camera.far = 400f;
        camera.position.set(0f,0f,-5f);

        sBatch = new SpriteBatch();
        sBatch.setProjectionMatrix(orthoCamera.combined);
        create2DShaders();
        sBatch.setShader(shader2D_default);

        dBatch = new DecalBatch(new MyGroupStrategy(camera));

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(orthoCamera.combined);


        Pixmap.Format format = Pixmap.Format.RGB565;
        if(Gdx.app.getType() == Application.ApplicationType.Desktop) format = Pixmap.Format.RGBA8888;

        if(format == Pixmap.Format.RGBA8888) Gdx.app.log("FORMAT", "RGBA8888");
        else Gdx.app.log("FORMAT", "RGB565");

        fbo = new FrameBuffer(format, (int) SCREEN_WIDTH, (int) SCREEN_HEIGHT, true);
        texture = new Sprite(new Texture((int) SCREEN_WIDTH, (int) SCREEN_HEIGHT, format));
        texture.flip(false, true);

        blurTargetA = new FrameBuffer(format, (int) SCREEN_WIDTH, (int) SCREEN_HEIGHT,false);
        blurTargetB = new FrameBuffer(format, (int) SCREEN_WIDTH, (int) SCREEN_HEIGHT,false);

        //setScreen(new MainMenu(this, false));
        setScreen(new SplashScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		sBatch.dispose();
        dBatch.dispose();
        shapeRenderer.dispose();
        fbo.dispose();
        blurTargetA.dispose();
        blurTargetB.dispose();
        texture.getTexture().dispose();
        Shaders2D.dispose();

		Assets.dispose();
        AssetsAudio.dispose();
        Gdx.app.log("DISPOSED","Assets");
	}
}
