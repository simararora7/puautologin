package simararora.puautologin;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
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
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        buildVersion = Build.VERSION.SDK_INT;
        if (isConnectedToWiFi()) {
            if (buildVersion < 21) {
                if (Functions.isPUCampus(context)) {
                    new LoginTask(context, true).execute();
                } else {
                    Functions.sendNotification(context, "Not Connected To PU@Campus", false);
                }
            } else {
                ConnectivityManager.setProcessDefaultNetwork(network);
                if (Functions.isPUCampus(context)) {
                    new LoginTask(context, true).execute();
                }
            }
        }


//        debug("Initialised");
//        Functions.setNetworkTypeToWiFi(context);
//        new LoginTask(context).execute();
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//        if ((networkInfo != null) && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) && (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))) {
//            if (Functions.isPUCampus(context)){
//                new LoginTask(context).execute();
//            }
//        }
    }

    private void debug(String message) {
        Log.d("Simar", message);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean isConnectedToWiFi() {
        if (buildVersion < 21) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if ((networkInfo != null) && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) && (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))) {
                return true;
            } else {
                return false;
            }
        } else {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (int i = 0; i < networks.length; i++) {
                network = networks[i];
                networkInfo = connectivityManager.getNetworkInfo(network);
                if ((networkInfo.getType() == ConnectivityManager.TYPE_WIFI) && (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))) {
                    return true;
                }
            }
            return false;
        }
    }
}
