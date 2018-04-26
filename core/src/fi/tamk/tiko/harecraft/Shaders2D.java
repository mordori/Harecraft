package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Created by Mika on 5.4.2018.
 *
 * Class that handles 2D Shaders used in conjuction with an orthographic camera.
 */

public class Shaders2D {
    static ShaderProgram shader2D_vignette;
    static ShaderProgram shader2D_default;

    public static void create2DShaders() {
        createShader2D_Default();
        createShader2D_Vignette();
    }

    private static void createShader2D_Default() {
        FileHandle VERTEX = Gdx.files.internal("shaders/shader2D_default_vertex.txt");
        FileHandle FRAGMENT = Gdx.files.internal("shaders/shader2D_default_fragment.txt");
        shader2D_default = new ShaderProgram(VERTEX, FRAGMENT);
        if(!shader2D_default.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader2D_default.getLog());
    }

    private static void createShader2D_Vignette() {
        FileHandle VERTEX = Gdx.files.internal("shaders/shader2D_vignette_vertex.txt");
        FileHandle FRAGMENT = Gdx.files.internal("shaders/shader2D_vignette_fragment.txt");
        shader2D_vignette = new ShaderProgram(VERTEX, FRAGMENT);
        if(!shader2D_vignette.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader2D_vignette.getLog());
    }

    public static void dispose() {
        if(shader2D_default != null) shader2D_default.dispose();
        if(shader2D_vignette != null) shader2D_vignette.dispose();
    }
}
