type Medication = {
    drugName: string
    dosage: Dosage
}

type Dosage = {
    kind: "tablet",
    morning: number,
    midday: number,
    evening: number
} | {
    kind: "infusion",
    speed: number,
    duration: number
}

function format(m: Medication) {
    let d: string;
    switch (m.dosage.kind) {
        case "tablet":
            d = m.dosage.morning + "-" + m.dosage.midday + "-" + m.dosage.evening
            break
        case "infusion":
            d = m.dosage.speed + " ml/min for" + m.dosage.duration + "h"
            break
    }
    return m.drugName + ": " + d
}

const paracetamol: Medication = {
    drugName: "Paracetamol",
    dosage: { kind: "tablet", morning: 1, midday: 0, evening: 2 }
}
const infliximab: Medication = {
    drugName: "Infliximab",
    dosage: { kind: "infusion", speed: 1.5, duration: 2 }
}
console.log(format(paracetamol))
console.log(format(infliximab))
