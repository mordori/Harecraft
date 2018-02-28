package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Mika on 23.2.2018.
 */

abstract class GameObject {

    Decal decal;

    Vector3 velocity;
    Vector3 direction;
    Vector3 position;

    float stateTime;
    float opacity;
    float rotation;

    public GameObject() {

    }
}
