package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Mika on 23.2.2018.
 */

public class Assets {
    static TextureRegion texR_foreground;
    static TextureRegion texR_sun;
    static TextureRegion texR_hotairballoon;
    static TextureRegion texR_ribbons;
    static TextureRegion texR_playertag;

    static TextureRegion texR_speedometer;

    static TextureRegion texR_powerup_red;
    static TextureRegion texR_powerup_green;
    static TextureRegion texR_powerup_blue;

    static TextureRegion texR_plane_2_red_body;
    static TextureRegion texR_plane_2_red_wings;
    static TextureRegion texR_plane_2_orange_body;
    static TextureRegion texR_plane_2_orange_wings;
    static TextureRegion texR_plane_2_blue_body;
    static TextureRegion texR_plane_2_blue_wings;
    static TextureRegion texR_plane_2_pink_body;
    static TextureRegion texR_plane_2_pink_wings;

    static TextureRegion texR_character_hare_head;
    static TextureRegion texR_character_default_head;

    static TextureRegion texR_cloud;
    static TextureRegion texR_ring;
    static TextureRegion texR_ring_arrows;

    static TextureRegion texR_tree_big_light;
    static TextureRegion texR_tree_big_dark;
    static TextureRegion texR_tree_small_light;
    static TextureRegion texR_tree_small_dark;
    static TextureRegion texR_lake;
    static TextureRegion texR_hill;

    static TextureAtlas atlas_text_race_positions;
    static TextureAtlas atlas_text_race_states;
    static Array<Sprite> sprites_text_race_states;
    static Array<Sprite> sprites_text_race_positions;
    //static MyAnimation<TextureRegion> animation_text_race_states;

    static Music music_course_1;

    static Sound sound_cloud_hit;
    static Sound sound_ring_collected;
    static Sound sound_countdown;
    static Sound sound_airplane_engine;

    static ParticleEffect pfx_scarf;
    static ParticleEffect pfx_cloud_dispersion;
    static ParticleEffect pfx_balloon_hit;

    static BitmapFont font;

