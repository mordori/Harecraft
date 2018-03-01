package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;

/**
 * Created by Mika on 28/02/2018.
 */

abstract class Pilot extends GameObject {
    enum State {
        NORMAL
    }

    ParticleEffect pfx_scarf;

    float distance;

    public Pilot() {
        State state = State.NORMAL;
    }

    public void dispose() {
        pfx_scarf.dispose();
    }
}
