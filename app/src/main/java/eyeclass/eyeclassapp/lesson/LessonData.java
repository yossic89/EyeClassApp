package eyeclass.eyeclassapp.lesson;

import java.util.List;

import eyeclass.eyeclassapp.Questions.QuestionData;

public class LessonData {
    private byte[] lessonFile;
    private String mCurr;
    private String mTitle;
    private List<QuestionData> questions;

    public byte[] getLessonFile() {
        return lessonFile;
    }

    public void setLessonFile(byte[] lessonFile) {
        this.lessonFile = lessonFile;
    }

    public String getmCurr() {
        return mCurr;
    }

    public void setmCurr(String mCurr) {
        this.mCurr = mCurr;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public List<QuestionData> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionData> questions) {
        this.questions = questions;
    }
}
