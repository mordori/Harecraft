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

    static TextureAtlas atlas_menu_plane;
    static TextureAtlas atlas_planes;
    static TextureAtlas atlas_objects;
    static TextureAtlas atlas_linear_objects;
    static TextureAtlas atlas_HUD;

    static Array<Sprite> sprites_menu_plane;

    static MyAnimation<TextureRegion> animation_menu_plane;

    static ParticleEffect pfx_scarf;
    static ParticleEffect pfx_cloud_dispersion;
    static ParticleEffect pfx_speed_lines;
    static ParticleEffect pfx_speed_up;
    static ParticleEffect pfx_snow;
    static ParticleEffect pfx_wind_trail;

    static BitmapFont font1;
    static BitmapFont font2;
    static BitmapFont font3;
    static BitmapFont font4;
    static BitmapFont font5;
    static BitmapFont font6;
    static BitmapFont font7;

    static Skin skin_menu;

    static ArrayList<FileHandle> flightsSource = new ArrayList<FileHandle>();

    public static void load() {
        //FILES
        System.out.println(Gdx.files.isLocalStorageAvailable());
        System.out.println(Gdx.files.getLocalStoragePath());


        /*if(!Gdx.files.local("data/flights/0.txt").exists()) {
            for (int i = 0; i < Gdx.files.internal("data/flights").list().length; i++) {

                FileHandle from = Gdx.files.internal("data/flights/" + i + ".txt");
                from.copyTo(Gdx.files.local("data/flights/" + i + ".txt"));
                System.out.println("COPIED!");
            }
        }
        else System.out.println("EXISTS!");*/

        for(FileHandle file: Gdx.files.internal("data/flights/").list()) {
            flightsSource.add(file);
        }

        //ATLASES
        atlas_menu_plane = loadTextureAtlas("atlas_menu_plane.atlas");
        atlas_planes = loadTextureAtlas("atlas_planes.atlas");
        atlas_objects = loadTextureAtlas("atlas_objects.atlas");
        atlas_linear_objects = loadTextureAtlas("atlas_linear_objects.atlas");
        atlas_HUD = loadTextureAtlas("atlas_HUD.atlas");

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

        //DECALS
        texR_background_summer = loadDecalTextureRegion("tex_background_summer.png");
        texR_background_tundra = loadDecalTextureRegion("tex_background_tundra.png");
        texR_sun = loadDecalTextureRegionFromAtlas(atlas_objects, "sun");
        texR_hotairballoon_summer = loadDecalTextureRegionFromAtlas(atlas_objects, "HAB_summer");
        texR_hotairballoon_tundra = loadDecalTextureRegionFromAtlas(atlas_objects, "HAB_tundra");
        texR_balloon_red = loadDecalTextureRegionFromAtlas(atlas_objects, "balloon_red");
        texR_balloon_orange = loadDecalTextureRegionFromAtlas(atlas_objects, "balloon_orange");
        texR_balloon_blue = loadDecalTextureRegionFromAtlas(atlas_objects, "balloon_blue");
        texR_plane_red_body = loadDecalTextureRegionFromAtlas(atlas_planes, "red_body");
        texR_plane_1_red_wings = loadDecalTextureRegionFromAtlas(atlas_planes,"red_1_wings");
        texR_plane_2_red_wings = loadDecalTextureRegionFromAtlas(atlas_planes, "red_2_wings");
        texR_plane_3_red_wings = loadDecalTextureRegionFromAtlas(atlas_planes,"red_3_wings");
        texR_plane_wolf_body = loadDecalTextureRegionFromAtlas(atlas_planes,"wolf_body");
        texR_plane_wolf_wings = loadDecalTextureRegionFromAtlas(atlas_planes,"wolf_wings");
        texR_plane_fox_body = loadDecalTextureRegionFromAtlas(atlas_planes,"fox_body");
        texR_plane_fox_wings = loadDecalTextureRegionFromAtlas(atlas_planes,"fox_wings");
        texR_plane_koala_body = loadDecalTextureRegionFromAtlas(atlas_planes,"koala_body");
        texR_plane_koala_wings = loadDecalTextureRegionFromAtlas(atlas_planes,"koala_wings");
        texR_plane_giraff_body = loadDecalTextureRegionFromAtlas(atlas_planes,"giraff_body");
        texR_plane_giraff_wings = loadDecalTextureRegionFromAtlas(atlas_planes,"giraff_wings");
        texR_plane_bear_body = loadDecalTextureRegionFromAtlas(atlas_planes,"bear_body");
        texR_plane_bear_wings = loadDecalTextureRegionFromAtlas(atlas_planes,"bear_wings");
        texR_character_wolf = loadDecalTextureRegionFromAtlas(atlas_planes,"wolf");
        texR_character_fox = loadDecalTextureRegionFromAtlas(atlas_planes,"fox");
        texR_character_koala = loadDecalTextureRegionFromAtlas(atlas_planes,"koala");
        texR_character_giraff = loadDecalTextureRegionFromAtlas(atlas_planes,"giraff");
        texR_character_bear = loadDecalTextureRegionFromAtlas(atlas_planes,"bear");
        texR_character_hare = loadDecalTextureRegionFromAtlas(atlas_planes,"hare");
        texR_cloud1 = loadDecalTextureRegionFromAtlas(atlas_objects, "cloud1");
        texR_cloud2 = loadDecalTextureRegionFromAtlas(atlas_objects, "cloud2");
        texR_cloud3 = loadDecalTextureRegionFromAtlas(atlas_objects, "cloud3");
        texR_ring0 = loadDecalTextureRegionFromAtlas(atlas_planes, "ring0");
        texR_ring1 = loadDecalTextureRegionFromAtlas(atlas_planes, "ring1");
        texR_ring2 = loadDecalTextureRegionFromAtlas(atlas_planes, "ring2");
        texR_ring_arrows0 = loadDecalTextureRegionFromAtlas(atlas_planes, "arrows0");
        texR_ring_arrows1 = loadDecalTextureRegionFromAtlas(atlas_planes, "arrows1");
        texR_ring_arrows2 = loadDecalTextureRegionFromAtlas(atlas_planes, "arrows2");
        texR_tree_summer_big_dark = loadDecalTextureRegionFromAtlas(atlas_objects, "tree_summer_big_dark");
        texR_tree_summer_small_dark = loadDecalTextureRegionFromAtlas(atlas_objects, "tree_summer_big_dark");
        texR_tree_summer_big_light = loadDecalTextureRegionFromAtlas(atlas_objects, "tree_summer_big_dark");
        texR_tree_summer_small_light = loadDecalTextureRegionFromAtlas(atlas_objects, "tree_summer_small_light");
        texR_tree_tundra_big_dark = loadDecalTextureRegionFromAtlas(atlas_objects, "tree_tundra_big_dark");
        texR_tree_tundra_small_dark = loadDecalTextureRegionFromAtlas(atlas_objects, "tree_tundra_small_dark");
        texR_tree_tundra_medium_dark = loadDecalTextureRegionFromAtlas(atlas_objects, "tree_tundra_medium_dark");
        texR_tree_tundra_medium_light = loadDecalTextureRegionFromAtlas(atlas_objects, "tree_tundra_medium_light");
        texR_tree_tundra_big_light = loadDecalTextureRegionFromAtlas(atlas_objects, "tree_tundra_big_light");
        texR_tree_tundra_small_light = loadDecalTextureRegionFromAtlas(atlas_objects, "tree_tundra_small_light");
        texR_lake_summer = loadDecalTextureRegionFromAtlas(atlas_linear_objects, "lake_summer");
        texR_lake_tundra = loadDecalTextureRegionFromAtlas(atlas_linear_objects, "lake_tundra");
        texR_hill_tundra = loadDecalTextureRegionFromAtlas(atlas_objects, "hill_tundra");
        texR_hill_summer = loadDecalTextureRegionFromAtlas(atlas_objects, "hill_summer");
        texR_island = loadDecalTextureRegionFromAtlas(atlas_linear_objects, "island");
        texR_lighthouse = loadDecalTextureRegionFromAtlas(atlas_objects, "lighthouse");
        texR_palmtree = loadDecalTextureRegionFromAtlas(atlas_objects, "palmtree");
        texR_boat = loadDecalTextureRegionFromAtlas(atlas_objects, "boat");

        //SPRITES
        sprites_menu_plane = atlas_menu_plane.createSprites();

        //ANIMATIONS
        animation_menu_plane = new MyAnimation<TextureRegion>(1/25f, atlas_menu_plane.getRegions());

        //PARTICLES
        pfx_scarf = new ParticleEffect();
        pfx_scarf.load(Gdx.files.internal("particles/pfx_scarf"), Gdx.files.internal("particles/"));
        pfx_cloud_dispersion = new ParticleEffect();
        pfx_cloud_dispersion.load(Gdx.files.internal("particles/pfx_cloud_dispersion"), Gdx.files.internal("particles/"));
        pfx_speed_lines = new ParticleEffect();
        pfx_speed_lines.load(Gdx.files.internal("particles/pfx_speed_lines"), Gdx.files.internal("particles/"));
        pfx_speed_up = new ParticleEffect();
        pfx_speed_up.load(Gdx.files.internal("particles/pfx_speed_up"), Gdx.files.internal("particles/"));
        pfx_snow = new ParticleEffect();
        pfx_snow.load(Gdx.files.internal("particles/pfx_snow"), Gdx.files.internal("particles/"));
        pfx_wind_trail = new ParticleEffect();
        pfx_wind_trail.load(Gdx.files.internal("particles/pfx_wind_trail"), Gdx.files.internal("particles/"));

        //FONTS
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/KOMIKAX_.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = new Color(1f,1f,1f,1f);
        parameter.size = 150;
        font1 = generator.generateFont(parameter);
        parameter.size = 100;
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
    }

    public static Texture loadTexture(String path) {
        Texture texture = new Texture(Gdx.files.internal("textures/" + path));
        texture.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        return texture;
    }

    public static TextureRegion loadDecalTextureRegion(String path) {return flip(new TextureRegion(loadTexture(path)));}

    public static TextureRegion loadDecalTextureRegionFromAtlas(TextureAtlas atlas, String index) {return atlas.findRegion(index);}

    public static TextureRegion loadTextureRegion(String path) {return new TextureRegion(loadTexture(path));}

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

        texR_background_summer.getTexture().dispose();
        texR_background_tundra.getTexture().dispose();
        texR_sun.getTexture().dispose();
        texR_hotairballoon_summer.getTexture().dispose();
        texR_hotairballoon_tundra.getTexture().dispose();
        texR_balloon_red.getTexture().dispose();
        texR_balloon_orange.getTexture().dispose();
        texR_balloon_blue.getTexture().dispose();
        texR_plane_red_body.getTexture().dispose();
        texR_plane_1_red_wings.getTexture().dispose();
        texR_plane_2_red_wings.getTexture().dispose();
        texR_plane_3_red_wings.getTexture().dispose();
        texR_plane_wolf_body.getTexture().dispose();
        texR_plane_wolf_wings.getTexture().dispose();
        texR_plane_fox_wings.getTexture().dispose();
        texR_plane_fox_body.getTexture().dispose();
        texR_plane_koala_wings.getTexture().dispose();
        texR_plane_koala_body.getTexture().dispose();
        texR_plane_giraff_wings.getTexture().dispose();
        texR_plane_giraff_body.getTexture().dispose();
        texR_plane_bear_body.getTexture().dispose();
        texR_plane_bear_wings.getTexture().dispose();
        texR_character_hare.getTexture().dispose();
        texR_character_wolf.getTexture().dispose();
        texR_character_fox.getTexture().dispose();
        texR_character_koala.getTexture().dispose();
        texR_character_giraff.getTexture().dispose();
        texR_character_bear.getTexture().dispose();
        texR_cloud1.getTexture().dispose();
        texR_cloud2.getTexture().dispose();
        texR_cloud3.getTexture().dispose();
        texR_ring0.getTexture().dispose();
        texR_ring1.getTexture().dispose();
        texR_ring2.getTexture().dispose();
        texR_ring_arrows0.getTexture().dispose();
        texR_ring_arrows1.getTexture().dispose();
        texR_ring_arrows2.getTexture().dispose();
        texR_tree_summer_big_light.getTexture().dispose();
        texR_tree_summer_small_light.getTexture().dispose();
        texR_tree_summer_big_dark.getTexture().dispose();
        texR_tree_summer_small_dark.getTexture().dispose();
        texR_tree_tundra_big_light.getTexture().dispose();
        texR_tree_tundra_medium_light.getTexture().dispose();
        texR_tree_tundra_small_light.getTexture().dispose();
        texR_tree_tundra_small_dark.getTexture().dispose();
        texR_tree_tundra_big_dark.getTexture().dispose();
        texR_tree_tundra_medium_dark.getTexture().dispose();
        texR_lake_tundra.getTexture().dispose();
        texR_lake_summer.getTexture().dispose();
        texR_hill_tundra.getTexture().dispose();
        texR_hill_summer.getTexture().dispose();
        texR_island.getTexture().dispose();
        texR_lighthouse.getTexture().dispose();
        texR_boat.getTexture().dispose();
        texR_palmtree.getTexture().dispose();

        atlas_menu_plane.dispose();
        atlas_planes.dispose();
        atlas_objects.dispose();
        atlas_linear_objects.dispose();
        atlas_HUD.dispose();

        pfx_scarf.dispose();
        pfx_cloud_dispersion.dispose();
        pfx_speed_lines.dispose();
        pfx_speed_up.dispose();
        pfx_snow.dispose();
        pfx_wind_trail.dispose();

        font1.dispose();
        font2.dispose();
        font3.dispose();
        font4.dispose();
        font5.dispose();
        font6.dispose();
        font7.dispose();

        skin_menu.dispose();
    }
}
