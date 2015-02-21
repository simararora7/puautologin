package simararora.puautologin.widget;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import simararora.puautologin.Functions;
import simararora.puautologin.LoginTask;

/**
 * Created by Simar Arora on 2/14/2015.
 *
 */
public class LoginService extends IntentService {

    public LoginService(String name) {
        super(name);
    }

    public LoginService() {
        super("LoginService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!Functions.isInitialised(this))
            return;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if ((networkInfo != null) && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) && (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))) {
            if (Functions.isPUCampus(this)){
                new LoginTask(this).execute();
            }else{
                Functions.sendNotification(this, "Not Connected To PU@Campus", false);
            }
        }else{
            Functions.sendNotification(this, "Not Connected To Wifi", false);
        }
        this.stopSelf();
    }
}

