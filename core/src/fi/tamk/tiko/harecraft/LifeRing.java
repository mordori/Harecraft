package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Mika on 26/02/2018.
 */

public class LifeRing extends GameObject {

    float width = Assets.texR_lifering.getRegionWidth()/100f;
    float height = Assets.texR_lifering.getRegionHeight()/100f;
    final float SPEED = 15f;
    boolean isCollected = false;

    public LifeRing(float x, float y, float z) {
        decal = Decal.newDecal(width * 8.5f, height * 8.5f, Assets.texR_lifering, true);
        decal.setPosition(x,y,z);
        position = new Vector2();
        velocity = new Vector2();
        direction = new Vector2();
    }

    public void update(float delta) {
        stateTime += delta;
        position.x = decal.getPosition().x;
        position.y = decal.getPosition().y;

        opacity = stateTime < 1f ? stateTime : 1f;
        decal.setColor(1f,1f,1f, opacity);

        if(!isCollected) {
            if(decal.getPosition().z < 0.1f && position.dst(GameScreen.player.position.x, GameScreen.player.position.y) < 1.7f) {
                isCollected = true;
                decal.setPosition(position.x,position.y,0.1f);
            }
        }
        else {
            decal.setScale(decal.getScaleX() + delta*stateTime/4f);

            direction = GameScreen.player.position.cpy().sub(position).nor();
            velocity.x = direction.x * SPEED * Math.abs(GameScreen.player.position.cpy().sub(position).x);
            velocity.y = direction.y * SPEED * Math.abs(GameScreen.player.position.cpy().sub(position).y);

            decal.translateX(velocity.x * delta);
            decal.translateY(velocity.y * delta);
        }

        if(!isCollected) decal.rotateZ(delta * 50f);
        else decal.rotateZ(delta * 70f * stateTime);

        if(!isCollected || decal.getScaleX() > 1.5f) decal.translateZ(-20f * delta);
    }
}
