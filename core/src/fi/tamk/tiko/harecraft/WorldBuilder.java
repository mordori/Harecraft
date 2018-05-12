package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

import static fi.tamk.tiko.harecraft.GameMain.camera;
import static fi.tamk.tiko.harecraft.GameScreen.DIFFICULTYSENSITIVITY;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.EXIT;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.FINISH;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.GameScreen.gameStateTime;
import static fi.tamk.tiko.harecraft.GameScreen.global_Multiplier;
import static fi.tamk.tiko.harecraft.GameScreen.global_Speed;
import static fi.tamk.tiko.harecraft.GameScreen.worldIndex;
import static fi.tamk.tiko.harecraft.World.end;
import static fi.tamk.tiko.harecraft.World.finish;
import static fi.tamk.tiko.harecraft.World.player;

/**
 * Created by Mika on 01/03/2018.
 *
 * Class used for adding, updating, deleting and all kinds of crazy handling of objects in the worlds.
 */

public class WorldBuilder {
    World world;
    float x, y, z;
    static float spawnDistance = 250;
    static float groundLevel = -25f;
    float rings_Timer = 3.5f;
    float clouds_LUpTimer = 1f;
    float clouds_LDownTimer = 1f;
    float clouds_RUpTimer = 1f;
    float clouds_RDownTimer = 1f;
    float boatsTimer = 5f;
    float islands_LTimer = 5f;
    float islands_RTimer = 5f;

    float trees_LTimer = 1f;
    float trees_RTimer = 1f;
    float lakes_LTimer = MathUtils.random(0f, 5f);
    float lakes_RTimer = MathUtils.random(0f, 5f);
    float hills_LTimer = 1f;
    float hills_RTimer = 1f;
    float hills_LRemoveTimer;
    float hills_RRemoveTimer;
    float trees_LRemoveTimer;
    float trees_RRemoveTimer;
    float boat_RemoveTimer;
    Vector3 pos = new Vector3();
    Vector2 ringSpawnVector = new Vector2(0f,18f);      //18 maksimi säde
    int staticHold = 0;

    float balloon1SpawnPos, balloon2SpawnPos, balloon3SpawnPos;
    boolean balloon1Collected, balloon2Collected, balloon3Collected;

    public WorldBuilder(World world) {
        this.world = world;
        balloon1SpawnPos = MathUtils.random(50f, 1f/3f * finish -100) ;
        balloon2SpawnPos = MathUtils.random(1f/3f * finish + 100f, 2f/3f * finish - 100f);
        balloon3SpawnPos = MathUtils.random(2f/3f * finish + 100f, 3f/3f * finish - 100f);

        spawnStartObjects();
    }

    public void update(float delta) {
        //SPAWN
        //-------------------------------------------
        spawnGroundObjects();
        if(gameState == START || gameState == RACE) {
            spawnSkyObjects();
        }

        //UPDATE
        //-------------------------------------------
        player.update(delta, Gdx.input.getAccelerometerY(), Gdx.input.getAccelerometerZ());

        updateOpponents(delta);
        updateClouds(delta);
        updateRings(delta);
        updateBalloons(delta);

        if(world instanceof WorldSummer || world instanceof WorldTundra) {
            updateLakes(delta);
            updateTrees(delta);
            updateHills(delta);
        }
        else if(world instanceof WorldSea) {
            updateBoats(delta);
            updateIslands(delta);
        }

        if(gameState == FINISH || gameState == END || gameState == EXIT) {
            if(worldIndex == 1 || worldIndex == 2) {
                for (HotAirBalloon hotAirBalloon : world.hotAirBalloons) {
                    hotAirBalloon.update(delta);
                }
                if (!world.hotAirBalloons.isEmpty() && world.hotAirBalloons.get(0).decal.getPosition().z < camera.position.z) {
                    world.hotAirBalloons.remove(0);
                }
            }
            else {
                for (LightHouse lightHouse : world.lightHouses) {
                    lightHouse.update(delta);
                }
                if (!world.lightHouses.isEmpty() && world.lightHouses.get(0).decal.getPosition().z < camera.position.z) {
                    int size = world.lightHouses.get(0).palmtrees.size() - 1;

                    for(int i = size; i > 0; i--) {
                        world.lightHouses.get(0).palmtrees.remove(i);
                    }
                    world.lightHouses.remove(0);
                }
            }
        }

        world.update(delta);
        world.updateShaders(delta);
        updateWorldParticles(delta);
    }

