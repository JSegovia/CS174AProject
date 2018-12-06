package edu.ucsb.cs.JonathanSegoviaArturoMilanes;

import com.google.gson.Gson;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DBHelper {

    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private Customers customer;

    public boolean createNewCustomer(String name, String address, int pin){
        if(String.valueOf(pin).length() != 4){
            System.out.println("PIN is not the right length");
            return false;
        }
        boolean success = false;
        try{
            String doesUserExist = "SELECT pin FROM Customers WHERE pin = " + pin;
            rs = stmt.executeQuery(doesUserExist);
            if(rs.next()){
                System.out.println("Customer with the PIN number already exists");
                return false;
            }

            //initialize customer
            int taxId = (int)(Math.random() * ((Integer.MAX_VALUE) + 1))*(-1);
            boolean isUnique = false;
            while(!isUnique){
                String doesTaxIdExist = "SELECT taxId FROM Customers WHERE taxId = " + taxId;
                rs = stmt.executeQuery(doesTaxIdExist);
                if(rs.next()){
                    taxId = (int)(Math.random() * ((Integer.MAX_VALUE) + 1))*(-1);
                }
                else{
                    isUnique = true;
                }
            }
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

    public boolean logIn(int pin){

        if(String.valueOf(pin).length() != 4){
            System.out.println("PIN is not the right length");
            return false;
        }
        boolean success = false;

        int taxId = Integer.MIN_VALUE;
        String address = "";
        String name = "";

        try{

            //get customer information
            String customerQuery = "SELECT * from Customers WHERE pin = '" + pin + "'";
            rs = stmt.executeQuery(customerQuery);
            while(rs.next()) {
                taxId = rs.getInt("taxId");
                address = rs.getString("address");
                name = rs.getString("name");
            }

            if(name.equals("")){
                System.out.println("This account does not exist");
                return false;
            }
            customer = new Customers(taxId, name, address, pin);

            //get co omwed customer accounts
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
                    int linkedAcctId = acctRS.getInt("accountLinkedId");

                    customer.addAccount(acctId, balance, branch, interestRate, accountType);
                    customer.accounts.get(accountType).populateTransactionHistory(transactionHistory);
                    customer.accounts.get(accountType).setLinkedAcctId(linkedAcctId);
                }
            }

            //get primary customer accounts
            String primaryAcctQuery = "SELECT accountId FROM PrimarilyOwns WHERE taxId = " + customer.taxId;
            rs = stmt.executeQuery(primaryAcctQuery);
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

    public boolean createNewAccount(String accountType, String branch, String accountLinked){
        //TODO: handle interest rates

        if(customer.accounts.containsKey(accountType)){
            System.out.println("This account already exists");
            return false;
        }

        boolean success = false;
        try{
            int accountId = (int)(Math.random() * ((Integer.MAX_VALUE) + 1))*(-1);
            float balance = 0.0f;
            float interestRate = 5.5f;
            String transactionHistory = new Gson().toJson(new ArrayList<String>());
            String insertAccount;

            //insert into Accounts
            if (accountType=="Pocket"){
                int accountLinkedId = customer.accounts.get(accountLinked).accountId;
                insertAccount = "INSERT INTO Accounts (accountId, balance, branch, interestRate, transactionHistory, accountType, accountLinkedId) " +
                        "VALUES (" + accountId + "," + balance + ", '" + branch + "', " + interestRate + ", '" + transactionHistory + "', '" + accountType + "', '" + accountLinkedId + "')";
            }
            else {
                insertAccount = "INSERT INTO Accounts (accountId, balance, branch, interestRate, transactionHistory, accountType) " +
                        "VALUES (" + accountId + "," + balance + ", '" + branch + "', " + interestRate + ", '" + transactionHistory + "', '" + accountType + "')";
            }
            stmt.executeUpdate(insertAccount);

            //insert into Owns
            String insertOwns = "INSERT INTO PrimarilyOwns (accountId, taxId) " +
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

    public boolean addCoOwner(int taxId, String accountType){
        boolean success = false;
        try{
            int accountId = customer.accounts.get(accountType).accountId;
            String addCoOwnerQuery = "INSERT INTO Owns(taxId, accountId) " +
                                     "VALUES (" + taxId + ", " + accountId + ")";
            stmt.executeUpdate(addCoOwnerQuery);

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

    public float withdraw(int accountId, float amount, boolean createTransaction, int recieverAcctId, String transactionType)  {
//        if (isClosed== true) {
//            //if account closed then no transactions allowed
//            System.out.println("account is closed");
//            return;
//        }

        //TODO: Add timestamp
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


        //add transaction
        if(createTransaction && isCustomer){
            customer.accounts.get(accountType).addTransaction(customer.name, transactionType, "", amount, accountId, recieverAcctId);
        }

        return newBalance;

    }
    public float deposit(int accountId, float amount, boolean createTransaction, int senderAcctId, String transactionType){

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
                        "where accountid = " + accountId;
        try{
            stmt.executeQuery(updatedBalance);


            // rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //add transaction
        if(createTransaction && isCustomer){

            customer.accounts.get(accountType).addTransaction(customer.name, transactionType, "", amount, senderAcctId, accountId);
        }

        //update balance in class
        if(isCustomer) {
            customer.accounts.get(accountType).balance = newBalance;
        }

        return newBalance;
    }

    public boolean transfer(int senderAcctId, int recieverAcctId, float amount){
        if(senderAcctId == recieverAcctId){
            System.out.println("Same account ids used");
            return false;
        }

        if(amount > 2000.0f){
            System.out.println("amount cannot be over 2000");
            return false;
        }

        if(isPocket(senderAcctId) || isPocket(recieverAcctId)){
            System.out.println("One of these accounts is a pocket account");
            return false;
        }

        boolean success = false;

        if(isCustomerAcct(senderAcctId)|| isCustomerAcct(senderAcctId)){
            System.out.println("Customer does not own one of these accounts");
            return false;
        }

        withdraw(senderAcctId, amount, true, recieverAcctId, "Transfer");
        deposit(recieverAcctId, amount, false, -1, "");
        return success;
    }
    public boolean collect(int acctId, float amount){
        if(!isPocket(acctId)){
            System.out.println("This account Id is not for a pocket account");
            return false;
        }

        if(!isCustomerAcct(acctId)){
            System.out.println("This account does not belong to current customer");
            return false;
        }

        int recieverId = -1;
        try{
            String getLinkedAcctId = "SELECT accountLinkedId FROM Accounts WHERE accountId = " + acctId;
            rs = stmt.executeQuery(getLinkedAcctId);
            while(rs.next()){
                recieverId = rs.getInt("accountLinkedId");
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        if(recieverId == -1){
            System.out.println("This account does is not linked to another");
            return false;
        }

        withdraw(acctId, amount + (amount * 0.03f), true, recieverId, "Collect");
        deposit(recieverId, amount, false, -1, "");

        return true;
    }
    public boolean wire(int senderAcctId, int recieverAcctId, float amount){
        boolean success = false;

        if(isCustomerAcct(senderAcctId)) {
            System.out.println("Customer does not own sender account");
            return false;
        }

        withdraw(senderAcctId, amount + (amount * 0.02f), true, recieverAcctId, "Wire");
        deposit(recieverAcctId, amount, false, -1, "");

        return success;
    }
    public boolean payFriend(int senderAcctId, int recieverAcctId, float amount){
        if(!isCustomerAcct(senderAcctId)){
            System.out.println("This account does not belong to current customer");
            return false;
        }
        if(isPocket(senderAcctId) && isPocket(recieverAcctId)){
            System.out.println("One of these accounts is not a pocket account");
            return false;
        }

        withdraw(senderAcctId, amount, true, recieverAcctId, "Pay-Friend");
        deposit(senderAcctId, amount, false, -1, "");

        return true;
    }

     public boolean topUp(int accountId, float amount){
        //
        ////        if (isClosed()) {
        ////            //if account closed then no transactions allowed
        ////            System.out.println("account is closed");
        ////            return false;
        ////        }

        if(!isCustomerAcct(accountId)){
            System.out.println("This account does not belong to current customer");
            return false;
        }

        int recieverId = -1;
        try{
            //pocketID
            String getLinkedAcctId = "SELECT accountLinkedId FROM Accounts WHERE accountId = " + accountId;
            rs = stmt.executeQuery(getLinkedAcctId);
            while(rs.next()){
                recieverId = rs.getInt("accountLinkedId");
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        if(recieverId == -1){
            System.out.println("This account does is not linked to another");
            return false;
        }

        //withdraw from checkings or savings
        withdraw(accountId, amount, true, recieverId, "TopUp");
        //deposit to pocket
        deposit(recieverId, amount, false, -1, "");
        return true;
    }
    public boolean purchase(int accountId, float amount){

//        if (isClosed()) {
//            //if account closed then no transactions allowed
//            System.out.println("account is closed");
//            return false;
//        }

        if(!isPocket(accountId)){
            System.out.println("This account Id is not for a pocket account");
            return false;
        }
        if(!isCustomerAcct(accountId)){
            System.out.println("This account does not belong to current customer");
            return false;
        }

        withdraw(accountId, amount, true, -1, "Purchase");
        return true;
    }
    
    private boolean isPocket(int accountID){
        try {
            String accountType = "";
            String checkingQuery = "SELECT accountType FROM Accounts WHERE accountId = " + accountID;
            rs = stmt.executeQuery(checkingQuery);
            while(rs.next()){
                accountType = rs.getString("accountType");
            }

            if(!accountType.equals("pocket")){
                return false;
            }
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }

        return true;
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
