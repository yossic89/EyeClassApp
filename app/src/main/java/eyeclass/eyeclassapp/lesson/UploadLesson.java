package eyeclass.eyeclassapp.lesson;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import eyeclass.eyeclassapp.Questions.QuestionData;
import eyeclass.eyeclassapp.R;

public class UploadLesson extends AppCompatActivity  {
    static final int READ_REQ = 24;
    private static LessonData mLesson;
    private List<String> currList;
    Spinner curriculums;

        ViewGroup cont;
        ListView contactLst;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_lesson);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            try {
                new curriculumData().execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            mLesson=new LessonData();
            curriculums = findViewById(R.id.spinner_cur_add_lesson);

            curriculums.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mLesson.setmCurr(parent.getItemAtPosition(position).toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            Spinner curSpinner = findViewById(R.id.spinner_cur_add_lesson);
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, currList);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            curSpinner.setAdapter(adapter1);

        }

    public void readFile(View view) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/pdf");
            startActivityForResult(intent, READ_REQ);
        }

    public void submit(View view) {
        mLesson.setmTitle(((EditText)findViewById(R.id.title_lesson)).getText().toString());
            if(mLesson.getLessonFile() == null)
                Toast.makeText(UploadLesson.this, "Your must choose a file", Toast.LENGTH_LONG).show();
            else if (mLesson.getmTitle() == null || mLesson.getmTitle().isEmpty())
                Toast.makeText(UploadLesson.this, "Please fill lesson Title", Toast.LENGTH_LONG).show();
            else{
                boolean isUpload = false;
                try {
                    isUpload = new submitLesson().execute().get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(isUpload)
                {
                    Toast.makeText(UploadLesson.this, "The lesson has been saved successfully", Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                    Toast.makeText(UploadLesson.this, "Failed to load lesson to server", Toast.LENGTH_LONG).show();


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
            private String mTopic;
            private String mTime;
            private String mQuestion;
            private String mCorrectAnswer;
            private String mWrongAnswer1;
            private String mWrongAnswer2;
            private String mWrongAnswer3;
            private List<QuestionData> questions;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_add_questions);

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                questions = new ArrayList<QuestionData>();

            }

            private void readFromScreen()
            {
                mTopic = ((EditText)findViewById(R.id.topic_quest)).getText().toString();
                mTime = ((EditText)findViewById(R.id.time_quest)).getText().toString();
                mQuestion = ((EditText)findViewById(R.id.question_quest)).getText().toString();
                mCorrectAnswer = ((EditText)findViewById(R.id.correct_ans_quest)).getText().toString();
                mWrongAnswer1 = ((EditText)findViewById(R.id.wrong_ans_1_quest)).getText().toString();
                mWrongAnswer2 = ((EditText)findViewById(R.id.wrong_ans_2_quest)).getText().toString();
                mWrongAnswer3 = ((EditText)findViewById(R.id.wrong_ans_3_quest)).getText().toString();
            }

            public void submitQuestion(View view) {

                mLesson.setQuestions(questions);
                Toast.makeText(AddQuestions.this, "The questions has been add successfully to the lesson", Toast.LENGTH_LONG).show();
                finish();
            }

            public void addQuestionToList(View view) {
                readFromScreen();
                QuestionData newQuestion = new QuestionData();
                newQuestion.setQuestion(mQuestion);
                newQuestion.setRightAns(mCorrectAnswer);
                List<String> allOptions = new ArrayList<String>();
                allOptions.add(mWrongAnswer1);
                allOptions.add(mWrongAnswer2);
                allOptions.add(mWrongAnswer3);
                newQuestion.setWrongOptions(allOptions);
                newQuestion.setTopic(mTopic);
                newQuestion.setTime(Integer.parseInt(mTime));
                questions.add(newQuestion);
                Toast.makeText(AddQuestions.this, "The question has been saved successfully", Toast.LENGTH_LONG).show();
                //delete screen content
                ((EditText)findViewById(R.id.topic_quest)).setText("");
                ((EditText)findViewById(R.id.time_quest)).setText("");
                ((EditText)findViewById(R.id.question_quest)).setText("");
                ((EditText)findViewById(R.id.correct_ans_quest)).setText("");
                ((EditText)findViewById(R.id.wrong_ans_1_quest)).setText("");
                ((EditText)findViewById(R.id.wrong_ans_2_quest)).setText("");
                ((EditText)findViewById(R.id.wrong_ans_3_quest)).setText("");

            }
        }


    class curriculumData extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            URL url = null;
            try {
                String data = "req=curriculum";
                url = new URL(Infra.Constants.Connections.TeacherServlet());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.connect();
                java.io.OutputStreamWriter wr = new java.io.OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    // Append server response in string
                    sb.append(line);
                }

                currList= new Gson().fromJson(sb.toString(),new TypeToken<List<String>>(){}.getType());

            }
            catch (Exception e){}
            return null;
        }
    }
    class submitLesson extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Void... voids) {
            URL url = null;
            try {
                String data = "req=upload_lesson";
                data+="&data=" + new Gson().toJson(mLesson);
                url = new URL(Infra.Constants.Connections.TeacherServlet());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);
                conn.setUseCaches(false);
                conn.connect();
                java.io.OutputStreamWriter wr = new java.io.OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    // Append server response in string
                    sb.append(line);
                }

                return Boolean.parseBoolean(sb.toString().trim());
            }
            catch (Exception e){}
            return false;
        }
    }
}
