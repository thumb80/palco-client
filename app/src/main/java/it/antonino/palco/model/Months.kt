package it.antonino.palco.model

enum class Months(val monthNum: String) {

    Gennaio("01"),
    Febbraio("02"),
    Marzo("03"),
    Aprile("04"),
    Maggio("05"),
    Giugno("06"),
    Luglio("07"),
    Agosto("08"),
    Settembre("09"),
    Ottobre("10"),
    Novembre("11"),
    Dicembre("12");

    companion object {
        fun from(findValue: String): Months = Months.values().first { it.monthNum == findValue }
        fun getMonthNum(monthNum: String) : String {
            return Months.valueOf(monthNum).monthNum
        }
    }

}
