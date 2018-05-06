package com.example.piotrskorupa.restauranthelper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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


    // insert strings
    private String clickedname;
    private String name;
    private String price;
    private String dostep;


    private final String defaultName = "Dish name";
    private final String defaultPrice = "0,00zl";

    public String response;
    static ResultSet rs;
    static PreparedStatement st;
    static Connection con;

    private final String connStr = "jdbc:mysql://node54808-pskorupa.unicloud.pl:3306/Restaurants?zeroDateTimeBehavior=convertToNull";
    private final String user = "root";
    private final String pass = "alamakota";


    private Button addButton;
    private Button deleteButton;
    private Button updateButton;
    private EditText namePlaceHolder;
    private EditText pricePlaceHolder;
    private CheckBox dostepCheck;

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

        //poberanie listy z bazy danych
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
        lista.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        namePlaceHolder = (EditText) findViewById(R.id.edit_name);
        pricePlaceHolder = (EditText) findViewById(R.id.edit_price);
        dostepCheck = (CheckBox) findViewById(R.id.dostepnosc);
        dostepCheck.setChecked(true);

        addButton = (Button) findViewById(R.id.menu_insert_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!(namePlaceHolder.getText().toString().equals(defaultName)) && !(pricePlaceHolder.getText().toString().equals(defaultPrice))
                        && !(namePlaceHolder.getText().toString().equals("")) && !(pricePlaceHolder.getText().toString().equals(""))

                        ){

                    try {

                        name = namePlaceHolder.getText().toString();
                        price = pricePlaceHolder.getText().toString();
                        if (dostepCheck.isChecked()){
                            dostep = "avaible";
                        }else{
                            dostep = "unavaible";
                        }


                        client.response = insertDishtoDB(client);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //poberanie listy z bazy danych
                    try {
                        client.response = getDishesFromDB(client);
                        //lista.removeAllViews();
                        customer.notifyDataSetChanged();
                        Toast.makeText(AdminMenu.this, "Dish has been added to menu!", Toast.LENGTH_SHORT).show();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }else{
                    Toast.makeText(AdminMenu.this, "You must enter name and price!", Toast.LENGTH_SHORT).show();
                }
                clickedname = "";
                namePlaceHolder.setText(defaultName);
                pricePlaceHolder.setText(defaultPrice);

            }});

        deleteButton = (Button) findViewById(R.id.menu_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    name = namePlaceHolder.getText().toString();
                    price = pricePlaceHolder.getText().toString();
                    client.response = deleteDishFromDB(client);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //poberanie listy z bazy danych
                try {
                    client.response = getDishesFromDB(client);
                    //lista.removeAllViews();
                    customer.notifyDataSetChanged();
                    Toast.makeText(AdminMenu.this, "Dish has been deleted!", Toast.LENGTH_SHORT).show();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                clickedname = "";
                namePlaceHolder.setText(defaultName);
                pricePlaceHolder.setText(defaultPrice);
            }});

        updateButton = (Button) findViewById(R.id.menu_update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!(namePlaceHolder.getText().toString().equals(defaultName)) && !(pricePlaceHolder.getText().toString().equals(defaultPrice))){

                    try {

                        name = namePlaceHolder.getText().toString();
                        price = pricePlaceHolder.getText().toString();
                        if (dostepCheck.isChecked()){
                            dostep = "avaible";
                        }else{
                            dostep = "unavaible";
                        }


                        client.response = updateDishFromDB(client);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //poberanie listy z bazy danych
                    try {
                        client.response = getDishesFromDB(client);
                        //lista.removeAllViews();
                        customer.notifyDataSetChanged();
                        Toast.makeText(AdminMenu.this, "Dish has been updated!", Toast.LENGTH_SHORT).show();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    clickedname = "";
                    namePlaceHolder.setText(defaultName);
                    pricePlaceHolder.setText(defaultPrice);

                }else{
                    Toast.makeText(AdminMenu.this, "You must click the dish first!", Toast.LENGTH_SHORT).show();
                }


            }});

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //int text = parent.getItemAtPosition(position);
                clickedname = nazwy.get(position);
                //Toast.makeText(AdminMenu.this, text, Toast.LENGTH_SHORT).show();
                fillTextBoxes(position);
            }
        });
        lista.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }


    class CustomAdapter extends BaseAdapter{

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
            convertView = getLayoutInflater().inflate(R.layout.customlist1, null);

            TextView name = (TextView) convertView.findViewById(R.id.dish_name);
            TextView cena = (TextView) convertView.findViewById(R.id.dish_price);
            int nr = position + 1;
            name.setText(nr +". "+ nazwy.get(position));
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

    private void fillTextBoxes(int pos)
    {
        namePlaceHolder.setText(nazwy.get(pos));
        pricePlaceHolder.setText(ceny.get(pos));
        if (dostepnosci.get(pos).equals("avaible")){
            dostepCheck.setChecked(true);
        }
        else{
            dostepCheck.setChecked(false);
        }

    }


    public String getDishesFromDB(User us) throws ExecutionException, InterruptedException {
        return new menuFromDatabase().execute(us.response).get();
    }

    public String insertDishtoDB(User us) throws ExecutionException, InterruptedException{

        return new insertToDatabase().execute(us.response).get();
    }

    public String deleteDishFromDB(User us) throws ExecutionException, InterruptedException{

        return new deleteFromDatabase().execute(us.response).get();
    }

    public String updateDishFromDB(User us) throws ExecutionException, InterruptedException{

        return new updateToDatabase().execute(us.response).get();
    }
    // ****************************** POBIERANIE MENU **********************************************************************
    //---------------------------------------------------------------------------------------------------------------

    private class menuFromDatabase extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try
            {
                nazwy.clear();
                ceny.clear();
                dostepnosci.clear();
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



    // ****************************** DODAWANIE POTRAWY DO MENU **********************************************************************
    //---------------------------------------------------------------------------------------------------------------



    private class insertToDatabase extends AsyncTask<String, String, String> {

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

                    st=con.prepareStatement("INSERT INTO "+res+"menu (potrawa, cena, dostepnosc) VALUES ('"+name+"','"+price+"','"+dostep+"')");
                    st.executeUpdate();


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

    // ****************************** USUWANIE POTRAWY DO MENU **********************************************************************
    //---------------------------------------------------------------------------------------------------------------



    private class deleteFromDatabase extends AsyncTask<String, String, String> {

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

                    st=con.prepareStatement("DELETE FROM "+res+"menu WHERE potrawa='"+name+"' AND cena='"+price+"'");
                    st.executeUpdate();


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

    //********************************* UPDATE DISH ****************************************
    //---------------------------------------------------------------------------------------------------------------



    private class updateToDatabase extends AsyncTask<String, String, String> {

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

                    st=con.prepareStatement("UPDATE "+res+"menu SET potrawa='"+name+"', cena='"+price+"', dostepnosc='"+dostep+"' WHERE potrawa='"+clickedname+"'");
                    st.executeUpdate();


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
