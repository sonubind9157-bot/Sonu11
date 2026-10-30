package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FantasyRepository
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

sealed class ScreenDestination {
  object Home : ScreenDestination()
  data class MatchDetail(val matchId: String) : ScreenDestination()
  data class TeamBuilder(val matchId: String) : ScreenDestination()
  data class CaptainSelection(val matchId: String) : ScreenDestination()
  data class PitchPreview(val matchId: String) : ScreenDestination()
  data class LiveMatch(val matchId: String) : ScreenDestination()
  object WalletHub : ScreenDestination()
  object ApkDownload : ScreenDestination()
}

enum class GatewayStep {
  IDLE,
  PROCESSING,
  OTP_CHALLENGE,
  SUCCESS
}

data class PaymentGatewayUiState(
  val isOpen: Boolean = false,
  val amount: Double = 500.0,
  val selectedMethod: PaymentMethod = PaymentMethod.UPI,
  val upiId: String = "sportsfan@okaxis",
  val cardNumber: String = "4532 8920 1928 3401",
  val cardExpiry: String = "08/29",
  val cardCvv: String = "742",
  val cardHolder: String = "Sonu Bind",
  val selectedBank: String = "HDFC Bank",
  val couponCode: String = "FIRST100",
  val couponBonus: Double = 100.0,
  val step: GatewayStep = GatewayStep.IDLE,
  val otpValue: String = "8492",
  val otpError: String? = null,
  val lastTransaction: Transaction? = null
)

data class WithdrawalUiState(
  val isOpen: Boolean = false,
  val amountText: String = "500",
  val accountType: String = "UPI", // "UPI" or "BANK"
  val upiId: String = "sonubind@oksbi",
  val accountNumber: String = "9823019230129",
  val ifscCode: String = "SBIN0001420",
  val isProcessing: Boolean = false,
  val errorMessage: String? = null,
  val successMessage: String? = null
)

data class ApkDownloadState(
  val isDownloading: Boolean = false,
  val progress: Float = 0f,
  val isDownloaded: Boolean = false,
  val downloadVersion: String = "v2.4.1 (Stable)",
  val apkFileSize: String = "34.8 MB",
  val apkDownloadUrl: String = "https://khel11fantasy.app/download/khel11-v2.4.1.apk",
  val websiteUrl: String = "https://ais-pre-i434rkiiyrh2cei2geg6gi-792899654187.asia-northeast1.run.app"
)