    public void spawnGroundObjects() {
        if(world instanceof WorldSummer || world instanceof WorldTundra) {
            addLakes();
            addHills();
            addTrees();
        }
        else if(world instanceof WorldSea) {
            addBoat();
            if(player.distance < end - 375f || player.distance > end - 250f) addIsland();
        }
    }

    public void updateBoats(float delta) {
        for(Boat b : world.boats) b.update(delta);
        boat_RemoveTimer -= delta;
        if(boat_RemoveTimer < 0f) boat_RemoveTimer = 0f;

        removeBoats();
    }

    public void updateIslands(float delta) {
        for(Island i : world.islands_L) i.update(delta);
        for(Island i : world.islands_R) i.update(delta);

        removeIslands();
    }

    public void removeIslands() {
        removeIsland(world.islands_L);
        removeIsland(world.islands_R);
    }

    public void removeIsland(ArrayList<Island> islandArray) {
        if(!islandArray.isEmpty() && islandArray.get(0).decal.getPosition().z < camera.position.z) {

            Island island = islandArray.get(0);
            islandArray.remove(island);
            world.islandPool.free(island);
        }
    }

    public void removeBoats() {
        if(!world.boats.isEmpty()) {
            if(!world.islands_L.isEmpty() && world.boats.get(world.boats.size() - 1).position.dst(world.islands_L.get(world.islands_L.size() - 1).position) < world.islands_L.get(world.islands_L.size() - 1).width
                    || !world.islands_R.isEmpty() && world.boats.get(world.boats.size() - 1).position.dst(world.islands_R.get(world.islands_R.size() - 1).position) < world.islands_R.get(world.islands_R.size() - 1).width) {

                Boat boat = world.boats.get(world.boats.size() - 1);
                world.boats.remove(boat);
                world.boatPool.free(boat);
                boat_RemoveTimer = 0.5f;

                System.out.println("BOAT REMOVED!!!!");
            }
        }

        if(!world.boats.isEmpty() && world.boats.get(0).decal.getPosition().z < camera.position.z) {
            Boat boat = world.boats.get(0);
            world.boats.remove(boat);
            world.boatPool.free(boat);
        }
    }

    public void addBoat() {
        if(boat_RemoveTimer == 0) {
            if (world.boats.isEmpty() || world.boats.get(world.boats.size() - 1).stateTime >= boatsTimer) {
                if (gameStateTime >= boatsTimer) {
                    x = MathUtils.random(-150f, 150f);
                    y = groundLevel;
                    Boat boat = world.boatPool.obtain();
                    boat.init(x, y, 350f);
                    world.boats.add(boat);
                    boatsTimer = MathUtils.random(0.5f, 8f - global_Multiplier * 0.5f);
                }
            }
        }
    }

    public void addIsland() {
        if(world.islands_L.isEmpty() || world.islands_L.get(world.islands_L.size() - 1).stateTime >= islands_LTimer) {
            if(gameStateTime >= islands_LTimer) {
                x = MathUtils.random(-135f, -20f);
                y = groundLevel;
                Island island = world.islandPool.obtain();
                island.init(x, y, 350f);
                world.islands_L.add(island);
                islands_LTimer = MathUtils.random(1f, 8f - global_Multiplier * 0.65f);
            }
        }

        if(world.islands_R.isEmpty() || world.islands_R.get(world.islands_R.size() - 1).stateTime >= islands_RTimer) {
            if(gameStateTime >= islands_RTimer) {
                x = MathUtils.random(20f, 135f);
                y = groundLevel;
                Island island = world.islandPool.obtain();
                island.init(x, y, 350f);
                world.islands_R.add(island);
                islands_RTimer = MathUtils.random(1f, 8f - global_Multiplier * 0.65f);
            }
        }
    }

    public void spawnSkyObjects() {
        addClouds();
        addRing();
        addBalloon();
    }

    public void updateOpponents(float delta) {
        for(Opponent o : world.opponents) o.update(delta);
    }

