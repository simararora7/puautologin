package simararora.puautologin;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginTask extends AsyncTask<String, String, Void> {

    private Context context;
    private static final String loginURL = "https://securelogin.arubanetworks.com/cgi-bin/login?cmd=login";

    public LoginTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(context, "Logging In", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Void doInBackground(String... params) {
        String urlParameters = "user=" + params[0] + "&password=" + params[1];
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
                    publishProgress("Login Successful");
                    break;
                } else if (line.contains("Authentication failed")) {
                    publishProgress("Authentication Failed");
                    break;
                } else if (line.contains("Only one user login session is allowed")) {
                    publishProgress("Only one user login session is allowed");
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
        Toast.makeText(context, values[0], Toast.LENGTH_SHORT);
    }
}
