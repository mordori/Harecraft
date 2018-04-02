package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.fieldOfView;
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
    Decal decal_arrows;
    float opacity_arrows;
    float stateTime_arrows;
    ParticleEffect pfx_speed_up;

    public Ring(float x, float y, float z) {
        width = Assets.texR_ring.getRegionWidth() / 100f;
        height = Assets.texR_ring.getRegionHeight() / 100f;
        width *= 8.5f;
        height *= 8.5f;
        decal = Decal.newDecal(width, height, Assets.texR_ring, true);
        decal.setPosition(x,y,z);

        decal_arrows = Decal.newDecal(width, height, Assets.texR_ring_arrows, true);
        decal_arrows.setPosition(x,y,z);
        decal_arrows.setScale(1.5f);

        pfx_speed_up = new ParticleEffect(Assets.pfx_speed_up);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        stateTime_arrows += delta;

        if(!isCollected) {
            if(decal.getPosition().z < 0.5f && decal.getPosition().z > -1.5f && position.dst(player.position) < 2.5f) {
                isCollected = true;

                if(global_Multiplier < MULTIPLIER_HIGH) {global_Multiplier += MULTIPLIER_INCREMENT;}
                if(global_Multiplier > MULTIPLIER_HIGH) global_Multiplier = MULTIPLIER_HIGH;

                Assets.sound_ring_collected.play();
                decal.setPosition(position.x, position.y,0.5f);
                decal_arrows.setPosition(position.x, position.y,0.5f);

                pfx_speed_up.start();
                pfx_speed_up.setPosition(SCREEN_WIDTH/2f - position.x * 31f, SCREEN_HEIGHT/2f + position.y * 15f);
            }
        }
        else {
            decal.setScale(decal.getScaleX() + delta * stateTime / 4f);

            direction = player.position.cpy().sub(position);
            velocity.x = direction.nor().x * COLLECTED_SPEED * Math.abs(direction.x);
            velocity.y = direction.nor().y * COLLECTED_SPEED * Math.abs(direction.y);

            decal.translateX(velocity.x * delta);
            decal.translateY(velocity.y * delta);
            decal_arrows.translateY(velocity.y * delta);
            decal_arrows.translateY(velocity.y * delta);

            increaseFOV(delta);
            updateParticles(delta);
        }

        decal_arrows.setScale(decal_arrows.getScaleX() - delta * stateTime_arrows/2f);

        if(decal_arrows.getScaleX() < 0.5f && position.z > 65f) {
            decal_arrows.setScale(1.5f);
            stateTime_arrows = 0f;
        }


        //Rotation
        if(!isCollected) rotation.z = delta * 50f;
        else rotation.z = delta * 70f * (stateTime / 2f);
        decal.rotateZ(rotation.z);
        decal_arrows.rotateZ(delta * 50f * (stateTime_arrows * 0.5f) + 0.51f);

        //Opacity
        setOpacity();
        if(decal_arrows.getScaleX() > 0.75f && position.z > 40f) {
            opacity_arrows = stateTime_arrows * 0.8f;
            if(opacity_arrows > 1f) opacity_arrows = 1f;
        }
        else if(decal_arrows.getScaleX() < 0.75f || position.z < 40f) {
            opacity_arrows -= delta * 2.5f;
            if(opacity_arrows < 0f) opacity_arrows = 0f;
        }
        decal_arrows.setColor(1f,1f,1f, opacity_arrows);

        //Movement Z
        if(!isCollected || decal.getScaleX() > 1.4f) {
            moveZ(delta);
            decal_arrows.translateZ(velocity.z * delta);
        }
    }

    public void increaseFOV(float delta) {
        fieldOfView += delta * 35f / stateTime;
        if(fieldOfView > 55f) fieldOfView = 55f;
    }

    public void updateParticles(float delta) {
        pfx_speed_up.update(delta);
    }

    public void dispose() {
        pfx_speed_up.dispose();
    }
}
