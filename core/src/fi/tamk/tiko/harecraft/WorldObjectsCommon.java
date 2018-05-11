package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import static fi.tamk.tiko.harecraft.GameMain.camera;
import static fi.tamk.tiko.harecraft.GameScreen.balloonsCollected;
import static fi.tamk.tiko.harecraft.GameScreen.ringCollectTimer;
import static fi.tamk.tiko.harecraft.GameScreen.fieldOfView;
import static fi.tamk.tiko.harecraft.GameScreen.global_Multiplier;
import static fi.tamk.tiko.harecraft.GameScreen.playerScore;
import static fi.tamk.tiko.harecraft.GameScreen.ringsCollected;
import static fi.tamk.tiko.harecraft.GameScreen.worldIndex;
import static fi.tamk.tiko.harecraft.GameScreen.worldScore;
import static fi.tamk.tiko.harecraft.World.player;

/**
 * Created by Mika on 19.4.2018.
 *
 * Contains all the common world objects-
 */

public abstract class WorldObjectsCommon {
}

/**
 * Created by Mika on 19.4.2018.
 *
 * Game object Ring.
 */

class Ring extends GameObject {
    final float COLLECTED_SPEED = 10f;
    final float MULTIPLIER_HIGH = 7f;
    final float MULTIPLIER_INCREMENT = 3.22f;
    boolean isCollected = false;
    Decal decal_arrows;
    float opacity_arrows;
    float stateTime_arrows;
    ParticleEffect pfx_speed_up;

