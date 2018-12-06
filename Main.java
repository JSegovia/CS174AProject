package edu.ucsb.cs.JonathanSegoviaArturoMilanes;
//STEP 1. Import required packages
//import java.sql.*;

import java.util.Scanner;

public class Main {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    static final String DB_URL = "jdbc:oracle:thin:@cloud-34-133.eci.ucsb.edu:1521:XE";

    //  Database credentials
    static final String USERNAME = "jsegovia";
    static final String PASSWORD = "7654999";

    public static void main(String[] args) {
        DBHelper dbHelper = new DBHelper();
        dbHelper.connect(JDBC_DRIVER,
                        DB_URL,
                        USERNAME,
                        PASSWORD);

        Scanner input = new Scanner(System.in);

        while(true) {
            System.out.println("Press 1 for Bank Teller and Press 2 for ATM");
            int option = input.nextInt();
            if (option == 1) {
                new BankTeller().bankTeller(dbHelper, input);
            }
            else{
                new ATM().atm(dbHelper, input);
            }
        }

    }

}
