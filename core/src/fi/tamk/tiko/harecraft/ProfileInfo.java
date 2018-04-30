package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;

import java.util.Locale;

/**
 * Created by musta on 5.4.2018.
 */

public class ProfileInfo {
    static String selectedPlayerProfile; //tätä muutetaan muualta
    static int selectedDifficulty;
    static int selectedStaticHolds;
    static int selectedDuration;
    static float selectedSensitivity;
    static Preferences profilesData = Gdx.app.getPreferences("ProfileFile");

    static Vector2 customVector1;
    static Vector2 customVector2;
    static Vector2 customVector3;
    static Vector2 customVector4;
    static Vector2 customVector5;
    static Vector2 customVector6;
    static Boolean invertY;

    static Locale gameLanguage;
    static String gameLanguageString;


    public static void load() {     //load from prefs based on selected profile
         //profilesData = Gdx.app.getPreferences("ProfileFile");
         selectedDifficulty = profilesData.getInteger(selectedPlayerProfile +"Difficulty", 2);
         selectedStaticHolds = profilesData.getInteger(selectedPlayerProfile +"StaticHolds", 1); // 0-5
         selectedDuration = profilesData.getInteger(selectedPlayerProfile +"Duration", 2000); //1000-3000 step 1000
         selectedSensitivity = profilesData.getFloat(selectedPlayerProfile +"Sensitivity", 1f);

         //customVector1 = new Vector2(profilesData.getFloat(selectedPlayerProfile+"VectorX0", 0), profilesData.getFloat(selectedPlayerProfile+"VectorY0", 0));
         //customVector2 = new Vector2(profilesData.getFloat(selectedPlayerProfile+"VectorX1", 0), profilesData.getFloat(selectedPlayerProfile+"VectorY1", 0));
         //customVector3 = new Vector2(profilesData.getFloat(selectedPlayerProfile+"VectorX2", 0), profilesData.getFloat(selectedPlayerProfile+"VectorY2", 0));
         //customVector4 = new Vector2(profilesData.getFloat(selectedPlayerProfile+"VectorX3", 0), profilesData.getFloat(selectedPlayerProfile+"VectorY3", 0));
         //customVector5 = new Vector2(profilesData.getFloat(selectedPlayerProfile+"VectorX4", 0), profilesData.getFloat(selectedPlayerProfile+"VectorY4", 0));
         //customVector6 = new Vector2(profilesData.getFloat(selectedPlayerProfile+"VectorX5", 0), profilesData.getFloat(selectedPlayerProfile+"VectorY5", 0));
        //selectedDifficulty = tmp;
        customVector1 = new Vector2(profilesData.getInteger(selectedPlayerProfile+"VectorX0", 0), profilesData.getInteger(selectedPlayerProfile+"VectorY0", 0));
        customVector2 = new Vector2(profilesData.getInteger(selectedPlayerProfile+"VectorX1", 0), profilesData.getInteger(selectedPlayerProfile+"VectorY1", 0));
        customVector3 = new Vector2(profilesData.getInteger(selectedPlayerProfile+"VectorX2", 0), profilesData.getInteger(selectedPlayerProfile+"VectorY2", 0));
        customVector4 = new Vector2(profilesData.getInteger(selectedPlayerProfile+"VectorX3", 0), profilesData.getInteger(selectedPlayerProfile+"VectorY3", 0));
        customVector5 = new Vector2(profilesData.getInteger(selectedPlayerProfile+"VectorX4", 0), profilesData.getInteger(selectedPlayerProfile+"VectorY4", 0));
        customVector6 = new Vector2(profilesData.getInteger(selectedPlayerProfile+"VectorX5", 0), profilesData.getInteger(selectedPlayerProfile+"VectorY5", 0));
        invertY = profilesData.getBoolean(selectedPlayerProfile +"Invert", true);
    }

    public static void determineGameLanguage() {
        String language = profilesData.getString("Language", "systemdefault");
        if (language.contains("English")) {
            gameLanguage = new Locale("en", "GB");
        }
        else if (language.contains("Finnish")) {
            gameLanguage = new Locale("fi", "FI");
        }
        else {
            gameLanguage = Locale.getDefault();
            Gdx.app.log("Language is: ", "System default ");
        }
        gameLanguageString = gameLanguage.toString();
    }

    public static void switchLanguage() {
        if (gameLanguage.toString().equals("en_GB")) {
            //gameLanguage = new Locale("fi","FI");
            profilesData.putString("Language" , "Finnish");
            profilesData.flush();
        }
        else if (gameLanguage.toString().equals("fi_FI")) {
            //gameLanguage = new Locale("en", "GB");
            profilesData.putString("Language" , "English");
            profilesData.flush();
        }
        else { //if american OS or other loads it switches from english to finnish
            profilesData.putString("Language" , "Finnish");
            profilesData.flush();
        }
    }
}
