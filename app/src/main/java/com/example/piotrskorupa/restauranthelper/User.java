package com.example.piotrskorupa.restauranthelper;

/**
 * Created by Piotr Skorupa on 2018-04-04.
 */

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutionException;

public class User {

    private String login;
    private String password;
    private String restaurant;
    private String function;


    public String response;
    static ResultSet rs;
    static PreparedStatement st;
    static Connection con;

    private final String connStr = "jdbc:mysql://node54808-pskorupa.unicloud.pl:3306/Restaurants?zeroDateTimeBehavior=convertToNull";
    private final String user = "root";
    private final String pass = "alamakota";

    User(){
        login = "";
        password = "";
        restaurant = "";
        function = "";
        response = "";
    }

    User (String log, String pass, String res, String func)
    {
        login = log;
        password = pass;
        restaurant = res;
        response = "";
        function = func;
    }

    public void setLogin(String log)
    {
        login = log;
    }
    public void setPassword(String pass){password = pass;}
    public void setRestaurant(String res) {restaurant = res;}
    public void setFunction(int func)
    {
        switch (func){
            case 1:
                function = "admin";
                break;
            case 2:
                function = "kelner";
                break;
            case 3:
                function = "kucharz";
                break;
            default:
                function = "";
                break;
        }
    }

    public String getLogin() { return login;}
    public String getPassword() {return password;}
    public String getRestaurant() {return restaurant;}
    public String getFunction() {return function;}

    private class connectDatabase extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {

            try
            {


                Class.forName("com.mysql.jdbc.Driver").newInstance();
                con=DriverManager.getConnection(connStr, user, pass);
                if (con == null){
                    response = "something go wrong";
                }
                else{

                    st=con.prepareStatement("select * from users where login='"+login+"' and password='"+password+"' and restaurant='"+restaurant+"' and function='"+function+"'");
                    rs=st.executeQuery();
                    if  (!rs.isBeforeFirst() ) {
                        response = "Login or Password is incorrect!";
                    }else{
                        rs.next();
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


    public String connection() throws ExecutionException, InterruptedException {

       return new connectDatabase().execute(response).get();


    }


}
