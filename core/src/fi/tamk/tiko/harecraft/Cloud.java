package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g3d.decals.Decal;

/**
 * Created by Minuet on 23.2.2018.
 */

public class Cloud extends GameObject {

    float width = Assets.texR_cloud.getRegionWidth()/100f;
    float height = Assets.texR_cloud.getRegionHeight()/100f;

    public Cloud(float x, float y, float z) {
        decal = Decal.newDecal(width * 5, height * 5f, Assets.texR_cloud, true);
        decal.setPosition(x,y,z);
    }

    public void update(float delta) {
        decal.translateZ(-12f * delta);
    }
}
