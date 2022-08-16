package cinema

class Cinema(
    private val rows: Int,
    private val seatsPerRow: Int,
) {
    private val SMALL_CINEMA_SEATS = 60
    private val SMALL_CINEMA_TICKET_PRICE = 10
    private val BIG_CINEMA_FRONT_ROW_TICKET_PRICE = 10
    private val BIG_CINEMA_BACK_ROW_TICKET_PRICE = 8
    private val TICKET_SOLD = "B"
    private val TICKET_AVAILABLE = "S"

    private val totalSeats = rows * seatsPerRow
    private val frontRows = rows / 2
    private val backRows = rows - frontRows
    private val tickets = MutableList(rows) { MutableList(seatsPerRow) { TICKET_AVAILABLE } }

    fun execute() {
        fun readMenuChoice(): Int =
            try {
                readln().toInt()
            } catch (e: NumberFormatException) {
                println()
                println("Invalid command, please enter a valid option")
                readMenuChoice()
            }

        while (true) {
            printMenu()
            when (readMenuChoice()) {
                1 -> showSeats()
                2 -> buyTicketPrompt()
                3 -> showStatistics()
                else -> return
            }
        }
    }

    private fun printMenu() {
        println()
        println("1. Show the seats")
        println("2. Buy a ticket")
        println("3. Statistics")
        println("0. Exit")
    }

    private fun showSeats() {
        fun printTableColumnHeader() {
            print("  ")
            (1..seatsPerRow).forEach() { print("$it ") }
        }

        fun printTableBody() {
            println()
            tickets.forEachIndexed { index, row ->
                print("${index + 1} ")
                print(row.joinToString(" "))
                println()
            }
        }

        println()
        println("Cinema:")
        printTableColumnHeader()
        printTableBody()
    }

    private fun buyTicketPrompt() {
        val (selectedRow, selectedSeat) = requestSeat()
        try {
            bookSeat(selectedRow, selectedSeat)
            val ticketPrice = getTicketPrice(selectedRow)
            printTicketPrice(ticketPrice)
        } catch (e: Exception) {
            println()
            println(
                if (e.message == "ticket already taken") "That ticket has already been purchased!"
                else "Wrong input!"
            )
            buyTicketPrompt()
        }
    }

    private fun requestSeat(): List<Int> =
        try {
            println()
            print("Enter a row number: ")
            val selectedRow = readln().toInt()
            if (selectedRow < 0) throw Exception("negative rows")
            print("Enter a seat number in that row: ")
            val selectedSeat = readln().toInt()
            if (selectedSeat < 0) throw Exception("negative seats")
            listOf(selectedRow, selectedSeat)
        } catch (e: NumberFormatException) {
            println()
            println("Invalid number, please enter a valid number")
            requestSeat()
        } catch (e: Exception) {
            println()
            println("Rows and seats must be greater than 0")
            requestSeat()
        }

    private fun getTicketPrice(selectedRow: Int): Int = when {
        totalSeats <= SMALL_CINEMA_SEATS -> SMALL_CINEMA_TICKET_PRICE
        selectedRow <= frontRows -> BIG_CINEMA_FRONT_ROW_TICKET_PRICE
        else -> BIG_CINEMA_BACK_ROW_TICKET_PRICE
    }

    private fun printTicketPrice(ticketPrice: Int) {
        println()
        println("Ticket price: \$$ticketPrice")
    }

    private fun bookSeat(selectedRow: Int, selectedSeat: Int) {
        fun isValidSeat(): Boolean = selectedRow in 0..rows && selectedSeat in 0..seatsPerRow
        fun isTicketSold(): Boolean = tickets[selectedRow - 1][selectedSeat - 1] == TICKET_SOLD
        fun reserveSeat() {
            tickets[selectedRow - 1][selectedSeat - 1] = TICKET_SOLD
        }

        if (!isValidSeat()) throw Exception("out of index")
        if (isTicketSold()) throw Exception("ticket already taken")
        else reserveSeat()
    }

    private fun showStatistics() {
        println()
        println("Number of purchased tickets: ${getPurchasedTicketQuantity().first()}")
        println("Percentage: ${getPurchasedTicketQuantityAsPercentage()}%")
        println("Current income: \$${getCurrentIncome()}")
        println("Total income: \$${getTotalIncome()}")
    }

    /**
     * @return a list containing total tickets sold, front rows tickets sold and back rows tickets sold
     */
    private fun getPurchasedTicketQuantity(): List<Int> {
        var frontTicketsSold = 0
        var backTicketsSold = 0
        tickets.forEachIndexed { rowIndex, row ->
            row.forEach { seat ->
                if (seat == TICKET_SOLD) if (rowIndex < frontRows) frontTicketsSold++ else backTicketsSold++
            }
        }
        return listOf(frontTicketsSold + backTicketsSold, frontTicketsSold, backTicketsSold)
    }

    private fun getPurchasedTicketQuantityAsPercentage(): String {
        val percentage = getPurchasedTicketQuantity().first().toDouble() * 100 / totalSeats
        return "%.2f".format(percentage)
    }

    private fun getCurrentIncome(): Int =
        if (totalSeats <= SMALL_CINEMA_SEATS) getPurchasedTicketQuantity().first() * SMALL_CINEMA_TICKET_PRICE
        else getPurchasedTicketQuantity()[1] * BIG_CINEMA_FRONT_ROW_TICKET_PRICE +
                getPurchasedTicketQuantity()[2] * BIG_CINEMA_BACK_ROW_TICKET_PRICE

    private fun getTotalIncome(): Int =
        if (totalSeats <= SMALL_CINEMA_SEATS) totalSeats * SMALL_CINEMA_TICKET_PRICE
        else {
            seatsPerRow * (frontRows * BIG_CINEMA_FRONT_ROW_TICKET_PRICE + backRows * BIG_CINEMA_BACK_ROW_TICKET_PRICE)
        }
}

/* inspired by hugo sun's solution */
fun main() {
    fun readCinemaSize(): List<Int> =
        try {
            print("Enter the number of rows: ")
            val rows = readln().toInt()
            if (rows < 0) throw Exception("negative rows")
            print("Enter the number of seats in each row: ")
            val seatsPerRow = readln().toInt()
            if (seatsPerRow < 0) throw Exception("negative seats")
            listOf(rows, seatsPerRow)
        } catch (e: NumberFormatException) {
            println()
            println("Invalid number, please enter a valid number")
            readCinemaSize()
        } catch (e: Exception) {
            println()
            println("Rows and seats must be greater than 0")
            readCinemaSize()
        }

    val (rows, seatsPerRow) = readCinemaSize()
    val cinema = Cinema(rows, seatsPerRow)
    cinema.execute()
}
