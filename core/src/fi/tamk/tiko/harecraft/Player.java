package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Mika on 23.2.2018.
 */

public class Player extends GameObject {

    enum State {
        NORMAL, HIT_BOUNDS
    }

    static final float ACCEL_Y_OFFSET = 5f;

    float width = Assets.texR_player.getRegionWidth()/100f;
    float height = Assets.texR_player.getRegionHeight()/100f;
    State state = State.NORMAL;

    final float SPEED = 15f;
    final float MAX_SPEED = 5f;

    public Player(float x, float y, float z) {

        decal = Decal.newDecal(width,height,Assets.texR_player, true);
        decal.setPosition(x,y,z);

        velocity = new Vector3();
        position = new Vector3();
    }

    public void update(float delta, float accelX, float accelY) {
        stateTime += delta;
        position = decal.getPosition();

        if(decal.getPosition().x >= GameScreen.WORLD_WIDTH) velocity.x = 3f;
        else if(decal.getPosition().x <= -GameScreen.WORLD_WIDTH) velocity.x = -3f;

        if(decal.getPosition().y >= GameScreen.WORLD_WIDTH) {
            velocity.y = -3f;
        } else if(decal.getPosition().y <= -GameScreen.WORLD_WIDTH) {
            velocity.y = 3f;
        }

        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            velocity.x = accelX * 1.5f;
            velocity.y = (accelY - ACCEL_Y_OFFSET) * 1.5f;
            rotation = velocity.x * 5f;
        } else {
            checkInput(delta);
            rotation = velocity.x * 15f;
        }

        decal.setRotationZ(rotation);

        if(decal.getPosition().x < 12.8f && velocity.x < 0f || decal.getPosition().x > -12.8f && velocity.x > 0f) {
            decal.translateX(-velocity.x * delta * Math.abs(decal.getRotation().z) * 2f);
        }

        if(decal.getPosition().y < 7.2f && velocity.y > 0f || decal.getPosition().y > -7.2f && velocity.y < 0f)
        decal.translateY(velocity.y * delta);

        if(velocity.y != 0 && Math.abs(velocity.y) > 0f) {
            velocity.y -= Math.abs(velocity.y)/velocity.y * 0.05f;
            if(Math.abs(velocity.y) < 0.05f) velocity.y = 0f;
        }

        if(velocity.x != 0 && Math.abs(velocity.x) > 0f) {
            velocity.x -= Math.abs(velocity.x)/velocity.x * 0.05f;
            if(Math.abs(velocity.x) < 0.05f) velocity.x = 0f;
        }
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
}
