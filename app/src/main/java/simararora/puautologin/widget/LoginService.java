package simararora.puautologin.widget;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import simararora.puautologin.Functions;
import simararora.puautologin.LoginTask;
import simararora.puautologin.NotificationService;

/**
 * Created by Simar Arora on 2/14/2015.
 *
 */
public class LoginService extends IntentService {
    private static final String loginURL = "https://securelogin.arubanetworks.com/cgi-bin/login?cmd=login";

    public LoginService(String name) {
        super(name);
    }

    public LoginService() {
        super("LoginService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
       new LoginTask(this).execute();
    }
}
