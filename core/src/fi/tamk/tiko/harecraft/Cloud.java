package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Mika on 23.2.2018.
 */

public class Cloud extends GameObject {

    float width = Assets.texR_cloud.getRegionWidth()/100f;
    float height = Assets.texR_cloud.getRegionHeight()/100f;
    boolean isTransparent = false;

    public Cloud(float x, float y, float z) {
        decal = Decal.newDecal(width * 10f, height * 10f, Assets.texR_cloud, true);
        decal.setPosition(x,y,z);
        position = new Vector2();
    }

    public void update(float delta) {
        stateTime += delta;
        position.x = decal.getPosition().x;
        position.y = decal.getPosition().y;
        if(!isTransparent) {
            opacity = stateTime < 1f ? stateTime : 1f;
            if(decal.getPosition().z < 0f && position.dst(GameScreen.player.position.x, GameScreen.player.position.y) < 2.8f) isTransparent = true;
            if(decal.getPosition().z < 0f && position.dst(GameScreen.camera.position.x, GameScreen.camera.position.y) < 4f) isTransparent = true;
        }
        else {
            opacity = 0.3f;
        }
        decal.setColor(1f,1f,1f, opacity);
        decal.translateZ(-20f * delta);
    }
}
