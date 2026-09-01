import org.junit.Test
import org.junit.Assert.*

class CalculateCommissionTest {
    @Test(expected = LimitExceededException::class)
    fun `daily limit exceeded`() {
        calculateCommission(CardType.VISA_MIR, 0.0, 200000.0)
    }
    @Test
    fun `vk pay no commission`() {
        assertEquals(0.0, calculateCommission(CardType.VKPAY, 0.0, 10_000.0), 0.01)
    }

    @Test(expected = LimitExceededException::class)
    fun `vk pay single limit exceeded`() {
        calculateCommission(CardType.VKPAY, 0.0, 20000.0)
    }
    @Test(expected = LimitExceededException::class)
    fun `vk pay monthly limit exceeded`() {
        calculateCommission(CardType.VKPAY, 35000.0, 10000.0)
    }

    @Test(expected = LimitExceededException::class)
    fun `monthly limit exceeded`() {
        calculateCommission(CardType.VISA_MIR, 700_000.0, 100_000.0)
    }

    @Test
    fun `mastercard no commission`() {
        val result = calculateCommission(CardType.MASTERCARD_MAESTRO, 0.0, 35_000.0)
        assertEquals(0.0, result, 0.01)
    }

    @Test
    fun `mastercard with commission`() {
        val result = calculateCommission(CardType.MASTERCARD_MAESTRO, 0.0, 95_000.0)
        val expected = 95_000.0 * 0.006 + 20.0  // = 590.0
        assertEquals(expected, result, 0.01)
    }
    @Test
    fun `visa commission above minimum`() {
        val result = calculateCommission(CardType.VISA_MIR, 0.0, 10000.0)
        assertEquals(75.0, result, 0.01)
    }

    @Test
    fun `visa commission below minimum - takes minimum`() {
        val result = calculateCommission(CardType.VISA_MIR, 0.0, 1000.0)
        assertEquals(35.0, result, 0.01)
    }

    @Test
    fun `mastercard promo applies` (){
        calculateCommission(CardType.MASTERCARD_MAESTRO, 0.0, 35_000.0)
    }
    @Test
    fun `mastercard below promo min` (){
        calculateCommission(CardType.MASTERCARD_MAESTRO, 0.0, 150.0)
    }
    @Test
    fun `mastercard above promo max in transfer` (){
        calculateCommission(CardType.MASTERCARD_MAESTRO, 0.0, 80_000.0)
    }

    @Test
    fun `mastercard total month exceeds promo max` (){
        calculateCommission(CardType.MASTERCARD_MAESTRO, 80_000.0, 20_000.0)
    }
}