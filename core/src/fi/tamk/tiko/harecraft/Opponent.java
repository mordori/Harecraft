package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.WorldBuilder.spawnDistance;

/**
 * Created by Mika on 28/02/2018.
 */

public class Opponent extends Pilot {
    float width = Assets.texR_opponent_yellow.getRegionWidth()/100f;
    float height = Assets.texR_opponent_yellow.getRegionHeight()/100f;

    float spawnPositionZ;

    public Opponent(float x, float y, float z, float spawnPositionZ) {
        velocity = new Vector3();
        position = new Vector3();
        rotation = new Vector3();

        decal = Decal.newDecal(width*2f,height*2f,Assets.texR_opponent_yellow, true);
        decal.setPosition(x,y,z);

        this.spawnPositionZ = spawnPositionZ;
    }

    public void update(float delta) {
        super.update(delta);

        if(gameState == START) velocity.z = 9f * stateTime;
        else velocity.z = 5f - GameScreen.global_Multiplier * 2f;
        moveZ(delta);
    }

    public void dispose() {
        super.dispose();
    }
}
