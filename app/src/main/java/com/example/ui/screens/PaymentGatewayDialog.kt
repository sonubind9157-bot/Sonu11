package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.PaymentMethod
import com.example.ui.theme.*
import com.example.viewmodel.GatewayStep
import com.example.viewmodel.PaymentGatewayUiState

@Composable
fun PaymentGatewayDialog(
  state: PaymentGatewayUiState,
  onDismiss: () -> Unit,
  onAmountChange: (Double) -> Unit,
  onMethodChange: (PaymentMethod) -> Unit,
  onApplyCoupon: (String) -> Unit,
  onUpdateUpi: (String) -> Unit,
  onUpdateCard: (String, String, String, String) -> Unit,
  onUpdateBank: (String) -> Unit,
  onInitiatePayment: () -> Unit,
  onUpdateOtp: (String) -> Unit,
  onVerifyOtp: () -> Unit,
  modifier: Modifier = Modifier
) {
  if (!state.isOpen) return

  Dialog(
    onDismissRequest = {
      if (state.step != GatewayStep.PROCESSING) onDismiss()
    },
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = modifier
        .fillMaxWidth(0.95f)
        .padding(vertical = 24.dp)
        .testTag("payment_gateway_dialog"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = DarkSurface),
      border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(20.dp)
      ) {
        // Gateway Header & Security Brand
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
                imageVector = Icons.Default.Security,
                contentDescription = "SafePay Gateway",
                tint = EmeraldGreen,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "SafePay Payment Gateway",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = Color.White
              )
              Text(
                text = "256-Bit Bank Grade SSL Encrypted",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = EmeraldGreen
              )
            }
          }

          if (state.step != GatewayStep.PROCESSING) {
            IconButton(
              onClick = onDismiss,
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = TextSecondary
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = DarkSurfaceBorder, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(14.dp))

        when (state.step) {
          GatewayStep.IDLE -> {
            // STEP 0: Select Amount, Coupon & Payment Mode
            AmountAndModeSelectionStep(
              state = state,
              onAmountChange = onAmountChange,
              onMethodChange = onMethodChange,
              onApplyCoupon = onApplyCoupon,
              onUpdateUpi = onUpdateUpi,
              onUpdateCard = onUpdateCard,
              onUpdateBank = onUpdateBank,
              onInitiatePayment = onInitiatePayment
            )
          }

          GatewayStep.PROCESSING -> {
            // STEP 1: Bank Handshake Animation
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              CircularProgressIndicator(
                color = ElectricCyan,
                strokeWidth = 3.dp,
                modifier = Modifier.size(54.dp)
              )
              Spacer(modifier = Modifier.height(20.dp))
              Text(
                text = "Authorizing with Bank Server...",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Encrypting transaction payload with 256-bit AES",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Please do not press back or close the app",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = GoldAmber
              )
            }
          }

          GatewayStep.OTP_CHALLENGE -> {
            // STEP 2: 3D Secure 2-Factor OTP Challenge
            OtpChallengeStep(
              state = state,
              onUpdateOtp = onUpdateOtp,
              onVerifyOtp = onVerifyOtp
            )
          }

          GatewayStep.SUCCESS -> {
            // STEP 3: Transaction Success Receipt
            TransactionSuccessStep(
              state = state,
              onClose = onDismiss
            )
          }
        }
      }
    }
  }
}

