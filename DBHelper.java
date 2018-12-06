package edu.ucsb.cs.JonathanSegoviaArturoMilanes;

import com.google.gson.Gson;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class DBHelper {

    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private Customers customer;

    public boolean createNewCustomer(String name, String address, int pin, int taxId){
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

            String doesTaxIdExist = "SELECT taxId FROM Customers WHERE taxId = " + taxId;
            rs = stmt.executeQuery(doesTaxIdExist);
            if(rs.next()){
                System.out.println("This Tax ID Already Exists");
                return false;
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
                    int isClosed = acctRS.getInt("isClosed");

                    customer.addAccount(acctId, balance, branch, interestRate, accountType, isClosed);
                    customer.accounts.get(accountId).populateTransactionHistory(transactionHistory);
                    customer.accounts.get(accountId).setLinkedAcctId(linkedAcctId);
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
                    int isClosed = acctRS.getInt("isClosed");

                    customer.addAccount(acctId, balance, branch, interestRate, accountType, isClosed);
                    customer.accounts.get(accountId).populateTransactionHistory(transactionHistory);
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

    public boolean createNewAccount(int accountId, int taxId, String accountType, String branch, int accountLinkedId){
        //TODO: handle interest rates

        boolean success = false;
        try{
            //int accountId = (int)(Math.random() * ((Integer.MAX_VALUE) + 1))*(-1);
            float balance = 0.0f;
            float interestRate = 0.0f;
            if(accountType.equals("checking")){
                interestRate = 5.5f/12.0f;
            }
            else if(accountType.equals("savings")){
                interestRate = 7.5f/12.0f;
            }
            else{
                interestRate = 0.0f;
            }

            String transactionHistory = new Gson().toJson(new ArrayList<String>());
            String insertAccount;

            //insert into Accounts
            if (accountType=="pocket"){
                String getAccount = "SELECT * FROM Accounts WHERE accountId = " + accountLinkedId;
                rs = stmt.executeQuery(getAccount);
                if(!rs.next()){
                    System.out.println("The account you are trying to link to does not exist");
                    return false;
                }

                if(getAcctType(accountLinkedId).equals("pocket")){
                    System.out.println("The account you are trying to link to is a Pocket Account");
                    return false;
                }
                insertAccount = "INSERT INTO Accounts (accountId, balance, branch, interestRate, transactionHistory, accountType, accountLinkedId, isClosed) " +
                        "VALUES (" + accountId + "," + balance + ", '" + branch + "', " + interestRate + ", '" + transactionHistory + "', '" + accountType + "', '" + accountLinkedId + "', " + 1 +")";

                String updateAccount = "UPDATE Accounts SET accountLinkedId = " + accountId + " WHERE accountID = " + accountLinkedId;
                stmt.executeUpdate(updateAccount);
            }
            else {
                insertAccount = "INSERT INTO Accounts (accountId, balance, branch, interestRate, transactionHistory, accountType, isClosed) " +
                        "VALUES (" + accountId + "," + balance + ", '" + branch + "', " + interestRate + ", '" + transactionHistory + "', '" + accountType + "', " + 1 +")";
            }
            stmt.executeUpdate(insertAccount);

            //insert into Owns
            String insertOwns = "INSERT INTO PrimarilyOwns (accountId, taxId) " +
                    "VALUES(" + accountId + ", " + taxId + ")";
            stmt.executeUpdate(insertOwns);

            //initialize new Account
            //customer.addAccount(accountId, balance, branch, interestRate, accountType, 1);

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

    public boolean addCoOwner(int taxId, int accountId){
        boolean success = false;
        try{
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
        if (isAcctClosed(accountId)) {
            //if account closed then no transactions allowed
            System.out.println("You cannot make this transaction because Account " + accountId + " is Closed");
            return -1.0f;
        }



        //TODO: Add timestamp
        //check is account belongs to current customer

        String accountType = getAcctType(accountId);
        if(accountType.equals("")) return -1.0f;


        float oldBalance = 0.0f;

        boolean isCustomer = isCustomerAcct(accountId);

        if(isCustomer){
            oldBalance = customer.accounts.get(accountId).balance;
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
        if (newBalance <= 0){
            closeAcct(accountId);
            System.out.println("Balance of Account " + accountId + " fell below 0 and is now closed");
            return newBalance;
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
            customer.accounts.get(accountId).balance = newBalance;
        }



        //add transaction
        if(createTransaction && isCustomer){
            customer.accounts.get(accountId).addTransaction(customer.name, transactionType, "", amount, accountId, recieverAcctId);
        }
        else if(createTransaction){
            String getTransactionHistory = "SELECT transactionHistory FROM Accounts WHERE accountId = " + accountId;
            try {
                List<String> th = new ArrayList<>();
                rs = stmt.executeQuery(getTransactionHistory);
                while(rs.next()){
                    String thAsString = rs.getString("transactionHistory");
                    th = new Gson().fromJson(thAsString, new ArrayList<String>().getClass());
                }

                th.add("" + " " + transactionType + " " + new SimpleDateFormat("dd-MM-yyyy").format(new Date().getTime()) + " " + amount + " " + accountId + " " + recieverAcctId);

                String json = new Gson().toJson(th);
                String deleteTrans = "UPDATE Accounts SET transactionHistory = '" + json + "' WHERE accountId = " + accountId;
                stmt.executeUpdate(deleteTrans);
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return newBalance;

    }
    public float deposit(int accountId, float amount, boolean createTransaction, int senderAcctId, String transactionType){

        if (isAcctClosed(accountId)) {
            //if account closed then no transactions allowed
            System.out.println("You cannot make this transaction because Account " + accountId + " is Closed");
            return -1.0f;
        }

        String accountType = getAcctType(accountId);
        if(accountType.equals("")) return -1.0f;


        float oldBalance = 0.0f;

        boolean isCustomer = isCustomerAcct(accountId);

        if(isCustomer){
            oldBalance = customer.accounts.get(accountId).balance;
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

        //add transaction
        if(createTransaction && isCustomer){
            customer.accounts.get(accountId).addTransaction(customer.name, transactionType, new SimpleDateFormat("dd-MM-yyyy").format(new Date().getTime()), amount, senderAcctId, accountId);
        }
        else if(createTransaction){
            String getTransactionHistory = "SELECT transactionHistory FROM Accounts WHERE accountId = " + accountId;
            try {
                List<String> th = new ArrayList<>();
                rs = stmt.executeQuery(getTransactionHistory);
                while(rs.next()){
                    String thAsString = rs.getString("transactionHistory");
                    th = new Gson().fromJson(thAsString, new ArrayList<String>().getClass());
                }

                th.add("" + " " + transactionType + " " + new SimpleDateFormat("dd-MM-yyyy").format(new Date().getTime()) + " " + amount + " " + senderAcctId + " " + accountId);

                String json = new Gson().toJson(th);
                String deleteTrans = "UPDATE Accounts SET transactionHistory = '" + json + "' WHERE accountId = " + accountId;
                stmt.executeUpdate(deleteTrans);
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //update balance in class
        if(isCustomer) {
            customer.accounts.get(accountId).balance = newBalance;
        }

        return newBalance;
    }

    public boolean transfer(int senderAcctId, int recieverAcctId, float amount){
        if (isAcctClosed(senderAcctId)) {
            //if account closed then no transactions allowed
            System.out.println("You cannot make this transaction because Account " + senderAcctId + " is Closed");
            return false;
        }

        if (isAcctClosed(recieverAcctId)) {
            //if account closed then no transactions allowed
            System.out.println("You cannot make this transaction because Account " + recieverAcctId + " is Closed");
            return false;
        }

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
        deposit(recieverAcctId, amount, true, senderAcctId, "Transfer");
        return success;
    }
    public boolean collect(int acctId, float amount){
        if (isAcctClosed(acctId)) {
            //if account closed then no transactions allowed
            System.out.println("You cannot make this transaction because Account " + acctId + " is Closed");
            return false;
        }

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
        deposit(recieverId, amount, true, acctId, "Collect");

        return true;
    }
    public boolean wire(int senderAcctId, int recieverAcctId, float amount){
        if (isAcctClosed(senderAcctId)) {
            //if account closed then no transactions allowed
            System.out.println("You cannot make this transaction because Account " + senderAcctId + " is Closed");
            return false;
        }
        if (isAcctClosed(recieverAcctId)) {
            //if account closed then no transactions allowed
            System.out.println("You cannot make this transaction because Account " + recieverAcctId+ " is Closed");
            return false;
        }
        boolean success = false;

        if(isCustomerAcct(senderAcctId)) {
            System.out.println("Customer does not own sender account");
            return false;
        }

        withdraw(senderAcctId, amount + (amount * 0.02f), true, recieverAcctId, "Wire");
        deposit(recieverAcctId, amount, true, senderAcctId, "Wire");

        return success;
    }
    public boolean payFriend(int senderAcctId, int recieverAcctId, float amount){
        if (isAcctClosed(senderAcctId)) {
            //if account closed then no transactions allowed
            System.out.println("You cannot make this transaction because Account " + senderAcctId + " is Closed");
            return false;
        }

        if (isAcctClosed(recieverAcctId)) {
            //if account closed then no transactions allowed
            System.out.println("You cannot make this transaction because Account " + recieverAcctId + " is Closed");
            return false;
        }

        if(!isCustomerAcct(senderAcctId)){
            System.out.println("This account does not belong to current customer");
            return false;
        }
        if(isPocket(senderAcctId) && isPocket(recieverAcctId)){
            System.out.println("One of these accounts is not a pocket account");
            return false;
        }

        withdraw(senderAcctId, amount, true, recieverAcctId, "Pay-Friend");
        deposit(recieverAcctId, amount, true, senderAcctId, "Pay-Friend");

        return true;
    }

    public List<String> customerReport(int taxId){
        List<String> report = new ArrayList<String>();
        try{
            //get co owned customer accounts
            String accountIdQuery = "SELECT accountId FROM Owns WHERE taxId = " + customer.taxId;
            rs = stmt.executeQuery(accountIdQuery);
            while(rs.next()){
                int accountId = rs.getInt("accountId");
                String accountQuery = "SELECT * FROM Accounts WHERE accountId = " + accountId;
                ResultSet acctRS = stmt.executeQuery(accountQuery);

                while(acctRS.next()){
                    int acctId = acctRS.getInt("accountId");
                    int isClosed = acctRS.getInt("isClosed");

                    String closed = isClosed == 1 ? "Not Closed" : "Closed";
                    String r = "Account " + accountId + "is " + closed;
                    report.add(r);
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
                    int isClosed = acctRS.getInt("isClosed");

                    String closed = isClosed == 1 ? "Not Closed" : "Closed";
                    String r = "Account " + accountId + "is " + closed;
                    report.add(r);
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return report;
    }

    public boolean removeClosedAccts(){
        boolean success = false;

        try {
            //String deleteClosed = "DELETE FROM Accounts WHERE isClosed = 0";
            //stmt.executeUpdate(deleteClosed);
            String getAccountIds = "SELECT accountId FROM Accounts WHERE isClosed = 0";
            rs = stmt.executeQuery(getAccountIds);
            while(rs.next()){
                int acctId = rs.getInt("accountId");

                String deleteFromOwns = "DELETE FROM Owns WHERE accountId = " + acctId;
                stmt.executeUpdate(deleteFromOwns);

                String deleteFromPrimarilyOwns = "DELETE FROM PrimarilyOwns WHERE accountId = " + acctId;
                stmt.executeUpdate(deleteFromPrimarilyOwns);
            }

            String deleteClosed = "DELETE FROM Accounts WHERE isClosed = 0";
            stmt.executeUpdate(deleteClosed);

            success = true;
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return success;
    }
    public boolean removeCustomersWithNoAccounts(){
        boolean success = false;

        try{
            String getTaxIds = "SELECT taxId FROM Customers";
            rs = stmt.executeQuery(getTaxIds);
            while(rs.next()){
                int taxId = rs.getInt("taxId");

                String checkOwns = "SELECT * FROM Owns WHERE taxId = " + taxId;
                ResultSet ownsRS = stmt.executeQuery(checkOwns);

                String checkPOwns = "SELECT * FROM PrimarilyOwns WHERE taxId = " + taxId;
                ResultSet pOwnsRS = stmt.executeQuery(checkPOwns);

                if(!ownsRS.next() && !pOwnsRS.next()){
                    String deleteCustomer = "DELETE FROM Customers WHERE taxId = " + taxId;
                    stmt.executeUpdate(deleteCustomer);
                }
            }
            success = true;
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return success;
    }
    public boolean removeTransactions(){
        boolean success = false;

        try{
            String transactionHistory = new Gson().toJson(new ArrayList<String>());
            String getAcctIds = "SELECT accountId FROM Accounts";
            rs = stmt.executeQuery(getAcctIds);
            while(rs.next()){
                int accountId = rs.getInt("accountId");
                String deleteTrans = "UPDATE Accounts SET transactionHistory = '" + transactionHistory + "' WHERE accountId = " + accountId;
                stmt.executeUpdate(deleteTrans);
            }
            success = true;

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return success;
    }

    public void closeAcct(int acctId){
        try{
            String closeQuery = "UPDATE Accounts SET isClosed = 0 WHERE accountId = " + acctId;
            stmt.executeUpdate(closeQuery);
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        catch(Exception e){
            //Handle errors for Class.forName
            System.out.println(e.getMessage());
        }

        if(isCustomerAcct(acctId)){
            customer.accounts.get(acctId).isClosed = 0;
        }
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
        for(Map.Entry<Integer, Customers.Account> e : customer.accounts.entrySet()){
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

    private boolean isAcctClosed(int acctId){
        if(isCustomerAcct(acctId)){
            for(Map.Entry<Integer, Customers.Account> e : customer.accounts.entrySet()){
                if(e.getValue().isClosed == 0){
                    return true;
                }
            }
        }
        else{
            try{
                String isClosedQuery = "SELECT isClosed FROM Accounts WHERE accountId = " + acctId;
                rs = stmt.executeQuery(isClosedQuery);
                while(rs.next()){
                    if(rs.getInt("isClosed") == 0){
                        return true;
                    }
                }
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
            catch(Exception e){
                //Handle errors for Class.forName
                System.out.println(e.getMessage());
            }

        }

        return false;
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
