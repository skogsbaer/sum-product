enum Dosage {
  case Tablet(morning: Int, midday: Int, evening: Int)
  case Infusion(speed: Double, duration: Int)

  def format =
    this match {
      case Tablet(morning, midday, evening) =>
        morning + midday + evening
      case Infusion(speed, duration) =>
        speed + "ml/min for " + duration + "h"
    }
}

case class Medication(drugName: String, dosage: Dosage) {
  def format =
    this.drugName + this.dosage.format
}
