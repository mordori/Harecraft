package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

/**
 * Created by Mika on 23.2.2018.
 *
 * Asset handling class.
 */

public class Assets {
    static Texture tex_grass;
    static Texture tex_ground;
    static Texture tex_sea;
    static Texture tex_foam;
    static Texture tex_sea_deep;
    static Texture tex_mask_foam;
    static Texture tex_mask_sea_deep;

    static Texture tex_loading;
    static Texture tex_tamk;
    static Texture tex_exerium;
    static Texture tex_projectile;
    static Texture tex_tiko;
    static Texture tex_lataa;

    static TextureRegion texR_background_summer;
    static TextureRegion texR_background_tundra;
    static TextureRegion texR_sun;
    static TextureRegion texR_hotairballoon_summer;
    static TextureRegion texR_hotairballoon_tundra;
    static TextureRegion texR_balloon_red;
    static TextureRegion texR_balloon_orange;
    static TextureRegion texR_balloon_blue;
    static TextureRegion texR_plane_red_body;
    static TextureRegion texR_plane_1_red_wings;
    static TextureRegion texR_plane_2_red_wings;
    static TextureRegion texR_plane_3_red_wings;
    static TextureRegion texR_plane_bear_body;
    static TextureRegion texR_plane_bear_wings;
    static TextureRegion texR_plane_giraff_body;
    static TextureRegion texR_plane_giraff_wings;
    static TextureRegion texR_plane_koala_body;
    static TextureRegion texR_plane_koala_wings;
    static TextureRegion texR_plane_fox_body;
    static TextureRegion texR_plane_fox_wings;
    static TextureRegion texR_plane_wolf_body;
    static TextureRegion texR_plane_wolf_wings;
    static TextureRegion texR_character_hare;
    static TextureRegion texR_character_wolf;
    static TextureRegion texR_character_fox;
    static TextureRegion texR_character_koala;
    static TextureRegion texR_character_giraff;
    static TextureRegion texR_character_bear;
    static TextureRegion texR_cloud1;
    static TextureRegion texR_cloud2;
    static TextureRegion texR_cloud3;
    static TextureRegion texR_ring0;
    static TextureRegion texR_ring1;
    static TextureRegion texR_ring2;
    static TextureRegion texR_ring_arrows0;
    static TextureRegion texR_ring_arrows1;
    static TextureRegion texR_ring_arrows2;
    static TextureRegion texR_tree_summer_big_light;
    static TextureRegion texR_tree_summer_small_light;
    static TextureRegion texR_tree_summer_big_dark;
    static TextureRegion texR_tree_summer_small_dark;
    static TextureRegion texR_tree_tundra_big_light;
    static TextureRegion texR_tree_tundra_medium_light;
    static TextureRegion texR_tree_tundra_small_light;
    static TextureRegion texR_tree_tundra_small_dark;
    static TextureRegion texR_tree_tundra_big_dark;
    static TextureRegion texR_tree_tundra_medium_dark;
    static TextureRegion texR_lake_tundra;
    static TextureRegion texR_lake_summer;
    static TextureRegion texR_hill_tundra;
    static TextureRegion texR_hill_summer;
    static TextureRegion texR_island;
    static TextureRegion texR_lighthouse;
    static TextureRegion texR_boat;
    static TextureRegion texR_palmtree;

    static TextureRegion texR_finnishFlag;
    static TextureRegion texR_englishFlag;
    static TextureRegion texR_banana;
    static TextureRegion texR_gamelogo;
    static TextureRegion texR_radar;
    static TextureRegion texR_radar_dot;
    static TextureRegion texR_mainmenu_background;
    static TextureRegion texR_instructions;
    static TextureRegion texR_stage1;
    static TextureRegion texR_stage2;
    static TextureRegion texR_stage3;
    static TextureRegion texR_stage1_pressed;
    static TextureRegion texR_stage2_pressed;
    static TextureRegion texR_stage3_pressed;
    static TextureRegion texR_adjusting;
    static TextureRegion texR_asettaa;
    static TextureRegion texR_pause;
    static TextureRegion texR_highscoreList;

