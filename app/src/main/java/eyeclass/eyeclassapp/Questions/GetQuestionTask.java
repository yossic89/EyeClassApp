package eyeclass.eyeclassapp.Questions;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import Infra.Constants;

public class GetQuestionTask  extends AsyncTask<Void, Void, Integer> {

    @Override
    protected Integer doInBackground(Void... params) {
        String data = null;
        try {
            data = URLEncoder.encode("action", "UTF-8")
                    + "=" + URLEncoder.encode("get", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String line = null;
        StringBuilder sb = new StringBuilder();
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
            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                // Append server response in string
                sb.append(line);
            }
            System.out.println("************ sb is: " + sb);
            if (!(sb.toString().equals("null")))
                setQuestionData(sb.toString());

        }
        catch(Exception ex)
        {
            System.out.println("on ex:" + ex.toString());
        }

        if (!(sb.toString().equals("null"))) return 1;
        else return 0;
    }

    public static String getQuestionData() {
        return questionData;
    }

    private void setQuestionData(String questionData) {
        this.questionData =  questionData;
    }

    private static String questionData;
}
