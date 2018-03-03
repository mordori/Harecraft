package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;

import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.FINISH;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.camera;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.GameScreen.gameStateTime;
import static fi.tamk.tiko.harecraft.GameScreen.global_Multiplier;
import static fi.tamk.tiko.harecraft.World.WORLD_HEIGHT_UP;
import static fi.tamk.tiko.harecraft.World.player;

/**
 * Created by Mika on 01/03/2018.
 */

public class WorldBuilder {
    World world;
    float x, y;
    static float spawnDistance = 250;
    static float groundLevel = -25f;
    float rings_Timer = 3.5f;
    float clouds_LUpTimer = 1f;
    float clouds_LDownTimer = 1f;
    float clouds_RUpTimer = 1f;
    float clouds_RDownTimer = 1f;
    float trees_LTimer = 1f;
    float trees_RTimer = 1f;
    float lakes_LTimer = 10f;
    float lakes_RTimer = 50f;

    static final int TREE = 0;

    public WorldBuilder(World world) {
        this.world = world;
    }

    public void update(float delta) {
        if(gameState == RACE && gameStateTime == 0f) {
            world.rings.add(new Ring(0f, 2f, spawnDistance/3.25f));
            world.rings.add(new Ring(2f, 0f, spawnDistance/1.35f));
            Assets.music_default.play();
            for(Opponent o : world.opponents) {
                o.position.z = o.spawnPositionZ;
            }
        }
        else if(gameState == FINISH && gameStateTime == 0f) {
            world.finishLine = new FinishLine(0f,WORLD_HEIGHT_UP,spawnDistance + 50f);
        }

        player.update(delta, Gdx.input.getAccelerometerY(), Gdx.input.getAccelerometerZ());

        spawnGroundObjects();
        if(gameState != FINISH && gameState != END) {
            spawnSkyObjects();
        }
        else if(gameState == FINISH) {
            world.finishLine.update(delta);
        }

        updateOpponents(delta);
        updateClouds(delta);
        updateRings(delta);
        updateLakes(delta);
        updateTrees(delta);

        int i = world.trees_R.size()+world.trees_L.size() + world.clouds_RDown.size() +world.clouds_RUp.size()+world.clouds_LDown.size()+world.clouds_LUp.size();

        //System.out.println("Decals: " + i);
    }

    public void spawnGroundObjects() {
            addLakes();
            addTrees();
    }

    public void spawnSkyObjects() {
            addClouds();
            addRing();
    }

    public void updateOpponents(float delta) {
        for(Opponent o : world.opponents) {
            o.update(delta);
        }
    }

    public void updateClouds(float delta) {
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

        for(Ring l : world.rings) {
            l.update(delta);
        }
        disposeRing();
    }

    public void updateTrees(float delta) {
        for(Tree t : world.trees_L) {
            t.update(delta);
        }
        for(Tree t : world.trees_R) {
            t.update(delta);
        }
        disposeTrees();
    }

