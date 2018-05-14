package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

import java.util.ArrayList;

import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.EXIT;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.GameScreen.gameStateTime;
import static fi.tamk.tiko.harecraft.GameScreen.isTransition;
import static fi.tamk.tiko.harecraft.GameScreen.world;
import static fi.tamk.tiko.harecraft.MyGroupStrategy.shader3D_sea;
import static fi.tamk.tiko.harecraft.WorldBuilder.groundLevel;
import static fi.tamk.tiko.harecraft.WorldBuilder.spawnDistance;

/**
 * Created by Mika on 01/03/2018.
 *
 * An abstract parent class for all kinds of Worlds.
 */

public abstract class World {
    //World length
    static float finish;
    static float end;

    ParticleEffect pfx_speed_lines;
    ParticleEffect pfx_snow = Assets.pfx_snow;
    ParticleEffect pfx_cloud = Assets.pfx_cloud_dispersion;
    ParticleEffect pfx_speedUp = Assets.pfx_speed_up;

    //Player
    static Player player;

    //Opponents
    ArrayList<Opponent> opponents = new ArrayList<Opponent>();

    //Powerups
    ArrayList<Balloon> balloons = new ArrayList<Balloon>();

    //HotAirBalloons
    ArrayList<HotAirBalloon> hotAirBalloons = new ArrayList<HotAirBalloon>();
    ArrayList<LightHouse> lightHouses = new ArrayList<LightHouse>();

    //Ground
    Decal ground;

    //Ground objects
    ArrayList<Tree> trees_L = new ArrayList<Tree>();
    ArrayList<Tree> trees_R = new ArrayList<Tree>();
    ArrayList<Lake> lakes_L = new ArrayList<Lake>();
    ArrayList<Lake> lakes_R = new ArrayList<Lake>();
    ArrayList<Hill> hills_L = new ArrayList<Hill>();
    ArrayList<Hill> hills_R = new ArrayList<Hill>();

    //Sea Objects
    ArrayList<Boat> boats = new ArrayList<Boat>();
    ArrayList<Island> islands_L = new ArrayList<Island>();
    ArrayList<Island> islands_R = new ArrayList<Island>();

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

    float opacity;

    ParticleEffectPool pfxPool_cloudDispersion;
    ParticleEffectPool pfxPool_playerSpeedUp;

    final Pool<Cloud> cloudPool = new Pool<Cloud>() {
        @Override
        protected Cloud newObject() {
            return new Cloud();
        }
    };
    final Pool<Ring> ringPool = new Pool<Ring>() {
        @Override
        protected Ring newObject() {
            return new Ring();
        }
    };
    final Pool<Hill> hillPool = new Pool<Hill>() {
        @Override
        protected Hill newObject() {
            return new Hill();
        }
    };
    final Pool<Tree> treePool = new Pool<Tree>() {
        @Override
        protected Tree newObject() {
            return new Tree();
        }
    };
    final Pool<Lake> lakePool = new Pool<Lake>(15) {
        @Override
        protected Lake newObject() {
            return new Lake();
        }
    };
    final Pool<Island> islandPool = new Pool<Island>(15) {
        @Override
        protected Island newObject() {
            return new Island();
        }
    };
    final Pool<Boat> boatPool = new Pool<Boat>() {
        @Override
        protected Boat newObject() {
            return new Boat();
        }
    };

    public World() {
        finish = ProfileInfo.selectedDuration;
        end = finish + spawnDistance + 20f;

        if(SCREEN_WIDTH >= 1600) pfx_speed_lines = Assets.pfx_speed_lines_2;
        else pfx_speed_lines = Assets.pfx_speed_lines;

        pfx_snow.getEmitters().first().setPosition(SCREEN_WIDTH/2f, SCREEN_HEIGHT/2.2f);
        pfx_snow.getEmitters().get(1).setPosition(SCREEN_WIDTH/2f, SCREEN_HEIGHT/2.2f);

        player = new Player(0f,-7f,-5f);
        opponents.add(new Opponent(-65f*2f, Pilot.WOLF));
        opponents.add(new Opponent(-52f*2f, Pilot.GIRAFF));
        opponents.add(new Opponent(-61f*2f, Pilot.FOX));
        opponents.add(new Opponent(-55f*2f, Pilot.KOALA));
        opponents.add(new Opponent(-61f*2f, Pilot.BEAR));

        decal_sun1 = Decal.newDecal(Assets.texR_sun, true);
        decal_sun1.setPosition(0f, -40f, 294f);
        decal_sun2 = Decal.newDecal(Assets.texR_sun, true);
        decal_sun2.setPosition(0f, -40f, 298f);
        decal_sun2.rotateZ(45f);

        pfxPool_cloudDispersion = new ParticleEffectPool(pfx_cloud,2,3);
        pfxPool_playerSpeedUp = new ParticleEffectPool(pfx_speedUp,2,2);

        pfx_snow.allowCompletion();
    }

