package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.PlayerRole
import com.example.ui.components.FantasyTopBar
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.*

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        FantasyAppMain()
      }
    }
  }
}

@Composable
fun FantasyAppMain(
  viewModel: FantasyViewModel = viewModel()
) {
  val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
  val wallet by viewModel.wallet.collectAsStateWithLifecycle()
  val matches by viewModel.matches.collectAsStateWithLifecycle()
  val contestsMap by viewModel.contests.collectAsStateWithLifecycle()
  val transactions by viewModel.transactions.collectAsStateWithLifecycle()
  val userTeams by viewModel.userTeams.collectAsStateWithLifecycle()

  val selectedMatchId by viewModel.selectedMatchId.collectAsStateWithLifecycle()
  val selectedPlayerIds by viewModel.selectedPlayerIds.collectAsStateWithLifecycle()
  val selectedRoleFilter by viewModel.selectedRoleFilter.collectAsStateWithLifecycle()
  val captainId by viewModel.captainId.collectAsStateWithLifecycle()
  val viceCaptainId by viewModel.viceCaptainId.collectAsStateWithLifecycle()

  val gatewayState by viewModel.gatewayState.collectAsStateWithLifecycle()
  val withdrawalState by viewModel.withdrawalState.collectAsStateWithLifecycle()
  val apkState by viewModel.apkState.collectAsStateWithLifecycle()
  val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(snackbarMessage) {
    snackbarMessage?.let {
      snackbarHostState.showSnackbar(it)
      viewModel.clearSnackbar()
    }
  }

  // Handle Android Back Navigation
  BackHandler(enabled = currentScreen !is ScreenDestination.Home) {
    viewModel.navigateBack()
  }

  val activeMatch = remember(selectedMatchId, matches) {
    matches.find { it.id == selectedMatchId } ?: matches.first()
  }
  val activeContests = remember(selectedMatchId, contestsMap) {
    contestsMap[selectedMatchId] ?: emptyList()
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = DarkBg,
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      val isSubScreen = currentScreen !is ScreenDestination.Home &&
          currentScreen !is ScreenDestination.WalletHub &&
          currentScreen !is ScreenDestination.ApkDownload

      val title = when (currentScreen) {
        is ScreenDestination.Home -> "Khel11 Fantasy"
        is ScreenDestination.MatchDetail -> "${activeMatch.team1Code} vs ${activeMatch.team2Code}"
        is ScreenDestination.TeamBuilder -> "Create Team"
        is ScreenDestination.CaptainSelection -> "Pick C & VC"
        is ScreenDestination.PitchPreview -> "Pitch Preview"
        is ScreenDestination.LiveMatch -> "Live Fantasy Tracker"
        is ScreenDestination.WalletHub -> "Wallet & Passbook"
        is ScreenDestination.ApkDownload -> "Download APK & Web"
      }

      FantasyTopBar(
        title = title,
        walletBalance = wallet.totalBalance,
        showBackButton = isSubScreen,
        onBackClick = { viewModel.navigateBack() },
        onWalletClick = { viewModel.navigateTo(ScreenDestination.WalletHub) },
        onApkClick = { viewModel.navigateTo(ScreenDestination.ApkDownload) }
      )
    },
    bottomBar = {
      // Show bottom navigation bar on primary destinations
      val showBottomBar = currentScreen is ScreenDestination.Home ||
          currentScreen is ScreenDestination.WalletHub ||
          currentScreen is ScreenDestination.ApkDownload ||
          currentScreen is ScreenDestination.LiveMatch

      if (showBottomBar) {
        NavigationBar(
          containerColor = DarkSurfaceElevated,
          tonalElevation = 8.dp,
          windowInsets = WindowInsets.navigationBars,
          modifier = Modifier.testTag("main_bottom_nav_bar")
        ) {
          NavigationBarItem(
            selected = currentScreen is ScreenDestination.Home,
            onClick = { viewModel.navigateTo(ScreenDestination.Home) },
            icon = {
              Icon(
                imageVector = if (currentScreen is ScreenDestination.Home) Icons.Filled.SportsCricket else Icons.Outlined.SportsCricket,
                contentDescription = "Matches"
              )
            },
            label = { Text("Matches", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color.White,
              selectedTextColor = CrimsonPrimary,
              indicatorColor = CrimsonPrimary,
              unselectedIconColor = TextSecondary,
              unselectedTextColor = TextSecondary
            ),
            modifier = Modifier.testTag("nav_matches")
          )

          NavigationBarItem(
            selected = currentScreen is ScreenDestination.LiveMatch,
            onClick = { viewModel.navigateTo(ScreenDestination.LiveMatch("match_csk_mi")) },
            icon = {
              Icon(
                imageVector = if (currentScreen is ScreenDestination.LiveMatch) Icons.Filled.Bolt else Icons.Outlined.Bolt,
                contentDescription = "Live"
              )
            },
            label = { Text("Live", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color.White,
              selectedTextColor = CrimsonPrimary,
              indicatorColor = CrimsonPrimary,
              unselectedIconColor = TextSecondary,
              unselectedTextColor = TextSecondary
            ),
            modifier = Modifier.testTag("nav_live")
          )

          NavigationBarItem(
            selected = currentScreen is ScreenDestination.WalletHub,
            onClick = { viewModel.navigateTo(ScreenDestination.WalletHub) },
            icon = {
              Icon(
                imageVector = if (currentScreen is ScreenDestination.WalletHub) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet,
                contentDescription = "Wallet"
              )
            },
            label = { Text("Wallet", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color.White,
              selectedTextColor = CrimsonPrimary,
              indicatorColor = CrimsonPrimary,
              unselectedIconColor = TextSecondary,
              unselectedTextColor = TextSecondary
            ),
            modifier = Modifier.testTag("nav_wallet")
          )

          NavigationBarItem(
            selected = currentScreen is ScreenDestination.ApkDownload,
            onClick = { viewModel.navigateTo(ScreenDestination.ApkDownload) },
            icon = {
              Icon(
                imageVector = if (currentScreen is ScreenDestination.ApkDownload) Icons.Filled.Download else Icons.Outlined.Download,
                contentDescription = "Get APK"
              )
            },
            label = { Text("Get APK", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color.White,
              selectedTextColor = ElectricCyan,
              indicatorColor = ElectricCyan.copy(alpha = 0.3f),
              unselectedIconColor = TextSecondary,
              unselectedTextColor = TextSecondary
            ),
            modifier = Modifier.testTag("nav_download_apk")
          )
        }
      }
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      when (val screen = currentScreen) {
        is ScreenDestination.Home -> {
          HomeScreen(
            matches = matches,
            onMatchClick = { matchId -> viewModel.selectMatch(matchId) },
            onOpenWallet = { viewModel.navigateTo(ScreenDestination.WalletHub) },
            onOpenApkDownload = { viewModel.navigateTo(ScreenDestination.ApkDownload) }
          )
        }

        is ScreenDestination.MatchDetail -> {
          MatchDetailScreen(
            match = activeMatch,
            contests = activeContests,
            selectedPlayerCount = selectedPlayerIds.size,
            userTeamCount = userTeams.count { it.matchId == activeMatch.id },
            onJoinContest = { contest -> viewModel.joinContest(contest) },
            onCreateOrEditTeamClick = { viewModel.navigateTo(ScreenDestination.TeamBuilder(activeMatch.id)) },
            onPitchPreviewClick = { viewModel.navigateTo(ScreenDestination.PitchPreview(activeMatch.id)) }
          )
        }

        is ScreenDestination.TeamBuilder -> {
          val matchPlayers = remember(screen.matchId) {
            com.example.data.FantasyRepository().let { it.players.value[screen.matchId] ?: emptyList() }
          }

          TeamBuilderScreen(
            match = activeMatch,
            allPlayers = matchPlayers,
            selectedPlayerIds = selectedPlayerIds,
            selectedRole = selectedRoleFilter,
            onRoleSelected = { role -> viewModel.setRoleFilter(role) },
            onTogglePlayer = { player -> viewModel.togglePlayerSelection(player) },
            onPitchPreviewClick = { viewModel.navigateTo(ScreenDestination.PitchPreview(screen.matchId)) },
            onNextCaptainClick = { viewModel.navigateTo(ScreenDestination.CaptainSelection(screen.matchId)) }
          )
        }

        is ScreenDestination.CaptainSelection -> {
          val matchPlayers = remember(screen.matchId) {
            com.example.data.FantasyRepository().let { it.players.value[screen.matchId] ?: emptyList() }
          }
          val selectedPlayersList = matchPlayers.filter { selectedPlayerIds.contains(it.id) }

          CaptainSelectionScreen(
            selectedPlayers = selectedPlayersList,
            captainId = captainId,
            viceCaptainId = viceCaptainId,
            onSetCaptain = { pid -> viewModel.setCaptain(pid) },
            onSetViceCaptain = { pid -> viewModel.setViceCaptain(pid) },
            onSaveTeamClick = {
              if (viewModel.saveCurrentTeam()) {
                viewModel.navigateTo(ScreenDestination.MatchDetail(screen.matchId))
              }
            },
            onPitchPreviewClick = { viewModel.navigateTo(ScreenDestination.PitchPreview(screen.matchId)) }
          )
        }

        is ScreenDestination.PitchPreview -> {
          val matchPlayers = remember(screen.matchId) {
            com.example.data.FantasyRepository().let { it.players.value[screen.matchId] ?: emptyList() }
          }
          val selectedPlayersList = matchPlayers.filter { selectedPlayerIds.contains(it.id) }

          PitchViewScreen(
            selectedPlayers = selectedPlayersList,
            captainId = captainId,
            viceCaptainId = viceCaptainId,
            onClose = { viewModel.navigateBack() }
          )
        }

        is ScreenDestination.LiveMatch -> {
          val matchPlayers = remember(screen.matchId) {
            com.example.data.FantasyRepository().let { it.players.value[screen.matchId] ?: emptyList() }
          }
          val chosen = matchPlayers.take(11)

          LiveMatchScreen(
            match = matches.find { it.id == "match_csk_mi" } ?: activeMatch,
            selectedPlayers = chosen,
            captainId = "p_virat",
            viceCaptainId = "p_bumrah",
            onBackClick = { viewModel.navigateBack() }
          )
        }

        is ScreenDestination.WalletHub -> {
          WalletScreen(
            wallet = wallet,
            transactions = transactions,
            onAddCashClick = { viewModel.openPaymentGateway(500.0) },
            onWithdrawClick = { viewModel.openWithdrawalDialog() },
            onApplyCoupon = { code ->
              viewModel.openPaymentGateway(500.0)
              viewModel.applyCoupon(code)
            }
          )
        }

        is ScreenDestination.ApkDownload -> {
          ApkDownloadScreen(
            state = apkState,
            onStartDownload = { viewModel.startApkDownload() },
            onShowMessage = { msg -> viewModel.showSnackbar(msg) }
          )
        }
      }

      // Payment Gateway Dialog (Bank Grade Checkout & 2FA OTP)
      PaymentGatewayDialog(
        state = gatewayState,
        onDismiss = { viewModel.closePaymentGateway() },
        onAmountChange = { amt -> viewModel.setGatewayAmount(amt) },
        onMethodChange = { method -> viewModel.setPaymentMethod(method) },
        onApplyCoupon = { coupon -> viewModel.applyCoupon(coupon) },
        onUpdateUpi = { upi -> viewModel.updateUpiId(upi) },
        onUpdateCard = { num, exp, cvv, name -> viewModel.updateCardDetails(num, exp, cvv, name) },
        onUpdateBank = { bank -> viewModel.updateBank(bank) },
        onInitiatePayment = { viewModel.initiatePayment() },
        onUpdateOtp = { otp -> viewModel.updateOtp(otp) },
        onVerifyOtp = { viewModel.verifyOtpAndPay() }
      )

      // Withdrawal Payout Dialog (Instant IMPS & UPI)
      WithdrawalDialog(
        state = withdrawalState,
        winningsAvailable = wallet.winningsAmount,
        onDismiss = { viewModel.closeWithdrawalDialog() },
        onAmountChange = { text -> viewModel.updateWithdrawalAmount(text) },
        onDestinationChange = { type, upi, num, ifsc ->
          viewModel.updateWithdrawalDestination(type, upi, num, ifsc)
        },
        onProcessWithdrawal = { viewModel.processWithdrawal() }
      )
    }
  }
}
