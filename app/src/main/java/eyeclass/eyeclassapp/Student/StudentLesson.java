package eyeclass.eyeclassapp.Student;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import Infra.Constants;
import eyeclass.eyeclassapp.R;

public class StudentLesson extends AppCompatActivity implements OnPageChangeListener {

    PDFView pdfView;
    int m_page;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_lesson);

        pdfView = (PDFView) findViewById(R.id.StudentPDFView);
        //DELETE

        try {
            //new StartLesson().execute().get(); //TODO = this is for teacher !!!!!!!!
            InputStream pdf = new pdf().execute().get();

            pdfView.fromStream(pdf).onPageChange(this).load();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        m_page = page;
    }

    class pdf extends AsyncTask<Void,Void,InputStream>
    {

        @Override
        protected InputStream doInBackground(Void... voids) {
            URL url = null;
            try {
                String data = "req=display_pdf";
                url = new URL(Constants.Connections.StudentServlet());
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.connect();
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write( data );
                wr.flush();

                return conn.getInputStream();

            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }
    }


}