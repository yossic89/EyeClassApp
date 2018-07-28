package eyeclass.eyeclassapp.Student;


import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import Infra.EyesDetector;
import eyeclass.eyeclassapp.APictureCapturingService;
import eyeclass.eyeclassapp.PictureCapturingListener;
import eyeclass.eyeclassapp.PictureCapturingServiceImpl;
import eyeclass.eyeclassapp.R;

public class StudentLesson extends AppCompatActivity implements OnPageChangeListener, PictureCapturingListener, ActivityCompat.OnRequestPermissionsResultCallback {

    PDFView pdfView;
    int m_page;

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
        //DELETE

        try {
            //new StartLesson().execute().get(); //TODO = this is for teacher !!!!!!!!
            InputStream pdf = new pdf().execute().get();

            pdfView.fromStream(pdf).onPageChange(this).load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //camera
        checkPermissions();
        uploadBackPhoto = (ImageView) findViewById(R.id.backIV);
        pictureService = PictureCapturingServiceImpl.getInstance(this);
        //   for(int i=0;i<5;i++) {
        pictureService.startCapturing(this);
        //  }
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
     *
     * @param text The message to show
     */
    private void showToast(final String text) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show()
        );
    }

    /**
     * We've finished taking pictures from all phone's cameras
     */
    @Override
    public void onDoneCapturingAllPhotos(TreeMap<String, byte[]> picturesTaken) {
        if (picturesTaken != null && !picturesTaken.isEmpty()) {
            showToast("Done capturing all photos!");
            return;
        }
        showToast("No camera detected!");
    }

    /**
     * Displaying the pictures taken.
     */
    @Override
    public void onCaptureDone(String pictureUrl, byte[] pictureData) {
        System.out.println("7777777777777777777");
        if (pictureData != null && pictureUrl != null) {
            System.out.println("888888888");
            //TEST
            EyesDetector d = new EyesDetector();
            d.initClassifiers(getAssets(), getFilesDir());
            int a = d.getEyesFromImage(pictureData);
            //
            /*runOnUiThread(() -> {
                final Bitmap bitmap = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length);
                final int nh = (int) (bitmap.getHeight());
                final Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                if (pictureUrl.contains("12_pic.jpg")) {
                    System.out.println("999999999999999999");
                    uploadBackPhoto.setImageBitmap(scaled);
                } else if (pictureUrl.contains("1_pic.jpg")) {
                    System.out.println("000000000000");

                }
                else
                    System.out.println("aaaaaaaaaaaaaaaaaaaaa");
            });*/
            showToast("Picture saved to " + pictureUrl);
        }
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
}