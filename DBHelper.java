package edu.ucsb.cs.JonathanSegoviaArturoMilanes;

import com.google.gson.Gson;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DBHelper {

    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private Customers customer;

    public boolean createNewCustomer(String name, String address, int pin){
        //TODO: check to see if pin is 4 digits, check if the user already exists, and check to see if taxId already exists
        boolean success = false;
        try{
            //initialize customer
            int taxId = (int)(Math.random() * ((Integer.MAX_VALUE) + 1))*(-1);
            customer = new Customers(taxId, name, address, pin);

            //Insert into db
            String insertCustomer = "INSERT INTO Customers (taxId, name, address, pin) " +
                    "VALUES (" + taxId + ", '" + name + "', '" + address +"'," + pin + ")";
            stmt.executeUpdate(insertCustomer);

            success = true;
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        catch(Exception e){
            //Handle errors for Class.forName
            System.out.println(e.getMessage());
        }
        return success;
    }

    public boolean logIn(String name, int pin){
        //TODO: check to see if user doesn't exit
        boolean success = false;

        int taxId = Integer.MIN_VALUE;
        String address = "";

        try{

            //get customer information
            String customerQuery = "SELECT * from Customers WHERE name = '" + name + "'";
            rs = stmt.executeQuery(customerQuery);
            while(rs.next()) {
                taxId = rs.getInt("taxId");
                address = rs.getString("address");
                System.out.println(address);
            }
            customer = new Customers(taxId, name, address, pin);

            //get customer accounts
            String accountIdQuery = "SELECT accountId FROM Owns WHERE taxId = " + customer.taxId;
            rs = stmt.executeQuery(accountIdQuery);
            while(rs.next()){
                int accountId = rs.getInt("accountId");
                String accountQuery = "SELECT * FROM Accounts WHERE accountId = " + accountId;
                ResultSet acctRS = stmt.executeQuery(accountQuery);

                while(acctRS.next()){
                    int acctId = acctRS.getInt("accountId");
                    float balance = acctRS.getFloat("balance");
                    String branch = acctRS.getString("branch");
                    float interestRate = acctRS.getFloat("interestRate");
                    String accountType = acctRS.getString("accountType");
                    String transactionHistory = acctRS.getString("transactionHistory");

                    customer.addAccount(acctId, balance, branch, interestRate, accountType);
                    customer.accounts.get(accountType).populateTransactionHistory(transactionHistory);
                }
            }

            success = true;
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        catch(Exception e){
            //Handle errors for Class.forName
            System.out.println(e.getMessage());
        }

        return success;
    }

    public boolean createNewAccount(String accountType, String branch){
        //TODO: handle interest rates

        if(customer.accounts.containsKey(accountType)){
            System.out.println("This account already exists");
        }

        boolean success = false;
        try{
            int accountId = (int)(Math.random() * ((Integer.MAX_VALUE) + 1))*(-1);
            float balance = 0.0f;
            float interestRate = 5.5f;
            String transactionHistory = new Gson().toJson(new ArrayList<String>());

            //insert into Accounts
            String insertAccount = "INSERT INTO Accounts (accountId, balance, branch, interestRate, transactionHistory, accountType) " +
                    "VALUES (" + accountId + "," + balance + ", '" + branch + "', " + interestRate + ", '" + transactionHistory + "', '" + accountType + "')";
            stmt.executeUpdate(insertAccount);

            //insert into Owns
            String insertOwns = "INSERT INTO Owns (accountId, taxId) " +
                                "VALUES(" + accountId + ", " + customer.taxId + ")";
            stmt.executeUpdate(insertOwns);

            //initialize new Account
            customer.addAccount(accountId, balance, branch, interestRate, accountType);

            success = true;
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        catch(Exception e){
            //Handle errors for Class.forName
            System.out.println(e.getMessage());
        }

        return success;
    }
    public void withdraw(int amount){
    }
    public void deposit(){}

    public void connect(String jdbcDriver, String DbUrl, String username, String password){
        try{
            //Register JDBC driver
            Class.forName(jdbcDriver);

            //Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DbUrl, username, password);
            System.out.println("Connected database successfully...");

            //initialize stmt
            stmt = conn.createStatement();
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }//end try
    }
}
