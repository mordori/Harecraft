package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

import static fi.tamk.tiko.harecraft.GameMain.camera;
import static fi.tamk.tiko.harecraft.GameScreen.DIFFICULTYSENSITIVITY;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.FINISH;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.GameScreen.gameStateTime;
import static fi.tamk.tiko.harecraft.GameScreen.global_Multiplier;
import static fi.tamk.tiko.harecraft.GameScreen.global_Speed;
import static fi.tamk.tiko.harecraft.World.player;

/**
 * Created by Mika on 01/03/2018.
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
    float balloon_Timer = 2f * rings_Timer;

    float trees_LTimer = 1f;
    float trees_RTimer = 1f;
    float lakes_LTimer = 10f;
    float lakes_RTimer = 10f;
    float hills_LTimer = 1f;
    float hills_RTimer = 1f;
    float hills_LRemoveTimer;
    float hills_RRemoveTimer;
    float trees_LRemoveTimer;
    float trees_RRemoveTimer;
    Vector3 pos = new Vector3();
    Vector2 ringSpawnVector = new Vector2(0f,18f);      //18 maksimi säde
    int staticHold = 0;


    public WorldBuilder(World world) {
        this.world = world;
        spawnStartObjects();
    }

    public void update(float delta) {
        //SPAWN
        //-------------------------------------------
        spawnGroundObjects();
        if(gameState != FINISH && gameState != END) {
            spawnSkyObjects();
        }

        //UPDATE
        //-------------------------------------------
        player.update(delta, Gdx.input.getAccelerometerY(), Gdx.input.getAccelerometerZ());

        updateOpponents(delta);
        updateClouds(delta);
        updateRings(delta);
        updatePowerups(delta);

        if(world instanceof WorldForest || world instanceof WorldTundra) {
            updateLakes(delta);
            updateTrees(delta);
            updateHills(delta);
        }
        else if(world instanceof WorldSea) {

        }

        if(gameState == FINISH || gameState == END) {
            for(HotAirBalloon hotAirBalloon : world.hotAirBalloons) {
                hotAirBalloon.update(delta);
            }
            if(!world.hotAirBalloons.isEmpty() && world.hotAirBalloons.get(0).decal.getPosition().z < camera.position.z) {
                world.hotAirBalloons.remove(0);
            }
        }

        world.update(delta);
        world.updateShaders(delta);
        updateWorldParticles(delta);
    }

    public void spawnGroundObjects() {
        if(world instanceof WorldForest || world instanceof WorldTundra) {
            addLakes();
            addHills();
            addTrees();
        }
        else if(world instanceof WorldSea) {

        }
    }

    public void spawnSkyObjects() {
        addClouds();
        addRing();
        addPowerup();
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

    public void updatePowerups(float delta) {
        for(Balloon b : world.balloons) b.update(delta);

        removePowerup();
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
        if(world.clouds_LUp.isEmpty() || world.clouds_LUp.get(world.clouds_LUp.size() - 1).stateTime >= clouds_LUpTimer) {
            x = MathUtils.random(-40f,0f);
            y = MathUtils.random(0f,6.2f);
            world.clouds_LUp.add(new Cloud(x, y, spawnDistance));
            clouds_LUpTimer = MathUtils.random(0.4f, 1f - global_Multiplier *  0.1f) + (4 - DIFFICULTYSENSITIVITY) * 0.25f;
        }
        if(world.clouds_LDown.isEmpty() || world.clouds_LDown.get(world.clouds_LDown.size() - 1).stateTime >= clouds_LDownTimer) {
            x = MathUtils.random(-40f,0f);
            y = MathUtils.random(0f,-6.2f);
            world.clouds_LDown.add(new Cloud(x, y, spawnDistance));
            clouds_LDownTimer = MathUtils.random(0.4f, 1f - global_Multiplier *  0.1f) + (4 - DIFFICULTYSENSITIVITY) * 0.25f;
        }
        if(world.clouds_RUp.isEmpty() || world.clouds_RUp.get(world.clouds_RUp.size() - 1).stateTime >= clouds_RUpTimer) {
            x = MathUtils.random(0f,40f);
            y = MathUtils.random(0f,6.2f);
            world.clouds_RUp.add(new Cloud(x, y, spawnDistance));
            clouds_RUpTimer = MathUtils.random(0.4f, 1f - global_Multiplier *  0.1f) + (4 - DIFFICULTYSENSITIVITY) * 0.25f;
        }
        if(world.clouds_RDown.isEmpty() || world.clouds_RDown.get(world.clouds_RDown.size() - 1).stateTime >= clouds_RDownTimer) {
            x = MathUtils.random(0f,40f);
            y = MathUtils.random(0f,-6.2f);
            world.clouds_RDown.add(new Cloud(x, y, spawnDistance));
            clouds_RDownTimer = MathUtils.random(0.4f, 1f - global_Multiplier *  0.1f) + (4 - DIFFICULTYSENSITIVITY) * 0.25f;
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
                    world.rings.add(new Ring(ringSpawnVector.x, ringSpawnVector.y -2f, spawnDistance - 50f));  //-2 modifier for y spawn
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
                world.rings.add(new Ring(ringSpawnVector.x, ringSpawnVector.y -2f, spawnDistance - 50f)); //-2f modifier for y spawn
            }
        }

        /*if(!world.rings.isEmpty()) {
            for (int i = 0; i < world.clouds_LDown.size(); i++) {
                if (world.clouds_LDown.get(i).position.dst(world.rings.get(world.rings.size() - 1).position) < world.rings.get(world.rings.size() - 1).width * 2f)
                    world.clouds_LDown.remove(i);
            }
            for (int i = 0; i < world.clouds_RDown.size(); i++) {
                if (world.clouds_RDown.get(i).position.dst(world.rings.get(world.rings.size() - 1).position) < world.rings.get(world.rings.size() - 1).width * 2f)
                    world.clouds_RDown.remove(i);
            }
            for (int i = 0; i < world.clouds_LUp.size(); i++) {
                if (world.clouds_LUp.get(i).position.dst(world.rings.get(world.rings.size() - 1).position) < world.rings.get(world.rings.size() - 1).width * 2f)
                    world.clouds_LUp.remove(i);
            }
            for (int i = 0; i < world.clouds_RUp.size(); i++) {
                if (world.clouds_RUp.get(i).position.dst(world.rings.get(world.rings.size() - 1).position) < world.rings.get(world.rings.size() - 1).width * 2f)
                    world.clouds_RUp.remove(i);
            }
        }*/
    }

    public void addPowerup() {
        if((gameState == RACE || gameState == FINISH) && ((world.balloons.isEmpty() && gameStateTime > 5.2f && gameStateTime < 10f) || (gameStateTime > 5.2f && world.balloons.get(world.balloons.size() - 1).stateTime >= balloon_Timer))) {
            x = MathUtils.random(-5f, 5f);
            y = -23f;
            world.balloons.add(new Balloon(x,y, spawnDistance - 50f));
        }
    }

    public void addTrees() {
        if((world.trees_L.isEmpty() && trees_LRemoveTimer == 0f) || (!world.trees_L.isEmpty() && trees_LRemoveTimer == 0f && world.trees_L.get(world.trees_L.size() - 1).stateTime >= trees_LTimer)) {
            x = MathUtils.random(-150f, 0f);
            y = groundLevel;
            world.trees_L.add(new Tree(x, y, spawnDistance));
            trees_LTimer = MathUtils.random(0.25f - global_Multiplier * 0.015f, 0.45f - global_Multiplier * 0.035f);
        }

        if((world.trees_R.isEmpty() && trees_RRemoveTimer == 0f) || (!world.trees_R.isEmpty() && trees_RRemoveTimer == 0f && world.trees_R.get(world.trees_R.size() - 1).stateTime >= trees_RTimer)) {
            x = MathUtils.random(0f, 150f);
            y = groundLevel;
            world.trees_R.add(new Tree(x, y, spawnDistance));
            trees_RTimer = MathUtils.random(0.25f - global_Multiplier * 0.015f, 0.45f - global_Multiplier * 0.035f);
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

    public void addHills() {
        if((world.hills_L.isEmpty() && hills_LRemoveTimer == 0f) || (!world.hills_L.isEmpty() && hills_LRemoveTimer == 0f && world.hills_L.get(world.hills_L.size() - 1).stateTime >= hills_LTimer)) {
            x = MathUtils.random(-160f, 0f);
            y = groundLevel;
            world.hills_L.add(new Hill(x, y, spawnDistance));
            hills_LTimer = MathUtils.random(1f, 4f - global_Multiplier * 0.3f);
        }
        if((world.hills_R.isEmpty() && hills_RRemoveTimer == 0f) || (!world.hills_L.isEmpty() && hills_RRemoveTimer == 0f && world.hills_R.get(world.hills_R.size() - 1).stateTime >= hills_RTimer)) {
            x = MathUtils.random(0f, 160f);
            y = groundLevel;
            world.hills_R.add(new Hill(x, y, spawnDistance));
            hills_RTimer = MathUtils.random(1f, 4f - global_Multiplier * 0.3f);
        }
    }

    public void removeClouds() {
        removeCloud(world.clouds_LUp);
        removeCloud(world.clouds_LDown);
        removeCloud(world.clouds_RUp);
        removeCloud(world.clouds_RDown);
    }

    public void removeTrees() {
        removeTree(world.trees_L);
        removeTree(world.trees_R);
    }

    public void removeLakes() {
        removeLake(world.lakes_L);
        removeLake(world.lakes_R);
    }

    public void removeHills() {
        removeHill(world.hills_L);
        removeHill(world.hills_R);
    }

    public void removeRing() {
        if(!world.rings.isEmpty() && world.rings.get(0).decal.getPosition().z < camera.position.z) {
            if(!world.rings.get(0).isCollected || world.rings.get(0).pfx_speed_up.isComplete()) {
                world.rings.get(0).dispose();
                world.rings.remove(0);

                int i = world.trees_R.size()+world.trees_L.size() + world.clouds_RDown.size() +world.clouds_RUp.size()+world.clouds_LDown.size()+world.clouds_LUp.size()
                        +world.hills_L.size()+world.hills_R.size();

                System.out.println("Decals: " + i);
            }
        }
    }

    public void removePowerup() {
        if(world.balloons.size() > 1 && world.balloons.get(0).decal.getPosition().z < camera.position.z) {
            if(!world.balloons.get(0).isCollected) {
                world.balloons.remove(0);
            }
        }
    }

    public void removeCloud(ArrayList<Cloud> cloudArray) {
        if(!cloudArray.isEmpty() && cloudArray.get(0).decal.getPosition().z < camera.position.z) {
            if(!cloudArray.get(0).isCollided || cloudArray.get(0).pfx_dispersion.isComplete()) {
                cloudArray.get(0).dispose();
                cloudArray.remove(0);
            }
        }
    }

    public void removeTree(ArrayList<Tree> treeArray) {

        if(!world.trees_L.isEmpty()) {
            if (!world.lakes_L.isEmpty() && world.trees_L.get(world.trees_L.size() - 1).position.dst(world.lakes_L.get(world.lakes_L.size() - 1).position) < world.lakes_L.get(world.lakes_L.size() - 1).width / 1.75f
                    || !world.lakes_R.isEmpty() && world.trees_L.get(world.trees_L.size() - 1).position.dst(world.lakes_R.get(world.lakes_R.size() - 1).position) < world.lakes_R.get(world.lakes_R.size() - 1).width / 1.75f) {
                world.trees_L.remove(world.trees_L.size() - 1);
                trees_LRemoveTimer = 0.25f;
            }
        }

        if(!world.trees_R.isEmpty()) {
            if (!world.lakes_R.isEmpty() && world.trees_R.get(world.trees_R.size() - 1).position.dst(world.lakes_R.get(world.lakes_R.size() - 1).position) < world.lakes_R.get(world.lakes_R.size() - 1).width / 1.75f
                    || !world.lakes_L.isEmpty() && world.trees_R.get(world.trees_R.size() - 1).position.dst(world.lakes_L.get(world.lakes_L.size() - 1).position) < world.lakes_L.get(world.lakes_L.size() - 1).width / 1.75f) {
                world.trees_R.remove(world.trees_R.size() - 1);
                trees_RRemoveTimer = 0.25f;
            }
        }

        if(!world.hills_L.isEmpty()) {
            pos = (world.hills_L.get(world.hills_L.size() - 1).position.cpy());
            pos.y -= world.hills_L.get(world.hills_L.size() - 1).height / 2f;

            if (!world.trees_L.isEmpty() && world.trees_L.get(world.trees_L.size() - 1).position.z > world.hills_L.get(world.hills_L.size() - 1).position.z && world.trees_L.get(world.trees_L.size() - 1).position.dst(pos) < world.hills_L.get(world.hills_L.size() - 1).width / 1.15f) {
                world.trees_L.remove(world.trees_L.size() - 1);
                trees_LRemoveTimer = 0.25f;
            }
            if (!world.trees_R.isEmpty() && world.trees_R.get(world.trees_R.size() - 1).position.z > world.hills_L.get(world.hills_L.size() - 1).position.z && world.trees_R.get(world.trees_R.size() - 1).position.dst(pos) < world.hills_L.get(world.hills_L.size() - 1).width / 1.25f) {
                world.trees_R.remove(world.trees_R.size() - 1);
                trees_RRemoveTimer = 0.25f;
            }
        }

        if(!world.hills_R.isEmpty()) {
            pos = world.hills_R.get(world.hills_R.size() - 1).position.cpy();
            pos.y -= world.hills_R.get(world.hills_R.size() - 1).height / 2f;

            if (!world.trees_R.isEmpty() && world.trees_R.get(world.trees_R.size() - 1).position.z > world.hills_R.get(world.hills_R.size() - 1).position.z && world.trees_R.get(world.trees_R.size() - 1).position.dst(pos) < world.hills_R.get(world.hills_R.size() - 1).width / 1.15f) {
                world.trees_R.remove(world.trees_R.size() - 1);
                trees_RRemoveTimer = 0.25f;
            }
            if (!world.trees_L.isEmpty() && world.trees_L.get(world.trees_L.size() - 1).position.z > world.hills_R.get(world.hills_R.size() - 1).position.z && world.trees_L.get(world.trees_L.size() - 1).position.dst(pos) < world.hills_R.get(world.hills_R.size() - 1).width / 1.15f) {
                world.trees_L.remove(world.trees_L.size() - 1);
                trees_LRemoveTimer = 0.25f;
            }
        }

        if(!treeArray.isEmpty() && treeArray.get(0).decal.getPosition().z < camera.position.z) {
            treeArray.remove(0);
        }
    }

    public void removeLake(ArrayList<Lake> lakeArray) {
        if(!lakeArray.isEmpty() && lakeArray.get(0).decal.getPosition().z < camera.position.z) {
            lakeArray.remove(0);
        }
    }

    public void removeHill(ArrayList<Hill> hillArray) {
        if(!world.hills_L.isEmpty()) {
            if (!world.lakes_L.isEmpty() && world.hills_L.get(world.hills_L.size() - 1).position.dst(world.lakes_L.get(world.lakes_L.size() - 1).position) < world.lakes_L.get(world.lakes_L.size() - 1).width
                    || !world.lakes_R.isEmpty() && world.lakes_R.get(world.lakes_R.size()-1).stateTime < 1f && world.hills_L.get(world.hills_L.size() - 1).position.dst(world.lakes_R.get(world.lakes_R.size() - 1).position) < world.lakes_R.get(world.lakes_R.size() - 1).width) {

                world.hills_L.remove(world.hills_L.size() - 1);
                hills_LRemoveTimer = 1f;
            }
        }

        if(!world.hills_R.isEmpty()) {
            if (!world.lakes_L.isEmpty() && world.hills_R.get(world.hills_R.size() - 1).position.dst(world.lakes_L.get(world.lakes_L.size() - 1).position) < world.lakes_L.get(world.lakes_L.size() - 1).width
                    || !world.lakes_R.isEmpty() && world.lakes_R.get(world.lakes_R.size()-1).stateTime < 1f && world.hills_R.get(world.hills_R.size() - 1).position.dst(world.lakes_R.get(world.lakes_R.size() - 1).position) < world.lakes_R.get(world.lakes_R.size() - 1).width) {

                world.hills_R.remove(world.hills_R.size() - 1);
                hills_RRemoveTimer = 1f;
            }
        }

        if(!hillArray.isEmpty() && hillArray.get(0).decal.getPosition().z < camera.position.z) {
            hillArray.remove(0);
        }
    }

    public void updateWorldParticles(float delta) {
        world.pfx_speed_lines.setPosition(SCREEN_WIDTH/2f, SCREEN_HEIGHT/2f);
        world.pfx_speed_lines.getEmitters().get(0).getTransparency().setHigh(Math.abs(player.velocity.z / (global_Speed - 5.5f*3f) - 0.54f) * 1f);
        world.pfx_speed_lines.update(delta);
        if(gameState == END) world.pfx_speed_lines.allowCompletion();
    }

    public void spawnStartObjects() {
        for (int j = 100; j < 220; j += MathUtils.random(30,40)) {               //Z Depth step
            for (int i = -100; i < 100; i += MathUtils.random(15, 50)) {         //X step
                if(world instanceof  WorldForest || world instanceof WorldTundra) world.trees_L.add(new Tree(i, groundLevel, j));
                if ( i < -10 || i > 10 ) {
                    world.clouds_LDown.add(new Cloud(i, MathUtils.random(0, 8), j)); //Clouds
                }
            }
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
