package eyeclass.eyeclassapp.lesson;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import eyeclass.eyeclassapp.Questions.QuestionData;
import eyeclass.eyeclassapp.R;

public class UploadLesson extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    static final int READ_REQ = 24;
    private static LessonData mLesson;

        ViewGroup cont;
        ListView contactLst;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_lesson);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            mLesson=new LessonData();
            //TODO get from DB
            ArrayList<String> cur;
            cur=new ArrayList<String>();
            cur.add("Bible");
            cur.add("Math");
            cur.add("Science");
            cur.add("Logic");
            Spinner curSpinner = findViewById(R.id.spinner_cur_add_lesson);
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, cur);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            curSpinner.setAdapter(adapter1);
            curSpinner.setOnItemSelectedListener(this);
            //TODO get from DB
            ArrayList<String> classLesson;
            classLesson=new ArrayList<String>();
            classLesson.add("1");
            classLesson.add("2");
            classLesson.add("3");
            classLesson.add("4");
            Spinner classSpinner = findViewById(R.id.spinner_class_add_lesson);
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, classLesson);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            classSpinner.setAdapter(adapter2);
            classSpinner.setOnItemSelectedListener(this);

            mLesson.setmTitle(findViewById(R.id.title_lesson));
        }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        //TODO get values from spinners
        //get curr
        //mLesson.setmCurr();
        //get class
        //mLesson.setmClass();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void readFile(View view) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/pdf");
            startActivityForResult(intent, READ_REQ);
        }

    public void submit(View view) {
            if(mLesson.getLessonFile() == null)
                Toast.makeText(UploadLesson.this, "Your must choose a file", Toast.LENGTH_LONG).show();
            else{
                //TODO send to server
            }

    }

    public void addQuestions(View view) {
        Intent intent = new Intent(this, AddQuestions.class);
        startActivity(intent);
    }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onActivityResult(int requestCode, int resultCode,
                                     Intent resultData) {

            if (resultCode == Activity.RESULT_OK) {

                Uri uri = null;
                if (resultData != null) {
                    uri = resultData.getData();
                }
                if(requestCode == READ_REQ){
                    readTextFile(uri);
                }
            }
        }

        private void readTextFile(Uri uri) {
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(uri);

                // this dynamically extends to take the bytes you read
                ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

                // this is storage overwritten on each iteration with bytes
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];

                // we need to know how may bytes were read to write them to the byteBuffer
                int len = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    byteBuffer.write(buffer, 0, len);
                }

                //send to server as byte[]
                mLesson.setLessonFile(byteBuffer.toByteArray());
                Toast.makeText(UploadLesson.this, "Your file has been saved successfully", Toast.LENGTH_LONG).show();

                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static class AddQuestions extends AppCompatActivity {
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
                questions = new ArrayList<QuestionData>();
                mTopic = (EditText) findViewById(R.id.topic_quest);
                mTime = (EditText) findViewById(R.id.time_quest);
                mQuestion = (EditText) findViewById(R.id.question_quest);
                mCorrectAnswer = (EditText) findViewById(R.id.correct_ans_quest);
                mWrongAnswer1 = (EditText) findViewById(R.id.wrong_ans_1_quest);
                mWrongAnswer2 = (EditText) findViewById(R.id.wrong_ans_2_quest);
                mWrongAnswer3 = (EditText) findViewById(R.id.wrong_ans_3_quest);
            }

            public void submitQuestion(View view) {

                mLesson.setQuestions(questions);
                Toast.makeText(AddQuestions.this, "The questions has been add successfully to the lesson", Toast.LENGTH_LONG).show();
                finish();
            }

            public void addQuestionToList(View view) {

                QuestionData newQuestion = new QuestionData();
                newQuestion.setQuestion(mQuestion.toString());
                newQuestion.setRightAns(mCorrectAnswer.toString());
                List<String> allOptions = new ArrayList<String>();
                allOptions.add(mWrongAnswer1.toString());
                allOptions.add(mWrongAnswer2.toString());
                allOptions.add(mWrongAnswer3.toString());
                newQuestion.setAllOptions(allOptions);
                newQuestion.setTopic(mTopic.toString());
                newQuestion.setTime(mTime.toString());
                questions.add(newQuestion);
                Toast.makeText(AddQuestions.this, "The question has been saved successfully", Toast.LENGTH_LONG).show();
                //delete screen content
                mTopic.setText("");
                mTime.setText("");
                mQuestion.setText("");
                mCorrectAnswer.setText("");
                mWrongAnswer1.setText("");
                mWrongAnswer2.setText("");
                mWrongAnswer3.setText("");

            }
        }
}
