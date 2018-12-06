package edu.ucsb.cs.JonathanSegoviaArturoMilanes;

import java.util.Scanner;

public class ATM {
    public void atm(DBHelper dbHelper, Scanner input){
        System.out.println("Would you like to login or create a new account?");
        System.out.println("1 - Login");
        System.out.println("2 - Create New Customer");
        switch (input.nextInt()){
            case 1:
                login(dbHelper, input);
                break;
            case 2:
                createNewCustomer(dbHelper, input);
                break;
        }
        while(true) {
            System.out.println("Please Select One of the following options");
            System.out.println("1 - Deposit");
            System.out.println("2 - Top Up");
            System.out.println("3 - Withdrawal");
            System.out.println("4 - Purchase");
            System.out.println("5 - Transfer");
            System.out.println("6 - Collect");
            System.out.println("7 - Wire");
            System.out.println("8 - Pay Friend");
            System.out.println("9 - Exit ATM");


            switch (input.nextInt()) {
                case 1:
                    deposit(dbHelper, input);
                    break;

                case 2:
                    topUp(dbHelper, input);
                    break;
                case 3:
                    withdraw(dbHelper, input);
                    break;
                case 4:
                    purchase(dbHelper, input);
                    break;
                case 5:
                    transfer(dbHelper, input);
                    break;
                case 6:
                    collect(dbHelper, input);
                    break;
                case 7:
                    wire(dbHelper, input);
                    break;
                case 8:
                    payFriend(dbHelper, input);
                    break;
                case 9:
                    return;


            }
        }
    }
    private void login(DBHelper dbHelper, Scanner input){
        System.out.println("Please Enter Your Pin");
        if(dbHelper.logIn(input.nextInt())){
            System.out.println("Logged In");
        }
        else{
            System.out.println("Error on Log In");
        }
    }
    private void createNewCustomer(DBHelper dbHelper, Scanner input){
        String name;
        String address;
        int pin;
        int taxId;
        input.nextLine(); //fixes skipping
        System.out.println("Please Enter Your Name");
        name = input.nextLine();
        System.out.println("Please Enter Your Address");
        address = input.nextLine();
        System.out.println("Please Enter Your Desired PIN");
        pin = input.nextInt();
        System.out.println("Please Enter Your Tax Id");
        taxId = input.nextInt();


        if(dbHelper.createNewCustomer(name, address, pin, taxId)){
            if(dbHelper.logIn(pin)){
                System.out.println("Customer Created");
            }
            else{
                System.out.println("Error on Log In After Creation");
            }
        }
        else{
            System.out.println("Error on Customer Creation");
        }
    }

    private void deposit(DBHelper dbHelper, Scanner input){
        int accountId;
        float amount;
        System.out.println("Please Enter the Account ID that you would like to deposit to");
        accountId = input.nextInt();
        System.out.println("Please Enter the amount you would like to deposit");
        amount = input.nextFloat();


        float result = dbHelper.deposit(accountId, amount, true, -1, "Deposit");
        System.out.println("The New Balance in Account " + accountId + " is: " + result);
    }
    private void topUp(DBHelper dbHelper, Scanner input){
        int accountId;
        float amount;
        System.out.println("Please Enter the Account ID that you would like to top-up from");
        accountId = input.nextInt();
        System.out.println("Please Enter the amount you would like to top-up");
        amount = input.nextFloat();

         dbHelper.topUp(accountId, amount);
         }
    private void withdraw(DBHelper dbHelper, Scanner input){
        int accountId;
        float amount;
        System.out.println("Please Enter the Account ID that you would like to withdraw from");
        accountId = input.nextInt();
        System.out.println("Please Enter the amount you would like to withdraw");
        amount = input.nextFloat();


        float result = dbHelper.withdraw(accountId, amount, true, -1, "Withdraw");
        System.out.println("The New Balance in Account " + accountId + " is: " + result);
    }
    private void purchase(DBHelper dbHelper, Scanner input){
        int accountId;
        float amount;
        System.out.println("Please Enter the Account ID that you would like to purchase from");
        accountId = input.nextInt();
        System.out.println("Please Enter the amount you would like to purchase");
        amount = input.nextFloat();

        dbHelper.purchase(accountId, amount);
    }
    private void transfer(DBHelper dbHelper, Scanner input){
        int senderAcctId;
        int receiverAcctId;
        float amount;

        System.out.println("Please Enter the Account ID that you would like to transfer from");
        senderAcctId = input.nextInt();
        System.out.println("Please Enter the Account ID that you would like to transfer to");
        receiverAcctId = input.nextInt();
        System.out.println("Please Enter the amount you'd like to transfer");
        amount = input.nextFloat();

        if(dbHelper.transfer(senderAcctId, receiverAcctId, amount)){
            System.out.println("Transfer Successful");
        }
    }
    private void collect(DBHelper dbHelper, Scanner input){
        int accountId;
        float amount;

        System.out.println("Please Enter the Account ID of the Pocket Account you'd like to collect from");
        accountId = input.nextInt();

        System.out.println("Please Enter the amount you'd like to collect");
        amount = input.nextFloat();

        dbHelper.collect(accountId, amount);
    }
    private void wire(DBHelper dbHelper, Scanner input){
        int senderAcctId;
        int receiverAcctId;
        float amount;

        System.out.println("Please Enter the Account ID that you would like to wire from");
        senderAcctId = input.nextInt();
        System.out.println("Please Enter the Account ID that you would like to wire to");
        receiverAcctId = input.nextInt();
        System.out.println("Please Enter the amount you'd like to wire");
        amount = input.nextFloat();
        dbHelper.wire(senderAcctId, receiverAcctId, amount);
    }
    private void payFriend(DBHelper dbHelper, Scanner input){
        int senderAcctId;
        int receiverAcctId;
        float amount;

        System.out.println("Please Enter the Account ID that you would like to pay from");
        senderAcctId = input.nextInt();
        System.out.println("Please Enter your friend's Account ID that you would like to send to");
        receiverAcctId = input.nextInt();
        System.out.println("Please Enter the amount you'd like to pay");
        amount = input.nextFloat();
        dbHelper.payFriend(senderAcctId, receiverAcctId, amount);
    }

}
