package simararora.puautologin.widget;

import android.app.IntentService;
import android.content.Intent;

import simararora.puautologin.LoginTask;

/**
 * Created by Simar Arora on 2/14/2015.
 *
 */
public class LoginService extends IntentService {

    public LoginService(String name) {
        super(name);
    }

    public LoginService(){
        super("LoginService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        new LoginTask(this).execute();
    }
}
