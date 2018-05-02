package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

import static fi.tamk.tiko.harecraft.GameMain.orthoCamera;
import static fi.tamk.tiko.harecraft.GameScreen.SCREEN_WIDTH;
import static fi.tamk.tiko.harecraft.WorldRenderer.radius;

/**
 * Created by Mika on 5.4.2018.
 *
 * Class that handles 2D Shaders used in conjuction with an orthographic camera.
 */

public class Shaders2D {
    static ShaderProgram shader2D_vignette;
    static ShaderProgram shader2D_default;
    static ShaderProgram shader2D_blur;
    static ShaderProgram shader2D_luminance;

    public static void create2DShaders() {
        createShader2D_Default();
        createShader2D_Vignette();
        createShader2D_Blur();
        createShader2D_Luminance();

        shader2D_blur.begin();
        shader2D_blur.setUniformMatrix("u_projTrans", orthoCamera.combined);
        shader2D_blur.setUniformi("u_texture", 0);
        shader2D_blur.setUniformf("dir", 0f, 0f);
        shader2D_blur.setUniformf("resolution", SCREEN_WIDTH);
        shader2D_blur.setUniformf("radius", radius);
        shader2D_blur.end();

        shader2D_luminance.begin();
        shader2D_luminance.setUniformMatrix("u_projTrans", orthoCamera.combined);
        shader2D_luminance.setUniformi("u_texture", 0);
        shader2D_luminance.setUniformf("brightPassThreshold",0.5f);
        shader2D_luminance.end();
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

    private static void createShader2D_Blur() {
        FileHandle VERTEX = Gdx.files.internal("shaders/shader2D_blur_vertex.txt");
        FileHandle FRAGMENT = Gdx.files.internal("shaders/shader2D_blur_fragment.txt");
        shader2D_blur = new ShaderProgram(VERTEX, FRAGMENT);
        if(!shader2D_blur.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader2D_blur.getLog());
    }

    private static void createShader2D_Luminance() {
        FileHandle VERTEX = Gdx.files.internal("shaders/shader2D_luminance_vertex.txt");
        FileHandle FRAGMENT = Gdx.files.internal("shaders/shader2D_luminance_fragment.txt");
        shader2D_luminance = new ShaderProgram(VERTEX, FRAGMENT);
        if(!shader2D_luminance.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader2D_luminance.getLog());
    }

    public static void dispose() {
        if(shader2D_default != null) shader2D_default.dispose();
        if(shader2D_vignette != null) shader2D_vignette.dispose();
        if(shader2D_blur != null) shader2D_blur.dispose();
        if(shader2D_luminance != null) shader2D_luminance.dispose();
    }
}
