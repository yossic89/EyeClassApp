package eyeclass.eyeclassapp.Admin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

import eyeclass.eyeclassapp.R;

public class AdminAddUser extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_user);

        Spinner spinner = findViewById(R.id.view_user_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.view_user_type_option_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        String[] classes = {"A1", "A2", "B1", "B2"};
        Spinner classesSpinner = findViewById(R.id.view_classes_spinner);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, classes);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classesSpinner.setAdapter(adapter2);
        //classesSpinner.setEnabled(false);

        ArrayList<String> cur;
        cur=new ArrayList<String>();
        cur.add("Bible");
        cur.add("Math");
        cur.add("Science");
        cur.add("Logic");
        Spinner curSpinner = findViewById(R.id.view_cur_spinner);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, cur);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        curSpinner.setAdapter(adapter3);
        //curSpinner.setEnabled(false);


    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Spinner spin = (Spinner)parent;
        if(spin.getId() == R.id.view_user_type_spinner){
            chooseUserType(pos);
        }
        if(spin.getId() == R.id.view_classes_spinner){
            chooseClass(pos);
        }
        if(spin.getId() == R.id.view_cur_spinner){
            chooseCurriculum(pos);
        }

    }

    public void chooseUserType(int position){
            switch(position){
            case 0://Student
                setContentView(R.layout.admin_add_student);
                break;
            case 1://Teacher
                setContentView(R.layout.admin_add_teacher);
                break;

        }
    }

    public void chooseClass(int position){
        //set class and send to DB
    }

    public void chooseCurriculum(int position){
        //set curriculum and send to DB
    }
}
