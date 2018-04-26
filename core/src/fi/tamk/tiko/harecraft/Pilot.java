package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import static fi.tamk.tiko.harecraft.GameMain.camera;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.file;
import static fi.tamk.tiko.harecraft.GameScreen.file2;
import static fi.tamk.tiko.harecraft.GameScreen.file3;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.GameScreen.gameStateTime;
import static fi.tamk.tiko.harecraft.GameScreen.global_Multiplier;
import static fi.tamk.tiko.harecraft.GameScreen.global_Speed;
import static fi.tamk.tiko.harecraft.GameScreen.renderCount;
import static fi.tamk.tiko.harecraft.GameScreen.strFlightRecord;
import static fi.tamk.tiko.harecraft.World.player;
import static fi.tamk.tiko.harecraft.WorldBuilder.spawnDistance;

/**
 * Created by Mika on 28/02/2018.
 *
 * Pilot is an abstract parent class for Player and Opponents.
 */

public abstract class Pilot extends GameObject {
    Decal decal_head;
    Decal decal_wings;

    float distance;
    float acceleration;
    float speed;
    float drawDistance;
    boolean isDrawing;

    static final int COLOR_RED = 0;
    static final int COLOR_BLUE = 1;
    static final int COLOR_ORANGE = 2;
    static final int COLOR_PINK = 3;

    static final int PLANE_1 = 1;
    static final int PLANE_2 = 2;
    static final int PLANE_3 = 3;

    static final int CHARACTER_DEF = 0;
    static final int CHARACTER_HARE = 1;

    public void update(float delta) {
        super.update(delta);

        //DISTANCE
        if(this instanceof Opponent) distance += -(global_Speed - speed) * delta;
        else distance += -velocity.z * delta;

        //DRAWING
        if(position.z < drawDistance) isDrawing = true;
        else if(opacity != 0f) isDrawing = false;
        if(this instanceof Opponent && gameState == END) isDrawing = false;

        //OPACITY
        if(isDrawing) opacity += this instanceof Opponent ? delta : delta * 0.4f;
        else opacity -= delta;
        if(opacity > 1f) opacity = 1f;
        else if(opacity < 0f) opacity = 0f;
        decal.setColor(1f,1f,1f, opacity);
        decal_wings.setColor(1f,1f,1f, opacity);
        decal_head.setColor(1f,1f,1f, opacity);
    }

    @Override
    public void updateParticles(float delta) {}

    public void dispose() {}
}

/**
 * Created by Mika on 28/02/2018.
 *
 * Player Class.
 */

class Player extends Pilot {
    //REFERENCE AMOUNTS
    //Desktop = -2f
    //Tablet handheld = 4f
    //Tablet chair = 1f
    static float ACCEL_Y_OFFSET = -20f;
    final float SPEED = 15f;
    float accelerationZ;
    float []rotationsArray;
    float destdist;
    float avarageY;
    float sumY;
    float countY;
    Vector3 keyboardDestination;
    Vector3 destination;
    Vector3 curPosition;
    Vector3 projPosition;

    TextureRegion texR_body = Assets.texR_plane_2_red_body;
    TextureRegion texR_wings = Assets.texR_plane_2_red_wings;
    TextureRegion texR_head = Assets.texR_character_hare_head;

    ParticleEffect pfx_scarf;

    public Player(float x, float y, float z) {
        destination = new Vector3();
        keyboardDestination = new Vector3();
        curPosition = new Vector3();
        rotationsArray = new float[10];
        pfx_scarf = new ParticleEffect(Assets.pfx_scarf);

        //strFlightRecord.intern();

        drawDistance = spawnDistance/50f;
        speed = SPEED;
        acceleration = 1f;
        accelerationZ = 1f;
        rotation.z = (MathUtils.random(0,1) == 0) ? -45f : 45f;

        width = texR_body.getRegionWidth()/250f;
        height = texR_body.getRegionHeight()/250f;
        decal = Decal.newDecal(width, height, texR_body,true);
        decal.setPosition(x,y,z);

        width = texR_head.getRegionWidth()/250f;
        height = texR_head.getRegionHeight()/250f;
        decal_head = Decal.newDecal(width, height, texR_head,true);
        decal_head.setPosition(x,y,z + 0.1f);

        width = texR_wings.getRegionWidth()/250f;
        height = texR_wings.getRegionHeight()/250f;
        decal_wings = Decal.newDecal(width, height, texR_wings,true);
        decal_wings.setPosition(x,y,z + 0.2f);

        decal_head.rotateY(-90f);
    }

