package com.example.piotrskorupa.restauranthelper;

/**
 * Created by Piotr Skorupa on 2018-04-04.
 */

public class User {

    private String login;
    private String password;
    private String restaurant;
    private String function;

    User(){
        login = "";
        password = "";
        restaurant = "";
        function = "";
    }

    User (String log, String pass, String res, String func)
    {
        login = log;
        password = pass;
        restaurant = res;
        function = func;
    }




}
