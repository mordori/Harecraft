package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.FINISH;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.camera;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.WorldBuilder.spawnDistance;

/**
 * Created by Mika on 03/03/2018.
 */

public class HotAirBalloon extends GameObject {
    float width = Assets.texR_hotairballoon.getRegionWidth()/10f;
    float height = Assets.texR_hotairballoon.getRegionHeight()/10f;
    static Decal decal_ribbons;
    boolean isDrawing;
    Vector3 up = new Vector3(0f,1f,0f);

    public HotAirBalloon(float x, float y, float z) {
        decal = Decal.newDecal(width, height, Assets.texR_hotairballoon, true);
        decal.setPosition(x,y,z);

        if(x < 0f) decal_ribbons = Decal.newDecal(Assets.texR_ribbons.getRegionWidth()/10f, Assets.texR_ribbons.getRegionHeight()/10f, Assets.texR_ribbons, true);

        velocity.y = 5f;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        velocity.y -= delta * stateTime / 5.5f;
        if (velocity.y < 0f) velocity.y = 0f;
        decal.translateY(velocity.y * delta);

        setOpacity();
        if(position.x < 0f){
            decal_ribbons.setPosition(World.WORLD_WIDTH/2f - 5f, position.y + 5f, position.z -1f);
            decal_ribbons.setColor(1f,1f,1f, opacity);
        }

        //Movement Z
        moveZ(delta);

        decal.lookAt(camera.position,up);
    }
}