    public static void load() {
        texR_foreground = loadTextureRegion("tex_foreground_tundra.png");
        texR_sun = loadTextureRegion("tex_sun.png");
        texR_hotairballoon = loadTextureRegion("tex_hotairballoon.png");
        texR_ribbons = loadTextureRegion("tex_ribbons.png");
        texR_playertag = loadTextureRegion("tex_playertag.png");
        texR_speedometer = loadTextureRegion("tex_speedometer.png");

        texR_powerup_red = loadTextureRegion("tex_powerup_red.png");
        texR_powerup_green = loadTextureRegion("tex_powerup_green.png");
        texR_powerup_blue = loadTextureRegion("tex_powerup_blue.png");

        texR_plane_2_red_body = loadTextureRegion("tex_plane_2_red_body.png");
        texR_plane_2_red_wings = loadTextureRegion("tex_plane_2_red_wings.png");
        texR_plane_2_orange_body = loadTextureRegion("tex_plane_2_orange_body.png");
        texR_plane_2_orange_wings = loadTextureRegion("tex_plane_2_orange_wings.png");
        texR_plane_2_blue_body = loadTextureRegion("tex_plane_2_blue_body.png");
        texR_plane_2_blue_wings = loadTextureRegion("tex_plane_2_blue_wings.png");
        texR_plane_2_pink_body = loadTextureRegion("tex_plane_2_pink_body.png");
        texR_plane_2_pink_wings = loadTextureRegion("tex_plane_2_pink_wings.png");

        texR_character_default_head = loadTextureRegion("tex_character_default_head.png");
        texR_character_hare_head = loadTextureRegion("tex_character_hare_head.png");

        texR_cloud = loadTextureRegion("tex_cloud.png");
        texR_ring = loadTextureRegion("tex_ring.png");
        texR_ring_arrows = loadTextureRegion("tex_ring_arrows.png");

        texR_tree_big_light = loadTextureRegion("tex_tree_big_light.png");
        texR_tree_big_dark = loadTextureRegion("tex_tree_big_dark.png");
        texR_tree_small_light = loadTextureRegion("tex_tree_small_light.png");
        texR_tree_small_dark = loadTextureRegion("tex_tree_small_dark.png");
        texR_lake = loadTextureRegion("tex_lake.png");
        texR_hill = loadTextureRegion("tex_hill_tundra.png");

        atlas_text_race_positions = loadTextureAtlas("atlas_text_positions.txt");
        atlas_text_race_states = loadTextureAtlas("atlas_text_race_states.txt");
        //animation_player_scarf = new MyAnimation<TextureRegion>(1f/15f, test_atlas.getRegions());
        //flip(animation_player_scarf, animation_player_scarf.getKeyFrames().length);

        music_course_1 = loadMusic("music_course_1.mp3");
        music_course_1.setLooping(true);

        sound_cloud_hit = loadSound("sound_cloud_hit.wav");
        sound_ring_collected = loadSound("sound_ring_collected.wav");
        sound_countdown = loadSound("sound_countdown.wav");
        sound_airplane_engine = loadSound("sound_airplane_engine.wav");

        pfx_scarf = new ParticleEffect();
        pfx_scarf.load(Gdx.files.internal("particles/pfx_scarf"), Gdx.files.internal("particles/"));
        pfx_cloud_dispersion = new ParticleEffect();
        pfx_cloud_dispersion.load(Gdx.files.internal("particles/pfx_cloud_dispersion"), Gdx.files.internal("particles/"));
        pfx_balloon_hit = new ParticleEffect();
        pfx_balloon_hit.load(Gdx.files.internal("particles/pfx_balloon_hit"), Gdx.files.internal("particles/"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/foo.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 64;
        parameter.borderWidth = 4;
        parameter.color = new Color(1f,0.6f,0f,1f);
        font = generator.generateFont(parameter);
        generator.dispose();

        sprites_text_race_states = atlas_text_race_states.createSprites();
        sprites_text_race_positions = atlas_text_race_positions.createSprites();
    }

    public static Texture loadTexture(String path) {
        Texture texture = new Texture(Gdx.files.internal("textures/" + path));
        texture.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        return texture;
    }

    public static TextureRegion loadTextureRegion(String path) {return flip(new TextureRegion(loadTexture(path)));}

    public static TextureRegion flip(TextureRegion texR) {
        texR.flip(true, false);
        return texR;
    }

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

    public static void dispose() {
        texR_foreground.getTexture().dispose();
        texR_sun.getTexture().dispose();
        texR_hotairballoon.getTexture().dispose();
        texR_ribbons.getTexture().dispose();

        texR_playertag.getTexture().dispose();
        texR_speedometer.getTexture().dispose();

        texR_powerup_red.getTexture().dispose();
        texR_powerup_green.getTexture().dispose();
        texR_powerup_blue.getTexture().dispose();

        texR_plane_2_red_body.getTexture().dispose();
        texR_plane_2_red_wings.getTexture().dispose();
        texR_plane_2_orange_body.getTexture().dispose();
        texR_plane_2_orange_wings.getTexture().dispose();
        texR_plane_2_blue_body.getTexture().dispose();
        texR_plane_2_blue_wings.getTexture().dispose();
        texR_plane_2_pink_body.getTexture().dispose();
        texR_plane_2_pink_wings.getTexture().dispose();

        texR_character_hare_head.getTexture().dispose();
        texR_character_default_head.getTexture().dispose();

        texR_cloud.getTexture().dispose();
        texR_ring.getTexture().dispose();
        texR_ring_arrows.getTexture().dispose();

        texR_tree_big_light.getTexture().dispose();
        texR_tree_big_dark.getTexture().dispose();
        texR_tree_small_light.getTexture().dispose();
        texR_tree_small_dark.getTexture().dispose();
        texR_lake.getTexture().dispose();
        texR_hill.getTexture().dispose();

        atlas_text_race_positions.dispose();
        atlas_text_race_states.dispose();

        music_course_1.dispose();

        sound_cloud_hit.dispose();
        sound_ring_collected.dispose();
        sound_countdown.dispose();
        sound_airplane_engine.dispose();

        pfx_scarf.dispose();
        pfx_cloud_dispersion.dispose();
        pfx_balloon_hit.dispose();

        font.dispose();
    }
}
