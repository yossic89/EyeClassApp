package eyeclass.eyeclassapp.Student;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import Infra.Constants;
import Infra.EyesDetector;
import eyeclass.eyeclassapp.APictureCapturingService;
import eyeclass.eyeclassapp.PictureCapturingListener;
import eyeclass.eyeclassapp.PictureCapturingServiceImpl;
import eyeclass.eyeclassapp.Questions.GetQuestionTask;
import eyeclass.eyeclassapp.Questions.QuestionPopUp;
import eyeclass.eyeclassapp.Questions.QuestionPopUpStudent;
import eyeclass.eyeclassapp.R;
import eyeclass.eyeclassapp.teacher.TeacherLesson;

public class StudentLesson extends AppCompatActivity implements OnPageChangeListener, PictureCapturingListener, ActivityCompat.OnRequestPermissionsResultCallback {

    PDFView pdfView;
    int m_page;
    private boolean sendMyImage;
    private int sendDeviationTimerMS;
    EyesDetector eyesDetector = new EyesDetector();
    boolean isQuestionOn = false;
    long startTime;
    long timeForQuestion;

    //camera
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_CODE = 1;

    private ImageView uploadBackPhoto;

    //service
    private APictureCapturingService pictureService;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_lesson);

        pdfView = (PDFView) findViewById(R.id.StudentPDFView);
        try {
            new properties().execute().get();
            InputStream pdf = new pdf().execute().get();

            pdfView.fromStream(pdf).onPageChange(this).load();
        } catch (Exception e) {
            System.out.println("error - aaaaaaaaa");
            e.printStackTrace();
        }
        //init eyes detector
        eyesDetector.initClassifiers(getAssets(), getFilesDir());
        //fulscreen
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
        checkForQuestions();

        //camera
        checkPermissions();
        uploadBackPhoto = (ImageView) findViewById(R.id.backIV);
        pictureService = PictureCapturingServiceImpl.getInstance(this);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            System.out.println("error - bbbbbbb");
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        takePhoto(this);

    }


    private synchronized void takePhoto(PictureCapturingListener activity)
    {
        try
        {
            pictureService.startCapturing(activity);
        }
        catch (Exception e)
        {
            System.out.println("error in taking photo");
            e.printStackTrace();
            takePhoto(activity);
        }
    }

    public void goToTeacherPage(View view)
    {
        try
        {
            int teacher_page = new teacherPage().execute().get();
            String text = String.format("your page was %d, teacher page is %d", m_page, teacher_page);
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
            pdfView.jumpTo(teacher_page, true);
        }
        catch (Exception e){Toast.makeText(this, "Failed to get teacher page", Toast.LENGTH_LONG).show();}
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        synchronized (this)
        {
            m_page = page;
        }

    }

    class teacherPage extends AsyncTask<Void,Void,Integer>
    {

        @Override
        protected Integer doInBackground(Void... voids) {
            URL url = null;
            try {
                String data = "req=teacher_page";
                url = new URL(Constants.Connections.StudentServlet());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.connect();
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
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

                return Integer.parseInt(sb.toString().trim());
            }
            catch (Exception e){}
                return null;
        }
    }

    class properties extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            URL url = null;
            try {
                String data = "req=student_measure_params";
                url = new URL(Constants.Connections.StudentServlet());
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.connect();
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
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

                JSONObject jObj = new JSONObject(sb.toString());
                sendMyImage = jObj.getBoolean("ifSendPhoto");
                sendDeviationTimerMS = jObj.getInt("photoSampling");



            } catch (Exception e) {
                System.out.println("error - cccccccccc");
                e.printStackTrace();
            }
            return null;
        }
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
                System.out.println("error - dddddddddd");
                e.printStackTrace();
            }


            return null;
        }
    }


    //camera/////////////////////////////////////////
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {

        final String[] requiredPermissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
        };
        final List<String> neededPermissions = new ArrayList<>();
        for (final String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    permission) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(permission);
            }
        }
        if (!neededPermissions.isEmpty()) {
            requestPermissions(neededPermissions.toArray(new String[]{}),
                    MY_PERMISSIONS_REQUEST_ACCESS_CODE);
        }
    }

    /**
     * We've finished taking pictures from all phone's cameras
     */
    @Override
    public void onDoneCapturingAllPhotos(TreeMap<String, byte[]> picturesTaken) {
        if (picturesTaken != null && !picturesTaken.isEmpty()) {
            return;
        }
    }

    class deviationReportSend extends AsyncTask<DeviationData, Void, Void>
    {
        @Override
        protected Void doInBackground(DeviationData... deviationData) {
            URL url = null;
            try {
                String data = "req=measure_data&";
                synchronized (this)
                {
                    deviationData[0].setPage_num(m_page);
                }
                data += "data=" + new Gson().toJson(deviationData[0]);
                url = new URL(Constants.Connections.StudentServlet());
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.connect();
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
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

            } catch (Exception e) {
                System.out.println("error - eeeeeeeeee");
                e.printStackTrace();
            }

            return null;
        }

    }

    class checkIfLessonDone extends AsyncTask<Void, Void, Void>
    {
        boolean status = true;
        PictureCapturingListener activity;
        checkIfLessonDone(PictureCapturingListener _activity)
        {
            activity = _activity;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            URL url = null;
            try {
                //sleep before checking
                Thread.sleep(sendDeviationTimerMS);
                String data = "req=finish_lesson";
                url = new URL(Constants.Connections.StudentServlet());
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.connect();
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
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
                status = Boolean.parseBoolean(sb.toString().trim());
            }
            catch (Exception e) {
                System.out.println("error - fffffffffff");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            if (status)
                finish();
            else
                takePhoto(activity);
        }
    }

    /**
     * Displaying the pictures taken.
     */
    @Override
    public void onCaptureDone(String pictureUrl, byte[] pictureData) {
        if (pictureData != null && pictureUrl != null) {
            //Send deviation data
            int eyesCount = -1;
            synchronized (this)
            {
                eyesCount = eyesDetector.getEyesFromImage(pictureData);
            }
            //send photo if needed, otherwise send null
            byte[] picture = null;
            if (sendMyImage)
                picture = eyesDetector.getProcceedImage();
              //  picture = pictureData;
            DeviationData dd = new DeviationData(eyesCount, picture);
            new deviationReportSend().execute(dd);
        }
        try
        {
            new checkIfLessonDone(this).execute();
        }
        catch (Exception e){
            System.out.println("error - 111111111");e.printStackTrace();}
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_CODE: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    checkPermissions();
                }
            }
        }
    }

    public class DeviationData
    {
        public DeviationData(int _eyes, byte[] _photo)
        {
            eyes_count = _eyes;
            photo = _photo;
        }

        public void setPage_num(int page_num) {
            this.page_num = page_num;
        }

        public byte[] getPhoto() {
            return photo;
        }

        public int getPage_num() {
            return page_num;
        }

        public int getEyes_count() {
            return eyes_count;
        }

        byte[] photo;
        int page_num;
        int eyes_count;

    }



    public void checkForQuestions() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {

                        if(!isQuestionOn) {
                            try {
                                int questionsRes = new GetQuestionTask().execute().get();
                                if (isFinishing())
                                {
                                    cancel();
                                }
                                System.out.println("questionsRes " + questionsRes);
                                if (questionsRes == 1) {
                                    Intent intent = new Intent(StudentLesson.this, QuestionPopUpStudent.class);
                                    intent.putExtra("questionData", GetQuestionTask.getQuestionData());
                                    startActivityForResult(intent,100);
                                    isQuestionOn = true;
                                    startTime = System.currentTimeMillis();
                                    timeForQuestion = 31000;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else{
                            long different = System.currentTimeMillis() - startTime;
                            if (different >= timeForQuestion) {
                                finishActivity(100);
                                isQuestionOn = false;
                            }
                        }
                    }});}
        };
        timer.schedule(doAsynchronousTask, 0, 3000);

    }


}

