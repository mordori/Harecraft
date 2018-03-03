package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.WorldBuilder.spawnDistance;

/**
 * Created by Mika on 03/03/2018.
 */

public class FinishLine extends GameObject {
    float width = Assets.texR_finishline.getRegionWidth()/15f;
    float height = Assets.texR_finishline.getRegionHeight()/15f;
    boolean isDrawing;

    public FinishLine(float x, float y, float z) {
        position = new Vector3();
        velocity = new Vector3();
        direction = new Vector3();
        rotation = new Vector3();

        decal = Decal.newDecal(width, height, Assets.texR_finishline, true);
        decal.setPosition(x,y,z);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if(position.z < spawnDistance) isDrawing = true;

        if(isDrawing) opacity += delta;
        if(opacity > 1f) opacity = 1f;
        decal.setColor(1f,1f,1f, opacity);

        //Movement Z
        moveZ(delta);
    }
}
