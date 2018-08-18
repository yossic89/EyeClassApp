package eyeclass.eyeclassapp.teacher;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
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
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import Infra.Constants;
import eyeclass.eyeclassapp.R;

public class TeacherLesson extends AppCompatActivity implements OnPageChangeListener, AdapterView.OnItemSelectedListener{

    PDFView pdfView;

    private List<StudentActiveItem> studList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studList = new ArrayList<StudentActiveItem>();
        setContentView(R.layout.activity_teacher_lesson);
        pdfView = (PDFView) findViewById(R.id.TeacherPDFView);
        try{
            new StartLesson().execute().get();
            InputStream pdf = new pdf().execute().get();
            pdfView.fromStream(pdf).onPageChange(this).load();
            new studentsStatus().execute();
        }
        catch (Exception er) { er.printStackTrace();
        }
    }

    public void displayStudentsStatus()
    {
        String[] studNames =new String[studList.size()];

        Integer[] images = new Integer[studNames.length];

        int i=0;
        for (StudentActiveItem k:studList ) {
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
//        listView.setAdapter(new ArrayAdapter<String>(
//                this, R.layout.teacher_lesson_stud_list,
//                R.id.StudNameListFocus,studNames,R.id.ImageListFocus,images));

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
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        //activate the correct choice
        displayByPosition(pos);
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

    class sendTeacherPage extends AsyncTask<Integer, Void, Void>
    {
        @Override
        protected Void doInBackground(Integer... integers) {
            URL url = null;
            try {
                String data = "req=teacher_page";
                data += "&" + Infra.Constants.Teacher.Class_id + "=" + Infra.Constants.Teacher.Demo_class_id;
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

    class studentsStatus extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            URL url = null;
            try {
                String data = "req=students_status";
                data += "&" + Infra.Constants.Teacher.Class_id + "=" + Infra.Constants.Teacher.Demo_class_id;
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
            displayStudentsStatus();
        }
    }

     class pdf extends AsyncTask<Void,Void,InputStream> {

        @Override
        protected InputStream doInBackground(Void... voids) {
            URL url = null;
            try {
                String data = "req=display_pdf";
                data += "&" + Infra.Constants.Teacher.Class_id + "=" + Infra.Constants.Teacher.Demo_class_id;
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
                String data = "req=demo_lesson";
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

}
