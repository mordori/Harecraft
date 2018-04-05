package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import static fi.tamk.tiko.harecraft.GameMain.dBatch;
import static fi.tamk.tiko.harecraft.GameMain.fbo;
import static fi.tamk.tiko.harecraft.GameMain.sBatch;
import static fi.tamk.tiko.harecraft.GameMain.texture;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.FINISH;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.MyGroupStrategy.SHADER3D_DEFAULT;
import static fi.tamk.tiko.harecraft.MyGroupStrategy.SHADER3D_SEA;
import static fi.tamk.tiko.harecraft.MyGroupStrategy.activeShader;
import static fi.tamk.tiko.harecraft.Shaders2D.shader2D_vignette;
import static fi.tamk.tiko.harecraft.World.player;

/**
 * Created by Mika on 01/03/2018.
 */

public class WorldRenderer {
    World world;
    boolean isFBOEnabled;

    public WorldRenderer(World world) {
        this.world = world;
        Gdx.gl.glClearColor(42/255f, 116/255f, 154/255f, 1f);
    }

    public void renderWorld() {
        if(gameState == END) isFBOEnabled = true;
        else isFBOEnabled = false;

        if(!isFBOEnabled) Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

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

        dBatch.add(world.decal_foreground);
        dBatch.add(world.decal_sun1);
        dBatch.add(world.decal_sun2);
        dBatch.flush();

        activeShader = SHADER3D_SEA;
        //----------------------------
        dBatch.add(world.sea);
        dBatch.flush();

        activeShader = SHADER3D_DEFAULT;
        //----------------------------

        if(gameState == FINISH || gameState == END) {
            for(HotAirBalloon hotAirBalloon : world.hotAirBalloons) {
                dBatch.add(hotAirBalloon.decal);
                if(hotAirBalloon == world.hotAirBalloons.get(0)) dBatch.add(hotAirBalloon.decal_ribbons);
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

        if(isFBOEnabled) sBatch.setShader(shader2D_vignette);
        //------------------------------------------------
        sBatch.begin();
        texture.draw(sBatch);
        sBatch.end();
    }

    public void drawParticles() {
        sBatch.begin();
        if(!isFBOEnabled) player.pfx_scarf.draw(sBatch);

        for(Cloud c : world.clouds_RDown) {
            if(c.isCollided) c.pfx_dispersion.draw(sBatch);
        }
        for(Cloud c : world.clouds_RUp) {
            if(c.isCollided) c.pfx_dispersion.draw(sBatch);
        }
        for(Cloud c : world.clouds_LDown) {
            if(c.isCollided) c.pfx_dispersion.draw(sBatch);
        }
        for(Cloud c : world.clouds_LUp) {
            if(c.isCollided) c.pfx_dispersion.draw(sBatch);
        }

        for(Powerup p : world.powerups) {
            if(p.isCollected) p.pfx_hit.draw(sBatch);
        }

        for(Ring r : world.rings) {
            if(r.isCollected) r.pfx_speed_up.draw(sBatch);
        }

        world.pfx_speed_lines.draw(sBatch);
        sBatch.end();
    }

    public void drawDecalLists() {
        for(Cloud c : world.clouds_LUp) {
            dBatch.add(c.decal);
        }
        for(Cloud c : world.clouds_LDown) {
            dBatch.add(c.decal);
        }
        for(Cloud c : world.clouds_RUp) {
            dBatch.add(c.decal);
        }
        for(Cloud c : world.clouds_RDown) {
            dBatch.add(c.decal);
        }
        for(Ring l : world.rings) {
            dBatch.add(l.decal);
            dBatch.add(l.decal_arrows);
        }
        for(Powerup p : world.powerups) {
            dBatch.add(p.decal);
        }
        for(Tree t : world.trees_L) {
            dBatch.add(t.decal);
        }
        for(Tree t : world.trees_R) {
            dBatch.add(t.decal);
        }
        for(Hill h : world.hills_L) {
            dBatch.add(h.decal);
        }
        for(Hill h : world.hills_R) {
            dBatch.add(h.decal);
        }
        for(Lake l : world.lakes_L) {
            dBatch.add(l.decal);
        }
        for(Lake l : world.lakes_R) {
            dBatch.add(l.decal);
        }
        for(Opponent o : world.opponents) {
            if(o.isDrawing || o.opacity != 0f) {
                dBatch.add(o.decal_wings);
                dBatch.add(o.decal_head);
                dBatch.add(o.decal);
                dBatch.add(o.decal_playerTag);

            }
        }
    }
}
