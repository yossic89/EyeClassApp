package eyeclass.eyeclassapp.Questions;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;

import eyeclass.eyeclassapp.R;
import eyeclass.eyeclassapp.Student.StudentLesson;

public class QuestionPopUpStudent extends Activity {
    public int index;
    public String questions = "";
    private Gson gson = new Gson();
    private List<String> randomOptions;
    private QuestionData questionData;
    private boolean isAnswerPopUpDisplay = false;
    private int time=30;
    private int timeOfAnswerPopUp = -1;
    private final int ANSWER_POP_UP = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_pop_up_student);
        displayQuestions();
        displayTime();
        Button submitBtn = (Button) findViewById(R.id.que_pop_stud_submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAnswer();
            }});

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
        questionData = gson.fromJson(passedArg,QuestionData.class);
        setSizeOfPopUp();
        TextView question =(TextView)findViewById(R.id.que_pop_question_student);
        question.setText(" " + questionData.getQuestion());
        RadioButton option = null;
        randomOptions = questionData.getAllOptions();
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

    private void getAnswer(){
        RadioGroup radioButtonGroup = (RadioGroup)findViewById(R.id.que_pop_stud_radio_group);
        Button submitBtn = (Button) findViewById(R.id.que_pop_stud_submit);
        submitBtn.setClickable(false);
        submitBtn.setBackground(null);
        submitBtn.setText("");
        int radioButtonID = radioButtonGroup.getCheckedRadioButtonId();
        View radioButton = radioButtonGroup.findViewById(radioButtonID);
        int idx = radioButtonGroup.indexOfChild(radioButton);
        RadioButton r = (RadioButton)  radioButtonGroup.getChildAt(idx);
        String selectedtext = r.getText().toString().substring(1);
        if (questionData.getRightAns().equals(selectedtext)) {
            Intent intent = new Intent(QuestionPopUpStudent.this, GoodAnswer.class);
            startActivityForResult(intent,ANSWER_POP_UP);
            isAnswerPopUpDisplay = true;
            timeOfAnswerPopUp = 4;
        }
        else{
            Intent intent = new Intent(QuestionPopUpStudent.this, WrongAnswer.class);
            startActivityForResult(intent,ANSWER_POP_UP);
            isAnswerPopUpDisplay = true;
            timeOfAnswerPopUp = 4;
        }
    }

    private void displayTime(){
        TextView textTimer = (TextView)findViewById(R.id.que_pop_stud_timer);

        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                if (time < 5) textTimer.setTextColor(Color.parseColor("#ff0000"));
                textTimer.setText("0:"+checkDigit(time));
                time--;
                if (isAnswerPopUpDisplay){
                    timeOfAnswerPopUp--;
                    if (timeOfAnswerPopUp == 0){
                        finishActivity(ANSWER_POP_UP);
                    }

                }
            }

            public void onFinish() {
                if(isAnswerPopUpDisplay) finishActivity(ANSWER_POP_UP);
                textTimer.setText("DONE");
            }

        }.start();

    }
    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }
}
