object Main extends App {
  val paracetamol = Medication("Paracetamol", Dosage.Tablet(1,0,2))
  val infliximab = Medication("Infliximab", Dosage.Infusion(1.5, 2))
  println(paracetamol.format)
  println(infliximab.format)
}
