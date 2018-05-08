package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import java.util.ArrayList;

import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.EXIT;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.GameScreen.gameStateTime;
import static fi.tamk.tiko.harecraft.GameScreen.isTransition;
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
    ParticleEffect pfx_snow;

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

    public World() {
        finish = ProfileInfo.selectedDuration;
        //finish = 100f;
        end = finish + spawnDistance + 20f;

        if(SCREEN_WIDTH > 1600) pfx_speed_lines = new ParticleEffect(Assets.pfx_speed_lines_2);
        else pfx_speed_lines = new ParticleEffect(Assets.pfx_speed_lines);
        pfx_snow = new ParticleEffect(Assets.pfx_snow);
        pfx_snow.getEmitters().first().setPosition(SCREEN_WIDTH/2f, SCREEN_HEIGHT/2.2f);
        pfx_snow.getEmitters().get(1).setPosition(SCREEN_WIDTH/2f, SCREEN_HEIGHT/2.2f);


        player = new Player(0f,-7f,-5f);
        opponents.add(new Opponent(-65f*2f, Pilot.WOLF));
        opponents.add(new Opponent(-52f*2f, Pilot.GIRAFF));
        opponents.add(new Opponent(-61f*2f, Pilot.FOX));
        opponents.add(new Opponent(-55f*2f, Pilot.KOALA));
        opponents.add(new Opponent(-61f*2f, Pilot.BEAR));

        /*opponents.add(new Opponent(-3f, -2f, -65f*2f, finish/4f, Pilot.WOLF));
        opponents.add(new Opponent(0f, 4f, -52f*2f, finish/5f, Pilot.GIRAFF, 8f - (5f/(1f+DIFFICULTYSENSITIVITY) * 0.65f)));
        opponents.add(new Opponent(4f, -2f, -61f*2f, finish/6f, Pilot.FOX, 7.5f - (5f/(1f+DIFFICULTYSENSITIVITY) * 0.65f)));
        opponents.add(new Opponent(4f, 2f, -55f*2f, finish/10f, Pilot.KOALA, 9f - (5f/(1f+DIFFICULTYSENSITIVITY) * 0.65f)));
        opponents.add(new Opponent(3f, -5f, -61f*2f, finish/12f, Pilot.BEAR, 6.75f - (5f/(1f+DIFFICULTYSENSITIVITY) * 0.65f)));*/

        decal_sun1 = Decal.newDecal(Assets.texR_sun, true);
        decal_sun1.setPosition(0f, -40f, 294f);
        decal_sun2 = Decal.newDecal(Assets.texR_sun, true);
        decal_sun2.setPosition(0f, -40f, 298f);
        decal_sun2.rotateZ(45f);
    }

    public void dispose() {
        player.dispose();

        for(Opponent o : opponents) {
            o.dispose();
        }

        if(pfx_speed_lines != null) pfx_speed_lines.dispose();
        if(pfx_snow != null) pfx_snow.dispose();
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
        pfx_snow = null;

        hotAirBalloons.add(new HotAirBalloon(-26f, -23f, spawnDistance + 36f));
        hotAirBalloons.add(new HotAirBalloon(26f, -23f, spawnDistance + 36f));
    }

    public void update(float delta) {
        super.update(delta);
        decal_background.setColor(1f,1f,1f, opacity);

        if(pfx_snow != null) pfx_snow.getEmitters().get(0).setPosition(SCREEN_WIDTH/2f, SCREEN_HEIGHT/2.2f);
        if(pfx_snow != null) pfx_snow.getEmitters().get(1).setPosition(SCREEN_WIDTH/2f, SCREEN_HEIGHT/2.2f);
    }

    public void updateShaders(float delta) {
        /*shader2D_vignette.begin();
        if (GameScreen.gameStateTime > 2f && GameScreen.gameState == START) {
            shader2D_vignette.setUniformf("u_stateTime", (GameScreen.gameStateTime - 2f) / 4f);
            if((gameStateTime - 2f) / 4f > 0.8f) shader2D_vignette.setUniformf("u_stateTime", 0.8f);
        }
        if (GameScreen.gameStateTime > 0.5f && GameScreen.gameState == END) {
            shader2D_vignette.setUniformf("u_stateTime", 0.8f -(GameScreen.gameStateTime - 0.5f) / 3f);
            if(0.8f -(GameScreen.gameStateTime - 0.5f) / 3f < 0f) shader2D_vignette.setUniformf("u_stateTime", 0f);
        }
        shader2D_vignette.end();
        */
        //shader2D_blur.begin();
        //shader3D_blur.setUniformMatrix("u_projTrans", camera.combined);
        //shader3D_blur.setUniformi("u_texture", 0);
        //shader2D_blur.end();
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

        hotAirBalloons.add(new HotAirBalloon(-26f, -23f, spawnDistance + 36f));
        hotAirBalloons.add(new HotAirBalloon(26f, -23f, spawnDistance + 36f));
    }

    public void update(float delta) {
        super.update(delta);
        decal_background.setColor(1f,1f,1f, opacity);
    }

    public void updateShaders(float delta) {
        /*shader2D_vignette.begin();
        if (GameScreen.gameStateTime > 2f && GameScreen.gameState == START) {
            shader2D_vignette.setUniformf("u_stateTime", (GameScreen.gameStateTime - 2f) / 4f);
            if((gameStateTime - 2f) / 4f > 0.8f) shader2D_vignette.setUniformf("u_stateTime", 0.8f);
        }
        if (GameScreen.gameStateTime > 0.5f && GameScreen.gameState == END) {
            shader2D_vignette.setUniformf("u_stateTime", 0.8f -(GameScreen.gameStateTime - 0.5f) / 3f);
            if(0.8f -(GameScreen.gameStateTime - 0.5f) / 3f < 0f) shader2D_vignette.setUniformf("u_stateTime", 0f);
        }
        shader2D_vignette.end();
        */
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
        ground = Decal.newDecal(new TextureRegion(Assets.tex_sea, 0, 0, 600, 330), true);
        ground.setPosition(0f, -30f, 125f);
        ground.rotateX(90f);
        pfx_snow = null;

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
