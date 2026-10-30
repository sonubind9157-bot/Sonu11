package com.example.model

data class Wallet(
  val totalBalance: Double = 1250.0,
  val unutilizedAmount: Double = 450.0, // Deposited cash, usable for contests
  val winningsAmount: Double = 650.0,    // Withdrawable winnings
  val cashBonus: Double = 150.0          // Can cover up to 10-25% of contest fees
) {
  val withdrawableBalance: Double get() = winningsAmount
}

enum class TransactionType(val label: String, val isCredit: Boolean) {
  DEPOSIT("Add Cash", true),
  CONTEST_ENTRY("Contest Joined", false),
  CONTEST_WINNINGS("Contest Winnings", true),
  WITHDRAWAL("Bank Withdrawal", false),
  BONUS_REWARD("Cash Bonus", true),
  REFUND("Contest Refund", true)
}

enum class TransactionStatus {
  SUCCESS,
  PROCESSING,
  FAILED
}

enum class PaymentMethod(val displayName: String) {
  UPI("UPI (GPay / PhonePe / Paytm)"),
  CARD("Credit / Debit Card (Visa / MC / RuPay)"),
  NET_BANKING("Net Banking"),
  WALLET("Mobile Wallet (Paytm / Mobikwik)")
}

data class Transaction(
  val id: String,
  val type: TransactionType,
  val amount: Double,
  val status: TransactionStatus,
  val timestamp: Long,
  val paymentMethod: PaymentMethod? = null,
  val referenceNumber: String,
  val title: String,
  val description: String,
  val bankOrUpiAccount: String? = null
)
