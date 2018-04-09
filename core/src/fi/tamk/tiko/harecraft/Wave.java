package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g3d.decals.Decal;

/**
 * Created by Minuet on 9.4.2018.
 */

public class Wave extends GameObject {
    int currentState;
    static final int APPEAR = 0;
    static final int COMPLETE = 1;
    static final int DISAPPEAR = 2;

    public Wave(float x, float y, float z) {
        width = Assets.texR_tree_small_light.getRegionWidth() / 100f;
        height = Assets.texR_tree_small_light.getRegionHeight() / 100f;
        width *= 17f;
        height *= 17f;

        decal = Decal.newDecal(width, height, Assets.texR_tree_small_light, true);
        decal.setPosition(x,y,z);
    }

    public void update(float delta) {
        super.update(delta);

        switch (currentState) {
            case APPEAR:
                velocity.y += delta * stateTime * 15f;
                opacity = stateTime/1.5f;
                if(opacity > 1f) opacity = 1f;
                if(stateTime > 1.5f) {
                    currentState++;
                    stateTime = 0;
                }
                break;
            case COMPLETE:
                velocity.y = 0f;
                if(stateTime > 0.5f) {
                    currentState++;
                    stateTime = 0;
                }
                break;
            case DISAPPEAR:
                velocity.y -= delta * stateTime * 15f;
                opacity = (1.5f - stateTime)/1.5f;
                if(opacity < 0f) opacity = 0f;
                if(stateTime > 1.5f) {
                    currentState++;
                    stateTime = 0;
                }
                break;
        }

        //Opacity
        setOpacity();

        //Movement
        moveZ(delta);
        //moveY(delta);
    }

    public void moveY(float delta) {decal.translateZ(velocity.y * delta);}

    @Override
    public void setOpacity() {decal.setColor(1f,1f,1f, opacity);}
}
