package Infra;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.SearchView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.levitnudi.legacytableview.LegacyTableView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eyeclass.eyeclassapp.R;

public abstract class TableViewBase extends AppCompatActivity {

    LegacyTableView legacyTableView;
    String[] titles;
    List<List<String>> cells_data = null;

    protected abstract String[] getTitles();
    protected abstract String getUrl();
    protected abstract String getReqParam();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_view);
        legacyTableView = (LegacyTableView)findViewById(R.id.legacy_table_view);
        init();
    }

    private void init()
    {
        Button button = (Button)findViewById(R.id.filter_button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //get filtered text
                SearchView sv = (SearchView) findViewById(R.id.table_filter);
                String query = sv.getQuery().toString();
                //close keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                //Unknown bug - need to press twice
                if (query == null || query.trim().isEmpty())
                    showAll();
                else
                    filter(query);

                if (query == null || query.trim().isEmpty())
                    showAll();
                else
                    filter(query);
            }
        });
        titles = getTitles();
        String url = getUrl();
        String reqParam = getReqParam();
        try{new getTableDataServer().execute(url, reqParam).get();}
        catch(Exception e){}
        //validate there is data
        if (cells_data == null)
            cells_data = new ArrayList<>();
        showAll();
    }

    private void showAll()
    {
        List<String> data = new ArrayList<>();

        for(List<String> l : cells_data)
        {
            for(String s : l)
                data.add(s);
        }
        setTableData(data);
    }

    public void filter(String filterd_Text)
    {
        //build from new text filtered
        List<String> data = new ArrayList<>();
        for(List<String> l : cells_data)
        {
            for(String s : l)
            {
                if (s.contains(filterd_Text))
                {
                    data.addAll(l);
                    break;
                }
            }
        }
        setTableData(data);
    }

    private void setTableData(List<String> data){
        legacyTableView.resetVariables();
        legacyTableView.setTitle(titles);
        legacyTableView.setContent(data.toArray(new String[0]));
        legacyTableView.setTablePadding(7);
        //to enable users to zoom in and out:
        legacyTableView.setZoomEnabled(true);
        legacyTableView.setShowZoomControls(true);
        legacyTableView.build();
    }

    class getTableDataServer extends AsyncTask<String, Void, Void>
    {
        @Override
        protected Void doInBackground(String... strings) {
            URL url = null;
            try {
                String servlet_url = strings[0];
                String reqParam = strings[1];
                String data = "req=" + reqParam;
                url = new URL(servlet_url);
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

                cells_data = new Gson().fromJson(sb.toString(),new TypeToken<List<List<String>>>(){}.getType());

            }
            catch (Exception e){}
            return null;
        }
    }
}
