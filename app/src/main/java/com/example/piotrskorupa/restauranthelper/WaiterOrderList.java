package com.example.piotrskorupa.restauranthelper;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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

public class WaiterOrderList extends AppCompatActivity {

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


    private ListView lista;
    private ArrayList<Integer> id_zamowienia;
    private ArrayList<Integer> numery_stolu;
    private ArrayList<Integer> wydano;
    private ArrayList<Integer> oplacono;
    private ArrayList<String> dodatkowe_info;
    private ArrayList<Integer> kto_obsluguje;
    private ArrayList<String> potrawy;

    private User client;

    private TextView waiterText;
    private TextView menuText;
    private TextView infoText;
    private TextView priceText;
    private double price = 0.0;
    private String userName = "";
    private Button refreshButton;

    int clickedIdz = 0;
    int clickedIdu = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter_order_list);

        Intent intent = getIntent();
        log = intent.getStringExtra("login");
        passw = intent.getStringExtra("pass");
        res = intent.getStringExtra("res");
        fun = intent.getStringExtra("func");

        connStr = "jdbc:mysql://node54808-pskorupa.unicloud.pl:3306/"+res+"?zeroDateTimeBehavior=convertToNull";
        client = new User(log, pass, res, fun);

        id_zamowienia = new ArrayList<Integer>();
        numery_stolu = new ArrayList<Integer>();
        wydano = new ArrayList<Integer>();
        oplacono = new ArrayList<Integer>();
        dodatkowe_info = new ArrayList<String>();
        kto_obsluguje = new ArrayList<Integer>();
        potrawy = new ArrayList<String>();


        waiterText = (TextView) findViewById(R.id.waiter_name);
        menuText = (TextView) findViewById(R.id.dish_name3);
        infoText = (TextView) findViewById(R.id.info_name);
        priceText = (TextView) findViewById(R.id.price_name);
        refreshButton = (Button) findViewById(R.id.refresh_button);

        //poberanie listy z bazy danych
        try {
            client.response = getOrdersFromDB(client);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        lista = (ListView) findViewById(R.id.list_order_waiter);
        CustomAdapter customer = new CustomAdapter();
        lista.setAdapter(customer);
        lista.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //poberanie listy z bazy danych
                try {
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
        priceText.setText("PRICE: "+ price + " zl");

        String orderString = "DISHES: ";
        for (int j =0 ; j < potrawy.size(); j++){
            orderString += potrawy.get(j);
            orderString += ", ";
        }

        menuText.setText(orderString);

    }


    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return numery_stolu.size();
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
            convertView = getLayoutInflater().inflate(R.layout.order_list_layout, null);


            TextView idz = (TextView) convertView.findViewById(R.id.numer_zamowienia);
            TextView nrStol = (TextView) convertView.findViewById(R.id.numer_stolu);
            TextView wydanotext = (TextView) convertView.findViewById(R.id.wydano);

            Button payButton = (Button) convertView.findViewById(R.id.oplac);
            Button delButton = (Button) convertView.findViewById(R.id.usun);
            Button infoButton = (Button) convertView.findViewById(R.id.info_button);


            idz.setText(id_zamowienia.get(position).toString());
            nrStol.setText(numery_stolu.get(position).toString());
            if (wydano.get(position) == 1) {
                  wydanotext.setText("READY!");
                  idz.setTextColor(Color.GREEN);
                  nrStol.setTextColor(Color.GREEN);
                  wydanotext.setTextColor(Color.GREEN);
            } else {
                 wydanotext.setText("COOKING...");
                 idz.setTextColor(Color.YELLOW);
                 nrStol.setTextColor(Color.YELLOW);
                 wydanotext.setTextColor(Color.YELLOW);
            }

            infoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickedIdu = kto_obsluguje.get(position);
                    clickedIdz = id_zamowienia.get(position);
                    fillTextBoxes(position);

                }});

            delButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (wydano.get(position) == 0){
                        clickedIdz = id_zamowienia.get(position);
                        try {
                            client.response = deleteOrdersFromDB(client);
                            client.response = getOrdersFromDB(client);
                            notifyDataSetChanged();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }else
                    {
                        Toast.makeText(WaiterOrderList.this, "You can not delete this order!", Toast.LENGTH_SHORT).show();
                    }



                }});

            payButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (wydano.get(position) == 1){
                        clickedIdz = id_zamowienia.get(position);
                        try {
                            client.response = updateOrdersFromDB(client);
                            client.response = getOrdersFromDB(client);
                            notifyDataSetChanged();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }else
                    {
                        Toast.makeText(WaiterOrderList.this, "This order is not ready to pay!", Toast.LENGTH_SHORT).show();
                    }



                }});

            return convertView;
        }
    }

    public String updateOrdersFromDB(User us) throws ExecutionException, InterruptedException {
        return new WaiterOrderList.payToDatabase().execute(us.response).get();
    }


    public String deleteOrdersFromDB(User us) throws ExecutionException, InterruptedException {
        return new WaiterOrderList.deleteFromDatabase().execute(us.response).get();
    }


    public String getOrdersFromDB(User us) throws ExecutionException, InterruptedException {
        return new WaiterOrderList.orderFromDatabase().execute(us.response).get();
    }

    public String getDetalsFromDB(User us) throws ExecutionException, InterruptedException {
        return new WaiterOrderList.detalsFromDatabase().execute(us.response).get();
    }


    // ****************************** POBIERANIE ZAMOWIEN **********************************************************************
    //---------------------------------------------------------------------------------------------------------------

    private class orderFromDatabase extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try
            {

                numery_stolu.clear();
                id_zamowienia.clear();
                oplacono.clear();
                wydano.clear();
                dodatkowe_info.clear();
                kto_obsluguje.clear();

                Class.forName("com.mysql.jdbc.Driver").newInstance();
                con= DriverManager.getConnection(connStr, user, pass);
                if (con == null){
                    response = "something go wrong";
                }
                else{

                    st=con.prepareStatement("select * from zamowienie where oplacono=0");
                    rs=st.executeQuery();
                    if  (!rs.isBeforeFirst() ) {
                        response = "Empty!";
                    }else{
                        while(rs.next()){
                            id_zamowienia.add(rs.getInt("idz"));
                            numery_stolu.add(rs.getInt("nr_stolu"));
                            dodatkowe_info.add(rs.getString("dodatkowe_info"));
                            wydano.add(rs.getInt("wydano"));
                            kto_obsluguje.add(rs.getInt("u_id"));
                            oplacono.add(0);
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
                price = 0.0;
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
                                        price += rs.getDouble("cena");
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


    // ****************************** USUWANIE ZAMOWIEN **********************************************************************
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

                    st=con.prepareStatement("delete from zamowienie where idz="+clickedIdz+"");
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


    // ****************************** OPLACANIE ZAMOWIEN **********************************************************************
    //---------------------------------------------------------------------------------------------------------------

    private class payToDatabase extends AsyncTask<String, String, String> {

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

                    st=con.prepareStatement("update zamowienie set oplacono=1 where idz="+clickedIdz+"");
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
