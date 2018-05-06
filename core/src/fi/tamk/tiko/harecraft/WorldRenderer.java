package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import static fi.tamk.tiko.harecraft.GameMain.blurTargetA;
import static fi.tamk.tiko.harecraft.GameMain.blurTargetB;
import static fi.tamk.tiko.harecraft.GameMain.camera;
import static fi.tamk.tiko.harecraft.GameMain.dBatch;
import static fi.tamk.tiko.harecraft.GameMain.fbo;
import static fi.tamk.tiko.harecraft.GameMain.orthoCamera;
import static fi.tamk.tiko.harecraft.GameMain.sBatch;
import static fi.tamk.tiko.harecraft.GameMain.texture;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.EXIT;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.FINISH;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.GameScreen.gameStateTime;
import static fi.tamk.tiko.harecraft.MyGroupStrategy.SHADER3D_DEFAULT;
import static fi.tamk.tiko.harecraft.MyGroupStrategy.SHADER3D_SEA;
import static fi.tamk.tiko.harecraft.MyGroupStrategy.activeShader;
import static fi.tamk.tiko.harecraft.Shaders2D.shader2D_blur;
import static fi.tamk.tiko.harecraft.Shaders2D.shader2D_default;
import static fi.tamk.tiko.harecraft.Shaders2D.shader2D_luminance;
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
    boolean isBlurEnabled;

    static float radius = 3f;
    final static float MAX_BLUR = 5.0f;
    private static final float WORLD_TO_SCREEN = 1.0f / 100.0f;


    public WorldRenderer(World world) {
        this.world = world;

        if(world instanceof WorldSea) {
            isSeaEnabled = true;
            //isBlurEnabled = true;
            Gdx.gl.glClearColor(32/255f, 137/255f, 198/255f, 1f);
        }
        else if(world instanceof WorldSummer) {
            //isBlurEnabled = true;
            Gdx.gl.glClearColor(137/255f, 189/255f, 255/255f, 1f);
        }
        else if(world instanceof WorldTundra) {
            //isBlurEnabled = true;
            Gdx.gl.glClearColor(60/255f, 140/255f, 208/255f, 1f);
        }
    }

    public void renderWorld() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if(gameState == EXIT) isFBOEnabled = true;

        if(isFBOEnabled) {
            fbo.begin();
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        }

        if(isBlurEnabled) {

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

        //if(!isSeaEnabled) dBatch.add(world.decal_background);
        //dBatch.flush();

        if(isBlurEnabled) {
            blurTargetA.begin();
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        }

        if(!isSeaEnabled) dBatch.add(world.decal_background);
        //dBatch.flush();

        dBatch.add(world.decal_sun1);
        dBatch.add(world.decal_sun2);
        dBatch.flush();

        if(isBlurEnabled) {
            blurTargetA.end();

            applyBlur(2.0f);
            sBatch.setShader(null);

            //if(!isSeaEnabled) dBatch.add(world.decal_background);
            dBatch.flush();
        }


        if(isSeaEnabled) activeShader = SHADER3D_SEA;
        //----------------------------
        dBatch.add(world.ground);
        dBatch.flush();

        if(isSeaEnabled) activeShader = SHADER3D_DEFAULT;
        //----------------------------

        if(gameState == FINISH || gameState == END) {
            for(HotAirBalloon hotAirBalloon : world.hotAirBalloons) {
                dBatch.add(hotAirBalloon.decal);
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

    private void applyBlur(float blur) {
        //Gdx.gl.glBlendEquation( GL20.GL_FUNC_ADD );


        //sBatch.begin();
        //sBatch.setShader(shader2D_default);
        //sBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
        //sBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        //sprite.draw(sBatch);
        //sBatch.end();

        blurTargetB.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        shader2D_luminance.setUniformf("brightPassThreshold",0f);
        sBatch.setShader(shader2D_luminance);
        sBatch.begin();
        drawTexture(blurTargetA.getColorBufferTexture(),  0.0f, 0.0f);
        sBatch.flush();
        blurTargetB.end();


        //sBatch.begin();
        blurTargetA.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        shader2D_blur.setUniformf("dir", 1.0f, 1.0f);
        shader2D_blur.setUniformf("radius", blur);
        sBatch.setShader(shader2D_blur);
        drawTexture(blurTargetB.getColorBufferTexture(),  0.0f, 0.0f);
        sBatch.flush();
        blurTargetA.end();

        Sprite sprite = new Sprite(blurTargetA.getColorBufferTexture());
        sprite.flip(false, true);


        //blurTargetB.begin();
        shader2D_blur.setUniformf("dir", 1.0f, 1.0f);
        shader2D_blur.setUniformf("radius", blur);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        sBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        //drawTexture(blurTargetA.getColorBufferTexture(),  0.0f, 0.0f);
        sprite.draw(sBatch);
        //sBatch.flush();
        //blurTargetB.end();


        //sBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);

        //Gdx.gl.glBlendEquation( GL20.GL_FUNC_ADD);
        //sBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        //drawTexture(blurTargetB.getColorBufferTexture(),  0.0f, 0.0f);
        sBatch.flush();
        sBatch.end();

        //sBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void drawTexture(Texture texture, float x, float y) {
        sBatch.draw(texture, x, y);
    }

    public void renderToTexture() {
        texture.setTexture(fbo.getColorBufferTexture());

        //if(gameState == END) sBatch.setShader(shader2D_vignette);

        //------------------------------------------------
        sBatch.begin();
        texture.draw(sBatch, gameStateTime);
        sBatch.end();
    }

    public void drawParticles() {
        sBatch.begin();
        //if(world.pfx_sea_glimmer != null) world.pfx_sea_glimmer.draw(sBatch);
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

        for(Tree t : world.trees_L) dBatch.add(t.decal);
        for(Tree t : world.trees_R) dBatch.add(t.decal);

        for(Hill h : world.hills_L) dBatch.add(h.decal);
        for(Hill h : world.hills_R) dBatch.add(h.decal);

        for(Lake l : world.lakes_L) dBatch.add(l.decal);
        for(Lake l : world.lakes_R) dBatch.add(l.decal);

        for(Opponent o : world.opponents) {
            if(o.isDrawing || o.opacity != 0f) {
                dBatch.add(o.decal_wings);
                dBatch.add(o.decal_head);
                dBatch.add(o.decal);
                //dBatch.add(o.decal_playerTag);
            }
        }
    }
}
