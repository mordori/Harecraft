package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

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
    Decal decal_palmtree;
    ArrayList<Decal> palmtrees = new ArrayList<Decal>();
    public Island(float x, float y, float z) {
        TextureRegion textureRegion = Assets.texR_island;

        width = textureRegion.getRegionWidth() / 20f;
        height = textureRegion.getRegionHeight() / 20f;

        float randomSize = MathUtils.random(1f, 2.5f);
        width *= randomSize;
        height *= randomSize;

        decal = Decal.newDecal(width, height, textureRegion,true);
        decal.setPosition(x,y - 4f,z + height);
        decal.rotateX(90f);
        decal.rotateZ(MathUtils.random(0f,360f));

        textureRegion = Assets.texR_palmtree;

        width = textureRegion.getRegionWidth() / 20f;
        height = textureRegion.getRegionHeight() / 20f;

        int random = MathUtils.random(1,6);

        for(int i = 0; i < random; i++) {
            decal_palmtree = Decal.newDecal(width, height, textureRegion,true);
            Vector3 palmtreePos = new Vector3(decal.getPosition().x + MathUtils.random(-decal.getWidth()/6f,decal.getWidth()/6f), decal.getPosition().y + height/2.1f, decal.getPosition().z+ MathUtils.random(-decal.getHeight()/6f,-decal.getHeight()/10f));
            decal_palmtree.setPosition(palmtreePos);

            if(palmtrees.isEmpty()) palmtrees.add(decal_palmtree);

            boolean isNear = true;

            for(Decal d : palmtrees) {
                if(!palmtrees.isEmpty() && decal_palmtree.getPosition().cpy().dst(d.getPosition()) > 7.5f) isNear = false;
                else isNear = true;
            }

            if(!isNear) palmtrees.add(decal_palmtree);
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if(position.z > 250) stateTime = 0;

        //Opacity
        setOpacity();
        for(Decal d : palmtrees) {
            d.setColor(1f,1f,1f, opacity);
        }

        //Movement Z
        moveZ(delta);
        for(Decal d : palmtrees) {
            d.translateZ(velocity.z * delta);
        }
    }
}

/**
 * Created by Mika on 19.4.2018.
 *
 * Game object Lighthouse.
 */

class LightHouse extends GameObject {
    Decal decal_island;

    public LightHouse(float x, float y, float z) {
        TextureRegion textureRegion = Assets.texR_lighthouse;
        width = textureRegion.getRegionWidth() / 15f;
        height = textureRegion.getRegionHeight() / 15f;

        decal = Decal.newDecal(width, height, textureRegion,true);
        decal.setPosition(x,y - 4f + height/2f, z);

        textureRegion = Assets.texR_island;
        width = textureRegion.getRegionWidth() / 13.5f;
        height = textureRegion.getRegionHeight() / 13.5f;

        decal_island = Decal.newDecal(width, height, textureRegion,true);
        decal_island.setPosition(x,y - 4f, z + 5f);
        decal_island.rotateX(90f);
        decal_island.rotateZ(MathUtils.random(0f,360f));
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        //Opacity
        setOpacity();
        decal_island.setColor(1f,1f,1f, opacity);

        //Movement Z
        moveZ(delta);
        decal_island.translateZ(velocity.z * delta);
    }
}