    public void updateClouds(float delta) {
        for(Cloud c : world.clouds_LUp) c.update(delta);
        for(Cloud c : world.clouds_LDown) c.update(delta);
        for(Cloud c : world.clouds_RUp) c.update(delta);
        for(Cloud c : world.clouds_RDown) c.update(delta);

        removeClouds();
    }

    public void updateRings(float delta) {
        for(Ring l : world.rings) l.update(delta);

        removeRing();
    }

    public void updateBalloons(float delta) {
        for(Balloon b : world.balloons) b.update(delta);

        removeBalloon();
    }

    public void updateTrees(float delta) {
        for(Tree t : world.trees_L) t.update(delta);
        for(Tree t : world.trees_R) t.update(delta);

        trees_RRemoveTimer -= delta;
        if(trees_RRemoveTimer < 0f) trees_RRemoveTimer = 0f;
        trees_LRemoveTimer -= delta;
        if(trees_LRemoveTimer < 0f) trees_LRemoveTimer = 0f;
        removeTrees();
    }

    public void updateLakes(float delta) {
        for(Lake l : world.lakes_L) l.update(delta);
        for(Lake l : world.lakes_R) l.update(delta);

        removeLakes();
    }

    public void updateHills(float delta) {
        for(Hill h : world.hills_L) h.update(delta);
        for(Hill h : world.hills_R) h.update(delta);

        hills_LRemoveTimer -= delta;
        if(hills_LRemoveTimer < 0f) hills_LRemoveTimer = 0f;
        hills_RRemoveTimer -= delta;
        if(hills_RRemoveTimer < 0f) hills_RRemoveTimer = 0f;
        removeHills();
    }

    public void addClouds() {
        float difficultyRate = (4 - DIFFICULTYSENSITIVITY) * 0.17f;
        float min = 0.45f + 5.6f/global_Multiplier * 0.12f;
        float max = 1.35f - global_Multiplier * 0.15f;

        if(world.clouds_LUp.isEmpty() || world.clouds_LUp.get(world.clouds_LUp.size() - 1).stateTime >= clouds_LUpTimer) {
            x = MathUtils.random(-40f,0f);
            y = MathUtils.random(0f,6.2f);
            Cloud cloud = world.cloudPool.obtain();
            cloud.init(x, y, spawnDistance);
            world.clouds_LUp.add(cloud);
            clouds_LUpTimer = MathUtils.random(min, max) + difficultyRate;
        }
        if(world.clouds_LDown.isEmpty() || world.clouds_LDown.get(world.clouds_LDown.size() - 1).stateTime >= clouds_LDownTimer) {
            x = MathUtils.random(-40f,0f);
            y = MathUtils.random(0f,-6.2f);
            Cloud cloud = world.cloudPool.obtain();
            cloud.init(x, y, spawnDistance);
            world.clouds_LDown.add(cloud);
            clouds_LDownTimer = MathUtils.random(min, max) + difficultyRate;
        }
        if(world.clouds_RUp.isEmpty() || world.clouds_RUp.get(world.clouds_RUp.size() - 1).stateTime >= clouds_RUpTimer) {
            x = MathUtils.random(0f,40f);
            y = MathUtils.random(0f,6.2f);
            Cloud cloud = world.cloudPool.obtain();
            cloud.init(x, y, spawnDistance);
            world.clouds_RUp.add(cloud);
            clouds_RUpTimer = MathUtils.random(min, max) + difficultyRate;
        }
        if(world.clouds_RDown.isEmpty() || world.clouds_RDown.get(world.clouds_RDown.size() - 1).stateTime >= clouds_RDownTimer) {
            x = MathUtils.random(0f,40f);
            y = MathUtils.random(0f,-6.2f);
            Cloud cloud = world.cloudPool.obtain();
            cloud.init(x, y, spawnDistance);
            world.clouds_RDown.add(cloud);
            clouds_RDownTimer = MathUtils.random(min, max) + difficultyRate;
        }
    }

