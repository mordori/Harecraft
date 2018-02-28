package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

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

        position = new Vector3();
        velocity = new Vector3();
    }

    public void update(float delta) {
        stateTime += delta;
        position = decal.getPosition();

        //Opacity
        if(!isTransparent) {
            opacity = stateTime < 1f ? stateTime : 1f;
            if(decal.getPosition().z < 0.1f && position.dst(GameScreen.player.position) < 2.8f) {
                isTransparent = true;
                if(decal.getPosition().z > -0.5f && position.dst(GameScreen.player.position) < 1.85f) {
                    if(GameScreen.global_Multiplier > 1f) {
                        GameScreen.global_Multiplier -= 0.65f;
                        System.out.println("DECREASE");
                        System.out.println(GameScreen.global_Multiplier);
                    }
                    if(GameScreen.global_Multiplier < 1f) GameScreen.global_Multiplier = 1f;
                    Assets.sound_cloud_hit.play();
                }
            }
            if(decal.getPosition().z < 0f && position.dst(GameScreen.camera.position) < 5f) isTransparent = true;
        } else {
            opacity = 0.3f;
        }
        decal.setColor(1f,1f,1f, opacity);

        //Movement Z
        velocity.z = (GameScreen.global_Speed - GameScreen.global_Multiplier * 3f) * delta;
        decal.translateZ(velocity.z);
    }
}
