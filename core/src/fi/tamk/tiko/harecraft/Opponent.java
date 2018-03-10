package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.GameScreen.gameStateTime;
import static fi.tamk.tiko.harecraft.GameScreen.global_Multiplier;
import static fi.tamk.tiko.harecraft.GameScreen.global_Speed;
import static fi.tamk.tiko.harecraft.World.player;
import static fi.tamk.tiko.harecraft.WorldBuilder.spawnDistance;

/**
 * Created by Mika on 28/02/2018.
 */

public class Opponent extends Pilot {
    float spawnZ;

    TextureRegion texR_body = Assets.texR_player_plane_body;
    TextureRegion texR_wings = Assets.texR_player_plane_wings;
    TextureRegion texR_head = Assets.texR_player_plane_head;

    public Opponent(float x, float y, float z, float spawnZ, TextureRegion texR, float speed) {
        velocity = new Vector3();
        position = new Vector3();
        rotation = new Vector3();

        drawDistance = spawnDistance / 5f;
        this.spawnZ = spawnZ;
        this.speed = speed;

        width = texR.getRegionWidth() / 100f;
        height = texR.getRegionHeight() / 100f;
        width *= 2f;
        height *= 2f;

        decal = Decal.newDecal(width, height, texR,true);
        decal.setPosition(x,y,z);

        width = texR_body.getRegionWidth() / 100f;
        height = texR_body.getRegionHeight() / 100f;
        decal_body = Decal.newDecal(width, height, texR_body,true);
        decal_body.setPosition(x,y,z);

        width = texR_head.getRegionWidth() / 100f;
        height = texR_head.getRegionHeight() / 100f;
        decal_head = Decal.newDecal(width, height, texR_head,true);
        decal_head.setPosition(x,y,z+0.1f);

        width = texR_wings.getRegionWidth() / 100f;
        height = texR_wings.getRegionHeight() / 100f;
        decal_wings = Decal.newDecal(width, height, texR_wings,true);
        decal_wings.setPosition(x,y,z+0.2f);

        //decal = decal_body;

        r = g = b = 1f;
    }

    public void update(float delta) {
        super.update(delta);

        if(gameState == START) velocity.z = 9f * stateTime;
        else velocity.z = speed - global_Multiplier * 3f;

        if(gameState == RACE && gameStateTime == 0f) {
            distance = player.distance + spawnZ;
        }

        if(distance > World.end) {
            acceleration += delta * 2f;
            decal.translateZ(-(global_Speed - speed) * delta * acceleration);
            decal.translateY(-(global_Speed - speed) * delta * (acceleration / 10f));
        }
        else moveZ(delta);
    }

    public void dispose() {
        super.dispose();
    }
}