    public void addRing() {
        if((gameState == RACE || gameState == FINISH) && (world.rings.isEmpty() || world.rings.get(world.rings.size() - 1).stateTime >= rings_Timer)) {
            //x = MathUtils.random(-10f, 10f); //mikko rings
            //y = MathUtils.random(-9.2f, 6.2f);
            //ringSpawnVector.rotate(MathUtils.random(1f,20f));

            //Gdx.app.log("Profiili", ""+ProfileInfo.selectedPlayerProfile);
            //Gdx.app.log("vaikeus", ""+ProfileInfo.selectedDifficulty);

            if (staticHold > 0 || MathUtils.random(1,5) <= ProfileInfo.selectedStaticHolds) {   // d6 if static hold starts OR if static hold is running 0 off 5 100%

                if (staticHold == 0) {      //static hold starts
                    staticHold = MathUtils.random(2,4);     //static hold rings amount
                    //ringSpawnVector.rotate(MathUtils.random(0f, 360f)); //randomize new vector for static hold
                    //ringSpawnVector.setLength(MathUtils.random(2f + DIFFICULTYSENSITIVITY + (4f - DIFFICULTYSENSITIVITY)*0.25f, 3f + (DIFFICULTYSENSITIVITY * 1.3f) + (4f - DIFFICULTYSENSITIVITY)*0.5f));  //minimum increased because static hold is useless in center
                    ringSpawnVector = randomizeRingSpawnVector();
                    rings_Timer = 1f;
                }
                else if (staticHold > 0) {       //static hold is running
                    //world.rings.add(new Ring(ringSpawnVector.x, ringSpawnVector.y -2f, spawnDistance - 50f));  //-2 modifier for y spawn
                    Ring ring = world.ringPool.obtain();
                    ring.init(ringSpawnVector.x, ringSpawnVector.y -2f, spawnDistance - 50f);
                    world.rings.add(ring);
                    staticHold--;
                }

                if (staticHold == 0) {      //static hold ends
                    rings_Timer = 3.5f;
                }
            }
            else {          //Spawn basic vector Ring
                //ringSpawnVector.rotate(MathUtils.random(0f, 360f));
                //ringSpawnVector.setLength(MathUtils.random(2f + DIFFICULTYSENSITIVITY + (4f - DIFFICULTYSENSITIVITY)*0.25f, 3f + (DIFFICULTYSENSITIVITY * 1.3f) + (4f - DIFFICULTYSENSITIVITY)*0.5f));
                ringSpawnVector = randomizeRingSpawnVector();
                //world.rings.add(new Ring(ringSpawnVector.x, ringSpawnVector.y -2f, spawnDistance - 50f)); //-2f modifier for y spawn
                Ring ring = world.ringPool.obtain();
                ring.init(ringSpawnVector.x, ringSpawnVector.y -2f, spawnDistance - 50f);
                world.rings.add(ring);
            }
        }
    }

    public void addBalloon() {
        if(!world.rings.isEmpty() && world.rings.get(world.rings.size() - 1).stateTime > 1.5f && world.rings.get(world.rings.size() - 1).stateTime < 2f) {
            if (!balloon1Collected && player.distance > balloon1SpawnPos) {
                x = MathUtils.random(-5f, 5f);
                y = -23f;
                world.balloons.add(new Balloon(x, y, spawnDistance - 50f));
                balloon1Collected = true;
            } else if (!balloon2Collected && player.distance > balloon2SpawnPos) {
                x = MathUtils.random(-5f, 5f);
                y = -23f;
                world.balloons.add(new Balloon(x, y, spawnDistance - 50f));
                balloon2Collected = true;
            } else if (!balloon3Collected && player.distance > balloon3SpawnPos) {
                x = MathUtils.random(-5f, 5f);
                y = -23f;
                world.balloons.add(new Balloon(x, y, spawnDistance - 50f));
                balloon3Collected = true;
            }
        }
    }

