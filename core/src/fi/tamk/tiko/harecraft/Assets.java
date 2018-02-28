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
    static TextureRegion texR_background;
    static TextureRegion texR_player;
    static TextureRegion texR_opponent_yellow;
    static TextureRegion texR_cloud;
    static TextureRegion texR_lifering;
    static TextureRegion texR_tree;

    static Music music_default;

    static Sound sound_cloud_hit;
    static Sound sound_lifering_collected;

    public static void load() {
        texR_background = new TextureRegion(loadTexture("textures/tex_background.png"));
        texR_player = new TextureRegion(loadTexture("textures/tex_plane.png"));
        texR_opponent_yellow = new TextureRegion(loadTexture("textures/tex_plane_yellow.png"));
        texR_cloud = new TextureRegion(loadTexture("textures/tex_cloud.png"));
        texR_lifering = new TextureRegion(loadTexture("textures/tex_lifering.png"));
        texR_tree = new TextureRegion(loadTexture("textures/tex_tree.png"));

        music_default = loadMusic("sound/elevator.wav");
        music_default.setLooping(true);

        sound_cloud_hit = loadSound("sound/Boup.wav");
        sound_lifering_collected = loadSound("sound/Spring.wav");
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
        texR_background.getTexture().dispose();
        texR_player.getTexture().dispose();
        texR_opponent_yellow.getTexture().dispose();
        texR_cloud.getTexture().dispose();
        texR_lifering.getTexture().dispose();
        texR_tree.getTexture().dispose();

        sound_cloud_hit.dispose();
        sound_lifering_collected.dispose();
    }
}
