package simararora.puautologin;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import java.util.List;

/**
 * Created by Simar Arora on 2/4/2015.
 * This App is Licensed under GNU General Public License. A copy of this license can be found in the root of this project.
 */
public class Functions {

    private static final String KEY_INITIALISE = "initialBit";
    private static final String KEY_ACTIVE_USER = "activeUser";
    private static final String PREFERENCES_NAME = "puSharedPreferences";
    private static final String WIFI_KEY = "wifiKey";
    private static final String KEY_RUN_COUNT = "runCount";
    private static final String KEY_CAN_RATE = "canRate";
    private static final String KEY_TIMESTAMP = "timestamp";

    public static void setTimeStamp(Context context, int timestamp) {
        writeToSharedPreferences(context, KEY_TIMESTAMP, timestamp + "");
    }

    public static int getTimeStamp(Context context) {
        String str = readFromSharedPreferences(context, KEY_TIMESTAMP);
        if (str.isEmpty())
            return 0;
        return Integer.parseInt(str);
    }

    /**
     * Check if user has initialised the app by entering at least one username password pair
     *
     * @param context
     * @return initialised
     */
    public static boolean isInitialised(Context context) {
        String str = readFromSharedPreferences(context, KEY_INITIALISE);
        switch (str) {
            case "":
            case "0":
                return false;
        }
        return true;
    }

    /**
     * Set initialised flag in shared preferences
     *
     * @param context
     */
    public static void initialise(Context context) {
        writeToSharedPreferences(context, KEY_INITIALISE, 1 + "");
    }

    /**
     * Reset initialised flag in shared preferences
     *
     * @param context
     */
    public static void disable(Context context) {
        writeToSharedPreferences(context, KEY_INITIALISE, 0 + "");
    }

    /**
     * Retrieve default username from shared preferences
     *
     * @param context
     * @return default username selected by username
     */
    public static String getActiveUserName(Context context) {
        return readFromSharedPreferences(context, KEY_ACTIVE_USER);
    }

    /**
     * Set default user in shared preferences
     *
     * @param context
     * @param userName
     */
    public static void setActiveUser(Context context, String userName) {
        writeToSharedPreferences(context, KEY_ACTIVE_USER, userName);
    }

    /**
     * Return password for the username provided
     *
     * @param context
     * @param userName
     * @return
     */
    public static String getPasswordForUserName(Context context, String userName) {
        UserDatabase userDatabase = new UserDatabase(context);
        userDatabase.open();
        String password = userDatabase.getPasswordFromUserName(userName);
        userDatabase.close();
        return password;
    }

    /**
     * Read from shared preferences
     *
     * @param context
     * @param key
     * @return
     */
    private static String readFromSharedPreferences(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
        return sharedPreferences.getString(key, "");
    }

    /**
     * Write To Shared Preferences
     *
     * @param context
     * @param key
     * @param value
     */
    private static void writeToSharedPreferences(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(key, value);
        sharedPreferencesEditor.apply();
    }

    /**
     * Check if app is on foreground
     *
     * @param context
     * @return
     */
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

    /**
     * Check if current SSID is PU@Campus
     *
     * @param context
     * @return
     */
    public static boolean isPUCampus(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                String ssid = wifiInfo.getSSID();
                if (ssid != null) {
                    ssid = ssid.toLowerCase();
                    if (ssid.contains("pu@campus")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Send Notification
     *
     * @param context
     * @param message
     * @param showAction
     */
    public static void sendNotification(Context context, String message, boolean showAction) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.putExtra("message", message);
        intent.putExtra("showAction", showAction);
        context.startService(intent);
    }

    /**
     * Check if Wifi is connected
     *
     * @param context
     * @return
     */
    public static boolean isConnectedToWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= 21) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network network : networks) {
                networkInfo = connectivityManager.getNetworkInfo(network);
                if ((networkInfo.getType() == ConnectivityManager.TYPE_WIFI) && (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))) {
                    ConnectivityManager.setProcessDefaultNetwork(network);
                    return true;
                }
            }
            return false;

        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return (networkInfo != null) && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) && (networkInfo.getState().equals(NetworkInfo.State.CONNECTED));
        }
    }

    public static boolean isDisconnectedFlagSet(Context context) {
        String str = readFromSharedPreferences(context, WIFI_KEY);
        if (str.equals(""))
            return true;
        int x = Integer.parseInt(str);
        return x == 1;
    }

    public static void setDisconnectedFromPUCampusFlag(Context context, int value) {
        writeToSharedPreferences(context, WIFI_KEY, value + "");
    }

    public static int getRunCount(Context context) {
        String str = readFromSharedPreferences(context, KEY_RUN_COUNT);
        if (str.equals(""))
            return 1;
        else return Integer.parseInt(str);
    }

    public static void setRunCount(Context context, int count) {
        writeToSharedPreferences(context, KEY_RUN_COUNT, count + "");
    }

    public static boolean canShowRateDialog(Context context) {
        String str = readFromSharedPreferences(context, KEY_CAN_RATE);
        return str.equals("") || Boolean.parseBoolean(str);
    }

    public static void setCanRateFlag(Context context, boolean canRate) {
        writeToSharedPreferences(context, KEY_CAN_RATE, canRate + "");
    }
}