    public void addTrees() {
        //float min = 0.1f + 6.1f/global_Multiplier * 0.04f;
        //float max = 0.4f - global_Multiplier * 0.05f;
        //float min = 0.01f;
        //float max = 0.05f;
        float min = 0.05f + 5.6f/global_Multiplier * 0.04f;
        float max = 0.35f - global_Multiplier * 0.04f;

        if((world.trees_L.isEmpty() && trees_LRemoveTimer == 0f)
                || (!world.trees_L.isEmpty() && trees_LRemoveTimer == 0f && world.trees_L.get(world.trees_L.size() - 1).stateTime >= trees_LTimer)) {
            x = MathUtils.random(-150f, 0f);
            y = groundLevel;
            Tree tree = world.treePool.obtain();
            tree.init(x, y, spawnDistance);
            world.trees_L.add(tree);
            trees_LTimer = MathUtils.random(min, max);
        }

        if((world.trees_R.isEmpty() && trees_RRemoveTimer == 0f)
                || (!world.trees_R.isEmpty() && trees_RRemoveTimer == 0f && world.trees_R.get(world.trees_R.size() - 1).stateTime >= trees_RTimer)) {
            x = MathUtils.random(0f, 150f);
            y = groundLevel;
            Tree tree = world.treePool.obtain();
            tree.init(x, y, spawnDistance);
            world.trees_R.add(tree);
            trees_RTimer = MathUtils.random(min, max);
        }
    }

    public void addLakes() {
        if(world.lakes_L.isEmpty() || world.lakes_L.get(world.lakes_L.size() - 1).stateTime >= lakes_LTimer) {
            if(gameStateTime >= lakes_LTimer) {
                x = MathUtils.random(-150f, 0f);
                y = groundLevel;
                Lake lake = world.lakePool.obtain();
                lake.init(x, y, spawnDistance);
                world.lakes_L.add(lake);
                lakes_LTimer = MathUtils.random(0.5f, 8f - global_Multiplier * 0.5f);
            }
        }
        if(world.lakes_R.isEmpty() || world.lakes_R.get(world.lakes_R.size() - 1).stateTime >= lakes_RTimer) {
            if(gameStateTime >= lakes_RTimer) {
                x = MathUtils.random(0f, 150f);
                y = groundLevel;
                Lake lake = world.lakePool.obtain();
                lake.init(x, y, spawnDistance);
                world.lakes_R.add(lake);
                lakes_RTimer = MathUtils.random(0.5f, 8f - global_Multiplier * 0.5f);
            }
        }
    }

    public void addHills() {
        float min = 0.7f;
        float max = 3f - global_Multiplier * 0.3f;

        if((world.hills_L.isEmpty() && hills_LRemoveTimer == 0f) || (!world.hills_L.isEmpty() && hills_LRemoveTimer == 0f && world.hills_L.get(world.hills_L.size() - 1).stateTime >= hills_LTimer)) {
            x = MathUtils.random(-160f, 0f);
            y = groundLevel;
            Hill hill = world.hillPool.obtain();
            hill.init(x, y, spawnDistance);
            world.hills_L.add(hill);
            hills_LTimer = MathUtils.random(min, max);
        }
        if((world.hills_R.isEmpty() && hills_RRemoveTimer == 0f) || (!world.hills_L.isEmpty() && hills_RRemoveTimer == 0f && world.hills_R.get(world.hills_R.size() - 1).stateTime >= hills_RTimer)) {
            x = MathUtils.random(0f, 160f);
            y = groundLevel;
            Hill hill = world.hillPool.obtain();
            hill.init(x, y, spawnDistance);
            world.hills_R.add(hill);
            hills_RTimer = MathUtils.random(min, max);
        }
    }

    public void removeClouds() {
        removeCloud(world.clouds_LUp);
        removeCloud(world.clouds_LDown);
        removeCloud(world.clouds_RUp);
        removeCloud(world.clouds_RDown);
    }

