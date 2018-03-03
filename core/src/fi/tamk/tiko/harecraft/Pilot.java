package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;

import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.WorldBuilder.spawnDistance;

/**
 * Created by Mika on 28/02/2018.
 */

abstract class Pilot extends GameObject {
    enum State {
        NORMAL
    }

    ParticleEffect pfx_scarf;

    float distance;
    boolean isDrawing;

    public Pilot() {
        State state = State.NORMAL;
    }

    public void update(float delta) {
        super.update(delta);
        distance += -velocity.z * delta;

        if(position.z < ((this instanceof Opponent) ? spawnDistance/10f : spawnDistance/50f)) isDrawing = true;
        else if(opacity != 0f) isDrawing = false;

        if(this instanceof Opponent && gameState == END) isDrawing = false;

        if(isDrawing) opacity += delta;
        else opacity -= delta;

        if(opacity > 1f) opacity = 1f;
        if(opacity < 0f) opacity = 0f;
        decal.setColor(1f,1f,1f, opacity);
    }

    public void dispose() {
        pfx_scarf.dispose();
    }
}
