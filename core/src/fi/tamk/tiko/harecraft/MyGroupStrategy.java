package fi.tamk.tiko.harecraft;

import java.util.Comparator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalMaterial;
import com.badlogic.gdx.graphics.g3d.decals.GroupStrategy;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;

import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE0;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE1;
import static com.badlogic.gdx.graphics.GL20.GL_TEXTURE2;
import static fi.tamk.tiko.harecraft.GameScreen.GameState.START;



public class MyGroupStrategy implements GroupStrategy, Disposable {
    private static final int GROUP_OPAQUE = 0;
    private static final int GROUP_BLEND = 1;

    Pool<Array<Decal>> arrayPool = new Pool<Array<Decal>>(16) {
        @Override
        protected Array<Decal> newObject () {
            return new Array();
        }
    };
    Array<Array<Decal>> usedArrays = new Array<Array<Decal>>();
    ObjectMap<DecalMaterial, Array<Decal>> materialGroups = new ObjectMap<DecalMaterial, Array<Decal>>();

    Camera camera;
    ShaderProgram shader;
    private final Comparator<Decal> cameraSorter;

    public MyGroupStrategy (final Camera camera) {
        this(camera, new Comparator<Decal>() {
            @Override
            public int compare (Decal o1, Decal o2) {
                float dist1 = camera.position.dst(o1.getPosition());
                float dist2 = camera.position.dst(o2.getPosition());
                return (int)Math.signum(dist2 - dist1);
            }
        });
    }

    public MyGroupStrategy (Camera camera, Comparator<Decal> sorter) {
        this.camera = camera;
        this.cameraSorter = sorter;
        createDefaultShader();

        ShaderProgram.pedantic = false;
        createShader_Vignette();
        createShader_Sea();

        shader_sea.begin();
        shader_sea.setUniformi("u_texture1", 1);
        shader_sea.setUniformi("u_mask", 2);
        shader_sea.setUniformf("time", GameScreen.tick);
        shader_sea.end();

        Gdx.gl.glActiveTexture(GL_TEXTURE2);
        World.mask.bind();
        Gdx.gl.glActiveTexture(GL_TEXTURE1);
        World.tex1.bind();
        Gdx.gl.glActiveTexture(GL_TEXTURE0);
        World.tex0.bind();
    }

    public void setCamera (Camera camera) {
        this.camera = camera;
    }

    public Camera getCamera () {
        return camera;
    }

    @Override
    public int decideGroup (Decal decal) {
        return decal.getMaterial().isOpaque() ? GROUP_OPAQUE : GROUP_BLEND;
    }

    @Override
    public void beforeGroup (int group, Array<Decal> contents) {
        if (group == GROUP_BLEND) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            contents.sort(cameraSorter);
        } else {
            for (int i = 0, n = contents.size; i < n; i++) {
                Decal decal = contents.get(i);
                Array<Decal> materialGroup = materialGroups.get(decal.getMaterial());
                if (materialGroup == null) {
                    materialGroup = arrayPool.obtain();
                    materialGroup.clear();
                    usedArrays.add(materialGroup);
                    materialGroups.put(decal.getMaterial(), materialGroup);
                }
                materialGroup.add(decal);
            }

            contents.clear();
            for (Array<Decal> materialGroup : materialGroups.values()) {
                contents.addAll(materialGroup);
            }

            materialGroups.clear();
            arrayPool.freeAll(usedArrays);
            usedArrays.clear();
        }
    }

    @Override
    public void afterGroup (int group) {
        if (group == GROUP_BLEND) {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    @Override
    public void beforeGroups () {
        switch(activeShader) {
            case SHADER_VIGNETTE:
                shader_vignette.begin();
                shader_vignette.setUniformMatrix("u_projectionViewMatrix", camera.combined);
                shader_vignette.setUniformi("u_texture", 0);
                if (GameScreen.gameStateTime > 2f && GameScreen.gameState == START)
                    shader_vignette.setUniformf("u_stateTime", (GameScreen.gameStateTime - 2f) / 5f);
                break;
            case SHADER_SEA:
                shader_sea.begin();
                shader_sea.setUniformMatrix("u_projectionViewMatrix", camera.combined);
                shader_sea.setUniformi("u_texture", 0);
                shader_sea.setUniformi("u_mask", 2);
                break;
            default:
                shader.begin();
                shader.setUniformMatrix("u_projectionViewMatrix", camera.combined);
                shader.setUniformi("u_texture", 0);
                break;
        }
    }

    @Override
    public void afterGroups () {
        switch(activeShader) {
            case SHADER_VIGNETTE:
                shader_vignette.end();
                break;
            case SHADER_SEA:
                shader_sea.end();
                break;
            default:
                shader.end();
                break;
        }
    }

    private void createDefaultShader () {
        String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "uniform mat4 u_projectionViewMatrix;\n" //
                + "varying vec4 v_color;\n" //
                + "varying vec2 v_texCoords;\n" //
                + "\n" //
                + "void main()\n" //
                + "{\n" //
                + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "   v_color.a = v_color.a * (255.0/254.0);\n" //
                + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "   gl_Position =  u_projectionViewMatrix * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "}\n";
        String fragmentShader = "#ifdef GL_ES\n" //
                + "precision mediump float;\n" //
                + "#endif\n" //
                + "varying vec4 v_color;\n" //
                + "varying vec2 v_texCoords;\n" //
                + "uniform sampler2D u_texture;\n" //
                + "void main()\n"//
                + "{\n" //
                + "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" //
                + "}";

        shader = new ShaderProgram(vertexShader, fragmentShader);
        if (shader.isCompiled() == false) throw new IllegalArgumentException("couldn't compile shader: " + shader.getLog());
    }

    @Override
    public ShaderProgram getGroupShader (int group) {
        switch(activeShader) {
            case SHADER_VIGNETTE:
                return shader_vignette;
            case SHADER_SEA:
                return shader_sea;
            default:
                return shader;
        }
    }

    @Override
    public void dispose () {
        if(shader != null) shader.dispose();
        if(shader_vignette != null) shader_vignette.dispose();
        if(shader_sea != null) shader_sea.dispose();
    }

    private void createShader_Vignette() {
        FileHandle VERTEX = Gdx.files.internal("shaders/shader_vignette_vertex.txt");
        FileHandle FRAGMENT = Gdx.files.internal("shaders/shader_vignette_fragment.txt");
        shader_vignette = new ShaderProgram(VERTEX, FRAGMENT);
        if(!shader_vignette.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader_vignette.getLog());
    }
    private void createShader_Sea() {
        FileHandle VERTEX = Gdx.files.internal("shaders/shader_sea_vertex.txt");
        FileHandle FRAGMENT = Gdx.files.internal("shaders/shader_sea_fragment.txt");
        shader_sea = new ShaderProgram(VERTEX, FRAGMENT);
        if(!shader_sea.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader_sea.getLog());
    }

    public static int activeShader;
    public static final int SHADER_DEFAULT = 0;
    public static final int SHADER_VIGNETTE = 1;
    public static final int SHADER_SEA = 2;

    static ShaderProgram shader_vignette;
    static ShaderProgram shader_sea;
}


