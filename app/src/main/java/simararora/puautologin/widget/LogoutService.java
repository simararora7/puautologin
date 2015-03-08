package simararora.puautologin.widget;

import android.app.IntentService;
import android.content.Intent;

import simararora.puautologin.Functions;
import simararora.puautologin.LogoutTask;

/**
 * Created by Simar Arora on 2/14/2015.
 * This App is Licensed under GNU General Public License. A copy of this license can be found in the root of this project.
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
