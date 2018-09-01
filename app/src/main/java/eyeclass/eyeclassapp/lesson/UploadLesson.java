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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import eyeclass.eyeclassapp.R;

public class UploadLesson extends AppCompatActivity {
    static final int READ_REQ = 24;
    byte[] lessonFile;
    private EditText mClass;
    private EditText mCurr;
    private EditText mTitle;

        ViewGroup cont;
        ListView contactLst;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_lesson);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

            mClass = (EditText) findViewById(R.id.class_lesson);
            mCurr = (EditText) findViewById(R.id.curriculum_lesson);
            mTitle = (EditText) findViewById(R.id.title_lesson);

        }

        public void readFile(View view) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/pdf");
            startActivityForResult(intent, READ_REQ);
        }

    public void submit(View view) {
        //TODO
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
                lessonFile=byteBuffer.toByteArray();
                Toast.makeText(UploadLesson.this, "Your file has been saved successfully", Toast.LENGTH_LONG).show();

                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
