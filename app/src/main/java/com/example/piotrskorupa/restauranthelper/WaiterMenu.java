package com.example.piotrskorupa.restauranthelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WaiterMenu extends AppCompatActivity {

    private Button addButton;
    private Button showButton;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter_menu);

        String log, pass, res, fun;

        Intent intent = getIntent();
        log = intent.getStringExtra("login");
        pass = intent.getStringExtra("pass");
        res = intent.getStringExtra("res");
        fun = intent.getStringExtra("func");

        user = new User(log,pass,res,fun);

        addButton = (Button) findViewById(R.id.button_add_order);
        showButton = (Button) findViewById(R.id.button_show_orders);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(WaiterMenu.this, WaiterAddOrder.class);
                intent.putExtra("login", user.getLogin());
                intent.putExtra("pass", user.getPassword());
                intent.putExtra("res", user.getRestaurant());
                intent.putExtra("func", user.getFunction());
                startActivity(intent);


            }});


        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent intentMenu = new Intent(WaiterMenu.this, AdminMenu.class);
                //intentMenu.putExtra("login", user.getLogin());
               // intentMenu.putExtra("pass", user.getPassword());
               // intentMenu.putExtra("res", user.getRestaurant());
               // intentMenu.putExtra("func", user.getFunction());
               // startActivity(intentMenu);


            }});



    }
}
