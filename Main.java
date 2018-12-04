package edu.ucsb.cs.JonathanSegoviaArturoMilanes;
//STEP 1. Import required packages
//import java.sql.*;

public class Main {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    static final String DB_URL = "jdbc:oracle:thin:@cloud-34-133.eci.ucsb.edu:1521:XE";

    //  Database credentials
    static final String USERNAME = "arturo";
    static final String PASSWORD = "8016297";

    public static void main(String[] args) {
        DBHelper dbHelper = new DBHelper();
        dbHelper.connect(JDBC_DRIVER,
                        DB_URL,
                        USERNAME,
                        PASSWORD);

        if(dbHelper.logIn("jonathan", 6969)){
            System.out.println("Login Successful");
        }
        else{
            System.out.println("Login Failed");
        }

    }//end main
}//end JDBCExample