    public void update(float delta, float accelX, float accelY) {
        super.update(delta);

        if(gameState == START) {
            sumY += accelY;
            countY++;
        }

        if(gameState != START && gameState != END) {
            //destination.x = accelX * -5f;
            //destination.y = (accelY - ACCEL_Y_OFFSET) * -5f;
            accelX = accelX * ProfileInfo.selectedSensitivity;
            accelY = accelY * ProfileInfo.selectedSensitivity;
            destination.x = accelX * -5f * (960/SCREEN_WIDTH);
            destination.y = (accelY - ACCEL_Y_OFFSET) * -5f * (960/SCREEN_WIDTH);
            //destination.x = destination.x * ProfileInfo.selectedSensitivity;
            //destination.y = destination.y * ProfileInfo.selectedSensitivity;
            destination = destination.add(keyboardDestination);

            destdist = 1f + destination.dst(0f, 6f, 0f)/100;   //100=1-1.3   reunanopeuden nollapiste +6y
            destination.y += 6f;                 //pelaajan default postionia korkeammalle +6y
            destination.x *= destdist;
            destination.y *= destdist;

            curPosition.x = decal.getX();
            curPosition.y = decal.getY();
            direction = destination.sub(curPosition);
            direction.x /= 60f;
            direction.y /= 60f;
            decal.translate(direction);

            velocity.x = direction.x * 20f;
            velocity.y = direction.y * 20f;

            for (int i = 9; i > 0; i--) {       //Rotation
                rotationsArray[i] = rotationsArray[i - 1];
            }
            rotationsArray[0] = direction.x;
            decal.setRotationZ(getRotationAverage() * -15);

            checkInput(); //Keyboard input

            //RECORD
            /*
                StringBuilder value = new StringBuilder(strFlightRecord);
                value.append(direction.x).append(",").append(direction.y).append(",").append(getRotationAverage() * -15).append("\n");
                strFlightRecord = value.toString();
            */
        }
        else if(gameState == START) {
            decal.setRotationZ(rotation.z);
            acceleration -= delta * 0.5f;
            accelerationZ += stateTime * 0.001f;
            if(acceleration > 0f) {
                decal.translateY(-velocity.z/2.5f * delta * acceleration/1.3f);
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
            acceleration += delta*2f;
            if(gameStateTime < 2.5f) {
                decal.translateZ(-velocity.z/10f * delta * (acceleration*2.5f));
                decal.translateY(-velocity.z/6f * delta * (acceleration/1.5f));
            }
        }

        decal_head.setRotation(decal.getRotation().cpy().exp(2f));
        decal_head.setPosition(decal.getPosition().x, decal.getPosition().y, decal.getPosition().z + 0.07f);
        decal_wings.setRotation(decal.getRotation());
        decal_wings.setPosition(decal.getPosition().x, decal.getPosition().y, decal.getPosition().z + 0.14f);

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

        projPosition = camera.project(curPosition.cpy());
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

    @Override
    public void updateParticles(float delta) {
        pfx_scarf.setPosition(
                projPosition.x,
                projPosition.y - 10f);

        pfx_scarf.getEmitters().get(0).getXScale().setHigh(velocity.x*5f);
        pfx_scarf.getEmitters().get(1).getXScale().setHigh(velocity.x*5f);

        pfx_scarf.update(delta);
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
    }
}

/**
 * Created by Mika on 28/02/2018.
 *
 * Opponent Class.
 */

class Opponent extends Pilot {
    float spawnZ;
    Decal decal_playerTag;
    String flight;
    String[] input;
    String[] line;
    float xDir;
    float yDir;
    float rotZ;
    int count;

    public Opponent(float x, float y, float z, float spawnZ, int color, int planetype, int character, float speed) {
        drawDistance = spawnDistance / 5f;
        this.spawnZ = spawnZ;
        this.speed = speed;

        switch(MathUtils.random(0,2)) {
            case 0:
                flight = file.readString();
                break;
            case 1:
                flight = file2.readString();
                break;
            case 2:
                flight = file3.readString();
                break;
        }

        input = flight.split("\n");

        TextureRegion texR_body;
        TextureRegion texR_wings;
        TextureRegion texR_head;

        switch (color) {
            case COLOR_RED:
                texR_body = Assets.texR_plane_2_red_body;
                texR_wings = Assets.texR_plane_2_red_wings;
                break;
            case COLOR_BLUE:
                texR_body = Assets.texR_plane_2_blue_body;
                texR_wings = Assets.texR_plane_2_blue_wings;
                break;
            case COLOR_ORANGE:
                texR_body = Assets.texR_plane_2_orange_body;
                texR_wings = Assets.texR_plane_2_orange_wings;
                break;
            case COLOR_PINK:
                texR_body = Assets.texR_plane_2_pink_body;
                texR_wings = Assets.texR_plane_2_pink_wings;
                break;
            default:
                texR_body = Assets.texR_plane_2_red_body;
                texR_wings = Assets.texR_plane_2_red_wings;
        }

        switch (character) {
            case CHARACTER_DEF:
                texR_head = Assets.texR_character_default_head;
                break;
            case CHARACTER_HARE:
                texR_head = Assets.texR_character_hare_head;
                break;
            default:
                texR_head = Assets.texR_character_default_head;
        }

        width = texR_body.getRegionWidth() / 100f;
        height = texR_body.getRegionHeight() / 100f;
        decal = Decal.newDecal(width, height, texR_body,true);
        decal.setPosition(x,y,z);

        width = texR_head.getRegionWidth() / 100f;
        height = texR_head.getRegionHeight() / 100f;
        decal_head = Decal.newDecal(width, height, texR_head,true);
        decal_head.setPosition(x,y,z+0.1f);

        width = texR_wings.getRegionWidth() / 100f;
        height = texR_wings.getRegionHeight() / 100f;
        decal_wings = Decal.newDecal(width, height, texR_wings,true);
        decal_wings.setPosition(x,y,z+0.2f);

        decal_playerTag = Decal.newDecal(Assets.texR_playertag.getRegionWidth()/70f,Assets.texR_playertag.getRegionHeight()/70f, Assets.texR_playertag,true);
    }

    public void update(float delta) {
        super.update(delta);

        decal_playerTag.setColor(1f,1f,1f, opacity);

        if(gameState == START) velocity.z = 9f * stateTime;
        else {
            velocity.z = speed - global_Multiplier * 3f;
            move(delta);
        }

        if(gameState == RACE && gameStateTime == 0f) {
            distance = player.distance + spawnZ;
            decal.setPosition(0f, -1.8f, decal.getPosition().z);
        }

        if(distance > World.end) {
            acceleration += delta * 2f;
            decal.translateZ(-(global_Speed - speed) * delta * acceleration);
            decal.translateY(-(global_Speed - speed) * delta * (acceleration / 10f));
        }
        else moveZ(delta);

        decal_head.setRotation(decal.getRotation().cpy().exp(2f));
        decal_head.setPosition(decal.getPosition().x, decal.getPosition().y,decal.getPosition().z + 0.04f);
        decal_wings.setRotation(decal.getRotation());
        decal_wings.setPosition(decal.getPosition().x, decal.getPosition().y,decal.getPosition().z + 0.08f);

        decal_playerTag.setPosition(decal.getPosition().x, decal.getPosition().y + 1.3f, decal.getPosition().z);
    }

    public void move(float delta) {
        //if(renderCount == 0) {
            line = input[count].split(",");
            xDir = Float.parseFloat(line[0]);
            yDir = Float.parseFloat(line[1]);
            rotZ = Float.parseFloat(line[2]);
        //}
        decal.translateX(xDir);
        decal.translateY(yDir);
        decal.setRotationZ(rotZ * 1.5f);


        count++;

        if(count == input.length - 1) count = 0;
    }

    public void dispose() {
        super.dispose();
    }
}
