package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import com.example.viewmodel.WithdrawalUiState

@Composable
fun WithdrawalDialog(
  state: WithdrawalUiState,
  winningsAvailable: Double,
  onDismiss: () -> Unit,
  onAmountChange: (String) -> Unit,
  onDestinationChange: (String, String, String, String) -> Unit,
  onProcessWithdrawal: () -> Unit,
  modifier: Modifier = Modifier
) {
  if (!state.isOpen) return

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("withdrawal_dialog"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = DarkSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        // Title
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(EmeraldGreen.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AccountBalance,
                contentDescription = "Withdraw",
                tint = EmeraldGreen,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Instant Bank Withdrawal",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = Color.White
              )
              Text(
                text = "Instant IMPS • 0% Transfer Fee",
                style = MaterialTheme.typography.labelSmall,
                color = EmeraldGreen
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Available Balance Pill
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = DarkSurfaceElevated,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Withdrawable Winnings",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
              )
              Text(
                text = "₹${String.format("%.2f", winningsAvailable)}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = EmeraldGreen
              )
            }
            TextButton(
              onClick = { onAmountChange(winningsAvailable.toInt().toString()) },
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
              Text("Withdraw All", color = GoldAmber, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Amount Input
        OutlinedTextField(
          value = state.amountText,
          onValueChange = onAmountChange,
          label = { Text("Withdraw Amount (Min ₹100)") },
          leadingIcon = { Text("₹", fontWeight = FontWeight.Black, color = EmeraldGreen, fontSize = 18.sp) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("withdraw_amount_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = EmeraldGreen,
            unfocusedBorderColor = DarkSurfaceBorder,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Transfer Mode Toggle: UPI vs Bank Account
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FilterChip(
            selected = state.accountType == "UPI",
            onClick = { onDestinationChange("UPI", state.upiId, state.accountNumber, state.ifscCode) },
            label = { Text("Instant UPI Payout") },
            modifier = Modifier.weight(1f)
          )
          FilterChip(
            selected = state.accountType == "BANK",
            onClick = { onDestinationChange("BANK", state.upiId, state.accountNumber, state.ifscCode) },
            label = { Text("Bank Account (IMPS)") },
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (state.accountType == "UPI") {
          OutlinedTextField(
            value = state.upiId,
            onValueChange = { onDestinationChange("UPI", it, state.accountNumber, state.ifscCode) },
            label = { Text("Registered UPI ID") },
            placeholder = { Text("e.g. mobile@upi") },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("withdraw_upi_input"),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
          )
        } else {
          OutlinedTextField(
            value = state.accountNumber,
            onValueChange = { onDestinationChange("BANK", state.upiId, it, state.ifscCode) },
            label = { Text("Bank Account Number") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
          )
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = state.ifscCode,
            onValueChange = { onDestinationChange("BANK", state.upiId, state.accountNumber, it) },
            label = { Text("IFSC Code") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
          )
        }

        if (state.errorMessage != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = state.errorMessage,
            color = CrimsonLight,
            style = MaterialTheme.typography.labelSmall
          )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = onProcessWithdrawal,
          enabled = !state.isProcessing,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("withdraw_confirm_btn")
        ) {
          if (state.isProcessing) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Processing Instant IMPS...")
          } else {
            Icon(
              imageVector = Icons.Default.Send,
              contentDescription = "Send",
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "WITHDRAW ₹${state.amountText}",
              fontWeight = FontWeight.Black
            )
          }
        }
      }
    }
  }
}
