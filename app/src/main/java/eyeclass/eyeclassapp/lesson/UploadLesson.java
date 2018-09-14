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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import eyeclass.eyeclassapp.Questions.QuestionData;
import eyeclass.eyeclassapp.R;

public class UploadLesson extends AppCompatActivity  {
    static final int READ_REQ = 24;
    private static LessonData mLesson;
    private HashMap<String, String> class_to_id = new HashMap<>();
    private List<String> currList;
    Spinner curriculums;
    Spinner classes;

        ViewGroup cont;
        ListView contactLst;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_lesson);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            try {
                new classesData().execute().get();
                new curriculumData().execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            mLesson=new LessonData();
            curriculums = findViewById(R.id.spinner_cur_add_lesson);
            classes = findViewById(R.id.spinner_class_add_lesson);
            classes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String classStr = parent.getItemAtPosition(position).toString();
                    mLesson.setmClass(class_to_id.get(classStr));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
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

            List<String> data =  new ArrayList<>();
            for (Object obj : class_to_id.keySet())
                data.add(obj.toString());
            Spinner classSpinner = findViewById(R.id.spinner_class_add_lesson);
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, data);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            classSpinner.setAdapter(adapter2);

            mLesson.setmTitle(findViewById(R.id.title_lesson).toString());
        }

    public void readFile(View view) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/pdf");
            startActivityForResult(intent, READ_REQ);
        }

    public void submit(View view) {
        System.out.println("YAMIT 111111");
            //if(mLesson.getLessonFile() == null)
             //   Toast.makeText(UploadLesson.this, "Your must choose a file", Toast.LENGTH_LONG).show();
            //else{
                try {
                    System.out.println("YAMIT 22222");

                    new submitLesson().execute().get();
                    System.out.println("YAMIT 33333");

                } catch (Exception e) {
                    System.out.println("YAMIT 44444");

                    e.printStackTrace();
                }
                //TODO wait for server response
                //loader
                Toast.makeText(UploadLesson.this, "The lesson has been saved successfully", Toast.LENGTH_LONG).show();
                finish();
           // }

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
                mTopic = findViewById(R.id.topic_quest).toString();
                mTime = findViewById(R.id.time_quest).toString();
                mQuestion = findViewById(R.id.question_quest).toString();
                mCorrectAnswer = findViewById(R.id.correct_ans_quest).toString();
                mWrongAnswer1 = findViewById(R.id.wrong_ans_1_quest).toString();
                mWrongAnswer2 = findViewById(R.id.wrong_ans_2_quest).toString();
                mWrongAnswer3 = findViewById(R.id.wrong_ans_3_quest).toString();
            }

            public void submitQuestion(View view) {

                mLesson.setQuestions(questions);
                Toast.makeText(AddQuestions.this, "The questions has been add successfully to the lesson", Toast.LENGTH_LONG).show();
                finish();
            }

            public void addQuestionToList(View view) {

                QuestionData newQuestion = new QuestionData();
                newQuestion.setQuestion(mQuestion);
                newQuestion.setRightAns(mCorrectAnswer);
                List<String> allOptions = new ArrayList<String>();
                allOptions.add(mWrongAnswer1);
                allOptions.add(mWrongAnswer2);
                allOptions.add(mWrongAnswer3);
                newQuestion.setAllOptions(allOptions);
                newQuestion.setTopic(mTopic);
                newQuestion.setTime(mTime);
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
    class classesData extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            URL url = null;
            try {
                String data = "req=classes";
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

                class_to_id = new Gson().fromJson(sb.toString(),new TypeToken<HashMap<String, String>>(){}.getType());

            }
            catch (Exception e){}
            return null;
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
    class submitLesson extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            URL url = null;
            try {
                String data = "req=upload_lesson";
                data+="&data=" + new Gson().toJson(mLesson);
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
}
