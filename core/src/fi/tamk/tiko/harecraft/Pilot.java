package fi.tamk.tiko.harecraft;

/**
 * Created by Mika on 28/02/2018.
 */

abstract class Pilot extends GameObject {
    enum State {
        NORMAL
    }

    float distance;

    public Pilot() {
        State state = State.NORMAL;
    }
}
