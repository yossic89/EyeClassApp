package eyeclass.eyeclassapp.Questions;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;

import eyeclass.eyeclassapp.R;

public class QuestionPopUp extends Activity {
    public int index;
    public JSONArray questions = new JSONArray();
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_pop_up);
        String passedArg = getIntent().getExtras().getString("questionData");
        QuestionData questionData = gson.fromJson(passedArg,QuestionData.class);
        setSizeOfPopUp();
        TextView question =(TextView)findViewById(R.id.que_pop_question);
        question.setText(" " + questionData.getQuestion());
        TextView correctAnswer =(TextView)findViewById(R.id.que_pop_correct_answer);
        correctAnswer.setText(" " + questionData.getRightAns());
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

    private void setSizeOfPopUp(){

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.8),(int)(height*.6));
    }



}
