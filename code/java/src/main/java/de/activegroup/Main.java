package de.activegroup;

import de.activegroup.Dosage.Infusion;
import de.activegroup.Dosage.Tablet;

public class Main {

    static String formatDosage(Dosage d) {
        return switch (d) {
            case Tablet(int morning, int midday, int evening) ->
                    morning + "-" + midday + "-" + evening;
            case Infusion(double speed, int duration) ->
                    speed + "ml/min for " + duration + "h";
        };
    }

    static String formatMedication(Medication m) {
        return m.drugName() + ": " + formatDosage(m.dosage());
    }

    public static void main(String[] args) {
        var paracetamol = new Medication("Paracetamol",
                new Dosage.Tablet(1,0,2));
        var infliximab = new Medication("Infliximab",
                new Dosage.Infusion(1.5, 2));

        System.out.println(formatMedication(paracetamol));
        System.out.println(formatMedication(infliximab));
    }
}
