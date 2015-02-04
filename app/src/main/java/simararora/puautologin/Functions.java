package simararora.puautologin;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Simar Arora on 2/4/2015.
 *
 */
public class Functions {

    private static final String KEY_INITIALISE = "initialBit";
    private static final String KEY_ACTIVE_USER = "activeUser";
    private static final String PREFERENCES_NAME = "puSharedPreferences";

    public static boolean isInitialised(Context context){
        String str = readFromSharedPreferences(context, KEY_INITIALISE);
        if(str.equals("")){
            return false;
        }
        return Boolean.parseBoolean(str);
    }

    public static void initialise(Context context){
        writeToSharedPreferences(context, KEY_INITIALISE, true + "");
    }

    public static void disable(Context context){
        writeToSharedPreferences(context, KEY_INITIALISE, false + "");
    }

    public static String getActiveUserName(Context context){
        return readFromSharedPreferences(context, KEY_ACTIVE_USER);
    }

    public void setActiveUser(Context context, String userName){
        writeToSharedPreferences(context, KEY_ACTIVE_USER, userName);
    }

    public static String getPasswordForUserName(Context context, String userName){
        UserDatabase userDatabase = new UserDatabase(context);
        userDatabase.open();
        String password = userDatabase.getPasswordFromUserName(userName);
        userDatabase.close();
        return password;
    }

    private static String readFromSharedPreferences(Context context, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
        return sharedPreferences.getString(key, "");
    }

    private static void writeToSharedPreferences(Context context, String key, String value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(key, value);
        sharedPreferencesEditor.commit();
    }
}
