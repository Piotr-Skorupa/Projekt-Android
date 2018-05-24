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

    private String connStr = "jdbc:mysql://node54808-pskorupa.unicloud.pl:3306/Restaurants?zeroDateTimeBehavior=convertToNull";
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


    // ****************************** LOGOWANIE **********************************************************************
    //---------------------------------------------------------------------------------------------------------------

    private class logingToDatabase extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {

            try
            {

                connStr = "jdbc:mysql://node54808-pskorupa.unicloud.pl:3306/Restaurants?zeroDateTimeBehavior=convertToNull";
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


    // ****************************** REJESTROWANIE ADMINA **********************************************************************
    //---------------------------------------------------------------------------------------------------------------

    private class registerToDatabase extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {

            try
            {

                connStr = "jdbc:mysql://node54808-pskorupa.unicloud.pl:3306/Restaurants?zeroDateTimeBehavior=convertToNull";
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                con=DriverManager.getConnection(connStr, user, pass);
                if (con == null){
                    response = "something go wrong";
                }
                else{

                    st=con.prepareStatement("select * from users where restaurant='"+restaurant+"'");
                    rs=st.executeQuery();
                    if  (!rs.isBeforeFirst() ) {

                        // TODO: Rejestracja i tworzenie tabel dla restauracji
                        st = con.prepareStatement("CREATE DATABASE IF NOT EXISTS "+ restaurant + "");
                        st.executeUpdate();

                        st = con.prepareStatement("INSERT INTO users(login, password, restaurant, function) VALUES('" + login + "', '" + password + "', '" + restaurant + "', '" + function + "')");
                        st.executeUpdate();

                        con.close();

                        connStr = "jdbc:mysql://node54808-pskorupa.unicloud.pl:3306/"+restaurant+"?zeroDateTimeBehavior=convertToNull";

                        con = DriverManager.getConnection(connStr, user, pass);
                        if (con == null){
                            response = "something go wrong";
                            return response;
                        }else {

                            // menu
                            st = con.prepareStatement("CREATE TABLE IF NOT EXISTS menu(\n" +
                                    "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                                    "  `potrawa` varchar(45) NOT NULL,\n" +
                                    "  `cena` DOUBLE(11,2) NOT NULL,\n" +
                                    "  `dostepnosc` varchar(45) DEFAULT NULL,\n" +
                                    "  PRIMARY KEY (`id`)\n" +
                                    ") ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1");
                            st.executeUpdate();

                            // working day
                            st = con.prepareStatement("CREATE TABLE IF NOT EXISTS working_day(\n" +
                                    "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                                    "  `isWorkingDay` varchar(45) NOT NULL,\n" +
                                    "   PRIMARY KEY (`id`)\n" +
                                    "   )ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1");

                            st.executeUpdate();

                            st = con.prepareStatement("INSERT INTO working_day(id, isWorkingDay) VALUES(1, 'no')");
                            st.executeUpdate();

                            //zamowienie
                            st = con.prepareStatement("CREATE TABLE IF NOT EXISTS  zamowienie(\n" +
                                    "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                                    "  `tresc_zamowienia` varchar(3000) NOT NULL,\n" +
                                    "  `nr_stolu` INT NOT NULL,\n" +
                                    "  `cena` varchar(45) NOT NULL,\n" +
                                    "  `dodatkowe_info` varchar(45) DEFAULT NULL,\n" +
                                    "  `wydano` varchar(45) NOT NULL DEFAULT 'nie wydano',\n" +
                                    "  `oplacono` varchar(45) NOT NULL DEFAULT 'nie oplacono',\n" +
                                    "  `kto_obsluguje` varchar(45) DEFAULT NULL,\n" +
                                    "  PRIMARY KEY (`id`)\n" +
                                    "   )ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1");
                            st.executeUpdate();

                            //bilans
                            st = con.prepareStatement("CREATE TABLE IF NOT EXISTS  bilans(\n" +
                                    "   `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                                    "   `data` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP UNIQUE,\n" +
                                    "   `przychod` INT NOT NULL,\n" +
                                    "   `ilosc_zmowien` INT NOT NULL,\n" +
                                    "   PRIMARY KEY (`id`)\n" +
                                    "   )ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1");

                            st.executeUpdate();

                            response = "OK";
                        }
                    }
                    else{
                        rs.next();
                        response = "This restaurant already exists in our database";
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


    // ****************************** REJESTROWANIE PRACOWNIKA **********************************************************************
    //---------------------------------------------------------------------------------------------------------------

    private class registerWorkerToDatabase extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {

            try
            {

                connStr = "jdbc:mysql://node54808-pskorupa.unicloud.pl:3306/Restaurants?zeroDateTimeBehavior=convertToNull";
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                con=DriverManager.getConnection(connStr, user, pass);
                if (con == null){
                    response = "something go wrong";
                }
                else{



                        st = con.prepareStatement("INSERT INTO users(login, password, restaurant, function) VALUES('" + login + "', '" + password + "', '" + restaurant + "', '" + function + "')");
                        st.executeUpdate();

                        response = "OK";


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





    // ****************************** START DNIA ROBOCZEGO **********************************************************************
    //---------------------------------------------------------------------------------------------------------------

    private class startWorkingDay extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {

            try
            {

                connStr = "jdbc:mysql://node54808-pskorupa.unicloud.pl:3306/"+restaurant+"?zeroDateTimeBehavior=convertToNull";
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                con=DriverManager.getConnection(connStr, user, pass);
                if (con == null){
                    response = "something go wrong";
                }
                else{

                    st=con.prepareStatement("UPDATE working_day SET isWorkingDay='yes' where id=1");
                    st.executeUpdate();

                }
                response = "Let's the working begin!";
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



    // ****************************** KONIEC DNIA ROBOCZEGO **********************************************************************
    //---------------------------------------------------------------------------------------------------------------

    private class endWorkingDay extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {

            try
            {

                connStr = "jdbc:mysql://node54808-pskorupa.unicloud.pl:3306/"+restaurant+"?zeroDateTimeBehavior=convertToNull";
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                con=DriverManager.getConnection(connStr, user, pass);
                if (con == null){
                    response = "something go wrong";
                }
                else{

                    st=con.prepareStatement("UPDATE working_day SET isWorkingDay='no' where id=1");
                    st.executeUpdate();

                }
                response = "The day has been finished!";
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


    public String startDay() throws ExecutionException, InterruptedException {

        return new startWorkingDay().execute(response).get();

    }

    public String endDay() throws ExecutionException, InterruptedException {

        return new endWorkingDay().execute(response).get();

    }


    public String connectionForLoging() throws ExecutionException, InterruptedException {

       return new logingToDatabase().execute(response).get();

    }

    public String connectionForRegister() throws ExecutionException, InterruptedException {

        return new registerToDatabase().execute(response).get();

    }

    public String connectionForRegisterWorker() throws ExecutionException, InterruptedException {

        return new registerWorkerToDatabase().execute(response).get();

    }

}
