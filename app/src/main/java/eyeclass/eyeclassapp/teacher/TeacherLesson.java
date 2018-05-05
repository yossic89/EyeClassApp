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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Infra.Constants.StudentActiveState;
import eyeclass.eyeclassapp.R;

public class TeacherLesson extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    PDFView pdfView;
    int m_page;

    private List<StudentActiveItem> studList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //local students list
        StudentActiveItem a = new StudentActiveItem("Yamit Shefler", StudentActiveState.NotFollowing);
        StudentActiveItem b = new StudentActiveItem("Gal Talya", StudentActiveState.Following);
        StudentActiveItem c = new StudentActiveItem("Benni Cohen", StudentActiveState.Following);
        StudentActiveItem d = new StudentActiveItem("Noam Shay", StudentActiveState.NotConnected);
        StudentActiveItem e = new StudentActiveItem("Yossi Cohen", StudentActiveState.NotFollowing);
        StudentActiveItem f = new StudentActiveItem("Ido Levi", StudentActiveState.Following);
        StudentActiveItem g = new StudentActiveItem("Asaf Dan", StudentActiveState.Following);
        StudentActiveItem h = new StudentActiveItem("Mor Perets", StudentActiveState.NotFollowing);
        StudentActiveItem l = new StudentActiveItem("Omer Lerer", StudentActiveState.NotConnected);
        StudentActiveItem m = new StudentActiveItem("Jason Perets", StudentActiveState.Following);

        studList = new ArrayList<StudentActiveItem>();
        studList.add(a);
        studList.add(b);
        studList.add(c);
        studList.add(d);
        studList.add(e);
        studList.add(f);
        studList.add(g);
        studList.add(h);
        studList.add(l);
        studList.add(m);
        String[] studNames =new String[studList.size()];

        Integer[] images = new Integer[studNames.length];

        int i=0;
        for (StudentActiveItem k:studList ) {
            if(k.state == StudentActiveState.Following)
                images[i]=R.drawable.greendot;
            else if (k.state == StudentActiveState.NotFollowing)
                images[i]=R.drawable.reddot;
            else if (k.state == StudentActiveState.NotConnected)
                images[i]=R.drawable.greydot;
            studNames[i]=k.name;
            i++;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_lesson);
        pdfView = (PDFView) findViewById(R.id.TeacherPDFView);
        //new RetrivePDFStream().execute("http://www.pdf995.com/samples/pdf.pdf");
        try{
            new StartLesson().execute().get();
            new pdf().execute();
        }
        catch (Exception er) { er.printStackTrace();
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
            else if (position==1 && k.state == StudentActiveState.NotFollowing)
                studFilter.add(k);
            else if (position==2 && k.state == StudentActiveState.Following)
                studFilter.add(k);
            else if (position==3 && k.state == StudentActiveState.NotConnected)
                studFilter.add(k);
        }
        String[] studNames =new String[studFilter.size()];

        Integer[] images = new Integer[studNames.length];

        int i=0;
        for (StudentActiveItem k:studFilter ) {
            if(k.state == StudentActiveState.Following)
                images[i]=R.drawable.greendot;
            else if (k.state == StudentActiveState.NotFollowing)
                images[i]=R.drawable.reddot;
            else if (k.state == StudentActiveState.NotConnected)
            images[i]=R.drawable.greydot;
            studNames[i]=k.name;
            i++;
        }
        ListView listView = (ListView)findViewById(R.id.studList);
        CustomListAdapter adapter=new CustomListAdapter(this, studNames, images);
        listView.setAdapter(adapter);
    }

    class StudentActiveItem{
        String name;
        int state;

        public StudentActiveItem(String _name, int _state) {
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

         @Override
        protected void onPostExecute(InputStream inputStream){
            pdfView.fromStream(inputStream).load();
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
