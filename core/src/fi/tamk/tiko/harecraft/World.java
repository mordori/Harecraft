package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;

import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.WorldBuilder.spawnDistance;

/**
 * Created by Mika on 01/03/2018.
 */

public abstract class World {
    public static final float WORLD_WIDTH = SCREEN_WIDTH / 100f;
    public static final float WORLD_HEIGHT_UP = SCREEN_HEIGHT / 100f * 1.5f;
    public static final float WORLD_HEIGHT_DOWN = SCREEN_HEIGHT / 100f * 2f;

    //World length
    static float finish;
    static float end;

    ParticleEffect pfx_speed_lines;

    //Player
    static Player player;

    //Opponents
    ArrayList<Opponent> opponents = new ArrayList<Opponent>();

    //Powerups
    ArrayList<Powerup> powerups = new ArrayList<Powerup>();

    //HotAirBalloons
    ArrayList<HotAirBalloon> hotAirBalloons = new ArrayList<HotAirBalloon>();

    //Ground
    Decal ground;

    //Ground objects
    ArrayList<Tree> trees_L = new ArrayList<Tree>();
    ArrayList<Tree> trees_R = new ArrayList<Tree>();
    ArrayList<Lake> lakes_L = new ArrayList<Lake>();
    ArrayList<Lake> lakes_R = new ArrayList<Lake>();
    ArrayList<Hill> hills_L = new ArrayList<Hill>();
    ArrayList<Hill> hills_R = new ArrayList<Hill>();

    //Sky
    ArrayList<Cloud> clouds_LUp = new ArrayList<Cloud>();
    ArrayList<Cloud> clouds_LDown = new ArrayList<Cloud>();
    ArrayList<Cloud> clouds_RUp = new ArrayList<Cloud>();
    ArrayList<Cloud> clouds_RDown = new ArrayList<Cloud>();
    ArrayList<Ring> rings = new ArrayList<Ring>();

    //Background
    Decal decal_background;
    Decal decal_sun1;
    Decal decal_sun2;

    public World() {
        switch (MathUtils.random(0,2)) {
            case 0:
                finish = 100f;
                break;
            case 1:
                finish = 200f;
                break;
            case 2:
                finish = 300f;
                break;
        }
        end = finish + spawnDistance + 20f;

        pfx_speed_lines = new ParticleEffect(Assets.pfx_speed_lines);

        player = new Player(0f,-7f,-5f);
        opponents.add(new Opponent(-3f, -2f, -65f*2f, 2000, Pilot.COLOR_RED, Pilot.PLANE_2, Pilot.CHARACTER_HARE, 6.5f));
        opponents.add(new Opponent(4f, -2f, -61f*2f, 130, Pilot.COLOR_RED, Pilot.PLANE_2, Pilot.CHARACTER_HARE, 7.5f));
        opponents.add(new Opponent(0f, 4f, -52f*2f, 90, Pilot.COLOR_RED, Pilot.PLANE_2, Pilot.CHARACTER_HARE, 7f));
        opponents.add(new Opponent(4f, 2f, -55f*2f, 500, Pilot.COLOR_RED, Pilot.PLANE_2, Pilot.CHARACTER_HARE, 8.5f));
        opponents.add(new Opponent(3f, -5f, -61f*2f, 1000, Pilot.COLOR_RED, Pilot.PLANE_2, Pilot.CHARACTER_HARE, 5f));

        decal_sun1 = Decal.newDecal(Assets.texR_sun, true);
        decal_sun1.setPosition(0f, -40f, 294f);
        decal_sun2 = Decal.newDecal(Assets.texR_sun, true);
        decal_sun2.setPosition(0f, -40f, 298f);
        decal_sun2.rotateZ(45f);

        hotAirBalloons.add(new HotAirBalloon(-25f, -23f, spawnDistance + 30f));
        hotAirBalloons.add(new HotAirBalloon(25f, -23f, spawnDistance + 30f));
    }

    public void dispose() {
        player.dispose();

        for(Opponent o : opponents) {
            o.dispose();
        }

        pfx_speed_lines.dispose();
    }
}

class WorldForest extends World {


    public WorldForest() {
        float width = Assets.texR_foreground.getRegionWidth()/1.5f;
        float height = Assets.texR_foreground.getRegionHeight()/1.5f;
        decal_background = Decal.newDecal(width, height,Assets.texR_foreground, true);
        decal_background.setPosition(0f, -80f, 275f);

        ground = Decal.newDecal(new TextureRegion(Assets.tex_grass, 0, 0, 600, 330), true);
        ground.setPosition(0f, -28f, 125f);
        ground.rotateX(90f);
    }
}

class WorldSea extends World {


    public WorldSea() {
        ground = Decal.newDecal(new TextureRegion(Assets.tex_sea, 0, 0, 600, 330), true);
        ground.setPosition(0f, -28f, 125f);
        ground.rotateX(90f);
    }
}