@Composable
private fun AmountAndModeSelectionStep(
  state: PaymentGatewayUiState,
  onAmountChange: (Double) -> Unit,
  onMethodChange: (PaymentMethod) -> Unit,
  onApplyCoupon: (String) -> Unit,
  onUpdateUpi: (String) -> Unit,
  onUpdateCard: (String, String, String, String) -> Unit,
  onUpdateBank: (String) -> Unit,
  onInitiatePayment: () -> Unit
) {
  var customAmountText by remember { mutableStateOf(state.amount.toInt().toString()) }
  var couponInput by remember { mutableStateOf(state.couponCode) }

  Column {
    Text(
      text = "Select Deposit Amount",
      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
      color = Color.White
    )
    Spacer(modifier = Modifier.height(8.dp))

    // Quick Amount Chips
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      listOf(100.0, 200.0, 500.0, 1000.0).forEach { amt ->
        val isSelected = state.amount == amt
        Surface(
          onClick = {
            onAmountChange(amt)
            customAmountText = amt.toInt().toString()
          },
          shape = RoundedCornerShape(10.dp),
          color = if (isSelected) CrimsonPrimary else DarkSurfaceElevated,
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) CrimsonPrimary else DarkSurfaceBorder
          ),
          modifier = Modifier.weight(1f)
        ) {
          Text(
            text = "₹${amt.toInt()}",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 10.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Editable Amount Field
    OutlinedTextField(
      value = customAmountText,
      onValueChange = {
        customAmountText = it
        it.toDoubleOrNull()?.let { num -> onAmountChange(num) }
      },
      label = { Text("Deposit Amount (₹)") },
      leadingIcon = { Text("₹", fontWeight = FontWeight.Black, color = GoldAmber, fontSize = 18.sp) },
      singleLine = true,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("gateway_amount_input"),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = GoldAmber,
        unfocusedBorderColor = DarkSurfaceBorder,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White
      )
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Coupon Code Box
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = couponInput,
        onValueChange = { couponInput = it },
        label = { Text("Promo / Coupon Code") },
        placeholder = { Text("e.g. FIRST100") },
        singleLine = true,
        modifier = Modifier
          .weight(1f)
          .testTag("gateway_coupon_input"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = ElectricCyan,
          unfocusedBorderColor = DarkSurfaceBorder,
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White
        )
      )
      Spacer(modifier = Modifier.width(8.dp))
      Button(
        onClick = { onApplyCoupon(couponInput) },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
        modifier = Modifier.testTag("gateway_apply_coupon_btn")
      ) {
        Text("APPLY", color = DarkBg, fontWeight = FontWeight.Black)
      }
    }

    if (state.couponBonus > 0) {
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "✓ ₹${state.couponBonus.toInt()} extra cash bonus will be credited on deposit!",
        style = MaterialTheme.typography.labelSmall,
        color = EmeraldGreen
      )
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Payment Methods Tabs
    Text(
      text = "Choose Payment Option",
      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
      color = Color.White
    )
    Spacer(modifier = Modifier.height(8.dp))

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      PaymentMethodRadioItem(
        method = PaymentMethod.UPI,
        title = "Instant UPI (GPay, PhonePe, Paytm, BHIM)",
        subtitle = "Zero convenience fee • Instant processing",
        isSelected = state.selectedMethod == PaymentMethod.UPI,
        onClick = { onMethodChange(PaymentMethod.UPI) }
      )

      if (state.selectedMethod == PaymentMethod.UPI) {
        OutlinedTextField(
          value = state.upiId,
          onValueChange = onUpdateUpi,
          label = { Text("Enter UPI ID (VPA)") },
          placeholder = { Text("yourname@oksbi / user@paytm") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp)
            .testTag("gateway_upi_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = EmeraldGreen,
            unfocusedBorderColor = DarkSurfaceBorder,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          )
        )
      }

      PaymentMethodRadioItem(
        method = PaymentMethod.CARD,
        title = "Credit & Debit Cards",
        subtitle = "Visa, MasterCard, RuPay, Maestro",
        isSelected = state.selectedMethod == PaymentMethod.CARD,
        onClick = { onMethodChange(PaymentMethod.CARD) }
      )

      if (state.selectedMethod == PaymentMethod.CARD) {
        Column(modifier = Modifier.padding(start = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = state.cardNumber,
            onValueChange = { onUpdateCard(it, state.cardExpiry, state.cardCvv, state.cardHolder) },
            label = { Text("Card Number") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
          )
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = state.cardExpiry,
              onValueChange = { onUpdateCard(state.cardNumber, it, state.cardCvv, state.cardHolder) },
              label = { Text("Expiry (MM/YY)") },
              modifier = Modifier.weight(1f),
              colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
            OutlinedTextField(
              value = state.cardCvv,
              onValueChange = { onUpdateCard(state.cardNumber, state.cardExpiry, it, state.cardHolder) },
              label = { Text("CVV") },
              modifier = Modifier.weight(1f),
              colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
          }
        }
      }

      PaymentMethodRadioItem(
        method = PaymentMethod.NET_BANKING,
        title = "Net Banking",
        subtitle = "HDFC, SBI, ICICI, Axis & 50+ Banks",
        isSelected = state.selectedMethod == PaymentMethod.NET_BANKING,
        onClick = { onMethodChange(PaymentMethod.NET_BANKING) }
      )

      if (state.selectedMethod == PaymentMethod.NET_BANKING) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("HDFC Bank", "SBI", "ICICI Bank", "Axis Bank").forEach { bank ->
            FilterChip(
              selected = state.selectedBank == bank,
              onClick = { onUpdateBank(bank) },
              label = { Text(bank, fontSize = 11.sp) }
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Secure Pay Button
    Button(
      onClick = onInitiatePayment,
      shape = RoundedCornerShape(12.dp),
      colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .testTag("gateway_pay_secure_btn")
    ) {
      Icon(
        imageVector = Icons.Default.Lock,
        contentDescription = "Lock",
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "PAY SECURELY ₹${state.amount.toInt()}",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
      )
    }
  }
}

@Composable
private fun PaymentMethodRadioItem(
  method: PaymentMethod,
  title: String,
  subtitle: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Card(
    onClick = onClick,
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) DarkSurfaceElevated else DarkBg
    ),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (isSelected) EmeraldGreen else DarkSurfaceBorder
    )
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      RadioButton(
        selected = isSelected,
        onClick = onClick,
        colors = RadioButtonDefaults.colors(selectedColor = EmeraldGreen)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
          color = Color.White
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.labelSmall,
          color = TextMuted
        )
      }
    }
  }
}

