package eyeclass.eyeclassapp.Questions;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import Infra.Constants;

public class DeliveryQuestionsTask extends AsyncTask<String, Void, Integer> {
    @Override
    protected Integer doInBackground(String... params) {
        String questionData = params[0];
        String class_id = params[1];
        String data = null;
        try {
            data = URLEncoder.encode("questionData", "UTF-8")
                    + "=" + URLEncoder.encode(questionData, "UTF-8");
            data += "&" + Infra.Constants.Teacher.Class_id + "=" + class_id;
            if (questionData.equals("clear")) {
                data += "&" + URLEncoder.encode("action", "UTF-8")
                        + "=" + URLEncoder.encode("clear", "UTF-8");
            }else{
                data += "&" + URLEncoder.encode("action", "UTF-8")
                        + "=" + URLEncoder.encode("send", "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String userPerm = "";
        // Send data
        try
        {
            // Defined URL  where to send data
            URL url = new URL(Constants.Connections.QuestionsDeliveryServlet());
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.connect();
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write( data );
            wr.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return 1;
    }
}
