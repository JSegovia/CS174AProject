package edu.ucsb.cs.JonathanSegoviaArturoMilanes;


public class Customers {
    int taxId;
    String name;
    String address;
    int pinNumber;
    boolean isEmpty = true;

    Customers(int taxId, String name, String address, int pinNumber){
        this.taxId = taxId;
        this.name = name;
        this.address = address;
        this.pinNumber = pinNumber;
        isEmpty = false;
    }
}
