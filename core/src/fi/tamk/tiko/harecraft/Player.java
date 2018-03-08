package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.FINISH;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.GameScreen.gameStateTime;
import static fi.tamk.tiko.harecraft.GameScreen.global_Multiplier;
import static fi.tamk.tiko.harecraft.GameScreen.global_Speed;
import static fi.tamk.tiko.harecraft.World.WORLD_HEIGHT_DOWN;
import static fi.tamk.tiko.harecraft.World.WORLD_HEIGHT_UP;
import static fi.tamk.tiko.harecraft.World.WORLD_WIDTH;
import static fi.tamk.tiko.harecraft.WorldBuilder.spawnDistance;

/**
 * Created by Mika on 23.2.2018.
 */

public class Player extends Pilot {
    static final float ACCEL_Y_OFFSET = 1f;
    final float SPEED = 15f;
    final float MAX_SPEED = 7f;
    float accelerationZ;
    float []rotationsArray;
    Vector3 keyboardDestination;
    Vector3 destination;
    Vector3 curPosition;

    public Player(float x, float y, float z) {
        velocity = new Vector3();
        position = new Vector3();
        rotation = new Vector3();
        direction = new Vector3();

        destination = new Vector3();
        curPosition = new Vector3();

        rotationsArray = new float[]{0f,0f,0f,0f,0f,0f,0f,0f,0f,0f};
        keyboardDestination = new Vector3 (0f,0f,0f);


        width = Assets.texR_player.getRegionWidth()/100f;
        height = Assets.texR_player.getRegionHeight()/100f;

        decal = Decal.newDecal(width,height,Assets.texR_player, true);
        decal.setPosition(x,y,z);

        pfx_scarf = new ParticleEffect(Assets.pfx_scarf);
        speed = SPEED;
        drawDistance = spawnDistance / 50f;

        acceleration = 1f;
        accelerationZ = 1f;
        rotation.z = (MathUtils.random(0,1) == 0) ? -70f : 70f;

        //DECAL TEXTURE SCROLLING
        /*decal.getVertices()[decal.U1] = width/2.5f;
        decal.getVertices()[decal.U3] = width/2.5f;
        decal.getVertices()[decal.U2] = width/1.22f;
        decal.getVertices()[decal.U4] = width/1.22F;*/
    }

