import kotlinx.serialization.Serializable

@Serializable
data class Card(
    val cardNumber: Int,
    val pinCode: Int,
    val amountOfMoney: Int,
    val transaction: MutableList<Transaction>
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Card

        return cardNumber == other.cardNumber
    }

    override fun hashCode(): Int {
        return cardNumber
    }

    override fun toString(): String {
        return "Card(cardNumber=$cardNumber)"
    }
}

@Serializable
data class Transaction(
    val amount:Int,
    val type: String
)

enum class Type(name:String){
    SEND("Переказ"),
    GET("Отримання")
}