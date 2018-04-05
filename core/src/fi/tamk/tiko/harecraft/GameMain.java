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
import static fi.tamk.tiko.harecraft.Shaders2D.create2DShaders;
import static fi.tamk.tiko.harecraft.Shaders2D.shader2D_default;

//      ^__^
//      (oo)\_______
//      (__)\       )\/\
//          ||----w |
//          ||     ||

public class GameMain extends Game {
	static SpriteBatch sBatch;
    static DecalBatch dBatch;

    static PerspectiveCamera camera;
    static OrthographicCamera orthoCamera;

    static ShapeRenderer shapeRenderer;

    static FrameBuffer fbo;
    static Sprite texture;
	
	@Override
	public void create () {
		Assets.load();

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


        if(Gdx.app.getType() == Application.ApplicationType.Desktop) {
            fbo = new FrameBuffer(Pixmap.Format.RGBA8888, (int) SCREEN_WIDTH, (int) SCREEN_HEIGHT, true);
            texture = new Sprite(new Texture((int) SCREEN_WIDTH, (int) SCREEN_HEIGHT, Pixmap.Format.RGBA8888));
            Gdx.app.log("FORMAT", "RGBA8888");
        }
        else if(Gdx.app.getType() == Application.ApplicationType.Android) {
            fbo = new FrameBuffer(Pixmap.Format.RGB565, (int) SCREEN_WIDTH, (int) SCREEN_HEIGHT, true);
            texture = new Sprite(new Texture((int) SCREEN_WIDTH, (int) SCREEN_HEIGHT, Pixmap.Format.RGB565));
            Gdx.app.log("FORMAT", "RGB565");

        }
        texture.flip(false, true);

		setScreen(new MainMenu(this));
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
        texture.getTexture().dispose();
        Shaders2D.dispose();

		Assets.dispose();
        Gdx.app.log("DISPOSED","Assets");
	}
}
