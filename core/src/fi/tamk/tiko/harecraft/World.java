package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.decals.Decal;

import java.util.ArrayList;

/**
 * Created by Mika on 01/03/2018.
 */

public class World {
    public static final float WORLD_WIDTH = Gdx.graphics.getWidth() / 100f;
    public static final float WORLD_HEIGHT = Gdx.graphics.getHeight() / 100f;

    Decal decal_background;

    static Player player;

    ArrayList<Cloud> clouds_LUp = new ArrayList<Cloud>();
    ArrayList<Cloud> clouds_LDown = new ArrayList<Cloud>();
    ArrayList<Cloud> clouds_RUp = new ArrayList<Cloud>();
    ArrayList<Cloud> clouds_RDown = new ArrayList<Cloud>();
    ArrayList<Ring> rings = new ArrayList<Ring>();
    ArrayList<Tree> trees = new ArrayList<Tree>();
    ArrayList<Opponent> opponents = new ArrayList<Opponent>();

    public World() {
        decal_background = Decal.newDecal(Assets.texR_background, true);
        decal_background.setPosition(0f,12f,300f);

        player = new Player(0f,-9f,0f);

        opponents.add(new Opponent(-2f, 4f, -75f, 30f));
        opponents.add(new Opponent(2f, 2f, -80f, 65f));
        opponents.add(new Opponent(-4f, -2f, -85f, 100f));
    }

    public void dispose() {
        player.dispose();

        for(Opponent o : opponents) {
            o.dispose();
        }
    }
}
