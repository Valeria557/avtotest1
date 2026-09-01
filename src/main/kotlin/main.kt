enum class CardType {
    MASTERCARD_MAESTRO,VISA_MIR, VKPAY
}

const val DAILY_LIMIT = 150_000.0
const val MONTHLY_LIMIT = 600_000.0
const val MASTERCARD_MIN_LIMIT =300.0
const val MASTERCARD_MAX_LIMIT = 75_000.0
const val VKPAY_TRANSLATION_DAILY = 15_000.0
const val VKPAY_TRANSLATION_MONTHLY =40_000.0

class LimitExceededException(message: String) : Exception(message)

fun calculateCommission(cardType: CardType, previousMonthAmount: Double = 0.0, transferAmount: Double): Double {
    val totalMonthAmount = previousMonthAmount + transferAmount
    // Лимиты для VK Pay — отдельные, более строгие
    if (cardType == CardType.VKPAY) {
        if (transferAmount > VKPAY_TRANSLATION_DAILY) {
            throw LimitExceededException("Операция заблокирована: лимит на один перевод с VK Pay — 15 000 руб.")

        }
        if (totalMonthAmount > VKPAY_TRANSLATION_MONTHLY) {
            throw LimitExceededException("Операция заблокирована: месячный лимит VK Pay — 40 000 руб.")

        }
        return 0.0
    }

    if (transferAmount > DAILY_LIMIT) {
        throw LimitExceededException("Превышен суточный лимит (150 000 руб.)")
    }
    // Проверка месячного лимита
    if (totalMonthAmount > MONTHLY_LIMIT) {
        throw LimitExceededException("Операция заблокирована: превышен месячный лимит перевода (600 000 руб.)")
    }
    val commission = when (cardType) {
        CardType.MASTERCARD_MAESTRO  -> {
            if (transferAmount in MASTERCARD_MIN_LIMIT .. MASTERCARD_MAX_LIMIT && totalMonthAmount <= MASTERCARD_MAX_LIMIT) {
                0.0
            } else {
                transferAmount*0.006+20
            }
        }

        CardType.VISA_MIR -> maxOf(transferAmount * 0.0075, 35.0)

        else -> 0.0
    }
    return commission
}



