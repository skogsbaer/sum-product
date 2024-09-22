public record Dosage {
    public record Tablet(int morning, int midday, int evening) : Dosage();
    public record Infusion(double speed, int duration) : Dosage();

    public string format() {
        return this switch {
            Tablet t => t.morning + "-" + t.midday + "-" + t.evening,
            Infusion i => i.speed + "ml/min for " + i.duration + "h",
            _ => throw new ApplicationException("unexpected dosage: " + this)
        };
    }

    private Dosage() {} // private constructor can prevent derived cases from being defined elsewhere
}

public record Medication(string drugName, Dosage dosage) {

    public string format() {
        return this.drugName + ": " + this.dosage.format();
    }
}
