package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Mika on 06/03/2018.
 */

public class Hill extends GroundObject{
    public Hill(float x, float y, float z) {
        position = new Vector3();
        velocity = new Vector3();

        width = Assets.texR_hill.getRegionWidth() / 7f;
        height = Assets.texR_hill.getRegionHeight() / 7f;

        float randomSize = MathUtils.random(1.25f, 2.6f);
        width = width * randomSize;
        height = height * randomSize;

        decal = Decal.newDecal(width, height, Assets.texR_hill,true);
        decal.setPosition(x,y + height/2f - 6f,z);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        //Opacity
        setOpacity();

        //Movement Z
        moveZ(delta);
    }
}
