package fi.tamk.tiko.harecraft;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Created by musta on 5.4.2018.
 */

public class ProfileInfo {
    static String selectedPlayerProfile; //tätä muutetaan muualta
    static int selectedDifficulty;
    static Preferences profilesData;

    public static void load() {     //load from prefs based on selected profile
         profilesData = Gdx.app.getPreferences("ProfileFile");
         selectedDifficulty = profilesData.getInteger(selectedPlayerProfile +"Difficulty", 2);
        //selectedDifficulty = tmp;
    }
}
