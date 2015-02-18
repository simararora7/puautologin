package simararora.puautologin;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;

import java.util.List;

/**
 * Created by Simar Arora on 2/4/2015.
 * 
 */
public class Functions {

    private static final String KEY_INITIALISE = "initialBit";
    private static final String KEY_ACTIVE_USER = "activeUser";
    private static final String PREFERENCES_NAME = "puSharedPreferences";

    public static boolean isInitialised(Context context) {
        String str = readFromSharedPreferences(context, KEY_INITIALISE);
        switch (str) {
            case "":
            case "0":
                return false;
        }
        return true;
    }

    public static void initialise(Context context) {
        writeToSharedPreferences(context, KEY_INITIALISE, 1 + "");
    }

    public static void disable(Context context) {
        writeToSharedPreferences(context, KEY_INITIALISE, 0 + "");
    }

    public static String getActiveUserName(Context context) {
        return readFromSharedPreferences(context, KEY_ACTIVE_USER);
    }

    public static void setActiveUser(Context context, String userName) {
        writeToSharedPreferences(context, KEY_ACTIVE_USER, userName);
    }

    public static String getPasswordForUserName(Context context, String userName) {
        UserDatabase userDatabase = new UserDatabase(context);
        userDatabase.open();
        String password = userDatabase.getPasswordFromUserName(userName);
        userDatabase.close();
        return password;
    }

    private static String readFromSharedPreferences(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
        return sharedPreferences.getString(key, "");
    }

    private static void writeToSharedPreferences(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(key, value);
        sharedPreferencesEditor.apply();
    }

    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setNetworkTypeToWifi(Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder requestBuilder = new NetworkRequest.Builder();
        requestBuilder.addCapability(NetworkCapabilities.TRANSPORT_WIFI);
        connectivityManager.registerNetworkCallback(requestBuilder.build(), new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                ConnectivityManager.setProcessDefaultNetwork(network);
            }
        });
    }

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= 21;
    }
}