    static TextureAtlas atlas_menu_plane;
    static TextureAtlas atlas_planes;
    static TextureAtlas atlas_objects;
    static TextureAtlas atlas_1;

    static Array<Sprite> sprites_menu_plane;

    static MyAnimation<TextureRegion> animation_menu_plane;

    static ParticleEffect pfx_scarf;
    static ParticleEffect pfx_cloud_dispersion;
    static ParticleEffect pfx_speed_lines;
    static ParticleEffect pfx_speed_lines_2;
    static ParticleEffect pfx_speed_up;
    static ParticleEffect pfx_snow;
    static ParticleEffect pfx_wind_trail;
    //static ParticleEffect pfx_placement;
    //static ParticleEffect pfx_placement1;
    //static ParticleEffect pfx_placement2;
    //static ParticleEffect pfx_placement3;

    static BitmapFont font0;
    static BitmapFont font1;
    static BitmapFont font12;
    static BitmapFont font2;
    static BitmapFont font3;
    static BitmapFont font4;
    static BitmapFont font5;
    static BitmapFont font6;
    static BitmapFont font7;

    static Skin skin_menu;

    static ArrayList<FileHandle> flightsSource = new ArrayList<FileHandle>();

    public static boolean load() {
        //FILES;
        for(FileHandle file: Gdx.files.internal("data/flights/").list()) {
            flightsSource.add(file);
        }

        //ATLASES
        atlas_menu_plane = loadTextureAtlas("atlas_menu_plane.atlas");
        atlas_planes = loadTextureAtlas("atlas_planes.atlas");
        atlas_objects = loadTextureAtlas("atlas_objects.atlas");
        atlas_1 = loadTextureAtlas("atlas_1.atlas");

        //TEXTURES
        tex_mask_sea_deep = new Texture(Gdx.files.internal("shaders/tex_sea_deep_mask.png"));
        tex_mask_sea_deep.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        tex_mask_sea_deep.setWrap(Texture.TextureWrap.Repeat,Texture.TextureWrap.Repeat);
        tex_mask_foam = new Texture(Gdx.files.internal("shaders/tex_sea_mask.png"));
        tex_mask_foam.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        tex_mask_foam.setWrap(Texture.TextureWrap.Repeat,Texture.TextureWrap.Repeat);
        tex_sea_deep = new Texture(Gdx.files.internal("shaders/tex_sea_deep.png"));
        tex_sea_deep.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        tex_sea_deep.setWrap(Texture.TextureWrap.Repeat,Texture.TextureWrap.Repeat);
        tex_foam = new Texture(Gdx.files.internal("shaders/tex_foam.png"));
        tex_foam.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        tex_foam.setWrap(Texture.TextureWrap.Repeat,Texture.TextureWrap.Repeat);
        tex_sea = new Texture(Gdx.files.internal("shaders/tex_sea.png"));
        tex_sea.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        tex_sea.setWrap(Texture.TextureWrap.Repeat,Texture.TextureWrap.Repeat);
        tex_grass = new Texture(Gdx.files.internal("shaders/tex_grass.png"));
        tex_grass.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        tex_grass.setWrap(Texture.TextureWrap.Repeat,Texture.TextureWrap.Repeat);
        tex_ground = new Texture(Gdx.files.internal("shaders/tex_ground.png"));
        tex_ground.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        tex_ground.setWrap(Texture.TextureWrap.Repeat,Texture.TextureWrap.Repeat);


        //TEXTURE REGIONS
        texR_finnishFlag = loadAtlasTextureRegion(atlas_1,"fiFlag");
        texR_englishFlag = loadAtlasTextureRegion(atlas_1,"ukFlag");
        texR_banana = loadAtlasTextureRegion(atlas_1,"banana");
        texR_gamelogo = loadAtlasTextureRegion(atlas_1,"logo");
        texR_radar = loadAtlasTextureRegion(atlas_1,"radar");
        texR_radar_dot = loadAtlasTextureRegion(atlas_1,"radardot");
        texR_mainmenu_background = loadAtlasTextureRegion(atlas_1,"menubg");
        texR_instructions = loadAtlasTextureRegion(atlas_1,"help");
        texR_stage1 = loadAtlasTextureRegion(atlas_1,"stage1");
        texR_stage2 = loadAtlasTextureRegion(atlas_1,"stage2");
        texR_stage3 = loadAtlasTextureRegion(atlas_1,"stage3");
        texR_stage1_pressed = loadAtlasTextureRegion(atlas_1,"stage1p");
        texR_stage2_pressed = loadAtlasTextureRegion(atlas_1,"stage2p");
        texR_stage3_pressed = loadAtlasTextureRegion(atlas_1,"stage3p");
        texR_asettaa = loadAtlasTextureRegion(atlas_1,"asettaa");
        texR_adjusting = loadAtlasTextureRegion(atlas_1,"adjusting");
        texR_pause = loadAtlasTextureRegion(atlas_1,"pause");

        //DECALS
        texR_background_summer = loadAtlasTextureRegion(atlas_1, "bg_summer");
        texR_background_tundra = loadAtlasTextureRegion(atlas_1, "bg_tundra");
        texR_sun = loadAtlasTextureRegion(atlas_objects, "sun");
        texR_hotairballoon_summer = loadAtlasTextureRegion(atlas_objects, "HAB_summer");
        texR_hotairballoon_tundra = loadAtlasTextureRegion(atlas_objects, "HAB_tundra");
        texR_balloon_red = loadAtlasTextureRegion(atlas_objects, "balloon_red");
        texR_balloon_orange = loadAtlasTextureRegion(atlas_objects, "balloon_orange");
        texR_balloon_blue = loadAtlasTextureRegion(atlas_objects, "balloon_blue");
        texR_plane_red_body = loadAtlasTextureRegion(atlas_planes, "red_body");
        texR_plane_1_red_wings = loadAtlasTextureRegion(atlas_planes,"red_1_wings");
        texR_plane_2_red_wings = loadAtlasTextureRegion(atlas_planes, "red_2_wings");
        texR_plane_3_red_wings = loadAtlasTextureRegion(atlas_planes,"red_3_wings");
        texR_plane_wolf_body = loadAtlasTextureRegion(atlas_planes,"wolf_body");
        texR_plane_wolf_wings = loadAtlasTextureRegion(atlas_planes,"wolf_wings");
        texR_plane_fox_body = loadAtlasTextureRegion(atlas_planes,"fox_body");
        texR_plane_fox_wings = loadAtlasTextureRegion(atlas_planes,"fox_wings");
        texR_plane_koala_body = loadAtlasTextureRegion(atlas_planes,"koala_body");
        texR_plane_koala_wings = loadAtlasTextureRegion(atlas_planes,"koala_wings");
        texR_plane_giraff_body = loadAtlasTextureRegion(atlas_planes,"giraff_body");
        texR_plane_giraff_wings = loadAtlasTextureRegion(atlas_planes,"giraff_wings");
        texR_plane_bear_body = loadAtlasTextureRegion(atlas_planes,"bear_body");
        texR_plane_bear_wings = loadAtlasTextureRegion(atlas_planes,"bear_wings");
        texR_character_wolf = loadAtlasTextureRegion(atlas_planes,"wolf");
        texR_character_fox = loadAtlasTextureRegion(atlas_planes,"fox");
        texR_character_koala = loadAtlasTextureRegion(atlas_planes,"koala");
        texR_character_giraff = loadAtlasTextureRegion(atlas_planes,"giraff");
        texR_character_bear = loadAtlasTextureRegion(atlas_planes,"bear");
        texR_character_hare = loadAtlasTextureRegion(atlas_planes,"hare");
        texR_cloud1 = loadAtlasTextureRegion(atlas_objects, "cloud1");
        texR_cloud2 = loadAtlasTextureRegion(atlas_objects, "cloud2");
        texR_cloud3 = loadAtlasTextureRegion(atlas_objects, "cloud3");
        texR_ring0 = loadAtlasTextureRegion(atlas_planes, "ring0");
        texR_ring1 = loadAtlasTextureRegion(atlas_planes, "ring1");
        texR_ring2 = loadAtlasTextureRegion(atlas_planes, "ring2");
        texR_ring_arrows0 = loadAtlasTextureRegion(atlas_planes, "arrows0");
        texR_ring_arrows1 = loadAtlasTextureRegion(atlas_planes, "arrows1");
        texR_ring_arrows2 = loadAtlasTextureRegion(atlas_planes, "arrows2");
        texR_tree_summer_big_dark = loadAtlasTextureRegion(atlas_objects, "tree_summer_big_dark");
        texR_tree_summer_small_dark = loadAtlasTextureRegion(atlas_objects, "tree_summer_big_dark");
        texR_tree_summer_big_light = loadAtlasTextureRegion(atlas_objects, "tree_summer_big_dark");
        texR_tree_summer_small_light = loadAtlasTextureRegion(atlas_objects, "tree_summer_small_light");
        texR_tree_tundra_big_dark = loadAtlasTextureRegion(atlas_objects, "tree_tundra_big_dark");
        texR_tree_tundra_small_dark = loadAtlasTextureRegion(atlas_objects, "tree_tundra_small_dark");
        texR_tree_tundra_medium_dark = loadAtlasTextureRegion(atlas_objects, "tree_tundra_medium_dark");
        texR_tree_tundra_medium_light = loadAtlasTextureRegion(atlas_objects, "tree_tundra_medium_light");
        texR_tree_tundra_big_light = loadAtlasTextureRegion(atlas_objects, "tree_tundra_big_light");
        texR_tree_tundra_small_light = loadAtlasTextureRegion(atlas_objects, "tree_tundra_small_light");
        texR_lake_summer = loadAtlasTextureRegion(atlas_1, "lake_summer");
        texR_lake_tundra = loadAtlasTextureRegion(atlas_1, "lake_tundra");
        texR_hill_tundra = loadAtlasTextureRegion(atlas_objects, "hill_tundra");
        texR_hill_summer = loadAtlasTextureRegion(atlas_objects, "hill_summer");
        texR_island = loadAtlasTextureRegion(atlas_1, "island");
        texR_lighthouse = loadAtlasTextureRegion(atlas_objects, "lighthouse");
        texR_palmtree = loadAtlasTextureRegion(atlas_objects, "palmtree");
        texR_boat = loadAtlasTextureRegion(atlas_objects, "boat");
        texR_highscoreList = loadAtlasTextureRegion(atlas_1, "highscorelist");

        //SPRITES
        sprites_menu_plane = atlas_menu_plane.createSprites();

        //ANIMATIONS
        animation_menu_plane = new MyAnimation<TextureRegion>(1/25f, atlas_menu_plane.getRegions());

        //PARTICLES
        pfx_scarf = new ParticleEffect();
        pfx_cloud_dispersion = new ParticleEffect();
        pfx_speed_lines = new ParticleEffect();
        pfx_speed_lines_2 = new ParticleEffect();
        pfx_speed_up = new ParticleEffect();
        pfx_snow = new ParticleEffect();
        pfx_wind_trail = new ParticleEffect();
        //pfx_placement = new ParticleEffect();
        //pfx_placement1 = new ParticleEffect();
        //pfx_placement2 = new ParticleEffect();
        //pfx_placement3 = new ParticleEffect();

        pfx_scarf.load(Gdx.files.internal("particles/pfx_scarf"), Gdx.files.internal("particles/"));
        pfx_cloud_dispersion.load(Gdx.files.internal("particles/pfx_cloud_dispersion"), Gdx.files.internal("particles/"));
        pfx_speed_lines.load(Gdx.files.internal("particles/pfx_speed_lines"), Gdx.files.internal("particles/"));
        pfx_speed_lines_2.load(Gdx.files.internal("particles/pfx_speed_lines_2"), Gdx.files.internal("particles/"));
        pfx_speed_up.load(Gdx.files.internal("particles/pfx_speed_up"), Gdx.files.internal("particles/"));
        pfx_snow.load(Gdx.files.internal("particles/pfx_snow"), Gdx.files.internal("particles/"));
        pfx_wind_trail.load(Gdx.files.internal("particles/pfx_wind_trail"), Gdx.files.internal("particles/"));
        //pfx_placement.load(Gdx.files.internal("particles/pfx_placement"), Gdx.files.internal("particles/"));
        //pfx_placement1.load(Gdx.files.internal("particles/pfx_placement1"), Gdx.files.internal("particles/"));
        //pfx_placement2.load(Gdx.files.internal("particles/pfx_placement2"), Gdx.files.internal("particles/"));
        //pfx_placement3.load(Gdx.files.internal("particles/pfx_placement3"), Gdx.files.internal("particles/"));

        //FONTS
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/KOMIKAX_.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = new Color(1f,1f,1f,1f);
        parameter.size = 225;
        font0 = generator.generateFont(parameter);
        parameter.size = 150;
        font1 = generator.generateFont(parameter);
        parameter.size = 100;
        font12 = generator.generateFont(parameter);
        parameter.size = 75;
        font2 = generator.generateFont(parameter);
        parameter.size = 60;
        font3 = generator.generateFont(parameter);
        parameter.size = 55;
        font4 = generator.generateFont(parameter);
        parameter.size = 40;
        font5 = generator.generateFont(parameter);
        parameter.size = 30;
        font6 = generator.generateFont(parameter);
        parameter.size = 20;
        font7 = generator.generateFont(parameter);
        generator.dispose();

        //SKINS
        skin_menu = new Skin(Gdx.files.internal("harejson/hare.json"));

        return true;
    }

