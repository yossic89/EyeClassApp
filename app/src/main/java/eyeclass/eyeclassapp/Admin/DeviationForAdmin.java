package eyeclass.eyeclassapp.Admin;

import Infra.Constants;
import Infra.TableViewBase;

public class DeviationForAdmin extends TableViewBase {
    @Override
    protected String[] getTitles() {
        return new String[]{"ID", "Name", "Class", "Date", "Curriculum","Teacher", "Type", "Duration"};
    }

    @Override
    protected String getUrl() {
        return Constants.Connections.AdminServlet();
    }

    @Override
    protected String getReqParam() {
        return "admin_distractions";
    }
}
