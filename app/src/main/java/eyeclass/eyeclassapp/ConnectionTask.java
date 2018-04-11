package eyeclass.eyeclassapp;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class ConnectionTask extends AsyncTask<String, Void, Integer> {
    @Override
    protected Integer doInBackground(String... params) {
        String id = params[0];
        String pass = params[1];
        String data = null;
        try {
            data = URLEncoder.encode("id", "UTF-8")
                    + "=" + URLEncoder.encode(id, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8")
                    + "=" + URLEncoder.encode(pass, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        BufferedReader reader=null;
        String userPerm = "";
        // Send data
        try
        {
            // Defined URL  where to send data
            URL url = new URL("http:/192.168.194.95:8080/EyeClass/login");

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.connect();
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write( data );
            wr.flush();

            // Get the server response

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                // Append server response in string
                sb.append(line + "\n");
            }

            userPerm = sb.toString().trim();
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
        }
        finally
        {
            try
            {

                reader.close();
            }

            catch(Exception ex) {;System.out.println(ex.toString());}
        }

        // Show response on activity
        return Integer.parseInt(userPerm);
    }
}
