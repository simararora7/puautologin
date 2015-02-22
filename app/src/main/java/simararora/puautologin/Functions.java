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

    public static boolean isPUCampus(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                String ssid = wifiInfo.getSSID().toLowerCase();
                if (ssid.contains("pu@campus")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void sendNotification(Context context, String message, boolean showAction) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.putExtra("message", message);
        intent.putExtra("showAction", showAction);
        context.startService(intent);
    }

    public static boolean isConnectedToWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(Build.VERSION.SDK_INT >= 21){
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            Network network;
            for (int i = 0; i < networks.length; i++) {
                network = networks[i];
                networkInfo = connectivityManager.getNetworkInfo(network);
                if ((networkInfo.getType() == ConnectivityManager.TYPE_WIFI) && (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))) {
                   ConnectivityManager.setProcessDefaultNetwork(network);
                    return true;
                }
            }
            return false;

        }else{
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if ((networkInfo != null) && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) && (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))) {
                return true;
            } else {
                return false;
            }
        }
    }
}
