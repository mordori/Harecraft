package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Mika on 23.2.2018.
 */

public class Player extends Pilot {

    static final float ACCEL_Y_OFFSET = 5f;

    float width = Assets.texR_player.getRegionWidth()/100f;
    float height = Assets.texR_player.getRegionHeight()/100f;

    final float SPEED = 15f;
    final float MAX_SPEED = 5f;

    public Player(float x, float y, float z) {

        decal = Decal.newDecal(width,height,Assets.texR_player, true);
        decal.setPosition(x,y,z);

        velocity = new Vector3();
        position = new Vector3();
        rotation = new Vector3();

        pfx_scarf = new ParticleEffect(Assets.pfx_scarf);
    }

    public void update(float delta, float accelX, float accelY) {
        stateTime += delta;
        position = decal.getPosition();

        if(decal.getPosition().x >= GameScreen.WORLD_WIDTH) velocity.x = 3f;
        else if(decal.getPosition().x <= -GameScreen.WORLD_WIDTH) velocity.x = -3f;

        if(decal.getPosition().y >= GameScreen.WORLD_HEIGHT * 1.5f) {
            velocity.y = -3f;
        } else if(decal.getPosition().y <= -GameScreen.WORLD_HEIGHT * 2f) {
            velocity.y = 3f;
        }

        if(GameScreen.state == GameScreen.State.RACE) {
            if (Gdx.app.getType() == Application.ApplicationType.Android) {
                velocity.x = accelX * 1.5f;
                velocity.y = (accelY - ACCEL_Y_OFFSET) * 1.5f;
                rotation.z = velocity.x * 5f;
            } else {
                checkInput(delta);
                rotation.z = velocity.x * 15f;
            }
        }

        decal.setRotationZ(rotation.z);

        if(decal.getPosition().x < GameScreen.WORLD_WIDTH && velocity.x < 0f || decal.getPosition().x > -GameScreen.WORLD_WIDTH && velocity.x > 0f) {
            decal.translateX(-velocity.x * delta * Math.abs(decal.getRotation().z) * 2f);
        }

        if(decal.getPosition().y < GameScreen.WORLD_HEIGHT * 1.5f && velocity.y > 0f || decal.getPosition().y > -GameScreen.WORLD_HEIGHT * 2f && velocity.y < 0f)
        decal.translateY(velocity.y * delta);

        if(velocity.y != 0 && Math.abs(velocity.y) > 0f) {
            velocity.y -= Math.abs(velocity.y)/velocity.y * 0.05f;
            if(Math.abs(velocity.y) < 0.05f) velocity.y = 0f;
        }

        if(velocity.x != 0 && Math.abs(velocity.x) > 0f) {
            velocity.x -= Math.abs(velocity.x)/velocity.x * 0.05f;
            if(Math.abs(velocity.x) < 0.05f) velocity.x = 0f;
        }

        distance += -(GameScreen.global_Speed - GameScreen.global_Multiplier * 3f) * delta;

        updateParticles(delta);
    }

    public void checkInput(float delta) {
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if(velocity.x > -MAX_SPEED) {
                velocity.x -= SPEED * delta;
                if(velocity.x <= -MAX_SPEED) velocity.x = -MAX_SPEED;
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if(velocity.x < MAX_SPEED) {
                velocity.x += SPEED * delta;
                if(velocity.x >= MAX_SPEED) velocity.x = MAX_SPEED;
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if(velocity.y < MAX_SPEED) {
                velocity.y += SPEED * delta;
                if(velocity.y >= MAX_SPEED) velocity.y = MAX_SPEED;
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if(velocity.y > -MAX_SPEED) {
                velocity.y -= SPEED * delta;
                if(velocity.y <= -MAX_SPEED) velocity.y = -MAX_SPEED;
            }
        }
    }

    public void updateParticles(float delta) {
        /*float dir = 0;
        if(rotation.z != 0) dir = Math.abs(rotation.z)/rotation.z;
            pfx_windLeft.setPosition(
                    -position.x * 31.5f + GameScreen.WORLD_WIDTH * 100f / 2f - width * 80f ,
                    position.y * 31.5f + GameScreen.WORLD_HEIGHT * 100f / 2f);
            pfx_windRight.setPosition(
                    -position.x * 31.5f + GameScreen.WORLD_WIDTH * 100f / 2f + width * 80f,
                    position.y * 31.5f + GameScreen.WORLD_HEIGHT * 100f / 2f);
*/
        /*if(dir < 0) {
            pfx_windLeft.setPosition(
                    -position.x * 31.5f + GameScreen.WORLD_WIDTH * 100f / 2f - width * 80f + rotation.z * dir,
                    position.y * 31.5f + GameScreen.WORLD_HEIGHT * 100f / 2f + rotation.z * -dir * 1.1f);
            pfx_windRight.setPosition(
                    -position.x * 31.5f + GameScreen.WORLD_WIDTH * 100f / 2f + width * 80f + rotation.z * -dir,
                    position.y * 31.5f + GameScreen.WORLD_HEIGHT * 100f / 2f + rotation.z * dir * 1.1f);
        }
        else {
            pfx_windLeft.setPosition(
                    -position.x*31.5f + GameScreen.WORLD_WIDTH * 100f / 2f - width * 80f + rotation.z * dir,
                    position.y*31.5f + GameScreen.WORLD_HEIGHT * 100f / 2f + rotation.z * dir * 1.1f);
            pfx_windRight.setPosition(
                    -position.x*31.5f + GameScreen.WORLD_WIDTH * 100f / 2f + width * 80f + rotation.z * -dir,
                    position.y*31.5f + GameScreen.WORLD_HEIGHT * 100f / 2f + rotation.z * -dir * 1.1f);
        }*/

        pfx_scarf.setPosition(
                -position.x * 31.5f / 1.05f + GameScreen.WORLD_WIDTH * 100f /2f,
                position.y * 31.5f / 1.15f + GameScreen.WORLD_HEIGHT * 100f/2f + 10f);

        pfx_scarf.getEmitters().get(0).getYScale().setHigh(velocity.x  * 6f);
        pfx_scarf.getEmitters().get(1).getYScale().setHigh(velocity.x  * 5f);

        //pfx_scarf.getEmitters().get(0).getXScale().setHigh(velocity.y * 6f);
        //pfx_scarf.getEmitters().get(1).getXScale().setHigh(velocity.y * 5f);



        /*ParticleEmitter.ScaledNumericValue angle;
        float range;

        for(ParticleEmitter em : pfx_windLeft.getEmitters()) {
            angle = em.getAngle();
            range = angle.getHighMax() - angle.getHighMin();

            em.getAngle().setHigh(new Vector2(velocity.x, velocity.y).angle() - range / 2.0f, velocity.angle() + range / 2.0f);
        }
        for(ParticleEmitter em : pfx_windRight.getEmitters()) {
            angle = em.getAngle();
            range = angle.getHighMax() - angle.getHighMin();

            em.getAngle().setHigh(new Vector2(velocity.x, velocity.y).angle() - range / 2.0f, velocity.angle() + range / 2.0f);
        }*/

        pfx_scarf.update(delta);
    }

    public void dispose() {
        super.dispose();
    }
}
