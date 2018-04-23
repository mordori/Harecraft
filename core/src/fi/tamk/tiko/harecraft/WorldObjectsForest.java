package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import static fi.tamk.tiko.harecraft.GameMain.camera;
import static fi.tamk.tiko.harecraft.WorldBuilder.spawnDistance;

/**
 * Created by Mika on 19.4.2018.
 */

public abstract class WorldObjectsForest {
}

class Hill extends GameObject {
    public Hill(float x, float y, float z) {
        width = Assets.texR_hill.getRegionWidth()/20f;
        height = Assets.texR_hill.getRegionHeight()/20f;

        float randomSize = MathUtils.random(1.5f, 3f);
        width *= randomSize;
        height *= randomSize;

        decal = Decal.newDecal(width, height, Assets.texR_hill,true);
        decal.setPosition(x,y + height/2f - 6f,z);
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        //Opacity
        setOpacity();

        //Movement Z
        moveZ(delta);
    }
}

class Lake extends GameObject {
    public Lake(float x, float y, float z) {
        width = Assets.texR_lake.getRegionWidth() / 15f;
        height = Assets.texR_lake.getRegionHeight() / 15f;

        float randomSize = MathUtils.random(1f, 2.5f);
        width *= randomSize;
        height *= randomSize;

        decal = Decal.newDecal(width, height, Assets.texR_lake, true);
        decal.setPosition(x,y - 5f,z + height);
        decal.rotateX(90f);
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

class Tree extends GameObject {
    public Tree(float x, float y, float z) {
        TextureRegion textureRegion;
        float transposedY = y - 1f;

        if(GameScreen.world instanceof WorldForest) {
            textureRegion = Assets.texR_tree_leafy_big_light;

            width = textureRegion.getRegionWidth()/20f;
            height = textureRegion.getRegionHeight()/20f;
        }
        else {
            if (MathUtils.random(0, 9) < 8) {
                if (MathUtils.random(0, 9) < 8) {
                    textureRegion = Assets.texR_tree_big_light;
                } else textureRegion = Assets.texR_tree_big_dark;
            } else {
                if (MathUtils.random(0, 5) < 4) {
                    textureRegion = Assets.texR_tree_small_light;
                } else textureRegion = Assets.texR_tree_small_dark;

                transposedY -= 1.5f;
            }

            width = textureRegion.getRegionWidth()/7.5f;
            height = textureRegion.getRegionHeight()/7.5f;
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
    }
}

class HotAirBalloon extends GameObject {
    static Decal decal_ribbons;
    Vector3 up = new Vector3(0f,1f,0f);

    public HotAirBalloon(float x, float y, float z) {
        width = Assets.texR_hotairballoon.getRegionWidth()/12f;
        height = Assets.texR_hotairballoon.getRegionHeight()/12f;

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


