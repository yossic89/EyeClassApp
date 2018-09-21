package eyeclass.eyeclassapp.teacher;

import Infra.TableViewBase;

public class QuestionStatisticForTeacher extends TableViewBase {
    @Override
    protected String[] getTitles() {
        return new String[]{"ID", "Name", "Class", "Curriculum", "Lesson", "is Right", "Answer"};
    }

    @Override
    protected String getUrl() {
        return null;
    }

    @Override
    protected String getReqParam() {
        return null;
    }
}
