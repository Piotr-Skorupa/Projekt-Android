package com.example.piotrskorupa.restauranthelper;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

    }
}
