package eyeclass.eyeclassapp.teacher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.levitnudi.legacytableview.LegacyTableView;

import Infra.Constants;
import Infra.TableViewBase;

public class DeviationForTeacher extends TableViewBase {

    @Override
    protected String[] getTitles() {
        return new String[]{"ID", "Name", "Class", "Date", "Curriculum", "Type", "Duration"};
    }

    @Override
    protected String getUrl() {
        return Infra.Constants.Connections.TeacherServlet();
    }

    @Override
    protected String getReqParam() {
        return "lesson_distractions";
    }
}
