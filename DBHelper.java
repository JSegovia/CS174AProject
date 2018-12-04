package edu.ucsb.cs.JonathanSegoviaArturoMilanes;

import java.sql.*;



public class DBHelper {

    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs;
    private Customers customer;

    public void logIn(){
        //if user doesn't exist create one in the db
        //else get user and initialize User
            //query DB for taxId, name, address and pinNumber

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
            //STEP 2: Register JDBC driver
            Class.forName(jdbcDriver);

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DbUrl, username, password);
            System.out.println("Connected database successfully...");

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();

            String sql = "SELECT cid, cname, city, discount FROM cs174.Customers";
            rs = stmt.executeQuery(sql);
            //STEP 5: Extract data from result set
            while(rs.next()){
                //Retrieve by column name
                String cid  = rs.getString("cid");
                String cname = rs.getString("cname");
                String city = rs.getString("city");
                double discount = rs.getDouble("discount");

                //Display values
                System.out.print("cid: " + cid);
                System.out.print(", cname: " + cname);
                System.out.print(", city: " + city);
                System.out.println(", discount: " + discount);
            }
            rs.close();
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