    public static Texture loadTexture(String path) {
        Texture texture = new Texture(Gdx.files.internal("textures/" + path));
        texture.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        return texture;
    }

    public static TextureRegion loadAtlasTextureRegion(TextureAtlas atlas, String index) {return atlas.findRegion(index);}


    public static TextureRegion flip(TextureRegion texR) {
        texR.flip(true, false);
        return texR;
    }

    public static TextureAtlas loadTextureAtlas(String path) {return new TextureAtlas(Gdx.files.internal("atlases/" + path));}

    public static void flip(Animation<TextureRegion> animation, int frames) {
        TextureRegion frame;

        for(int i = 0; i < frames; i++) {
            frame = animation.getKeyFrame(i * animation.getFrameDuration());
            flip(frame);
        }
    }

    public static void flip(Array<Sprite> sprites) {
        for(int i = 0; i < sprites.size; i++) {
            flip(sprites.get(i));
        }
    }

    public static void dispose() {
        tex_grass.dispose();
        tex_ground.dispose();
        tex_sea.dispose();
        tex_foam.dispose();
        tex_sea_deep.dispose();
        tex_mask_foam.dispose();
        tex_mask_sea_deep.dispose();
        tex_loading.dispose();
        tex_exerium.dispose();
        tex_projectile.dispose();
        tex_tiko.dispose();
        tex_tamk.dispose();

        atlas_menu_plane.dispose();
        atlas_planes.dispose();
        atlas_objects.dispose();
        atlas_1.dispose();

        pfx_scarf.dispose();
        pfx_cloud_dispersion.dispose();
        pfx_speed_lines.dispose();
        pfx_speed_lines_2.dispose();
        pfx_speed_up.dispose();
        pfx_snow.dispose();
        pfx_wind_trail.dispose();
        //pfx_placement.dispose();
        //pfx_placement1.dispose();
        //pfx_placement2.dispose();
        //pfx_placement3.dispose();

        font0.dispose();
        font1.dispose();
        font2.dispose();
        font3.dispose();
        font4.dispose();
        font5.dispose();
        font6.dispose();
        font7.dispose();
        font12.dispose();

        skin_menu.dispose();
    }
}
