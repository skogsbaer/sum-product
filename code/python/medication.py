from typing import Union
from dataclasses import dataclass
import string

@dataclass
class TabletDosage:
    morning: int
    midday: int
    evening: int

@dataclass
class InfusionDosage:
    speed: float
    duration: int

Dosage = TabletDosage | InfusionDosage

@dataclass
class Medication:
    drugName: string
    dosage: Dosage
