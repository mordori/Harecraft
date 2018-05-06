package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import static fi.tamk.tiko.harecraft.GameMain.camera;
import static fi.tamk.tiko.harecraft.WorldBuilder.spawnDistance;

/**
 * Created by Mika on 19.4.2018.
 *
 * Contains all the game objects for Sea World.
 */

public abstract class WorldObjectsSea {
}

/**
 * Created by Mika on 19.4.2018.
 *
 * Game object Boat.
 */

class Boat extends GameObject {
    Vector3 up = new Vector3(0f,1f,0f);
    public Boat(float x, float y, float z) {
        TextureRegion textureRegion = Assets.texR_boat;

        width = textureRegion.getRegionWidth()/25f;
        height = textureRegion.getRegionHeight()/25f;

        float randomSize = MathUtils.random(1.5f, 2f);
        width *= randomSize;
        height *= randomSize;

        decal = Decal.newDecal(width, height, textureRegion,true);
        decal.setPosition(x,y + height/2f - 7f,z);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        //Opacity
        setOpacity();

        //Movement Z
        moveZ(delta);

        decal.lookAt(camera.position,up);
    }
}

/**
 * Created by Mika on 19.4.2018.
 *
 * Game object Island.
 */

class Island extends GameObject {
    public Island(float x, float y, float z) {
        TextureRegion textureRegion = Assets.texR_island;

        width = textureRegion.getRegionWidth() / 15f;
        height = textureRegion.getRegionHeight() / 15f;

        float randomSize = MathUtils.random(1f, 2.5f);
        width *= randomSize;
        height *= randomSize;

        decal = Decal.newDecal(width, height, textureRegion, true);
        decal.setPosition(x,y - 5.5f,z + height);
        decal.rotateX(90f);
        decal.rotateZ(MathUtils.random(0f,360f));
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if(position.z + height/3f > spawnDistance) stateTime = 0;

        //Opacity
        setOpacity();

        //Movement Z
        moveZ(delta);
    }
}

/**
 * Created by Mika on 19.4.2018.
 *
 * Game object Lighthouse.
 */

class LightHouse extends GameObject {
    public LightHouse(float x, float y, float z) {
        TextureRegion textureRegion = Assets.texR_lighthouse;

        width = textureRegion.getRegionWidth() / 15f;
        height = textureRegion.getRegionHeight() / 15f;

        decal = Decal.newDecal(width, height, textureRegion,true);
        decal.setPosition(x,y - 5.5f,z + height);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if(position.z + height/3f > spawnDistance) stateTime = 0;

        //Opacity
        setOpacity();

        //Movement Z
        moveZ(delta);
    }
}

/**
 * Created by Mika on 19.4.2018.
 *
 * Game object Whale.
 */

class Whale extends GameObject {
}
