from dataclasses import dataclass

@dataclass
class TabletDosage:
    morning: int
    midday: int
    evening: int

@dataclass
class InfusionDosage:
    speed: float
    duration: int

# Requires python 3.12
type Dosage = TabletDosage | InfusionDosage

@dataclass
class Medication:
    drugName: str
    dosage: Dosage

def format(m: Medication) -> str:
    return f'{m.drugName}: {formatDosage(m.dosage)}'

def formatDosage(d: Dosage) -> str:
    match d:
        case TabletDosage():
            return f'{d.morning}-{d.midday}-{d.evening}'
        case InfusionDosage():
            return f'{d.speed} ml/min for {d.duration}h'

paracetamol = Medication("Paracetamol", TabletDosage(1,0,2))
infliximab = Medication("Infliximab", InfusionDosage(1.5, 2))
print(format(paracetamol))
print(format(infliximab))
