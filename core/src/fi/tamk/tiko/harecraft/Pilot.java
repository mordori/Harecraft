package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import static fi.tamk.tiko.harecraft.GameMain.camera;
import static fi.tamk.tiko.harecraft.GameScreen.DIFFICULTYSENSITIVITY;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.balloonsCollected;
import static fi.tamk.tiko.harecraft.GameScreen.flights;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.GameScreen.gameStateTime;
import static fi.tamk.tiko.harecraft.GameScreen.global_Multiplier;
import static fi.tamk.tiko.harecraft.GameScreen.global_Speed;
import static fi.tamk.tiko.harecraft.GameScreen.strFlightRecord;
import static fi.tamk.tiko.harecraft.World.player;
import static fi.tamk.tiko.harecraft.World.finish;
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

    static final int HARE = 0;
    static final int WOLF = 1;
    static final int FOX = 2;
    static final int KOALA = 3;
    static final int GIRAFF = 4;
    static final int BEAR = 5;

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
        if(this instanceof Opponent && position.z < 0f) opacity = 0.3f;
        else if(isDrawing) opacity += this instanceof Opponent ? delta : delta * 0.4f;
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
    static float ACCEL_Y_OFFSET = 0f;
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
    Vector3 windPos;

    ParticleEffect pfx_scarf;
    static ParticleEffect pfx_wind_trail_left;
    static ParticleEffect pfx_wind_trail_right;
    float angle;
    float offsetX;
    float offsetY;
    float windYOffset;
    float windXOffset;

    public Player(float x, float y, float z) {
        destination = new Vector3();
        keyboardDestination = new Vector3();
        curPosition = new Vector3();
        rotationsArray = new float[10];
        pfx_scarf = new ParticleEffect(Assets.pfx_scarf);
        pfx_wind_trail_left = new ParticleEffect(Assets.pfx_wind_trail);
        pfx_wind_trail_right = new ParticleEffect(Assets.pfx_wind_trail);

        drawDistance = spawnDistance/50f;
        speed = SPEED;
        acceleration = 1f;
        accelerationZ = 1f;
        rotation.z = (MathUtils.random(0,1) == 0) ? -45f : 45f;

        TextureRegion texR_body = Assets.texR_plane_red_body;
        TextureRegion texR_wings = Assets.texR_plane_2_red_wings;
        TextureRegion texR_head = Assets.texR_character_hare;


        /*if(ProfileInfo.profilesData.getInteger(ProfileInfo.selectedPlayerProfile +"Score", 0) >= 4000) {
            texR_wings = Assets.texR_plane_1_red_wings;
        }
        else if(ProfileInfo.profilesData.getInteger(ProfileInfo.selectedPlayerProfile +"Score", 0) >= 2000) {
            texR_wings = Assets.texR_plane_3_red_wings;
        }*/

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

        /*if(ProfileInfo.profilesData.getInteger(ProfileInfo.selectedPlayerProfile +"Score", 0) >= 4000) {
            windYOffset = -decal.getHeight()/15f;
            windXOffset = decal.getWidth()/2.25f;
        }
        else if(ProfileInfo.profilesData.getInteger(ProfileInfo.selectedPlayerProfile +"Score", 0) >= 2000) {
            windYOffset = decal.getHeight()/20f;
            windXOffset = decal.getWidth()/2.75f;
        }
        else {
            windYOffset = 0f;
            windXOffset = decal.getWidth()/2.5f;
        }*/

        windYOffset = decal.getHeight()/6.55f;
        windXOffset = decal.getWidth()/2.1f;

        pfx_scarf.getEmitters().get(0).getYScale().setHigh(43f * (SCREEN_WIDTH/1280f));
        pfx_scarf.getEmitters().get(1).getYScale().setHigh(36f * (SCREEN_WIDTH/1280f));

        pfx_wind_trail_left.getEmitters().get(0).getXScale().setHigh(15f * (SCREEN_WIDTH/1920f));
        pfx_wind_trail_left.getEmitters().get(1).getXScale().setHigh(10f * (SCREEN_WIDTH/1920f));

        pfx_wind_trail_right.getEmitters().get(0).getXScale().setHigh(15f * (SCREEN_WIDTH/1920f));
        pfx_wind_trail_right.getEmitters().get(1).getXScale().setHigh(10f * (SCREEN_WIDTH/1920f));


        //pfx_wind_trail_left.allowCompletion();
        //pfx_wind_trail_right.allowCompletion();
    }

    public void update(float delta, float accelX, float accelY) {
        super.update(delta);

        if(gameState == START) {
            sumY += accelY;
            countY++;
        }

        if(gameState != START && gameState != END) {
            accelX = accelX * ProfileInfo.selectedSensitivity;
            accelY = accelY * ProfileInfo.selectedSensitivity;

            destination.x = accelX * -5f;
            destination.y = (accelY - ACCEL_Y_OFFSET) * -5f;

            if (ProfileInfo.invertY == false) { //INVERTOITU LENTO
                destination.y = (destination.y * -1) -ACCEL_Y_OFFSET -ACCEL_Y_OFFSET;
            }

            if (destination.x > 25f)
                destination.x = 25f;
            if (destination.x < -25f)
                destination.x = -25f;
            if (destination.y > 15f)
                destination.y = 15f;
            if (destination.y < -22f)
                destination.y = -22f;

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
            //recordFlight();
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
        decal_head.setPosition(decal.getPosition().x, decal.getPosition().y, decal.getPosition().z + 0.03f);
        decal_wings.setRotation(decal.getRotation());
        decal_wings.setPosition(decal.getPosition().x, decal.getPosition().y, decal.getPosition().z + 0.06f);


        /*if(player.velocity.z < -29.5f) {
            pfx_wind_trail_left.start();
            pfx_wind_trail_right.start();
        }
        else if(player.velocity.z >= -29.5f){
            pfx_wind_trail_left.allowCompletion();
            pfx_wind_trail_right.allowCompletion();
        }*/

        updateParticles(delta);
    }

    private void recordFlight() {
        StringBuilder value = new StringBuilder(strFlightRecord);
        value.append(direction.x).append(",").append(direction.y).append(",").append(getRotationAverage() * -15).append("\n");
        strFlightRecord = value.toString();
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
        windPos = curPosition.cpy();
        windPos.x -= windXOffset;
        windPos.y += windYOffset;
        angle = decal.getRotation().getAngleRad();
        offsetX = (float)((windPos.x - curPosition.x) * Math.cos(angle) - (windPos.y - curPosition.y) * Math.sin(angle) + curPosition.x);
        offsetY = (float)((windPos.x - curPosition.x) * Math.sin(angle) + (windPos.y - curPosition.y) * Math.cos(angle) + curPosition.y);
        windPos.x = offsetX;
        windPos.y = offsetY;
        pfx_wind_trail_right.getEmitters().first().getAngle().setHigh(-MathUtils.atan2(direction.y, direction.x)*(float)(180f/Math.PI));
        pfx_wind_trail_right.getEmitters().get(1).getAngle().setHigh(-MathUtils.atan2(direction.y, direction.x)*(float)(180f/Math.PI));

        System.out.println(decal.getRotation().getAngle());
        angle = decal.getRotation().getAngle();

        if(angle < 345f && angle > 300f) {
            //pfx_wind_trail_right.getEmitters().get(0).getTransparency().setHigh(1f);
            //pfx_wind_trail_right.getEmitters().get(1).getTransparency().setHigh(1f);
            pfx_wind_trail_right.start();
        }
        else {
            //pfx_wind_trail_right.getEmitters().get(0).getTransparency().setHigh(0f);
            //pfx_wind_trail_right.getEmitters().get(1).getTransparency().setHigh(0f);
            pfx_wind_trail_right.allowCompletion();
        }

        projPosition = camera.project(windPos);
        pfx_wind_trail_right.setPosition(
                projPosition.x,
                projPosition.y);
        pfx_wind_trail_right.update(delta);

        windPos = curPosition.cpy();
        windPos.x += windXOffset;
        windPos.y += windYOffset;
        angle = decal.getRotation().getAngleRad();
        offsetX = (float)((windPos.x - curPosition.x) * Math.cos(angle) - (windPos.y - curPosition.y) * Math.sin(angle) + curPosition.x);
        offsetY = (float)((windPos.x - curPosition.x) * Math.sin(angle) + (windPos.y - curPosition.y) * Math.cos(angle) + curPosition.y);
        windPos.x = offsetX;
        windPos.y = offsetY;
        pfx_wind_trail_left.getEmitters().first().getAngle().setHigh(-MathUtils.atan2(direction.y, direction.x)*(float)(180f/Math.PI));
        pfx_wind_trail_left.getEmitters().get(1).getAngle().setHigh(-MathUtils.atan2(direction.y, direction.x)*(float)(180f/Math.PI));


        angle = decal.getRotation().getAngle();


        if(angle > 15f && angle < 50f) {
            //pfx_wind_trail_left.getEmitters().get(0).getTransparency().setHigh(1f);
            //pfx_wind_trail_left.getEmitters().get(1).getTransparency().setHigh(1f);
            pfx_wind_trail_left.start();
        }
        else {
            //pfx_wind_trail_left.getEmitters().get(0).getTransparency().setHigh(0f);
            //pfx_wind_trail_left.getEmitters().get(1).getTransparency().setHigh(0f);
            pfx_wind_trail_left.allowCompletion();
        }


        projPosition = camera.project(windPos);
        pfx_wind_trail_left.setPosition(
                projPosition.x,
                projPosition.y);
        pfx_wind_trail_left.update(delta);


        projPosition = camera.project(curPosition.cpy());
        pfx_scarf.setPosition(
                projPosition.x,
                projPosition.y - 5f);
        pfx_scarf.getEmitters().get(0).getXScale().setHigh(velocity.x*5f * (SCREEN_WIDTH/1280f));
        pfx_scarf.getEmitters().get(1).getXScale().setHigh(velocity.x*5f * (SCREEN_WIDTH/1280f));
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
        pfx_wind_trail_right.dispose();
        pfx_wind_trail_left.dispose();
    }
}

