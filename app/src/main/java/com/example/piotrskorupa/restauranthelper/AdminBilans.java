package com.example.piotrskorupa.restauranthelper;

import android.content.Intent;
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

public class AdminBilans extends AppCompatActivity {


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

    private User client;

    private ListView lista;
    private TextView dateText;
    private Button refreshButton;
    private int clickedNum = 0;

    private ArrayList<Integer> zamowienia;
    private ArrayList<Integer> ceny;
    private ArrayList<String> daty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_bilans);

        dateText = (TextView) findViewById(R.id.date_bilans);
        lista = (ListView) findViewById(R.id.list_bilans);
        refreshButton = (Button) findViewById(R.id.refresh_button_bilans);

        Intent intent = getIntent();
        log = intent.getStringExtra("login");
        passw = intent.getStringExtra("pass");
        res = intent.getStringExtra("res");
        fun = intent.getStringExtra("func");

        connStr = "jdbc:mysql://node54808-pskorupa.unicloud.pl:3306/"+res+"?zeroDateTimeBehavior=convertToNull";
        client = new User(log, pass, res, fun);

        zamowienia = new ArrayList<Integer>();
        ceny = new ArrayList<Integer>();
        daty = new ArrayList<String>();

        try
        {
            client.response = getBilansFromDB(client);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();

        }



        CustomAdapter customer = new CustomAdapter();
        lista.setAdapter(customer);
        lista.setChoiceMode(ListView.CHOICE_MODE_SINGLE);




        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateText.setText("DATE OF ADDITION: ");

                try
                {
                    client.response = getBilansFromDB(client);
                    customer.notifyDataSetChanged();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }


            }});


    }



    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return zamowienia.size();
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
            convertView = getLayoutInflater().inflate(R.layout.list_bilans_view, null);

            TextView text = (TextView) convertView.findViewById(R.id.bilansText);
            Button button = (Button) convertView.findViewById(R.id.date_button);

            text.setText(zamowienia.get(position) + " orders, " + ceny.get(position) + " zl");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dateText.setText("DATE OF ADDITION: " + daty.get(position));

                }});

            return convertView;
        }
    }

    public String getBilansFromDB(User us) throws ExecutionException, InterruptedException {
        return new AdminBilans.bilansFromDatabase().execute(us.response).get();
    }


    // ****************************** POBIERANIE ZAMOWIEN **********************************************************************
    //---------------------------------------------------------------------------------------------------------------

    private class bilansFromDatabase extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try
            {

                zamowienia.clear();
                ceny.clear();
                daty.clear();


                Class.forName("com.mysql.jdbc.Driver").newInstance();
                con= DriverManager.getConnection(connStr, user, pass);
                if (con == null){
                    response = "something go wrong";
                }
                else{

                    st=con.prepareStatement("select * from bilans");
                    rs=st.executeQuery();
                    if  (!rs.isBeforeFirst() ) {
                        response = "Empty!";
                    }else{
                        while(rs.next()){
                            ceny.add(rs.getInt("przychod"));
                            zamowienia.add(rs.getInt("ilosc_zmowien"));
                            daty.add(rs.getTimestamp("data").toString());
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
