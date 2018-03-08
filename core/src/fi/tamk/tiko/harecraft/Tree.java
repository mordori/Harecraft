package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;

/**
 * Created by Mika on 28/02/2018.
 */

public class Tree extends GroundObject {
    public Tree(float x, float y, float z) {
        position = new Vector3();
        velocity = new Vector3();

        TextureRegion textureRegion;

        if(MathUtils.random(0f,7f) < 1f) textureRegion = Assets.texR_tree_dark;
        else textureRegion = Assets.texR_tree;

        width = textureRegion.getRegionWidth() / 100f;
        height = textureRegion.getRegionHeight() / 100f;
        width *= 17f;
        height *= 17f;

        decal = Decal.newDecal(width, height, textureRegion, true);
        decal.setPosition(x,y,z);
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
