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
        new LogoutTask(this).execute();
        this.stopSelf();
    }
}
