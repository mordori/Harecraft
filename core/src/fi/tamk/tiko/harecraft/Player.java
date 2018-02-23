package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g3d.decals.Decal;

/**
 * Created by Mika on 23.2.2018.
 */

public class Player extends GameObject {

    float width = Assets.texR_player.getRegionWidth()/100f;
    float height = Assets.texR_player.getRegionHeight()/100f;

    public Player(float x, float y, float z) {
        decal = Decal.newDecal(width,height,Assets.texR_player, true);
        decal.setPosition(x,y,z);
    }
}
