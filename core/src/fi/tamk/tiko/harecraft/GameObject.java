package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Minuet on 23.2.2018.
 */

public class GameObject extends Decal {

    Vector3 position;
    float width, height;

    public GameObject(Vector3 position) {
        this.position = position;
    }

    public void update() {
        super.update();
    }
}
