package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Created by musta on 5.4.2018.
 */

public class ProfileInfo {
    static String selectedPlayerProfile; //tätä muutetaan muualta
    static int selectedDifficulty;
    static int selectedStaticHolds;
    static int selectedDuration;
    static Preferences profilesData;

    public static void load() {     //load from prefs based on selected profile
         profilesData = Gdx.app.getPreferences("ProfileFile");
         selectedDifficulty = profilesData.getInteger(selectedPlayerProfile +"Difficulty", 2);
         selectedStaticHolds = profilesData.getInteger(selectedPlayerProfile +"StaticHolds", 1); // 0-5
         selectedDuration = profilesData.getInteger(selectedPlayerProfile +"Duration", 2000); //1000-3000 step 1000
        //selectedDifficulty = tmp;
    }
}
