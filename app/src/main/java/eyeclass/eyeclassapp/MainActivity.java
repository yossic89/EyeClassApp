package eyeclass.eyeclassapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import Infra.Constants;
import eyeclass.eyeclassapp.Student.StudentSchedule;
import eyeclass.eyeclassapp.teacher.TeacherLesson;

public class MainActivity extends AppCompatActivity implements PictureCapturingListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private EditText mIdView;
    private EditText mPasswordView;

    //camera
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_CODE = 1;

    private ImageView uploadBackPhoto;

    //service
    private APictureCapturingService pictureService;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mPasswordView = (EditText) findViewById(R.id.password);
        mIdView = (EditText) findViewById(R.id.IdNum);

        Button mSignInButton = (Button) findViewById(R.id.signInBtn);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    attemptLogin();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //camera
        checkPermissions();
        uploadBackPhoto = (ImageView) findViewById(R.id.backIV);
        pictureService = PictureCapturingServiceImpl.getInstance(this);
     //   for(int i=0;i<5;i++) {
            pictureService.startCapturing(this);
      //  }

    }

    private void attemptLogin() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        // Store values at the time of the login attempt.
        String id = mIdView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError("Your password should contain 5 characters or more");
            focusView = mPasswordView;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            //startActivity(new Intent(this, TeacherLesson.class));
            int permmision = new ConnectionTask().execute(id, password).get();
            switch (permmision)
            {
                case Infra.Constants.Permissions.Teacher:
                    startActivity(new Intent(this, TeacherLesson.class));
                    break;
                case Constants.Permissions.Student:
                    startActivity(new Intent(this, StudentSchedule.class));
                    break;
            }
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
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
            runOnUiThread(() -> {
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
            });
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


