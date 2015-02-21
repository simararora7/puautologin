package simararora.puautologin;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginTask extends AsyncTask<Void, String, Void> {

    private Context context;
    private static final String loginURL = "https://securelogin.arubanetworks.com/cgi-bin/login?cmd=login";

    public LoginTask(Context context) {
        this.context = context;
    }

    @SuppressWarnings("UnusedParameters")
    public LoginTask(Context context, boolean fromMainActivity){
        this.context = context;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if ((networkInfo != null) && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) && (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))) {
            if (!Functions.isPUCampus(context)){
                Functions.sendNotification(context, "Not Connected To PU@Campus", false);
                this.cancel(true);
            }
        }else{
            Functions.sendNotification(context, "Not Connected To Wifi", false);
            this.cancel(true);
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d("Simar", "Login");
        String userName = Functions.getActiveUserName(context);
        String password = Functions.getPasswordForUserName(context, userName);
        String urlParameters = "user=" + userName + "&password=" + password;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(loginURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    "" + Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            DataOutputStream dataOutputStream = new DataOutputStream(
                    connection.getOutputStream());
            dataOutputStream.writeBytes(urlParameters);
            dataOutputStream.flush();
            dataOutputStream.close();

            inputStream = connection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("External Welcome Page")) {
                    publishProgress("Login Successful", "true");
                    break;
                } else if (line.contains("Authentication failed")) {
                    publishProgress("Authentication Failed", "false");
                    break;
                } else if (line.contains("Only one user login session is allowed")) {
                    publishProgress("Only one user login session is allowed", "false");
                    break;
                }
            }

        } catch (Exception ignored) {
        } finally {
            if (connection != null)
                connection.disconnect();
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        boolean showAction = Boolean.parseBoolean(values[1]);
        Functions.sendNotification(context, values[0], showAction);
    }
}

