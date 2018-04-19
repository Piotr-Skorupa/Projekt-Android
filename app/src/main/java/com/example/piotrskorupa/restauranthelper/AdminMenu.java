package com.example.piotrskorupa.restauranthelper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class AdminMenu extends AppCompatActivity {

    private String log;
    private String passw;
    private String res;
    private String fun;

    public String response;
    static ResultSet rs;
    static PreparedStatement st;
    static Connection con;

    private final String connStr = "jdbc:mysql://node54808-pskorupa.unicloud.pl:3306/Restaurants?zeroDateTimeBehavior=convertToNull";
    private final String user = "root";
    private final String pass = "alamakota";


    private ListView lista;
    private ArrayList<String> nazwy;
    private ArrayList<String> ceny;
    private ArrayList<String> dostepnosci;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        nazwy = new ArrayList<String>();
        ceny = new ArrayList<String>();
        dostepnosci = new ArrayList<String>();


        Intent intent = getIntent();
        log = intent.getStringExtra("login");
        passw = intent.getStringExtra("pass");
        res = intent.getStringExtra("res");
        fun = intent.getStringExtra("func");

        User client = new User(log, pass, res, fun);


        try {
            client.response = getDishesFromDB(client);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        lista = (ListView) findViewById(R.id.menu_list);
        CustomAdapter customer = new CustomAdapter();
        lista.setAdapter(customer);

    }


    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return nazwy.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.customlist1, null);

            TextView name = (TextView) convertView.findViewById(R.id.dish_name);
            TextView cena = (TextView) convertView.findViewById(R.id.dish_price);

            name.setText(nazwy.get(position));
            cena.setText(ceny.get(position));

            if (dostepnosci.get(position).equals("avaible")){
                name.setTextColor(Color.GREEN);
                cena.setTextColor(Color.GREEN);
            }
            else{
                name.setTextColor(Color.RED);
                cena.setTextColor(Color.RED);
            }

            return convertView;
        }
    }


    public String getDishesFromDB(User us) throws ExecutionException, InterruptedException {
        return new menuFromDatabase().execute(us.response).get();
    }


    // ****************************** POBIERANIE MENU **********************************************************************
    //---------------------------------------------------------------------------------------------------------------

    private class menuFromDatabase extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try
            {


                Class.forName("com.mysql.jdbc.Driver").newInstance();
                con= DriverManager.getConnection(connStr, user, pass);
                if (con == null){
                    response = "something go wrong";
                }
                else{

                    st=con.prepareStatement("select * from "+res+"menu");
                    rs=st.executeQuery();
                    if  (!rs.isBeforeFirst() ) {
                        response = "Empty!";
                    }else{
                        while(rs.next()){
                            nazwy.add(rs.getString("potrawa"));
                            ceny.add(rs.getString("cena"));
                            dostepnosci.add(rs.getString("dostepnosc"));
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
}
