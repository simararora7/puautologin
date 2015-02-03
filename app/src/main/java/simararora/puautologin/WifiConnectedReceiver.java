package simararora.puautologin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by Simar Arora on 2/3/2015.
 *
 */
public class WifiConnectedReceiver extends BroadcastReceiver{
    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(!isUsernamePasswordInitialised())
            return;
        this.context = context;
        String action = intent.getAction();
        if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if(networkInfo != null
                    && NetworkInfo.State.CONNECTED.equals(networkInfo.getState())){
                if (canLogin()) {
                    startLoginTask();
                }
            }
        }else if(action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)){
            //TODO show notification
        }
    }

    private void startLoginTask() {
        new LoginTask(context).execute(getUserNameFromSharedPreferences(), getPasswordFromSharedPreferences());
    }

    private boolean canLogin(){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                String ssid = wifiInfo.getSSID().toLowerCase();
                if(ssid.contains("pu@campus")){
                    return true;
                }
            }
        }
        return false;
    }

    private String getUserNameFromSharedPreferences(){
        return "";
    }

    private String getPasswordFromSharedPreferences(){
        return "";
    }

    private boolean isUsernamePasswordInitialised(){
        return true;
    }
}