    public void update(float delta, float accelX, float accelY) {
        super.update(delta);

        //Mikon kontrolit alkaa
        if(gameState != START && gameState != END) {
            destination.x = accelX * -2.5f;
            destination.y = accelY - ACCEL_Y_OFFSET * -2f;
            destination = destination.add(keyboardDestination);
            curPosition.x = decal.getX();
            curPosition.y = decal.getY();
            direction = destination.sub(curPosition);
            direction.x = direction.x / 20;
            direction.y = direction.y / 20;
            decal.translate(direction);

            velocity.x = direction.x * 20f;
            velocity.y = direction.y * 20f;

            for (int i = 9; i > 0; i--) {       //Rotation
                rotationsArray[i] = rotationsArray[i - 1];
            }
            rotationsArray[0] = direction.x;
            float keskiarvo = 0;
            for (int i = 0; i < 10; i++) {
                keskiarvo += rotationsArray[i];
            }
            decal.setRotationZ(-keskiarvo * 10);

            checkInput(delta); //Keyboard
        }
        //Mikon kontrollit loppuu


        //DECAL TEXTURE SCROLLING
        /*decal.getVertices()[decal.U1] -=0.002f;
        decal.getVertices()[decal.U2] -=0.002f;
        decal.getVertices()[decal.U3] -=0.002f;
        decal.getVertices()[decal.U4] -=0.002f;

        if(decal.getVertices()[decal.U1] <= -0.01f) decal.getVertices()[decal.U1] = -0.01f;

        if(decal.getVertices()[decal.U1] == -0.01f) {
            decal.getVertices()[decal.U1] = width/3/2.5f;
            decal.getVertices()[decal.U2] = width/3/1.22f;
            decal.getVertices()[decal.U3] = width/3/2.5f;
            decal.getVertices()[decal.U4] = width/3/1.22f;
        }*/

        /*
        if(gameState != END && gameState != START) {
            if (decal.getPosition().x >= WORLD_WIDTH) velocity.x = 3f;
            else if (decal.getPosition().x <= -WORLD_WIDTH) velocity.x = -3f;

            if (decal.getPosition().y >= WORLD_HEIGHT_UP) {
                velocity.y = -3f;
            } else if (decal.getPosition().y <= -WORLD_HEIGHT_DOWN) {
                velocity.y = 3f;
            }
        }

        if(gameState == RACE || gameState == FINISH) {
            if (Gdx.app.getType() == Application.ApplicationType.Android) {
                velocity.x = accelX * 2f;
                velocity.y = (accelY - ACCEL_Y_OFFSET) * 2f;
                rotation.z = velocity.x * 5f;
            } else {
                checkInput(delta);
                rotation.z = velocity.x * 15f;
            }
        }

        if(gameState == END) rotation.z = velocity.x * 15f;

        decal.setRotationZ(rotation.z);

        if(gameState != START && (decal.getPosition().x < WORLD_WIDTH && velocity.x < 0f || decal.getPosition().x > -WORLD_WIDTH && velocity.x > 0f))
            decal.translateX(-velocity.x * delta);


        if(gameState != START && (decal.getPosition().y < WORLD_HEIGHT_UP && velocity.y > 0f || decal.getPosition().y > -WORLD_HEIGHT_DOWN && velocity.y < 0f))
            decal.translateY(velocity.y * delta);

        if(velocity.y != 0 && Math.abs(velocity.y) > 0f) {
            velocity.y -= Math.abs(velocity.y)/velocity.y * 0.05f;
            if(Math.abs(velocity.y) < 0.05f) velocity.y = 0f;
        }

        if(velocity.x != 0 && Math.abs(velocity.x) > 0f) {
            velocity.x -= Math.abs(velocity.x)/velocity.x * 0.05f;
            if(Math.abs(velocity.x) < 0.05f) velocity.x = 0f;
        } */

        if(gameState == START) {
            acceleration -= delta * 0.5f;
            accelerationZ += stateTime * 0.001f;
            if(acceleration > 0f) {
                decal.translateY(-velocity.z/2.5f * delta * acceleration / 1.3f);
            }
            if(rotation.z != 0f && accelerationZ > 1f) {
                rotation.z -= Math.abs(rotation.z)/rotation.z / accelerationZ * MathUtils.random(1f, 2f);
            }
            if(Math.abs(rotation.z)/rotation.z > 0f && rotation.z < 0f) rotation.z = 0f;
            if(Math.abs(rotation.z)/rotation.z < 0f && rotation.z > 0f) rotation.z = 0f;

            if(position.z < 0f) decal.translateZ(-velocity.z/20f * delta / accelerationZ);
            else decal.setPosition(decal.getX(), decal.getY(), 0f);
        }

        if(gameState == END) {
            acceleration += delta * 2f;
            if(gameStateTime < 2.5f) {
                decal.translateZ(-velocity.z/10f * delta * (acceleration * 2.5f));
                decal.translateY(-velocity.z/6f * delta * (acceleration / 1.5f));
            }
        }
    }

    public void checkInput(float delta) {
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            //if(velocity.x > -MAX_SPEED) {
            //    velocity.x -= SPEED * delta;
            //    if(velocity.x <= -MAX_SPEED) velocity.x = -MAX_SPEED;
            //}
            keyboardDestination.x = keyboardDestination.x + 0.2f;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            //if(velocity.x < MAX_SPEED) {
            //    velocity.x += SPEED * delta;
            //    if(velocity.x >= MAX_SPEED) velocity.x = MAX_SPEED;
            //}
            keyboardDestination.x = keyboardDestination.x - 0.2f;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            //if(velocity.y < MAX_SPEED) {
            //    velocity.y += SPEED * delta;
            //    if(velocity.y >= MAX_SPEED) velocity.y = MAX_SPEED;
            //}
            keyboardDestination.y = keyboardDestination.y +0.2f;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            //if(velocity.y > -MAX_SPEED) {
             //   velocity.y -= SPEED * delta;
            //    if(velocity.y <= -MAX_SPEED) velocity.y = -MAX_SPEED;
            //}
            keyboardDestination.y = keyboardDestination.y -0.2f;
        }
    }

    public void dispose() {
        super.dispose();
    }
}
