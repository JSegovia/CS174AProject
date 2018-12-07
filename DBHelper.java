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
            System.out.println("10 - Add CoOwner");
            System.out.println("11 - Exit Bank Teller");

            int i = input.nextInt();
            input.nextLine();

            switch (i) {
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
                    addCoOwner(dbHelper, input);
                case 11:
                	return;

            }
        }
    }

    
    private void addCoOwner(DBHelper dbHelper, Scanner input){
    	int taxId;
    	int accountId;
    	
    	System.out.println("Please Enter the Tax ID of the Customer");
        taxId = input.nextInt();
        input.nextLine();
        System.out.println("Please Enter the Account ID of the Account");
        accountId = input.nextInt();
        input.nextLine();
    	
    	dbHelper.addCoOwner(taxId, accountId);
    }
    private void checkTransaction(DBHelper dbHelper, Scanner input){
        int accountId;
        float amount;

        System.out.println("Please Enter the Account ID of the Checking Account you'd like to write a check from");
        accountId = input.nextInt();
        input.nextLine();
        System.out.println("Please Enter the amount you'd like to write a check for");
        amount = input.nextFloat();
        input.nextLine();

        if(dbHelper.writeCheck(accountId,amount)){
            System.out.println("Check Transactions Successful");
        }
        else{
            System.out.println("There was an error with the Check Transaction");
        }
    }


    private void generateMonthlyStatement(DBHelper dbHelper, Scanner input){
        int accountId;


        System.out.println("Please Enter the Account ID of  Account you'd like to gms from");
        accountId = input.nextInt();
        input.nextLine();
        if(dbHelper.gms(accountId)){
            System.out.println("Generated Monthly Statement Successful");
        }
        else{
            System.out.println("There was an error with the Generated Monthly Statement");
        }
    }
    private void listClosedAccounts(DBHelper dbHelper, Scanner input){
        if(dbHelper.closedAccList()){
            System.out.println("List of Closed Accounts has been printed Successfully");
        }
        else{
            System.out.println("There was an error generating the list of Closed Accounts");
        }

    }
    private void generateDrugAndTaxReport(DBHelper dbHelper, Scanner input){

        if(dbHelper.dter()){
            System.out.println("List of GDTR has been printed Successfully");
        }
        else{
            System.out.println("There was an error generating the list of GDTR");
        }

    }
    private void customerReport(DBHelper dbHelper, Scanner input){
        int taxId;

        System.out.println("Please Enter the Tax ID for the Customer");
        taxId = input.nextInt();
        input.nextLine();
        List<String> report = dbHelper.customerReport(taxId);

        if(report.isEmpty()){
            System.out.println("This Customer has no Accounts");
            return;
        }

        for(String r : report){
            System.out.println(r);
        }
    }
    private void addInterest(DBHelper dbHelper, Scanner input){
        if(dbHelper.addInterest()){
            System.out.println("Added interest was Successful");
        }
        else{
            System.out.println("There was an error adding interest");
        }
    }
    private void createAccount(DBHelper dbHelper, Scanner input){
        int accountId;
        int taxId;
        String accountType = "";
        String branch = "";
        int linkId = -1;


        System.out.println("Please Enter the Account ID");
        accountId = input.nextInt();
        input.nextLine();
        System.out.println("Please Enter the Tax ID for the Customer");
        taxId = input.nextInt();
        input.nextLine();
        System.out.println("Please Enter the Branch for the Account");
        branch = input.nextLine();
        //input.nextLine();
        System.out.println("Please Enter the Account Type");
        System.out.println("1 - Checking");
        System.out.println("2 - Savings");
        System.out.println("3 - Pocket");
        int i = input.nextInt();
        input.nextLine();
        switch(i){
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
            input.nextLine();
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
