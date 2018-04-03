package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g3d.decals.Decal;

import static fi.tamk.tiko.harecraft.GameScreen.GameState.END;
import static fi.tamk.tiko.harecraft.GameScreen.gameState;
import static fi.tamk.tiko.harecraft.GameScreen.global_Speed;

/**
 * Created by Mika on 28/02/2018.
 */

abstract class Pilot extends GameObject {
    enum State {
        NORMAL
    }

    Decal decal_head;
    Decal decal_wings;

    float distance;
    float acceleration;
    float speed;
    float drawDistance;
    boolean isDrawing;

    static final int COLOR_RED = 0;
    static final int COLOR_BLUE = 1;
    static final int COLOR_ORANGE = 2;
    static final int COLOR_PINK = 3;

    static final int PLANE_1 = 1;
    static final int PLANE_2 = 2;
    static final int PLANE_3 = 3;

    static final int CHARACTER_DEF = 0;
    static final int CHARACTER_HARE = 1;


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
        decal_wings.setColor(1f,1f,1f, opacity);
        decal_head.setColor(1f,1f,1f, opacity);
    }

    @Override
    public void updateParticles(float delta) {}

    public void dispose() {}
}
