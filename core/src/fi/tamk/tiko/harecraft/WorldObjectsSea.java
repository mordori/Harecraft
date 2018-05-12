package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

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

class Boat extends GameObject implements Pool.Poolable{
    Vector3 up = new Vector3(0f,1f,0f);
    public Boat() {
        TextureRegion textureRegion = Assets.texR_boat;

        width = textureRegion.getRegionWidth()/25f;
        height = textureRegion.getRegionHeight()/25f;

        float randomSize = MathUtils.random(1.5f, 2f);
        width *= randomSize;
        height *= randomSize;

        decal = Decal.newDecal(width, height, textureRegion,true);
    }

    public void init(float x, float y, float z) {
        decal.setPosition(x,y + height/2f-4f,z);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if(position.z > 250f) stateTime = 0;

        //Opacity
        setOpacity();

        //Movement Z
        moveZ(delta);

        decal.lookAt(camera.position,up);
    }

    @Override
    public void reset() {
        refresh();
    }
}

/**
 * Created by Mika on 19.4.2018.
 *
 * Game object Island.
 */

class Island extends GameObject implements Pool.Poolable{
    Decal decal_palmtree;
    ArrayList<Decal> palmtrees = new ArrayList<Decal>();
    Vector3 up = new Vector3(0f,1f,0f);
    TextureRegion textureRegion;
    Decal[] palmtreesArr;
    float randomSize;
    int random;
    public Island() {
        textureRegion = Assets.texR_island;
        width = textureRegion.getRegionWidth() / 20f;
        height = textureRegion.getRegionHeight() / 20f;

        randomSize = MathUtils.random(1f, 2.5f);
        width *= randomSize;
        height *= randomSize;

        decal = Decal.newDecal(width, height, textureRegion,true);
        decal.rotateX(90f);
        decal.rotateZ(MathUtils.random(0f,360f));

        textureRegion = Assets.texR_palmtree;
        width = textureRegion.getRegionWidth() / 20f;
        height = textureRegion.getRegionHeight() / 20f;

        palmtreesArr = new Decal[(int)(3.5f * (randomSize*1.5f))];

        for(int i = 0; i < (int)(3.5f * (randomSize*1.5f)); i++) {
            if(MathUtils.random(0,1) == 0) textureRegion = Assets.flip(textureRegion);
            decal_palmtree = Decal.newDecal(width, height, textureRegion,true);
            palmtreesArr[i] = decal_palmtree;
        }
    }

    public void init(float x, float y, float z) {
        decal.setPosition(x,y - 4f, z);

        random = (int)(MathUtils.random(1f, 3.5f) * (randomSize*1.5f));
        for(int i = 0; i < random; i++) {
            palmtreesArr[i].setPosition(decal.getX() + MathUtils.random(-decal.getWidth()/6.5f,decal.getWidth()/6.5f),
                    decal.getY() + width/2f + 0.5f, decal.getZ() + MathUtils.random(-decal.getHeight()/6.5f, decal.getHeight()/6.5f));

            palmtrees.add(palmtreesArr[i]);
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if(position.z > 250f) stateTime = 0;

        //Opacity
        setOpacity();

        //Movement Z
        moveZ(delta);
        for(Decal d : palmtrees) {
            d.setColor(1f,1f,1f, opacity);
            d.translateZ(velocity.z * delta);
            d.lookAt(camera.position,up);
        }
    }

    public void dispose() {
        for(int i = 0; i < palmtrees.size(); i++) {
            palmtrees.remove(i);
        }
    }

    @Override
    public void reset() {
        refresh();

        palmtrees.removeAll(palmtrees);
    }
}

/**
 * Created by Mika on 19.4.2018.
 *
 * Game object Lighthouse.
 */

class LightHouse extends GameObject {
    Decal decal_island;
    Decal decal_palmtree;
    ArrayList<Decal> palmtrees = new ArrayList<Decal>();
    Vector3 up = new Vector3(0f,1f,0f);

    public LightHouse(float x, float y, float z) {
        TextureRegion textureRegion = Assets.texR_lighthouse;
        width = textureRegion.getRegionWidth() / 15f;
        height = textureRegion.getRegionHeight() / 15f;

        decal = Decal.newDecal(width, height, textureRegion,true);
        decal.setPosition(x,y - 4.5f + height/2f, z);

        textureRegion = Assets.texR_island;
        width = textureRegion.getRegionWidth() / 12f;
        height = textureRegion.getRegionHeight() / 12f;

        decal_island = Decal.newDecal(width, height, textureRegion,true);
        decal_island.setPosition(x,y - 4f, z);
        decal_island.rotateX(90f);
        decal_island.rotateZ(MathUtils.random(0f,360f));


        textureRegion = Assets.texR_palmtree;

        width = textureRegion.getRegionWidth() / 20f;
        height = textureRegion.getRegionHeight() / 20f;

        int random = (MathUtils.random(11, 20));

        for(int i = 0; i < random; i++) {
            if(MathUtils.random(0,1) == 0) textureRegion = Assets.flip(textureRegion);
            decal_palmtree = Decal.newDecal(width, height, textureRegion,true);

            decal_palmtree.setPosition(decal_island.getX() + MathUtils.random(-decal_island.getWidth()/6f,decal_island.getWidth()/6f),
                    decal_island.getY() + width/2f + 0.5f, decal_island.getZ() + MathUtils.random(-decal_island.getHeight()/6f, decal_island.getHeight()/6f));


            boolean isNear = false;
            boolean isClose = false;
            boolean isAdded = false;

            if(decal_palmtree.getZ() < decal.getZ() && (decal_palmtree.getZ() > decal.getZ() - decal_island.getHeight()/9f)) isNear = true;
            if(decal_palmtree.getZ() > decal.getZ() && (decal_palmtree.getZ() < decal.getZ() + decal_island.getHeight()/11)) isClose = true;

            if(isNear && isClose) isAdded = true;
            if((isNear == false && isClose == false) == false) isAdded = true;


            if(!isAdded) palmtrees.add(decal_palmtree);
            else {
                System.out.println(decal_palmtree.getZ());
                System.out.println("SUCCESSSSDFSDFSDF!!!!");
            }

        }
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


        for(Decal d : palmtrees) {
            d.setColor(1f,1f,1f, opacity);
            d.translateZ(velocity.z * delta);
            d.lookAt(camera.position,up);
        }
    }
}

