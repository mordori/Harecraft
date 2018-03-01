package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import static fi.tamk.tiko.harecraft.World.player;

/**
 * Created by Mika on 26/02/2018.
 */

public class Ring extends GameObject {
    float width = Assets.texR_lifering.getRegionWidth()/100f;
    float height = Assets.texR_lifering.getRegionHeight()/100f;
    final float SPEED = 10f;
    boolean isCollected = false;

    public Ring(float x, float y, float z) {
        position = new Vector3();
        velocity = new Vector3();
        direction = new Vector3();
        rotation = new Vector3();

        decal = Decal.newDecal(width * 8.5f, height * 8.5f, Assets.texR_lifering, true);
        decal.setPosition(x,y,z);
    }

    public void update(float delta) {
        stateTime += delta;
        position = decal.getPosition();

        if(!isCollected) {
            if(decal.getPosition().z < 0.1f && decal.getPosition().z > -1f && position.dst(player.position) < 1.7f) {
                isCollected = true;

                if(GameScreen.global_Multiplier < 5f) {
                    GameScreen.global_Multiplier += 2.35f;
                }
                if(GameScreen.global_Multiplier > 5f) GameScreen.global_Multiplier = 5f;

                Assets.sound_lifering_collected.play();

                decal.setPosition(position.x, position.y,0.1f);
            }
        }
        else {
            decal.setScale(decal.getScaleX() + delta * stateTime / 4f);

            direction = player.position.cpy().sub(position);
            velocity.x = direction.nor().x * SPEED * Math.abs(direction.x);
            velocity.y = direction.nor().y * SPEED * Math.abs(direction.y);

            decal.translateX(velocity.x * delta);
            decal.translateY(velocity.y * delta);
        }

        //Opacity
        opacity = stateTime < 1f ? stateTime : 1f;
        decal.setColor(1f,1f,1f, opacity);

        //Rotation
        if(!isCollected) rotation.z = delta * 50f;
        else rotation.z = delta * 70f * (stateTime / 2);
        decal.rotateZ(rotation.z);

        //Movement Z
        velocity.z = (GameScreen.global_Speed - GameScreen.global_Multiplier * 3f) * delta;
        if(!isCollected || decal.getScaleX() > 1.4f) decal.translateZ(velocity.z);
    }
}
