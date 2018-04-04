package com.example.piotrskorupa.restauranthelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private EditText userLoginPlace;
    private EditText userPasswordPlace;
    private EditText userRestaurantPlace;
    private CheckBox checkAdmin;
    private CheckBox checKelner;
    private CheckBox checkKucharz;

    private Button logowanie;
    private Button rejestracja;

    private int function;      // 1 = admin; 2 = kelner; 3 = kucharz;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userLoginPlace = (EditText) findViewById(R.id.login);
        userPasswordPlace = (EditText) findViewById(R.id.pass);
        userRestaurantPlace = (EditText) findViewById(R.id.restaurant);

        checkAdmin = (CheckBox) findViewById(R.id.admin);
        checKelner = (CheckBox) findViewById(R.id.kelner);
        checkKucharz = (CheckBox) findViewById(R.id.kucharz);

        function = 0;

        User user = new User();


        logowanie = (Button) findViewById(R.id.loguj);

        logowanie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkState()){
                    //TODO:logowanie
                    Toast.makeText(MainActivity.this, "Logujemy!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this, "Wrong Data!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        rejestracja = (Button) findViewById(R.id.rejestruj);

        rejestracja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkState()){
                    //TODO:rejestracja
                    if (function == 1){
                        Toast.makeText(MainActivity.this, "Rejestrujemy!", Toast.LENGTH_SHORT).show();
                    }else
                    {
                        Toast.makeText(MainActivity.this, "Only Admin, can Create Account!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Wrong Data!", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    protected boolean checkState(){

        if (userLoginPlace.getText().toString().equals("")
                || userPasswordPlace.getText().toString().equals("")
                || userRestaurantPlace.getText().toString().equals("")
                ){
            return false;
        }
        // checking check boxes
        int checkboxCounter = 0;
        if (checKelner.isChecked()) {
            checkboxCounter++;
            function = 2;
        }
        if (checkAdmin.isChecked()) {
            checkboxCounter++;
            function = 1;
        }
        if (checkKucharz.isChecked()) {
            checkboxCounter++;
            function = 3;
        }

        if (checkboxCounter != 1)
            return false;


        return true;
    }


}