    public void removeTrees() {
        if(!world.trees_L.isEmpty()) {
            if (!world.lakes_L.isEmpty() && world.trees_L.get(world.trees_L.size() - 1).position.dst(world.lakes_L.get(world.lakes_L.size() - 1).position) < world.lakes_L.get(world.lakes_L.size() - 1).width / 1.75f
                    || !world.lakes_R.isEmpty() && world.trees_L.get(world.trees_L.size() - 1).position.dst(world.lakes_R.get(world.lakes_R.size() - 1).position) < world.lakes_R.get(world.lakes_R.size() - 1).width / 1.75f) {

                Tree tree = world.trees_L.get(world.trees_L.size() - 1);
                world.trees_L.remove(tree);
                world.treePool.free(tree);
                trees_LRemoveTimer = 0.25f;
            }
        }

        if(!world.trees_R.isEmpty()) {
            if (!world.lakes_R.isEmpty() && world.trees_R.get(world.trees_R.size() - 1).position.dst(world.lakes_R.get(world.lakes_R.size() - 1).position) < world.lakes_R.get(world.lakes_R.size() - 1).width / 1.75f
                    || !world.lakes_L.isEmpty() && world.trees_R.get(world.trees_R.size() - 1).position.dst(world.lakes_L.get(world.lakes_L.size() - 1).position) < world.lakes_L.get(world.lakes_L.size() - 1).width / 1.75f) {

                Tree tree = world.trees_R.get(world.trees_R.size() - 1);
                world.trees_R.remove(tree);
                world.treePool.free(tree);
                trees_RRemoveTimer = 0.25f;
            }
        }

        if(!world.hills_L.isEmpty()) {
            pos = (world.hills_L.get(world.hills_L.size() - 1).position.cpy());
            pos.y -= world.hills_L.get(world.hills_L.size() - 1).height / 2f;

            if (!world.trees_L.isEmpty() && world.trees_L.get(world.trees_L.size() - 1).position.z > world.hills_L.get(world.hills_L.size() - 1).position.z && world.trees_L.get(world.trees_L.size() - 1).position.dst(pos) < world.hills_L.get(world.hills_L.size() - 1).width) {

                Tree tree = world.trees_L.get(world.trees_L.size() - 1);
                world.trees_L.remove(tree);
                world.treePool.free(tree);
                trees_LRemoveTimer = 0.25f;
            }
            if (!world.trees_R.isEmpty() && world.trees_R.get(world.trees_R.size() - 1).position.z > world.hills_L.get(world.hills_L.size() - 1).position.z && world.trees_R.get(world.trees_R.size() - 1).position.dst(pos) < world.hills_L.get(world.hills_L.size() - 1).width) {

                Tree tree = world.trees_R.get(world.trees_R.size() - 1);
                world.trees_R.remove(tree);
                world.treePool.free(tree);
                trees_RRemoveTimer = 0.25f;
            }
        }

        if(!world.hills_R.isEmpty()) {
            pos = world.hills_R.get(world.hills_R.size() - 1).position.cpy();
            pos.y -= world.hills_R.get(world.hills_R.size() - 1).height / 2f;

            if (!world.trees_R.isEmpty() && world.trees_R.get(world.trees_R.size() - 1).position.z > world.hills_R.get(world.hills_R.size() - 1).position.z && world.trees_R.get(world.trees_R.size() - 1).position.dst(pos) < world.hills_R.get(world.hills_R.size() - 1).width) {

                Tree tree = world.trees_R.get(world.trees_R.size() - 1);
                world.trees_R.remove(tree);
                world.treePool.free(tree);
                trees_RRemoveTimer = 0.25f;
            }
            if (!world.trees_L.isEmpty() && world.trees_L.get(world.trees_L.size() - 1).position.z > world.hills_R.get(world.hills_R.size() - 1).position.z && world.trees_L.get(world.trees_L.size() - 1).position.dst(pos) < world.hills_R.get(world.hills_R.size() - 1).width) {

                Tree tree = world.trees_L.get(world.trees_L.size() - 1);
                world.trees_L.remove(tree);
                world.treePool.free(tree);
                trees_LRemoveTimer = 0.25f;
            }
        }

        removeTree(world.trees_L);
        removeTree(world.trees_R);
    }

    public void removeLakes() {
        removeLake(world.lakes_L);
        removeLake(world.lakes_R);
    }

