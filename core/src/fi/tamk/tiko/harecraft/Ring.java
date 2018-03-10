package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import static fi.tamk.tiko.harecraft.GameScreen.global_Multiplier;
import static fi.tamk.tiko.harecraft.GameScreen.global_Speed;
import static fi.tamk.tiko.harecraft.World.player;

/**
 * Created by Mika on 26/02/2018.
 */

public class Ring extends GameObject {
    final float COLLECTED_SPEED = 10f;
    final float MULTIPLIER_HIGH = 5.5f;
    final float MULTIPLIER_INCREMENT = 2.4f;
    boolean isCollected = false;

    public Ring(float x, float y, float z) {
        position = new Vector3();
        velocity = new Vector3();
        direction = new Vector3();
        rotation = new Vector3();

        width = Assets.texR_ring.getRegionWidth() / 100f;
        height = Assets.texR_ring.getRegionHeight() / 100f;
        width *= 8.5f;
        height *= 8.5f;

        decal = Decal.newDecal(width, height, Assets.texR_ring, true);
        decal.setPosition(x,y,z);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if(!isCollected) {
            if(decal.getPosition().z < 0.5f && decal.getPosition().z > -1.5f && position.dst(player.position) < 1.85f) {
                isCollected = true;

                if(global_Multiplier < MULTIPLIER_HIGH) {global_Multiplier += MULTIPLIER_INCREMENT;}
                if(global_Multiplier > MULTIPLIER_HIGH) global_Multiplier = MULTIPLIER_HIGH;

                Assets.sound_ring_collected.play();
                decal.setPosition(position.x, position.y,0.1f);
            }
        }
        else {
            decal.setScale(decal.getScaleX() + delta * stateTime / 4f);

            direction = player.position.cpy().sub(position);
            velocity.x = direction.nor().x * COLLECTED_SPEED * Math.abs(direction.x);
            velocity.y = direction.nor().y * COLLECTED_SPEED * Math.abs(direction.y);

            decal.translateX(velocity.x * delta);
            decal.translateY(velocity.y * delta);
        }

        //Rotation
        if(!isCollected) rotation.z = delta * 50f;
        else rotation.z = delta * 70f * (stateTime / 2);
        decal.rotateZ(rotation.z);

        //Opacity
        setOpacity();

        //Movement Z
        if(!isCollected || decal.getScaleX() > 1.4f) moveZ(delta);
    }
}
