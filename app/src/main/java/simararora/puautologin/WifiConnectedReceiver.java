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

        // Check if user has set up the app, else return
        if (!Functions.isInitialised(context))
            return;

        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        buildVersion = Build.VERSION.SDK_INT;

        //Check if the state chang in wifi is that wifi has been connected
        if (isConnectedToWiFi()) {

            //For PreLollipop Devices
            if (buildVersion < 21) {

                // Check if the connected wifi is PU@Campus
                if (Functions.isPUCampus(context)) {

                    if (Functions.isDisconnectedFlagSet(context)) {
                        //Begin Login Task
                        new LoginTask(context, true).execute();
                        Functions.setDisconnectedFromPUCampusFlag(context, 0);
                    }
                } else {
                    Functions.setDisconnectedFromPUCampusFlag(context, 1);
                }
            } else {
                // For Lollipop, set default network to wifi
                ConnectivityManager.setProcessDefaultNetwork(network);

                //Check if connected ssid is PU@Campus
                if (Functions.isPUCampus(context)) {
                    //Begin Login Task
                    new LoginTask(context, true).execute();
                    Functions.setDisconnectedFromPUCampusFlag(context, 0);
                } else {
                    Functions.setDisconnectedFromPUCampusFlag(context, 1);
                }
            }
        } else {
            Functions.setDisconnectedFromPUCampusFlag(context, 1);
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
