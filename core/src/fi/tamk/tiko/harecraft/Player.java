package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.World.WORLD_HEIGHT_DOWN;
import static fi.tamk.tiko.harecraft.World.WORLD_HEIGHT_UP;
import static fi.tamk.tiko.harecraft.World.WORLD_WIDTH;

/**
 * Created by Mika on 23.2.2018.
 */

public class Player extends Pilot {
    static final float ACCEL_Y_OFFSET = 5f;
    float width = Assets.texR_player.getRegionWidth()/100f;
    float height = Assets.texR_player.getRegionHeight()/100f;
    final float SPEED = 15f;
    final float MAX_SPEED = 7f;

    public Player(float x, float y, float z) {
        velocity = new Vector3();
        position = new Vector3();
        rotation = new Vector3();

        decal = Decal.newDecal(width,height,Assets.texR_player, true);
        decal.setPosition(x,y,z);

        pfx_scarf = new ParticleEffect(Assets.pfx_scarf);
    }

    public void update(float delta, float accelX, float accelY) {
        stateTime += delta;
        position = decal.getPosition();

        if(decal.getPosition().x >= WORLD_WIDTH) velocity.x = 3f;
        else if(decal.getPosition().x <= -WORLD_WIDTH) velocity.x = -3f;

        if(decal.getPosition().y >= WORLD_HEIGHT_UP) {
            velocity.y = -3f;
        } else if(decal.getPosition().y <= -WORLD_HEIGHT_DOWN) {
            velocity.y = 3f;
        }

        if(gameState == RACE) {
            if (Gdx.app.getType() == Application.ApplicationType.Android) {
                velocity.x = accelX * 2f;
                velocity.y = (accelY - ACCEL_Y_OFFSET) * 2f;
                rotation.z = velocity.x * 5f;
            } else {
                checkInput(delta);
                rotation.z = velocity.x * 15f;
            }
        }

        decal.setRotationZ(rotation.z);

        if(decal.getPosition().x < WORLD_WIDTH && velocity.x < 0f || decal.getPosition().x > -WORLD_WIDTH && velocity.x > 0f) {
            decal.translateX(-velocity.x * delta);
        }

        if(decal.getPosition().y < WORLD_HEIGHT_UP && velocity.y > 0f || decal.getPosition().y > -WORLD_HEIGHT_DOWN && velocity.y < 0f)
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
        pfx_scarf.setPosition(
                -position.x * 31.5f / 1.05f + SCREEN_WIDTH * 100f /2f,
                position.y * 31.5f / 1.15f + SCREEN_HEIGHT * 100f/2f + 10f);

        pfx_scarf.getEmitters().get(0).getYScale().setHigh(velocity.x  * 6f);
        pfx_scarf.getEmitters().get(1).getYScale().setHigh(velocity.x  * 5f);

        pfx_scarf.update(delta);
    }

    public void dispose() {
        super.dispose();
    }
}