    public Ring(float x, float y, float z) {
        TextureRegion textureRegion = Assets.texR_ring0;
        TextureRegion textureRegion2 = Assets.texR_ring_arrows0;
        switch (worldIndex) {
            case 0:
                textureRegion = Assets.texR_ring0;
                textureRegion2 = Assets.texR_ring_arrows2;
                break;
            case 1:
                textureRegion = Assets.texR_ring2;
                textureRegion2 = Assets.texR_ring_arrows0;
                break;
            case 2:
                textureRegion = Assets.texR_ring1;
                textureRegion2 = Assets.texR_ring_arrows1;
                break;
        }
        width = textureRegion.getRegionWidth()/75f;
        height = textureRegion.getRegionHeight()/75f;
        decal = Decal.newDecal(width, height, textureRegion, true);
        decal.setPosition(x,y,z);

        decal_arrows = Decal.newDecal(width, height, textureRegion2, true);
        decal_arrows.setPosition(x,y,z);
        decal_arrows.setScale(1.5f);

        pfx_speed_up = new ParticleEffect(Assets.pfx_speed_up);

        worldScore++;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        stateTime_arrows += delta;

        if(!isCollected) {
            if(decal.getPosition().z < 0.5f && decal.getPosition().z > -1.5f && position.dst(player.curPosition) < 2.5f) {
                isCollected = true;
                playerScore++;
                ringsCollected++;
                ringCollectTimer = 0f;

                if(global_Multiplier < MULTIPLIER_HIGH) global_Multiplier += MULTIPLIER_INCREMENT;
                if(global_Multiplier > MULTIPLIER_HIGH) global_Multiplier = MULTIPLIER_HIGH;

                AssetsAudio.playSound(AssetsAudio.SOUND_RING_COLLECTED,0.6f);

                decal.setPosition(position.x, position.y,0.5f);
                decal_arrows.setPosition(position.x, position.y,0.5f);

                pfx_speed_up.start();
                pfx_speed_up.setPosition(
                        player.projPosition.x,
                        player.projPosition.y);
                float randomAngle = MathUtils.random(0f, 18f);
                for(int i = 0; i < 20; i++) {
                    pfx_speed_up.getEmitters().get(0).getAngle().setLow(randomAngle + i * 360f/20f);
                    pfx_speed_up.getEmitters().get(0).getXOffsetValue().setLow((i % 2 == 0 ? 250f : 240) * MathUtils.cosDeg(pfx_speed_up.getEmitters().get(0).getAngle().getLowMin()));
                    pfx_speed_up.getEmitters().get(0).getYOffsetValue().setLow((i % 2 == 0 ? 250f : 240) * MathUtils.sinDeg(pfx_speed_up.getEmitters().get(0).getAngle().getLowMin()));
                    pfx_speed_up.getEmitters().get(0).addParticle();
                }
            }
        }
        else {
            decal.setScale(decal.getScaleX() + delta * stateTime / 4f);

            direction = player.curPosition.cpy().sub(position);
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

/**
 * Created by Mika on 19.4.2018.
 *
 * Game object Cloud.
 */

class Cloud extends GameObject {
    final float MULTIPLIER_LOW = 1.5f;
    final float MULTIPLIER_DECREMENT = 2.5f;

    Vector2 transposedPosition = new Vector2();
    boolean isCollided = false;
    boolean isTransparent = false;
    float proximity = 1.2f;

    ParticleEffect pfx_dispersion;

    public Cloud(float x, float y, float z) {
        TextureRegion textureRegion = Assets.texR_cloud1;

        switch(MathUtils.random(0,2)) {
            case 0:
                textureRegion = Assets.texR_cloud1;
                break;
            case 1:
                textureRegion = Assets.texR_cloud2;
                break;
            case 2:
                textureRegion = Assets.texR_cloud3;
                break;
        }

        if(MathUtils.random(0,1) == 0) textureRegion = Assets.flip(textureRegion);

        width = textureRegion.getRegionWidth() / 65f;
        height = textureRegion.getRegionHeight() / 65f;

        decal = Decal.newDecal(width, height, textureRegion, true);
        decal.setPosition(x,y,z);

        pfx_dispersion = new ParticleEffect(Assets.pfx_cloud_dispersion);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        //Opacity && Collision
        if(!isTransparent) {
            opacity = stateTime < 1f ? stateTime : 1f;
            if (decal.getPosition().z < 0.1f) {
                if (position.dst(player.position) < 2.8f) {
                    isTransparent = true;
                }
                if (decal.getPosition().z < 0f && position.dst(camera.position) < 5f)
                    isTransparent = true;
                if (!isCollided) checkCollision();
            }
        } else {
            opacity = 0.3f;
        }
        decal.setColor(1f,1f,1f, opacity);

        if(isCollided) {
            if(decal.getScaleX() < 0.7f) updateParticles(delta);
            decal.setScale(decal.getScaleX() - delta * stateTime / 2f);
            if(decal.getScaleX() < 0f) decal.setScale(0f);
        }

        //Movement Z
        if(!isCollided || decal.getScaleX() == 0f) moveZ(delta);
    }

    public void checkCollision() {
        if(decal.getPosition().z < 2f && decal.getPosition().z > -0.5f) {
            //Center
            if(position.dst(player.curPosition) < 1.65f) {
                decreaseSpeed();
                return;
            }

            //Left
            transposedPosition.x = position.x - width / 3.2f;
            transposedPosition.y = position.y;
            if (transposedPosition.dst(player.curPosition.x - player.width / 4f, player.curPosition.y) < proximity) {
                decreaseSpeed();
                return;
            } else if (transposedPosition.dst(player.curPosition.x + player.width / 4f, player.curPosition.y) < proximity) {
                decreaseSpeed();
                return;
            }

            //Right
            transposedPosition.x = position.x + width / 3.2f;
            if (transposedPosition.dst(player.curPosition.x - player.width / 4f, player.curPosition.y) < proximity) {
                decreaseSpeed();
                return;
            } else if (transposedPosition.dst(player.curPosition.x + player.width / 4f, player.curPosition.y) < proximity) {
                decreaseSpeed();
                return;
            }
        }
    }

    public void decreaseSpeed() {
        if(global_Multiplier > MULTIPLIER_LOW) {
            global_Multiplier -= MULTIPLIER_DECREMENT;
        }
        if(global_Multiplier < MULTIPLIER_LOW) global_Multiplier = MULTIPLIER_LOW;

        AssetsAudio.playSound(AssetsAudio.SOUND_CLOUD_HIT, 0.7f);

        pfx_dispersion.start();
        pfx_dispersion.setPosition(
                player.projPosition.x,
                player.projPosition.y);

        isCollided = true;
    }

    @Override
    public void updateParticles(float delta) {
        pfx_dispersion.update(delta);
    }

    public void dispose() {
        pfx_dispersion.dispose();
    }
}

/**
 * Created by Mika on 19.4.2018.
 *
 * Game object Balloon.
 */

class Balloon extends GameObject {
    final float COLLECTED_SPEED = 10f;
    Vector2 transposedPosition = new Vector2();
    Vector2 transposedDirection = new Vector2();
    boolean isCollected = false;
    float random;

    public Balloon(float x, float y, float z) {
        TextureRegion textureRegion;

        if(worldIndex == 0) textureRegion = Assets.texR_balloon_red;
        else if(worldIndex == 2) textureRegion = Assets.texR_balloon_blue;
        else textureRegion = Assets.texR_balloon_orange;

        width = textureRegion.getRegionWidth() / 110f;
        height = textureRegion.getRegionHeight() / 110f;

        decal = Decal.newDecal(width, height, textureRegion, true);
        decal.setPosition(x,y,z);

        velocity.y = MathUtils.random(3.6f,5.2f);
        random = MathUtils.random(-1,1);
        if(random == 0) random = 1;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        velocity.y -= delta * stateTime/5.5f;
        if(velocity.y < 0f) velocity.y = 0f;

        if(!isCollected)decal.translateY(velocity.y * delta);

        transposedPosition.x = position.x;
        transposedPosition.y = position.y + height / 4f;

        if(!isCollected) {
            if(decal.getPosition().z < 0.5f && decal.getPosition().z > -1.5f && (transposedPosition.dst(player.curPosition.x, player.curPosition.y) < 2.2f)) {
                isCollected = true;
                balloonsCollected++;

                AssetsAudio.playSound(AssetsAudio.SOUND_BALLOON_COLLECTED, 1f);
                decal.setPosition(position.x, position.y,0.5f);
            }
        }
        else {
            decal.setScale(decal.getScaleX() - delta * stateTime / 10f);
            if(decal.getScaleX() < 0f) decal.setScale(0f);
            transposedDirection = transposedPosition.cpy().sub(position.x, position.y);
            velocity.y = transposedDirection.nor().y * COLLECTED_SPEED * Math.abs(transposedDirection.y);

            decal.translateX(velocity.x * delta);
            decal.translateY(velocity.y * delta);

            rotation.z = random * delta * stateTime * 3f;
            updateParticles(delta);
        }

        decal.rotateZ(rotation.z);

        //Opacity
        setOpacity();


        //Movement Z
        if(!isCollected || decal.getScaleX() == 0f) moveZ(delta);
        if(isCollected && decal.getScaleX() > 0f) decal.translateZ(-velocity.z/5f * delta);
    }
}

