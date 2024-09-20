package de.activegroup;

public sealed interface Dosage {

    String format2();
    record Tablet(int morning, int midday, int evening)
            implements Dosage {
        @Override
        public String format2() {
            return morning + "-" + midday + "-" + evening;
        }

    }
    record Infusion(double speed, int duration)
            implements Dosage {
        @Override
        public String format2() {
            return speed + "ml/min for " + duration + "h";
        }

    }

}


