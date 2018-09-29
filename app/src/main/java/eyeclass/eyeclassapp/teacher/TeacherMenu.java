package eyeclass.eyeclassapp.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import eyeclass.eyeclassapp.R;
import eyeclass.eyeclassapp.lesson.UploadLesson;

public class TeacherMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_first_screen);
    }

    public void uploadLesson(View view) {
        Intent intent = new Intent(this, UploadLesson.class);
        startActivity(intent);
    }

    public void selectLesson(View view) {
        Intent intent = new Intent(this, LessonSelect.class);
        startActivity(intent);
    }

    public void showDeviation(View view)
    {
        Intent intent = new Intent(this, DeviationForTeacher.class);
        startActivity(intent);
    }

    public void showQuestionReport(View view)
    {
        Intent intent = new Intent(this, QuestionStatisticForTeacher.class);
        startActivity(intent);
    }
}
