package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g3d.decals.Decal;

import java.util.ArrayList;

import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.WorldBuilder.spawnDistance;

/**
 * Created by Mika on 01/03/2018.
 */

public class World {
    public static final float WORLD_WIDTH = SCREEN_WIDTH;
    public static final float WORLD_HEIGHT_UP = SCREEN_HEIGHT * 1.5f;
    public static final float WORLD_HEIGHT_DOWN = SCREEN_HEIGHT * 2f;

    float finish = 300;
    float end = finish + spawnDistance + 50f;

    //BACKGROUND
    Decal decal_background;

    //FINISHLINE
    FinishLine finishLine;

    //PLAYER
    static Player player;

    //SKY
    ArrayList<Cloud> clouds_LUp = new ArrayList<Cloud>();
    ArrayList<Cloud> clouds_LDown = new ArrayList<Cloud>();
    ArrayList<Cloud> clouds_RUp = new ArrayList<Cloud>();
    ArrayList<Cloud> clouds_RDown = new ArrayList<Cloud>();
    ArrayList<Ring> rings = new ArrayList<Ring>();

    //GROUND
    ArrayList<Tree> trees_L = new ArrayList<Tree>();
    ArrayList<Tree> trees_R = new ArrayList<Tree>();
    ArrayList<Lake> lakes_L = new ArrayList<Lake>();
    ArrayList<Lake> lakes_R = new ArrayList<Lake>();

    //OPPONENTS
    ArrayList<Opponent> opponents = new ArrayList<Opponent>();

    public World() {
        decal_background = Decal.newDecal(Assets.texR_background, true);
        decal_background.setPosition(0f,12f,300f);

        player = new Player(0f,-4f,0f);

        opponents.add(new Opponent(-3f, 2f, -85f, 50f));
        opponents.add(new Opponent(4f, 1f, -75f, 85f));
        opponents.add(new Opponent(-0f, -1f, -70f, 150f));
    }

    public void dispose() {
        player.dispose();

        for(Opponent o : opponents) {
            o.dispose();
        }
    }
}
