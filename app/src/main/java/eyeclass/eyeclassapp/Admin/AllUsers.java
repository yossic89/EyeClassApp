package eyeclass.eyeclassapp.Admin;

import Infra.TableViewBase;

public class AllUsers extends TableViewBase {
    @Override
    protected String[] getTitles() {
        return new String[]{"ID", "Name", "Type", "Curriculum", "Class"};
    }

    @Override
    protected String getUrl() {
        return Infra.Constants.Connections.AdminServlet();
    }

    @Override
    protected String getReqParam() {
        return "users_list";
    }
}
