package eyeclass.eyeclassapp.lesson;

import android.widget.EditText;

import java.util.List;

import eyeclass.eyeclassapp.Questions.QuestionData;

public class LessonData {
    private byte[] lessonFile;
    private String mClass;
    private String mCurr;
    private EditText mTitle;
    private List<QuestionData> questions;

    public byte[] getLessonFile() {
        return lessonFile;
    }

    public void setLessonFile(byte[] lessonFile) {
        this.lessonFile = lessonFile;
    }

    public String getmClass() {
        return mClass;
    }

    public void setmClass(String mClass) {
        this.mClass = mClass;
    }

    public String getmCurr() {
        return mCurr;
    }

    public void setmCurr(String mCurr) {
        this.mCurr = mCurr;
    }

    public EditText getmTitle() {
        return mTitle;
    }

    public void setmTitle(EditText mTitle) {
        this.mTitle = mTitle;
    }

    public List<QuestionData> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionData> questions) {
        this.questions = questions;
    }
}
