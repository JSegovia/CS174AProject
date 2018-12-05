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

    private boolean isClosed=false;
    
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
public float withdraw(int accountId, float amount)  {
//        if (isClosed== true) {
//            //if account closed then no transactions allowed
//            System.out.println("account is closed");
//            return;
//        }


        //check is account belongs to current customer

        String accountType = getAcctType(accountId);
        if(accountType.equals("")) return -1.0f;


        float oldBalance = 0.0f;

        boolean isCustomer = isCustomerAcct(accountId);

        if(isCustomer){
            oldBalance = customer.accounts.get(accountType).balance;
        }
        else{
            String getBalance = "SELECT balance FROM Accounts WHERE accountId = " + accountId;
            try{
                rs = stmt.executeQuery(getBalance);
                while(rs.next()){
                    oldBalance = rs.getFloat("balance");
                }
            }catch (SQLException E) {
                E.printStackTrace();
            }
        }


        float newBalance = oldBalance-amount;
        //if transaction makes balance below 0, then fail.
        //if transaction makes balance .01 or less automatically closes the account
        if (newBalance <0){
            //fail
            System.out.println("fail");
            return -2.0f;
        }

        else if (newBalance <=.01) {
            //close account
            //isClosed = true; //if closed then no transactions allowed;
            System.out.println("close account");
        }
        else {


            //update balance!!
            String updatedBalance=
                    "update accounts " +
                            "set balance = " + newBalance +
                            " where accountid= " +  accountId;
            try {
                stmt.executeQuery(updatedBalance);
            }catch (SQLException E) {
                E.printStackTrace();
            }
        }

        //update balance in class
        if(isCustomer) {
            customer.accounts.get(accountType).balance = newBalance;
        }

        return newBalance;

    }
    public float deposit(int accountId, float amount){

//        if (isClosed== true) {
//            //if account closed then no transactions allowed
//            System.out.println("account is closed");
//            return;
//        }

        String accountType = getAcctType(accountId);
        if(accountType.equals("")) return -1.0f;


        float oldBalance = 0.0f;

        boolean isCustomer = isCustomerAcct(accountId);

        if(isCustomer){
            oldBalance = customer.accounts.get(accountType).balance;
        }
        else{
            String getBalance = "SELECT balance FROM Accounts WHERE accountId = " + accountId;
            try{
                rs = stmt.executeQuery(getBalance);
                while(rs.next()){
                    oldBalance = rs.getFloat("balance");
                }
            }catch (SQLException E) {
                E.printStackTrace();
            }
        }
        float newBalance = oldBalance + amount;



        //update balance!!
        String updatedBalance =
                "update accounts " +
                        "set balance = " + newBalance +
                        " where accountid = " + accountId;
        try{
            stmt.executeQuery(updatedBalance);


            // rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //update balance in class
        if(isCustomer) {
            customer.accounts.get(accountType).balance = newBalance;
        }

        return newBalance;
    }

    
    private boolean isCustomerAcct(int accountId){
        for(Map.Entry<String, Customers.Account> e : customer.accounts.entrySet()){
            if(e.getValue().accountId == accountId) {
                return true;
            }
        }

        return false;
    }

    private String getAcctType(int accountId){
        //make sure account exists and get accountType
        String accountType = "";
        try{
            String accountQuery = "SELECT accountType FROM Accounts WHERE accountId = " + accountId;
            rs = stmt.executeQuery(accountQuery);
            while(rs.next()){
                accountType = rs.getString("accountType");
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        catch(Exception e){
            //Handle errors for Class.forName
            System.out.println(e.getMessage());
        }

             return accountType;
    }

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
