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
        pdfView = (PDFView) findViewById(R.id.pdfView);
        new RetrivePDFStream().execute("http://www.pdf995.com/samples/pdf.pdf");
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

}