/**
 * Created by Mika on 28/02/2018.
 *
 * Opponent Class.
 */

class Opponent extends Pilot {
    float spawnZ;
    String flight;
    String[] input;
    String[] line;
    float xDir;
    float yDir;
    float rotZ;
    int count;

    public Opponent(float z, int character) {
        drawDistance = 100f;

        float max = finish/4.3f;
        float min = finish/12f;

        spawnZ = MathUtils.random(min, max);
        speed = MathUtils.random(1f, 6f) + MathUtils.random(2f, 10.5f);

        if(spawnZ > finish/5f && speed > 11.2f) speed = 11.2f;
        else if(spawnZ > finish/5.5f && speed > 11.4f) speed = 11.4f;
        else if(spawnZ > finish/6f && speed > 11.5f) speed = 11.5f;
        else if(spawnZ > finish/6.5f && speed > 11.6f) speed = 11.6f;
        else if(spawnZ > finish/8f && speed > 12.75f) speed = 12.75f;
        else if(spawnZ > finish/10f && speed > 14f) speed = 14f;

        if(character == WOLF) {
            spawnZ = finish/4f;
            speed = 11.4f;
        }

        speed -= (5f/(1f+DIFFICULTYSENSITIVITY) * 0.9f)/(ProfileInfo.selectedDuration/1000f);
        if(speed < 3f) speed = 3f;

        System.out.println("SPAWN: " + spawnZ + ", SPEED: " + speed);

        int i = MathUtils.random(0, flights.size() - 1);
        flight = flights.get(i).readString();
        flights.remove(i);
        input = flight.split("\n");

        TextureRegion texR_head = Assets.texR_character_hare;
        TextureRegion texR_body = Assets.texR_plane_red_body;
        TextureRegion texR_wings = Assets.texR_plane_2_red_wings;

        switch (character) {
            case WOLF:
                texR_head = Assets.texR_character_wolf;
                texR_body = Assets.texR_plane_wolf_body;
                texR_wings = Assets.texR_plane_wolf_wings;
                break;
            case FOX:
                texR_head = Assets.texR_character_fox;
                texR_body = Assets.texR_plane_fox_body;
                texR_wings = Assets.texR_plane_fox_wings;
                break;
            case KOALA:
                texR_head = Assets.texR_character_koala;
                texR_body = Assets.texR_plane_koala_body;
                texR_wings = Assets.texR_plane_koala_wings;
                break;
            case GIRAFF:
                texR_head = Assets.texR_character_giraff;
                texR_body = Assets.texR_plane_giraff_body;
                texR_wings = Assets.texR_plane_giraff_wings;
                break;
            case BEAR:
                texR_head = Assets.texR_character_bear;
                texR_body = Assets.texR_plane_bear_body;
                texR_wings = Assets.texR_plane_bear_wings;
                break;
        }

        float x = MathUtils.random(-5f, 5f);
        float y = MathUtils.random(-5f, 4f);

        switch (MathUtils.random(0,1)) {
            case 0:
                if(x < 0) x -= 3f;
                else x += 3f;
                break;
            case 1:
                if(y < 0) y -= 3f;
                else y += 3f;
                break;
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
    }

    public void update(float delta) {
        super.update(delta);

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
    }

    public void move(float delta) {

        line = input[count].split(",");
        xDir = Float.parseFloat(line[0]);
        yDir = Float.parseFloat(line[1]);
        rotZ = Float.parseFloat(line[2]);

        decal.translateX(xDir);
        decal.translateY(yDir);
        decal.setRotationZ(rotZ * 1.25f);


        count++;

        if(count == input.length - 1) count = 0;
    }

    public void dispose() {
        super.dispose();
    }
}
