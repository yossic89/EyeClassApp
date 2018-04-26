package eyeclass.eyeclassapp.teacher;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import eyeclass.eyeclassapp.R;

public class TeacherLesson extends AppCompatActivity {

    PDFView pdfView;
    int m_page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String[] studNames ={
                "ימית שפלר",
                "גל טליה",
                "בני כהן",
                "נועם שי",
                "יוסי כהן",
                "עידו לוי",
                "מירב לוי",
                "יוסי דן",
                "מור פרץ",
                "עומר לרר",
                "דפנה דן",
                "ג'וסי פרץ",
        };


        Integer[] images = new Integer[studNames.length];
        for (int i=0;i<studNames.length;i++){
            images[i]=R.drawable.greendot;
        }

        for (int i=0;i<studNames.length;i++) {
            if (studNames[i].equals("מור פרץ") || (studNames[i].equals("יוסי כהן") ||(studNames[i].equals("ימית שפלר"))))
                images[i]=R.drawable.reddot;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_lesson);
        pdfView = (PDFView) findViewById(R.id.TeacherPDFView);
        try{
            new StartLesson().execute().get();
            new pdf().execute();
        }
        catch (Exception e) { e.printStackTrace();}
        //new RetrivePDFStream().execute("http://www.pdf995.com/samples/pdf.pdf");
        ListView listView = (ListView)findViewById(R.id.studList);
        CustomListAdapter adapter=new CustomListAdapter(this, studNames, images);
        listView.setAdapter(adapter);
//        listView.setAdapter(new ArrayAdapter<String>(
//                this, R.layout.teacher_lesson_stud_list,
//                R.id.StudNameListFocus,studNames,R.id.ImageListFocus,images));

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

     class pdf extends AsyncTask<Void,Void,InputStream>
    {

        @Override
        protected InputStream doInBackground(Void... voids) {
            URL url = null;
            try {
                String data = "req=display_pdf";
                data += "&" + Infra.Constants.Teacher.Class_id + "=" + Infra.Constants.Teacher.Demo_class_id;
                url = new URL(Infra.Constants.Connections.TeacherServlet);
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

    class StartLesson extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            URL url = null;
            try {
                String data = "req=demo_lesson";
                url = new URL(Infra.Constants.Connections.TeacherServlet);
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
