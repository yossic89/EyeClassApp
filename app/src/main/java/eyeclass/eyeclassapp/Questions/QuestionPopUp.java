package eyeclass.eyeclassapp.Questions;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.concurrent.ExecutionException;

import eyeclass.eyeclassapp.ConnectionTask;
import eyeclass.eyeclassapp.R;

public class QuestionPopUp extends Activity {
    public int index;
    public String questions = "";
    private Gson gson = new Gson();
    private int time;
    private String class_id;
    private  QuestionData questionData;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        class_id = getIntent().getExtras().getString("class_id");;
        setContentView(R.layout.activity_question_pop_up);
        displayQuestions();
        Button deliveryToStudents = (Button) findViewById(R.id.deliver_to_stud_btn);
        deliveryToStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeliveryQuestionsTask().execute(questions, class_id);
                TextView deliveryToStudentsTxt = (TextView) findViewById(R.id.delivery_que_text);
                deliveryToStudentsTxt.setText("Question delivered\nto students");
                deliveryToStudentsTxt.setTextColor(Color.parseColor("#870274"));
                deliveryToStudents.setBackground(null);
                displayTime();
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
        TextView question =(TextView)findViewById(R.id.que_pop_question);
        question.setText(" " + questionData.getQuestion());
        TextView correctAnswer =(TextView)findViewById(R.id.que_pop_correct_answer);
        correctAnswer.setText(" " + questionData.getRightAns());
        //TextView timeForQuestion =(TextView)findViewById(R.id.que_pop_time);
       // timeForQuestion.setText(String.valueOf(questionData.getTime()));
        TextView option = null;
        for(int i = 1;i < questionData.getAllOptions().size(); i++){
            switch(i){
                case 1:
                    option = (TextView)findViewById(R.id.que_pop_opt1);
                    option.setText("1. ");
                    break;
                case 2:
                    option = (TextView)findViewById(R.id.que_pop_opt2);
                    option.setText("2. ");
                    break;
                case 3:
                    option = (TextView)findViewById(R.id.que_pop_opt3);
                    option.setText("3. ");
                    break;
                case 4:
                    option = (TextView)findViewById(R.id.que_pop_opt4);
                    option.setText("4. ");
                    break;
                case 5:
                    option = (TextView)findViewById(R.id.que_pop_opt5);
                    option.setText("5. ");
                    break;
            }
            option.setText(option.getText() + questionData.getAllOptions().get(i));
        }
    }

    private void displayTime(){
        TextView t = (TextView)findViewById(R.id.que_pop_teacher_t);
        t.setText("Time:");
        TextView textTimer = (TextView)findViewById(R.id.que_pop_teacher_timer);
        time = questionData.getTime();

        new CountDownTimer((time + 1)*1000, 1000) {

            public void onTick(long millisntilFinished) {
                String min = checkDigit(time / 60);
                String sec = checkDigit(time % 60);
                textTimer.setText(min + ":" + sec);
                time--;
                if (time <= 0)
                    textTimer.setText("DONE");
            }

            public void onFinish() {
                new DeliveryQuestionsTask().execute("clear", class_id);
                try{Thread.sleep(500);}
                catch (Exception e){}
                finish();
            }

        }.start();

    }
    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

}
