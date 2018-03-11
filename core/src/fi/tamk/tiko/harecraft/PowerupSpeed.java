package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Mika on 11/03/2018.
 */

public class PowerupSpeed extends Powerup {

    public PowerupSpeed(float x, float y, float z) {
        TextureRegion textureRegion = Assets.texR_powerup_blue;

        width = textureRegion.getRegionWidth() / 100f;
        height = textureRegion.getRegionHeight() / 100f;
        width *= 9f;
        height *= 9f;

        decal = Decal.newDecal(width, height, textureRegion, true);
        decal.setPosition(x,y,z);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }
}
