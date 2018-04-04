package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;

import java.util.ArrayList;

import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.WorldBuilder.spawnDistance;

/**
 * Created by Mika on 01/03/2018.
 */

public class World {
    public static final float WORLD_WIDTH = SCREEN_WIDTH / 100f;
    public static final float WORLD_HEIGHT_UP = SCREEN_HEIGHT / 100f * 1.5f;
    public static final float WORLD_HEIGHT_DOWN = SCREEN_HEIGHT / 100f * 2f;

    //World length
    static float finish = 1000;
    static float end = finish + spawnDistance + 20f;

    ParticleEffect pfx_speed_lines;

    //Background
    Decal decal_foreground;
    Decal decal_sun1;
    Decal decal_sun2;
    Decal sea;

    //Finishline
    ArrayList<HotAirBalloon> hotAirBalloons = new ArrayList<HotAirBalloon>();

    //Player
    static Player player;

    //Sky
    ArrayList<Cloud> clouds_LUp = new ArrayList<Cloud>();
    ArrayList<Cloud> clouds_LDown = new ArrayList<Cloud>();
    ArrayList<Cloud> clouds_RUp = new ArrayList<Cloud>();
    ArrayList<Cloud> clouds_RDown = new ArrayList<Cloud>();
    ArrayList<Ring> rings = new ArrayList<Ring>();

    //Ground
    ArrayList<Tree> trees_L = new ArrayList<Tree>();
    ArrayList<Tree> trees_R = new ArrayList<Tree>();
    ArrayList<Lake> lakes_L = new ArrayList<Lake>();
    ArrayList<Lake> lakes_R = new ArrayList<Lake>();
    ArrayList<Hill> hills_L = new ArrayList<Hill>();
    ArrayList<Hill> hills_R = new ArrayList<Hill>();

    //Opponents
    ArrayList<Opponent> opponents = new ArrayList<Opponent>();

    //Powerups
    ArrayList<Powerup> powerups = new ArrayList<Powerup>();

    public World(int index) {

    }

    public World() {
        float width = Assets.texR_foreground.getRegionWidth()/1.5f;
        float height = Assets.texR_foreground.getRegionHeight()/1.5f;
        decal_foreground = Decal.newDecal(width, height,Assets.texR_foreground, true);
        decal_foreground.setPosition(0f, -80f, 275f);
        decal_sun1 = Decal.newDecal(Assets.texR_sun, true);
        decal_sun1.setPosition(0f, -40f, 294f);
        decal_sun2 = Decal.newDecal(Assets.texR_sun, true);
        decal_sun2.setPosition(0f, -40f, 298f);
        decal_sun2.rotateZ(45f);

        sea = Decal.newDecal(new TextureRegion(Assets.tex_sea, 0, 0, 600, 330), true);
        sea.setPosition(0f, -28f, 125f);
        sea.rotateX(90f);

        hotAirBalloons.add(new HotAirBalloon(-25f, -23f, spawnDistance + 30f));
        hotAirBalloons.add(new HotAirBalloon(25f, -23f, spawnDistance + 30f));

        player = new Player(0f,-7f,-5f);
        opponents.add(new Opponent(-3f, -2f, -65f*2f, 2000, Pilot.COLOR_RED, Pilot.PLANE_2, Pilot.CHARACTER_HARE, 6.5f));
        opponents.add(new Opponent(4f, -2f, -61f*2f, 130, Pilot.COLOR_RED, Pilot.PLANE_2, Pilot.CHARACTER_HARE, 7.5f));
        opponents.add(new Opponent(0f, 4f, -52f*2f, 90, Pilot.COLOR_RED, Pilot.PLANE_2, Pilot.CHARACTER_HARE, 7f));
        opponents.add(new Opponent(4f, 2f, -55f*2f, 500, Pilot.COLOR_RED, Pilot.PLANE_2, Pilot.CHARACTER_HARE, 8.5f));
        opponents.add(new Opponent(3f, -5f, -61f*2f, 1000, Pilot.COLOR_RED, Pilot.PLANE_2, Pilot.CHARACTER_HARE, 5f));

        pfx_speed_lines = new ParticleEffect(Assets.pfx_speed_lines);
    }

    public void dispose() {
        player.dispose();

        for(Opponent o : opponents) {
            o.dispose();
        }
    }
}

class WorldForest extends World {
    public WorldForest() {

    }
}

class WorldSea extends World {

}