    public void removeHills() {
        if(!world.hills_L.isEmpty()) {
            if (!world.lakes_L.isEmpty() && world.hills_L.get(world.hills_L.size() - 1).position.dst(world.lakes_L.get(world.lakes_L.size() - 1).position) < world.lakes_L.get(world.lakes_L.size() - 1).width
                    || !world.lakes_R.isEmpty() && world.lakes_R.get(world.lakes_R.size()-1).stateTime < 1f && world.hills_L.get(world.hills_L.size() - 1).position.dst(world.lakes_R.get(world.lakes_R.size() - 1).position) < world.lakes_R.get(world.lakes_R.size() - 1).width) {


                Hill hill = world.hills_L.get(world.hills_L.size() - 1);
                world.hills_L.remove(hill);
                world.hillPool.free(hill);
                hills_LRemoveTimer = 1f;
            }
        }

        if(!world.hills_R.isEmpty()) {
            if (!world.lakes_L.isEmpty() && world.hills_R.get(world.hills_R.size() - 1).position.dst(world.lakes_L.get(world.lakes_L.size() - 1).position) < world.lakes_L.get(world.lakes_L.size() - 1).width
                    || !world.lakes_R.isEmpty() && world.lakes_R.get(world.lakes_R.size()-1).stateTime < 1f && world.hills_R.get(world.hills_R.size() - 1).position.dst(world.lakes_R.get(world.lakes_R.size() - 1).position) < world.lakes_R.get(world.lakes_R.size() - 1).width) {


                Hill hill = world.hills_R.get(world.hills_R.size() - 1);
                world.hills_R.remove(hill);
                world.hillPool.free(hill);
                hills_RRemoveTimer = 1f;
            }
        }

        removeHill(world.hills_L);
        removeHill(world.hills_R);
    }

    public void removeRing() {
        if(!world.rings.isEmpty() && world.rings.get(0).decal.getPosition().z < camera.position.z) {
            if(!world.rings.get(0).isCollected || world.rings.get(0).pfx_speed_up.isComplete()) {
                if(world.rings.get(0).pfx_speed_up != null) {
                    world.pfxPool_playerSpeedUp.free(world.rings.get(0).pfx_speed_up);
                }
                Ring ring = world.rings.get(0);
                world.rings.remove(0);
                world.ringPool.free(ring);
            }
        }
    }

    public void removeBalloon() {
        if(world.balloons.size() > 1 && world.balloons.get(0).decal.getPosition().z < camera.position.z) {
            if(!world.balloons.get(0).isCollected) {
                world.balloons.remove(0);
            }
        }
    }

    public void removeCloud(ArrayList<Cloud> cloudArray) {
        if(!cloudArray.isEmpty() && cloudArray.get(0).decal.getPosition().z < camera.position.z) {
            if(!cloudArray.get(0).isCollided || cloudArray.get(0).pfx_dispersion.isComplete()) {
                if(cloudArray.get(0).pfx_dispersion != null) {
                    world.pfxPool_cloudDispersion.free(cloudArray.get(0).pfx_dispersion);
                }
                Cloud cloud = cloudArray.get(0);
                cloudArray.remove(cloud);
                world.cloudPool.free(cloud);
            }
        }
    }

    public void removeTree(ArrayList<Tree> treeArray) {
        if(!treeArray.isEmpty() && treeArray.get(0).decal.getPosition().z < camera.position.z) {

            Tree tree = treeArray.get(0);
            treeArray.remove(tree);
            world.treePool.free(tree);
        }
    }

    public void removeLake(ArrayList<Lake> lakeArray) {
        if(!lakeArray.isEmpty() && lakeArray.get(0).decal.getPosition().z < camera.position.z) {

            Lake lake = lakeArray.get(0);
            lakeArray.remove(lake);
            world.lakePool.free(lake);
        }
    }

    public void removeHill(ArrayList<Hill> hillArray) {
        if(!hillArray.isEmpty() && hillArray.get(0).decal.getPosition().z < camera.position.z) {

            Hill hill = hillArray.get(0);
            hillArray.remove(hill);
            world.hillPool.free(hill);
        }
    }

    public void updateWorldParticles(float delta) {
        world.pfx_speed_lines.setPosition(SCREEN_WIDTH/2f, SCREEN_HEIGHT/2f);
        world.pfx_speed_lines.getEmitters().get(0).getTransparency().setHigh(Math.abs(player.velocity.z / (global_Speed - 7f*3f) - 0.54f) * 1f);
        world.pfx_speed_lines.update(delta);

        if(world.pfx_snow != null) world.pfx_snow.update(delta);

        if(gameState == END) {
            if(world.pfx_speed_lines != null) world.pfx_speed_lines.allowCompletion();
            if(world.pfx_snow != null) world.pfx_snow.allowCompletion();
        }
    }

