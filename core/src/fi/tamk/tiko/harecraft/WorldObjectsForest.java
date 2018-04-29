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
 * Contains all the game objects for Forest and Tundra Worlds.
 */

public abstract class WorldObjectsForest {
}

/**
 * Created by Mika on 19.4.2018.
 *
 * Game object Hill.
 */

class Hill extends GameObject {
    Vector3 up = new Vector3(0f,1f,0f);
    public Hill(float x, float y, float z) {
        TextureRegion textureRegion = Assets.texR_summer_hill;

        if(GameScreen.world instanceof WorldSummer) {
            textureRegion = Assets.texR_summer_hill;
        }
        else if(GameScreen.world instanceof WorldTundra) {
            textureRegion = Assets.texR_tundra_hill;
        }

        width = textureRegion.getRegionWidth()/25f;
        height = textureRegion.getRegionHeight()/25f;

        float randomSize = MathUtils.random(1.5f, 3f);
        width *= randomSize;
        height *= randomSize;

        decal = Decal.newDecal(width, height, textureRegion,true);
        decal.setPosition(x,y + height/2f - 6f,z);
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
 * Game object Lake.
 */

class Lake extends GameObject {
    public Lake(float x, float y, float z) {
        TextureRegion textureRegion = Assets.texR_summer_lake;

        if(GameScreen.world instanceof WorldSummer) {
            textureRegion = Assets.texR_summer_lake;
        }
        else if(GameScreen.world instanceof WorldTundra) {
            textureRegion = Assets.texR_tundra_lake;
        }

        width = textureRegion.getRegionWidth() / 15f;
        height = textureRegion.getRegionHeight() / 15f;

        float randomSize = MathUtils.random(1f, 2.5f);
        width *= randomSize;
        height *= randomSize;

        decal = Decal.newDecal(width, height, textureRegion, true);
        decal.setPosition(x,y - 5f,z + height);
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
 * Game object Tree.
 */

class Tree extends GameObject {
    Vector3 up = new Vector3(0f,1f,0f);

    public Tree(float x, float y, float z) {
        TextureRegion textureRegion = Assets.texR_tree_summer_big_light;
        float transposedY = y - 2f;

        if(GameScreen.world instanceof WorldSummer) {
            switch(MathUtils.random(0,1)) {
                case 0:
                    switch (MathUtils.random(0, 3)) {
                        case 0:
                            textureRegion = Assets.texR_tree_summer_small_light;
                            transposedY -= 1.5f;
                            break;
                        case 1:
                        case 2:
                        case 3:
                            textureRegion = Assets.texR_tree_summer_big_light;
                            break;
                    }
                    break;
                case 1:
                    switch (MathUtils.random(0, 3)) {
                        case 0:
                            textureRegion = Assets.texR_tree_summer_small_dark;
                            transposedY -= 1.5f;
                            break;
                        case 1:
                        case 2:
                        case 3:
                            textureRegion = Assets.texR_tree_summer_big_dark;
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
                            transposedY -= 1.5f;
                            break;
                        case 1:
                            textureRegion = Assets.texR_tree_tundra_big_light;
                            break;
                        case 2:
                        case 3:
                            textureRegion = Assets.texR_tree_tundra_medium_light;
                            transposedY -= 1f;
                            break;
                    }
                    break;
                case 2:
                    switch (MathUtils.random(0, 3)) {
                        case 0:
                            textureRegion = Assets.texR_tree_tundra_small_dark;
                            transposedY -= 1.5f;
                            break;
                        case 1:
                            textureRegion = Assets.texR_tree_tundra_big_dark;
                            break;
                        case 2:
                        case 3:
                            textureRegion = Assets.texR_tree_tundra_medium_dark;
                            transposedY -= 1f;
                            break;
                    }
                    break;
            }

            width = textureRegion.getRegionWidth()/20f;
            height = textureRegion.getRegionHeight()/20f;
        }

        decal = Decal.newDecal(width, height, textureRegion, true);
        decal.setPosition(x,transposedY,z);
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
 * Game object HotAirBalloon.
 */

class HotAirBalloon extends GameObject {
    Vector3 up = new Vector3(0f,1f,0f);

    public HotAirBalloon(float x, float y, float z) {
        width = Assets.texR_hotairballoon.getRegionWidth()/12f;
        height = Assets.texR_hotairballoon.getRegionHeight()/12f;

        decal = Decal.newDecal(width, height, Assets.texR_hotairballoon, true);
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


