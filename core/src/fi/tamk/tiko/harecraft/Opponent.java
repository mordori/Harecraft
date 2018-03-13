package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;

import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.GameScreen.gameStateTime;
import static fi.tamk.tiko.harecraft.GameScreen.global_Multiplier;
import static fi.tamk.tiko.harecraft.GameScreen.global_Speed;
import static fi.tamk.tiko.harecraft.World.player;
import static fi.tamk.tiko.harecraft.WorldBuilder.spawnDistance;

/**
 * Created by Mika on 28/02/2018.
 */

public class Opponent extends Pilot {
    float spawnZ;
    Decal decal_playerTag;

    public Opponent(float x, float y, float z, float spawnZ, int color, int planetype, int character, float speed) {
        drawDistance = spawnDistance / 5f;
        this.spawnZ = spawnZ;
        this.speed = speed;

        TextureRegion texR_body;
        TextureRegion texR_wings;
        TextureRegion texR_head;

        switch (color) {
            case COLOR_RED:
                texR_body = Assets.texR_plane_2_red_body;
                texR_wings = Assets.texR_plane_2_red_wings;
                break;
            case COLOR_BLUE:
                texR_body = Assets.texR_plane_2_blue_body;
                texR_wings = Assets.texR_plane_2_blue_wings;
                break;
            case COLOR_ORANGE:
                texR_body = Assets.texR_plane_2_orange_body;
                texR_wings = Assets.texR_plane_2_orange_wings;
                break;
                case COLOR_PINK:
                texR_body = Assets.texR_plane_2_pink_body;
                texR_wings = Assets.texR_plane_2_pink_wings;
                break;
            default:
                texR_body = Assets.texR_plane_2_red_body;
                texR_wings = Assets.texR_plane_2_red_wings;
        }

        switch (character) {
            case CHARACTER_DEF:
                texR_head = Assets.texR_character_default_head;
                break;
            case CHARACTER_HARE:
                texR_head = Assets.texR_character_hare_head;
                break;
            default:
                texR_head = Assets.texR_character_default_head;
        }

        width = texR_body.getRegionWidth() / 50f;
        height = texR_body.getRegionHeight() / 50f;
        decal = Decal.newDecal(width, height, texR_body,true);
        decal.setPosition(x,y,z);

        width = texR_head.getRegionWidth() / 50f;
        height = texR_head.getRegionHeight() / 50f;
        decal_head = Decal.newDecal(width, height, texR_head,true);
        decal_head.setPosition(x,y,z+0.1f);

        width = texR_wings.getRegionWidth() / 50f;
        height = texR_wings.getRegionHeight() / 50f;
        decal_wings = Decal.newDecal(width, height, texR_wings,true);
        decal_wings.setPosition(x,y,z+0.2f);

        decal_playerTag = Decal.newDecal(Assets.texR_playertag.getRegionWidth()/50f,Assets.texR_playertag.getRegionHeight()/50f, Assets.texR_playertag,true);
    }

    public void update(float delta) {
        super.update(delta);

        decal_playerTag.setColor(1f,1f,1f, opacity);

        if(gameState == START) velocity.z = 9f * stateTime;
        else velocity.z = speed - global_Multiplier * 3f;

        if(gameState == RACE && gameStateTime == 0f) {
            distance = player.distance + spawnZ;
        }

        if(distance > World.end) {
            acceleration += delta * 2f;
            decal.translateZ(-(global_Speed - speed) * delta * acceleration);
            decal.translateY(-(global_Speed - speed) * delta * (acceleration / 10f));
        }
        else moveZ(delta);

        decal_head.setRotation(decal.getRotation().cpy().exp(2f));
        decal_head.setPosition(decal.getPosition().x, decal.getPosition().y,decal.getPosition().z + 0.07f);
        decal_wings.setRotation(decal.getRotation());
        decal_wings.setPosition(decal.getPosition().x, decal.getPosition().y,decal.getPosition().z + 0.14f);

        decal_playerTag.setPosition(decal.getPosition().x, decal.getPosition().y + 1f, decal.getPosition().z);
    }

    public void dispose() {
        super.dispose();
    }
}
