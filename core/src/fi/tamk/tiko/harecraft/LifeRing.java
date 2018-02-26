package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Mika on 26/02/2018.
 */

public class LifeRing extends GameObject {

    float width = Assets.texR_lifering.getRegionWidth()/100f;
    float height = Assets.texR_lifering.getRegionHeight()/100f;
    Vector2 projection = new Vector2();
    boolean isTransparent = false;

    public LifeRing(float x, float y, float z) {
        decal = Decal.newDecal(width * 9f, height * 9f, Assets.texR_lifering, true);
        decal.setPosition(x,y,z);
    }

    public void update(float delta) {
        projection.x = decal.getPosition().x;
        projection.y = decal.getPosition().y;
        stateTime += delta;
        if(!isTransparent) {
            opacity = stateTime < 1f ? stateTime : 1f;
            if(decal.getPosition().z < 0f && projection.dst(GameScreen.player.decal.getPosition().x, GameScreen.player.decal.getPosition().y) < 2f) isTransparent = true;
        }
        else {
            opacity = 0.75f;
            decal.setScale(decal.getScaleX() + delta*stateTime/3f);
        }
        decal.setColor(1f,1f,1f, opacity);
        if(!isTransparent || decal.getScaleX() > 1.35f)decal.translateZ(-20f * delta);
        decal.rotateZ(delta * 50f);
    }
}
