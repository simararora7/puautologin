package simararora.puautologin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Created by Simar Arora on 2/3/2015.
 */
public class WifiConnectedReceiver extends BroadcastReceiver {
    private Context context;
    private WifiManager wifiManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Simar", "Received");
        if (!isUsernamePasswordInitialised())
            return;
        Log.d("Simar", "Initialised");
        this.context = context;
//        Log.d("Simar", "Equal");
//        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
//        if (networkInfo != null
//                && NetworkInfo.State.CONNECTED.equals(networkInfo.getState())) {
//            if (canLogin()) {
//                Log.d("Simar", "Can Login");
//                startLoginTask();
//            } else
//                Log.d("Simar", "Can't Login");
//        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if ((networkInfo != null) && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) && (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))) {
            if (canLogin())
                startLoginTask();
        }
    }

    private void startLoginTask() {
        new LoginTask(context).execute(getUserNameFromSharedPreferences(), getPasswordFromSharedPreferences());
    }

    private boolean canLogin() {
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

    private String getUserNameFromSharedPreferences() {
        return "abh1121662";
    }

    private String getPasswordFromSharedPreferences() {
        return "Boobs@123 ";
    }

    private boolean isUsernamePasswordInitialised() {
        return true;
    }
}
