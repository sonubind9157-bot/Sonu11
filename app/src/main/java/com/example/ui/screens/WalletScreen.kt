package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Transaction
import com.example.model.TransactionType
import com.example.model.Wallet
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletScreen(
  wallet: Wallet,
  transactions: List<Transaction>,
  onAddCashClick: () -> Unit,
  onWithdrawClick: () -> Unit,
  onApplyCoupon: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTxFilter by remember { mutableStateOf<TransactionType?>(null) } // null = All
  var selectedTxDetail by remember { mutableStateOf<Transaction?>(null) }

  val filteredTransactions = remember(selectedTxFilter, transactions) {
    if (selectedTxFilter == null) transactions else transactions.filter { it.type == selectedTxFilter }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBg)
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 90.dp)
    ) {
      // Wallet Balance Hero Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("wallet_balance_card"),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = DarkSurface),
          border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAmber.copy(alpha = 0.6f))
        ) {
          Column(modifier = Modifier.padding(20.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "TOTAL WALLET BALANCE",
                  style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                  ),
                  color = TextSecondary
                )
                Text(
                  text = "₹${String.format("%.2f", wallet.totalBalance)}",
                  style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp
                  ),
                  color = Color.White
                )
              }

              Box(
                modifier = Modifier
                  .size(48.dp)
                  .clip(CircleShape)
                  .background(GoldAmber.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.AccountBalanceWallet,
                  contentDescription = "Wallet",
                  tint = GoldAmber,
                  modifier = Modifier.size(28.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = DarkSurfaceBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(14.dp))

            // 3 Sub-balances
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              // Unutilized / Deposited Cash
              Column {
                Text(
                  text = "Deposited Cash",
                  style = MaterialTheme.typography.labelSmall,
                  color = TextSecondary
                )
                Text(
                  text = "₹${wallet.unutilizedAmount.toInt()}",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = Color.White
                )
                Text(
                  text = "100% Usable",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                  color = EmeraldGreen
                )
              }

              // Winnings Cash
              Column {
                Text(
                  text = "Winnings (Cash)",
                  style = MaterialTheme.typography.labelSmall,
                  color = TextSecondary
                )
                Text(
                  text = "₹${wallet.winningsAmount.toInt()}",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = EmeraldGreen
                )
                Text(
                  text = "Withdrawable",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                  color = EmeraldGreen
                )
              }

              // Cash Bonus
              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = "Cash Bonus",
                  style = MaterialTheme.typography.labelSmall,
                  color = TextSecondary
                )
                Text(
                  text = "₹${wallet.cashBonus.toInt()}",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = GoldAmber
                )
                Text(
                  text = "Usable in contests",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                  color = TextMuted
                )
              }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons: ADD CASH & WITHDRAW
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Button(
                onClick = onAddCashClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                modifier = Modifier
                  .weight(1f)
                  .testTag("wallet_add_cash_btn")
              ) {
                Icon(
                  imageVector = Icons.Default.AddCircleOutline,
                  contentDescription = "Add Cash",
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("ADD CASH", fontWeight = FontWeight.Black)
              }

              OutlinedButton(
                onClick = onWithdrawClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                modifier = Modifier
                  .weight(1f)
                  .testTag("wallet_withdraw_btn")
              ) {
                Icon(
                  imageVector = Icons.Default.NorthEast,
                  contentDescription = "Withdraw",
                  tint = ElectricCyan,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("WITHDRAW", fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // Security Badges
      item {
        Spacer(modifier = Modifier.height(14.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceElevated)
            .padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.VerifiedUser,
              contentDescription = "Bank Grade Security",
              tint = EmeraldGreen,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "256-Bit SSL Bank Grade Encryption",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
              color = Color.White
            )
          }
          Text(
            text = "PCI-DSS Verified",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = TextMuted
          )
        }
      }

      // Promo Coupons Row
      item {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "Exclusive Deposit Offers",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          CouponCard(
            code = "FIRST100",
            title = "₹100 Instant Bonus",
            desc = "Min deposit ₹200",
            onApply = { onApplyCoupon("FIRST100") }
          )
          CouponCard(
            code = "IPL2026",
            title = "₹150 Super Bonus",
            desc = "Min deposit ₹500",
            onApply = { onApplyCoupon("IPL2026") }
          )
        }
      }

      // Transactions Ledger Header & Filter Chips
      item {
        Spacer(modifier = Modifier.height(20.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Transaction Passbook",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
          Text(
            text = "${filteredTransactions.size} records",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          FilterChip(
            selected = selectedTxFilter == null,
            onClick = { selectedTxFilter = null },
            label = { Text("All", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = ElectricCyan,
              selectedLabelColor = DarkBg,
              containerColor = DarkSurface,
              labelColor = TextSecondary
            )
          )

          listOf(
            TransactionType.DEPOSIT to "Deposits",
            TransactionType.CONTEST_ENTRY to "Contests",
            TransactionType.CONTEST_WINNINGS to "Winnings",
            TransactionType.WITHDRAWAL to "Withdrawals"
          ).forEach { (type, label) ->
            FilterChip(
              selected = selectedTxFilter == type,
              onClick = { selectedTxFilter = type },
              label = { Text(label, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ElectricCyan,
                selectedLabelColor = DarkBg,
                containerColor = DarkSurface,
                labelColor = TextSecondary
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
      }

      // Transactions List
      if (filteredTransactions.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 40.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "No transactions found in this category",
              style = MaterialTheme.typography.bodyMedium,
              color = TextMuted
            )
          }
        }
      } else {
        items(filteredTransactions, key = { it.id }) { tx ->
          TransactionListItem(
            transaction = tx,
            onClick = { selectedTxDetail = tx }
          )
        }
      }
    }

    // Receipt Detail Modal
    selectedTxDetail?.let { tx ->
      TransactionReceiptDialog(
        transaction = tx,
        onDismiss = { selectedTxDetail = null }
      )
    }
  }
}

@Composable
fun CouponCard(
  code: String,
  title: String,
  desc: String,
  onApply: () -> Unit
) {
  Card(
    modifier = Modifier
      .width(165.dp)
      .clickable(onClick = onApply)
      .testTag("coupon_$code"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAmber.copy(alpha = 0.3f))
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = GoldAmber.copy(alpha = 0.15f)
        ) {
          Text(
            text = code,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
            color = GoldAmber,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
        Text(
          text = "APPLY",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = ElectricCyan
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = Color.White
      )
      Text(
        text = desc,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
        color = TextMuted
      )
    }
  }
}

@Composable
fun TransactionListItem(
  transaction: Transaction,
  onClick: () -> Unit
) {
  val isCredit = transaction.type.isCredit
  val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

  Card(
    onClick = onClick,
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
      .testTag("txn_${transaction.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = DarkSurface),
    border = androidx.compose.foundation.BorderStroke(0.5.dp, DarkSurfaceBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(if (isCredit) EmeraldGreen.copy(alpha = 0.15f) else CrimsonPrimary.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = when (transaction.type) {
            TransactionType.DEPOSIT -> Icons.Default.AddCard
            TransactionType.CONTEST_WINNINGS -> Icons.Default.EmojiEvents
            TransactionType.WITHDRAWAL -> Icons.Default.AccountBalance
            TransactionType.BONUS_REWARD -> Icons.Default.CardGiftcard
            else -> Icons.Default.SportsCricket
          },
          contentDescription = transaction.type.label,
          tint = if (isCredit) EmeraldGreen else CrimsonLight,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = transaction.title,
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
          color = Color.White
        )
        Text(
          text = dateFormat.format(Date(transaction.timestamp)),
          style = MaterialTheme.typography.labelSmall,
          color = TextMuted
        )
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = if (isCredit) "+₹${transaction.amount.toInt()}" else "-₹${transaction.amount.toInt()}",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
          color = if (isCredit) EmeraldGreen else CrimsonLight
        )
        Text(
          text = "SUCCESS",
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
          color = EmeraldGreen
        )
      }
    }
  }
}

@Composable
fun TransactionReceiptDialog(
  transaction: Transaction,
  onDismiss: () -> Unit
) {
  val dateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm:ss a", Locale.getDefault())

  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
      ) {
        Text("Done")
      }
    },
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.ReceiptLong,
          contentDescription = "Receipt",
          tint = GoldAmber
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Transaction Receipt",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = DarkSurfaceElevated,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = if (transaction.type.isCredit) "+₹${String.format("%.2f", transaction.amount)}" else "-₹${String.format("%.2f", transaction.amount)}",
              style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
              color = if (transaction.type.isCredit) EmeraldGreen else CrimsonLight
            )
            Text(
              text = "Status: ${transaction.status.name}",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = EmeraldGreen
            )
          }
        }

        ReceiptRow("Transaction ID", transaction.id)
        ReceiptRow("Reference No", transaction.referenceNumber)
        ReceiptRow("Type", transaction.type.label)
        ReceiptRow("Date & Time", dateFormat.format(Date(transaction.timestamp)))
        if (transaction.paymentMethod != null) {
          ReceiptRow("Payment Mode", transaction.paymentMethod.displayName)
        }
        if (transaction.bankOrUpiAccount != null) {
          ReceiptRow("Destination Account", transaction.bankOrUpiAccount)
        }
        ReceiptRow("Remarks", transaction.description)
      }
    },
    containerColor = DarkSurface,
    titleContentColor = Color.White,
    textContentColor = TextSecondary
  )
}

@Composable
private fun ReceiptRow(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = TextMuted
    )
    Text(
      text = value,
      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
      color = Color.White
    )
  }
}
