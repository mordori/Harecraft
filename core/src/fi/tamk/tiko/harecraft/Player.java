package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.GameScreen.gameStateTime;
import static fi.tamk.tiko.harecraft.WorldBuilder.spawnDistance;

/**
 * Created by Mika on 23.2.2018.
 */

public class Player extends Pilot {
    //REFERENCE AMOUNTS
    //Desktop = -2f
    //Tablet handheld = 4f
    //Tablet chair = 1f
    static final float ACCEL_Y_OFFSET = -2f;
    final float SPEED = 15f;
    final float MAX_SPEED = 7f;
    float accelerationZ;
    float []rotationsArray;
    float destdist;
    Vector3 keyboardDestination;
    Vector3 destination;
    Vector3 curPosition;
    float posXTranspose;
    float posYTranspose;

    TextureRegion texR_body = Assets.texR_plane_2_red_body;
    TextureRegion texR_wings = Assets.texR_plane_2_red_wings;
    TextureRegion texR_head = Assets.texR_character_hare_head;

    ParticleEffect pfx_scarf;
    ParticleEffect pfx_stream;
    ParticleEffect pfx_stream2;

    public Player(float x, float y, float z) {
        destination = new Vector3();
        keyboardDestination = new Vector3();
        curPosition = new Vector3();
        rotationsArray = new float[10];
        pfx_scarf = new ParticleEffect(Assets.pfx_scarf);
        //pfx_stream = new ParticleEffect(Assets.pfx_stream);
        //pfx_stream2 = new ParticleEffect(Assets.pfx_stream);

        drawDistance = spawnDistance / 50f;
        speed = SPEED;
        acceleration = 1f;
        accelerationZ = 1f;
        rotation.z = (MathUtils.random(0,1) == 0) ? -45f : 45f;

        width = texR_body.getRegionWidth() / 100f;
        height = texR_body.getRegionHeight() / 100f;
        decal = Decal.newDecal(width, height, texR_body,true);
        decal.setPosition(x,y,z);

        width = texR_head.getRegionWidth() / 100f;
        height = texR_head.getRegionHeight() / 100f;
        decal_head = Decal.newDecal(width, height, texR_head,true);
        decal_head.setPosition(x,y,z + 0.1f);

        width = texR_wings.getRegionWidth() / 100f;
        height = texR_wings.getRegionHeight() / 100f;
        decal_wings = Decal.newDecal(width, height, texR_wings,true);
        decal_wings.setPosition(x,y,z + 0.2f);

        decal_head.rotateY(-90f);
    }

    public void update(float delta, float accelX, float accelY) {
        super.update(delta);

        if(gameState != START && gameState != END) {
            destination.x = accelX * -5f * (960 / (SCREEN_WIDTH * 100f));
            destination.y = (accelY - ACCEL_Y_OFFSET) * -5f * (960 / (SCREEN_WIDTH * 100f));
            destination = destination.add(keyboardDestination);

            destdist = 1f + destination.dst(0f,6f,0f) /100;   //100=1-1.3   reunanopeuden nollapiste +6y
            destination.y += 6f;                 //pelaajan default postionia korkeammalle +6y
            //Gdx.app.log("TAG", "dest dist: " +destdist);
            destination.x *= destdist;
            destination.y *= destdist;

            curPosition.x = decal.getX();
            curPosition.y = decal.getY();
            direction = destination.sub(curPosition);
            direction.x /= 60f;
            direction.y /= 60f;
            //direction.rotate(getRotationAverage(), 0f, 0f, 1f);
            decal.translate(direction);

            velocity.x = direction.x * 20f;
            velocity.y = direction.y * 20f;

            for (int i = 9; i > 0; i--) {       //Rotation
                rotationsArray[i] = rotationsArray[i - 1];
            }
            rotationsArray[0] = direction.x;
            //float keskiarvo = 0f;
            //for (int i = 0; i < 10; i++) {
            //    keskiarvo += rotationsArray[i];
            //}
            //decal.setRotationZ(-keskiarvo * 15f);   //10f oli alkuarvo
            decal.setRotationZ(getRotationAverage() * -15);

            checkInput(); //Keyboard input
        }
        else if(gameState == START) {
            decal.setRotationZ(rotation.z);
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
            if(gameStateTime < 2.5f) { //gameStateTime < 2.5f
                decal.translateZ(-velocity.z/10f * delta * (acceleration * 2.5f));
                decal.translateY(-velocity.z/6f * delta * (acceleration / 1.5f));
            }
        }

        decal_head.setRotation(decal.getRotation().cpy().exp(2f));
        decal_head.setPosition(decal.getPosition().x, decal.getPosition().y,decal.getPosition().z + 0.07f);
        decal_wings.setRotation(decal.getRotation());
        decal_wings.setPosition(decal.getPosition().x, decal.getPosition().y,decal.getPosition().z + 0.14f);

        //ANIMATION
        //decal_head.setTextureRegion((TextureRegion) Assets.animation_player_scarf.getKeyFrame(stateTime,true));
        /*if(getRotationAverage() >= 0f && Assets.animation_player_scarf.isFlipped) {
            Assets.flip(Assets.animation_player_scarf, Assets.animation_player_scarf.getKeyFrames().length);
            Assets.animation_player_scarf.isFlipped = false;
        }
        else if(getRotationAverage() < 0f && !Assets.animation_player_scarf.isFlipped) {
            Assets.flip(Assets.animation_player_scarf, Assets.animation_player_scarf.getKeyFrames().length);
            Assets.animation_player_scarf.isFlipped = true;
        }*/
        //decal_head.rotateY(getRotationAverage());

        updateParticles(delta);
    }

