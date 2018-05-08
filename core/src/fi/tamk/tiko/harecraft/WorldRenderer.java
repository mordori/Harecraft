package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.decals.Decal;

import static fi.tamk.tiko.harecraft.GameMain.dBatch;
import static fi.tamk.tiko.harecraft.GameMain.fbo;
import static fi.tamk.tiko.harecraft.GameMain.sBatch;
import static fi.tamk.tiko.harecraft.GameMain.texture;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.EXIT;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.FINISH;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.GameScreen.isTransition;
import static fi.tamk.tiko.harecraft.GameScreen.opacity;
import static fi.tamk.tiko.harecraft.GameScreen.worldIndex;
import static fi.tamk.tiko.harecraft.MyGroupStrategy.SHADER3D_DEFAULT;
import static fi.tamk.tiko.harecraft.MyGroupStrategy.SHADER3D_SEA;
import static fi.tamk.tiko.harecraft.MyGroupStrategy.activeShader;
import static fi.tamk.tiko.harecraft.World.player;

/**
 * Created by Mika on 01/03/2018.
 *
 * Class used for sending stuff to batchers and Frame Bufferer.
 */

public class WorldRenderer {
    World world;
    boolean isFBOEnabled;
    boolean isSeaEnabled;

    public WorldRenderer(World world) {
        this.world = world;
    }

    public void renderWorld() {
        if(worldIndex == 0) {
            isSeaEnabled = true;
            Gdx.gl.glClearColor(32/255f, 137/255f, 198/255f, 1f);
        }
        else if(worldIndex == 1) {
            Gdx.gl.glClearColor(137/255f, 189/255f, 255/255f, 1f);
        }
        else if(worldIndex == 2) {
            Gdx.gl.glClearColor(60/255f, 140/255f, 208/255f, 1f);
        }
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if(isTransition) isFBOEnabled = true;

        if(isFBOEnabled) {
            fbo.begin();
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        }

        drawDecals();
        drawParticles();

        if(isFBOEnabled) {
            fbo.end();
            renderToTexture();
        }
    }

    public void drawDecals() {
        activeShader = SHADER3D_DEFAULT;
        //----------------------------
        if(!isSeaEnabled) dBatch.add(world.decal_background);

        dBatch.add(world.decal_sun1);
        dBatch.add(world.decal_sun2);
        dBatch.flush();


        if(isSeaEnabled) activeShader = SHADER3D_SEA;
        //----------------------------
        dBatch.add(world.ground);
        dBatch.flush();

        if(isSeaEnabled) activeShader = SHADER3D_DEFAULT;
        //----------------------------

        if(gameState == FINISH || gameState == END || (gameState == EXIT && player.distance > world.finish)) {
            if(worldIndex == 1 || worldIndex == 2) {
                for (HotAirBalloon hotAirBalloon : world.hotAirBalloons) {
                    dBatch.add(hotAirBalloon.decal);
                }
            }
            else {
                for (LightHouse lightHouse : world.lightHouses) {
                    dBatch.add(lightHouse.decal);
                    dBatch.add(lightHouse.decal_island);
                }
            }
        }

        drawDecalLists();

        if(player.isDrawing || player.opacity != 0f) {
            dBatch.add(player.decal_wings);
            dBatch.add(player.decal_head);
            dBatch.add(player.decal);
        }

        //////////////////////////////////////////////
        dBatch.flush();
    }

    public void renderToTexture() {
        texture.setTexture(fbo.getColorBufferTexture());

        //------------------------------------------------
        sBatch.begin();
        texture.draw(sBatch, opacity);
        sBatch.end();
    }

    public void drawParticles() {
        sBatch.begin();
        if(gameState == RACE || gameState == FINISH) {
            player.pfx_scarf.draw(sBatch);
            player.pfx_wind_trail_right.draw(sBatch);
            player.pfx_wind_trail_left.draw(sBatch);
        }

        for(Cloud c : world.clouds_RDown) if(c.isCollided) c.pfx_dispersion.draw(sBatch);
        for(Cloud c : world.clouds_RUp) if(c.isCollided) c.pfx_dispersion.draw(sBatch);
        for(Cloud c : world.clouds_LDown) if(c.isCollided) c.pfx_dispersion.draw(sBatch);
        for(Cloud c : world.clouds_LUp) if(c.isCollided) c.pfx_dispersion.draw(sBatch);

        for(Ring r : world.rings) if(r.isCollected) {
            r.pfx_speed_up.draw(sBatch);
        }

        if(world.pfx_snow != null) world.pfx_snow.draw(sBatch);
        world.pfx_speed_lines.draw(sBatch);
        sBatch.end();
    }

    public void drawDecalLists() {
        for(Cloud c : world.clouds_LUp) dBatch.add(c.decal);
        for(Cloud c : world.clouds_LDown) dBatch.add(c.decal);
        for(Cloud c : world.clouds_RUp) dBatch.add(c.decal);
        for(Cloud c : world.clouds_RDown) dBatch.add(c.decal);

        for(Ring l : world.rings) {
            dBatch.add(l.decal);
            dBatch.add(l.decal_arrows);
        }

        for(Balloon b : world.balloons) dBatch.add(b.decal);

        for(Boat b : world.boats) dBatch.add(b.decal);

        for(Tree t : world.trees_L) dBatch.add(t.decal);
        for(Tree t : world.trees_R) dBatch.add(t.decal);

        for(Hill h : world.hills_L) dBatch.add(h.decal);
        for(Hill h : world.hills_R) dBatch.add(h.decal);

        for(Lake l : world.lakes_L) dBatch.add(l.decal);
        for(Lake l : world.lakes_R) dBatch.add(l.decal);

        for(Island i : world.islands_L) {
            dBatch.add(i.decal);
            for(Decal d : i.palmtrees) {
                dBatch.add(d);
            }
        }
        for(Island i : world.islands_R) {
            dBatch.add(i.decal);
            for(Decal d : i.palmtrees) {
                dBatch.add(d);
            }
        }

        for(Opponent o : world.opponents) {
            if(o.isDrawing || o.opacity != 0f) {
                dBatch.add(o.decal_wings);
                dBatch.add(o.decal_head);
                dBatch.add(o.decal);
            }
        }
    }
}
