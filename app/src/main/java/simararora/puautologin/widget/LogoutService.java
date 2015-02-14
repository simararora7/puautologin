package simararora.puautologin.widget;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Simar Arora on 2/14/2015.
 *
 */
public class LogoutService extends IntentService {

    public LogoutService(String name) {
        super(name);
    }

    public LogoutService(){
        super("LogoutService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        new simararora.puautologin.LogoutTask(this).execute();
    }
}
