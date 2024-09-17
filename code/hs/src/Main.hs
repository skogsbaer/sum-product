data TabletDosage = TabletDosage { morning :: Int, midday :: Int, evening :: Int }
data InfusionDosage = InfusionDosage { speed :: Double, duration :: Int }
data Dosage = DosageTablet TabletDosage | DosageInfusion InfusionDosage
data Medication = Medication { drug :: String, dosage :: Dosage }

exampleParacetamol :: Medication
exampleParacetamol =
    Medication {
        drug = "Paracetamol",
        dosage = DosageTablet (TabletDosage { morning = 1, midday = 0, evening = 2})
    }

exampleInfliximab :: Medication
exampleInfliximab =
    Medication {
        drug = "Infliximab",
        dosage = DosageInfusion (InfusionDosage { speed = 1.5, duration = 2})
    }

formatMedication :: Medication -> String
formatMedication med =
    drug med ++ ": " ++ formatDosage (dosage med)

formatDosage :: Dosage -> String
formatDosage d =
    case d of
        DosageTablet dt -> show (morning dt) ++ "-" ++ show (midday dt) ++ "-" ++ show (evening dt)
        DosageInfusion di -> show (speed di) ++ "ml/min for " ++ show (duration di) ++ "h"

main :: IO ()
main = do
    putStrLn (formatMedication exampleParacetamol)
    putStrLn (formatMedication exampleInfliximab)

{-
Output:

Paracetamol: 1-0-2
Infliximab: 1.5ml/min for 2h

-}
