package eyeclass.eyeclassapp.Questions;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;

import eyeclass.eyeclassapp.R;

public class QuestionPopUpStudent extends Activity {
    public int index;
    public String questions = "";
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_pop_up_student);

        displayQuestions();
//        Button deliveryToStudents = (Button) findViewById(R.id.deliver_to_stud_btn);
//        deliveryToStudents.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new DeliveryQuestionsTask().execute(questions);
//                TextView deliveryToStudentsTxt = (TextView) findViewById(R.id.delivery_que_text);
//                deliveryToStudentsTxt.setText("Question delivered\nto students");
//                deliveryToStudentsTxt.setTextColor(Color.parseColor("#870274"));
//                deliveryToStudents.setBackground(null);
//
//            }});

    }

    private void setSizeOfPopUp(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.8),(int)(height*.6));
    }

    private void displayQuestions(){
        String passedArg = getIntent().getExtras().getString("questionData");
        questions = passedArg;
        QuestionData questionData = gson.fromJson(passedArg,QuestionData.class);
        setSizeOfPopUp();
        TextView question =(TextView)findViewById(R.id.que_pop_question_student);
        question.setText(" " + questionData.getQuestion());
        RadioButton option = null;
        List<String> randomOptions = questionData.getAllOptions();
        Collections.shuffle(randomOptions);
        for(int i = 1;i <= questionData.getAllOptions().size(); i++){
            switch(i){
                case 1:
                    option = (RadioButton)findViewById(R.id.que_pop_stud_opt1);
                    break;
                case 2:
                    option = (RadioButton)findViewById(R.id.que_pop_stud_opt2);
                    break;
                case 3:
                    option = (RadioButton)findViewById(R.id.que_pop_stud_opt3);
                    break;
                case 4:
                    option = (RadioButton)findViewById(R.id.que_pop_stud_opt4);
                    break;
                case 5:
                    option = (RadioButton)findViewById(R.id.que_pop_stud_opt5);
                    break;
                case 6:
                    option = (RadioButton)findViewById(R.id.que_pop_stud_opt6);
                    break;
            }
            option.setText(" " + option.getText() + randomOptions.get(i-1));
        }
    }


}
