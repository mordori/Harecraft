package fi.tamk.tiko.harecraft;

import static fi.tamk.tiko.harecraft.GameScreen.dBatch;
import static fi.tamk.tiko.harecraft.World.player;
import static fi.tamk.tiko.harecraft.WorldBuilder.spawnDistance;

/**
 * Created by Mika on 01/03/2018.
 */

public class WorldRenderer {
    World world;

    public WorldRenderer(World world) {
        this.world = world;
    }

    public void renderWorld() {
        drawDecals();
    }

    public void drawDecals() {
        dBatch.add(world.decal_background);

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
        }

        for(Tree t : world.trees) {
            dBatch.add(t.decal);
        }

        for(Opponent o : world.opponents) {
            if(o.position.z < spawnDistance/10f) {
                o.isDrawing = true;
                dBatch.add(o.decal);
            }
            else if(o.opacity != 0f) {
                o.isDrawing = false;
                dBatch.add(o.decal);
            }
        }

        dBatch.add(player.decal);
        //////////////////////////////////////////////
        dBatch.flush();
    }
}
