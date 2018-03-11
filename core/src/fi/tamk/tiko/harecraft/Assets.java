package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * Created by Mika on 23.2.2018.
 */

public class Assets {
    static TextureRegion texR_background;
    static TextureRegion texR_foreground;
    static TextureRegion texR_sun;
    static TextureRegion texR_airbuoy;
    static TextureRegion texR_player_plane_body;
    static TextureRegion texR_player_plane_wings;
    static TextureRegion texR_player_plane_head;
    static TextureRegion texR_opponent_yellow;
    static TextureRegion texR_cloud;
    static TextureRegion texR_ring;
    static TextureRegion texR_tree_big_light;
    static TextureRegion texR_tree_big_dark;
    static TextureRegion texR_tree_small_light;
    static TextureRegion texR_tree_small_dark;
    static TextureRegion texR_lake;
    static TextureRegion texR_hill;

    static TextureAtlas test_atlas;
    static MyAnimation<TextureRegion> animation_player_scarf;

    static Music music_course_1;

    static Sound sound_cloud_hit;
    static Sound sound_ring_collected;
    static Sound sound_countdown;
    static Sound sound_airplane_engine;

    static ParticleEffect pfx_scarf;
    static ParticleEffect pfx_stream;

    static BitmapFont font;

    public static void load() {
        texR_background = loadTextureRegion("tex_background.png");
        texR_foreground = loadTextureRegion("tex_foreground.png");
        texR_sun = loadTextureRegion("tex_sun.png");
        texR_airbuoy = loadTextureRegion("tex_airbuoy.png");
        texR_player_plane_body = loadTextureRegion("tex_player_plane_body.png");
        texR_player_plane_wings = loadTextureRegion("tex_player_plane_wings.png");
        texR_player_plane_head = loadTextureRegion("tex_player_plane_head.png");
        texR_opponent_yellow = loadTextureRegion("tex_plane_yellow.png");
        texR_cloud = loadTextureRegion("tex_cloud.png");
        texR_ring = loadTextureRegion("tex_ring.png");
        texR_tree_big_light = loadTextureRegion("tex_tree_big_light.png");
        texR_tree_big_dark = loadTextureRegion("tex_tree_big_dark.png");
        texR_tree_small_light = loadTextureRegion("tex_tree_small_light.png");
        texR_tree_small_dark = loadTextureRegion("tex_tree_small_dark.png");
        texR_lake = loadTextureRegion("tex_lake.png");
        texR_hill = loadTextureRegion("tex_hill.png");

        test_atlas = loadTextureAtlas("atlas_animation_player_scarf.txt");
        animation_player_scarf = new MyAnimation<TextureRegion>(1f/15f, test_atlas.getRegions());
        flip(animation_player_scarf, animation_player_scarf.getKeyFrames().length);

        music_course_1 = loadMusic("music_course_1.mp3");
        music_course_1.setLooping(true);

        sound_cloud_hit = loadSound("sound_cloud_hit.wav");
        sound_ring_collected = loadSound("sound_ring_collected.wav");
        sound_countdown = loadSound("sound_countdown.wav");
        sound_airplane_engine = loadSound("sound_airplane_engine.wav");

        pfx_scarf = new ParticleEffect();
        pfx_scarf.load(Gdx.files.internal("particles/pfx_scarf"), Gdx.files.internal("particles/"));

        pfx_stream = new ParticleEffect();
        pfx_stream.load(Gdx.files.internal("particles/pfx_stream"), Gdx.files.internal("particles/"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("foo.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 128;
        parameter.borderWidth = 4;
        parameter.color = new Color(1f,0.6f,0f,1f);
        font = generator.generateFont(parameter);
        generator.dispose();
    }

    public static TextureRegion loadTextureRegion(String path) {return flip(new TextureRegion(new Texture(Gdx.files.internal("textures/" + path))));}

    public static TextureAtlas loadTextureAtlas(String path) {return new TextureAtlas(Gdx.files.internal("textures/" + path));}

    public static Sound loadSound(String path) {return Gdx.audio.newSound(Gdx.files.internal("audio/" + path));}

    public static Music loadMusic(String path) {return Gdx.audio.newMusic(Gdx.files.internal("audio/" + path));}

    public static void flip(Animation<TextureRegion> animation, int frames) {
        TextureRegion regions;

        for(int i = 0; i < frames; i++) {
            regions = animation.getKeyFrame(i * animation.getFrameDuration());
            regions.flip(true, false);
        }
    }

    public static TextureRegion flip(TextureRegion texR) {
        texR.flip(true, false);
        return texR;
    }

    public static void dispose() {
        texR_background.getTexture().dispose();
        texR_foreground.getTexture().dispose();
        texR_sun.getTexture().dispose();
        texR_airbuoy.getTexture().dispose();
        texR_player_plane_body.getTexture().dispose();
        texR_player_plane_wings.getTexture().dispose();
        texR_player_plane_head.getTexture().dispose();
        texR_opponent_yellow.getTexture().dispose();
        texR_cloud.getTexture().dispose();
        texR_ring.getTexture().dispose();
        texR_tree_big_light.getTexture().dispose();
        texR_tree_big_dark.getTexture().dispose();
        texR_tree_small_light.getTexture().dispose();
        texR_tree_small_dark.getTexture().dispose();
        texR_lake.getTexture().dispose();
        texR_hill.getTexture().dispose();

        test_atlas.dispose();

        music_course_1.dispose();

        sound_cloud_hit.dispose();
        sound_ring_collected.dispose();
        sound_countdown.dispose();
        sound_airplane_engine.dispose();

        pfx_scarf.dispose();
        pfx_stream.dispose();

        font.dispose();
    }
}
