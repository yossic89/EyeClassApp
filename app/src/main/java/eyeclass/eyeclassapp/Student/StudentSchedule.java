package eyeclass.eyeclassapp.Student;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import Infra.Constants;
import eyeclass.eyeclassapp.MainActivity;
import eyeclass.eyeclassapp.R;

public class StudentSchedule extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_schedule);
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
}
