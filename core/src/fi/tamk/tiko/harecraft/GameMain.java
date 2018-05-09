package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.fieldOfView;
import static fi.tamk.tiko.harecraft.GameScreen.stage;


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
    static Sprite texture;
    static float musicVolume;
	
	@Override
	public void create () {
        orthoCamera = new OrthographicCamera();
        orthoCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera = new PerspectiveCamera(fieldOfView, SCREEN_WIDTH, SCREEN_HEIGHT);
        camera.near = 0.1f;
        camera.far = 400f;
        camera.position.set(0f,0f,-5f);

        sBatch = new SpriteBatch();
        sBatch.setProjectionMatrix(orthoCamera.combined);
        sBatch.setShader(null);

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(orthoCamera.combined);

        Pixmap.Format format = Pixmap.Format.RGB565;
        if(Gdx.app.getType() == Application.ApplicationType.Desktop) format = Pixmap.Format.RGBA8888;

        if(format == Pixmap.Format.RGBA8888) Gdx.app.log("FORMAT", "RGBA8888");
        else Gdx.app.log("FORMAT", "RGB565");

        fbo = new FrameBuffer(format, (int) SCREEN_WIDTH, (int) SCREEN_HEIGHT, true);
        texture = new Sprite(new Texture((int) SCREEN_WIDTH, (int) SCREEN_HEIGHT, format));
        texture.flip(false, true);

        setScreen(new SplashScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
        if(sBatch != null) sBatch.dispose();
        if(dBatch != null) dBatch.dispose();
        if(shapeRenderer != null) shapeRenderer.dispose();
        if(fbo != null) fbo.dispose();
        if(stage != null) stage.dispose();

        texture.getTexture().dispose();

		Assets.dispose();
        AssetsAudio.dispose();
        Gdx.app.log("DISPOSED","Assets");
	}
}
