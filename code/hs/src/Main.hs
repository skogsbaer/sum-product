data Dosage
  = TabletDosage { morning :: Int, midday :: Int, evening :: Int }
  | InfusionDosage { speed :: Double, duration :: Int }
 
data Medication = Medication { drugName :: String, dosage :: Dosage }

exampleParacetamol :: Medication
exampleParacetamol =
    Medication {
        drugName = "Paracetamol",
        dosage = TabletDosage { morning = 1, midday = 0, evening = 2}
    }

exampleInfliximab :: Medication
exampleInfliximab =
    Medication {
        drugName = "Infliximab",
        dosage = InfusionDosage { speed = 1.5, duration = 2}
    }

formatMedication :: Medication -> String
formatMedication med =
    drugName med ++ ": " ++ formatDosage (dosage med)

formatDosage :: Dosage -> String
formatDosage d =
    case d of
        TabletDosage morning midday evening -> show morning ++ "-" ++ show midday ++ "-" ++ show evening
        InfusionDosage speed duration -> show speed ++ "ml/min for " ++ show duration ++ "h"

main :: IO ()
main = do
    putStrLn (formatMedication exampleParacetamol)
    putStrLn (formatMedication exampleInfliximab)

{-
Output:

Paracetamol: 1-0-2
Infliximab: 1.5ml/min for 2h

-}
