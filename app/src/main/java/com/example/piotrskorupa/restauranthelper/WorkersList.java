package com.example.piotrskorupa.restauranthelper;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class WorkersList extends AppCompatActivity {

    private ListView lista;
    private ArrayList<String> nazwy;
    private ArrayList<String> funkcje;

    private String log;
    private String passw;
    private String res;
    private String fun;

    private String clicked_name;
    User client;

    public String response;
    static ResultSet rs;
    static PreparedStatement st;
    static Connection con;
    WorkersList.CustomAdapter customer;


    private String connStr = "jdbc:mysql://node54808-pskorupa.unicloud.pl:3306/"+res+"?zeroDateTimeBehavior=convertToNull";
    private final String user = "root";
    private final String pass = "alamakota";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workers_list);

        nazwy = new ArrayList<String>();
        funkcje = new ArrayList<String>();

        Intent intent = getIntent();
        log = intent.getStringExtra("login");
        passw = intent.getStringExtra("pass");
        res = intent.getStringExtra("res");
        fun = intent.getStringExtra("func");

        client = new User(log, pass, res, fun);

        //poberanie listy z bazy danych
        try {
            client.response = getWorkersFromDB(client);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        lista = (ListView) findViewById(R.id.worker_list);
        customer = new WorkersList.CustomAdapter();
        lista.setAdapter(customer);
        lista.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return nazwy.size();
        }

        @Override
        public Object getItem(int position) {

            return position;


        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.workerlist, null);

            TextView name = (TextView) convertView.findViewById(R.id.worker_name);
            TextView funkcja = (TextView) convertView.findViewById(R.id.worker_function);
            int nr = position + 1;
            name.setText(nr +". "+ nazwy.get(position));
            funkcja.setText(funkcje.get(position));

            Button button = (Button) convertView.findViewById(R.id.button_delete_worker);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    clicked_name = nazwy.get(position);
                    try {
                        client.response = deleteFromDB(client);
                        client.response = getWorkersFromDB(client);
                        customer.notifyDataSetChanged();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }});

            return convertView;
        }
    }


    public String getWorkersFromDB(User us) throws ExecutionException, InterruptedException {
        return new WorkersList.workersFromDatabase().execute(us.response).get();
    }

    public String deleteFromDB(User us) throws ExecutionException, InterruptedException {
        return new WorkersList.delWorkersFromDatabase().execute(us.response).get();
    }


    // ****************************** POBIERANIE PRACOWNIKOW **********************************************************************
    //---------------------------------------------------------------------------------------------------------------

    private class workersFromDatabase extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try
            {

                connStr = "jdbc:mysql://node54808-pskorupa.unicloud.pl:3306/"+res+"?zeroDateTimeBehavior=convertToNull";

                nazwy.clear();
                funkcje.clear();
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                con= DriverManager.getConnection(connStr, user, pass);
                if (con == null){
                    response = "something go wrong";
                }
                else{


                    st=con.prepareStatement("select * from users");
                    rs=st.executeQuery();
                    if  (!rs.isBeforeFirst() ) {
                        response = "Empty!";
                    }else{
                        while(rs.next()){
                            nazwy.add(rs.getString("login"));
                            funkcje.add(rs.getString("function"));

                        }
                        response = "OK";
                    }
                }
                con.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                response = "Database Connection Error";
            }

            return response;
        }
    }

    //////////////////////////////////// DELETE WORKER ////////////////////////////////////////
    private class delWorkersFromDatabase extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try
            {

                connStr = "jdbc:mysql://node54808-pskorupa.unicloud.pl:3306/Restaurants?zeroDateTimeBehavior=convertToNull";
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                con= DriverManager.getConnection(connStr, user, pass);
                if (con == null){
                    response = "something go wrong";
                }
                else{

                    st=con.prepareStatement("delete from users where restaurant='"+res+"' and login='"+clicked_name+"'");
                    st.executeUpdate();

                    connStr = "jdbc:mysql://node54808-pskorupa.unicloud.pl:3306/"+res+"?zeroDateTimeBehavior=convertToNull";
                    con = DriverManager.getConnection(connStr, user, pass);
                    if (con == null){
                        response = "something go wrong";
                        return response;
                    }else {

                        st=con.prepareStatement("delete from users where login='"+clicked_name+"'");
                        st.executeUpdate();

                    }

                    response = "OK";
                }
                con.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                response = "Database Connection Error";
            }

            return response;
        }
    }
}