class FantasyViewModel(
  private val repository: FantasyRepository = FantasyRepository()
) : ViewModel() {

  private val _navigationStack = MutableStateFlow<List<ScreenDestination>>(listOf(ScreenDestination.Home))
  val currentScreen: StateFlow<ScreenDestination> = MutableStateFlow(ScreenDestination.Home)

  val wallet: StateFlow<Wallet> = repository.wallet
  val transactions: StateFlow<List<Transaction>> = repository.transactions
  val matches: StateFlow<List<Match>> = repository.matches
  val contests: StateFlow<Map<String, List<Contest>>> = repository.contests
  val userTeams: StateFlow<List<FantasyTeam>> = repository.userTeams

  // Selected Match & Team Building State
  private val _selectedMatchId = MutableStateFlow("match_ind_aus")
  val selectedMatchId: StateFlow<String> = _selectedMatchId.asStateFlow()

  private val _selectedPlayerIds = MutableStateFlow<Set<String>>(emptySet())
  val selectedPlayerIds: StateFlow<Set<String>> = _selectedPlayerIds.asStateFlow()

  private val _selectedRoleFilter = MutableStateFlow(PlayerRole.WICKET_KEEPER)
  val selectedRoleFilter: StateFlow<PlayerRole> = _selectedRoleFilter.asStateFlow()

  private val _captainId = MutableStateFlow<String?>(null)
  val captainId: StateFlow<String?> = _captainId.asStateFlow()

  private val _viceCaptainId = MutableStateFlow<String?>(null)
  val viceCaptainId: StateFlow<String?> = _viceCaptainId.asStateFlow()

  // Payment Gateway State
  private val _gatewayState = MutableStateFlow(PaymentGatewayUiState())
  val gatewayState: StateFlow<PaymentGatewayUiState> = _gatewayState.asStateFlow()

  // Withdrawal State
  private val _withdrawalState = MutableStateFlow(WithdrawalUiState())
  val withdrawalState: StateFlow<WithdrawalUiState> = _withdrawalState.asStateFlow()

  // APK Download State
  private val _apkState = MutableStateFlow(ApkDownloadState())
  val apkState: StateFlow<ApkDownloadState> = _apkState.asStateFlow()

  // User Message / Snackbar
  private val _snackbarMessage = MutableStateFlow<String?>(null)
  val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

  fun navigateTo(destination: ScreenDestination) {
    _navigationStack.value = _navigationStack.value + destination
    (currentScreen as MutableStateFlow).value = destination
  }

  fun navigateBack(): Boolean {
    val stack = _navigationStack.value
    if (stack.size > 1) {
      val newStack = stack.dropLast(1)
      _navigationStack.value = newStack
      (currentScreen as MutableStateFlow).value = newStack.last()
      return true
    }
    return false
  }

  fun selectMatch(matchId: String) {
    _selectedMatchId.value = matchId
    // If not already configured, auto-select a default starter fantasy team for fast play
    val matchPlayers = repository.players.value[matchId] ?: emptyList()
    if (_selectedPlayerIds.value.isEmpty() && matchPlayers.isNotEmpty()) {
      // Pick 11 balanced players as starting team
      val autoPicked = matchPlayers.take(11).map { it.id }.toSet()
      _selectedPlayerIds.value = autoPicked
      _captainId.value = matchPlayers.firstOrNull { it.id == "p_virat" }?.id ?: matchPlayers.firstOrNull()?.id
      _viceCaptainId.value = matchPlayers.firstOrNull { it.id == "p_bumrah" }?.id ?: matchPlayers.getOrNull(1)?.id
    }
    navigateTo(ScreenDestination.MatchDetail(matchId))
  }

  fun setRoleFilter(role: PlayerRole) {
    _selectedRoleFilter.value = role
  }

  fun togglePlayerSelection(player: Player) {
    val currentSet = _selectedPlayerIds.value.toMutableSet()
    if (currentSet.contains(player.id)) {
      currentSet.remove(player.id)
      if (_captainId.value == player.id) _captainId.value = null
      if (_viceCaptainId.value == player.id) _viceCaptainId.value = null
      _selectedPlayerIds.value = currentSet
    } else {
      if (currentSet.size >= 11) {
        showSnackbar("You can only select a maximum of 11 players!")
        return
      }
      val allMatchPlayers = repository.players.value[_selectedMatchId.value] ?: emptyList()
      val selectedPlayersList = allMatchPlayers.filter { currentSet.contains(it.id) }
      
      // Check credits limit (100.0)
      val currentCredits = selectedPlayersList.sumOf { it.credits }
      if (currentCredits + player.credits > 100.0) {
        showSnackbar("Credit limit exceeded! Max 100 credits allowed.")
        return
      }

      // Check max 7 players per team
      val teamCount = selectedPlayersList.count { it.teamCode == player.teamCode }
      if (teamCount >= 7) {
        showSnackbar("You can select maximum 7 players from ${player.teamCode}!")
        return
      }

      currentSet.add(player.id)
      _selectedPlayerIds.value = currentSet
    }
  }

  fun setCaptain(playerId: String) {
    if (_viceCaptainId.value == playerId) {
      _viceCaptainId.value = null
    }
    _captainId.value = playerId
  }

  fun setViceCaptain(playerId: String) {
    if (_captainId.value == playerId) {
      _captainId.value = null
    }
    _viceCaptainId.value = playerId
  }

  fun saveCurrentTeam(teamName: String = "Team 1"): Boolean {
    val matchId = _selectedMatchId.value
    val allMatchPlayers = repository.players.value[matchId] ?: emptyList()
    val chosenPlayers = allMatchPlayers.filter { _selectedPlayerIds.value.contains(it.id) }

    if (chosenPlayers.size != 11) {
      showSnackbar("Please select exactly 11 players to save your team (${chosenPlayers.size}/11 selected)")
      return false
    }

    val cap = _captainId.value
    val vc = _viceCaptainId.value
    if (cap == null || vc == null) {
      showSnackbar("Please assign a Captain (2x) and Vice-Captain (1.5x)!")
      return false
    }

    val team = FantasyTeam(
      id = "TEAM_" + UUID.randomUUID().toString().take(6).uppercase(),
      matchId = matchId,
      teamName = teamName,
      selectedPlayers = chosenPlayers,
      captainId = cap,
      viceCaptainId = vc,
      totalCreditsUsed = chosenPlayers.sumOf { it.credits }
    )
    repository.saveFantasyTeam(team)
    showSnackbar("Team '$teamName' saved successfully!")
    return true
  }

  fun joinContest(contest: Contest) {
    val matchId = _selectedMatchId.value
    val teams = repository.userTeams.value.filter { it.matchId == matchId }
    val teamToUse = teams.firstOrNull() ?: run {
      // Auto-save current selection if 11 chosen
      val allMatchPlayers = repository.players.value[matchId] ?: emptyList()
      val chosenPlayers = allMatchPlayers.filter { _selectedPlayerIds.value.contains(it.id) }
      if (chosenPlayers.size == 11) {
        val cap = _captainId.value ?: chosenPlayers[0].id
        val vc = _viceCaptainId.value ?: chosenPlayers[1].id
        val autoTeam = FantasyTeam(
          id = "TEAM_1",
          matchId = matchId,
          teamName = "Team 1",
          selectedPlayers = chosenPlayers,
          captainId = cap,
          viceCaptainId = vc,
          totalCreditsUsed = chosenPlayers.sumOf { it.credits }
        )
        repository.saveFantasyTeam(autoTeam)
        autoTeam
      } else {
        showSnackbar("Please create your 11-player fantasy team first!")
        navigateTo(ScreenDestination.TeamBuilder(matchId))
        return
      }
    }

    val (success, message) = repository.joinContest(contest.id, matchId, teamToUse)
    if (success) {
      showSnackbar(message)
    } else {
      if (message == "INSUFFICIENT_FUNDS") {
        showSnackbar("Insufficient balance! Please add ₹${contest.entryFee.toInt()} via Payment Gateway.")
        openPaymentGateway(contest.entryFee)
      } else {
        showSnackbar(message)
      }
    }
  }

  // --- Payment Gateway Methods ---

  fun openPaymentGateway(defaultAmount: Double = 500.0) {
    _gatewayState.value = _gatewayState.value.copy(
      isOpen = true,
      amount = defaultAmount,
      step = GatewayStep.IDLE,
      lastTransaction = null,
      otpError = null
    )
  }

  fun closePaymentGateway() {
    _gatewayState.value = _gatewayState.value.copy(isOpen = false, step = GatewayStep.IDLE)
  }

  fun setGatewayAmount(amount: Double) {
    _gatewayState.value = _gatewayState.value.copy(amount = amount)
  }

  fun setPaymentMethod(method: PaymentMethod) {
    _gatewayState.value = _gatewayState.value.copy(selectedMethod = method)
  }

  fun applyCoupon(code: String) {
    val bonus = repository.promoCodes[code.uppercase()]
    if (bonus != null) {
      _gatewayState.value = _gatewayState.value.copy(
        couponCode = code.uppercase(),
        couponBonus = bonus
      )
      showSnackbar("Coupon '$code' applied! ₹${bonus.toInt()} cash bonus will be added.")
    } else {
      showSnackbar("Invalid coupon code")
    }
  }

  fun updateUpiId(id: String) {
    _gatewayState.value = _gatewayState.value.copy(upiId = id)
  }

  fun updateCardDetails(number: String, expiry: String, cvv: String, holder: String) {
    _gatewayState.value = _gatewayState.value.copy(
      cardNumber = number,
      cardExpiry = expiry,
      cardCvv = cvv,
      cardHolder = holder
    )
  }

  fun updateBank(bank: String) {
    _gatewayState.value = _gatewayState.value.copy(selectedBank = bank)
  }

  fun initiatePayment() {
    viewModelScope.launch {
      _gatewayState.value = _gatewayState.value.copy(step = GatewayStep.PROCESSING)
      // Simulate bank SSL handshake & payment encryption
      delay(1200)

      // If Card or NetBanking, trigger 3D Secure 2FA challenge
      if (_gatewayState.value.selectedMethod == PaymentMethod.CARD ||
          _gatewayState.value.selectedMethod == PaymentMethod.NET_BANKING) {
        _gatewayState.value = _gatewayState.value.copy(step = GatewayStep.OTP_CHALLENGE)
      } else {
        // Direct UPI Instant Flow
        finalizePayment()
      }
    }
  }

  fun updateOtp(otp: String) {
    _gatewayState.value = _gatewayState.value.copy(otpValue = otp, otpError = null)
  }

  fun verifyOtpAndPay() {
    viewModelScope.launch {
      val entered = _gatewayState.value.otpValue
      if (entered.length < 4) {
        _gatewayState.value = _gatewayState.value.copy(otpError = "Please enter 4-digit bank OTP")
        return@launch
      }
      _gatewayState.value = _gatewayState.value.copy(step = GatewayStep.PROCESSING)
      delay(1000)
      finalizePayment()
    }
  }

  private fun finalizePayment() {
    val st = _gatewayState.value
    val ref = "PAY_" + System.currentTimeMillis().toString().takeLast(8)
    val tx = repository.addCashDeposit(
      amount = st.amount,
      paymentMethod = st.selectedMethod,
      referenceId = ref,
      couponApplied = st.couponCode.takeIf { it.isNotBlank() }
    )
    _gatewayState.value = _gatewayState.value.copy(
      step = GatewayStep.SUCCESS,
      lastTransaction = tx
    )
    showSnackbar("₹${st.amount.toInt()} added securely to your wallet!")
  }

  // --- Withdrawal Methods ---

  fun openWithdrawalDialog() {
    _withdrawalState.value = WithdrawalUiState(isOpen = true)
  }

  fun closeWithdrawalDialog() {
    _withdrawalState.value = _withdrawalState.value.copy(isOpen = false)
  }

  fun updateWithdrawalAmount(text: String) {
    _withdrawalState.value = _withdrawalState.value.copy(amountText = text, errorMessage = null)
  }

  fun updateWithdrawalDestination(accountType: String, upi: String, accountNum: String, ifsc: String) {
    _withdrawalState.value = _withdrawalState.value.copy(
      accountType = accountType,
      upiId = upi,
      accountNumber = accountNum,
      ifscCode = ifsc,
      errorMessage = null
    )
  }

  fun processWithdrawal() {
    val st = _withdrawalState.value
    val amt = st.amountText.toDoubleOrNull() ?: run {
      _withdrawalState.value = st.copy(errorMessage = "Enter valid withdrawal amount")
      return
    }

    val destination = if (st.accountType == "UPI") st.upiId else "${st.accountNumber} (${st.ifscCode})"
    viewModelScope.launch {
      _withdrawalState.value = st.copy(isProcessing = true, errorMessage = null)
      delay(1000)
      val (success, msg) = repository.withdrawWinnings(amt, destination)
      _withdrawalState.value = _withdrawalState.value.copy(
        isProcessing = false,
        errorMessage = if (!success) msg else null,
        successMessage = if (success) msg else null,
        isOpen = !success
      )
      if (success) {
        showSnackbar(msg)
      }
    }
  }

  // --- APK Download Simulator ---

  fun startApkDownload() {
    if (_apkState.value.isDownloading) return
    viewModelScope.launch {
      _apkState.value = _apkState.value.copy(isDownloading = true, progress = 0.05f)
      showSnackbar("Starting Khel11 APK download...")
      for (i in 1..10) {
        delay(250)
        _apkState.value = _apkState.value.copy(progress = i / 10f)
      }
      _apkState.value = _apkState.value.copy(isDownloading = false, isDownloaded = true)
      showSnackbar("APK Downloaded successfully! Check Downloads / notification.")
    }
  }

  fun showSnackbar(message: String) {
    _snackbarMessage.value = message
  }

  fun clearSnackbar() {
    _snackbarMessage.value = null
  }
}
