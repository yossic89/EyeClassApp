package eyeclass.eyeclassapp.Student;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import Infra.Constants;
import eyeclass.eyeclassapp.MainActivity;
import eyeclass.eyeclassapp.R;

public class StudentSchedule extends AppCompatActivity {
    private List<String> facts = new ArrayList<>();
    private int shuffleIndex = 0;
    TextView funFacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_schedule);
        funFacts = (TextView) findViewById(R.id.fun_facts);
        funFacts.setText("\n\nClick on shuffle to see some facts and learn something new!");
        funFacts.setTextColor(Color.parseColor("#870274"));
        Button suffle = (Button) findViewById(R.id.shuffle_btn);
        suffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funFacts.setTextColor(Color.parseColor("#000000"));
                if (facts.size()>0) funFacts.setText(facts.get(shuffleIndex));
                shuffleIndex++;
                if ( shuffleIndex > 20 ) shuffleIndex = 0;
            }});

    }

    @Override
    protected void onStart(){
        super.onStart();
        try {
            new WaitForLesson().execute();
            //startActivity(new Intent(StudentSchedule.this, StudentLesson.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class WaitForLesson extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            String data = "req=active_lesson";
            boolean keepWaiting = true;
            BufferedReader reader = null;
            getListOfFacts();


           //funFacts.setText(facts.get(0).toString());

            try{
                URL url = new URL(Constants.Connections.StudentServlet());

                while (keepWaiting)
                {
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.connect();
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write( data );
                    wr.flush();
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        // Append server response in string
                        sb.append(line);
                    }
                    keepWaiting = !Boolean.parseBoolean(sb.toString().trim());
                    Thread.sleep(Constants.Student.CheckActiveLessonMS);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            startActivity(new Intent(StudentSchedule.this, StudentLesson.class));
        }
    }

    private void getListOfFacts(){
        String url = "http://numbersapi.com/random/date";

        try {
            URL obj = new URL(url);
            int i = 20;
            while(i>0) {
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //print result
                facts.add(response.toString());
                i--;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
