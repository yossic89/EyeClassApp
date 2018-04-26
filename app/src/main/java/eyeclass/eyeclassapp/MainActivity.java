package eyeclass.eyeclassapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.concurrent.ExecutionException;

import Infra.Constants;
import eyeclass.eyeclassapp.Student.StudentLesson;
import eyeclass.eyeclassapp.Student.StudentSchedule;
import eyeclass.eyeclassapp.teacher.TeacherLesson;

public class MainActivity extends AppCompatActivity {

    private EditText mIdView;
    private EditText mPasswordView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    }

    private void attemptLogin() throws UnsupportedEncodingException, ExecutionException, InterruptedException {

        // Store values at the time of the login attempt.
        String id = mIdView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError("הסיסמא צריכה להכיל יותר מ 4 תווים");
            focusView = mPasswordView;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
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



}


