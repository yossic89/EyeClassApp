package eyeclass.eyeclassapp.lesson;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.EditText;

import eyeclass.eyeclassapp.R;

public class AddQuestions extends AppCompatActivity {
    private EditText mTitle;
    private EditText mTopic;
    private EditText mTime;
    private EditText mQuestion;
    private EditText mCorrectAnswer;
    private EditText mWrongAnswer1;
    private EditText mWrongAnswer2;
    private EditText mWrongAnswer3;

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
        //TODO
    }
}
