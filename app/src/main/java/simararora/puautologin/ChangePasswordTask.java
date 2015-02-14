package simararora.puautologin;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * Created by Simar Arora on 2/4/2015.
 *
 */
public class ChangePasswordTask extends AsyncTask<String, String, Void> {

    private static final String changePasswordURL = "http://netportal.pu.ac.in/change-password.php";
    private String userName = "12uit359";
    private String oldPassword = "Simar@765";
    private String newPassword = "Simar@7654";

    @Override
    protected Void doInBackground(String... params) {
        loginToPortal();
        changePassword();
        logoutFromPortal();
        return null;
    }

    private void loginToPortal() {

    }

    private void changePassword() {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        BufferedReader bufferedReader =null;
        String urlParameters = "user_name=" +userName + "&oldpass=" + oldPassword + "&passwd=" + newPassword + "&rnewpass=" + newPassword;
        try{
            URL url = new URL(changePasswordURL);
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
                Log.d("Simar", line);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
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
    }

    private void logoutFromPortal() {

    }
}
