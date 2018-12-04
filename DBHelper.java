package edu.ucsb.cs.JonathanSegoviaArturoMilanes;

import java.sql.*;



public class DBHelper {

    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private Customers customer;

    public boolean logIn(String name, int pin){

        boolean success = false;

        try{
            System.out.println("Start of Try");
            String customerQuery = "SELECT * from Accounts WHERE name = /'" + name + "'/";
            //if customer doesn't exist create one in db
            boolean queryIsSuccess = !stmt.execute(customerQuery);
            System.out.println(queryIsSuccess);
            if(queryIsSuccess){
                int taxId = (int) Math.random();
                String address = "123 blah blah wy";
                //initialize customer
                customer = new Customers(taxId, name, address, pin);

                //Insert into db
                String insertCustomer = "INSERT INTO Customers (taxId, name, address, pinNumber) " +
                                        "VALUES (" + taxId + ", /'" + name + "/', /'" + address +"/'," + pin + ")";
                System.out.println(stmt.executeUpdate(insertCustomer));
            }
            //if the customer does exist, just initialize customer
            else{
                rs = stmt.executeQuery(customerQuery);
                //get taxid and address
                int taxId = rs.getInt("taxId");
                String address = rs.getString("address");
                customer = new Customers(taxId, name, address, pin);
                System.out.println(customer.pinNumber);
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
        finally {
            //finally block used to close resources
            try {
                if (stmt != null)
                    conn.close();
            } catch (SQLException se) {
            }// do nothing
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

        return success;
    }

    public void createNewAccount(String accountType){}
    public void withdraw(int amount){
        if(customer.isEmpty){
            //do something
        }


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
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    conn.close();
            }catch(SQLException se){
            }// do nothing
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try
    }
}
