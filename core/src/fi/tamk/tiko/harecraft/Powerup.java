package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import static fi.tamk.tiko.harecraft.GameMain.camera;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.World.player;

/**
 * Created by Mika on 11/03/2018.
 */

public class Powerup extends GameObject {
    final float COLLECTED_SPEED = 10f;
    Vector2 transposedPosition = new Vector2();
    Vector2 transposedDirection = new Vector2();
    boolean isCollected = false;
    float random;

    ParticleEffect pfx_hit;

    public Powerup() {
        velocity.y = MathUtils.random(3.6f,5.2f);
        random = MathUtils.random(-1,1);
        if(random == 0) random = 1;

        pfx_hit = new ParticleEffect(Assets.pfx_balloon_hit);
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

                Assets.sound_cloud_hit.play();
                decal.setPosition(position.x, position.y,0.5f);

                pfx_hit.start();
                pfx_hit.setPosition(
                        player.projPosition.x,
                        player.projPosition.y);
                for(int i = 3; i > 0; i--) {
                    pfx_hit.getEmitters().get(0).getAngle().setLow(i * 45f);
                    pfx_hit.getEmitters().get(0).addParticle();
                }
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

    public void updateParticles(float delta) {
        pfx_hit.update(delta);
    }

    public void dispose() {
        pfx_hit.dispose();
    }
}
