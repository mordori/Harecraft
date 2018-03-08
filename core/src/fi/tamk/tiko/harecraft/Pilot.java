package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;

import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.RACE;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_HEIGHT;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.GameScreen.camera;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.GameScreen.gameStateTime;
import static fi.tamk.tiko.harecraft.GameScreen.global_Multiplier;
import static fi.tamk.tiko.harecraft.GameScreen.global_Speed;
import static fi.tamk.tiko.harecraft.WorldBuilder.spawnDistance;

/**
 * Created by Mika on 28/02/2018.
 */

abstract class Pilot extends GameObject {
    enum State {
        NORMAL
    }

    float distance;
    float acceleration;
    float speed;
    float drawDistance;
    boolean isDrawing;

    public Pilot() {
        State state = State.NORMAL;
    }

    public void update(float delta) {
        super.update(delta);

        //DISTANCE
        if(this instanceof Opponent) distance += -(global_Speed - speed) * delta;
        else distance += -velocity.z * delta;

        //DRAWING
        if(position.z < drawDistance) isDrawing = true;
        else if(opacity != 0f) isDrawing = false;
        if(this instanceof Opponent && gameState == END) isDrawing = false;

        //OPACITY
        if(isDrawing) opacity += this instanceof Opponent ? delta : delta * 0.4f;
        else opacity -= delta;
        if(opacity > 1f) opacity = 1f;
        else if(opacity < 0f) opacity = 0f;
        decal.setColor(1f,1f,1f, opacity);
    }

    public void updateParticles(float delta) {}

    public void dispose() {}
}
