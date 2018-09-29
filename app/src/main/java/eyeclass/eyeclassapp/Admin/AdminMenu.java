package eyeclass.eyeclassapp.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import eyeclass.eyeclassapp.R;

public class AdminMenu  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);
    }

    public void usersList(View view) {
        Intent intent = new Intent(this, AllUsers.class);
        startActivity(intent);
    }

    public void showDeviation(View view)
    {
        Intent intent = new Intent(this, DeviationForAdmin.class);
        startActivity(intent);
    }

    public void addUser(View view)
    {
        Intent intent = new Intent(this, AdminAddUser.class);
        startActivity(intent);
    }
}
