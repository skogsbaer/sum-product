package de.activegroup;

public sealed interface Dosage {
    record Tablet(int morning, int midday, int evening)
            implements Dosage {}
    record Infusion(double speed, int duration)
            implements Dosage {}
}


