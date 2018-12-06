package edu.ucsb.cs.JonathanSegoviaArturoMilanes;
//STEP 1. Import required packages
//import java.sql.*;

import java.sql.SQLException;
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

//        if(dbHelper.createNewCustomer("Jonathan", "6621 Del Playa Dr", 6969)){
//            System.out.println("Customer Creation Successful");
//        }
//        else{
//            System.out.println("Customer Creation Failed");
//        }
//
//      2
//        if(dbHelper.createNewAccount(1, 11111111, "Student","BA",-1)){
//            System.out.println("Account Creation Successful");
//        }
//        else{
//            System.out.println("Account Creation Failed");
//        }
//       if(dbHelper.createNewAccount(2, 11111112, "Pocket","BA",1)){
//            System.out.println("Account Creation Successful");
//        }
//        else{
//            System.out.println("Account Creation Failed");
//        }
//if(dbHelper.topUp(806527761,80.0f)) {
//    System.out.println("Purchased Successful");
//}
//else {
//    System.out.println("Purchase failed");
//}
        //dbHelper.deposit(1549060227,1000.0f,true,-1,"");
//dbHelper.addInterest();
//        dbHelper.writeCheck(1911061714, 30.0f);
       // dbHelper.closedAccList();
        //dbHelper.withdraw(1549060227,12.70f,true,-1,"");
       // dbHelper.deposit(100.0f,"Student");
     //   dbHelper.topUp(60.0f,"Student");
            // dbHelper.purchase(988965995,20.0f);
//        Scanner input = new Scanner(System.in);
//
//        while(true) {
//            System.out.println("Press 1 for Bank Teller and Press 2 for ATM");
//            int option = input.nextInt();
//            if (option == 1) {
//                new BankTeller().bankTeller(dbHelper, input);
//            }
//            else{
//                new ATM().atm(dbHelper, input);
//            }
//        }
    }//end main
}//end JDBCExample
