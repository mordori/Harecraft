package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Mika on 28/02/2018.
 */

public class Tree extends GroundObject {

    float width = Assets.texR_tree.getRegionWidth()/100f;
    float height = Assets.texR_tree.getRegionHeight()/100f;

    public Tree(float x, float y, float z) {

        decal = Decal.newDecal(width * 13f, height * 13f, Assets.texR_tree, true);
        decal.setPosition(x,y,z);

        position = new Vector3();
        velocity = new Vector3();
    }

    public void update(float delta) {
        stateTime += delta;
        position = decal.getPosition();

        opacity = stateTime < 1f ? stateTime : 1f;
        decal.setColor(1f,1f,1f, opacity);

        //Movement Z
        velocity.z = (GameScreen.global_Speed - GameScreen.global_Multiplier * 3f) * delta;
        decal.translateZ(velocity.z);
    }
}
