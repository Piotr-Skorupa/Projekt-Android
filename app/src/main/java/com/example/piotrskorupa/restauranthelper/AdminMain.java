package com.example.piotrskorupa.restauranthelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

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

    }
}
