package eyeclass.eyeclassapp.teacher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import eyeclass.eyeclassapp.R;

public class TeacherMainActivity extends AppCompatActivity {
    private Button buttonChooseLesson, buttonAddLesson, buttonDistractionReport, buttonQuestionsReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_first_screen);

        buttonChooseLesson = findViewById(R.id.buttonChooseLesson);
        buttonAddLesson = findViewById(R.id.buttonAddLesson);
        buttonDistractionReport = findViewById(R.id.buttonDistractionReport);
        buttonQuestionsReport = findViewById(R.id.buttonQuestionsReport);
        /*buttonChooseLesson.setOnClickListener(new View.OnClickListener(){

            @Override
            public void OnClick(View v){
                openActivity2();
            }
        });*/
    }
    /*public void openActivity2(){
        Intent intent = new Intent(this, .class);
        startActivity(intent);

    }*/
}
