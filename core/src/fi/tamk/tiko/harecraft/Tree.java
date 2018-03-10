package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Mika on 28/02/2018.
 */

public class Tree extends GroundObject {
    public Tree(float x, float y, float z) {
        position = new Vector3();
        velocity = new Vector3();

        TextureRegion textureRegion;
        float transposedY = y - 1f;

        if(MathUtils.random(0, 9) < 7) {
            if(MathUtils.random(1,3) < 3) {
                textureRegion = Assets.texR_tree_big_light;
            }
            else textureRegion = Assets.texR_tree_big_dark;
        }
        else {
            if(MathUtils.random(0, 1) < 1) {
                textureRegion = Assets.texR_tree_small_light;
            }
            else textureRegion = Assets.texR_tree_small_dark;

            transposedY -= 1f;
        }


        width = textureRegion.getRegionWidth() / 100f;
        height = textureRegion.getRegionHeight() / 100f;
        width *= 17f;
        height *= 17f;

        decal = Decal.newDecal(width, height, textureRegion, true);
        decal.setPosition(x,transposedY,z);
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
