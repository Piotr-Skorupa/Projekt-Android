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

import org.w3c.dom.Text;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class CookMain extends AppCompatActivity {

    private String log;
    private String passw;
    private String res;
    private String fun;


    public String response;
    static ResultSet rs;
    static PreparedStatement st;
    static Connection con;

    private String connStr;
    private final String user = "root";
    private final String pass = "alamakota";

    private Button refreshButton;
    private TextView waiterText;
    private TextView dishesText;
    private TextView infoText;

    private User client;

    private ListView lista;
    private ArrayList<Integer> id_zamowienia;
    private ArrayList<String> dodatkowe_info;
    private ArrayList<Integer> kto_obsluguje;
    private ArrayList<String> potrawy;

    private String userName = "";
    private int clickedIdz = 0;
    private int clickedIdu = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_main);

        refreshButton = (Button) findViewById(R.id.refresh_button2);
        waiterText = (TextView) findViewById(R.id.waiter_name2);
        dishesText = (TextView) findViewById(R.id.dish_name4);
        infoText = (TextView) findViewById(R.id.info_name2);

        id_zamowienia = new ArrayList<Integer>();
        dodatkowe_info = new ArrayList<String>();
        kto_obsluguje = new ArrayList<Integer>();
        potrawy = new ArrayList<String>();

        Intent intent = getIntent();
        log = intent.getStringExtra("login");
        passw = intent.getStringExtra("pass");
        res = intent.getStringExtra("res");
        fun = intent.getStringExtra("func");

        connStr = "jdbc:mysql://node54808-pskorupa.unicloud.pl:3306/"+res+"?zeroDateTimeBehavior=convertToNull";
        client = new User(log, pass, res, fun);

        //poberanie listy z bazy danych
        try {
            client.response = getOrdersFromDB(client);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        lista = (ListView) findViewById(R.id.list_order_cook);
        CustomAdapter customer = new CustomAdapter();
        lista.setAdapter(customer);
        lista.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    client.response = getOrdersFromDB(client);
                    customer.notifyDataSetChanged();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }



            }});

    }

    private void fillTextBoxes(int i)
    {

        try {
            client.response = getDetalsFromDB(client);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        waiterText.setText("WAITER: " +  userName);
        infoText.setText("INFO: " + dodatkowe_info.get(i).toString());

        String orderString = "DISHES: ";
        for (int j =0 ; j < potrawy.size(); j++){
            orderString += potrawy.get(j);
            orderString += ", ";
        }

        dishesText.setText(orderString);

    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return id_zamowienia.size();
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
            convertView = getLayoutInflater().inflate(R.layout.cook_list_component, null);


            TextView idz = (TextView) convertView.findViewById(R.id.numer_zamowienia2);

            Button doneButton = (Button) convertView.findViewById(R.id.wydaj2);
            Button infoButton = (Button) convertView.findViewById(R.id.info_button2);


            idz.setText(id_zamowienia.get(position).toString());


            infoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickedIdu = kto_obsluguje.get(position);
                    clickedIdz = id_zamowienia.get(position);
                    fillTextBoxes(position);

                }});

            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    clickedIdu = kto_obsluguje.get(position);
                    clickedIdz = id_zamowienia.get(position);

                    try
                    {
                        client.response = updateDB(client);
                        client.response = getOrdersFromDB(client);
                        notifyDataSetChanged();
                        Toast.makeText(CookMain.this, client.response, Toast.LENGTH_SHORT).show();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    }



                }});



            return convertView;
        }
    }

    public String getOrdersFromDB(User us) throws ExecutionException, InterruptedException {
        return new CookMain.orderFromDatabase().execute(us.response).get();
    }

    public String getDetalsFromDB(User us) throws ExecutionException, InterruptedException {
        return new CookMain.detalsFromDatabase().execute(us.response).get();
    }

    public String updateDB(User us) throws ExecutionException, InterruptedException {
        return new CookMain.updateToDatabase().execute(us.response).get();
    }

    // ****************************** POBIERANIE ZAMOWIEN **********************************************************************
    //---------------------------------------------------------------------------------------------------------------

    private class orderFromDatabase extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try
            {

                id_zamowienia.clear();
                dodatkowe_info.clear();
                kto_obsluguje.clear();

                Class.forName("com.mysql.jdbc.Driver").newInstance();
                con= DriverManager.getConnection(connStr, user, pass);
                if (con == null){
                    response = "something go wrong";
                }
                else{

                    st=con.prepareStatement("select * from zamowienie where wydano=0 and oplacono=0");
                    rs=st.executeQuery();
                    if  (!rs.isBeforeFirst() ) {
                        response = "Empty!";
                    }else{
                        while(rs.next()){
                            id_zamowienia.add(rs.getInt("idz"));
                            dodatkowe_info.add(rs.getString("dodatkowe_info"));
                            kto_obsluguje.add(rs.getInt("u_id"));

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

    // ****************************** POBIERANIE DETALI ZAMOWIEN **********************************************************************
    //---------------------------------------------------------------------------------------------------------------

    private class detalsFromDatabase extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try
            {

                potrawy.clear();
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                con= DriverManager.getConnection(connStr, user, pass);
                if (con == null){
                    response = "something go wrong";
                }
                else{

                    st=con.prepareStatement("select login from users where idu="+clickedIdu+"");
                    rs=st.executeQuery();
                    if  (!rs.isBeforeFirst() ) {
                        response = "Empty!";
                    }else{
                        while(rs.next()){
                            userName = rs.getString("login");
                        }
                        ArrayList<Integer> idPotraw = new ArrayList<Integer>();
                        st=con.prepareStatement("select m_id from detal_zamow where z_id="+clickedIdz+"");
                        rs=st.executeQuery();
                        if  (!rs.isBeforeFirst() ) {
                            response = "Empty!";
                        }else{
                            while(rs.next()){
                                idPotraw.add(rs.getInt("m_id"));
                            }

                            for (int i = 0; i < idPotraw.size(); i++)
                            {
                                st=con.prepareStatement("select potrawa, cena from menu where idm="+idPotraw.get(i)+"");
                                rs=st.executeQuery();
                                if  (!rs.isBeforeFirst() ) {
                                    response = "Empty!";
                                }else {
                                    while (rs.next()) {
                                        potrawy.add(rs.getString("potrawa"));
                                    }

                                }
                            }

                            response = "OK";
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


    // ****************************** WYDAWANIE ZAMOWIEN **********************************************************************
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

                    st=con.prepareStatement("update zamowienie set wydano=1 where idz="+clickedIdz+"");
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
