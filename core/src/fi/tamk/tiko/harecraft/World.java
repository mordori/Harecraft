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

    static float finish = 2000;
    static float end = finish + spawnDistance + 20f;

    //BACKGROUND
    Decal decal_background;
    Decal decal_foreground;
    Decal decal_sun1;
    Decal decal_sun2;

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
    ArrayList<Hill> hills_L = new ArrayList<Hill>();
    ArrayList<Hill> hills_R = new ArrayList<Hill>();

    //OPPONENTS
    ArrayList<Opponent> opponents = new ArrayList<Opponent>();

    public World() {
        decal_background = Decal.newDecal(Assets.texR_background, true);
        decal_background.setPosition(0f,12f,300f);

        decal_foreground = Decal.newDecal(Assets.texR_foreground, true);
        decal_foreground.setPosition(0f,15f,287f);

        decal_sun1 = Decal.newDecal(Assets.texR_sun, true);
        decal_sun1.setPosition(0f,-40f,293f);
        decal_sun2 = Decal.newDecal(Assets.texR_sun, true);
        decal_sun2.setPosition(0f,-40f,290f);
        decal_sun2.rotateZ(90f);

        finishLine = new FinishLine(0f,-1.35f,5f);

        player = new Player(0f,-9.5f,0f);

        opponents.add(new Opponent(-3f, -2f, -65f*2f, 105,Assets.texR_opponent_yellow,6.5f));
        opponents.add(new Opponent(4f, -2f, -61f*2f, 130, Assets.texR_opponent_yellow,7.5f));
        opponents.add(new Opponent(0f, 2f, -59f*2f, 175, Assets.texR_opponent_yellow, 4.5f));
        opponents.add(new Opponent(-3f, 1f, -63f*2f, 200, Assets.texR_opponent_yellow,6.5f));
        opponents.add(new Opponent(-4f, -1f, -64f*2f, 250f, Assets.texR_player,5.5f));
        opponents.add(new Opponent(2f, 0f, -60f*2f, 350f, Assets.texR_opponent_yellow,5f));
    }

    public void dispose() {
        player.dispose();

        for(Opponent o : opponents) {
            o.dispose();
        }
    }
}
