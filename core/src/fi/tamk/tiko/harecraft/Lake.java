package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import static fi.tamk.tiko.harecraft.WorldBuilder.spawnDistance;

/**
 * Created by Mika on 02/03/2018.
 */

public class Lake extends GroundObject {
    public Lake(float x, float y, float z) {
        position = new Vector3();
        velocity = new Vector3();

        width = Assets.texR_lake.getRegionWidth() / 4f;
        height = Assets.texR_lake.getRegionHeight() / 4f;

        float randomSize = MathUtils.random(1f, 2.5f);
        width = width * randomSize;
        height = height * randomSize;

        decal = Decal.newDecal(width, height, Assets.texR_lake, true);
        decal.setPosition(x,y - 5f,z + height);
        decal.rotateX(90f);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if(position.z + height/3f > spawnDistance) stateTime = 0;

        //Opacity
        setOpacity();

        //Movement Z
        moveZ(delta);
    }
}