    public void checkInput() {
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            keyboardDestination.x = keyboardDestination.x + 0.2f;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            keyboardDestination.x = keyboardDestination.x - 0.2f;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            keyboardDestination.y = keyboardDestination.y +0.2f;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            keyboardDestination.y = keyboardDestination.y -0.2f;
        }
    }

    public void updateParticles(float delta) {
        pfx_scarf.setPosition(
                (-position.x * 31f + posTest() * 1.5f + velocity.x * Math.abs(position.y/posYTranspose * getRotationAverage())) * (SCREEN_WIDTH/12.80f) + SCREEN_WIDTH * 100f / 2f,
                (position.y * 15f + (velocity.x*position.x/2f) ) * (SCREEN_HEIGHT/7.20f) + SCREEN_HEIGHT * 100f / 2f + 12f);

        pfx_scarf.getEmitters().get(0).getXScale().setHigh(velocity.x * 5f);
        pfx_scarf.getEmitters().get(1).getXScale().setHigh(velocity.x * 5f);

        pfx_scarf.update(delta);

        /*pfx_stream.setPosition(
                (-position.x * 31f + test()*1.5f + velocity.x * Math.abs(position.y/test2 * getRotationAverage())) * (SCREEN_WIDTH/12.80f) + SCREEN_WIDTH * 100f / 2f - width*130f/2f,
                (position.y * 15f + (velocity.x*position.x/2f) ) * (SCREEN_HEIGHT/7.20f) + SCREEN_HEIGHT * 100f / 2f + 12f);
        pfx_stream.update(delta);

        pfx_stream2.setPosition(
                (-position.x * 31f + test()*1.5f + velocity.x * Math.abs(position.y/test2 * getRotationAverage())) * (SCREEN_WIDTH/12.80f) + SCREEN_WIDTH * 100f / 2f + width*140f/2f,
                (position.y * 15f + (velocity.x*position.x/2f) ) * (SCREEN_HEIGHT/7.20f) + SCREEN_HEIGHT * 100f / 2f + 12f);
        pfx_stream2.update(delta);*/
    }

    public float getRotationAverage() {
        float average = 0f;
        for (int i = 0; i < 10; i++) {
            average += rotationsArray[i];
        }
        return average;
    }

    public void dispose() {
        super.dispose();
        pfx_scarf.dispose();
        //pfx_stream.dispose();
        //pfx_stream2.dispose();
    }

    public float posTest() {
        if (Math.abs(position.x) < 10f) {
            posXTranspose = Math.abs(position.x);
        } else if (Math.abs(position.x) > 10f) {
            posXTranspose = 20f - Math.abs(position.x);
        }

        if(position.y >= 0f) {
            posYTranspose = 5f;
        }
        else posYTranspose = 20f + position.y;

        if(position.x > 0f) return posXTranspose;
        else return -posXTranspose;
    }
}
