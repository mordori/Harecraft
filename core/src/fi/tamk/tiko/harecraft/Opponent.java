package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Mika on 28/02/2018.
 */

public class Opponent extends Pilot {
    float width = Assets.texR_opponent_yellow.getRegionWidth()/100f;
    float height = Assets.texR_opponent_yellow.getRegionHeight()/100f;

    final float SPEED = 15f;
    final float MAX_SPEED = 5f;

    float spawnPositionZ;

    boolean isDrawing;

    public Opponent(float x, float y, float z, float spawnPositionZ) {
        velocity = new Vector3();
        position = new Vector3();
        rotation = new Vector3();

        decal = Decal.newDecal(width*2f,height*2f,Assets.texR_opponent_yellow, true);
        decal.setPosition(x,y,z);

        this.spawnPositionZ = spawnPositionZ;
    }

    public void update(float delta) {
        stateTime += delta;
        position = decal.getPosition();

        if(isDrawing) opacity += delta;
        else opacity -= delta;

        if(opacity > 1f) opacity = 1f;
        if(opacity < 0f) opacity = 0f;
        decal.setColor(1f,1f,1f, opacity);

        if(GameScreen.state == GameScreen.State.RACE) velocity.z = 5f - GameScreen.global_Multiplier * 2f;
        else velocity.z = 9f*stateTime;
        decal.translateZ(velocity.z * delta);

        distance += -velocity.z;
    }

    public void dispose() {
        super.dispose();
    }
}
