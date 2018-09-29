package eyeclass.eyeclassapp.teacher;

import Infra.TableViewBase;

public class QuestionStatisticForTeacher extends TableViewBase {
    @Override
    protected String[] getTitles() {
        return new String[]{"ID", "Name", "Class", "Curriculum", "Lesson","Question","Right Answer", "Student answer", "is Right"};
    }

    @Override
    protected String getUrl() {
        return Infra.Constants.Connections.TeacherServlet();
    }

    @Override
    protected String getReqParam() {
        return "question_report";
    }
}
