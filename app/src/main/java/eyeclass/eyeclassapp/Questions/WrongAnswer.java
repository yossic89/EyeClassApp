package eyeclass.eyeclassapp.Questions;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import eyeclass.eyeclassapp.R;

public class WrongAnswer extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrong_answer_stud);
        setSizeOfPopUp();
    }

    private void setSizeOfPopUp(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.6),(int)(height*.3));
    }
}
