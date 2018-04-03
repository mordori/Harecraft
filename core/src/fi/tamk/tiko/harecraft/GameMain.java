package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;

import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.fieldOfView;

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

        dBatch = new DecalBatch(new MyGroupStrategy(camera));

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
		Assets.dispose();
        Gdx.app.log("DISPOSED","Assets");
	}
}
