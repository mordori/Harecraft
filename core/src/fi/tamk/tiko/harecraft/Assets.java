package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
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
    //static int[][] worldsArr = new int[2][2];

    static Texture tex_grass;
    static Texture tex_sea;
    static Texture tex_foam;
    static Texture tex_sea_deep;
    static Texture tex_mask_foam;
    static Texture tex_mask_sea_deep;

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
    static ParticleEffect pfx_speed_lines;
    static ParticleEffect pfx_speed_up;

    static BitmapFont font;

    public static void load() {
        //FILES
        /*FileHandle handle = Gdx.files.internal("files/worlds.txt");
        String str = handle.readString();
        String[] strWorlds = str.split("-");
        String[] temp;
        int index = 0;
        int index1 = 0;

        for(String s : strWorlds) {
            temp = s.split("\n");
            for(String s1 : temp) {
                worldsArr[index][index1] = Integer.parseInt(temp[index1]);
                index1++;
            }
            index++;
        }*/

        //TEXTURES
        tex_grass = new Texture(Gdx.files.internal("shaders/tex_grass.png"));
        tex_grass.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        tex_sea = new Texture(Gdx.files.internal("shaders/tex_sea.png"));
        tex_sea.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        tex_sea.setWrap(Texture.TextureWrap.Repeat,Texture.TextureWrap.Repeat);
        tex_foam = new Texture(Gdx.files.internal("shaders/tex_foam.png"));
        tex_foam.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        tex_foam.setWrap(Texture.TextureWrap.Repeat,Texture.TextureWrap.Repeat);
        tex_sea_deep = new Texture(Gdx.files.internal("shaders/tex_sea_deep.png"));
        tex_sea_deep.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        tex_sea_deep.setWrap(Texture.TextureWrap.Repeat,Texture.TextureWrap.Repeat);
        tex_mask_foam = new Texture(Gdx.files.internal("shaders/tex_sea_mask.png"));
        tex_mask_foam.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        tex_mask_foam.setWrap(Texture.TextureWrap.Repeat,Texture.TextureWrap.Repeat);
        tex_mask_sea_deep = new Texture(Gdx.files.internal("shaders/tex_sea_deep_mask.png"));
        tex_mask_sea_deep.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        tex_mask_sea_deep.setWrap(Texture.TextureWrap.Repeat,Texture.TextureWrap.Repeat);

        //DECALS
        texR_foreground = loadDecalTextureRegion("tex_foreground_tundra.png");
        texR_sun = loadDecalTextureRegion("tex_sun.png");
        texR_hotairballoon = loadDecalTextureRegion("tex_hotairballoon.png");
        texR_ribbons = loadDecalTextureRegion("tex_ribbons.png");
        texR_playertag = loadDecalTextureRegion("tex_playertag.png");
        texR_powerup_red = loadDecalTextureRegion("tex_powerup_red.png");
        texR_powerup_green = loadDecalTextureRegion("tex_powerup_green.png");
        texR_powerup_blue = loadDecalTextureRegion("tex_powerup_blue.png");
        texR_plane_2_red_body = loadDecalTextureRegion("tex_plane_2_red_body.png");
        texR_plane_2_red_wings = loadDecalTextureRegion("tex_plane_2_red_wings.png");
        texR_plane_2_orange_body = loadDecalTextureRegion("tex_plane_2_orange_body.png");
        texR_plane_2_orange_wings = loadDecalTextureRegion("tex_plane_2_orange_wings.png");
        texR_plane_2_blue_body = loadDecalTextureRegion("tex_plane_2_blue_body.png");
        texR_plane_2_blue_wings = loadDecalTextureRegion("tex_plane_2_blue_wings.png");
        texR_plane_2_pink_body = loadDecalTextureRegion("tex_plane_2_pink_body.png");
        texR_plane_2_pink_wings = loadDecalTextureRegion("tex_plane_2_pink_wings.png");
        texR_character_default_head = loadDecalTextureRegion("tex_character_default_head.png");
        texR_character_hare_head = loadDecalTextureRegion("tex_character_hare_head.png");
        texR_cloud = loadDecalTextureRegion("tex_cloud.png");
        texR_ring = loadDecalTextureRegion("tex_ring.png");
        texR_ring_arrows = loadDecalTextureRegion("tex_ring_arrows.png");
        texR_tree_big_light = loadDecalTextureRegion("tex_tree_big_light.png");
        texR_tree_big_dark = loadDecalTextureRegion("tex_tree_big_dark.png");
        texR_tree_small_light = loadDecalTextureRegion("tex_tree_small_light.png");
        texR_tree_small_dark = loadDecalTextureRegion("tex_tree_small_dark.png");
        texR_lake = loadDecalTextureRegion("tex_lake.png");
        texR_hill = loadDecalTextureRegion("tex_hill_tundra.png");

        //SPRITES
        texR_speedometer = loadTextureRegion("tex_speedometer.png");

        //ATLASES
        atlas_text_race_positions = loadTextureAtlas("atlas_text_positions.txt");
        atlas_text_race_states = loadTextureAtlas("atlas_text_race_states.txt");
        //animation_player_scarf = new MyAnimation<TextureRegion>(1f/15f, test_atlas.getRegions());
        //flip(animation_player_scarf, animation_player_scarf.getKeyFrames().length);

        sprites_text_race_states = atlas_text_race_states.createSprites();
        sprites_text_race_positions = atlas_text_race_positions.createSprites();

        //AUDIO
        music_course_1 = loadMusic("music_course_1.mp3");
        music_course_1.setLooping(true);
        sound_cloud_hit = loadSound("sound_cloud_hit.wav");
        sound_ring_collected = loadSound("sound_ring_collected.wav");
        sound_countdown = loadSound("sound_countdown.wav");
        sound_airplane_engine = loadSound("sound_airplane_engine.wav");

        //PARTICLES
        pfx_scarf = new ParticleEffect();
        pfx_scarf.load(Gdx.files.internal("particles/pfx_scarf"), Gdx.files.internal("particles/"));
        pfx_cloud_dispersion = new ParticleEffect();
        pfx_cloud_dispersion.load(Gdx.files.internal("particles/pfx_cloud_dispersion"), Gdx.files.internal("particles/"));
        pfx_speed_lines = new ParticleEffect();
        pfx_speed_lines.load(Gdx.files.internal("particles/pfx_speed_lines"), Gdx.files.internal("particles/"));
        pfx_speed_up = new ParticleEffect();
        pfx_speed_up.load(Gdx.files.internal("particles/pfx_speed_up"), Gdx.files.internal("particles/"));

        //FONTS
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/foo.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 64;
        parameter.borderWidth = 4;
        parameter.color = new Color(1f,0.6f,0f,1f);
        font = generator.generateFont(parameter);
        generator.dispose();
    }

    public static Texture loadTexture(String path) {
        Texture texture = new Texture(Gdx.files.internal("textures/" + path));
        texture.setFilter(Texture.TextureFilter.Nearest,Texture.TextureFilter.Nearest);
        return texture;
    }

    public static TextureRegion loadDecalTextureRegion(String path) {return flip(new TextureRegion(loadTexture(path)));}

    public static TextureRegion loadTextureRegion(String path) {return new TextureRegion(loadTexture(path));}

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
        tex_grass.dispose();
        tex_sea.dispose();
        tex_foam.dispose();
        tex_sea_deep.dispose();
        tex_mask_foam.dispose();
        tex_mask_sea_deep.dispose();

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
        pfx_speed_lines.dispose();
        pfx_speed_up.dispose();

        font.dispose();
    }
}
