package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

import static fi.tamk.tiko.harecraft.GameScreen.camera;
import static fi.tamk.tiko.harecraft.GameScreen.global_Multiplier;
import static fi.tamk.tiko.harecraft.World.player;

/**
 * Created by Mika on 23.2.2018.
 */

public class Cloud extends GameObject {
    float width = Assets.texR_cloud.getRegionWidth()/100f;
    float height = Assets.texR_cloud.getRegionHeight()/100f;
    boolean isTransparent = false;
    final float MULTIPLIER_LOW = 1f;
    final float MULTIPLIER_DECREMENT = 1f;

    public Cloud(float x, float y, float z) {
        position = new Vector3();
        velocity = new Vector3();

        decal = Decal.newDecal(width * 13f, height * 13f, Assets.texR_cloud, true);
        decal.setPosition(x,y,z);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        //Opacity
        if(!isTransparent) {
            opacity = stateTime < 1f ? stateTime : 1f;
            if(decal.getPosition().z < 0.1f && position.dst(player.position) < 2.8f) {
                isTransparent = true;
                if(decal.getPosition().z > -0.5f && position.dst(player.position) < 1.85f) {
                    if(global_Multiplier > MULTIPLIER_LOW) {
                        global_Multiplier -= MULTIPLIER_DECREMENT;
                    }
                    if(global_Multiplier < MULTIPLIER_LOW) global_Multiplier = MULTIPLIER_LOW;
                    Assets.sound_cloud_hit.play();
                }
            }
            if(decal.getPosition().z < 0f && position.dst(camera.position) < 5f) isTransparent = true;
        } else {
            opacity = 0.3f;
        }
        decal.setColor(1f,1f,1f, opacity);

        //Movement Z
        moveZ(delta);
    }
}