    public abstract void updateShaders(float delta);
    public void update(float delta) {
        if(gameState == START) opacity = gameStateTime < 1f ? gameStateTime : 1f;
        else if(gameState == END && gameStateTime > 4f) opacity = 5f - gameStateTime > 0f ? 5f - gameStateTime : 0f;

        decal_sun1.rotateZ(delta/2f);
        decal_sun1.setColor(1f,1f,1f, opacity);

        decal_sun2.rotateZ(-delta);
        decal_sun2.setColor(1f,1f,1f, opacity);

        if(gameState == EXIT) {
            if(isTransition) opacity -= delta;
            if(opacity < 0f) opacity = 0f;
            float opacity2 = (1f - gameStateTime/2f) > 0f ? (1f - gameStateTime/2f) : 0f;
            decal_sun1.setColor(1f,1f,1f, opacity2);
            decal_sun2.setColor(1f,1f,1f, opacity2);
        }
    }
}

/**
 * Created by Mika on 01/03/2018.
 *
 * World of forests.
 */

class WorldSummer extends World {
    public WorldSummer() {
        float width = Assets.texR_background_summer.getRegionWidth()/2f;
        float height = Assets.texR_background_summer.getRegionHeight()/2f;
        decal_background = Decal.newDecal(width, height,Assets.texR_background_summer, true);
        decal_background.setPosition(0f, 2f, 275f);

        ground = Decal.newDecal(new TextureRegion(Assets.tex_grass, 0, 0, 600, 330), true);
        ground.setPosition(0f, -45f, 125f);
        ground.rotateX(90f);

        hotAirBalloons.add(new HotAirBalloon(-26f, -23f, spawnDistance + 33f));
        hotAirBalloons.add(new HotAirBalloon(26f, -23f, spawnDistance + 33f));
    }

    public void update(float delta) {
        super.update(delta);
        decal_background.setColor(1f,1f,1f, opacity);

        if(pfx_snow != null) pfx_snow.getEmitters().get(0).setPosition(SCREEN_WIDTH/2f, SCREEN_HEIGHT/2.2f);
        if(pfx_snow != null) pfx_snow.getEmitters().get(1).setPosition(SCREEN_WIDTH/2f, SCREEN_HEIGHT/2.2f);
    }

    public void updateShaders(float delta) {

    }

}

/**
 * Created by Mika on 01/03/2018.
 *
 * World of tundra.
 */

class WorldTundra extends World {
    public WorldTundra() {
        float width = Assets.texR_background_tundra.getRegionWidth()/2.25f;
        float height = Assets.texR_background_tundra.getRegionHeight()/2.25f;
        decal_background = Decal.newDecal(width, height,Assets.texR_background_tundra, true);
        decal_background.setPosition(0f, 3f, 275f);

        ground = Decal.newDecal(new TextureRegion(Assets.tex_ground, 0, 0, 600, 330), true);
        ground.setPosition(0f, -45f, 125f);
        ground.rotateX(90f);

        hotAirBalloons.add(new HotAirBalloon(-26f, -23f, spawnDistance + 33f));
        hotAirBalloons.add(new HotAirBalloon(26f, -23f, spawnDistance + 33f));

        pfx_snow.reset();
    }

    public void update(float delta) {
        super.update(delta);
        decal_background.setColor(1f,1f,1f, opacity);
    }

    public void updateShaders(float delta) {

    }
}

/**
 * Created by Mika on 01/03/2018.
 *
 * World of sea.
 */

class WorldSea extends World {
    float tick;
    float velocity;

    public WorldSea() {
        ground = Decal.newDecal(new TextureRegion(Assets.tex_sea, 0, 0, 560, 280), true);
        ground.setPosition(0f, -30f, 165f);
        ground.rotateX(90f);

        lightHouses.add(new LightHouse(-33f, groundLevel, spawnDistance + 48f));
        lightHouses.add(new LightHouse(33f, groundLevel, spawnDistance + 48f));
    }

    public void update(float delta) {
        super.update(delta);
    }

    public void updateShaders(float delta) {
        tick += delta/2f;
        velocity += player.velocity.z/2f;
        velocity %= 3000f;

        shader3D_sea.begin();
        shader3D_sea.setUniformf("time", tick);
        shader3D_sea.setUniformf("velocity", velocity);
        shader3D_sea.end();
    }
}