    public void spawnStartObjects() {
        for (int j = 100; j < 220; j += MathUtils.random(30,40)) {               //Z Depth step
            for (int i = -100; i < 100; i += MathUtils.random(15, 50)) {         //X step
                if(world instanceof WorldSummer || world instanceof WorldTundra) {
                    Tree tree = world.treePool.obtain();
                    tree.init(i, groundLevel, j);
                    world.trees_L.add(tree);
                }
                if ( i < -10 || i > 10 ) {
                    Cloud cloud = world.cloudPool.obtain();
                    cloud.init(i, MathUtils.random(0, 8), j);
                    world.clouds_LDown.add(cloud); //Clouds
                }
            }
        }

        if(worldIndex == 0) {
            x = MathUtils.random(-135f, -35f);
            y = groundLevel;
            z = MathUtils.random(200f, 350f);
            Island island = world.islandPool.obtain();
            island.init(x, y, z);
            world.islands_L.add(island);
            islands_LTimer = MathUtils.random(1f, 7f - global_Multiplier * 0.65f);

            x = MathUtils.random(135f, 35f);
            z = MathUtils.random(200f, 350f);
            island = world.islandPool.obtain();
            island.init(x, y, z);
            world.islands_R.add(island);
            islands_RTimer = MathUtils.random(1f, 7f - global_Multiplier * 0.65f);
        }
    }

    public Vector2 randomizeRingSpawnVector() {
        int whatVector = MathUtils.random(1,6);     //radomize what vector to spawn
        Vector2 tmpVector = new Vector2(18f,0f);

        if (whatVector == 1) {
            tmpVector.x = ProfileInfo.customVector1.x;
            tmpVector.y = ProfileInfo.customVector1.y;
            Gdx.app.log("VECTOR", " 1");
        }
        if (whatVector == 2) {
            tmpVector.x = ProfileInfo.customVector2.x;
            tmpVector.y = ProfileInfo.customVector2.y;
            Gdx.app.log("VECTOR", " 2");
        }
        if (whatVector == 3) {
            tmpVector.x = ProfileInfo.customVector3.x;
            tmpVector.y = ProfileInfo.customVector3.y;
            Gdx.app.log("VECTOR", " 3");
        }
        if (whatVector == 4) {
            tmpVector.x = ProfileInfo.customVector4.x;
            tmpVector.y = ProfileInfo.customVector4.y;
            Gdx.app.log("VECTOR", " 4");
        }
        if (whatVector == 5) {
            tmpVector.x = ProfileInfo.customVector5.x;
            tmpVector.y = ProfileInfo.customVector5.y;
            Gdx.app.log("VECTOR", " 5");
        }
        if (whatVector == 6) {
            tmpVector.x = ProfileInfo.customVector6.x;
            tmpVector.y = ProfileInfo.customVector6.y;
            Gdx.app.log("VECTOR", " 6");
        }

        Gdx.app.log("tmpvector" , " X: " +tmpVector.x +" Y : " +tmpVector.y );

        if (tmpVector.x == 0 && tmpVector.y == 0) {
            tmpVector.y = 18f;
            tmpVector.x = 0f;
            //tmpVector.setLength(MathUtils.random(2f + DIFFICULTYSENSITIVITY + (4f - DIFFICULTYSENSITIVITY)*0.25f, 3f + (DIFFICULTYSENSITIVITY * 1.3f) + (4f - DIFFICULTYSENSITIVITY)*0.5f));
            tmpVector.setLength(MathUtils.random(1f + DIFFICULTYSENSITIVITY +DIFFICULTYSENSITIVITY, 4f + DIFFICULTYSENSITIVITY + DIFFICULTYSENSITIVITY ));
            tmpVector.rotate(MathUtils.random(0f, 360f));
            Gdx.app.log("total", " randomization");
        }
        else {
            tmpVector.x = tmpVector.x / 17f * -1f;
            tmpVector.y = tmpVector.y /17f;
        }
        Gdx.app.log("tmpvector" , " X: " +tmpVector.x +" Y : " +tmpVector.y );
        Gdx.app.log("length :", " " +tmpVector.len() );
        return tmpVector;
    }
}
