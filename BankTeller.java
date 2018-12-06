package edu.ucsb.cs.JonathanSegoviaArturoMilanes;

import java.util.List;
import java.util.Scanner;

public class BankTeller {
    public void bankTeller(DBHelper dbHelper, Scanner input){
        while(true) {
            System.out.println("Please Select One of the following options");
            System.out.println("1 - Enter Check Transaction");
            System.out.println("2 - Generate Monthly Statement");
            System.out.println("3 - List Closed Accounts");
            System.out.println("4 - Generate Drug and TaxEvasion Report");
            System.out.println("5 - Customer Report");
            System.out.println("6 - Add Interest");
            System.out.println("7 - Create Account");
            System.out.println("8 - Delete Closed Accounts and Customers");
            System.out.println("9 - Delete Transactions");
            System.out.println("10 - Exit Bank Teller");

            switch (input.nextInt()) {
                case 1:
                    checkTransaction(dbHelper, input);
                    break;

                case 2:
                    generateMonthlyStatement(dbHelper, input);
                    break;
                case 3:
                    listClosedAccounts(dbHelper, input);
                    break;
                case 4:
                    generateDrugAndTaxReport(dbHelper, input);
                    break;
                case 5:
                    customerReport(dbHelper, input);
                    break;
                case 6:
                    addInterest(dbHelper, input);
                    break;
                case 7:
                    createAccount(dbHelper, input);
                    break;
                case 8:
                    deleteClosedAccountsAndCustomers(dbHelper, input);
                    break;
                case 9:
                    deleteTransations(dbHelper, input);
                    break;
                case 10:
                    return;

            }
        }
    }

    private void checkTransaction(DBHelper dbHelper, Scanner input){}
    private void generateMonthlyStatement(DBHelper dbHelper, Scanner input){}
    private void listClosedAccounts(DBHelper dbHelper, Scanner input){}
    private void generateDrugAndTaxReport(DBHelper dbHelper, Scanner input){}
    private void customerReport(DBHelper dbHelper, Scanner input){
        int taxId;

        System.out.println("Please Enter the Tax ID for the Customer");
        taxId = input.nextInt();

        List<String> report = dbHelper.customerReport(taxId);

        if(report.isEmpty()){
            System.out.println("This Customer has no Accounts");
            return;
        }

        for(String r : report){
            System.out.println(r);
        }
    }
    private void addInterest(DBHelper dbHelper, Scanner input){}
    private void createAccount(DBHelper dbHelper, Scanner input){
        int accountId;
        int taxId;
        String accountType = "";
        String branch = "";
        int linkId = -1;


        System.out.println("Please Enter the Accoount ID");
        accountId = input.nextInt();
        System.out.println("Please Enter the Tax ID for the Customer");
        taxId = input.nextInt();
        System.out.println("Please Enter the Account Type");
        System.out.println("1 - Checking");
        System.out.println("2 - Savings");
        System.out.println("3 - Pocket");
        switch(input.nextInt()){
            case 1:
                accountType = "checking";
                break;
            case 2:
                accountType = "savings";
                break;
            case 3:
                accountType = "pocket";
                break;
        }

        if(accountType.equals("pocket")){
            System.out.println("Please Enter the Account Id for the account you want to link your Pocket account to");
            linkId = input.nextInt();
        }

        if(dbHelper.createNewAccount(accountId, taxId, accountType, branch, linkId)){
            System.out.println("New Account Created Successfully");
        }
    }
    private void deleteClosedAccountsAndCustomers(DBHelper dbHelper, Scanner input){
        if(dbHelper.removeClosedAccts() && dbHelper.removeCustomersWithNoAccounts()){
            System.out.println("Closed Accounts and Customers with no Accounts removed");
        }
        else{
            System.out.println("There was an error");
        }

    }
    private void deleteTransations(DBHelper dbHelper, Scanner input){
        if(dbHelper.removeTransactions()){
            System.out.println("Transactions Removed");
        }
        else{
            System.out.println("There was an error");
        }
    }
}
