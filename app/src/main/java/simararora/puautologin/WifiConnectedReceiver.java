package simararora.puautologin;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

/**
 * Created by Simar Arora on 2/3/2015.
 */

public class WifiConnectedReceiver extends BroadcastReceiver {
    private ConnectivityManager connectivityManager;
    private int buildVersion;
    private Network network;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        debug("Start");
        if (!Functions.isInitialised(context))
            return;
        debug("initialised");
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        buildVersion = Build.VERSION.SDK_INT;
        if (isConnectedToWiFi()) {
            debug("Connected");
            if (buildVersion < 21) {
                if (Functions.isPUCampus(context)) {
                    debug("PUCampus");
                    if (Functions.isDisconnectedFlagSet(context)) {
                        new LoginTask(context, true).execute();
                        Functions.setDisconnectedFromPUCampusFlag(context, 0);
                    }
                } else {
                    Functions.setDisconnectedFromPUCampusFlag(context, 1);
                }
            } else {
                ConnectivityManager.setProcessDefaultNetwork(network);
                if (Functions.isPUCampus(context)) {
                    new LoginTask(context, true).execute();
                }
            }
        } else {
            Functions.setDisconnectedFromPUCampusFlag(context, 1);
        }
    }

    private void debug(String message) {
        Log.d("Simar", message);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean isConnectedToWiFi() {
        if (buildVersion < 21) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return (networkInfo != null) && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) && (networkInfo.getState().equals(NetworkInfo.State.CONNECTED));
        } else {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network network1 : networks) {
                network = network1;
                networkInfo = connectivityManager.getNetworkInfo(network);
                if ((networkInfo.getType() == ConnectivityManager.TYPE_WIFI) && (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))) {
                    return true;
                }
            }
            return false;
        }
    }
}
