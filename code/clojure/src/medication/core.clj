(ns medication.core)

(defrecord Tablet [morning midday evening])
(defrecord Infusion [speed duration])

(defn format-dosage
  [dosage]
  (cond
    (instance? Tablet dosage)
    (str (:morning dosage) "-" (:midday dosage) "-" (:evening dosage))
    (instance? Infusion dosage)
    (str (:speed dosage) "ml/min for " (:duration dosage) "h")))

