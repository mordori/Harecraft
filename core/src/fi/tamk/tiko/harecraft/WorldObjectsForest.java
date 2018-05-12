package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

import static fi.tamk.tiko.harecraft.GameMain.camera;
import static fi.tamk.tiko.harecraft.GameScreen.worldIndex;
import static fi.tamk.tiko.harecraft.WorldBuilder.spawnDistance;

/**
 * Created by Mika on 19.4.2018.
 *
 * Contains all the game objects for Forest and Tundra Worlds.
 */

public abstract class WorldObjectsForest {}

/**
 * Created by Mika on 19.4.2018.
 *
 * Game object Hill.
 */

class Hill extends GameObject implements Pool.Poolable {
    Vector3 up = new Vector3(0f,1f,0f);

    public Hill() {
        TextureRegion textureRegion = Assets.texR_hill_summer;

        if(GameScreen.world instanceof WorldSummer) {
            textureRegion = Assets.texR_hill_summer;
        }
        else if(GameScreen.world instanceof WorldTundra) {
            textureRegion = Assets.texR_hill_tundra;
        }

        width = textureRegion.getRegionWidth()/25f;
        height = textureRegion.getRegionHeight()/25f;

        float randomSize = MathUtils.random(1.5f, 3f);
        width *= randomSize;
        height *= randomSize;

        decal = Decal.newDecal(width, height, textureRegion,true);
    }

    public void init(float x, float y, float z) {
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

    @Override
    public void reset() {
        refresh();
    }
}

/**
 * Created by Mika on 19.4.2018.
 *
 * Game object Lake.
 */

class Lake extends GameObject implements Pool.Poolable{

    public Lake() {
        TextureRegion textureRegion = Assets.texR_lake_summer;

        if(GameScreen.world instanceof WorldSummer) {
            textureRegion = Assets.texR_lake_summer;
        }
        else if(GameScreen.world instanceof WorldTundra) {
            textureRegion = Assets.texR_lake_tundra;
        }

        width = textureRegion.getRegionWidth() / 15f;
        height = textureRegion.getRegionHeight() / 15f;
        float randomSize = MathUtils.random(1f, 2.5f);
        width *= randomSize;
        height *= randomSize;

        decal = Decal.newDecal(width, height, textureRegion, true);
        decal.rotateX(90f);
    }

    public void init(float x, float y, float z) {
        decal.setPosition(x,y - 5.5f,z + height);
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

    @Override
    public void reset() {
        refresh();
    }
}

/**
 * Created by Mika on 19.4.2018.
 *
 * Game object Tree.
 */

class Tree extends GameObject implements Pool.Poolable{
    Vector3 up = new Vector3(0f,1f,0f);
    float transposedY;

    public Tree() {
        TextureRegion textureRegion = Assets.texR_tree_summer_big_light;

        if(GameScreen.world instanceof WorldSummer) {
            switch(MathUtils.random(0,1)) {
                case 0:
                    switch (MathUtils.random(0, 3)) {
                        case 0:
                            textureRegion = Assets.texR_tree_summer_small_light;
                            transposedY = 1.5f;
                            break;
                        case 1:
                        case 2:
                        case 3:
                            textureRegion = Assets.texR_tree_summer_big_light;
                            transposedY = 0.25f;
                            break;
                    }
                    break;
                case 1:
                    switch (MathUtils.random(0, 3)) {
                        case 0:
                            textureRegion = Assets.texR_tree_summer_small_dark;
                            transposedY = 1.5f;
                            break;
                        case 1:
                        case 2:
                        case 3:
                            textureRegion = Assets.texR_tree_summer_big_dark;
                            transposedY = 0.25f;
                            break;
                    }
                    break;
            }
            width = textureRegion.getRegionWidth()/20f;
            height = textureRegion.getRegionHeight()/20f;
        }
        else if(GameScreen.world instanceof WorldTundra) {
            switch(MathUtils.random(0,2)) {
                case 0:
                case 1:
                    switch (MathUtils.random(0, 3)) {
                        case 0:
                            textureRegion = Assets.texR_tree_tundra_small_light;
                            transposedY = 1.5f;
                            break;
                        case 1:
                            textureRegion = Assets.texR_tree_tundra_big_light;
                            break;
                        case 2:
                        case 3:
                            textureRegion = Assets.texR_tree_tundra_medium_light;
                            transposedY = 1f;
                            break;
                    }
                    break;
                case 2:
                    switch (MathUtils.random(0, 3)) {
                        case 0:
                            textureRegion = Assets.texR_tree_tundra_small_dark;
                            transposedY = 1.5f;
                            break;
                        case 1:
                            textureRegion = Assets.texR_tree_tundra_big_dark;
                            break;
                        case 2:
                        case 3:
                            textureRegion = Assets.texR_tree_tundra_medium_dark;
                            transposedY = 1f;
                            break;
                    }
                    break;
            }
            width = textureRegion.getRegionWidth()/17f;
            height = textureRegion.getRegionHeight()/17f;
        }

        if(MathUtils.random(0,1) == 0) textureRegion = Assets.flip(textureRegion);
        decal = Decal.newDecal(width, height, textureRegion, true);
    }

    public void init(float x, float y, float z) {
        decal.setPosition(x,y - 2f - transposedY, z);
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

    @Override
    public void reset() {
        refresh();
    }
}

/**
 * Created by Mika on 19.4.2018.
 *
 * Game object HotAirBalloon.
 */

class HotAirBalloon extends GameObject {
    Vector3 up = new Vector3(0f,1f,0f);

    public HotAirBalloon(float x, float y, float z) {
        TextureRegion textureRegion;

        if(worldIndex == 1) textureRegion = Assets.texR_hotairballoon_summer;
        else textureRegion = Assets.texR_hotairballoon_tundra;


        width = textureRegion.getRegionWidth()/12f;
        height = textureRegion.getRegionHeight()/12f;

        decal = Decal.newDecal(width, height, textureRegion, true);
        decal.setPosition(x,y,z);

        velocity.y = 5f;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        velocity.y -= delta * stateTime / 5.5f;
        if (velocity.y < 0f) velocity.y = 0f;
        decal.translateY(velocity.y * delta);

        setOpacity();

        //Movement Z
        moveZ(delta);

        decal.lookAt(camera.position,up);
    }
}


