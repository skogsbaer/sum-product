package de.activegroup;

public class Main {
    public static void main(String[] args) {
        var paracetamol = new Medication("Paracetamol",
                new Dosage.Tablet(1,0,2));
        var infliximab = new Medication("Infliximab",
                new Dosage.Infusion(1.5, 2));

        System.out.println("Hello world!");
    }
}