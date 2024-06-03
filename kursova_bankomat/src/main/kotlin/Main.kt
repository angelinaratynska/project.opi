import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

fun main() {

    val cardFile = File("C:\\MySpace\\VNTU\\my\\card.txt")

    if (!cardFile.exists())
        cardFile.createNewFile()

    val listCards = mutableListOf<Card>().apply {
        cardFile.readText().let {
            if (it.isNotBlank())
                addAll(Json.decodeFromString<List<Card>>(it))
        }
    }

    enter()
    println("Введіть номер карти")
    enter()
    var card = Card(readln().toInt(), 0, 0, mutableListOf())
    enter()
    if (listCards.contains(card)) {
        card = listCards[listCards.indexOf(card)]
        var flag = true
        var chanse = 3
        while (flag && chanse != 0) {
            println("Введіть пінкод")
            enter()
            if (card.pinCode == 0) {
                card =
                    card.copy(
                        pinCode = readln().toInt(),
                        amountOfMoney = card.amountOfMoney + (1000..15000).random()
                    )
                listCards.remove(card)
                listCards.add(card)
                cardFile.writeText(Json.encodeToString<List<Card>>(listCards))
                enter()
                println("Успіх")
                flag = false
                continue
            }
            if (card.pinCode != readln().toInt()) {
                chanse--
                enter()
                println("Невірно набраний пінкод, повторіть спробу. Кількість спроб $chanse")
                enter()
            } else {
                enter()
                println("Успіх")
                flag = false
            }
        }

    } else {
        println("Введіть пінкод")
        enter()
        card = card.copy(pinCode = readln().toInt(), amountOfMoney = (1000..250000).random())
        listCards.add(card)
        cardFile.writeText(Json.encodeToString<List<Card>>(listCards))
    }

    while (true) {
        enter()
        println(
            """             
            Оберіть дію:
            1 - перевірити баланс
            2 - переказати гроші на іншу карту
            3 - поповнити рахунок телефону
            4 - переглянути транзакції
            5 - відсортувати транзакції за сумою переказу
            6 - очистити файл з картами
            7 - вийти
        """.trimIndent()
        )
        enter()
        try {
            when (readln().toInt()) {
                1 -> {
                    enter()
                    println("Баланс карти: ${card.amountOfMoney}")
                }

                2 -> {
                    enter()
                    println("Введіть номер кари на яку бажаєте перевести кошти")
                    enter()
                    var cartTransition = Card(readln().toInt(), 0, 0, mutableListOf())
                    if (listCards.contains(cartTransition))
                        cartTransition = listCards[listCards.indexOf(cartTransition)]
                    enter()
                    println("Введіть суму коштів що бажаєте переказати")
                    enter()
                    val amount = readln().toInt()
                    if (card.amountOfMoney < amount) {
                        enter()
                        println("Недостатньо коштів")
                    } else {
                        cartTransition =
                            cartTransition.copy(
                                amountOfMoney = cartTransition.amountOfMoney + amount,
                                transaction = cartTransition.transaction.apply {
                                    add(Transaction(amount, Type.GET.name))
                                }
                            )
                        card =
                            card.copy(amountOfMoney = card.amountOfMoney - amount,
                                transaction = card.transaction.apply { add(Transaction(amount, Type.SEND.name)) })
                        listCards.apply {
                            remove(cartTransition)
                            remove(card)
                            add(cartTransition)
                            add(card)
                        }
                        cardFile.writeText(Json.encodeToString<List<Card>>(listCards))
                    }
                }

                3 -> {
                    enter()
                    println("Введіть номер телефону")
                    enter()
                    val number = readln().toInt()
                    enter()
                    println("Введіть суму на яку бажаєте поповнити")
                    enter()
                    val amount = readln().toInt()
                    if (card.amountOfMoney < amount) {
                        enter()
                        println("Недостатньо коштів")
                    } else {
                        println("Поповнено рахунок телефону $number, на $amount")
                        card =
                            card.copy(
                                amountOfMoney = card.amountOfMoney - amount,
                                transaction = card.transaction.apply { add(Transaction(amount, Type.SEND.name)) })
                        cardFile.writeText(Json.encodeToString<List<Card>>(listCards))
                    }
                }

                4 -> {
                    enter()
                    println("Історія транзакціїй")
                    card.transaction.forEach {
                        enter()
                        println(it)
                    }
                }

                5 -> {
                    enter()
                    println("Не відсортовані транзакції")
                    enter()
                    card.transaction.forEach {
                        println(it)
                    }
                    enter()
                    card = card.copy(
                        transaction = bubbleSort(card.transaction, TransactionComparator())
                    )
                    println("Відсортовані транзакції")
                    enter()
                    card.transaction.forEach {
                        println(it)
                    }
                    cardFile.writeText(Json.encodeToString<List<Card>>(listCards))
                }

                6 -> {
                    cardFile.writeText("")
                    return
                }

                7 -> {
                    return
                }

                else -> {
                    enter()
                    println("Невірно вибрана дія, спробуйте ще раз")
                }
            }

        } catch (e: Exception) {
            enter()
            println("Невідома помилка")
            continue
        }
    }
}

fun enter() {
    println("-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-_-")
}


fun <T> bubbleSort(cards: List<T>, comparator: Comparator<T>): MutableList<T> {
    val n = cards.size
    val newList = mutableListOf<T>().apply { addAll(cards) }
    for (i in 0..<n - 1) {
        for (j in 0..<n - i - 1) {
            if (comparator.compare(newList[j], newList[j + 1]) < 0) {
                // Swap arr[j] and arr[j + 1]
                val temp = newList[j]
                newList[j] = newList[j + 1]
                newList[j + 1] = temp
            }
        }

    }
    return newList
}

fun binarySearch(arr: Array<Card>, target: Int): Card? {
    var left = 0
    var right = arr.size - 1

    while (left <= right) {
        val mid = left + (right - left) / 2

        when {
            arr[mid].cardNumber == target -> return arr[mid]
            arr[mid].cardNumber < target -> left = mid + 1
            else -> right = mid - 1
        }
    }

    return null
}

fun printCard(listCastles: List<Card>) {
    listCastles.forEach {
        println(it)
        enter()
    }
}

class TransactionComparator : Comparator<Transaction> {
    override fun compare(o1: Transaction, o2: Transaction): Int {
        return o1.amount.compareTo(o2.amount)
    }
}

class CardComparator : Comparator<Int> {
    override fun compare(o1: Int, o2: Int): Int {
        return o1.compareTo(o2)
    }
}
