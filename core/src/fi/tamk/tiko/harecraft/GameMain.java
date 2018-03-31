package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

//      ^__^
//      (oo)\_______
//      (__)\       )\/\
//          ||----w |
//          ||     ||

public class GameMain extends Game {
	SpriteBatch sBatch;
	
	@Override
	public void create () {
		Assets.load();
		sBatch = new SpriteBatch();
		setScreen(new MainMenu(this));
	    //setScreen(new GameScreen(this, new World()));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		sBatch.dispose();
		Assets.dispose();
        Gdx.app.log("DISPOSED","Assets");
	}
}