@Composable
private fun OtpChallengeStep(
  state: PaymentGatewayUiState,
  onUpdateOtp: (String) -> Unit,
  onVerifyOtp: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 12.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .size(48.dp)
        .clip(CircleShape)
        .background(GoldAmber.copy(alpha = 0.2f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Password,
        contentDescription = "OTP 2FA",
        tint = GoldAmber,
        modifier = Modifier.size(28.dp)
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Text(
      text = "Bank 3D Secure 2-Factor Verification",
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
      color = Color.White,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = "SMS OTP sent to mobile registered with your bank account ending in **8492 for ₹${state.amount.toInt()}",
      style = MaterialTheme.typography.bodySmall,
      color = TextSecondary,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Demo Auto-fill notice
    Surface(
      shape = RoundedCornerShape(8.dp),
      color = DarkSurfaceElevated,
      border = androidx.compose.foundation.BorderStroke(0.5.dp, ElectricCyan)
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Demo Bank OTP: 8492",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = ElectricCyan
        )
        Spacer(modifier = Modifier.width(8.dp))
        TextButton(
          onClick = { onUpdateOtp("8492") },
          contentPadding = PaddingValues(0.dp)
        ) {
          Text("Auto-fill", fontSize = 11.sp, color = GoldAmber)
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    OutlinedTextField(
      value = state.otpValue,
      onValueChange = onUpdateOtp,
      label = { Text("4-Digit Bank OTP") },
      singleLine = true,
      isError = state.otpError != null,
      modifier = Modifier
        .width(180.dp)
        .testTag("gateway_otp_input"),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = EmeraldGreen,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White
      )
    )

    if (state.otpError != null) {
      Text(
        text = state.otpError,
        color = CrimsonLight,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.padding(top = 4.dp)
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    Button(
      onClick = onVerifyOtp,
      shape = RoundedCornerShape(12.dp),
      colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("gateway_confirm_otp_btn")
    ) {
      Text(
        text = "CONFIRM & AUTHORIZE PAYMENT",
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black)
      )
    }
  }
}

@Composable
private fun TransactionSuccessStep(
  state: PaymentGatewayUiState,
  onClose: () -> Unit
) {
  val tx = state.lastTransaction

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .size(64.dp)
        .clip(CircleShape)
        .background(EmeraldGreen),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Check,
        contentDescription = "Success",
        tint = Color.White,
        modifier = Modifier.size(36.dp)
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    Text(
      text = "Payment Successful!",
      style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
      color = Color.White
    )

    Text(
      text = "₹${state.amount.toInt()} has been credited to your Khel11 Wallet",
      style = MaterialTheme.typography.bodySmall,
      color = TextSecondary
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Receipt Summary Box
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
    ) {
      Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        ReceiptRowMini("Transaction ID", tx?.id ?: "TXN_SUCCESS")
        ReceiptRowMini("Reference No", tx?.referenceNumber ?: "PAY_20261030")
        ReceiptRowMini("Amount Deposited", "₹${state.amount.toInt()}")
        if (state.couponBonus > 0) {
          ReceiptRowMini("Bonus Credited", "+₹${state.couponBonus.toInt()}")
        }
        ReceiptRowMini("Status", "SUCCESSFUL (Instant)")
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Button(
      onClick = onClose,
      shape = RoundedCornerShape(12.dp),
      colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("gateway_success_done_btn")
    ) {
      Text(
        text = "START PLAYING CONTESTS",
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black)
      )
    }
  }
}

@Composable
private fun ReceiptRowMini(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
    Text(text = value, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
  }
}
