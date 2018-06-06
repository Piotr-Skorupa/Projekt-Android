package com.example.piotrskorupa.restauranthelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {


    private EditText userLoginPlace;
    private EditText userPasswordPlace;
    private EditText userRestaurantPlace;
    private RadioButton checkAdmin;
    private RadioButton checKelner;
    private RadioButton checkKucharz;

    private Button logowanie;
    private Button rejestracja;

    private int function;      // 1 = admin; 2 = waiter; 3 = cook;
    User user;
    private String loginResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = new User();

        userLoginPlace = (EditText) findViewById(R.id.login);
        userPasswordPlace = (EditText) findViewById(R.id.pass);
        userRestaurantPlace = (EditText) findViewById(R.id.restaurant);

        checkAdmin = (RadioButton) findViewById(R.id.admin);
        checKelner = (RadioButton) findViewById(R.id.kelner);
        checkKucharz = (RadioButton) findViewById(R.id.kucharz);

        function = 0;




        logowanie = (Button) findViewById(R.id.loguj);

        logowanie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkState()){

                    user.setLogin(userLoginPlace.getText().toString());
                    user.setPassword(userPasswordPlace.getText().toString());
                    user.setRestaurant(userRestaurantPlace.getText().toString());
                    user.setFunction(function);
                    try {
                        if (user.connectionForLoging() == "OK"){
                            if (function == 1){

                                // ADMIN SIGN IN SECTION

                                Intent adminIntent = new Intent(MainActivity.this, AdminMain.class);
                                adminIntent.putExtra("login", user.getLogin());
                                adminIntent.putExtra("pass", user.getPassword());
                                adminIntent.putExtra("res", user.getRestaurant());
                                adminIntent.putExtra("func", user.getFunction());
                                startActivity(adminIntent);
                            }
                            if (function == 2)
                            {

                                // WAITER SIGN IN SECTION


                                Intent waiterIntent = new Intent(MainActivity.this, WaiterMenu.class);
                                waiterIntent.putExtra("login", user.getLogin());
                                waiterIntent.putExtra("pass", user.getPassword());
                                waiterIntent.putExtra("res", user.getRestaurant());
                                waiterIntent.putExtra("func", user.getFunction());
                                startActivity(waiterIntent);
                            }

                            if (function == 3)
                            {
                                // COOK SIGN IN SECTION

                                Intent cookIntent = new Intent(MainActivity.this, CookMain.class);
                                cookIntent.putExtra("login", user.getLogin());
                                cookIntent.putExtra("pass", user.getPassword());
                                cookIntent.putExtra("res", user.getRestaurant());
                                cookIntent.putExtra("func", user.getFunction());
                                startActivity(cookIntent);
                            }


                        }else{
                            Toast.makeText(MainActivity.this, user.response, Toast.LENGTH_SHORT).show();
                        }

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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

                    // SIGN UP SECTION - ONLY ADMIN CAN ADD A RESTAURANT ACCOUNT

                    user.setLogin(userLoginPlace.getText().toString());            //converting EditText fields to strings
                    user.setPassword(userPasswordPlace.getText().toString());
                    user.setRestaurant(userRestaurantPlace.getText().toString());
                    user.setFunction(function);
                    if (function == 1){
                        try{
                            if (user.connectionForRegister() == "OK"){
                                Toast.makeText(MainActivity.this, "Succesfully registered! You can sign in now.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(MainActivity.this, user.response, Toast.LENGTH_SHORT).show();
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }


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

        // function checking fill of EditTexts and CheckBoxes
        // if empty you can not create account or sign in

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
