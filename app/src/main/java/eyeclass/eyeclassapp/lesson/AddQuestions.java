package eyeclass.eyeclassapp.lesson;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import eyeclass.eyeclassapp.Questions.QuestionData;
import eyeclass.eyeclassapp.R;

public class AddQuestions extends AppCompatActivity {
    private EditText mTopic;
    private EditText mTime;
    private EditText mQuestion;
    private EditText mCorrectAnswer;
    private EditText mWrongAnswer1;
    private EditText mWrongAnswer2;
    private EditText mWrongAnswer3;
    private List<QuestionData> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_questions);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mTopic = (EditText) findViewById(R.id.topic_quest);
        mTime = (EditText) findViewById(R.id.time_quest);
        mQuestion = (EditText) findViewById(R.id.question_quest);
        mCorrectAnswer = (EditText) findViewById(R.id.correct_ans_quest);
        mWrongAnswer1 = (EditText) findViewById(R.id.wrong_ans_1_quest);
        mWrongAnswer2 = (EditText) findViewById(R.id.wrong_ans_2_quest);
        mWrongAnswer3 = (EditText) findViewById(R.id.wrong_ans_3_quest);
    }

    public void submitQuestion(){
        //TODO
    }

    public void addQuestionToList(){

        QuestionData newQuestion=new QuestionData();
        //TODO
        newQuestion.setId("?");
        newQuestion.setQuestion(mQuestion.toString());
        newQuestion.setRightAns(mCorrectAnswer.toString());
        List<String> allOptions= new ArrayList<String>();;
        allOptions.add(mWrongAnswer1.toString());
        allOptions.add(mWrongAnswer2.toString());
        allOptions.add(mWrongAnswer3.toString());
        newQuestion.setAllOptions(allOptions);
        newQuestion.setTopic(mTopic.toString());
        //TODO
        //time?
        questions.add(newQuestion);
        Toast.makeText(AddQuestions.this, "The question has been saved successfully", Toast.LENGTH_LONG).show();


    }
}
