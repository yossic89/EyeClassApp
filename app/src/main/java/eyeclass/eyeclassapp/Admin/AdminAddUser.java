package eyeclass.eyeclassapp.Admin;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidbuts.multispinnerfilter.MultiSpinner;
import com.androidbuts.multispinnerfilter.MultiSpinnerListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import eyeclass.eyeclassapp.R;

public class AdminAddUser extends AppCompatActivity {

    List<View> students_view;
    List<View> teacher_view;
    HashMap<String, String> class_to_id = new HashMap<>();
    LinkedHashMap<String, Boolean> cur_list = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_user);

        Spinner spinner = findViewById(R.id.view_user_type_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chooseUserType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.view_user_type_option_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        initClasses();
        initCur();

        students_view = new ArrayList<>();
        students_view.add(findViewById(R.id.view_classes_spinner));
        students_view.add(findViewById(R.id.choose_class_text));

        teacher_view = new ArrayList<>();
        teacher_view.add(findViewById(R.id.view_cur_spinner));
        teacher_view.add(findViewById(R.id.choose_cur_text));
    }

    private void initCur()
    {
        try{new curriculum().execute().get();}
        catch (Exception e){}
        MultiSpinner cur = (MultiSpinner)findViewById(R.id.view_cur_spinner);
        cur.setItems(cur_list, new MultiSpinnerListener() {

            @Override
            public void onItemsSelected(boolean[] selected) {

                // your operation with code...
                for(int i=0; i<selected.length; i++) {
                    String key = (String)cur_list.keySet().toArray()[i];
                    cur_list.put(key, selected[i]);
                }
            }
        });
    }

    private void initClasses()
    {
        try{new classes().execute().get();}
        catch (Exception e){}
        Spinner classes_for_student = findViewById(R.id.view_classes_spinner);
        List classes_view = new ArrayList<>(class_to_id.keySet());
        Collections.sort(classes_view);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, classes_view);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classes_for_student.setAdapter(adapter2);
    }

    public void chooseUserType(int position){
            switch(position){
            case 0://Student
                for(View adp : students_view)
                    adp.setVisibility(View.VISIBLE);
                for(View adp : teacher_view)
                    adp.setVisibility(View.GONE);
                break;
            case 1://Teacher
                for(View adp : students_view)
                    adp.setVisibility(View.GONE);
                for(View adp : teacher_view)
                    adp.setVisibility(View.VISIBLE);
                break;
                case 2://Admin
                    for(View adp : students_view)
                        adp.setVisibility(View.GONE);
                    for(View adp : teacher_view)
                        adp.setVisibility(View.GONE);
                    break;

        }
    }

    public void addUser(View view)
    {
        //validate common data
        boolean isValid = true;
        String extras = "";
        int user_pos = ((Spinner)findViewById(R.id.view_user_type_spinner)).getSelectedItemPosition();
        //id need to be bigger than 8 digit
        String id = ((EditText)findViewById(R.id.user_id)).getText().toString();
        if (id.length() < 8)
        {
            isValid = false;
            ((EditText) findViewById(R.id.user_id)).setError("ID must be at least 8 digits");
        }

        //name must be not empty
        String name = ((EditText)findViewById(R.id.user_name)).getText().toString();
        if (name == null || name.isEmpty())
        {
            isValid = false;
            ((EditText) findViewById(R.id.user_name)).setError("Please fill user full name");
        }

        //password at least 5 chars
        String pass = ((EditText)findViewById(R.id.user_password)).getText().toString();
        if (pass.length() < 5)
        {
            isValid = false;
            ((EditText) findViewById(R.id.user_password)).setError("Your password should contain 5 characters or more");

        }
        //password and conf pass same
        String conf_pass = ((EditText)findViewById(R.id.user_confirm_pass)).getText().toString();
        if (!pass.equals(conf_pass))
        {
            isValid = false;
            ((EditText) findViewById(R.id.user_confirm_pass)).setError("Confirm password is incorrect");
        }

        //validate if teacher select there is curriculums
        if (user_pos == 1)
        {
            List<String> selected_cur = new ArrayList<>();
            for (String cur : cur_list.keySet())
            {
                if (cur_list.get(cur))
                    selected_cur.add(cur);
            }
            if (selected_cur.size() == 0)
            {
                isValid = false;
                Toast.makeText(this, "Select curriculum for teacher", Toast.LENGTH_LONG).show();
            }

            extras = new Gson().toJson(selected_cur);
        }

        //take user class
        if (user_pos == 0)
        {
            String class_view_name = ((Spinner)findViewById(R.id.view_classes_spinner)).getSelectedItem().toString();
            extras = class_to_id.get(class_view_name);
        }

        if (isValid)
        {
            String server_comment = "";
            String reqParam = "";
            try
            {
                switch (user_pos)
                {
                    case 0://student
                    {
                        reqParam = "add_student";
                        break;
                    }
                    case 1://teacher
                    {
                        reqParam = "add_teacher";
                        break;
                    }
                    case 2://admin
                    {
                        reqParam = "add_admin";
                        break;
                    }
                }

                server_comment = new sendUserToServer(reqParam, id, name, pass, extras).execute().get();

                if (server_comment.trim().isEmpty())
                {
                    Toast.makeText(this, "User "+ id + " Added to school", Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                {
                    Toast.makeText(this, "Failed to add user: " + server_comment, Toast.LENGTH_LONG).show();

                }
            }
            catch (Exception e){}
        }
    }

    public class sendUserToServer extends AsyncTask<String, Void, String>
    {
        private String id;
        private String name;
        private String password;
        private String reqParam;
        private String extras;

        public sendUserToServer(String reqParam, String id, String name, String password, String extras) {
            this.id = id;
            this.name = name;
            this.password = password;
            this.reqParam = reqParam;
            this.extras = extras;
        }


        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            try {
                String data = "req=" + reqParam;
                data += String.format("&id=%s&name=%s&password=%s", id, name, password);
                if (extras != null)
                    data+= "&extra=" + extras;
                url = new URL(Infra.Constants.Connections.AdminServlet());
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
               return sb.toString().trim();


            }
            catch (Exception e){
                e.printStackTrace();
            }
            return "";
        }
    }

    public class classes extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            URL url = null;
            try {
                String data = "req=classes";
                url = new URL(Infra.Constants.Connections.AdminServlet());
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

                class_to_id = new Gson().fromJson(sb.toString(),new TypeToken<HashMap<String, String>>(){}.getType());

            }
            catch (Exception e){}
            return null;
        }
    }

    public class curriculum extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            URL url = null;
            try {
                String data = "req=cur_list";
                url = new URL(Infra.Constants.Connections.AdminServlet());
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

                List<String> cur = new Gson().fromJson(sb.toString(),new TypeToken<List<String>>(){}.getType());
                for (String c : cur)
                    cur_list.put(c, false);

            }
            catch (Exception e){}
            return null;
        }
    }
}
