package eyeclass.eyeclassapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import eyeclass.eyeclassapp.Admin.AdminMenu;
import eyeclass.eyeclassapp.Student.StudentNoLesson;
import eyeclass.eyeclassapp.teacher.TeacherMenu;

public class MainActivity extends AppCompatActivity {

    private EditText mIdView;
    private EditText mPasswordView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkForExtras();
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

    private void checkForExtras()
    {
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            if (extras.containsKey("developer_ip"))
                Constants.Connections.setIP(this.getIntent().getExtras().getString(("developer_ip")));
        }
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
                case Constants.Permissions.Error:
                    mPasswordView.setText("");
                    mPasswordView.setError("Network error");
                    break;
                case Constants.Permissions.NoPermission:
                    mPasswordView.setText("");
                    mPasswordView.setError("Wrong credentials");
                    break;
                case Infra.Constants.Permissions.Teacher:
                  //startActivity(new Intent(this, LessonSelect.class));
                    startActivity(new Intent(this, TeacherMenu.class));
                    break;
                case Constants.Permissions.Student:
                    startActivity(new Intent(this, StudentNoLesson.class));
                    break;
                case Constants.Permissions.Admin:
                    startActivity(new Intent(this, AdminMenu.class));
                    break;
            }
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }



}


