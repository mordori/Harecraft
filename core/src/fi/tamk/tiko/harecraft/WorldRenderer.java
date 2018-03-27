package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.FINISH;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.camera;
import static fi.tamk.tiko.harecraft.GameScreen.dBatch;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.MyGroupStrategy.SHADER_DEFAULT;
import static fi.tamk.tiko.harecraft.MyGroupStrategy.SHADER_SEA;
import static fi.tamk.tiko.harecraft.MyGroupStrategy.SHADER_VIGNETTE;
import static fi.tamk.tiko.harecraft.MyGroupStrategy.activeShader;
import static fi.tamk.tiko.harecraft.World.player;

/**
 * Created by Mika on 01/03/2018.
 */

public class WorldRenderer {
    GameMain game;
    World world;

    public WorldRenderer(World world, GameMain game) {
        this.world = world;
        this.game = game;
    }

    public void renderWorld() {
        drawDecals();
        drawParticles();
    }

    public void drawDecals() {
        if(gameState == START) activeShader = SHADER_VIGNETTE;
        else activeShader = SHADER_DEFAULT;

        dBatch.add(world.decal_foreground);
        dBatch.add(world.decal_sun1);
        dBatch.add(world.decal_sun2);
        dBatch.flush();

        activeShader = SHADER_SEA;
        dBatch.add(world.sea);
        dBatch.flush();

        if(gameState == START) activeShader = SHADER_VIGNETTE;
        else activeShader = SHADER_DEFAULT;

        if(gameState == FINISH || gameState == END) {
            for(HotAirBalloon hotAirBalloon : world.hotAirBalloons) {
                dBatch.add(hotAirBalloon.decal);
                if(hotAirBalloon == world.hotAirBalloons.get(0)) dBatch.add(hotAirBalloon.decal_ribbons);
            }
        }

        drawDecalLists();

        if(player.isDrawing || player.opacity != 0f){
            dBatch.add(player.decal_wings);
            dBatch.add(player.decal_head);
            dBatch.add(player.decal);
        }

        //////////////////////////////////////////////
        dBatch.flush();
    }

    public void drawParticles() {
        game.sBatch.begin();
        if((player.velocity.x != 0f || player.velocity.y != 0f) && gameState != END) player.pfx_scarf.draw(game.sBatch);
        //if((player.velocity.x != 0f || player.velocity.y != 0f) && gameState != END) player.pfx_stream.draw(game.sBatch);
        //if((player.velocity.x != 0f || player.velocity.y != 0f) && gameState != END) player.pfx_stream2.draw(game.sBatch);
        game.sBatch.end();
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
