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

public class SaveStudAnswerTask extends AsyncTask<String, Void, Integer> {
    @Override
    protected Integer doInBackground(String... params) {
        String questionId = params[0];
        String question = params[1];
        String isGoodAns = params[2];
        String studAnswer = params[3];
        String rightAnswer = params[4];

        String data = null;
        try {
            data = URLEncoder.encode("action", "UTF-8") + "=" + URLEncoder.encode("save", "UTF-8");
            data += "&" + URLEncoder.encode("question_id", "UTF-8") + "=" + URLEncoder.encode(questionId, "UTF-8");
            data += "&" + URLEncoder.encode("question", "UTF-8") + "=" + URLEncoder.encode(question, "UTF-8");
            data += "&" + URLEncoder.encode("is_good_answer", "UTF-8") + "=" + URLEncoder.encode(isGoodAns, "UTF-8");
            data += "&" + URLEncoder.encode("student_answer", "UTF-8") + "=" + URLEncoder.encode(studAnswer, "UTF-8");
            data += "&" + URLEncoder.encode("right_answer", "UTF-8") + "=" + URLEncoder.encode(rightAnswer, "UTF-8");


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
            System.out.println("on ex:" + ex.toString());
        }

        return 1;
    }
}
