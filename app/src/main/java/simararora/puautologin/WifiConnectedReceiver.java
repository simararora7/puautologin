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
 *
 */

public class WifiConnectedReceiver extends BroadcastReceiver {
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        debug("Start");
        if (!Functions.isInitialised(context))
            return;
        debug("Continue");
        this.context = context;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if ((networkInfo != null) && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) && (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))) {
            if (isPUCampus()){
                debug("Can Login");
                startLoginTask();
            }
        }
    }

    private void startLoginTask() {
        new LoginTask(context).execute();
    }

    private boolean isPUCampus() {
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

    private void debug(String s){
        Log.d("Simar", s);
    }
}
