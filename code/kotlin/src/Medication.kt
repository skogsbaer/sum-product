data class Medication(val drugName: String, val dosage: Dosage) {
    fun format(): String =
        "${this.drugName}: ${this.dosage.format()}"
}
