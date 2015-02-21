package simararora.puautologin.widget;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import simararora.puautologin.Functions;
import simararora.puautologin.LogoutTask;

/**
 * Created by Simar Arora on 2/14/2015.
 *
 */
public class LogoutService extends IntentService {

    public LogoutService(String name) {
        super(name);
    }

    public LogoutService() {
        super("LogoutService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if ((networkInfo != null) && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) && (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))) {
            if (Functions.isPUCampus(this)) {
                new LogoutTask(this).execute();
            }else{
                Functions.sendNotification(this, "Not Connected To PU@Campus", false);
            }
        }else{
            Functions.sendNotification(this, "Not Connected To Wifi", false);
        }
        this.stopSelf();
    }
}
