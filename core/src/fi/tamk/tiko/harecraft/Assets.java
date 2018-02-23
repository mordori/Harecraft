package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Mika on 23.2.2018.
 */

public class Assets {
    static Texture tex_backgroundTest;

    public static void load() {
        tex_backgroundTest = loadTexture("tex_backgroundTest.png");
    }

    public static Texture loadTexture(String path) {return new Texture(Gdx.files.internal(path));}

    public static TextureAtlas loadTextureAtlas(String path) {return new TextureAtlas(Gdx.files.internal(path));}

    public static Sound loadSound(String path) {return Gdx.audio.newSound(Gdx.files.internal(path));}

    public static Music loadMusic(String path) {return Gdx.audio.newMusic(Gdx.files.internal(path));}

    public static void flip(Animation<TextureRegion> animation, int frames) {
        TextureRegion regions;

        for(int i = 0; i < frames; i++) {
            regions = animation.getKeyFrame(i * animation.getFrameDuration());
            regions.flip(true, false);
        }
    }


    public static void dispose() {
        tex_backgroundTest.dispose();
    }
}
