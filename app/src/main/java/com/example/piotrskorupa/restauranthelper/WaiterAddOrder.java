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
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class WaiterAddOrder extends AppCompatActivity {

    private String log;
    private String passw;
    private String res;
    private String fun;


    // insert strings
    private String nrStolu;
    private String uwagi;
    private int WaiterID;

    public String response;
    static ResultSet rs;
    static PreparedStatement st;
    static Connection con;

    private String connStr;
    private final String user = "root";
    private final String pass = "alamakota";

    private EditText uwagiPlaceHolder;
    private EditText nrstoluPlaceHolder;

    private ListView lista;
    private ArrayList<String> nazwy;
    private ArrayList<String> ceny;
    private ArrayList<Integer> m_id;

    private Button addButton;

    private ArrayList<String> nazwy_zamowionych;
    private ArrayList<String> ceny_zamowionych;
    private ArrayList<Integer> id_zamowionych;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter_add_order);

        nazwy = new ArrayList<String>();
        ceny = new ArrayList<String>();
        m_id = new ArrayList<Integer>();

        nazwy_zamowionych = new ArrayList<String>();
        ceny_zamowionych = new ArrayList<String>();
        id_zamowionych = new ArrayList<Integer>();

        Intent intent = getIntent();
        log = intent.getStringExtra("login");
        passw = intent.getStringExtra("pass");
        res = intent.getStringExtra("res");
        fun = intent.getStringExtra("func");

        connStr = "jdbc:mysql://node54808-pskorupa.unicloud.pl:3306/"+res+"?zeroDateTimeBehavior=convertToNull";
        User client = new User(log, pass, res, fun);

        nrstoluPlaceHolder = (EditText) findViewById(R.id.editNumerStolu);
        uwagiPlaceHolder = (EditText) findViewById(R.id.editUwagi);

        //poberanie listy z bazy danych
        try {
            client.response = getDishesFromDB(client);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        lista = (ListView) findViewById(R.id.menu_list_waiter);
        CustomAdapter customer = new CustomAdapter();
        lista.setAdapter(customer);
        lista.setChoiceMode(ListView.CHOICE_MODE_SINGLE);





        addButton = (Button) findViewById(R.id.button_add_new_order);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!nrstoluPlaceHolder.getText().toString().equals("")) {

                    uwagi = uwagiPlaceHolder.getText().toString();
                    nrStolu = nrstoluPlaceHolder.getText().toString();

                    try {
                        client.response = insertOrderToDB(client);
                        Toast.makeText(WaiterAddOrder.this, client.response, Toast.LENGTH_SHORT).show();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(WaiterAddOrder.this, "You can't add empty order", Toast.LENGTH_SHORT).show();
                }

            }});


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
            convertView = getLayoutInflater().inflate(R.layout.waiterlistmenu, null);


            TextView name = (TextView) convertView.findViewById(R.id.dish_name2);
            TextView cena = (TextView) convertView.findViewById(R.id.dish_price2);
            CheckBox check = (CheckBox) convertView.findViewById(R.id.checkOrder);


            int nr = position + 1;
            name.setText(nr +". "+ nazwy.get(position));
            cena.setText(ceny.get(position)+" zl");


            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    if(isChecked == true){
                    nazwy_zamowionych.add(nazwy.get(position));
                    ceny_zamowionych.add(ceny.get(position));
                    id_zamowionych.add(m_id.get(position));
                    }
                    else if (isChecked == false)
                    {
                        for (int i = 0; i < nazwy_zamowionych.size(); i++)
                        {
                            if (nazwy.get(position).equals(nazwy_zamowionych.get(i))){
                                nazwy_zamowionych.remove(i);
                                ceny_zamowionych.remove(i);
                                id_zamowionych.remove(i);
                            }

                        }
                    }

                }
            }
            );



            return convertView;
        }
    }

    public String getDishesFromDB(User us) throws ExecutionException, InterruptedException {
        return new WaiterAddOrder.menuFromDatabase().execute(us.response).get();
    }


    public String insertOrderToDB(User us) throws ExecutionException, InterruptedException {
        return new WaiterAddOrder.addOrdeToDatabase().execute(us.response).get();
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
                m_id.clear();
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                con= DriverManager.getConnection(connStr, user, pass);
                if (con == null){
                    response = "something go wrong";
                }
                else{

                    st=con.prepareStatement("select * from menu where dostepnosc='avaible'");
                    rs=st.executeQuery();
                    if  (!rs.isBeforeFirst() ) {
                        response = "Empty!";
                    }else{
                        while(rs.next()){
                            nazwy.add(rs.getString("potrawa"));
                            ceny.add(rs.getString("cena"));
                            m_id.add(rs.getInt("idm"));
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


    // ****************************** DODAWANIE ZAMOWIENIA **********************************************************************
    //---------------------------------------------------------------------------------------------------------------

    private class addOrdeToDatabase extends AsyncTask<String, String, String> {

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
                    int working_day = 0;
                    st=con.prepareStatement("select isWorkingDay from working_day");
                    rs=st.executeQuery();
                    if  (!rs.isBeforeFirst() ) {
                        response = "Empty!";
                    }else {
                        while (rs.next()) {
                            working_day = rs.getInt("isWorkingDay");
                        }
                        if (working_day == 0){
                            response = "Your work was not started yet. Chill Out.";
                            con.close();
                        }
                        else{

                            st=con.prepareStatement("select idu from users where login='"+log+"'");
                            rs=st.executeQuery();
                            if  (!rs.isBeforeFirst() ) {
                                response = "Empty!";
                            }else{
                                while (rs.next()){
                                    WaiterID = rs.getInt("idu");
                                }



                                st = con.prepareStatement("INSERT INTO zamowienie(nr_stolu, dodatkowe_info, u_id) VALUES("+nrStolu+", '"+uwagi+"', "+WaiterID+")");
                                st.executeUpdate();

                                int numerZamowienia=0;
                                st=con.prepareStatement("select idz from zamowienie where u_id="+WaiterID+" and nr_stolu="+nrStolu+" and dodatkowe_info='"+uwagi+"' and oplacono=0 and wydano=0");
                                rs=st.executeQuery();
                                if  (!rs.isBeforeFirst() ) {
                                    response = "Empty!";
                                }else {
                                    while (rs.next()) {
                                        numerZamowienia = rs.getInt("idz");
                                    }

                                    for (int i = 0; i < id_zamowionych.size(); i++)
                                    {
                                        st = con.prepareStatement("INSERT INTO detal_zamow(z_id, m_id) VALUES("+numerZamowienia+", "+id_zamowionych.get(i)+")");
                                        st.executeUpdate();
                                    }

                                    response = "OK";

                                }

                            }

                        }
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
