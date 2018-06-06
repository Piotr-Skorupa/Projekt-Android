package com.example.piotrskorupa.restauranthelper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class AdminMain extends AppCompatActivity {

    // odbieranie danych z glownej aktywnosci
    private String log;
    private String pass;
    private String res;
    private String fun;

    private Button startDayButton;
    private Button endDayButton;
    private Button workerButton;
    private Button editMenuButton;
    private Button bilansButton;

    private double przychod = 0;
    private int iloscZamowien = 0;
    private ArrayList<Integer> idzy = new ArrayList<Integer>();
    private ArrayList<Integer> idmy = new ArrayList<Integer>();

    private String connStr;
    private final String user = "root";
    private final String passw = "alamakota";
    public String response;
    static ResultSet rs;
    static PreparedStatement st;
    static Connection con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        Intent intent = getIntent();
        log = intent.getStringExtra("login");
        pass = intent.getStringExtra("pass");
        res = intent.getStringExtra("res");
        fun = intent.getStringExtra("func");

        User user = new User(log, pass, res, fun);

        connStr = "jdbc:mysql://node54808-pskorupa.unicloud.pl:3306/"+res+"?zeroDateTimeBehavior=convertToNull";

        startDayButton = (Button) findViewById(R.id.button_start);
        endDayButton = (Button) findViewById(R.id.button_end);
        editMenuButton = (Button) findViewById(R.id.button_edit_menu);
        workerButton = (Button) findViewById(R.id.button_worker);
        bilansButton = (Button) findViewById(R.id.button_bilans);


        // ROZPOCZYNANIE DNIA PRACY

        startDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                try {
                                    String odp = user.startDay();
                                    Toast.makeText(AdminMain.this, odp, Toast.LENGTH_SHORT).show();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(AdminMain.this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }});


        // KONCZENIE DNIA ROBOCZEGO
        endDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked

                                try {
                                    String odp = user.endDay();
                                    odp = bilansDB(user);
                                    Toast.makeText(AdminMain.this, odp, Toast.LENGTH_SHORT).show();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(AdminMain.this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }});



        editMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentMenu = new Intent(AdminMain.this, AdminMenu.class);
                intentMenu.putExtra("login", user.getLogin());
                intentMenu.putExtra("pass", user.getPassword());
                intentMenu.putExtra("res", user.getRestaurant());
                intentMenu.putExtra("func", user.getFunction());
                startActivity(intentMenu);


            }});

        workerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentWorker = new Intent(AdminMain.this, Admin_zarzadzanie.class);
                intentWorker.putExtra("login", user.getLogin());
                intentWorker.putExtra("pass", user.getPassword());
                intentWorker.putExtra("res", user.getRestaurant());
                intentWorker.putExtra("func", user.getFunction());
                startActivity(intentWorker);


            }});


        bilansButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intentBilans = new Intent(AdminMain.this, AdminBilans.class);
                intentBilans.putExtra("login", user.getLogin());
                intentBilans.putExtra("pass", user.getPassword());
                intentBilans.putExtra("res", user.getRestaurant());
                intentBilans.putExtra("func", user.getFunction());
                startActivity(intentBilans);


            }});

    }

    public String bilansDB(User us) throws ExecutionException, InterruptedException {
        return new AdminMain.bilansToDatabase().execute(us.response).get();
    }

    // ****************************** POBIERANIE DANYCH DO BILANSU **********************************************************************
    //---------------------------------------------------------------------------------------------------------------

    private class bilansToDatabase extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try
            {
                przychod = 0.0;
                iloscZamowien = 0;
                idzy.clear();
                idmy.clear();

                Class.forName("com.mysql.jdbc.Driver").newInstance();
                con= DriverManager.getConnection(connStr, user, passw);
                if (con == null){
                    response = "something go wrong";
                }
                else{

                    st=con.prepareStatement("delete from zamowienie where oplacono=0");
                    st.executeUpdate();

                    st=con.prepareStatement("select * from zamowienie");
                    rs=st.executeQuery();
                    if  (!rs.isBeforeFirst() ) {
                        response = "Empty!";
                    }else{
                        while(rs.next()){

                            iloscZamowien++;

                        }
                        response = "OK";
                    }

                    st=con.prepareStatement("select * from detal_zamow");
                    rs=st.executeQuery();
                    if  (!rs.isBeforeFirst() ) {
                        response = "Empty!";
                    }else{
                        while(rs.next()){

                            idmy.add(rs.getInt("m_id"));

                        }
                        response = "OK";
                    }

                    for (int i = 0; i < idmy.size(); i++)
                    {
                        st=con.prepareStatement("select cena from menu where idm="+idmy.get(i)+"");
                        rs=st.executeQuery();
                        if  (!rs.isBeforeFirst() ) {
                            response = "Empty!";
                        }else{
                            while(rs.next()){

                                przychod+=(rs.getDouble("cena"));

                            }
                            response = "OK";
                        }
                    }

                    st=con.prepareStatement("delete from zamowienie");
                    st.executeUpdate();

                    if (przychod > 0 && iloscZamowien > 0) {
                        st = con.prepareStatement("insert into bilans(przychod, ilosc_zmowien) values("+ przychod +", " + iloscZamowien + ")");
                        st.executeUpdate();
                        response = "OK";
                    }
                    else{
                        response = "This day was empty!";
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
