package eyeclass.eyeclassapp.teacher;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import eyeclass.eyeclassapp.R;

public class LessonSelect extends AppCompatActivity {

    Spinner curriculums;
    Spinner headlines;
    Spinner classes;

    String selected_curriculum = "";
    String selected_class = "";
    long selected_lesson_id = -1;

    HashMap<String, HashMap<String, Long>> curculum_headline_id = new HashMap<>();
    HashMap<String, String> class_to_id = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_lesson);
        curriculums = findViewById(R.id.spinner_curriculum);
        headlines = findViewById(R.id.spinner_headline);
        classes = findViewById(R.id.spinner_class);

        //get data
        try {
            new lessonsData().execute().get();
            new classesData().execute().get();
            initSpinnersListeners();
            initCurculumSpinner();
            initClassSpinner();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSpinnersListeners(){
        curriculums.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_curriculum = parent.getItemAtPosition(position).toString();
                initHeadlinesSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        headlines.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String headline = parent.getItemAtPosition(position).toString();
                selected_lesson_id = curculum_headline_id.get(selected_curriculum).get(headline);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        classes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String classStr = parent.getItemAtPosition(position).toString();
                selected_class = class_to_id.get(classStr);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initClassSpinner()
    {
        List<String> data =  new ArrayList<>();
        for (Object obj : class_to_id.keySet())
            data.add(obj.toString());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classes.setAdapter(adapter);
    }

    private void initHeadlinesSpinner()
    {
        List<String> data =  new ArrayList<>();
        for (Object obj : curculum_headline_id.get(selected_curriculum).keySet())
            data.add(obj.toString());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        headlines.setAdapter(adapter);
    }

    private void initCurculumSpinner()
    {
        List<String> data =  new ArrayList<>();
        for (Object obj : curculum_headline_id.keySet())
            data.add(obj.toString());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        curriculums.setAdapter(adapter);
    }

    public void startLesson(View view) {
        if (selected_class == "" || selected_lesson_id == -1)
        {
            Toast.makeText(this, "Please fill all data", Toast.LENGTH_LONG).show();
            return;
        }

         Intent teacher = new Intent(this, TeacherLesson.class);
         teacher.putExtra("class_id", selected_class);
         teacher.putExtra("lesson_id", selected_lesson_id);
         startActivity(teacher);

    }

    class lessonsData extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            URL url = null;
            try {
                String data = "req=lesson_select";
                url = new URL(Infra.Constants.Connections.TeacherServlet());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.connect();
                java.io.OutputStreamWriter wr = new java.io.OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    // Append server response in string
                    sb.append(line);
                }

                curculum_headline_id = new Gson().fromJson(sb.toString(),new TypeToken<HashMap<String, HashMap<String, Long>>>(){}.getType());

            }
            catch (Exception e){}
            return null;
        }
    }

    class classesData extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            URL url = null;
            try {
                String data = "req=classes";
                url = new URL(Infra.Constants.Connections.TeacherServlet());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.connect();
                java.io.OutputStreamWriter wr = new java.io.OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    // Append server response in string
                    sb.append(line);
                }

                class_to_id = new Gson().fromJson(sb.toString(),new TypeToken<HashMap<String, String>>(){}.getType());

            }
            catch (Exception e){}
            return null;
        }
    }
}
