package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import static fi.tamk.tiko.harecraft.GameMain.camera;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.global_Multiplier;
import static fi.tamk.tiko.harecraft.World.player;

/**
 * Created by Mika on 23.2.2018.
 */

public class Cloud extends GameObject {
    final float MULTIPLIER_LOW = 1f;
    final float MULTIPLIER_DECREMENT = 1.5f;

    Vector2 transposedPosition = new Vector2();
    boolean isCollided = false;
    boolean isTransparent = false;
    float proximity = 1.2f;

    ParticleEffect pfx_dispersion;

    public Cloud(float x, float y, float z) {
        TextureRegion textureRegion = Assets.texR_cloud;
        if(MathUtils.random(0,1) == 0) textureRegion = Assets.flip(textureRegion);

        width = textureRegion.getRegionWidth() / 80f;
        height = textureRegion.getRegionHeight() / 80f;

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
        if(decal.getPosition().z < 3f && decal.getPosition().z > -0.5f) {
            //Center
            if (position.dst(player.position) < 1.65f) {
                decreaseSpeed();
                return;
            }

            //Left
            transposedPosition.x = position.x - width / 3.2f;
            transposedPosition.y = position.y - height / 4f;
            if (transposedPosition.dst(player.position.x - player.width / 4f, player.position.y) < proximity) {
                decreaseSpeed();
                return;
            } else if (transposedPosition.dst(player.position.x + player.width / 4f, player.position.y) < proximity) {
                decreaseSpeed();
                return;
            }

            //Right
            transposedPosition.x = position.x + width / 3.2f;
            if (transposedPosition.dst(player.position.x - player.width / 4f, player.position.y) < proximity) {
                decreaseSpeed();
                return;
            } else if (transposedPosition.dst(player.position.x + player.width / 4f, player.position.y) < proximity) {
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
        Assets.sound_cloud_hit.play();
        pfx_dispersion.start();
        pfx_dispersion.setPosition(
                camera.project(player.curPosition.cpy()).x,
                camera.project(player.curPosition.cpy()).y);
        /*for(int i = 15; i > 0; i--) {
            if(MathUtils.random(0,1) == 0) pfx_dispersion.getEmitters().get(0).getRotation().setHigh(-60f);
            else pfx_dispersion.getEmitters().get(0).getRotation().setHigh(60f);
            pfx_dispersion.getEmitters().get(0).getAngle().setLow(0f + i * 45f);
            pfx_dispersion.getEmitters().get(0).addParticle();
        }*/
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
