package eyeclass.eyeclassapp.teacher;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Infra.Constants;
import eyeclass.eyeclassapp.Questions.QuestionPopUp;
import eyeclass.eyeclassapp.R;

public class TeacherLesson extends AppCompatActivity implements OnPageChangeListener, AdapterView.OnItemSelectedListener{

    PDFView pdfView;
    private String questionsFromServer = null;
    private String class_id;
    private long lesson_id;
    private List<StudentActiveItem> studList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        class_id = getIntent().getExtras().getString("class_id");
        lesson_id =getIntent().getExtras().getLong("lesson_id");
        studList = new ArrayList<StudentActiveItem>();
        setContentView(R.layout.activity_teacher_lesson);
        pdfView = (PDFView) findViewById(R.id.TeacherPDFView);
        try{
            new StartLesson().execute().get();
            InputStream pdf = new pdf().execute().get();
            pdfView.fromStream(pdf).onPageChange(this).load();
            new studentsStatus().execute(true);
            new QuestionsLesson().execute();
        }
        catch (Exception er) { er.printStackTrace();
        }
        initTrackerSwitch();
        buildQuestionsSelection();

        //build students status spinner
        //spinner to change students list
        Spinner spinner = (Spinner) findViewById(R.id.view_students_spinner);
        spinner.setOnItemSelectedListener(this);
        //create array of options
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.view_students_option_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter2);
    }

    private void initTrackerSwitch()
    {
        Switch s = (Switch)findViewById(R.id.tracker_switch);
        s.setChecked(true);
        s.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is chkIos checked?
                boolean isChecked = ((Switch) v).isChecked();
                new Tracker().execute(isChecked);
            }
        });
    }

    @Override
    public void onBackPressed() {
        endLessonHandler();
        return;
    }

    private void endLessonHandler()
    {
        try {
            new endLesson().execute().get();
        } catch (Exception e) {
            System.out.println("Error in exit lesson");
            e.printStackTrace();
        }
        finish();
    }

    private void buildQuestionsSelection()
    {
        waitForQuestionsFromServer();
        JSONArray jsonArrayQuestions;
        String t ="";

        try {
            jsonArrayQuestions = new JSONArray(questionsFromServer);
            for(int i=1;i<=jsonArrayQuestions.length();i++) {
                JSONObject jsonObject = new JSONObject(jsonArrayQuestions.get(i-1).toString());
                t = jsonObject.getString("topic");
                TableLayout ll = (TableLayout) findViewById(R.id.questionsTableLayout);
                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                TextView topic = new TextView(this);
                topic.setText(t);
                topic.setTextColor(Color.parseColor("#000000"));
                topic.setPadding(10, 10, 10, 10);
                Button deliverQue = new Button(this);
                deliverQue.setText("Show Question");
                deliverQue.setTag(i-1);
                deliverQue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(TeacherLesson.this, QuestionPopUp.class);
                            int index = Integer.valueOf(v.getTag().toString());
                            JSONObject jsonObject = new JSONObject(jsonArrayQuestions.get(index).toString());
                            intent.putExtra("questionData", jsonObject.toString());
                            intent.putExtra("class_id", class_id);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }});
                // deliverQue.setPadding(20, 10, 10, 10);
                row.setGravity(Gravity.LEFT);
                ///row.setPadding(20,10,10,20);
                row.addView(topic, new TableRow.LayoutParams(0,
                        TableRow.LayoutParams.WRAP_CONTENT, 1f));
                row.addView(deliverQue);
                ll.addView(row, i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setQuestionsFromServer(String questions){
        questionsFromServer = questions;
    }

    private void setPieChart()
    {
        PieChart pie = (PieChart)findViewById(R.id.pie_chart);
        List<PieEntry> entries = new ArrayList<>();
        //count students status
        int concentrated = 0;
        int notConcentrated = 0;
        int unknown = 0;
        for (StudentActiveItem k:studList ) {
            if (k.state == Constants.StudentActiveStateNew.NotConcentrated)
                notConcentrated++;
            else if (k.state == Constants.StudentActiveStateNew.Concentrated)
                concentrated++;
            else if (k.state == Constants.StudentActiveStateNew.Unknown)
                unknown++;
        }

        entries.add(new PieEntry(concentrated, "Concentrated"));
        entries.add(new PieEntry(notConcentrated, "Not Concentrated"));
        entries.add(new PieEntry(unknown, "Unknown"));
        PieDataSet set = new PieDataSet(entries, "Student status");
        set.setColors(new int[] { Color.GREEN, Color.RED, Color.GRAY});
        PieData data = new PieData(set);
        data.setHighlightEnabled(false);
        pie.setData(data);
        pie.setDrawCenterText(true);
        pie.setDrawHoleEnabled(false);
        pie.setEntryLabelColor(Color.BLACK);
        pie.invalidate(); // refresh
    }

    private void handleSpinnerStatus(int pos)
    {
        //if select pie chart
        if (pos == 4)
        {
            findViewById(R.id.studList).setVisibility(View.GONE);
            findViewById(R.id.pie_chart).setVisibility(View.VISIBLE);
            setPieChart();
        }

        else
        {
            findViewById(R.id.studList).setVisibility(View.VISIBLE);
            findViewById(R.id.pie_chart).setVisibility(View.GONE);
            //activate the correct choice
            displayByPosition(pos);
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        handleSpinnerStatus(pos);
    }
    public void onNothingSelected(AdapterView<?> parent) {}

    public void displayByPosition(int position){
        List<StudentActiveItem> studFilter = new ArrayList<StudentActiveItem>();
        for (StudentActiveItem k:studList ) {
            if (position==0)//All
                studFilter.add(k);
            else if (position==1 && k.state == Constants.StudentActiveStateNew.NotConcentrated)
                studFilter.add(k);
            else if (position==2 && k.state == Constants.StudentActiveStateNew.Concentrated)
                studFilter.add(k);
            else if (position==3 && k.state == Constants.StudentActiveStateNew.Unknown)
                studFilter.add(k);
        }
        String[] studNames =new String[studFilter.size()];

        Integer[] images = new Integer[studNames.length];

        int i=0;
        for (StudentActiveItem k:studFilter ) {
            if(k.state == Constants.StudentActiveStateNew.Concentrated)
                images[i]=R.drawable.greendot;
            else if (k.state == Constants.StudentActiveStateNew.NotConcentrated)
                images[i]=R.drawable.reddot;
            else if (k.state == Constants.StudentActiveStateNew.Unknown)
                images[i]=R.drawable.greydot;
            studNames[i]=k.name;
            i++;
        }
        ListView listView = (ListView)findViewById(R.id.studList);
        CustomListAdapter adapter=new CustomListAdapter(this, studNames, images);
        listView.setAdapter(adapter);
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        new sendTeacherPage().execute(page);
    }

    class endLesson extends  AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            URL url = null;
            try {
                String data = "req=end_lesson";
                data += "&class_id=" + class_id;
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
            }
            catch (Exception e){}
            return null;
        }
    }

    class sendTeacherPage extends AsyncTask<Integer, Void, Void>
    {
        @Override
        protected Void doInBackground(Integer... integers) {
            URL url = null;
            try {
                String data = "req=teacher_page";
                data += "&" + Infra.Constants.Teacher.Class_id + "=" + class_id;
                data += "&page=" + integers[0];
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
            }
            catch (Exception e){}
            return null;
        }
    }

    class StudentActiveItem{
        String name;
        Constants.StudentActiveStateNew state;

        public StudentActiveItem(String _name, Constants.StudentActiveStateNew _state) {
            name=_name;
            state=_state;
        }
    }

    class RetrivePDFStream extends AsyncTask<String,Void,InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) {
                return null;
            }
            return inputStream;
        }
        @Override
        protected void onPostExecute(InputStream inputStream){
            pdfView.fromStream(inputStream).load();

        }
    }

    class studentsStatus extends AsyncTask<Boolean, Void, Void>
    {

        @Override
        protected Void doInBackground(Boolean... booleans) {
            URL url = null;
            boolean isFirstTime = booleans[0];
            try {
                if (!isFirstTime)
                    Thread.sleep(2000);
                String data = "req=students_status";
                data += "&" + Infra.Constants.Teacher.Class_id + "=" + class_id;
                url = new URL(Infra.Constants.Connections.TeacherServlet());
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.connect();
                java.io.OutputStreamWriter wr = new java.io.OutputStreamWriter(conn.getOutputStream());
                wr.write( data );
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                // Read Server Response
                while((line = reader.readLine()) != null)
                {
                    // Append server response in string
                    sb.append(line);
                }

                Map<String, String> stuendts_status = new Gson().fromJson(sb.toString(), new TypeToken<Map<String, String>>(){}.getType());
                studList.clear();
                for (String key : stuendts_status.keySet())
                {
                    Infra.Constants.StudentActiveStateNew e_val = Infra.Constants.StudentActiveStateNew.valueOf(stuendts_status.get(key));
                    studList.add(new StudentActiveItem(key, e_val));

                }

                return null;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            //get spinner position
            int pos = ((Spinner) findViewById(R.id.view_students_spinner)).getSelectedItemPosition();
            handleSpinnerStatus(pos);
            if (!isFinishing())
                new studentsStatus().execute(false);
        }
    }

    class pdf extends AsyncTask<Void,Void,InputStream> {

        @Override
        protected InputStream doInBackground(Void... voids) {
            URL url = null;
            try {
                String data = "req=display_pdf";
                data += "&" + Infra.Constants.Teacher.Class_id + "=" +class_id;
                url = new URL(Infra.Constants.Connections.TeacherServlet());
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.connect();
                java.io.OutputStreamWriter wr = new java.io.OutputStreamWriter(conn.getOutputStream());
                wr.write( data );
                wr.flush();

                return conn.getInputStream();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class StartLesson extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            URL url = null;
            try {
                String data = "req=start_lesson";
                data += "&class_id=" + class_id + "&lesson_id=" + lesson_id;
                url = new URL(Infra.Constants.Connections.TeacherServlet());
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.connect();
                java.io.OutputStreamWriter wr = new java.io.OutputStreamWriter(conn.getOutputStream());
                wr.write( data );
                wr.flush();

                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                // Read Server Response
                while((line = reader.readLine()) != null)
                {
                    // Append server response in string
                    sb.append(line);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    class Tracker extends AsyncTask<Boolean, Void, Void>
    {

        @Override
        protected Void doInBackground(Boolean... booleans) {
            boolean track = booleans[0];
            URL url = null;
            StringBuilder sb = new StringBuilder();
            try {
                String data = "req=tracker";
                data += "&" + Infra.Constants.Teacher.Class_id + "=" + class_id;
                data += "&track=" + track;
                url = new URL(Infra.Constants.Connections.TeacherServlet());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.connect();
                java.io.OutputStreamWriter wr = new java.io.OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;
                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    // Append server response in string
                    sb.append(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class QuestionsLesson extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String s = getQuestionsFromServer();
            setQuestionsFromServer(s);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }

    private String getQuestionsFromServer() {
        URL url = null;
        StringBuilder sb = new StringBuilder();
        try {
            String data = "req=questions_lesson";
            data += "&" + Infra.Constants.Teacher.Class_id + "=" + class_id;
            url = new URL(Infra.Constants.Connections.TeacherServlet());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.connect();
            java.io.OutputStreamWriter wr = new java.io.OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public void waitForQuestionsFromServer() {
        int count = 0;
        while(questionsFromServer == null && count < 60){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        if (count >= 60){
            System.out.println("NO QUESTIONS FROM SERVER!!");
        }
    }

}