    public void updateLakes(float delta) {
        for(Lake l : world.lakes_L) {
            l.update(delta);
        }
        for(Lake l : world.lakes_R) {
            l.update(delta);
        }
        disposeLakes();
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

    public void addRing() {
        if((gameState == RACE || gameState == FINISH) && (world.rings.isEmpty() || world.rings.get(world.rings.size() - 1).stateTime >= rings_Timer)) {
            x = MathUtils.random(-10f, 10f);
            y = MathUtils.random(-9.2f, 6.2f);
            world.rings.add(new Ring(x, y, spawnDistance - 50f));
        }
    }

    public void addTrees() {
        if(world.trees_L.isEmpty() || world.trees_L.get(world.trees_L.size() - 1).stateTime >= trees_LTimer) {
            x = MathUtils.random(-150f, 0f);
            y = groundLevel;
            world.trees_L.add(new Tree(x, y, spawnDistance));
            trees_LTimer = MathUtils.random(0.1f - global_Multiplier * 0.015f, 0.3f - global_Multiplier * 0.035f);
        }

        if(world.trees_R.isEmpty() || world.trees_R.get(world.trees_R.size() - 1).stateTime >= trees_RTimer) {
            x = MathUtils.random(0f, 150f);
            y = groundLevel;
            world.trees_R.add(new Tree(x, y, spawnDistance));
            trees_RTimer = MathUtils.random(0.1f - global_Multiplier * 0.015f, 0.3f - global_Multiplier * 0.035f);
        }
    }

    public void addLakes() {
        if(world.lakes_L.isEmpty() || world.lakes_L.get(world.lakes_L.size() - 1).stateTime >= lakes_LTimer) {
            x = MathUtils.random(-150f, 0f);
            y = groundLevel;
            world.lakes_L.add(new Lake(x, y, spawnDistance));
            lakes_LTimer = MathUtils.random(1f, 5f - global_Multiplier * 0.3f);
        }
        if(world.lakes_R.isEmpty() || world.lakes_R.get(world.lakes_R.size() - 1).stateTime >= lakes_RTimer) {
            x = MathUtils.random(0f, 150f);
            y = groundLevel;
            world.lakes_R.add(new Lake(x, y, spawnDistance));
            lakes_RTimer = MathUtils.random(1f, 5f - global_Multiplier * 0.3f);
        }
    }

    public void disposeClouds() {
        disposeCloud(world.clouds_LUp);
        disposeCloud(world.clouds_LDown);
        disposeCloud(world.clouds_RUp);
        disposeCloud(world.clouds_RDown);
    }

    public void disposeTrees() {
        disposeTree(world.trees_L);
        disposeTree(world.trees_R);
    }

    public void disposeLakes() {
        disposeLake(world.lakes_L);
        disposeLake(world.lakes_R);
    }

    public void disposeRing() {
        if(!world.rings.isEmpty() && world.rings.get(0).decal.getPosition().z < camera.position.z) {
            world.rings.remove(0);
        }
    }

    public void disposeCloud(ArrayList<Cloud> cloudArray) {
        if(!cloudArray.isEmpty() && cloudArray.get(0).decal.getPosition().z < camera.position.z) {
            cloudArray.remove(0);
        }
    }

    public void disposeTree(ArrayList<Tree> treeArray) {
        if(!world.lakes_L.isEmpty() && world.trees_L.get(world.trees_L.size()-1).position.cpy().dst(world.lakes_L.get(world.lakes_L.size()-1).position) < world.lakes_L.get(world.lakes_L.size()-1).width/1.75f) {
            world.trees_L.remove(world.trees_L.size()-1);
        }
        if(!world.lakes_R.isEmpty() && world.trees_L.get(world.trees_L.size()-1).position.cpy().dst(world.lakes_R.get(world.lakes_R.size()-1).position) < world.lakes_R.get(world.lakes_R.size()-1).width/1.75f) {
            world.trees_L.remove(world.trees_L.size()-1);
        }
        if(!world.lakes_R.isEmpty() && world.trees_R.get(world.trees_R.size()-1).position.cpy().dst(world.lakes_R.get(world.lakes_R.size()-1).position) < world.lakes_R.get(world.lakes_R.size()-1).width/1.75f) {
            world.trees_R.remove(world.trees_R.size()-1);
        }
        if(!world.lakes_L.isEmpty() && world.trees_R.get(world.trees_R.size()-1).position.cpy().dst(world.lakes_L.get(world.lakes_L.size()-1).position) < world.lakes_L.get(world.lakes_L.size()-1).width/1.75f) {
            world.trees_R.remove(world.trees_R.size()-1);
        }

        if(!treeArray.isEmpty() && treeArray.get(0).decal.getPosition().z < camera.position.z) {
            treeArray.remove(0);
        }
    }

    public void disposeLake(ArrayList<Lake> lakeArray) {
        if(!lakeArray.isEmpty() && lakeArray.get(0).decal.getPosition().z < camera.position.z) {
            lakeArray.remove(0);
        }
    }
}
