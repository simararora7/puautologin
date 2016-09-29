package simararora.puautologin;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * Created by Simar Arora on 2/3/2015.
 * This App is Licensed under GNU General Public License. A copy of this license can be found in the root of this project.
 */

public class WifiConnectedReceiver extends BroadcastReceiver {
    private ConnectivityManager connectivityManager;
    private int buildVersion;
    private Network network;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Functions.isInitialised(context) && Functions.isAutoLoginEnabled(context)) {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            buildVersion = Build.VERSION.SDK_INT;
            if (!isConnectedToWiFi()) {
                Functions.setDisconnectedFromPUCampusFlag(context, 1);
            } else if (this.buildVersion >= 21) {
                ConnectivityManager.setProcessDefaultNetwork(this.network);
                if (Functions.isPUCampus(context)) {
                    new LoginTask(context, true).execute();
                    Functions.setDisconnectedFromPUCampusFlag(context, 0);
                    return;
                }
                Functions.setDisconnectedFromPUCampusFlag(context, 1);
            } else if (!Functions.isPUCampus(context)) {
                Functions.setDisconnectedFromPUCampusFlag(context, 1);
            } else if (Functions.isDisconnectedFlagSet(context)) {
                new LoginTask(context, true).execute();
                Functions.setDisconnectedFromPUCampusFlag(context, 0);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean isConnectedToWiFi() {
        if (buildVersion < 21) {
            // For Pre Lollipop Devices, get active network info and check if it is wifi
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return (networkInfo != null) && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) && (networkInfo.getState().equals(NetworkInfo.State.CONNECTED));
        } else {
            // For Lollipop, get all networks, check if a wifi network is connected
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network network1 : networks) {
                network = network1;
                if (network == null)
                    continue;
                networkInfo = connectivityManager.getNetworkInfo(network);
                if (networkInfo == null)
                    continue;
                if ((networkInfo.getType() == ConnectivityManager.TYPE_WIFI) && (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))) {
                    return true;
                }
            }
            return false;
        }
    }
}
