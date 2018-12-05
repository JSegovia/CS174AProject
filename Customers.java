package edu.ucsb.cs.JonathanSegoviaArturoMilanes;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Customers {
    int taxId;
    String name;
    String address;
    int pinNumber;
    Map<String, Account> accounts; //Key: AccountType, Value Account

    Customers(int taxId, String name, String address, int pinNumber){
        this.taxId = taxId;
        this.name = name;
        this.address = address;
        this.pinNumber = pinNumber;
        accounts = new HashMap<>();
    }

    public void addAccount(int accountId, float balance, String branch, float interestRate, String accountType){
        Account acct = new Account(accountId, balance, branch, interestRate, accountType);
        accounts.put(accountType, acct);
    }


    public class Account{
        int accountId;
        float balance;
        String branch;
        float interestRate;
        String accountType;
        List<String> transactionHistory;

        Account(int accountId, float balance, String branch, float interestRate, String accountType){
            this.accountId = accountId;
            this.balance = balance;
            this.branch = branch;
            this.interestRate = interestRate;
            this.accountType = accountType;
            //transactionHistory = new ArrayList<>();
        }

        public void addTransaction(String transaction){
            transactionHistory.add(transaction);
        }
        public String transactionHistoryToJson(){
            return new Gson().toJson(transactionHistory);
        }
        public void populateTransactionHistory(String json){
            transactionHistory = new Gson().fromJson(json, new ArrayList<String>().getClass());
        }
    }
}
