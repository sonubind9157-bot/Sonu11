package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.FantasyRepository
import com.example.model.PaymentMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Khel11 Fantasy", appName)
  }

  @Test
  fun `test wallet deposit and coupon logic`() {
    val repo = FantasyRepository()
    val initialBalance = repo.wallet.value.totalBalance

    // Deposit 500 with coupon FIRST100 (+100 bonus)
    val tx = repo.addCashDeposit(
      amount = 500.0,
      paymentMethod = PaymentMethod.UPI,
      referenceId = "TEST_REF_100",
      couponApplied = "FIRST100"
    )

    assertEquals(500.0, tx.amount, 0.01)
    val newWallet = repo.wallet.value
    // 500 deposit + 100 bonus = 600 total increase
    assertEquals(initialBalance + 600.0, newWallet.totalBalance, 0.01)
    assertTrue(newWallet.unutilizedAmount >= 500.0)
  }
}
