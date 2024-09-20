sealed interface Dosage {
    fun format(): String =
        when(this) {
            is Tablet ->
                "$morning-$midday-$evening"
            is Infusion ->
                speed.toString() + "ml/min for " + duration + "h"
        }
    fun format2(): String

    data class Tablet(val morning: Int, val midday: Int, val evening: Int) : Dosage {
        override fun format2(): String {
            return "$morning-$midday-$evening"
        }
    }

    data class Infusion(val speed: Double, val duration: Int) : Dosage {
        override fun format2(): String {
            return speed.toString() + "ml/min for " + duration + "h"
        }
    }
}
