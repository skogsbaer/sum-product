package de.activegroup;

public sealed interface Dosage {
    default String format() {
        return switch (this) {
            case Tablet(int morning, int midday, int evening) ->
                    morning + "-" + midday + "-" + evening;
            case Infusion(double speed, int duration) ->
                    speed + "ml/min for " + duration;
        };
    }

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
            return speed + "ml/min for " + duration;
        }

    }

}


