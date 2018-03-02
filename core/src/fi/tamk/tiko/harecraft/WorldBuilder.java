package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;

import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.camera;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.GameScreen.gameTime;
import static fi.tamk.tiko.harecraft.GameScreen.global_Multiplier;
import static fi.tamk.tiko.harecraft.World.player;

/**
 * Created by Mika on 01/03/2018.
 */

public class WorldBuilder {
    World world;
    static float spawnDistance = 250;
    float rings_Timer = 3.5f;
    float clouds_LUpTimer = 1f;
    float clouds_LDownTimer = 1f;
    float clouds_RUpTimer = 1f;
    float clouds_RDownTimer = 1f;
    float trees_Timer = 1f;
    float x, y;

    GroundObject[] groundObjects = new GroundObject[1];

    static final int TREE = 0;

    public WorldBuilder(World world) {
        this.world = world;
    }

    public void update(float delta) {
        if(gameState == START && gameTime > 6f) {
            gameState = RACE;
            world.rings.add(new Ring(0f, -6f, spawnDistance/4f));
            world.rings.add(new Ring(3f, 1f, spawnDistance/1.5f));
            Assets.music_default.play();
            for(Opponent o : world.opponents) {
                o.position.z = o.spawnPositionZ;
            }
        }

        player.update(delta, Gdx.input.getAccelerometerY(), Gdx.input.getAccelerometerZ());
        spawnGroundObjects();
        spawnSkyObjects();

        updateOpponents(delta);
        updateClouds(delta);
        updateRings(delta);
        updateTrees(delta);
    }

    public void spawnGroundObjects() {

    }

    public void spawnSkyObjects() {

    }

    public void updateOpponents(float delta) {
        for(Opponent o : world.opponents) {
            o.update(delta);
        }
    }

    public void updateClouds(float delta) {
        addClouds();
        for(Cloud c : world.clouds_LUp) {
            c.update(delta);
        }
        for(Cloud c : world.clouds_LDown) {
            c.update(delta);
        }
        for(Cloud c : world.clouds_RUp) {
            c.update(delta);
        }
        for(Cloud c : world.clouds_RDown) {
            c.update(delta);
        }
        disposeClouds();
    }

    public void updateRings(float delta) {
        addRings();
        for(Ring l : world.rings) {
            l.update(delta);
        }
        disposeLifeRings();
    }

    public void updateTrees(float delta) {
        addTrees();
        for(Tree t : world.trees) {
            t.update(delta);
        }
        disposeTrees();
    }

    public void addClouds() {
        if(world.clouds_LUp.isEmpty() || world.clouds_LUp.get(world.clouds_LUp.size() - 1).stateTime >= clouds_LUpTimer) {
            x = MathUtils.random(-40f,0f);
            y = MathUtils.random(0f,6.2f);
            world.clouds_LUp.add(new Cloud(x, y, spawnDistance));
            clouds_LUpTimer = MathUtils.random(0.4f, 1f - global_Multiplier *  0.1f);
        }
        if(world.clouds_LDown.isEmpty() || world.clouds_LDown.get(world.clouds_LDown.size() - 1).stateTime >= clouds_LDownTimer) {
            x = MathUtils.random(-40f,0f);
            y = MathUtils.random(0f,-6.2f);
            world.clouds_LDown.add(new Cloud(x, y, spawnDistance));
            clouds_LDownTimer = MathUtils.random(0.4f, 1f - global_Multiplier *  0.1f);
        }
        if(world.clouds_RUp.isEmpty() || world.clouds_RUp.get(world.clouds_RUp.size() - 1).stateTime >= clouds_RUpTimer) {
            x = MathUtils.random(0f,40f);
            y = MathUtils.random(0f,6.2f);
            world.clouds_RUp.add(new Cloud(x, y, spawnDistance));
            clouds_RUpTimer = MathUtils.random(0.4f, 1f - global_Multiplier *  0.1f);
        }
        if(world.clouds_RDown.isEmpty() || world.clouds_RDown.get(world.clouds_RDown.size() - 1).stateTime >= clouds_RDownTimer) {
            x = MathUtils.random(0f,40f);
            y = MathUtils.random(0f,-6.2f);
            world.clouds_RDown.add(new Cloud(x, y, spawnDistance));
            clouds_RDownTimer = MathUtils.random(0.4f, 1f - global_Multiplier *  0.1f);
        }
    }

    public void addRings() {
        if(gameState == RACE && (world.rings.isEmpty() || world.rings.get(world.rings.size() - 1).stateTime >= rings_Timer)) {
            x = MathUtils.random(-10f, 10f);
            y = MathUtils.random(-9.2f, 6.2f);
            world.rings.add(new Ring(x, y, spawnDistance - 50f));
        }
    }

    public void addTrees() {
        if(world.trees.isEmpty() || world.trees.get(world.trees.size() - 1).stateTime >= trees_Timer) {
            x = MathUtils.random(-100f, 100f);
            y = -25f;
            world.trees.add(new Tree(x, y, spawnDistance));
            trees_Timer = MathUtils.random(0.05f, 0.2f - global_Multiplier * 0.025f);
        }
    }

    public void disposeClouds() {
        disposeCloud(world.clouds_LUp);
        disposeCloud(world.clouds_LDown);
        disposeCloud(world.clouds_RUp);
        disposeCloud(world.clouds_RDown);
    }

    public void disposeCloud(ArrayList<Cloud> cloudArray) {
        if(cloudArray.get(0).decal.getPosition().z < camera.position.z) {
            cloudArray.remove(0);
        }
    }

    public void disposeTrees() {
        if(!world.trees.isEmpty() && world.trees.get(0).decal.getPosition().z < camera.position.z) {
            world.trees.remove(0);
        }
    }

    public void disposeLifeRings() {
        if(!world.rings.isEmpty() && world.rings.get(0).decal.getPosition().z < camera.position.z) {
            world.rings.remove(0);
        }
    }
}
