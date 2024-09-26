struct Medication {
    drug_name: String,
    dosage: Dosage
}

enum Dosage {
    Tablet { morning: i32, midday: i32, evening: i32 },
    Infusion { speed: f32, duration: i32 }
}

fn format_dosage(dosage: Dosage) -> String {
    match dosage {
        Dosage::Tablet { morning, midday, evening } =>
            format!("{morning}-{midday}-{evening}"),
        Dosage::Infusion { speed, duration } =>
            format!("{speed} ml/min for {duration}h")
    }
}

fn format_medication(m: Medication) -> String {
    format!("{0}: {1}", m.drug_name, format_dosage(m.dosage))
}

fn main() {
    let paracetamol = Medication {
        drug_name: "Paracetamol".into(),
        dosage: Dosage::Tablet { morning: 1, midday: 0, evening: 2 }
    };
    let infliximab = Medication {
        drug_name: "Infliximab".into(),
        dosage: Dosage::Infusion { speed: 1.5, duration: 2 }
    };
    println!("{}", format_medication(paracetamol));
    println!("{}", format_medication(infliximab))
}
