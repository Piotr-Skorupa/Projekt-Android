package com.example.piotrskorupa.restauranthelper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutionException;

public class Admin_zarzadzanie extends AppCompatActivity {

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

    private Button addButton;
    private Button showButton;
    private Button deleteButton;

    private EditText loginEdit;
    private EditText passEdit;

    private int function;

    private RadioButton radioKelner;
    private RadioButton radioKucharz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_zarzadzanie);

        Intent intent = getIntent();
        log = intent.getStringExtra("login");
        passw = intent.getStringExtra("pass");
        res = intent.getStringExtra("res");
        fun = intent.getStringExtra("func");

        User client = new User(log, pass, res, fun);

        addButton = (Button) findViewById(R.id.button_add_worker);
        showButton = (Button) findViewById(R.id.button_show_workers);
        deleteButton = (Button) findViewById(R.id.button_delete_restaurant);

        loginEdit = (EditText) findViewById(R.id.login_worker);
        passEdit = (EditText) findViewById(R.id.pass_worker);

        radioKelner = (RadioButton) findViewById(R.id.kelner_add);
        radioKucharz = (RadioButton) findViewById(R.id.kucharz_add);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                try {
                                    String odp = dropDB(client);
                                    Toast.makeText(Admin_zarzadzanie.this, odp, Toast.LENGTH_SHORT).show();

                                    Intent intentEnd = new Intent(Admin_zarzadzanie.this, MainActivity.class);
                                    startActivity(intentEnd);

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

                AlertDialog.Builder builder = new AlertDialog.Builder(Admin_zarzadzanie.this);
                builder.setMessage("Are you sure you want to delete the restaurant with all its elements?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }});


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkState()){

                    User userNew = new User();
                    userNew.setLogin(loginEdit.getText().toString());
                    userNew.setPassword(passEdit.getText().toString());
                    userNew.setRestaurant(res);
                    userNew.setFunction(function);

                        try{
                            if (userNew.connectionForRegisterWorker() == "OK"){
                                Toast.makeText(Admin_zarzadzanie.this, "Worker has been succesfully registered!", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(Admin_zarzadzanie.this, userNew.response, Toast.LENGTH_SHORT).show();
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                }
                else{
                    Toast.makeText(Admin_zarzadzanie.this, "Wrong Data!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentMenu = new Intent(Admin_zarzadzanie.this, WorkersList.class);
                intentMenu.putExtra("login", log);
                intentMenu.putExtra("pass", passw);
                intentMenu.putExtra("res", res);
                intentMenu.putExtra("func", fun);
                startActivity(intentMenu);


            }});
    }


    protected boolean checkState(){

        if (loginEdit.getText().toString().equals("")
                || passEdit.getText().toString().equals("")
                ){
            return false;
        }
        // checking check boxes
        int checkboxCounter = 0;
        if (radioKelner.isChecked()) {
            checkboxCounter++;
            function = 2;
        }

        if (radioKucharz.isChecked()) {
            checkboxCounter++;
            function = 3;
        }

        if (checkboxCounter != 1)
            return false;


        return true;
    }




    public String dropDB(User us) throws ExecutionException, InterruptedException{

        return new Admin_zarzadzanie.dropDatabase().execute(us.response).get();
    }


    // ****************************** USUWANIE CALEJ BAZY RESTAURACJI **********************************************************************
    //---------------------------------------------------------------------------------------------------------------

    private class dropDatabase extends AsyncTask<String, String, String> {

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

                    st=con.prepareStatement("DROP DATABASE "+ res + "");
                    st.executeUpdate();

                    st=con.prepareStatement("DELETE FROM users WHERE restaurant='"+ res + "'");
                    st.executeUpdate();

                    response = "Your restaurant doesn't exists now!";
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
