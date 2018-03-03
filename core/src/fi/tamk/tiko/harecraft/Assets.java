package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


/**
 * Created by Mika on 23.2.2018.
 */

public class Assets {
    static TextureRegion texR_background;
    static TextureRegion texR_foreground;
    static TextureRegion texR_sun;
    static TextureRegion texR_finishline;

    static TextureRegion texR_player;
    static TextureRegion texR_opponent_yellow;
    static TextureRegion texR_cloud;
    static TextureRegion texR_ring;
    static TextureRegion texR_tree;
    static TextureRegion texR_lake;

    static Music music_default;

    static Sound sound_cloud_hit;
    static Sound sound_ring_collected;

    static ParticleEffect pfx_scarf;

    public static void load() {
        texR_background = new TextureRegion(loadTexture("textures/tex_background.png"));
        texR_foreground = new TextureRegion(loadTexture("textures/tex_foreground.png"));
        texR_sun = new TextureRegion(loadTexture("textures/tex_sun.png"));
        texR_finishline = new TextureRegion(loadTexture("textures/tex_finishline.png"));
        texR_finishline.flip(true,false);

        texR_player = new TextureRegion(loadTexture("textures/tex_plane_red.png"));
        texR_opponent_yellow = new TextureRegion(loadTexture("textures/tex_plane_yellow.png"));
        texR_cloud = new TextureRegion(loadTexture("textures/tex_cloud.png"));
        texR_ring = new TextureRegion(loadTexture("textures/tex_ring.png"));
        texR_tree = new TextureRegion(loadTexture("textures/tex_tree.png"));
        texR_lake = new TextureRegion(loadTexture("textures/tex_lake.png"));

        music_default = loadMusic("sound/elevator.wav");
        music_default.setLooping(true);

        sound_cloud_hit = loadSound("sound/Boup.wav");
        sound_ring_collected = loadSound("sound/Spring.wav");

        pfx_scarf = new ParticleEffect();
        pfx_scarf.load(Gdx.files.internal("pfx_scarf"), Gdx.files.internal(""));

    }

    public static Texture loadTexture(String path) {return new Texture(Gdx.files.internal(path));}

    public static TextureAtlas loadTextureAtlas(String path) {return new TextureAtlas(Gdx.files.internal(path));}

    public static Sound loadSound(String path) {return Gdx.audio.newSound(Gdx.files.internal(path));}

    public static Music loadMusic(String path) {return Gdx.audio.newMusic(Gdx.files.internal(path));}

    public static Music loadParticles(String path) {return Gdx.audio.newMusic(Gdx.files.internal(path));}


    public static void flip(Animation<TextureRegion> animation, int frames) {
        TextureRegion regions;

        for(int i = 0; i < frames; i++) {
            regions = animation.getKeyFrame(i * animation.getFrameDuration());
            regions.flip(true, false);
        }
    }

    public static void dispose() {
        texR_background.getTexture().dispose();
        texR_foreground.getTexture().dispose();
        texR_sun.getTexture().dispose();
        texR_finishline.getTexture().dispose();

        texR_player.getTexture().dispose();
        texR_opponent_yellow.getTexture().dispose();
        texR_cloud.getTexture().dispose();
        texR_ring.getTexture().dispose();
        texR_tree.getTexture().dispose();
        texR_lake.getTexture().dispose();

        sound_cloud_hit.dispose();
        sound_ring_collected.dispose();

        music_default.dispose();

        pfx_scarf.dispose();
    }
}
