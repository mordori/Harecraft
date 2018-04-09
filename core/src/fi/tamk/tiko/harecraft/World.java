package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;

import static fi.tamk.tiko.harecraft.GameScreen.DIFFICULTYSENSITIVITY;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.GameScreen.gameStateTime;
import static fi.tamk.tiko.harecraft.MyGroupStrategy.shader3D_sea;
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

    //Sea Objects
    ArrayList<Boat> boats = new ArrayList<Boat>();
    ArrayList<Whale> whales = new ArrayList<Whale>();
    ArrayList<Island> islands = new ArrayList<Island>();

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
        switch (MathUtils.random(0,2)) {
            case 0:
                finish = 1000f;
                break;
            case 1:
                finish = 2000f;
                break;
            case 2:
                finish = 1000f;
                break;
        }
        end = finish + spawnDistance + 20f;

        pfx_speed_lines = new ParticleEffect(Assets.pfx_speed_lines);

        player = new Player(0f,-7f,-5f);
        opponents.add(new Opponent(-3f, -2f, -65f*2f, finish/4f, Pilot.COLOR_ORANGE, Pilot.PLANE_2, Pilot.CHARACTER_DEF, 8f - (5f/(1f+DIFFICULTYSENSITIVITY) * 0.5f)));
        opponents.add(new Opponent(0f, 4f, -52f*2f, finish/5f, Pilot.COLOR_ORANGE, Pilot.PLANE_2, Pilot.CHARACTER_DEF, 8f - (5f/(1f+DIFFICULTYSENSITIVITY) * 0.5f)));
        opponents.add(new Opponent(4f, -2f, -61f*2f, finish/6f, Pilot.COLOR_ORANGE, Pilot.PLANE_2, Pilot.CHARACTER_DEF, 8f - (5f/(1f+DIFFICULTYSENSITIVITY) * 0.5f)));
        opponents.add(new Opponent(4f, 2f, -55f*2f, finish/10f, Pilot.COLOR_ORANGE, Pilot.PLANE_2, Pilot.CHARACTER_DEF, 9f - (5f/(1f+DIFFICULTYSENSITIVITY) * 0.5f)));
        opponents.add(new Opponent(3f, -5f, -61f*2f, finish/12f, Pilot.COLOR_ORANGE, Pilot.PLANE_2, Pilot.CHARACTER_DEF, 6f - (5f/(1f+DIFFICULTYSENSITIVITY) * 0.5f)));

        decal_sun1 = Decal.newDecal(Assets.texR_sun, true);
        decal_sun1.setPosition(0f, -40f, 294f);
        decal_sun2 = Decal.newDecal(Assets.texR_sun, true);
        decal_sun2.setPosition(0f, -40f, 298f);
        decal_sun2.rotateZ(45f);

        hotAirBalloons.add(new HotAirBalloon(-26f, -23f, spawnDistance + 30f));
        hotAirBalloons.add(new HotAirBalloon(26f, -23f, spawnDistance + 30f));
    }

    public void dispose() {
        player.dispose();

        for(Opponent o : opponents) {
            o.dispose();
        }

        pfx_speed_lines.dispose();
    }

    public abstract void updateShaders(float delta);
    public void update(float delta) {
        if(gameState == START) opacity = gameStateTime < 1f ? gameStateTime : 1f;
        else if(gameState == END && gameStateTime > 4f) opacity = 5f - gameStateTime > 0f ? 5f - gameStateTime : 0f;

        decal_sun1.rotateZ(delta/2f);
        decal_sun1.setColor(1f,1f,1f, opacity);

        decal_sun2.rotateZ(-delta);
        decal_sun2.setColor(1f,1f,1f, opacity);
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

class WorldSea extends World {
    float tick;
    float velocity;

    public WorldSea() {
        ground = Decal.newDecal(new TextureRegion(Assets.tex_sea, 0, 0, 600, 330), true);
        ground.setPosition(0f, -28f, 125f);
        ground.rotateX(90f);
    }

    public void update(float delta) {
        super.update(delta);
    }

    public void updateShaders(float delta) {
        tick += delta;
        velocity += player.velocity.z;
        velocity %= 3000f;

        shader3D_sea.begin();
        shader3D_sea.setUniformf("time", tick);
        shader3D_sea.setUniformf("velocity", velocity);
        shader3D_sea.end();
    }
}
