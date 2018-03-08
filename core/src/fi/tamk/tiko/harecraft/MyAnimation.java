package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Mika on 09/03/2018.
 */

public class MyAnimation<T> extends Animation {

    boolean isFlipped = false;

    public MyAnimation(float frameDuration, Array<? extends T> keyFrames) {
        super(frameDuration, keyFrames);
    }
}
